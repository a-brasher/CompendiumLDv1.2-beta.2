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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JOptionPane;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.HashSet;
import java.util.NoSuchElementException;

import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.Link;
import com.compendium.core.datamodel.View;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.learningdesign.core.ILdCoreConstants;
import com.compendium.learningdesign.core.datamodel.*;
import com.compendium.ui.UILink;
import com.compendium.ui.UINode;

/**
 * The purpose of the UILdTaskSequence class is to store information about sequences
 * of learning design tasks. It does this by maintaining a list of the links. 
 * The links stored may be of two types: they are either links between task nodes, 
 * or links between a role node and the first task node in the sequence. 
 *   
 * @author ajb785
 *
 */
public class UILdTaskSequence implements PropertyChangeListener {
	
	/** A reference to the link added property for PropertyChangeEvents.*/
    public static final String LINK_ADDED 		= "linkAdded";
    
    /** A reference to the link added property for PropertyChangeEvents.*/
    public static final String TASK_NODE_ADDED 		= "taskNodeAdded";
    
    /** A reference to the link added property for PropertyChangeEvents.*/
    public static final String TASK_NODE_DELETED 		= "taskNodeDeleted";
    
    /** A reference to the link added property for PropertyChangeEvents.*/
    public static final String TIME_CHANGED 		= "timeChanged";
    
    /** A reference to the link added property for PropertyChangeEvents.*/
    public static final String SEQUENCE_CREATED 		= "sequenceCreated";
    
    /** A reference to the role added property for PropertyChangeEvents.*/
    public static final String ROLE_ADDED 		= "roleAdded";
    
    /** A reference to the role added property for PropertyChangeEvents.*/
    public static final String ROLE_DELETED 		= "roleDeleted";
    
    /** A reference to the property indicating that all links have been deleted i.e. that this UILdTaskSequence is now empty for PropertyChangeEvents.*/
    public static final String ALL_LINKS_DELETED 		= "allLinksDeleted";
    
    /** A reference to the role label changed property for PropertyChangeEvents.*/
    public static final String ROLE_LABEL_CHANGED 		= "roleLabelChanged";
    
    /** Default role label if a task sequence has no role node */
    public static final String DEFAULT_ROLE_LABEL 		= "Role";
        
    /** The LdTaskSequence that this instance of UILdTaskSequence holds the UI data and events for. **/
    private LdTaskSequence	oTaskSequence = null;
    
	/** This indicates the role that this task sequence is carried out by, e.g.
	 *  learner or tutor. These should be a value drawn from the values given for roles in the
	 *  enumeration interface ILdCoreConstants.	**/
	private int iRoleType = ILdCoreConstants.iLD_TYPE_NO_TYPE;
	
	/** The label of the role NodeSummary object	**/
	private String sRoleName	= "";
	
	/** The unique id of the role NodeSummary object. **/
	private String sRoleId	=	"";
	
	/**	Representation of the sequence of tasks for the role, by a list of 
	 * UILdTaskLinks between the tasks. Note: if it exist, the (first) link between 
	 * a the role node 'performing' the tasks and the first task is stored in the 
	 * instance variable oRoleLink.  
	 ***/
	private LinkedList<UILdTaskLink> oUITaskSequence = null;
	
	/**	Representation of the sequence of tasks for the role, by a list of the 
	 * unique ids of all the Links in this task sequence. These Link instances
	 * may be represented in the UI by either UILdTaskLink instances, or UILink
	 * instances depending on whether they are links between tasks or the link
	 * between the role and first task.*/
	private LinkedList<String> oLinkIds = null;
	
	/** The UILink containing the role node for this task sequence.		**/
	private UILink oRoleUILink = null;
	
	/** The time in minutes that this task sequence should take. 	**/
	private long  oTaskSequenceTime;
	
	
	/**
	 * Holds a list of registered property change listeners.
	 * @uml.property  name="listenerList"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.beans.PropertyChangeListener"
	 */
	private Vector listenerList = new Vector();
	
	/**	Enumeration of role types, including iLD_TYPE_NO_TYPE for "no role".
	 *   Note that iLD_TYPE_ROLE_GROUP is not currently include because it is
	 *    no longer used. 		**/
	
	/**
	 * 	
	 * Constructor. Creates a UILdTaskSequence object which has  
	 * an empty LinkedList for the task sequence oTaskSequence. 
	 * 	 
	 */
	public UILdTaskSequence() {
		super();
		sRoleName = UILdTaskSequence.DEFAULT_ROLE_LABEL;
		oUITaskSequence = new LinkedList<UILdTaskLink>();
		oLinkIds = new LinkedList<String>();
		oTaskSequence = new LdTaskSequence();
	}
	
	/**
	 * Constructor. Creates a UILdTaskSequence object containing the link aLink 
	 * if aLink is a task link in the task sequence oTaskTSequrence, otherwise 
	 * an empty LinkedList for the task sequence oTaskSequence.	 
	 * 
	 */
	public UILdTaskSequence(UILink aLink) {
		this();
		// Check that link is suitable to be added before adding it
		if (UILdTaskSequence.isRoletoTaskLink(aLink))	{
			this.addFirst(aLink);	
			/** Add the UILdViewPane as a property change listener **/
			this.addPropertyChangeListener(aLink.getViewPane());
	//		this.addPropertyChangeListener(aLink.getViewPane().getAc);
			this.firePropertyChange(UILdTaskSequence.SEQUENCE_CREATED, "", this);			
		}
		else if(UILdTaskSequence.isTasktoTaskLink(aLink))	{
			this.addFirst(aLink);	
			this.addPropertyChangeListener(aLink.getViewPane());
			this.firePropertyChange(UILdTaskSequence.SEQUENCE_CREATED, "", this);
		}
		
	}
	
	/**
	 * Constructor. Creates a UILdTaskSequence object containing the LinkedList of 
	 * links oUITaskSequence.	 
	 * 
	 */
	public UILdTaskSequence(LinkedList<UILdTaskLink> oUITaskSequence) {
		this();		
		if (!oUITaskSequence.isEmpty())	{
		LinkedList<String> aLinkIds = settLinkIdsUsing(oUITaskSequence);
		this.setLinkIds(aLinkIds);
		/** Add the UILdViewPane as a property change listener for this sequence**/
		this.addPropertyChangeListener(oUITaskSequence.getFirst().getViewPane());
		/** Need to setTaskSequence(oUITaskSequence) after the sequence created property change is sent
		 * because it updates the times and sends a property change message about the time.
		 */ 
		this.setTaskSequence(oUITaskSequence);	
		this.addListenersForTasks(oUITaskSequence);
		this.firePropertyChange(UILdTaskSequence.SEQUENCE_CREATED, "", this);
		/** Note: should synchronize and wait for this task sequence to be 
		 * added to the UILdActivityTimesFrame before setting the time. Might 
		 * need to move the setting of the time to propertyChangeEvent method in
		 * class UILdActivityTimesFrame.
		 */
//		long oNewTime = this.updateTimes();
//		this.setTaskSequenceTime(oNewTime);
		}
		else	{
			String sError = oUITaskSequence.toString();
			JOptionPane.showMessageDialog(ProjectCompendium.APP, sError, "Error: method UILdTaskSequence(LinkedList<UILdTaskLink> oUITaskSequence)", JOptionPane.ERROR_MESSAGE);;
		}
	}
	
