package com.engine;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import android.graphics.Point;

/**
 * This class is the engine of the game (grid, IA, play method, ...)
 * @author Richard GUERCI & Julien SIERRA
 *
 */
public class Game {
	Piece grid[][];
	int turn;
	int winner;
	ArrayList<Game> next_state;

	/**
	 * Default constructor.
	 */
	public Game(){
		turn=0;
		winner=-1;
		next_state = new ArrayList<Game>();

		grid = new Piece[6][7];
		for(int i=0;i<grid.length;i++){
			for(int j=0;j<grid[i].length;j++){
				grid[i][j] = Piece.None;
			}
		}
	}

	/**
	 * Reset all variables of this Class.
	 */
	public void newGame(){
		turn=0;
		winner=-1;
		for(int i=0;i<grid.length;i++){
			for(int j=0;j<grid[i].length;j++){
				grid[i][j] = Piece.None;
			}
		}
	}

	/**
	 * This function clone the Game in parameter and return a new instance of Game
	 * @return new instance of Game
	 */
	public Game clone(){
		Game result = new Game();

		for(int i=0;i<grid.length;i++){
			for(int j=0;j<grid[i].length;j++){
				result.grid[i][j] = this.grid[i][j];
			}
		}
		result.turn = this.turn;
		result.winner = this.winner;
		result.next_state = new ArrayList<Game>();

		return result;
	}

	/**
	 * This function increment the turn variable (0 or 1)
	 */
	public void nextTurn(){
		turn++;
		turn%=2;
	}

	/**
	 * This function return the number of the player who has to play.
	 * @return turn variable + 1 (1 or 2) 
	 */
	public int getTurn(){
		return turn+1;
	}



	/**
	 * This function allows to play. The piece is selected form the variable "turn".
	 * It will call the "nextTrun()" function until there is no winner.
	 * @param column column number you want to play
	 * @throws BadColumnException thrown when a column is full
	 */
	public int play(int column) throws BadColumnException{
		if(!isDraw()){
			if (grid[5][column] != Piece.None || column < 0 || column >=7) throw new BadColumnException();
			boolean done = false;
			int i = 0;
			while(!done){
				if(grid[i][column] == Piece.None){
					Piece p = Piece.Green;
					if (getTurn()==2) p = Piece.Red; 
					grid[i][column] = p;
					done = true;
				}
				i++;
			}

			if(isWin()){
				winner=getTurn();
			}
			else{
				nextTurn();
			}
			System.err.println("GAME ENGINE SPEAK: column: "+column+", line:"+(i-1));
			return i-1;
		}
		return -1;
	}

	public boolean isDraw(){
		boolean b = true;
		for(int i=0; i<7; i++) if(grid[5][i]==Piece.None) return false;
		return b;
	}

	/**
	 * This function change the four pieces in line into green color.
	 * @return true if someone win
	 */
	public boolean isWin(){
		boolean result = false;

		if(winner!=-1) return true;

		for(int i=0;i<grid.length-3;i++){
			for(int j=0;j<grid[i].length-3;j++){
				/*
				 * ###G
				 * ##G#
				 * #G##
				 * G###
				 */
				if(grid[i][j]!=Piece.None&&grid[i][j]==grid[i+1][j+1]&&grid[i+1][j+1]==grid[i+2][j+2]&&grid[i+2][j+2]==grid[i+3][j+3]){
					grid[i][j]=Piece.Green;
					grid[i+1][j+1]=Piece.Green;
					grid[i+2][j+2]=Piece.Green;
					grid[i+3][j+3]=Piece.Green;
					return true;
				}
				/*
				 * G###
				 * #G##
				 * ##G#
				 * ###G
				 */
				if(grid[i+3][j]!=Piece.None&&grid[i+3][j]==grid[i+2][j+1]&&grid[i+2][j+1]==grid[i+1][j+2]&&grid[i+1][j+2]==grid[i][j+3]){
					grid[i+3][j]=Piece.Green;
					grid[i+2][j+1]=Piece.Green;
					grid[i+1][j+2]=Piece.Green;
					grid[i][j+3]=Piece.Green;
					return true;
				}
			}
		}

		/*
		 * G###
		 * G###
		 * G###
		 * G###
		 */
		for(int i=0;i<grid.length-3;i++){
			for(int j=0;j<grid[i].length;j++){
				if(grid[i][j]!=Piece.None&&grid[i][j]==grid[i+1][j]&&grid[i+1][j]==grid[i+2][j]&&grid[i+2][j]==grid[i+3][j]){
					grid[i][j]=Piece.Green;
					grid[i+1][j]=Piece.Green;
					grid[i+2][j]=Piece.Green;
					grid[i+3][j]=Piece.Green;
					return true;
				}
			}
		}

		/*
		 * ####
		 * ####
		 * ####
		 * GGGG
		 */
		for(int i=0;i<grid.length;i++){
			for(int j=0;j<grid[i].length-3;j++){
				if(grid[i][j]!=Piece.None&&grid[i][j]==grid[i][j+1]&&grid[i][j+1]==grid[i][j+2]&&grid[i][j+2]==grid[i][j+3]){
					grid[i][j]=Piece.Green;
					grid[i][j+1]=Piece.Green;
					grid[i][j+2]=Piece.Green;
					grid[i][j+3]=Piece.Green;
					return true;
				}
			}
		}

		return result;
	}

