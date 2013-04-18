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

package com.compendium.learningdesign.core.datamodel;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Vector;
import java.util.LinkedHashSet;
//import java.util.concurrent.TimeUnit;
import com.compendium.core.datamodel.ILink;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.learningdesign.util.TimeUnit;
import java.util.Iterator;

import com.compendium.learningdesign.ui.ILdTaskTime;
import com.compendium.learningdesign.ui.UILdTaskSequence;

/**
 * The LdActivityTimes class holds the set of task sequences for an activity.
 * @author ajb785
 *
 */
public class LdActivityTimes implements ILdTaskTime {
	/**	The String that is the id of the NodeSummary that relates to the activity **/
	private String sId = null;
	
	/**	A reference to the set of Ld task sequences for this instance  **/
	private LinkedHashSet<LdTaskSequence> oTaskSequenceSet = null;

	/**	Indicates whether the task time should be shown for this node. Set to
	 * false initially, changed when time is set. WHILE TESTING SET TO TRUE INITIALLY		 **/
	private boolean bShowTime = true;
	
	/** The activity  time units that were set the last time the activity time was displayed. 
	 * These should be used to display the task time.	**/
	private TimeUnit oCurrentTaskTimeUnits = TimeUnit.HOURS;
	// Note: use Vector for oLdActivityTimesList because it is synchronised
	/** A static list of LdActivityTimes objects already created in this session.*/
	private static Vector<LdActivityTimes> oLdActivityTimesList = new Vector<LdActivityTimes>();;
	
	
	
	/**
	 * @param id, the id of the NodeSummary that represents the Activity node
	 * @param taskSequenceSet, the task sequence set in the UILDViewPane instance which displays the View of the Activity
	 * @param showTime, a boolean which states whether times should be displayed on activity nodes (true), or not (false) 
	 * @param currentTaskTimeUnits, the units in which the time should be displayed; if it is displayed is determined by the value of aparmeter showTime
	 */
	/**
	public LdActivityTimes(String id, LinkedHashSet<LdTaskSequence> taskSequenceSet,
			boolean showTime, TimeUnit currentTaskTimeUnits) {
		super();
		sId = id;
		oTaskSequenceSet = taskSequenceSet;
		bShowTime = showTime;
		oCurrentTaskTimeUnits = currentTaskTimeUnits;
	}
	**/
	/**
	 * Create a LdActivityTimes object for the Activity represented by the 
	 * NodeSummary referred to by the parameter id, which contains the set
	 * of task sequences oUILdTaskSequenceSet, for which the showTime parameter 
	 * indicates whether the total time should be shown on the Activity node. If
	 * this is 'true', the time will be displayed in the units given by the parameter
	 * currentTaskTimeUnits.
	 * @param id, the id of the NodeSummary that represents the Activity node
	 * @param taskSequenceSet, the task sequence set in the UILDViewPane instance which displays the View of the Activity
	 * @param showTime, a boolean which states whether times should be displayed on activity nodes (true), or not (false) 
	 * @param currentTaskTimeUnits, the units in which the time should be displayed; if it is displayed is determined by the value of aparmeter showTime
	 */
	public LdActivityTimes(String id, LinkedHashSet<UILdTaskSequence> oUILdTaskSequenceSet,
			boolean showTime, TimeUnit currentTaskTimeUnits) {
		this(id);
		oTaskSequenceSet = LdTaskSequence.createLdTaskSequenceSet(oUILdTaskSequenceSet);
		bShowTime = showTime;
		oCurrentTaskTimeUnits = currentTaskTimeUnits;
	}

	/**
	 * Create a LdActivityTimes object for the NodeSummary that represents the Activity node.
	 * This instance has an empty oTaskSequenceSet;
	 * Add this instance to the list of  LdActivityTimes instances created in this session
	 * @param id, the id of the NodeSummary that represents the Activity node
	 */
	public LdActivityTimes(String id)	{
		super();
		sId = id;
		/** Change made on 10/9/2009 Empty oTaskSequenceSet instantiated	**/ 
		oTaskSequenceSet = new LinkedHashSet<LdTaskSequence>();
		LdActivityTimes.getLdActivityTimesList().add(this);
	}
	
	/**
	 * Constructor. Creates an instance of LdActivityTimes, but initialises
	 * none of the attributes. That is, this constructor creates an 'empty'
	 * instance which may be 'filled' using setter methods.
	 */
	public LdActivityTimes()	{
		super();
	}
	
	/**
	 * @return the oLdActivityTimesList
	 */
	public static Vector<LdActivityTimes> getLdActivityTimesList() {
		return oLdActivityTimesList;
	}
	
	/**
	 * Return a LdActivityTimes object with the given id.
	 * If a LdActivityTimes object with the given id has already been created in this session, 
	 * return it, else create a new one, and add it to the list.

	 * @param sActivityId
	 * @return
	 */
/**	public static synchronized LdActivityTimes getLdActivityTimes(String sActivityId){
		// Get the instance with id sActivityId that's in the list, or null if there isn't one 
		LdActivityTimes oLdActivityTimes = 	LdActivityTimes.getActivityFromSessionList(sActivityId);
		if (oLdActivityTimes != null)
			return oLdActivityTimes;
		else	{
			
		}
	}
	**/
	
	/**
	 * This method returns true if the list of activities created in this session
	 * oLdActivityTimesList contains the activity identified by the id sActivityId,
	 * false otherwise.
	 * @param sActivityId
	 * @return
	 */
	public static boolean sessionListContains(String sActivityId)	{
		// For every activity in the list
		for (LdActivityTimes aLdActivityTimes : oLdActivityTimesList)	{
			if (aLdActivityTimes.getId().equals(sActivityId))
				return true;
		}
		// else return false
		return false;
	}
	
