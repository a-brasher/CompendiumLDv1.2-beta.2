/******************************************************************************
 *                                                                            *
/*  (c) Copyright 2009 Verizon Communications USA and The Open University UK    *
 *                                                                            *
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
 *                                                                            *
 ******************************************************************************/
/**
 * Development of this class has started  from a copy of class UINodePopupMenu.
 * However, have aborted because need to turn UINodePopupMenu class into a class 
 * which can be sensibly subclassed. 
 * Do this when all ld operations have been decided on.  
 * to create this a s a subclass.
 */
package com.compendium.learningdesign.ui.popups;

import java.awt.event.*;
import java.util.*;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;

import com.compendium.core.CoreUtilities;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;

import com.compendium.ProjectCompendium;

import com.compendium.core.*;
import com.compendium.meeting.*;
import com.compendium.ui.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.edits.*;
import com.compendium.ui.dialogs.UIExportDialog;
import com.compendium.ui.dialogs.UIExportViewDialog;
import com.compendium.ui.dialogs.UIImportDialog;
import com.compendium.ui.dialogs.UIImportFlashMeetingXMLDialog;
import com.compendium.ui.dialogs.UIReadersDialog;
import com.compendium.ui.dialogs.UITrashViewDialog;
import com.compendium.io.udig.UDigClientSocket;
import com.compendium.ui.popups.UINodePopupMenu;
import com.compendium.learningdesign.core.ILdCoreConstants;
import com.compendium.learningdesign.ui.UILdImages;
import com.compendium.learningdesign.ui.UILdNode;
import com.compendium.learningdesign.ui.UILdTaskNode;
import com.compendium.learningdesign.ui.dialogs.UILdTaskTimeDialog;
import com.compendium.learningdesign.ui.panels.UINodeLdHelpToolPanel;
import com.compendium.learningdesign.ui.panels.nodecontent.*;
import com.compendium.learningdesign.ui.UILdActivityViewFrame;
import com.compendium.learningdesign.ui.UILdViewPane;


/**
 * This class draws and handles events for the right-click menu for Learning desgn nodes in a map
 *
 * @author	Andrew Brasher
 */
public class UILdNodePopupMenu extends UINodePopupMenu   implements ActionListener {

	/** The default width for this popup menu.*/
	private static final int WIDTH					= 100;

	/** The default height for this popup menu.*/
	private static final int HEIGHT					= 50;
	
	/**
	 * The x value for the location of this popup menu.
	 * @uml.property  name="nX"
	 */
	private int				nX						= 0;

	/**
	 * The y value for the location of this popup menu.
	 * @uml.property  name="nY"
	 */
	private int				nY						= 0;
	
	/**
	 * The platform specific shortcut key used to access menus and thier options.
	 * @uml.property  name="shortcutKey"
	 */
	private int 		shortcutKey;
	
	/**
	 * The NodeUI object associated with this popup menu. Note the NodeUI class handles the look and feel.
	 * @uml.property  name="oNode"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private NodeUI			oNode					= null;
	
	/**
	 * The UIViewPane object associated with this popup menu.
	 * @uml.property  name="oViewPane"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private UIViewPane		oViewPane				= null;
	
	/**
	 * The JMenuItem to open this node's contents dialog.
	 * @uml.property  name="miMenuItemOpen"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JMenuItem		miMenuItemOpen			= null;

	/**
	 * The JMenuItem to perform a copy operation.
	 * @uml.property  name="miMenuItemCopy"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miMenuItemCopy			= null;

	/**
	 * The JMenuItem to perform a cut operation.
	 * @uml.property  name="miMenuItemCut"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miMenuItemCut			= null;

	/**
	 * The menu item to create an internal reference node to this node.
	 * @uml.property  name="miInternalReference"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JMenuItem		miInternalReference		= null;
	
	/**
	 * The JMenuItem to create a clone of the currently selected nodes, or the node associated with this popup.
	 * @uml.property  name="miMenuItemClone"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miMenuItemClone			= null;
	
	/**
	 * The JMenuItem to delete the currently selected nodes, or the node associated with this popup.
	 * @uml.property  name="miMenuItemDelete"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miMenuItemDelete		= null;
	
	/**
	 * The JMenu for node type change options.
	 * @uml.property  name="mnuChangeType"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JMenu			mnuChangeType			= null;
	
	/**
	 * The JMenuItem to change the selected nodes to Argument nodes.
	 * @uml.property  name="miTypeArgument"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miTypeArgument			= null;

	/**
	 * The JMenuItem to change the selected nodes to Con nodes.
	 * @uml.property  name="miTypeCon"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miTypeCon				= null;

	/**
	 * The JMenuItem to change the selected nodes to Issue nodes.
	 * @uml.property  name="miTypeIssue"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miTypeIssue				= null;

	/**
	 * The JMenuItem to change the selected nodes to Position nodes.
	 * @uml.property  name="miTypePosition"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miTypePosition			= null;

	/**
	 * The JMenuItem to change the selected nodes to Pro nodes.
	 * @uml.property  name="miTypePro"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miTypePro				= null;
	
	/**
	 * The JMenuItem to change the selected nodes to Decision nodes.
	 * @uml.property  name="miTypeDecision"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miTypeDecision			= null;

	/**
	 * The JMenuItem to change the selected nodes to Note nodes.
	 * @uml.property  name="miTypeNote"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miTypeNote				= null;

	/**
	 * The JMenuItem to change the selected nodes to Refrence nodes.
	 * @uml.property  name="miTypeReference"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miTypeReference			= null;

	/**
	 * The JMenuItem to change the selected nodes to List nodes.
	 * @uml.property  name="miTypeList"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miTypeList				= null;

	/**
	 * The JMenuItem to change the selected nodes to Map nodes.
	 * @uml.property  name="miTypeMap"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miTypeMap				= null;

	/**************** The specifically learning design popup menu items follow	*****************/ 
	/**
	 * The JMenuItem to change the selected nodes type to the LD Activity type*
	 * @uml.property  name="miLdTypeActivity"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miLdTypeActivity			= null;
	
	/**
	 * The JMenuItem to change the selected nodes type to the LD Assignment type *
	 * @uml.property  name="miLdTypeAssignment"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miLdTypeAssignment			= null;
	
	/**
	 * The JMenuItem to change the selected nodes type to the LD learning outcome type *
	 * @uml.property  name="miLdTypeOutput"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miLdTypeOutcome			= null;
	
	/**
	 * The JMenuItem to change the selected nodes type to the LD Output type *
	 * @uml.property  name="miLdTypeOutput"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miLdTypeOutput			= null;
	
	/**
	 * The JMenuItem to change the selected nodes type to the LD Resource type *
	 * @uml.property  name="miLdTypeResource"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miLdTypeResource			= null;
	
	/**
	 * The JMenuItem to change the selected nodes type to the LD Role type *
	 * @uml.property  name="miLdTypeRole"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miLdTypeRole			= null;
	
	/**
	 * The JMenuItem to change the selected nodes LD type to the LD stop type */
	
	private JMenuItem		miLdTypeStop			= null;
	
