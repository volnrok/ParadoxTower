package game;

import gameObjects.Player;

import java.awt.*;

public abstract class GameState
{
	protected Game game;
	private boolean isAlive = true;
	
	public GameState(Game newGame)
	{
		game = newGame;
	}
	
	public void Kill()
	{
		isAlive = false;
	}
	
	public boolean IsAlive()
	{
		return isAlive;
	}
	
	public void Update() {}
	
	public void Draw(Graphics g) {}
}