	/**
	 * Constructor. Creates a UILdTaskSequence object containing the LinkedList of 
	 * links oUITaskSequence.	
	 * 
	 *  NOT FINISHED
	 * @param aLdTaskSequence
	 * @param aView
	 */
	public UILdTaskSequence(LdTaskSequence aLdTaskSequence, UILdViewPane oViewPane) {
		super();
		oUITaskSequence = new LinkedList<UILdTaskLink>();
		oTaskSequence = aLdTaskSequence;
		oLinkIds = new LinkedList<String>();
		LinkedList<LdTask> oTaskList = aLdTaskSequence.getTaskSequence();
		String sRoleId = aLdTaskSequence.getRoleId();
		String sTaskId = "";
		UILdNode oRoleNode = null;
		UILink oUILink = null;
		UILdTaskLink oUITaskLink = null;
		UILdTaskNode oUITaskNode = null;
		UILdTaskNode oNextUITaskNode = null;
		LinkedList<UILdTaskLink> oUITaskList = new LinkedList<UILdTaskLink>();

		/** First find and add the role link	**/
		// If there is a role id in the LdTaskSequence and the task list is not empty
		if (sRoleId.length() > 0 && !(oTaskList.isEmpty()))	{
			// Get the first task node
			sTaskId = oTaskList.getFirst().getId();
			oUITaskNode = (UILdTaskNode)oViewPane.get(sTaskId);
			// Get the role node
			oRoleNode = (UILdNode) oViewPane.get(sRoleId);
			// Get the role UILink and add it to this task sequence
			oUILink = oRoleNode.getLink(oUITaskNode);
			if (oUILink != null) {
				this.addFirst(oUILink);	
				/** Add the UILdViewPane as a property change listener **/
				this.addPropertyChangeListener(oUILink.getViewPane());				
			}
			else	{
				System.out.println("UILdTaskSequence() Role link problem???: sRoldeId = " + sRoleId + "sTaskNodeIId = " + oUITaskNode.getNode().getId()+ "\n");
			}
		}

		/** Now add the task links	**/
		/******
 Iterator<LdTask> oIt = oTaskList.iterator();

		String sNextTaskId;
		LdTask oCurrentTask;
		while (oIt.hasNext())	{
			oCurrentTask = oIt.next();
			sTaskId = oCurrentTask.getNodeSummaryTaskId();
			if (oIt.hasNext())	{
				sNextTaskId = oIt.next().getNodeSummaryTaskId();
				oUITaskNode = (UILdTaskNode)oViewPane.get(sTaskId);
				oNextUITaskNode = (UILdTaskNode)oViewPane.get(sNextTaskId);
				oUITaskLink = oUITaskNode.getLinkTo(oNextUITaskNode);
				if (oUITaskLink != null)
					this.add(oUITaskLink);
					//oUITaskList.add(oUITaskLink);
			}
		}
		 ********/		
		/**	New: Now add the task links **/		
		UILdTaskNode oPrevUITaskNode = null;
		UILdTaskNode oCurrentUITaskNode = null;
		int nTasks = oTaskList.size();
		int iTaskNo = 0;
		for (LdTask oThisTask: oTaskList) {		 
			sTaskId = oThisTask.getNodeSummaryTaskId();
			if (iTaskNo == 0)	
				oPrevUITaskNode = (UILdTaskNode)oViewPane.get(sTaskId);
			else	{
				oCurrentUITaskNode = (UILdTaskNode)oViewPane.get(sTaskId);
				oUITaskLink = oPrevUITaskNode.getLinkTo(oCurrentUITaskNode);
				oPrevUITaskNode = oCurrentUITaskNode;
				if (oUITaskLink != null)	{
					if (iTaskNo == 1 && this.getRoleLink() == null)	// The first Task link - call add first method to add time from both task nodes linked
						this.addFirst(oUITaskLink);
					else	
						this.add(oUITaskLink);				
				}
			}
			iTaskNo++;
		}

	}
	
	/**
	 * Static method to determine if an instance of class Link is a link between
	 * two Ld task nodes. 
	 * @param aLink
	 * @return true, if the links' from node is a a task node AND the 
	 * links' to node is a task node, false otherwise.
	 */
	public static boolean isTasktoTaskLink(UILink aLink)	{
		if ( (aLink.getLink().getFrom().getLdType() == ILdCoreConstants.iLD_TYPE_TASK) &&
				(aLink.getLink().getTo().getLdType() == ILdCoreConstants.iLD_TYPE_TASK) )
		{
			return true;
		}
		else	{
			return false;
		}
	}
	
	/**
	 * Static method to determine if an instance of class Link is a link from
	 * an Ld role node to an Ld  task node. 
	 * @param aLink
	 * @return true, if the links' from node is a a Role node AND the 
	 * links' to node is a task node, false otherwise.
	 */
	public static boolean isRoletoTaskLink(UILink aLink)	{
		if ( (aLink.getLink().getFrom().getLdType() == ILdCoreConstants.iLD_TYPE_ROLE) &&
				(aLink.getLink().getTo().getLdType() == ILdCoreConstants.iLD_TYPE_TASK) )
		{
			return true;
		}
		else	{
			return false;
		}
	}
	
	/**
	 * Static method to determine if an instance of class UILink is suitable to be added
	 * to a UILdTaskSequence. 
	 * @param aLink
	 * @return true, if the links' from node is a role or a task node AND the 
	 * links' to node is a task node, false otherwise.
	 */
	public static boolean isSuitableLink(UILink aLink)	{
		if (UILdTaskSequence.isRoletoTaskLink(aLink) || UILdTaskSequence.isTasktoTaskLink(aLink))
			return true;
		else
			return false;
	}
	
	/**
	 * Static method to determine if an instance of class Link is suitable
	 * for adding to a UILdTaskSequence, or to determine if a TaskSequence 
	 * should be created. If the Link is suitable, its corresponding UILink
	 * instance can be added to the UILdTaskSequence. 
	 * @param aLink
	 * @return true, if the links' from node is a role or a task node AND the 
	 * links' to node is a task node, false otherwise.
	 */
	public static boolean isSuitableLink(Link aLink)	{
		if (aLink.getTo().getLdType() != ILdCoreConstants.iLD_TYPE_TASK)	{
			// The link is not linking to a Ld task node so do nothing
			return false;
		}
		else	{
			if ((aLink.getFrom().getLdType() == ILdCoreConstants.iLD_TYPE_ROLE) ||
					(aLink.getFrom().getLdType() == ILdCoreConstants.iLD_TYPE_TASK) )	{
				return true;
			}
			else	{
				return false;
			}
			
		}
	}
	
	public static UILdTaskSequence createTaskSequence(UILink aLink)	{
		if (UILdTaskSequence.isSuitableLink(aLink))	{
			return (new UILdTaskSequence(aLink));
		}
		else	{
			return (new UILdTaskSequence());
		}
	}
	
	/**
	private void initialise(Link aLink)	{
		if (aLink.getTo().getLdType() != ILdCoreConstants.iLD_TYPE_TASK)	{
			// The link is not linking to a Ld task node so do nothing
		}
		else	{
			if ((aLink.getFrom().getLdType() == ILdCoreConstants.iLD_TYPE_ROLE) ||
					(aLink.getFrom().getLdType() == ILdCoreConstants.iLD_TYPE_TASK) )	{
				oTaskSequence.add(aLink);
			}
			
		}
	}
	**/
	/**
	 * This method returns true if the role link is empty and the list of task links oTaskSequence is empty
	 * , false otherwise.
	 * 
	 * @return true if the list of task links oUITaskSequence is empty, false
	 * otherwise.
	 */
	public boolean isEmpty()	{
		if (this.getRoleLink() == null )
			return (oUITaskSequence.isEmpty());
		else
			return false;
	}
	
