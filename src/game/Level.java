package game;
/* Loads from specified file name
 * May have background texture
 * Draws & updates all things related to / contained within the level
 */

import gameObjects.*;
import gameObjects.Robot;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

import util.*;
import scripts.*;

import java.util.*;

public class Level {
    public static final int TILE_SIZE = 12; //in pixels
    public static final int WALL_SIZE = 12; //height of the wall in pixels
    public static final int WALL_SHIFT = 3;
    
    public Player player;
    public String levelName;
    public String savedLevel = null;
    public void saveProgress() {savedLevel = levelName;}
    public void restoreProgress() {
     if(savedLevel != null) {
      Load(savedLevel, player);
     }
    }

    //images for stuff
    private static final int FLOOR_PICS = 2; //the number of floor pictures
    private static final int CEIL_PICS = 1; //the number of ceiling pictures
    private static BufferedImage[] pics;

    private static final Color WALL_COLOR = new Color(96, 96, 128);
    private static final Color SIDEWALL_COLOR = new Color(64, 64, 96);

    //list objects
    private LinkedList<GameObject> gameObjects;
    private int[][] wall;
    private LinkedList<GameObject>[][] objectReference;
    public LinkedList<GridCoordinate> endingLocations;
    public boolean updateRobotAI = true;

    public LinkedList<ScriptTrigger> triggers = new LinkedList<ScriptTrigger>();

    LinkedList<RobotInfo> newRobots;

    Game game;
    public ScriptHandler scriptHandler;

    private String nextLevel = null;
    public String GetNextLevel()
    {
        return nextLevel;
    }

    static {
        pics = new BufferedImage[FLOOR_PICS + CEIL_PICS];
        for(int i = 0; i < FLOOR_PICS; i++) {
            pics[i] = FileInput.loadImage("img/tiles/floor" + i + ".png");
        }
        for(int i = 0; i < CEIL_PICS; i++) {
            pics[FLOOR_PICS + i] = FileInput.loadImage("img/tiles/ceil" + i + ".png");
        }
    }
    public void SetScriptHandler(ScriptHandler s)
    {
        scriptHandler = s;
    }
    
    public boolean AddObject(GameObject obj){ //Returns false if addition failed
        if (spaceAvailable(obj.Location())){
            gameObjects.addLast(obj);
            objectReference[obj.Location().X][obj.Location().Y].add(obj);
            return true;
        }
        return false;
    }
    public void RemoveObject(GameObject obj){
        gameObjects.remove(obj);
        objectReference[obj.Location().X][obj.Location().Y].remove(obj);
    }

    private int levelWidth;
    private int levelHeight;
    public int LevelWidth(){
        return levelWidth;
    }
    public int LevelHeight(){
        return levelHeight;
    }

    public RobotAI robotAI;
    public Level(Game newGame)
    {
        game = newGame;
    }
    @SuppressWarnings("unchecked")
    public void Load(String newLevelName, Player p){ //Responsible for giving player it's location
  levelName = newLevelName;
  nextLevel = null;
  player = p;

        gameObjects = new LinkedList<GameObject>();
        endingLocations = new LinkedList<GridCoordinate>();

        BufferedImage level = FileInput.loadImage("img/levels/" + levelName + ".bmp");

        player.SetLevel(this);
        boolean playerAdded = false;
        ArrayList<Robot> debugRobots = new ArrayList<Robot>();
        levelWidth = level.getWidth();
        levelHeight = level.getHeight();
        wall = new int[levelWidth][levelHeight];
        objectReference = new LinkedList[levelWidth][levelHeight];
        triggers = new LinkedList<ScriptTrigger>();
        
        for (int x = 0; x < levelWidth; ++x){
            for (int y = 0; y < levelHeight; ++y){
                Color color = new Color(level.getRGB(x,y));
                if(color.equals(Color.BLACK)) {wall[x][y] = -1;}
                objectReference[x][y] = new LinkedList<GameObject>();
                if (color.equals(new Color(255, 0, 0))){ //Robots are red
                    Robot newRobot = new Robot(this, new GridCoordinate(x, y));
                    debugRobots.add(newRobot); //DEBUG
                    objectReference[x][y].addLast(newRobot);
                    gameObjects.add(newRobot);
                } else if (color.equals(new Color(255, 255, 0)) && !playerAdded){ //Player is yellow
                    player.Location(new GridCoordinate(x, y));
                    objectReference[x][y].add(player);
                    gameObjects.add(player);
                    playerAdded = true;
                } else if (color.equals(new Color(0, 255, 0))){
                    endingLocations.add(new GridCoordinate(x, y));
                    PortalGlow glow = new PortalGlow(new GridCoordinate(x, y));
                    objectReference[x][y].add(glow);
                    gameObjects.add(glow);
                    wall[x][y] = 1;
                }
            }
        }

        robotAI = new RobotAI(this, player);
        LoadConfig(levelName);
    }

