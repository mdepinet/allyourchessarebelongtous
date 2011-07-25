package productivity.todo.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
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

import productivity.todo.config.GameMode;
import productivity.todo.config.TeamDeathmatchMode;
import productivity.todo.control.Shoot;
import productivity.todo.model.GameMap;
import productivity.todo.model.Weapon;

public class MainMenuFrame extends JFrame implements ActionListener, ListSelectionListener {
	private static final long serialVersionUID = -6659414234158999706L;
	private File mapChosen;
	private JTextField textField;
	private ArrayList<JButton> buttonGroup;
	private Shoot control;
	private int team;
	private GameMode gameMode;
	private JTable table;
	public MainMenuFrame(Shoot control) {
		super("Shoot Menu");
		team = 1;
		this.control = control;
		buttonGroup = new ArrayList<JButton>();
		gameMode = new TeamDeathmatchMode();
		mapChosen = new File("resource/maps/default.map");
		setBounds(new Rectangle(400,300,400,400));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel filePane = new JPanel();
		filePane.setPreferredSize(new Dimension(400, 60));
		filePane.setLayout(new FlowLayout());
		filePane.setAlignmentY(JPanel.CENTER_ALIGNMENT);
		JLabel label = new JLabel("Pick a Map", SwingConstants.CENTER);
		label.setPreferredSize(new Dimension(300,20));
		filePane.add(label);
		textField = new JTextField("default.map");
		textField.setPreferredSize(new Dimension(250, 25));
		JButton button = new JButton("Browse...");
		button.setActionCommand("mapchooser");
		button.addActionListener(this);
		getContentPane().setLayout(new BorderLayout());
		filePane.add(textField);
		filePane.add(button);
		getContentPane().add(filePane, BorderLayout.NORTH);
		String[] columnNames = {"Select a Mode"};
		String[][] modeNames = new String[GameMode.modes.length][1];
		for(int i = 0; i < GameMode.modes.length;i++)
		{
			try {
				modeNames[i][0] = (String)GameMode.modes[i].getMethod("getModeName").invoke(GameMode.modes[i].newInstance());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
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
		for(c = 'L'; c <= (int)('O');c++)
		{
			button = new JButton(GameMap.teamNames[c-76], new ImageIcon(new Weapon(c, new Point()).getImage()));
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
	        else mapChosen = new File("resource/maps/default.map");
	        textField.setText(mapChosen.getName());
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
			control.startGame(mapChosen, gameMode, teams, team);
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
		try {
			gameMode = (GameMode) GameMode.modes[table.getSelectedRow()].newInstance();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
	}
}
