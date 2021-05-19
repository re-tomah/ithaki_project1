package ithakiRulez;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.sound.sampled.LineUnavailableException;

import ithakiRulez.echoPackets;
import ithakiRulez.imagePackets;
public class ithakiRulez {

	
	//Main class containing the basic GUI
	public static void main(String[] args) throws IOException, LineUnavailableException{
		
		String clientRequest, serverPort, clientPort;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Server Port: ");serverPort = in.readLine().replaceAll("\\s+", "");
		System.out.println("Client Port: ");clientPort = in.readLine().replaceAll("\\s+", "");
		echoPackets wireShark = new echoPackets("E0000", Integer.parseInt(serverPort)
											, Integer.parseInt(clientPort));
		
		for(;;) {
			System.out.println("----------------\n");
			System.out.println("Client Request: ");clientRequest = in.readLine().replaceAll("\\s+", "");
			if(clientRequest.isEmpty()){
				System.out.println("Happy to assist you. \nByeeeeeee!!!!!");
				break;
			} else if(clientRequest.charAt(0) == 'E') {
				echoPackets packet = new echoPackets(clientRequest, Integer.parseInt(serverPort)
						, Integer.parseInt(clientPort));
				packet.getEchoPackets();
			} else if(clientRequest.charAt(0) == 'M') {
				String temp;
				System.out.println("Camera's Position(PTZ or FIX): ");temp = in.readLine().replaceAll("\\s+", "").toUpperCase();
				if(temp.equals("PTZ") || (temp.equals("FIX"))) {
					clientRequest+=("CAM=" + temp);
					if(temp.equals("PTZ")) {
						System.out.println("Camera's Direction(L, U, R, D, M, C): ");temp = in.readLine().replaceAll("\\s+", "").toUpperCase();
						if(temp.equals("L") || temp.equals("U") || temp.equals("R") || temp.equals("D") || temp.equals("M") || temp.equals("C")) {
							clientRequest+=("DIR=" + temp);
						}else {System.out.println("Oops, there's no such kind of direction");}
						System.out.println("Flow (on or off)");temp = in.readLine().replaceAll("\\s+", "").toUpperCase();
						if(temp.equals("ON") || temp.equals("OFF")) {
							clientRequest+=("FLOW=" + temp);
						}else {System.out.println("Oops, there's no such kind of flow");}
						System.out.println("Packet Length(128, 256, 512, 1024): ");temp = in.readLine().replaceAll("\\s+", "").toUpperCase();
						if(temp.equals("128") || temp.equals("256") || temp.equals("512") || temp.equals("1024")) {
							clientRequest+=("UDP=" + temp);
						}else {temp = "128";}		
					}else {temp = "128";}
				}else {
					System.out.println("Packet Length(128, 256, 512, 1024): ");temp = in.readLine().replaceAll("\\s+", "").toUpperCase();
					if(temp == "128" || temp == "256" || temp == "512" || temp == "1024") {
						clientRequest+=("UDP=" + temp);
					}else {temp = "128";}
				}
				wireShark.wiresharkPackets();
				imagePackets packet = new imagePackets(clientRequest, Integer.parseInt(serverPort)
													,Integer.parseInt(clientPort), Integer.parseInt(temp));
				packet.getImagePackets();
				
			} else if(clientRequest.charAt(0) == 'A') {
				String temp;
				System.out.println("AQ for AQ-DPCM\nPress nothing for normal DPCM");temp = in.readLine().replaceAll("\\s+", "").toUpperCase();
				if(temp.equals("AQ") || temp.equals("")) {
					clientRequest+=temp;
				}else {System.out.println("Normal DPCM selected by default");}
				
				if(clientRequest.contains("AQ") == false) {
					System.out.println("T for frequency\nF for audio clip");temp = in.readLine().replaceAll("\\s+", "").toUpperCase();
					if(temp.equals("F") || temp.equals("T")) {
						String choice = temp;
						if(temp.equals("F")) {
							System.out.println("Enter the number of the audio clip you want To hear\nEntering nothing will play a random song");
							temp = in.readLine().replaceAll("\\s+", "").toUpperCase();
							if(temp.equals("") == false) {
								clientRequest+=("L"+temp);
							}
						}
						clientRequest+=(choice+"999");
					}else {
						System.out.println("Normal DPCM selected by default");
						clientRequest+="F999";
					}
				}else {
					clientRequest+="F999";
				}
				
				audioPackets packet = new audioPackets(clientRequest, Integer.parseInt(serverPort)
					, Integer.parseInt(clientPort));
				if(clientRequest.contains("AQ")) {
					wireShark.wiresharkPackets();
					packet.getQAudioPacket();
				}else {
					wireShark.wiresharkPackets();
					packet.getAudioPackets();
				}
			} else if(clientRequest.charAt(0) == 'Q'){
				String flightLevel, leftMotor, rightMotor;
				for(int i = 0; i < 2; i++) {
					do{
						System.out.println("Flight Level: ");flightLevel = in.readLine().replaceAll("\\s+", "");
						System.out.println("Left Motor: ");leftMotor = in.readLine().replaceAll("\\s+", "");
						System.out.println("Right Motor: ");rightMotor = in.readLine().replaceAll("\\s+", "");
					}while(flightLevel.length() != 3 || leftMotor.length() != 3 || rightMotor.length() != 3);
					
					tcpPackets packet = new tcpPackets( "AUTO FLIGHTLEVEL=" 
														+ String.valueOf(flightLevel) 
														+ " LMOTOR=" + String.valueOf(leftMotor) 
														+ " RMOTOR=" + String.valueOf(rightMotor) + " PILOT \r\n");
					wireShark.wiresharkPackets();
					packet.getCopterPackets();
				}
			}else if(clientRequest.charAt(0) == 'V') {
				tcpPackets packet = new tcpPackets(clientRequest);
				wireShark.wiresharkPackets();
				packet.getOBDPackets();
			}else {
				System.out.println("Please try again, with a valid input.");
			}
		}
	}

}
