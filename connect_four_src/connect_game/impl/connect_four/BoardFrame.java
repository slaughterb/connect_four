/*
 * Description: This class creates the visual environment for the
 * connect four board and the functionality that allows the board to
 * function. This class also creates a socket to allow the server and
 * the client to keep updated with the others' changes. This class
 * creates an expandable two-player connect four environment in which
 * two players can play against each other over multiple machines in 
 * a real-time manner. The board established is a 6 x 7 grid of labels,
 * with buttons at the top of each column such that a player may 
 * manually drop a button into the board
 * */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

public class BoardFrame extends JFrame {
  
  // intends to create the GUI buttons for
  // game functionality (drapping in columns, resetting the grid,
  // and exchanging your turn position if desired).
  private JButton dropCol0;
  private JButton dropCol1;
  private JButton dropCol2;
  private JButton dropCol3;
  private JButton dropCol4;
  private JButton dropCol5;
  private JButton dropCol6;
  private JButton restartButton;
  private JButton swapTurn;
  
  // intends to set up the grids which will be used to keep 
  // track of game values and display the game.
  private colorType[][] grid;
  private JLabel[][] imageGrid;
  
  // establishing the sizes and winning conditions in constant
  // form intends to make the game more expandable as these 
  // conditions can be changed here and alter the rules of the 
  // game.
  private final int ROWS = 6;
  private final int COLS = 7;
  private final int WINNING_SCORE = 4;
  
  // the socket will be used to create a connection between the 
  // game server and the game client
  private ClientServerSocket socket; 

  // enum serves to represent the color type of a given square on
  // the board. This will be used to keep track of game conditions
  enum colorType {UNKNOWN, RED, YELLOW};

  // this segment of attributes intends to keep track of the state
  // of the game. column inputs will be sent and received, allowing
  // the players' boards to be constantly updated. The displayed
  // message condition ensures that a victory is celebrated once
  // per game
  private String currentColor = "";
  private String receivedCol = "";
  private boolean msgDisplayed = false;
  
  private ArrayList<JButton> dropButtons;
  
  // Intends to ultimately display player info (if red/yellow is
  // starting or currently moving, who's waiting for the other etc.) 
  private JButton playerInfoLabel;
  private JButton playerTurnLabel;
  
  // Declares the panels which will be used for the organization
  // of the GUI layout:
  private JPanel boardDropCols;
  private JPanel board;
  private JPanel restartPanel;
  private JPanel playerInfo;
  
  
  // Contains the information for the client (which needs an IP)
  public BoardFrame(String ipAddress, int portNumber) {
    super("Play Connect Four!");
    // GUI related organization is categorized in this section 
    // before the networks as a way of ensuring that the GUI 
    // is fully established before starting any of the communication
    // aspects of the game.
    setUp();
    disableButtons(dropButtons);
    currentColor = "yellow";
    playerInfoLabel.setText("Player: YELLOW");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
    pack();
    setVisible(true);    
    
    
    // Intends to establish the networking-related aspect of the
    // project. The clientserversocket class provides a means to
    // communicate between the server and the client through a 
    // relaying of string messages. The server is started last,
    // once the GUI and the socket is set up.
    socket = new ClientServerSocket(ipAddress, portNumber);
    socket.startClient();
    new ButtonSwingWorker().execute();
  }
  
  // Contains the information for the server (which needs a port)
  public BoardFrame(int portNumber) {
    // GUI related organization is categorized in this section 
    // before the networks as a way of ensuring that the GUI 
    // is fully established before starting any of the communication
    // aspects of the game.
    super("Play Connect Four!");
    setUp();
    currentColor = "red";
    playerInfoLabel.setText("Player: RED");
    playerTurnLabel.setText("Status: Good to go!");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
    pack();
    setVisible(true);

    // Intends to establish the networking-related aspect of the
    // project. The clientserversocket class provides a means to
    // communicate between the server and the client through a 
    // relaying of string messages. The server is started last,
    // once the GUI and the socket is set up.
    socket = new ClientServerSocket("127.0.0.1", portNumber);
    socket.startServer();
 
  }
  
