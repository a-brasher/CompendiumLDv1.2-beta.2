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

package com.compendium.learningdesign.ui.panels.nodecontent;
//import com.cloudgarden.layout.AnchorLayout;
import com.compendium.ui.ProjectCompendiumFrame;

import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import java.util.ArrayList;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class UILdNodeLearningOutcomesPanel extends javax.swing.JPanel {
	/**
	 * @uml.property  name="lblTitle"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JLabel lblTitle;
	/**
	 * @uml.property  name="oButtonPanel"
	 * @uml.associationEnd  readOnly="true"
	 */
	private JPanel oButtonPanel;
	/**
	 * @uml.property  name="titleBarPanel"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JPanel titleBarPanel;
	/**
	 * @uml.property  name="pbToggleShowOnMap"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JButton pbToggleShowOnMap;
	/**
	 * @uml.property  name="pbDeleteLO"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JButton pbDeleteLO;
	/**
	 * @uml.property  name="pbAddLO"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JButton pbAddLO;
	/**
	 * @uml.property  name="oLONumberPanel"
	 * @uml.associationEnd  readOnly="true"
	 */
	private JLabel oLONumberPanel;
	/**
	 * @uml.property  name="oLearningOutcomePanel"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JPanel oLearningOutcomePanel;
	/**	List of Learning Outcome entry panels **/
	private ArrayList<UILdNodeLearningOutcomeEntryPanel> oLearningOutcomeEntryPanels = new ArrayList<UILdNodeLearningOutcomeEntryPanel>();
	/**
	 * Number of learning outcomes and number of Entry panels *
	 * @uml.property  name="nLOs"
	 */
	private int nLOs = 0;
	/**
	 * Scroll pane to hold up to 3 LOs without scrolling *
	 * @uml.property  name="scrollpane"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JScrollPane scrollpane;
/**
 * Font to use to write labels etc	*
 * @uml.property  name="font"
 */
	private Font font							= null;

/**	
	* Auto-generated main method to display this 
	* JPanel inside a new JFrame.
**/	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new UILdNodeLearningOutcomesPanel());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public UILdNodeLearningOutcomesPanel() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			font = ProjectCompendiumFrame.labelFont;
			BoxLayout thisLayout = new BoxLayout(this, javax.swing.BoxLayout.Y_AXIS);
			this.setLayout(thisLayout);
			this.setPreferredSize(new java.awt.Dimension(400, 80));
			this.setMinimumSize(new java.awt.Dimension(400, 30));
			//this.setMaximumSize(new java.awt.Dimension(400, 30));			
			this.setBorder(new LineBorder(new java.awt.Color(0,0,0), 1, true));
			{
				titleBarPanel = new JPanel();
				FlowLayout titleBarPanelLayout = new FlowLayout();
				titleBarPanelLayout.setAlignment(FlowLayout.LEFT);
				titleBarPanel.setLayout(titleBarPanelLayout);
				this.add(titleBarPanel);
				titleBarPanel.setPreferredSize(new java.awt.Dimension(400, 35));
				titleBarPanel.setBackground(new java.awt.Color(255,128,128));
				titleBarPanel.setBorder(new LineBorder(new java.awt.Color(0,0,0), 1, false));
				titleBarPanel.setMaximumSize(new java.awt.Dimension(32767, 35));
				{
					lblTitle = new JLabel();
					FlowLayout lblTitleLayout = new FlowLayout();
					titleBarPanel.add(lblTitle);
					lblTitle.setLayout(lblTitleLayout);
					lblTitle.setText("Learning outcomes");
					lblTitle.setBackground(new java.awt.Color(255,128,128));
					lblTitle.setFont(font);
					lblTitle.setForeground(new java.awt.Color(255,255,255));
					lblTitle.setOpaque(true);
				}
				{
					pbAddLO = new JButton();
					titleBarPanel.add(pbAddLO);
					pbAddLO.setText("Add LO");
					pbAddLO.addMouseListener(new MouseAdapter() {
						public void mousePressed(MouseEvent evt) {
							pbAddLOMousePressed(evt);
						}
					});
				}
				
				{
					pbDeleteLO = new JButton();
					pbDeleteLO.setText("Delete LO");
					pbDeleteLO.setVisible(false);
					titleBarPanel.add(pbDeleteLO);
					if (nLOs != 0)
						pbDeleteLO.setVisible(true);
				}
				{
					pbToggleShowOnMap = new JButton();
					pbToggleShowOnMap.setVisible(false);
					titleBarPanel.add(getPbToggleShowOnMap());
					pbToggleShowOnMap.setText("Show");
					if (nLOs != 0)
						pbToggleShowOnMap.setVisible(true);
				}
			oLearningOutcomePanel = new JPanel();
			BoxLayout oLOPanelLayout = new BoxLayout(oLearningOutcomePanel, javax.swing.BoxLayout.Y_AXIS);
			oLearningOutcomePanel.setLayout(oLOPanelLayout);
			scrollpane = new JScrollPane(oLearningOutcomePanel);
			scrollpane.setPreferredSize(new Dimension(300,90));
			//this.add(scrollpane, BorderLayout.CENTER);
			this.add(scrollpane);
			
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return
	 * @uml.property  name="oLearningOutcomePanel"
	 */
	public JPanel getOLearningOutcomePanel() {
		return oLearningOutcomePanel;
	}
	
	/**
	 * @return
	 * @uml.property  name="oLONumberPanel"
	 */
	public JLabel getOLONumberPanel() {
		return oLONumberPanel;
	}
	
	/**
	 * @return
	 * @uml.property  name="oButtonPanel"
	 */
	public JPanel getOButtonPanel() {
		return oButtonPanel;
	}
	
	/**
	 * @return
	 * @uml.property  name="pbAddLO"
	 */
	public JButton getPbAddLO() {
		return pbAddLO;
	}
	
	private void pbAddLOMousePressed(MouseEvent evt) {
		nLOs++;
		System.out.println("pbAddLO.mousePressed,nLOs = "+nLOs+ ", event="+evt);
		UILdNodeLearningOutcomeEntryPanel oNewLOEntryPanel = new UILdNodeLearningOutcomeEntryPanel();
		oLearningOutcomeEntryPanels.add(oNewLOEntryPanel);
		oNewLOEntryPanel.setOutcomeNumber(nLOs);
		
		oLearningOutcomePanel.add(oNewLOEntryPanel);
		//this.setPreferredSize(new java.awt.Dimension(400, (80 + (nLOs-1)*40)));
		if (nLOs != 0)	{
			pbDeleteLO.setVisible(true);
			pbToggleShowOnMap.setVisible(true);
		}
		this.validate();
	}
	
	/**
	 * @return
	 * @uml.property  name="pbToggleShowOnMap"
	 */
	public JButton getPbToggleShowOnMap() {
		return pbToggleShowOnMap;
	}

	/**
	 * Get the dialog to recalulate the default font and reset it on the text areas.
	 * Used for presentation font changes.
	 */
	public void refreshFont() {
		font = ProjectCompendiumFrame.labelFont;
		/** replace these two lines when work out what is wrong **/
	//	int scale = ProjectCompendium.APP.getToolBarManager().getTextZoom();
	//	font = new Font(font.getName(), font.getStyle(), font.getSize()+ scale);		
		/** reeset the font for each learning outcome	**/
		while(oLearningOutcomeEntryPanels.listIterator().hasNext())	{
			oLearningOutcomeEntryPanels.listIterator().next().setFont(font);
		}				
		repaint();
	}

}
