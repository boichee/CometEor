package com.win4causes.app;

import java.sql.*;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.win4causes.app.User;
import com.win4causes.app.DatabaseException;
import java.util.Date;
import java.text.DateFormat;

/*
create table auctionprice (
	auctionID integer(11) not null default '0',
	currentprice integer(11) not null default '0',
	primary key( auctionID )
)

create table auctiontime (
	auctionID integer(11) not null default '0',
	endingTime integer(11) not null default '0',
	primary key( auctionID )
)

create table userbids (
	uid integer(11) not null default '0',
	bids integer(11) not null default '0',
	isactive integer(1) not null default '1',
	displayName varchar(255) not null default '',
	link varchar(255) not null default '',
	primary key( uid )
)

create table actionlog (
uid integer(11) not null default 0,
bid integer(11) not null default 1,
auctionID integer(11) not null default 0,
actionType integer(11) not null default 0,
timeMillis bigint not null default 0,
summary varchar(255) not null default '',
description text,
index(uid,auctionID)
);

+----------+---------+------+-----+---------+-------+
| Field    | Type    | Null | Key | Default | Extra |
+----------+---------+------+-----+---------+-------+
| uid      | int(11) | NO   | PRI | 0       |       |
| bids     | int(11) | NO   |     | 0       |       |
| isactive | int(1)  | NO   |     | 1       |       |
+----------+---------+------+-----+---------+-------+
*/

class DrupalBridge {
		//	TO DO: get from database
	public static Auction getCurrentAuction() throws DatabaseException {
		return new Auction( 17 );
	}

	public static Object[] getToplist( int auctionID ) throws DatabaseException {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		ArrayList list;
		String name, link;
		int uid, bids;

		list = new ArrayList();

		String q = "SELECT a.uid, a.bid, u.displayName, u.link, sum(a.bid) AS s FROM actionlog a " +
					" JOIN userbids u ON u.uid=a.uid  " +
					" WHERE a.auctionID = ? GROUP BY a.uid ORDER BY s DESC LIMIT 10";

		try {
			conn = DBPool.getInstance().getConnection();

			ps = conn.prepareStatement( q );

			ps.setInt( 1, auctionID );

			rs = ps.executeQuery();
			while ( rs.next() ) {
				uid = rs.getInt( "uid" );
				bids = rs.getInt( "s" );
				name = rs.getString( "displayName" );
				link = rs.getString( "link" );
 
				if ( uid > 0 ) {
//					list.add( new UserInfo( uid, bids, name, link ) );

					Map<String, Object> temp = new HashMap<String, Object>();
					temp.put( "uid", uid );
					temp.put( "bids", bids );
					temp.put( "name", name );
					temp.put( "link", link );
					list.add( temp );
				}
			}

			return list.toArray();
		}
		catch ( SQLException e ) {
			System.out.println( "bridge " + e.toString() + e.getStackTrace() );
			throw new DatabaseException( e.toString() );
		}
		finally {
			try { if (rs != null) rs.close(); } catch(Exception e) { }
			try { if (ps != null) ps.close(); } catch(Exception e) { }
			try { if (conn != null) conn.close(); } catch(Exception e) { }
		}
	}

	public static void insertActionLog( int uid, int bid, int auctionID, int actionType,
										String summary, String description )
	throws DatabaseException {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		long nowTimeMillis = (new Date()).getTime();

		String q = "INSERT INTO actionlog(uid,bid,auctionID,actionType,timeMillis,summary,description) VALUES(?,?,?,?,?,?,?)";

		try {
			conn = DBPool.getInstance().getConnection();

			ps = conn.prepareStatement( q );
			ps.setInt( 1, uid );
			ps.setInt( 2, bid );
			ps.setInt( 3, auctionID );
			ps.setInt( 4, actionType );
			ps.setLong( 5, nowTimeMillis );
			ps.setString( 6, summary );
			ps.setString( 7, description );
			ps.executeUpdate();
		}
		catch ( SQLException e ) {
			System.out.println( "bridge " + e.toString() + e.getStackTrace() );
			throw new DatabaseException( e.toString() );
		}
		finally {
			try { if (rs != null) rs.close(); } catch(Exception e) { }
			try { if (ps != null) ps.close(); } catch(Exception e) { }
			try { if (conn != null) conn.close(); } catch(Exception e) { }
		}
	}

