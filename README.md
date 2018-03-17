# RDT-3.0
Project for networking class to simulate the RDT 3.0 protocol. Posting since deadline passed.

### Compiling the Code:
javac network.java

javac sender.java

javac receiver.java

javac packet.java

### Running the Code:
java network 8397									ON SAND MACHINE

java receiver sand.cise.ufl.edu 8397				ON STORM MACHINE

java sender sand.cise.ufl.edu 8397 message.txt		ON THUNDERMACHINE

### Code Structure:
The network program begins by creating a ServerSocket object. The ServerSocket object listens on a port, which is specified by the command line input parameter when running the network code. Once the server binds to its port, then the ServerSocket object is created and accepts a connection from the client. When the connection is requested and established, the accept method returns a new Socket object. I created a class called MessageThread, which extends on Thread. This allows the network to communicate with the clients and to continue listening for client connection requests.

In my class MessageThread, the sockets are added to an array list and given an ID. I have a method called sendToOtherThread(String pMessage), which keeps track of which thread it is. It sends to the opposite thread to allow the network to communicate with both the sender and receiver. In my send(String pMessage) method, I simply just have the PrintWriter so the network can write information to the PrintWriter object, and send a message to the sender or receiver.

I created a packet object to keep my code organized. It has functions such as generateMessage, corruptChecksum, generateChecksum, etc.
In my run() function, the random is implemented to mix up PASS, CORRUPT, and DROP. If the action PASS is chosen, the packet is forwarded to the receiver. If the action CORRUPT is chosen, the checksum is corrupted and then sent to the receiver. If the action DROP is chosen, ACK2 is sent to the sender. The receiver sends back either ACK0 or ACK1 to the sender if the packet is PASS or CORRUPT. In my packet object, I created a function called validateMessage, and the purpose of this method was to return the ACK. So in my receiver, I call this method on the packet.