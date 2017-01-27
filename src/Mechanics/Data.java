package Mechanics;
import java.util.List;

public class Data {

	public Cell[][] state;
	public List<Position> legalActions;
	public Position chosenAction;

	public Data(Cell[][] state, List<Position> legalActions, Position chosenAction) {
		this.state = state;
		this.legalActions = legalActions;
		this.chosenAction = chosenAction;

	}
	
	public void printBoard() {
		for (int i = 1; i <= Game.DIMENSION - 2; i++) {
			for (int j = 1; j <= Game.DIMENSION - 2; j++) {
				System.out.print("[" + state[i][j] + "]");
			}
			System.out.println();
		}
	}

}
