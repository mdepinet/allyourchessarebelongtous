package org.cwi.shoot.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import java.util.List;
import java.util.Scanner;

import javax.swing.BoxLayout;
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
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.cwi.shoot.config.GameMode;
import org.cwi.shoot.config.GameOptions;
import org.cwi.shoot.control.Shoot;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Weapon;
import org.cwi.shoot.model.Weapon.WeaponType;
import org.cwi.shoot.profile.Profile;
import org.cwi.shoot.util.WeaponDefinition;
import org.cwi.shoot.util.WeaponLoader;

public class GameSetupFrame extends JFrame implements ActionListener, ListSelectionListener {
	private static final long serialVersionUID = -6659414234158999706L;
	private File mapChosen;
	private JTextField mapTextField;
	private File nameSetChosen;
	private JTextField nameSetTextField;
	private ArrayList<JButton> buttonGroup;
	private Shoot control;
	private int team;
	private GameMode gameMode;
	private JTable table;
	private GameMode[] modes;
	private boolean compPlayersOnly;
	private Profile profile;
	private JPanel pPanel;
	private TableModel data;
	private String weaponSetChosen;
	private JLabel wsLabel;
	private WeaponTableModel wdata;
	private JTextField numPlayersPerTeam;
	private List<JButton> arrowbuttons;
	
