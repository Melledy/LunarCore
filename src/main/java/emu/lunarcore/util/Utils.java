package emu.lunarcore.util;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import it.unimi.dsi.fastutil.ints.IntList;

public class Utils {
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
    
    public static final Object EMPTY_OBJECT = new Object();
    public static final int[] EMPTY_INT_ARRAY = new int[0];
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    public static String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return "";
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String capitalize(String s) {
        StringBuilder sb = new StringBuilder(s);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    public static String lowerCaseFirstChar(String s) {
        StringBuilder sb = new StringBuilder(s);
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }

    /**
     * Creates a string with the path to a file.
     * @param path The path to the file.
     * @return A path using the operating system's file separator.
     */
    public static String toFilePath(String path) {
        return path.replace("/", File.separator);
    }

    /**
     * Checks if a file exists on the file system.
     * @param path The path to the file.
     * @return True if the file exists, false otherwise.
     */
    public static boolean fileExists(String path) {
        return new File(path).exists();
    }

    /**
     * Creates a folder on the file system.
     * @param path The path to the folder.
     * @return True if the folder was created, false otherwise.
     */
    public static boolean createFolder(String path) {
        return new File(path).mkdirs();
    }

    public static long getCurrentSeconds() {
        return Math.floorDiv(System.currentTimeMillis(), 1000);
    }

    public static int getMinPromotionForLevel(int level) {
        return Math.max(Math.min((int) ((level - 11) / 10D), 6), 0);
    }

    /**
     * Parses the string argument as a signed decimal integer. Returns a 0 if the string argument is not an integer.
     */
    public static int parseSafeInt(String s) {
        if (s == null) {
            return 0;
        }

        int i = 0;

        try {
            i = Integer.parseInt(s);
        } catch (Exception e) {
            i = 0;
        }

        return i;
    }

    /**
     * Parses the string argument as a signed decimal long. Returns a 0 if the string argument is not a long.
     */
    public static long parseSafeLong(String s) {
        if (s == null) {
            return 0;
        }

        long i = 0;

        try {
            i = Long.parseLong(s);
        } catch (Exception e) {
            i = 0;
        }

        return i;
    }
    
    /**
     * Add 2 integers without overflowing
     */
    public static int safeAdd(int a, int b) {
        return safeAdd(a, b, Integer.MAX_VALUE, Integer.MIN_VALUE);
    }
    
    public static int safeAdd(int a, int b, long max, long min) {
        long sum = (long) a + (long) b;
        
        if (sum > max) {
            return (int) max;
        } else if (sum < min) {
            return (int) min;
        }
        
        return (int) sum;
    }
    
    /**
     * Subtract 2 integers without overflowing
     */
    public static int safeSubtract(int a, int b) {
        return safeSubtract(a, b, Integer.MAX_VALUE, Integer.MIN_VALUE);
    }
    
    public static int safeSubtract(int a, int b, long max, long min) {
        long sum = (long) a - (long) b;
        
        if (sum > max) {
            return (int) max;
        } else if (sum < min) {
            return (int) min;
        }
        
        return (int) sum;
    }

    public static double generateRandomDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    public static int randomRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static int randomElement(int[] array) {
        return array[ThreadLocalRandom.current().nextInt(0, array.length)];
    }

    public static <T> T randomElement(List<T> list) {
        return list.get(ThreadLocalRandom.current().nextInt(0, list.size()));
    }
    
    public static int randomElement(IntList list) {
        return list.getInt(ThreadLocalRandom.current().nextInt(0, list.size()));
    }

    /**
     * Checks if an integer array contains a value
     * @param array
     * @param value The value to check for
     */
    public static boolean arrayContains(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) return true;
        }
        return false;
    }

    /**
     * Base64 encodes a given byte array.
     * @param toEncode An array of bytes.
     * @return A base64 encoded string.
     */
    public static String base64Encode(byte[] toEncode) {
        return Base64.getEncoder().encodeToString(toEncode);
    }

    /**
     * Base64 decodes a given string.
     * @param toDecode A base64 encoded string.
     * @return An array of bytes.
     */
    public static byte[] base64Decode(String toDecode) {
        return Base64.getDecoder().decode(toDecode);
    }

    /**
     * Checks if a port is open on a given host.
     *
     * @param host The host to check.
     * @param port The port to check.
     * @return True if the port is open, false otherwise.
     */
    public static boolean isPortOpen(String host, int port) {
        try (var serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(false);
            serverSocket.bind(new InetSocketAddress(InetAddress.getByName(host), port), 1);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
