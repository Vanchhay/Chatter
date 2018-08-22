package chatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.Enumeration;
import java.util.Scanner;

public class UDPApplication extends Thread{

	private final static Logger LOGGER = LoggerFactory.getLogger(UDPApplication.class);
	private static final int BUFFER_SIZE = 1024;
	private static final String HOST = "224.0.0.0";
	private static final int PORT = 9999;

	public static ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

	private static InetAddress group = null;
	private static DatagramChannel dc = null;
	private static NetworkInterface ni = null;
	static {
		Enumeration<NetworkInterface> en = null;
		try{
			en = NetworkInterface.getNetworkInterfaces();
		}catch(SocketException e){
			e.printStackTrace();
		}

		while (en.hasMoreElements()) {
			ni = en.nextElement();
			System.out.println("Network Interface Name: " +ni.getName());    // lo
			break;
		}
		try{
			group = InetAddress.getByName(HOST);
			dc = DatagramChannel.open(StandardProtocolFamily.INET)
					.setOption(StandardSocketOptions.SO_REUSEADDR, true)
					.bind(new InetSocketAddress(9999))
					.setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);
		}catch(Exception e){

		}
	}

	public static void main(String[] args) {
		UDPApplication reception = new UDPApplication();
		reception.start();

		Scanner reader = new Scanner(System.in);
		System.out.println("Chatter");
		while(true){
			try{
				/* Sender */
				String msg = reader.nextLine();
				buffer = ByteBuffer.wrap(msg.getBytes());
				InetSocketAddress serverAddress = new InetSocketAddress(HOST, PORT);

				dc.send(buffer, serverAddress);

			}catch(Exception e){
				LOGGER.info("Exception : " +e.getMessage());
			}
		}
	}

	@Override
	public void run(){
		while(true) {
			try{
				MembershipKey key = dc.join(group, ni);
				while (true) {
					if (key.isValid()) {
						buffer.clear();
						InetSocketAddress sa = (InetSocketAddress) dc.receive(buffer);
						buffer.flip();

						int limits = buffer.limit();
						byte bytes[] = new byte[limits];
						buffer.get(bytes,0, limits);
						String msg = new String(bytes);
						System.out.println(sa.getHostString()+ " : " + msg);
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
