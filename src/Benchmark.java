import Mechanics.Game;
import Mechanics.Simulation;
import Players.Player;
import Players.Policy;
import Players.RLPlayer;
import Players.RLValueFunction;
import Players.RandomPolicy;

public class Benchmark {

	private static final Game game = new Game();
	private static RLValueFunction valueFuncton = new RLValueFunction();
	private static final RLPlayer bP = new RLPlayer(30, valueFuncton);
	private static final Policy wP = new RandomPolicy();
	private static Player black;
	private static Player white;
	private static Simulation sim;
	
	public static void main(String[] args) {
		
		int blackWin = 0;
		int draw = 0;
		int whiteWin = 0;
		
		black = new Player(Player.Type.BLACK, game, 10, bP, wP);
		white =  new Player(Player.Type.WHITE, game, 10, bP, wP);
		sim = new Simulation(game, black, white, Player.Type.WHITE);
		
		// loop through games
		for(int j = 0; j < 100; j++) {
			while(true) {
				sim.update();
				if(sim.gameOver()) break;
			}
			if(game.getWinner() == Player.Type.BLACK) blackWin++;
			if(game.getWinner() == Player.Type.WHITE) whiteWin++;
			if(game.getWinner() == Player.Type.NOBODY) draw++;
			
			game.reset();
			System.out.println("Completed game " + j);
			System.out.println(blackWin + ", " + whiteWin + ", " + draw);
			
		}
		
		System.out.println(blackWin + ", " + whiteWin + ", " + draw);
		
	}
	
	
}