  public void setUp() {
    
    // This segment of code intends to create the actual images
    // which icons will be saved onto during the game. This 
    // intends to create a grid of JLabels so that board square
    // images may be placed over them
    imageGrid = new JLabel[ROWS][COLS];
    for (int r = 0; r < ROWS; r++) {
      for (int c = 0; c < COLS; c++) {
        imageGrid[r][c] = new JLabel();
      }
    }
    
    // Intends to establish the GUI labels which will display a 
    // player's given information.
    playerTurnLabel = new JButton();
    playerInfoLabel = new JButton();
    
    // creates the layouts for the drop-down buttons and the
    // restart button at the bottom
    boardDropCols = new JPanel(new FlowLayout());
    restartPanel = new JPanel(new FlowLayout());
    
    // creates the flow layout for the player information
    // to be displayed
    playerInfo = new JPanel(new FlowLayout());
    playerInfo.add(playerInfoLabel);
    playerInfo.add(playerTurnLabel);
    
    // Intends to create a list of buttons such that they do not
    // need to each be accessed individually 
    dropButtons = new ArrayList<JButton>();
    
    // Establishes the text of the initialized buttons
    dropCol0 = new JButton("Drop");
    dropCol1 = new JButton("Drop");
    dropCol2 = new JButton("Drop");
    dropCol3 = new JButton("Drop");
    dropCol4 = new JButton("Drop");
    dropCol5 = new JButton("Drop");
    dropCol6 = new JButton("Drop");
    restartButton = new JButton("RESTART GAME");
    swapTurn = new JButton("SWAP TURN");
      
    // Intends to fill a list of drop-down buttons into a 
    // collection so that operations can be done to each of
    // the drop-down buttons without accessing each individually
    dropButtons.add(dropCol0);
    dropButtons.add(dropCol1);
    dropButtons.add(dropCol2);
    dropButtons.add(dropCol3);
    dropButtons.add(dropCol4);
    dropButtons.add(dropCol5);
    dropButtons.add(dropCol6);
 
    // adds the restartButton to a panel which goes into the
    // board layout
    restartPanel.add(restartButton);
    restartPanel.add(swapTurn);
    
    // Gives all of the buttons a red color when enabled. An
    // additional touch to allow red/yellow to know if they're
    // whose turn it is or if they're starting  (because these 
    // buttons become empty in color if the player's waiting).
    for (JButton button : dropButtons) {
      button.setForeground(Color.red);
      boardDropCols.add(button);
    }    
    
    // Establishes the layout for the drop-down buttons and the 
    // physical board itself
    board = new JPanel(new GridLayout(ROWS + 1, COLS));    
    
    // Intends to initialize the values of the board to empty 
    // spots (all white images, all unknown colors in the
    // representative enum colorType grid).
    initializeBoard();
    
    // Intends to create the GUI's "big picture" image, existing
    // of the game board and the necessary buttons
    setLayout(new BorderLayout());
    add(playerInfo, BorderLayout.NORTH);
    add(board, BorderLayout.CENTER);
    add(restartPanel, BorderLayout.SOUTH);

    // creates the action listener which will listen to the 
    // functionality
    ButtonFunctionality dropListener = new ButtonFunctionality();
    
    for (JButton button : dropButtons) {
      button.addActionListener(dropListener);
    }
    restartButton.addActionListener(dropListener); 
    swapTurn.addActionListener(dropListener);
    
  }
  
  // this method intends to parse together a URL in order to 
  // set the color of a given icon depending on the color
  // parameter passed into the function
  public void setColor(JLabel label, String color) {
    URL url = getClass().getResource("/images/" + color + ".jpg");
    // changes the color of the specific JLabel
    label.setIcon(new ImageIcon(url));
  }
  
  // intends to reset the board's GUI and functionality (the 
  // enum grid will have all unknown values, drop buttons
  // will be added, and labels will all be white
  public void initializeBoard() {
    for (JButton button : dropButtons) {
      board.add(button);
    }
    // intends to initialize the grid which will keep track of
    // the moves that have been made
    grid = new colorType[ROWS][COLS];
    
    for (int r = 0; r < ROWS; r++) {
      for (int c = 0; c < COLS; c++) {
        
        // intends to set all labels/grid values to their
        // default/unknown settings
        grid[r][c] = colorType.UNKNOWN;
        setColor(imageGrid[r][c], "white");
        board.add(imageGrid[r][c]);
        
      }
    }
  }  
  