	/**
	 * The JMenuItem to change the selected nodes LD type to the LD Task type *
	 * @uml.property  name="miLdTypeTask"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miLdTypeTask			= null;
	
	/**
	 * The JMenuItem to change the selected nodes type to the LD VLE Tool type *
	 * @uml.property  name="miLdTypeVleTool"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miLdTypeVleTool			= null;
	
	/** The JMenuItem to change the selected nodes LD role type to student **/
	/**
	 * Open a broswer window and run a search on e.g. KN  for the current node's label text.
	 * @uml.property  name="miLdRoleTypeStudent"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JMenuItem		miLdRoleTypeStudent			= null;
	
	/**
	 * The JMenuItem to change the selected nodes LD role type to "student representing group" *
	 * Note this is no longer used: replaced with "other".
	 * @uml.property  name="miLdRoleTypeStudentGroup"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JMenuItem		miLdRoleTypeStudentGroup			= null;
	/**
	 * The JMenuItem to change the selected nodes LD role type *
	 * @uml.property  name="miLdRoleTypeTutor"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JMenuItem		miLdRoleTypeTutor			= null;
	
	/**
	 * The JMenuItem to change the selected nodes LD role type to other *
	 * @uml.property  name="miLdRoleTypeTutor"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JMenuItem		miLdRoleTypeOther			= null;
	
	
	/**
	 * The JMenuItem to change the selected nodes assessment type to "formative assessment" *
	 */
	private JMenuItem		miLdOutputAssessmentTypeFormative			= null;
	
	/**
	 * The JMenuItem to change the selected nodes assessment type to "summmative assessment" *
	 */
	private JMenuItem		miLdOutputAssessmentTypeSummative			= null;
	
	/**
	 * The JMenuItem to change the selected nodes assessment type to "other assessment" *
	 * This option will also be used if the user is not sure which assessment type to use
	 */
	private JMenuItem		miLdOutputAssessmentTypeOther			= null;
	
	/**
	 * Open a broswer window and run a search on e.g. KN  for the current node's label text.
	 * @uml.property  name="miToolHelpSearch"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JMenuItem		miToolHelpSearch			= null;
	
	/**
	 * The JMenuItem to change the selected nodes LD tool type *
	 * @uml.property  name="miLdToolTypeBlog"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JMenuItem		miLdToolTypeBlog			= null;
	
	/**
	 * The JMenuItem to change the selected nodes LD tool type *
	 * @uml.property  name="miLdToolTypeWiki"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JMenuItem		miLdToolTypeWiki			= null;
	
	/**
	 * The JMenuItem to change the selected nodes LD tool type *
	 * @uml.property  name="miLdToolTypeEPortfolio"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JMenuItem		miLdToolTypeEPortfolio			= null;
	
	/**
	 * The JMenuItem to change the selected nodes LD tool type to Instant Messaging *
	 * @uml.property  name="miLdToolTypeIM"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JMenuItem		miLdToolTypeIM			= null;
		
	/**
	 * The JMenuItem to change the selected nodes LD tool type to a Discussion forum*
	 * @uml.property  name="miLdToolTypeForum"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JMenuItem		miLdToolTypeForum			= null;
	
	/** The JMenuItem to change the selected nodes LD tool type to a Chat - USE INSTANT MESSAGING INSTEAD  **/
	// private JMenuItem		miLdToolTypeChat			= null;
	
	/**
	 * The JMenuItem to change the selected nodes LD tool type to 'Other' *
	 * @uml.property  name="miLdToolTypeOther"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JMenuItem		miLdToolTypeOther			= null;
	
	/**
	 * The JMenuItem to change the selected nodes LD tool type to a Podcast *
	 * @uml.property  name="miLdToolTypePodcast"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JMenuItem		miLdToolTypePodcast			= null;
	
	/**
	 * The JMenuItem to change the selected nodes LD tool type to a Simulation *
	 * @uml.property  name="miLdToolTypeSimulation"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JMenuItem		miLdToolTypeSimulation			= null;
	
	/**
	 * The JMenuItem to change the selected nodes LD tool type to a Virtual world *
	 * @uml.property  name="miLdToolTypeVW"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JMenuItem		miLdToolTypeVW			= null;
	
	/**
	 * The JMenu for LD node type change options.
	 * @uml.property  name="mnuLDChangeType"
	 * @uml.associationEnd  
	 */
	private JMenu			mnuLDChangeType			= null;
	
	/**
	 * Pop up learning design help for the current node
	 * @uml.property  name="miLDHelp"
	 * @uml.associationEnd  
	 */
	private JMenuItem		miLDHelp			= null;
	
	/**
	 * Pop up for LD entering learning time for a task node
	 * @uml.property  name="miLDLearningTime"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JMenuItem		miLDLearningTime			= null;
	
	/** End of the learning design popup menu items.	******************/
	/**
	 * @param title
	 * @param nodeui
	 * @param ldType
	 */
	
	
	public UILdNodePopupMenu(String title, NodeUI nodeui, int ldType) {
		super(title);

		oViewPane = nodeui.getUINode().getViewPane();

		shortcutKey = ProjectCompendium.APP.shortcutKey;
		setNode(nodeui);

		int nType = nodeui.getUINode().getNode().getType();	
		int lxType = ((UILdNode) nodeui.getUINode()).getLdType();
		/** If its a Vle tool and the label has not yet been changed the menu presented 
		 * should prompt the user to select the 'real' tool type.
		 */		
		if (ldType != ILdCoreConstants.iLD_TYPE_NO_TYPE)	{
//			It is a LD icon; do not know which type yet, but it is an LD icon.
			switch (ldType) {
			case ILdCoreConstants.iLD_TYPE_VLE_TOOL :
			{	// It is a VleTool. First put up a menu if the node label is still the default one.
				if(nodeui.getUINode().getNode().getLdToolType() == ILdCoreConstants.iLD_TYPE_NO_TYPE) {
					createVleToolSelectMenu();
					createLDToolHelpMenuItem();
				}
				else { // A tool type (e.g. wiki or blog etc.) has already been selected
					createLDNodeMenu(nodeui);
					createLDToolHelpMenuItem();
				}
			}; break;
			case ILdCoreConstants.iLD_TYPE_ROLE :	
			{  // It is a role. Put up a menu with role types if label is still the default one.
				if(nodeui.getUINode().getNode().getLdRoleType() == ILdCoreConstants.iLD_TYPE_NO_TYPE) {
					createRoleSelectMenu();
				}
				else {
					createLDNodeMenu(nodeui);
				}

			}; break;
			case ILdCoreConstants.iLD_TYPE_ASSESSMENT :	
			{  // It is a learner output node. Put up a menu with assesment types if label is still the default one.
				if(nodeui.getUINode().getNode().getLdAssessmentType() == ILdCoreConstants.iLD_TYPE_NO_TYPE) {
					createLearnerOutputSelectMenu();
				}
				else {
					createLDNodeMenu(nodeui);
				}

			}; break;
			case ILdCoreConstants.iLD_TYPE_TASK :	
			{  // It is a task. Put up a menu with role types if label is still the default one.
				{
					createLDNodeMenu(nodeui);
				}

			}; break;
//			The default.				
			default: createLDNodeMenu(nodeui); break;
			}
		}

		else	// Beginning of not LD Vle tool branch
		{
			// Code deleted because it should no longer be used. Replace with try/catch?
		}  // End of not VLE Tool else
		/**
		 * If on the Mac OS and the Menu bar is at the top of the OS screen, remove the menu shortcut Mnemonics.
		 */
		if (ProjectCompendium.isMac && (FormatProperties.macMenuBar || (!FormatProperties.macMenuBar && !FormatProperties.macMenuUnderline)) )
			UIUtilities.removeMenuMnemonics(getSubElements());

		pack();		
		setSize(WIDTH,HEIGHT);
	}	
	
