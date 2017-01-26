package com.jjpgpmwk.udagudagserver.controller.tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TcpMiniServer extends Thread{

	private Socket socket;    
	private BufferedReader inFromClient;
	private DataOutputStream outToClient;

	private TcpServer parentServer;

	private boolean running = true;

	public TcpMiniServer(Socket socket, BufferedReader inFromClient, DataOutputStream outToClient, TcpServer parentServer) {

		this.socket = socket;
		this.inFromClient = inFromClient;
		this.outToClient = outToClient;
		this.parentServer = parentServer;
	}

	@Override
	public void run(){
		while (running)
			receiveMessageFromClient();
	}

	public void receiveMessageFromClient() {
		try {
			String receivedMessage = inFromClient.readLine(); // returns null if the other end was not closed properly
			if (receivedMessage != null) {
				System.out.println("Received from [CLIENT" + socket.getInetAddress() + "/]: \"" + receivedMessage + "\".");
				String backCommand = parentServer.sendCommandToController(receivedMessage);
				sendMessageToClient(backCommand);
			} else {
				System.out.println("[CLIENT" + socket.getInetAddress() + "/] has closed.");
				shutDown();
			}
		} catch (IOException e) { // happens when the other end was not closed properly or the connection was lost or we closed our connections
			System.out.println("[CLIENT" + socket.getInetAddress() + "/] has disconnected in wrong way.");
			shutDown();
		}
	}

	public boolean sendMessageToClient(String message) {
		try {
			outToClient.writeBytes(message + '\n');
		} catch (IOException e) {	// very unlikely, we don't have to do anything with it, because our loop will do
			System.out.println("Problem while sending a message.");
			return false;
		}
		return true;
	}

	public void closeConnections() { // if there are any connections and they are opened, close them in opposite order to opening

		if (outToClient != null)
			try {
				outToClient.close(); //nothing happens if we close it more than once
			} catch (IOException e) {
				System.out.println("Problem while closing the socket."); // very unlikely
			}

		if (inFromClient != null)
			try {
				inFromClient.close(); // nothing happens if we close it more than once
			} catch (IOException e) {
				System.out.println("Problem while closing input reader."); // very unlikely
			}

		if (socket != null && !socket.isClosed())
			try {
				socket.close(); // not sure if we can close it more than once, but we can check if it is closed
			} catch (IOException e) {
				System.out.println("Problem while closing output stream."); // very unlikely	
			}
	}

	public void shutDown() {
		running = false;
		closeConnections();
		parentServer.removeMiniServer(this);
	}
	
	public void shutDownWithoutRemoving() {
		running = false;
		closeConnections();
	}
}
