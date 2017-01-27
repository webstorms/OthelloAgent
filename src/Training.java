import Mechanics.Data;
import Mechanics.Game;
import Mechanics.Simulation;
import Players.Player;
import Players.Policy;
import Players.Player.Type;
import Players.RLPlayer;
import Players.RLValueFunction;
import Players.RandomPolicy;

public class Training {

	private static final int trainingGames = 100;

	private static final Game game = new Game();
	private static RLValueFunction valueFuncton = new RLValueFunction();
	private static final RLPlayer bP = new RLPlayer(0, valueFuncton);
	private static final Policy wP = new RandomPolicy();
	
	private static Player black = new Player(Player.Type.BLACK, game, 20, bP, wP);
	private static Player white = new Player(Player.Type.BLACK, game, 20, bP, wP);
	private static Simulation sim = new Simulation(game, black, white, Player.Type.WHITE);
	
	public static void main(String[] args) {
		
		for(int j = 0; j < trainingGames; j++) {
			System.out.println("=====================NEW GAME=====================");
			while(true) {
				Data d = sim.update();
				if(d != null) {
					if(sim.getTurn() == Type.WHITE) valueFuncton.add(d);
				}
				if(sim.gameOver()) break;
				
			}
			
			System.out.println("Eps: " + bP.decayEps()); // Decay that epsilon bro
			System.out.println("WINNER: " + game.getWinner());
			float res = 0;
			if(game.getWinner() == Type.BLACK) {
				res = 1;
			}
			else if(game.getWinner() == Type.WHITE) {
				res = -1;
			}
			
			valueFuncton.train(res); // Train that value function bro
			game.reset();
			
		}

	}
	
	
}