package Players;

import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import Mechanics.Cell;
import Mechanics.Position;

public class RLPlayer implements Policy {

	// Training variables
	private static final Random seed = new Random();
	public static float eps = 1f; // CHANGE THIS AT A LATER STAGE! Forces both players to have the same eps.
	private int it = 0;
	private RLValueFunction valueFunc;

	public RLPlayer(int it, RLValueFunction valueFunc) {
		this.valueFunc = valueFunc;
		this.it = it;
		this.decayEps();

	}

	// Called by roll-outs
	@Override
	public Position policy(Cell[][] state, List<Position> moves) {

		// Get best move according to policy
		TreeMap<Float, Position> rankedMoves = new TreeMap<Float, Position>();

		// Generate score for all legal actions
		for(Position current : moves) {
			rankedMoves.put(getValue(state), current);

		}

		Position optimal = rankedMoves.lastEntry().getValue();
		
		if(seed.nextFloat() <= eps && moves.size() > 0) {
			int item = seed.nextInt(moves.size());
			return moves.get(item);

		}
		
		return optimal;

	}

	// Make sure this is referenced by all policy objects
	private float getValue(Cell[][] board) {
		return (float) this.valueFunc.getV(board);

	}

	public float decayEps() {
		// Decay Eps
		return eps = (float) Math.exp(-0.05 * it++);

	}


}