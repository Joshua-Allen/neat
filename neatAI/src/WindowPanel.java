import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;


@SuppressWarnings("all")
public class WindowPanel extends JPanel{
	
	//
	JFrame frame;
	
	//
	int screen_scale = 2;
	
	//
	BufferedImage main_image = null;
	BufferedImage simple_image_tile = null;
	BufferedImage tile_image = null;
	BufferedImage network_image = null;
	
	//
	Tile[][] tiles = new Tile[25][17];
	Sprite[] sprites = new Sprite[40];
	
	//
	Map[][] screenInfo = new Map[160][144];
	
	//
	int[] colors = new int[4];
	
	//
	byte[] background_colors = {0,1,2,3};
	byte[] sprite_colors0 = {0,1,2,3};
	byte[] sprite_colors1 = {0,1,2,3};
	
	public WindowPanel()
	{
		frame = new JFrame("AI");
		frame.add(this);
		frame.setSize(337, 330);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		main_image = new BufferedImage(160, 144, BufferedImage.TYPE_INT_ARGB);
		simple_image_tile = new BufferedImage(160, 144, BufferedImage.TYPE_INT_ARGB);
		tile_image = new BufferedImage(128, 192, BufferedImage.TYPE_INT_ARGB);
		network_image = new BufferedImage(5000, 640, BufferedImage.TYPE_INT_ARGB);
		
		colors[0] = Color.WHITE.getRGB();
		colors[1] = Color.LIGHT_GRAY.getRGB();
		colors[2] = Color.DARK_GRAY.getRGB();
		colors[3] = Color.BLACK.getRGB();
		
		//
		int index = 0;
		for(int y=0; y<25; y++)
		{
			for(int x=0; x<17; x++)
			{
				
				tiles[y][x] = new Tile(index);
				index++;
			}
			index--;
		}
		
		//
		for(int i=0; i<40; i++)
		{
			sprites[i] = new Sprite();
		}
		
		//
		for(int y=0; y<144; y++)
		{
			for(int x=0; x<160; x++)
			{
				
				screenInfo[x][y] = new HashMap<String, String>();
			}
		}
	}
	
	
    public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(simple_image_tile, 0, 0, 160*screen_scale, 144*screen_scale, null);
		g2d.drawImage(main_image, 0, 144*screen_scale + 5, 160, 144, null);
		g2d.drawImage(tile_image, 160+5, 144*screen_scale + 5, 128, 192, null);
		g2d.drawImage(network_image, 0, 0, 5000, 640, null);
    }
    
    
    void setSimpleImage(byte[] bytes)
    {
    	Graphics2D g2d = main_image.createGraphics();
    	Graphics2D g2d_tile = simple_image_tile.createGraphics();
    	
    	g2d.setColor(Color.WHITE);
    	g2d.fillRect(0, 0, 160, 144);
    	g2d.setColor(Color.BLACK);
    	
    	g2d_tile.setColor(Color.WHITE);
    	g2d_tile.fillRect(0, 0, 160, 144);
    	g2d_tile.setColor(Color.BLACK);
    	
    	int index = 0;
    	while(index  <bytes.length)
    	{
    		byte type   = bytes[index++];
    		
    		// background
    		if (type == 1)
    		{
    			int tileX = bytes[index++] % 0xFF;
    			int tileY = bytes[index++] % 0xFF;
        		byte x      = bytes[index++];
        		byte y      = bytes[index++];
    			int screenX = bytes[index++] % 0xFF;
    			int screenY = bytes[index++] % 0xFF;

    			int tx = ((int)x % 0xFF)*8;
    			int ty = ((int)y % 0xFF)*8;
    			
    			g2d.drawRect(tx-screenX, ty-screenY, 8, 8);
    			//g2d.setColor(Color.RED);
	    		g2d.drawRect(tx-screenX-256, ty-screenY,     8, 8);//left
	    		g2d.drawRect(tx-screenX-256, ty-screenY+256, 8, 8);
	    		g2d.drawRect(tx-screenX    , ty-screenY+256, 8, 8);//up
	    		g2d.drawRect(tx-screenX+256, ty-screenY+256, 8, 8);
	    		g2d.drawRect(tx-screenX+256, ty-screenY,     8, 8);//right
	    		g2d.drawRect(tx-screenX+256, ty-screenY-256, 8, 8);
	    		g2d.drawRect(tx-screenX    , ty-screenY-256, 8, 8);//down
	    		g2d.drawRect(tx-screenX-256, ty-screenY-256, 8, 8);
	    		//g2d.setColor(Color.BLACK);

	    		//
	    		if (tileY < 0 || tileX >15) continue;
	    		
	    		Tile tile = tiles[tileY][tileX];
	    		drawTile(g2d_tile, 0, tx-screenX, ty-screenY, tile, tile.tile_back);
	    		
	    		drawTile(g2d_tile, 0, tx-screenX-256, ty-screenY,     tile, tile.tile_back);//left
	    		drawTile(g2d_tile, 0, tx-screenX-256, ty-screenY+256, tile, tile.tile_back);
	    		drawTile(g2d_tile, 0, tx-screenX    , ty-screenY+256, tile, tile.tile_back);//up
	    		drawTile(g2d_tile, 0, tx-screenX+256, ty-screenY+256, tile, tile.tile_back);
	    		drawTile(g2d_tile, 0, tx-screenX+256, ty-screenY,     tile, tile.tile_back);//right
	    		drawTile(g2d_tile, 0, tx-screenX+256, ty-screenY-256, tile, tile.tile_back);
	    		drawTile(g2d_tile, 0, tx-screenX    , ty-screenY-256, tile, tile.tile_back);//down
	    		drawTile(g2d_tile, 0, tx-screenX-256, ty-screenY-256, tile, tile.tile_back);
    		}
    		
    		// window
    		if (type == 2)
    		{
    			byte tileX = bytes[index++];
    			byte tileY = bytes[index++];
        		byte x      = bytes[index++];
        		byte y      = bytes[index++];
    			byte windowX = bytes[index++];
    			byte windowY = bytes[index++];
    			
    			int tx = x*8+5;//((int)x % 0xFF)*8;
    			int ty = y*8;//((int)y % 0xFF)*8;
    			
    			g2d.setColor(Color.GREEN);
    			//g2d.drawRect(tx-windowX, ty-windowY, 8, 8);
	    		g2d.setColor(Color.BLACK);

	    		//drawTile(g2d_tile, 1, tx-windowX, ty-windowY, tiles[tileY][tileX], tiles[tileY][tileX].tile_back);
    		}
    		//index++;
    		// sprite
    		if (type == 3)
    		{
				byte flags  = bytes[index++];
    			byte tileX = bytes[index++];
    			byte tileY = bytes[index++];
				int x      = (bytes[index++]+1) & 0xFF;
				int y      = (bytes[index++] & 0xFF);
        		byte height = bytes[index++];
        		x--;
        		
        		g2d.setColor(Color.RED);
        		g2d.drawRect(x, y, 8, height);
        		g2d.setColor(Color.BLACK);
        		
        		boolean priority =			(flags & 0x80) == 0x80;
        		boolean yFlip =     		(flags & 0x40) == 0x40;
        		boolean xFlip =       		(flags & 0x20) == 0x20;
        		boolean palette_number =	(flags & 0x10) == 0x10; // gameboy only
        		boolean VRAM_Bank =       	(flags & 0x08) == 0x08; // gameboy color only
        		byte palette_number_gbc =   (byte) (flags & 0x07);  // gameboy color only
        		
        		int yStart = y;
        		int sheight = 8;
        		
        		int topX = tileX;
        		int topY = tileY;
        		int bottomX = tileX+1;
        		int bottomY = tileY;

        		if (yFlip)
        		{
        			if (height != 8)
        			{
        				topX = tileX+1;
        				bottomX = tileX;
        			}
        			yStart+=8;
        			sheight = -8;
        		}
        		
        		int xStart = x;
        		int sWidth = 8;
        		if (xFlip)
        		{
        			xStart+=8;
        			sWidth = -8;	
        		}
        		
        		Tile tileT = tiles[topY][topX];
        		Tile tileB = tiles[bottomY][bottomX];
        		
        		BufferedImage spr_tile_top = tileT.tile_spr0;
        		BufferedImage spr_tile_bottom = tileB.tile_spr0;
        		if (palette_number == true)
        		{
        			spr_tile_top = tileT.tile_spr1;
        			spr_tile_bottom = tileB.tile_spr1;
        		} 
        		
        		drawTile(g2d_tile, 2, xStart, yStart, sWidth, sheight,  tileT, spr_tile_top);
        		if (height != 8)
        			drawTile(g2d_tile, 2, xStart, yStart+8, sWidth, sheight,  tileB, spr_tile_bottom);
        		
    		}
    	}
    	
    	frame.repaint();
    }

    
    void updateTiles(byte[] bytes)
    {
    	int index = 0;
    	while(index+18 <= bytes.length)
    	{
    		// make it so the the first tile
    		int val = 0;
    		for(int i = 0; i<18; i++) 
    		{
    			int b = bytes[index+i] & 0xFF;
    			val+=b;
    		}
    		if (val == 0)  {index+=18; continue;}
    		
    		// 
    		int tileX = bytes[index++] & 0xFF;
    		int tileY = bytes[index++] & 0xFF;
    		int tileNum = tileX+tileY*16;

    		for(int row = 0; row<8; row++)
    		{
    			int pixel_row_left = bytes[index++] & 0xFF;
    			int pixel_row_right = bytes[index++] & 0xFF;
    			
    			byte pixel0 = (byte) ((pixel_row_left >> 6) & 0x3);
    			byte pixel1 = (byte) ((pixel_row_left >> 4) & 0x3);
    			byte pixel2 = (byte) ((pixel_row_left >> 2) & 0x3);
    			byte pixel3 = (byte) ((pixel_row_left >> 0) & 0x3);
    			
    			byte pixel4 = (byte) ((pixel_row_right >> 6) & 0x3);
    			byte pixel5 = (byte) ((pixel_row_right >> 4) & 0x3);
    			byte pixel6 = (byte) ((pixel_row_right >> 2) & 0x3);
    			byte pixel7 = (byte) ((pixel_row_right >> 0) & 0x3);
    			
    			tiles[tileY][tileX].setPixel(0, row, pixel0);
    			tiles[tileY][tileX].setPixel(1, row, pixel1);
    			tiles[tileY][tileX].setPixel(2, row, pixel2);
    			tiles[tileY][tileX].setPixel(3, row, pixel3);
    			tiles[tileY][tileX].setPixel(4, row, pixel4);
    			tiles[tileY][tileX].setPixel(5, row, pixel5);
    			tiles[tileY][tileX].setPixel(6, row, pixel6);
    			tiles[tileY][tileX].setPixel(7, row, pixel7);
    		}
    	}
    	drawTiles();
    	frame.repaint();
    }
  
    
    void drawTile(Graphics2D g2, int type, int x, int y, Tile tile, BufferedImage image)
    {
    	g2.drawImage(image, x, y, null);
    	
    	// loop through each map that are in the area
    	for(int i=0; i<8; i++)
    	{
    		for(int j=0; j<8; j++)
    		{
    			if (x+i >= 160) continue;
    			if (y+j >= 144) continue;
    			if (x+i < 0) continue;
    			if (y+j < 0) continue;
    			
    			// get the current map
    			Map map = screenInfo[x+i][y+j];
    			map.clear();
    			
		    	// set the type
		    	if (type == 0) map.put("type", "background");
		    	if (type == 1) map.put("type", "window");
		    	if (type == 2) map.put("type", "sprite");
		    	
		    	// set the color
		    	map.put("color", Integer.toString(tile.getPixel(i, j)));
		    	
		    	// set the tile number
		    	map.put("tile", Integer.toString(tile.index));
		    	
		    	// the array x of the tile
		    	map.put("x", Integer.toString(x));
		    	
		    	// the array y of the tile
		    	map.put("y", Integer.toString(y));
    		}
    	}
    }
    void drawTile(Graphics2D g2, int type, int x, int y, int width, int height, Tile tile, BufferedImage image)
    {
    	g2.drawImage(image, x, y, width, height, null);
    	
    	// loop through each map that are in the area
    	for(int i=0; i<8; i++)
    	{
    		for(int j=0; j<8; j++)
    		{
    			if (x+i >= 160) continue;
    			if (y+j >= 144) continue;
    			if (x+i < 0) continue;
    			if (y+j < 0) continue;
    			
    			// get the current map
    			Map map = screenInfo[x+i][y+j];
    			map.clear();
    			
		    	// set the type
		    	if (type == 0) map.put("type", "background");
		    	if (type == 1) map.put("type", "window");
		    	if (type == 2) map.put("type", "sprite");
		    	
		    	// set the color
		    	map.put("color", Integer.toString(tile.getPixel(i, j)));
		    	
		    	// set the tile number
		    	map.put("tile", Integer.toString(tile.index));
		    	
		    	// the array x of the tile
		    	map.put("x", Integer.toString(x));
		    	
		    	// the array y of the tile
		    	map.put("y", Integer.toString(y));
    		}
    	}
    }
    
    private void drawTiles()
    {
    	Graphics2D tile2d = (Graphics2D) tile_image.getGraphics();
    	
		for(int y=0; y<24; y++)
		{
			for(int x=0; x<16; x++)
			{
				tile2d.drawImage(tiles[y][x].tile_back, x*8, y*8, 8, 8, null);
			}
		}
    }
    
    
    void setPalettes(byte[] bytes)
    {
    	byte background = bytes[0];
    	byte sprite0 = bytes[1];
    	byte sprite1 = bytes[2];
    	
    	background_colors[3] = (byte) ((background >> 6) & 0x3);
    	background_colors[2] = (byte) ((background >> 4) & 0x3);
    	background_colors[1] = (byte) ((background >> 2) & 0x3);
    	background_colors[0] = (byte) ((background >> 0) & 0x3);
    	
    	sprite_colors0[3] = (byte) ((sprite0 >> 6) & 0x3);
    	sprite_colors0[2] = (byte) ((sprite0 >> 4) & 0x3);
    	sprite_colors0[1] = (byte) ((sprite0 >> 2) & 0x3);
    	sprite_colors0[0] = (byte) ((sprite0 >> 0) & 0x3);
    	
    	sprite_colors1[3] = (byte) ((sprite1 >> 6) & 0x3);
    	sprite_colors1[2] = (byte) ((sprite1 >> 4) & 0x3);
    	sprite_colors1[1] = (byte) ((sprite1 >> 2) & 0x3);
    	sprite_colors1[0] = (byte) ((sprite1 >> 0) & 0x3);
    }
    
    
    Map getPoint(int x, int y)
    {
    	/*
    	 * 
    	 * type - background/sprite/window
    	 * color - 0/1/2/3
    	 * tile - [0 - 424]
    	 * x - 
    	 * y - 
    	 * 
    	 */
    	if (x >= 0 && x < 160 && y >= 0 && y < 144)
    		return screenInfo[x][y];
    	else
    		return null;
    }
    
    
    void draw_ai(Object ai)
    {
    	Graphics2D g2d = network_image.createGraphics();
    	
    	// draw clear alpha black
    	g2d.setColor(new Color(0,0,0,0));
    	g2d.setBackground(new Color(255, 255, 255, 0));
    	g2d.setComposite(AlphaComposite.Clear); 
    	g2d.fillRect(0, 0, 5000, 640); 
    	g2d.setComposite(AlphaComposite.SrcOver);
    	
    	((simple_ai)ai).draw(g2d);
    	/*
    	int startX = 50;
    	int startY = 50;
    	int endX = 500;
    	int endY = 75;
    	
    	// draw clear alpha black
    	g2d.setColor(new Color(0,0,0,0));
    	g2d.setBackground(new Color(255, 255, 255, 0));
    	g2d.setComposite(AlphaComposite.Clear); 
    	g2d.fillRect(0, 0, 5000, 640); 
    	g2d.setComposite(AlphaComposite.SrcOver);
    	
    	// 
    	Map pointInfo = getPoint(startX, startY);
    	
    	// draw the line
    	g2d.setStroke(new BasicStroke(3));
    	g2d.setColor(Color.BLACK);
    	g2d.drawLine(startX*screen_scale, startY*screen_scale, endX, endY);
    	g2d.setStroke(new BasicStroke(1));
    	g2d.setColor(Color.WHITE);
    	g2d.drawLine(startX*screen_scale, startY*screen_scale, endX, endY);
    	
    	// draw the text
    	g2d.setColor(Color.BLACK);
    	g2d.drawString("type - " + (String) pointInfo.get("type"), endX+5, endY);
    	g2d.drawString("color - " + (String) pointInfo.get("color"), endX+5, endY+15);
    	g2d.drawString("tile - " + (String) pointInfo.get("tile"), endX+5, endY+15*2);
    	g2d.drawString("x - " + (String) pointInfo.get("x"), endX+5, endY+15*3);
    	g2d.drawString("y - " + (String) pointInfo.get("y"), endX+5, endY+15*4);*/
    }
    
    class Tile
    {
    	//
    	public byte[][] pixels;
    	public BufferedImage tile_back;
    	public BufferedImage tile_spr0;
    	public BufferedImage tile_spr1;;
    	public int index;
    	
    	//
    	public Tile(int index)
    	{
    		pixels = new byte[8][8];
    		tile_back = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
    		tile_spr0 = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
    		tile_spr1 = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
    		this.index = index;
    	}
    	
    	public void setPixel(int x, int y, byte pixel)
    	{
    		pixels[y][x] = pixel;
    		
    		int color = colors[background_colors[pixel]];
    		tile_back.setRGB(x, y, color);
    		
    		color = colors[sprite_colors0[pixel]];
    		if (pixel == 0) color = (0 << 24);// | color);
    		tile_spr0.setRGB(x, y, color);
    		
    		
    		color = colors[sprite_colors1[pixel]];
    		
    		tile_spr1.setRGB(x, y, color);
    	}
    	
    	public byte getPixel(int x, int y)
    	{
    		return pixels[y][x];
    	}
    }

    class Sprite
    {
    	int x = 0;
    	int y = 0;
    	int tileX = 0;
    	int tileY = 0;
    	int height = 8;
    	int flags = 0;
    	
		boolean priority;
		boolean yFlip;
		boolean xFlip;
		boolean palette_number; // gameboy only
		boolean VRAM_Bank; // gameboy color only
		byte palette_number_gbc;  // gameboy color only
		
		public Sprite() { setFlags(0); }
		public void draw()
		{
	    	Graphics2D g2d = main_image.createGraphics();
	    	Graphics2D g2d_tile = simple_image_tile.createGraphics();
	    	
    		g2d.setColor(Color.RED);
    		g2d.drawRect(x, y, 8, height);
    		g2d.setColor(Color.BLACK);
    		
    		int yStart = y;
    		int sheight = 8;
    		
    		int topX = tileX;
    		int topY = tileY;
    		int bottomX = tileX+1;
    		int bottomY = tileY;

    		if (yFlip)
    		{
    			if (height != 8)
    			{
    				topX = tileX+1;
    				bottomX = tileX;
    			}
    			yStart+=8;
    			sheight = -8;
    		}
    		
    		int xStart = x;
    		int sWidth = 8;
    		if (xFlip)
    		{
    			xStart+=8;
    			sWidth = -8;	
    		}
    		
    		BufferedImage spr_tile_top = tiles[topY][topX].tile_spr0;
    		BufferedImage spr_tile_bottom = tiles[bottomY][bottomX].tile_spr0;
    		if (palette_number == true)
    		{
    			spr_tile_top = tiles[topY][topX].tile_spr1;
    			spr_tile_bottom = tiles[bottomY][bottomX].tile_spr1;
    		}
    		
    		g2d_tile.drawImage(spr_tile_top, xStart, yStart, sWidth, sheight, null);
    		if (height != 8)
    		g2d_tile.drawImage(spr_tile_bottom, xStart, yStart+8, sWidth, sheight, null);
		}
		
		public int getX()     	{ return x; }
		public int getY() 		{ return y; }
		public int getTileX() 	{ return tileX; }
		public int getTileY() 	{ return tileY; }
		public int getFlags() 	{ return flags; }

		public void setX(int x) 		{ this.x = x; }
		public void setY(int y) 		{ this.y = y; }
		public void setTileX(int tileX) 	{ this.tileX = tileX; }
		public void setTileY(int tileY) 	{ this.tileY = tileY; }
		public void setFlags(int flags) {
			this.flags = flags;
			
    		priority =			(flags & 0x80) == 0x80;
    		yFlip =     		(flags & 0x40) == 0x40;
    		xFlip =       		(flags & 0x20) == 0x20;
    		palette_number =	(flags & 0x10) == 0x10; // gameboy only
    		VRAM_Bank =       	(flags & 0x08) == 0x08; // gameboy color only
    		palette_number_gbc =   (byte) (flags & 0x07);  // gameboy color only
		}
    }

    class Background
    {
    	public int[][][] tiles;
    	public int screenX = 0;
    	public int screenY = 0;
    	
    	Graphics2D g2d = main_image.createGraphics();
    	Graphics2D g2d_tile = simple_image_tile.createGraphics();
    	
    	public Background()
    	{
    		for(int y=0; y<25; y++)
    		{
    			for(int x=0; x<17; x++)
    			{
    				tiles[y][x][0] = 0; // tile x
    				tiles[y][x][1] = 0; // tile y
    			}
    		}
    	}
    	
    	public void draw()
    	{
    		for(int y=0; y<25; y++)
    		{
    			for(int x=0; x<17; x++)
    			{
    				tiles[y][x][0] = 0; // tile x
    				tiles[y][x][1] = 0; // tile y
    			}
    		}
    	}
    	
    	private void draw_index(int x, int y)
    	{
			int tx = ((int)x % 0xFF)*8;
			int ty = ((int)y % 0xFF)*8;
			
			g2d.drawRect(tx-screenX, ty-screenY, 8, 8);
			//g2d.setColor(Color.RED);
    		g2d.drawRect(tx-screenX-256, ty-screenY,     8, 8);//left
    		g2d.drawRect(tx-screenX-256, ty-screenY+256, 8, 8);
    		g2d.drawRect(tx-screenX    , ty-screenY+256, 8, 8);//up
    		g2d.drawRect(tx-screenX+256, ty-screenY+256, 8, 8);
    		g2d.drawRect(tx-screenX+256, ty-screenY,     8, 8);//right
    		g2d.drawRect(tx-screenX+256, ty-screenY-256, 8, 8);
    		g2d.drawRect(tx-screenX    , ty-screenY-256, 8, 8);//down
    		g2d.drawRect(tx-screenX-256, ty-screenY-256, 8, 8);
    		//g2d.setColor(Color.BLACK);

    		//
    		/*
    		if (tileY < 0 || tileX >15) continue;
    		g2d_tile.drawImage(tiles[tileY][tileX].tile_back, tx-screenX, ty-screenY, null);
    		
    		g2d_tile.drawImage(tiles[tileY][tileX].tile_back, tx-screenX-256, ty-screenY,     null);//left
    		g2d_tile.drawImage(tiles[tileY][tileX].tile_back, tx-screenX-256, ty-screenY+256, null);
    		g2d_tile.drawImage(tiles[tileY][tileX].tile_back, tx-screenX    , ty-screenY+256, null);//up
    		g2d_tile.drawImage(tiles[tileY][tileX].tile_back, tx-screenX+256, ty-screenY+256, null);
    		g2d_tile.drawImage(tiles[tileY][tileX].tile_back, tx-screenX+256, ty-screenY,     null);//right
    		g2d_tile.drawImage(tiles[tileY][tileX].tile_back, tx-screenX+256, ty-screenY-256, null);
    		g2d_tile.drawImage(tiles[tileY][tileX].tile_back, tx-screenX    , ty-screenY-256, null);//down
    		g2d_tile.drawImage(tiles[tileY][tileX].tile_back, tx-screenX-256, ty-screenY-256, null);    		
    		*/
    	}
    }
    
    class Window
    {
    	public int[][] tiles;
    	public int windowX = 0;
    	public int windowY = 0;
    	
    	public Window()
    	{
    		for(int y=0; y<25; y++)
    		{
    			for(int x=0; x<17; x++)
    			{
    				
    				tiles[y][x] = 0;
    			}
    		}
    	}
    	
    	public void draw()
    	{
    		
    	}
    }
}







