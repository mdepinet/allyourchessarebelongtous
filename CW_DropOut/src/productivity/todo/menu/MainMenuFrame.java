package productivity.todo.menu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

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

public class MainMenuFrame extends JFrame implements ActionListener, ListSelectionListener {
	private static final long serialVersionUID = -6659414234158999706L;
	private File mapChosen;
	private JTextField textField;
	private Shoot control;
	private GameMode gameMode;
	private JTable table;
	public MainMenuFrame(Shoot control) {
		super("Shoot Menu");
		this.control = control;
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
		button = new JButton("Start Game");
		button.setAlignmentY(JButton.RIGHT_ALIGNMENT);
		button.setActionCommand("startgame");
		button.setSize(new Dimension(200, 30));
		button.addActionListener(this);
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
		getContentPane().add(panel, BorderLayout.CENTER);
		getContentPane().add(button, BorderLayout.SOUTH);
		setVisible(true);
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
		}
		else if(e.getActionCommand().equals("startgame")) {
			control.startGame(mapChosen, gameMode);
			this.dispose();
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