	/** Creates the default pop menu for a LD node.
	 * 
	 */
private JMenu createLDNodeMenu(NodeUI nodeui) {
	// Contents menu item
	miMenuItemOpen = new JMenuItem("Contents");
	int iLdType = nodeui.getUINode().getNode().getLdType();
	if (iLdType == ILdCoreConstants.iLD_TYPE_TASK)	{
		miMenuItemOpen.setText("Task information");
		miMenuItemOpen.setToolTipText("Open the window for editing task details");
		/** Create task time menu item if node is a task node ***/
		miLDLearningTime = new JMenuItem("Task time");
		if (nodeui.getUINode().getViewPane() instanceof UILdViewPane)
			miLDLearningTime.setToolTipText("Enter the time to be spent on this task");
		else {
			miLDLearningTime.setEnabled(false);	
			miLDLearningTime.setToolTipText("Setting and showing task times is only possible for tasks within Activity maps");
		}
		miLDLearningTime.setMnemonic(KeyEvent.VK_L);
		miLDLearningTime.addActionListener(this);
		
	}
	miMenuItemOpen.setMnemonic(KeyEvent.VK_O);
	miMenuItemOpen.addActionListener(this);
	add(miMenuItemOpen);
	/** Only show the time set dalog if it's a task node and if it's in a UILdViewPane
	 * - can change this but neeed to make sure code does something sensible if the
	 * noe is not in a UILdViewPane 
	 */
	//&& (nodeui.getUINode().getViewPane() instanceof UILdViewPane))
	if (iLdType == ILdCoreConstants.iLD_TYPE_TASK) 	{
			add(miLDLearningTime);
	}
	
// Need to edit the change node type menu so it allows to change roles and tools	
/** 	Do not include the ChangeNodeTypeMenu until have worked out how to change the icons.
 * 		Need to complete this coding (see CompendiumLD-to-do.doc). 18/03/2008
 */  
//	createChangeNodeTypeMenu();
// Learning time 
	
// Create internal reference node
	String sSource = nodeui.getUINode().getNode().getSource();
	
	if (!sSource.startsWith(ICoreConstants.sINTERNAL_REFERENCE)) {
		miInternalReference = new JMenuItem("Create 'go to'");
		miInternalReference.setToolTipText("Create a node which will go to this node from another point on the map");
		miInternalReference.setMnemonic(KeyEvent.VK_I);
		miInternalReference.addActionListener(this);
		add(miInternalReference);
		}

	
	createLdCopyMenuItems();
	
	// create and add the menu to allow the user to change the node type if it is not a Task or Activity node (because there's a bug in the code to change from a Task node)
	if (iLdType != ILdCoreConstants.iLD_TYPE_TASK && iLdType != ILdCoreConstants.iLD_TYPE_ACTIVITY)	{
		addSeparator();
		createChangeNodeTypeMenu();
	}
	if (iLdType == ILdCoreConstants.iLD_TYPE_VLE_TOOL)	{
		JMenu mnuToolSelect = createVleToolSelectMenu();
		add(mnuToolSelect);
	}

	if ( 
			(nodeui.getUINode().getNode().getLdType() == ILdCoreConstants.iLD_TYPE_VLE_TOOL)
			|| (nodeui.getUINode().getNode().getLdType() == ILdCoreConstants.iLD_TYPE_TASK))	{
		addSeparator();
		createLdHelpMenuItem();		
		// Note. Help for activities no longer included  (nodeui.getUINode().getNode().getLdType() == ILdCoreConstants.iLD_TYPE_ACTIVITY) 
	}

return mnuChangeType;
}

/**
 * Helper method which creates  menu item for Ld help.
 * 
 */	
	private void createLdHelpMenuItem() {
		miLDHelp = new JMenuItem("LD Help");
		miLDHelp.setToolTipText("Pop up help about relevant tools and activities");
		miLDHelp.setMnemonic(KeyEvent.VK_O);
		miLDHelp.addActionListener(this);
		add(miLDHelp);
	}

/**
 * Creates  menu items for copying a node.
 * @return the JMENU instance for selecting the tool type for a LD VLE tool node.
 */	
	private void createLdCopyMenuItems() {
		//Clone	
		addSeparator();
/**		miMenuItemClone = new JMenuItem("Clone");
		miMenuItemClone.setToolTipText("Copy node and its contents");
		miMenuItemClone.addActionListener(this);
		miMenuItemClone.setMnemonic(KeyEvent.VK_L);
		add(miMenuItemClone);
		**/
//		addSeparator();
//		Copy, Cut and Delete 	
		miMenuItemCopy = new JMenuItem("Copy", UIImages.get(IUIConstants.COPY_ICON));
		miMenuItemCopy.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_C, shortcutKey));
		miMenuItemCopy.setToolTipText("Copy the selected node");
		miMenuItemCopy.setMnemonic(KeyEvent.VK_C);
		miMenuItemCopy.addActionListener(this);
		/** Only add the menu option to transclude if the node is NOT a tsk node.
		 * Need to work out how to deal with the ttiming information for task nodes
		 * through transclusions before this option is enabled for task nodes.
		 */
/**		if (this.getNode().getUINode().getLdType() != ILdCoreConstants.iLD_TYPE_TASK)	{ 	}	**/
			add(miMenuItemCopy);
	
		
		miMenuItemCut = new JMenuItem("Cut", UIImages.get(IUIConstants.CUT_ICON));
		miMenuItemCut.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_X, shortcutKey));
		miMenuItemCut.setMnemonic(KeyEvent.VK_U);
		miMenuItemCut.addActionListener(this);				
		add(miMenuItemCut);
	

		miMenuItemDelete = new JMenuItem("Delete", UIImages.get(IUIConstants.DELETE_ICON));
		miMenuItemDelete.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0));
		miMenuItemDelete.addActionListener(this);
		miMenuItemDelete.setMnemonic(KeyEvent.VK_D);
		add(miMenuItemDelete);
		
	}

