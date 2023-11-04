package emu.lunarcore.database.codecs;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

/**
 * Custom mongodb codec for encoding/decoding fastutil int2int maps.
 */
public class Int2IntMapCodec implements Codec<Int2IntMap> {
    
    @Override
    public Class<Int2IntMap> getEncoderClass() {
        return Int2IntMap.class;
    }

    @Override
    public void encode(BsonWriter writer, Int2IntMap collection, EncoderContext encoderContext) {
        writer.writeStartDocument();
        for (var entry : collection.int2IntEntrySet()) {
            writer.writeName(Integer.toString(entry.getIntKey()));
            writer.writeInt32(entry.getIntValue());
        }
        writer.writeEndDocument();
    }
    
    @Override
    public Int2IntMap decode(BsonReader reader, DecoderContext decoderContext) {
        Int2IntMap collection = new Int2IntOpenHashMap();
        reader.readStartDocument();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            collection.put(Integer.parseInt(reader.readName()), reader.readInt32());
        }
        reader.readEndDocument();
        return collection;
    }
}