	public static User createUserFromCookie( String cookie ) throws DatabaseException {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String name, link;
		int uid, bids;

		System.out.println( "bridge::createUserFromCookie=" + cookie );

		if ( cookie == null || cookie.length() < 10 ) {
			return null;
		}

		String q = "SELECT u.uid, u.bids, u.displayName, u.link FROM userbids u " +
					" JOIN sessions s on s.uid = u.uid " +
					" WHERE s.sid=? AND s.uid > 0 AND u.isactive > 0";

		try {
			conn = DBPool.getInstance().getConnection();

			ps = conn.prepareStatement( q );

			ps.setString( 1, cookie );

			rs = ps.executeQuery();

			if ( rs.next() ) {
				uid = rs.getInt( "uid" );
				bids = rs.getInt( "bids" );
				name = rs.getString( "displayName" );
				link = rs.getString( "link" );
 
				if ( uid > 0 ) {
					return new User( uid, bids, name, link );
				}
			}

			return null;
		}
		catch ( SQLException e ) {
			System.out.println( "bridge " + e.toString() + e.getStackTrace() );
			throw new DatabaseException( e.toString() );
		}
		finally {
			try { if (rs != null) rs.close(); } catch(Exception e) { }
			try { if (ps != null) ps.close(); } catch(Exception e) { }
			try { if (conn != null) conn.close(); } catch(Exception e) { }
		}
	}

	public static int decrementUserBids( User user, Auction auction, int decrementAmount, int maxBid, int timeToAdd )
	throws DatabaseException {
		if ( maxBid > 0 ) {
			return decrementUserBidsWithMax( user, auction, decrementAmount, maxBid, timeToAdd );
		}
		else {
			return decrementUserBidsPlain( user, auction, decrementAmount, timeToAdd );
		}
	}

	public static int decrementUserBidsPlain( User user, Auction auction, int decrementAmount, int timeToAdd ) throws DatabaseException {
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		PreparedStatement psUpdateAuctionTime = null;
		int uid, bids, rowCount;

		int priceAdditionAmount = auction.getPriceAdditionAmount();

/*
		String qUpdateUserBids = "UPDATE userbids SET bids = bids - ? WHERE " +
					"uid = ? AND bids - ? >= 0 AND ( ( @oldbids := bids ) OR ( 1 = 1 ) )";

		String qUpdateAuctionPrice = "UPDATE auctionprice SET currentprice = currentprice + ? WHERE " +
					"auctionID = ? AND ( ( @oldcurrentprice := currentprice ) OR ( 1 = 1 ) )";
*/
		String qUpdateUserBids = "UPDATE userbids SET bids = bids - ? WHERE " +
					"uid = ? AND bids - ? >= 0 AND ( @oldbids := bids )";

		String qUpdateAuctionPrice = "UPDATE auctionprice SET currentprice = currentprice + ? WHERE " +
					"auctionID = ? AND ( @oldcurrentprice := currentprice )";

		String qUpdateAuctionTime = "UPDATE auctiontime SET endingTime = endingTime + ? WHERE auctionID = ?";

		try {
			conn = DBPool.getInstance().getConnection();

//////////////////////////////
			ps = conn.prepareStatement( qUpdateUserBids );

			ps.setInt( 1, decrementAmount );
			ps.setInt( 2, user.getID() );
			ps.setInt( 3, decrementAmount );

			rowCount = ps.executeUpdate();
			if ( rowCount < 1 ) {
				return ResultCode.NOT_ENOUGH_BIDS_AVAILABLE;
			}
//////////////////////////////

//////////////////////////////
			ps2 = conn.prepareStatement( qUpdateAuctionPrice );

			ps2.setInt( 1, priceAdditionAmount );
			ps2.setInt( 2, auction.getID() );

			rowCount = ps2.executeUpdate();
			if ( rowCount < 1 ) {
				return ResultCode.GENERAL_ERROR;
			}
//////////////////////////////

//////////////////////////////
			stmt = conn.createStatement();
			rs = stmt.executeQuery( "SELECT @oldbids" );

			if ( rs.next() ) {
				int oldbids = rs.getInt( "@oldbids" );
				System.out.println( "oldbids=" + oldbids );
				user.setBids( Math.max( 0, oldbids - decrementAmount ) );
			}
			else {
				throw new IllegalStateException( "Can't get oldbids" );
			}
//////////////////////////////

//////////////////////////////
			if ( timeToAdd > 0 ) {
				psUpdateAuctionTime = conn.prepareStatement( qUpdateAuctionTime );

				psUpdateAuctionTime.setInt( 1, timeToAdd );
				psUpdateAuctionTime.setInt( 2, auction.getID() );

				rowCount = psUpdateAuctionTime.executeUpdate();
				if ( rowCount < 1 ) {
					throw new DatabaseException( "Can't updateAuctionTime" );
				}
			}
//////////////////////////////

			return ResultCode.OK;
		}
		catch ( SQLException e ) {
			System.out.println( "bridge " + e.toString() + e.getStackTrace() );
			throw new DatabaseException( e.toString() );
		}
		finally {
			try { if (rs != null) rs.close(); } catch(Exception e) { }
			try { if (ps != null) ps.close(); } catch(Exception e) { }
			try { if (ps2 != null) ps2.close(); } catch(Exception e) { }
			try { if (conn != null) conn.close(); } catch(Exception e) { }
		}
	}

