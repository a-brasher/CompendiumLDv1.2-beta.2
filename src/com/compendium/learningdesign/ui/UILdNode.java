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

package com.compendium.learningdesign.ui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.sql.SQLException;
import java.util.*;

import javax.help.CSH;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.Code;
import com.compendium.core.datamodel.IModel;
import com.compendium.core.datamodel.NodeDetailPage;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.ModelSessionException;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.ShortCutNodeSummary;
import com.compendium.core.datamodel.View;
import com.compendium.core.datamodel.ViewLayer;
import com.compendium.ui.IUIConstants;
import com.compendium.ui.UIImages;
import com.compendium.ui.UILink;
import com.compendium.ui.UINode;
import com.compendium.ui.UIReferenceNodeManager;
import com.compendium.ui.UIUtilities;
import com.compendium.ui.UIViewFrame;
import com.compendium.ui.UIViewPane;
import com.compendium.ui.dialogs.UINodeContentDialog;
import com.compendium.ui.plaf.NodeUI;
import com.compendium.ui.plaf.ViewPaneUI;
import com.compendium.ui.popups.UINodePopupMenu;

import com.compendium.learningdesign.core.ILdCoreConstants;
import com.compendium.learningdesign.ui.panels.UILdInformationDialog;
import com.compendium.learningdesign.ui.popups.*;
import com.compendium.ui.stencils.*;
import com.compendium.learningdesign.ui.panels.nodecontent.*;
import com.compendium.learningdesign.ui.plaf.LdNodeUI;
import com.compendium.learningdesign.ui.UILdViewPane;
import com.compendium.learningdesign.core.datamodel.*;
import com.compendium.learningdesign.io.svg.SvgExport;

/**
 * UILdNode is a subclass of UINode. It encapsulates the learning
 * design state and behaviour needed by learning design nodes, e.g.
 * storing the ldType, and generation of LD menus. 
 *
 * @author ajb785
 *
 */
public class UILdNode extends UINode implements ILdTaskTime {
	private static final String uiClassID = "LdNodeUI";
	
	static	{
		UIManager.getDefaults().put("LdNodeUI",  "com.compendium.learningdesign.ui.plaf.LdNodeUI");
	}
	/**
	 * Integer indicating the type of this learning design node, e.g. activity, task, resource etc.	*
	 * @uml.property  name="ldType"
	 */
	private int ldType;
	/**
	 * Integer indicating the sub type of this learning design node, e.g. student (in the case of a role node) or wiki (in the case of a tool node.	*
	 * @uml.property  name="ldSubType"
	 */
	private int ldSubType;
	
	/**
	 * The node right-click popup menu associated with this node - null if one has not been opened yet.
	 * @uml.property  name="nodePopup"
	 * @uml.associationEnd  
	 */
	protected UILdNodePopupMenu			nodePopup			= null;
	
	/** The node contents dialog associated with this node - null if one has not been opened yet.*/
	// Use inherited definition
	// protected UILdActivityNodeContentDialog 	contentDialog		= null;


	/**
	 * Create a new UILdNode
	 * @param nodePos
	 * @param author
	 */
	public UILdNode(NodePosition nodePos, String author) {
		super(nodePos, author);
		ldType = ILdCoreConstants.iLD_TYPE_NO_TYPE;
		ldSubType = ILdCoreConstants.iLD_TYPE_NO_TYPE;
		// Make the ActivityLabelProcessor listen for changes so that changes to this nodes label can be used to trigger searches
		this.addPropertyChangeListener(ProjectCompendium.APP.getActivityLabelProcessor());
	}

	/**
	 * Create a new UILdNode with the ldType set to the parameter type.
	 * @param nodePos
	 * @param author
	 * @param type
	 */
	public UILdNode(NodePosition nodePos, String author, int type) {
		super(nodePos, author);
		ldType = type;
		ldSubType = ILdCoreConstants.iLD_TYPE_NO_TYPE;
		// Make the ActivityLabelProcessor listen for changes so that changes to this nodes label can be used to trigger searches
		this.addPropertyChangeListener(ProjectCompendium.APP.getActivityLabelProcessor());
	}
	
	/**
	 * Create a new UILdNode with the ldType set to the parameter type, and the 
	 * ldSubType set to the paramer subType..
	 * @param nodePos
	 * @param author
	 * @param type
	 */
	public UILdNode(NodePosition nodePos, String author, int type, int subType) {
		super(nodePos, author);
		ldType = type;
		ldSubType = subType;
		
		// If it's a specific type of assessment node the icon needs to be set 
		if (ldSubType == ILdCoreConstants.iLD_TYPE_ASSESSMENT_FORMATIVE ||
				ldSubType == ILdCoreConstants.iLD_TYPE_ASSESSMENT_SUMMATIVE	)	{
			if (oPos.getShowSmallIcon())	{
				this.setIcon(UILdImages.getNodeIconSmall(ldSubType));
			}
			else	{
				this.setIcon(UILdImages.getNodeIcon(ldSubType));
			}			
		}
			
		// Make the ActivityLabelProcessor listen for changes so that changes to this nodes label can be used to trigger searches
		this.addPropertyChangeListener(ProjectCompendium.APP.getActivityLabelProcessor());
	}
	

	/**
	 * Returns the Learning design type of this node. Added by Andrew
	 * @return  int, the learning design type of this node.
	 * @see  #setType
	 * @uml.property  name="ldType"
	 */
	
	/**
	 * @param aNode
	 * @return true if aNode is a UILdNode of ld type task, false otherwise
	 */
	public static boolean isLdTaskNode(UINode aNode)	{
		if (aNode.getLdType() == ILdCoreConstants.iLD_TYPE_TASK )
			return true;
		else
			return false; 			
	}
	
	/**
	 * @param aNode
	 * @return true if aNode is a UILdNode of ld type role, false otherwise
	 */
	public static boolean isLdRoleNode(UINode aNode)	{
		if (aNode.getLdType() == ILdCoreConstants.iLD_TYPE_ROLE )
			return true;
		else
			return false; 			
	}
	public int getLdType() {
	    return ldType;
	}
	
	/**
 	 * Change the LD type of this node to the given type.
	 * Return if type changed.
	 *
	 * @param nNewType, the new type for this node.
	 * @return boolean, true of the type was changed, else false.
	 * @see #getType
	 */
	public boolean setLdType( int nNewType ) {
		return setLdType(nNewType, false, -1);
	}
	
	/**
 	 * Refine the LD type of this node to the given type, i.e. set the ld 
 	 * subtype of the node. 
	 * Return if type refined.
	 *
	 * @param nNewType, the new type for this node.
	 * @return boolean, true of the type was changed, else false.
	 * @see #getType
	 */
	public boolean refineLdType( int nNewType ) {
		return refineLdType(nNewType, false, -1);
	}
	
