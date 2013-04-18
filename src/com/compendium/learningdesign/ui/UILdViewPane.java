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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import com.compendium.ProjectCompendium;
import com.compendium.core.CoreUtilities;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.Code;
import com.compendium.core.datamodel.IModel;
import com.compendium.core.datamodel.Link;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.PCSession;
import com.compendium.core.datamodel.UserProfile;
import com.compendium.core.datamodel.View;
import com.compendium.ui.FormatProperties;
import com.compendium.ui.UIImages;
import com.compendium.ui.UILink;
import com.compendium.ui.UIListViewFrame;
import com.compendium.ui.UIMapViewFrame;
import com.compendium.ui.UINode;
import com.compendium.ui.UIScribblePad;
import com.compendium.ui.UIUtilities;
import com.compendium.ui.UIViewFrame;
import com.compendium.ui.UIViewPane;
import com.compendium.ui.dialogs.UIDropFileDialog;
import com.compendium.ui.dialogs.UIDropSelectionDialog;
import com.compendium.ui.edits.PCEdit;
import com.compendium.ui.panels.UIHintNodeDetailPanel;
import com.compendium.ui.plaf.LinkUI;
import com.compendium.ui.plaf.NodeUI;
import com.compendium.ui.plaf.ViewPaneUI;

import com.compendium.ui.stencils.DraggableStencilIcon;
import com.compendium.meeting.MeetingEvent;

import java.awt.LayoutManager;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.compendium.learningdesign.ui.panels.*;
import com.compendium.learningdesign.ui.dialogs.*;
import com.compendium.learningdesign.ui.draggable.LdDraggableToolBarIcon;
import com.compendium.learningdesign.core.ILdCoreConstants;
import com.compendium.learningdesign.core.datamodel.*;
import com.compendium.learningdesign.ui.*;
import com.compendium.learningdesign.ui.popups.UILdViewPopupMenu;

/**
 * Sub-class of UIViewPane which displays learning design activities.
 * This class has an additional instance variable which stores the instance
 * of UILdActivityTimesDialog for the activity.
 * @author ajb785
 *
 */
/**
 * @author ajb785
 *
 */
public class UILdViewPane extends UIViewPane {
	/** Task sequence added property name for use with property change events */
	public final static String TASK_SEQUENCE_ADDED		= 	"taskSequenceAddded";

	/** Task sequence deleted property name for use with property change events */
	public final static String TASK_SEQUENCE_DELETED		= 	"taskSequenceDeleted";
	
	/** Task sequence created property name for use with property change events */
	public final static String TASK_SEQUENCE_SET_CREATED		= 	"taskSequenceCreated";
	
	/** Task sequence added property name for use with property change events */
	public final static String LINK_DELETED		= 	"linkDeleted";
	
	
	/** A reference to the layer holding the activity information  notes, e.g. task times.*/
	public final static Integer	ACTIVITY_INFO_LAYER		= new Integer(500);
	
	/**	A reference to the activity times panel for this instance - NOT USED oActivityTimesFrame  used instead **/
	private UILdActivityTimesPanel oActivityTimesPanel = null;
	
	/**	A reference to the activity times panel for this instance **/
	private UILdActivityTimesFrame oActivityTimesFrame = null;
	
	/**	A reference to the activity times dialog for this instance - NOT USED oActivityTimesFrame  used instead **/
	private UILdActivityTimesDialog dlgActivityTimesDialog = null;
	
	/**	A reference to the set of Ld task sequences for this instance  **/
	private LinkedHashSet<UILdTaskSequence> oTaskSequenceSet = null;
	
	/** Boolean to indicate whether the timing information should be displayed.	**/
	private boolean bShowTimingInfo = true;
	
	/** 
	 * @param view
	 * @param viewframe
	 */
	public UILdViewPane(LdActivityView view, UIViewFrame viewframe) {
		super(view, viewframe);
		oActivityTimesFrame = new UILdActivityTimesFrame(this);
		LdActivityTimes oLdActivityTimes  = ((LdActivityView)this.getView()).getLdActivityTimes();
		LinkedHashSet<LdTaskSequence> oLdTaskSequenceSet = oLdActivityTimes.getTaskSequenceSet();
		Iterator<LdTaskSequence> oIt = oLdTaskSequenceSet.iterator();
		UILdTaskSequence oUILdTaskSequence;
		oTaskSequenceSet = new LinkedHashSet<UILdTaskSequence>();
		while (oIt.hasNext()){
			oUILdTaskSequence = new UILdTaskSequence(oIt.next(), this);
//			oActivityTimesFrame.getTaskTimesTable().addTaskSequence(oUILdTaskSequence);
			// The addToTaskSequenceSet(oUILdTaskSequence) method fires a property change to update oActivityTimesFrame 
			this.addToTaskSequenceSet(oUILdTaskSequence);
		}
		this.addPropertyChangeListener(oActivityTimesFrame);
		if (oTaskSequenceSet.size() > 0)
			this.firePropertyChange(UILdViewPane.TASK_SEQUENCE_SET_CREATED, "", oTaskSequenceSet);
		// Create the UILdActivityTimesFrame instance which displays the timing info if oLdActivityTimes.getShowTime() = true
		
		this.setShowTimingInfo(oLdActivityTimes.getShowTime());
	}

	/**
	 * Show or hide the timing info  for this activity depending on the value
	 * of the parameter bShow.
	 * @param bShow, true to show the timing info, false to hide the timing info.
	 */
	public void updateShowActivityTimes(boolean bShow) {
		/** Create oActivityTimesFrame if it is  not null  ****/	
		if (oActivityTimesFrame == null)	{
			oActivityTimesFrame = new UILdActivityTimesFrame(this);
		}
		LdActivityTimes oLdActivityTimes  = ((LdActivityView)this.getView()).getLdActivityTimes();
		oLdActivityTimes.setShowTime(bShow);
		showActivityTimes();
		this.updateShowTaskTimes(bShow);
//		}
	}

	public void initialiseActivityTimes()	{
		if (oActivityTimesFrame != null)	{
			//	Point newLoc= calculateLocation(oActivityTimesPanel, 10, 10);
			Point newLoc= calculateLocation(oActivityTimesFrame, 10, 10);
			oActivityTimesFrame.setLocation(newLoc.x, newLoc.y);
			add(oActivityTimesFrame, ACTIVITY_INFO_LAYER);
			
			if (!oActivityTimesFrame.isShowing()) {
				oActivityTimesFrame.setVisible(true);
			}
		}
		/** else oActivityTimesDialog is null 	**/
		else	{
			oActivityTimesFrame = new UILdActivityTimesFrame(this);
			showActivityTimes();
		}
		
	}
		
	
	/**
	 * Show the timing info  for this activity. This method calculates where to 
	 * display the UILdActivityTimesFrame instance and shows it. If it does not 
	 * already exists, this method creates the  UILdActivityTimesFrame instance 
	 * necessary to display the timing info. 
	 */
	public void showActivityTimes() {
		// Hide timing info then return if boolean bShowTimingInfo indicates that the timing info should not be shown
		if (!this.getShowTimingInfo())	{
			if (oActivityTimesFrame != null)	{
				oActivityTimesFrame.setVisible(false);
			}
			else {
				//Need to create a oActivityTimesFrame ??? AJB 5 p.m. 23/02/2009
			}
			//this.updateShowTaskTimes(false);
			return;
		}

		else	{
			// Timing info should be shown, so display it or create it and display it
			/** Test if  oActivityTimesDialog is not null  if (oActivityTimesPanel != null) ****/
			if (oActivityTimesFrame != null)	{
				//	Point newLoc= calculateLocation(oActivityTimesPanel, 10, 10);
				Point newLoc = calculateLocation(oActivityTimesFrame, 10, 10);
				oActivityTimesFrame.setLocation(newLoc.x, newLoc.y);
				add(oActivityTimesFrame, ACTIVITY_INFO_LAYER);
			}
			else	{ /** else oActivityTimesDialog is null 	**/
				oActivityTimesFrame = new UILdActivityTimesFrame(this);
				//showActivityTimes();
			}
			if (!oActivityTimesFrame.isShowing()) {
				oActivityTimesFrame.setVisible(true);
			}
		}

		//this.updateShowTaskTimes(true);
	}


