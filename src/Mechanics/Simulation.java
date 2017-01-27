package Mechanics;

import Players.Player;

public class Simulation {
	
	private Player blackPlayer;
	private Player whitePlayer;
	private Player.Type turn;
	
	// Game: The game to simulate
	// Player objects: The players to use in the simulation
	// Player.Type turn: The current players turn
	
	public Simulation(Game game, Player black, Player white, Player.Type turn) {
		this.blackPlayer = new Player(Player.Type.BLACK, game, black.getIterations(), black.getPolicy(), white.getPolicy());
		this.whitePlayer = new Player(Player.Type.WHITE, game, white.getIterations(), black.getPolicy(), white.getPolicy());
		this.turn = turn;
		
	}
	
	public Player.Type getTurn() {
		return this.turn;
		
	}
	
	public boolean gameOver() {
		return !blackPlayer.played() && !whitePlayer.played();
		
	}
	
	public Data update() {
		if(turn == Player.Type.BLACK) {
			turn = Player.Type.WHITE;
			return blackPlayer.move();
			
		}
		else {
			turn = Player.Type.BLACK;
			return whitePlayer.move();  
			
		}
		
	}

}