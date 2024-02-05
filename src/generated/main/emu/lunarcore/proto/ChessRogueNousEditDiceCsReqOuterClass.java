// Code generated by protocol buffer compiler. Do not edit!
package emu.lunarcore.proto;

import java.io.IOException;
import us.hebi.quickbuf.FieldName;
import us.hebi.quickbuf.InvalidProtocolBufferException;
import us.hebi.quickbuf.JsonSink;
import us.hebi.quickbuf.JsonSource;
import us.hebi.quickbuf.MessageFactory;
import us.hebi.quickbuf.ProtoMessage;
import us.hebi.quickbuf.ProtoSink;
import us.hebi.quickbuf.ProtoSource;

public final class ChessRogueNousEditDiceCsReqOuterClass {
  /**
   * Protobuf type {@code ChessRogueNousEditDiceCsReq}
   */
  public static final class ChessRogueNousEditDiceCsReq extends ProtoMessage<ChessRogueNousEditDiceCsReq> implements Cloneable {
    private static final long serialVersionUID = 0L;

    /**
     * <code>optional .ChessRogueNousDiceInfo dice_info = 12;</code>
     */
    private final ChessRogueNousDiceInfoOuterClass.ChessRogueNousDiceInfo diceInfo = ChessRogueNousDiceInfoOuterClass.ChessRogueNousDiceInfo.newInstance();

    private ChessRogueNousEditDiceCsReq() {
    }

    /**
     * @return a new empty instance of {@code ChessRogueNousEditDiceCsReq}
     */
    public static ChessRogueNousEditDiceCsReq newInstance() {
      return new ChessRogueNousEditDiceCsReq();
    }

    /**
     * <code>optional .ChessRogueNousDiceInfo dice_info = 12;</code>
     * @return whether the diceInfo field is set
     */
    public boolean hasDiceInfo() {
      return (bitField0_ & 0x00000001) != 0;
    }

    /**
     * <code>optional .ChessRogueNousDiceInfo dice_info = 12;</code>
     * @return this
     */
    public ChessRogueNousEditDiceCsReq clearDiceInfo() {
      bitField0_ &= ~0x00000001;
      diceInfo.clear();
      return this;
    }

    /**
     * <code>optional .ChessRogueNousDiceInfo dice_info = 12;</code>
     *
     * This method returns the internal storage object without modifying any has state.
     * The returned object should not be modified and be treated as read-only.
     *
     * Use {@link #getMutableDiceInfo()} if you want to modify it.
     *
     * @return internal storage object for reading
     */
    public ChessRogueNousDiceInfoOuterClass.ChessRogueNousDiceInfo getDiceInfo() {
      return diceInfo;
    }

    /**
     * <code>optional .ChessRogueNousDiceInfo dice_info = 12;</code>
     *
     * This method returns the internal storage object and sets the corresponding
     * has state. The returned object will become part of this message and its
     * contents may be modified as long as the has state is not cleared.
     *
     * @return internal storage object for modifications
     */
    public ChessRogueNousDiceInfoOuterClass.ChessRogueNousDiceInfo getMutableDiceInfo() {
      bitField0_ |= 0x00000001;
      return diceInfo;
    }

    /**
     * <code>optional .ChessRogueNousDiceInfo dice_info = 12;</code>
     * @param value the diceInfo to set
     * @return this
     */
    public ChessRogueNousEditDiceCsReq setDiceInfo(
        final ChessRogueNousDiceInfoOuterClass.ChessRogueNousDiceInfo value) {
      bitField0_ |= 0x00000001;
      diceInfo.copyFrom(value);
      return this;
    }

    @Override
    public ChessRogueNousEditDiceCsReq copyFrom(final ChessRogueNousEditDiceCsReq other) {
      cachedSize = other.cachedSize;
      if ((bitField0_ | other.bitField0_) != 0) {
        bitField0_ = other.bitField0_;
        diceInfo.copyFrom(other.diceInfo);
      }
      return this;
    }

    @Override
    public ChessRogueNousEditDiceCsReq mergeFrom(final ChessRogueNousEditDiceCsReq other) {
      if (other.isEmpty()) {
        return this;
      }
      cachedSize = -1;
      if (other.hasDiceInfo()) {
        getMutableDiceInfo().mergeFrom(other.diceInfo);
      }
      return this;
    }

    @Override
    public ChessRogueNousEditDiceCsReq clear() {
      if (isEmpty()) {
        return this;
      }
      cachedSize = -1;
      bitField0_ = 0;
      diceInfo.clear();
      return this;
    }

