package connect_game.impl.connect_four;

/*
 * Description: This class provides the server implementation for
 * the game. The server's game frame implementation requires a port
 * number as the server's IP address is implicit. Thus, a port number
 * is established and passed in. When the server of the program runs,
 * it waits for a client to make a connection so that two machines 
 * may have the capability to communicate in real time.
 * 
 * */

public class ServerDemo {

  public static void main(String [] args) {
    // Instantiates port
    int portNumber = 45000;
    // creates the game frame for the server
    BoardFrame game = new BoardFrame(portNumber);

  } 
}