    public void LoadConfig(String levelName)
    {
        newRobots = new LinkedList<RobotInfo>();

        try
        {
            LinkedList<String> lines = new LinkedList<String>();

            BufferedReader input =  new BufferedReader(new FileReader(new File("img/levels/" + levelName + ".cfg")));

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

            {
                ListIterator<String> i = lines.listIterator();

                while (i.hasNext())
                {
                    RunConfigCommand(i.next());
                }
            }
        }
        catch(Exception e) {/*Not every level has to have a config, so do nothing on error*/}

        ListIterator<RobotInfo> i = newRobots.listIterator();

        while (i.hasNext())
        {
            robotAI.AddRobot(i.next());
        }
    }

    public void RunConfigCommand(String command)
    {
        Scanner s = new Scanner(command);
        String function = s.next();

        if (function.equals("next-level"))
        {
            nextLevel = s.next();
        }
        else if (function.equals("door-add"))
        {
            int r = Integer.parseInt(s.next());
            int g = Integer.parseInt(s.next());
            int b = Integer.parseInt(s.next());

            int x1 = Integer.parseInt(s.next());
            int y1 = Integer.parseInt(s.next());
            int x2 = Integer.parseInt(s.next());
            int y2 = Integer.parseInt(s.next());

            Door d = new Door(this, new Color(r, g, b), new GridCoordinate(x1, y1), new GridCoordinate(x2, y2));
            gameObjects.addLast(d);
            objectReference[x1][y1].add(d);
            objectReference[x2][y2].add(d);
        }
        else if (function.equals("key-add"))
        {
            int r = Integer.parseInt(s.next());
            int g = Integer.parseInt(s.next());
            int b = Integer.parseInt(s.next());

            int x = Integer.parseInt(s.next());
            int y = Integer.parseInt(s.next());

            Color c = new Color(r, g, b);

            AddObject(new CollectibleItem(this, new GridCoordinate(x, y),
                        Keys.GetImage(c),
                        "player-add-key " + r + " " + g + " " + b,
                        false));
        }
        else if (function.equals("robot-add"))
        {
            String name = s.next();

            int x = Integer.parseInt(s.next());
            int y = Integer.parseInt(s.next());

            Robot r = new Robot(this, new GridCoordinate(x, y));
            AddObject(r);
            RobotInfo newRI = new RobotInfo(name, r);
            newRobots.add(newRI);
        }
        else if (function.equals("robot-add-route"))
        {
            String name = s.next();

            int x = Integer.parseInt(s.next());
            int y = Integer.parseInt(s.next());

            RobotInfo ri = GetNewRobotInfo(name);

            if (ri.defaultRoute == null)
            {
                ri.defaultRoute = new ArrayList<GridCoordinate>();
            }

            ri.defaultRoute.add(new GridCoordinate(x, y));
        }
        else if (function.equals("robot-add-target")){
   RobotInfo ri = GetNewRobotInfo(s.next());
   RobotInfo ri2 = GetNewRobotInfo(s.next());
   if (ri2 != null){
    ri.ChaseTarget(ri2.robot);
   }
  }
        else if (function.equals("run-script"))
        {
            String scriptName = s.next();
            scriptHandler.RunScript(scriptName);
        }
        else if (function.equals("trigger"))
        {
            String scriptName = s.next();
            int x1 = Integer.parseInt(s.next());
            int y1 = Integer.parseInt(s.next());
            int x2 = Integer.parseInt(s.next());
            int y2 = Integer.parseInt(s.next());

            triggers.add(new ScriptTrigger(this, new GridCoordinate(x1, y1), new GridCoordinate(x2, y2), scriptName));
        }
        else if(function.equals("add-scenery")) {
            int x1 = Integer.parseInt(s.next());
            int y1 = Integer.parseInt(s.next());
            int w = Integer.parseInt(s.next());
            int h = Integer.parseInt(s.next());
            String loc = s.next();

            int c1 = Integer.parseInt(s.next());
            boolean c2 = false;
            if(c1 != 0) {c2 = true;}

            SceneryObject obj = new SceneryObject(this, new GridCoordinate(x1, y1), w, h, loc, c2);

            for(int x2 = 0; x2 < w; x2++) {
                for(int y2 = 0; y2 < h; y2++) {
                    objectReference[x1 - x2][y1 - y2].add(obj);
                }
            }
            gameObjects.add(obj);
        }
        else if(function.equals("music"))
        {
         String musicFile = s.next();
         
         //game.audio.loadFile(musicFile);
        }
    }

