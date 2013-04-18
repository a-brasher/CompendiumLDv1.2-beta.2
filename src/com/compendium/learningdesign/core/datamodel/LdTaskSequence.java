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

package com.compendium.learningdesign.core.datamodel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.Link;
import com.compendium.ui.UILink;

import com.compendium.learningdesign.core.ILdCoreConstants;
import com.compendium.learningdesign.ui.*;

/**
 * The purpose of the LdTaskSequence class is to store information about sequences
 * of learning design tasks. It does this by maintaining a list of the links. 
 * The links stored may be of two types: they are either links between task nodes, 
 * or links between a role node and the first task node in the sequence. 
 *   
 * @author ajb785
 *
 */
public class LdTaskSequence {

	/** The unique id of this task sequence. **/
	private String sId = "";

	/** This indicates the role that this task sequence is carried out by, e.g.
	 *  learner or tutor. These should be a value drawn from the values given for roles in the
	 *  enumeration interface ILdCoreConstants.	**/
	private int iRoleType = ILdCoreConstants.iLD_TYPE_NO_TYPE;

	/** The label of the role NodeSummary object	**/
	private String sRoleName = "";

	/** The unique id of the role NodeSummary object. **/
	private String sRoleId = "";

	/**	Representation of the sequence of tasks for the role, as the ids of the tasks in the sequence.	**/
	private LinkedList<String> oTaskIds = null;
		
	/**	Representation of the sequence of tasks for the role, as the links between the role and sequence of tasks.	**/
	private LinkedList<Link> oTaskSequence = null;
	
	/** List of LdTaskObjects that are represented in this sequence **/
	private LinkedList<LdTask> oLdTaskList = null;
	
	/** The time in minutes that this task sequence should take. 	**/
	private long  oTaskSequenceTime;
	
	// NOTE: use Vector because it is synchronised. Could use ArrayList but would have to write code to syncrhonise 
	
	/** A static list of LdTaskSequence objects already created in this session.*/
	private static Vector<LdTaskSequence> oLdTaskSequenceList = new Vector<LdTaskSequence>();
	
	/**	Enumeration of role types, including iLD_TYPE_NO_TYPE for "no role".
	 *   Note that iLD_TYPE_ROLE_GROUP is not currently include because it is
	 *    no longer used. 		**/
	//	public  static  enum eLD_ROLE_TYPES	{iLD_TYPE_ROLE_STUDENT, iLD_TYPE_ROLE_TUTOR,  iLD_TYPE_ROLE_OTHER};
	//	public HashSet<Integer> hs = new HashSet<Integer>(Arrays.asList (ILdCoreConstants.iROLETYPES));
/**	public static final HashSet hsRoleType = new HashSet(Arrays
			.asList(ILdCoreConstants.iROLETYPES));	**/
	
	/**
	 * Returns the static list of LdTaskSequence objects already created in this session.
	 * @return the oLdTaskSequenceList
	 */
	public static Vector<LdTaskSequence> getLdTaskSequenceList() {
		return oLdTaskSequenceList;
	}

	/**
	 * @param ldTaskSequenceList the oLdTaskSequenceList to set
	 */
	public static void setLdTaskSequenceList(
			Vector<LdTaskSequence> ldTaskSequenceList) {
		oLdTaskSequenceList = ldTaskSequenceList;
	}
	
