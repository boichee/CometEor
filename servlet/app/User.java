package com.cometEor.app;

//	to do: add canonical class elements

public class User {
	private String name, link;
	private int id, bids;

	public User( int i, int b, String nm, String ln ) {
		id = i;
		bids = b;
		name = nm;
		link = ln;
	}

	public int getID() { return id; }
	public void setID( int i ) { id = i; }
	
	public int getBids() { return bids; }
	public void setBids( int b ) { bids = b; }

	public String getName() { return name; }
	public void setName( String nm ) { name = nm; }

	public String getLink() { return link; }
	public void setLink( String ln ) { link = ln; }

	public String toString() {
		return "uid=" + getID() + ", bids=" + getBids();
	}
}
