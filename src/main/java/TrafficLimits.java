
import groovy.lang.Tuple2;
import org.apache.hadoop.hive.llap.Row;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.StorageLevels;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.pcap4j.core.*;
import org.pcap4j.util.NifSelector;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class TrafficLimits {
    protected static JavaSparkContext sc;
    protected static SparkConf sparkConf;

    private static int totalPacketLength;
    private static int maxPacketLength = 1073741824;    // todo: get from hive
    private static int minPacketLength = 1024;          // todo: get from hive


    public static void main(String[] args) throws PcapNativeException, NotOpenException {
        setup( args );
        execute();
    }

    public static void setup(String[] args) {
        sparkConf = new SparkConf().setAppName( "Traffic_Limits" );
        sc = new JavaSparkContext( sparkConf );
    }

    public static void execute() throws PcapNativeException, NotOpenException {

        // Выбираем узел сети
        PcapNetworkInterface device = getNetworkDevice();
        System.out.println( "You chose: " + device );

        // Проверка существования узла
        if (device == null) {
            System.out.println( "No device chosen." );
            System.exit( 1 );
        }

        // Открываем узел и запускаем управляющий модуль handle
        int snapshotLength = 65536; // in bytes
        int readTimeout = 500;      // in milliseconds
        final PcapHandle handle;
        handle = device.openLive( snapshotLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, readTimeout );

        final long[] startTime = {System.currentTimeMillis()};

        // Create the context with a 5 minutes batch size
//        JavaStreamingContext ssc = new JavaStreamingContext(sc, Durations.minutes( 5 ));
//        JavaReceiverInputDStream<String> packets = ssc.socketStream( handle.getNextPacketEx() );

//        JavaReceiverInputDStream<String> lines = ssc.socketTextStream(
//                        args[0], Integer.parseInt(args[1]), StorageLevels.MEMORY_AND_DISK_SER);
//        JavaDStream<String> words = lines.flatMap( x -> Arrays.asList(SPACE.split(x)).iterator());
//        JavaPairDStream<String, Integer> wordCounts = words.mapToPair( s -> new Tuple2<>(s, 1))
//                        .reduceByKey((i1, i2) -> i1 + i2);
//
//        wordCounts.print();
//        ssc.start();
//        ssc.awaitTermination();

        PacketListener listener = new PacketListener() {
            @Override
            public void gotPacket(PcapPacket pcapPacket) {
                int len = pcapPacket.length();
                totalPacketLength += len;
                
                // захват трафика за 5 минут
                if (startTime[0] > 5 * 60 * 1000) {
                    if (totalPacketLength < minPacketLength) {
                        // send alert to Kafka
                        sendAlert();

                        System.out.println( "total.length() is less than min value " + totalPacketLength );
                    }
                    if (totalPacketLength > maxPacketLength) {
                        // send alert to Kafka
                        sendAlert();

                        System.out.println( "total.length() is higher than max value " + totalPacketLength );
                    }
                    totalPacketLength = 0;
                    startTime[0] = System.currentTimeMillis();
                }

                System.out.println( "1.pcapPacket.length() " + len );
                System.out.println( "2.handle.getTimestampPrecision() " + handle.getTimestampPrecision() );
                System.out.println( "3.pcapPacket " + pcapPacket );
            }
        };

        try {
            int maxPackets = 0;     // 0 = INFINITE
            handle.loop( maxPackets, listener );

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Cleanup when complete
        handle.close();

        System.out.println( "Total Packet Length " + totalPacketLength
                + " per " + readTimeout + " milliseconds" );
    }

    // todo: select network interface by args
    private static PcapNetworkInterface getNetworkDevice() {
        PcapNetworkInterface device = null;
        try {
            device = new NifSelector().selectNetworkInterface();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return device;
    }

    // todo: send alert to Kafka (example)
    public static void sendAlert() {
//        DataFrame<Row> df = spark
//                .write()
//                .format( "kafka" )
//                .option( "kafka.bootstrap.servers", "host1:port1,host2:port2" );
//
//        StreamingQuery ds = df
//                .selectExpr( "CAST(key AS STRING)", "CAST(value AS STRING)" )
//                .writeStream()
//                .format( "kafka" )
//                .option( "kafka.bootstrap.servers", "host1:port1,host2:port2" )
//                .option( "topic", "alerts" )
//                .start();
    }


    // todo: example for read limits from Hive
    private static void readLimits() {
        String warehouseLocation = new File("spark-warehouse").getAbsolutePath();
        SparkSession spark = SparkSession
                .builder()
                .appName( "Java Spark Hive Example" )
                .config( "spark.sql.warehouse.dir", warehouseLocation )
                .enableHiveSupport()
                .getOrCreate();

        spark.sql( "SELECT * FROM limits_per_hour" );
    }

    // todo: example for write limits to Hive
    private static void writeLimits() {
        String warehouseLocation = new File("spark-warehouse").getAbsolutePath();
        SparkSession spark = SparkSession
                .builder()
                .appName( "Java Spark Hive Example" )
                .config( "spark.sql.warehouse.dir", warehouseLocation )
                .enableHiveSupport()
                .getOrCreate();

        spark.sql( "INSERT INTO TABLE limits_per_hour VALUES ..." );
    }
}
