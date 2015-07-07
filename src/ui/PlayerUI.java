package ui;

import game.*;
import gameObjects.*;
import util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class PlayerUI
{
	Font font;
	Game game;
	
	Player player;
	
	public PlayerUI(Game newGame, Player newPlayer)
	{
		game = newGame;
		player = newPlayer;
		font = new Font("Consolas", Font.PLAIN, 38);
	}
	
	public void Draw(Graphics g)
	{
		LinkedList<Color> keys = player.GetKeys();
		if(!keys.isEmpty()) {
			
			g.setFont(font);
			g.setColor(Color.WHITE);
			g.drawString("Keys: ", 25, game.getHeight() - 25);
			
			{
				ListIterator<Color> i = keys.listIterator();
				int count = 0;
				
				while (i.hasNext())
				{
					Color current = i.next();
					g.setColor(current);
					g.drawImage(Keys.GetImage(current), 135 + count * 34, game.getHeight() - 45, 30, 30, null);
					//g.fillRect(135 + count * 34, game.getHeight() - 45, 20, 20);
					count++;
				}
			}
		}
	}
}