	/**
	 * This method adds the Link aLink to the task sequence oTaskSequence,
	 * if the link is suitable to be added. A link is suitable if it is a link 
	 * between two task nodes, or a link from a role node to a task node. This 
	 * method fires property change events to indicate that a task node, or a 
	 * role node, have been added, and updates the task sequence time if a 
	 * task node is added.
	 * 
	 * @param aLink
	 * @return true if the link is added, false otherwise
	 */
	public boolean add(UILink aLink)	{
		/** Make this sequence listen for changes in the View that aLink is 
		 * part of. This enables this sequence instance to detect when the 
		 * link is deleted from the view.
		 */
		aLink.getViewPane().getView().addPropertyChangeListener(this);
		if (UILdTaskSequence.isTasktoTaskLink(aLink))	{
			this.getTaskSequence().add((UILdTaskLink)aLink);
			// Make this task sequence  listen  to property changes from the 'to' and 'from' nodes, i.e  UILdTaskNodes
			/** Need to make sure that only one copy of tasksequence listens to any UILdNode
			 * This is done in the addPropertyChangeListener(PropertyChangeListener listener)method.
			 */ 
			
			aLink.getToNode().addPropertyChangeListener(this);
			aLink.getFromNode().addPropertyChangeListener(this);
			
			setTaskSequenceTime(oTaskSequenceTime + ((UILdTaskNode)aLink.getToNode()).getTaskTime());
			// Make the UIiLdViewPane containing the link listen to changes in this task sequence
			//this.addPropertyChangeListener(aLink.getViewPane());
			//Change the property name???
			this.firePropertyChange(UILdTaskSequence.TASK_NODE_ADDED, aLink.getLink().getTo().getId());
			this.oLinkIds.add(aLink.getLink().getId());
			return true;
		}
		else if (UILdTaskSequence.isRoletoTaskLink(aLink))	{
			// This else clause shouldn't be reached for  'normal' task sequence  construction, i.e. with one role at the beginning
			if (!this.contains((UILdTaskNode)aLink.getToNode()))	{
				// If the task sequence does not already contain the 'to' Task node then update the sequence time
				setTaskSequenceTime(oTaskSequenceTime + ((UILdTaskNode)aLink.getToNode()).getTaskTime());
			}
			// Add the link to the task sequence
			this.setRoleLink(aLink);
			this.setRoleId(aLink.getFromNode().getNode().getId());
	//		this.getTaskSequence().add(aLink);
			// Make this task sequence  listen  to property changes from the 'to' node, i.e a UILdTaskNode 
			aLink.getToNode().addPropertyChangeListener(this);
			
			
			/** Make this task sequence listen to changes to the UINode
			 * 	object that is the 'from' node i.e. the role node in this link.
			 */ 
			aLink.getFromNode().addPropertyChangeListener(this);
			// Have added a role node therefore fire property change
			this.firePropertyChange(UILdTaskSequence.ROLE_ADDED, aLink.getFromNode().getText());
			this.firePropertyChange(UILdTaskSequence.LINK_ADDED, aLink.getLink().getId());
			this.oLinkIds.add(aLink.getLink().getId());
			return true;
		}
		else	{
			return false;
		}
	}
	
	
	
	/**
	 * Add this link to the beginning of the sequence, make this sequence listen 
	 * to property change events from the taskNode, then fire the appropriate
	 * propertyChange notification. 
	 * @param aLink
	 */
	public void addFirst(UILink aLink)	{
		/** Make this sequence listen for changes in the View that aLink is 
		 * part of. This enables this sequence instance to detect when the 
		 * link is deleted from the view.
		 */
		aLink.getViewPane().getView().addPropertyChangeListener(this);
		// If the task sequence does not already contain the 'to' task node, update the time
		if (!this.contains((UILdTaskNode)aLink.getToNode()))	{
			this.setTaskSequenceTime(this.getTaskSequenceTime() + ((UILdTaskNode)aLink.getToNode()).getTaskTime() );
		}
		
 		
		if (aLink.getFromNode().getLdType() == ILdCoreConstants.iLD_TYPE_ROLE )	{
			//Set the role name of this instance to be the label of the 'from'; node i.e. the role node
			this.setRoleName(aLink.getFromNode().getText());
			iRoleType = ((UILdNode)(aLink.getFromNode())).getLdSubType();
			sRoleId = aLink.getFromNode().getNode().getId();
			/** Make this task sequence listen to changes to the NodeSummary
			 * 	object that is the 'from' node i.e. the role node in this link.
			 No longer necessary - changes to the UINode are listened to */ 
			//aLink.getFromNode().getNode().addPropertyChangeListener(this);
			/** Make this task sequence listen to changes to the UINode instance
			 * 	object that is the 'from' node i.e. the role node in this link.
			 */ 
			aLink.getFromNode().addPropertyChangeListener(this);
			aLink.getToNode().addPropertyChangeListener(this);
			// Make this task sequence  listen  to property changes from the 'from' node, i.e a UILdNode that is a 'role'
			// Do not need to do this because the sequence is listening for changes to the NodeSummary to pick up changes to the label.
			//aLink.getFromNode().addPropertyChangeListener(this);
			//this.getTaskSequence().addFirst(aLink);
			this.setRoleLink(aLink);
			this.firePropertyChange(UILdTaskSequence.ROLE_ADDED, aLink.getFromNode().getText());
		}	
		else if (aLink.getFromNode().getLdType() == ILdCoreConstants.iLD_TYPE_TASK)	{
			// If the from node isn't currently in the tasksequence, add its time (this will only be the case for a first task-to task link
			if (!this.contains((UILdTaskNode)aLink.getFromNode()))	{
				this.setTaskSequenceTime(this.getTaskSequenceTime() + ((UILdTaskNode)aLink.getFromNode()).getTaskTime() );
			}	
			// Make this task sequence  listen  to property changes from the 'to' node, i.e a UILdTaskNode 
			aLink.getToNode().addPropertyChangeListener(this);
			aLink.getFromNode().addPropertyChangeListener(this);
			this.getTaskSequence().addFirst((UILdTaskLink)aLink);
			this.firePropertyChange(UILdTaskSequence.TASK_NODE_ADDED, ((UILdTaskNode)aLink.getFromNode()));
		}
	this.oLinkIds.addFirst(aLink.getLink().getId());
	}
	
	/**
	 * Add the links in the task sequence aTaskSequence to this task sequence, 
	 * and update the time duration to equal the sum of this sequence time
	 * plus the time for aTaskSequence.
	 * @param aTaskSequence
	 * @return true if the task sequence for this instance is changed, false otherwise.
	 */
	public boolean addUILdTaskSequence(UILdTaskSequence aTaskSequence){
		boolean bReturnValue =  this.getTaskSequence().addAll(aTaskSequence.getTaskSequence());
		long currentTime = this.getTaskSequenceTime();
		long timeToBeAdded = aTaskSequence.getTaskSequenceTime(); 
		this.setTaskSequenceTime(currentTime + timeToBeAdded);
		return bReturnValue;
	}
	/**
	 * This method returns true if the list of links that this task sequence 
	 * represents contains the link aLink, false otherwise.	  
	 * @param aLink
	 * @return true,  if the list of task links this task sequence represents contains the link aLink, 
	 * false otherwise.
	 */
	public boolean contains(UILink aLink)	{
		return (this.contains(aLink.getLink()));
	}
	
	/**
	 * This method returns true if the list of UILink  links that this oTaskSequence 
	 * maintains contain a UILink instance which represents the data model link aLink,
	 * false otherwise.	  
	 * @param aLink
	 * @return true,  if the list of task links this task sequence represents contains the link aLink, 
	 * false otherwise.
	 */
	public boolean contains(Link aLink)	{
		// If aLink is the role link, return true
		if (this.getRoleLink() != null)	{
			if (this.getRoleLink().getLink().getId().equals(aLink.getId()))	{
				return true;
			}
		}
		// Otherwise, if aLink is in the task sequence return true
		if (oUITaskSequence != null)	{
			Iterator<UILdTaskLink> oIt = oUITaskSequence.iterator();
			while (oIt.hasNext())	{
				if (oIt.next().getLink().getId().equals(aLink.getId()))
					return true;
			}		
		}
		return false;
	}
	
