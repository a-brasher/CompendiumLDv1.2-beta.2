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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.DocumentListener;

import com.compendium.core.datamodel.NodeSummary;
import com.compendium.ui.*;
import com.compendium.*;
import com.compendium.ui.stencils.*;
import com.compendium.core.ICoreConstants;
import com.compendium.learningdesign.core.ILdCoreConstants;
import com.compendium.learningdesign.ui.*;
 
/**
 * UINodeLdHelpPanel provides help for LD tasks.
 * It uses BoxLayout on the Y_AXIS to layout its component panels. 
 * @author ajb785
 *
 */
public class UINodeLdHelpPanel extends JPanel implements ActionListener,  IUIConstants {
	/**
	 * The parent dialog that this panel is in.
	 * @uml.property  name="oParentDialog"
	 * @uml.associationEnd  multiplicity="(1 1)" inverse="oNodeLdTasksHelpPanel:com.compendium.learningdesign.ui.panels.UILdInformationDialog"
	 */
	private UILdInformationDialog oParentDialog	= null;
	
	/**
	 * The user author name of the current user
	 * @uml.property  name="sAuthor"
	 */
	private String 			sAuthor 		= "";
	
	/**
	 * The current node data this is the help for.
	 * @uml.property  name="oNode"
	 * @uml.associationEnd  
	 */
	private NodeSummary		oNode			= null;
	
	/**
	 * The current node this is the help for - if in a map.
	 * @uml.property  name="oUILdNode"
	 * @uml.associationEnd  
	 */
	private UILdNode			oUILdNode			= null;
	
	/**
	 * paneType indicats which type of pane this is, i.e. Tasks help or Tools help	*
	 * @uml.property  name="iPaneType"
	 */
	private int iPaneType	=	0;
	
	/**
	 * The tools component of this instance of UINodeLdHelpPanel	*
	 * @uml.property  name="toolsComponent"
	 * @uml.associationEnd  
	 */
	private UINodeLdHelpToolsPanel	toolsComponent;
	
	/**
	 * The activities component of this instance of UINodeLdHelpPanel	*
	 * @uml.property  name="activitiesComponent"
	 * @uml.associationEnd  
	 */
	private UINodeLdHelpActivitiesPanel	activitiesComponent;
	
	/**
	 * The button to set the options for this panel.
	 * @uml.property  name="pbOptions"
	 * @uml.associationEnd  
	 */
	private UIButton		pbOptions				= null;

	/**
	 * The button to close this and the parent dialog.
	 * @uml.property  name="pbClose"
	 * @uml.associationEnd  
	 */
	private UIButton		pbClose			= null;

	/**
	 * The button to open the relevant help.
	 * @uml.property  name="pbHelp"
	 * @uml.associationEnd  
	 */
	public UIButton			pbHelp				= null;
	
	/** The   Vector (item list)  for the  LD stencil set. **/
//	private Vector vtLdStencilItems;


	/**
	 * Constructor.
	 * @param parent, the parent frame for the dialog this panel is in.
	 * @param uinode com.compendium.ui.UINode, the current node this is the contents for - if in a map.
 	 * @param tabbedPane, the parent dialog this panel is in.
	 */
	public UINodeLdHelpPanel(JFrame parent, UILdNode uinode, UILdInformationDialog tabbedPane) {
		super();
		oParentDialog = tabbedPane;
		oUILdNode =  uinode;
		
		this.sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
		try	{
			initLdHelpPanel(uinode.getNode());
		}
		catch (Exception e)	{
			// handled by initLdHelpPanel()
		}
		
		
		//set the updated node retireved from the db to the old node
		oUILdNode.setNode(oNode);
	}
	
	/**
	 * Constructor.
	 * @param parent, the parent frame for the dialog this panel is in.
	 * @param uinode com.compendium.ui.UINode, the current node this is the help for - if in a map.
 	 * @param tabbedPane, the parent dialog this panel is in.
 	 * @param paneType, the type of pane iTASKSHELP_TAB or iTOOLSHELP_TAB
	 */
	public UINodeLdHelpPanel(JFrame parent, UILdNode uinode, UILdInformationDialog tabbedPane, int paneType) {
		super();
		oParentDialog = tabbedPane;
		oUILdNode = uinode;
		iPaneType = paneType;
		
		this.sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
		try	{
			initLdHelpPanel(uinode.getNode());
		}
		catch (Exception e)	{
			// handled by initLdHelpPanel()
		}
		
		//set the updated node retireved from the db to the old node
		oUILdNode.setNode(oNode);
	}

