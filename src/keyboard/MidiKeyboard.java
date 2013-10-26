package keyboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Patch;
import javax.sound.midi.Synthesizer;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import keyboard.Key.KeyLabelAlignment;

/**
 * An interactive MIDI soundboard based keyboard that maps alphabetical keys to different notes.
 * Any instrument available to the OS can be played via the instrument list at the side.
 * 
 * @author Darragh Maguire Kane
 */

public class MidiKeyboard
{
	private boolean[] keyInUse = new boolean[26]; // 1 per letter of the alphabet

	private Instrument[] instruments;
	private MidiChannel[] midiChannels;
	private String[] instrumentNames;
	private Synthesizer synthesizer;

	private JFrame frameMain;
	private JList<String> instrumentList;
	private JPanel keyboardPanel;
	private JPanel panelMain;
	private JScrollPane instrumentScrollpane;
	private ImageIcon icon;
	private Keyboard keyboard;

	public static void main(String[] args)
	{
		new MidiKeyboard();		
	}
	
	public MidiKeyboard()
	{
		setLookAndFeel();
		initialiseSynthesizer();
		initialiseGuiComponents();
		initialiseListeners();
		frameMain.setVisible(true);		
	}

	private void setLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void initialiseSynthesizer()
	{
		try
		{
			synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();

			instruments = synthesizer.getAvailableInstruments();
			instrumentNames = getUsableInstrumentNames();

			midiChannels = synthesizer.getChannels();
		}
		catch (MidiUnavailableException e)
		{
			e.printStackTrace();
		}
	}

	private String[] getUsableInstrumentNames()
	{
		ArrayList<String> tempNames = new ArrayList<String>();
		int bankNo;
		int counter = 0;
		for (int i = 0; i < instruments.length; i++)
		{
			bankNo = instruments[i].getPatch().getBank();
			if (bankNo == 0 || bankNo == 3)
				tempNames.add(counter++ + " - " + instruments[i].getName());
		}

		return tempNames.toArray(new String[0]);
	}

	private void initialiseGuiComponents()
	{
		frameMain = new JFrame();
		instrumentList = new JList<String>();
		instrumentScrollpane = new JScrollPane();
		panelMain = new JPanel();

		instrumentList.setBackground(new Color(0xFFFFFFF));
		instrumentList.setFont(new Font("Trebuchet MS", Font.PLAIN, 16));
		instrumentList.setListData(instrumentNames);
		instrumentList.setSelectedIndex(0);

		instrumentScrollpane.setViewportView(instrumentList);
		instrumentScrollpane.setBackground(new Color(0xFFFFFFF));
		instrumentScrollpane.setBorder(new LineBorder(Color.BLACK, 3));

		keyboard = new Keyboard(48, KeyLabelAlignment.DIGITS_AND_CAPITAL_LETTERS);
		keyboard.setMaximumSize(keyboard.getSize());

		GridBagLayout gridBag = new GridBagLayout();
		keyboardPanel = new JPanel(gridBag);
		keyboardPanel.add(keyboard);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.CENTER;
		gridBag.setConstraints(keyboard, constraints);
		
		setBackground(new Color(0x444444));

		panelMain.setBorder(new EmptyBorder(10, 10, 10, 0));
		panelMain.setLayout(new BorderLayout());
		panelMain.add(instrumentScrollpane, BorderLayout.WEST);
		panelMain.add(keyboardPanel, BorderLayout.CENTER);

		icon = new ImageIcon(this.getClass().getResource("/keyboard/resources/music_note_icon.png"));
		
		frameMain.add(panelMain);
		frameMain.setTitle("MIDI Keyboard");
		frameMain.setIconImage(icon.getImage());
		frameMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameMain.setPreferredSize(new Dimension(770, 220));
		frameMain.pack();
		frameMain.setLocationRelativeTo(null);
		frameMain.setVisible(true);
	}

	private void setBackground(Color color)
	{
		keyboard.setBackgroundColor(color);
		keyboardPanel.setBackground(color);
		panelMain.setBackground(color);
	}

	private void initialiseListeners()
	{
		instrumentList.addListSelectionListener(new ListSelectionListener()
		{
			Patch temp;

			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					temp = instruments[instrumentList.getSelectedIndex()].getPatch();
					midiChannels[0].programChange(temp.getBank(), temp.getProgram());
				}
			}
		});

		instrumentList.addKeyListener(new KeyListener()
		{
			public void keyTyped(KeyEvent e)
			{
			}

			private final char[] keys = { 'z', 'x', 'c', 'v', 'b', 'n', 'm', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p' };

			private int getToneValue(char c)
			{
				for (int i = 0; i < keys.length; i++)
					if (c == keys[i])
						return i;
				return 0;
			}
			
			private boolean isValidKey(char c)
			{
				if (c - 'a' >= 0 && c - 'a' < keys.length)
				{
					return true;
				}
				
				return false;
			}

			public void keyPressed(KeyEvent e)
			{
				char c = Character.toLowerCase(e.getKeyChar());
				
				if (isValidKey(c) && !keyInUse[c - 'a']) //Checks if key is held down
				{
					keyboard.keyPressed(c);
					midiChannels[0].noteOn(getToneValue(c) + 36, 600);
					keyInUse[c - 'a'] = true;
				}
			}

			public void keyReleased(KeyEvent e)
			{
				char c = Character.toLowerCase(e.getKeyChar());

				if (isValidKey(c))
				{
					keyboard.keyReleased(c);
					midiChannels[0].noteOff(getToneValue(c) + 36);
					keyInUse[c - 'a'] = false;
				}
			}
		});
	}
}