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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.util.*;

import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.UIDialog;
import com.compendium.ui.panels.UINodeEditPanel;
import com.compendium.ui.panels.UINodePropertiesPanel;
import com.compendium.ui.panels.UINodeViewPanel;
import com.compendium.core.datamodel.*;
import com.compendium.learningdesign.mappers.TaskVerbToToolsMapper;
import com.compendium.learningdesign.mappers.ToolsToOtherToolsMappper;
import com.compendium.learningdesign.ui.ILdUIConstants;
import com.compendium.learningdesign.ui.UILdNode;
import com.compendium.learningdesign.core.ILdCoreConstants;
import com.compendium.ui.stencils.*;


/**
 * This class offers the user information intended to support their use of 
 * learning design nodes.
 * 
 * Note from class JTabbedPane: "A tab is represented by an index corresponding 
 * to the position it was added in, where the first tab has an index equal to 0 
 * and the last tab has an index equal to the tab count minus 1".
 *  
 * @author Andrew Brasher ajb785
 *
 */

public class UILdInformationDialog extends UIDialog  {
	/**
	 * Indicates if this is the first time the dialog has recieved the focus.
	 * @uml.property  name="firstFocus"
	 */
	private boolean 		firstFocus 						= true;
	
	/**
	 * The parent JFrame of JDialog to this dialog.
	 * @uml.property  name="oParent"
	 */
	private Window			oParent							= null;
	
	/**
	 * The UINode that this dialog is showing the contents for, is in a map.
	 * @uml.property  name="oUINode"
	 * @uml.associationEnd  
	 */
	private UILdNode			oUINode							= null;

	/**
	 * The NodeSummary object that this is the contents dialog for.
	 * @uml.property  name="oNode"
	 * @uml.associationEnd  
	 */
	private NodeSummary		oNode							= null;

	/**
	 * The NodePosition object that this is the contents dialog for.
	 * @uml.property  name="oNodePosition"
	 * @uml.associationEnd  
	 */
	private NodePosition	oNodePosition					= null;

	/**
	 * The current view.
	 * @uml.property  name="oView"
	 * @uml.associationEnd  
	 */
	private View			oView							= null;
	
	/**
	 * This UILdInformationDialog class offers the user two tabs, the tools tab and the tasks tab.  ldHelpPanel is the JTabbedPane which holds the tabs  oNodeLdTasksHelpPanel and oNodeLdToolsHelpPanel 			.
	 * @uml.property  name="ldHelpPanel"
	 * @uml.associationEnd  
	 */
	private JTabbedPane				ldHelpPanel				= null;
	
	/**
	 * The pane to add the contents for the dialog to.
	 * @uml.property  name="oContentPane"
	 */
	private Container		oContentPane					= null;
	
	/**
	 * This UILdInformationDialog class offers the user two tabs, the tools tab and the tasks tab. This is the  UINodeLdHelpPanel for the tasks help tab.
	 * @uml.property  name="oNodeLdTasksHelpPanel"
	 * @uml.associationEnd  inverse="oParentDialog:com.compendium.learningdesign.ui.panels.UINodeLdHelpPanel"
	 */
	private UINodeLdHelpPanel			oNodeLdTasksHelpPanel			= null;
	
	/**
	 * This UILdInformationDialog class offers the user two tabs, the tools tab and the tasks tab.  This is the UINodeLdHelpPanel for the tools help tab.
	 * @uml.property  name="oNodeLdToolsHelpPanel"
	 * @uml.associationEnd  
	 */
	private UINodeLdHelpPanel			oNodeLdToolsHelpPanel			= null;
	
	/**
	 * Indicates the currently selected tab. 0 = none, 1 = "Tools for activities", 2 = "Activities for tools"
	 * @uml.property  name="nSelectedTab"
	 */
	private int 			nSelectedTab 					= 0;
	
	/**
	 * The object to process node labels for tools for this instance	*
	 * @uml.property  name="oTaskVerbToToolMapper"
	 * @uml.associationEnd  
	 */ 
	private TaskVerbToToolsMapper		oTaskVerbToToolMapper					= null;
	
