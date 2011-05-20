package main;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import java.awt.*;

public class OrderVarTable extends JPanel {
	public static JTable table = null;
	public static OrderVarTableModel myTable;
	public static boolean firstTime = true;

	public OrderVarTable() {
		super(new BorderLayout());
		if (firstTime) {
			myTable = new OrderVarTableModel();
			firstTime = false;
			table = new JTable(myTable);
		}
		JScrollPane scrollPane = new JScrollPane(table);
		JScrollBar jj = new JScrollBar();
		jj.setOrientation(JScrollBar.HORIZONTAL);
		// scrollPane.getViewport().setViewPosition(new java.awt.Point(10000,
		// 0));

		scrollPane.setHorizontalScrollBar(jj);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setColumnSelectionAllowed(true);
		table.setRowSelectionAllowed(false);

		table.getColumnModel().addColumnModelListener(new ColumnListener());
		// Set up renderer and editor for the Favorite Color column.
	//	table.setDefaultRenderer(Color.class, new ColorRenderer(true));
	//	table.setDefaultEditor(Color.class, new ColorEditor());
		// table.setPreferredSize(new Dimension(285,65));
		// table.getColumnModel().getColumn(0).setPreferredWidth(124);
		// table.getColumnModel().getColumn(1).setPreferredWidth(80);
		// table.getColumnModel().getColumn(2).setPreferredWidth(80);

		add(scrollPane, BorderLayout.NORTH);
		this.setPreferredSize(new Dimension(900, 40));
	}

	private class ColumnListener implements TableColumnModelListener {
		public void columnAdded(TableColumnModelEvent e) {
			// TODO Auto-generated method stub

		}

		public void columnMarginChanged(ChangeEvent e) {
			// TODO Auto-generated method stub

		}

		public void columnMoved(TableColumnModelEvent e) {
			for (int i = 0; i < myTable.getColumnCount(); i++) {
			}
		}

		public void columnRemoved(TableColumnModelEvent e) {
			// TODO Auto-generated method stub

		}

		public void columnSelectionChanged(ListSelectionEvent e) {
			// TODO Auto-generated method stub

		}
	}
}