	/**
	 * Return the LdActivityTimes instance with id equal to sActivityId from 
	 * the list of LdActivityTimes objects already created in this session, or
	 * a null LdActivityTimes object if none exists for the session.
	 * 
	 * @param sActivityId
	 * @return
	 */
	public static LdActivityTimes getActivityFromSessionList(String sActivityId)	{
		// For every activity in the list
		for (LdActivityTimes aLdActivityTimes : oLdActivityTimesList)	{
			if (aLdActivityTimes.getId().equals(sActivityId))
				return aLdActivityTimes;
		}
		// else return false
		LdActivityTimes oLdActivityTimes = null;
		return oLdActivityTimes;
	}
	
	public boolean getShowTime() {
		return bShowTime;
	}

	public void setShowTime(boolean showTime) {
		bShowTime = showTime;
		
	}

	/**
	 * @return the oCurrentTaskTimeUnits
	 */
	public TimeUnit getCurrentTaskTimeUnits() {
		return oCurrentTaskTimeUnits;
	}

	/**
	 * @param currentTaskTimeUnits the oCurrentTaskTimeUnits to set
	 */
	public void setCurrentTaskTimeUnits(TimeUnit currentTaskTimeUnits) {
		oCurrentTaskTimeUnits = currentTaskTimeUnits;
	}

	/**
	 * Returns the String that is the id of the NodeSummary that relates to the
	 *  activity that this LdActivityTimes object relates to.
	 * @return the sId
	 */
	public String getId() {
		return sId;
	}

	/**
	 * @param id the sId to set
	 */
	public void setId(String id) {
		sId = id;
	}

	/**
	 * Get the task sequence set for this instance.
	 * @return the oTaskSequenceSet
	 */
	public LinkedHashSet<LdTaskSequence> getTaskSequenceSet() {
		return oTaskSequenceSet;
	}

	/**
	 * Set the task sequence set for this instance.
	 * @param taskSequenceSet the oTaskSequenceSet to set
	 */
	public void setTaskSequenceSet(LinkedHashSet<LdTaskSequence> taskSequenceSet) {
		oTaskSequenceSet = taskSequenceSet;
	}
	
	/**
	 * Convenience method to add the LdTaskSequence to the set of LdTaskSequences
	 * for this instance.
	 * @param oLdTaskSequence
	 */
	public boolean  addTaskSequence(LdTaskSequence oLdTaskSequence)	{
		return this.getTaskSequenceSet().add(oLdTaskSequence);
	}

	/**
	 * Get the task identified by the id sTaskId in this sequence set, or a null
	 * LdTask if the task with id sTaskId is not in this set.
	 * @param sTaskId
	 */
	public LdTask getTask(String sTaskId)	{
		Iterator<LdTaskSequence> oIt = oTaskSequenceSet.iterator();
		LdTaskSequence oTaskSequence;
		LdTask oTask = null;
		while (oIt.hasNext())	{
			oTaskSequence = oIt.next();
			oTask = oTaskSequence.getTask(sTaskId);
			if (oTask != null)
				return oTask;
		}
		return oTask;
	}
	/**
	 * Get the task time for the task identified by the id sTaskId
	 * @param sTaskId
	 */
	/**
	public void getTimeForTask(String sTaskId)	{
		this.getTaskSequenceSet();		
	}
	***/
	/**
	 * The presence of an id indicates that this instance is associated with
	 * an activity. This method return true if this instance does not have an
	 * id string.
	 * @return true if this instance does not have an id, false if it does
	 */
	public boolean isEmpty()	{
		if (this.getId()== null)
			return true;
		else 
			return false;
	}
	
	public String toString()	{
		//Output Id
		String sNewLine = "\n";
		String sOutput = "++--------------------++" + sNewLine;
		sOutput += "LdActivityTimes sId = " + this.getId() + sNewLine;
		sOutput += "bShowTime = " + this.getShowTime() + " oCurrentTaskTimeUnits " + oCurrentTaskTimeUnits.toString() +sNewLine;
		
		Iterator<LdTaskSequence> oIt = oTaskSequenceSet.iterator();
		while (oIt.hasNext()){
			sOutput += oIt.next().toString();
		}
		sOutput += "++---------------------++" + sNewLine;
		return sOutput;
	}

	/**
	 * Order the TaskLink data in this instance to reflect the order in the View.
	 * Note this could be done by adding an 'order' column to the TaskSequenceTask 
	 * table and then ordering the data retrieved by that value but this is a stop-gap
	 * solution to check I've diagnosed the XML import/export problem correctly. 
	 * @param ldActivityView
	 */

	public void orderLinks(LdActivityView aLdActivityView) {
		//Enumeration<ILink> oLinks = (Enumeration<ILink>)aLdActivityView.getLinks();
		//Enumeration<Link> oLinks = links;
		
		for (Enumeration<ILink> oLinks = (Enumeration<ILink>)aLdActivityView.getLinks(); oLinks.hasMoreElements() ;)	{
			ILink oLinkA = oLinks.nextElement();
			NodeSummary oFromA = oLinkA.getFrom();
			NodeSummary oToA = oLinkA.getTo();
			ILink oLinkB = oLinks.nextElement();
			NodeSummary oFromB = oLinkB.getFrom();
			NodeSummary oToB = oLinkB.getTo();
			
		}
		for (LdTaskSequence aTaskSEquence: oTaskSequenceSet)	{
			LinkedList<LdTask>  oTaskList = aTaskSEquence.getTaskSequence();
			for (LdTask aTask : oTaskList) {
		
			}
		}
	}
	
}