	/**
	 * This method hides the timing information for this instance.
	 */
	public void hideActivityTimes()	{
		if (oActivityTimesFrame != null)	{
			oActivityTimesFrame.setVisible(false);
		}
		this.updateShowTaskTimes(false);
	}
	
	/**
	 * Update all task nodes to hide or show the task time for all the task nodes in this UILdViewPane instance.
	 * @param bShowTimes, true if the task times are to be shown, false if 
	 * they're to be hidden.
	 */
	private void updateShowTaskTimes(boolean bShowTimes)	{
		Vector<UILdTaskNode> vtAllTaskNodes = this.getUILdTaskNodes();
		// Hide the task time for each node ** REMEMBER THAT THE VECTOR MIGHT BE EMPTY
		Iterator<UILdTaskNode> oIt = vtAllTaskNodes.iterator();
		UILdTaskNode oCurrentTaskNode;
		while (oIt.hasNext())	{
			oCurrentTaskNode=oIt.next(); 
			oCurrentTaskNode.setShowTime(bShowTimes);
		}
		this.repaint();
	}
	
	/**
	 * Retrieves a UILdTaskNode from this view based on it Id.
	 * This is  a rewrite of the UIViewPane methot get(String id) because that method failed 
	 * in some operations and I can't work out why.
	 *
	 * @param id of the component to return.
	 * @return Object, the object with the given id.
	 * @see java.awt.LayoutManager
	 */
	public Object getTaskNode(String id) {

		Component [] arrayAllComps = this.getComponents();
		for(int i=0;i<arrayAllComps.length;i++) {
			JComponent object = (JComponent)arrayAllComps[i];
			if (object instanceof UILdTaskNode)	{
				UILdTaskNode uinode = (UILdTaskNode)object;

				if(uinode != null && uinode.getNode() != null && uinode.getNode().getId().equals(id))
											return uinode;	
					
			}
		}

		return null;
	}
	
	/**
	 * Retrieves a component from this view based on it Id.
	 *
	 * @param id of the component to return.
	 * @return Object, the object with the given id.
	 * @see java.awt.LayoutManager
	 */
	public Object get(String id) {

		Component [] array = getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());
		for(int i=0;i<array.length;i++) {
			JComponent object = (JComponent)array[i];
			UINode uinode = (UINode)object;
			if(uinode != null && uinode.getNode() != null && uinode.getNode().getId().equals(id))
				return uinode;
		}

		Component [] array1 = getComponentsInLayer((UIViewPane.LINK_LAYER).intValue());
		for(int i=0;i<array1.length;i++) {
			JComponent object = (JComponent)array1[i];
			UILink uilink = (UILink)object;
			if(uilink != null && uilink.getLink() != null && uilink.getLink().getId().equals(id))
				return uilink;
		}
		return null;
	}
	
	/**
	 * This method is not need because additions to the task sequence set are 
	 * made using links, i.e when links are created.
	 * @param from
	 * @param to
	 */
	/**
	public void addToTaskSequenceSet(UINode uiFrom, UINode uiTo)	{
		NodeSummary from = uiFrom.getNode();
		NodeSummary to	= uiTo.getNode();
		
		if (((from.getLdType() == ILdCoreConstants.iLD_TYPE_TASK) &&
				(to.getLdType() == ILdCoreConstants.iLD_TYPE_TASK))	)	{
		// It's a link between two task nodes	
			UILdTaskNode fromTaskNode = (UILdTaskNode)uiFrom;
			UILdTaskNode toTaskNode = (UILdTaskNode)uiTo;
			LdTaskSequence aTaskSequence = new LdTaskSequence(fromTaskNode, toTaskNode);
			if (!this.getTaskSequenceSet().add(aTaskSequence))
			 	{
				ProjectCompendium.APP.displayError("Warning: (UILdViewPane.addToTaskSequenceSet for nodes from: " + 
						fromTaskNode.toString() + ", to: " + toTaskNode.toString() + "failed!");
			}
		}
	}
	
	**/
	
	/**
	 * @param aLdTaskSequence. an instance of LdTaskSequence.
	 * @return boolean, true if the parameter aLdTaskSequence is successfully 
	 * added to the task sequence set, false otherwise. This method fires a 
	 * TASK_SEQUENCE_ADDED property change if the task sequence was added successfully.
	 */
	public boolean addToTaskSequenceSet(UILdTaskSequence aUILdTaskSequence)	{
		// Make the ActivityTimesFrame instance listen to property changes from aUILdTaskSequence
		aUILdTaskSequence.addPropertyChangeListener(this.getActivityTimesFrame());
		//Make this UILDViewPane listen to property changes from the sequence - this is now done in the UILdTaskSequence constructor  
		// aUILdTaskSequence.addPropertyChangeListener(this);
		boolean sequenceAdded = this.getTaskSequenceSet().add(aUILdTaskSequence);
		if (sequenceAdded)	{
		this.firePropertyChange(UILdViewPane.TASK_SEQUENCE_ADDED, null, aUILdTaskSequence);
		return sequenceAdded;
		}
		else	{
			return false;
		}
		
	}
	
	  
	
	
	/**
	 * Calculate the position for the popup panel passed. 
	 * This is unchanged from the superclass - do I need to change it?
	 * @param pop the popup panel
	 * @param realX the starting x
	 * @param realY the starting y
	 * @return the final location for the panel
	 */
	private Point calculateLocation(UILdActivityTimesFrame pop, int realX, int realY) {
		Rectangle rect =  this.getBounds();
		int screenWidth = rect.width;
		int screenHeight = rect.height;
		int iTimesWidth = pop.getWidth();
		int iTimesHeight = pop.getHeight();
		
		Rectangle  oVisRect = this.getVisibleRect();
		/**
		Rectangle rect = getViewFrame().getViewport().getViewRect();
		int screenWidth = rect.width;
		int screenHeight = rect.height;
		screenWidth = rect.x + screenWidth;
		screenHeight = rect.y + screenHeight;
		if ((pop.getWidth() < 10) || (pop.getHeight() < 15))
			pop.setSize(120, 80);
		int endXCoordForPopUpMenu = realX + pop.getWidth();
		int endYCoordForPopUpMenu = realY + pop.getHeight();

		int offsetX = (screenWidth) - endXCoordForPopUpMenu;
		int offsetY = (screenHeight) - endYCoordForPopUpMenu;

		if(offsetX > 0)
			offsetX = 0;
		if(offsetY > 0)
			offsetY = 0;

		int finalX = realX+offsetX;
		if (realX+offsetX < rect.x) {
			finalX = rect.x;
		}
		
		return new Point(finalX, realY+offsetY);
		**/
		// This point is fixed until position data is stored in the database
		return new Point(10, 10);
	}
	
	/**
	 *	Convenience Method to get Containing ViewFrame.
	 * @return UIViewFrame, the parent frame for this view.
	 */
	public UILdActivityViewFrame getViewFrame() {
		return (UILdActivityViewFrame)oViewFrame;
	}

	/**
	 * @return the oActivityTimesPanel
	 */
	public UILdActivityTimesPanel getActivityTimesPanel() {
		return oActivityTimesPanel;
	}

	/**
	 * @param activityTimesPanel the oActivityTimesPanel to set
	 */
	public void setActivityTimesPanel(UILdActivityTimesPanel activityTimesPanel) {
		oActivityTimesPanel = activityTimesPanel;
	}

	//*********************** PROPERTY CHANGE LISTENER *************************/

	/**
	 * Handles property change events. 
	 * Based on UIViewPane method, but with addition event handlers for learning
	 * design events. NOTE - NO LD CODE has yet been ADDED - THIS METHOD IS 
	 * STILL JUST A PURE COPY OF THAT FROM CLASS UIViewPane.
	 * @param evt, the associated PropertyChangeEvent object.
	 */
	public void propertyChange(PropertyChangeEvent evt) {

		String prop = evt.getPropertyName();

		Object source = evt.getSource();
		Object oldvalue = evt.getOldValue();
		Object newvalue = evt.getNewValue();
		/** Added by Andrew	**/
		if (source instanceof UILdTaskTimeDialog) {
			prop.contentEquals("");

		}
		/** End of added by Andrew	**/
//		View events	    
		if (source instanceof View) {
			if (prop.equals(View.LINK_ADDED)) {
				Link link = (Link)newvalue;
				((UIMapViewFrame)oViewFrame).addAerialLink(link);
				/**
				if (UILdTaskSequence.isSuitableLink(link))	{
					addToOrCreateTaskSequence(link);
				}
				**/
			}
			else if (prop.equals(View.LINK_REMOVED)) {
				Link link = (Link)newvalue;
				((UIMapViewFrame)oViewFrame).removeAerialLink(link);
			}
			else if (prop.equals(View.NODE_ADDED)) {
				/**	Andrew - put Learning design stuff here e.g. add to sequence **/
				NodePosition oNodePos = (NodePosition)newvalue;
				((UIMapViewFrame)oViewFrame).addAerialNode(oNodePos);

				// IF RECODRING or REPLAYING A MEETING, SEND A NODE ADDED EVENT
				if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents()) {

					// IF NODE NOT ALREADY THERE, SEND EVENT
					UINode oNode = (UINode)get(oNodePos.getNode().getId());
					if (oNode == null) {
						ProjectCompendium.APP.oMeetingManager.addEvent(
								new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
										ProjectCompendium.APP.oMeetingManager.isReplay(),
										MeetingEvent.NODE_ADDED_EVENT,
										oNodePos));
					}
				}
			}