	/**
	 * @return the number of the player who wins
	 */
	public int whoWin(){
		return winner;
	}

	/**
	 * This function return the grid and the turn of this instance as a string
	 */
	public String toString(){
		String result = "";

		result += "Turn : player " + getTurn() + "\n";

		for(int i=grid.length-1;i>=0;i--){
			result += "| ";
			for(int j=0;j<grid[i].length;j++){
				if(grid[i][j]==Piece.None) result += "# ";
				else if(grid[i][j]==Piece.Yellow) result += "Y ";
				else if(grid[i][j]==Piece.Red) result += "R ";
				else if(grid[i][j]==Piece.Green) result += "G ";
			}
			result += "|\n";
		}
		for(int i=0;i<grid[0].length;i++) result += " Ð";
		result += "\n";

		return result;
	}



	/**
	 * This function allows the computer to play.
	 * There is no strategy here, all moves are random.
	 */
	public Point computerLevel1(){
		boolean has_played = false;//this variable allow the computer to play even if an Exception is thrown.
		Point pnt = new Point();
		while (!has_played){
			try {
				pnt.x=new Random().nextInt(7);
				pnt.y=play(pnt.x);
				has_played=true;
			} catch (BadColumnException e) {}
		}
		return pnt;
	}

	
	
	/**
	 * This function allows the computer to play.
	 * Level 2 : computer try to avoid the player to win and try to win
	 * 2 moves in advance
	 */
	public Point computerLevel2(){
		Point pnt = new Point();
		try {
			pnt.x=bestMove(turn,2);
			pnt.y = play(pnt.x);
		} catch (BadColumnException e) {}
		return pnt;
	}
	
	/**
	 * This function allows the computer to play.
	 * Level 3 : computer try to avoid the player to win and try to win
	 * 4 moves in advance
	 */
	public Point computerLevel3(){
		Point pnt = new Point();
		try {
			pnt.x=bestMove(turn,4);
			pnt.y = play(pnt.x);
		} catch (BadColumnException e) {}
		return pnt;
	}

	private int bestMove(int computer_turn,int depth){
		int results[] = new int[7];

		//Initialize
		for(int i=0; i<7; i++) results[i] = -50000;

		for (int i=0; i<7; i++){
			try {
				Game g = clone();
				g.play(i);
				results[i] = nextMoveRate(computer_turn,depth,depth,g);
			} catch (BadColumnException e) {}
		}

		//best move (max of results)
		int best = 0;
		for(int i=1; i<7; i++) if(results[best] < results[i]) best = i;

		//if there are some column with the same number, we choose one of them
		ArrayList<Integer> best_moves = new ArrayList<Integer>();
		for(int i=0; i<7; i++) if(results[best] == results[i]) best_moves.add(i);

		return best_moves.get(new Random().nextInt(best_moves.size()));
	}

	/**
	 * recursive method, allow to calculate the best move.
	 * @param computer_turn
	 * @param depth
	 * @param maxdepth
	 * @param g
	 * @return
	 */
	private int nextMoveRate(int computer_turn, int depth, int maxdepth, Game g){
		int result = 0;
		
		if(g.isWin() && winner == computer_turn){//computer win
			for(int i = depth; i>0;i--){
				result += 7^(maxdepth-(maxdepth-i)+1);
			}
		}
		else if(g.isWin() && winner != computer_turn){//player win
			for(int i = depth; i>0;i--){
				result += -7^(maxdepth-(maxdepth-i)+1);
			}
		}
		else if(g.isDraw()){//draw
			result = 1;
		}
		
		if(depth>0){
			for (int i=0; i<7; i++){
				try {
					Game g1 = g.clone();
					g1.play(i);
					result += nextMoveRate(computer_turn,depth-1,maxdepth,g1);
				} catch (BadColumnException e) {}
			}
		}

		return result;
	}


	public void playFromConsole(){
		System.out.print("Choose column : ");
		Scanner scan = new Scanner(System.in); 
		int i = scan.nextInt(); 
		try {
			play(i);
		} catch (BadColumnException e) {
			e.printStackTrace();
		}
	}

	
	/*
	/**
	 * Main function for testing
	 * @param args
	 * @throws BadColumnException 
	public static void main(String[] args) throws BadColumnException {
		Game g = new Game();
		System.out.println(g);
		/*for(int i=0; i<42; i++){
			if(i%2==0) g.computerLevel1();
			else g.computerLevel3();
			System.out.println(g);
			if(g.isWin() || g.isDraw()){
				System.out.println("### Player "+g.whoWin()+" win ###");
				i=100;
			}
		}
		g.play(1);
		System.out.println(g);
		
	}*/
}
