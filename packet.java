// Chelsea Metcalf

import java.net.*;
import java.io.*;

public class packet {
	
	Integer seqNo;
	Integer packetID;
	Integer checksum;
	public String content;
	public boolean isLastMessage = false;
	
	public packet() {
		seqNo = 1;
		packetID = 0;
	}
	
	// Packets contain these items (sequence number, packet ID, checksum, and content)
	public void createPacket(String pContent) {
		content = pContent;
		getSequenceNum();
		checksum = generateChecksum(content);
		packetID++;
	}
	
	// Generates the message
	public String generateMessage() {
		return seqNo + " " + packetID + " " + checksum + " " + content;
	}
	
	// Sum the ascii characters of the word
	public Integer generateChecksum(String s) {
		int asciiInt;
		int sum = 0;
		for (int i = 0; i < s.length(); i++) {
			asciiInt = (int) s.charAt(i);
			sum = sum + asciiInt;
			
			//System.out.println(s.charAt(i) + "   " + asciiInt);
			if ((int) s.charAt(i) == 46) {
				isLastMessage = true;
			}
		}
		return sum;
    }
    
    // Parse the message and break up by spaces
    public void parseMessage(String pcontent) {
    	String[] splited = pcontent.split("\\s+");
    	for (int i = 0; i < splited.length; i++) { 
    		seqNo = Integer.parseInt(splited[0]);
    		packetID = Integer.parseInt(splited[1]);
    		checksum = Integer.parseInt(splited[2]);
    		content = splited[3];
    	}
    }
    
    // Corrupt the checksum by adding 1
    public void corruptChecksum() {
    	checksum = checksum + 1;
    }
    
    // Check the checksum to see whether or not it is corrupt
    public String validateMessage() {
    	Integer newChecksum = generateChecksum(content);
    	if (newChecksum.equals(checksum)) {
    		return "ACK" + seqNo.toString();
    	}
    	else {
	    		if (seqNo == 0) {
					seqNo = 1;
				}
				else {
					seqNo = 0;
				}
    		return "ACK" + seqNo.toString();
    	}
    }
    
    // Alternates the 0 and 1
    public void getSequenceNum() {
    	if (seqNo == 0) {
			seqNo = 1;
		}
		else {
			seqNo = 0;
		}
    }
	
}