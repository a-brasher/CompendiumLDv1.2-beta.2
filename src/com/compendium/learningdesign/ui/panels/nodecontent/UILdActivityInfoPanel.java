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

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.JFrame;

import com.compendium.*;
import com.compendium.core.datamodel.*;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.UINodeContentDialog;

/**
* This panel is for editing the label (title) of a learning activity, and for
* adding, editing and/or deleting learning outcomes associated with the 
* activity.
*/
public class UILdActivityInfoPanel extends javax.swing.JPanel {
	/**
	 * The panel for editing the label (title) of the learning activity 	*
	 * @uml.property  name="oUILdNodeTitleEditor"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private UILdNodeTitleEditor oUILdNodeTitleEditor;
	
	/**
	 * The panel for adding, editing and deleting learning outcomes		*
	 * @uml.property  name="oUILdNodeLearningOutcomePanel"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private UILdNodeLearningOutcomesPanel oUILdNodeLearningOutcomePanel;
	
	/**
	 * The parent dialog that this panel is in.
	 * @uml.property  name="oParentDialog"
	 * @uml.associationEnd  inverse="oLearningOutcomesPane:com.compendium.learningdesign.ui.panels.nodecontent.UILdActivityNodeContentDialog"
	 */
	private UILdActivityNodeContentDialog oParentDialog	= null;
	
	/**
	 * The current node this is the contents for - if in a map.
	 * @uml.property  name="oUINode"
	 * @uml.associationEnd  
	 */
	private UINode			oUINode			= null;
	
	/**
	 * The current node data this is the contents for.
	 * @uml.property  name="oNode"
	 * @uml.associationEnd  
	 */
	private NodeSummary		oNode			= null;
	
	/**
	 * The user author name of the current user
	 * @uml.property  name="sAuthor"
	 */
	private String 			sAuthor 		= "";
	
	/**
	 * The button to save all node chagens and close the parent dialog.
	 * @uml.property  name="pbOK"
	 * @uml.associationEnd  
	 */
	private UIButton		pbOK				= null;

	/**
	* Auto-generated main method to display this 
	* JPanel inside a new JFrame.
	*/
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new UILdActivityInfoPanel());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public UILdActivityInfoPanel() {
		super();
		initGUI();
	}
	
	/**
	 * Constructor.
	 * @param parent, the parent frame for the dialog this panel is in.
	 * @param uinode com.compendium.ui.UINode, the current node this is the contents for - if in a map.
 	 * @param tabbedPane, the parent dialog this panel is in.
	 */
	public UILdActivityInfoPanel(JFrame parent, UINode uinode, UILdActivityNodeContentDialog tabbedPane) {
		super();
		oParentDialog = tabbedPane;
		oUINode = uinode;
		oNode = oUINode.getNode();
		this.sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
		
	//	initEditPanel(uinode.getNode());
		initGUI();
		//set the updated node retrieved from the db to the old node
		oUINode.setNode(oNode);
	}

	/**
	 * Constructor.
	 * @param parent, the parent frame for the dialog this panel is in.
	 * @param node com.compendium.core.datamodel.NodeSummary, the current node this is the contents for.
 	 * @param tabbedPane, the parent dialog this panel is in.
	 */
	public UILdActivityInfoPanel(JFrame parent, NodeSummary node, UILdActivityNodeContentDialog tabbedPane) {
		super();
		
		this.sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
		
		oParentDialog = tabbedPane;

	//	initEditPanel(node);
		initGUI();
	}
	
	private void initGUI() {
		try {
			BoxLayout thisLayout = new BoxLayout(this, javax.swing.BoxLayout.Y_AXIS);
			this.setLayout(thisLayout);
			//setPreferredSize(new Dimension(400, 300));
			{
				oUILdNodeTitleEditor = new UILdNodeTitleEditor();
				this.add(getOUILdNodeTitleEditor());
			}
			{
				oUILdNodeLearningOutcomePanel = new UILdNodeLearningOutcomesPanel();
				this.add(getOUILdNodeLearningOutcomePanel());
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the default button for the parent dialog to be this panel's default button.
	 */
	public void setDefaultButton() {
		oParentDialog.getRootPane().setDefaultButton(pbOK);
	}
	
	/**
	 * @return
	 * @uml.property  name="oUILdNodeTitleEditor"
	 */
	public UILdNodeTitleEditor getOUILdNodeTitleEditor() {
		return oUILdNodeTitleEditor;
	}
	
	/**
	 * @return
	 * @uml.property  name="oUILdNodeLearningOutcomePanel"
	 */
	public UILdNodeLearningOutcomesPanel getOUILdNodeLearningOutcomePanel() {
		return oUILdNodeLearningOutcomePanel;
	}

	/**
	 * Convenience method that sets the Detail/title box to be in focus.
	 */
	public void setDetailFieldFocused() {
		oUILdNodeTitleEditor.getOLabel().requestFocus();		
	}
	
	/**
	 * Get the dialog to recalulate the default font and reset it on the text areas.
	 * Used for presentation font changes.
	 */
	public void refreshFont() {
		Font font = ProjectCompendiumFrame.labelFont;
		/** replace these two lines when work out what is wrong **/
	//	int scale = ProjectCompendium.APP.getToolBarManager().getTextZoom();
	//	font = new Font(font.getName(), font.getStyle(), font.getSize()+ scale);		
		oUILdNodeTitleEditor.refreshFont();
		oUILdNodeLearningOutcomePanel.refreshFont();
		//txtDetail.setFont(font);
				
		repaint();
	}
	
	/**
	 * Process the saving of any node contents changes.
	 */
	public void onUpdate() {	
	/** To be written **/	
	}
	
}
