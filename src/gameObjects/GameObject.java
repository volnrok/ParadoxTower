package gameObjects;

import java.awt.*;
import util.*;
import game.*;

public abstract class GameObject {
  
  //directions and the numbers they correspond to
  public static final int DIR_DOWN = 0;
  public static final int DIR_LEFT = 1;
  public static final int DIR_UP = 2;
  public static final int DIR_RIGHT = 3;
  
 protected GridCoordinate location;
 public GridCoordinate Location(){
  return location;
 }
 public void Location(GridCoordinate location){
  this.location = location.Copy();
 }
 
 Level level; //A reference to the level
 public GameObject(){
  Location(new GridCoordinate());
  level = null;
 }
 public GameObject(Level level, GridCoordinate location){
  this.level = level;
  Location(location);
 }
 public void SetLevel(Level newLevel)
 {
  level = newLevel;
 }
 public abstract void Draw(Graphics g);
 public abstract void Update();
 public abstract boolean Collides();
}