	public static int decrementUserBidsWithMax( User user, Auction auction, int decrementAmount, int maxBid, int timeToAdd )
	throws DatabaseException {
		Connection conn = null;
		ResultSet rs = null;
		ResultSet rs3 = null;
		Statement stmt = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		PreparedStatement psUpdateAuctionTime = null;
		int uid, bids, rowCount;

		int priceAdditionAmount = auction.getPriceAdditionAmount();

		String qCheckMaxBid = "SELECT currentprice FROM auctionprice WHERE currentprice < ? AND auctionID = ?";

/*
		String qUpdateUserBids = "UPDATE userbids SET bids = bids - ? WHERE " +
					"uid = ? AND bids - ? >= 0 AND ( ( @oldbids := bids ) OR ( 1 = 1 ) )";

		String qUpdateAuctionPrice = "UPDATE auctionprice SET currentprice = currentprice + ? WHERE " +
					"auctionID = ? AND ( ( @oldcurrentprice := currentprice ) OR ( 1 = 1 ) )";
*/
		String qUpdateUserBids = "UPDATE userbids SET bids = bids - ? WHERE " +
					"uid = ? AND bids - ? >= 0 AND ( @oldbids := bids )";

		String qUpdateAuctionPrice = "UPDATE auctionprice SET currentprice = currentprice + ? WHERE " +
					"auctionID = ? AND ( @oldcurrentprice := currentprice )";

		String qUpdateAuctionTime = "UPDATE auctiontime SET endingTime = endingTime + ? WHERE auctionID = ?";

		try {
			conn = DBPool.getInstance().getConnection();

//////////////////////////////
			ps3 = conn.prepareStatement( qCheckMaxBid );

			ps3.setInt( 1, maxBid );
			ps3.setInt( 2, auction.getID() );

			rs3 = ps3.executeQuery();
			if ( !rs3.next() ) {
				System.out.println( "decrementUserBidsWithMax: not enough " + ps3.toString() );
				return ResultCode.BID_PRICE_NOT_HIGH_ENOUGH;
			}
			System.out.println( "decrementUserBidsWithMax: continuing" );
//////////////////////////////

//////////////////////////////
			ps = conn.prepareStatement( qUpdateUserBids );

			ps.setInt( 1, decrementAmount );
			ps.setInt( 2, user.getID() );
			ps.setInt( 3, decrementAmount );

			rowCount = ps.executeUpdate();
			if ( rowCount < 1 ) {
				return ResultCode.NOT_ENOUGH_BIDS_AVAILABLE;
			}
//////////////////////////////

//////////////////////////////
			ps2 = conn.prepareStatement( qUpdateAuctionPrice );

			ps2.setInt( 1, priceAdditionAmount );
			ps2.setInt( 2, auction.getID() );

			rowCount = ps2.executeUpdate();
			if ( rowCount < 1 ) {
				return ResultCode.GENERAL_ERROR;
			}
//////////////////////////////

//////////////////////////////
			stmt = conn.createStatement();
			rs = stmt.executeQuery( "SELECT @oldbids" );

			if ( rs.next() ) {
				int oldbids = rs.getInt( "@oldbids" );
				user.setBids( Math.max( 0, oldbids - decrementAmount ) );
			}
			else {
				throw new IllegalStateException( "Can't get oldbids" );
			}
//////////////////////////////

//////////////////////////////
			if ( timeToAdd > 0 ) {
				psUpdateAuctionTime = conn.prepareStatement( qUpdateAuctionTime );

				psUpdateAuctionTime.setInt( 1, timeToAdd );
				psUpdateAuctionTime.setInt( 2, auction.getID() );

				rowCount = psUpdateAuctionTime.executeUpdate();
				if ( rowCount < 1 ) {
					throw new DatabaseException( "Can't updateAuctionTime" );
				}
			}
//////////////////////////////

			return ResultCode.OK;
		}
		catch ( SQLException e ) {
			System.out.println( "bridge " + e.toString() + e.getStackTrace() );
			throw new DatabaseException( e.toString() );
		}
		finally {
			try { if (rs != null) rs.close(); } catch(Exception e) { }
			try { if (ps != null) ps.close(); } catch(Exception e) { }
			try { if (ps2 != null) ps2.close(); } catch(Exception e) { }
			try { if (conn != null) conn.close(); } catch(Exception e) { }
		}
	}

