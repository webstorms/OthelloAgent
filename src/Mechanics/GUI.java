package Mechanics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import Graphics.Graphics;
import Players.Player;
import Players.Policy;
import Players.RLPlayer;
import Players.RLValueFunction;
import Players.RandomPolicy;

public class GUI {

	// Simulation parameters
	private static final Game game = new Game();
	
	private static final Policy bP = new RLPlayer(50, new RLValueFunction());
	private static final Policy wP = new RandomPolicy();
	
	private static final Player black = new Player(Player.Type.BLACK, game, 10, bP, wP);
	private static final Player white =  new Player(Player.Type.WHITE, game, 10, bP, wP);
	private static final Mode mode = Mode.COMP_VS_COMP;
	private static final int delay = 1000;

	public static void main(String[] args) throws Exception {

		Simulation sim = new Simulation(game, black, white, Player.Type.WHITE);
		Graphics graphics = new Graphics(game, sim);
		
		if(mode == Mode.COMP_VS_COMP) {
			Timer timer = new Timer(delay, null);
			timer.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					sim.update();
					graphics.repaint();
					if(sim.gameOver()) {
						timer.stop();
					}
					
				}

			});
			timer.start();

		}
		else if(mode == Mode.COMP_VS_HUM) {
			throw new Exception("Not implemented yet");

		}

	}

	// Different game modes
	enum Mode {
		COMP_VS_HUM, COMP_VS_COMP
	}


}