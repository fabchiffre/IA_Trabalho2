import java.util.Scanner;
import java.util.Random;

class QMachine {
	private double gamma;
	private double alpha;
	// private double n;
	// private double m;
	private State[][] board;
	private double defaultReward;

	public final static State NULL_STATE = new State(Double.NEGATIVE_INFINITY);

	private static Random randGen = new Random();

	private static final int EAST = 0;
	private static final int NORTH = 1;
	private static final int WEST = 2;
	private static final int SOUTH = 3;

	public QMachine(double gamma, double alpha) {
		this.alpha = alpha;
		this.gamma = gamma;
	}

	public void QLearn(int nbIter) {
		
		for (int iter = 0; iter< nbIter; iter++) {
			State current = board[board.length - 2][1];
			while(current.reward == defaultReward) 
				current = current.updateQ(this.alpha, this.gamma);
		}
	}

	public void setBoard(State[][] board) {
		this.board = board;
	}

	public State[][] getBoard() {
		return board;
	}

	public void setDefaultReward(double defaultReward) {
		this.defaultReward = defaultReward;
	}


	public void displayAction() {
		for(int i=0; i<board.length; ++i) {
			
			for(int j=0; j<board[0].length; ++j)
				System.out.print("_______");
			System.out.print("\n");

			
			for(int j=0; j<board[0].length; ++j) {
				if(j == 0)
					System.out.print("|");

				if(board[i][j] == NULL_STATE) {
					System.out.print("  XXX |");
				}

				else {
					switch(board[i][j].chooseBestAction()) {
						case 0:
						System.out.print("RIGHT |");
						break;
						case 1:
						System.out.print(" TOP  |");
						break;
						case 2:
						System.out.print(" LEFT |");
						break;
						case 3:
						System.out.print(" BOT  |");
						break;
					}
				}
			}
			System.out.print("\n");
		}

		for(int j=0; j<board[0].length; ++j)
			System.out.print("_______");
		System.out.print("\n");
	}

	public static class State {
		public double[] value = new double[4];
		public State[] next = new State[4];
		public double reward;

		public State(double reward) {
			this.reward = reward;
		}

		public void initNext(State[][] q, int i, int j) {
			if(this != NULL_STATE) {
				this.next[NORTH] = q[i-1][j];
				if(this.next[NORTH] == NULL_STATE)
					value[NORTH] = Double.NEGATIVE_INFINITY;

				this.next[SOUTH] = q[i+1][j];
				if(this.next[SOUTH] == NULL_STATE)
					value[SOUTH] = Double.NEGATIVE_INFINITY;

				this.next[EAST] = q[i][j+1];
				if(this.next[EAST] == NULL_STATE)
					value[EAST] = Double.NEGATIVE_INFINITY;

				this.next[WEST] = q[i][j-1];
				if(this.next[WEST] == NULL_STATE)
					value[WEST] = Double.NEGATIVE_INFINITY;
			}
		}

		public int chooseBestAction() {
			int max_index = 0;
			for(int i=1; i<4; ++i) {
				max_index = (value[i] > value[max_index] ? i : max_index);
			}
			return max_index;
		}

		public int chooseNextAction() {	
			int dir;
			while (true) {
				dir =  randGen.nextInt( Integer.MAX_VALUE ) % 4;

				if (this.next[dir] != NULL_STATE)
					break;
			}
			return dir;
		}

		public double getNextMaxValue() {
			double max  = 0;
			if(this != NULL_STATE) {
				max = this.value[0];
				for (int i =1; i<4; ++i) {
					if (max < this.value[i]) {
						max = this.value[i];
					}
				}
			}
			return max;
		}

		public double getReward() {
			if(this == NULL_STATE)
				return 0;
			return reward;
		}

		public State updateQ(double alpha, double gamma) {
			int dir = this.chooseNextAction();
			State nextState = this.next[dir];
			double max = 0.8 * nextState.getNextMaxValue();
			double r = 0.8 * nextState.getReward();

			if( this.next[(dir + 1)%4] != NULL_STATE) {
				max +=  0.1 * this.next[(dir + 1)%4].getNextMaxValue();
				r +=  0.1 * this.next[(dir + 1)%4].getReward();
			} else {
				max +=  0.1 * this.getNextMaxValue();
				r +=  0.1 * this.getReward();
			}

			if(this.next[(dir == 0 ? 3 : dir-1)] != NULL_STATE) {
				max += 0.1 * this.next[(dir == 0 ? 3 : dir-1)].getNextMaxValue();
				r += 0.1 * this.next[(dir == 0 ? 3 : dir-1)].getReward();
			} else {
				max +=  0.1 * this.getNextMaxValue();
				r +=  0.1 * this.getReward();
			}

			this.value[dir]  = (1-alpha) * this.value[dir] + alpha * (r + gamma * max - this.value[dir]);

			return nextState;
		}
	}

	public static void main(String args[]) {
		QMachine machine = new QMachine(0.8, 0.9);
		QParser parser = new QParser(args[0]);
		parser.parseMap();

		machine.setBoard(parser.getMap());
		machine.setDefaultReward(parser.getDefaultReward());

		machine.QLearn(Integer.parseInt(args[1]));
		machine.displayAction();
	}
}