	/**
	 * Create a set of LdTaskSequence objects containing data corresponding to 
	 * the set of UILdTaskSequence objects contained in the parameter 
	 * oUILdTaskSequenceSet.
	 * @param oUILdTaskSequenceSet
	 * @return
	 */
	public static LinkedHashSet<LdTaskSequence>  createLdTaskSequenceSet(LinkedHashSet<UILdTaskSequence> oUILdTaskSequenceSet )	{
		LdTaskSequence aLdTaskSequence;
		UILdTaskSequence aUILdTaskSequence;
		LinkedHashSet<LdTaskSequence> oLdTaskSequenceSet = new LinkedHashSet<LdTaskSequence>();
		Iterator<UILdTaskSequence> oIt = oUILdTaskSequenceSet.iterator();
		LinkedList<LdTask> oLdTaskList; 
		while (oIt.hasNext())	{
			aUILdTaskSequence = oIt.next();
			aLdTaskSequence = aUILdTaskSequence.getLdTaskSequence();
			/* This a LDtaskSequence object will have an empty LinkedList for the task sequence oTaskSequence, 
			 * and a unique ID created by the application. **/
			// Set the RoleID for the task sequence
			aLdTaskSequence.setRoleId(aUILdTaskSequence.getRoleId());
			// Set the RoleName for the task sequence
			aLdTaskSequence.setRoleName(aUILdTaskSequence.getRoleName());
			// Set the Task Node ids for the task sequence
			aLdTaskSequence.setTaskIds(aUILdTaskSequence.getTaskNodeIds());	
			// Create a linked list of LdTask objects for this task sequence
			oLdTaskList = aLdTaskSequence.createLdTaskSet(aUILdTaskSequence);
			// Add them to this task sequence
			aLdTaskSequence.setTaskSequence(oLdTaskList);
			// Set the task sequence time value
			aLdTaskSequence.setTaskSequenceTime(aUILdTaskSequence.getTaskSequenceTime());
			//Addd the task sequence to the set
			oLdTaskSequenceSet.add(aLdTaskSequence);
		}
		return oLdTaskSequenceSet;
	}
	/**
	 * 	
	 * Constructor. Creates a LDtaskSequence object which has  
	 * an empty LinkedList for the task sequence oTaskSequence, and a unique
	 * ID created by the application. 
	 * 	 
	 */
	public LdTaskSequence() {
		super();
		sId = ProjectCompendium.APP.getModel().getUniqueID();
		oTaskSequence = new LinkedList<Link>();
		oTaskIds = new LinkedList<String>();
		// Add this instance to the lsit created this session
		LdTaskSequence.getLdTaskSequenceList().add(this);
	}
	
	/**
	 * 	
	 * Constructor. Creates a LDtaskSequence object with the supplied Id  which 
	 * has  an empty LinkedList for the task sequence oTaskSequence.
	 * 	 
	 */
	public LdTaskSequence(String sSuppliedId) {
		super();
		sId = sSuppliedId;
		oTaskSequence = new LinkedList<Link>();
		oTaskIds = new LinkedList<String>();
		// Add this instance to the lsit created this session
		LdTaskSequence.getLdTaskSequenceList().add(this);
	}
	/**
	 * 
	 * @param aUILdTaskSequence
	 */
	public LdTaskSequence(UILdTaskSequence aUILdTaskSequence)	{
		this();
		sRoleId = aUILdTaskSequence.getRoleId();
	}
	
	/**
	 * This method returns true if the list of sequences oTaskSequenceList contains the 
	 * sequence oTaskSequence, false otherwise.
	 * @param oTaskSequenceList
	 * @param oTaskSequence
	 * @return
	 */
	public static boolean listContains(LinkedHashSet<LdTaskSequence>  oTaskSequenceList, LdTaskSequence oTaskSequence)	{
		// For every task in the new list
		for (LdTaskSequence aTaskSequence : oTaskSequenceList)	{
			if (aTaskSequence.equals(oTaskSequence))
				return true;
		}
		// else return false
		return false;
	}
	
	/**
	 * This method returns true if the list of sequences that have been loaded 
	 * into the model in this session contains the  sequence oTaskSequence, false 
	 * otherwise. Note that currently this will be 'true' for every LdTaskSequence
	 * because each LdTaskSequence is added to the list as it is created.
	 * @param oTaskSequence
	 * @return
	 */
	public static boolean listContains(LdTaskSequence oTaskSequence)	{
		// For every task sequence in the model (i.e. in the list in static variable oLdTaskSequenceList)
		for (LdTaskSequence aTaskSequence : LdTaskSequence.getLdTaskSequenceList())	{
			if (aTaskSequence.equals(oTaskSequence))
				return true;
		}
		// else return false
		return false;
	}
	
