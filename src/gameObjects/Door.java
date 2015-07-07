package gameObjects;

import game.Level;

import java.awt.*;
import util.*;
import java.awt.image.*;

import util.GridCoordinate;

public class Door extends GameObject
{
	Color doorColor;
	boolean isOpen = false;
	boolean alreadyDrawn = false;
	GridCoordinate location2;
	BufferedImage doorImageClosed;
	BufferedImage doorImageOpen;
	boolean isHorizontal = true;
	
	static BufferedImage[] images;
	
	static{
		images = new BufferedImage[5];
		
		images[0] = FileInput.loadImage("img\\doors\\h0.png");
		images[1] = FileInput.loadImage("img\\doors\\h1.png");
		images[2] = FileInput.loadImage("img\\doors\\v0.png");
		images[3] = FileInput.loadImage("img\\doors\\v1.png");
	}
	
	//swaps shades of red for shades of the given color
	public static BufferedImage colorSwap(BufferedImage img, Color c) {
	    
	    BufferedImage img2 = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
	    
	    int alphamask = 0xff000000;
	    int redmask = 0x00ff0000;
	    int nored = 0x0000ffff;
	    
	    for(int x = 0; x < img.getWidth(); x++) {
	        for(int y = 0; y < img.getHeight(); y++) {
	            int pixel = img.getRGB(x, y);
	            
	            if((pixel & nored) == 0) {
	                float val = ((pixel & redmask) >> 16) / 255.0f;
	                
	                img2.setRGB(x, y, (pixel & alphamask) + (((int) (c.getRed() * val)) << 16)
	                                + (((int) (c.getGreen() * val)) << 8) + ((int) (c.getBlue() * val)));
	            } else {
	                img2.setRGB(x, y, pixel);
	            }
	        }
	    }
	    
	    return img2;
	}
	
	public Door(Level level, Color newDoorColor, GridCoordinate location, GridCoordinate location2)
	{
		super(level, location);
		
		doorColor = newDoorColor;
		this.location2 = location2;
		
		int dx = location.X - location2.X;
		int dy = location.Y - location2.Y;
		
		if (dy == 0)
		{
			isHorizontal = true;
			doorImageClosed = colorSwap(images[0], doorColor);
			doorImageOpen = colorSwap(images[1], doorColor);
		}
		else
		{
			isHorizontal = false;
			doorImageClosed = colorSwap(images[2], doorColor);
			doorImageOpen = colorSwap(images[3], doorColor);
		}
	}
	
	public Color GetColor()
	{
		return doorColor;
	}
	
	public void Open()
	{
		isOpen = true;
	}
	
	public void Close()
	{
		isOpen = false;
	}

	@Override
	public void Draw(Graphics g)
	{
		if (!alreadyDrawn)
		{
			if (isHorizontal)
			{
				int x = Math.min(location.X, location2.X);
				
				BufferedImage drawImage;
				
				if (isOpen)
				{
					drawImage = doorImageOpen;
				}
				else
				{
					drawImage = doorImageClosed;
				}
				
				g.drawImage(drawImage, x * level.TILE_SIZE - 3, location.Y * level.TILE_SIZE - level.TILE_SIZE / 2, null);
			}
			else
			{
				int y = Math.min(location.Y, location2.Y);
				
				BufferedImage drawImage;
				
				if (isOpen)
				{
					drawImage = doorImageOpen;
				}
				else
				{
					drawImage = doorImageClosed;
				}
				
				g.drawImage(drawImage, location.X * level.TILE_SIZE, (y - 1) * level.TILE_SIZE, null);
			}
		}
		
		alreadyDrawn = true;
	}

	@Override
	public void Update()
	{
		alreadyDrawn = false;
	}

	@Override
	public boolean Collides()
	{
		return !isOpen;
	}

}
