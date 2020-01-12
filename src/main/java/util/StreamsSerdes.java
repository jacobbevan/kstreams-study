package util;

import model.*;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import model.Pair;

public class StreamsSerdes {

    public static Serde<RateFile> RateFileSerde() {
        return new RateFileSerde();
    }
    public static Serde<SnapshotDef> EodDefSerde() {
        return new EodDefSerde();
    }
    public static Serde<Record> RecordSerde() {
        return new RecordSerde();
    }
    public static Serde<KeyAtDiscreteTime> KeyAtDiscreteTimeSerde() {
        return new KeyAtDiscreteTimeSerde();
    }
    public static Serde<StringList> StringListSerde() {
        return new StringListSerde();
    }


    public static <T> Serde<Optional<T>> OptionalSerde(Class<T> clazz) {
        Type[] parameters = {clazz};
        ParameterizedType parameterizedType = getType(Optional.class, parameters);
        return new OptionalSerde<T>(parameterizedType);
    }

    public static final class OptionalSerde<T> extends WrapperSerde<Optional<T>> {
        public OptionalSerde(ParameterizedType pairType) {
            super(new JsonSerializer<Optional<T>>(), new JsonDeserializer<Optional<T>>(pairType));
        }
    }


    public static <T1,T2> Serde<Pair<T1,T2>> PairSerde(Class<T1> t1, Class<T2> t2) {
        Type[] parameters = {t1,t2};
        ParameterizedType parameterizedType = getType(Pair.class, parameters);
        return new PairSerde<T1,T2>(parameterizedType);
    }

    public static final class PairSerde<T1,T2> extends WrapperSerde<Pair<T1,T2>> {
        public PairSerde(ParameterizedType pairType) {
            super(new JsonSerializer<Pair<T1,T2>>(), new JsonDeserializer<>(pairType));
        }
    }

    private static ParameterizedType getType(Class<?> rawClass, Type[] parameters) {

        return new ParameterizedType() {

            @Override
            public Type[] getActualTypeArguments() {
                return parameters;
            }

            @Override
            public Type getRawType() {

                return rawClass;
            }

            @Override
            public Type getOwnerType() {

                return null;
            }

        };

    }

    public static Serde<SnapshotConfig> SnapshotConfigSerde() {
        return new SnapshotConfigSerde();
    }

    public static final class SnapshotConfigSerde extends WrapperSerde<SnapshotConfig> {
        public SnapshotConfigSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<SnapshotConfig>(SnapshotConfig.class));
        }
    }

    public static final class EodDefSerde extends WrapperSerde<SnapshotDef> {
        public EodDefSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(SnapshotDef.class));
        }
    }

    public static final class RateFileSerde extends WrapperSerde<RateFile> {
        public RateFileSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(RateFile.class));
        }
    }

    public static final class RecordSerde extends WrapperSerde<Record> {
        public RecordSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(Record.class));
        }
    }

    public static final class KeyAtDiscreteTimeSerde extends WrapperSerde<KeyAtDiscreteTime> {
        public KeyAtDiscreteTimeSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(KeyAtDiscreteTime.class));
        }
    }


    public static final class StringListSerde extends WrapperSerde<StringList> {
        public StringListSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(StringList.class));
        }
    }

    public static Serde<CutKeyPair> CutKeyPairSerde() {
        return new CutKeyPairSerde();
    }


    public static final class CutKeyPairSerde extends WrapperSerde<CutKeyPair> {
        public CutKeyPairSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(CutKeyPair.class));
        }
    }
    private static class WrapperSerde<T> implements Serde<T> {

        private JsonSerializer<T> serializer;
        private JsonDeserializer<T> deserializer;

        WrapperSerde(JsonSerializer<T> serializer, JsonDeserializer<T> deserializer) {
            this.serializer = serializer;
            this.deserializer = deserializer;
        }

        @Override
        public void configure(Map<String, ?> map, boolean b) {

        }

        @Override
        public void close() {

        }

        @Override
        public Serializer<T> serializer() {
            return serializer;
        }

        @Override
        public Deserializer<T> deserializer() {
            return deserializer;
        }
    }
}