	/**
	 * This method creates a linked list of LdTask objects from a linked list of UILDTaskLinks; these instances contain
	 * the task data corresponding to the task nodes contained in the list passed to the method 
	 * in the parameter oTaskLinkSequence.
	 * @param LinkedList<UILdTaskLink> oTaskLinkSequence
	 * @return LinkedList<LdTask>, a list of LdTask objects
	 */
	private LinkedList<LdTask> createLdTaskSet(LinkedList<UILdTaskLink> oTaskLinkSequence)	{				
		LinkedList<LdTask> oLdTaskList = new LinkedList<LdTask>();
		UILdTaskNode fromNode, toNode;
		LdTask oTask;
		if (oTaskLinkSequence.isEmpty())
			return oLdTaskList;
		
		// Create LdTask objects corresponding to all the 'from' nodes and add them to the oLdTaskSet
		for (UILdTaskLink  oTaskLink: oTaskLinkSequence)	{
			fromNode = (UILdTaskNode)oTaskLink.getFromNode();
			oTask = new LdTask(fromNode.getNode().getId(),  fromNode.getTaskTime(), fromNode.getCurrentTaskTimeUnits(), fromNode.getShowTime() );
			// Only add the task to the list if it is not already in the list - may need to add a flag to warn user if it is already in the list
			/**	if (!oLdTaskList.contains(oTask))	{
				oLdTaskList.add(oTask);
			}	**/
			if (!LdTask.listContains(oLdTaskList, oTask)) {
				oLdTaskList.add(oTask);
			}
		}
		// Create a LdTask object corresponding to the last 'to' node and add it to the oLdTaskSet
	//	UILdTaskNode lastNode = (UILdTaskNode)oTaskLinkSequence.getLast().getToNode();
//		oTask = new LdTask(lastNode.getNode().getId(),  lastNode.getTaskTime(), lastNode.getCurrentTaskTimeUnits(), lastNode.getShowTime() );
		//oLdTaskList.add(oTask);
		
		// Create LdTask objects corresponding to any  'to' nodes which have not already been added as 'from' nodes, and add them to the oLdTaskSet
		// This should only be the last 'to' node
		for (UILdTaskLink  oTaskLink: oTaskLinkSequence)	{
			toNode = (UILdTaskNode)oTaskLink.getToNode();
			oTask = new LdTask(toNode.getNode().getId(),  toNode.getTaskTime(), toNode.getCurrentTaskTimeUnits(), toNode.getShowTime() );
//			if (!oLdTaskList.contains(oTask))	{
			if (!LdTask.listContains(oLdTaskList, oTask)) {
				oLdTaskList.add(oTask);
			}
		}
		
		return oLdTaskList;
	}
	
	/**
	 * This method creates a linked list of LdTask objects; these instances contain
	 * the task data corresponding to the task nodes contained in the list passed to the method 
	 * in the parameter oTaskLinkSequence.
	 * @param LinkedList<UILdTaskLink> oTaskLinkSequence
	 * @return LinkedList<LdTask>, a list of LdTask objects
	 */
	
	public LinkedList<LdTask> createLdTaskSet(UILdTaskSequence oUITaskSequence)	{
		// Create a list of tasks from the list of UILDTaskLinks
		LinkedList<LdTask> oLdTaskList = this.createLdTaskSet(oUITaskSequence.getTaskSequence());
		UILink oRoleLink = oUITaskSequence.getRoleLink();
		// If the role link is not null, and the task node in the role link is not already in oLdTaskList, add it to the list
		if (oRoleLink != null)	{
			UILdTaskNode oTaskNode = ((UILdTaskNode)oRoleLink.getToNode());
			if (!oUITaskSequence.contains(oTaskNode))	{
				LdTask oTask = new LdTask(oTaskNode.getNode().getId(),  oTaskNode.getTaskTime(), oTaskNode.getCurrentTaskTimeUnits(), oTaskNode.getShowTime() );
				oLdTaskList.addFirst(oTask);
			}
		}
		return oLdTaskList;
	}
	

	/**
	 * @param oAnotherLdTaskSequence
	 * @return true if this LdTaskSequence's id is equal to oAnotherLdTaskSequence's id
	 */
	public boolean equals(LdTaskSequence oAnotherLdTaskSequence)	{
		return (this.getId().equals(oAnotherLdTaskSequence.getId()));
	}
	
	/**
	 * This method returns true if the list of task links oTaskSequence is empty
	 * , false otherwise.
	 * 
	 * @return true if the list of task links oTaskSequence is empty, false
	 * otherwise.
	 */
	public boolean isEmpty() {
		return (oTaskSequence.isEmpty());
	}

	/**
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
	 * Returns the id of the role  for this task sequence, or an empty String if the 
	 * role id has not been set.
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
	 * @return oLdTaskList, the list of LdTaskObjects for this instance
	 */
	public LinkedList<LdTask> getTaskSequence() {
		return oLdTaskList;
	}

