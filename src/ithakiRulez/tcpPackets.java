package ithakiRulez;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;


//Class that handles ithakiCopter and OBD requests
public class tcpPackets {
	String clientRequest;
	int serverPort = 38048;
	static int timesCopter = 0;
	static int timesOBD = 0;
	
	public tcpPackets() {
		clientRequest = "";
	}
	
	public tcpPackets(String clientRequest) {
		this.clientRequest = clientRequest;

	}
	
	//Method in charge of ithakiCopter requests
	public void getCopterPackets() throws UnknownHostException, SocketException, IOException{
		timesCopter++;
		String response = "";
		ArrayList<String> copterStats = new ArrayList<String>();
		
		InetAddress hostAddress = InetAddress.getByName("155.207.18.208");
		Socket s = new Socket(hostAddress, 38048);
		
		InputStream is = s.getInputStream();
		OutputStream os = s.getOutputStream();
		long endTime = System.currentTimeMillis() + (2 * 60 * 1000);
		s.setSoTimeout(200);
		
	
		int index = 0;
		
		do {
			response = "";
			try {
				os.write(clientRequest.getBytes());
			} catch(Exception e) {
				//System.out.println(e);
			}
			
			int k;
			for(;;) {
				try {
					k = is.read();
					if((k == -1)) {break;}
					response += (char)k;
				} catch (Exception e) {
					break;
				}
			} 
			if(response.contains("ITHAKICOPTER")){
				index++;
				if(index > 1) {copterStats.add(response.trim());}
			}
		}while(System.currentTimeMillis() < endTime);
		s.close();
		
		try {
			FileWriter writer = new FileWriter("ithakiCopter" + timesCopter + ".txt");			
			for(String str: copterStats) {							
				writer.write(str + System.lineSeparator());
			}
			writer.close();
		}catch(Exception e) {
			System.out.println(e);
		}
	
	}
	
	//Method for sending and receiving tcp requests
	public String tcpComm(String request, OutputStream os, InputStream is) {
		String response = "";
		try {
			os.write(request.getBytes());
		} catch(Exception e) {
			System.out.println(e);
		}
		
		int k;
		for(;;) {
			try {
				k = is.read();
				if((k == -1)) {break;}
				response += (char)k;
			} catch (Exception e) {
				break;
			}
		} 
		return response;
	}
	
	//Method used to communicate with OBD-II
	public void getOBDPackets() throws UnknownHostException, SocketException, IOException{
		timesOBD++;
		String[] response;
		
		InetAddress hostAddress = InetAddress.getByName("155.207.18.208");
		Socket s = new Socket(hostAddress, 29078);
		
		InputStream is = s.getInputStream();
		OutputStream os = s.getOutputStream();
		s.setSoTimeout(400);
		String[] requests = {"01 1F\r", "01 0F\r", "01 11\r", "01 0C\r", "01 0D\r", "01 05\r",
							"runTime", "airTemp", "throttlePos", "engineRPM", "vehicleSpeed", "coolantTemp"};
		ArrayList<Integer> runTime = new ArrayList<Integer>();
		ArrayList<Integer> airTemp = new ArrayList<Integer>();
		ArrayList<Integer> throttlePos = new ArrayList<Integer>();
		ArrayList<Integer> engineRPM = new ArrayList<Integer>();
		ArrayList<Integer> vehicleSpeed = new ArrayList<Integer>();
		ArrayList<Integer> coolantTemp = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> requestNames = new ArrayList<ArrayList<Integer>>();
		requestNames.add(runTime);
		requestNames.add(airTemp);
		requestNames.add(throttlePos);
		requestNames.add(engineRPM);
		requestNames.add(vehicleSpeed);
		requestNames.add(coolantTemp);
		
		int index = 0;
		
		//On every iteration we send appropriate request to Ithaki to receive every variable
		while(true) {
			
			response = tcpComm(requests[0], os, is).replaceAll(("\\r"), "").split(" ");
			runTime.add(((Integer.parseInt(response[2], 16)) * 256) + (Integer.parseInt(response[3], 16)));
			System.out.println("Time: " + runTime.get(index));
			
			if(runTime.get(index) > 4*60) {break;}
			
			response = tcpComm(requests[1], os, is).replaceAll(("\\r"), "").split(" ");
			airTemp.add((Integer.parseInt(response[2], 16)) - 40);
			System.out.println("Air Temperature: " + airTemp.get(index));
			
			
			response = tcpComm(requests[2], os, is).replaceAll(("\\r"), "").split(" ");
			throttlePos.add(((Integer.parseInt(response[2], 16)) * 100) / 255);
			System.out.println("Throttle Position: " + throttlePos.get(index));
			
			
			response = tcpComm(requests[3], os, is).replaceAll(("\\r"), "").split(" ");
			engineRPM.add((((Integer.parseInt(response[2], 16)) * 256) + (Integer.parseInt(response[3], 16))) / 4);
			System.out.println("Engine RPM: " + engineRPM.get(index));
			
			response = tcpComm(requests[4], os, is).replaceAll(("\\r"), "").split(" ");
			vehicleSpeed.add(Integer.parseInt(response[1], 16));
			System.out.println("Vehicle Speed: " + vehicleSpeed.get(index));
			
			response = tcpComm(requests[5], os, is).replaceAll(("\\r"), "").split(" ");
			coolantTemp.add((Integer.parseInt(response[1], 16)) - 40);
			System.out.println("Coolant Temperature: " + coolantTemp.get(index));
			index++;
			System.out.println("-----------------------");
		}
		
		for(int i = 0; i < requestNames.size(); i++) {
			try {
				FileWriter writer = new FileWriter(requests[6 + i]+ timesOBD + ".txt");			
				for(int number: requestNames.get(i)) {							
					writer.write(number + System.lineSeparator());
				}
				writer.close();
			}catch(Exception e) {
				System.out.println(e);
			}
		}
	}
}
