package main;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.ListSelectionModel;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TableView extends JPanel {
	public JTable table = null;
	public boolean stopUpdating = false;
	public JScrollPane scrollPane;
	public ArrayList[] data = new ArrayList[3];
	private int sat =222;
	public Color[] colors; 
	private int[] selectedRows;
	public int colorChangingRow;
	//public int selectedRow=-1;
	private Main main;
	MyTableModel myTable;
	
	
	public TableView(Main parent) {
		super(new GridLayout(1, 0));
		this.main = parent;
		 myTable = new MyTableModel();
		
		
		table = new JTable(myTable);
	    scrollPane = new JScrollPane(table);
		JScrollBar jj = new JScrollBar();
		jj.setOrientation(JScrollBar.HORIZONTAL);
		scrollPane.getViewport().setViewPosition(new java.awt.Point(0, 0));

		scrollPane.setHorizontalScrollBar(jj);
		//table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.getSelectionModel().addListSelectionListener(new RowListener());
		/*table.addMouseListener(new MouseAdapter()
		{
		   public void mouseClicked(MouseEvent evt)
		   {
				if(evt.getSource() == table)
				{
				
					int [] selectedRows = table.getSelectedRows();
					evt.get
			
				}
		   }
		});*/
		table.getColumnModel().getColumn(0).setPreferredWidth(10);
		table.getColumnModel().getColumn(1).setPreferredWidth(50);
		table.getColumnModel().getColumn(2).setPreferredWidth(130);
		table.setSelectionBackground(Color.GRAY);//new Color(40,60,220));
		
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(SwingConstants.RIGHT);
		table.getColumnModel().getColumn(0).setCellRenderer(dtcr);
		
		
		// Set up renderer and editor for the Favorite Color column.
		table.setDefaultRenderer(Color.class, new ColorRenderer(true));
		table.setDefaultEditor(Color.class, new ColorEditor(this));
		// Add the scroll pane to this panel.
		add(scrollPane);
		Color c = new Color(245,245,245);
		scrollPane.getViewport().setBackground(c);
		table.setBackground(c);
		
		colors = new Color[main.getCanvas().MAXCOMPOUND];
		colors[0]= new Color(255,0,0,sat);
		colors[1]= new Color(0,255,0,sat);
		colors[2]= new Color(0,0,255,sat);
		colors[3]= new Color(255,255,0,sat);
		colors[4]= new Color(0,255,255,sat);
		colors[5]= new Color(255,0 ,255,sat);
		colors[6]= Color.PINK;
		colors[7]= Color.ORANGE;
		for (int i = 8;i<main.getCanvas().MAXCOMPOUND;i++){
			colors[i] = Color.BLACK;
		}
		
		
	}

    private class RowListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            int [] selectedRows = table.getSelectedRows();
            //output.append("ROW SELECTION EVENT. ");
            //outputSelection();
        }
    }

	public Color getColor(int index) {
		if (index<colors.length){
			return colors[index];
		}
		return Color.BLACK;
	}
	
	public void setSelectedRow(int [] rows) {
		selectedRows = rows;
		table.clearSelection();
		if (selectedRows!=null && selectedRows.length>0){
		for( int row:selectedRows)
		{
			table.addRowSelectionInterval(row, row);
		}
		table.updateUI();
		}
	}
	public boolean addSelectedRow( int index)
	{
		if(selectedRowsContain(index)) //If this row has been selected
			return false;
		else
		{
			if(index>=0 && index<table.getRowCount())
			{
				int [] tempRows = selectedRows.clone();
				selectedRows = new int [selectedRows.length+1];
				int i = 0;
				for( i = 0;i<tempRows.length;i++)
				{
					selectedRows[i] = tempRows[i];
				}
				selectedRows[i] = index;
				return true;
			}
			return false;
		}
		
	}
	public void deselectRow(int [] rows)
	{
		if(selectedRows!=null)
		{
			if(selectedRows.length>0)
			{
				List newSelectedRows = new LinkedList<Integer>();
				List deselectRows = new LinkedList<Integer>();
				for(int deselectRow:rows) //Translate Rows to list
				{
					deselectRows.add(deselectRow);
				}
				
				for(int rowIndex:selectedRows) //Remove those rows which we want to deselect
				{
					if(!deselectRows.contains(rowIndex))
						newSelectedRows.add((Integer)rowIndex);
				}
				//Make a new int array
				selectedRows = new int[newSelectedRows.size()];
				for (int i = 0; i < newSelectedRows.size(); i++) {
					selectedRows[i] = ((Integer)newSelectedRows.get(i)).intValue();
				}
			}
		}
	}
	public boolean selectedRowsContain(int row)
	{
		boolean res = false;
		if(selectedRows!=null)
			if(selectedRows.length>0)
				for( int r:selectedRows)
				{
					if(r == row)
					{
						res = true;
						break;
					}
				}
		return res;
	}

	public boolean selectedRowsIsEmpty()
	{
		if(selectedRows!=null)
			if(selectedRows.length>0)
				return false;
		return true;
	}
	public int selectedRowsCount()
	{
		return selectedRows.length;
	}
	public int [] getSelectedRows()
	{
		return selectedRows.clone();
	}
	
	public boolean contains(String name)
	{
		return (indexOf(name)==-1)?false:true;
	}

	public int indexOf(String name)
	{
		String moleculeName = null;
		for(int i =0;i<myTable.getRowCount();i++)
		{
			moleculeName = new String((String) myTable.getValueAt(i, 2)); //col is molecule name
			if(moleculeName.equals(name))
			{
				return i;
			}
		}
		return -1;
	}
	public void clearSelection()
	{
		table.clearSelection();
	}
	class MyTableModel extends AbstractTableModel {
		private String[] columnNames = {};
		
		public MyTableModel() {
			data[0] = new ArrayList<Integer>();
			data[1] = new ArrayList<Color>();
			data[2] = new ArrayList<String>();

			columnNames = new String[3];
			columnNames[0] = "    #";
			columnNames[1] = "Color";
			columnNames[2] = "Molecule";
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data[0].size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return data[col].get(row);
		}

		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			if (col == 1) {
				return true;
			} else {
				return false;
			}
		}

		public void setValueAt(Object value, int row, int col) {
			data[col].set(row, value);
			colors[row] = (Color) value;
			fireTableCellUpdated(row, col);
		}
	}

	public Main getMain() {
		// TODO Auto-generated method stub
		return main;
	}

}
