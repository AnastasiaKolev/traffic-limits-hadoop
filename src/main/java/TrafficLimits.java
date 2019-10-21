import org.pcap4j.core.*;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.NifSelector;
import org.pcap4j.util.Packets;

import java.io.IOException;
import java.io.EOFException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

public class TrafficLimits {

    private static int totalPacketLength = 0;

    private static PcapNetworkInterface getNetworkDevice() {
        PcapNetworkInterface device = null;
        try {
            device = new NifSelector().selectNetworkInterface();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return device;
    }

    public static void main(String[] args) throws PcapNativeException, NotOpenException {
        // The code we had before
        PcapNetworkInterface device = getNetworkDevice();
        System.out.println( "You chose: " + device );

        // New code below here
        if (device == null) {
            System.out.println( "No device chosen." );
            System.exit( 1 );
        }

        // Open the device and get a handle
        int snapshotLength = 65536; // in bytes
        int readTimeout = 500; // in milliseconds
        final PcapHandle handle;
        handle = device.openLive( snapshotLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, readTimeout );


//        int max=1073741824;
//        int sum = 0;
//
//        // current = текущее время

        PacketListener listener = new PacketListener() {
            @Override
            public void gotPacket(PcapPacket pcapPacket) {
                int len = pcapPacket.length();

//                test();
//                t++;
//
//                // newCurrent = текущее время
//
//               /* if (newCurrent - current >= 5*//*минут*//*) {
//                    if (sum > max) {
//                        //alert
//                        sum = 0;
//                    } else {
//                        sum = 0;
//                    }
//                }*/
//
                totalPacketLength += len;
                System.out.println( "1.pcapPacket.length() "+ len );
                System.out.println( "2.handle.getTimestampPrecision() " + handle.getTimestampPrecision() );
                System.out.println( "3.pcapPacket "  + pcapPacket );
            }
        };

        try {
            int maxPackets = 50;
            handle.loop( maxPackets, listener );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Cleanup when complete
        handle.close();

//
//        InetAddress addr = InetAddress.getByName("192.168.10.100");
//        PcapNetworkInterface nif = Pcaps.getDevByAddress(addr);
//
//        int snapLen = 65536;
//        PcapNetworkInterface.PromiscuousMode mode = PcapNetworkInterface.PromiscuousMode.PROMISCUOUS;
//        int timeout = 10;
//        PcapHandle handle = nif.openLive(snapLen, mode, timeout);
//
//        Packet packet = handle.getNextPacketEx();
//        handle.close();

        System.out.println( "Total Packet Length " + totalPacketLength + " per " + readTimeout + " milliseconds");
//

//        IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
//        System.out.println( "4.packet.get(IpV4Packet.class) " + ipV4Packet );
//        Inet4Address srcAddr = ipV4Packet.getHeader().getSrcAddr();
//        System.out.println( "5.ipV4Packet.getHeader().getSrcAddr() " + srcAddr);
    }
}