    public RobotInfo GetNewRobotInfo(String name)
    {
        ListIterator<RobotInfo> i = newRobots.listIterator();

        while (i.hasNext())
        {
            RobotInfo current = i.next();

            if (current.identifier.equals(name))
            {
                return current;
            }
        }

        return null;
    }

    public void Update(){
        if (updateRobotAI)
        {
            robotAI.Update();
        }
        GameObject current;
        GridCoordinate previousLocation;
        for (ListIterator<GameObject> i = gameObjects.listIterator(); i.hasNext(); ){
            current = i.next();
            previousLocation = new GridCoordinate(current.Location().X, current.Location().Y);
            current.Update();
            if (!current.Location().Equals(previousLocation)){
                objectReference[previousLocation.X][previousLocation.Y].remove(current);
                objectReference[current.Location().X][current.Location().Y].add(current);
            }
        }
    }
    public void Draw(Graphics g)
    {
     //draw all the floor first
     for(int y = 0; y < levelHeight; y++) {
            for (int x = 0; x < levelWidth; ++x){
                if(wall[x][y] >= 0) {g.drawImage(pics[wall[x][y]], x * TILE_SIZE, y * TILE_SIZE, null);}
            }
     }
        for (int y = 0; y < levelHeight; ++y){ //Draw stuff back to front

            for (int x = 0; x < levelWidth; ++x){
                LinkedList<GameObject> objectsHere = objectsAt(new GridCoordinate(x, y));

                {
                    ListIterator<GameObject> i = objectsHere.listIterator();

                    while (i.hasNext())
                    {
                        GameObject current = i.next();

                        current.Draw(g);

                        if (y > 0 && x < levelWidth - 1)
                        {
                            if (wall[x + 1][y] < 0 && wall[x + 1][y-1] < 0)
                            {
                                g.drawImage(pics[FLOOR_PICS + 0], (x + 1) * TILE_SIZE - WALL_SHIFT, (y - 1) * TILE_SIZE - WALL_SIZE, null);
                            }
                        }
                    }
                }

                if (wall[x][y] < 0){
                    boolean wallBelow = false;
                    boolean wallAbove = false;
                    boolean wallRight = false;
                    boolean wallBelowRight = false;

                    if (y > 0)
                    {
                        wallAbove = (wall[x][y - 1] < 0);
                    }

                    if (y < levelHeight - 1 && x < levelWidth - 1)
                    {
                        wallBelowRight = (wall[x + 1][y + 1] < 0);
                    }

                    if (y < levelHeight - 1)
                    {
                        wallBelow = (wall[x][y + 1] < 0);
                    }

                    if (x < levelWidth - 1)
                    {
                        wallRight = (wall[x + 1][y] < 0);
                    }

                    g.drawImage(pics[FLOOR_PICS + 0], x * TILE_SIZE - WALL_SHIFT, y * TILE_SIZE - WALL_SIZE, null);

                    if(!wallRight) {
                        g.setColor(SIDEWALL_COLOR);
                        DrawVerticalShiftedRect(g, (x + 1) * TILE_SIZE - WALL_SHIFT, y * TILE_SIZE - WALL_SIZE, WALL_SIZE, WALL_SHIFT, TILE_SIZE);
                    }

                    if (!wallBelow)
                    {
                        g.setColor(WALL_COLOR);
                        DrawSideShiftedRect(g, x * TILE_SIZE, (y + 1) * TILE_SIZE, WALL_SHIFT, TILE_SIZE, -WALL_SIZE);
                    }
                }
            }
        }

        /*g.setColor(Color.BLACK);
          for (int y = 0; y < levelHeight; ++y){ //Draw stuff back to front
          for (int x = 0; x < levelHeight; ++x){
          if (x > 0)
          {
          g.drawLine((x - 1) * TILE_SIZE, y * TILE_SIZE, x * TILE_SIZE, y * TILE_SIZE);
          }

          if (y > 0)
          {
          g.drawLine(x * TILE_SIZE, (y - 1) * TILE_SIZE, x * TILE_SIZE, y * TILE_SIZE);
          }
          }
          }*/
    }