	/**
	 * Constructor.
	 * @param parent, the parent frame for the dialog this panel is in.
	 * @param node com.compendium.core.datamodel.NodeSummary, the current node this is the contents for.
 	 * @param tabbedPane, the parent dialog this panel is in.
 	 * @param paneType, he type of pane iTASKSHELP_TAB or iTOOLSHELP_TAB
	 */
	public UINodeLdHelpPanel(JFrame parent, NodeSummary node, UILdInformationDialog tabbedPane, int paneType) {
		super();
		oParentDialog = tabbedPane;
		iPaneType = paneType;
		this.sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();		
		try	{
			initLdHelpPanel(node);
		}
		catch (Exception e) {
		// Error is handled by initLdHelpPanel	
		}
	}
	
	
	/**
	 * Constructor.
	 * @param parent, the parent frame for the dialog this panel is in.
	 * @param node com.compendium.core.datamodel.NodeSummary, the current node this is the contents for.
 	 * @param tabbedPane, the parent dialog this panel is in.
 	 * @param paneType, the type of pane iTASKSHELP_TAB or iTOOLSHELP_TAB
 	 * @param showContents, boolean : if true, generate and add contents, if false return an empty layout 
	 */
	public UINodeLdHelpPanel(JFrame parent, NodeSummary node, UILdInformationDialog tabbedPane, int paneType, boolean addContents) {
		super();
		oParentDialog = tabbedPane;
		iPaneType = paneType;
		this.sAuthor = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
		if (!addContents)
			initLayoutLdHelpPanel(node);
		else	{
			try	{
				initLdHelpPanel(node);
			}
			catch (Exception e) {
				// Error is handled by initLdHelpPanel	
			}
		}
	}
	/**
	 * Initialize this panel, but do not add it's contents
	 * @param node com.compendium.core.datamodel.NodeSummary, the current node to draw the contents for.
	 */
	private void initLayoutLdHelpPanel(NodeSummary node)  {		
		oNode = node;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));				
	} 
	/**
	 * Initialize and draw this panel's contents.
	 * @param node com.compendium.core.datamodel.NodeSummary, the current node to draw the contents for.
	 */
	private void initLdHelpPanel(NodeSummary node) {
		initLayoutLdHelpPanel(node);
		showNodeLDHelpPanel();

	}
	/** Return the iPaneType value * */
	private int getPaneType()	{
		return iPaneType;
	}
	/** Return true if this is a tasks help panel, false otherwise 
	 * @return boolean 
	 * **/
	public boolean isTasksHelpPanel()	{
		if (getPaneType() == ILdUIConstants.iTASKSHELP_TAB)
			return true;
		else
			return false;
	}
	/**
	 * Helper method to layout the tools help components
	 */
	private void createToolsHelp(String sInput)	{
		int type = getPaneType();
		if (sInput.length() == 0)	{
			String toolLabel = "";
			// Create the instance of UINodeLdHelpToolsPanel
			toolsComponent = new UINodeLdHelpToolsPanel(type, oUILdNode);
			// If it's a tool node use the tool name in the panel header 
			if (oUILdNode.getLdType() == ILdCoreConstants.iLD_TYPE_VLE_TOOL)
				toolLabel = ProjectCompendium.APP.getLdTypeTagMaps().getToolTypeToToolNamesTable().get(oNode.getLdToolType());
			else
				toolLabel = oUILdNode.getNode().getLabel();
			toolsComponent.initLDHelpToolsPanel(toolLabel);
			// Create the instance of UINodeLdHelpActivitiesPanel
			//	activitiesComponent = new UINodeLdHelpActivitiesPanel(type, oNode.getLabel());
			activitiesComponent = new UINodeLdHelpActivitiesPanel(type, oNode);
			// Create the instance of UINodeLdHelpcaseStudiesPanel
		}
		else	{
			// Create the instance of UINodeLdHelpToolsPanel
			toolsComponent = new UINodeLdHelpToolsPanel(type, oUILdNode);
			toolsComponent.initLDHelpToolsPanel(sInput);
			// Create the instance of UINodeLdHelpActivitiesPanel
			activitiesComponent = new UINodeLdHelpActivitiesPanel(type, sInput);
			// Create the instance of UINodeLdHelpcaseStudiesPanel
			// TO BE DONE ONCE THE CASE STUDIES DATABASE IS READY
		}
	}
	/**
	 * Helper method to layout the tasks help components. This method 
	 * can  be called from a menu for a node with a label (in which case 
	 * sInput will be an empty string, or via a method
	 * that detects input into the node label and feeds into this via the 
	 * parameter sInput.
	 * 
	 */
	private void createTasksHelp(String sInput)	{
		int type = getPaneType();
		if (sInput.length() == 0)	{ 
			// Create the instance of UINodeLdHelpToolsPanel
			toolsComponent = new UINodeLdHelpToolsPanel(type, oUILdNode);
			toolsComponent.initLDHelpToolsPanel(oUILdNode.getNode().getLabel());
			// Create the instance of UINodeLdHelpActivitiesPanel
			activitiesComponent = new UINodeLdHelpActivitiesPanel(type, oNode);
			// Create the instance of UINodeLdHelpcaseStudiesPanel
			// TO BE DONE ONCE THE CASE STUDIES DATABASE IS READY
		}
		else	{
			// Create the instance of UINodeLdHelpToolsPanel
			toolsComponent = new UINodeLdHelpToolsPanel(type, oUILdNode);
			toolsComponent.initLDHelpToolsPanel(sInput);
			// Create the instance of UINodeLdHelpActivitiesPanel
			activitiesComponent = new UINodeLdHelpActivitiesPanel(type, sInput);
			// Create the instance of UINodeLdHelpcaseStudiesPanel
			// TO BE DONE ONCE THE CASE STUDIES DATABASE IS READY
		}
	}
	/** Return true if this is a tools help panel, false otherwise 
	 * @return boolean 
	 * **/
	public boolean isToolsHelpPanel()	{
		if (getPaneType() == ILdUIConstants.iTOOLSHELP_TAB)
			return true;
		else
			return false;
	}
