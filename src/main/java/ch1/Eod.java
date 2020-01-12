package ch1;


import model.*;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import util.StreamsSerdes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Eod {


    public static final String RATE_FILE_TOPIC = "rateFile";
    public static final String EOD_CONFIG_TOPIC = "eodConfig";

    public static Topology build() {

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        //KTable<String, SnapshotDef> eodDefs = streamsBuilder.table("eodConfig", Consumed.with(Serdes.String(), StreamsSerdes.EodDefSerde()));

        //key string == cut
        //value string == key;
        KStream<String, CutKeyPair> eodDefs = streamsBuilder.stream("eodConfig", Consumed.with(Serdes.String(), StreamsSerdes.EodDefSerde()))
                .flatMapValues(
                        (value) -> {
                            List<CutKeyPair> result = new LinkedList<>();
                            for(String k : value.getInScope()) {
                                result.add(new CutKeyPair(value.getCut(), k));
                            }
                            return result;
                        });

        KGroupedStream<String,CutKeyPair> groupedStream = eodDefs.groupBy((k, v) -> v.getKey(), Serialized.with(Serdes.String(), StreamsSerdes.CutKeyPairSerde()));

        KTable<String, StringList> keyToEod = groupedStream
                .aggregate(StringList::new, (k, v, agg)-> {
                    agg.getValues().add(v.getCut());
                    return  agg;
                }, Materialized.with(Serdes.String(), StreamsSerdes.StringListSerde()));

        keyToEod.toStream().print(Printed.toSysOut());

        eodDefs.print(Printed.toSysOut());




        //use flatMap to decompose rate file to record per doc
        //TODO check repartitioning
        KStream<KeyAtDiscreteTime, Record> rateFiles =
                streamsBuilder
                        .stream(RATE_FILE_TOPIC, Consumed.with(Serdes.String(), StreamsSerdes.RateFileSerde()))
                        .flatMap(
                                (key, value) -> {
                                    List<KeyValue<KeyAtDiscreteTime, Record>> result = new LinkedList<>();
                                    for(Record kv : value.getRates()) {
                                        result.add(KeyValue.pair(new KeyAtDiscreteTime(kv.getKey(), value.getDate(), value.getCut()), kv));
                                    }
                                    return result;
                                });




        rateFiles.print(Printed.toSysOut());





        rateFiles.to("rates", Produced.with(StreamsSerdes.KeyAtDiscreteTimeSerde(), StreamsSerdes.RecordSerde()));





//        KStream<KeyAtDiscreteTime,Record> volStream = streamsBuilder.stream("vols", Consumed.with(Serdes.String(), StreamsSerdes.RecordSerde()))
//                .map(
//                        (key, value) -> KeyValue.pair(new KeyAtDiscreteTime(key,



        //KStream<String,Double> ratesStream = streamsBuilder.stream("rates", Consumed.with(Serdes.String(), Serdes.Double()));

        //KStream<String,Double> all = volStream.merge(ratesStream);

        //all.to("all", Produced.with(Serdes.String(), Serdes.Double()));

        //KTable<String,Double> candidates = streamsBuilder.table("all", Consumed.with(Serdes.String(), Serdes.Double()));


        return streamsBuilder.build();
    }
}
