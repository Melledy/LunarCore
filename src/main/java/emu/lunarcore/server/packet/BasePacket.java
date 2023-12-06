package emu.lunarcore.server.packet;

import java.io.IOException;

import emu.lunarcore.util.Utils;
import lombok.Getter;
import us.hebi.quickbuf.ProtoMessage;
import us.hebi.quickbuf.ProtoSink;
import us.hebi.quickbuf.ProtoSource;

@Getter
public class BasePacket {
    public static final int HEADER_CONST = 0x9d74c714;
    public static final int TAIL_CONST = 0xd7a152c8;

    private int cmdId;
    private ProtoMessage<?> data;

    public BasePacket(int cmdId) {
        this.cmdId = cmdId;
    }
    
    public void setCmdId(int cmdId) {
        this.cmdId = cmdId;
    }
    
    public void setData(byte[] data) {
        this.data = new RawProto(data);
    }

    public void setData(ProtoMessage<?> proto) {
        this.data = proto;
    }

    public byte[] build() {
        // Setup
        int protoSize = 0;
        
        // Set proto message size
        if (this.data != null) {
            protoSize = this.data.getSerializedSize();
        }
        
        // Create proto sink
        byte[] packet = new byte[16 + protoSize];
        ProtoSink output = ProtoSink.newInstance(packet, 0, packet.length);
        
        try {
            // Write packet header
            this.writeUint32(output, HEADER_CONST);
            this.writeUint16(output, cmdId);
            this.writeUint16(output, 0);
            this.writeUint32(output, protoSize);
            
            // Write protobuf message
            if (this.data != null) {
                this.data.writeTo(output);
            }
            
            // Write packet footer
            this.writeUint32(output, TAIL_CONST);
        } catch (Exception e) {
            // Should never happen
        }

        return packet;
    }

    private void writeUint16(ProtoSink out, int i) throws Exception {
        // Unsigned short
        out.writeRawByte((byte) ((i >>> 8) & 0xFF));
        out.writeRawByte((byte) (i & 0xFF));
    }

    private void writeUint32(ProtoSink out, int i) throws Exception {
        // Unsigned int (long)
        out.writeRawByte((byte) ((i >>> 24) & 0xFF));
        out.writeRawByte((byte) ((i >>> 16) & 0xFF));
        out.writeRawByte((byte) ((i >>> 8) & 0xFF));
        out.writeRawByte((byte) (i & 0xFF));
    }
    
    /**
     * A byte array wrapped in a ProtoMessage object
     */
    @SuppressWarnings("rawtypes")
    private static class RawProto extends ProtoMessage {
        private byte[] data;
        
        public RawProto(byte[] data) {
            this.data = data;
        }
        
        @Override
        public ProtoMessage copyFrom(ProtoMessage other) {
            data = other.toByteArray();
            return this;
        }
        
        @Override
        public ProtoMessage clear() {
            data = Utils.EMPTY_BYTE_ARRAY;
            return this;
        }

        @Override
        protected int computeSerializedSize() {
            return data.length;
        }

        @Override
        public void writeTo(ProtoSink output) throws IOException {
            output.writeRawBytes(data);
        }

        @Override
        public ProtoMessage mergeFrom(ProtoSource input) throws IOException {
            return this; // Skip
        }

        @Override
        public boolean equals(Object obj) {
            return false; // Skip
        }

        @Override
        public ProtoMessage clone() {
            return null; // Skip
        }
        
    }
}
