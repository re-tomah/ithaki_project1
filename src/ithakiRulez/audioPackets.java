package ithakiRulez;
import java.net.*;
import java.util.ArrayList;
import java.io.*;
import javax.sound.sampled.*;

//Class that handles audio requests
public class audioPackets {
	String clientRequest;
	int serverPort;
	int clientPort;
	
	public audioPackets() {
		clientRequest = "";
		serverPort = 0;
		clientPort = 0;
	}
	
	public audioPackets(String clientRequest, int serverPort, int clientPort) {
		this.clientRequest = clientRequest;
		this.serverPort = serverPort;
		this.clientPort = clientPort;
	}

	//Method in charge of receiving DPCM audio packets and producing files containing useful info
	public void getAudioPackets() throws UnknownHostException, SocketException, IOException, LineUnavailableException{
		DatagramSocket s = new DatagramSocket();
		byte[] txbuffer = clientRequest.getBytes();
		
		ArrayList<Long> timeDelay = new ArrayList<Long>();
		InetAddress hostAddress = InetAddress.getByName("155.207.18.208");
		System.out.println("Host name is: " + hostAddress.getHostName());
		System.out.println("IP address is: " + hostAddress.getHostAddress());
		
		DatagramPacket packet = new DatagramPacket(txbuffer, txbuffer.length, hostAddress, serverPort);
		DatagramSocket r = new DatagramSocket(clientPort);
		
		int packetSize = 128;
		byte[] rxbuffer = new byte[packetSize];
		DatagramPacket receivedPacket = new DatagramPacket(rxbuffer, rxbuffer.length);
		
		int soundPackets = clientRequest.contains("F")?Integer.parseInt(clientRequest.substring(9, 12)):Integer.parseInt(clientRequest.substring(6, 9));
		int initialSize = 128 * 2 * soundPackets;
		int[] nibles = new int[initialSize];
		
		s.send(packet);
		r.setSoTimeout(5000);
		byte[] receivedData = new byte[packetSize * soundPackets];
		int index = 0;
		
		//Audio received in an Array
		long currentTime = System.currentTimeMillis();
		for(;;) {
			try {
				r.receive(receivedPacket);
				for(int j = 0; j < 128; j++)
					receivedData[index*128 + j] = rxbuffer[j];
				index++;
			}catch (Exception e) {
				break;
			}
			timeDelay.add((System.currentTimeMillis()- currentTime));
			currentTime = System.currentTimeMillis();	
		}
		
		AudioFormat dcpm = new AudioFormat(8000, 8, 1, true, false);
		SourceDataLine line = AudioSystem.getSourceDataLine(dcpm);
		line.open(dcpm, initialSize);
		
		//Extract audio nibles in another array
		for (int i = 0; i < packetSize * soundPackets; i++) {
			int k = (int) receivedData[i];
			nibles[2*i + 1] = ((k & 0xF) - 8);			
			nibles[2*i] = (((k >> 4) & 0xF) - 8);
		}
		
		int[] soundData = new int[initialSize];
		byte[] audioOutput = new byte[initialSize];
		
		//Transform nibles data to audio data for SourceDataLine to read from
		soundData[0] = 0;
		audioOutput[0] = (byte) (soundData[0]);
		for(int i = 1; i < initialSize; i++) {
			soundData[i]   =  nibles[i] + soundData[i-1];
			audioOutput[i] = (byte) ( soundData[i] & 0x00FF );
		}
		
		soundData[0] = nibles[0] + soundData[255];
		audioOutput[0] = (byte) (soundData[0] & 0x00FF);
		
		String[] timeDelayString = new String[timeDelay.size()];
		for(int i = 0; i < timeDelay.size(); i++) 
			timeDelayString[i] = timeDelay.get(i).toString();

		//Produce files we need
		try {
			FileWriter writer = new FileWriter(clientRequest.contains("F")?"audioOutputDelay.txt":"frequencyOutputDelay.txt");			
			for(String str: timeDelayString) {							
				writer.write(str + System.lineSeparator());
			}
			
			FileWriter writer2 = new FileWriter(clientRequest.contains("F")?"audioDiffs.txt":"frequencyDiffs.txt");			
			for(int num: nibles) {							
				writer2.write(num + System.lineSeparator());
			}
			
			FileWriter writer3 = new FileWriter(clientRequest.contains("F")?"audioSamples.txt":"frequencySamples.txt");			
			for(int num: audioOutput) {							
				writer3.write(num + System.lineSeparator());
			}
			writer.close();
			writer2.close();
			writer3.close();
		}catch(Exception e) {
			System.out.println(e);
		}
		
		//Play audio
		System.out.println("Music Playing in the background retard");
		line.start();
		line.write(audioOutput, 0, initialSize);
		line.drain();
		
		line.close();
		s.close();
		r.close();
		
	}
	