/**
 * Creates a menu for selecting the type of a VLE tool LD node.
 * @return the JMENU instance for selecting the tool type for a LD VLE tool node.
 */	
	private JMenu createVleToolSelectMenu() {
			if (this.getNode().getUINode().getNode().getLdToolType() == ILdCoreConstants.iLD_TYPE_NO_TYPE){
				mnuChangeType = new JMenu("Select Tool ...");
			}
			else	{
				mnuChangeType = new JMenu("Change Tool ...");
			}
			mnuChangeType.setMnemonic(KeyEvent.VK_Y);
			mnuChangeType.addActionListener(this);
			add(mnuChangeType);
			miLdToolTypeBlog = new JMenuItem("Blog"); 
			miLdToolTypeBlog.addActionListener(this);
			miLdToolTypeBlog.setMnemonic(KeyEvent.VK_Q);
			mnuChangeType.add(miLdToolTypeBlog);
/** Do  not need "Chat" - use "Instant messaging" instead
			miLdToolTypeChat = new JMenuItem("Chat"); 
			miLdToolTypeChat.addActionListener(this);
			miLdToolTypeChat.setMnemonic(KeyEvent.VK_A);
			mnuChangeType.add(miLdToolTypeChat);
**/
			miLdToolTypeEPortfolio = new JMenuItem("E-portfolio");
			miLdToolTypeEPortfolio.addActionListener(this);
			miLdToolTypeEPortfolio.setMnemonic(KeyEvent.VK_P);
			mnuChangeType.add(miLdToolTypeEPortfolio);
			
			miLdToolTypeForum = new JMenuItem("Forum");
			miLdToolTypeForum.addActionListener(this);
			miLdToolTypeForum.setMnemonic(KeyEvent.VK_M);
			mnuChangeType.add(miLdToolTypeForum);

			miLdToolTypeIM = new JMenuItem("Instant messaging");
			miLdToolTypeIM.addActionListener(this);
			miLdToolTypeIM.setMnemonic(KeyEvent.VK_L);
			mnuChangeType.add(miLdToolTypeIM);

			miLdToolTypePodcast = new JMenuItem("Podcast");
			miLdToolTypePodcast.addActionListener(this);
			miLdToolTypePodcast.setMnemonic(KeyEvent.VK_C);
			mnuChangeType.add(miLdToolTypePodcast);
			
			miLdToolTypeSimulation = new JMenuItem("Simulation");
			miLdToolTypeSimulation.addActionListener(this);
			miLdToolTypeSimulation.setMnemonic(KeyEvent.VK_C);
			mnuChangeType.add(miLdToolTypeSimulation);

			miLdToolTypeVW = new JMenuItem("Virtual world");
			miLdToolTypeVW.addActionListener(this);
			miLdToolTypeVW.setMnemonic(KeyEvent.VK_R);
			mnuChangeType.add(miLdToolTypeVW);

			miLdToolTypeWiki = new JMenuItem("Wiki");
			miLdToolTypeWiki.addActionListener(this);
			miLdToolTypeWiki.setMnemonic(KeyEvent.VK_N);
			mnuChangeType.add(miLdToolTypeWiki);
			
			mnuChangeType.addSeparator();
			
			miLdToolTypeOther = new JMenuItem("Other");
			miLdToolTypeOther.addActionListener(this);
			miLdToolTypeOther.setMnemonic(KeyEvent.VK_N);
			mnuChangeType.add(miLdToolTypeOther);
	
	return mnuChangeType;
	}

	/** Creates the menu item for tool help for a LD tool node.
	 * 
	 */
	private JMenuItem createLDToolHelpMenuItem() {
		miToolHelpSearch = new JMenuItem("Search for tool help");
		miToolHelpSearch.setToolTipText("Web search for help about tools"); 
		miToolHelpSearch.addActionListener(this);
		miToolHelpSearch.setMnemonic(KeyEvent.VK_T);
		add(miToolHelpSearch);
		
		return miToolHelpSearch;	
	}
		
	/**
	 * Creates a menu for selecting the type of a LD Role  node.
	 * The user can choose from: Student, tutor, other.
	 * Note that this uses some of the same instance variables that 
	 * createVleToolSelectMenu() uses; this should not matter as the menu
	 * does not persist. CHECK THAT THIS IS CORRECT !!!!
	 * @return the JMENU instnce for selecting the role type for a LD role node.
	 */	
	private JMenu createRoleSelectMenu() {
		
		mnuChangeType = new JMenu("Select Role ...");
		mnuChangeType.setMnemonic(KeyEvent.VK_Y);
		mnuChangeType.addActionListener(this);
		add(mnuChangeType);
		miLdRoleTypeStudent = new JMenuItem("Student"); 
		miLdRoleTypeStudent.addActionListener(this);
		miLdRoleTypeStudent.setMnemonic(KeyEvent.VK_Q);
		mnuChangeType.add(miLdRoleTypeStudent);
/**
		miLdRoleTypeStudentGroup = new JMenuItem("Student (representing group)"); 
		miLdRoleTypeStudentGroup.addActionListener(this);
		miLdRoleTypeStudentGroup.setMnemonic(KeyEvent.VK_A);
		miLdRoleTypeStudentGroup.setToolTipText("use when a student represents a group of students, e.g. as a chair person");
		mnuChangeType.add(miLdRoleTypeStudentGroup);
**/
		miLdRoleTypeTutor = new JMenuItem("Tutor");
		miLdRoleTypeTutor.addActionListener(this);
		miLdRoleTypeTutor.setMnemonic(KeyEvent.VK_P);
		mnuChangeType.add(miLdRoleTypeTutor);
		
		mnuChangeType.addSeparator();
		
		miLdRoleTypeOther = new JMenuItem("Other"); 
		miLdRoleTypeOther.addActionListener(this);
		miLdRoleTypeOther.setMnemonic(KeyEvent.VK_A);
		miLdRoleTypeOther.setToolTipText("Use for roles other than 'student' or 'tutor'");
		mnuChangeType.add(miLdRoleTypeOther);
				
return mnuChangeType;
}
	
	/**
	 * Creates a menu for selecting the type of an LD Assessment node.
	 * The user can choose from: formative, summative, other.
	 * Note that this uses some of the same instance variables that 
	 * createVleToolSelectMenu() uses; this should not matter as the menu
	 * does not persist. CHECK THAT THIS IS CORRECT !!!!
	 * @return the JMENU instnce for selecting the role type for a LD role node.
	 */	
	private JMenu createLearnerOutputSelectMenu() {
		
		mnuChangeType = new JMenu("Select assessment type...");
		mnuChangeType.setMnemonic(KeyEvent.VK_Y);
		mnuChangeType.addActionListener(this);
		add(mnuChangeType);
		miLdOutputAssessmentTypeFormative = new JMenuItem("Formative"); 
		miLdOutputAssessmentTypeFormative.addActionListener(this);
		miLdOutputAssessmentTypeFormative.setMnemonic(KeyEvent.VK_Q);
		mnuChangeType.add(miLdOutputAssessmentTypeFormative);
/**
		miLdRoleTypeStudentGroup = new JMenuItem("Student (representing group)"); 
		miLdRoleTypeStudentGroup.addActionListener(this);
		miLdRoleTypeStudentGroup.setMnemonic(KeyEvent.VK_A);
		miLdRoleTypeStudentGroup.setToolTipText("use when a student represents a group of students, e.g. as a chair person");
		mnuChangeType.add(miLdRoleTypeStudentGroup);
**/
		miLdOutputAssessmentTypeSummative = new JMenuItem("Summative");
		miLdOutputAssessmentTypeSummative.addActionListener(this);
		miLdOutputAssessmentTypeSummative.setMnemonic(KeyEvent.VK_P);
		mnuChangeType.add(miLdOutputAssessmentTypeSummative);
		
		mnuChangeType.addSeparator();
		
		miLdOutputAssessmentTypeOther = new JMenuItem("Other"); 
		miLdOutputAssessmentTypeOther.addActionListener(this);
		miLdOutputAssessmentTypeOther.setMnemonic(KeyEvent.VK_A);
		miLdOutputAssessmentTypeOther.setToolTipText("Use for assessment types other than 'formative' or 'summative'");
		mnuChangeType.add(miLdOutputAssessmentTypeOther);
				
return mnuChangeType;
}	
	
	/**
	 * Menu to change the node type.
	 * @return the JMenu
	 */
	private JMenu createChangeNodeTypeMenu()	{

		int ldType = oNode.getUINode().getNode().getLdType();
	// Set up standard change type menu	
		mnuChangeType = new JMenu("Change Type To ...");
		mnuChangeType.setMnemonic(KeyEvent.VK_Y);
		mnuChangeType.addActionListener(this);
		
		miTypeIssue = new JMenuItem("Question"); // issue renamed to question
		miTypeIssue.addActionListener(this);
		miTypeIssue.setMnemonic(KeyEvent.VK_Q);
		mnuChangeType.add(miTypeIssue);

		miTypePosition = new JMenuItem("Answer"); //position renamed to answer
		miTypePosition.addActionListener(this);
		miTypePosition.setMnemonic(KeyEvent.VK_A);
		mnuChangeType.add(miTypePosition);

		miTypeMap = new JMenuItem("Map");
		miTypeMap.addActionListener(this);
		miTypeMap.setMnemonic(KeyEvent.VK_M);
		mnuChangeType.add(miTypeMap);

		miTypeList = new JMenuItem("List");
		miTypeList.addActionListener(this);
		miTypeList.setMnemonic(KeyEvent.VK_L);
		mnuChangeType.add(miTypeList);

		miTypePro = new JMenuItem("Pro");
		miTypePro.addActionListener(this);
		miTypePro.setMnemonic(KeyEvent.VK_P);
		mnuChangeType.add(miTypePro);

		miTypeCon = new JMenuItem("Con");
		miTypeCon.addActionListener(this);
		miTypeCon.setMnemonic(KeyEvent.VK_C);
		mnuChangeType.add(miTypeCon);

		miTypeReference = new JMenuItem("Reference");
		miTypeReference.addActionListener(this);
		miTypeReference.setMnemonic(KeyEvent.VK_R);
		mnuChangeType.add(miTypeReference);

		miTypeNote = new JMenuItem("Note");
		miTypeNote.addActionListener(this);
		miTypeNote.setMnemonic(KeyEvent.VK_N);
		mnuChangeType.add(miTypeNote);

		miTypeDecision = new JMenuItem("Decision");
		miTypeDecision.addActionListener(this);
		miTypeDecision.setMnemonic(KeyEvent.VK_D);
		mnuChangeType.add(miTypeDecision);

		miTypeArgument = new JMenuItem("Argument");
		miTypeArgument.addActionListener(this);
		miTypeArgument.setMnemonic(KeyEvent.VK_U);
		mnuChangeType.add(miTypeArgument);
		
	// Set up LD change type menu
		// Type change is working but change of node icon for LD types is not yet, so 
		// this method is not called i.e. the menu is not created.
		// Need to complete this coding (see CompendiumLD-to-do.doc). 18/03/2008
		/** **/
		mnuLDChangeType = new JMenu("Change Learning Design Type To ..."); 
		mnuLDChangeType.setMnemonic(KeyEvent.VK_Y);
		mnuLDChangeType.addActionListener(this);

		miLdTypeActivity = new JMenuItem("Activity");
		miLdTypeActivity.addActionListener(this);
		miLdTypeActivity.setMnemonic(KeyEvent.VK_U);
/**
 * DO NOT add option to change to activity type until issues with changes
 * between task, map, reference and other standard node types are resolved 
 		if (ldType != ILdCoreConstants.iLD_TYPE_ACTIVITY)

			mnuLDChangeType.add(miLdTypeActivity);
**/		
		miLdTypeOutput = new JMenuItem("Learner output");
		miLdTypeOutput.addActionListener(this);
		miLdTypeOutput.setMnemonic(KeyEvent.VK_U);
		if (ldType != ILdCoreConstants.iLD_TYPE_ASSESSMENT)
			mnuLDChangeType.add(miLdTypeOutput);
		
		miLdTypeOutcome = new JMenuItem("Learning outcome");
		miLdTypeOutcome.addActionListener(this);
		miLdTypeOutcome.setMnemonic(KeyEvent.VK_U);
		if (ldType != ILdCoreConstants.iLD_TYPE_LEARNING_OUTCOME)
			mnuLDChangeType.add(miLdTypeOutcome);
		
		miLdTypeResource = new JMenuItem("Resource");
		miLdTypeResource.addActionListener(this);
		miLdTypeResource.setMnemonic(KeyEvent.VK_U);
		if (ldType != ILdCoreConstants.iLD_TYPE_RESOURCE)
			mnuLDChangeType.add(miLdTypeResource);
			
		miLdTypeRole = new JMenuItem("Role");
		miLdTypeRole.addActionListener(this);
		miLdTypeRole.setMnemonic(KeyEvent.VK_U);
		if (ldType != ILdCoreConstants.iLD_TYPE_ROLE)
			mnuLDChangeType.add(miLdTypeRole);
		
		miLdTypeStop = new JMenuItem("Stop");
		miLdTypeStop.addActionListener(this);
		miLdTypeStop.setMnemonic(KeyEvent.VK_U);
		if (ldType != ILdCoreConstants.iLD_TYPE_STOP)
			mnuLDChangeType.add(miLdTypeStop);

		
		miLdTypeTask = new JMenuItem("Task");
		miLdTypeTask.addActionListener(this);
		miLdTypeTask.setMnemonic(KeyEvent.VK_U);
/**		
 * Don't add change to task menu option because it's not working yet
 * 		if (ldType != ILdCoreConstants.iLD_TYPE_TASK)
			mnuLDChangeType.add(miLdTypeTask);
*/		
		miLdTypeVleTool = new JMenuItem("Tool");
		miLdTypeVleTool.addActionListener(this);
		miLdTypeVleTool.setMnemonic(KeyEvent.VK_U);
		if (ldType != ILdCoreConstants.iLD_TYPE_VLE_TOOL)
			mnuLDChangeType.add(miLdTypeVleTool);
		
		if (ldType == ILdCoreConstants.iLD_TYPE_NO_TYPE)	{
/** Note - options to change from a Ld node to a non ld node commented out 
 * 	becuase they're not working yet. Trial code still in place though  **/			
	// The node is not a LD node		
			add(mnuChangeType);
	//		mnuChangeType.add(mnuLDChangeType);
			return mnuChangeType;
		}
		else	{
			add(mnuLDChangeType);
	//		mnuLDChangeType.add(mnuChangeType);		
			return mnuLDChangeType;
		}
		
		
	}
	
	/**
	 * Handles the event of an option being selected.
	 * @param evt, the event associated with the option being selected.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();

		ProjectCompendium.APP.setWaitCursor();

						
		if (source.equals(miInternalReference)) {
			onCreateInternalLink();
		}		
					

		else if(source.equals(miMenuItemDelete)) {

			// delete all the selected nodes if user MULTISELECTs otherwise
			// delete node in focus
		 	// record the effect of the deletion
			// need to pass to this method the info you need to recreate the nodes/links

			DeleteEdit edit = new DeleteEdit(oViewPane.getViewFrame());
			if(oViewPane.getNumberOfSelectedNodes() >= 1) {
				oViewPane.deleteSelectedNodesAndLinks(edit);
			}
			else {
				oNode.deleteNodeAndLinks(oNode.getUINode(), edit);
			}

			// notify the listeners
			oViewPane.getViewFrame().getUndoListener().postEdit(edit);

			//Thread thread = new Thread() {
			//	public void run() {
					ProjectCompendium.APP.setTrashBinIcon();
			//	}
			//};
			//thread.start();
		}

		else if(source.equals(miMenuItemClone)) {

			int nOffset = 55;

			Hashtable cloneNodes = new Hashtable();
			Vector uinodes = new Vector(50);

			UIViewPane oViewPane = oNode.getUINode().getViewPane();
			ViewPaneUI oViewPaneUI = oViewPane.getViewPaneUI();

			if(oViewPane.getNumberOfSelectedNodes() > 1) {

				//delink all selected nodes if any
				for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {
					UINode uinode = (UINode)e.nextElement();
					NodeUI nodeui = (uinode.getUI());
					ProjectCompendium.APP.setStatus("Cloning  " + nodeui.getUINode().getNode().getLabel());
					int x = uinode.getX();
					int y = uinode.getY();

					UINode tmpuinode = oViewPaneUI.createCloneNode(uinode, x+nOffset, y+nOffset);

					cloneNodes.put(uinode,tmpuinode);
					uinodes.addElement(tmpuinode);
				}

				oViewPane.setSelectedNode(null, ICoreConstants.DESELECTALL);

				for(int i=0;i<uinodes.size();i++) {
					UINode uinode = (UINode)uinodes.elementAt(i);
					uinode.requestFocus();
					uinode.setSelected(true);
					oViewPane.setSelectedNode(uinode,ICoreConstants.MULTISELECT);
				}
				ProjectCompendium.APP.setStatus("");
			}
			else {
				// clone the node
				UINode uinode = oNode.getUINode();
				int x = uinode.getX();
				int y = uinode.getY();

				Point pos = new Point(x, y);

				// MOVE THE MOUSE POINTER TO THE CORRECT POSITION
				try {
					Point mousepos = new Point(pos.x, pos.y);
					SwingUtilities.convertPointToScreen(mousepos, oViewPane);
					Robot rob = new Robot();

					// MOVE X AN Y FOR CUSRER SO NOT RIGHT ON EDGE OF NODE
					mousepos.x += 20;
					mousepos.y += 20;

					rob.mouseMove( mousepos.x+nOffset, mousepos.y+nOffset);
				}
				catch(AWTException ex) {}

				UINode cloneNode = oViewPaneUI.createCloneNode(uinode, pos.x+nOffset, pos.y+nOffset);

				oViewPane.setSelectedNode(null, ICoreConstants.DESELECTALL);
				oViewPane.setSelectedLink(null, ICoreConstants.DESELECTALL);

				cloneNode.requestFocus();
				cloneNode.setSelected(true);
				oViewPane.setSelectedNode(cloneNode, ICoreConstants.SINGLESELECT);
			}

			if (oViewPane.getNumberOfSelectedLinks() > 0) {
				Vector linkList = new Vector();
				for(Enumeration e = oViewPane.getSelectedLinks();e.hasMoreElements();) {
					UILink link = (UILink)e.nextElement();
					UINode uiFrom = link.getFromNode();
					UINode uiTo = link.getToNode();
					if ((cloneNodes.get(uiFrom) != null) && (cloneNodes.get(uiTo) != null) ) {
						UILink tmpLink = (uiFrom.getUI()).createLink(
									(UINode)cloneNodes.get(uiFrom),
									(UINode)cloneNodes.get(uiTo),
									link.getLink().getType(),
									link.getLink().getArrow());
						linkList.addElement(tmpLink);
					}
				}
				oViewPane.setSelectedLink(null, ICoreConstants.DESELECTALL);

				for(int i=0;i<linkList.size();i++) {
					UILink uiLink = (UILink)linkList.elementAt(i);
					uiLink.setSelected(true);
					oViewPane.setSelectedLink(uiLink,ICoreConstants.MULTISELECT);
				}
			}
			else {
				//System.out.println("Number of selected links is zero");
			}
		}
		else if(source.equals(miMenuItemCopy)) {

			UIViewPane oViewPane = oNode.getUINode().getViewPane();
			UINode uinode = oNode.getUINode();
			if (uinode.isSelected()) {
				uinode.getViewPane().getViewPaneUI().copyToClipboard(null);
			}
			else {
				uinode.getViewPane().getViewPaneUI().copyToClipboard(oNode);
			}
			uinode.requestFocus();
		}
		else if(source.equals(miMenuItemCut)) {
			UIViewPane oViewPane = oNode.getUINode().getViewPane();
			//select the node first and then cut it to clipboard.
			UINode uinode = oNode.getUINode();
			if (uinode.isSelected()) {
				uinode.getViewPane().getViewPaneUI().cutToClipboard(null);
			}
			else {
				uinode.getViewPane().getViewPaneUI().cutToClipboard(oNode);
			}
		}
		else if(source.equals(miMenuItemOpen)) {
			// open the node
			if(oNode.getUINode().getNode().getType() == ICoreConstants.TRASHBIN) {
				UITrashViewDialog dlgTrash = new UITrashViewDialog(ProjectCompendium.APP, oNode);
				UIUtilities.centerComponent(dlgTrash, ProjectCompendium.APP);
				dlgTrash .setVisible(true);
			}
			else {
				oNode.openEditDialog(false);
			}
			oNode.getUINode().requestFocus();
		}


/***	Added by Andrew		***/
		else if (source.equals(miToolHelpSearch)) {
			String sLabel = oNode.getUINode().getText();
			try {
				sLabel = CoreUtilities.cleanURLText(sLabel);
			} catch (Exception e) {}
			if (sLabel.equals("Tool"))	{
				// Specific tool has not been selected so search for all tools. Note Google uses %22 for " character 
				String sSearchText = "blog OR e-portfolio OR forum OR %22instant messaging%22 OR  podcast OR simulation OR %22virtual world%22 OR wiki";
				ExecuteControl.launch( "http://www.google.com/cse?cx=000971387191123125524%3Alworuyth0qs&q="+sSearchText+
				"&sa=Search&cof=FORID%3A0&ie=utf-8&oe=utf-8");
			}
			else	{
			ExecuteControl.launch( "http://www.google.com/cse?cx=000971387191123125524%3Alworuyth0qs&q="+"%22"+sLabel+"%22"+
					"&sa=Search&cof=FORID%3A0&ie=utf-8&oe=utf-8");
			}
			oNode.getUINode().requestFocus();
		}
		else if (source.equals(miLDLearningTime))	{
			onSetTaskTime();
		}
		else if (source.equals(miLdOutputAssessmentTypeFormative ))	{
			String label = ILdCoreConstants.sOUTPUT_LABEL + " (FA)";
			onRefineLDType(ILdCoreConstants.iLD_TYPE_ASSESSMENT_FORMATIVE, label);
		}
		
		else if (source.equals(miLdOutputAssessmentTypeSummative ))	{
			String label = ILdCoreConstants.sOUTPUT_LABEL + " (SA)";
			onRefineLDType(ILdCoreConstants.iLD_TYPE_ASSESSMENT_SUMMATIVE, label);
		}
		