/**			else if (prop.equals(UIViewPane.NODE_ADDED)) {
				NodePosition oNodePos = (NodePosition)newvalue;
				((UIMapViewFrame)oViewFrame).addAerialNode(oNodePos);
			}	**/
			else if (prop.equals(View.NODE_TRANSCLUDED)) {
				NodePosition oNodePos = (NodePosition)newvalue;
				((UIMapViewFrame)oViewFrame).addAerialNode(oNodePos);

				// IF RECODRING or REPLAYING A MEETING, SEND A NODE TRANSCLUDED EVENT
				if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents()) {

					// IF NODE NOT ALREADY THERE, SEND EVENT
					UINode oNode = (UINode)get(oNodePos.getNode().getId());
					if (oNode == null) {
						ProjectCompendium.APP.oMeetingManager.addEvent(
								new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
										ProjectCompendium.APP.oMeetingManager.isReplay(),
										MeetingEvent.NODE_TRANSCLUDED_EVENT,
										oNodePos));
					}
				}
			}
			else if (prop.equals(View.NODE_REMOVED)) {

				NodeSummary node = (NodeSummary)newvalue;
				((UIMapViewFrame)oViewFrame).removeAerialNode(node);

				// IF RECODRING or REPLAYING A MEETING, SEND A NODE REMOVED EVENT
				if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents()) {

					ProjectCompendium.APP.oMeetingManager.addEvent(
							new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
									ProjectCompendium.APP.oMeetingManager.isReplay(),
									MeetingEvent.NODE_REMOVED_EVENT,
									oView,
									node));
				}
			}
		}   
