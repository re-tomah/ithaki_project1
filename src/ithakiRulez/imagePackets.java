package ithakiRulez;
import java.net.*;

import java.util.Arrays;

import java.io.*;

//Class that handles images requests
public class imagePackets {
	String clientRequest;
	int serverPort;
	int clientPort;
	int packetLength;
	static int imageCounter = 0;
	
	public imagePackets() {
		clientRequest = "";
		serverPort = 0;
		clientPort = 0;
		packetLength = 0;
	}
	
	public imagePackets(String clientRequest, int serverPort, int clientPort, int packetLength) {
		this.clientRequest = clientRequest;
		this.serverPort = serverPort;
		this.clientPort = clientPort;
		this.packetLength = packetLength;
	}
	
	//Method that based on clients request receives an Image and produces an image file
	public void getImagePackets() throws UnknownHostException, SocketException, IOException{
		DatagramSocket s = new DatagramSocket();
		byte[] txbuffer = clientRequest.getBytes();
		
		InetAddress hostAddress = InetAddress.getByName("155.207.18.208");
		System.out.println("Host name is: " + hostAddress.getHostName());
		System.out.println("IP address is: " + hostAddress.getHostAddress());
		
		DatagramPacket packet = new DatagramPacket(txbuffer, txbuffer.length, hostAddress, serverPort);
		DatagramSocket r = new DatagramSocket(clientPort);
		
		byte[] rxbuffer = new byte[packetLength];
		DatagramPacket receivedPacket = new DatagramPacket(rxbuffer, rxbuffer.length);
		
		s.send(packet);
		r.setSoTimeout(5000);
		
		File file = new File("outputImage.jpeg");
		OutputStream image = new FileOutputStream(file);
		boolean flow = clientRequest.contains("FLOW=ON");
		
		for(;;){
			try {	
				r.receive(receivedPacket);
				byte[] receivedData = receivedPacket.getData();
				int zerosIndex = receivedData.length-1;
				if(receivedData[receivedData.length-1] == 0) {if(receivedData[receivedData.length-2] == 0) {break;}}
				if(flow == true) {
					s.send(packet);
				}
				
				while((receivedData[zerosIndex] == 0) && (zerosIndex > (packetLength - 1))) {
					zerosIndex--;
				}
				
				byte[] imageData = Arrays.copyOfRange(receivedData, 0, zerosIndex+1);
				
				image.write(imageData);
				
			}catch(Exception e) {
				break;
			}
		}
		
		s.close();
		r.close();
		image.close();
		imageCounter++;
	}
}
