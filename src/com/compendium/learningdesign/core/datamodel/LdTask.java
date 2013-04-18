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

import java.util.LinkedList;
import java.util.Vector;
import com.compendium.learningdesign.util.TimeUnit;
import java.lang.Comparable;

import com.compendium.learningdesign.ui.ILdTaskTime;

public class LdTask implements ILdTaskTime, Comparable<LdTask> {
	/**	The id of the NodeSummary that this task data is related to	**/
	String sNodeSummaryTaskId;
	
	/** The task time value: the units for this value are given by oCurrentTaskTimeUnits **/
	private long  oTaskTime;
	
	/** The task time units that were set the last time the task time was set. 
	 * These should be used to display the task time.	**/
	private TimeUnit oCurrentTaskTimeUnits = TimeUnit.HOURS;
	
	/**	Indicates whether the task time should be shown for this node. Set to
	 * false initially, changed when time is set. 		 **/
	private boolean bShowTime = false;
	
	/** A static list of LdTask objects already created in this session.*/
	private static Vector<LdTask> oLdTaskList = new Vector<LdTask>();

	/**
	 * @param nodeSummaryTaskId
	 * @param taskTime
	 * @param timeUnit
	 * @param showTime
	 */
	public LdTask(String nodeSummaryTaskId, long taskTime,
			com.compendium.learningdesign.util.TimeUnit timeUnit, boolean showTime) {
		super();
		sNodeSummaryTaskId = nodeSummaryTaskId;
		oTaskTime = taskTime;
		oCurrentTaskTimeUnits = timeUnit;
		bShowTime = showTime;
		oLdTaskList.add(this);
	}
	
	
	public LdTask() {
		super();
		oLdTaskList.add(this);
	}

	public LdTask(String sId) {
		super();
		sNodeSummaryTaskId = sId;
		oLdTaskList.add(this);
	}
	/**
	 * This method returns true if the list of tasks oTaskList contains the the
	 * task oTask, false otherwise.
	 * @param oTaskList
	 * @param oTask
	 * @return
	 */
	public static boolean listContains(LinkedList<LdTask>  oTaskList, LdTask oTask)	{
		// For every task in the new list
		for (LdTask aTask : oTaskList)	{
			if (aTask.equals(oTask))
				return true;
		}
		// else return false
		return false;
	}
	
	/**
	 * Return a LdTask object with the given id if a LdTask with the given id 
	 * has already been created in this session, return that,
	 * else return a null LdTask.
	 *
	 * @param String sNodeID, the id of the node to return/create.
	 * @return LdTask, a LdTask  object with the given id.
	 */
	public static LdTask getLdTask(String sID) {
		for (LdTask oTask: LdTask.oLdTaskList)	{
			if (oTask.getId().equals(sID))
				return oTask;
		}
		LdTask  oTask = null;
		
		return oTask;
	}
	
	/**
	 * Get the boolean value which indicates whether the  node task time should
	 * be shown.
	 * @return the bShowTime
	 */
	public boolean getShowTime() {
		return bShowTime;
	}

	/**
	 * Set the boolean value which indicates whether the  node task time should
	 * be shown.
	 * @param showTime the bShowTime to set
	 */
	public void setShowTime(boolean showTime) {
		bShowTime = showTime;
	}



	/**
	 * @return the sNodeSummaryTaskId
	 */
	public String getNodeSummaryTaskId() {
		return sNodeSummaryTaskId;
	}



	/**
	 * Get the time for this task
	 * @return the oTaskTime
	 */
	public long getTaskTime() {
		return oTaskTime;
	}



	/**
	 * Get the TimeUnit value currently set for this task.
	 * @return the oCurrentTaskTimeUnits
	 */
	public TimeUnit getCurrentTaskTimeUnits() {
		return oCurrentTaskTimeUnits;
	}	
	
	/**
	 * @return the id of the nodeSummary associated with the relevant task
	 */
	public String getId()	{
		return this.getNodeSummaryTaskId();
		
	}
	
	/**
	 * @param oAnotherLdTask
	 * @return true if this LdTask's id is equal to oAnotherLdTask's id
	 */
	public boolean equals(LdTask oAnotherLdTask)	{
		return (this.getId().equals(oAnotherLdTask.getId()));
	}
	
	public int compareTo(LdTask oTask)	{
		
		if (this.equals(oTask))
			return 0;
		else {
			int iThis = new Integer(this.getId()); 
			int iTask = new Integer(oTask.getId());
			if (iThis < iTask )
				return -1;
			else
				return 1;
		}
			 
	}


	


	/**
	 * Set the time for this task
	 * @param taskTime the oTaskTime to set
	 */
	public void setTaskTime(long taskTime) {
		oTaskTime = taskTime;
	}


	/**
	 * TSEt the time units to be used to display this instance's time value.
	 * @param currentTaskTimeUnits the oCurrentTaskTimeUnits to set
	 */
	public void setCurrentTaskTimeUnits(TimeUnit currentTaskTimeUnits) {
		oCurrentTaskTimeUnits = currentTaskTimeUnits;
	}

	public String toString()	{
		String sNewLine = "\n";
		String sOutput = "---------------------" + sNewLine;
		sOutput += "LdTask sId = " + this.getId() + sNewLine;
		sOutput += "bShowTime = " + this.getShowTime() + " oCurrentTaskTimeUnits " + oCurrentTaskTimeUnits.toString()
			+ " taskTime = " + this.getTaskTime() + 	 sNewLine;
		sOutput += "---------------------" + sNewLine;
		return(sOutput);
	}

}
