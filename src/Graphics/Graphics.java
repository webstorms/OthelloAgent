package Graphics;

import java.awt.*;        
import javax.swing.*;

import Mechanics.Cell;
import Mechanics.Game;
import Mechanics.Simulation;

public class Graphics extends JPanel {

	private Game game;
	private Simulation sim;

	public Graphics(Game game, Simulation sim) {

		this.game = game;
		this.sim = sim;

		// Setup JFrame
		JFrame window = new JFrame("Othello Game");
		window.setContentPane(this);
		window.setSize(529, 551);
		window.setResizable(false);
		window.setLocation(100, 100);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);

	}

	public void paintComponent(java.awt.Graphics g) {
		setBackground(new Color(153, 239, 139));

		int width = getWidth();
		int height = getHeight();
		int offest = width / (Game.DIMENSION - 2);                 

		// Draw the lines on the board
		g.setColor(Color.BLACK);
		for(int i = 1; i <= Game.DIMENSION - 2; i++) {        
			g.drawLine(i * offest, 0, i * offest, height);
			g.drawLine(0, i * offest, width, i * offest);

		}

		for(int i = 1; i < Game.DIMENSION - 1; i++) {        
			for(int j = 1; j < Game.DIMENSION - 1; j++) {
				
				// Draw the discs
				if(game.getBoard()[i][j] == Cell.BLACK) {       
					g.setColor(Color.BLACK);

				}
				else if(game.getBoard()[i][j] == Cell.WHITE) {  
					g.setColor(Color.WHITE);

				}
				if(game.getBoard()[i][j] != Cell.EMPTY) {
					g.fillOval((j * offest) - offest + 7,(i * offest) - offest + 7, 50, 50); 
				}

//				// Show the legal moves for the current player
//				if(sim.getTurn() == Player.Type.BLACK && game.legalMove(i, j, Cell.BLACK, false)) {
//					g.setColor(Color.BLACK);
//					g.fillOval((j * offest + 29) - offest, (i*offest+29)-offest,6,6);
//				}
//				// If other player cannot move, current player cleans up
//				if(sim.getTurn() == Player.Type.WHITE && game.legalMove(i,j, Cell.WHITE,false)) {
//					g.setColor(Color.WHITE);
//					g.fillOval((j*offest+29)-offest,(i*offest+29)-offest,6,6);
//				}

			}
			
		}

	}


} 