package com.win4causes.app;

import java.sql.*;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.dbcp.BasicDataSource;

public class DBPool {
	private BasicDataSource dataSource;
	private static DBPool instance = new DBPool();

    private DBPool() {
		dataSource = new BasicDataSource();

		dataSource.setDriverClassName( "com.mysql.jdbc.Driver" );
		dataSource.setUsername( "DKrfweo3" );
		dataSource.setPassword( "JKLSq3123e" );
		dataSource.setUrl( "jdbc:mysql://173.231.136.147:3306/drplmain3" );
/*
		dataSource.setDriverClassName( "com.mysql.jdbc.Driver" );
		dataSource.setUsername( "win4caus_U32kdq" );
		dataSource.setPassword( "u9KJER)(#" );
		dataSource.setUrl( "jdbc:mysql://localhost:3306/win4caus_drplmain" );
*/
    }

	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	public static DBPool getInstance() {
		return instance;
	}
}




/*
    public static DataSource setupDataSource( String connectURI ) {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName( "com.mysql.jdbc.Driver" );
		ds.setUsername( "win4caus_U32kdq" );
		ds.setPassword( "u9KJER)(#" );
		ds.setUrl( connectURI );

		return ds;
    }

	public void databaseTest() {
		DataSource dataSource = setupDataSource( "jdbc:mysql://localhost:3306/win4caus_drplmain" );
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		try {
			System.out.println("Creating connection.");
			conn = dataSource.getConnection();
			System.out.println("Creating statement.");
			stmt = conn.createStatement();
			System.out.println("Executing statement.");
			rset = stmt.executeQuery( "SELECT * FROM users" );
			System.out.println("Results:");
			int numcols = rset.getMetaData().getColumnCount();
			while(rset.next()) {
				for(int i=1;i<=numcols;i++) {
					System.out.print("\t" + rset.getString(i));
				}
				System.out.println("");
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			try { if (rset != null) rset.close(); } catch(Exception e) { }
			try { if (stmt != null) stmt.close(); } catch(Exception e) { }
			try { if (conn != null) conn.close(); } catch(Exception e) { }
		}
	}

*/



/*
	public void databaseTest() {
		try {
			Statement stmt;
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/win4caus_drplmain";

			Connection con = DriverManager.getConnection( url, "win4caus_U32kdq", "u9KJER)(#" );

			System.out.println("URL: " + url);
			System.out.println("Connection: " + con);

			stmt = con.createStatement();

			con.close();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}
*/










/*
    public static DataSource setupDataSource( String connectURI ) {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName( "com.mysql.jdbc.Driver" );
		ds.setUsername( "win4caus_U32kdq" );
		ds.setPassword( "u9KJER)(#" );
		ds.setUrl( connectURI );

		return ds;
    }

	public void databaseTest() {
		DataSource dataSource = setupDataSource( "jdbc:mysql://localhost:3306/win4caus_drplmain" );
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		try {
			System.out.println("Creating connection.");
			conn = dataSource.getConnection();
			System.out.println("Creating statement.");
			stmt = conn.createStatement();
			System.out.println("Executing statement.");
			rset = stmt.executeQuery( "SELECT * FROM users" );
			System.out.println("Results:");
			int numcols = rset.getMetaData().getColumnCount();
			while(rset.next()) {
				for(int i=1;i<=numcols;i++) {
					System.out.print("\t" + rset.getString(i));
				}
				System.out.println("");
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			try { if (rset != null) rset.close(); } catch(Exception e) { }
			try { if (stmt != null) stmt.close(); } catch(Exception e) { }
			try { if (conn != null) conn.close(); } catch(Exception e) { }
		}
	}

*/