	public GameSetupFrame(Shoot control, Profile profile) {
		super("Game Setup Menu");
		compPlayersOnly = false;
		team = 1;
		this.control = control;
		this.profile = profile;
		weaponSetChosen = profile.getPrevWepSet() != null ? profile.getPrevWepSet() : WeaponLoader.weaponSet;
		buttonGroup = new ArrayList<JButton>();
		arrowbuttons = new ArrayList<JButton>();
		gameMode = null;
		mapChosen = new File(GameOptions.MAP_RESOURCE);
		nameSetChosen = new File(profile.getPrevNameSet() != null ? profile.getPrevNameSet() : GameOptions.NAME_RESOURCE);
		setBounds(new Rectangle(800,600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel cPane = new JPanel(new BorderLayout());
		cPane.setBorder(new LineBorder(Color.BLACK, 5));
		setContentPane(cPane);
		getContentPane().setLayout(new BorderLayout());
		
		
		data = new PlayerTableModel();
		table = new JTable(data);
		pPanel = new JPanel(new BorderLayout());
		JScrollPane spane = new JScrollPane(table);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(this);
		spane.setPreferredSize(new Dimension(150, 200));
		table.setRowHeight(30);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setTableHeader(null);
		pPanel.add(spane, BorderLayout.CENTER);
		getContentPane().add(pPanel, BorderLayout.WEST);
		
		wdata = new WeaponTableModel(weaponSetChosen);
		table = new JTable(wdata);
		pPanel = new JPanel(new BorderLayout());
		spane = new JScrollPane(table);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(this);
		spane.setPreferredSize(new Dimension(150, 200));
		table.setRowHeight(30);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		pPanel.add(spane, BorderLayout.CENTER);
		JPanel wsPanel = new JPanel(new BorderLayout());
		JLabel labelws = new JLabel("Current Weapon Set:");
//		wsLabel.setPreferredSize(new Dimension(150,200));
		wsPanel.add(labelws, BorderLayout.NORTH);
		wsLabel = new JLabel("\t     " + wdata.getWeaponSet());
		wsPanel.add(wsLabel, BorderLayout.SOUTH);
		pPanel.add(wsPanel, BorderLayout.NORTH);
		JButton wsButton = new JButton("Change Weapon Set");
		wsButton.addActionListener(this);
		wsButton.setActionCommand("changews");
		pPanel.add(wsButton, BorderLayout.SOUTH);
		getContentPane().add(pPanel, BorderLayout.EAST);
		
		JPanel optionsPanel = new JPanel();
		//optionsPanel.setPreferredSize(new Dimension(400,200));
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.PAGE_AXIS));
		//getContentPane().add(optionsPanel, BorderLayout.NORTH);
		
		JPanel optionsSubPanel = new JPanel();
		optionsSubPanel.setPreferredSize(new Dimension(400,30));
		optionsSubPanel.setLayout(new FlowLayout());
//		optionsSubPanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);
		optionsPanel.add(optionsSubPanel);
		
		optionsSubPanel = new JPanel();
		optionsSubPanel.setPreferredSize(new Dimension(400, 60));
		optionsSubPanel.setLayout(new FlowLayout());
		optionsSubPanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);
		JLabel label = new JLabel("Pick a Map", SwingConstants.CENTER);
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
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setPreferredSize(new Dimension(400,200));
		JPanel panePanel = new JPanel();
		JScrollPane pane = new JScrollPane(table);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(this);
		pane.setPreferredSize(new Dimension(350, 100));
		table.setRowSelectionInterval(0, 0);
		table.setRowHeight(30);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		panePanel.add(pane);
		panel.add(panePanel);
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
//		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
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
			buttonPanel.add(button);
			buttonGroup.add(button);
		}
		panel.add(buttonPanel);
		optionsPanel.add(panel);
		
		JPanel gameSettingsPanel = new JPanel(new FlowLayout());
		gameSettingsPanel.add(new JLabel("Number of Players per Team: "));
		numPlayersPerTeam = new JTextField("" + profile.getNumPlayersPerTeam());
		numPlayersPerTeam.setPreferredSize(new Dimension(20,20));
		gameSettingsPanel.add(numPlayersPerTeam);
		JButton arrowButton = new JButton(new ImageIcon("resource/images/arrowup.gif"));
		arrowButton.addActionListener(this);
		arrowButton.setActionCommand("arrowupteammates");
		arrowButton.setPreferredSize(new Dimension(10,10));
		arrowButton.setFont(new Font("Button", arrowButton.getFont().getStyle(), 8));
		arrowbuttons.add(arrowButton);
		JPanel arrowPanel = new JPanel();
		arrowPanel.setLayout(new BoxLayout(arrowPanel, BoxLayout.PAGE_AXIS));
		arrowPanel.add(arrowButton);
		arrowButton = new JButton(new ImageIcon("resource/images/arrowdown.gif"));
		arrowButton.addActionListener(this);
		arrowButton.setActionCommand("arrowdownteammates");
		arrowButton.setFont(new Font("Button", arrowButton.getFont().getStyle(), 8));
		arrowButton.setPreferredSize(new Dimension(10,10));
		arrowPanel.add(arrowButton);
		arrowbuttons.add(arrowButton);
		gameSettingsPanel.add(arrowPanel);
		
		optionsPanel.add(gameSettingsPanel);
		
		getContentPane().add(optionsPanel, BorderLayout.CENTER);
		
		JPanel southPanel = new JPanel(new FlowLayout());
		button = new JButton("Start Game");
		button.setAlignmentY(JButton.RIGHT_ALIGNMENT);
		button.setActionCommand("startgame");
		button.setSize(new Dimension(200, 30));
		button.addActionListener(this);
		southPanel.add(button);
		button = new JButton("Back");
		button.setAlignmentY(JButton.RIGHT_ALIGNMENT);
		button.setActionCommand("back");
		button.setSize(new Dimension(200, 30));
		button.addActionListener(this);
		southPanel.add(button);
		getContentPane().add(southPanel, BorderLayout.SOUTH);
		
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
        setUndecorated(true);
        setLocationRelativeTo(getRootPane());
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
		else if (e.getActionCommand().equals("changews")){
			JFileChooser chooser = new JFileChooser("resource/weapon_sets");
			chooser.setFileFilter(new FileFilter(){
				@Override
				public boolean accept(File f) {
					return f.isDirectory() || f.getName().endsWith(".ws");
				}
				@Override
				public String getDescription() {
					return "Weapon Set files";
				}
			});
			weaponSetChosen = null;
	        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	        	weaponSetChosen = chooser.getSelectedFile().getAbsolutePath();
	        	wdata.changeWS(weaponSetChosen);
	        	wdata.fireTableDataChanged();
	        	wsLabel.setText("\t     " + wdata.getWeaponSet());
	        }
	        else weaponSetChosen = WeaponLoader.DEFAULT_WEAPON_SET;
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
	        profile.setPrevNameSet(nameSetChosen.getAbsolutePath());
	        profile.setPrevWepSet(weaponSetChosen);
	        profile.setNumPlayersPerTeam(Integer.parseInt(numPlayersPerTeam.getText()));
	        profile.writeToFile();
	        control.startGame(profile, mapChosen, nameSetChosen, gameMode, teams, (compPlayersOnly ? -1 : team), weaponSetChosen, Integer.parseInt(numPlayersPerTeam.getText()));
			this.dispose();
		}
		else if(e.getActionCommand().equals("back")) {
			new MainMenu(control, profile);
			this.dispose();
		}
		else
		{
			if(!arrowbuttons.contains(e.getSource())) {
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
			else {
				if(e.getActionCommand().equals("arrowupteammates")) {
					if(Integer.parseInt(numPlayersPerTeam.getText())<12) numPlayersPerTeam.setText("" + (Integer.parseInt(numPlayersPerTeam.getText())+1));
				}
				else if(e.getActionCommand().equals("arrowdownteammates")) {
					if(Integer.parseInt(numPlayersPerTeam.getText())>1) numPlayersPerTeam.setText("" + (Integer.parseInt(numPlayersPerTeam.getText())-1));
				}
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
	
	private class PlayerTableModel extends AbstractTableModel {
		public String getColumnName(int index) {
			return "Players";
		}
		@Override
		public int getColumnCount() {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public int getRowCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if(rowIndex==0) return profile.getRankAndName();
			if(rowIndex==getRowCount()-1) return "Add Player";
			return null;
		}
		
	}
	private class WeaponTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private List<String> weaponSet;
		private String wepSet;
		public WeaponTableModel(String ws) {
			weaponSet = new ArrayList<String>();
			wepSet = ws;
			Scanner scan = null;
			try{
				scan = new Scanner(new File(ws));
				while (scan.hasNext()){
					String line = scan.nextLine();
					if (!line.startsWith(WeaponLoader.COMMENT_MARKER)){
						WeaponDefinition wepDef = new WeaponDefinition(line);
						if(!wepDef.getTypes().contains(WeaponType.OBJECTIVE)) {
							String wep = wepDef.getName() + " / " + wepDef.getRepresentativeChar();
							weaponSet.add(wep);
						}
					}
				}
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
		public String getColumnName(int index) {
			return "Weapon/Character on Map";
			
		}
		@Override
		public int getColumnCount() {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public int getRowCount() {
			// TODO Auto-generated method stub
			return weaponSet.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return weaponSet.get(rowIndex);
		}
		public void changeWS(String ws) {
			weaponSet = new ArrayList<String>();
			wepSet = ws;
			Scanner scan = null;
			try{
				scan = new Scanner(new File(ws));
				while (scan.hasNext()){
					String line = scan.nextLine();
					if (!line.startsWith(WeaponLoader.COMMENT_MARKER)){
						WeaponDefinition wepDef = new WeaponDefinition(line);
						if(!wepDef.getTypes().contains(WeaponType.OBJECTIVE)) {
							String wep = wepDef.getName() + " / " + wepDef.getRepresentativeChar();
							weaponSet.add(wep);
						}
					}
				}
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
		public String getWeaponSet() {
			return wepSet.substring((wepSet.lastIndexOf("/") != -1 ? wepSet.lastIndexOf("/") : wepSet.lastIndexOf("\\"))+1, wepSet.indexOf(".ws"));
		}
	}
}
