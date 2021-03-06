package cn.leancloud.play.codec;

import cn.leancloud.play.collection.PlayObject;
import cn.leancloud.play.proto.GenericCollection;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.Map;

import static cn.leancloud.play.codec.GenericCollectionValueCodec.deserializeValue;
import static cn.leancloud.play.codec.GenericCollectionValueCodec.serializeValue;

public final class PlayObjectCodec implements Codec {
    @Override
    public byte[] serialize(Object obj) {
        PlayObject map = (PlayObject) obj;

        GenericCollection.Builder collection = GenericCollection.newBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            GenericCollection.MapEntry.Builder e = GenericCollection.MapEntry.newBuilder();
            e.setKey(entry.getKey());
            e.setVal(serializeValue(entry.getValue()));
            collection.addMapEntryValue(e);
        }

        return collection.build().toByteArray();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] bytes) {
        try {
            PlayObject m = new PlayObject();
            GenericCollection collection = GenericCollection.parseFrom(bytes);
            for (GenericCollection.MapEntry e : collection.getMapEntryValueList()) {
                String k = e.getKey();
                m.put(k, deserializeValue(e.getVal()));
            }
            return (T) m;
        } catch (InvalidProtocolBufferException ex) {
            throw new DeserializationException("Can't deserialize bytes to PlayObject", ex);
        }
    }
}
