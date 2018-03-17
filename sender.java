// Chelsea Metcalf

import java.net.*;
import java.io.*;

public class sender {
	static String hostName;
	static int portNumber;
	static String fileName;

	public static void main(String[] args) {
		if (args.length != 3) 
        {
            System.err.println("Usage: java sender <host name> <port number> <message file name>");
            System.exit(1);
        }

        hostName = args[0];
        portNumber = Integer.parseInt(args[1]);
        fileName = args[2];
        
		try {
			new sender().startSender(hostName, portNumber, fileName);
		} catch (Exception e) {
			System.out.println("Something falied: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Function to return whether or not the ACK is the same from the network
	public boolean validateACK(String ACKfromNetwork, Integer seqNo) {
    	String ACKN = "ACK" + seqNo.toString();
    	return ACKN.equals(ACKfromNetwork);
    }
    
    public void startSender(String hostName, int portNumber, String fileName) throws IOException {
 
		Socket socket = null;
		PrintWriter writerOut = null;
		BufferedReader bufferIn = null;
 
		try {
			socket = new Socket(hostName, portNumber);
			writerOut = new PrintWriter(socket.getOutputStream(), true);
			bufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 
            String ACK;
					
				try {
					// Code to read in the file
					FileInputStream fstream = new FileInputStream(fileName);
					DataInputStream dataIn = new DataInputStream(fstream);
					BufferedReader buffReader = new BufferedReader(new InputStreamReader(dataIn));
		
					String input;
		
					int totalSent = 0;
					String message = "";
					
					// Create new packet object
					packet pak = new packet();
					while ((input = buffReader.readLine()) != null) {
						String[] splited = input.split("\\s+");
						int i = 0;
						while (i < splited.length) {
							// Create the message
							pak.createPacket(splited[i]);
							// Generate the message based on the format
							message = pak.generateMessage();
							// Send message to network
							writerOut.println(message);
							ACK = bufferIn.readLine();
							
							totalSent++;
							//System.out.println(ACK + " " + pak.seqNo);
							if (ACK.equals("ACK2")) { // DROP
								System.out.println("Waiting: " + ACK + ", " + totalSent + ", DROP, resend Packet" + pak.seqNo);
							}
							else if (validateACK(ACK, pak.seqNo) == true) { // PASS
								i++;
								System.out.println("Waiting: " + ACK + ", " + totalSent + ", " + ACK + ", no more packets to send");
							}
							else { // CORRUPT
								System.out.println("Waiting: " + ACK + ", " + totalSent + ", " + ACK + ", send Packet" + pak.seqNo);
							}
						}
						writerOut.println(-1);
				}
				dataIn.close();
				} catch (Exception e) {
					System.err.println("Error: " + e.getMessage());
				}
		} catch (UnknownHostException e) {
			System.err.println("Cannot find the host: " + hostName);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't read/write from the connection: " + e.getMessage());
			System.exit(1);
		} finally {
			writerOut.close();
			bufferIn.close();
			socket.close();
		}
	}
}