//		UINode events	    
		else if (source instanceof UINode) {
			if (prop.equals(UINode.ROLLOVER_PROPERTY)) {
				UINode node = (UINode)source;
				((UIMapViewFrame)oViewFrame).setAerialRolloverNode(node, ((Boolean)newvalue).booleanValue());
			}
			//else if (prop.equals(UINode.TYPE_PROPERTY)) {
			//	UINode node = (UINode)source;
			//	((UIMapViewFrame)oViewFrame).setAerialNodeType(node, ((Integer)newvalue).intValue());
			//}
			else if (prop.equals(NodePosition.POSITION_PROPERTY)) {
				UINode uinode = (UINode)source;
				Point oPoint = (Point)newvalue;
				Point transPoint = UIUtilities.transformPoint(oPoint.x, oPoint.y, currentScale);

				// CHECK THAT THIS NODE WAS NOT THE ONE ORIGINATING THE EVENT
				Point location = uinode.getLocation();
				if (location.x != transPoint.x && location.y != transPoint.y) {
					uinode.setBounds(transPoint.x, transPoint.y, uinode.getWidth(), uinode.getHeight());
					uinode.updateLinks();
				}		    	
			}
		}

		else if (source instanceof UILdTaskSequence) {
//			ALL_LINKS_DELETED means a node has been  deleted that has removed all links from the sequence
			if (prop.equals(UILdTaskSequence.ALL_LINKS_DELETED))	{
				LinkedHashSet<UILdTaskSequence> oSeqSet = new LinkedHashSet<UILdTaskSequence>();
				oSeqSet.add((UILdTaskSequence)source);
				this.removeTaskSequences(oSeqSet);
			}
			if (prop.equals(UILdTaskSequence.SEQUENCE_CREATED))	{
				UILdTaskSequence aUILdTaskSequence = (UILdTaskSequence)newvalue;
				this.addToTaskSequenceSet(aUILdTaskSequence);
			}	
		
		}
	}

	
	/**
	 * Get the UILdActivityTimesFrame instance which displays the timing info 
	 * for the activity displayed in this pane.
	 * @return the oActivityTimesFrame
	 */
	public UILdActivityTimesFrame getActivityTimesFrame() {
		return oActivityTimesFrame;
	}

	/**
	 * @return Vector<NodeSummary> vtRoleNodes, a Vector of the Ld role nodes 
	 * in the View shown in this instance of UILdViewPane.
	 */
	public Vector<NodeSummary> getRoleNodes()	{
		Vector<NodeSummary> vtMemberNodes = new Vector<NodeSummary>(this.getView().getMemberNodes());
		Vector<NodeSummary> vtRoleNodes = new Vector<NodeSummary>();
		NodeSummary thisNode;
		while(vtMemberNodes.iterator().hasNext())	{
			thisNode = vtMemberNodes.iterator().next();
			if (thisNode.isLdRoleNode() )	{
				vtRoleNodes.add(thisNode);
			}
	}
	return vtRoleNodes;
	}
	
	/**
	 * @return Vector<NodeSummary> vtRoleNodes, a Vector of the Ld task nodes 
	 * in the View shown in this instance of UILdViewPane.
	 */
	public Vector<NodeSummary> getTaskNodes()	{
		Vector<NodeSummary> vtMemberNodes = new Vector<NodeSummary>(this.getView().getMemberNodes());
		Vector<NodeSummary> vtTaskNodes = new Vector<NodeSummary>();
		Iterator<NodeSummary> oIt = vtMemberNodes.iterator();
		NodeSummary thisNode;
		while(oIt.hasNext())	{
			thisNode = oIt.next();
			if (thisNode.getLdType() == ILdCoreConstants.iLD_TYPE_TASK )	{
				vtTaskNodes.add(thisNode);
			}
	}
	return vtTaskNodes;
	}	
	
	/**
	 * Find all the UILdTaskNodes in this view pane and return a vector of them.
	 * @return Vector<UILdTaskNode>, a vector containing all the task nodes in this view pane.
	 */
	public Vector<UILdTaskNode> getUILdTaskNodes()	{
		Vector<NodeSummary> vtTaskNodes = this.getTaskNodes();
		/** this getComponentsInLayer method doesn't seem to work reliably so use
		 * another mechanism i.e. get all task nodes in the view then get Uinodes with samme id  **/
	//	Component componentArray[] = this.getComponentsInLayer((UIViewPane.NODE_LAYER).intValue());

		UILdTaskNode oUITaskNode = null;
		Vector<UILdTaskNode> vtUILdTaskNodes = new Vector<UILdTaskNode>();
		String sTaskNodeId;
		for (NodeSummary oTaskNodeSummary: vtTaskNodes)	{
			sTaskNodeId = oTaskNodeSummary.getId();
			oUITaskNode = (UILdTaskNode)this.getTaskNode(oTaskNodeSummary.getId());
			if (oUITaskNode != null)	{
				vtUILdTaskNodes.add(oUITaskNode);
			}
			else	{
				ProjectCompendium.APP.displayError("Could not find the requested node in this view.\nIt may have been moved or deleted from the view since the reference was created.\n\nSearch may find the node in its new location.\n");
				System.out.println("Could not find the requested node in this view. Node id = " + sTaskNodeId + " View id = " + this.getView().getId() );
			}
			
		}
		return vtUILdTaskNodes;
	}
	
	
	private void hideTimesForTaskNodes(Vector<NodeSummary> vtTaskNodes)	{
		Iterator<NodeSummary> oIt = vtTaskNodes.iterator();
		while(oIt.hasNext())	{
			NodeSummary thisNode = oIt.next();
		}
	}
	
	/**
	 * Create a new node from a stencil item. This method creates 
	 * LdTasksequence data, whereas the method with the same signature in
	 * class UIViewPane does not, because a UIViewPane cannot display 
	 * task sequence data.
	 * @param stencil, the stencil item to create the node from.
	 * @param nX, the x position for the new node.
	 * @param nY, the y position for the new node.
	 */
