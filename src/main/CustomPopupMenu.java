package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;

import net.miginfocom.swing.MigLayout;

/**
 * This class implements a scrollable Popup Menu
 * @author balajihe
 *
 */
public class CustomPopupMenu extends JPopupMenu implements ActionListener {
	private static final long	serialVersionUID	= 1;
	private JPanel				panel			= new JPanel();
	private JScrollPane			scroll				= null;
	public static final Icon EMPTY_IMAGE_ICON = new ImageIcon("menu_spacer.gif");

	public CustomPopupMenu() {
		super();
		this.setLayout(new BorderLayout());
		panel.setLayout(new MigLayout("insets 0, gap 0", "[200.00,grow]", "[grow][grow][grow][grow][grow]"));
		panel.setBackground(UIManager.getColor("MenuItem.background"));
		//		panelMenus.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		init();
	}

	private void init() {
		super.removeAll();
		scroll = new JScrollPane();
		scroll.setViewportView(panel);
		scroll.setBorder(null);
		scroll.setMinimumSize(new Dimension(240, 40));

		scroll.setMaximumSize(new Dimension(340,650));
		super.add(scroll, BorderLayout.CENTER);
		//		super.add(scroll);
	}

	public void show(Component invoker, int x, int y, int h) {
		panel.validate();
		int maxsize = scroll.getMaximumSize().height;
		int realsize = panel.getPreferredSize().height;

		int sizescroll = 0;

		if (maxsize < realsize) {
			sizescroll = scroll.getVerticalScrollBar().getPreferredSize().width;
		}
		scroll.setMaximumSize(new Dimension(340,h));
		this.pack();
		this.setInvoker(invoker);
		if (sizescroll != 0) {
			//Set popup size only if scrollbar is visible
			this.setPopupSize(new Dimension(scroll.getPreferredSize().width + 20, scroll.getMaximumSize().height - 20));
		}
		//        this.setMaximumSize(scroll.getMaximumSize());
		Point invokerOrigin = invoker.getLocationOnScreen();
		this.setLocation((int) invokerOrigin.getX() + x, (int) invokerOrigin.getY() + y);
		this.setVisible(true);
	}

	public void hidemenu() {
		if (this.isVisible()) {
			this.setVisible(false);
		}
	}

	public void add(final CustomButton menuItem, int id) {
		//		menuItem.setMargin(new Insets(0, 20, 0 , 0));
		if (menuItem == null) {
			return;
		}
		
		final JPanel panel_2 = new JPanel();
		panel.add(panel_2, "cell 0 "+id);
		panel_2.setLayout(new MigLayout("insets 0, gap 0", "10[][][300.00]", "[][]"));
		panel_2.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
				panel_2.setBackground(CustomButton.MENU_HIGHLIGHT_BG_COLOR);
				menuItem.setForeground(CustomButton.MENU_HIGHLIGHT_FG_COLOR);
			}	
			public void mouseExited(MouseEvent e) {
				panel_2.setBackground(CustomButton.MENUITEM_BG_COLOR);
				menuItem.setForeground(CustomButton.MENUITEM_FG_COLOR);
			}

		});
		menuItem.addContainerPanel(panel_2);
		panel_2.add(menuItem, "cell 0 0");
		menuItem.removeActionListener(this);
		menuItem.addActionListener(this);
		if (menuItem.getIcon() == null) {
			menuItem.setIcon(EMPTY_IMAGE_ICON);
		}
	}
	
	private MouseAdapter getMouseAdapter() {
		return new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

		};
	}
	

	

	public void actionPerformed(ActionEvent e) {
		this.hidemenu();
	}

	public Component[] getComponents() {
		return panel.getComponents();
	}

	

}