/***	End of added by Andrew		***/		

		else if(source.equals(miTypeIssue)) {
			onChangeType(ICoreConstants.ISSUE);
		}
		else if(source.equals(miTypePosition)) {
			onChangeType(ICoreConstants.POSITION);
		}
		else if(source.equals(miTypeMap)) {

			int count = 0;
			for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {
				count++;
				UINode uinode = (UINode)e.nextElement();
				onChangeTypeToMap(uinode);
			}

			// IF THERE ARE NO SELECTED NODE, TREAT IT AS THE CURRENT NODE
			if (count == 0) {
				onChangeTypeToMap(oNode.getUINode());
			}
		}
		else if(source.equals(miTypeList)) {

			int count = 0;
			for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {
				count++;
				UINode uinode = (UINode)e.nextElement();
				onChangeTypeToList(uinode);
			}

			// IF THERE ARE NO SELECTED NODE, TREAT IT AS THE CURRENT NODE
			if (count == 0) {
				onChangeTypeToList(oNode.getUINode());
			}
		}
		else if(source.equals(miTypePro)) {
			onChangeType(ICoreConstants.PRO);
		}
		else if(source.equals(miTypeCon)) {
			onChangeType(ICoreConstants.CON);
		}
		else if(source.equals(miTypeArgument)) {
			onChangeType(ICoreConstants.ARGUMENT);
		}
		else if(source.equals(miTypeDecision)) {
			onChangeType(ICoreConstants.DECISION);
		}
		else if(source.equals(miTypeNote)) {
			onChangeType(ICoreConstants.NOTE);
		}
		else if(source.equals(miTypeReference)) {
			onChangeType(ICoreConstants.REFERENCE);
		}

