package ithakiRulez;
import java.net.*;
import java.util.ArrayList;
import java.io.*;

//Class that handles echo requests
public class echoPackets {
	String clientRequest;
	int serverPort;
	int clientPort;
	
	public echoPackets() {
		clientRequest = "";
		serverPort = 0;
		clientPort = 0;
	}
	
	public echoPackets(String clientRequest, int serverPort, int clientPort) {
		this.clientRequest = clientRequest;
		this.serverPort = serverPort;
		this.clientPort = clientPort;
	}
	
	//Method that receives echo packets given the right client request
	public void getEchoPackets() throws UnknownHostException, SocketException, IOException{
		
		ArrayList<Long> timeDelay = new ArrayList<Long>();
		long currentTime, endTime;
		currentTime = System.currentTimeMillis();
		endTime = (clientRequest.contains("T")?(System.currentTimeMillis() + (200)):((System.currentTimeMillis()) + (4 * 60 * 1000)));
		
		DatagramSocket s = new DatagramSocket();
		byte[] txbuffer = clientRequest.getBytes();
		
		InetAddress hostAddress = InetAddress.getByName("155.207.18.208");
		System.out.println("Host name is: " + hostAddress.getHostName());
		System.out.println("IP address is: " + hostAddress.getHostAddress());
		
		DatagramPacket packet = new DatagramPacket(txbuffer, txbuffer.length, hostAddress, serverPort);
		DatagramSocket r = new DatagramSocket(clientPort);
		
		//without T00 => byte[23]
		//with T00 => byte[54]
		byte[] rxbuffer = new byte[60];
		DatagramPacket receivedPacket = new DatagramPacket(rxbuffer, rxbuffer.length);
		PrintWriter messagePrint;
		
		if(clientRequest.contains("T"))
			messagePrint = new PrintWriter("messageTemp.txt");
		else
			if(clientRequest.contains("0000"))
				messagePrint = new PrintWriter("messageEchoNoDelay.txt");
			else
				messagePrint = new PrintWriter("messageEcho.txt");
		
		//Receive echo packet
		do {
			s.send(packet);
			r.setSoTimeout(3000);
			
			String message;
			
			for(;;) {
				try {
					r.receive(receivedPacket);
					message = new String(rxbuffer, 0, receivedPacket.getLength());
					if (message.indexOf("PSTOP") != -1) {
						System.out.println(message);
						messagePrint.println(message);
						break;
					}
				} catch (Exception x) {
					System.out.println(x);
					break;
				}
			}
			timeDelay.add((System.currentTimeMillis()- currentTime));
			currentTime = System.currentTimeMillis();	
		}while (currentTime < endTime);
		
		r.close();
		s.close();
		messagePrint.close();
		
		ArrayList<String> timeDelayString = new ArrayList<String>();
		for(int i = 0; i < timeDelay.size(); i++) 
			timeDelayString.add(timeDelay.get(i).toString());
		
		//Produce file containing Latency
		try {
		String fileName = (clientRequest.contains("T")?"echoTemperature":"echoOutput");
		fileName+=(clientRequest.contains("0000")?"NoLatency":"");
		fileName+=".txt";
		FileWriter writer = new FileWriter(fileName);	
		for(String str: timeDelayString) {							
			writer.write(str + System.lineSeparator());
		}
		writer.close();
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	//Method that runs for predetermined ammount of echo packets received (used right before every request)(testing purposes)
	public void wiresharkPackets() throws UnknownHostException, SocketException, IOException{
		
		DatagramSocket s = new DatagramSocket();
		byte[] txbuffer = clientRequest.getBytes();
		
		InetAddress hostAddress = InetAddress.getByName("155.207.18.208");
		
		DatagramPacket packet = new DatagramPacket(txbuffer, txbuffer.length, hostAddress, serverPort);
		DatagramSocket r = new DatagramSocket(clientPort);
		
		byte[] rxbuffer = new byte[60];
		DatagramPacket receivedPacket = new DatagramPacket(rxbuffer, rxbuffer.length);
		
		for(int times = 0; times < 5; times++) {
			s.send(packet);
			r.setSoTimeout(3000);
			String message;
			
			for(;;) {
				try {
					r.receive(receivedPacket);
					message = new String(rxbuffer, 0, receivedPacket.getLength());
					if (message.indexOf("PSTOP") != -1) {
						System.out.println(message);
						break;
					}
				} catch (Exception x) {
					System.out.println(x);
					break;
				}
			}
		}
		System.out.println("----------------");
		r.close();
		s.close();
	}
}