  // function intends to enable each of the buttons, excluding
  // buttons in which the column is already filled
  public void enableButtons(ArrayList<JButton> buttons) {
    // tells the player they are able to make a move once
    // their buttons are re-enabled for gameplay
    playerTurnLabel.setText("Status: Good to go!");
    for (int i = 0; i < buttons.size(); i++) {
      // intends to check that all buttons which would lead
      // to dropping a chip into a full column are selected
      // to be false
      if (columnIsFull(i, ROWS)) {
        buttons.get(i).setEnabled(false);
      }
      else {
        buttons.get(i).setEnabled(true);
      }
    }
  }
  
  // intends to ensure that all buttons are disabled 
  public void disableButtons(ArrayList<JButton> buttons) {

    // sets the player's turn information to waiting while
    // the buttons are disabled
    playerTurnLabel.setText("Status: Waiting... ");
    for (JButton button : buttons) { 
      button.setEnabled(false);
    }
  }
  
  // function intends to place a chip at the nearest available column
  // space on the board. Finds the first unknown grid space, sets the
  // color at the space, and places the associated color value into
  // the grid.
  public void dropInColumn(int totalRows, Integer column, String colorName) {
    for (int r = totalRows - 1; r >= 0; r--) {
      if (grid[r][column] == colorType.UNKNOWN) {
        if (colorName.equals("red")) {
          grid[r][column] = colorType.RED;
        }
        else if (colorName.equals("yellow")) {
          grid[r][column] = colorType.YELLOW;
        }
      // instantiates board chip of location (r, c)  
      setColor(imageGrid[r][column], colorName);
      return;
      }
    }
  }  
  
  // function intends to check if a column is fully filled
  boolean columnIsFull(int column, int totalRows) {
    for (int r = 0; r < totalRows; r++) {
      if (grid[r][column] == colorType.UNKNOWN) {
        return false;
      }
    }
    return true;
  } 
  
  // function intends to check for a horizontal win in a 
  // given row
  boolean isWinInRow
  (colorType[][] board, int row, int cols, colorType color) {
    int count = 0; 
    // intends to increment a counter for each consecutive
    // value seen within a given row. Counter resets when 
    // next move is not consecutive.
    for (int c = 0; c < cols; c++) {
      if (board[row][c] == color) {
        count++;
      }
      else {
        count = 0;
      }
      if (count == WINNING_SCORE) {
        return true;
      }
    }
    return false;
  }
  
  // method intends to ensure that a vertical win within a given
  // column is noticed
  boolean isWinInCol
  (colorType[][] board, int rows, int col, colorType color) {
    int count = 0; 
    // intends to increment a counter each time a consecutive
    // value is found: will reset when no consecutive value
    // is seen
    for (int r = 0; r < rows; r++) {
      if (board[r][col] == color) {
        count++;
      }
      else {
        count = 0;
      }
      if (count == WINNING_SCORE) {
        return true;
      }
    }
    return false;
  }
  
  // method intends to check that there are no negative diagonal
  // wins. It does so with a tri-nested loop, checking each 
  // consecutive diagonal square within the valid bounds of the
  // board. A for loop keeps track of a counter while the nested
  // r/c loops ensure that the function checks each row/col spot
  // The winning counter in the checking loop intends to make
  // the game potentially more expandable.
  boolean isWinNegDiagonal
  (colorType[][] board, int rows, int cols, colorType color) {
    
    // loop conditions set up to rows - (winningscore - 1) 
    // because testing diagonals that go out of bounds would
    // be superfluous
    for (int r = 0; r + (WINNING_SCORE - 1) < rows; r++) {
      for (int c = 0; c + (WINNING_SCORE - 1) < cols; c++) {
        int count = 1;
        // loop controlling the counter
        for (int offset = 1; offset < WINNING_SCORE; offset++) {
          if (board[r][c] == color && 
              board[r][c] == board[r + offset][c + offset]) {
              count++;
          }
        }
        // intends to return that a diagonal win exists if
        // the counter matches the winning score
        if (count == WINNING_SCORE) {
          return true;
        }
      }
    }
    return false;
  }
  
