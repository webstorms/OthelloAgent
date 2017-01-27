package Players;

import java.util.List;
import java.util.Random;

import Mechanics.Cell;
import Mechanics.Position;

public class RandomPolicy implements Policy {
	
	private Random seed = new Random();
	
	@Override
	public Position policy(Cell[][] state, List<Position> moves) {
		int item = seed.nextInt(moves.size());
		return moves.get(item);
		
	}

	
}