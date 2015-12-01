import java.util.ArrayList;
import java.util.Map;


@SuppressWarnings("all")
public class Gameboy {
	Controller con;
	Network net;
	
	String[][] tiles = new String[19][18];
	int[][] game = new int[10][18];
	
	public int score = 0;
	public int level = 0;
	public int lines = 0;
	
	public Gameboy(Controller con)
	{
		this.con = con;
		net = con.network;
	}
	
	public void update()
	{
		updateTiles();
		
		byte[] receiveData;
		
		// level
		String lvl = "";
		net.sendToEmulater("get_byte;FFA9"); receiveData = net.getFromEmulater();
		lvl += Integer.toString(receiveData[0]);
		if (receiveData.length == 2) lvl += Integer.toString(receiveData[1]);
		level = Integer.parseInt(lvl);
		
		//net.sendToEmulater("get_byte;FFA9");
		//level = net.getFromEmulater()[0] & 0xFF;		
		
		String lns = "";
		net.sendToEmulater("get_byte;FF9E"); receiveData = net.getFromEmulater();
		lns += Integer.toString(receiveData[0]);
		if (receiveData.length == 2) lns += Integer.toString(receiveData[1]);
		lines = Integer.parseInt(lns);
		//net.sendToEmulater("get_byte;FF9E");
		//lines = (net.getFromEmulater()[0]*level) & 0xFF;
		
		/*
		•	Score – if the score is “16547”
		o	0xC0A2 – “01”
		o	0xC0A1 – “65”
		o	0xC0A0 – “47”
		*/
		
		String text_score = "";

		
		net.sendToEmulater("get_byte;C0A2"); receiveData = net.getFromEmulater();
		text_score += Integer.toString(receiveData[0]);
		if (receiveData.length == 2) text_score += Integer.toString(receiveData[1]);
		
		net.sendToEmulater("get_byte;C0A1"); receiveData = net.getFromEmulater();
		text_score += Integer.toString(receiveData[0]);
		if (receiveData.length == 2) text_score += Integer.toString(receiveData[1]);
		
		net.sendToEmulater("get_byte;C0A0"); receiveData = net.getFromEmulater();
		text_score += Integer.toString(receiveData[0]);
		if (receiveData.length == 2) text_score += Integer.toString(receiveData[1]);

		score = Integer.parseInt(text_score);
		
		keepScore();
	}

	public void updateTiles()
	{
		WindowPanel window = con.w;
		for(int x=0; x<19; x++)
		{
			for(int y=0; y<=17; y++)
			{
				Map cell = window.getPoint(4+x*8, 4+y*8);
				String tile = (String) cell.get("tile");
				tiles[x][y] = tile;
				
				if (x>=2 && x<=11)
				{
					String type = (String) cell.get("type");
					if (!type.equals("sprite") && !tile.equals("47"))
					{
						game[x-2][y] = 1;
					} else {
						game[x-2][y] = 0;
					}
				}
			}
		}
		
	}
	
	public String getScreenType()
	{
		if (tiles[2][14].equals("89") && tiles[12][14].equals("153"))
			return "title";
		if (tiles[5][3].equals("16") && tiles[10][3].equals("29"))
			return "gameType";
		if (tiles[7][4].equals("21") && tiles[5][11].equals("29"))
			return "levelChoose";
		
		if (tiles[5][4].equals("16") && tiles[14][1].equals("28"))// game over
			return "gameOver";		
		if (tiles[16][14].equals("47") && tiles[14][1].equals("28"))// pause
			return "pause";
		if (tiles[14][1].equals("28") && tiles[14][9].equals("21"))// in game 
			return "inGame";

		return "unknown";
	}

	
	
	// analytics
	boolean in_game = false;
	ArrayList<Score> scores = new ArrayList<Score>();
	Score current_score = null;
	
	
	public void keepScore()
	{
		if (getScreenType() == "inGame"){
			if (in_game){
				
			} else {
				in_game = true;
				current_score = new Score();
				scores.add(current_score);
			}
		} else {
			if (in_game){
				in_game = false;
			} else {
				
			}
			return;
		}
		
		//
		current_score.level = level;
		current_score.lines = lines;
		current_score.score = score;
		
		//
		Score best = null;//new Score();
		Score worst = null;//new Score(999999);
		Score average = new Score();
		
		for(int i=0; i<scores.size(); i++)
		{
			Score cur = scores.get(i);
			if (cur != current_score)
			{
				if (best == null) best = cur;
				if (worst == null) worst = cur;
				
				if (cur.score < worst.score) worst = cur;
				if (cur.score > best.score)  best = cur;
				
				average.level += cur.level;
				average.lines += cur.lines;
				average.score += cur.score;
			}
		}
		
		if (scores.size() > 1)
		{
			average.level /= scores.size()-1;
			average.lines /= scores.size()-1;
			average.score /= scores.size()-1;
		}
		String text = "";
		if (best != null)
		{
			text += "Best:		Worst:		Average:\n";
			text += "Score:\t"+String.valueOf(best.score)+"\tScore:\t"+String.valueOf(worst.score)+"\tScore:\t"+String.valueOf(average.score)+"\n";
			text += "Level:\t"+String.valueOf(best.level)+"\tLevel:\t"+String.valueOf(worst.level)+"\tLevel:\t"+String.valueOf(average.level)+"\n";
			text += "Lines:\t"+String.valueOf(best.lines)+"\tLines:\t"+String.valueOf(worst.lines)+"\tLines:\t"+String.valueOf(average.lines)+"\n\n\n";
		} else {
			text += "Best:		Worst:		Average:\n";
			text += "Score:\t0\tScore:\t0\tScore:\t0\n";
			text += "Level:\t0\tLevel:\t0\tLevel:\t0\n";
			text += "Lines:\t0\tLines:\t0\tLines:\t0\n\n\n";
		}
		
		for(int i=0; i<scores.size(); i++)
		{
			Score cur = scores.get(i);
			text += "Run: "+ String.valueOf(i+1) + 
					"\tScore: " + String.valueOf(cur.score) + 
					"\tLevel: " + String.valueOf(cur.level) + 
					"\tLines: " + String.valueOf(cur.lines)+"\n";
		}

		con.w.textAreal.setText(text);
	}
	
	class Score{
		public int score = 0;
		public int level = 0;
		public int lines = 0;
		public Score(){};
		public Score(int val)
		{
			score = level = lines = val;
		};
		
	}
}
