package com.jjpgpmwk.udagudagserver.controller.tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.jjpgpmwk.udagudagserver.controller.Controller;

public class TcpServer {

	private Controller controller;

	private ServerSocket serverSocket;
	private List<TcpMiniServer> tcpMiniServers;
	
	private volatile boolean running = true;

	public TcpServer() {
		try {
			serverSocket = new ServerSocket(8086);
		} catch (IOException e) {
			System.out.println("Cannot create SERVER. The port is already in use.");
		}
		tcpMiniServers = new ArrayList<TcpMiniServer>();
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	public void listen() {
		while (running) {
			try {
				Socket clientSocket = serverSocket.accept();
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());

				System.out.println("[CLIENT" + clientSocket.getInetAddress() + "/] has joined.");

				TcpMiniServer tcpMiniServer = new TcpMiniServer(clientSocket, inFromClient, outToClient, this);
				tcpMiniServer.start();
				tcpMiniServers.add(tcpMiniServer);

			} catch (IOException e) {
				System.out.println("Problem while connecting to new CLIENT."); // very unlikely
			}
		}
		// to shutDownServer, we only have to stop the thread with setting 'running' to false and now we can do the rest: (not thread yet)
		shutDownServer();
	}
	
	private void setRunning(boolean running) { // it can be used from GUI
		this.running = running;
	}

	private void shutDownServer() {
		
		shutDownAllMiniServers();

		if (serverSocket != null && !serverSocket.isClosed())
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	private void shutDownAllMiniServers() {
		for (TcpMiniServer ms : tcpMiniServers)
			ms.shutDownWithoutRemoving();
		tcpMiniServers.clear();
	}
	

	public boolean sendMessageToAllClients(String message) {
		boolean success = true;
		for (TcpMiniServer ms : tcpMiniServers)
			if (ms.sendMessageToClient(message) == false)
				success = false;
		return success;
	}

	public String sendCommandToController(String message) {
		return controller.executeCommand(message);
	}

	public void removeMiniServer(TcpMiniServer tcpMiniServer) {
		int index = tcpMiniServers.indexOf(tcpMiniServer);
		tcpMiniServers.remove(index);
	}
}
