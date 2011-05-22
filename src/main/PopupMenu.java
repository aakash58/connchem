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
    protected  String[] moleculeNames;
    protected JPanel moleculePanel;
    
    public PopupMenu(JButton button, String[] moleculeFiles) {
    	moleculeNames = parseNames(moleculeFiles);
    	
    	this.button = button;
    	
    	
    	JPanel p = new JPanel();
		this.add(p, "cell 0 1,grow");
		p.setLayout(new CardLayout(0, 0));

		JScrollPane legendScrollContainer_1 = new JScrollPane();
		p.add(legendScrollContainer_1, "name_1303765324750467000");
		
		moleculePanel = new JPanel();
		legendScrollContainer_1.setViewportView(moleculePanel);
		moleculePanel.setLayout(new MigLayout("insets 6", "[174.00,grow]", "[53.00,grow][grow][grow][grow][grow]"));
		
		for (int i=0;i<moleculeNames.length;i++){
    		String cName =  moleculeNames[i];
			cName = cName.replace(" ", "-");
			
			JLabel label = new JLabel(cName);
			label.setIcon(new ImageIcon(Main.class.getResource("/resources/compoundsPng50/"+cName+".png")));
			moleculePanel.add(label, "cell 0 " + i + "3 1,growx");
			
		}

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

    protected String[] parseNames(String[] files) {
    	int numMolecules = 0;
    	for (int i=0;i<files.length;i++){
    		if (files[i].endsWith(".png")){
    			numMolecules++;
    		}
    	}
    	String[] moleculeNames = new String[numMolecules];
    	int count =0;
    	for (int i=0;i<files.length;i++){
    		if (files[i].endsWith(".png")){
    			moleculeNames[count] = files[i].split(".png")[0];   
    			count++;
    		}
    	}
    	return moleculeNames;
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
