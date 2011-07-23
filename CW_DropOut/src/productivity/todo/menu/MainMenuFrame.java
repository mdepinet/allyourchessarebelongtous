package productivity.todo.menu;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import productivity.todo.control.Shoot;

public class MainMenuFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = -6659414234158999706L;
	private File mapChosen;
	private JTextField textField;
	private Shoot control;
	public MainMenuFrame(Shoot control) {
		super("Shoot Menu");
		this.control = control;
		mapChosen = new File("resource/maps/default.map");
		setBounds(new Rectangle(400,300,400,400));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout());
		p.setAlignmentY(JPanel.CENTER_ALIGNMENT);
		JLabel label = new JLabel("Pick a Map", SwingConstants.CENTER);
		label.setPreferredSize(new Dimension(300,20));
		p.add(label);
		textField = new JTextField("default.map");
		textField.setPreferredSize(new Dimension(250, 25));
		JButton button = new JButton("Browse...");
		button.setActionCommand("mapchooser");
		button.addActionListener(this);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		p.add(textField);
		p.add(button);
		button = new JButton("Start Game");
		button.setActionCommand("startgame");
		button.addActionListener(this);
		getContentPane().add(p);
		getContentPane().add(button);
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
			control.startGameWithMap(mapChosen);
			this.dispose();
		}
		
	}
}
