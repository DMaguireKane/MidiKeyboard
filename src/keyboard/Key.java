package keyboard;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

/**
 * Handles drawing of a keyboard key.
 * 
 * @author Darragh Maguire Kane
 * @version 25/10/2013
 */

@SuppressWarnings("serial")
public class Key extends JComponent
{
	private static KeyLabelAlignment keyLabelAlignment = KeyLabelAlignment.ALL_CHARACTERS;
	private static int size = 100;
	private static Color BACKGROUND = Color.WHITE;
	private static int w = 200;
	private static BasicStroke stroke = new BasicStroke(2);
	private static Color WHITE = Color.white;
	private static Color BLACK = Color.black;	
	public static boolean[] keyPressed = new boolean[26];

	private String label;
	public char alphaLabel;

	public static void setLabelAlignment(KeyLabelAlignment k)
	{
		Key.keyLabelAlignment = k;
	}

	public static void setSize(int size)
	{
		Key.size = size;
	}

	public Key(String label, char alphaChar)
	{
		this.label = label;
		this.alphaLabel = Character.toUpperCase(alphaChar);
		setPreferredSize(new Dimension(size, size));
	}

	public void paint(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(BACKGROUND);
		g2.fillRect(0, 0, size, size);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.translate(getWidth() / 2, getHeight() / 2);
		w = getHeight() - 4;

		g2.setFont(new Font("Tahoma", Font.BOLD, w / 2));
		FontMetrics fm = g2.getFontMetrics();

		g2.setStroke(stroke);
		g2.setColor(keyPressed[alphaLabel - 'A']?BLACK:WHITE);
		g2.fillRoundRect(-w / 2, -w / 2, w, w, w / 5, w / 5);
		g2.setColor(keyPressed[alphaLabel - 'A']?WHITE:BLACK);
		g2.drawRoundRect(-w / 2, -w / 2, w, w, w / 5, w / 5);
		
		if (keyLabelAlignment == KeyLabelAlignment.ALL_CHARACTERS)
		{
			g2.drawString(label, -fm.stringWidth(label) / 2, fm.getDescent());
		}
		else if (keyLabelAlignment == KeyLabelAlignment.DIGITS_AND_CAPITAL_LETTERS)
		{
			g2.drawString(label, -fm.stringWidth(label) / 2, fm.getAscent()/3);
		}
		
		g2.setColor(new Color(0xAAAAAA));
		g2.setFont(new Font("Tahoma", Font.BOLD, w / 4));
		g2.drawString(alphaLabel + "", w/2 - w/4, -w/2 + w/4);
	}

	public enum KeyLabelAlignment
	{
		ALL_CHARACTERS, DIGITS_AND_CAPITAL_LETTERS
	}
	
	public static void setBackgroundColor(Color color)
	{
		BACKGROUND = color;
	}
}