	/**
 	 * Change the LD type of this node to the given type.
	 * Return if type changed.
	 *
	 * @param newtype, the new type for this node.
	 * @param focus, indicates whether to focus the node after type change.
	 * @param position, indicates to focus node label for editing after type change at the given point. -1 indicates not to focus label.
	 * @return boolean, true of the type was changed, else false.
	 * @see #getType
	 */
	public boolean setLdType( int nNewLdType, boolean focus, int position ) {
		/**
		 * Note that the constants defining Ld node types are in the range 701 -
		 * 707. Constants for standard node types are in the range 1 - 7
		 */
		int nOldType = oNode.getType(); 
		int nOldLdType = oNode.getLdType();

		if (nOldLdType == nNewLdType)	{
			/** Set ldType here so this method can be used by subclasses to set
			 *	the type on initialisation of a new UILdNode or new 
			 *	UILdActivity node i.e. one that is being created from a stencil. 
			 */ 
			
			ldType = nNewLdType;
			return false;
		}
			
// Need to add instance variable this to indicate change of LdType?
	    boolean changeType = true;
// If the old type is a map view, a learning activity view  or list view, and the new type is not an activity  
	    if ( (nOldType == ICoreConstants.LISTVIEW || nOldType == ICoreConstants.MAPVIEW  || nOldType == ICoreConstants.LDMAPVIEW)
		 	&& (nNewLdType != ILdCoreConstants.iLD_TYPE_ACTIVITY ) ) {

			int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, "WARNING! Nodes inside Maps/Lists will be deleted.\nIf they are not transcluded in another Map/List, they will be placed in the trashbin.\n\nAre you sure you still want to continue?",
						      "Change Type - "+oNode.getLabel(), JOptionPane.YES_NO_OPTION);
			if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) {
		    	return false;
			}
			else {
		    	try {
					View view = (View)oNode;
					view.clearViewForTypeChange();
					ProjectCompendium.APP.setTrashBinIcon();
					ProjectCompendium.APP.removeView((View)view);
					// Need to write setLdType(int, String) for NodeSummary class
					oNode.setLdType(nNewLdType, sAuthor);
					ldType = nNewLdType;
					ldSubType = ILdCoreConstants.iLD_TYPE_NO_TYPE;
				}
		    	catch(Exception io) {
					io.printStackTrace();
					return false;
				}
			}
		}	// End of: If the old type is a map view or list view, and the new type is not an activity  
	    //	Else if the new type is an activity
	    else if ( nNewLdType == ILdCoreConstants.iLD_TYPE_ACTIVITY) {
			if (nOldType == ICoreConstants.LISTVIEW || nOldType == ICoreConstants.MAPVIEW) {
				try {
					if (nOldType == ICoreConstants.MAPVIEW) {
						View view = (View)oNode;
						view.purgeAllLinks();
					}
					ProjectCompendium.APP.removeView((View)oNode);
				    oNode.setLdType(nNewLdType, sAuthor);
				    ldType = nNewLdType;
				    ldSubType = ILdCoreConstants.iLD_TYPE_NO_TYPE;
				}
				catch(Exception io){
					io.printStackTrace();
					return false;
				}
			}
			else {
				try {
	   				oNode.setLdType(nNewLdType, sAuthor);
	   				ldType = nNewLdType;
	   				ldSubType = ILdCoreConstants.iLD_TYPE_NO_TYPE;
	   				// Commented out to try to fix nld node change type problem
//	    			setIcon(getNodeImage(oNode.getLdType(), oPos.getShowSmallIcon()));
				}
				catch(Exception io) {
					io.printStackTrace();
					return false;
				}
			}
		}	//End of:	Else if the new type is an activity
	    // An activity node is the only Ld Node type which is not a reference node
	    else if (nOldType == ICoreConstants.REFERENCE && nNewLdType == ILdCoreConstants.iLD_TYPE_ACTIVITY) {

			String source = oNode.getSource();
			String image = oNode.getImage();

			if (!image.equals("") || !source.equals("")) {

				int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, "If you change the type of a reference node,\nany external reference will be lost.\n\nAre you sure you want to continue?",
							      "Change Type - "+oNode.getLabel(), JOptionPane.YES_NO_OPTION);

				if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION)
				    changeType = false;
				else
					changeType = true;
			}

			if (changeType) {
		    	try {
					if ( nNewLdType != ICoreConstants.LISTVIEW && nNewLdType != ICoreConstants.MAPVIEW)
						oNode.setSource("", "", sAuthor);
					oNode.setLdType(nNewLdType, sAuthor);
					ldType = nNewLdType;
					ldSubType = ILdCoreConstants.iLD_TYPE_NO_TYPE;
				}
		    	catch(Exception io) {
					return false;
				}
			}
	    }
	    else if (nOldType > ICoreConstants.PARENT_SHORTCUT_DISPLACEMENT && nNewLdType <=
	    					ICoreConstants.PARENT_SHORTCUT_DISPLACEMENT) {

			try {
			    oNode.setLdType(nNewLdType, sAuthor);
			    ldType = nNewLdType;
			    ldSubType = ILdCoreConstants.iLD_TYPE_NO_TYPE;
			}
			catch(Exception ex) {
				return false;
			}
		}
	    else {
			try {
				 if (nOldLdType == ILdCoreConstants.iLD_TYPE_TASK)	{
				    	if (this.getViewPane() instanceof UILdViewPane)	{
				    		UILdTaskSequence  oUITS = ((UILdViewPane)this.getViewPane()).getTaskSequenceContaining(this);
				    		oUITS.removeNode(this);
				    	}
				    	
				    }
			    oNode.setLdType(nNewLdType, sAuthor);
			    this.setIcon(UILdImages.getNodeIcon(nNewLdType));
			    ldType = nNewLdType;
			    ldSubType = ILdCoreConstants.iLD_TYPE_NO_TYPE;
			}
			catch(Exception ex) {
				ex.printStackTrace();
				return false;
			}
	    }
