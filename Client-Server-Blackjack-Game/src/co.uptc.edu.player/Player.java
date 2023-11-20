
// Fig. 27.7: Client.java
// Client portion of a stream-socket connection between client and server.
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Player extends JFrame {
	private String playerName;
    private String host;
    private int port;
	private JButton Hit;
	private JButton Stay;
	private JPanel buttons;
	private JTextArea displayArea; // display information to user
	private ObjectOutputStream output; // output stream to server
	private ObjectInputStream input; // input stream from server
	private Card card; // message from server
	private String message = "";
	private String chatServer; // host server for this application
	private Socket client; // socket to communicate with server
	private int cardamt = 0;
	private static Set<String> playerNamesSet = new HashSet<>();
	BlackjackTable table;
	Decision request;
	Decision stand;
	// initialize chatServer and set up GUI
	public Player(String host) {
		this.host = host;
        this.getPlayerInfo();
        this.createPlayerRequests();
		chatServer = host; // set server to which this client connects
		this.createPlayerRequests();
		buttons = new JPanel();
		buttons.setLayout(new GridLayout(1, 2));
		Hit = new JButton("Pedir");
		Stay = new JButton("Quedarse");

		Hit.addActionListener(new ActionListener() {
			// send message to server
			public void actionPerformed(ActionEvent event) {
				sendData(request);
			} // end method actionPerformed
		} // end anonymous inner class
		); // end call to addActionListener

		Stay.addActionListener(new ActionListener() {
			// send message to server
			public void actionPerformed(ActionEvent event) {
				sendData(stand);
			} // end method actionPerformed
		} // end anonymous inner class
		); // end call to addActionListener

		buttons.add(Hit, BorderLayout.SOUTH);
		buttons.add(Stay, BorderLayout.SOUTH);
		buttons.setVisible(true);
		add(buttons, BorderLayout.SOUTH);
		displayArea = new JTextArea(); // create displayArea
		add(new JScrollPane(displayArea), BorderLayout.CENTER);
		add(this.createTableGame());
		setResizable(false);
		setSize(924,710); // set size of window
		setVisible(true); // show window
	} // end Client constructor
	
	public JPanel createTableGame() {
		 return table = new BlackjackTable();
		 
	}
	
    private void getPlayerInfo() {
        // Use JOptionPane to get player information
        while (true) {
            this.playerName = JOptionPane.showInputDialog("Ingrese su nombre:");
            if (isValidPlayerName(this.playerName)) {
                playerNamesSet.add(this.playerName);
                break;
            } else {
                JOptionPane.showMessageDialog(null, "Nombre invalido. Elija otro nombre.");
            }
        }

        // Prompt for host
        this.host = JOptionPane.showInputDialog("Ingrese el Host:");

        // Prompt for port
        String portString = JOptionPane.showInputDialog("Ingrese el puerto:");
        this.port = Integer.parseInt(portString);
    }

    private static boolean isValidPlayerName(String name) {
        synchronized (playerNamesSet) {
            return !playerNamesSet.contains(name);
        }
    }
    private void savePlayerInfoToFile() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("player_info.bin"))) {
            outputStream.writeObject(playerName);
            outputStream.writeObject(host);
            outputStream.writeInt(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	public void createPlayerRequests() {
		request = new Decision("Pedir");
		stand = new Decision("Quedarse");
	}
	
	// connect to server and process messages from server
	public void runClient() {
        try {
            this.savePlayerInfoToFile();
            this.connectToServer();
            this.getStreams();
            this.processConnection();
        } catch (EOFException eofException) {
            this.displayMessage("\nClient terminated connection");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            this.closeConnection();
        }
    }
// end method runClient

	// connect to server
	private void connectToServer() throws IOException {
        this.displayMessage("Trying to connect\n");

        // create Socket to make connection to server
        this.client = new Socket(InetAddress.getByName(host), port);

        // display connection information
        this.displayMessage("Connected to: " + client.getInetAddress().getHostName());
    }
	// get streams to send and receive data
	private void getStreams() throws IOException {
		// set up output stream for objects
		output = new ObjectOutputStream(client.getOutputStream());
		output.flush(); // flush output buffer to send header information

		// set up input stream for objects
		input = new ObjectInputStream(client.getInputStream());

		displayMessage("\nSe obtuvieron flujos de entrada/salida\n");
	} // end method getStreams
	public void procesarObjeto(Object objetoLeido,String message) {
	    if (objetoLeido instanceof Card) {
	        
	        Card carta = (Card) objetoLeido;
	        table.addImageCards(carta.toString());	        	
	    } else if(objetoLeido instanceof Decision){
	    	System.out.println("entra");
	        message = ((Decision)objetoLeido).getValue();
	    }
	}
	// process connection with server
	private void processConnection() throws IOException {
		message = "Por favor, espera";
		do // process messages sent from server
		{
			try // read message and display it
			{
				Object receivedObject = input.readObject();
	            if (receivedObject instanceof Card) {
	                Card receivedCard = (Card) receivedObject;
	                displayMessage("\nCarta recibida: " + receivedCard.toString() + "\n");
	                // Actualiza la interfaz gráfica para mostrar la carta recibida
	                table.addImageCards(receivedCard.toString());
	            } else if (receivedObject instanceof Decision) {
	                message = ((Decision) receivedObject).getValue();
	                displayMessage("\nMensaje del servidor: " + message + "\n");
	                if (message.contains("¡Te pasaste!") || message.contains("Por favor, espera")) {
	                    buttons.setVisible(false);
	                }
	            }

			} // end try
			catch (ClassNotFoundException classNotFoundException) {
				displayMessage("\nTipo de objeto desconocido recibido");
			} // end catch

		} while (!message.equals("SERVIDOR>>> TERMINADO"));
		System.out.println("salio");
	} // end method processConnection

	// close streams and socket
	private void closeConnection() {
		displayMessage("\nCerrando conexión");

		try {
			output.close(); // close output stream
			input.close(); // close input stream
			client.close(); // close socket
		} // end try
		catch (IOException ioException) {
		} // end catch
	} // end method closeConnection

	// send message to server
	private void sendData(Object message) {
		try // send object to server
		{
			output.writeObject(message);
			output.flush(); // flush data to output

		} // end try
		catch (IOException ioException) {
			displayArea.append("\nError al escribir el objeto");
		} // end catch
	} // end method sendData

	// manipulates displayArea in the event-dispatch thread
	private void displayMessage(final String messageToDisplay) {
        SwingUtilities.invokeLater(() -> this.displayArea.append(messageToDisplay));
    }


}// end player class
