package com.online;

/**
 * This class contain the main informations for a session :
 * - id of the session,
 * - name of the session,
 * - name of player 1 and 2 (the user of the devise will always be player 1)
 * - boolean host (true if player 1 (host) the game)
 * 
 * @author Richard GUERCI & Julien SIERRA
 *
 */
public class Session {
	String id = "";
	String name = "";
	String player1 = "";
	String player2 = "";
	boolean host = false;
	
	public Session(String id, String name, String player1, String player2, boolean host) {
		super();
		this.id = id;
		this.name = name;
		this.player1 = player1;
		this.player2 = player2;
		this.host = host;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPlayer1() {
		return player1;
	}

	public void setPlayer1(String player1) {
		this.player1 = player1;
	}

	public String getPlayer2() {
		return player2;
	}

	public void setPlayer2(String player2) {
		this.player2 = player2;
	}

	public boolean isHost() {
		return host;
	}

	public void setHost(boolean host) {
		this.host = host;
	}

	@Override
	public String toString() {
		return "Session [id=" + id + ", name=" + name + ", player1=" + player1
				+ ", player2=" + player2 + ", host=" + host + "]";
	}
	
	
}