	/**
	 * The object to process node labels for tools for this instance	*
	 * @uml.property  name="oToolsToOtherToolsMapper"
	 * @uml.associationEnd  
	 */ 
	private ToolsToOtherToolsMappper		oToolsToOtherToolsMapper					= null;
	/**
	 * @uml.property  name="oTools"
	 */ 
	private TreeSet<Integer> 		oTools 	= 	null;

	/**
	 * @param parent
	 * @param modal
	 */
	public UILdInformationDialog(JFrame parent, boolean modal) {
		super(parent, modal);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param parent, the parent frame for this dialog.
	 * @param view com.compendium.core.datamodel.View, the current view.
	 * @param uinode com.compendium.ui.UINode, the node to display the contents for, (if in a map).
	 * @param oMapper a TaskVerbToToolsMapper, to map between task verbs and tools
	 * @param sLablelText text, e.g. from a node label, which will seed the mapping
	 */
	public UILdInformationDialog(JFrame parent, View view, UILdNode uinode, TaskVerbToToolsMapper oMapper, String sLablelText) {
		// This has been made non model to enable tagging while it is open.
		super(parent, false);
		oParent = parent;
		oUINode = uinode;
		oNodePosition = oUINode.getNodePosition();
		oView = view;
		// Set the selected tab to be the activity tab
		nSelectedTab = ILdUIConstants.iTASKSHELP_TAB;
		oNode = uinode.getNode();
		oTaskVerbToToolMapper = oMapper;
		initLayout();
	}
	
	/**
	 * @param parent, the parent frame for this dialog.
	 * @param view com.compendium.core.datamodel.View, the current view.
	 * @param uinode com.compendium.ui.UINode, the node to display the contents for, (if in a map).
	 * @param oMapper a ToolsToOtherToolsMappper, to map between tool descriptions and tools
	 * @param sLablelText text, e.g. from a node label, which will seed the mapping
	 */
	public UILdInformationDialog(JFrame parent, View view, UILdNode uinode, ToolsToOtherToolsMappper oMapper, String sLablelText) {
		// This has been made non model to enable tagging while it is open.
		super(parent, false);
		oParent = parent;
		oUINode = uinode;
		oNodePosition = oUINode.getNodePosition();
		oView = view;
		// Set the selected tab to be the activity tab
		nSelectedTab = ILdUIConstants.iTOOLSHELP_TAB;
		oNode = uinode.getNode();
		oToolsToOtherToolsMapper = oMapper;
		initLayout();
	}
	
	/**
	 * Constructor. Initialise this dialog. Used by UINode
	 * Copied from class UINodeContentDialog.
	 * @param parent, the parent frame for this dialog.
	 * @param view com.compendium.core.datamodel.View, the current view.
	 * @param uinode com.compendium.ui.UINode, the node to display the contents for, (if in a map).
	 * @param selectedTab, the tabbed panel to initially select when opening this dialog.
	 */
	public UILdInformationDialog(JFrame parent, View view, UILdNode uinode, int selectedTab) {
		// This has been made non model to enable tagging while it is open.
		super(parent, false);
		oParent = parent;
		oUINode = uinode;
		oNodePosition = oUINode.getNodePosition();
		oView = view;
		nSelectedTab = selectedTab;
		oNode = uinode.getNode();
		initDialog(oNode, selectedTab);
	}

	/**
	 * Initialise the layout without adding anything specific to it.
	 * 
	 */
	public void initLayout()	{
		String label = "";
		int ldType = oNode.getLdType(); 
		if (ldType == ILdCoreConstants.iLD_TYPE_VLE_TOOL)	{
			label = ProjectCompendium.APP.getLdTypeTagMaps().getToolTypeToToolNamesTable().get(oNode.getLdToolType());
		}
		else	{
			label = oNode.getLabel();
		}
		setTitle("Learning design help: " + label);
		//Get and setup the content pane for the UILdInformationDialog
		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());
//		Create the tabbed pane ldHelpPanel
		ldHelpPanel = new JTabbedPane();
	}
	/**
	 * Initialise this instance of UILdInformationDialog by using the 
	 * parameter sLablelText to generate the components of the interface.
	 * 
	 * @param sLablelText
	 */
	public void initDialog(String sLablelText)  {
		// Get the set of tool type integers relevant to the String sLablelText
		HashSet<DraggableStencilIcon> hsToolIcons = oTaskVerbToToolMapper.getToolStencils(sLablelText);
		
		initLayout();
		
//		Create the pane for holding the tasks help information i.e. oNodeLdTasksHelpPanel
		if (oUINode != null)	{
			oNodeLdTasksHelpPanel = new UINodeLdHelpPanel(ProjectCompendium.APP, oUINode, this, ILdUIConstants.iTASKSHELP_TAB );
			oNodeLdToolsHelpPanel = new UINodeLdHelpPanel(ProjectCompendium.APP, oUINode, this, ILdUIConstants.iTOOLSHELP_TAB );
		}
		else	{
			ProjectCompendium.APP.displayMessage("A node should be selected", "Error");		
		}
	}
	
