package scripts;

import java.awt.*;
import java.awt.image.BufferedImage;

import game.*;
import gameObjects.*;
import util.*;
import java.util.*;
import java.io.*;
import ui.*;

public class ScriptHandler
{
 private Player player;
 private Level level;
 private TextDisplayer textDisplayer;
 private Camera camera;
 private long timer = 0;
 private Queue<String> lines = new LinkedList<String>();
 
 public ScriptHandler(Player newPlayer, Level newLevel, TextDisplayer newTextDisplayer, Camera newCamera)
 {
  textDisplayer = newTextDisplayer;
  player = newPlayer;
  level = newLevel;
  camera = newCamera;
 }
 
 public void RunScript(String scriptFile)
 {
  try
  {
   BufferedReader input =  new BufferedReader(new FileReader(new File(scriptFile)));
   
   while (input.ready())
   {
    String str = "";
    
    while (str.equals("") && input.ready())
    {
     str = input.readLine();
    }
    
    if (!str.equals(""))
    {
     lines.add(str);
    }
   }
   
   input.close();
  }
  catch (Exception e)
  {
   System.out.println("Failed to load script: " + scriptFile);
  }
 }
 
 public void AddCommand(String command)
 {
  lines.add(command);
 }
 
 public void RunCommand(String command) throws Exception
 {
  Scanner s = new Scanner(command);
  
  String function = s.next();
  
  if (function.equals("pause"))
  {
   int amount = Integer.parseInt(s.next());
   timer = System.currentTimeMillis() + amount;
  }
  else if (function.equals("println"))
  {
   String rest = "";
   
   while (s.hasNext())
   {
    rest += s.next() + " ";
   }
   
   System.out.println(rest);
  }
  else if (function.equals("disable-player"))
  {
   player.getInput = false;
   player.moveTowards = player.Position().Copy();
  }
  else if (function.equals("enable-player"))
  {
   player.getInput = true;
  }
  else if (function.equals("move-player"))
  {
   int x = GetIntVariable(s.next());
   int y = GetIntVariable(s.next());
   
   player.moveTowards = (level.GridToScreen(new GridCoordinate(x, y))).Plus(new Vector2(level.TILE_SIZE, level.TILE_SIZE).Times(0.5f));
  }
  else if (function.equals("say"))
  {
   String rest = "";
   
   while (s.hasNext())
   {
    rest += s.next() + " ";
   }
   
            textDisplayer.Say(rest);
        }
        else if (function.equals("character-say"))
        {
            String charMood = s.next();

            BufferedImage character = DialoguePortraits.lookup(charMood);

            String rest = "";

            while (s.hasNext())
            {
                rest += s.next() + " ";
            }

            textDisplayer.Say(rest, character);
        }
        else if (function.equals("load-level"))
        {
            String levelName = s.next();
            level.Load(levelName, player);
        }
        else if (function.equals("player-add-key"))
        {
            int r = Integer.parseInt(s.next());
            int g = Integer.parseInt(s.next());
   int b = Integer.parseInt(s.next());
   
   player.AddKey(new Color(r, g, b));
  }
        else if (function.equals("player-remove-key"))
        {
         int r = Integer.parseInt(s.next());
            int g = Integer.parseInt(s.next());
   int b = Integer.parseInt(s.next());
   
   LinkedList<Color> keys = player.GetKeys();
   
   ListIterator<Color> i = keys.listIterator();
   
   while (i.hasNext())
   {
    Color current = i.next();
    
    if (current.equals(new Color(r, g, b)))
    {
     i.remove();
    }
   }
        }
        else if(function.equals("player-save-keys")) {
         player.saveKeys();
        }
        else if(function.equals("player-load-keys")) {
         player.loadKeys();
        }
        else if(function.equals("player-no-keys")) {
         player.noKeys();
        }
        else if(function.equals("player-face")) {
         int d = Integer.parseInt(s.next());
         player.faceDir(d);
        }
        else if(function.equals("player-stop")) {
         player.stopMoving();
        }
  else if (function.equals("camera-moveto"))
  {
   float x = GetFloatVariable(s.next());
   float y = GetFloatVariable(s.next());
   
   camera.SetTranslation(new Vector2(x, y));
  }
  else if (function.equals("camera-aim-at"))
  {
   int x = GetIntVariable(s.next());
   int y = GetIntVariable(s.next());
   
   camera.AimAtPoint(level.CentreGridToScreen(new GridCoordinate(x, y)));
  }
  else if (function.equals("camera-go-back"))
  {
   camera.AimAtNonStaticTarget();
  }
  else if(function.equals("camera-fade")) {
   int a = GetIntVariable(s.next());
   int r = GetIntVariable(s.next());
   int g = GetIntVariable(s.next());
   int b = GetIntVariable(s.next());
   int t = GetIntVariable(s.next());
   
   camera.fade(a, r, g, b, t);
  }
  else if(function.equals("camera-fade-immediate")) {
   int a = GetIntVariable(s.next());
   int r = GetIntVariable(s.next());
   int g = GetIntVariable(s.next());
   int b = GetIntVariable(s.next());
   
   camera.fadeImmediate(a, r, g, b);
  }
  else if(function.equals("camera-unfade")) {
   int t = GetIntVariable(s.next());
   camera.unfade(t);
  }
  else if (function.equals("load-next-level"))
  {
   level.Load(level.GetNextLevel(), player);
  }
  else if (function.equals("disable-all-robots"))
  {
   ArrayList<RobotInfo> robots = level.robotAI.robots;
   for (int i = 0; i < robots.size(); ++i){
    robots.get(i).robot.Freeze();
   }
  }
  else if (function.equals("enable-all-robots"))
  {
   ArrayList<RobotInfo> robots = level.robotAI.robots;
   for (int i = 0; i < robots.size(); ++i){
    robots.get(i).robot.Unfreeze();
   }
  }
  else if (function.equals("robot-move"))
  {
   String identifier = s.next();
   int x = Integer.parseInt(s.next());
   int y = Integer.parseInt(s.next());
   ArrayList<RobotInfo> robots = level.robotAI.robots;
   for (int i = 0; i < robots.size(); ++i){
    if (robots.get(i).identifier.equals(identifier)){
     robots.get(i).robot.Unfreeze();
     robots.get(i).ChaseTarget(new GridCoordinate(x, y));
     break;
    }
   }
  }
  else if (function.equals("robots-make-puppets"))
  {
   level.updateRobotAI = false;
  }
  else if (function.equals("robots-unmake-puppets"))
  {
   level.updateRobotAI = true;
  }
  else if(function.equals("disable-robot-collisions")) {
   player.setRobotCollisions(false);
  }
  else if(function.equals("enable-robot-collisions")) {
   player.setRobotCollisions(true);
  }
  else if (function.equals("open-door"))
  {
   int x = Integer.parseInt(s.next());
   int y = Integer.parseInt(s.next());
   
   LinkedList<GameObject> objects = level.objectsAt(new GridCoordinate(x, y));
   
   ListIterator<GameObject> i = objects.listIterator();
   
   while (i.hasNext())
   {
    GameObject current = i.next();
    
    if (current instanceof Door)
    {
     ((Door)current).Open();
    }
   }
  }
  else if (function.equals("close-door"))
  {
   int x = Integer.parseInt(s.next());
   int y = Integer.parseInt(s.next());
   
   LinkedList<GameObject> objects = level.objectsAt(new GridCoordinate(x, y));
   
   ListIterator<GameObject> i = objects.listIterator();
   
   while (i.hasNext())
   {
    GameObject current = i.next();
    
    if (current instanceof Door)
    {
     ((Door)current).Close();
    }
   }
  }
  else if (function.equals("be-the-scientist")) {
   player.setMode(true);
  } else if (function.equals("be-the-mutant")) {
   player.setMode(false);
  }
  else if(function.equals("level-save-progress")) {
   level.saveProgress();
  } else if(function.equals("level-restore-progress")) {
   level.restoreProgress();
  }
 }
 
 public float GetFloatVariable(String s)
 {
  if (s.startsWith("%"))
  {
   if (s.equals("%Player.Position.X"))
   {
    return player.Position().X;
   }
   else if (s.equals("%Player.Position.Y"))
   {
    return player.Position().Y;
   }
  }
  
  return Float.parseFloat(s);
 }
 
 public int GetIntVariable(String s)
 {
  if (s.startsWith("%"))
  {
   if (s.equals("%Player.Location.X"))
   {
    return player.Location().X;
   }
   else if (s.equals("%Player.Location.Y"))
   {
    return player.Location().Y;
   }
  }
  
  return Integer.parseInt(s);
 }
 
 public void RunNextCommand()
 {
  String line = lines.remove();
  
  try
  {
   RunCommand(line);
  }
  catch (Exception e)
  {
   System.out.println("Script command failed: " + line);
  }
 }
 
 public void Update()
 {
  long time = System.currentTimeMillis();
  
  while (time > timer && !lines.isEmpty() && textDisplayer.IsTextCleared())
  {
   RunNextCommand();
  }
 }
}
