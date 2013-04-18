package com.compendium.ui;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableCellRenderer;

/**
 * The class renderers table headers for tables using TableSorter.
 */
public class UITableHeaderRenderer extends JLabel implements TableCellRenderer {
 	
	private static final long serialVersionUID = 7005634601532410299L;

	public UITableHeaderRenderer() {
    	super();
        setBorder(new BevelBorder(BevelBorder.RAISED));
        setHorizontalAlignment(SwingConstants.CENTER);
	}

    public Component getTableCellRendererComponent(JTable table, Object value,
                  boolean isSelected, boolean hasFocus, int row, int column) {

    	Font font = getFont();
    	
		int selectedcolumn = ((TableSorter)table.getModel()).getSelectedColumn();		
		if (selectedcolumn == column) {
			setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
			setForeground(IUIConstants.DEFAULT_COLOR);
			Object model = table.getModel();
			if (model instanceof TableSorter) {
				if (((TableSorter)model).getAscending()) {
					setIcon(UIImages.get(IUIConstants.UP_ARROW_ICON));
				} else {
					setIcon(UIImages.get(IUIConstants.DOWN_ARROW_ICON));
				}
			}
			
			this.setHorizontalTextPosition(SwingConstants.LEFT);							
		}
		else {
			setIcon(null);
			setFont(new Font(font.getName(), Font.PLAIN, font.getSize()));
			setBackground(this.getBackground());
			setForeground(table.getForeground());
		}

    	setValue(value);
        return this;
	}

    protected void setValue(Object value) {
    	setText((value == null) ? "" : value.toString());
    }
}
