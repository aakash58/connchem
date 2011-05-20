package main;

import javax.swing.table.AbstractTableModel;
import java.awt.Color;

public class OrderVarTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private String[] columnNames = {};
	private int rowNum = 1;
	private int colNum = 0;
	private Object[][] data;

	public OrderVarTableModel() {
		colNum = 3;
		data = new Object[rowNum][colNum];
		columnNames = new String[colNum];
		for (int i = 0; i < colNum; i++) {
			data[0][i] = new Boolean(true);
		}

		for (int i = 0; i < colNum; i++) {
			columnNames[i] = "A"+i;
		}
	}

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

	public boolean isCellEditable(int row, int col) {
		return true;
	}

	public void setValueAt(Object value, int row, int col) {
		data[row][col] = value;
		fireTableCellUpdated(row, col);
		boolean v = Boolean.valueOf(value.toString());
	}
}