	public static int getAuctionPrice( int auctionID ) throws DatabaseException {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		int uid, bids;

		String qGetAuctionPrice = "SELECT currentprice FROM auctionprice WHERE auctionID = ?";

		try {
			conn = DBPool.getInstance().getConnection();

//////////////////////////////
			ps = conn.prepareStatement( qGetAuctionPrice );

			ps.setInt( 1, auctionID );

			rs = ps.executeQuery();

			if ( rs.next() ) {
				return rs.getInt( "currentprice" );
			}
			else {
				throw new IllegalStateException( "Can't get currentprice" );
			}
//////////////////////////////
		}
		catch ( SQLException e ) {
			System.out.println( "bridge " + e.toString() + e.getStackTrace() );
			throw new DatabaseException( e.toString() );
		}
		finally {
			try { if (rs != null) rs.close(); } catch(Exception e) { }
			try { if (ps != null) ps.close(); } catch(Exception e) { }
			try { if (conn != null) conn.close(); } catch(Exception e) { }
		}
	}

	public static int getAuctionTime( int auctionID ) throws DatabaseException {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		int uid, bids;

		String qGetAuctionTime = "SELECT endingTime FROM auctiontime WHERE auctionID = ?";

		try {
			conn = DBPool.getInstance().getConnection();

//////////////////////////////
			ps = conn.prepareStatement( qGetAuctionTime );

			ps.setInt( 1, auctionID );

			rs = ps.executeQuery();

			if ( rs.next() ) {
				return rs.getInt( "endingTime" );
			}
			else {
				throw new IllegalStateException( "Can't get endingTime" );
			}
//////////////////////////////
		}
		catch ( SQLException e ) {
			System.out.println( "bridge " + e.toString() + e.getStackTrace() );
			throw new DatabaseException( e.toString() );
		}
		finally {
			try { if (rs != null) rs.close(); } catch(Exception e) { }
			try { if (ps != null) ps.close(); } catch(Exception e) { }
			try { if (conn != null) conn.close(); } catch(Exception e) { }
		}
	}

	public static boolean incrementAuctionEndTime( int auctionID, int incrementAmount ) throws DatabaseException {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		int uid, bids;

		String qUpdateAuctionTime = "UPDATE auctiontime SET endingTime = endingTime + ? WHERE auctionID = ?";

		try {
			conn = DBPool.getInstance().getConnection();

			ps = conn.prepareStatement( qUpdateAuctionTime );

			ps.setInt( 1, incrementAmount );
			ps.setInt( 2, auctionID );

			int rowCount = ps.executeUpdate();

			return rowCount > 0;
		}
		catch ( SQLException e ) {
			System.out.println( "bridge " + e.toString() + e.getStackTrace() );
			throw new DatabaseException( e.toString() );
		}
		finally {
			try { if (rs != null) rs.close(); } catch(Exception e) { }
			try { if (ps != null) ps.close(); } catch(Exception e) { }
			try { if (conn != null) conn.close(); } catch(Exception e) { }
		}
	}

	public static void testFillTable() throws DatabaseException {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		Random randomGenerator = new Random();
		int uid, i;

		String q = "INSERT INTO test(uid,bid,auctionID) VALUES(?,1,17)";

		try {
			conn = DBPool.getInstance().getConnection();

			ps = conn.prepareStatement( q );

			for ( i = 1; i < 100000; i++ ) {
				uid = randomGenerator.nextInt( 1000 ) + 1;
				ps.setInt( 1, uid );

				ps.executeUpdate();
			}

		}
		catch ( SQLException e ) {
			System.out.println( "bridge " + e.toString() + e.getStackTrace() );
			throw new DatabaseException( e.toString() );
		}
		finally {
			try { if (rs != null) rs.close(); } catch(Exception e) { }
			try { if (ps != null) ps.close(); } catch(Exception e) { }
			try { if (conn != null) conn.close(); } catch(Exception e) { }
		}
	}
}


/*
			conn.setAutoCommit( false );
			conn.setTransactionIsolation( Connection.TRANSACTION_SERIALIZABLE );
*/