	/**
	 * Create a tasks help tab the contents of which are related to the string
	 * sDescriptionText; this string originates in e.g. a  task label or tool label.
	 * @param sLablelText
	 * @return
	 */
	public UINodeLdHelpPanel createTasksHelpTab(String sDescriptionText)	{
		/** Construct the "Tools to support" panel 	**/
		/**  Get the set of tool type integers relevant to the String sLablelText.
		 * Begin by getting the relevant mapper.
		 */
		HashSet<DraggableStencilIcon> hsToolIcons = null;
		if (oTaskVerbToToolMapper == null)	{
			hsToolIcons = oToolsToOtherToolsMapper.getToolStencils(sDescriptionText);
		}
		else	{
			// If both mappers are  null something has gone wrong
			if ((oToolsToOtherToolsMapper == null) && (oTaskVerbToToolMapper == null))	{
				ProjectCompendium.APP.displayError("Error: oToolsToOtherToolsMapper mappers is  null!!", "createTasksHelpTab("+sDescriptionText+")");
				return null;
			}
			hsToolIcons = oTaskVerbToToolMapper.getToolStencils(sDescriptionText);
		}
		
		UINodeLdHelpToolsPanel helpToolsPanel = new UINodeLdHelpToolsPanel(ILdUIConstants.iTASKSHELP_TAB);
//		helpToolsPanel.initLayout(ILdUIConstants.oLDActivitiesHelpBackGroundColour);
		helpToolsPanel.initLDHelpItemsPanel(sDescriptionText, ILdUIConstants.oLDToolsHelpBackGroundColour);
		helpToolsPanel.setToolsPanels(hsToolIcons);
		helpToolsPanel.layoutToolPanels();
		/** Finished constructing the "Tools to support" panel 	**/
		/** Construct the "Activities using task" panel 	**/
		// Need to rewrite this to take account of sActivityDescriptionText
		UINodeLdHelpActivitiesPanel helpActivitiesPanel = new UINodeLdHelpActivitiesPanel(ILdUIConstants.iTASKSHELP_TAB,  oNode, sDescriptionText);
		/** Finished constructing the "Activities using task" panel 	**/
		/** Construct the main panel	**/
		UINodeLdHelpPanel oNodeLdTasksHelpPanel = new UINodeLdHelpPanel(ProjectCompendium.APP, oUINode, this, ILdUIConstants.iTASKSHELP_TAB );		
		
		oNodeLdTasksHelpPanel.add(helpToolsPanel);
		oNodeLdTasksHelpPanel.add(helpActivitiesPanel);
//		oNodeLdTasksHelpPanel.add(oNodeLdTasksHelpPanel.createButtonPanel());
		ldHelpPanel.add(oNodeLdTasksHelpPanel);
		return oNodeLdTasksHelpPanel;
	}
	
