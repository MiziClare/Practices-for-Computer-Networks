
/************************************
 * Filename:  SMTPInteraction.java
 ************************************/
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Open an SMTP connection to mailserver and send one mail.
 *
 */
public class SMTPInteraction {
	/** Socket to the SMTP server ***/
	private Socket connection;

	/* Streams for reading from and writing to socket */
	private BufferedReader fromServer;
	private DataOutputStream toServer;

	private static final String CRLF = "\r\n";

	/* Are we connected? Used in close() to determine what to do. */
	private boolean isConnected = false;

	/*
	 * Create an SMTPInteraction object. Create the socket and the
	 * associated streams. Initialise SMTP connection.
	 */

	public SMTPInteraction(EmailMessage mailmessage) throws IOException {
		// Open a TCP client socket with hostname and portnumber specified in
		// mailmessage.DestHost and mailmessage.DestHostPort, respectively.
		connection = new Socket(mailmessage.DestHost, mailmessage.DestHostPort);

		// attach the BufferedReader fromServer to read from the socket and
		// the DataOutputStream toServer to write to the socket
		fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		toServer = new DataOutputStream(connection.getOutputStream());

		// Check reply code from server
		String reply = fromServer.readLine();
		System.out.println("SERVER: " + reply);

		if (!reply.startsWith("220")) {
			throw new IOException("220 reply not received from server.");
		}

		/*
		 * Read one line from server and check that the reply code is 220.
		 * If not, throw an IOException.
		 */

		/*
		 * SMTP handshake. We need the name of the local machine.
		 * Send the appropriate SMTP handshake command.
		 */
		String localhost = InetAddress.getLocalHost().getHostName();
		sendCommand("HELO " + localhost, 250);
		isConnected = true;
	}

	/*
	 * Send message. Write the correct SMTP-commands in the
	 * correct order. No checking for errors, just throw them to the
	 * caller.
	 */
	public void send(EmailMessage mailmessage) throws IOException {

		sendCommand("MAIL FROM: <" + mailmessage.Sender + ">", 250);
		sendCommand("RCPT TO: <" + mailmessage.Recipient + ">", 250);
		sendCommand("DATA", 354);
		toServer.writeBytes(mailmessage.Headers + CRLF + CRLF + mailmessage.Body + CRLF + "." + CRLF);
		toServer.flush();

		/*
		 * Send all the necessary commands to send a message. Call
		 * sendCommand() to do the dirty work. Do _not_ catch the
		 * exception thrown from sendCommand().
		 */

	}

	/*
	 * Close SMTP connection. First, terminate on SMTP level, then
	 * close the socket.
	 */
	public void close() {
		isConnected = false;
		try {
			sendCommand("QUIT", 221);
			connection.close();
		} catch (IOException e) {
			System.out.println("Unable to close connection: " + e);
			isConnected = true;
		}
	}

	/*
	 * Send the SMTP command to the server. Check that the reply code is
	 * what is is supposed to be according to RFC 821.
	 */
	private void sendCommand(String command, int rc) throws IOException {

		/* Write command to server and read reply from server. */
		toServer.writeBytes(command + CRLF);
		// toServer.flush();

		System.out.println("CLIENT: " + command);

		String response = fromServer.readLine();
		System.out.println("SERVER: " + response);

		/*
		 * Check that the server's reply code is the same as the parameter
		 * rc. If not, throw an IOException.
		 */
		if (!response.startsWith(String.valueOf(rc))) {
			throw new IOException(rc + " reply not received from server.");
		}

	}
}
