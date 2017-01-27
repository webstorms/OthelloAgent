package Players;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.util.ArrayUtil;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;

import Mechanics.Cell;
import Mechanics.Data;
import Mechanics.Game;
import Mechanics.Position;

public class RLValueFunction {

	private static final File f = new File("model");

	private static final float gamma = 0.95f;
	private Stack<Data> stack = new Stack<Data>();
	private MultiLayerNetwork net;
	
	public RLValueFunction() {
		// net = initNetwork();
		net = this.readModel(f);
		
	}
	
	public MultiLayerNetwork initNetwork() {
		int numInput = 64;
		MultiLayerNetwork foo = new MultiLayerNetwork(new NeuralNetConfiguration.Builder()
				.seed(123)
				.iterations(1)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.learningRate(0.01)
				.weightInit(WeightInit.XAVIER)
				.updater(Updater.NESTEROVS).momentum(0.9)
				.list()
				.layer(0, new DenseLayer.Builder().nIn(numInput).nOut(80)
						.activation(Activation.RELU)
						.build())
				.layer(1, new DenseLayer.Builder().nOut(30)
						.activation(Activation.RELU)
						.build())
				.layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
						.activation(Activation.IDENTITY)
						.nOut(1).build())
				.pretrain(false).backprop(true).build()
				);
		foo.init();
		return foo;
		//	net.setListeners(new ScoreIterationListener(1));

	}

	public void add(Data d) {
		this.stack.add(d);
	}

	public void train(float res) {
		List<Cell[][]> boards = new ArrayList<Cell[][]>();
		List<Float> labels = new ArrayList<Float>();

		int it = 0;
		while(!stack.empty()) {
			Data d = stack.pop();
			boards.add(d.state);

			if(it == 0) {
				labels.add(res);
			}
			else {
				labels.add(getIntermediateV(res, it, d.state, d.legalActions, d.chosenAction));
			}
			it++;

		}

		int epochs = 50;
		DataSetIterator iterator = getData(boards, labels, 10);
		
		for(int i = 0; i < boards.size(); i++) {
			System.out.println("Before Score: " + this.getV(boards.get(i)) + " should be " + labels.get(i));

		}
		
		for(int i = 0; i < epochs; i++) {
			iterator.reset();
			net.fit(iterator);

		}

		for(int i = 0; i < boards.size(); i++) {
			System.out.println("After Score: " + this.getV(boards.get(i)) + " should be " + labels.get(i));
			
		}

		// Save the network
		writeModel(net, f);

	}

	// This method retrieves the current state value
	public float getV(Cell[][] board) {		
		INDArray in = Nd4j.create(convertBoard(board));
		INDArray out = Nd4j.create(1, 1).addi(1); // Can be what evs. Who cares. Yolo.
		DataSet next = new DataSet(in, out);
		INDArray output = net.output(next.getFeatureMatrix()); //get the networks prediction

		return (float) output.getDouble(0, 0);

	}

	// This method calculates the new state values
	// score: Final score of end state
	// it: current index of chain going back
	// state: Current state
	private float getIntermediateV(float score, int it, Cell[][] state, List<Position> moves, Position chosenMove) {

		float sum = 0;

		int n = moves.size();

		for(int i = 0; i < n; i++) {
			Game g = new Game(state);
			g.move(moves.get(i).getRow(), moves.get(i).getColumn(), Cell.BLACK);

			float probability = 0;

			if(Position.isSame(moves.get(i), chosenMove)) {
				probability = 1 - RLPlayer.eps;
				sum += Math.pow(gamma, it) * (probability * score);
				//	System.out.println(it + " Fuck yeh dude " + probability);

			}
			else {
				probability = RLPlayer.eps / (n - 1);
				sum += gamma * (probability * this.getV(g.getBoard()));
				//	System.out.println(it + " Other " + probability);

			}


		}

		// System.out.println("Sum: " + sum + " it: " + it + " score: " + score);

		return sum;
	}

	// Data Set relevant methods

	private DataSetIterator getData(List<Cell[][]> boards, List<Float> labels, final int batchSize) {
		int n = boards.size();

		List<DataSet> list = new ArrayList<DataSet>();
		for(int i = 0; i < n; i++) { // Iterate over all all provided states
			for(DataSet d : augment(boards.get(i), labels.get(i))) {
				list.add(d);

			}

		}

		return new ListDataSetIterator(list, batchSize);

	}

	private double[] convertBoard(Cell[][] board) {
		double[][] v = new double[8][8];

		for(int i = 1; i <= 8; i++) {
			for(int j = 1; j <= 8; j++) {
				if(board[i][j] == Cell.BLACK) v[i - 1][j - 1] = 1;
				if(board[i][j] == Cell.EMPTY) v[i - 1][j - 1] = 0;
				if(board[i][j] == Cell.WHITE) v[i - 1][j - 1] = -1;
			}

		}

		return ArrayUtil.flattenDoubleArray(v);

	}

	private List<DataSet> augment(Cell[][] state, float label) {
		List<DataSet> list = new ArrayList<DataSet>();

		for(int i = 0; i < 4; i++) { // How many times to rotate
			Cell[][] tB = copy(state); // Make a copy of the original state
			for(int j = 0; j < i; j++) { // Do them rotations
				tB = rotate(tB);

			}
			list.add(getDataSet(tB, label));
			list.add(getDataSet(flip(tB), label));

		}

		return list;

	}

	private DataSet getDataSet(Cell[][] state, float label) {
		INDArray in = Nd4j.create(convertBoard(state));
		INDArray out = Nd4j.create(1, 1).addi(label);
		return new DataSet(in, out);
	}

	private Cell[][] copy(Cell[][] board) {
		Cell[][] tB = new Cell[Game.DIMENSION][Game.DIMENSION];
		for(int i = 0; i < Game.DIMENSION; i++) {        
			for(int j = 0; j < Game.DIMENSION; j++) {
				tB[i][j] = board[i][j];

			}
		}
		return tB;

	}

	// Augmentation methods

	private Cell[][] rotate(Cell[][] state) {
		Cell[][] foo = new Cell[Game.DIMENSION][Game.DIMENSION];

		// 1) Transpose
		for(int i = 0; i < Game.DIMENSION; i++) {
			for(int j = 0; j < Game.DIMENSION; j++) {
				foo[i][j] = state[j][i];

			}

		}

		Cell[][] foo2 = new Cell[Game.DIMENSION][Game.DIMENSION];

		// 2) Reverse all rows
		for(int i = 0; i < Game.DIMENSION; i++) {
			for(int j = 0; j < Game.DIMENSION; j++) {
				foo2[i][j] = foo[i][Game.DIMENSION - 1 - j];

			}

		}

		return foo2;

	}

	private Cell[][] flip(Cell[][] state) {
		Cell[][] foo = new Cell[Game.DIMENSION][Game.DIMENSION];

		// Flip
		for(int i = 0; i < Game.DIMENSION; i++) {
			for(int j = 0; j < Game.DIMENSION; j++) {
				foo[i][j] = state[i][Game.DIMENSION - 1 - j];

			}

		}

		return foo;

	}

	// Util methods

	private void print(Cell[][] board) {
		for(int i = 1; i < board.length - 1; i++) {
			System.out.println();
			for(int j = 1; j < board.length - 1; j++) {
				System.out.print(board[i][j] + " ");
			}

		}
		System.out.println();
	}

	private MultiLayerNetwork readModel(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			return ModelSerializer.restoreMultiLayerNetwork(fis);

		} 
		catch (IOException e) {
			e.printStackTrace();

		}

		return null;

	}

	private void writeModel(MultiLayerNetwork model, File file) {
		//Where to save the network. Note: the file is in .zip format - can be opened externally
		boolean saveUpdater = true;                                             //Updater: i.e., the state for Momentum, RMSProp, Adagrad etc. Save this if you want to train your network more in the future
		try {
			ModelSerializer.writeModel(model, file, saveUpdater);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}