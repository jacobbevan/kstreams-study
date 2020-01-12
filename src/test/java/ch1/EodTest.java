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
import util.StreamsSerdes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class EodTest {


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
        Topology topology = Eod.build();

        topologyTestDriver = new ProcessorTopologyTestDriver(streamsConfig, topology);
    }



    @Test
    @DisplayName("Testing the ZMart Topology Flow")
    public void testZMartTopology() {

        Serde<RateFile> rateFileSerde = StreamsSerdes.RateFileSerde();
        Serde<String> stringSerde = Serdes.String();


        LocalDateTime uploadTime = LocalDateTime.of(2020,01,02,16,00,00);
        LocalDate date = uploadTime.toLocalDate();

        List<Record> rates = new ArrayList<>();

        rates.add(new Record("YC.EUR", 0.9, uploadTime));
        rates.add(new Record("YC.CAD", 0.8, uploadTime));

        RateFile file = new RateFile("DEVON", date, uploadTime, rates);

        topologyTestDriver.process(Eod.RATE_FILE_TOPIC,
                null,
                file,
                stringSerde.serializer(),
                rateFileSerde.serializer());

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

    }


/*
    @Test
    public void testEoDConfig() {

        Serde<SnapshotDef> eodDefSerde = StreamsSerdes.EodDefSerde();
        Serde<String> stringSerde = Serdes.String();

        SnapshotDef def1 = new SnapshotDef("DEVON", Arrays.asList("YC.EUR", "VOL.EURUSD", "YC.USD"));
        SnapshotDef def2 = new SnapshotDef("APAC", Arrays.asList("YC.JPY", "VOL.USDJPY", "YC.USD"));



        topologyTestDriver.process(Eod.EOD_CONFIG_TOPIC,
                def1.getCut(),
                def1,
                stringSerde.serializer(),
                eodDefSerde.serializer());

        topologyTestDriver.process(Eod.EOD_CONFIG_TOPIC,
                def2.getCut(),
                def2,
                stringSerde.serializer(),
                eodDefSerde.serializer());

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

    }
*/

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


        Record r1 = new Record("VOLS.EURUSD", 0.9, uploadTime);
        Record r2 = new Record("VOLS.CADJPY", 0.8, uploadTime);

        StringList cuts = new StringList(Arrays.asList("DEVON", "APAC"));


        topologyTestDriver.process("eodConfig",
                null,
                cuts,
                stringSerde.serializer(),
                StreamsSerdes.StringListSerde().serializer());

        topologyTestDriver.process("vols",
                null,
                r1,
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