// This should be just change ld type in some cases
	    return changeType;
	}
	
	/**
 	 * Add a refinement to  the LD type of this node.
	 * Return true  if type changed.
	 *
	 * @param newtype, the new type for this node.
	 * @param focus, indicates whether to focus the node after type change.
	 * @param position, indicates to focus node label for editing after type change at the given point. -1 indicates not to focus label.
	 * @return boolean, true of the type was changed, else false.
	 * @see #getType
	 */
	public boolean refineLdType( int nNewLdType, boolean focus, int position ) {
		/**
		 * Note that the constants defining Ld node types are in the ranges 201-202,  
		 * 501-504 and 701-708. 
		 * Constants for standard node types are in the range 101 - 108
		 */
		// check that the new type is an ldtype before making refinements
		if (nNewLdType == ILdCoreConstants.iLD_TYPE_NO_TYPE)
			return false;
		int nOldType = oNode.getType(); 
		int nOldLdType = oNode.getLdType();
//		LdTypeTagMaps oLdTypeTagMaps = new LdTypeTagMaps(this.getViewPane().getRootPane())
//		Hashtable<Integer, String> htTypestoCodesTable = LdTypeTagMaps.;
		/*
		 * Currently return false if node is not currently  a Ld node: this needs to be
		 * changed so that one can change from a Ld Node to a non-ld node and
		 * vice-versa.
		 */	   
		String sIconPath = "";
		try {
			
			oNode.addLdType(nNewLdType, sAuthor);
			if (this.oPos.getShowSmallIcon())	{
				this.setIcon(UILdImages.getNodeIconSmall(nNewLdType));
				sIconPath = UILdImages.getSmallPath(nNewLdType);
			}
			else	{
				this.setIcon(UILdImages.getNodeIcon(nNewLdType));
				sIconPath = UILdImages.getNodeIconPath(nNewLdType);
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Error: (oNode.refineLdType)\n\n"+ex.getMessage());
			return false;
			}
		ldSubType = nNewLdType;
		if (ldType == ILdCoreConstants.iLD_TYPE_ASSESSMENT)
			this.setReferenceIcon(sIconPath);
		/** If it is  a new role node, create a Task sequence if it is being 
		 * added to an instance of UILdViewPane. No need to do this if it is 
		 * being added to a standard map (i.e. an instance of UIViewPane) because
		 * a standard map does not display the timing info.
		 */ 
		if (nOldLdType == ILdCoreConstants.iLD_TYPE_ROLE )	{
			String sClassName = this.getViewPane().getClass().getName();
			
			if (sClassName.equals("com.compendium.learningdesign.ui.UILdViewPane"))	{
				// The node is in a UILdViewPane 
				/** Do not need this any more because Task sequences depend on Links between tasks
				LdTaskSequence aTaskSequence = new LdTaskSequence(this);
				UILdViewPane myViewPane = (UILdViewPane)this.getViewPane();
				myViewPane.getTaskSequenceSet().add(aTaskSequence);	
				**/			
			}
			
		}
		return true;
		}
	
	
	
	/** Added by Andrew	**/
	/**
	 * Create and show a right-click node popup menu for the given nodeui.
	 * The pop up menu generated will depend on the LDType of the node.
	 * @param nodeui com.compendium.ui.plad.NodeUI, the node to create the popup for.
	 * @param x, the x position of the mouse event that triggered this request.
	 * @param y, the y position of the mouse event that triggered this request.
	 */
	public UILdNodePopupMenu showLdSelectionPopUpMenu(NodeUI nodeui,  int x, int y) {

	    if(getNode().getType() == ICoreConstants.TRASHBIN || 
	    		getNode().getId().equals(ProjectCompendium.APP.getInBoxID())) {
			return null;
	    }
	    //Create the pop up menu for the node of this LD type 
	    nodePopup = new UILdNodePopupMenu("Popup menu", nodeui, getNode().getLdType());
	    UIViewFrame viewFrame = getViewPane().getViewFrame();

	    Dimension dim = ProjectCompendium.APP.getScreenSize();
	    int screenWidth = dim.width - 50; //to accomodate for the scrollbar
	    int screenHeight = dim.height -200; //to accomodate for the menubar...

	    Point point = viewFrame.getViewPosition();
	    int realX = Math.abs(point.x - getX())+50;
	    int realY = Math.abs(point.y - getY())+50;

	    int endXCoordForPopUpMenu = realX + nodePopup.getWidth();
	    int endYCoordForPopUpMenu = realY + nodePopup.getHeight();

	    int offsetX = (screenWidth) - endXCoordForPopUpMenu;
	    int offsetY = (screenHeight) - endYCoordForPopUpMenu;

	    if(offsetX > 0)
		offsetX = 0;
	    if(offsetY > 0)
		offsetY = 0;

	    nodePopup.setCoordinates(realX+offsetX, realY+offsetY);
	    nodePopup.setViewPane(getViewPane());
	    nodePopup.show(viewFrame, realX+offsetX, realY+offsetY);

	    return nodePopup;
	}
	
	/**
	 * Create and show the right-click node popup menu for the given nodeui.
	 * @param nodeui com.compendium.ui.plad.NodeUI, the node to create the popup for.
	 * @param x, the x position of the mouse event that triggered this request.
	 * @param y, the y position of the mouse event that triggered this request.
	 */
	public UILdNodePopupMenu showPopupMenu(NodeUI nodeui,  int x, int y) {

	    if(getNode().getType() == ICoreConstants.TRASHBIN || 
	    		getNode().getId().equals(ProjectCompendium.APP.getInBoxID())) {
			return null;
	    }
	 // Get the LDType of the node 
	    int ldType = getLdType();
	    nodePopup = new UILdNodePopupMenu("Popup menu", nodeui, ldType);
	    UIViewFrame viewFrame = getViewPane().getViewFrame();

	    Dimension dim = ProjectCompendium.APP.getScreenSize();
	    int screenWidth = dim.width - 50; //to accomodate for the scrollbar
	    int screenHeight = dim.height -200; //to accomodate for the menubar...

	    Point point = viewFrame.getViewPosition();
	    int realX = Math.abs(point.x - getX())+50;
	    int realY = Math.abs(point.y - getY())+50;

	    int endXCoordForPopUpMenu = realX + nodePopup.getWidth();
	    int endYCoordForPopUpMenu = realY + nodePopup.getHeight();

	    int offsetX = (screenWidth) - endXCoordForPopUpMenu;
	    int offsetY = (screenHeight) - endYCoordForPopUpMenu;

	    if(offsetX > 0)
		offsetX = 0;
	    if(offsetY > 0)
		offsetY = 0;

	    nodePopup.setCoordinates(realX+offsetX, realY+offsetY);
	    nodePopup.setViewPane(getViewPane());
	    nodePopup.show(viewFrame, realX+offsetX, realY+offsetY);

	    return nodePopup;
	}
	
	/**
	 * Generate the Learning design help dialog for this node.
	 * @return - an instance of UILdInformationDialog
	 */
	public UILdInformationDialog showLdHelpDialog() {
		UILdInformationDialog oLdDialog = null;
		switch (this.getNode().getLdType())	{
		case ILdCoreConstants.iLD_TYPE_VLE_TOOL: oLdDialog =  showLDInfoDialog(ILdUIConstants.iTOOLSHELP_TAB); break;
		case ILdCoreConstants.iLD_TYPE_TASK: {
			String sWord = this.getNode().getLabel().trim();			
			if (ProjectCompendium.APP.getActivityLabelProcessor().getHmWordToToolsMap().containsKey(sWord.toLowerCase()))
				{
				oLdDialog = this.getViewPane().showTasksHelp(this, sWord);
				}
			// Need to decide what to do if the word isn't related to any help items
			else	{
				JOptionPane.showMessageDialog(this.getParent(),"The phrase \"" + sWord + "\" can not be mapped to a learning tool or a learning task! \\\n " +
						"This prototype only maps single words.", "Apologies!", JOptionPane.INFORMATION_MESSAGE);
			}
				
			break;
		}
		default:  // Do nothing //oLdDialog =  showLDInfoDialog(ILdUIConstants.iTOOLSHELP_TAB); break;
		}
		return oLdDialog;
	}
	
	/** 	Added by Andrew		**/
	/**
	 * Open and return the UILdInformationDialog dialog and select the given tab.
	 *
	 * @param int tab, the tab on the dialog to select.
	 * @return com.compendium.ui.dialogs.UILdInformationDialog, the Ld information dialog for this node.
	 */
	private UILdInformationDialog showLDInfoDialog(int tab) {
	    if(getNode().getType() == ICoreConstants.TRASHBIN || 
	    		getNode().getId().equals(ProjectCompendium.APP.getInBoxID())) {
	    	return null;
	    }
	    	
		if (ldInfoDialog != null && ldInfoDialog.isVisible())
			return ldInfoDialog;

		ldInfoDialog = new UILdInformationDialog(ProjectCompendium.APP, oPos.getView(), ((UILdNode)this), tab);
		ldInfoDialog.setVisible(true);
   		//Lakshmi (4/19/06) - if the contents dialog is opened set state as read in NodeUserState DB
		/** Andrew writes:
		 *  Should state be set to read when the UILdInformationDialog is shown? 
		 *  Not sure - need to check and delete this code if appropriate.
		 */ 
   		int state = this.getNode().getState();
   		if(state != ICoreConstants.READSTATE){
   			try {
				this.getNode().setState(ICoreConstants.READSTATE);
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Error: (UINode.showLDInfoDialog) \n\n"+e.getMessage());
			} catch (ModelSessionException e) {
				e.printStackTrace();
				System.out.println("Error: (UINode.showLDInfoDialog) \n\n"+e.getMessage());
			}
   		}
	    return ldInfoDialog;
	}

	/**
	 * Returns the ldSubType of this UILdNode, i.e. an integer  denoting which subtype (or refinement) it is.
	 * @return  the ldSubType
	 * @uml.property  name="ldSubType"
	 */
	public int getLdSubType() {
		return ldSubType;
	}

	/**
	 * Generate the String to be used in an SVG file as part of the identifier,
	 * or the selectors for CSS classes for this node. 
	 * @return String denote the class of this node for use in SVG files, 
	 * or SvgExport.sGeneralClass if the relevant class can not be determined.
	 */
	public String getSvgClass() {
		//Hashmap.get(key) returns null if no mapping to the key can be found
		/// Return the class denoted by this node's Ld type
		 String sNodeClass = SvgExport.hmNodeClasses.get(this.getNode().getLdType());
		if (sNodeClass==null) 	{
			sNodeClass = SvgExport.sGeneralClass;
		}
		return sNodeClass;
	}
	
	/**
	 * Return the small size icon for the given node type.
	 * @param type, the node type to return the icon for.
	 * @param ldType, the learning design type of the node
	 * * @return ImageIcon, the icon for the given node type.
	 */
	public static ImageIcon getNodeImageSmall(int type, int ldType) {
		ImageIcon img = null;
		if (ldType == ILdCoreConstants.iLD_TYPE_NO_TYPE)	{
			return(UINode.getNodeImageSmall(type));
		}
		else	{
			img = UILdImages.getNodeIconSmall(ldType);
		}
		return img;
	}
	
	
	/**
	 * Return the small size icon for the given reference string for file types (not images).
	 * @param sRefString the reference string to get an icon for.
	 * @return ImageIcon the icon for the given node type.
	 */
	public static ImageIcon getReferenceImageSmall(String sRefString, int iLdType, int iLdSubType) {
		if (iLdType == ILdCoreConstants.iLD_TYPE_NO_TYPE)
			return UIReferenceNodeManager.getSmallReferenceIcon(sRefString);
		else	{
			return getNodeImageSmall(ICoreConstants.REFERENCE, iLdType);
		}
	}
	
	

	/**
	 * Return the small size icon for the given Ld node type.
	 * This method assumes that the node is a UILdNode, which should
	 * always be the case, but not exceptions are trapped.
	 * @param type, the node type to return the icon for.
	 * @return ImageIcon, the icon for the given node type.
	 */
	public static ImageIcon getNodeImageSmall(int ldType) {
		// Use REFERENCE type so that can swap in standard image e.g. word file image for ld resources.
	    return getNodeImageSmall(ICoreConstants.REFERENCE, ldType);
	    
	}

	/**
	 * Restore the icon on this node to its original type specific icon.
	 */
	/** REMOVE this method in a minute - it's not used **/ 
	public ImageIcon restoreIcon() {

	    int type = oNode.getType();
	    int ldType = oNode.getLdType();

	    if (ldType == ILdCoreConstants.iLD_TYPE_NO_TYPE)	{
	    	if (type == ICoreConstants.TRASHBIN) {
	    		ImageIcon icon = ProjectCompendium.APP.setTrashBinIcon();
	    		if (icon != null) {
	    			setIcon(icon);
	    		}
	    	}
	    	else if (type == ICoreConstants.REFERENCE || type == ICoreConstants.REFERENCE_SHORTCUT) {
	    		String refString = oNode.getImage();
	    		if (refString == null || refString.equals(""))
	    			refString = oNode.getSource();
	    		setReferenceIcon(refString);
	    	}
	    	else if(type == ICoreConstants.MAPVIEW || type == ICoreConstants.MAP_SHORTCUT ||
	    			type == ICoreConstants.LISTVIEW || type == ICoreConstants.LIST_SHORTCUT) {

	    		String refString = oNode.getImage();
	    		if (refString != null && !refString.equals(""))
	    			setReferenceIcon(refString);
	    		else
	    	//		setIcon(getNodeImage(type, oPos.getShowSmallIcon()));
	    		setIcon(getNodeImage(this.getLdType(), oPos.getShowSmallIcon()));
	    	}
	    	else {
	    		//setIcon(getNodeImage(type, oPos.getShowSmallIcon()));
	    		setIcon(getNodeImage(this.getLdType(), oPos.getShowSmallIcon()));
	    	}

	    }
	    
	    else	{
	    	// It's being changed to an LdType
	    	// This is not working 
	    	if (ldType == ILdCoreConstants.iLD_TYPE_RESOURCE)	{
	    		String refString = oNode.getImage();
	    		if (refString == null || refString.equals(""))
	    			refString = oNode.getSource();
	    		setReferenceIcon(refString);
	    	}
	    	else	{
	    		setIcon(getNodeImage(this.getLdType(), oPos.getShowSmallIcon()));
	    	}
	    	}
	    return oDefaultIcon;	
	    }
	    
	
	/**
	 * Set the correct reference icon for the given file path or url. This 
	 * implementation takes account of the use of reference nodes for 
	 * learning design node types, in particular it sets the icon to be a
	 * small image according to the preferences set in the project options
	 * dialog. 
	 *
	 * @param refString, the string for the file path or url for this reference node.
	 */
	public void setReferenceIcon(String refString) {
		final String imageRef = refString;

		//Thread thread = new Thread("UINode.setReferenceIcon") {
		//	public void run() {
		ImageIcon icon = null;
		int ldType = this.getNode().getLdType();
		ldType = this.getLdType();
		
		if (imageRef != null) {
			if ( UIImages.isImage(imageRef) ) {
				ImageIcon originalSizeImage = UIImages.createImageIcon(imageRef); 		
				if (originalSizeImage == null) {
					setIcon(UIImages.get(IUIConstants.BROKEN_IMAGE_ICON));
					return;
				}
				if (this.getLdType() != ILdCoreConstants.iLD_TYPE_NO_TYPE && oPos.getShowSmallIcon())	{
					if (ldType == ILdCoreConstants.iLD_TYPE_ASSESSMENT && this.getLdSubType() != ILdCoreConstants.iLD_TYPE_NO_TYPE)
						icon = getNodeImageSmall(this.getLdSubType());
					else
						icon = getNodeImageSmall(this.getLdType());
				}
				else	{
					Image originalIcon = originalSizeImage.getImage();
					int originalWidth = originalIcon.getWidth(null);
					int originalHeight = originalIcon.getHeight(null);

					Dimension specifiedSize = getNode().getImageSize();
					if (specifiedSize.width == 0 && specifiedSize.height == 0) {
						icon = UIImages.thumbnailIcon(originalSizeImage);
						Image newIcon = icon.getImage();
						int newWidth = newIcon.getWidth(null);
						int newHeight = newIcon.getHeight(null);
						if (newWidth < originalWidth || newHeight < originalHeight) {
							bIsImageScaled = true;
						}
					} else if (specifiedSize.width == originalWidth && specifiedSize.height == originalHeight) {
						icon = originalSizeImage;
						bIsImageScaled = false;
					} else {
						icon = UIImages.scaleIcon(originalSizeImage, specifiedSize);
						Image newIcon = icon.getImage();
						int newWidth = newIcon.getWidth(null);
						int newHeight = newIcon.getHeight(null);
						if (newWidth < originalWidth || newHeight < originalHeight) {
							bIsImageScaled = true;
						}					
					}
				}
			}
			else {					    
				// IF USING SMALL ICON MODE, LOAD SMALL VERSION
				if (oPos.getShowSmallIcon()) {
					icon = getReferenceImageSmall(imageRef);
				}
				else {
					icon = getReferenceImage(imageRef);			    	
				}
			}
		}
		setIcon(icon);
	}
	
/**
 * Return the standard size icon for the given node type.
 * @param type, the node type to return the icon for.
 * @return ImageIcon, the icon for the given node type.
 */
public static ImageIcon getNodeImage(int ldType, boolean isSmall) {

    if (isSmall) {
    	return getNodeImageSmall(ldType);
    }

    ImageIcon img = null;
    switch (ldType) {
	case ILdCoreConstants.iLD_TYPE_ACTIVITY:
	    img = UILdImages.getNodeIcon(ILdCoreConstants.iLD_TYPE_ACTIVITY);
	    break;

	case ILdCoreConstants.iLD_TYPE_ASSESSMENT_SUMMATIVE:
	    img = UILdImages.getNodeIcon(ILdCoreConstants.iLD_TYPE_ASSESSMENT_SUMMATIVE);
	    break;
	case ILdCoreConstants.iLD_TYPE_ASSESSMENT_FORMATIVE:
	    img = UILdImages.getNodeIcon(ILdCoreConstants.iLD_TYPE_ASSESSMENT_FORMATIVE);
	    break;
	case ILdCoreConstants.iLD_TYPE_ASSESSMENT:
	    img = UILdImages.getNodeIcon(ILdCoreConstants.iLD_TYPE_ASSESSMENT);
	    break;
	case ILdCoreConstants.iLD_TYPE_CONDITIONAL_CONDITION:
	    img = UILdImages.getNodeIcon(ILdCoreConstants.iLD_TYPE_CONDITIONAL_CONDITION);
	    break;
	case ILdCoreConstants.iLD_TYPE_CONDITIONAL_FALSE:
	    img = UILdImages.getNodeIcon(ILdCoreConstants.iLD_TYPE_CONDITIONAL_FALSE);
	    break;
	case ILdCoreConstants.iLD_TYPE_CONDITIONAL_TRUE:
	    img = UILdImages.getNodeIcon(ILdCoreConstants.iLD_TYPE_CONDITIONAL_TRUE);
	    break;
	case ILdCoreConstants.iLD_TYPE_LEARNING_OUTCOME:
	    img = UILdImages.getNodeIcon(ILdCoreConstants.iLD_TYPE_LEARNING_OUTCOME);
	    break;
	case ILdCoreConstants.iLD_TYPE_RESOURCE:
	    img = UILdImages.getNodeIcon(ILdCoreConstants.iLD_TYPE_RESOURCE);
	    break;
	case ILdCoreConstants.iLD_TYPE_ROLE:
	    img = UILdImages.getNodeIcon(ILdCoreConstants.iLD_TYPE_ROLE);
	    break;
	case ILdCoreConstants.iLD_TYPE_STOP:
	    img = UILdImages.getNodeIcon(ILdCoreConstants.iLD_TYPE_STOP);
	    break;
	case ILdCoreConstants.iLD_TYPE_TASK:
	    img = UILdImages.getNodeIcon(ILdCoreConstants.iLD_TYPE_TASK);
	    break;
	case ILdCoreConstants.iLD_TYPE_VLE_TOOL:
	    img = UILdImages.getNodeIcon(ILdCoreConstants.iLD_TYPE_VLE_TOOL);
	    break;
	case ICoreConstants.ARGUMENT:
	    img = UIImages.getNodeIcon(IUIConstants.ARGUMENT_ICON);
	    break;

	case ICoreConstants.REFERENCE:
		img = UIImages.getNodeIcon(IUIConstants.REFERENCE_ICON);
	    break;

	case ICoreConstants.DECISION:
	    img = UIImages.getNodeIcon(IUIConstants.DECISION_ICON);
	    break;

	case ICoreConstants.NOTE:
	    img = UIImages.getNodeIcon(IUIConstants.NOTE_ICON);
	    break;

	case ICoreConstants.MAPVIEW:
    	img = UIImages.getNodeIcon(IUIConstants.MAP_ICON);
	    break;

	case ICoreConstants.LISTVIEW:
	    img = UIImages.getNodeIcon(IUIConstants.LIST_ICON);
	    break;

	case ICoreConstants.PRO:
	    img = UIImages.getNodeIcon(IUIConstants.PRO_ICON);
	    break;

	case ICoreConstants.CON:
	    img = UIImages.getNodeIcon(IUIConstants.CON_ICON);
	    break;

	case ICoreConstants.ISSUE_SHORTCUT:
	    img = UIImages.getNodeIcon(IUIConstants.ISSUE_SHORTCUT_ICON);
	    break;

	case ICoreConstants.POSITION_SHORTCUT:
	    img = UIImages.getNodeIcon(IUIConstants.POSITION_SHORTCUT_ICON);
	    break;

	case ICoreConstants.ARGUMENT_SHORTCUT:
	    img = UIImages.getNodeIcon(IUIConstants.ARGUMENT_SHORTCUT_ICON);
	    break;

	case ICoreConstants.REFERENCE_SHORTCUT:
	    img = UIImages.getNodeIcon(IUIConstants.REFERENCE_SHORTCUT_ICON);
	    break;

	case ICoreConstants.DECISION_SHORTCUT:
	    img = UIImages.getNodeIcon(IUIConstants.DECISION_SHORTCUT_ICON);
	    break;

	case ICoreConstants.NOTE_SHORTCUT:
	    img = UIImages.getNodeIcon(IUIConstants.NOTE_SHORTCUT_ICON);
	    break;

	case ICoreConstants.MAP_SHORTCUT:
	    img = UIImages.getNodeIcon(IUIConstants.MAP_SHORTCUT_ICON);
	    break;

	case ICoreConstants.LIST_SHORTCUT:
	    img = UIImages.getNodeIcon(IUIConstants.LIST_SHORTCUT_ICON);
	    break;

	case ICoreConstants.PRO_SHORTCUT:
	    img = UIImages.getNodeIcon(IUIConstants.PRO_SHORTCUT_ICON);
	    break;

	case ICoreConstants.CON_SHORTCUT:
	    img = UIImages.getNodeIcon(IUIConstants.CON_SHORTCUT_ICON);
	    break;

	case ICoreConstants.TRASHBIN:
	    img = UIImages.getNodeIcon(IUIConstants.TRASHBIN_ICON);
	    break;
    }
    return img;
}

/**
 * Open and return the content dialog and select the Edit/Contents tab.
 * @return com.compendium.ui.dialogs.UINodeContentDialog, the content dialog for this node.
 */
public UINodeContentDialog showEditDialog() {
	// Next line commented out until UILdActivityNodeContentDialog class is completed 
	// return showContentDialog(UILdActivityNodeContentDialog.DESIGNERS_TAB);
	return showContentDialog(UINodeContentDialog.CONTENTS_TAB);
}


/**
 * Open and return the Ld activity content dialog and select the given tab.
 *
 * @param int tab, the tab on the dialog to select.
 * @return com.compendium.ui.dialogs.UINodeContentDialog, or UILdActivityNodeContentDialog
 *  as the content dialog for this node.
 */
private UINodeContentDialog  showContentDialog(int tab) {
    if(getNode().getType() == ICoreConstants.TRASHBIN || 
    		getNode().getId().equals(ProjectCompendium.APP.getInBoxID())) {
    	return null;
    }
    	
	if (contentDialog != null && contentDialog.isVisible())
		return contentDialog;
	if (ldType == ILdCoreConstants.iLD_TYPE_ACTIVITY)	{
	// Following line commented out 'till UILdActivityNodeContentDialog class completed
	// contentDialog = new UILdActivityNodeContentDialog (ProjectCompendium.APP, oPos.getView(), this, tab);
		contentDialog = new UINodeContentDialog(ProjectCompendium.APP, oPos.getView(), this, tab);
	}
	else	{
		contentDialog = new UINodeContentDialog(ProjectCompendium.APP, oPos.getView(), this, tab);
	}
		contentDialog.setVisible(true);
		//Lakshmi (4/19/06) - if the contents dialog is opened set state as read in NodeUserState DB
		int state = this.getNode().getState();
		if(state != ICoreConstants.READSTATE){
			try {
			this.getNode().setState(ICoreConstants.READSTATE);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error: (UILdNode.showContentDialog) \n\n"+e.getMessage());
		} catch (ModelSessionException e) {
			e.printStackTrace();
			System.out.println("Error: (UILdNode.showContentDialog) \n\n"+e.getMessage());
		}
		}
    return contentDialog;
}

/**
	 * Set the NodeSummary data object for this node.
	 * This sets different value for oDefaultIcon compared to standard Compendium
	 * UINode class.
 * <p>
 * @param node com.compendium.core.datamodel.NodeSummary, the node data object for this UINode.
 */
public void setNode(NodeSummary node) {

    oNode = node;
	oNodeType = node.getType();
	
    oNode.addPropertyChangeListener(this);
    oPos.addPropertyChangeListener(this);

    setHelp(oNode.getType());

    //remove all returns and tabs which show up in the GUI as evil black char
    String label = "";
    label = oNode.getLabel();
    if (label.equals(ICoreConstants.NOLABEL_STRING))
		label = "";

    label = label.replace('\n',' ');
    label = label.replace('\r',' ');
    label = label.replace('\t',' ');
    setText(label);

    int type = oNode.getType();
    

    //if the default icon has not already been set, then set it here.
    //otherwise leave the icon image alone since it may have been changed.
    ImageIcon icon = null;

    if (oDefaultIcon == null) {
    	if (type == ICoreConstants.REFERENCE || type == ICoreConstants.REFERENCE_SHORTCUT) {

		    String refString = oNode.getImage();
		    if (refString == null || refString.equals(""))
				refString = oNode.getSource();
		    
		    if (refString == null || refString.equals("")) {
		    	setIcon(getNodeImage(type, oPos.getShowSmallIcon()));
		    } else {
		    	setReferenceIcon(refString);
		    }
 /**   		String refString = "";
    		if (oNode.getLdType() != ILdCoreConstants.iLD_TYPE_RESOURCE)	{
    			refString = oNode.getImage();
    		}
    		else	{
    			refString = oNode.getSource();
    		}
    		
    		if (refString == null || refString.equals(""))
    			refString = oNode.getSource();
    		if (refString == null || refString.equals(""))	
    			refString = oNode.getImage();
    		int iTempType = oNode.getLdType();
    		boolean bTemp = UINode.isReferenceNode(refString);
    		if (oNode.getLdType() != ILdCoreConstants.iLD_TYPE_NO_TYPE && !UINode.isReferenceNode(refString))	{
    			setIcon(getNodeImage(oNode.getLdType(), oPos.getShowSmallIcon()));
    		}
    		
    		else	{
    			if (refString == null || refString.equals("")) {
    				setIcon(getNodeImage(type, oPos.getShowSmallIcon()));
    			} else {
    				setReferenceIcon(refString);
    			}
    		}
   **/ 	}
		else if(type == ICoreConstants.MAPVIEW || type == ICoreConstants.MAP_SHORTCUT ||
				type == ICoreConstants.LISTVIEW || type == ICoreConstants.LIST_SHORTCUT) {
		    String refString = oNode.getImage();
		    if (refString == null || refString.equals("") || refString.endsWith("meeting_big.gif"))
			    setIcon(getNodeImage(type, oPos.getShowSmallIcon()));
			else
			    setReferenceIcon(refString);
		}
		else {
		    setIcon(getNodeImage(type, oPos.getShowSmallIcon()));
		}
    }
 
    oNode.updateMultipleViews();
}

/**
 * Return the logical parent of this node determined from linkage.
 * @param NodeSummary, the node that is the linkage parent of this node.
 */
public NodeSummary getParentNode() {
	return this.getNode().getParentNode();
} 


/**	Returns true if this node is a Ld Role node, false otherwise. **/
public boolean isRoleNode()	{
	return(this.getLdType() == ILdCoreConstants.iLD_TYPE_ROLE); 
}
/**
 * This is a copy of the UINode method. Need to implement functionality specific
 * to UILdNode class. 
 * Set the help context for this node depending on node type.
 * @param type, the node type to set the help string for.
 */
private void setHelp(int type) {

    switch (type) {
	case ICoreConstants.ISSUE:
	    CSH.setHelpIDString(this,"node.node_types");
	    break;
	case ICoreConstants.POSITION:
	    CSH.setHelpIDString(this,"node.node_types");
	    break;
	case ICoreConstants.ARGUMENT:
	    CSH.setHelpIDString(this,"node.node_types");
	    break;
	case ICoreConstants.REFERENCE:
	    CSH.setHelpIDString(this,"node.refimage");
	    break;
	case ICoreConstants.DECISION:
	    CSH.setHelpIDString(this,"node.node_types");
	    break;
	case ICoreConstants.NOTE:
	    CSH.setHelpIDString(this,"node.node_types");
	    break;
	case ICoreConstants.MAPVIEW:
	    CSH.setHelpIDString(this,"node.views");
	    break;
	case ICoreConstants.LISTVIEW:
	    CSH.setHelpIDString(this,"node.views");
	    break;
	case ICoreConstants.PRO:
	    CSH.setHelpIDString(this,"node.node_types");
	    break;
	case ICoreConstants.CON:
	    CSH.setHelpIDString(this,"node.node_types");
	    break;
	case ICoreConstants.ISSUE_SHORTCUT:
	    CSH.setHelpIDString(this,"node.node_types");
	    break;
	case ICoreConstants.POSITION_SHORTCUT:
	case ICoreConstants.ARGUMENT_SHORTCUT:
	case ICoreConstants.REFERENCE_SHORTCUT:
	case ICoreConstants.DECISION_SHORTCUT:
	case ICoreConstants.NOTE_SHORTCUT:
	case ICoreConstants.MAP_SHORTCUT:
	case ICoreConstants.LIST_SHORTCUT:
	case ICoreConstants.PRO_SHORTCUT:
	case ICoreConstants.CON_SHORTCUT:
	    CSH.setHelpIDString(this,"node.shortcuts");
	    break;
	case ICoreConstants.TRASHBIN:
	    CSH.setHelpIDString(this,"basics.trashbin");
	    break;
    }
}

/**
* Notification from the UIFactory that the L&F has changed.
*
* @see JComponent#updateUI
*/
public void updateUI() {
	if (UIManager.get(getUIClassID()) != null) {
		setUI((LdNodeUI)UIManager.getUI(this));
	}
	else	{
  	LdNodeUI newNodeUI = (LdNodeUI)LdNodeUI.createUI(this);
  	setUI(newNodeUI);
	}
	invalidate();
}

/** Only task and activity nodes should show times so this method should
 * return false by default.
 */ 
public boolean getShowTime() {
	return false;
}

/* Only task and activity nodes should show times so this method should do
 * nothing by default.
 * @see com.compendium.learningdesign.ui.ILdTaskTime#setShowTime(boolean)
 */
public void setShowTime(boolean showTime) {
	// Do nothing	
}

/**
 * This method returns true if the node is connected to a Ld task node,
 * false otherwise.
 * @return boolean
 */
public boolean isConnectedToAnyTaskNode()	{
	for(Enumeration es = this.getLinks();es.hasMoreElements();) 	{
		UILink uilink = (UILink)es.nextElement();
		if ((uilink.getFromNode().getNode().getLdType() == ILdCoreConstants.iLD_TYPE_TASK) ||
		(uilink.getToNode().getNode().getLdType() == ILdCoreConstants.iLD_TYPE_TASK))	{
			return true;
		}					
	}
	return false;
}

/**
 * This method returns true if the node is connected to a Ld task node,
 * false otherwise.
 * @return boolean
 */
public boolean isConnectedToAnyTaskNodeByLinks(Hashtable<String, UILink> oLinks)	{
	for(Enumeration<UILink> es = oLinks.elements();es.hasMoreElements();) 	{
		UILink uilink = (UILink)es.nextElement();
		if ((uilink.getFromNode().getNode().getLdType() == ILdCoreConstants.iLD_TYPE_TASK) ||
		(uilink.getToNode().getNode().getLdType() == ILdCoreConstants.iLD_TYPE_TASK))	{
			return true;
		}					
	}
	return false;
}

/* 
 * Adds a PropertyChangeListener to the listener list for a specific property. 
 * The specified property may be user-defined, or one of the 'standard' properties.
 * @see java.awt.Component#addPropertyChangeListener(java.beans.PropertyChangeListener)
 */
public void addPropertyChangeListener(PropertyChangeListener listener)	{
	if (listener == null) return;
	PropertyChangeListener[] oListeners = this.getPropertyChangeListeners();
	//Only add the listener if it is not already in the list
	if (!Arrays.asList(oListeners).contains(listener))	{
		super.addPropertyChangeListener(listener);
	}
}

/**
 * Create a SVG representation of  this node, for occurences when the SVG for 
 * transclude occurences have already been created, i.e. a representation which
 * refers to the origibnal via a <use> tag.
 * @param oDoc, the SVG document to which the SVG representation of this node will be added.
 */
public void generateTranclusionUseSVG( Document oDoc) {
	String sIdThisNode = this.getNode().getId();
	if (oDoc == null )
		return;
	//Need to generate  code akin to: <use x="316" y="263" xlink:href="#pro.137108491691311175409673"/>
	String sNodeClass = "";
	try	{
		sNodeClass = SvgExport.hmNodeClasses.get(this.getNode().getLdType());
	}
	catch (Exception e) {
		System.out.print("HashMap SvgExport.hmNodeClasses out of bounds: index accessed = "  + this.getNode().getLdType());
	}
// Find the transcluded node whose representtion has already been written
String sIdofViewTranscludedInto = SvgExport.hmDocToTransWrittenInView.get(oDoc).get(sIdThisNode);
View  oViewTransCludedInto = View.getView(sIdofViewTranscludedInto);
NodePosition oNpSource = oViewTransCludedInto.getNodePosition(sIdThisNode);
int iXPosSource = oNpSource.getXPos(); int iYPosSource = oNpSource.getYPos();
// Element oSvgParentView = oDoc.getElementById(SvgExport.sMapViewClass+SvgExport.sIdFragmentConnector + this.getViewPane().getView().getId());
Element oSvgParentView = this.getSvgParentMap(this.getViewPane().getView().getId(), oDoc);
NodePosition oNp = this.getNodePosition();
int iXPosTrans = oNp.getXPos(); int iYPosTrans = oNp.getYPos();
Element oUse = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_USE_TAG);
oUse.setAttribute(SVGGraphics2D.XLINK_HREF_QNAME, org.apache.batik.svggen.SVGSyntax.SIGN_POUND + sNodeClass + SvgExport.sIdFragmentConnector + sIdThisNode); 
oUse.setAttribute("x",  Integer.toString(iXPosTrans - iXPosSource));
oUse.setAttribute("y", Integer.toString(iYPosTrans - iYPosSource));
oSvgParentView.appendChild(oUse);
Comment oComment = oDoc.createComment("Start of Transclusion: " + sNodeClass  + " " + this.getNode().getId());
oSvgParentView.insertBefore(oComment, oUse);
oComment = oDoc.createComment("End  of Transclusion " + sNodeClass  + " " + this.getNode().getId());
oSvgParentView.appendChild(oComment);
}


