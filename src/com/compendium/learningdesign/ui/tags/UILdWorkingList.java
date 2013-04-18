/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2009 Verizon Communications USA and The Open University UK    *
 *                                                                              *
 *  This software is freely distributed in accordance with                      *
 *  the GNU Lesser General Public (LGPL) license, version 3 or later            *
 *  as published by the Free Software Foundation.                               *
 *  For details see LGPL: http://www.fsf.org/licensing/licenses/lgpl.html       *
 *               and GPL: http://www.fsf.org/licensing/licenses/gpl-3.0.html    *
 *                                                                              *
 *  This software is provided by the copyright holders and contributors "as is" *
 *  and any express or implied warranties, including, but not limited to, the   *
 *  implied warranties of merchantability and fitness for a particular purpose  *
 *  are disclaimed. In no event shall the copyright owner or contributors be    *
 *  liable for any direct, indirect, incidental, special, exemplary, or         *
 *  consequential damages (including, but not limited to, procurement of        *
 *  substitute goods or services; loss of use, data, or profits; or business    *
 *  interruption) however caused and on any theory of liability, whether in     *
 *  contract, strict liability, or tort (including negligence or otherwise)     *
 *  arising in any way out of the use of this software, even if advised of the  *
 *  possibility of such damage.                                                 *
 *                                                                              *
 ********************************************************************************/

package com.compendium.learningdesign.ui.tags;

import javax.swing.table.TableColumn;

import com.compendium.ui.UITableHeaderRenderer;
import com.compendium.ui.tags.UIWorkingList;
import com.compendium.ui.tags.UIWorkingList.CellRenderer;

/**
 * @author ajb785
 *
 */
public class UILdWorkingList extends UIWorkingList {

	/**
	 * 
	 */
	public UILdWorkingList() {
		super();
	}

	/**
	 * Set the header renderers for the table column headers and the table cells.
	 */
    public void setRenderers() {
    	int count = table.getModel().getColumnCount();
    	
        for (int i = 0; i < count; i++) {
        	TableColumn aColumn = table.getColumnModel().getColumn(i);
        	
        	// Set the cell renderer for the column headers
        	UITableHeaderRenderer headerRenderer = new UITableHeaderRenderer();
            aColumn.setHeaderRenderer(headerRenderer);
            
            // Set the cell renderer for column cells
            UIWorkingList oWorkingList = new UIWorkingList();
 //           UIWorkingList.CellRenderer cellRenderer = oWorkingList.new CellRenderer();
  //          aColumn.setCellRenderer(cellRenderer);
    	}
 	}
	
}
