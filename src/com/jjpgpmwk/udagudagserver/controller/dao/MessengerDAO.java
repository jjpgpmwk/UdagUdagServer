package com.jjpgpmwk.udagudagserver.controller.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MessengerDAO {
	private final static String DBURL = "jdbc:mysql://sql.kazior.nazwa.pl:3306/kazior_1";
	private final static String DBUSER = "kazior_1";
	private final static String DBPASS = "Pr0jektZesp0l0wy";
	private final static String DBDRIVER = "com.mysql.jdbc.Driver";


	public boolean logIn(String email, String password) {
		
		String selectQuery = "select email from user where email='"+email+"' and password='"+password+"';";

		boolean succeed = true;
		Connection connection = null;
		Statement statement = null;

			try {
				Class.forName(DBDRIVER).newInstance();
				connection = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
				statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(selectQuery);
				if(!resultSet.next())
					succeed = false;
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
				succeed = false;
				e.printStackTrace();
			} finally {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		return succeed;
	}
	
	public boolean signUp(String email, String birthday, String password, String firstName, String lastName, String place, String state) {
		
		String selectQuery = "select email from user where email='"+email+"';";
		String insertQuery = "insert into user (id, email, birthday, password, firstName, lastName, place, state) values"
				+ " (NULL, '"+email+"', '"+birthday+"', '"+password+"', '"+firstName+"', '"+lastName+"', '"+place+"', '"+state+"');";

		boolean succeed = true;
		Connection connection = null;
		Statement statement = null;

			try {
				Class.forName(DBDRIVER).newInstance();
				connection = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
				statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(selectQuery);
				if(resultSet.next())
					succeed = false;
				else
				    statement.executeUpdate(insertQuery);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
				succeed = false;
				e.printStackTrace();
			} finally {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		return succeed;
	}
}
