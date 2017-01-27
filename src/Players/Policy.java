package Players;

import java.util.List;

import Mechanics.Cell;
import Mechanics.Position;

public interface Policy {
	
	// Mapping from state to action
	public abstract Position policy(Cell[][] state, List<Position> moves);
	
}
