package main;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import net.miginfocom.swing.MigLayout;
import static model.YAMLinterface.*;

public class PopupMenu extends JPopupMenu implements MouseListener {
    class ImageRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            Component component = (Component) value;
            Color selected = new Color(0.f, 0.f, 1.f, 0.5f);
            Color unselected = Color.WHITE;
            component.setBackground(isSelected ? selected : unselected);
            component.setForeground(isSelected ? unselected : selected);
            return component;
        }
    }

    protected JButton button;
    protected JList list;
   // protected CustomIcon[] images;
    protected  Component[] components;
    public PopupMenu(JButton button) {
        this.button = button;
        JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		panel.setLayout(new MigLayout("insets 6, gap 0", "[][][69.00]", "[][]"));
		this.add(panel, "cell 0 0,grow");
		
        makeMenu();
        setVisible(true);
        addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuCanceled(PopupMenuEvent e) {

            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                setVisible(true);
            }

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }
        });

    }

    protected void makeMenu() {	
    	Object selectedSim;
		ArrayList compounds= getSetCompounds(8,2,1);
		if (compounds!=null){
			for (int i=0;i<compounds.size();i++){
				JPanel panel = new JPanel();
				panel.setBackground(Color.LIGHT_GRAY);
				panel.setLayout(new MigLayout("insets 6, gap 0", "[][][69.00]", "[][]"));
				this.add(panel, "cell 0 "+i+",grow");
				
				String cName =  getCompoundName(8,2,1,i);
				cName = cName.replace(" ", "-");
				System.out.println(""+cName);
				
				JLabel label = new JLabel(cName);
				label.setIcon(new ImageIcon(Main.class.getResource("/resources/compoundsPng50/"+cName+".png")));
				panel.add(label, "cell 0 0 3 1,growx");
				
			}
		}	
      
    }
    protected void updateMenu() {
    	
        this.updateUI();
      }
    
    public void setColumnHeader(JScrollPane jsp, String header) {
        JViewport jvp = new JViewport();
        jvp.setView(new JLabel(header));
        jvp.setBackground(Color.YELLOW);
        jsp.setColumnHeader(jvp);
    }

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
