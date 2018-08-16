package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPApplication {

	private final static Logger LOGGER = LoggerFactory.getLogger(UDPApplication.class);

	public static void main(String[] args) {

		DatagramSocket serverSocket = null;
		try{
			serverSocket = new DatagramSocket(9876);
		}catch(SocketException e){
			e.printStackTrace();
		}
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		System.out.println("Chatter");
		while(true){
			try{

				while(true) {
					/* Sender */
					BufferedReader inFromUser =	new BufferedReader(new InputStreamReader(System.in));
					DatagramSocket clientSocket = new DatagramSocket();
					InetAddress IPAddress = InetAddress.getByName("localhost");

					String sentence = inFromUser.readLine();
					sendData = sentence.getBytes();

					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
					clientSocket.send(sendPacket);

					/* Receiver */
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					serverSocket.receive(receivePacket);
					sentence = new String(receivePacket.getData());
					System.out.println(sentence);
						/* Get client info */
					IPAddress = receivePacket.getAddress();
					int port = receivePacket.getPort();
					sendData = sentence.getBytes();

					sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
					serverSocket.send(sendPacket);
//					LOGGER.info("PACKET SENT");
				}
			}catch(Exception e){
				LOGGER.info("Exception : " +e.getMessage());
			}
		}
	}
}
