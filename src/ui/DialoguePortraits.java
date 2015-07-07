package ui;

import java.awt.image.*;
import util.*;

public class DialoguePortraits
{
 public static BufferedImage mutantNeutral;
 public static BufferedImage mutantHappy;
 public static BufferedImage mutantAngry;
 
 public static BufferedImage scientistNeutral;
 public static BufferedImage scientistAngry;
 
 public static BufferedImage robotNeutral;
 public static BufferedImage robotScanning;
 public static BufferedImage robotRedeye;
 
 static{
  mutantNeutral = FileInput.loadImage("img\\dialogue\\mutant\\neutral.png");
  mutantHappy = FileInput.loadImage("img\\dialogue\\mutant\\happy.png");
  mutantAngry = FileInput.loadImage("img\\dialogue\\mutant\\angry.png");
  
  scientistNeutral = FileInput.loadImage("img\\dialogue\\scientist\\neutral.png");
  scientistAngry = FileInput.loadImage("img\\dialogue\\scientist\\angry.png");
  
  robotNeutral = FileInput.loadImage("img\\dialogue\\robot\\neutral.png");
  robotScanning = FileInput.loadImage("img\\dialogue\\robot\\scanning.png");
  robotRedeye = FileInput.loadImage("img\\dialogue\\robot\\redeye.png");
 }
 
 public static BufferedImage lookup(String charMood) {
     if (charMood.equals("mutantHappy")) {
         return mutantHappy;
     } else if (charMood.equals("mutantAngry")) {
         return mutantAngry;
     } else if (charMood.equals("mutantNeutral")) {
         return mutantNeutral;
     }
     
     else if (charMood.equals("scientistNeutral")) {
         return scientistNeutral;
     } else if (charMood.equals("scientistAngry")) {
         return scientistAngry;
     }
     
     else if (charMood.equals("robotNeutral")) {
         return robotNeutral;
     } else if (charMood.equals("robotScanning")) {
         return robotScanning;
     } else if (charMood.equals("robotRedeye")) {
         return robotRedeye;
     }
     
     return null;
 }
}
