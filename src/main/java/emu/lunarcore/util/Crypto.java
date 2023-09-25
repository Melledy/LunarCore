package emu.lunarcore.util;

import java.security.SecureRandom;

import emu.lunarcore.LunarRail;

public final class Crypto {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static void xor(byte[] packet, byte[] key) {
        try {
            for (int i = 0; i < packet.length; i++) {
                packet[i] ^= key[i % key.length];
            }
        } catch (Exception e) {
            LunarRail.getLogger().error("Crypto error.", e);
        }
    }

    public static byte[] createSessionKey(int length) {
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        return bytes;
    }
}
