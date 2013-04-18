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

package com.compendium.learningdesign.ui.dialogs;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.compendium.learningdesign.ui.UILdViewPane;
import com.compendium.learningdesign.ui.panels.UILdActivityTimesPanel;
import com.compendium.learningdesign.ui.panels.UILdTaskTimesTable;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.compendium.ui.dialogs.UIDialog;
import com.compendium.learningdesign.ui.*;

public class UILdActivityTimesDialog extends UIDialog {
	private JPanel oContentPanel;
	private UILdTaskTimesTable oTaskTimesTable;

	public UILdActivityTimesDialog(JFrame parent, boolean modal) {
		super(parent, modal);
		oTaskTimesTable = new UILdTaskTimesTable();
		initGUI();
	}

	public UILdActivityTimesDialog() {
		// TODO Auto-generated constructor stub
	}

	public UILdActivityTimesDialog(JDialog parent, boolean modal) {
		super(parent, modal);
		// TODO Auto-generated constructor stub
	}
/** Need to fix this  - make superclass JInternalFrame ???
	public UILdActivityTimesDialog(UILdViewPane owner, boolean modal)	{
		super(owner, modal);
		// this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		initGUI();
	}

	public UILdActivityTimesDialog(UILdActivityViewFrame owner, boolean modal)	{
		super(owner, modal);
		// this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		initGUI();
	}
	**/	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(300, 300);
	//	frame.getContentPane().add(new UILdActivityTimesDialog(frame, false));
		new UILdActivityTimesDialog(frame, false);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	}
	/**
	* This method should return an instance of this class which does 
	* NOT initialize it's GUI elements. This method is ONLY required by
	* Jigloo if the superclass of this class is abstract or non-public. It 
	* is not needed in any other situation.
	 */
	public static Object getGUIBuilderInstance() {
		return new UILdActivityTimesDialog(Boolean.FALSE);
	}
	
	/**
	 * This constructor is used by the getGUIBuilderInstance method to
	 * provide an instance of this class which has not had it's GUI elements
	 * initialized (ie, initGUI is not called in this constructor).
	 */
	public UILdActivityTimesDialog(Boolean initGUI) {
		super();
	}
	
	private JPanel getContentPanel() {
		if(oContentPanel == null) {
			oContentPanel = new JPanel();
		}
		return oContentPanel;
	}
	
	public UILdTaskTimesTable getOTaskTimesTable() {
		if(oTaskTimesTable == null) {
			TableModel oTaskTimesTableModel = 
				new DefaultTableModel(
						new String[][] { { "One", "Two" }, { "Three", "Four" } },
						new String[] { "Column 1", "Column 2" });
			oTaskTimesTable = new UILdTaskTimesTable();
			oTaskTimesTable.setModel(oTaskTimesTableModel);
		}
		return oTaskTimesTable;
	}

	private void initGUI() {
		try {
			
			{
				this.setTitle("Timing info");
				oContentPanel = new JPanel(); 
				
				JButton pbClose = new JButton("OK");
				oContentPanel.add(pbClose);
				pbClose.setSize(21, 21);
				pbClose.setPreferredSize(new java.awt.Dimension(21, 21));
				pbClose.setToolTipText("Click to hide timing information");
				pbClose.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						//System.out.println("pbClose.actionPerformed, event="+evt);
						setVisible(false);
					}
				});
				
				//oTaskTimesTable.setPreferredSize(new java.awt.Dimension(150, 15));
//				this.setTitle("Timing info");
				JPanel oTablePanel = new JPanel(); 
				oTablePanel.add(oTaskTimesTable.getTableHeader());
				oTablePanel.add(oTaskTimesTable);
				oTablePanel.setBorder(new LineBorder(new java.awt.Color(0,0,255), 1, false));
				this.add(oTablePanel);
				//this.setPreferredSize(new java.awt.Dim	ension(150, 50));
				//this.setLocation(0, 0);
				this.setMinimumSize(new java.awt.Dimension(216, 50));
		//		this.setOpaque(true);
				
				this.setPreferredSize(new java.awt.Dimension(230, 70));
				
	//			this.setBorder(new LineBorder(new java.awt.Color(0,0,255), 2, false));
				
			//	this.setVisible(true);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
