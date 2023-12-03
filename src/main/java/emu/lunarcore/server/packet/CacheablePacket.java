package emu.lunarcore.server.packet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a BasePacket class as cacheable. Cacheable packets are created only once for all clients and stored in a map to be sent.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheablePacket {

}