/** 
 * Need to write this method!
 */	
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();
		
		if (source == pbClose) {
			oParentDialog.onCancel();
		}
		else if (source == pbOptions) {
			// To DO - 
			
		}
	}

	/**
	 * Set the default button for the parent dialog to be this panel's default button.
	 */
public void setDefaultButton() {
	oParentDialog.getRootPane().setDefaultButton(pbOptions);
	
}

/**
 * Create and return the button panel.
 */
public UIButtonPanel createButtonPanel() {

	UIButtonPanel oButtonPanel = new UIButtonPanel();

	pbOptions = new UIButton("Options");
	pbOptions.setMnemonic(KeyEvent.VK_O);
	pbOptions.addActionListener(this);
	oButtonPanel.addButton(pbOptions);

	pbClose = new UIButton("Close");
	pbClose.setMnemonic(KeyEvent.VK_C);
	pbClose.addActionListener(this);
	oButtonPanel.addButton(pbClose);

	pbHelp = new UIButton("Help");
	pbHelp.setMnemonic(KeyEvent.VK_H);
	ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "node.node_details", ProjectCompendium.APP.mainHS);
	oButtonPanel.addHelpButton(pbHelp);

	return oButtonPanel;
}

/**
 * Draw the contents of the panel.
 */
private void showNodeLDHelpPanel() {
	
	// Tools help is the default 
	if (isTasksHelpPanel() )	{
		createTasksHelp("");
	}
	else 	{
		createToolsHelp("");
	}
	
	add(toolsComponent);
	add(activitiesComponent);
//	add(createButtonPanel());
	

}

/**
 * Convenience method that sets the tools panel to be in focus.
 */
public void setToolsPanelFocused() {
	boolean b = toolsComponent.requestFocusInWindow();
}

/**
 * @return the oNode
 */
public NodeSummary getNode() {
	return oNode;
}

/**
 * @return the oUINode
 */
public UINode getUINode() {
	return oUILdNode;
}

/**
 * Process button pushes.
 * @param evt, the associated ActionEvent object.
 */


}