	/**
	 * Create a tools help tab the contents of which are related to the string
	 * sActivityDescriptionText; this string originates in e.g. a  task label.
	 * @param sLablelText
	 * @return
	 */
	public UINodeLdHelpPanel createToolsHelpTab(String sDescriptionText)	{
		// Get the set of tool type integers relevant to the String sLablelText
		/**  Get the set of tool type integers relevant to the String sLablelText.
		 * Begin by getting the relevant mapper, which is set in the constructor.
		 */
		HashSet<DraggableStencilIcon> hsToolIcons = null;
		if (oTaskVerbToToolMapper == null)	{
			hsToolIcons = oToolsToOtherToolsMapper.getToolStencils(sDescriptionText);
		}
		else	{
			// If both mappers are  null something has gone wrong
			if ((oToolsToOtherToolsMapper == null) && (oTaskVerbToToolMapper == null))	{
				ProjectCompendium.APP.displayError("Error: oToolsToOtherToolsMapper mappers is  null!!", "createToolsHelpTab("+sDescriptionText+")");
			}
			hsToolIcons = oTaskVerbToToolMapper.getToolStencils(sDescriptionText);
		}
		
//		HashSet<DraggableStencilIcon> hsToolIcons = oTaskVerbToToolMapper.getToolStencils(sActivityDescriptionText);
		UINodeLdHelpToolsPanel helpToolsPanel = new UINodeLdHelpToolsPanel(ILdUIConstants.iTOOLSHELP_TAB);
		helpToolsPanel.initLayout(ILdUIConstants.oLDToolsHelpBackGroundColour);
		helpToolsPanel.setToolsPanels(hsToolIcons);
		// Need to rewrite this to take account of sActivityDescriptionText
		UINodeLdHelpActivitiesPanel helpActivitiesPanel = new UINodeLdHelpActivitiesPanel(ILdUIConstants.iTOOLSHELP_TAB,  oNode);
		UINodeLdHelpPanel oNodeLdToolsHelpPanel = new UINodeLdHelpPanel(ProjectCompendium.APP, oUINode, this, ILdUIConstants.iTOOLSHELP_TAB );
		oNodeLdToolsHelpPanel.add(helpToolsPanel);
		oNodeLdToolsHelpPanel.add(helpActivitiesPanel);
//		oNodeLdToolsHelpPanel.add(oNodeLdToolsHelpPanel.createButtonPanel());
		ldHelpPanel.add(oNodeLdToolsHelpPanel);
		return oNodeLdToolsHelpPanel;
	}
	
	/**
	 * Initialise the UILdInformationDialog instance with information from the
	 * node itself, and set the selected tab.
	 * @param node
	 * @param selectedTab
	 */
	public void initDialog(NodeSummary node, int selectedTab)  {	
			initLayout();
//			Create the pane for holding the tasks help information i.e. oNodeLdTasksHelpPanel
			if (oUINode != null)	{
				oNodeLdTasksHelpPanel = new UINodeLdHelpPanel(ProjectCompendium.APP, oUINode, this, ILdUIConstants.iTASKSHELP_TAB );
				oNodeLdToolsHelpPanel = new UINodeLdHelpPanel(ProjectCompendium.APP, oUINode, this, ILdUIConstants.iTOOLSHELP_TAB );
			}
			else	{
				oNodeLdTasksHelpPanel = new UINodeLdHelpPanel(ProjectCompendium.APP, node, this, ILdUIConstants.iTASKSHELP_TAB);
				oNodeLdToolsHelpPanel = new UINodeLdHelpPanel(ProjectCompendium.APP, node, this, ILdUIConstants.iTASKSHELP_TAB);			
			}
			
//			Add the task help tab to the tabbed pane 			
			ldHelpPanel.add(oNodeLdTasksHelpPanel, "Tasks help");
//			Add the tools help tab to the tabbed pane 		
			ldHelpPanel.add(oNodeLdToolsHelpPanel, "Tools help");
//			TabbedPane.add(oSelectViewPane, "Views");


			oNodeLdTasksHelpPanel.setDefaultButton();

//			oContentPane.add(oNodeLdTasksHelpPanel, BorderLayout.CENTER);
			oContentPane.add(ldHelpPanel, BorderLayout.CENTER);
			ldHelpPanel.setSelectedIndex(selectedTab);
		//	oContentPane.addFocusListener( new FocusListener() {
			ldHelpPanel.addFocusListener( new FocusListener() {
				public void focusGained(FocusEvent e) {
					if (firstFocus) {
						if (nSelectedTab == 0)
							oNodeLdTasksHelpPanel.setToolsPanelFocused();
						firstFocus = false;
					}
				}
				public void focusLost(FocusEvent e) {

				}
		});

	

		ldHelpPanel.addChangeListener( new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {
				int nIndex = ldHelpPanel.getSelectedIndex();
				if (nIndex == ILdUIConstants.iTASKSHELP_TAB) {
					oNodeLdTasksHelpPanel.setToolsPanelFocused();
					oNodeLdTasksHelpPanel.setDefaultButton();
				}
				else if (nIndex == ILdUIConstants.iTOOLSHELP_TAB) {
					oNodeLdToolsHelpPanel.setToolsPanelFocused();
					oNodeLdToolsHelpPanel.setDefaultButton();
				}
			}
		});
		
