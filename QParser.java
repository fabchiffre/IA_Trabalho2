import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class QParser {

	private String fileName;
	private QMachine.State map[][];
	private Double defaultReward;

	public QParser(String fileName) {
		this.fileName = fileName;
	}

	public void parseMap() {
		FileInputStream fis = null;
		Scanner sc = null;
		String line;
		int l, c;

		try {
			fis = new FileInputStream(new File(fileName));
			sc = new Scanner(fis);

			line = sc.nextLine();
			line = line.substring(2);
			l = Integer.parseInt(line);

			line = sc.nextLine();
			line = line.substring(2);
			c = Integer.parseInt(line);

			line = sc.nextLine();
			line = line.substring(2);
			defaultReward = Double.parseDouble(line);

			initMap(l+2, c+2);

			for(int i=0; i<l; ++i){
				line = sc.nextLine();
			}

			for(int i=0; i<l; ++i){
				line = sc.nextLine();
				StringTokenizer strToken = new StringTokenizer(line);
				for(int j=0; j<c; ++j) {
					String token = strToken.nextToken();
					if(token.equals("D")) 
						map[i+1][j+1] = new QMachine.State(defaultReward);
					else if(token.equals("X"))
						map[i+1][j+1] = QMachine.NULL_STATE;
					else
						map[i+1][j+1] = new QMachine.State(Double.parseDouble(token));
				}
			}

			for(int i=0; i<l+2; ++i){
				for(int j=0; j<c+2; ++j) {
					map[i][j].initNext(map, i, j);
				}
			}

		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			if(sc != null)
				sc.close();
			try {
				if (fis != null)
					fis.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void initMap(int l, int c) {
		map = new QMachine.State[l][c];
		for(int i=0; i<c; ++i) {
			map[0][i] = QMachine.NULL_STATE;
			map[l-1][i] = QMachine.NULL_STATE;
		}

		for(int i=0; i<l; ++i) {
			map[i][0] = QMachine.NULL_STATE;
			map[i][c-1] = QMachine.NULL_STATE;
		}
	}

	public void displayMap() {
		for(int i=0; i<map.length; ++i) {
			for(int j=0; j<map[0].length; ++j) {
				if(map[i][j] == QMachine.NULL_STATE)
					System.out.print("X ");
				else
					System.out.print(map[i][j].reward + " " );
			}
			System.out.print("\n");
		}
	}

	public double getDefaultReward() {
		return defaultReward;
	}

	public QMachine.State[][] getMap() {
		return map;
	}

	public static void main(String args[]) {
		QParser parser = new QParser(args[0]);
		parser.parseMap();
		QMachine.State[][] map = parser.getMap();

		parser.displayMap();
	}
 }