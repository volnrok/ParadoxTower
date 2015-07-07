package util;

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public abstract class FileInput {
 
 public static String platformFilePath(String fpath) {
  return fpath.replace('\\', File.separatorChar);
 }
 
 //loads an image file
 public static BufferedImage loadImage(String fpath) {
  try {
   return ImageIO.read(new File(platformFilePath(fpath)));
  } catch(Exception e) {
   try {
    return ImageIO.read(new File(platformFilePath("..\\" + fpath)));
   } catch(Exception e2) {
       try {
    return ImageIO.read(new File(platformFilePath("D:\\GGJGame\\" + fpath)));
   } catch(Exception e3) {
       System.err.println("IMAGE FILE FAILED TO LOAD: " + fpath);
    return null;
   }
   }
  }
 }
 
}