		pack();
		final Dimension size = getSize();

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {

				Dimension dim = getSize();

				//revert to optmized size if dialog is made smaller than the min opt size
				if((dim.height < size.height) || (dim.width < size.width))
					setSize(size);
				else {
					oNodeLdTasksHelpPanel.revalidate();
				}
			
		};
		});
	}

	/**
	 * Initialise the UILdInformationDialog instance with information from the
	 * node itself, and set the selected tab.
	 * @param node
	 * @param selectedTab
	 */
	public void initHelpDialog(String sActivityDescriptionText)  {	
			initLayout();
			
//			Create the pane for holding the tasks help information i.e. oNodeLdTasksHelpPanel
			if (sActivityDescriptionText.length() > 0)	{
				oNodeLdTasksHelpPanel = createTasksHelpTab(sActivityDescriptionText);
//				oNodeLdToolsHelpPanel = createToolsHelpTab(sActivityDescriptionText);
				// It's activity related help so make a default tools panel 
				oNodeLdToolsHelpPanel = new UINodeLdHelpPanel(ProjectCompendium.APP, oUINode, this, ILdUIConstants.iTOOLSHELP_TAB );
			}
			else	{
				oNodeLdTasksHelpPanel = new UINodeLdHelpPanel(ProjectCompendium.APP, this.getNode(), this, ILdUIConstants.iTASKSHELP_TAB);
				oNodeLdToolsHelpPanel = new UINodeLdHelpPanel(ProjectCompendium.APP, this.getNode(), this, ILdUIConstants.iTASKSHELP_TAB);			
			}

//			Add the task help tab to the tabbed pane 			
			ldHelpPanel.add(oNodeLdTasksHelpPanel, "Tasks help");
//			Add the tools help tab to the tabbed pane 		
			ldHelpPanel.add(oNodeLdToolsHelpPanel, "Tools help");
//			TabbedPane.add(oSelectViewPane, "Views");


			oNodeLdTasksHelpPanel.setDefaultButton();

//			oContentPane.add(oNodeLdTasksHelpPanel, BorderLayout.CENTER);
			oContentPane.add(ldHelpPanel, BorderLayout.CENTER);
			ldHelpPanel.setSelectedIndex(ILdUIConstants.iTASKSHELP_TAB);
		//	oContentPane.addFocusListener( new FocusListener() {
			ldHelpPanel.addFocusListener( new FocusListener() {
				public void focusGained(FocusEvent e) {
					if (firstFocus ) {
						/**  Let the user decide 	**/
						if (nSelectedTab == 0)
							oNodeLdTasksHelpPanel.setToolsPanelFocused();
						firstFocus = false;
						
					}
				}
				public void focusLost(FocusEvent e) {

				}
		});

	

		ldHelpPanel.addChangeListener( new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {
				int nIndex = ldHelpPanel.getSelectedIndex();
				if (nIndex == ILdUIConstants.iTASKSHELP_TAB) {
					// oNodeLdTasksHelpPanel.setToolsPanelFocused();
					oNodeLdTasksHelpPanel.setDefaultButton();
				}
				else if (nIndex == ILdUIConstants.iTOOLSHELP_TAB) {
					oNodeLdToolsHelpPanel.setToolsPanelFocused();
					oNodeLdToolsHelpPanel.setDefaultButton();
				}
			}
		});
		
		pack();
		final Dimension size = getSize();

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {

				Dimension dim = getSize();

				//revert to optmized size if dialog is made smaller than the min opt size
				if((dim.height < size.height) || (dim.width < size.width))
					setSize(size);
				else {
					oNodeLdTasksHelpPanel.revalidate();
				}
			
		};
		});
	}	
	
	/**
	 * Initialise the UILdInformationDialog instance with information from the
	 * node itself, and set the selected tab.
	 * @param node
	 * @param selectedTab
	 */
	public void initTasksHelpDialog(String sActivityDescriptionText)  {	
	//		initLayout(); This is now called in the constructor
//			Create the pane for holding the tasks help information i.e. oNodeLdTasksHelpPanel
			if (sActivityDescriptionText.length() > 0)	{
				oNodeLdTasksHelpPanel = createTasksHelpTab(sActivityDescriptionText);
//				oNodeLdToolsHelpPanel = createToolsHelpTab(sActivityDescriptionText);
				// This is a tasks help panel so create an empty tools help panel
				// oNodeLdToolsHelpPanel = new UINodeLdHelpPanel(ProjectCompendium.APP, oUINode.getNode(), this, ILdUIConstants.iTOOLSHELP_TAB, false );
//				Add the task help tab to the tabbed pane 			
				ldHelpPanel.add(oNodeLdTasksHelpPanel, "Tasks help");
//				Add the tools help tab to the tabbed pane				
//				ldHelpPanel.add(oNodeLdToolsHelpPanel, "Tools help");
//				TabbedPane.add(oSelectViewPane, "Views");
			}
			else	{
				oNodeLdTasksHelpPanel = new UINodeLdHelpPanel(ProjectCompendium.APP, this.getNode(), this, ILdUIConstants.iTASKSHELP_TAB);
				oNodeLdToolsHelpPanel = new UINodeLdHelpPanel(ProjectCompendium.APP, this.getNode(), this, ILdUIConstants.iTOOLSHELP_TAB);
//				Add the task help tab to the tabbed pane 			
				ldHelpPanel.add(oNodeLdTasksHelpPanel, "Tasks help");
//				Add the tools help tab to the tabbed pane
				
				ldHelpPanel.add(oNodeLdToolsHelpPanel, "Tools help");
//				TabbedPane.add(oSelectViewPane, "Views");
			}




			oNodeLdTasksHelpPanel.setDefaultButton();

//			oContentPane.add(oNodeLdTasksHelpPanel, BorderLayout.CENTER);
			oContentPane.add(ldHelpPanel, BorderLayout.CENTER);
			ldHelpPanel.setSelectedIndex(ILdUIConstants.iTASKSHELP_TAB);
		//	oContentPane.addFocusListener( new FocusListener() {
			ldHelpPanel.addFocusListener( new FocusListener() {
				public void focusGained(FocusEvent e) {
					if (firstFocus ) {
						/**  Let the user decide 	**/
						if (nSelectedTab == 0)
							oNodeLdTasksHelpPanel.setToolsPanelFocused();
						firstFocus = false;
						
					}
				}
				public void focusLost(FocusEvent e) {

				}
		});

	

		ldHelpPanel.addChangeListener( new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {
				int nIndex = ldHelpPanel.getSelectedIndex();
				if (nIndex == ILdUIConstants.iTASKSHELP_TAB) {
					// oNodeLdTasksHelpPanel.setToolsPanelFocused();
					oNodeLdTasksHelpPanel.setDefaultButton();
				}
				else if (nIndex == ILdUIConstants.iTOOLSHELP_TAB) {
					oNodeLdToolsHelpPanel.setToolsPanelFocused();
					oNodeLdToolsHelpPanel.setDefaultButton();
				}
			}
		});
		
		pack();
		final Dimension size = getSize();

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {

				Dimension dim = getSize();

				//revert to optmized size if dialog is made smaller than the min opt size
				if((dim.height < size.height) || (dim.width < size.width))
					setSize(size);
				else {
					oNodeLdTasksHelpPanel.revalidate();
				}
			
		};
		});
	}
	
	/**
	 * Initialise the UILdInformationDialog instance with information from the
	 * node itself, and set the selected tab.
	 * @param node
	 * @param selectedTab
	 */
	public void initToolsHelpDialog(String sActivityDescriptionText)  {	
	//		initLayout(); This is now called in the constructor
//			Create the pane for holding the tasks help information i.e. oNodeLdTasksHelpPanel
			if (sActivityDescriptionText.length() > 0)	{
				oNodeLdTasksHelpPanel = createTasksHelpTab(sActivityDescriptionText);
//				oNodeLdToolsHelpPanel = createToolsHelpTab(sActivityDescriptionText);
				// This is a tasks help panel so create an empty tools help panel
				oNodeLdToolsHelpPanel = new UINodeLdHelpPanel(ProjectCompendium.APP, oUINode.getNode(), this, ILdUIConstants.iTOOLSHELP_TAB, false );
			}
			else	{
				oNodeLdTasksHelpPanel = new UINodeLdHelpPanel(ProjectCompendium.APP, this.getNode(), this, ILdUIConstants.iTASKSHELP_TAB);
				oNodeLdToolsHelpPanel = new UINodeLdHelpPanel(ProjectCompendium.APP, this.getNode(), this, ILdUIConstants.iTOOLSHELP_TAB);			
			}

//			Add the task help tab to the tabbed pane 			
			ldHelpPanel.add(oNodeLdTasksHelpPanel, "Tasks help");
//			Add the tools help tab to the tabbed pane 		
			ldHelpPanel.add(oNodeLdToolsHelpPanel, "Tools help");
//			TabbedPane.add(oSelectViewPane, "Views");


			oNodeLdTasksHelpPanel.setDefaultButton();

//			oContentPane.add(oNodeLdTasksHelpPanel, BorderLayout.CENTER);
			oContentPane.add(ldHelpPanel, BorderLayout.CENTER);
			ldHelpPanel.setSelectedIndex(ILdUIConstants.iTASKSHELP_TAB);
		//	oContentPane.addFocusListener( new FocusListener() {
			ldHelpPanel.addFocusListener( new FocusListener() {
				public void focusGained(FocusEvent e) {
					if (firstFocus ) {
						/**  Let the user decide 	**/
						if (nSelectedTab == 0)
							oNodeLdTasksHelpPanel.setToolsPanelFocused();
						firstFocus = false;
						
					}
				}
				public void focusLost(FocusEvent e) {

				}
		});

	

		ldHelpPanel.addChangeListener( new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {
				int nIndex = ldHelpPanel.getSelectedIndex();
				if (nIndex == ILdUIConstants.iTASKSHELP_TAB) {
					// oNodeLdTasksHelpPanel.setToolsPanelFocused();
					oNodeLdTasksHelpPanel.setDefaultButton();
				}
				else if (nIndex == ILdUIConstants.iTOOLSHELP_TAB) {
					oNodeLdToolsHelpPanel.setToolsPanelFocused();
					oNodeLdToolsHelpPanel.setDefaultButton();
				}
			}
		});
		
		pack();
		final Dimension size = getSize();

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {

				Dimension dim = getSize();

				//revert to optmized size if dialog is made smaller than the min opt size
				if((dim.height < size.height) || (dim.width < size.width))
					setSize(size);
				else {
					oNodeLdTasksHelpPanel.revalidate();
				}
			
		};
		});
	}
