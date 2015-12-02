import java.util.Timer;
import java.util.TimerTask;

public class Controller {
	
	WindowPanel w;
	Network network;
	Gameboy gameboy;
	
	String NEAT_SAVE_LOAD_FILE = "C:/Users/mck23/Downloads/neat-master/neat.sav";
	
	String data = "";
	long period = 100;
	
	int state = 0;
	int timer = 0;
	
	GameBoy_AI ai;
	
	public Controller()
	{
		// create a new window to draw stuff on
		w = new WindowPanel();
		w.controller = this;
		// create a new network that talks to the emulator
		network = new Network();
		gameboy = new Gameboy(this);
		
		//
		GameBoy_AI.controller = this;
		GameBoy_AI.gameboy = gameboy;
		
		//
		//simple_ai ai1 = new simple_ai();
		
		ai = new Neat();
		
		//
		
		
		// create a timer that runs ever 10 milliseconds
		network.sendToEmulater("refresh_all_tiles");
		
		
		new Timer().schedule(new TimerTask() {
		public void run()  
			{
			timer++;
			
			network.sendToEmulater("get_palettes");
			w.setPalettes(network.getFromEmulater());
			
			network.sendToEmulater("get_screen_simplified");
			w.setSimpleImage(network.getFromEmulater());
			
			network.sendToEmulater("get_tiles");
			w.updateTiles(network.getFromEmulater());
			
			gameboy.update();
			// update the ai
			ai.GameBoy_AI_run();
			
			if (timer >= 2)
			{
				//ai1.GameBoy_AI_run();
				timer = 0;
			}

			w.draw_ai(ai);
			/*
			if (state == 0)
				{
					// get palettes
					network.sendToEmulater("get_palettes");
					w.setPalettes(network.getFromEmulater());
					state = 1;
				} else 
				if (state == 1)
				{
					// get simple screen
					network.sendToEmulater("get_screen_simplified");
					w.setSimpleImage(network.getFromEmulater());
					gameboy.update();
					ai2.GameBoy_AI_run();
					state = 2;
				} else 
				if (state == 2)
				{
					// get tile
					network.sendToEmulater("get_tiles");
					w.updateTiles(network.getFromEmulater());
					gameboy.update();
					ai2.GameBoy_AI_run();
					state = 3;
				} else 
				if (state == 3)
				{
					gameboy.update();
					// update the ai
					ai2.GameBoy_AI_run();
					ai1.GameBoy_AI_run();

					w.draw_ai(ai2);
					state = 0;
				}
				*/
				//
				
			}
		}, 1, period);
	}
	
	public void saveNeat() {
		if(!ai.getClass().equals(Neat.class)) {
			System.out.println("Cannot Save! AI is not Neat");
			return;
		}
		if(!SavingLoadingFeature.saveNeat(NEAT_SAVE_LOAD_FILE, (Neat)ai))
			System.out.println("Neat did not save"); 
		else
			System.out.println("Neat saved"); 
	}
	
	public void loadNeat() {
		ai = SavingLoadingFeature.loadNeat(NEAT_SAVE_LOAD_FILE);
	}
	
	public void newNeat() {
		ai = new Neat();
	}
	
	public void newSimpleAI() {
		ai = new simple_ai();
	}
	
	public static void main(String[] args)
	{     
		new Controller();
	}	
	
	
	
}