    public void DrawSideShiftedRect(Graphics g, float x1, float y1, float shift, float width, float height)
    {
        int[] x = new int[4];
        int[] y = new int[4];

        x[0] = (int)x1;
        y[0] = (int)y1;
        x[1] = (int)(x1 + width);
        y[1] = (int)y1;
        x[2] = (int)(x1 + width - shift);
        y[2] = (int)(y1 + height);
        x[3] = (int)(x1 - shift);
        y[3] = (int)(y1 + height);

        g.fillPolygon(x, y, 4);
    }

    public void DrawVerticalShiftedRect(Graphics g, float x1, float y1, float shift, float width, float height)
    {
        int[] x = new int[4];
        int[] y = new int[4];

        x[0] = (int)x1;
        y[0] = (int)y1;
        x[1] = (int)(x1 + width);
        y[1] = (int)(y1 + shift);
        x[2] = (int)(x1 + width);
        y[2] = (int)(y1 + height + shift);
        x[3] = (int)x1;
        y[3] = (int)(y1 + height);

        g.fillPolygon(x, y, 4);
    }

    public void DrawShiftedTriangle(Graphics g, float x1, float y1, float shift, float height)
    {
        int[] x = new int[3];
        int[] y = new int[3];

        x[0] = (int)x1;
        y[0] = (int)y1;
        x[1] = (int)x1;
        y[1] = (int)(y1 + height);
        x[2] = (int)(x1 - shift);
        y[2] = (int)(y1 + height);

        g.fillPolygon(x, y, 3);
    }

    public GridCoordinate ScreenToGrid(Vector2 screenPosition)
    {
        return new GridCoordinate((int)(screenPosition.X / TILE_SIZE), (int)(screenPosition.Y / TILE_SIZE));
    }
    public Vector2 GridToScreen(GridCoordinate gridPosition){
        return new Vector2(gridPosition.X * TILE_SIZE, gridPosition.Y * TILE_SIZE);
    }
    public Vector2 CentreGridToScreen(GridCoordinate gridPosition){
        return new Vector2((gridPosition.X + 0.5f) * TILE_SIZE, (gridPosition.Y + 0.5f) * TILE_SIZE);
    }
    public boolean spaceAvailable(GridCoordinate location){
        if (location.X < 0 || location.X >= levelWidth || location.Y < 0 || location.Y >= levelHeight){
            return false;
        }
        if (wall[location.X][location.Y] < 0){
            return false;
        }
        ListIterator<GameObject> i = objectReference[location.X][location.Y].listIterator();
        GameObject current;
        while (i.hasNext()){
            current = i.next();
            if (current.Collides()){
                return false;
            }
        }
        return true;
    }
    public boolean spaceAvailable(GridCoordinate location, GameObject ignoreObj){
        if (spaceAvailable(location)){
            return true;
        } else if (collidingObjectAt(location) == ignoreObj && ignoreObj != null){
            return true;
        }
        return false;
    }
    public boolean spaceAvailable(GridCoordinate location, ArrayList<GameObject> ignoreObjs){
        if (spaceAvailable(location)){
            return true;
        }
        GameObject collider = collidingObjectAt(location);
        if (collider == null){
            return false;
        }
        for (int i = 0; i < ignoreObjs.size(); ++i){
            if (collider == ignoreObjs.get(i)){
                return true;
            }
        }
        return false;
    }
    public GameObject collidingObjectAt(GridCoordinate location){ //Returns colliding object
        if (location.X < 0 || location.X >= levelWidth || location.Y < 0 || location.Y >= levelHeight){
            return null;
        }
        ListIterator<GameObject> i = objectReference[location.X][location.Y].listIterator();
        GameObject current;
        while (i.hasNext()){
            current = i.next();
            if (current.Collides()){
                return current;
            }
        }
        return null;
    }
    @SuppressWarnings("unchecked")
    public LinkedList<GameObject> objectsAt(GridCoordinate location){
        if (location.X < 0 || location.X >= levelWidth || location.Y < 0 || location.Y >= levelHeight){
            return null;
        }
        return (LinkedList<GameObject>)objectReference[location.X][location.Y].clone();
    } 

