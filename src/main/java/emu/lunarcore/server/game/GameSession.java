package emu.lunarcore.server.game;

import java.net.InetSocketAddress;

import emu.lunarcore.LunarCore;
import emu.lunarcore.game.account.Account;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdIdUtils;
import emu.lunarcore.server.packet.SessionState;
import emu.lunarcore.util.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import kcp.highway.Ukcp;
import lombok.AccessLevel;
import lombok.Getter;
import us.hebi.quickbuf.ProtoMessage;

@Getter
public class GameSession {
    private final GameServer server;
    private final Int2LongMap packetCooldown;
    private InetSocketAddress address;

    private Account account;
    private Player player;

    // Network
    @Getter(AccessLevel.PRIVATE) private Ukcp ukcp;
    
    // Flags
    private SessionState state = SessionState.WAITING_FOR_TOKEN;
    private boolean useSecretKey;

    private GameSession(GameServer server) {
        this.server = server;
        this.packetCooldown = new Int2LongOpenHashMap();
    }

    public GameSession(GameServer server, Ukcp ukcp) {
        this(server);
        this.ukcp = ukcp;
        this.address = this.ukcp.user().getRemoteAddress();
    }

    public int getUid() {
        return this.player.getUid();
    }

    public boolean useSecretKey() {
        return useSecretKey;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setPlayer(Player player) {
        this.player = player;
        this.player.setSession(this);
        this.getServer().registerPlayer(player);
    }

    public void setUseSecretKey(boolean key) {
        this.useSecretKey = key;
    }

    public void setState(SessionState state) {
        this.state = state;
    }

    public void onConnect() {
        if (LunarCore.getConfig().getLogOptions().connections) {
            LunarCore.getLogger().info("Client connected from " + address.getHostString());
        }
    }

    public void onDisconnect() {
        if (LunarCore.getConfig().getLogOptions().connections) {
            LunarCore.getLogger().info("Client disconnected from " + address.getHostString());
        }

        this.state = SessionState.INACTIVE;

        if (player != null) {
            // Handle player logout event
            player.onLogout();
            
            // Save first
            player.save();
            
            // Deregister player from server
            this.getServer().deregisterPlayer(player);
        }
    }

    public void onMessage(ByteBuf packet) {
        try {
            // Decrypt and turn back into a packet
            // Crypto.xor(packet.array(), useSecretKey() ? Crypto.ENCRYPT_KEY : Crypto.DISPATCH_KEY);

            // Decode
            while (packet.readableBytes() > 0) {
                // Length
                if (packet.readableBytes() < 16) {
                    return;
                }

                // Packet header sanity check
                int constHeader = packet.readInt();
                if (constHeader != BasePacket.HEADER_CONST) {
                    return; // Bad packet
                }

                // Data
                int opcode = packet.readShort();
                int headerLength = packet.readShort();
                int dataLength = packet.readInt();
                
                byte[] data = new byte[dataLength];

                packet.skipBytes(headerLength);
                packet.readBytes(data);

                // Packet tail sanity check
                int constTail = packet.readInt();
                if (constTail != BasePacket.TAIL_CONST) {
                    return; // Bad packet
                }

                // Log packet
                if (LunarCore.getConfig().getLogOptions().packets) {
                    if (!(LunarCore.getConfig().getLogOptions().filterLoopingPackets && CmdIdUtils.IGNORED_LOG_PACKETS.contains(opcode))) {
                        logPacket("RECV", opcode, data);
                    }
                }

                // Handle
                getServer().getPacketHandler().handle(this, opcode, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // packet.release();
        }
    }

    public void send(BasePacket packet) {
        // Test
        if (packet.getCmdId() <= 0) {
            LunarCore.getLogger().warn("Tried to send packet with missing cmd id!");
            return;
        }

        // Send
        this.send(packet.build());

        // Log
        if (LunarCore.getConfig().getLogOptions().packets) {
            if (!(LunarCore.getConfig().getLogOptions().filterLoopingPackets && CmdIdUtils.IGNORED_LOG_PACKETS.contains(packet.getCmdId()))) {
                logPacket("SEND", packet.getCmdId(), packet.getData());
            }
        }
    }
    
    /**
     * Sends a cached packet with the specified cmd id. If the packet isnt cacheable, then an empty packet is sent.
     * @param cmdId
     */
    public void send(int cmdId) {
        // Get packet from the server's packet cache. This will allow us to reuse empty packets if needed.
        if (this.ukcp != null) {
            this.ukcp.write(getServer().getPacketCache().getCachedPacket(cmdId));
        }
        
        // Log
        if (LunarCore.getConfig().getLogOptions().packets) {
            if (!(LunarCore.getConfig().getLogOptions().filterLoopingPackets && CmdIdUtils.IGNORED_LOG_PACKETS.contains(cmdId))) {
                logPacket("SEND", cmdId, Utils.EMPTY_BYTE_ARRAY);
            }
        }
    }

    public void send(byte[] bytes) {
        if (this.ukcp != null) {
            ByteBuf buf = Unpooled.wrappedBuffer(bytes);
            this.ukcp.write(buf);
            buf.release();
        }
    }
    
    public void logPacket(String sendOrRecv, int opcode, ProtoMessage<?> payload) {
        logPacket(sendOrRecv, opcode, payload != null ? payload.toByteArray() : Utils.EMPTY_BYTE_ARRAY);
    }

    public void logPacket(String sendOrRecv, int opcode, byte[] payload) {
        LunarCore.getLogger().info(sendOrRecv + ": " + CmdIdUtils.getCmdIdName(opcode) + " (" + opcode + ")" + System.lineSeparator() + Utils.bytesToHex(payload));
    }

    public void close() {
        if (this.ukcp != null) {
            this.ukcp.close();
        }
    }
}
