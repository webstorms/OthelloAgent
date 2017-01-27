package Players;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import Mechanics.Cell;
import Mechanics.Data;
import Mechanics.Game;
import Mechanics.Position;
import Mechanics.Simulation;

public class Player {

	protected Type type;
	protected Game game; // The Game object this player can alter
	protected boolean played; // 
	
	private int iterations;
	private Policy black;
	private Policy white;

	public Player(Type type, Game game, int iterations, Policy black, Policy white) {
		this.type = type;
		this.game = game;
		this.iterations = iterations;
		this.black = black;
		this.white = white;
		this.played = false;
		
	}
	
	public Policy getPolicy() {
		if(this.type == Type.BLACK) return black;
		return white;
		
	}
	
	public int getIterations() {
		return this.iterations;
		
	}
	
	public Data move() {
		List<Position> moves = this.getLegalMoves();

		// System.out.println(moves.size());
		
		int size = moves.size();
		if(size == 0) { 
			this.played = false;
			return null;

		}

		this.played = true;
		Position p = null;

		if(iterations == 0) {
			// Just go by the policy
			if(this.type == Type.WHITE) p = white.policy(game.getBoard(), this.getLegalMoves());
			if(this.type == Type.BLACK) p = black.policy(game.getBoard(), this.getLegalMoves());
			
		}
		else {
			// Go with roll-outs
			TreeMap<Float, Position> rankedMoves = new TreeMap<Float, Position>();
			
			// Generate score for all legal actions
			for(Position current : moves) {
				rankedMoves.put(rollout(game, current), current);

			}
			p = rankedMoves.lastEntry().getValue();
			
		}

		// Position p = policy(game.getBoard(), moves);
		Data d = new Data(game.getBoard(), moves, p);
		this.game.move(p.getRow(), p.getColumn(), Player.getCell(type));
		return d;
		
	}

	private Float rollout(Game g, Position p) {

		int score = 0;

		// Roll-out
		for(int i = 0; i < iterations; i++) {
			Game game2 = new Game(g.getBoard());
			game2.move(p.getRow(), p.getColumn(), Player.getCell(Player.Type.BLACK));
			
			Simulation sim = new Simulation(game2, new Player(Player.Type.BLACK, game2, 0, black, white), new Player(Player.Type.WHITE, game2, 0, black, white), Player.Type.WHITE);
			
			// Simulate game
			while(true) {
				sim.update(); 
				if(sim.gameOver()) break;

			}

			if(game2.getWinner() == this.type) { score++; }
			// if(game2.getWinner() == Player.Type.WHITE) { score--; w++; }
			
		}

		return (float) score / iterations;

	}

	List<Position> getLegalMoves() {
		List<Position> legal = new ArrayList<Position>();

		for(int i = 1; i < Game.DIMENSION - 1; i++) {        
			for(int j = 1; j < Game.DIMENSION - 1; j++) {
				if(this.game.isLegal(i, j, Player.getCell(type))) legal.add(new Position(i, j));

			}
		}

		return legal;

	}

	public static Cell getCell(Type player) {
		return (player == Type.BLACK) ? Cell.BLACK : Cell.WHITE;

	}

	public boolean played() {
		return this.played;

	}

	// Different players
	public static enum Type {
		BLACK, WHITE, NOBODY

	}


}