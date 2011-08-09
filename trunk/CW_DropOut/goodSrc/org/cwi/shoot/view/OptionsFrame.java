package org.cwi.shoot.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.cwi.shoot.control.Shoot;
import org.cwi.shoot.menu.MainMenu;
import org.cwi.shoot.profile.Profile;

public class OptionsFrame extends JFrame implements ActionListener, ListSelectionListener, KeyListener {
	private static final long serialVersionUID = 1L;
	private Shoot control;
	private JPanel buttonPanel;
	private JPanel panel;
	private ArrayList<JButton> buttonGroup;
	private String[] profiles;
	private JTable table;
	private JButton createNewProf;
	private JPanel pPanel;
	private TableModel data;
	private Profile profile;
	private JTable profileTable;
	private TableModel profileData;
	private String prevFrame;
	public OptionsFrame(Shoot control, String prevFrame) {
		super("Options");
		
		this.control = control;
		this.prevFrame = prevFrame;
		
		setBounds(new Rectangle(800,600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel cPane = new JPanel(new BorderLayout());
		cPane.setBorder(new LineBorder(Color.BLACK, 5));
		setContentPane(cPane);
		getContentPane().setLayout(new BorderLayout());
		
		
		data = new ProfileNameTableModel();
		table = new JTable(data);
		pPanel = new JPanel(new BorderLayout());
		JScrollPane pane = new JScrollPane(table);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(this);
		pane.setPreferredSize(new Dimension(150, 200));
		//table.setRowSelectionInterval(0, 0);
		table.setRowHeight(30);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setTableHeader(null);
		table.addKeyListener(this);
		createNewProf = new JButton("Create new profile");
		createNewProf.addActionListener(this);
		createNewProf.setActionCommand("newprofile");
		pPanel.add(pane, BorderLayout.CENTER);
		pPanel.add(createNewProf, BorderLayout.SOUTH);
		
		
		
		buttonGroup = new ArrayList<JButton>();
		panel = new JPanel(new FlowLayout());
		buttonPanel = new JPanel(new FlowLayout());
		JButton button = new JButton("Back", new ImageIcon(MainMenu.IMG_LOC + "MenuButton" + ".png"));
		button.setVerticalTextPosition(JButton.BOTTOM);
		button.setHorizontalTextPosition(JButton.CENTER);
		button.setPreferredSize(new Dimension(185,100));
		button.setForeground(Color.BLACK);
		button.setBackground(Color.WHITE);
		button.setFocusPainted(true);
		button.setActionCommand("back");
		button.addActionListener(this);
		button.setOpaque(false);
		panel.add(button);
		buttonGroup.add(button);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.setPreferredSize(new Dimension(50,300));
		profile = getProfileNames().size()==0 ? null : new Profile(getProfileNames().get((table.getSelectedRow()==-1 ? 0 : table.getSelectedRow())).substring(0, getProfileNames().get((table.getSelectedRow()==-1 ? 0 : table.getSelectedRow())).indexOf(".pprf")));
		profileData = new ProfileTableModel(profile);
		profileTable = new JTable(profileData);
		profileTable.clearSelection();
		profileTable.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainPanel.add(profileTable);
		
		JPanel ssPanel = new JPanel(new FlowLayout());
		JTextField width = new JTextField(profile.getScreenSize()==null ? 750 : profile.getScreenSize().x);
		width.setPreferredSize(new Dimension(25,25));
		JTextField height = new JTextField(profile.getScreenSize()==null ? 750 : profile.getScreenSize().y);
		height.setPreferredSize(new Dimension(25,25));
		JLabel ssLabel = new JLabel("Width");
		ssPanel.add(ssLabel);
		ssPanel.add(width);
		ssLabel = new JLabel("Height");
		ssPanel.add(ssLabel);
		ssPanel.add(height);
		ssPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
		//mainPanel.add(ssPanel);
		
		getContentPane().add(pPanel, BorderLayout.WEST);
		getContentPane().add(panel, BorderLayout.SOUTH);
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		setLocationRelativeTo(getRootPane());
		setUndecorated(true);
		setVisible(true);
	}
	
	public static List<String> getProfileNames() {
		List<String> filenames = new ArrayList<String>();
		File directory = new File(Profile.PROFILE_LOCATION);
		File[] allFiles = directory.listFiles();
		for(File f : allFiles)
			if(f.getName().contains(".pprf"))
				filenames.add(f.getName());
		
		return filenames;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("back")) {
			if(!prevFrame.equals("pause")) new MainMenu(control, profile);
//			menu.setProfile(new Profile(getProfileNames().get((table.getSelectedRow()==-1 ? 0 : table.getSelectedRow())).substring(0, getProfileNames().get((table.getSelectedRow()==-1 ? 0 : table.getSelectedRow())).indexOf(".pprf"))));
			this.dispose();
		}
		else if(e.getActionCommand().equals("newprofile")) {
			new ProfileFrame();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		profile = new Profile(getProfileNames().get((table.getSelectedRow()==-1 ? 0 : table.getSelectedRow())).substring(0, getProfileNames().get((table.getSelectedRow()==-1 ? 0 : table.getSelectedRow())).indexOf(".pprf")));
		((ProfileTableModel)profileData).changeProfile(profile);
//		profileTable = new JTable(profileData);
		((AbstractTableModel)profileData).fireTableDataChanged();
	}

	private class ProfileFrame extends JFrame implements KeyListener {
		private static final long serialVersionUID = 1L;
		private JTextField tf;
		public ProfileFrame() {
			JPanel panel = new JPanel(new FlowLayout());
			JLabel label = new JLabel("Enter Name: ");
			tf = new JTextField();
			tf.setPreferredSize(new Dimension(100,22));
			tf.addKeyListener(this);
			panel.add(label);
			panel.add(tf);
			
			getContentPane().add(panel);
			
			setBounds(new Rectangle(200,25));
			setLocationRelativeTo(createNewProf);
			setUndecorated(true);
			setVisible(true);
		}
		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode()==KeyEvent.VK_ENTER) {
				File file = new File(Profile.PROFILE_LOCATION + tf.getText() + ".pprf");
				if(file.exists()) {
					JOptionPane.showMessageDialog(tf, "Profile exists already", "Cannot create profile", 1);
					return;
				}
				try {
					file.createNewFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				this.dispose();
				((AbstractTableModel)data).fireTableDataChanged();
			}
		}
		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private class ProfileNameTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;

		public ProfileNameTableModel() {
			
		}
		
		public int getRowCount() {
			return getProfileNames().size();
		}

		@Override
		public int getColumnCount() {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			// TODO Auto-generated method stub
			return getProfileNames().get(rowIndex).substring(0, getProfileNames().get(rowIndex).indexOf(".pprf"));
		}
		
	}
	private class ProfileTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private Profile prof;
		private int cols;

		public ProfileTableModel(Profile profile) {
			prof = profile;
			cols = 2;
		}
		
		public int getRowCount() {
			if(prof==null) return 0;
			return prof.getData().keySet().size()+1;
		}

		@Override
		public int getColumnCount() {
			// TODO Auto-generated method stub
			return cols;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			// TODO Auto-generated method stub
			if(rowIndex==0 && columnIndex==0) return prof.getRankAndName(); 
			//if(rowIndex==1) return (columnIndex==0 ? "Total score:" : prof.getScore());
			int i = 1;
			for(Map.Entry<String, Object> ent : prof.getData().entrySet()) {
				if(i==rowIndex) {
					if(columnIndex==0) return ent.getKey();
					if(ent.getValue() instanceof List)  {
						for(int j = cols; j < ((List)ent.getValue()).size()+1; j++) {
							profileTable.addColumn(new TableColumn(j));
							((AbstractTableModel)this).fireTableDataChanged();
							profileTable.convertColumnIndexToModel(j);
						}
						cols = ((List)ent.getValue()).size()+1;
						return ((List)ent.getValue()).get(columnIndex-1);
					}
					if(columnIndex>1) return null;
					return ent.getValue();
				}
				i++;
			}
			return null;
		}
		
		public void changeProfile(Profile p) {
			prof = p;
		}
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_DELETE) {
			if(table.hasFocus() && table.getSelectedRow()!=-1) {
				int choice = JOptionPane.showConfirmDialog(table, "Are you sure you wish to delete this profile?\nProfile Name: " + getProfileNames().get(table.getSelectedRow()).substring(0,getProfileNames().get(table.getSelectedRow()).indexOf(".pprf")), "Confirm", 0);
				if(choice==0) {
					File file = new File(Profile.PROFILE_LOCATION + getProfileNames().get(table.getSelectedRow()));
					file.delete();
					((AbstractTableModel)data).fireTableDataChanged();
				}
			}
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