	/**
	 * Set oLdTaskList, the list of LdTaskObjects for this instance. Set the
	 * list of task ids oTaskIds to be list of ids of the tasks, and update the 
	 * tasksequence time to reflect the total time taken by the tasks in the list.
	 * @param aLdTaskList, the list of LdTask objects to set
	 */
	public void setTaskSequence(LinkedList<LdTask> aLdTaskList) {
		oLdTaskList = aLdTaskList;
		Iterator<LdTask> oIt = oLdTaskList.iterator();
		String sId;
		this.oTaskIds.clear();
		long lSeqTime = 0;
		LdTask oCurrentTask;
		while (oIt.hasNext())	{
			oCurrentTask = oIt.next();
			sId = oCurrentTask.getNodeSummaryTaskId();
			this.oTaskIds.add(sId);
			lSeqTime += oCurrentTask.getTaskTime();
		}
		this.setTaskSequenceTime(lSeqTime);
	}

	/**
	 * Method to clear the role data from this task sequence, i.e. set it to the same 
	 * state as for a newly created task sequence with no role data. 
	 * Returns true if the data is clered and false otherwise (i.e. if the role 
	 * node with the specified id is not this task sequence's role node.
	 *
	 * @param sRoleId, the id of the role node to cleared
	 * @return true if the data was cleared, false otherwise.
	 */
	public boolean clearRoleData(String sRoleId) {
		if (this.getRoleId().equals(sRoleId)) {
			this.setRoleId("");
			this.setRoleName("");
			this.setRoleType(ILdCoreConstants.iLD_TYPE_NO_TYPE);
			return true;
		} else
			return false;

	}

	/**
	 * Returns true if this task sequence contains the role specified by the 
	 * parameter sRoleId, false otherwise.
	 * @param sRoleId
	 * @return
	 */
	public boolean containsRoleNode(String sRoleId) {
		return this.getRoleId().equals(sRoleId);
	}
	
	/**
	 * Returns the LdTask object with the id sTaskId from this sequence, or a
	 * null LdTask object if there is not one with id sTaskId in the sequence.
	 * @param sTaskId
	 * @return
	 */
	public LdTask getTask(String sTaskId)	{
		boolean bContains = oTaskIds.contains(sTaskId);
		Iterator<LdTask> oIt = oLdTaskList.iterator();
		// Variable to  be used in the llop
		LdTask oTask = null;
		// Variable to be returned if the task with id sTaskId is not found in  this sequence
		LdTask oNullTask = null;
		while (oIt.hasNext())	{
			oTask = oIt.next();
			if (oTask.getNodeSummaryTaskId().equals(sTaskId))
				return oTask;			
		}
		return oNullTask;
	}

	

	/**
	 * Get the unique id of this task sequence.
	 * @return String, the sId
	 */
	public String getId() {
		return sId;
	}





	/**
	 * Return the linked list of task ids for this sequence.
	 * @return the oTaskIds
	 */
	public LinkedList<String> getTaskIds() {
		return oTaskIds;
	}





	/**
	 * Set the linked list of task ids to be the list in the argument.
	 * @param taskIds the oTaskIds to set
	 */
	public void setTaskIds(LinkedList<String> taskIds) {
		oTaskIds = taskIds;
	}
	/**
	 * @return the oTaskSequenceTime
	 */
	public long getTaskSequenceTime() {
		return oTaskSequenceTime;
	}
	/**
	 * @param taskSequenceTime the oTaskSequenceTime to set
	 */
	public void setTaskSequenceTime(long taskSequenceTime) {
		oTaskSequenceTime = taskSequenceTime;
	}
	
	public String toString()	{
		String sNewLine = "\n";
		String sOutput = "**-------------------**" + sNewLine;
		sOutput += "LdTaskSequence sId = " + this.getId() + sNewLine;
		sOutput += "sRoleName = " + this.getRoleName() + " sRoleId = " + this.getRoleId() + sNewLine;
		sOutput += "oTaskIds = " + oTaskIds.toString() + sNewLine;
		Iterator<LdTask>  oIt = oLdTaskList.iterator();
		sOutput += "-----LdTasks ---" + sNewLine;
		while (oIt.hasNext()){
			sOutput +=  "LdTask: " + oIt.next().toString() + sNewLine;
		}
		sOutput += "**--------------------**" + sNewLine;
		return(sOutput);
	}
}
