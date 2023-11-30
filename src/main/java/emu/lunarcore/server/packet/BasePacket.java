package emu.lunarcore.server.packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import lombok.Getter;
import us.hebi.quickbuf.ProtoMessage;

@Getter
public class BasePacket {
    public static final int HEADER_CONST = 0x9d74c714;
    public static final int TAIL_CONST = 0xd7a152c8;

    private int cmdId;
    private byte[] data;

    public BasePacket(int cmdId) {
        this.cmdId = cmdId;
    }
    
    public BasePacket(int cmdId, byte[] data) {
        this.cmdId = cmdId;
        this.data = data;
    }
    
    public void setCmdId(int cmdId) {
        this.cmdId = cmdId;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setData(ProtoMessage<?> proto) {
        this.data = proto.toByteArray();
    }

    public byte[] build() {
        if (getData() == null) {
            this.data = new byte[0];
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream(4 + 2 + 4 + getData().length + 4);

        this.writeUint32(baos, HEADER_CONST);
        this.writeUint16(baos, cmdId);
        this.writeUint16(baos, 0); // Empty header
        this.writeUint32(baos, data.length);
        this.writeBytes(baos, data);
        this.writeUint32(baos, TAIL_CONST);

        byte[] packet = baos.toByteArray();

        return packet;
    }

    private void writeUint16(ByteArrayOutputStream baos, int i) {
        // Unsigned short
        baos.write((byte) ((i >>> 8) & 0xFF));
        baos.write((byte) (i & 0xFF));
    }

    private void writeUint32(ByteArrayOutputStream baos, int i) {
        // Unsigned int (long)
        baos.write((byte) ((i >>> 24) & 0xFF));
        baos.write((byte) ((i >>> 16) & 0xFF));
        baos.write((byte) ((i >>> 8) & 0xFF));
        baos.write((byte) (i & 0xFF));
    }

    private void writeBytes(ByteArrayOutputStream baos, byte[] bytes) {
        try {
            baos.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