/** Added by Andrew 	**/		
		/** Refine tool types  **/
		else if (source.equals(miLdToolTypeBlog)) {
			String label = "Blog";
			onRefineLDType(ILdCoreConstants.iLD_TYPE_VLE_TOOL_BLOG, label);
//			oViewPane.showToolsHelp(oNode.getUINode(), label);
			//((UILdNode)(oNode.getUINode())).showLdHelpDialog();
		}		
		else if (source.equals(miLdToolTypeForum)) {
			String label = "Forum";
			onRefineLDType(ILdCoreConstants.iLD_TYPE_VLE_TOOL_FORUM, label);
		}
		else if (source.equals(miLdToolTypeIM)) {
			String label = "Instant messaging";
			onRefineLDType(ILdCoreConstants.iLD_TYPE_VLE_TOOL_IM, label);
		}
		else if (source.equals(miLdToolTypeEPortfolio)) {
			String label = "E-portfolio";
			onRefineLDType(ILdCoreConstants.iLD_TYPE_VLE_TOOL_EP, label);
		}
		else if (source.equals(miLdToolTypePodcast)) {
			String label = "Podcast";
			onRefineLDType(ILdCoreConstants.iLD_TYPE_VLE_TOOL_PODCAST, label);
		}
		else if (source.equals(miLdToolTypeSimulation)) {
			String label = "Simulation";
			onRefineLDType(ILdCoreConstants.iLD_TYPE_VLE_TOOL_SIM, label);
		}
		else if (source.equals(miLdToolTypeVW)) {
			String label = "Virtual world";
			onRefineLDType(ILdCoreConstants.iLD_TYPE_VLE_TOOL_VW, label);
		}
		else if (source.equals(miLdToolTypeWiki)) {
			String label = "Wiki";
			onRefineLDType(ILdCoreConstants.iLD_TYPE_VLE_TOOL_WIKI, label);
		}
		else if (source.equals(miLdToolTypeOther)) {
			String label = "Other tool";
			onRefineLDType(ILdCoreConstants.iLD_TYPE_VLE_TOOL_OTHER, label);
		}
