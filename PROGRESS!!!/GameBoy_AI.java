import java.awt.Graphics2D;
import java.util.ArrayList;

public abstract class GameBoy_AI {
	static Controller controller;
	static Gameboy gameboy;
	
	static boolean inGame;
	static boolean AI_running;
	
	ArrayList<String> tasks = new ArrayList<String>();
	ArrayList<String> send = new ArrayList<String>();

	void GameBoy_AI_run()
	{
		inGame = (gameboy.getScreenType().equals("inGame"));
		
		if(!inGame) {
			menuTasks();
		}

		run();
		
		AI_running = inGame;
		
		runTasks();
		sendTasks();
	}
	
	public int getScore()
	{
		return controller.gameboy.score;
	}
	
	void menuTasks()
	{
		tasks.add("release_down");
		tasks.add("release_up");
		
		switch(gameboy.getScreenType())
		{
		case "title":  case "gameOver": case "pause":
			tasks.add("enter"); break;
		case "levelChoose": case "gameType":
			tasks.add("left"); 
			tasks.add("enter"); 
			break;
		}
	}

	void runTasks()
	{
		while(tasks.size() > 0)
		{
			String task = tasks.remove(0);
			switch(task)
			{
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
	
	void sendTasks() 
	{
		for(String send : send)
		{
			controller.network.sendToEmulater(send);
		}
		
		tasks.clear();
		send.clear();
	}
	
	
	public abstract void addTasks();
	public abstract void draw(Graphics2D g);	
	public abstract void run();
}
