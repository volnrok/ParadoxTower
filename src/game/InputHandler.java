package game;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener
{
 private LinkedList<Integer> keysDown = new LinkedList<Integer>();
 private LinkedList<Integer> keysDownOnce = new LinkedList<Integer>();
 
 private int mouseX;
 private int mouseY;
 private LinkedList<Integer> mouseButtonsDown = new LinkedList<Integer>();
 private LinkedList<Integer> mouseButtonsOnce = new LinkedList<Integer>();
 
 public InputHandler(Component c)
 {
  c.addKeyListener(this);
  c.addMouseListener(this);
  c.addMouseMotionListener(this);
 }

 //@Override
 public void mouseDragged(MouseEvent e)
 {
  mouseX = e.getX();
  mouseY = e.getY();
 }

 //@Override
 public void mouseMoved(MouseEvent e)
 {
  mouseX = e.getX();
  mouseY = e.getY();
 }

 //@Override
 public void mouseClicked(MouseEvent e)
 {
  mouseX = e.getX();
  mouseY = e.getY();
  
 }

 //@Override
 public void mouseEntered(MouseEvent e)
 {
  // not used
 }

 //@Override
 public void mouseExited(MouseEvent e)
 {
  // not used
 }

// @Override
 public void mousePressed(MouseEvent e)
 {
  
 }

 //@Override
 public void mouseReleased(MouseEvent e)
 {
  // TODO Auto-generated method stub
  
 }

 //@Override
 public synchronized void keyPressed(KeyEvent e)
 {
  boolean keyAlreadyDown = false;
  
  ListIterator<Integer> i = keysDown.listIterator();
  
  while (i.hasNext())
  {
   int current = i.next();
   
   if (current == e.getKeyCode())
   {
    keyAlreadyDown = true;
   }
  }
  
  if (!keyAlreadyDown)
  {
   keysDown.add(e.getKeyCode());
   keysDownOnce.add(e.getKeyCode());
  }
 }

 //@Override
 public synchronized void keyReleased(KeyEvent e)
 {
  {
   ListIterator<Integer> i = keysDown.listIterator();
   
   while (i.hasNext())
   {
    int current = i.next();
    
    if (current == e.getKeyCode())
    {
     i.remove();
    }
   }
  }
  
  {
   ListIterator<Integer> i = keysDownOnce.listIterator();
   
   while (i.hasNext())
   {
    int current = i.next();
    
    if (current == e.getKeyCode())
    {
     i.remove();
    }
   }
  }
 }

 //@Override
 public void keyTyped(KeyEvent e)
 {
  // not used
  
 }
 
 public synchronized boolean IsKeyDown(int keyCode)
 {
  ListIterator<Integer> i = keysDown.listIterator();
  
  while (i.hasNext())
  {
   int current = i.next();
   
   if (current == keyCode)
   {
    return true;
   }
  }
  
  return false;
 }
 
 public synchronized boolean IsKeyHit(int keyCode)
 {
  ListIterator<Integer> i = keysDown.listIterator();
  
  while (i.hasNext())
  {
   int current = i.next();
   
   if (current == keyCode)
   {
    ListIterator<Integer> j = keysDownOnce.listIterator();
    
    while (j.hasNext())
    {
     int current2 = j.next();
     
     if (current2 == keyCode)
     {
      j.remove();
      return true;
     }
    }
   }
  }
  
  return false;
 }
}
