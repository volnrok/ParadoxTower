package gameObjects;


import java.awt.*;
import java.awt.image.*;
import java.util.*;
import util.FileInput;

public class Keys
{
	static BufferedImage key;
	
	static class KeyColorImage
	{
		public BufferedImage image;
		public Color color;
	}
	
	static LinkedList<KeyColorImage> keyImages = new LinkedList<KeyColorImage>();
	
	static{
		key = FileInput.loadImage("img\\doors\\key.png");
	}
	
	public static BufferedImage GetImage(Color c)
	{
		ListIterator<KeyColorImage> i = keyImages.listIterator();
		
		while (i.hasNext())
		{
			KeyColorImage current = i.next();
			if (c.equals(current.color))
			{
				return current.image;
			}
		}
		
		KeyColorImage newKCI = new KeyColorImage();
		newKCI.color = c;
		newKCI.image = Door.colorSwap(key, c);
		keyImages.add(newKCI);
		return newKCI.image;
	}
}
