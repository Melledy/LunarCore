package emu.lunarcore.database.codecs;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

/**
 * Custom mongodb codec for encoding/decoding fastutil int sets.
 */
public class IntListCodec implements Codec<IntList> {
    
    @Override
    public Class<IntList> getEncoderClass() {
        return IntList.class;
    }

    @Override
    public void encode(BsonWriter writer, IntList collection, EncoderContext encoderContext) {
        writer.writeStartArray();
        for (int value : collection) {
            writer.writeInt32(value);
        }
        writer.writeEndArray();
    }
    
    @Override
    public IntList decode(BsonReader reader, DecoderContext decoderContext) {
        IntList collection = new IntArrayList();
        reader.readStartArray();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            collection.add(reader.readInt32());
        }
        reader.readEndArray();
        return collection;
    }
}