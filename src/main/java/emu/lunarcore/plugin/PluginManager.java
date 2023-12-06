package emu.lunarcore.plugin;

import emu.lunarcore.util.JsonUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarFile;

/** Manages the server's plugins and the event system. */
@Getter
public final class PluginManager {
    /*
     * This should only be changed when a breaking change is made to the plugin API.
     * A 'breaking change' is something which changes the existing logic of the API.
     */
    public static final int API_VERSION = 1;

    /**
     * The directory where plugins are stored.
     */
    public static final File PLUGINS_DIR = new File("plugins");

    /** Map of loaded plugins; Name -> Instance */
    private final Map<String, Plugin> plugins = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger("Plugin Manager");

    private boolean pluginsLoaded = false;

    /**
     * Loads all plugins from the plugins directory.
     * This can only be called once.
     */
    public void loadPlugins() throws IOException {
        if (this.pluginsLoaded)
            throw new IllegalStateException("Plugins have already been loaded.");
        this.pluginsLoaded = true;

        if (!PLUGINS_DIR.exists() && !PLUGINS_DIR.mkdirs())
            throw new IOException("Failed to create plugins directory.");

        // Read files from the directory.
        var files = PLUGINS_DIR.listFiles();
        if (files == null) return;

        var loadingExceptions = new ArrayList<Exception>();

        var pluginFiles = Arrays.stream(files)
            .filter(file -> file.getName().endsWith(".jar"))
            .toList();
        var pluginURLs = pluginFiles.stream()
            .map(file -> {
                try {
                    return file.toURI().toURL();
                } catch (IOException e) {
                    loadingExceptions.add(e);
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .toArray(URL[]::new);

        loadingExceptions.forEach(e -> this.getLogger().warn("Failed to load plugin: " + e.getMessage()));

        // Begin loading plugins.
        var classLoader = new URLClassLoader(pluginURLs);
        var dependencies = new ArrayList<Plugin.Data>();

        pluginFiles.forEach(pluginFile -> {
            try {
                var pluginUrl = pluginFile.toURI().toURL();

                // Read the plugin's configuration file.
                var jarReader = new URLClassLoader(new URL[] { pluginUrl });
                var pluginConfigFile = jarReader.getResourceAsStream("plugin.json");
                if (pluginConfigFile == null) {
                    this.getLogger().warn("Plugin {} did not specify a config file.", pluginFile.getName());
                    return;
                }

                // Deserialize the plugin's configuration file.
                var configReader = new InputStreamReader(pluginConfigFile);
                var pluginConfig = JsonUtils.loadToClass(configReader, Plugin.Config.class);
                if (pluginConfig == null) {
                    this.getLogger().warn("Plugin {} has an invalid config file.", pluginFile.getName());
                    return;
                }
                jarReader.close();

                // Validate the plugin's configuration file.
                if (pluginConfig.api() == null) {
                    this.getLogger().warn("Plugin {} did not specify an API version.", pluginFile.getName());
                    return;
                } else if (pluginConfig.api() != API_VERSION) {
                    this.getLogger().warn("Plugin {} requires API version {}, but this server is running version {}.",
                        pluginFile.getName(), pluginConfig.api(), API_VERSION);
                    return;
                } else if (!pluginConfig.validate()) {
                    this.getLogger().warn("Plugin {} has an invalid config file.", pluginFile.getName());
                    return;
                }

                // Load all classes in the plugin's JAR file.
                var pluginJar = new JarFile(pluginFile);
                var entries = pluginJar.entries();
                while (entries.hasMoreElements()) {
                    var entry = entries.nextElement();
                    if (entry.isDirectory() || !entry.getName().endsWith(".class")) continue;

                    var className = entry.getName().substring(0, entry.getName().length() - 6);
                    className = className.replace('/', '.');
                    classLoader.loadClass(className);
                }

                // Instantiate the plugin.
                var pluginClass = classLoader.loadClass(pluginConfig.mainClass());
                var pluginInstance = (Plugin) pluginClass.getDeclaredConstructor(
                    Plugin.Identifier.class,
                    URLClassLoader.class,
                    File.class,
                    Logger.class
                ).newInstance(
                    Plugin.Identifier.from(pluginConfig),
                    classLoader,
                    new File(PLUGINS_DIR, pluginConfig.name()),
                    LoggerFactory.getLogger(pluginConfig.name())
                );

                // Check for plugin dependencies.
                var loadAfter = pluginConfig.loadAfter();
                if (loadAfter != null && loadAfter.length > 0) {
                    dependencies.add(new Plugin.Data(
                        pluginInstance,
                        classLoader,
                        loadAfter
                    ));
                } else try {
                    pluginInstance.onLoad();
                } catch (Throwable exception) {
                    this.getLogger().warn("Failed to load plugin {}.", pluginFile.getName());
                }
            } catch (IOException | ClassNotFoundException e) {
                this.getLogger().warn("Failed to load plugin {}.", pluginFile.getName());
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });

        // Load all plugins with dependencies.
        var depth = 0;
        final var maxDepth = 30;
        while (!dependencies.isEmpty()) {
            // Check if the depth is too high.
            if (depth >= maxDepth) {
                this.getLogger().warn("Failed to load plugins due to circular dependencies.");
                break;
            }

            try {
                // Get the next plugin to load.
                var pluginData = dependencies.get(0);

                // Check if the plugin's dependencies are loaded.
                if (!this.plugins.keySet().containsAll(List.of(pluginData.dependencies()))) {
                    depth++; // Increase depth counter.
                    continue; // Continue to next plugin.
                }

                // Remove the plugin from the list of dependencies.
                dependencies.remove(pluginData);

                // Load the plugin.
                pluginData.instance().onLoad();
            } catch (Throwable exception) {
                this.getLogger().warn("Failed to load plugin {}.", exception.getMessage());
                depth++;
            }
        }
    }

    /**
     * Enables all plugins.
     */
    public void enablePlugins() {
        this.getPlugins().forEach((name, plugin) -> {
            try {
                this.getLogger().info("Enabling plugin {}.", name);
                plugin.onEnable();
                return;
            } catch (NoSuchMethodError | NoSuchFieldError ignored) {
                this.getLogger().warn("Plugin {} is not compatible with this server version.", name);
            } catch (Throwable exception) {
                this.getLogger().warn("Failed to enable plugin {}.", name);
            }

            plugin.onDisable();
        });
    }

    /**
     * Disables all plugins.
     */
    public void disablePlugins() {
        this.getPlugins().forEach((name, plugin) -> {
            try {
                this.getLogger().info("Disabling plugin {}.", name);
                plugin.onDisable();
            } catch (Throwable exception) {
                this.getLogger().warn("Failed to disable plugin {}.", name);
            }
        });
    }
}
