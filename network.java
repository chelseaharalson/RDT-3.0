// Chelsea Metcalf

import java.io.*;
import java.net.*;
import java.util.*;


public class network {
	// Array list for the threads
	static List<Object> allMessages = new ArrayList<Object>();
	static ServerSocket serverSocket;
	//static boolean listening = true;
	
	public static void main(String[] args) throws IOException {
		int portNumber;
    
    	if (args.length != 1)
        {
            System.err.println("Usage: java network <port number>");
            System.exit(1);
        }
        
        portNumber = Integer.parseInt(args[0]);
        
        try {
			serverSocket = new ServerSocket(portNumber);
			System.out.println("Waiting... connect receiver");
			//while (listening) {
				new MessageThread(serverSocket.accept()).start();
				new MessageThread(serverSocket.accept()).start();
			//}
		} catch (Exception e) {
			System.out.println("I/O failure: " + e.getMessage());
			e.printStackTrace();
		}
    }	


public static class MessageThread extends Thread {
	private Socket socket = null;
	MessageThread mt = null;
	int ID = 0;
	
	public MessageThread(Socket socket) {
		this.socket = socket;
		allMessages.add(this);
		ID = allMessages.size() - 1;
	}
	
	public void run() {
		String input = "";
		String ACK0 = "ACK0";
		String ACK1 = "ACK1";
		String ACK2 = "ACK2";
		
		try {
			PrintWriter writerOut = null;
			BufferedReader readerIn = null;
			writerOut = new PrintWriter(socket.getOutputStream(), true);
			readerIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	
			System.out.println("Get connection from: " + socket.getRemoteSocketAddress().toString());
			//System.out.println("Message Thread - Is connected: " + socket.isConnected());
			
			while ((input = readerIn.readLine()) != null) {
				if (input.equals("-1")) {
					// If sender thread, then send -1 to the receiver
					if (ID == 1) {
						sendToOtherThread("-1");
					}
					break;
				}

				String[] splitedMsg = input.split("\\s+");
				//System.out.println(input);

				// Random - PASS, CORRUPT, DROP
				double x = Math.random();
				
				// If pass, then send the packet to the receiver
				if (x < 0.5 || splitedMsg.length == 1) // PASS
				{
					//System.out.println(input);
					if (splitedMsg[0].contains("ACK")) {
						System.out.println("Received: " + splitedMsg[0] + ", PASS");
					}
					else {
						System.out.println("Received: ACK" + splitedMsg[0] + ", PASS");
					}
					sendToOtherThread(input);
				}
				// If corrupt, corrupt the checksum, generate the message and send to other thread
				else if (x >= 0.5 && x <= 0.75) // CORRUPT
				{
					//System.out.println(input);
					packet p = new packet();
					p.parseMessage(input);
					p.corruptChecksum();
					System.out.println("Received: Packet" + splitedMsg[0] + ", " + splitedMsg[1] + ", CORRUPT");
					sendToOtherThread(p.generateMessage());
				}
				// If drop, send ACK2 back to sender
				else // DROP
				{
					//System.out.println(input);
					System.out.println("Received: Packet" + splitedMsg[0] + ", " + splitedMsg[1] + ", DROP");
					writerOut.println(ACK2);
				}
		   }
		   //socket.shutdownInput();
		   //socket.shutdownOutput();
		   socket.close();
		   //System.out.println("Socket close? " + socket.isClosed());
		}
		catch (IOException e) {
				e.printStackTrace();
		}
	}
	
	// Send message
	public void send(String pMessage) {
		PrintWriter writerOut = null;
		
		try {
			writerOut = new PrintWriter(socket.getOutputStream(), true);
			
			//System.out.println("Message Thread - Get connection from: " + socket.getRemoteSocketAddress().toString());
			//System.out.println("Message Thread - Is connected: " + socket.isConnected());
			
			writerOut.println(pMessage);	
		}
		catch (IOException e) {
				e.printStackTrace();
		}
	}
	
	// This method keeps track of which thread it is. It sends to the opposite thread.
	public void sendToOtherThread(String pMessage) {
		if (ID == 0) {
			mt = (MessageThread)allMessages.get(1);
		}
		else {
			mt = (MessageThread)allMessages.get(0);
		}
		// Send to opposite thread
		mt.send(pMessage);
	}
}

}