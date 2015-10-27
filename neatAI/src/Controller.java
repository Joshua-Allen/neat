import java.util.Timer;
import java.util.TimerTask;

public class Controller {
	
	WindowPanel w;
	Network network;
	String data = "";
	long period = 100;
	
	int state = 0;
	public Controller()
	{
		// create a new window to draw stuff on
		w = new WindowPanel();
		
		// create a new network that talks to the emulator
		network = new Network();
		
		//
		simple_ai ai = new simple_ai(this);
		
		//
		
		
		// create a timer that runs ever 10 milliseconds
		network.sendToEmulater("refresh_all_tiles");
		new Timer().schedule(new TimerTask() {
		public void run()  
			{
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
					state = 2;
				} else 
				if (state == 2)
				{
					// get tile
					network.sendToEmulater("get_tiles");
					w.updateTiles(network.getFromEmulater());
					state = 3;
				} else 
				if (state == 3)
				{
					// update the ai
					ai.run();
					state = 0;
				}
				
				//
				//w.draw_ai(ai);
			}
		}, 1, period);
	}
	
	
	
	public static void main(String[] args)
	{     
		new Controller();
	}	
	
	
	
}