    //PATH FINDING SECTION
    public class AStructComparator implements Comparator<AStruct>
    {
        //@Override
        public int compare(AStruct arg0, AStruct arg1) {
            if (arg0.score < arg1.score){
                return -1;
            } else if (arg0.score > arg1.score){
                return 1;
            }
            return 0;
        }
    }

    class AStruct{
        public int score;
        public AStruct parent;
        public GridCoordinate location;
        public AStruct(int score, GridCoordinate location){
            this.score = score;
            this.location = location;
        }
        public AStruct(int score, GridCoordinate location, AStruct parent){
            this.score = score;
            this.location = location;
            this.parent = parent;
        }
    }
    public GridCoordinate GetNextLocation(GridCoordinate startingLocation, GridCoordinate targetLocation, ArrayList<GameObject> targetObj) {
        //The 'targetObj' is ignored in potential collisions.
        //If targetLocation refers to a square that has a colliding object != targetObj, then there is no path
        // and null is returned.
        startingLocation = startingLocation.Copy(); //Unnecessary, but "safer"
        targetLocation = targetLocation.Copy();
        if (startingLocation.Equals(targetLocation)){
            return targetLocation;
        }
        if (!spaceAvailable(targetLocation, targetObj)){
            //System.out.println("A*: Something's in the way at the destination!");
            return null;
        }
        Comparator<AStruct> comparator = new AStructComparator();
        PriorityQueue<AStruct> open = new PriorityQueue<AStruct>(50, comparator);
        ArrayList<AStruct> closed = new ArrayList<AStruct>();
        GridCoordinate newLocation;
        AStruct start = new AStruct(0, startingLocation);
        AStruct current, temp;
        Iterator<AStruct> iterator;
        open.add(start);
        int score;
        while (true){
            if (open.size() == 0){
                //System.out.println("A*: No nodes left");
                return null;
            }
            current = open.remove();
            closed.add(current);
            for (int x = -1; x <= 1; ++x){
                for (int y = -1; y<=1; ++y){
                    if (x == 0 && y == 0){
                        continue;
                    }
                    newLocation = new GridCoordinate(current.location.X + x, current.location.Y + y);
                    if (spaceAvailable(newLocation, targetObj) && (Math.abs(x) - Math.abs(y) != 0 || 
                                (spaceAvailable(new GridCoordinate(current.location.X + x, current.location.Y), targetObj)
                                 && spaceAvailable(new GridCoordinate(current.location.X, current.location.Y + y), targetObj)))){

                        if (newLocation.Equals(targetLocation)){
                            if (current == start){
                                return targetLocation;
                            }
                            AStruct prev = current;
                            while (current != start){
                                prev = current;
                                current = current.parent;
                            }
                            return prev.location;
                        }
                        boolean closedContains = false;
                        for (int i = 0; i < closed.size(); ++i){
                            if (closed.get(i).location.Equals(newLocation)){
                                closedContains = true;
                                break;
                            }
                        }
                        if (!closedContains){       
                            score = current.score + (Math.abs(x) - Math.abs(y) == 0 ? 14 : 10) +
                                Math.abs(newLocation.X - targetLocation.X) +
                                Math.abs(newLocation.Y - targetLocation.Y);
                            AStruct openObj = null;
                            iterator = open.iterator();
                            while (iterator.hasNext()){
                                temp = iterator.next();
                                if (temp.location.Equals(newLocation)){
                                    openObj = temp;
                                    break;
                                }
                            }
                            if (openObj != null && openObj.score > score){
                                open.remove(openObj);
                                open.add(new AStruct(score, newLocation, current));
                            } else if (openObj == null){
                                open.add(new AStruct(score, newLocation, current));
                            }
                        }      
                                 }
                }
            }
        }
    }
    public GridCoordinate GetNextLocation(GridCoordinate startingLocation, GameObject targetObj) {
        ArrayList<GameObject> al = new ArrayList<GameObject>(1);
        al.add(targetObj);
        return GetNextLocation(startingLocation, targetObj.Location(), al);
    }
}
