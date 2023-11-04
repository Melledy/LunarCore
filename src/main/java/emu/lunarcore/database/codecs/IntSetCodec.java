package emu.lunarcore.database.codecs;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * Custom mongodb codec for encoding/decoding fastutil int sets.
 */
public class IntSetCodec implements Codec<IntSet> {
    
    @Override
    public Class<IntSet> getEncoderClass() {
        return IntSet.class;
    }

    @Override
    public void encode(BsonWriter writer, IntSet collection, EncoderContext encoderContext) {
        writer.writeStartArray();
        for (int value : collection) {
            writer.writeInt32(value);
        }
        writer.writeEndArray();
    }
    
    @Override
    public IntSet decode(BsonReader reader, DecoderContext decoderContext) {
        IntSet collection = new IntOpenHashSet();
        reader.readStartArray();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            collection.add(reader.readInt32());
        }
        reader.readEndArray();
        return collection;
    }
}