/** End of change tool types  **/
/** Change role types	********/
		else if (source.equals(miLdRoleTypeStudent)) {
			String label = "Student";
			onRefineLDType(ILdCoreConstants.iLD_TYPE_ROLE_STUDENT, label);
		}
		else if (source.equals(miLdRoleTypeStudentGroup)) {
			String label = "Student group representative";
			onRefineLDType(ILdCoreConstants.iLD_TYPE_ROLE_GROUP, label);
		}
		else if (source.equals(miLdRoleTypeTutor)) {
			String label = "Tutor";
			onRefineLDType(ILdCoreConstants.iLD_TYPE_ROLE_TUTOR, label);
		}
			else if (source.equals(miLdRoleTypeOther)) {
				String label = "Other";
				onRefineLDType(ILdCoreConstants.iLD_TYPE_ROLE_OTHER, label);
		}
/*** End of change role types	**/
/** Change Ld node types	***/
		else if(source.equals(miLdTypeActivity)) {
			String label = ILdCoreConstants.sACTIVITY_LABEL;
			onChangeLDType(ILdCoreConstants.iLD_TYPE_ACTIVITY, label);
		}
		
		else if(source.equals(miLdTypeOutput))	{
			String label = ILdCoreConstants.sOUTPUT_LABEL;
			onChangeLDType(ILdCoreConstants.iLD_TYPE_ASSESSMENT, label);
		}
		
		else if(source.equals(miLdTypeOutcome))	{
			String label = "Learning outcome";
			onChangeLDType(ILdCoreConstants.iLD_TYPE_LEARNING_OUTCOME, label);
		}
		
		else if(source.equals(miLdTypeResource))	{
			String label = ILdCoreConstants.sRESOURCE_LABEL;
			onChangeLDType(ILdCoreConstants.iLD_TYPE_RESOURCE, label);
		}
		
		else if(source.equals(miLdTypeRole))	{
			String label = ILdCoreConstants.sROLE_LABEL;
			onChangeLDType(ILdCoreConstants.iLD_TYPE_ROLE, label);
		}
		
		else if(source.equals(miLdTypeStop))	{
			String label = ILdCoreConstants.sSTOP_LABEL;
			onChangeLDType(ILdCoreConstants.iLD_TYPE_STOP, label);
		}
		
		else if(source.equals(miLdTypeTask))	{
			String label = ILdCoreConstants.sTASK_LABEL;
			onChangeLDType(ILdCoreConstants.iLD_TYPE_TASK, label);
		}
		
		else if(source.equals(miLdTypeVleTool))	{
			String label =ILdCoreConstants.sVLE_TOOL_LABEL;
			onChangeLDType(ILdCoreConstants.iLD_TYPE_VLE_TOOL, label);
		}
/**	End of change ld node types	**/
		else if(source.equals(miLDHelp )) {			
			((UILdNode)(oNode.getUINode())).showLdHelpDialog();
		}
