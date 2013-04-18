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

package com.compendium.learningdesign.ui.panels;

import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;
import javax.swing.*;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.border.LineBorder;

import com.compendium.learningdesign.ui.panels.UILdTaskTimesTable;
import com.compendium.learningdesign.ui.panels.nodecontent.UILdNodeLearningOutcomesPanel;
import com.compendium.learningdesign.ui.UILdActivityViewFrame;
import com.compendium.learningdesign.ui.UILdViewPane;
import com.compendium.ui.ProjectCompendiumFrame;
import com.compendium.ui.dialogs.UIDialog;
import com.compendium.ProjectCompendium;
/**
 * Class UILdActivityTimesDialog displays the learner and tutor task times
 * for a learning activity.
 * 
 * @author ajb785
 *
 */
public class UILdActivityTimesPanel extends JPanel {
	/**	The table of times to be displayed by this dialog		**/
	private UILdTaskTimesTable oTaskTimesTable;
	private JLabel lblTitle;
	private Font font;

	/**	The parent frame of this dialog **/
// Commented out until decision hass been made about parent class 	private UILdActivityViewFrame oParent = null;	
	private UILdViewPane oParent = null;
	
	/** The parent JInternalFrame that this panel is within. 	**/
	private JInternalFrame oParentFrame = null;
	
	/** Scroll pane for displaying the table within. **/
	private JScrollPane oScrollPane = null;
	
	/**	
	* Auto-generated main method to display this 
	* JPanel inside a new JFrame.
**/	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(300, 300);
		frame.getContentPane().add(new UILdActivityTimesPanel(frame));
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	/**
	 * Minimal constructor. Does not initilise or display the GUI.
	 */
	public UILdActivityTimesPanel() {
		oTaskTimesTable = new UILdTaskTimesTable();
		
	}

	public UILdActivityTimesPanel(UILdViewPane owner)	{
		this();
		oParent = owner;
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		initGUI();
	}


	public UILdActivityTimesPanel(JFrame owner)	{
		this();
	//	oParent = (JFrame)owner;
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		initGUI();
	}
	
	public UILdActivityTimesPanel(JInternalFrame owner)	{
		this();
		oParentFrame = owner;
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		initGUI();
	}
	
	
	private UILdTaskTimesTable getTaskTimesTable() {
		if(oTaskTimesTable == null) {
			oTaskTimesTable = new UILdTaskTimesTable();
			oTaskTimesTable.setBorder(new LineBorder(new java.awt.Color(0,0,0), 1, false));
			oTaskTimesTable.setGridColor(new java.awt.Color(0,0,0));
			oTaskTimesTable.setToolTipText("Task times");
		}
		return oTaskTimesTable;
	}
	
	private void initGUI() {
		try {
			
			{
				font = ProjectCompendiumFrame.labelFont;
				int scale = ProjectCompendium.APP.getToolBarManager().getTextZoom();
				font = new Font(font.getName(), font.getStyle(), font.getSize()+ scale);	

				JPanel oTitlePanel = new JPanel(); 
				oTitlePanel.setLayout(new BoxLayout(oTitlePanel, BoxLayout.PAGE_AXIS));
				oTitlePanel.add(getLblTitle());
				JButton pbClose = new JButton("OK");
				oTitlePanel.add(pbClose);
				pbClose.setSize(21, 21);
				pbClose.setPreferredSize(new java.awt.Dimension(21, 21));
				pbClose.setToolTipText("Click to hide timing information");
				pbClose.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						//System.out.println("pbClose.actionPerformed, event="+evt);
						setVisible(false);
					}
				});
				oTitlePanel.setBorder(new LineBorder(new java.awt.Color(0,0,255), 1, false));
				oTitlePanel.setBackground(new java.awt.Color(128,128,255));
				this.add(oTitlePanel);
				
				//oTaskTimesTable.setPreferredSize(new java.awt.Dimension(150, 15));
//				this.setTitle("Timing info");
				JPanel oTablePanel = new JPanel(); 
				oScrollPane = new JScrollPane(oTaskTimesTable);
//				oTablePanel.add(oTaskTimesTable.getTableHeader());
//				oTablePanel.add(oTaskTimesTable);
				oTablePanel.add(oScrollPane);
				oTablePanel.setBorder(new LineBorder(new java.awt.Color(0,0,255), 1, false));
				this.add(oTablePanel);
				oTablePanel.setPreferredSize(new java.awt.Dimension(200, 100));
				//this.setPreferredSize(new java.awt.Dim	ension(150, 50));
				//this.setLocation(0, 0);
				this.setMinimumSize(new java.awt.Dimension(216, 50));
		//		this.setOpaque(true);
				
				this.setPreferredSize(new java.awt.Dimension(228, 68));
				this.setBorder(new LineBorder(new java.awt.Color(0,0,0), 1, false));

	//			this.setBorder(new LineBorder(new java.awt.Color(0,0,255), 2, false));
				
			//	this.setVisible(true);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public JLabel getLblTitle() {
		if(lblTitle == null) {
			lblTitle = new JLabel();
			lblTitle.setText("Timing info");
			
			lblTitle.setBounds(0, 20, 61, 13);
			lblTitle.setFont(font);
			lblTitle.setForeground(new java.awt.Color(255,255,255));
		}
		return lblTitle;
	}

}
