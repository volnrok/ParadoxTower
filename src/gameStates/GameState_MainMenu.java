package gameStates;

import game.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.awt.geom.*;

public class GameState_MainMenu extends GameState
{
 private ArrayList<String> menuOptions = new ArrayList<String>();
 private int selected = 0;
 private Font font;
 
 public GameState_MainMenu(Game newGame)
 {
  super (newGame);
  
  font = new Font("Consolas", Font.PLAIN, 48);
  
  menuOptions.add("Play Game");
  menuOptions.add("Quit");
  
  //game.audio.loadFile("mp3/Darkness.mp3");
 }
 
 public void Update()
 {
  //game.audio.Update();
  
  InputHandler input = game.GetInput();
  
  if (input.IsKeyHit(KeyEvent.VK_ESCAPE))
  {
   Kill();
  }
  
  if (input.IsKeyHit(KeyEvent.VK_W) || input.IsKeyHit(KeyEvent.VK_UP))
  {
   if (selected > 0)
   {
    selected -= 1;
   }
  }
  
  if (input.IsKeyHit(KeyEvent.VK_S) || input.IsKeyHit(KeyEvent.VK_DOWN))
  {
   if (selected < menuOptions.size() - 1)
   {
    selected += 1;
   }
  }
  
  if (input.IsKeyHit(KeyEvent.VK_ENTER) || input.IsKeyHit(KeyEvent.VK_SPACE))
  {
   switch (selected)
   {
   case 0:
    game.AddGameState(new GameState_Test(game));
    break;
   case 1:
    Kill();
    break;
   }
  }
 }
 
 public void Draw(Graphics g)
 {
  g.setFont(font);
  
  for (int i = 0; i < menuOptions.size(); i++)
  {
   Rectangle2D rect = font.getStringBounds(menuOptions.get(i), new FontRenderContext(new AffineTransform(), false, false));
   
   int width = (int)rect.getWidth();
   int height = (int)rect.getHeight();
   
   if (i == selected)
   {
    g.setColor(Color.YELLOW);
   }
   else
   {
    g.setColor(Color.WHITE);
   }
   
   g.drawString(menuOptions.get(i), (game.getWidth() - width) / 2, 200 + i * 100);
  }
 }
}
