package gameObjects;

import game.Level;

import java.awt.*;
import java.awt.image.*;

import util.GridCoordinate;

public class CollectibleItem extends GameObject {
	BufferedImage image;
	String script;
	boolean loadFromFile = false;
	boolean collected = false;
	
	public CollectibleItem(Level level, GridCoordinate location, BufferedImage newImage, String newScript, boolean shouldLoadFromFile)
	{
		super(level, location);
		
		image = newImage;
		script = newScript;
		loadFromFile = shouldLoadFromFile;
	}

	@Override
	public void Draw(Graphics g) {
		if (!collected)
		{
			g.drawImage(image, location.X * level.TILE_SIZE, location.Y * level.TILE_SIZE, level.TILE_SIZE, level.TILE_SIZE, null);
		}
	}

	@Override
	public void Update() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean Collides() {
		// TODO Auto-generated method stub
		return false;
	}

	public void RunScript()
	{
		if (!collected)
		{
			if (loadFromFile)
			{
				level.scriptHandler.RunScript(script);
			}
			else
			{
				level.scriptHandler.AddCommand(script);
			}
			
			collected = true;
		}
	}
}
