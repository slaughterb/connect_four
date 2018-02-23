package connect_game.impl.connect_four;
/* 
 * Description: This class provides for the client implementation of
 * the Connect Four game. Within the main method a GUI frame containing 
 * the board and the game's underlying functionality is instantiated.
 * The client will connect to a server whenever the connect four server
 * is running. The client implementation of the game requires an IP
 * address and a port number. 
 * 
 * 
 * */

public class ClientDemo {
  
  public static void main(String [] args) {
    // sets up the IP and port number
    String ipAddress = "127.0.0.1";
    int portNumber = 45000;
    // creates the instantiation of the board frame.
    BoardFrame game = new BoardFrame(ipAddress, portNumber);   
  }
}