	/**
	 * Returns the index of the first UILink which corresponds to the specified Link 
	 * aLink in this list, or -1 if this list does not contain a corresponding UILink.
 
	 * @param aLink
	 * @return the index of the first UILink which corresponds to the specified Link 
	 * aLink in this list, or -1 if this list does not contain a corresponding UILink.
	 */
	public int indexOf(Link aLink)	{
		Iterator<UILdTaskLink> oIt = oUITaskSequence.iterator();
		int n = 0;
		while (oIt.hasNext())	{
			if (oIt.next().getLink().getId().equals(aLink.getId()))
					return n;
			n++;
		}		
		return -1;
	}
	
	/**
	 * Returns the UIlink corresponding to the Link aLink from the oUITaskSequence
	 *  task sequence, or a null UILink if this task sequence does not contain
	 * a UILink corresponding to aLink. 
	 * @param aLink
	 * @return - the UIlink corresponding to the Link aLink from this task 
	 * sequence, or a null UILink if this task sequence does not contain
	 * a UILink corresponding to aLink.
	 */
	public UILink getFromUITaskSequence(Link aLink){
		Iterator<UILdTaskLink> oIt = oUITaskSequence.iterator();
		UILink oUILink = null;
		while (oIt.hasNext())	{
			oUILink = oIt.next();
			if (oUILink.getLink().getId().equals(aLink.getId()))
					return oUILink;
		}	
		return oUILink;
	}
	
	/**
	 * Returns the UIlink corresponding to the Link aLink from this task 
	 * sequence, or a null UILink if this task sequence does not contain
	 * a UILink corresponding to aLink. 
	 * @param aLink
	 * @return - the UIlink corresponding to the Link aLink from this task 
	 * sequence, or a null UILink if this task sequence does not contain
	 * a UILink corresponding to aLink.
	 */
	public UILink get(Link aLink){
		if (this.isRoleLink(aLink))
			return this.getRoleLink();
		else	{
		UILink oUILink = this.getFromUITaskSequence(aLink);;
		return oUILink;
		}
	}
	/**
	 * This method returns true if the list of task links oTaskSequence contains
	 * the NodeSummary aNode, false otherwise.	  
	 * @param aLink
	 * @return true if the list of task links oTaskSequence contains, the link aLink, 
	 * false otherwise.
	 */
	public boolean contains(NodeSummary aNode)	{		
		if (this.roleLinkContains(aNode))	{
			return true;
		}
		LinkedList<UILdTaskLink> oTS = this.getTaskSequence();
		Iterator<UILdTaskLink> it = oTS.iterator();
		UILdTaskLink oLink;
		String sNodeId = aNode.getId(); 
		
		while(it.hasNext())	{
			oLink = it.next();
			if (oLink.getFromNode().getNode().getId().equals(sNodeId) ||
					oLink.getToNode().getNode().getId().equals(sNodeId))	{
				return true;
			}
		}
		return (false);
	}
	
	/**
	 * This method returns true if the roleLink for this oTaskSequence contains
	 * the UILdNode aNode, false otherwise.	  
	 * @param UILdNode aNode
	 * @return true if the roleLink oRoleLink for this oTaskSequence contains the UILdNode aNode 
	 * false otherwise.
	 */
	public boolean roleLinkContains(UILdNode aNode)	{
		Link oRoleLink = this.getRoleLink().getLink();
		String sNodeId = aNode.getNode().getId();
		if (oRoleLink.getFrom().getId().equals(sNodeId) || 
				oRoleLink.getTo().getId().equals(sNodeId) )
			return true;
		else
			return false;
	}
	
	/**
	 * This method returns true if the roleLink for this oTaskSequence contains
	 * the NodeSummary aNode, false otherwise.	  
	 * @param NodeSummary aNode, the NodeSummary instance which is represented by a UILdNode in 
	 * this instances roleLink.
	 * @return true if the roleLink oRoleLink for this oTaskSequence contains the UILdNode aNode 
	 * false otherwise.
	 */
	public boolean roleLinkContains(NodeSummary aNode)	{
		if (this.getRoleLink() == null)	{
			return false;
		}
		Link oRoleLink = this.getRoleLink().getLink();
		String sNodeId = aNode.getId();
		if (oRoleLink.getFrom().getId().equals(sNodeId) || 
				oRoleLink.getTo().getId().equals(sNodeId) )
			return true;
		else
			return false;
	}
	
	/**
	 * This method returns true if the list of task links oTaskSequence contains
	 * the UILdNode aNode, false otherwise.	  
	 * @param UILdNode aNode
	 * @return true if the list of task links oTaskSequence contains, the UILdNode aNode 
	 * false otherwise.
	 */
	public boolean contains(UILdNode aNode)	{
		NodeSummary oNode = aNode.getNode();
		return (this.contains(oNode));
		/**
		String sNodeId = aNode.getNode().getId(); 
		// Check if aNode is the role node.
		if (this.getRoleId().equals(sNodeId))
			return true;
		// If it's not the role node, it might be a task node
		LinkedList<UILdTaskLink> oTS = this.getTaskSequence();
		Iterator<UILdTaskLink> it = oTS.iterator();
		UILink oLink;
		
		while(it.hasNext())	{
			oLink = it.next();
			if (oLink.getFromNode().getNode().getId().equals(sNodeId) ||
					oLink.getToNode().getNode().getId().equals(sNodeId))	{
				return true;
			}
		}
		return (false);
		***/
	}
	
	/**
	 * This method returns the number of tasks in the sequence.
	 * @return number of tasks in the sequence as an int
	 */
	public int taskLength()	{
		/** Number of nodes in the sequence is equal to the number of links + 1
		 * E.g. there is 1 link between 2 nodes.
		 */
		if (this.getTaskSequence() == null)
			return 0;
		int n = this.getTaskSequence().size() + 1;
		return n;
	}
	
	
	/**
	 * This method returns the total number of role and task nodes in the sequence.
	 * @return number of total number of role and task nodes in the sequence as an int
	 */
	public int size()	{
		/** Number of nodes in the sequence is equal to the number of links + 1
		 * E.g. there is 1 link between 2 nodes.
		 */
		int n = this.taskLength();
		if (this.getRoleLink() != null)
			return n+1;
		else
			return n;
	}
	/**
	 * This method returns true if the list of task links oTaskSequence contains
	 * the UILdTaskNode aTaskNode, false otherwise.	  
	 * @param aLink
	 * @return true if the list of task links oTaskSequence contains, the link aLink, 
	 * false otherwise.
	 */
	public boolean contains(UILdTaskNode aTaskNode)	{
		String sTaskNodeId = aTaskNode.getNode().getId();
		Iterator<UILdTaskLink> oIt = this.getTaskSequence().iterator();
		UILink aUILink;
		while(oIt.hasNext())	{
			aUILink = oIt.next();
			if (aUILink.getToNode().getNode().getId().equals(sTaskNodeId) ||
					aUILink.getFromNode().getNode().getId().equals(sTaskNodeId)
			)	{
				return true;
			}
			
		}
		return false;
	}
	
	/**
	 * Get the task time for the Task node specified by the node id sTaskNodeId.
	 * If the node is not in the sequence, this method will return 0.
	 * @param sTaskNodeId
	 * @return
	 */
	public long getTimeForTaskNode(String sTaskNodeId)	{
		long oTime = 0;
		Iterator<UILdTaskLink> oIt = this.getTaskSequence().iterator();
		UILink aUILink;
		while(oIt.hasNext())	{
			aUILink = oIt.next();			
			if (aUILink.getToNode().getNode().getId().equals(sTaskNodeId))	{
				return  ((UILdTaskNode)(aUILink.getToNode())).getTaskTime();
				}
			else if (aUILink.getFromNode().getNode().getId().equals(sTaskNodeId)) {
				return  ((UILdTaskNode)(aUILink.getFromNode())).getTaskTime();
			}
		}
		return oTime;
	}
	/**
	 * 
	 */
	public void addRoleInfoToSequence(UILdTaskNode aTaskNode)	{
		NodeSummary oParentNode = aTaskNode.getParentNode();
		if( oParentNode.isLdRoleNode())	{
			setRoleType(oParentNode.getLdSubType());
			setRoleId(oParentNode.getId());
			setRoleName(oParentNode.getLabel());
		}
	}
	
	
	/**
	 * Return the role type. This should be one of these values: 
	 * ILdCoreConstants.iLD_TYPE_ROLE_STUDENT, ILdCoreConstants.iLD_TYPE_ROLE_TUTOR, 
	 * ILdCoreConstants.iLD_TYPE_ROLE_OTHER
	 * @return the iRoleType
	 */
	public int getRoleType() {
		return iRoleType;
	}

