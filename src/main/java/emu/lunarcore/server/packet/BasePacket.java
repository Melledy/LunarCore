package emu.lunarcore.server.packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import us.hebi.quickbuf.ProtoMessage;

public class BasePacket {
    public static final int HEADER_CONST = 0x9d74c714;
    public static final int TAIL_CONST = 0xd7a152c8;

    private int opcode;
    private byte[] data;

    // Encryption
    private boolean useDispatchKey;
    public boolean shouldEncrypt = true;

    public BasePacket(int opcode) {
        this.opcode = opcode;
    }

    public int getOpcode() {
        return opcode;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    public boolean useDispatchKey() {
        return useDispatchKey;
    }

    public void setUseDispatchKey(boolean useDispatchKey) {
        this.useDispatchKey = useDispatchKey;
    }

    public byte[] getData() {
        return data;
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
        this.writeUint16(baos, opcode);
        this.writeUint16(baos, 0); // Empty header
        this.writeUint32(baos, data.length);
        this.writeBytes(baos, data);
        this.writeUint32(baos, TAIL_CONST);

        byte[] packet = baos.toByteArray();

        return packet;
    }

    public void writeUint16(ByteArrayOutputStream baos, int i) {
        // Unsigned short
        baos.write((byte) ((i >>> 8) & 0xFF));
        baos.write((byte) (i & 0xFF));
    }

    public void writeUint32(ByteArrayOutputStream baos, int i) {
        // Unsigned int (long)
        baos.write((byte) ((i >>> 24) & 0xFF));
        baos.write((byte) ((i >>> 16) & 0xFF));
        baos.write((byte) ((i >>> 8) & 0xFF));
        baos.write((byte) (i & 0xFF));
    }

    public void writeBytes(ByteArrayOutputStream baos, byte[] bytes) {
        try {
            baos.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
