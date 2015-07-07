package gameStates;

import java.awt.*;
import java.awt.event.*;
import game.*;
import gameObjects.*;
import util.*;
import scripts.*;
import ui.*;

public class GameState_Test extends GameState
{
 Player player;
 Level level;
 Camera camera;
 ScriptHandler scriptHandler;
 TextDisplayer textDisplayer;
 PlayerUI playerUI;
 
 public GameState_Test(Game newGame)
 {
  super(newGame);
  
  level = new Level(game);
  player = new Player(level, newGame);
  
  camera = new Camera(game, level);
  camera.SetScale(3);
  
  camera.SetTranslationTarget(player.Position());
  camera.SetTranslation(player.Position());
  textDisplayer = new TextDisplayer(game);
  
  scriptHandler = new ScriptHandler(player, level, textDisplayer, camera);
  level.SetScriptHandler(scriptHandler);
  
  playerUI = new PlayerUI(game, player);
  
  level.Load("IntroLab1", player);
 }
 
 public void Update()
 {
  //game.audio.Update();
  
  InputHandler input = game.GetInput();
  
  if (input.IsKeyHit(KeyEvent.VK_ESCAPE))
  {
   Kill();
  }
  
  if (input.IsKeyHit(KeyEvent.VK_R))
  {
   //scriptHandler.RunScript("scripts/GimmeKeys.txt");
  }
  
  if (player.IsPlayerCaptured())
  {
   //level.Load(level.levelName, player);
   scriptHandler.RunScript("scripts/RestartLevel.txt");
   player.CaptureAcknowledged();
  }
  
  if (player.HasPlayerWon())
  {
   scriptHandler.RunScript("scripts/NextLevel.txt");
   player.WinAcknowledged();
  }
  
  textDisplayer.Update();
  scriptHandler.Update();
  
  camera.Update();
  camera.SetTranslationTarget(player.Position());
  
  level.Update();
 }
 
 public void Draw(Graphics g)
 {
  camera.Apply((Graphics2D)g);
  level.Draw(g);
  
  camera.Reset((Graphics2D)g);
  playerUI.Draw(g);
  camera.applyFade(g);
  textDisplayer.Draw(g);
 }
}
