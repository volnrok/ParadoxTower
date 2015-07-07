package gameObjects;

import game.*;
import scripts.*;
import util.*;

public class ScriptTrigger
{
    public GridCoordinate lower;
    public GridCoordinate upper;
    public String scriptFile = null;
    private Level level;

    public ScriptTrigger(Level newLevel, GridCoordinate newLower, GridCoordinate newUpper, String newScriptFile)
    {
    	level = newLevel;
    	
        lower = newLower.Copy();
        upper = newUpper.Copy();

        scriptFile = newScriptFile;
    }

    public void RunScript()
    {
        level.scriptHandler.RunScript(scriptFile);
    }
}