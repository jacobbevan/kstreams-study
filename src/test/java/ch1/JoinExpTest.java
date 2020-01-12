package ch1;

import model.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.apache.kafka.test.ProcessorTopologyTestDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rocksdb.Snapshot;
import util.StreamsSerdes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class JoinExpTest {


    private ProcessorTopologyTestDriver topologyTestDriver;

    @BeforeEach
    public  void setUp() {
        Properties props = new Properties();
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "FirstZmart-Kafka-Streams-Client");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "zmart-purchases");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "FirstZmart-Kafka-Streams-App");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);

        StreamsConfig streamsConfig = new StreamsConfig(props);
        Topology topology = JoinExperiment.build();

        topologyTestDriver = new ProcessorTopologyTestDriver(streamsConfig, topology);
    }



    @Test
    public void testTupleSerde() {

        Serde<Pair<String,Double>> serde = StreamsSerdes.PairSerde(String.class, Double.class);


        Pair<String,Double> p = new  Pair<>("abc", 1.0);

        byte[] bytes = serde.serializer().serialize("a",p);

        Pair<String,Double> p2 = serde.deserializer().deserialize("a",bytes);
    }



    @Test
    public void testJoin() {


        Serde<Record> recordSerde = StreamsSerdes.RecordSerde();
        Serde<String> stringSerde = Serdes.String();


        LocalDateTime uploadTime = LocalDateTime.of(2020,01,02,16,00,00);
        LocalDate date = uploadTime.toLocalDate();

        List<Record> rates = new ArrayList<>();


        SnapshotDef devon = new SnapshotDef("DEVON", LocalTime.of(16,00),  Arrays.asList("VOLS.EURUSD", "VOLS.EURCAD"));
        SnapshotDef apac = new SnapshotDef("JAPAN", LocalTime.of(9,30),  Arrays.asList("VOLS.EURUSD", "VOLS.USDJPY"));


        SnapshotConfig snapshotConfig = new SnapshotConfig(Arrays.asList(devon,apac));


        Record r1 = new Record("VOLS.EURUSD", 0.9,  LocalDateTime.of(2020,01,02,03,00,00));
        Record r2 = new Record("VOLS.CADJPY", 0.8, LocalDateTime.of(2020,01,02,03,33,00));
        Record r3 = new Record("VOLS.EURUSD", 0.8, LocalDateTime.of(2020,01,02,12,33,00));

        topologyTestDriver.process("snapshotConfig",
                "any",
                snapshotConfig ,
                stringSerde.serializer(),
                StreamsSerdes.SnapshotConfigSerde().serializer());



        topologyTestDriver.process("vols",
                "any",
                r1,
                stringSerde.serializer(),
                recordSerde.serializer());


        topologyTestDriver.process("vols",
                "any",
                r2,
                stringSerde.serializer(),
                recordSerde.serializer());


        topologyTestDriver.process("vols",
                "any",
                r3,
                stringSerde.serializer(),
                recordSerde.serializer());


        /*
        ProducerRecord<KeyAtDiscreteTime, Record> r1 = topologyTestDriver.readOutput("rates",
                StreamsSerdes.KeyAtDiscreteTimeSerde().deserializer(),
                StreamsSerdes.RecordSerde().deserializer());

        ProducerRecord<KeyAtDiscreteTime, Record> r2 = topologyTestDriver.readOutput("rates",
                StreamsSerdes.KeyAtDiscreteTimeSerde().deserializer(),
                StreamsSerdes.RecordSerde().deserializer());

        System.out.print(r1.key());
        System.out.print(r2.value());
        //System.out.print(r2);

        assertThat(r1.key(), equalTo("YC.EUR"));
        assertThat(r2.key(), equalTo("YC.CAD"));
*/
    }
}
