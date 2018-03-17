// Chelsea Metcalf

import java.net.*;
import java.io.*;

public class receiver {
	static String hostName;
	static int portNumber;

	public static void main(String[] args) {
		if (args.length != 2) 
        {
            System.err.println("Usage: java receiver <host name> <port number>");
            System.exit(1);
        }

        hostName = args[0];
        portNumber = Integer.parseInt(args[1]);
        
		try {
			new receiver().startReceiver(hostName, portNumber);
		} catch (Exception e) {
			System.out.println("Something falied: " + e.getMessage());
			e.printStackTrace();
		}
	}
    
    public void startReceiver(String hostName, int portNumber) throws IOException {
 
		Socket socket = null;
		PrintWriter writerOut = null;
		BufferedReader bufferIn = null;
 
		try {
			socket = new Socket(hostName, portNumber);
			writerOut = new PrintWriter(socket.getOutputStream(), true);
			bufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					
			String input, output;
			output = "";
			int totalReceived = 0;
			String msg = "";
			System.out.println("Waiting... connect sender");

			// Create new packet object
			packet pak = new packet();
			while ((input = bufferIn.readLine()) != null) {
				// Terminate receiver
				if (input.equals("-1")) {
					writerOut.println(-1);
					break;
				}

				// Split message by spaces (parse)
				pak.parseMessage(input);
				msg = msg + pak.content + " ";
				totalReceived++;
				// Print out current sequence number, total packets received, message, and ACK to be transmitted
				output = "Waiting " + pak.seqNo + ", " + totalReceived + ", " + input + ", " + pak.validateMessage();
				System.out.println(output);
				// Validate message (from packet object) - checks the checksum and then returns the proper ACK
				writerOut.println(pak.validateMessage());
				// If last message, then return the full message :)
				if (pak.isLastMessage == true) {
					System.out.println("Message: " + msg);
				}
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