import java.net.Socket;
import java.net.ServerSocket; 
import java.io.DataOutputStream; 
import java.io.DataInputStream; 
import java.io.IOException; 
import java.util.Vector;
/*
 * Description: Intends to send and receive String data, serving as
 * the main connection between the server board and the client board.
 * Functions defined through this socket class give us the opportunity
 * to send string representations of users' moves between the server
 * and the client. 
 * 
 * */
public class ClientServerSocket {
  private String ipAddress;
  private int portNumber;
  private Socket socket;
  private DataOutputStream outputData;
  private DataInputStream inputData;
  
  // socket accepts an IP address and a port number
  public ClientServerSocket(String inipAddress, int inPortNumber) {
    ipAddress = inipAddress;
    portNumber = inPortNumber;
    outputData = null;
    socket = null;
  }
  
  // starts the client's streams of data
  public void startClient() {
    try {
      socket = new Socket(ipAddress, portNumber);
      outputData = new DataOutputStream(socket.getOutputStream());
      inputData = new DataInputStream(socket.getInputStream());
    }
    catch (IOException error) {
      System.out.println("Error: can't connect - is server running?");
      System.exit(10);
    }
  }
  
  public void startServer() {
    ServerSocket serverSock;
    
    // Intends to start the server's streams of data
    try {
      serverSock = new ServerSocket(portNumber);
      System.out.println("Waiting for client to connect... ");
      socket = serverSock.accept();
      outputData = new DataOutputStream(socket.getOutputStream());
      inputData = new DataInputStream(socket.getInputStream());
      System.out.println("Client server accepted! ");
    }
    catch (IOException ioe) {
      System.out.println("Error: caught exception starting server.");
      System.exit(7);
    }
  }
  
  // function intends to receive a string from the other end
  public String receiveString() {
    String recvString = "";
    byte recByte;
    byte[] byteAry; 
    // intends to treat string data as vectors of bytes
    Vector<Byte> byteVector = new Vector<Byte>();
    
    try {
      recByte = inputData.readByte();
      while (recByte != 0) {
        byteVector.add(recByte);
        recByte = inputData.readByte();
      }
      byteAry = new byte[byteVector.size()];
      
      // byteAry values are matched with the bytevector's
      // which contain recBytes (received bytes)
      for (int i = 0; i < byteVector.size(); i++) {
        byteAry[i] = byteVector.elementAt(i).byteValue();
      }
      recvString = new String(byteAry);
    }
    // error handles if a string is not properly received
    catch (IOException ioe) {
      System.out.println("Error: receiving string from socket.");
      System.exit(8);
    }
    
    return (recvString);
  }
  
  // function intends to send String data
  public boolean sendString (String inSendString) {
    boolean success = false;
    
    // writes out the bytes
    try { 
      outputData.writeBytes(inSendString);
      outputData.writeByte(0);
      success = true; 
    }
    catch (IOException ioe) {
      System.out.println("Error: writing to socket stream.");
      System.exit(-1);
    }
    // intends to return a boolean feedback on the string sending
    return (success);
  }
}