/** Return the instance of NodeSummary this instance is associated with
 * 
 * @return NodeSummary
 */ 	
	public NodeSummary getNode()	{
		return oNode;
	}

	/**	This UILdInformationDialog class offers the user two tabs, the tools tab and the tasks tab. 
	 * This is the UINodeLdHelpPanel for the tools help tab.
	 * 
	 * */
/**
 * Return the UILdInformationDialog's task tab panel
 * @return UINodeLdHelpPanel, the UILdInformationDialog's task tab panel
 */
public UINodeLdHelpPanel getNodeLdTasksHelpPanel() {
	return oNodeLdTasksHelpPanel;
}
/**
 * Set the value of UILdInformationDialog's task tab panel
 * @param UINodeLdHelpPanel nodeLdTasksHelpPanel, 
 * the value to set the task tab panel to.
 */
public void setNodeLdTasksHelpPanel(UINodeLdHelpPanel nodeLdTasksHelpPanel) {
	oNodeLdTasksHelpPanel = nodeLdTasksHelpPanel;
}

/**
 * Return the UILdInformationDialog's tools tab panel
 * @return UINodeLdHelpPanel oNodeLdToolsHelpPanel, the UILdInformationDialog's tools tab panel
 */
public UINodeLdHelpPanel getNodeLdToolsHelpPanel() {
	return oNodeLdToolsHelpPanel;
}

/**
 * Set the value of UILdInformationDialog's tools tab panel
 * @param UINodeLdHelpPanel nodeLdToolsHelpPanel, 
 * the value to set the task tab panel to.
 */
public void setNodeLdToolsHelpPanel(UINodeLdHelpPanel nodeLdToolsHelpPanel) {
	oNodeLdToolsHelpPanel = nodeLdToolsHelpPanel;
}

}
