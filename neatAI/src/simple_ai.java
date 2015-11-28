import java.util.Timer;
import java.util.TimerTask;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class simple_ai {

	Controller con;
	Gameboy gameboy;
	
	//String state = "start";
	ArrayList<String> tasks = new ArrayList<String>();
	ArrayList<String> send = new ArrayList<String>();
	
	String[][] tiles = new String[19][18];
	
	int[][] game = new int[10][18];
	
	Goal curGoal = new Goal();
	
	//keep track of score
	boolean in_game = false;
	ArrayList<Score> scores = new ArrayList<Score>();
	Score current_score = null;
	
	
	public simple_ai(Controller con)
	{
		this.con = con;
		gameboy = con.gameboy;
	}
	
	
	public void run()
	{
		updateTiles();
		//String type = getScreenType();
		
		addTasks();
		runTasks();
		
		//
		for(String send : send)
		{
			con.network.sendToEmulater(send);
		}
		
		tasks.clear();
		send.clear();
	}
	
	
	void updateTiles()
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
	
	
	String getScreenType()
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
	
	void addTasks()
	{
		if (!getScreenType().equals("inGame"))
		{
			menuTasks();
			in_game = false;
			return;
		}
		
		if (in_game == false)
		{
			in_game = true;
			current_score = new Score();
			scores.add(current_score);
		}
		
		current_score.level = gameboy.level;
		current_score.lines = gameboy.lines;
		current_score.score = gameboy.score;
		
		//
		Sprite curSpr = new Sprite();
		
		//
		int[][] tempGame = copyGame();
		placeSprite(tempGame, curSpr.rot0, curSpr.x, curSpr.y);

		findGoal(new Sprite());
		//tasks.add("findGoal");
		//
		boolean cmp_0 = curSpr.compare(curGoal.spr, 0);
		boolean cmp_1 = curSpr.compare(curGoal.spr, 1);
		boolean cmp_2 = curSpr.compare(curGoal.spr, 2);
		boolean cmp_3 = curSpr.compare(curGoal.spr, 3);
		
		
		boolean move = false;
		
		//tasks.add("release_rotate_right");
		
		if (curSpr.compare(curGoal.spr, 0) == false)
		{
			tasks.add("rotate_right");
			move = true;
		}
		
		
		//
		if (curGoal.x == -1) return;
		
		//
		if(curSpr.x > curGoal.x+2)  {tasks.add("left"); move = true;}
		if(curSpr.x < curGoal.x+2)  {tasks.add("right"); move = true;}
			
			tasks.add("release_down");
		//
		if (move == false)
		{
			tasks.add("hold_down");
			tasks.add("release_up");
		} else {
			tasks.add("hold_up");
		}
	}
	
	void runTasks()
	{
		while(tasks.size() > 0)
		{
			String task = tasks.remove(0);
			switch(task)
			{
				case "findGoal": findGoal(new Sprite()); break;
				case "rotate_left": send.add("press_a"); break;
				case "rotate_right": send.add("press_b"); break;
				case "left": send.add("press_left"); break;
				case "right": send.add("press_right"); break;
				case "down": send.add("press_down"); break;
				case "enter": send.add("press_start"); break;

				case "hold_rotate_left": send.add("hold_a"); break;
				case "release_rotate_left": send.add("release_a"); break;

				case "hold_rotate_right": send.add("hold_b"); break;
				case "release_rotate_right": send.add("release_b"); break;
				
				case "hold_left": send.add("hold_left"); break;
				case "release_left": send.add("release_left"); break;
				
				case "hold_right": send.add("hold_right"); break;
				case "release_right": send.add("release_right"); break;
				
				case "hold_down": send.add("hold_down"); break;
				case "release_down": send.add("release_down"); break;
				
				case "hold_up": send.add("hold_up"); break;
				case "release_up": send.add("release_up"); break;
				
			}
		}
	}
	
	void menuTasks()
	{
		tasks.add("release_down");
		tasks.add("release_up");
		
		switch(getScreenType())
		{
		case "title":  case "gameOver": case "pause":
			tasks.add("enter"); break;
		case "levelChoose": case "gameType":
			tasks.add("left"); 
			tasks.add("enter"); 
			break;
		}
	}
	
	//
	void setCurSprite()
	{
		
	}
	
	// test all sprite locations
	void findGoal(Sprite spr)
	{
		//Goal g = new Goal();
		
		//int[][][] rots = null;
		ArrayList<int[][]> list = new ArrayList<int[][]>();
		list.add(spr.rot0);
		list.add(spr.rot1);
		list.add(spr.rot2);
		list.add(spr.rot3);
		
		int heighScore = -500;
		
		// for each sprite rotation
		for(int rot=0; rot<4; rot++)
		{
			int maxX = 10-spr.rot_width[rot];
			// for each possible x
			for(int x=0; x<maxX; x++)
			{
				// copy game
				int spr_y = 0;
				
				// move as far down as possible
				int maxY = 18-spr.rot_height[rot];
				for(int y=spr.y; y<maxY; y++)
				{
					// copy game and add sprite
					int[][] copy = copyGame();
					
					//placeSprite(copy, rots[rot], x, y);
					placeSprite(copy, list.get(rot), x, y);
					if (cheakForOverlap(copy))
					{
						break;//Math.min(spr_y, y);
					} else {
						spr_y = y;
					}
				}
				
				//
				int[][] copy = copyGame();
				placeSprite(copy, list.get(rot), x, spr_y);
				
				int testScore = testNewGame(copy);
				if (testScore > heighScore)
				{
					//g.spr = rots[rot];
					curGoal.spr = list.get(rot);
					curGoal.x = x;
					curGoal.gameGoal = copy;
					
					heighScore = testScore;
				}
				
				// run testNewGame()
			}
		}
		// if score is best, return
		
		//return g;
	}
	
	//
	void placeSprite(int[][] newGame, int[][] spr, int x, int y)
	{
		for(int sx=0; sx<4; sx++)
		{
			for(int sy=0; sy<4; sy++)
			{
				if(spr[sx][sy] != 0)
				{
					if (x+sx < 10)
						newGame[x+sx][y+sy]+=2;
				}
			}
		}
	}
	
	// this will make a new copy of the game 2d array
	int[][] copyGame()
	{
		int[][] newGame = new int[10][18];
		
		for(int x=0; x<10; x++)
		{
			for(int y=0; y<18; y++)
			{
				newGame[x][y] = game[x][y];
			}
		}
		
		return newGame;
	}
	
	// I will place a sprite onto a copy of the game, this will check if that sprite is overlapping a cell
	boolean cheakForOverlap(int[][] test)
	{
		for(int x=0; x<10; x++)
		{
			for(int y=0; y<18; y++)
			{
				if (test[x][y] == 3)
					return true;
			}
		}
		return false;
	}
	
	// returns the score that the game is
	int testNewGame(int[][] newGame)
	{
		int score = 50;

		int number_of_filled_rows = 0;
		int number_of_new_holes = 0;
		int number_of_cells_from_worldBottom_to_spriteTop = 0;
		int number_of_cells_from_worldLeft_to_spriteLeft = -1;
		int number_of_cells_from_worldRight_to_spriteRight = -1;
		
		int percent_of_taken_around_sprite = 0;
		
		
		//////////////////////////////
		// look for filled rows
		//////////////////////////////
		
		// Scene each cell from top to bottom
		for(int y=0; y<18; y++) {
			boolean filled = true;
			// Scene each cell from left to right
			for(int x=0; x<10; x++) {
				// when there is an empty cell we know that the row is not full
				if (newGame[x][y] == 0)  {
					filled = false;
					break;
				}
			}
			if (filled) number_of_filled_rows++;
		}
		
		//////////////////////////////
		// look for new holes
		//////////////////////////////
		
		// Scene each cell from left to right
		for(int x=0; x<10; x++){
			boolean under = false;
			// Scene each cell from top to bottom
			for(int y=0; y<18; y++)
			{
				// if a cell is the sprite 
				if (newGame[x][y] == 2) under = true;
				if (under == true) {
					// if there is a hole it must be a new hole
					if (newGame[x][y] == 0) number_of_new_holes++;
					// if there is a block in a cell anything under must be old
					if (newGame[x][y] == 1) under = false;
				}
			}
		}
		
		//////////////////////////////
		// check the height and check the sides
		//////////////////////////////
		
		boolean foundSprite = false;

		// Scene each cell from bottom to top
		for(int y=17; y>=0; y--) {
			
			int left = 0;
			int right = 0;
			
			boolean Sprite_in_row = false;
			// Scene each cell from left to right
			for(int x=0; x<10; x++){
				if(newGame[x][y] == 2)
				{
					Sprite_in_row = true;
					if (foundSprite == false) {foundSprite = true;}
				} else {
					if (Sprite_in_row) {
						right++;
					} else {
						left++;
					}
				}
			}
			if (Sprite_in_row)
			{
				//number_of_cells_from_worldLeft_to_spriteLeft = left;
				if (number_of_cells_from_worldLeft_to_spriteLeft == -1) {number_of_cells_from_worldLeft_to_spriteLeft = left;}
				if (number_of_cells_from_worldRight_to_spriteRight == -1) number_of_cells_from_worldRight_to_spriteRight = right;
				
				if (number_of_cells_from_worldLeft_to_spriteLeft > left) {number_of_cells_from_worldLeft_to_spriteLeft = left;}
				if (number_of_cells_from_worldRight_to_spriteRight > right) number_of_cells_from_worldRight_to_spriteRight = right;
			}
			
			if (foundSprite == Sprite_in_row)
			{
				number_of_cells_from_worldBottom_to_spriteTop++;
			} else {
				break;
			}
		}
		
		//////////////////////////////
		// percent_of_taken_around_sprite
		//////////////////////////////
		
		// Scene each cell from left to right
		/*for(int x=0; x<10; x++){
			// Scene each cell from top to bottom
			for(int y=0; y<18; y++) {
				
			}
		}*/
		
		
		//////////////////////////////
		// calculate the score
		//////////////////////////////
		
		score += number_of_filled_rows;
		score -= number_of_new_holes*10;
		score -= number_of_cells_from_worldBottom_to_spriteTop*3;
		
		// a slight pull to the sides
		score -= Math.min(number_of_cells_from_worldLeft_to_spriteLeft,number_of_cells_from_worldRight_to_spriteRight);
				
		
		return score;
	}

	// 
	void draw(Graphics2D g)
	{
		//g.setColor(Color.BLACK);
		
		/*
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
		//}
		
		
		
		/*
		// current game view
		for(int x=0; x<10; x++)
		{
			for(int y=0; y<18; y++)
			{
				g.drawString(Integer.toHexString(game[x][y]), 500+x*15, 115+y*15);
			}
		}
		
		// sprite
		Sprite spr = new Sprite();
		for(int x=0; x<4; x++)
		{
			for(int y=0; y<4; y++)
			{
				g.drawString(Integer.toHexString(spr.rot0[x][y]), 500+x*15, 25+y*15);
				g.drawString(Integer.toHexString(spr.rot1[x][y]), 600+x*15, 25+y*15);
				g.drawString(Integer.toHexString(spr.rot2[x][y]), 700+x*15, 25+y*15);
				g.drawString(Integer.toHexString(spr.rot3[x][y]), 800+x*15, 25+y*15);
			}
			
			
		}
		
		//findGoal(spr);
		for(int x=0; x<10; x++)
		{
			for(int y=0; y<18; y++)
			{
				g.drawString(Integer.toHexString(curGoal.gameGoal[x][y]), 800+x*15, 115+y*15);
			}
		}*/
	}

	// lol
	class Sprite{
		int[][] rot0 = new int[4][4];
		int[][] rot1 = new int[4][4];
		int[][] rot2 = new int[4][4];
		int[][] rot3 = new int[4][4];
		
		int[] rot_width = {3,3,3,3};
		int[] rot_height = {3,3,3,3};
		
		int x = 0;
		int y = 0;
		
		Sprite()
		{
			WindowPanel window = con.w;
			
			// find the sprite
			int sp_x = 5000;
			int sp_y = 5000;
			for(int x=0; x<=11; x++)
			{
				for(int y=0; y<=17; y++)
				{
					Map cell = window.getPoint(4+(x)*8, 4+y*8);
					String type = (String) cell.get("type");
					
					if (type.equals("sprite"))
					{
						sp_x = Math.min(sp_x, x);
						sp_y = Math.min(sp_y, y);
					}
				}
			}
			x = sp_x;
			y = sp_y;
			
			//
			setTheSprite();
			
			//
			fixSprites();
			
			//
			setWidth();
			
			//
			setHeight();
			
		}
		
		// returns true if they are the same
		boolean compare(int[][] spr, int index)
		{
			int[][] cmp0 = new int[4][4];
			int[][] cmp1 = new int[4][4];
			
			if (index == 0) {
				cmp0 = rot0; cmp1 = spr;}
			if (index == 1) {
				cmp0 = rot1; cmp1 = spr;}
			if (index == 2) {
				cmp0 = rot2; cmp1 = spr;}
			if (index == 3) {
				cmp0 = rot3; cmp1 = spr;}
			
			for(int x=0; x<4; x++)
			{
				for(int y=0; y<4; y++)
				{
					if (cmp0[x][y] != cmp1[x][y])
						return false;
				}
			}
			
			return true;
		}
		
		//
		void setTheSprite()
		{
			WindowPanel window = con.w;
			// copy the sprite over to rot0
			for(int ax=0; ax<4; ax++)
			{
				for(int ay=0; ay<4; ay++)
				{
					Map cell = window.getPoint(4+(ax+x)*8, 4+(ay+y)*8);
					if(cell != null)
					{
						String type = (String) cell.get("type");
						
						if (type.equals("sprite"))
						{
							rot0[ax][ay] = 1;
							rot1[3-ay][ax] = 1;
							rot2[3-ax][3-ay] = 1;
							rot3[ay][3-ax] = 1;
						}
					}
				}
			}
		}
		
		//
		void fixSprites()
		{
			// repeat shift - fuck it, if it is just a proof of concept why make it good
			for(int r=0; r<4; r++)
			{
				// shift left
				for(int i=0; i<3; i++)// left to right
				{
					int count0 = 0;
					int count1 = 0;
					int count2 = 0;
					int count3 = 0;
					for(int j=0; j<4; j++) // up to down
					{
						count0 += rot0[i][j];
						count1 += rot1[i][j];
						count2 += rot2[i][j];
						count3 += rot3[i][j];
					}				
					for(int j=0; j<4; j++) // up to down - shift
					{
						if (count0 == 0) {rot0[i][j] = rot0[i+1][j]; rot0[i+1][j] = 0;}
						if (count1 == 0) {rot1[i][j] = rot1[i+1][j]; rot1[i+1][j] = 0;}
						if (count2 == 0) {rot2[i][j] = rot2[i+1][j]; rot2[i+1][j] = 0;}
						if (count3 == 0) {rot3[i][j] = rot3[i+1][j]; rot3[i+1][j] = 0;}
					}
				}
				// shift up
				for(int j=0; j<3; j++)// up to down
				{
					int count0 = 0;
					int count1 = 0;
					int count2 = 0;
					int count3 = 0;
					for(int i=0; i<4; i++) // left to right
					{
						count0 += rot0[i][j];
						count1 += rot1[i][j];
						count2 += rot2[i][j];
						count3 += rot3[i][j];
					}				
					for(int i=0; i<4; i++) // left to right - shift
					{
						if (count0 == 0) {rot0[i][j] = rot0[i][j+1]; rot0[i][j+1] = 0;}
						if (count1 == 0) {rot1[i][j] = rot1[i][j+1]; rot1[i][j+1] = 0;}
						if (count2 == 0) {rot2[i][j] = rot2[i][j+1]; rot2[i][j+1] = 0;}
						if (count3 == 0) {rot3[i][j] = rot3[i][j+1]; rot3[i][j+1] = 0;}
					}
				}
			}
		}

		// 
		void setWidth()
		{
			for(int x=0; x<4; x++)// Check left to right
			{
				// count each index
				int count0 = 0;
				int count1 = 0;
				int count2 = 0;
				int count3 = 0;
				for(int y=0; y<4; y++) // check up to down
				{
					count0 += rot0[x][y];
					count1 += rot1[x][y];
					count2 += rot2[x][y];
					count3 += rot3[x][y];
				}
				if (count0 == 0) rot_width[0] = Math.min(rot_width[0], x-1);
				if (count1 == 0) rot_width[1] = Math.min(rot_width[1], x-1);
				if (count2 == 0) rot_width[2] = Math.min(rot_width[2], x-1);
				if (count3 == 0) rot_width[3] = Math.min(rot_width[3], x-1);
			}
		}
		// 
		void setHeight()
		{
			for(int y=0; y<4; y++)
			{
				int count0 = 0;
				int count1 = 0;
				int count2 = 0;
				int count3 = 0;
				for(int x=0; x<4; x++)
				{
					count0 += rot0[x][y];
					count1 += rot1[x][y];
					count2 += rot2[x][y];
					count3 += rot3[x][y];
				}
				if (count0 == 0) rot_height[0] = Math.min(rot_height[0], y-1);
				if (count1 == 0) rot_height[1] = Math.min(rot_height[1], y-1);
				if (count2 == 0) rot_height[2] = Math.min(rot_height[2], y-1);
				if (count3 == 0) rot_height[3] = Math.min(rot_height[3], y-1);
			}
		}
	}
	
	//
	class Goal{
		int[][] spr = new int[4][4];
		int[][] gameGoal = new int[10][18];
		int x = -1;
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