	/**
	 * @param roleType the iRoleType to set
	 */
	public void setRoleType(int roleType) {
		iRoleType = roleType;
	}

	/**
	 * @return the sRoleName
	 */
	public String getRoleName() {		
		return sRoleName;
	}

	/**
	 * @param roleName the sRoleName to set
	 */
	public void setRoleName(String roleName) {
		sRoleName = roleName;
	}

	/**
	 * @return the sRoleId
	 */
	public String getRoleId() {
		return sRoleId;
	}

	/**
	 * @param roleId the sRoleId to set
	 */
	public void setRoleId(String roleId) {
		sRoleId = roleId;
	}

	/**
	 * @return the oUITaskSequence, i.e. the Linked list of UILdTaskLinks
	 */
	public LinkedList<UILdTaskLink> getTaskSequence() {
		return oUITaskSequence;
	}

	/**
	 * Set the task sequence linked list to be the list oTaskSequence
	 * @param taskSequence the oTaskSequence to set
	 */
	public void setTaskSequence(LinkedList<UILdTaskLink> oTaskSequence) {
		oUITaskSequence = oTaskSequence;
	}
	
	/**
	 * Convenience method to generate the linked list of ids of Links for this 
	 * task sequence from the supplied LinkedList of task links.
	 * 
	 * @param taskSequence the LinkedList 
	 */
	private LinkedList<String>  settLinkIdsUsing(LinkedList<UILdTaskLink> oTaskSequence) {
		Iterator<UILdTaskLink> oIt;
		oIt = oTaskSequence.iterator();
		
		LinkedList<String> oNewLinkIds = new LinkedList<String>();
		
		while (oIt.hasNext())	{
			oNewLinkIds.add(oIt.next().getLink().getId());
		}
		
	return oNewLinkIds;
	}
	
	/**
	 * Remove the node  from this task sequence if it is part of the sequence.
	 * Returns true if the node is removed, false otherwise. 
	 * @param sNodeId
	 * @return
	 */
	public boolean removeNode(UILdNode oNode)	{
		boolean bReturnValue = false;
		int ldType = oNode.getLdType();
		
		String sNodeId = oNode.getNode().getId();
		// oTS is a copy of the original task sequence
		LinkedList<UILink> oTS = new LinkedList<UILink>(this.getTaskSequence());
		LinkedList<UILink> oLinksToRemove = new LinkedList<UILink>();
		Iterator<UILink> it = oTS.iterator();
		UILink oLink;
		
		
		/** Note following loop only needs to loop until the first 'from' node is detected.
		 * Leave as it is for now, but, put in a condition to break out of loop once rest of
		 * code is working.
		 */
		if (this.roleLinkContains(oNode))	{
			this.clearRoleData(this.getRoleId());
		}
		while(it.hasNext() && oLinksToRemove.isEmpty())	{
			oLink = it.next();
			if (oLink.getFromNode().getNode().getId().equals(sNodeId) ||
					oLink.getToNode().getNode().getId().equals(sNodeId))	{
				oLinksToRemove.add(oLink);
			}
		}
		
		if (oLinksToRemove.size()> 0)	{
			//Get the first link in the list of those to be removed
			UILink firsLinkToRemove = oLinksToRemove.getFirst();
			// Find the index in the task sequence of the first link to be removed
			int nFirst = oTS.indexOf(firsLinkToRemove);
			// Find the index in the task sequence of the last link to be removed
			int nLast = oTS.indexOf(oTS.getLast());
			
			/** Now add to the list of links to be removed every link after this first link.
			 *  Note that if the sequence is split be removing a link, only want the head part,
			 *  not the tail part.
			 */ 
			if (nFirst != nLast) //Note that if nFirst = nLast there's only one link to remove
				{
				//subList method includes element at 'from' index but excludes element at 'to' index
				oLinksToRemove = new LinkedList<UILink> (oTS.subList(nFirst, nLast));
				oLinksToRemove.addLast(oTS.get(nLast))	;
				}
			// Remove the links from the task sequence, and set the return value to indicate success or failure
			bReturnValue = (this.getTaskSequence().removeAll(oLinksToRemove));
			if (ldType == ILdCoreConstants.iLD_TYPE_ROLE)	{
				this.firePropertyChange(UILdTaskSequence.ROLE_DELETED, oTS, this.getTaskSequence());
				// If all the links in the sequence are being deleted fire a property change to indicate this
				if (oLinksToRemove.containsAll(oTS))
					this.firePropertyChange(UILdTaskSequence.ALL_LINKS_DELETED, oTS, this.getTaskSequence());
				return (clearRoleData(sNodeId));
			}
			else if (ldType == ILdCoreConstants.iLD_TYPE_TASK)	{
				//Update the total time for the sequence
				long currentTime = this.getTaskSequenceTime();
				long nodeTaskTime = ((UILdTaskNode)oNode).getTaskTime();
				long timeLost = this.getTaskSequenceTimeForSubList(oLinksToRemove);
				this.setTaskSequenceTime(currentTime - timeLost );
				this.firePropertyChange(UILdTaskSequence.TASK_NODE_DELETED, oTS, this.getTaskSequence());
				if (oLinksToRemove.containsAll(oTS))
					this.firePropertyChange(UILdTaskSequence.ALL_LINKS_DELETED, oTS, this.getTaskSequence());
				return bReturnValue;
			}
		}
	return false;
	}
	
	/**
	 * Remove the node  from this task sequence if it is part of the sequence.
	 * Returns true if the node is removed, false otherwise. 
	 * @param sNodeId
	 * @return
	 */
	/** Not needed???
	public boolean removeNode(NodeSummary oNode)	{
		int ldType = oNode.getLdType();
		String sNodeId = oNode.getId();
		if (ldType == ILdCoreConstants.iLD_TYPE_ROLE)	{
			return (clearRoleData(sNodeId));
		}
		else if (ldType == ILdCoreConstants.iLD_TYPE_TASK)	{
			this.getTaskSequence().remove(sNodeId);
			return true;
		}
		return false;
	}
**/
	/**
	 * Method to clear the role data from this task sequence, i.e. set it to the same 
	 * state as for a newly created task sequence with no role data. 
	 * Returns true if the data is cleared and false otherwise (i.e. if the role 
	 * node with the specified id is not this task sequence's role node.
	 *
	 * @param sRoleId, the id of the role node to cleared
	 * @return true if the data was cleared, false otherwise.
	 */
	public boolean clearRoleData(String sRoleId)	{
		if (this.getRoleId().equals(sRoleId)) {
			this.setRoleId("");
			this.setRoleName("");
			this.setRoleType(ILdCoreConstants.iLD_TYPE_NO_TYPE);
			oRoleUILink = null;
			this.firePropertyChange(UILdTaskSequence.ROLE_DELETED, "", this.getTaskSequence());
			return true;
		}
		else
			return false;

	}
	
	
	/**
	 * Returns true if this task sequence contains a role node,  false otherwise.
	 * @param sRoleId
	 * @return
	 */
	public boolean containsRoleNode()	{
		return !(this.getRoleId().equals(""));	
	}
	/**
	 * Returns true if this task sequence contains the role specified by the 
	 * parameter sRoleId, false otherwise.
	 * @param sRoleId
	 * @return
	 */
	public boolean containsRoleNode(String sRoleId)	{
		return this.getRoleId().equals(sRoleId);	
	}

