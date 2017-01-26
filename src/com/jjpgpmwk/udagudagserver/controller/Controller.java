package com.jjpgpmwk.udagudagserver.controller;

import java.util.regex.Pattern;

import com.jjpgpmwk.udagudagserver.controller.dao.MessengerDAO;
import com.jjpgpmwk.udagudagserver.controller.tcp.TcpServer;
import com.jjpgpmwk.udagudagserver.model.Model;

public class Controller {

	public static final String SIGN_IN_COMMAND = "[**SIGN_IN_COMMAND**]";
	public static final String SIGN_UP_COMMAND = "[**SIGN_UP_COMMAND**]";
	public static final String MESSAGE_COMMAND = "[**MESSAGE_COMMAND**]";
	public static final String SPLITTER = Pattern.quote("[**SPLITTER**]");

	public static final String SIGN_IN_BACK_COMMAND_TRUE = "[**SIGN_IN_BACK_COMMAND_TRUE**]";
	public static final String SIGN_IN_BACK_COMMAND_FALSE = "[**SIGN_IN_BACK_COMMAND_FALSE**]";
	public static final String SIGN_UP_BACK_COMMAND_TRUE = "[**SIGN_UP_BACK_COMMAND_TRUE**]";
	public static final String SIGN_UP_BACK_COMMAND_FALSE = "[**SIGN_UP_BACK_COMMAND_FALSE**]";
	public static final String MESSAGE_BACK_COMMAND_TRUE = "[**MESSAGE_BACK_COMMAND_TRUE**]";
	public static final String MESSAGE_BACK_COMMAND_FALSE = "[**MESSAGE_BACK_COMMAND_FALSE**]";

	private Model model;
	private TcpServer tcpServer;
	private MessengerDAO messengerDAO;

	public Controller() {
		model = new Model();
		messengerDAO = new MessengerDAO();
		tcpServer = new TcpServer();
		tcpServer.setController(this);
		tcpServer.listen();

	}

	public String executeCommand(String message) {
		
		String[] array = message.split(SPLITTER);

		if (array[0].equals(SIGN_IN_COMMAND))
			return messengerDAO.logIn(array[1], array[2]) ? SIGN_IN_BACK_COMMAND_TRUE : SIGN_IN_BACK_COMMAND_FALSE;

		else if (array[0].equals(SIGN_UP_COMMAND))
			return messengerDAO.signUp(array[1], array[2], array[3], array[4], array[5], array[6], array[7])
					? SIGN_UP_BACK_COMMAND_TRUE : SIGN_UP_BACK_COMMAND_FALSE;
		
		else if (array[0].equals(MESSAGE_COMMAND))
			return tcpServer.sendMessageToAllClients(array[1]) ? MESSAGE_BACK_COMMAND_TRUE : MESSAGE_BACK_COMMAND_FALSE;

		return message;
	}
}
