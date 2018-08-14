package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server {

	private final static Logger LOGGER = LoggerFactory.getLogger(Server.class);

	public static void main(String args[]) throws Exception
	{
		DatagramSocket serverSocket = new DatagramSocket(9876);
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		while(true)
		{
			/* Listen for client */
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			String sentence = new String( receivePacket.getData());
			LOGGER.info("RECEIVED: " + sentence);

			InetAddress IPAddress = receivePacket.getAddress();
			LOGGER.info("Client IP: " +IPAddress);

			int port = receivePacket.getPort();
			String capitalizedSentence = sentence.toUpperCase();
			sendData = capitalizedSentence.getBytes();
			DatagramPacket sendPacket =	new DatagramPacket(sendData, sendData.length, IPAddress, port);
			serverSocket.send(sendPacket);

			LOGGER.info("PACKET SENT");
		}
	}
}
