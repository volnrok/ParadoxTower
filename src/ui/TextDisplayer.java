package ui;

import game.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import java.util.*;

public class TextDisplayer
{
	class Message
	{
		public String text = "";
		public BufferedImage image = null;
		
		public Message(String newText)
		{
			text = newText;
		}
		
		public Message(String newText, BufferedImage newImage)
		{
			text = newText;
			image = newImage;
		}
	}
	
	Game game;
	Font font;
	LinkedList<String> text = new LinkedList<String>();
	LinkedList<Message> messages = new LinkedList<Message>();
	boolean textCleared = true;
	BufferedImage dialogImage = null;
	
	public TextDisplayer(Game newGame)
	{
		game = newGame;
		font = new Font("Consolas", Font.PLAIN, 38);
	}
	
	public boolean IsTextCleared()
	{
		return textCleared;
	}
	
	public void Update()
	{
		if (text.size() > 0)
		{
			InputHandler input = game.GetInput();
			
			if (input.IsKeyHit(KeyEvent.VK_SPACE) || input.IsKeyHit(KeyEvent.VK_ENTER))
			{
				NextText();
			}
		}
	}
	
	public void ProcessNextMessage()
	{
		Message newMessage = messages.remove();
		dialogImage = newMessage.image;
		
		Scanner scan = new Scanner(newMessage.text);
		
		LinkedList<String> lines = new LinkedList<String>();
		
		String temp = "";
		while (scan.hasNext())
		{
			String next = scan.next() + " ";
			
			if (temp.length() + next.length() > 32)
			{
				lines.add(temp);
				temp = next;
			}
			else
			{
				temp += next;
			}
		}
		
		if (temp.length() > 0)
		{
			lines.add(temp);
		}
		
		if (lines.size() % 2 == 1)
		{
			lines.add("");
		}
		
		ListIterator<String>i = lines.listIterator();
		
		while (i.hasNext())
		{
			text.add(i.next());
		}
	}
	
	public void NextText()
	{
		if (text.size() > 0)
		{
			text.remove();
			text.remove();
			
			if (text.isEmpty())
			{
				if (messages.isEmpty())
				{
					textCleared = true;
				}
				else
				{
					ProcessNextMessage();
				}
			}
		}
	}
	
	public void Say(String s)
	{
		messages.add(new Message(s));
		
		if (text.isEmpty())
		{
			ProcessNextMessage();
		}
		
		textCleared = false;
	}
	
	public void Say(String s, BufferedImage img)
	{
		messages.add(new Message(s, img));
		
		if (text.isEmpty())
		{
			ProcessNextMessage();
		}
		
		textCleared = false;
	}
	
	public void Draw(Graphics g)
	{
		if (!textCleared)
		{
			g.setColor(Color.GRAY);
			g.fillRect(50, game.getHeight() - 150, game.getWidth() - 100, 85);
			
			if (dialogImage != null)
			{
				g.drawImage(dialogImage, 25, game.getHeight() - 125, 50, 50, null);
			}
			
			g.setColor(Color.BLACK);
			g.setFont(font);
			g.drawString(text.get(0), 75, game.getHeight() - 120);
			g.drawString(text.get(1), 75, game.getHeight() - 80);
		}
	}
}
