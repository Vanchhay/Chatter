package chatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class UDPApplication {

	private final static Logger LOGGER = LoggerFactory.getLogger(UDPApplication.class);
	private static final int BUFFER_SIZE = 1024;
	private static final String HOST = "localhost";
	private static final int PORT = 9999;

	public static ByteBuffer buffer = null;
	public static DatagramChannel server = null;

	public static void main(String[] args) {

		if(serverIsInUsed(HOST, PORT)) {
			try{
				server = DatagramChannel.open();
				InetSocketAddress sAddr = new InetSocketAddress(HOST,PORT);
				server.bind(sAddr);
				buffer = ByteBuffer.allocate(BUFFER_SIZE);
			}catch(Exception e){

			}
		}
		Scanner reader = new Scanner(System.in);
		System.out.println("Chatter");
		DatagramChannel client;
		while(true){
			try{
				/* Client */
				client = DatagramChannel.open();
				client.bind(null);

				System.out.print("You : ");
				String msg = reader.nextLine();

				buffer = ByteBuffer.wrap(msg.getBytes());
				InetSocketAddress serverAddress = new InetSocketAddress(HOST, PORT);

				client.send(buffer, serverAddress);
				buffer.clear();
				/* Stream message to server */
				streamToServer();
			}catch(Exception e){
				LOGGER.info("Exception : " +e.getMessage());
			}
		}
	}

	public static void streamToServer(){
		try{
			SocketAddress remoteAddr = server.receive(buffer);
			buffer.flip();
			int limits = buffer.limit();
			byte bytes[] = new byte[limits];
			buffer.get(bytes, 0, limits);
			String message = new String(bytes);
		}catch(Exception e){

		}
	}

	public static boolean serverIsInUsed(String host, int port){
		try{
			DatagramChannel server = DatagramChannel.open();
			InetSocketAddress sAddr = new InetSocketAddress(host, port);
			server.bind(sAddr);
			server.close();
			return true;
		}catch(Exception e){
			return false;
		}
	}

}