    @Override
    public ChessRogueNousEditDiceCsReq clearQuick() {
      if (isEmpty()) {
        return this;
      }
      cachedSize = -1;
      bitField0_ = 0;
      diceInfo.clearQuick();
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (!(o instanceof ChessRogueNousEditDiceCsReq)) {
        return false;
      }
      ChessRogueNousEditDiceCsReq other = (ChessRogueNousEditDiceCsReq) o;
      return bitField0_ == other.bitField0_
        && (!hasDiceInfo() || diceInfo.equals(other.diceInfo));
    }

    @Override
    public void writeTo(final ProtoSink output) throws IOException {
      if ((bitField0_ & 0x00000001) != 0) {
        output.writeRawByte((byte) 98);
        output.writeMessageNoTag(diceInfo);
      }
    }

    @Override
    protected int computeSerializedSize() {
      int size = 0;
      if ((bitField0_ & 0x00000001) != 0) {
        size += 1 + ProtoSink.computeMessageSizeNoTag(diceInfo);
      }
      return size;
    }

    @Override
    @SuppressWarnings("fallthrough")
    public ChessRogueNousEditDiceCsReq mergeFrom(final ProtoSource input) throws IOException {
      // Enabled Fall-Through Optimization (QuickBuffers)
      int tag = input.readTag();
      while (true) {
        switch (tag) {
          case 98: {
            // diceInfo
            input.readMessage(diceInfo);
            bitField0_ |= 0x00000001;
            tag = input.readTag();
            if (tag != 0) {
              break;
            }
          }
          case 0: {
            return this;
          }
          default: {
            if (!input.skipField(tag)) {
              return this;
            }
            tag = input.readTag();
            break;
          }
        }
      }
    }

    @Override
    public void writeTo(final JsonSink output) throws IOException {
      output.beginObject();
      if ((bitField0_ & 0x00000001) != 0) {
        output.writeMessage(FieldNames.diceInfo, diceInfo);
      }
      output.endObject();
    }

    @Override
    public ChessRogueNousEditDiceCsReq mergeFrom(final JsonSource input) throws IOException {
      if (!input.beginObject()) {
        return this;
      }
      while (!input.isAtEnd()) {
        switch (input.readFieldHash()) {
          case -184174347:
          case -1394261434: {
            if (input.isAtField(FieldNames.diceInfo)) {
              if (!input.trySkipNullValue()) {
                input.readMessage(diceInfo);
                bitField0_ |= 0x00000001;
              }
            } else {
              input.skipUnknownField();
            }
            break;
          }
          default: {
            input.skipUnknownField();
            break;
          }
        }
      }
      input.endObject();
      return this;
    }

    @Override
    public ChessRogueNousEditDiceCsReq clone() {
      return new ChessRogueNousEditDiceCsReq().copyFrom(this);
    }

    @Override
    public boolean isEmpty() {
      return ((bitField0_) == 0);
    }

    public static ChessRogueNousEditDiceCsReq parseFrom(final byte[] data) throws
        InvalidProtocolBufferException {
      return ProtoMessage.mergeFrom(new ChessRogueNousEditDiceCsReq(), data).checkInitialized();
    }

    public static ChessRogueNousEditDiceCsReq parseFrom(final ProtoSource input) throws
        IOException {
      return ProtoMessage.mergeFrom(new ChessRogueNousEditDiceCsReq(), input).checkInitialized();
    }

    public static ChessRogueNousEditDiceCsReq parseFrom(final JsonSource input) throws IOException {
      return ProtoMessage.mergeFrom(new ChessRogueNousEditDiceCsReq(), input).checkInitialized();
    }

    /**
     * @return factory for creating ChessRogueNousEditDiceCsReq messages
     */
    public static MessageFactory<ChessRogueNousEditDiceCsReq> getFactory() {
      return ChessRogueNousEditDiceCsReqFactory.INSTANCE;
    }

    private enum ChessRogueNousEditDiceCsReqFactory implements MessageFactory<ChessRogueNousEditDiceCsReq> {
      INSTANCE;

      @Override
      public ChessRogueNousEditDiceCsReq create() {
        return ChessRogueNousEditDiceCsReq.newInstance();
      }
    }

    /**
     * Contains name constants used for serializing JSON
     */
    static class FieldNames {
      static final FieldName diceInfo = FieldName.forField("diceInfo", "dice_info");
    }
  }
}