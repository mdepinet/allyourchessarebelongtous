package productivity.chess.view;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import productivity.chess.control.networking.ServerThread;

public class PlayerPicker extends JFrame implements ActionListener, ListSelectionListener {
	private JTable table;
	private JButton button;
	List<ServerThread> threads = new LinkedList<ServerThread>();
	private Socket selected = null;
	public static final String[] ips = {"/10.0.0.103","/10.0.0.96"};
	public PlayerPicker()
	{
		super("Pick Your Opponent");
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		table = new JTable(new MyTableModel());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(this);
		table.setRowHeight(50);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		getContentPane().add(table);
		button = new JButton("Play");
		button.setEnabled(false);
		button.setPreferredSize(new Dimension(300,45));
		button.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		button.addActionListener(this);
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		getContentPane().add(button);
		this.setResizable(false);
		this.setBounds(500, 400, 300, 270);
		this.setVisible(true);
	}
	public void addClient(ServerThread thread)
	{
		threads.add(thread);
		for(int i = 0; i < ips.length;i++)
		{
			if(ips[i].equals(thread.getClient().getInetAddress().toString()))
				table.getModel().setValueAt(true, i, 1);
			repaint();
		}
	}
	public JTable getTable() {
		return table;
	}
	public void setTable(JTable table) {
		this.table = table;
	}
	public void valueChanged(ListSelectionEvent arg0) {
		if(table.getSelectedRow()!=-1 && (Boolean)table.getModel().getValueAt(table.getSelectedRow(), 1))
			button.setEnabled(true);
		else
			button.setEnabled(false);
		
	}
	public Socket getSelected() {
		return selected;
	}
	public void setSelected(Socket selected) {
		this.selected = selected;
	}
	public void actionPerformed(ActionEvent e) {
		for(ServerThread s: threads)
			if(s.getClient().getInetAddress().toString().equals(ips[table.getSelectedRow()]))
			{
				s.start();
				this.dispose();
			}
	}
	
	class MyTableModel extends AbstractTableModel {
	    private String[] columnNames = {"Player", "Connected"};
	    private Object[][] data = {{"Dillon", false}, {"Ian", false}};

	    public int getColumnCount() {
	        return columnNames.length;
	    }

	    public int getRowCount() {
	        return data.length;
	    }

	    public String getColumnName(int col) {
	        return columnNames[col];
	    }

	    public Object getValueAt(int row, int col) {
	        return data[row][col];
	    }

	    public Class getColumnClass(int c) {
	        return getValueAt(0, c).getClass();
	    }

	    /*
	     * Don't need to implement this method unless your table's
	     * editable.
	     */
	    public boolean isCellEditable(int row, int col) {
	        return false;
	    }

	    public void setValueAt(Object value, int row, int col) {
	        data[row][col] = value;
	        fireTableCellUpdated(row, col);
	    }
	}
	public static void main(String[] args)
	{
		new PlayerPicker();
	}
	
}