	 /**
   	* @see #firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
    
	public void firePropertyChange(String propertyName, Link oldValue, Link newValue)  {
		firePropertyChange(propertyName, new Integer(oldValue), new Integer(newValue));
	}
	*/
	
	  /**
	    * Support for reporting bound property changes. If oldValue and
	    * newValue are not equal and the PropertyChangeEvent listener list
	    * isn't empty, then fire a RemotePropertyChange event to each listener.
	    * This method has an overloaded method for each primitive type.  For
	    * example, here's how to write a bound property set method whose
	    * value is an int:
	    * <pre>
	    * public void setFoo(int newValue) {
	    *     int oldValue = foo;
	    *     foo = newValue;
	    *     firePropertyChange("foo", oldValue, newValue);
	    * }
	    * </pre>
	    *
	    * @param propertyName  The programmatic name of the property that was changed.
	    * @param oldValue  The old value of the property.
	    * @param newValue  The new value of the property.
	    */
	  	protected synchronized void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	   		//if (oChangeSupport != null) {
		  	//	oChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
			//}

	   		for (int i = listenerList.size()- 1 ; i >= 0 ; i--) {

				//if (propertyName == NodeSummary.NODE_TYPE_PROPERTY) {
				//	System.out.println("firing update for property type change + "+((PropertyChangeListener)listenerList.elementAt(i)).getClass().getName());
				//}

		   		((PropertyChangeListener)listenerList.elementAt(i)).propertyChange(new PropertyChangeEvent(this, propertyName, oldValue, newValue));
	   		}
	  	}
	  	
	  	/**
	     * Fire a property change with new value equal to the parameter newValue,
	     * oldValue equal to an empty String.
	     * 
	     * @see #firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
	     */
	 	public void firePropertyChange(String propertyName, String  newValue)  {
	 			 		firePropertyChange(propertyName, new String(""), newValue);
	   	}
	 	
	 	/**
	     * Fire a property change with new value equal to the parameter UILdNode newValue,
	     * oldValue equal to an empty String. This is for addition of new nodes to the tasksequence.
	     * UIldNode newValue - the node added to the sequence.
	     * @see #firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
	     */
	 	public void firePropertyChange(String propertyName, UILdNode  newValue)  {
	 			 		firePropertyChange(propertyName, new String(""), newValue);
	   	}
	 	
	 	/**
	 	 * Fire a property change with long integer values as arguments.
	 	 * 
	 	 * @param propertyName
	 	 * @param oldValue
	 	 * @param newValue
	 	 */
	 	protected synchronized void firePropertyChange(String propertyName, long oldValue, long newValue) {
	 		firePropertyChange(propertyName, new Long(oldValue), new Long(newValue));
	 	}
	   		
	 	 /**
	     * Add a PropertyChangeListener to the listener list.
	     * The listener is registered for all properties.
	     * <p>
	     * A PropertyChangeEvent will get fired in response to setting
	     * a bound property, e.g. setFont, setBackground, or setForeground.
	     * Note that if the current component is inheriting its foreground,
	     * background, or font from its container, then no event will be
	     * fired in response to a change in the inherited property.
	 	*
	     * @param listener  The PropertyChangeListener to be added
	     */
	 	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
	 		//Add the listener to the list, provided it is not already in the list
	 		if (listener == null) return;
	 		//boolean bContainsThsListener =  Arrays.asList(oListeners).contains(listener))
	 		boolean bContainsThsListener = 	listenerList.contains(listener);
	 		if (!bContainsThsListener)
	 			listenerList.addElement(listener);
	 	}

		/**
		 * Return the instance of LdTaskSequence that this instance is the UI 
		 * for.
		 * @return the oTaskSequence
		 */
		public LdTaskSequence getLdTaskSequence() {
			return oTaskSequence;
		}

		/**
		 * Set the task sequence that this instance is the user interface for.
		 * @param taskSequence the oTaskSequence to set
		 */
		public void setTaskSequence(LdTaskSequence taskSequence) {
			oTaskSequence = taskSequence;
		}

		/**
		 * @return the oTaskSequenceTime
		 */
		public long getTaskSequenceTime() {
			return oTaskSequenceTime;
		}

		
		/**
		 * Get the total task time for the list of tasks oList
		 * @param oList, LinkedList<UILink>, a list of the links to get the total
		 * time for 
		 * @return long, the total time for the links in the list.
		 */
		public long getTaskSequenceTimeForSubList(LinkedList<UILink> oList) {
			Iterator<UILink> it = oList.iterator();
			long totalTaskTime = 0;
			while (it.hasNext())	{
				totalTaskTime += ((UILdTaskNode)it.next().getToNode()).getTaskTime();
			}
			return totalTaskTime;
		}
		
		/**
		 * @param taskSequenceTime the oTaskSequenceTime to set
		 */
		public void setTaskSequenceTime(long taskSequenceTime) {
			long oldTime = oTaskSequenceTime;
			oTaskSequenceTime = taskSequenceTime;
			this.firePropertyChange(UILdTaskSequence.TIME_CHANGED, oldTime, oTaskSequenceTime);
		}

		public void propertyChange(PropertyChangeEvent evt) {
			String prop = evt.getPropertyName();

			Object source = evt.getSource();
			
			/** Added by Andrew	**/
			if (source instanceof UILdTaskNode) {
				if (prop.equals(UILdTaskNode.TIME_PROPERTY)) {
					// newvalue is a Long object
					Long newvalue = (Long)evt.getNewValue();
					Long oldvalue = (Long)evt.getOldValue();
					long oTime = this.getTaskSequenceTime();
					long valToSet = oTime + newvalue.longValue() - oldvalue.longValue();
					System.out.println("newvalue = " + newvalue + " oldvalue = " + oldvalue + " valToSet = " + valToSet + " source = " + source);
					
					this.setTaskSequenceTime(valToSet);

				}
			}
			if (source instanceof UINode) {
				int ldType = ((UILdNode)source).getLdType();
				if (ldType == ILdCoreConstants.iLD_TYPE_ROLE)	{
					if (prop.equals(UINode.TEXT_PROPERTY)) {
						this.firePropertyChange(UILdTaskSequence.ROLE_LABEL_CHANGED, ((UINode)source).getText());
					}
				}				
			}
			
			if (source instanceof View) {
				if (prop.equals(View.LINK_REMOVED)) {
					Link oLink = (Link)evt.getNewValue();
					this.removeLink(oLink);
				}
			}
			
/**		
			if (source instanceof UILdViewPane) {
				if (prop.equals(UILdViewPane.LINK_DELETED)) {
					UILink oLink = (UILink)evt.getNewValue();
					this.removeLink(oLink);
				}
			}
		**/
		
			
		}
		
		/**
		 * Remove the UILink  this task sequence, and update the times accordingly.
		 * 
		 * @param aLink
		 */
		public void removeLink(UILink aLink)	{
			
		}
		
		/**
		 * Convenience method to update the total time for this sequence when
		 * the task node ATaskNode is deleted from the sequence.
		 * @param aTaskNode
		 */
		private void updateTimeForRemovedTaskNode(UILdTaskNode aTaskNode)	{
			long currentTime = this.getTaskSequenceTime();
			long nodeTaskTime = aTaskNode.getTaskTime();
			this.setTaskSequenceTime(currentTime - nodeTaskTime );

		}
		
		/**
		 * Convenience method to update the total time for this sequence when
		 * the task node ATaskNode is deleted from the sequence.
		 * @param aTaskNode
		 */
		private void updateTimeForRemovedLink(Link aLink)	{
			long currentTime = this.getTaskSequenceTime();
			//Complete this method if needed
		//	long nodeTaskTime = aLink.getFrom().getTaskTime();
	//		this.setTaskSequenceTime(currentTime - nodeTaskTime );

		}
		/**
		 * Remove the UILink that represents the Link aLink from this task sequence.
		 * 
		 * @param aLink
		 */
		public void removeLink(Link aLink)	{
			UILink oRemovedUILink;
			// oTS is a copy of the original task sequence
			LinkedList<UILdTaskLink> oTS = new LinkedList<UILdTaskLink>(this.getTaskSequence());

			// If aLink is the last link in the sequence, but not the first, remove it and update the times, and remove the last entry from the list of Ids
			if (this.isLast(aLink) && !this.isFirst(aLink))	{
			/* Need to check that it is not the the first link, because the first link is a role link
			 * so oUITaskSequence.removeLast() would return an exception	**/ 			 	
				oRemovedUILink = oUITaskSequence.removeLast();
				this.oLinkIds.removeLast();
				// Update timing info 
				if (oRemovedUILink.getToNode().getLdType() == ILdCoreConstants.iLD_TYPE_TASK)	{
					// It's a  Ld task node, so update the time for this sequence accordingly and remove the node as a listener
					this.updateTimeForRemovedTaskNode(((UILdTaskNode)oRemovedUILink.getToNode()));
				}
				// The link to the toNode is being removed so this instance no longer needs to listen to that toNode
				oRemovedUILink.getToNode().removePropertyChangeListener(this);
			}
			// else if aLink is the first link in the sequence
			else if (this.isFirst(aLink))	{
				// Is the link also the last (i.e. the only link in the sequence)
				boolean bIsAlsoLast = this.isLast(aLink);
				oRemovedUILink = this.get(aLink);
				this.oLinkIds.removeFirst();
				// 
				if (oRemovedUILink.getFromNode().getLdType() == ILdCoreConstants.iLD_TYPE_TASK)	{
					oUITaskSequence.removeFirst();
					// If the first node is a Ld task node,  update timing info and remove the node as a listener
					this.updateTimeForRemovedTaskNode(((UILdTaskNode)oRemovedUILink.getToNode()));
				}
				else if (oRemovedUILink.getFromNode().getLdType() == ILdCoreConstants.iLD_TYPE_ROLE)	{
					// else if the first node is a Ld role node, clear the role data
					this.clearRoleData(oRemovedUILink.getFromNode().getNode().getId());
				}
				// The link from the fromNode is being removed so this instance no longer needs to listen to that fromNode
				oRemovedUILink.getFromNode().removePropertyChangeListener(this);
				// If the link removed is the first and last (i.e. the only) link, remove it from the UILDviewPane instance
				if (bIsAlsoLast)	{
					LinkedHashSet<UILdTaskSequence> oSequencesToRemove = new LinkedHashSet<UILdTaskSequence>();
					oSequencesToRemove.add(this);
					((UILdViewPane)oRemovedUILink.getToNode().getViewPane()).removeTaskSequences(oSequencesToRemove );
				}
			}
			
			else	{
				// Link to be removed is not the first or last link
				if (this.contains(aLink))	{
				// The link is in this sequence
					UILink oUILink = this.getFromUITaskSequence(aLink);
					// nIndex is the index of the link to be removed
					int nIndex = oTS.indexOf(oUILink);
					//Size of the list i.e no of links in it
					int nSize = oTS.size();
					// iLastLinkIndex is the index of the last link in the original sequence
					int iLastLinkIndex = nSize - 1;
					/** Get the index of the start of what will be the new sequence i.e one past  aLink in the sequence
					 *  i.e. one past the index of the link to be removed. */ 					 
					int nNewSeqStart = nIndex + 1;
					/** Get the index of the end  of what will be the original sequence i.e one before aLink in the sequence
					 *  i.e. one before the index of the link to be removed. */ 
					int nOldSeqEnd = nIndex - 1;
					// subList(int from, int  to) returns a view of the portion of this list between the specified fromIndex, inclusive, and toIndex, exclusive.
					//Make a new list for the old sequence containing links up to the one deleted
					LinkedList<UILdTaskLink> oOldSequenceList =  new LinkedList<UILdTaskLink>(oTS.subList(0, nIndex));
					//Make a new list for the new  sequence containing links after the one deleted
					LinkedList<UILdTaskLink> oNewSequenceList =  new LinkedList<UILdTaskLink>(oTS.subList(nNewSeqStart, nSize));
					// Update this task sequence with the truncated list
					this.setTaskSequence(oOldSequenceList);
					// Stop this task sequence listening to the task nodes in list that has been removed from this sequence
					this.removeListenersForTasks(oNewSequenceList);
					// Get the link ids for oOldSequenceList, and insert the role link id at the beginning 
					LinkedList<String> oIdList  = this.settLinkIdsUsing(oOldSequenceList);
					if (!(this.getRoleLink() == null))
							oIdList.addFirst(this.getRoleLink().getLink().getId());
					this.setLinkIds(oIdList);
					//Update the time for this sequence
					long oThisNewTime = this.updateTimes();
					this.setTaskSequenceTime(oThisNewTime);
					
					// Create a new task sequence containing the links after the deleted link					
					UILdTaskSequence oNewUITaskSequence = new UILdTaskSequence(oNewSequenceList);
					
					long oNewTime = oNewUITaskSequence.updateTimes();
					oNewUITaskSequence.setTaskSequenceTime(oNewTime);
				}
			}
				}
		
		/**
		 * @param aLink
		 * @return true if aLink is the role link for this instance, otherwise false
		 */
		public boolean isRoleLink(Link aLink)	{
			if (this.getRoleLink() == null)
				return false;
			if (aLink.getId().equals(this.getRoleLink().getLink().getId()))
				return true;
			else
				return false;
		}
		
		/**
		 * This method returns true if aLink is the data model Link instance that is 
		 * represented by the first UILink  in this task sequence. The link aLink may be a
		 * role link or a task link: this method will return true if its either and is
		 * the first in this task sequence.
		 * @param aLink
		 * @return true if aLink is the data model Link that is represented by 
		 * the first link in this task  sequence, false otherwise.
		 */
		public boolean isFirst(Link aLink)	{
			String sLinkId = aLink.getId();
			int i = this.oLinkIds.indexOf(sLinkId);
			if (i == 0)
				return true;
			else
				return false;
		}
		
		/**
		 * This method returns true if aLink is the data model Link instance that is 
		 * represented by the last UILink  in this task sequence.
		 * @param aLink
		 * @return true if aLink is the data model Link that is represented by 
		 * the last link in this task  sequence, false otherwise.
		 */
		public boolean isLast(Link aLink)	{
			// Return true if aLink is the role link and the task sequence is empty
			if (this.getTaskSequence().isEmpty())	{
				if (this.contains(aLink))
					return true;
			}
			// Otherwise return true if the last link in the task list is aLink
			try 	{
				if (!oUITaskSequence.isEmpty())	{ // This should stop the exception being thrown
					UILink oLastUILink = oUITaskSequence.getLast();
					if (oLastUILink.getLink().getId().equals(aLink.getId()))	{
						return true;
					}
					else	{
						return false;
					}
				}
				else
					return false;
			}
			catch (NoSuchElementException e)	{	
				//	String sError = e.getLocalizedMessage();
				String sError = "aLink: " + aLink.toString() + ", oUITaskSequence: " + oUITaskSequence.toString();
				JOptionPane.showMessageDialog(ProjectCompendium.APP, sError, "Error: method isLast(aLink)", JOptionPane.ERROR_MESSAGE);;
			}
			return false;
		}
		
		/**
		 * Calculates and returns the total task time for this sequence.
		 * @return long, the total task time for this sequence in minutes.
		 */
		private long updateTimes()	{
			long oTime = 0;
			oTime = this.getRoleLinkTime();
			if (oUITaskSequence.isEmpty())	{
				return oTime;
			}
			else	{
			// The task sequence is not empty
				UILdTaskLink aLink;
				UILdTaskNode aTaskNode;
				long oTaskTime;
			// Get the time for the first task 'from' node in the sequence
				aLink = oUITaskSequence.getFirst();
				aTaskNode = ((UILdTaskNode)aLink.getFromNode());
				oTime = this.getRoleLinkTime();
				oTime += aTaskNode.getTaskTime();
				Iterator<UILdTaskLink> oIt = oUITaskSequence.iterator();
				//Iterate over all the links in the task sequence, adding the time for each 'to' node to the total
				while (oIt.hasNext())	{
					aLink = oIt.next(); 
					if (aLink.getToNode().getLdType() == ILdCoreConstants.iLD_TYPE_TASK  ){
						aTaskNode = ((UILdTaskNode)aLink.getToNode());
						oTaskTime = aTaskNode.getTaskTime();
						oTime += oTaskTime;
//						oTime += ((UILdTaskNode)oIt.next().getToNode()).getTaskTime();
					}
				}
				return oTime;
			}
		}
		
		/**
		 * This method removes the listeners for all the TaskNodes in the supplied 
		 * list, i.e. after this method is run this instance will no longer
		 * listen for property changes (e.g. times) from these nodes.	 
		 */
		private void removeListenersForTasks(LinkedList<UILdTaskLink> oTaskSequenceList)	{			
			Iterator<UILdTaskLink> oIt = oTaskSequenceList.iterator();
			// Remove this sequence from the list of listeners for all the task nodes
			if (!oTaskSequenceList.isEmpty())
				oTaskSequenceList.getFirst().getFromNode().removePropertyChangeListener(this);
			while (oIt.hasNext())	{				
					oIt.next().getToNode().removePropertyChangeListener(this);				
			}
			
		}
		
		/**
		 * This method adds this task sequence as a listener for all the TaskNodes
		 * in the supplied list, i.e. after this method is run this instance will 
		 * listen for property changes (e.g. times) from these nodes.	 
		 */
		private void addListenersForTasks(LinkedList<UILdTaskLink> oTaskSequenceList)	{			
			Iterator<UILdTaskLink> oIt = oTaskSequenceList.iterator();
			UILdTaskNode oTaskNode;
			// Add this sequence to the list of listeners for all the task nodes
			if (!oTaskSequenceList.isEmpty())	{	
				// AddMake this task sequence lsitem to the first node IF it is not already listening
				PropertyChangeListener[] oPCL = oTaskSequenceList.getFirst().getFromNode().getPropertyChangeListeners();
				 List<PropertyChangeListener>  oListPCL = Arrays.asList(oPCL);
				 if (!oListPCL.contains(this))
					 oTaskSequenceList.getFirst().getFromNode().addPropertyChangeListener(this);
				
			}
			while (oIt.hasNext())	{	
				// Make this task sequence listen to the res of the task nodes in the list, if it is not already listening
				oTaskNode = (UILdTaskNode)oIt.next().getToNode();
				PropertyChangeListener[] oPCL =  	oTaskNode.getPropertyChangeListeners();
				 List<PropertyChangeListener>  oListPCL = Arrays.asList(oPCL);
				 if (!oListPCL.contains(this))
					 oTaskNode.addPropertyChangeListener(this);
			}
			
		}
		/**
		 * Get the time for the task node in the role link, or 0 if there is no role link.  
		 * @return long oTime, the task time for the task node in the role link. 
		 */
		private long getRoleLinkTime()	{
			// Get the task node in the role link
			if (this.getRoleLink() == null)
				return 0;
			else	{
			UILdTaskNode oNode = (UILdTaskNode)this.getRoleLink().getToNode();
			return(oNode.getTaskTime());
			}
		}

		/**
		 * Return the role link for this sequence i.e. the link between the role
		 * which carries out the tasks, and the the first task in the sequence.
		 * 
		 * @return the oRoleLink - a UILink between a UILdNode representing a 
		 * role and a UILdTaskNode.
		 */
		public UILink getRoleLink() {
			return oRoleUILink;
		}

		/**
		 * Set the role link for this sequence i.e. a link between the role
		 * which carries out the tasks, and the the first task in the sequence.
		 * 
		 * @param roleLink - the oRoleLink to set, a UILink between a UILdNode
		 *  representing a role and a UILdTaskNode.
		 */
		public void setRoleLink(UILink roleLink) {
			oRoleUILink = roleLink;
		}

		/**
		 * Get the LinkedList of ids of all the Links represented by this task
		 * sequence. Note that this method returns a list of ids of ALL the Links
		 * in the sequence, no matter if they are represented by UILinks or 
		 * UILdTaskLinks.
		 * @return the oLinkIds
		 */
		public LinkedList<String> getLinkIds() {
			return oLinkIds;
		}

		/**
		 * Get the LinkedList of ids of all the UILdTaskNodes represented by this task
		 * sequence. 
		 * @return the oLinkIds
		 */
		public LinkedList<String> getTaskNodeIds() {
			LinkedList<String> oTaskNodeIds = new LinkedList<String>(); 
			Iterator<UILdTaskLink> oIt = this.getTaskSequence().iterator();
			UILink aUILink;
			if (this.getTaskSequence().isEmpty()){
				// Task sequence is empty so return just the task node linked to the role node
				if (oRoleUILink == null)
					return oTaskNodeIds;
				else	{
					oTaskNodeIds.add(oRoleUILink.getToNode().getNode().getId());
					return oTaskNodeIds;
				}
			}
			else	{
				while(oIt.hasNext())	{
					aUILink = oIt.next();				
					oTaskNodeIds.add(aUILink.getFromNode().getNode().getId()); 				
				}
				// Add the last node id
				oTaskNodeIds.add( this.getTaskSequence().getLast().getToNode().getNode().getId());
				return oTaskNodeIds;
			}
		}
		
		/**
		 * Set the LinkedList of ids of all the Links represented by this task
		 * sequence. Note that this method should let a list of ids of ALL the Links
		 * in the sequence, no matter if they are represented by UILinks or 
		 * UILdTaskLinks.
		 * @param linkIds the oLinkIds to set
		 */
		public void setLinkIds(LinkedList<String> linkIds) {
			oLinkIds = linkIds;
		}
		
		/**
		 * This method tests if  oLdNode is the first task node in the task node 
		 * sequence. If   oLdNode is the first task node in the task sequence it 
		 * returns true. However, it oLdNode  is not a task node, or is not in 
		 * the sequence, or is not the first task node in the sequence it returns 
		 * false.
		 * @param oTaskNode
		 * @return true if oLdNode is the first task node in the task node sequence, false otherwise
		 */
		public boolean isFirstTaskNode(UILdNode oLdNode){
			if (oLdNode.getLdType() != ILdCoreConstants.iLD_TYPE_TASK)
				return false;
			if (oUITaskSequence.isEmpty())
				return false;
			else
				return this.oUITaskSequence.getFirst().getFromNode().getNode().getId().equals(oLdNode.getNode().getId());
		}

		/**
		 * This method tests if  oLdNode is the last  task node in the task node 
		 * sequence. If   oLdNode is the last task node in the task sequence it 
		 * returns true. However, it oLdNode  is not a task node, or is not in 
		 * the sequence, or is not the last task node in the sequence it returns 
		 * false.
		 * @param oTaskNode
		 * @return true if oLdNode is the last task node in the task node sequence, false otherwise
		 */
		public boolean isLastTaskNode(UILdNode oLdNode){
			if (oLdNode.getLdType() != ILdCoreConstants.iLD_TYPE_TASK)
				return false;
			
			if (oUITaskSequence.isEmpty() )	{
				if (this.getRoleLink() != null)	{
					return (this.getRoleLink().getToNode().getLdType() == ILdCoreConstants.iLD_TYPE_TASK);
				}
				else
					return false;
			}
			else	
				return this.oUITaskSequence.getLast().getToNode().getNode().getId().equals(oLdNode.getNode().getId());
		}

		
}
