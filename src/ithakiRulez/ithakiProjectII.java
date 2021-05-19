///*
//Stavros Sentonas
//ΑΕΜ: 9386
//*/
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.FileWriter;
//import java.io.OutputStream;
//import java.io.PrintWriter;
//
//import javax.sound.sampled.AudioFormat;
//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.LineUnavailableException;
//import javax.sound.sampled.SourceDataLine;
//
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.Socket;
//import java.net.SocketException;
//import java.net.UnknownHostException;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//
//public class ithakiRulez {
//
//	
//	//Main class containing the basic GUI
//	public static void main(String[] args) throws IOException, LineUnavailableException{
//		
//		String clientRequest, serverPort, clientPort;
//		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//		System.out.println("Server Port: ");serverPort = in.readLine().replaceAll("\\s+", "");
//		System.out.println("Client Port: ");clientPort = in.readLine().replaceAll("\\s+", "");
//		echoPackets wireShark = new echoPackets("E0000", Integer.parseInt(serverPort)
//											, Integer.parseInt(clientPort));
//		
//		for(;;) {
//			System.out.println("----------------\n");
//			System.out.println("Client Request: ");clientRequest = in.readLine().replaceAll("\\s+", "");
//			if(clientRequest.isEmpty()){
//				System.out.println("Happy to assist you. \nByeeeeeee!!!!!");
//				break;
//			} else if(clientRequest.charAt(0) == 'E') {
//				echoPackets packet = new echoPackets(clientRequest, Integer.parseInt(serverPort)
//						, Integer.parseInt(clientPort));
//				packet.getEchoPackets();
//			} else if(clientRequest.charAt(0) == 'M') {
//				String temp;
//				System.out.println("Camera's Position(PTZ or FIX): ");temp = in.readLine().replaceAll("\\s+", "").toUpperCase();
//				if(temp.equals("PTZ") || (temp.equals("FIX"))) {
//					clientRequest+=("CAM=" + temp);
//					if(temp.equals("PTZ")) {
//						System.out.println("Camera's Direction(L, U, R, D, M, C): ");temp = in.readLine().replaceAll("\\s+", "").toUpperCase();
//						if(temp.equals("L") || temp.equals("U") || temp.equals("R") || temp.equals("D") || temp.equals("M") || temp.equals("C")) {
//							clientRequest+=("DIR=" + temp);
//						}else {System.out.println("Oops, there's no such kind of direction");}
//						System.out.println("Flow (on or off)");temp = in.readLine().replaceAll("\\s+", "").toUpperCase();
//						if(temp.equals("ON") || temp.equals("OFF")) {
//							clientRequest+=("FLOW=" + temp);
//						}else {System.out.println("Oops, there's no such kind of flow");}
//						System.out.println("Packet Length(128, 256, 512, 1024): ");temp = in.readLine().replaceAll("\\s+", "").toUpperCase();
//						if(temp.equals("128") || temp.equals("256") || temp.equals("512") || temp.equals("1024")) {
//							clientRequest+=("UDP=" + temp);
//						}else {temp = "128";}		
//					}else {temp = "128";}
//				}else {
//					System.out.println("Packet Length(128, 256, 512, 1024): ");temp = in.readLine().replaceAll("\\s+", "").toUpperCase();
//					if(temp == "128" || temp == "256" || temp == "512" || temp == "1024") {
//						clientRequest+=("UDP=" + temp);
//					}else {temp = "128";}
//				}
//				wireShark.wiresharkPackets();
//				imagePackets packet = new imagePackets(clientRequest, Integer.parseInt(serverPort)
//													,Integer.parseInt(clientPort), Integer.parseInt(temp));
//				packet.getImagePackets();
//				
//			} else if(clientRequest.charAt(0) == 'A') {
//				String temp;
//				System.out.println("AQ for AQ-DPCM\nPress nothing for normal DPCM");temp = in.readLine().replaceAll("\\s+", "").toUpperCase();
//				if(temp.equals("AQ") || temp.equals("")) {
//					clientRequest+=temp;
//				}else {System.out.println("Normal DPCM selected by default");}
//				
//				if(clientRequest.contains("AQ") == false) {
//					System.out.println("T for frequency\nF for audio clip");temp = in.readLine().replaceAll("\\s+", "").toUpperCase();
//					if(temp.equals("F") || temp.equals("T")) {
//						String choice = temp;
//						if(temp.equals("F")) {
//							System.out.println("Enter the number of the audio clip you want To hear\nEntering nothing will play a random song");
//							temp = in.readLine().replaceAll("\\s+", "").toUpperCase();
//							if(temp.equals("") == false) {
//								clientRequest+=("L"+temp);
//							}
//						}
//						clientRequest+=(choice+"999");
//					}else {
//						System.out.println("Normal DPCM selected by default");
//						clientRequest+="F999";
//					}
//				}else {
//					clientRequest+="F999";
//				}
//				
//				audioPackets packet = new audioPackets(clientRequest, Integer.parseInt(serverPort)
//					, Integer.parseInt(clientPort));
//				if(clientRequest.contains("AQ")) {
//					wireShark.wiresharkPackets();
//					packet.getQAudioPacket();
//				}else {
//					wireShark.wiresharkPackets();
//					packet.getAudioPackets();
//				}
//			} else if(clientRequest.charAt(0) == 'Q'){
//				String flightLevel, leftMotor, rightMotor;
//				for(int i = 0; i < 2; i++) {
//					do{
//						System.out.println("Flight Level: ");flightLevel = in.readLine().replaceAll("\\s+", "");
//						System.out.println("Left Motor: ");leftMotor = in.readLine().replaceAll("\\s+", "");
//						System.out.println("Right Motor: ");rightMotor = in.readLine().replaceAll("\\s+", "");
//					}while(flightLevel.length() != 3 || leftMotor.length() != 3 || rightMotor.length() != 3);
//					
//					tcpPackets packet = new tcpPackets( "AUTO FLIGHTLEVEL=" 
//														+ String.valueOf(flightLevel) 
//														+ " LMOTOR=" + String.valueOf(leftMotor) 
//														+ " RMOTOR=" + String.valueOf(rightMotor) + " PILOT \r\n");
//					wireShark.wiresharkPackets();
//					packet.getCopterPackets();
//				}
//			}else if(clientRequest.charAt(0) == 'V') {
//				tcpPackets packet = new tcpPackets(clientRequest);
//				wireShark.wiresharkPackets();
//				packet.getOBDPackets();
//			}else {
//				System.out.println("Please try again, with a valid input.");
//			}
//		}
//	}
//
//}
//
//
////Class that handles echo requests
//public class echoPackets {
//	String clientRequest;
//	int serverPort;
//	int clientPort;
//	
//	public echoPackets() {
//		clientRequest = "";
//		serverPort = 0;
//		clientPort = 0;
//	}
//	
//	public echoPackets(String clientRequest, int serverPort, int clientPort) {
//		this.clientRequest = clientRequest;
//		this.serverPort = serverPort;
//		this.clientPort = clientPort;
//	}
//	
//	//Method that receives echo packets given the right client request
//	public void getEchoPackets() throws UnknownHostException, SocketException, IOException{
//		
//		ArrayList<Long> timeDelay = new ArrayList<Long>();
//		long currentTime, endTime;
//		currentTime = System.currentTimeMillis();
//		endTime = (clientRequest.contains("T")?(System.currentTimeMillis() + (200)):((System.currentTimeMillis()) + (4 * 60 * 1000)));
//		
//		DatagramSocket s = new DatagramSocket();
//		byte[] txbuffer = clientRequest.getBytes();
//		
//		InetAddress hostAddress = InetAddress.getByName("155.207.18.208");
//		System.out.println("Host name is: " + hostAddress.getHostName());
//		System.out.println("IP address is: " + hostAddress.getHostAddress());
//		
//		DatagramPacket packet = new DatagramPacket(txbuffer, txbuffer.length, hostAddress, serverPort);
//		DatagramSocket r = new DatagramSocket(clientPort);
//		
//		//without T00 => byte[23]
//		//with T00 => byte[54]
//		byte[] rxbuffer = new byte[60];
//		DatagramPacket receivedPacket = new DatagramPacket(rxbuffer, rxbuffer.length);
//		PrintWriter messagePrint;
//		
//		if(clientRequest.contains("T"))
//			messagePrint = new PrintWriter("messageTemp.txt");
//		else
//			if(clientRequest.contains("0000"))
//				messagePrint = new PrintWriter("messageEchoNoDelay.txt");
//			else
//				messagePrint = new PrintWriter("messageEcho.txt");
//		
//		//Receive echo packet
//		do {
//			s.send(packet);
//			r.setSoTimeout(3000);
//			
//			String message;
//			
//			for(;;) {
//				try {
//					r.receive(receivedPacket);
//					message = new String(rxbuffer, 0, receivedPacket.getLength());
//					if (message.indexOf("PSTOP") != -1) {
//						System.out.println(message);
//						messagePrint.println(message);
//						break;
//					}
//				} catch (Exception x) {
//					System.out.println(x);
//					break;
//				}
//			}
//			timeDelay.add((System.currentTimeMillis()- currentTime));
//			currentTime = System.currentTimeMillis();	
//		}while (currentTime < endTime);
//		
//		r.close();
//		s.close();
//		messagePrint.close();
//		
//		ArrayList<String> timeDelayString = new ArrayList<String>();
//		for(int i = 0; i < timeDelay.size(); i++) 
//			timeDelayString.add(timeDelay.get(i).toString());
//		
//		//Produce file containing Latency
//		try {
//		String fileName = (clientRequest.contains("T")?"echoTemperature":"echoOutput");
//		fileName+=(clientRequest.contains("0000")?"NoLatency":"");
//		fileName+=".txt";
//		FileWriter writer = new FileWriter(fileName);	
//		for(String str: timeDelayString) {							
//			writer.write(str + System.lineSeparator());
//		}
//		writer.close();
//		}catch(Exception e) {
//			System.out.println(e);
//		}
//	}
//	
//	//Method that runs for predetermined ammount of echo packets received (used right before every request)(testing purposes)
//	public void wiresharkPackets() throws UnknownHostException, SocketException, IOException{
//		
//		DatagramSocket s = new DatagramSocket();
//		byte[] txbuffer = clientRequest.getBytes();
//		
//		InetAddress hostAddress = InetAddress.getByName("155.207.18.208");
//		
//		DatagramPacket packet = new DatagramPacket(txbuffer, txbuffer.length, hostAddress, serverPort);
//		DatagramSocket r = new DatagramSocket(clientPort);
//		
//		byte[] rxbuffer = new byte[60];
//		DatagramPacket receivedPacket = new DatagramPacket(rxbuffer, rxbuffer.length);
//		
//		for(int times = 0; times < 5; times++) {
//			s.send(packet);
//			r.setSoTimeout(3000);
//			String message;
//			
//			for(;;) {
//				try {
//					r.receive(receivedPacket);
//					message = new String(rxbuffer, 0, receivedPacket.getLength());
//					if (message.indexOf("PSTOP") != -1) {
//						System.out.println(message);
//						break;
//					}
//				} catch (Exception x) {
//					System.out.println(x);
//					break;
//				}
//			}
//		}
//		System.out.println("----------------");
//		r.close();
//		s.close();
//	}
//}
//
//
////Class that handles images requests
//public class imagePackets {
//	String clientRequest;
//	int serverPort;
//	int clientPort;
//	int packetLength;
//	static int imageCounter = 0;
//	
//	public imagePackets() {
//		clientRequest = "";
//		serverPort = 0;
//		clientPort = 0;
//		packetLength = 0;
//	}
//	
//	public imagePackets(String clientRequest, int serverPort, int clientPort, int packetLength) {
//		this.clientRequest = clientRequest;
//		this.serverPort = serverPort;
//		this.clientPort = clientPort;
//		this.packetLength = packetLength;
//	}
//	
//	//Method that based on clients request receives an Image and produces an image file
//	public void getImagePackets() throws UnknownHostException, SocketException, IOException{
//		DatagramSocket s = new DatagramSocket();
//		byte[] txbuffer = clientRequest.getBytes();
//		
//		InetAddress hostAddress = InetAddress.getByName("155.207.18.208");
//		System.out.println("Host name is: " + hostAddress.getHostName());
//		System.out.println("IP address is: " + hostAddress.getHostAddress());
//		
//		DatagramPacket packet = new DatagramPacket(txbuffer, txbuffer.length, hostAddress, serverPort);
//		DatagramSocket r = new DatagramSocket(clientPort);
//		
//		byte[] rxbuffer = new byte[packetLength];
//		DatagramPacket receivedPacket = new DatagramPacket(rxbuffer, rxbuffer.length);
//		
//		s.send(packet);
//		r.setSoTimeout(5000);
//		
//		File file = new File("outputImage.jpeg");
//		OutputStream image = new FileOutputStream(file);
//		boolean flow = clientRequest.contains("FLOW=ON");
//		
//		for(;;){
//			try {	
//				r.receive(receivedPacket);
//				byte[] receivedData = receivedPacket.getData();
//				int zerosIndex = receivedData.length-1;
//				if(receivedData[receivedData.length-1] == 0) {if(receivedData[receivedData.length-2] == 0) {break;}}
//				if(flow == true) {
//					s.send(packet);
//				}
//				
//				while((receivedData[zerosIndex] == 0) && (zerosIndex > (packetLength - 1))) {
//					zerosIndex--;
//				}
//				
//				byte[] imageData = Arrays.copyOfRange(receivedData, 0, zerosIndex+1);
//				
//				image.write(imageData);
//				
//			}catch(Exception e) {
//				break;
//			}
//		}
//		
//		s.close();
//		r.close();
//		image.close();
//		imageCounter++;
//	}
//}
//
//
////Class that handles audio requests
//public class audioPackets {
//	String clientRequest;
//	int serverPort;
//	int clientPort;
//	
//	public audioPackets() {
//		clientRequest = "";
//		serverPort = 0;
//		clientPort = 0;
//	}
//	
//	public audioPackets(String clientRequest, int serverPort, int clientPort) {
//		this.clientRequest = clientRequest;
//		this.serverPort = serverPort;
//		this.clientPort = clientPort;
//	}
//
//	//Method in charge of receiving DPCM audio packets and producing files containing useful info
//	public void getAudioPackets() throws UnknownHostException, SocketException, IOException, LineUnavailableException{
//		DatagramSocket s = new DatagramSocket();
//		byte[] txbuffer = clientRequest.getBytes();
//		
//		ArrayList<Long> timeDelay = new ArrayList<Long>();
//		InetAddress hostAddress = InetAddress.getByName("155.207.18.208");
//		System.out.println("Host name is: " + hostAddress.getHostName());
//		System.out.println("IP address is: " + hostAddress.getHostAddress());
//		
//		DatagramPacket packet = new DatagramPacket(txbuffer, txbuffer.length, hostAddress, serverPort);
//		DatagramSocket r = new DatagramSocket(clientPort);
//		
//		int packetSize = 128;
//		byte[] rxbuffer = new byte[packetSize];
//		DatagramPacket receivedPacket = new DatagramPacket(rxbuffer, rxbuffer.length);
//		
//		int soundPackets = clientRequest.contains("F")?Integer.parseInt(clientRequest.substring(9, 12)):Integer.parseInt(clientRequest.substring(6, 9));
//		int initialSize = 128 * 2 * soundPackets;
//		int[] nibles = new int[initialSize];
//		
//		s.send(packet);
//		r.setSoTimeout(5000);
//		byte[] receivedData = new byte[packetSize * soundPackets];
//		int index = 0;
//		
//		//Audio received in an Array
//		long currentTime = System.currentTimeMillis();
//		for(;;) {
//			try {
//				r.receive(receivedPacket);
//				for(int j = 0; j < 128; j++)
//					receivedData[index*128 + j] = rxbuffer[j];
//				index++;
//			}catch (Exception e) {
//				break;
//			}
//			timeDelay.add((System.currentTimeMillis()- currentTime));
//			currentTime = System.currentTimeMillis();	
//		}
//		
//		AudioFormat dcpm = new AudioFormat(8000, 8, 1, true, false);
//		SourceDataLine line = AudioSystem.getSourceDataLine(dcpm);
//		line.open(dcpm, initialSize);
//		
//		//Extract audio nibles in another array
//		for (int i = 0; i < packetSize * soundPackets; i++) {
//			int k = (int) receivedData[i];
//			nibles[2*i + 1] = ((k & 0xF) - 8);			
//			nibles[2*i] = (((k >> 4) & 0xF) - 8);
//		}
//		
//		int[] soundData = new int[initialSize];
//		byte[] audioOutput = new byte[initialSize];
//		
//		//Transform nibles data to audio data for SourceDataLine to read from
//		soundData[0] = 0;
//		audioOutput[0] = (byte) (soundData[0]);
//		for(int i = 1; i < initialSize; i++) {
//			soundData[i]   =  nibles[i] + soundData[i-1];
//			audioOutput[i] = (byte) ( soundData[i] & 0x00FF );
//		}
//		
//		soundData[0] = nibles[0] + soundData[255];
//		audioOutput[0] = (byte) (soundData[0] & 0x00FF);
//		
//		String[] timeDelayString = new String[timeDelay.size()];
//		for(int i = 0; i < timeDelay.size(); i++) 
//			timeDelayString[i] = timeDelay.get(i).toString();
//
//		//Produce files we need
//		try {
//			FileWriter writer = new FileWriter(clientRequest.contains("F")?"audioOutputDelay.txt":"frequencyOutputDelay.txt");			
//			for(String str: timeDelayString) {							
//				writer.write(str + System.lineSeparator());
//			}
//			
//			FileWriter writer2 = new FileWriter(clientRequest.contains("F")?"audioDiffs.txt":"frequencyDiffs.txt");			
//			for(int num: nibles) {							
//				writer2.write(num + System.lineSeparator());
//			}
//			
//			FileWriter writer3 = new FileWriter(clientRequest.contains("F")?"audioSamples.txt":"frequencySamples.txt");			
//			for(int num: audioOutput) {							
//				writer3.write(num + System.lineSeparator());
//			}
//			writer.close();
//			writer2.close();
//			writer3.close();
//		}catch(Exception e) {
//			System.out.println(e);
//		}
//		
//		//Play audio
//		System.out.println("Music Playing in the background retard");
//		line.start();
//		line.write(audioOutput, 0, initialSize);
//		line.drain();
//		
//		line.close();
//		s.close();
//		r.close();
//		
//	}
//	
//	//Method in charge of receiving AQ-DPCM audio packets and producing files containing useful info
//	public void getQAudioPacket() throws UnknownHostException, SocketException, IOException, LineUnavailableException{
//		
//		DatagramSocket s = new DatagramSocket();
//		byte[] txbuffer = clientRequest.getBytes();
//		ArrayList<Long> timeDelay = new ArrayList<Long>();
//		
//		InetAddress hostAddress = InetAddress.getByName("155.207.18.208");
//		System.out.println("Host name is: " + hostAddress.getHostName());
//		System.out.println("IP address is: " + hostAddress.getHostAddress());
//		
//		DatagramPacket packet = new DatagramPacket(txbuffer, txbuffer.length, hostAddress, serverPort);
//		DatagramSocket r = new DatagramSocket(clientPort);
//		
//		int packetSize = 132;
//		byte[] rxbuffer = new byte[packetSize];
//		DatagramPacket receivedPacket = new DatagramPacket(rxbuffer, rxbuffer.length);
//		
//		int soundPackets = Integer.parseInt(clientRequest.substring(8,11));
//		int initialSize = 128 * 2 * soundPackets;
//		int[][] nibles = new int[128 * 2][soundPackets];
//		int[] soundData = new int[initialSize];
//		int[] means = new int[soundPackets];
//		int[] betas = new int[soundPackets];
//		byte[] audioOutput = new byte[2 * soundData.length];
//		
//		s.send(packet);
//		r.setSoTimeout(5000);
//		byte[][] receivedData = new byte[soundPackets][packetSize];
//		
//		System.out.println("Loading...");
//		
//		//Receive dataStream
//		long currentTime = System.currentTimeMillis();
//		int counter = 0;
//		for(;;) {
//			try {
//				r.receive(receivedPacket);
//				for(int i = 0; i < rxbuffer.length; i++)
//					receivedData[counter][i] = rxbuffer[i];
//				counter++;	
//			}catch(Exception e) {break;}
//			
//			timeDelay.add((System.currentTimeMillis()- currentTime));
//			currentTime = System.currentTimeMillis();
//		}
//	
//		//Mean and Step
//		int meanMSB, meanLSB, betaMSB, betaLSB;
//		for(int i = 0; i < soundPackets; i++) {
//			meanLSB = receivedData[i][0] & 0xFF;
//			meanMSB = ((receivedData[i][1]) << 8);
//			means[i] = meanMSB | meanLSB;
//			betaLSB = receivedData[i][2] & 0xFF;
//			betaMSB = ((receivedData[i][3] & 0xFF) << 8);
//			betas[i] = betaMSB | betaLSB;
//			
//			//Nibles
//			for(int j = 4; j < 132; j++) {
//				nibles[2*j - 8][i] = (((receivedData[i][j] >> 4) & 0xF) - 8);
//				nibles[2*j - 7][i] = ((receivedData[i][j] & 0xF) - 8);
//			}
//		}
//		
//		//SoundData and AudioOutput
//		int current;
//		for(int k = 0; k < soundPackets; k++) {
//			for(int i = 0; i < 128*2 ; i++) {
//				if(k == 0 && i == 0) {
//					soundData[0] = 0;
//					audioOutput[k*256 + i] = (byte) (soundData[k*256 + i] & 0x00FF);
//					audioOutput[k*256 + i + 1] = (byte) ((soundData[k*256 + i] >> 8) & 0x00FF);
//					continue;
//				}
//				current = k*256 + i;
//				//soundData[current] = (nibles[i][k] * (betas[k])) + soundData[current-1];
//				soundData[current] = Math.min(32700, Math.max(-32700, (nibles[i][k] * (betas[k])) + soundData[current-1]));
//				audioOutput[2*current] = (byte) (soundData[current] & 0x00FF);
//				audioOutput[2*current + 1] = (byte) ((soundData[current] >> 8) & 0x00FF);
//			}
//		}
//		
//		//Audio Play
//		AudioFormat aqdcpm = new AudioFormat(8000, 16, 1, true, false);
//		SourceDataLine lineOut = AudioSystem.getSourceDataLine( aqdcpm );
//		lineOut.open(aqdcpm, audioOutput.length); 
//		
//		System.out.println("Playing Audio");
//		
//		lineOut.start();
//		lineOut.write(audioOutput, 0, audioOutput.length);
//		lineOut.drain();
//		
//		lineOut.close();
//		s.close();
//		r.close();
//		
//		String[] timeDelayString = new String[timeDelay.size()];
//		for(int i = 0; i < timeDelay.size(); i++) 
//			timeDelayString[i] = timeDelay.get(i).toString();
//		
//		//Produce files we need
//		try {
//			FileWriter writer = new FileWriter(clientRequest.contains("F")?"audioAQOutputDelay.txt":"frequencyAQOutputDelay.txt");			
//			for(String str: timeDelayString) {							
//				writer.write(str + System.lineSeparator());
//			}
//			
//			FileWriter writer2 = new FileWriter(clientRequest.contains("F")?"audioAQDiffs.txt":"frequencyAQDiffs.txt");			
//			for(int[] array: nibles)
//				for(int num: array )
//					writer2.write(num + System.lineSeparator());
//			
//			FileWriter writer3 = new FileWriter(clientRequest.contains("F")?"audioAQSamples.txt":"frequencyAQSamples.txt");			
//			for(int num: audioOutput) {							
//				writer3.write(num + System.lineSeparator());
//			}
//			
//			FileWriter writer4 = new FileWriter(clientRequest.contains("F")?"meansAQ.txt":"meansFrequencyAQ.txt");			
//			for(int num: means) {							
//				writer4.write(num + System.lineSeparator());
//			}
//			
//			FileWriter writer5 = new FileWriter(clientRequest.contains("F")?"stepsAQ.txt":"stepsFrequencyAQ.txt");			
//			for(int num: betas) {							
//				writer5.write(num + System.lineSeparator());
//			}
//			writer.close();
//			writer2.close();
//			writer3.close();
//			writer4.close();
//			writer5.close();
//		}catch(Exception e) {
//			System.out.println(e);
//		}
//		System.out.println("Thanks for listening to radio Ithaki!!!");
//	
//		
//
//	}
//}
//
//
////Class that handles ithakiCopter and OBD requests
//public class tcpPackets {
//	String clientRequest;
//	int serverPort = 38048;
//	static int timesCopter = 0;
//	static int timesOBD = 0;
//	
//	public tcpPackets() {
//		clientRequest = "";
//	}
//	
//	public tcpPackets(String clientRequest) {
//		this.clientRequest = clientRequest;
//
//	}
//	
//	//Method in charge of ithakiCopter requests
//	public void getCopterPackets() throws UnknownHostException, SocketException, IOException{
//		timesCopter++;
//		String response = "";
//		ArrayList<String> copterStats = new ArrayList<String>();
//		
//		InetAddress hostAddress = InetAddress.getByName("155.207.18.208");
//		Socket s = new Socket(hostAddress, 38048);
//		
//		InputStream is = s.getInputStream();
//		OutputStream os = s.getOutputStream();
//		long endTime = System.currentTimeMillis() + (2 * 60 * 1000);
//		s.setSoTimeout(200);
//		
//	
//		int index = 0;
//		
//		do {
//			response = "";
//			try {
//				os.write(clientRequest.getBytes());
//			} catch(Exception e) {
//				//System.out.println(e);
//			}
//			
//			int k;
//			for(;;) {
//				try {
//					k = is.read();
//					if((k == -1)) {break;}
//					response += (char)k;
//				} catch (Exception e) {
//					break;
//				}
//			} 
//			if(response.contains("ITHAKICOPTER")){
//				index++;
//				if(index > 1) {copterStats.add(response.trim());}
//			}
//		}while(System.currentTimeMillis() < endTime);
//		s.close();
//		
//		try {
//			FileWriter writer = new FileWriter("ithakiCopter" + timesCopter + ".txt");			
//			for(String str: copterStats) {							
//				writer.write(str + System.lineSeparator());
//			}
//			writer.close();
//		}catch(Exception e) {
//			System.out.println(e);
//		}
//	
//	}
//	
//	//Method for sending and receiving tcp requests
//	public String tcpComm(String request, OutputStream os, InputStream is) {
//		String response = "";
//		try {
//			os.write(request.getBytes());
//		} catch(Exception e) {
//			System.out.println(e);
//		}
//		
//		int k;
//		for(;;) {
//			try {
//				k = is.read();
//				if((k == -1)) {break;}
//				response += (char)k;
//			} catch (Exception e) {
//				break;
//			}
//		} 
//		return response;
//	}
//	
//	//Method used to communicate with OBD-II
//	public void getOBDPackets() throws UnknownHostException, SocketException, IOException{
//		timesOBD++;
//		String[] response;
//		
//		InetAddress hostAddress = InetAddress.getByName("155.207.18.208");
//		Socket s = new Socket(hostAddress, 29078);
//		
//		InputStream is = s.getInputStream();
//		OutputStream os = s.getOutputStream();
//		s.setSoTimeout(400);
//		String[] requests = {"01 1F\r", "01 0F\r", "01 11\r", "01 0C\r", "01 0D\r", "01 05\r",
//							"runTime", "airTemp", "throttlePos", "engineRPM", "vehicleSpeed", "coolantTemp"};
//		ArrayList<Integer> runTime = new ArrayList<Integer>();
//		ArrayList<Integer> airTemp = new ArrayList<Integer>();
//		ArrayList<Integer> throttlePos = new ArrayList<Integer>();
//		ArrayList<Integer> engineRPM = new ArrayList<Integer>();
//		ArrayList<Integer> vehicleSpeed = new ArrayList<Integer>();
//		ArrayList<Integer> coolantTemp = new ArrayList<Integer>();
//		ArrayList<ArrayList<Integer>> requestNames = new ArrayList<ArrayList<Integer>>();
//		requestNames.add(runTime);
//		requestNames.add(airTemp);
//		requestNames.add(throttlePos);
//		requestNames.add(engineRPM);
//		requestNames.add(vehicleSpeed);
//		requestNames.add(coolantTemp);
//		
//		int index = 0;
//		
//		//On every iteration we send appropriate request to Ithaki to receive every variable
//		while(true) {
//			
//			response = tcpComm(requests[0], os, is).replaceAll(("\\r"), "").split(" ");
//			runTime.add(((Integer.parseInt(response[2], 16)) * 256) + (Integer.parseInt(response[3], 16)));
//			System.out.println("Time: " + runTime.get(index));
//			
//			if(runTime.get(index) > 4*60) {break;}
//			
//			response = tcpComm(requests[1], os, is).replaceAll(("\\r"), "").split(" ");
//			airTemp.add((Integer.parseInt(response[2], 16)) - 40);
//			System.out.println("Air Temperature: " + airTemp.get(index));
//			
//			
//			response = tcpComm(requests[2], os, is).replaceAll(("\\r"), "").split(" ");
//			throttlePos.add(((Integer.parseInt(response[2], 16)) * 100) / 255);
//			System.out.println("Throttle Position: " + throttlePos.get(index));
//			
//			
//			response = tcpComm(requests[3], os, is).replaceAll(("\\r"), "").split(" ");
//			engineRPM.add((((Integer.parseInt(response[2], 16)) * 256) + (Integer.parseInt(response[3], 16))) / 4);
//			System.out.println("Engine RPM: " + engineRPM.get(index));
//			
//			response = tcpComm(requests[4], os, is).replaceAll(("\\r"), "").split(" ");
//			vehicleSpeed.add(Integer.parseInt(response[1], 16));
//			System.out.println("Vehicle Speed: " + vehicleSpeed.get(index));
//			
//			response = tcpComm(requests[5], os, is).replaceAll(("\\r"), "").split(" ");
//			coolantTemp.add((Integer.parseInt(response[1], 16)) - 40);
//			System.out.println("Coolant Temperature: " + coolantTemp.get(index));
//			index++;
//			System.out.println("-----------------------");
//		}
//		
//		for(int i = 0; i < requestNames.size(); i++) {
//			try {
//				FileWriter writer = new FileWriter(requests[6 + i]+ timesOBD + ".txt");			
//				for(int number: requestNames.get(i)) {							
//					writer.write(number + System.lineSeparator());
//				}
//				writer.close();
//			}catch(Exception e) {
//				System.out.println(e);
//			}
//		}
//	}
//}
