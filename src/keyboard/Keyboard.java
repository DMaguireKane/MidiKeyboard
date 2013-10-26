package keyboard;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import keyboard.Key.KeyLabelAlignment;

/**
 * Handles layout of keys.
 * 
 * @author Darragh Maguire Kane
 * @version 25/10/2013
 */

@SuppressWarnings("serial")
public class Keyboard extends JComponent
{
	private final static String[] NOTES = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };

	private final static char[][] KEYS = { { 'z', 'x', 'c', 'v', 'b', 'n', 'm' }, { 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l' }, { 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p' } };

	private static HashMap<Character, Key> keys = new HashMap<Character, Key>();
	private JPanel[] panels;

	public Keyboard(int size, KeyLabelAlignment labelAlignment)
	{
		JPanel topPanel = new JPanel();
		JPanel middlePanel = new JPanel();
		JPanel bottomPanel = new JPanel();

		panels = new JPanel[] { bottomPanel, middlePanel, topPanel };

		new BoxLayout(topPanel, BoxLayout.X_AXIS);
		new BoxLayout(middlePanel, BoxLayout.X_AXIS);
		new BoxLayout(bottomPanel, BoxLayout.X_AXIS);

		String keyLabel;

		Key.setSize(size);
		Key.setLabelAlignment(labelAlignment);

		for (int i = 0; i < KEYS.length; i++)
		{
			for (int j = 0; j < KEYS[i].length; j++)
			{
				keyLabel = NOTES[((i * (KEYS[i].length - 2)) + j) % NOTES.length];
				keys.put(KEYS[i][j], new Key(keyLabel, KEYS[i][j]));
				panels[i].add(keys.get(KEYS[i][j]));
			}
		}

		this.setLayout(new GridLayout(0, 1));
		this.add(topPanel);
		this.add(middlePanel);
		this.add(bottomPanel);
		this.setPreferredSize(new Dimension((int)(size * 11 * 1.1), (int)(size * 3 * 1.1)));
	}

	public HashMap<Character, Key> getKeys()
	{
		return keys;
	}

	public void keyPressed(char c)
	{
		Key key;
		if ((key = keys.get(c)) != null)
		{
			int keyValue = c - 'a';
			if(!Key.keyPressed[keyValue])
			{
				Key.keyPressed[keyValue] = true;
				key.repaint();
			}
		}
	}

	public void keyReleased(char c)
	{
		Key key;
		if ((key = keys.get(c)) != null)
		{
			Key.keyPressed[c - 'a'] = false;
			key.repaint();
		}
	}

	public void setBackgroundColor(Color color)
	{
		for (JPanel p : panels)
			p.setBackground(color);
		Key.setBackgroundColor(color);
	}
}