/** End of added by Andrew	**/
		ProjectCompendium.APP.setDefaultCursor();
	}
	
	/**
	 * Added by Andrew
	 * Refines the selected learning design nodes/current node by making the node type more specific.
	 * For example, adding type of role (e.g. 'student') to a role type node, or type of tool
	 * (e.g. 'blog') to a tool type node.
	 * (Map and List node types have separate functions). 
	 *
	 * @param type, the type to change the selected nodes to.
	 * @see #onChangeTypeToList
	 * @see #onChangeTypeToMap
	 */
	private void onRefineLDType(int type, String label) {
		int count = 0;
		for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {
			count++;
			UILdNode uinode = (UILdNode)e.nextElement();
			uinode.setText(label);			
			uinode.refineLdType(type);
			oViewPane.setSelectedNode(uinode, ICoreConstants.MULTISELECT);
			uinode.requestFocus();
		}

		// IF THERE ARE NO SELECTED NODE, TREAT IT AS THE CURRENT NODE
		if (count == 0) {
			UILdNode uinode = (UILdNode) oNode.getUINode();
			uinode.setText(label);
			uinode.setLdType(type);
			oViewPane.setSelectedNode(uinode,ICoreConstants.MULTISELECT);
			uinode.requestFocus();
		}
	}	
	
	/**
	 * Change the selected nodes/current node to the given node type.
	 * (Map and List node types have separate functions).
	 *
	 * @param type, the type to change the selected nodes to.
	 * @see #onChangeTypeToList
	 * @see #onChangeTypeToMap
	 */
	private void onChangeType(int type) {

		int count = 0;
		for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {
			count++;
			UINode uinode = (UINode)e.nextElement();
			uinode.setType(type);
			oViewPane.setSelectedNode(uinode, ICoreConstants.MULTISELECT);
			uinode.requestFocus();
		}

		// IF THERE ARE NO SELECTED NODE, TREAT IT AS THE CURRENT NODE
		if (count == 0) {
			UINode uinode = oNode.getUINode();
			uinode.setType(type);
			oViewPane.setSelectedNode(uinode,ICoreConstants.MULTISELECT);
			uinode.requestFocus();
		}
	}

	/**
	 * Copied from UINodePopUpMenu class.
	 * Change the given node to a list type.
	 * @param uinode com.compendium.ui.UINode, the node to change the type for.
	 */
	private void onChangeTypeToList(UINode uinode) {

		if (uinode.setType(ICoreConstants.LISTVIEW)) {
			oViewPane.setSelectedNode(uinode,ICoreConstants.MULTISELECT);
			uinode.requestFocus();
		}
	}
	
	/**
	 * Copied from class UINodePopUpMenu
	 * Change the given node to a map type.
	 * @param uinode com.compendium.ui.UINode, the node to change the type for.
	 */
	private void onChangeTypeToMap(UINode uinode) {

		if (uinode.setType(ICoreConstants.MAPVIEW) || uinode.setType(ICoreConstants.LDMAPVIEW)) {
			oViewPane.setSelectedNode(uinode,ICoreConstants.MULTISELECT);
			uinode.requestFocus();
		}
	}
	/**
	 * Added by Andrew
	 * Change the selected learning design nodes/current node to the given node type.
	 * (Map and List node types have separate functions). 
	 *
	 * @param type, the type to change the selected nodes to.
	 * @see #onChangeTypeToList
	 * @see #onChangeTypeToMap
	 */
	private void onChangeLDType(int type, String label) {
/** Note the type attribute is not yet used in this method. It only changes the node label.
 * To really set the node LD  type requires changing the LD type tag, and the icon.
 */
		int count = 0;
		for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {
			count++;
			UILdNode uinode = (UILdNode)e.nextElement();
			//uinode.setText(label);
			uinode.setLdType(type);
			oViewPane.setSelectedNode(uinode, ICoreConstants.MULTISELECT);
			uinode.requestFocus();
			uinode.showLdSelectionPopUpMenu(this.getNode(), this.getX(), this.getY());
		}

		// IF THERE ARE NO SELECTED NODE, TREAT IT AS THE CURRENT NODE
		if (count == 0) {
			UILdNode uinode = (UILdNode) oNode.getUINode();
			//uinode.setText(label);
			uinode.setLdType(type);
			oViewPane.setSelectedNode(uinode,ICoreConstants.MULTISELECT);
			uinode.requestFocus();
			uinode.showLdSelectionPopUpMenu(this.getNode(), this.getX(), this.getY());
		}
		
		
	}	
	
	/**
	 * Copied from class UINodePopUpMenu
	 * Create a Reference node with internal link to this node.
	 */
	private void onCreateInternalLink() {

		UINode uinode = oNode.getUINode();		
		double scale = uinode.getScale();

		UINode newNode = null;

		UIViewPane oViewPane = uinode.getViewPane();
		View view = oViewPane.getView();
		
		String sRef = ICoreConstants.sINTERNAL_REFERENCE+view.getId()+"/"+uinode.getNode().getId();

		// Do all calculations at 100% scale and then scale back down if required.
		if (oViewPane != null) {
			
			if (scale != 1.0) {
				oViewPane.scaleNode(uinode, 1.0);
			}
			
			ViewPaneUI oViewPaneUI = oViewPane.getViewPaneUI();
			if (oViewPaneUI != null) {

				int parentHeight = uinode.getHeight();
				int parentWidth = uinode.getWidth();

				Point loc = uinode.getNodePosition().getPos();
				loc.x += parentWidth;
				loc.x += 100;

				// CREATE NEW NODE RIGHT OF THE GIVEN NODE WITH THE GIVEN LABEL
				newNode = oViewPaneUI.createNode(ICoreConstants.REFERENCE,
								 "",
								 ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
								 "GO TO: "+uinode.getText(),
								 "( "+view.getLabel()+" )",
								 loc.x,
								 loc.y,
								 sRef
								 );
				
				if (scale != 1.0) {
					oViewPane.scaleNode(newNode, 1.0);
				}

				//Adjust y location for height variation so new node centered.
				int childHeight = newNode.getHeight();

				int locy = 0;
				if (parentHeight > childHeight) {
					locy = loc.y + ((parentHeight-childHeight)/2);
				}
				else if (childHeight > parentHeight) {
					locy = loc.y - ((childHeight-parentHeight)/2);
				}

				if (locy > 0 && locy != loc.y) {
					loc.y = locy;
					(newNode.getNodePosition()).setPos(loc);
					try {
						oViewPane.getView().setNodePosition(newNode.getNode().getId(), loc);
					}
					catch(Exception ex) {
						System.out.println(ex.getMessage());
					}
				}
				if (scale != 1.0) {
					oViewPane.scaleNode(newNode, scale);
				}
			}
			
			if (scale != 1.0) {
				oViewPane.scaleNode(uinode, scale);
			}			
		}
	}	
	
	/**
	 * Helper method to create the set task time dialog.
	 */
	private void onSetTaskTime()	{
		UILdTaskNode oTaskNode = ((UILdTaskNode)oNode.getUINode());
		UILdActivityViewFrame oActivityViewFrame =  (UILdActivityViewFrame) oTaskNode.getViewPane().getViewFrame();
//		UILdTaskTimeDialog oTaskTimesDialog = new UILdTaskTimeDialog(oActivityViewFrame, "Set task time", false, oTaskNode);
		UILdTaskTimeDialog oTaskTimesDialog = new UILdTaskTimeDialog(oTaskNode, "Set task time", false, oActivityViewFrame);
//		JOptionPane.showInternalOptionDialog(oActivityViewFrame, new UILdTaskTimeDialog(ProjectCompendium.APP, "Set task time", false, oTaskNode),
	//			"Set task time", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, UILdImages.getNodeIcon(ILdCoreConstants.iLD_TYPE_ACTIVITY), null, null );
	}
	
	/**
	 * Copied from class UINodePopUpMenu.
	 * Set the node associated with this popup menu.
	 * @param node com.compendium.ui.plaf.NodeUI, the node associated with this popup menu.
	 */
	public void setNode(NodeUI node) {
		oNode = node;
	}
	
	/**
	 * Copied from class UINodePopUpMenu.
	 * Set the location to draw this popup menu at.
	 * @param x, the x position of this popup's location.
	 * @param y, the y position of this popup's location.
	 */
	public void setCoordinates(int x,int y) {
		nX = x;
		nY = y;
	}
	
	/**
	 * Copied from class UINodePopUpMenu.
	 * Set the UIViewPane associated with this popup menu.
	 * @param viewPane com.compendium.ui.UIViewPane, the UIViewPane associated with this popup menu.
	 */
	public void setViewPane(UIViewPane viewPane) {
		oViewPane = viewPane;
	}

	/**
	 * Return the NodeUI instance this popup is associated with.
	 * @return the oNode
	 */
	public NodeUI getNode() {
		return oNode;
	}

	/**
	 * Get the UIViewPane that is the parent of the node associated with 
	 * this popup. 
	 * @return the oViewPane
	 */
	public UIViewPane getViewPane() {
		return oViewPane;
	}

	
	
}
