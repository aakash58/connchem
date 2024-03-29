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

import simulations.models.Compound;

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
		//table.getSelectionModel().addListSelectionListener(new RowListener());
		table.addMouseListener(new MouseAdapter()
		{
		   public void mouseReleased(MouseEvent evt)
		   {
			   if(evt.getSource() == table)
               {
               
               	int [] tempRows = table.getSelectedRows();
               	if(tempRows.length==1) //If there is only one Selected Row, select it or deselect it
               	{
               		if(selectedRows==null || !selectedRowsContain(tempRows[0]))
               		{
               			selectedRows = tempRows;
               		}
               		else
               		{
               			selectedRows = null;
               			table.clearSelection();
               		}
               	}
               	else //If there is multiple rows get selected
               	{
               		selectedRows = table.getSelectedRows();
               	}
               	}
		   }
		});
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
	
	//Update tableView, which is presenting molecule legends below chart
	public void updateTableView(){
		
		data[0].clear();
		data[1].clear();
		data[2].clear();
		
		for (int i=0; i<Compound.names.size();i++){
			data[0].add((Integer)Compound.counts.get(i));
			data[1].add((Color)colors[i]);
			data[2].add((String)Compound.names.get(i));
		}
		if (this!=null && !stopUpdating){
			this.table.updateUI();
		}		
	}


	public Color getColor(int index) {
		if (index<colors.length){
			return colors[index];
		}
		return Color.BLACK;
	}
	/*
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
	}*/

	public boolean addSelectedRow( int [] rows)
	{
		if( rows==null || rows.length<=0)
			return false;
		else
		{
				if(selectedRows!=null)
				{
					List<Integer> tempRows = new LinkedList<Integer>();
					for(int index:selectedRows)
						tempRows.add(index);
					for(int addIndex:rows)
						if(!tempRows.contains(addIndex))
							tempRows.add(addIndex);
					selectedRows = new int [tempRows.size()];
					int i = 0;
					for( i = 0;i<tempRows.size();i++)
					{
						selectedRows[i] = tempRows.get(i).intValue();
					}
				}
				else //selectedRows ==null
				{
					selectedRows = new int [rows.length];
					for(int i =0;i<rows.length;i++)
						selectedRows[i] = rows[i];
				}
				table.clearSelection();
				for( int selectedIndex:selectedRows)
				table.getSelectionModel().addSelectionInterval(selectedIndex, selectedIndex);
				
				return true;
		}
		
	}

	public void deselectRows(int [] rows)
	{
		if(selectedRows!=null)
		{
			if(selectedRows.length>0)
			{
				List<Integer> newSelectedRows = new LinkedList<Integer>();
				List<Integer> deselectRows = new LinkedList<Integer>();
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
				
				//Clear table selection
				table.getSelectionModel().clearSelection();
				//Add selected rows to table
				for( int i = 0;i<selectedRows.length;i++)
				{
					//table.setRowSelectionInterval(selectedRows[i], selectedRows[i]);
					table.getSelectionModel().addSelectionInterval(selectedRows[i], selectedRows[i]);
				}
				
			}
		}
	}
	public void selectAllRows()
	{
		table.selectAll();
		selectedRows = table.getSelectedRows();
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
	
	//Return molecule name when that molecule is being selected on legends
	public String [] getSelectedMolecule(){
		if(!selectedRowsIsEmpty())
		{
			int [] selectedRows = getSelectedRows();
			String [] molecules = new String [selectedRows.length];
			
			for(int i = 0;i<selectedRows.length;i++)
			{
				if(selectedRows[i]<Compound.names.size())
				molecules[i] = new String(Compound.names.get(selectedRows[i]));
			}
			
			return molecules;
		}
		return null;
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
	public void increaseRowCount(int index, int count)
	{
		
		if(index>=0 && index<data[0].size())
		{
			Integer newCount = new Integer(((Integer)data[0].get(index)).intValue()+count);
			if(newCount.intValue()<0)
				newCount=0;
			data[0].set(index, newCount);
		}
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
		this.selectedRows = null;
	}
	
	public int getIndexByName(String compoundName)
	{
		return data[2].indexOf(compoundName);
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
