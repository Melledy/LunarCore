package emu.lunarcore.plugin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.net.URLClassLoader;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Plugin {
    private final Identifier identifier;
    private final URLClassLoader classLoader;
    private final File dataFolder;
    private final Logger logger;

    /*
     * Collection of plugin events which are called by the server.
     */

    public void onLoad() {}
    public void onEnable() {}
    public void onDisable() {}

    /**
     * Fetches a resource from the plugin's JAR file.
     *
     * @param fileName The name of the file to fetch.
     * @return An {@link InputStream} of the file.
     */
    public final InputStream getResource(String fileName) {
        return this.getClassLoader().getResourceAsStream(fileName);
    }

    /** Get the plugin's name. */
    public final String getName() {
        return this.getIdentifier().name;
    }

    /** Get the plugin's description. */
    public final String getDescription() {
        return this.getIdentifier().description;
    }

    /** Get the plugin's version. */
    public final String getVersion() {
        return this.getIdentifier().version;
    }

    /** Deserialized plugin config data. */
    public record Config(
        String name,
        String description,
        String version,
        String mainClass,
        Integer api,
        String[] authors,
        String[] loadAfter
    ) {
        /**
         * Attempts to validate this config instance.
         *
         * @return True if the config is valid, false otherwise.
         */
        public boolean validate() {
            return name != null && description != null && mainClass != null && api != null;
        }
    }

    /** Loaded plugin data. */
    public record Identifier(
        String name,
        String description,
        String version,
        String[] authors
    ) {
        /**
         * Converts a {@link Config} into a {@link Identifier}.
         *
         * @param config The config to convert.
         * @return An instance of {@link Identifier}.
         */
        public static Identifier from(Config config) {
            if (!config.validate())
                throw new IllegalArgumentException("Invalid plugin config supplied.");
            return new Identifier(config.name(), config.description(), config.version(), config.authors());
        }
    }

    /** Unloaded plugin data. */
    public record Data(
        Plugin instance,
        URLClassLoader classLoader,
        String[] dependencies
    ) {}
}