	//Method in charge of receiving AQ-DPCM audio packets and producing files containing useful info
	public void getQAudioPacket() throws UnknownHostException, SocketException, IOException, LineUnavailableException{
		
		DatagramSocket s = new DatagramSocket();
		byte[] txbuffer = clientRequest.getBytes();
		ArrayList<Long> timeDelay = new ArrayList<Long>();
		
		InetAddress hostAddress = InetAddress.getByName("155.207.18.208");
		System.out.println("Host name is: " + hostAddress.getHostName());
		System.out.println("IP address is: " + hostAddress.getHostAddress());
		
		DatagramPacket packet = new DatagramPacket(txbuffer, txbuffer.length, hostAddress, serverPort);
		DatagramSocket r = new DatagramSocket(clientPort);
		
		int packetSize = 132;
		byte[] rxbuffer = new byte[packetSize];
		DatagramPacket receivedPacket = new DatagramPacket(rxbuffer, rxbuffer.length);
		
		int soundPackets = Integer.parseInt(clientRequest.substring(8,11));
		int initialSize = 128 * 2 * soundPackets;
		int[][] nibles = new int[128 * 2][soundPackets];
		int[] soundData = new int[initialSize];
		int[] means = new int[soundPackets];
		int[] betas = new int[soundPackets];
		byte[] audioOutput = new byte[2 * soundData.length];
		
		s.send(packet);
		r.setSoTimeout(5000);
		byte[][] receivedData = new byte[soundPackets][packetSize];
		
		System.out.println("Loading...");
		
		//Receive dataStream
		long currentTime = System.currentTimeMillis();
		int counter = 0;
		for(;;) {
			try {
				r.receive(receivedPacket);
				for(int i = 0; i < rxbuffer.length; i++)
					receivedData[counter][i] = rxbuffer[i];
				counter++;	
			}catch(Exception e) {break;}
			
			timeDelay.add((System.currentTimeMillis()- currentTime));
			currentTime = System.currentTimeMillis();
		}
	
		//Mean and Step
		int meanMSB, meanLSB, betaMSB, betaLSB;
		for(int i = 0; i < soundPackets; i++) {
			meanLSB = receivedData[i][0] & 0xFF;
			meanMSB = ((receivedData[i][1]) << 8);
			means[i] = meanMSB | meanLSB;
			betaLSB = receivedData[i][2] & 0xFF;
			betaMSB = ((receivedData[i][3] & 0xFF) << 8);
			betas[i] = betaMSB | betaLSB;
			
			//Nibles
			for(int j = 4; j < 132; j++) {
				nibles[2*j - 8][i] = (((receivedData[i][j] >> 4) & 0xF) - 8);
				nibles[2*j - 7][i] = ((receivedData[i][j] & 0xF) - 8);
			}
		}
		
		//SoundData and AudioOutput
		int current;
		for(int k = 0; k < soundPackets; k++) {
			for(int i = 0; i < 128*2 ; i++) {
				if(k == 0 && i == 0) {
					soundData[0] = 0;
					audioOutput[k*256 + i] = (byte) (soundData[k*256 + i] & 0x00FF);
					audioOutput[k*256 + i + 1] = (byte) ((soundData[k*256 + i] >> 8) & 0x00FF);
					continue;
				}
				current = k*256 + i;
				//soundData[current] = (nibles[i][k] * (betas[k])) + soundData[current-1];
				soundData[current] = Math.min(32700, Math.max(-32700, (nibles[i][k] * (betas[k])) + soundData[current-1]));
				audioOutput[2*current] = (byte) (soundData[current] & 0x00FF);
				audioOutput[2*current + 1] = (byte) ((soundData[current] >> 8) & 0x00FF);
			}
		}
		
		//Audio Play
		AudioFormat aqdcpm = new AudioFormat(8000, 16, 1, true, false);
		SourceDataLine lineOut = AudioSystem.getSourceDataLine( aqdcpm );
		lineOut.open(aqdcpm, audioOutput.length); 
		
		System.out.println("Playing Audio");
		
		lineOut.start();
		lineOut.write(audioOutput, 0, audioOutput.length);
		lineOut.drain();
		
		lineOut.close();
		s.close();
		r.close();
		
		String[] timeDelayString = new String[timeDelay.size()];
		for(int i = 0; i < timeDelay.size(); i++) 
			timeDelayString[i] = timeDelay.get(i).toString();
		
		//Produce files we need
		try {
			FileWriter writer = new FileWriter(clientRequest.contains("F")?"audioAQOutputDelay.txt":"frequencyAQOutputDelay.txt");			
			for(String str: timeDelayString) {							
				writer.write(str + System.lineSeparator());
			}
			
			FileWriter writer2 = new FileWriter(clientRequest.contains("F")?"audioAQDiffs.txt":"frequencyAQDiffs.txt");			
			for(int[] array: nibles)
				for(int num: array )
					writer2.write(num + System.lineSeparator());
			
			FileWriter writer3 = new FileWriter(clientRequest.contains("F")?"audioAQSamples.txt":"frequencyAQSamples.txt");			
			for(int num: audioOutput) {							
				writer3.write(num + System.lineSeparator());
			}
			
			FileWriter writer4 = new FileWriter(clientRequest.contains("F")?"meansAQ.txt":"meansFrequencyAQ.txt");			
			for(int num: means) {							
				writer4.write(num + System.lineSeparator());
			}
			
			FileWriter writer5 = new FileWriter(clientRequest.contains("F")?"stepsAQ.txt":"stepsFrequencyAQ.txt");			
			for(int num: betas) {							
				writer5.write(num + System.lineSeparator());
			}
			writer.close();
			writer2.close();
			writer3.close();
			writer4.close();
			writer5.close();
		}catch(Exception e) {
			System.out.println(e);
		}
		System.out.println("Thanks for listening to radio Ithaki!!!");
	
		

	}
}
