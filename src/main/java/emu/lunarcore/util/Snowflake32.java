package emu.lunarcore.util;

public class Snowflake32 {
    private static final long EPOCH = 1672531200000L; // Sunday, January 1, 2023 12:00:00 AM (GMT)
    private static int cachedTimestamp;
    private static byte sequence;

    public synchronized static int newUid() {
        int timestamp = (int) ((System.currentTimeMillis() - EPOCH) / 1000);

        if (cachedTimestamp != timestamp) {
            sequence = 0;
            cachedTimestamp = timestamp;
        } else {
            sequence++;
        }

        return (cachedTimestamp << 4) + sequence;
    }

    public synchronized static int toTimestamp(int snowflake) {
        return (snowflake >> 4) + (int) (EPOCH / 1000);
    }

}
