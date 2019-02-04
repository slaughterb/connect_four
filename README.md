# connect_four
Server-client implementation of connect four game in Java. Project uses AWT and Swing to create a GUI visualization of the game logic. The data between the server and client is transferred via a byte stream using sockets.

Launching the server causes it to listen on a specified port number and wait for the client to connect. Once the client connects to the same port, a two-player game of connect four can be played. Users may take turns dropping chips into the columns, or alternatively reset the board or switch turns if desired.
