/**
 * Eoin Lardner
 * 30/10/2017
 */
import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TicTacToeServer extends JFrame {
    private char[] board;
    private JTextArea outputArea;
    private Player[] players;
    private ServerSocket server;
    private Boolean reset = false;
    private boolean rematchButton = false;
    private boolean rematchGame = true;
    private int currentPlayer;
    private int count;
    private final int PLAYER_X = 0, PLAYER_O = 1;
    private final char X_MARK = 'X', O_MARK = 'O';
    private String player1 = "player 1";
    private String player2 = "player 2";
    private String winner = "";
    private final String result = "Result:";
    //private boolean playerMarkChange = false;


    // set up tic-tac-toe server and GUI that displays messages
    public TicTacToeServer()
    {
        super( "Tic-Tac-Toe Server" );

        board = new char[ 9 ];
        players = new Player[ 2 ];
        currentPlayer = PLAYER_X;

        // set up ServerSocket
        try {
            server = new ServerSocket( 1111, 2 );
        }

        // process problems creating ServerSocket
        catch( IOException ioException ) {
            ioException.printStackTrace();
            System.exit( 1 );
        }

        // set up JTextArea to display messages during execution
        outputArea = new JTextArea();
        //getContentPane().add( outputArea, BorderLayout.CENTER );
        getContentPane().add( new JScrollPane( outputArea ), BorderLayout.CENTER );
        outputArea.setText( "Server waiting for connections\n" );

        setSize( 300, 300 );
        setVisible( true );

    } // end TicTacToeServer constructor

    // wait for two connections so game can be played
    public void execute()
    {
        // wait for each client to connect
        for ( int i = 0; i < players.length; i++ ) {

            // wait for connection, create Player, start thread
            try {
                players[ i ] = new Player( server.accept(), i );
                players[ i ].start();
            }

            // process problems receiving connection from client
            catch( IOException ioException ) {
                ioException.printStackTrace();
                System.exit( 1 );
            }
        }

        // Player X is suspended until Player O connects.
        // Resume player X now.
        synchronized ( players[ PLAYER_X ] ) {
            players[ PLAYER_X ].setSuspended( false );
            players[ PLAYER_X ].notify();
        }

    }  // end method execute

    // utility method called from other threads to manipulate
    // outputArea in the event-dispatch thread
    private void displayMessage( final String messageToDisplay )
    {
        // display message from event-dispatch thread of execution
        SwingUtilities.invokeLater(new Runnable() {  // inner class to ensure GUI updates properly
                                       public void run() // updates outputArea
                                       {
                                           outputArea.append( messageToDisplay );
                                           outputArea.setCaretPosition(
                                                   outputArea.getText().length() );
                                       }
                                   }  // end inner class

        ); // end call to SwingUtilities.invokeLater
    }

    // Determine if a move is valid. This method is synchronized because
    // only one move can be made at a time.
    public synchronized boolean validateAndMove( int location, int player )
    {
        //boolean moveDone = false;

        // while not current player, must wait for turn
        while ( player != currentPlayer ) {

            // wait for turn
            try {
                wait();
            }

            // catch wait interruptions
            catch( InterruptedException interruptedException ) {
                interruptedException.printStackTrace();
            }
        }

        // if location not occupied, make move
        if ( !isOccupied( location ) ) {

            board[ location ] = currentPlayer == PLAYER_O ? X_MARK : O_MARK;
            // change current player
            currentPlayer = ( currentPlayer + 1 ) % 2;

            // let new current player know that move occurred
            players[ currentPlayer ].otherPlayerMoved( location );

            notify(); // tell waiting player to continue

            // tell player that made move that the move was valid
            return true;
        }

        // tell player that made move that the move was not valid
        else
            return false;

    } // end method validateAndMove

    // determine whether location is occupied
    public boolean isOccupied( int location )
    {
        if ( board[ location ] == X_MARK || board [ location ] == O_MARK )
            return true;
        else
            return false;
    }
    // place code in this method to determine whether game over
    public boolean isGameOver()
    {
        if(board[0] == X_MARK && board[1] == X_MARK && board[2] == X_MARK || board[0] == O_MARK && board[1] == O_MARK && board[2] == O_MARK)
        {
            if(board[0] == X_MARK)
                winner = player1;
            else
                winner = player2;
            return true;
        }
        else if (board[3] == X_MARK && board[4] == X_MARK && board[5] == X_MARK || board[3] == O_MARK && board[4] == O_MARK && board[5] == O_MARK)
        {
            if(board[3] == X_MARK)
                winner = player1;
            else
                winner = player2;
            return true;
        }
        else if (board[6] == X_MARK && board[7] == X_MARK && board[8] == X_MARK || board[6] == O_MARK && board[7] == O_MARK && board[8] == O_MARK)
        {
            if(board[6] == X_MARK)
                winner = player1;
            else
                winner = player2;
            return true;
        }
        else if (board[0] == X_MARK && board[3] == X_MARK && board[6] == X_MARK || board[0] == O_MARK && board[3] == O_MARK && board[6] == O_MARK)
        {
            if(board[0] == X_MARK)
                winner = player1;
            else
                winner = player2;
            return true;
        }
        else if (board[1] == X_MARK && board[4] == X_MARK && board[7] == X_MARK || board[1] == O_MARK && board[4] == O_MARK && board[7] == O_MARK)
        {
            if(board[1] == X_MARK)
                winner = player1;
            else
                winner = player2;
            return true;
        }
        else if (board[2] == X_MARK && board[5] == X_MARK && board[8] == X_MARK || board[2] == O_MARK && board[5] == O_MARK && board[8] == O_MARK)
        {
            if(board[2] == X_MARK)
                winner = player1;
            else
                winner = player2;
            return true;
        }
        else if (board[0] == X_MARK && board[4] == X_MARK && board[8] == X_MARK || board[0] == O_MARK && board[4] == O_MARK && board[8] == O_MARK)
        {
            if(board[0] == X_MARK)
                winner = player1;
            else
                winner = player2;
            return true;
        }
        else if (board[2] == X_MARK && board[4] == X_MARK && board[6] == X_MARK || board[2] == O_MARK && board[4] == O_MARK && board[6] == O_MARK)
        {
            if(board[2] == X_MARK)
                winner = player1;
            else
                winner = player2;
            return true;
        }
        if(count == 9)
        {
            winner = "draw";
            return true;
        }
        return false;
    }

    public static void main( String args[] )
    {
        TicTacToeServer application = new TicTacToeServer();
        application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        application.execute();
    }

    // private inner class Player manages each Player as a thread
    private class Player extends Thread {
        private Socket connection;
        private DataInputStream input;
        private DataOutputStream output;
        private int playerNumber;
        private char mark;
        protected boolean suspended = true;

        // set up Player thread
        public Player( Socket socket, int number )
        {
            playerNumber = number;

            // specify player's mark
            mark = ( playerNumber == PLAYER_X ? X_MARK : O_MARK );

            connection = socket;

            // obtain streams from Socket
            try {
                input = new DataInputStream( connection.getInputStream() );
                output = new DataOutputStream( connection.getOutputStream() );
            }

            // process problems getting streams
            catch( IOException ioException ) {
                ioException.printStackTrace();
                System.exit( 1 );
            }

        } // end Player constructor

        // send message that other player moved
        public void otherPlayerMoved( int location )
        {
            // send message indicating move
            try
            {
                if(isGameOver())
                {
                    if(winner == "draw")
                        output.writeUTF(result+" "+winner);
                    else
                        output.writeUTF(result+" "+winner+" wins");
                }
                else
                {
                    output.writeUTF("Opponent moved");
                    output.writeInt(location);
                }
            }

            // process problems sending message
            catch ( IOException ioException ) {
                ioException.printStackTrace();
            }
        }

        // control thread's execution
        public void run() {
            // send client message indicating its mark (X or O),
            // process messages from client
            try {
                displayMessage("Player " + (playerNumber ==
                        PLAYER_X ? X_MARK : O_MARK) + " connected\n");

                output.writeChar(mark); // send player's mark

                // send message indicating connection
                output.writeUTF("Player " + (playerNumber == PLAYER_X ?
                        "X connected" : "O connected, please wait"));

                // if player X, wait for another player to arrive
                if (mark == X_MARK) {
                    output.writeUTF("Waiting for another player");

                    // wait for player O
                    try {
                        synchronized (this) {
                            while (suspended)
                                wait();
                        }
                    }

                    // process interruptions while waiting
                    catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }

                    // send message that other player connected and
                    // player X can make a move
                    output.writeUTF("Other player connected. Your move.");
                }
                while (rematchGame) {
                    // while game not over
                    while (!isGameOver())
                    {
                        // get move location from client
                        int location = input.readInt();

                        // check for valid move
                        if (validateAndMove(location, playerNumber)) {
                            displayMessage("\nlocation: " + location);
                            output.writeUTF("Valid move.");
                            count++;
                        }
                        else
                            output.writeUTF("Invalid move, try again");
                    }
                    if (winner == "draw")
                    {
                        displayMessage("\n" + result + winner);
                        output.writeUTF(result + " " + winner);
                        waitForBtn(input);
                    }
                    else
                    {
                        displayMessage("\n" + result + winner + " wins");
                        output.writeUTF(result + " " + winner + " wins");
                        waitForBtn(input);
                    }
                }
                connection.close(); // close connection to client
            }// end try

            // process problems communicating with client
            catch (IOException ioException) {
                ioException.printStackTrace();
                System.exit(1);
            }
        }
        // set whether or not thread is suspended
        public void setSuspended( boolean status )
        {
            suspended = status;
        }

    } // end class Player

    public void waitForBtn(DataInputStream input){
        try {
            while(reset == false) {
                if (rematchButton == false) {

                    players[currentPlayer].output.writeUTF("reset");
                    currentPlayer = (currentPlayer + 1) % 2;
                    players[currentPlayer].output.writeUTF("reset");
                    rematchButton = true;
                }
                if (input.readInt() == 1) {
                    for (int i = 0; i < 9; i++) {
                        board[i] = ' ';
                        count = 0;
                        rematchButton = false;
                        reset = true;
                        currentPlayer = PLAYER_X;

                    }
                    currentPlayer = (currentPlayer + 1) % 2;
                    players[currentPlayer].output.writeUTF("reset2");
                    currentPlayer = PLAYER_O;

                    //rematchButton = false;
                    reset = true;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
} // end class TicTacToeServer
