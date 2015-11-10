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
					switch(board[i][j].actionToDo()) {
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
				this.next[SOUTH] = q[i+1][j];
				this.next[EAST] = q[i][j+1];
				this.next[WEST] = q[i][j-1];
			}
		}

		public int chooseBestAction() {
			int max_index = 0;
			for(int i=1; i<4; ++i) {
				max_index = (value[i] > value[max_index] ? i : max_index);
			}
			return max_index;
		}

		public int actionToDo() {
			int max_index = -1;
			for(int i=0; i<4; ++i) { 
				if(next[i] != NULL_STATE) {
					if(max_index == -1)
						max_index = i;
					else	
						max_index = (value[i] > value[max_index]) ? i : max_index;
				}
			}
			return max_index;
		}

		public int chooseNextAction() {	
			if(Math.random()  < 0.75) 
				return  chooseBestAction();

			return randGen.nextInt( Integer.MAX_VALUE ) % 4;	
		}

		public State getNextState(int dir) {
			State nextState;
			double r = Math.random();
			if( r < 0.8 )
				nextState = next[dir];
			else if( r < 0.9 )
				nextState = next[(dir == 0 ? 3 : dir - 1)];
			else
				nextState = next[(dir + 1) % 4];

			if( nextState == NULL_STATE )
				nextState = this;

			return nextState;
		}


		public double getNextMaxValue() {
			double max  = 0;
			max = this.value[0];
			for (int i =1; i<4; ++i) {
				if (max < this.value[i]) {
					max = this.value[i];
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
			State nextState = this.getNextState(dir);
			double max = nextState.getNextMaxValue();
			double r = nextState.getReward();

			this.value[dir]  = (1-alpha) * this.value[dir] + alpha * (r + gamma * max - this.value[dir]);

			return nextState;
		}
	}

	public static void main(String args[]) {
		QMachine machine = new QMachine(0.9, 0.25);
		QParser parser = new QParser(args[0]);
		parser.parseMap();

		machine.setBoard(parser.getMap());
		machine.setDefaultReward(parser.getDefaultReward());

		machine.QLearn(Integer.parseInt(args[1]));
		machine.displayAction();
	}
}
