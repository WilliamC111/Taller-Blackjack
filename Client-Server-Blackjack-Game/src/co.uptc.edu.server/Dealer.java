
// Modified Fig. 27.5: Multi-threaded Chat Server.java
// Server portion of a client/server stream-socket connection. 
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Dealer extends JFrame {

	private JButton Deal;
	private Deck newdeck;
	private JTextArea displayArea; // display information to user
	private ExecutorService executor; // will run players
	private ServerSocket server; // server socket
	private SockServer[] sockServer; // Array of objects to be threaded
	private int counter = 1; // counter of number of connections
	private Card dcard1, dcard2;
	private ArrayList<Playbj> players;
	private Playbj dcards;
	private int playersleft;
	private boolean roundover = true;
	private Decision request;

	// set up GUI
	public Dealer() {

		super("Dealer");

		players = new ArrayList();
		sockServer = new SockServer[100]; // allocate array for up to 10 server threads
		executor = Executors.newFixedThreadPool(100); // create thread pool
		request = new Decision("Te pasaste!\n");
//		Deal = new JButton("Repartir Cartas");
//		Deal.addActionListener(new ActionListener() {
//			// send message to client
//			public void actionPerformed(ActionEvent event) {
//				Deal.setEnabled(false);
//				newdeck = new Deck();
//				roundover = false;
//				DealCards();
//				displayMessage("\n\nCARTAS REPARTIDAS\n\n");
//
//			} // end method actionPerformed
//		} // end anonymous inner class
//		); // end call to addActionListener

		//add(Deal, BorderLayout.SOUTH);

		displayArea = new JTextArea(); // create displayArea
		displayArea.setEditable(false);
		add(new JScrollPane(displayArea), BorderLayout.CENTER);

		setSize(300, 300); // set size of window
		setVisible(true); // show window
	} // end Server constructor

	// set up and run server
	public void runDeal() {
		try // set up server to receive connections; process connections
		{
			server = new ServerSocket(23555, 100); // create ServerSocket

			while (true) {
				try {
					// create a new runnable object to serve the next client to call in
					sockServer[counter] = new SockServer(counter);
					// make that new object wait for a connection on that new server object
					sockServer[counter].waitForConnection();
					// launch that server object into its own new thread
					executor.execute(sockServer[counter]);
					
					if(counter == 3) {
						newdeck = new Deck();
						newdeck.shuffle();
						DealCards();
						System.out.println("Cartas Repartidas");
					}
					
					// then, continue to create another object and wait (loop)

				} // end try
				catch (EOFException eofException) {
					displayMessage("\n\r\nConexión del servidor finalizada ");
				} // end catch
				finally {
					if(counter!=3) {
					++counter;	
					}
			   } // end finally
			} // end while
		} // end try
		catch (IOException ioException) {
		} // end catch
	} // end method runServer

	// manipulates displayArea in the event-dispatch thread
	private void displayMessage(final String messageToDisplay) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() // updates displayArea
			{
				displayArea.append(messageToDisplay); // append message
			} // end method run
		} // end anonymous inner class
		); // end call to SwingUtilities.invokeLater
	} // end method displayMessage

	private void DealCards() {

		try {
			playersleft = counter - 1;
			newdeck.shuffle();
			dcard1 = newdeck.dealCard();
			dcard2 = newdeck.dealCard();
			displayMessage("\n\n" + dcard1 + " " + dcard2);

			for (int i = 1; i <= counter; i++) {
				Card c1, c2;
				c1 = newdeck.dealCard();
				c2 = newdeck.dealCard();
				Playbj p = new Playbj(c1, c2);
				players.add(p);
				sockServer[i].sendData(new Decision("jugando"));
				sockServer[i].sendData(c1);
				sockServer[i].sendData(c2);
			}
		} catch (NullPointerException n) {
		}
	}

	private void Results() {

		try {
			System.out.println(counter);
			for (int i = 1; i <= counter; i++) {
				// sockServer[i].sendData("El Dealer tiene: " + dcards.GetCardTotal());

				if ((dcards.GetCardTotal() <= 21) && (players.get(i - 1).GetCardTotal() <= 21)) {

					if (dcards.GetCardTotal() > players.get(i - 1).GetCardTotal()) {
						// sockServer[i].sendData("\n Tu pierdes!");
					}

					if (dcards.GetCardTotal() < players.get(i - 1).GetCardTotal()) {
						// sockServer[i].sendData("\n You Ganas!");
					}

					if (dcards.GetCardTotal() == players.get(i - 1).GetCardTotal()) {
						// sockServer[i].sendData("\n Empate!");
					}

				} // end if statement when dealer and player are under 21

				if (dcards.CheckBust()) {

					if (players.get(i - 1).CheckBust()) {
						// sockServer[i].sendData("\n Empate!");
					}
					if (players.get(i - 1).GetCardTotal() <= 21) {
						// sockServer[i].sendData("\n Ganaste!");
					}
				}

				if (players.get(i - 1).CheckBust() && dcards.GetCardTotal() <= 21) {
					// sockServer[i].sendData("\n Tu pierdes!");
				}
			} // end for loop

		} // end try block
		catch (NullPointerException n) {
		}
	}

	/*
	 * This new Inner Class implements Runnable and objects instantiated from this
	 * class will become server threads each serving a different client
	 */
	private class SockServer implements Runnable {
		private ObjectOutputStream output; // output stream to client
		private ObjectInputStream input; // input stream from client
		private Socket connection; // connection to client
		private int myConID;

		public SockServer(int counterIn) {
			myConID = counterIn;
		}

		public void run() {
			try {
				try {
					getStreams(); // get input & output streams
					processConnection(); // process connection
				} // end try
				catch (EOFException eofException) {
					displayMessage("\nServidor" + myConID + "coneccion terminada");
				} finally {
					closeConnection(); // close connection
				} // end catch
			} // end try
			catch (IOException ioException) {
			} // end catch
		} // end try

		// wait for connection to arrive, then display connection info
		private void waitForConnection() throws IOException {

			displayMessage("Esperando la conexión" + myConID + "\n");
			connection = server.accept(); // allow server to accept connection
			displayMessage("Conexión " + myConID + " recibida de: " + connection.getInetAddress().getHostName());
		} // end method waitForConnection

		private void getStreams() throws IOException {
			// set up output stream for objects
			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush(); // flush output buffer to send header information

			// set up input stream for objects
			input = new ObjectInputStream(connection.getInputStream());

			displayMessage("\nSe obtuvieron flujos de entrada/salida\n");
		} // end method getStreams

		// process connection with client
		private void processConnection() throws IOException {
			String message = "Conexión " + myConID + " Exitosa";
			// sendData(message); // send connection successful message
			System.out.println(message);

			do // process messages sent from client
			{
				try // read message and display it
				{
					if (message.contains("Pedir")) {
						cardhit();
					}

					if (message.contains("Quedarse")) {
						this.sendData(new Decision("Esperar por favor"));
						playersleft--;
						CheckDone();
					}

					message = ((Decision) input.readObject()).getValue(); // read new message
					System.out.println(message);
				} // end try
				catch (ClassNotFoundException classNotFoundException) {
					displayMessage("\nTipo de objeto desconocido recibido");
				} // end catch

			} while (!message.equals("CLIENTE>>> TERMINAR"));
			System.out.println("salio");
		} // end method processConnection

		private void DealerGo() {
			dcards = new Playbj(dcard1, dcard2);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (dcards.GetCardTotal() < 16) {
				while (dcards.GetCardTotal() < 16) {
					Card card1 = newdeck.dealCard();
					dcards.addCard(card1);

					displayMessage(
							"Dealer pide..." + card1.toString() + "\n" + "Total:" + dcards.GetCardTotal() + "\n");
				}
			}
			if (dcards.CheckBust()) {
				displayMessage("Dealer pasa!");
			} else {
				displayMessage("Dealer tiene:" + " " + dcards.GetCardTotal());
			}

			Results();
		}

		private void cardhit() {

			Card nextc = newdeck.dealCard();
			sendData(nextc);
			players.get(this.myConID - 1).addCard(nextc);
			sendData("Tu Total: " + players.get(this.myConID - 1).GetCardTotal());
			if (players.get(this.myConID - 1).CheckBust()) { // if player busted
				sendData(request);
				playersleft--;
				if (playersleft == 0) {
					DealerGo();
				}
			}

		}

		private void CheckDone() {

			if (playersleft == 0) {

				DealerGo();
			}
		}

		// close streams and socket
		private void closeConnection() {
			displayMessage("\nTerminando conexión " + myConID + "\n");

			try {
				output.close(); // close output stream
				input.close(); // close input stream
				connection.close(); // close socket
			} // end try
			catch (IOException ioException) {
			} // end catch
		} // end method closeConnection

		private void sendData(Object object) {
			try // send object to client
			{
				output.writeObject(object);
				output.flush(); // flush output to client

			} // end try
			catch (IOException ioException) {
				displayArea.append("\nError al escribir el objeto");
			} // end catch
		} // end method sendData

	} // end class SockServer

} // end class Server
