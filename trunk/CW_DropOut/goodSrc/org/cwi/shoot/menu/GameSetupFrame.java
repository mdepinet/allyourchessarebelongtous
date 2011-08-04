package org.cwi.shoot.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import org.cwi.shoot.config.GameMode;
import org.cwi.shoot.config.GameOptions;
import org.cwi.shoot.control.Shoot;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Weapon;

public class GameSetupFrame extends JFrame implements ActionListener, ListSelectionListener {
	private static final long serialVersionUID = -6659414234158999706L;
	private File mapChosen;
	private JTextField mapTextField;
	private File nameSetChosen;
	private JTextField nameSetTextField;
	private JTextField nameTextField;
	private ArrayList<JButton> buttonGroup;
	private Shoot control;
	private int team;
	private GameMode gameMode;
	private JTable table;
	private GameMode[] modes;
	private boolean compPlayersOnly;
	
	public GameSetupFrame(Shoot control) {
		super("Shoot Menu");
		compPlayersOnly = false;
		team = 1;
		this.control = control;
		buttonGroup = new ArrayList<JButton>();
		gameMode = null;
		mapChosen = new File(GameOptions.MAP_RESOURCE);
		nameSetChosen = new File(GameOptions.NAME_RESOURCE);
		setBounds(new Rectangle(400,300,400,600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		
		JPanel optionsPanel = new JPanel();
		optionsPanel.setPreferredSize(new Dimension(400,200));
		optionsPanel.setLayout(new FlowLayout());
		getContentPane().add(optionsPanel, BorderLayout.NORTH);
		
		JPanel optionsSubPanel = new JPanel();
		optionsSubPanel.setPreferredSize(new Dimension(400,30));
		optionsSubPanel.setLayout(new FlowLayout());
		optionsSubPanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);
		JLabel label = new JLabel("What is your name?", SwingConstants.LEFT);
		label.setPreferredSize(new Dimension(150,20));
		optionsSubPanel.add(label);
		nameTextField = new JTextField("Player 1");
		nameTextField.setPreferredSize(new Dimension(200,20));
		optionsSubPanel.add(nameTextField);
		optionsPanel.add(optionsSubPanel);
		
		optionsSubPanel = new JPanel();
		optionsSubPanel.setPreferredSize(new Dimension(400, 60));
		optionsSubPanel.setLayout(new FlowLayout());
		optionsSubPanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);
		label = new JLabel("Pick a Map", SwingConstants.CENTER);
		label.setPreferredSize(new Dimension(300,20));
		optionsSubPanel.add(label);
		mapTextField = new JTextField(mapChosen.getName());
		mapTextField.setPreferredSize(new Dimension(250, 25));
		JButton button = new JButton("Browse...");
		button.setActionCommand("mapchooser");
		button.addActionListener(this);
		optionsSubPanel.add(mapTextField);
		optionsSubPanel.add(button);
		optionsPanel.add(optionsSubPanel);
		
		optionsSubPanel = new JPanel();
		optionsSubPanel.setPreferredSize(new Dimension(400, 80));
		optionsSubPanel.setLayout(new FlowLayout());
		optionsSubPanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);
		label = new JLabel("Pick a Name Set", SwingConstants.CENTER);
		label.setPreferredSize(new Dimension(300,20));
		optionsSubPanel.add(label);
		nameSetTextField = new JTextField(nameSetChosen.getName());
		nameSetTextField.setPreferredSize(new Dimension(250, 25));
		button = new JButton("Browse...");
		button.setActionCommand("namesetchooser");
		button.addActionListener(this);
		optionsSubPanel.add(nameSetTextField);
		optionsSubPanel.add(button);
		JCheckBox compOnly = new JCheckBox("Computer Players only");
		compOnly.setSelected(false);
		compOnly.setActionCommand("computerplayersonly");
		compOnly.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				compPlayersOnly = ((JCheckBox)ie.getSource()).isSelected();
			}
		});
		optionsSubPanel.add(compOnly);
		optionsPanel.add(optionsSubPanel);
		
		String[] columnNames = {"Select a Mode"};
		String[][] modeNames = new String[GameMode.availableTypes.size()][1];
		modes = new GameMode[GameMode.availableTypes.size()];
		for(int i = 0; i < GameMode.availableTypes.size();i++)
		{
			try {
				modes[i] = GameMode.availableTypes.get(i).newInstance();
				modeNames[i][0] = modes[i].getModeName();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
		}
		table = new JTable(modeNames, columnNames);
		JPanel panel = new JPanel(new FlowLayout());
		JScrollPane pane = new JScrollPane(table);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(this);
		pane.setPreferredSize(new Dimension(350, 100));
		table.setRowSelectionInterval(0, 0);
		table.setRowHeight(30);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		panel.add(pane);
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		char c = 0;
		for(c = 'L'; c <= (int)('O');c++) {
			Weapon w = new Weapon(c, new Point());
			if (Weapon.getWeaponImg(w.getImgLoc()) != null) button = new JButton(GameMap.teamNames[c-76], new ImageIcon(Weapon.getWeaponImg(w.getImgLoc())));
			else button = new JButton(GameMap.teamNames[c-76]);
			button.setVerticalTextPosition(JButton.BOTTOM);
			button.setHorizontalTextPosition(JButton.CENTER);
			button.setPreferredSize(new Dimension(90,60));
			button.setForeground((c=='L') ? Color.BLACK : Color.WHITE);
			button.setBackground((c=='L') ? Color.GREEN : Color.BLUE);
			button.setActionCommand("" + c);
			button.addActionListener(this);
			panel.add(button);
			buttonGroup.add(button);
		}
		panel.add(buttonPanel);
		getContentPane().add(panel, BorderLayout.CENTER);
		
		button = new JButton("Start Game");
		button.setAlignmentY(JButton.RIGHT_ALIGNMENT);
		button.setActionCommand("startgame");
		button.setSize(new Dimension(200, 30));
		button.addActionListener(this);
		getContentPane().add(button, BorderLayout.SOUTH);
		
		String mapString = "";
        try {
        	 mapString = readFile(mapChosen);
        } catch(IOException ioe) {}
        for(int i = 1; i < 5;i++)
        {
        	if(mapString.indexOf(""+i)==-1) {
        		buttonGroup.get(i-1).setVisible(false);
        	}
        }
		setVisible(true);
	}

	private static String readFile(File f) throws IOException {
	  FileInputStream stream = new FileInputStream(f);
	  try {
	    FileChannel fc = stream.getChannel();
	    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
	    /* Instead of using default, pass in a decoder. */
	    return Charset.defaultCharset().decode(bb).toString();
	  }
	  finally {
	    stream.close();
	  }
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("mapchooser")) {
			JFileChooser chooser = new JFileChooser("resource/maps");
			chooser.setFileFilter(new FileFilter(){
				@Override
				public boolean accept(File f) {
					return f.isDirectory() || f.getName().endsWith(".map");
				}
				@Override
				public String getDescription() {
					return "Map files";
				}
			});
			mapChosen = null;
			int returnVal = chooser.showOpenDialog(null);
	        if (returnVal == JFileChooser.APPROVE_OPTION) mapChosen = chooser.getSelectedFile();
	        else mapChosen = new File(GameOptions.MAP_RESOURCE);
	        mapTextField.setText(mapChosen.getName());
	        String mapString = "";
	        try {
	        	 mapString = readFile(mapChosen);
	        } catch(IOException ioe) {}
	        for(int i = 1; i < 5;i++)
	        {
	        	if(mapString.indexOf(""+i)==-1) {
	        		buttonGroup.get(i-1).setVisible(false);
	        	}
	        	else
	        		buttonGroup.get(i-1).setVisible(true);
	        }
	        resetButtonColors();
	        for(int i = 0; i < buttonGroup.size();i++)
	        	if(buttonGroup.get(i).isVisible()) { buttonGroup.get(i).setForeground(Color.BLACK); buttonGroup.get(i).setBackground(Color.GREEN); team = i+1; break; }
		}
		else if (e.getActionCommand().equals("namesetchooser")){
			JFileChooser chooser = new JFileChooser("resource/names");
			chooser.setFileFilter(new FileFilter(){
				@Override
				public boolean accept(File f) {
					return f.isDirectory() || f.getName().endsWith(".txt");
				}
				@Override
				public String getDescription() {
					return "Name Gen files";
				}
			});
			nameSetChosen = null;
	        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) nameSetChosen = chooser.getSelectedFile();
	        else nameSetChosen = new File(GameOptions.NAME_RESOURCE);
	        nameSetTextField.setText(nameSetChosen.getName());
		}
		else if(e.getActionCommand().equals("startgame")) {
			ArrayList<Character> list = new ArrayList<Character>();
			 String mapString = "";
		        try {
		        	 mapString = readFile(mapChosen);
		        } catch(IOException ioe) {}
		        for(int i = 1; i < 5;i++)
		        {
		        	if(mapString.indexOf(""+i)!=-1)
		        		list.add(new Character((char)('L'+(i-1))));
		        }
		        char[] teams = new char[list.size()];
		        for(int i =0; i < teams.length;i++)
		        	teams[i]=list.get(i);
	        control.startGame(nameTextField.getText(), mapChosen, nameSetChosen, gameMode, teams, (compPlayersOnly ? -1 : team));
			this.dispose();
		}
		else
		{
			resetButtonColors();
			((JButton)e.getSource()).setBackground(Color.GREEN);
			((JButton)e.getSource()).setForeground(Color.BLACK);
			switch((char)e.getActionCommand().charAt(0))
			{
				case 'L': team = 1; break;
				case 'M': team = 2; break;
				case 'N': team = 3; break;
				case 'O': team = 4; break;
				default: break;
			}
		}
		
	}

	private void resetButtonColors()
	{
		for(JButton b: buttonGroup) {
			b.setBackground(Color.BLUE);
			b.setForeground(Color.WHITE);
		}
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		gameMode = modes[table.getSelectedRow()];
	}

}
