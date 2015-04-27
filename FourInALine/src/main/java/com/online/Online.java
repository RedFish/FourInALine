package com.online;

/**
 * 
 * source : http://www.androidhive.info/2011/10/android-making-http-requests/
 * http://stackoverflow.com/questions/10692469/using-androidhttpclient-will-not-work
 * @author Richard GUERCI & Julien SIERRA
 *
 */
public class Online {
	public static Session session = null;
	static final String website = "http://jsierra.toile-libre.org/";

	public static String sessions(){
		return website+ "index.php";
	}

	public static String gameOver(String id_session){
		return website + "game_over.php?id_session="+id_session.trim();
	}

	public static String join(String id_session, String player2){
		return website + "join.php?id_session="+id_session+"&player2="+player2.trim();
	}

	public static String create(String name_session, String player1){
		return website + "create.php?name_session="+name_session.trim()+"&player1="+player1.trim();
	}

	public static String play(String id_session, String player, String column){
		return website + "play.php?id_session="+id_session.trim()+"&player="+player.trim()+"&column="+column.trim();
	}

	public static String lastMove(String id_session){
		return website + "last_move.php?id_session="+id_session.trim();
	}
}
