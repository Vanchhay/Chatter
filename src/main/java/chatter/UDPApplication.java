package chatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.Scanner;

public class UDPApplication extends Thread{

	private final static Logger LOGGER = LoggerFactory.getLogger(UDPApplication.class);
	private static final String MULTICAST_INTERFACE = "lo";
	private static final String MULTICAST_IP = "239.0.0.0";
	private static final int BUFFER_SIZE = 1024;
	private static final int PORT = 9999;
	private static NetworkInterface NETWORK_INTERFACE;
	static {
		try{
			NETWORK_INTERFACE = NetworkInterface.getByName(MULTICAST_INTERFACE);
		}catch(SocketException e){
			LOGGER.info(e.getMessage());
		}
	}

	public static void main(String[] args) {
		UDPApplication reception = new UDPApplication();
		reception.start();

		ByteBuffer buffer;
		DatagramChannel dc = null;
		try{
			dc = DatagramChannel.open();
			dc.bind(null);
			dc.setOption(StandardSocketOptions.IP_MULTICAST_IF, NETWORK_INTERFACE);
		}catch(Exception e){
			LOGGER.info(e.getMessage());
		}

		Scanner reader = new Scanner(System.in);
		System.out.println("Chatter");
		while(true){
			try{
				/* Sender */
				String msg = reader.nextLine();
				buffer = ByteBuffer.wrap(msg.getBytes());

				InetSocketAddress serverAddress = new InetSocketAddress(MULTICAST_IP,PORT);
				dc.send(buffer,serverAddress);
				buffer.clear();
			}catch(Exception e){
				LOGGER.info("Exception : " + e.getMessage());
			}
		}
	}

	@Override
	public void run(){
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		DatagramChannel dc = null;
		InetAddress group = null;
		try{
			dc = DatagramChannel.open();
			dc.bind(null);
			dc.setOption(StandardSocketOptions.IP_MULTICAST_IF, NETWORK_INTERFACE);

			group = InetAddress.getByName(MULTICAST_IP);
			dc = DatagramChannel.open(StandardProtocolFamily.INET)
					.setOption(StandardSocketOptions.SO_REUSEADDR, true)
					.bind(new InetSocketAddress(PORT))
					.setOption(StandardSocketOptions.IP_MULTICAST_IF, NETWORK_INTERFACE);
		}catch(Exception e){
			LOGGER.info(e.getMessage());
		}
		while(true) {
			try{
				MembershipKey key = dc.join(group, NETWORK_INTERFACE);
				while (true) {
					if (key.isValid()) {
						InetSocketAddress sa = (InetSocketAddress) dc.receive(buffer);
						buffer.flip();

						int limits = buffer.limit();
						byte bytes[] = new byte[limits];
						buffer.get(bytes,0, limits);
						String msg = new String(bytes);
						System.out.println(sa.getHostString()+ " : " + msg);
						buffer.clear();
					}
				}
			}catch(Exception e){
				LOGGER.info("Excetion Reception : " + e.getMessage());
				break;
			}finally {
				System.out.println("FINAL");
			}
		}
	}
}