  // method intends to check that there are no positive diagonal
  // wins. It does so with a tri-nested loop, checking each 
  // consecutive diagonal square within the valid bounds of the
  // board. A for loop keeps track of a counter while the nested
  // r/c loops ensure that the function checks each row/col spot.
  // The counter condition (WINNING_SCOERE) within the checking 
  // loop intends to potentially make the board more expandable.
  boolean isWinPosDiagonal
  (colorType[][] board, int rows, int cols, colorType color) {
    // rows start at a positive value 
    for (int r = (rows - WINNING_SCORE + 1); r < rows; r++) {
      for (int c = 0; c < (cols - WINNING_SCORE + 1); c++) {
        int count = 1;
        // for loop involving offsets comparing to the first
        // square found of the color the function is searching
        // for. Increments counter when offset squares equal
        // the first square, and are therefore consecutive
        for (int offset = 1; offset < WINNING_SCORE; offset++) {
          if (board[r][c] == color && 
              board[r - offset][c + offset] == board[r][c]) {
              count++;
          }
        }
        // intends to return true if the count matches the winning score
        if (count == WINNING_SCORE) {
          return true;
        }
      }
    }
    return false;
  }
  
  // method intends to count all of the unknown squares remaining in
  // the grid of values
  int countUnknownSquares(colorType[][] board, int rows, int cols) {
    int unknownSquares = 0;
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        // increments each grid spot with an unknown value
        if (board[r][c] == colorType.UNKNOWN) {
          unknownSquares++;
        }
      }
    }
    return unknownSquares;
  }
  
  // method intends to check that no wins have occurred yet on
  // the board by utilizing the prior functions which analyze
  // smaller portions of the board. This function amalgamates
  // each of the board-checking functions and ensures that 
  // a victory has not occurred yet.
  boolean boardHasNoWins(colorType[][] board, int rows, int cols) {
    
    // intends to insure no diagonal win of either color has occurred
    if (isWinPosDiagonal(board, rows, cols, colorType.RED) || 
        isWinPosDiagonal(board, rows, cols, colorType.YELLOW) || 
        isWinNegDiagonal(board, rows, cols, colorType.RED)|| 
        isWinNegDiagonal(board, rows, cols, colorType.YELLOW)) {
      return false;
    }
    
    // intends to ensure no horizontal row wins have occurred
    // for either color
    for (int r = 0; r < rows; r++) {
      if (isWinInRow(board, r, cols, colorType.RED)) {
        return false;
      }
      if (isWinInRow(board, r, cols, colorType.YELLOW)) {
        return false;
      }
    }
    // intends to ensure no vertical column wins have occurred
    // for either color
    for (int c = 0; c < cols; c++) {
      if (isWinInCol(board, rows, c, colorType.RED)) {
        return false;
      }
      if (isWinInCol(board, rows, c, colorType.YELLOW)) {
        return false;
      }
    }
    return true;
  }
  
  // intends to ensure that the game has satisfied a current
  // win or draw condition. If a board contains no winning 
  // connections, the game will end on a draw if there are 
  // no squares left to play.
  boolean gameIsOver(colorType[][] board, int rows, int cols) {
    if (countUnknownSquares(board, rows, cols) == 0 ||
        !boardHasNoWins(board, rows, cols)) {
      return true;
    }
    else {
      return false;
    }
  }
  
  // intends to return the opposite color of the color parameter
  // passed in
  public String getOppositeColor(String color) {
    if (color.equals("red")) {
      return "yellow";
    }
    else if (color.equals("yellow")) {
      return "red";
    }
    else {
      return "Invalid Color";
    }
  }
  
  // throws the pop-up message displaying a game is 
  // complete and prints a closing message
  public void displayClosingMessage() {
    System.out.println("That's game! Thank you for playing!");
    JOptionPane.showMessageDialog(new JFrame(), 
      "That's a wrap! Great game. Feel free to keep "
      + "toying around with hypothetical moves, "
      + "or restart the board if you "
      + "wish to play again!");
  }
  
  // prints a message notifying users of the grids being reset
  // and creates a pop-up message alerting the users of a grid
  // reset request
  public void displayRestartMessage() {
    System.out.println("Refreshing the board...");
    JOptionPane.showMessageDialog(new JFrame(), 
        "Game restart requested! Refreshing the grid:");     
  }
  
  // intends to display a pop up indicating that a player 
  // wishes to forfeit their turn. 
  public void displayTurnSwapMessage() {
    System.out.println("Changing turns...");
    JOptionPane.showMessageDialog(new JFrame(), 
        "Turn swap requested! Switching current turn:");     
  }
  

  public class ButtonFunctionality implements ActionListener {
    
    public void actionPerformed(ActionEvent onClick) {
      // intends to ensure a user cannot 'spam click'
      // by disabling buttons while waiting on players
      disableButtons(dropButtons);
      restartButton.setEnabled(false);
      
      // ensures you can only forfeit your own move if needed, not
      // force another player into losing theirs.
      swapTurn.setEnabled(false);
      
      // intends to match a button with a user's click request.
      // drops a chip into the column at which a button is 
      // found at. The for loop intends to minimize repetitive code
      // due to many of the drop buttons having the same
      // functionality even though they're in different columns.
      for (Integer i = 0; i < dropButtons.size(); i++) {
        // matches a button with a user's selection
        if (onClick.getSource() == dropButtons.get(i)) {
          // drops a chip in the column location
          dropInColumn(ROWS, i, currentColor);
          receivedCol = i.toString();
          // sends a string representation of the column
          socket.sendString(receivedCol);
          break;
        } 
      }
      // displays a game over message to a user
      // and disables buttons; imgDisplayed ensures that
      // the victory pop-up plays only once (if a user 
      // wants to toy around with hypothetical moves post-game,
      // they don't get spammed with fleets of pop-ups). Ensures
      // only 1 win notification occurs per game
      if (gameIsOver(grid, ROWS, COLS) && !msgDisplayed) {
        disableButtons(dropButtons);
        displayClosingMessage();
        msgDisplayed = true;
      }
      
      // Intends to provide the functionality needed for a game 
      // restart; a restart simultaneously functions as a turn.
      // turn swap functionality can be paired with this to 
      // allow the other player to play first if so desired
      if (onClick.getSource() == restartButton) {
        // intends to allow closing message to pop up again
        // since a new game is started
        msgDisplayed = false;
        // notifies user of restart
        displayRestartMessage();
        // sends the notification that a restart request has
        // been made
        receivedCol = "-1";
        // sends a notification that the restart button is clicked
        socket.sendString(receivedCol);
        // reinitializes the game board
        initializeBoard();

      }    
      // intends to give players the opportunity to exchange 
      // turns either at the start or mid-game if so desired.
      // (only from the senders end, you can't steal turns with
      // this functionality, only forfeit a turn if you want to 
      // change the board's state)
      if (onClick.getSource() == swapTurn) {
        // sends the notification that a request to give a 
        // turn has been made
        receivedCol = "-2";
        socket.sendString(receivedCol);
        // intends to notify the user of a turn being
        // switched/forfeited
        displayTurnSwapMessage();
      }
      // causes the swingWorker created below to execute a cycle
      new ButtonSwingWorker().execute();
    } 
  }
  
  public class ButtonSwingWorker extends SwingWorker <Integer, Integer> {
    
    public Integer doInBackground() {
      int restartSelection = -1;
      int turnSelection = -2;
      // Chosen column intends to represent the received 
      // column from a user's previous selection in the
      // action listener
      int chosenColumn = Integer.parseInt(socket.receiveString());
      // code below intends to mirror the actions of restarting 
      // a game
      if (chosenColumn == restartSelection) {
        msgDisplayed = false; 
        displayRestartMessage();
        initializeBoard();
      }
      // intends to notify the user of a turn switch /
      // a user passing a turn to another
      else if (chosenColumn == turnSelection) {
        displayTurnSwapMessage();
      }
      // else segment of conditional intends to mirror the action
      // of a user dropping a chip into a column in the listener
      else {
        dropInColumn(ROWS, chosenColumn, getOppositeColor(currentColor));
      }     
      
      // conditional intends to mirror a victory message display 
      // from the sending user in action listener.
      if (gameIsOver(grid, ROWS, COLS) && !msgDisplayed) {
        displayClosingMessage();
        msgDisplayed = true;
      }
      
      return 0;
    }
    // intends to set all buttons back to true once the
    // turn-related work is done
    public void done() {
      enableButtons(dropButtons);
      restartButton.setEnabled(true);
      swapTurn.setEnabled(true);
    }
  } 
}