/**
	 * Change the type of this node to the given type.
 * Return if type changed.
 *
 * @param newtype, the new type for this node.
 * @param focus, indicates whether to focus the node after type change.
 * @param position, indicaes to focus node label for editing after type change at the given point. -1 indicates not to focus label.
 * @return boolean, true of the type was changed, else false.
 * @see #getType
 */
public boolean setType( int nNewType, boolean focus, int position ) {

    int nOldType = oNode.getType();
	if (nOldType == nNewType)
		return false;

    boolean changeType = true;

    if ( (nOldType == ICoreConstants.LISTVIEW || nOldType == ICoreConstants.MAPVIEW)
	 	&& (nNewType != ICoreConstants.LISTVIEW && nNewType != ICoreConstants.MAPVIEW) ) {

		int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, "WARNING! Nodes inside Maps/Lists will be deleted.\nIf they are not transcluded in another Map/List, they will be placed in the trashbin.\n\nAre you sure you still want to continue?",
					      "Change Type - "+oNode.getLabel(), JOptionPane.YES_NO_OPTION);
		if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) {
	    	return false;
		}
		else {
	    	try {
				View view = (View)oNode;
				view.clearViewForTypeChange();
				ProjectCompendium.APP.setTrashBinIcon();
				ProjectCompendium.APP.removeView((View)view);
				oNode.setType(nNewType, sAuthor);
			}
	    	catch(Exception io) {
				io.printStackTrace();
				return false;
			}
		}
	}
    else if ( nNewType == ICoreConstants.LISTVIEW || nNewType == ICoreConstants.MAPVIEW) {
		if (nOldType == ICoreConstants.LISTVIEW || nOldType == ICoreConstants.MAPVIEW) {
			try {
				if (nOldType == ICoreConstants.MAPVIEW) {
					View view = (View)oNode;
					view.purgeAllLinks();
				}
				ProjectCompendium.APP.removeView((View)oNode);
			    oNode.setType(nNewType, sAuthor);
			}
			catch(Exception io){
				io.printStackTrace();
				return false;
			}
		}
		else {
			try {
   				oNode.setType(nNewType, sAuthor);
			}
			catch(Exception io) {
				return false;
			}
		}
	}
    // Make sure it's not an Ld type node (this check is made JUST IN CASE !!
    else if (this.getLdType() != ILdCoreConstants.iLD_TYPE_NO_TYPE && nOldType == ICoreConstants.REFERENCE && nNewType != ICoreConstants.REFERENCE) {

		String source = oNode.getSource();
		String image = oNode.getImage();

		if (!image.equals("") || !source.equals("")) {

			int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, "If you change the type of a reference node,\nany external reference will be lost.\n\nAre you sure you want to continue?",
						      "Change Type - "+oNode.getLabel(), JOptionPane.YES_NO_OPTION);

			if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION)
			    changeType = false;
			else
				changeType = true;
		}

		if (changeType) {
	    	try {
				if ( nNewType != ICoreConstants.LISTVIEW && nNewType != ICoreConstants.MAPVIEW)
					oNode.setSource("", "", sAuthor);
				oNode.setType(nNewType, sAuthor);
			}
	    	catch(Exception io) {
				return false;
			}
		}
    }
    else if (nOldType > ICoreConstants.PARENT_SHORTCUT_DISPLACEMENT && nNewType <=
    					ICoreConstants.PARENT_SHORTCUT_DISPLACEMENT) {

		try {
		    oNode.setType(nNewType, sAuthor);
		}
		catch(Exception ex) {
			return false;
		}
	}
    else {
		try {
		    oNode.setType(nNewType, sAuthor);
		}
		catch(Exception ex) {
			return false;
		}
    }

    return changeType;
}
/**
* Returns the L&F object that renders this component.
*
* @return LdNodeUI object.
*/
public LdNodeUI getUI() {
    return (LdNodeUI)ui;
}

/**
* Returns a string that specifies the name of the l&f class
* that renders this component.
*
* @return String "LdNodeUI"
*
* @see JComponent#getUIClassID
* @see UIDefaults#getUI
*/
public String getUIClassID() {
	return uiClassID;
}

/**
 * Paint on a SVGGraphics2D graphics context, allowing addition of SVG elements.
 * Note - only a comment is added so far, just to show it can be done.
 * @param g
 */
public void paint(SVGGraphics2D g)	{
	g.getGeneratorContext().setComment("From UILdNode");
	g.drawString("Node " +this.getText(), this.getX(), this.getY());

}

public void paintComponent(SVGGraphics2D g)	{
	g.getGeneratorContext().setComment("From UILdNode");
	g.drawString("Node " +this.getText(), this.getX(), this.getY());

}

/**
 * This method carries out the processing required to handle node type 
 * changes.
 * 
 * @param oldvalue, the old value of the node type (as an Object)
 * @param newvalue, the new value of the node type (as an Object)
 */
/** Commented out- code is included in revision 1891
protected void handleTypeChange(PropertyChangeEvent evt,Object oldvalue, Object newvalue) {
**/
}

