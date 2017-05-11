package it.consoft.budget.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnect {

	private final String jdbcURL = "jdbc:sqlserver://localhost;databaseName=CONSOFT_PROD_2009SP1;user=sa;password=Passw0rd;";

	public Connection getConnection(){
		
		Connection conn = null;
		
		try {
			
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn = DriverManager.getConnection(jdbcURL); 
		
		} catch (Exception e) {
	
			e.printStackTrace();
		}
		
		
		return conn;
	}
}