/** This is implemented in class UIViewPane so ld nodes can be created in any map 	
	public void createNodeFromStencil( DraggableStencilIcon stencil, int nX, int nY) {

		String sImage = stencil.getImage();
		String sBackgroundImage = stencil.getBackgroundImage();
		String sTemplate = stencil.getTemplate();
		
		String sLabel = stencil.getLabel();
		int nType = stencil.getNodeType();
		Vector vtTags = stencil.getTags();
		 ..etc 
		 
		 **/
	
	/**
	 * Override UIViewPane method to treat reference nodes 
	 * differently i.e. to tag them as Ld resource nodes.
	 * Create node(d) for the given file/directory of files
	 * @param pane The view to create the top node in.
	 * @param file the File object to process
	 * @param nX the x location of the top node.
	 * @param nY the y location of the top node.
	 */
	public void createNodes(UIViewPane pane, File file, int nX, int nY) {

		UINode node;
		String sAuthor = this.getAuthor();
		if (file.isDirectory()) {			
			int oneLevelChoice = JOptionPane.showConfirmDialog(
					ProjectCompendium.APP,
					"Create a Reference to the directory?\nSelect 'No' to create a Map " +
					"with the directory contents.",
					"Create a Reference to directory?",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (oneLevelChoice == JOptionPane.YES_OPTION){
				node = oViewPaneUI.addNewNode(ICoreConstants.REFERENCE, nX, nY);
				node.setText(file.getName());
				try { node.getNode().setSource(file.getPath(), "", sAuthor);}
				catch (Exception ex) {return;}
				node.setReferenceIcon(file.getPath());
				node.getUI().refreshBounds();
				return;
			} else if (oneLevelChoice == JOptionPane.CANCEL_OPTION) return; //do nothing
						

			node = oViewPaneUI.addNewNode(ICoreConstants.MAPVIEW, nX, nY);
			node.setText(file.getName());
			node.setReferenceIcon(file.getPath());
			node.getUI().refreshBounds();

			File[] files = file.listFiles(); // list files in directory
			nX = 10;
			nY = 10;
			View view = (View) node.getNode();
			boolean add_recursively = false;
			boolean alreadyAsked = false;
			if (FormatProperties.dndAddDirRecursively) {
				add_recursively = true;
				alreadyAsked = true;				
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory() && !alreadyAsked) {
					int recursiveChoice = JOptionPane.showConfirmDialog(
							ProjectCompendium.APP,
							"Do you want to add subdirectories recursively as map nodes?",
							"Add subdirectories recursively?",
							JOptionPane.YES_NO_OPTION);
					if (recursiveChoice == JOptionPane.YES_OPTION)
						add_recursively = true;
					alreadyAsked = true;
				}
				createSingleNode(view, files[i], nX, nY, add_recursively);
				nY += 80;
			}
		} else { // create a REFERENCE NODE
			String fileName = file.getName();
			if (!fileName.startsWith(".") || ProjectCompendium.isWindows) {
				fileName = fileName.toLowerCase();

				if ((fileName.endsWith(".xml") || fileName.endsWith(".zip"))
						&& file.exists()) {
					UIDropFileDialog dropDialog = new UIDropFileDialog(
							ProjectCompendium.APP, pane, file, nX, nY);
					dropDialog.setVisible(true);
				} else {
					file = UIUtilities.checkCopyLinkedFile(file);

					node = oViewPaneUI.addNewLdNode(ICoreConstants.REFERENCE, nX,
							nY, ILdCoreConstants.iLD_TYPE_RESOURCE);
//					node = oViewPaneUI.addNewNode(ICoreConstants.REFERENCE, nX,
//							nY);
					node.setReferenceIcon(file.getPath());
									
					try {
						if (UIImages.isImage(file.getPath()))
							node.getNode().setSource("", file.getPath(),
									sAuthor);
						else
							node.getNode().setSource(file.getPath(), "",
									sAuthor);
						/** Added by Andrew to set Ld type to resource  for resources added to activities **/

						if (oView.getLdType() == ILdCoreConstants.iLD_TYPE_ACTIVITY)	{
									node.getNode().setLdType(ILdCoreConstants.iLD_TYPE_RESOURCE, sAuthor);
						}

					} catch (Exception ex) {
						System.out.println("error in UIViewPane.drop-2) \n\n"
								+ ex.getMessage());
					}
					node.setText(file.getName());
					node.getUI().refreshBounds();
				}
			}
		}
	}
	
	  /**
     * Called when the drag operation has terminated with a drop on
     * the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     * <p>
     * This method is responsible for undertaking
     * the transfer of the data associated with the
     * gesture. The <code>DropTargetDropEvent</code>
     * provides a means to obtain a <code>Transferable</code>
     * object that represents the data object(s) to
     * be transfered.<P>
     * From this method, the <code>DropTargetListener</code>
     * shall accept or reject the drop via the
     * acceptDrop(int dropAction) or rejectDrop() methods of the
     * <code>DropTargetDropEvent</code> parameter.
     * <P>
     * Subsequent to acceptDrop(), but not before,
     * <code>DropTargetDropEvent</code>'s getTransferable()
     * method may be invoked, and data transfer may be
     * performed via the returned <code>Transferable</code>'s
     * getTransferData() method.
     * <P>
     * At the completion of a drop, an implementation
     * of this method is required to signal the success/failure
     * of the drop by passing an appropriate
     * <code>boolean</code> to the <code>DropTargetDropEvent</code>'s
     * dropComplete(boolean success) method.
     * <P>
	 * This method accept or declines the drop of an external file, directory or text block.
     * <P>
     * @param e the <code>DropTargetDropEvent</code>
     */
	public void drop(DropTargetDropEvent e) {

		// IF THE SCRIBBLE LAYER IS ON AND ON THE TOP LAYER, REJECT ALL DROPS
		UIScribblePad pad = getScribblePad();
		if (pad != null && pad.isVisible() && getLayer(oScribblePad) == SCRIBBLE_LAYER) {
			return;
		}

		try {
       		final Transferable tr = e.getTransferable();
			final UILdViewPane pane = this;
			final DropTargetDropEvent evt = e;

			Point dropPoint = e.getLocation();

			int nX = dropPoint.x;
			int nY = dropPoint.y;
			if (nX >= 20 && nY >= 10) {
				nX -= 20;
				nY -= 10;
			}
			DataFlavor[]  oDF = tr.getTransferDataFlavors();
		 	if (tr.isDataFlavorSupported(DraggableStencilIcon.supportedFlavors[0])) {
				Object source = tr.getTransferData(DraggableStencilIcon.supportedFlavors[0]);
				if (source instanceof DraggableStencilIcon) {
					DraggableStencilIcon stencil = (DraggableStencilIcon)source;
					createNodeFromStencil(stencil, nX, nY);
				}
				else if (source instanceof LdDraggableToolBarIcon) {
					LdDraggableToolBarIcon oLdTbIcon = (LdDraggableToolBarIcon)source;
					NodeSummary  oDroppedNode = oLdTbIcon.getNodeSummary();
					this.createNodeFromLdDraggableIcon(oLdTbIcon, nX, nY);
				} 
			}		 
            else if (tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {

				e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				final java.util.List fileList = (java.util.List) tr.getTransferData(DataFlavor.javaFileListFlavor);

				// new Thread required for Mac bug caused when code calls UIUtilities.checkCopyLinkedFile
				// and tries to open a JOptionPane popup.
				final int xPos = nX;
				final int yPos = nY;
				Thread thread = new Thread("UIViewPane.drop-FileListFlavor") {
					public void run() {

						int nX = xPos;
						int nY = yPos;

						Iterator iterator = fileList.iterator();
						while (iterator.hasNext()) {
							createNodes(pane, (File) iterator.next(), nX, nY);
							nY = +80;
						}
												
						evt.getDropTargetContext().dropComplete(true);
					}
				};
				thread.start();
        	}
  			else if (tr.isDataFlavorSupported(DataFlavor.stringFlavor)) {

				e.acceptDrop(DnDConstants.ACTION_COPY);
				final String dropString = (String)tr.getTransferData(DataFlavor.stringFlavor);

				// new Thread required for Mac bug caused when code calls UIUtilities.checkCopyLinkedFile
				// and tries to open a JOptionPane popup.
				final int xPos = nX;
				final int yPos = nY;
				
				Thread thread = new Thread("UIViewPane.drop-StringFlavor") {
					public void run() {
						String sAuthor = oViewPaneUI.getViewPane().getAuthor();
						int nX = xPos;
						int nY = yPos;
						String s = dropString;

						
						boolean bdragdropKDE = false;
						if (ProjectCompendium.isLinux) { 
							if (s.startsWith("www.") || s.startsWith("http://")
									|| s.startsWith("https://")) {
								UINode node = oViewPaneUI.addNewNode(
										ICoreConstants.REFERENCE, nX, nY);
								node.setText(s);
								try {
									node.getNode().setSource(s, "", sAuthor);
									node.setReferenceIcon(s);
								} catch (Exception ex) {
									System.out
											.println("error in UIViewPane.drop-2) \n\n"
													+ ex.getMessage());
								}
								node.getUI().refreshBounds();
							} else {
								final java.util.List fileList = new LinkedList();
								if (s.startsWith("file://")) {
									// remove 'file://' from file path								
									String[] liste = s.split("file://");

									for (int i = 1; i < liste.length; i++) {
										// remove 'file://' from file path
										String filename = new String(liste[i]
												.replaceFirst("\n", "")); 
										File file = new File(filename);
										fileList.add(file);
									}
									Iterator iterator = fileList.iterator();

									nX = xPos;
									nY = yPos;
									while (iterator.hasNext()) {
										createNodes(pane, (File) iterator
												.next(), nX, nY);
										nY = +80;
									}
									// drop object is not a file but e.g. text									
									bdragdropKDE = true; 
								} else {
									bdragdropKDE = false;
								}
							}
						}
						
						try {
							int nType = new Integer(s).intValue();
							oViewPaneUI.addNewNode(nType, nX, nY);
							evt.getDropTargetContext().dropComplete(true);
						}
						catch(NumberFormatException io) {

							if (UINode.isReferenceNode(s)) {

								File newFile = new File(s);
								String fileName = newFile.getName();
								fileName = fileName.toLowerCase();

								String sDatabaseName = CoreUtilities.cleanFileName(ProjectCompendium.APP.sFriendlyName);						
								UserProfile oUser = ProjectCompendium.APP.getModel().getUserProfile();
								String sUserDir = CoreUtilities.cleanFileName(oUser.getUserName())+"_"+oUser.getId();
								String sFullPath = "Linked Files"+ProjectCompendium.sFS+sDatabaseName+ProjectCompendium.sFS+sUserDir;			

								File directory = new File(sFullPath);
								if (!directory.isDirectory()) {
									directory.mkdirs();
								}
								String sFilePath = sFullPath+ProjectCompendium.sFS;
								directory = new File(sFilePath);
								if (ProjectCompendium.isMac)
									sFilePath = directory.getAbsolutePath()+ProjectCompendium.sFS;

								String sActualFilePath = "";
								try {
									sActualFilePath = UIImages.loadWebImageToLinkedFiles(s, fileName, sFilePath);
								}
								catch(Exception exp) {}

								if (!sActualFilePath.equals("")) {
									UINode node = oViewPaneUI.addNewNode(ICoreConstants.REFERENCE, nX, nY);
									node.setReferenceIcon(sActualFilePath);

									try {
										node.getNode().setSource("", sActualFilePath, sAuthor);
									}
									catch(Exception ex) {
										System.out.println("error in UIViewPane.drop-3b) \n\n"+ex.getMessage());
									}

									File temp = new File(sActualFilePath);
									node.setText(temp.getName());
									node.getUI().refreshBounds();
								}
								else {
									newFile = UIUtilities.checkCopyLinkedFile(newFile);
									if (newFile != null)
										s = newFile.getPath();																	
									UINode node = oViewPaneUI.addNewNode(ICoreConstants.REFERENCE, nX, nY);
									
									node.setReferenceIcon(s);

									try {
										if (UIImages.isImage(s))
											node.getNode().setSource("", s, sAuthor);
										else {
											node.getNode().setSource(s, "", sAuthor);
										}
										/** Added by Andrew to set Ld type to resource  for resources added to activities **/

										if (oView.getLdType() == ILdCoreConstants.iLD_TYPE_ACTIVITY)	{
											node.getNode().setLdType(ILdCoreConstants.iLD_TYPE_RESOURCE, sAuthor);

										}

									}
									catch(Exception ex) {
										System.out.println("error in UIViewPane.drop-3) \n\n"+ex.getMessage());
									}

									node.setText(s);
									node.getUI().refreshBounds();
								}
								evt.getDropTargetContext().dropComplete(true);
							}
							else {
								if (!bdragdropKDE) { 
									UIDropSelectionDialog dropDialog = new UIDropSelectionDialog(ProjectCompendium.APP, pane, s, nX, nY);
									if (FormatProperties.dndNoTextChoice) {
										dropDialog.processAsPlain();
										dropDialog.onCancel();
									}
									else {
										dropDialog.setVisible(true);
									}
									evt.getDropTargetContext().dropComplete(true);
								}
							}
						}
					}
				};
				thread.start();
			}
			else if (tr.isDataFlavorSupported(DataFlavor.imageFlavor)) {
				e.acceptDrop(DnDConstants.ACTION_COPY);

				Image img = (Image)tr.getTransferData(DataFlavor.imageFlavor);
				if (img instanceof BufferedImage) {
					try {
						String sAuthor = oViewPaneUI.getViewPane().getAuthor();
						File newFile = new File("Linked Files"+ProjectCompendium.sFS+"External_Image_"+(new Date()).getTime()+".jpg");

						ImageIO.write((RenderedImage)img, "jpeg", newFile);

						if (newFile.exists()) {
							String s = "";
							if (newFile != null)
								s = newFile.getPath();

							//UINode node = oViewPaneUI.addNewNode(ICoreConstants.REFERENCE, nX, nY);
							UINode node = oViewPaneUI.addNewLdNode(ICoreConstants.REFERENCE, nX, nY, ILdCoreConstants.iLD_TYPE_RESOURCE);
							node.setReferenceIcon(s);
							try {
								if (UIImages.isImage(s))
									node.getNode().setSource("", s, sAuthor);
								else {
									node.getNode().setSource(s, "", sAuthor);
								}
							}
							catch(Exception ex) {
								System.out.println("error in UIViewPane.drop-4) \n\n"+ex.getMessage());
							}

							node.setText(s);
							node.getUI().refreshBounds();
						}
					}
					catch(IOException io) {
						System.out.println("io exception "+io.getMessage());
					}
				}
			}
			else {
				e.rejectDrop();
			}
		}
  		catch (IOException io) {
            io.printStackTrace();
			System.out.flush();
			e.rejectDrop();
        }
		catch (UnsupportedFlavorException ufe) {
            ufe.printStackTrace();
			System.out.flush();
 			e.rejectDrop();
		}
	}

	
	/**
	 * Delete selected nodes and links in the view. This is a copy of the method in
	 *  class UIViewPane, with the additional functions of deleting the TaskSequence 
	 *  data from this instance.
	 * 
	 * @param edit, the PCEdit object to add the deleted object to for undo/redo purposes.
	 */
	public void deleteSelectedNodesAndLinks(PCEdit edit) {
		Hashtable<String, UILink> links = new Hashtable(51);

		// delete the NODES selected
	  	int i = 0;

		String sHomeViewID = ProjectCompendium.APP.getHomeView().getId();
		String sInBoxID = ProjectCompendium.APP.getInBoxID();
		IModel model = ProjectCompendium.APP.getModel();
		// boolean value to indicate if this node is connected to another node
		boolean isConnected = false;
		for(Enumeration e = getSelectedNodes(); e.hasMoreElements(); i++) {
			
			UINode uinode = (UINode)e.nextElement();
			String sNodeID = uinode.getNode().getId();
			
			if (uinode.getType() != ICoreConstants.TRASHBIN 
					&& !sNodeID.equals(sInBoxID)) {

				NodeUI nodeui = uinode.getUI();

				// IF YOU SOMEHOW GET YOUR HOMEVIEW AS A NODE AND TRY AND DELETE IT,
				// JUST REMOVE FROM VIEW, DO NOT ACTUALLY DELETE IT!!!
//***Start of HomeView block				
				if (sNodeID.equals(sHomeViewID)) {
					// StoreLinks being deleted
					for(Enumeration<UILink> es = uinode.getLinks();es.hasMoreElements();) {
						UILink uilink = es.nextElement();
						links.put(uilink.getLink().getId(), uilink);
					}

					nodeui.deleteLinksforNode(uinode, edit);
					try {
						model.getViewService().removeMemberNode(model.getSession(), oView.getId() ,sNodeID);
						model.getViewService().purgeMemberNode(model.getSession(), oView.getId() ,sNodeID);
					}
					catch(Exception ex) {
						System.out.println("Unable to remove home view node from view = "+oView.getLabel()+" due to:\n"+ex.getMessage());
					}
					nodeui.removeFromUI(uinode);
				}
//*** End of home view block				
				else {
// **** Start of normal node block i.e. sNodeID is NOT the 	sHomeViewID				
					// IF NODE ALREADY DELETED, DON'T TRY AND DELETE CHILDREN AGAIN
					// NEED TO CATCH NEVERENDING LOOP WHEN NODE CONTAINS ITSELF SOMEWHERE IN CHILDREN TREE
					boolean wasDeleted = false;
					try {
						if (model.getNodeService().isMarkedForDeletion(model.getSession(), uinode.getNode().getId())) {
							wasDeleted = true;
						}
					}
					catch (SQLException ex) {
						// WHAT TO DO?
					}
					boolean deleted = nodeui.removeFromDatamodel(uinode);
					//if (deleted || wasDeleted) {
						// StoreLinks being deleted		
//** For each link of the node of interest 					
						for(Enumeration<UILink> es = uinode.getLinks();es.hasMoreElements();) {
							UILink uilink = (UILink)es.nextElement();
							links.put(uilink.getLink().getId(), uilink);
						}
// ** end of For each link of the node of interest block	
		// delete the links attached to the node - this will cause LINK_REMOVED propertyChange to be fired, which will remove the link from the task sequence 
						nodeui.deleteLinksforNode(uinode, edit); 	
						edit.AddNodeToEdit(uinode);
						//  Delete this node (i.e. variable  uinode) from the  task sequence set, if it is in a sequence
						LinkedHashSet<UILdTaskSequence> oHs = this.getTaskSequenceSet();
						LinkedHashSet<UILdTaskSequence> oSequencesToRemove = new LinkedHashSet<UILdTaskSequence>();   
						UILdTaskSequence aTaskSequence;
						
						if (UILdNode.isLdRoleNode(uinode) || UILdNode.isLdTaskNode(uinode))	{
			// ----- Task sequence block
							// If the node is a role or task node find the task sequence containing it
							aTaskSequence = this.getTaskSequenceContaining((UILdNode)uinode);
							/** If aTaskSequence is not empty, it contains the 
							node, AND if it is only one link long, then it should be deleted
							because deleting a node from it will mean it has zero links.	**/
							if (aTaskSequence.isEmpty())	{
								oSequencesToRemove.add(aTaskSequence);
								// Remove the task sequence from the set associated with this UILdViewPane
								this.removeTaskSequences(oSequencesToRemove); // Also fire property change event
							}
							
							else 	{
							// The sequence containing the node contains more than one link, so remove the node  from it.
				// *** DO NOT NEED TO DO THIS because removing links should do it  		aTaskSequence.removeNode((UILdNode)uinode);
								
							}
			// ----- End of Task sequence block
						}
/*** Code cut goes here ********************************************/							
						
						// IF NODE IS A VIEW AND IF NODE WAS ACTUALLY LAST INSTANCE AND HAS NOT ALREADY BEEN DELETED, DELETE CHILDREN
						if (uinode.getNode() instanceof View && deleted && !wasDeleted) {
							View childView = (View)uinode.getNode();
							UIViewFrame childViewFrame = ProjectCompendium.APP.getViewFrame(childView, childView.getLabel());
							if (childViewFrame instanceof UIMapViewFrame) {
								((UIMapViewFrame)childViewFrame).deleteChildren(childView);
							} else {
								((UIListViewFrame)childViewFrame).deleteChildren(childView);
							}
							// delete from ProjectCompendium.APP opened frame list.
							ProjectCompendium.APP.removeViewFromHistory(childView);		
						}	
						// NEED TO CALL THIS TO REMOVE NODE
						nodeui.removeFromUI(uinode);
						
					//}
				} // **** End of normal node block
			}
		} //End of selected nodes loop 
		int iL = 0;
		int nL = 0;
		// PURGE ALL LINKS NOT ASSOCIATED WITH A NODE (OTHERS WILL ALREADY BE MARK FOR DELETION ABOVE)
		for(Enumeration et = getSelectedLinks();et.hasMoreElements();) {
	//		System.out.println("i = " +i);
			UILink uilink = (UILink)et.nextElement();
			if (!links.containsKey(uilink.getLink().getId())) {
				LinkUI linkui = (LinkUI)uilink.getUI();
				linkui.purgeLink(uilink);
				System.out.println("nLinks purged = " + nL);
				nL++;

				// save link in case operation needs to be undone.
				edit.AddLinkToEdit (uilink);
				
			}
			++iL;
		}
		
		
		setSelectedNode(null, ICoreConstants.DESELECTALL);
		setSelectedLink(null, ICoreConstants.DESELECTALL);
		/** Now nodes and links have been deleted remove any  empty sequences from the set **/
		LinkedHashSet<UILdTaskSequence> oHs = this.getTaskSequenceSet();
		LinkedHashSet<UILdTaskSequence> oSequencesToRemove = new LinkedHashSet<UILdTaskSequence>();   
		Iterator<UILdTaskSequence> oIt =  this.getTaskSequenceSet().iterator();
		UILdTaskSequence aTaskSequence;
		while (oIt.hasNext())	{
			aTaskSequence = oIt.next();
			if (aTaskSequence.isEmpty())	{
				oSequencesToRemove.add(aTaskSequence);
			}
		}
		this.removeTaskSequences(oSequencesToRemove);

		hideImages();
		repaint();
 	}

	/**
	 *	Remove the set of TaskSequences from this this instances TaskSequenceSet,
	 *	and fire a property change to tell listeners that a deletion has been made.
	 * @param oSequencesToRemove
	 * @return
	 */
	public boolean removeTaskSequences(LinkedHashSet<UILdTaskSequence> oSequencesToRemove){
		boolean bValuueToReturn = false;
		// Make oldVal a copy of oTaskSequenceset because oTaskSequenceSet is a reference to the set
		LinkedHashSet<UILdTaskSequence> oldVal =  new LinkedHashSet<UILdTaskSequence>(oTaskSequenceSet);
		bValuueToReturn = oTaskSequenceSet.removeAll(oSequencesToRemove);
		this.firePropertyChange(UILdViewPane.TASK_SEQUENCE_DELETED, oldVal, oTaskSequenceSet);
		return bValuueToReturn;
	}

	/**
	 * Get the set of task sequences for this instance of UILdViewPane. 
	 * @return the oTaskSequenceSet
	 */
	public LinkedHashSet<UILdTaskSequence> getTaskSequenceSet() {
		return oTaskSequenceSet;
	}


	
	/**
	 * Get the first task sequence containing the NodeSummary aNodeSummary from the set of 
	 * task sequences. If  aNodeSummary is not in any of the links in any of the sequences 
	 * in the set,return an empty  UILdTaskSequence sequence.  
	 * 
	 * @param aLink
	 * @return
	 */
	public UILdTaskSequence getTaskSequenceContaining(NodeSummary aNode)	{
		UILdTaskSequence aTaskSequence;

		while (oTaskSequenceSet.iterator().hasNext())	{
			aTaskSequence = oTaskSequenceSet.iterator().next();			
			if (aTaskSequence.contains(aNode))	{
				return aTaskSequence;				
			}
		}				
		return (new UILdTaskSequence());
	}

	
	/**
	 * Get the first task sequence containing the UILdTaskNode aUILdTaskNode from the set of 
	 * task sequences. If  aUILdTaskNode is not in any of the links in any of the sequences 
	 * in the set,return an empty  task sequence.  
	 * 
	 * @param aLink
	 * @return
	 */
	public UILdTaskSequence getTaskSequenceContaining(UILdTaskNode aUILdTaskNode)	{
		UILdTaskSequence aTaskSequence;
		Iterator<UILdTaskSequence> oIt = oTaskSequenceSet.iterator();
		while (oIt.hasNext())	{
			aTaskSequence = oIt.next();
			if (aTaskSequence.contains(aUILdTaskNode))	{
				return aTaskSequence;
			}
		}
		return (new UILdTaskSequence());
	}
	
	/**
	 * Get the first task sequence containing the UILdNode aUILdNode from the set of 
	 * task sequences. If  aUILdNode is not in any of the links in any of the sequences 
	 * in the set,return an empty  task sequence. This method will find the first 
	 * task sequence containing the node, no matter if it is a aUILdTaskNode or a
	 * UILdNode role node. 
	 * 
	 * @param aLink
	 * @return
	 */
	public UILdTaskSequence getTaskSequenceContaining(UILdNode aUILdNode)	{
		UILdTaskSequence aTaskSequence;
		Iterator<UILdTaskSequence> oIt = oTaskSequenceSet.iterator();
		while (oIt.hasNext())	{
			aTaskSequence = oIt.next();
			if (aTaskSequence.contains(aUILdNode))	{
				return aTaskSequence;
			}
		}
		return (new UILdTaskSequence());
	}
	
	
	/**
	 * Get the first task sequence containing the NodeSummary fromNode, or the 
	 * NodeSummary toNode, from the set of task sequences. An empty task sequence 
	 * is returned if no match can be found. In other words, if  neither fromNode
	 * or toNode  is in any of the links in any of the sequences in the set, 
	 * return an empty  task sequence.  Also return an empty task sequence if the
	 * Task sequence set is empty.
	 * 
	 * @param aLink
	 * @return
	 */
	/**
	public LdTaskSequence getTaskSequenceContainingANodeInLink(NodeSummary fromNode, NodeSummary toNode)	{
		LdTaskSequence aTaskSequence;
		Link aLink;
		Iterator<Link>	oLinkIt;
		if (oTaskSequenceSet.isEmpty())	{
			return (new LdTaskSequence());
		}
		else	{
			Iterator<UILdTaskSequence>	oTSIt = oTaskSequenceSet.iterator();
			while (oTSIt.hasNext())	{
				aTaskSequence = oTSIt.next().getLdTaskSequence();
				oLinkIt = aTaskSequence.getTaskSequence().iterator();
				// While there is  another Link in the Task sequence
				while (oLinkIt.hasNext())	{
					aLink = oLinkIt.next();
					//if it contains the of interest node return it...
					if (aLink.getFrom().getId().equals(fromNode.getId()) ||  
							aLink.getTo().getId().equals(toNode.getId()) ||
							aLink.getFrom().getId().equals(toNode.getId()) ||
							aLink.getTo().getId().equals(fromNode.getId()) )	
					{
						return aTaskSequence;
					}
				}				
			}
			return (new LdTaskSequence());
		}
	}
	**/
	/**
	 * Get the first task sequence containing the NodeSummary fromNode, or the 
	 * NodeSummary toNode, from the set of task sequences. An empty task sequence 
	 * is returned if no match can be found. In other words, if  neither fromNode
	 * or toNode  is in any of the links in any of the sequences in the set, 
	 * return an empty  task sequence.  Also return an empty task sequence if the
	 * Task sequence set is empty.
	 * 
	 * @param aLink
	 * @return
	 */
	public UILdTaskSequence getTaskSequenceContainingANodeInLink(UINode fromNode, UINode toNode)	{
		UILdTaskSequence aTaskSequence;
		UILink aLink;
		Iterator<UILdTaskLink>	oLinkIt;
		if (oTaskSequenceSet.isEmpty())	{
			return (new UILdTaskSequence());
		}
		else	{
			Iterator<UILdTaskSequence>	oTSIt = oTaskSequenceSet.iterator();
			while (oTSIt.hasNext())	{
				aTaskSequence = oTSIt.next();
				if (aTaskSequence.containsRoleNode(fromNode.getNode().getId()))	{
					return aTaskSequence;
				}
				oLinkIt = aTaskSequence.getTaskSequence().iterator();
				// While there is  another Link in the Task sequence
				while (oLinkIt.hasNext())	{
					aLink = oLinkIt.next();
					//if it contains the of interest node return it...
					if (aLink.getLink().getFrom().getId().equals(fromNode.getNode().getId()) ||  
							aLink.getLink().getTo().getId().equals(toNode.getNode().getId()) ||
							aLink.getLink().getFrom().getId().equals(toNode.getNode().getId()) ||
							aLink.getLink().getTo().getId().equals(fromNode.getNode().getId()) )	
					{
						return aTaskSequence;
					}
				}				
			}
			return (new UILdTaskSequence());
		}
	}
	
	/**
	 * Get the only task sequence containing the UINode oToOrFromNode from the set
	 *  of task sequences. An empty task sequence is returned if no match can be found.
	 *  In other words, if  oToOrFromNode is not in any of the links in any of the 
	 *  sequences in the set, return an empty  task sequence.  
	 *  Also return an empty task sequence if the Task sequence set is empty.
	 * 
	 * @param aLink
	 * @return
	 */
	public UILdTaskSequence getTaskSequenceContainingNode(UINode oToOrFromNode)	{
		UILdTaskSequence aTaskSequence;
		UILink aLink;
		Iterator<UILdTaskLink>	oLinkIt;
		if (oTaskSequenceSet.isEmpty())	{
			return (new UILdTaskSequence());
		}
		else	{
			Iterator<UILdTaskSequence>	oTSIt = oTaskSequenceSet.iterator();
			while (oTSIt.hasNext())	{
				aTaskSequence = oTSIt.next();
				oLinkIt = aTaskSequence.getTaskSequence().iterator();
				// While there is  another Link in the Task sequence
				while (oLinkIt.hasNext())	{
					aLink = oLinkIt.next();
					//if it contains the node of interest node return it...
					if (aLink.getLink().getFrom().getId().equals(oToOrFromNode.getNode().getId()) ||  
							aLink.getLink().getTo().getId().equals(oToOrFromNode.getNode().getId())  )	
					{
						return aTaskSequence;
					}
				}				
			}
			return (new UILdTaskSequence());
		}
	}
	/**
	 * Set the set of task sequences for this instance of UILdViewPane.
	 * @param taskSequenceSet the oTaskSequenceSet to set
	 */
	public void setTaskSequenceSet(LinkedHashSet<UILdTaskSequence> taskSequenceSet) {
		oTaskSequenceSet = taskSequenceSet;
	}

	/**
	 * Create and display an instance of the right-click popup menu for this view.
	 * @param com.compendium.ui.plaf.ViewPaneUI, the ui object for this view required as a parameter for the popup.
	 * @param x, the x position of the trigger event for this request. Used to calculate the popup x position.
	 * @param y, the y position of the trigger event for this request. Used to calculate the popup y position.
	 */
	public void showPopupMenu(ViewPaneUI viewpaneui, int x, int y) {

		viewPopup = new UILdViewPopupMenu("View Popup menu", viewpaneui);
		UIViewFrame viewFrame = oViewFrame;

		Dimension dim = ProjectCompendium.APP.getScreenSize();
		int screenWidth = dim.width - 70; //to accomodate for the scrollbar
		int screenHeight = dim.height-120; //to accomodate for the menubar...

		Point point = getViewFrame().getViewPosition();
		int realX = Math.abs(point.x - x)+20;
		int realY = Math.abs(point.y - y)+20;

		int endXCoordForPopUpMenu = realX + viewPopup.getWidth();
		int endYCoordForPopUpMenu = realY + viewPopup.getHeight();

		int offsetX = (screenWidth) - endXCoordForPopUpMenu;
		int offsetY = (screenHeight) - endYCoordForPopUpMenu;

		if(offsetX > 0)
			offsetX = 0;
		if(offsetY > 0)
			offsetY = 0;

		viewPopup.setCoordinates(realX+offsetX, realY+offsetY);
		// The viewPane for the pop up is now set in the constructor
//		viewPopup.setViewPane(this);
		viewPopup.show(viewFrame,realX+offsetX, realY+offsetY);
	}

	/**
	 * Return the boolean which indicates if timing info should be shown for 
	 * this activity
	 * @return the bShowTimingInfo
	 */
	public boolean getShowTimingInfo() {
		return bShowTimingInfo;
	}

	/**
	 * Set the boolean which indicates if timing info should be shown for 
	 * this activity
	 * @param showTimingInfo the bShowTimingInfo to set
	 */
	public void setShowTimingInfo(boolean showTimingInfo) {
		boolean bOldShowTiningInfo = bShowTimingInfo;
		bShowTimingInfo = showTimingInfo;
		this.updateShowActivityTimes(bShowTimingInfo);
	}
}
