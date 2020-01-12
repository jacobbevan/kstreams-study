package ch1;


import model.*;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.StreamsSerdes;

import javax.swing.text.html.Option;
import java.lang.reflect.Array;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class JoinExperiment {


    private static final Logger LOG = LoggerFactory.getLogger(JoinExperiment.class);


    public static final String RATE_FILE_TOPIC = "rateFile";
    public static final String EOD_CONFIG_TOPIC = "eodConfig";


    public static Topology build() {

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        //stream of vols
        KStream<String,Record> volStream = streamsBuilder.stream("vols", Consumed.with(Serdes.String(), StreamsSerdes.RecordSerde()));

        //table of config settings
        KTable<String, SnapshotConfig> cuts = streamsBuilder.table("snapshotConfig", Consumed.with(Serdes.String(), StreamsSerdes.SnapshotConfigSerde()));

        KStream<String, Pair<Record,SnapshotConfig>> volsByCut = volStream
                .join(
                        cuts,
                        Pair::new,
                        Joined.with(
                                Serdes.String(),
                                StreamsSerdes.RecordSerde(),
                                StreamsSerdes.SnapshotConfigSerde()
                        )
                );


        Serde<Pair<String,SnapshotDef>> pairSerde = StreamsSerdes.PairSerde(String.class, SnapshotDef.class);
        Serde<Record> recordSerde = StreamsSerdes.RecordSerde();
        Serde<Optional<Record>> optionalRecordSerde = StreamsSerdes.OptionalSerde(Record.class);

        KTable<Pair<String, SnapshotDef>, Optional<Record>> volsByCutFlat = volsByCut
                .flatMap(
                        (k,v) -> {
                            List<KeyValue<Pair<String,SnapshotDef>,Record>> results = new ArrayList<>();
                            for(SnapshotDef s : v.value2.getSnapshotDefs()) {

                                results.add(new KeyValue<>(new Pair<>(v.value1.getKey(), s) , v.value1));
                            }
                            return results;
                        })
                .groupByKey(Serialized.with(pairSerde, recordSerde))
                .aggregate(Optional::empty,(k, v, agg) -> {
                    if(v.getEventTime().toLocalTime().compareTo(k.value2.getCutTime()) > 0) {
                        return agg;
                    }
                    else {
                        return Optional.of(v);
                    }

                }, Materialized.with(pairSerde, optionalRecordSerde));


        volsByCutFlat.toStream().print(Printed.toSysOut());


        return streamsBuilder.build();
    }
}
