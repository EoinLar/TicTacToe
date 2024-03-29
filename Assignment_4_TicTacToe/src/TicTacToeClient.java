/**
 * Eoin Lardner
 * 30/10/2017
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class TicTacToeClient extends JFrame {

    private JTextField idField;
    private JTextArea displayArea;
    private JButton btn = new JButton();
    private JPanel boardPanel, panel2;
    private Square board[][], currentSquare;
    private Socket connection;
    private DataInputStream input;
    private DataOutputStream output;
    private char myMark;
    private boolean myTurn;
    private final char X_MARK = 'X', O_MARK = 'O';

    public TicTacToeClient()
    {
        super( "Tic-Tac-Toe Client" );

        // set up ClientSocket
        try {
            connection = new Socket( InetAddress.getLocalHost(), 1111);

            // get streams
            input = new DataInputStream( connection.getInputStream() );
            output = new DataOutputStream( connection.getOutputStream() );
        }

        // process problems creating ServerSocket
        catch( IOException ioException ) {
            ioException.printStackTrace();
            System.exit( 1 );
        }

        // create and start output thread
        Thread outputThread = new Thread();
        outputThread.start();

        Container container = getContentPane();

        // set up JTextArea to display messages to user
        displayArea = new JTextArea( 4, 30 );
        displayArea.setEditable( false );
        container.add( new JScrollPane( displayArea ), BorderLayout.SOUTH );

        // set up panel for squares in board
        boardPanel = new JPanel();
        boardPanel.setLayout( new GridLayout( 3, 3, 0, 0 ) );

        // create board
        board = new Square[ 3 ][ 3 ];

        // When creating a Square, the location argument to the constructor
        // is a value from 0 to 8 indicating the position of the Square on
        // the board. Values 0, 1, and 2 are the first row, values 3, 4,
        // and 5 are the second row. Values 6, 7, and 8 are the third row.
        for ( int row = 0; row < board.length; row++ ) {

            for ( int column = 0; column < board[ row ].length; column++ ) {

                // create Square
                board[ row ][ column ] = new Square( ' ', row * 3 + column );
                boardPanel.add( board[ row ][ column ] );
            }
        }

        // textfield to display player's mark
        idField = new JTextField();
        idField.setEditable( false );
        container.add( idField, BorderLayout.NORTH );

        // set up panel to contain boardPanel (for layout purposes)
        panel2 = new JPanel();
        panel2.add( boardPanel, BorderLayout.CENTER );
        container.add( panel2, BorderLayout.CENTER );

        setSize( 350, 350 );
        setVisible( true );

    } // end TicTacToeServer constructor

    // control thread that allows continuous update of displayArea
    public void run()
    {
        // get player's mark (X or O)
        try {
            myMark = input.readChar();

            // display player ID in event-dispatch thread
            SwingUtilities.invokeLater(
                    new Runnable() {
                        public void run()
                        {
                            idField.setText( "You are player \"" + myMark + "\"" );
                        }
                    }
            );

            myTurn = ( myMark == X_MARK ? true : false );

            // receive messages sent to client and output them
            while ( true ) {
                processMessage( input.readUTF() );
            }

        } // end try

        // process problems communicating with server
        catch ( IOException ioException ) {
            ioException.printStackTrace();
        }

    }  // end method run

    // process messages received by client
    private void processMessage( String message )
    {
        // valid move occurred
        if ( message.equals( "Valid move." ) ) {
            displayMessage( "Valid move, please wait.\n" );
            setMark( currentSquare, myMark );
        }

        // invalid move occurred
        else if ( message.equals( "Invalid move, try again" ) ) {
            displayMessage( message + "\n" );
            myTurn = true;
        }

        // opponent moved
        else if ( message.equals( "Opponent moved" ) ) {

            // get move location and update board
            try {
                int location = input.readInt();
                int row = location / 3;
                int column = location % 3;

                setMark(  board[ row ][ column ],
                        ( myMark == X_MARK ? O_MARK : X_MARK ) );
                displayMessage( "Opponent moved. Your turn.\n" );
                myTurn = true;

            } // end try

            // process problems communicating with server
            catch ( IOException ioException ) {
                ioException.printStackTrace();
            }

        } // end else if
        //Process winning or draw
        else if (message.equals("Player 1 wins") ||
                message.equals("Player 2 wins")||
                message.equals("Draw"))
        {
            displayMessage(message + "\n");
            myTurn = false;
        }
        else if(message.equals("reset")){
            panel2.add(btn);
            btn.setBounds(30,20,60,30);
            btn.setText("Play Again");
            btn.setVisible(true);
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for( int r = 0; r <board.length;r++){
                        for(int c = 0; c < board[r].length;c++){
                            setMark(board[r][c],' ');
                        }
                    }
                    displayArea.setText("");
                    displayArea.setText("Please wait for your turn\n");
                    try{
                        output.writeInt(1);
                        btn.setVisible(false);
                    }
                    catch(IOException IOE){
                        IOE.printStackTrace();
                    }
                }
            });
        }
        else if(message.equals("reset2")){
            for ( int row = 0; row < board.length; row++ ) {
                for ( int column = 0; column < board[ row ].length; column++ ) {
                    // create Square
                    setMark(board[ row ][ column ], ' ');
                }
            }
            btn.setVisible(false);
            displayArea.setText("  ");
            displayArea.setText("Other player started a new game\nYour Turn\n");

            myTurn = true;
        }
        // simply display message
        else
            displayMessage( message + "\n" );

    } // end method processMessage

    private void displayMessage( final String messageToDisplay )
    {
        // display message from event-dispatch thread of execution
        SwingUtilities.invokeLater(
                new Runnable() {  // inner class to ensure GUI updates properly

                    public void run() // updates displayArea
                    {
                        displayArea.append( messageToDisplay );
                        displayArea.setCaretPosition(
                                displayArea.getText().length() );
                    }

                }  // end inner class

        ); // end call to SwingUtilities.invokeLater
    }

    // utility method to set mark on board in event-dispatch thread
    private void setMark( final Square squareToMark, final char mark )
    {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run()
                    {
                        squareToMark.setMark( mark );
                    }
                }
        );
    }

    // send message to server indicating clicked square
    public void sendClickedSquare( int location )
    {
        if ( myTurn ) {

            // send location to server
            try {
                output.writeInt( location );
                myTurn = false;
            }

            // process problems communicating with server
            catch ( IOException ioException ) {
                ioException.printStackTrace();
            }
        }
    }

    // set current Square
    public void setCurrentSquare( Square square )
    {
        currentSquare = square;
    }

    // private inner class for the squares on the board
    private class Square extends JPanel {
        private char mark;
        private int location;

        public Square( char squareMark, int squareLocation )
        {
            mark = squareMark;
            location = squareLocation;

            addMouseListener(
                    new MouseAdapter() {
                        public void mouseReleased( MouseEvent e )
                        {
                            setCurrentSquare( Square.this );
                            sendClickedSquare( getSquareLocation() );
                        }
                    }
            );

        } // end Square constructor

        // return preferred size of Square
        public Dimension getPreferredSize()
        {
            return new Dimension( 30, 30 );
        }

        // return minimum size of Square
        public Dimension getMinimumSize()
        {
            return getPreferredSize();
        }

        // set mark for Square
        public void setMark( char newMark )
        {
            mark = newMark;
            repaint();
        }

        // return Square location
        public int getSquareLocation()
        {
            return location;
        }

        // draw Square
        public void paintComponent( Graphics g )
        {
            super.paintComponent( g );

            g.drawRect( 0, 0, 29, 29 );
            g.drawString( String.valueOf( mark ), 11, 20 );
        }

    } // end inner-class Square

    public static void main( String args[] )
    {
        TicTacToeClient application = new TicTacToeClient();
        application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        application.run();
    }
} // end class TicTacToeClient
