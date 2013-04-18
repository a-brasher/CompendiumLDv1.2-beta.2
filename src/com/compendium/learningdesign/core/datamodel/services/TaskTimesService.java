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

/**
 The TaskTimesService class provides services to retrieve and manipulate TaskTimes 
 data stored in the model or in the long term data storage. Long term data storage 
 is currently effected in a Derby or MySQL database which is accessed via classes in
 the com.compendium.core.db and com.compendium.learningdesign.core.db packages. 
 */
/**
 The TaskTimesService class provides services to manipulate TaskTimes record data 
 in the database. 
 */
package com.compendium.learningdesign.core.datamodel.services;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Vector;
import java.util.Collections;
import com.compendium.learningdesign.util.TimeUnit;

import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.ClientService;
import com.compendium.core.datamodel.services.ServiceManager;
import com.compendium.core.db.DBNode;
import com.compendium.core.db.management.*;

import com.compendium.learningdesign.core.datamodel.*;
import com.compendium.learningdesign.core.db.*;
import com.compendium.learningdesign.ui.UILdTaskSequence;

/**
 * @author ajb785
 *
 */
public class TaskTimesService extends ClientService implements ITaskTimesService, java.io.Serializable {

	/** The computed serial version ID  */
	private static final long serialVersionUID = -1810559048308598703L;
	// NOTE: use Vector because it is synchronised. Could use ArrayList but would have to write code to syncrhonise 
	/** A static list of LdTaskSequence objects already created in this session.*/
	private static Vector<LdTaskSequence> oLdTaskSequenceList;

	/** A static list of LdActivityTimes objects already created in this session.*/
	private static Vector<LdActivityTimes> oLdActivityTimesList = new Vector<LdActivityTimes>();

	/** A static list of LdActivityTimes objects already created in this session.*/
	private static Vector<LdTask> oLdTaskList = new Vector<LdTask>();
	
	/**
	 * Default constructor (not used yet).
	 */
	public TaskTimesService() {
		super();
	}

	/**
	 * Constructor, set the name of this service. Note this constructor is not used yet.
	 * @param name, the name of this service
	 */
	public TaskTimesService(String name) {
		super(name);
	}

	/**
	 *	Constructor, set the name, ServiceManager and DBDatabaseManager for this service.
	 *
	 * @param String sName, the name of this service.
	 * @param ServiceManager sm, the ServiceManager used by this service.
	 * @param DBDatabaseManager dbMgr, the DBDatabaseManager used by this service.
	 */
	public TaskTimesService(String name, ServiceManager sm,
			DBDatabaseManager dbMgr) {
		super(name, sm, dbMgr);
	}
	
	/**
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
	 * @return the oLdActivityTimesList
	 */
	public static Vector<LdActivityTimes> getLdActivityTimesList() {
		return oLdActivityTimesList;
	}

	/**
	 * @param ldActivityTimesList the oLdActivityTimesList to set
	 */
	public static void setLdActivityTimesList(
			Vector<LdActivityTimes> ldActivityTimesList) {
		oLdActivityTimesList = ldActivityTimesList;
	}

	/**
	 * @return the oLdTaskList
	 */
	public static Vector<LdTask> getLdTaskList() {
		return oLdTaskList;
	}

	/**
	 * @param ldTaskList the oLdTaskList to set
	 */
	public static void setLdTaskList(Vector<LdTask> ldTaskList) {
		oLdTaskList = ldTaskList;
	}

	/**
	 * This creates entries in the CompendiumLD database for the activity time 
	 * data represented by the parameter aLdActivityTimes.
	 * @param session, the database session. The PCSession object holds the association 
	 * between the user and the open database, and it contains a unique session id to identify this association, (again, stemming from when this was the client side of the code) 
	 * @param aLdActivityTimes
	 */
	public LdActivityTimes createTaskTimes(PCSession session, LdActivityTimes aLdActivityTimes)throws SQLException	{
		DBConnection dbcon = this.getDatabaseManager().requestConnection(session.getModelName());
		/** ActivityTimesDisplayed data may need to be updated as it may have
		 * been created before  task sequence data and already be in the database. 
		 */ 
		
		// System.out.println("TaskTimesService.createTaskTimes() aLdActivityTimes: " + aLdActivityTimes.toString() );
		if (!DBActivityTimesDisplayed.hasActivityTimesDiplayed(dbcon, aLdActivityTimes.getId()))
			DBActivityTimesDisplayed.insert(dbcon, aLdActivityTimes);
		else
			DBActivityTimesDisplayed.update(dbcon, aLdActivityTimes);
		// Insert data from aLdActivityTimes into the database table  TaskSequenceActivity
		DBTaskSequenceActivity.insertAll(dbcon, aLdActivityTimes);
		/** Create a HashSet to store ids of LdTasks already inserted in the loop, to avoid 
		 * inserting same data twice which may happen if a Task is in more than one task sequence. */ 
		HashSet<String> hsTaskAlreadyInserted = new HashSet<String>();
		// Loop through task sequences and insert role, task, and task times data into the relevant tables 
		LinkedList<LdTask> oTaskList;
		LinkedHashSet<LdTaskSequence> oTaskSequenceSet = aLdActivityTimes.getTaskSequenceSet();
		/**	Debugging data		***/
	//	System.out.println("*** DATA being written to DATABASE *******************");
	//	System.out.println(aLdActivityTimes.toString());
	//	System.out.println("*** END OF  DATA  being written to DATABA SE  *******************");
		/**  End of Debugging data	***/
		for (LdTaskSequence aTaskSequence : oTaskSequenceSet)	{
			DBTaskSequenceRole.insert(dbcon, aTaskSequence);
			// Insert into TaskTimes table first to prevent violation of foreign key constraint 
			oTaskList = aTaskSequence.getTaskSequence();
			
			for (LdTask aTask : oTaskList)	{
				if (!hsTaskAlreadyInserted.contains(aTask.getId()))	{
					DBTaskTimes.insert(dbcon, aTask);
					hsTaskAlreadyInserted.add(aTask.getId());
				}
			}		
			// Now can insert into table TaskSequenceTask
			DBTaskSequenceTask.insert(dbcon, aTaskSequence);
			// Add the task sequence to the list showing those that have been created in this session
			LdTaskSequence.getLdTaskSequenceList().add(aTaskSequence);
		}
		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
		return aLdActivityTimes;
	}
	
	
	/**
	 * This clears entries in the CompendiumLD database for the activity time 
	 * data represented by the parameter aLdActivityTimes.
	 * @param session, the database session. The PCSession object holds the association 
	 * between the user and the open database, and it contains a unique session id to identify 
	 * this association, (again, stemming from when this was the client side of the code). 
	 * @param aLdActivityTimes
	 */
	public void clearTaskTimes(PCSession session, LdActivityTimes aLdActivityTimes)	throws SQLException {
		DBConnection dbcon = this.getDatabaseManager().requestConnection(session.getModelName());
		LinkedHashSet<LdTaskSequence> oTaskSequenceSet = aLdActivityTimes.getTaskSequenceSet();
		String sActivityId = aLdActivityTimes.getId();
		/**
		 * Foreign key constraints on TaskSequenceTask table mean that there
		 * must be both a TaskSequenceActivity with the referenced
		 * TaskSequenceId and TaskTimes with the referenced TaskTimesId
		 * therefore need to delete TaskSequenceTask rows before both
		 * TaskSequenceActivity and TaskTimes.
		 * 
		 * Foreign key constraints on TaskSequenceRole table mean that there
		 * must be a TaskSequenceActivity with the referenced TaskSequenceId
		 * therefore need to delete TaskSequenceRole rows before
		 * TaskSequenceActivity.
		 * 
		 * Foreign key constraints on TaskSequenceActivity table mean that there
		 * must be a ActivityTimesDisplayed row with the referenced ActivityId
		 * therefore need to delete TaskSequenceActivity rows before
		 * ActivityTimesDisplayed.
		 * 
		 * Hence order of deletion: TaskSequenceTask,  TaskTimes, TaskSequenceRole, 
		 * TaskSequenceActivity,  ActivityTimesDisplayed.
		 * Note that here is some flexibility e.g.  TaskTimes poistion could be later
		 * in the sequence.
		 */
		try	{
			LinkedList<LdTask> oTaskList;
			/** Create a HashSet to store ids of LdTasks already deleted in the loop, to avoid 
			 * inserting same data twice which may happen if a Task is in more than one task sequence. */ 
			HashSet<String> hsTaskAlreadyDeleted = new HashSet<String>();
			//1  Purge table TaskSequenceTask
			for (LdTaskSequence aTaskSequence : oTaskSequenceSet)	{
				DBTaskSequenceTask.purge(dbcon, aTaskSequence.getId());
			}
			//2  Purge TaskTimes table 				
			for (LdTaskSequence aTaskSequence : oTaskSequenceSet)	{
				oTaskList = aTaskSequence.getTaskSequence();
				for (LdTask aTask : oTaskList)	{
					if (!hsTaskAlreadyDeleted.contains(aTask.getId()))	{
					if (DBTaskTimes.purge(dbcon, aTask))
						hsTaskAlreadyDeleted.add(aTask.getId());
					}
				}
			
				// 3 Purge the Role- taskSequence relation from the TaskSequenceRole table
				DBTaskSequenceRole.purge(dbcon, aTaskSequence);
				LdTaskSequence.getLdTaskSequenceList().remove(aTaskSequence);
			}
			DBTaskSequenceActivity.purge(dbcon, sActivityId);
			DBActivityTimesDisplayed.purge(dbcon, aLdActivityTimes);
		}
		catch (SQLException ex)	{
			ProjectCompendium.APP.displayError("Exception : (TaskTimesService.clearTaskTimes) " + ex.getMessage());
			 System.out.println(ex.getStackTrace());
		}
	}
	/**
	 * This creates entries in the CompendiumLD database for the activity time 
	 * data represented by the parameter aLdActivityTimes.
	 * @param session, the database session. The PCSession object holds the association 
	 * between the user and the open database, and it contains a unique session id to identify this association, (again, stemming from when this was the client side of the code) 
	 * @param aLdActivityTimes
	 */
	public LdActivityTimes createTaskSequence(PCSession session, LdTaskSequence aTaskSequence, LdActivityTimes aLdActivityTimes)throws SQLException	{
		DBConnection dbcon = this.getDatabaseManager().requestConnection(session.getModelName());
		// Insert data from aLdActivityTimes into the database table ActivityTimesDisplayed, or update the data if it already exists
		boolean bHasTimesDisplayed = DBActivityTimesDisplayed.hasActivityTimesDiplayed(dbcon, aLdActivityTimes.getId());
		if (bHasTimesDisplayed)
			DBActivityTimesDisplayed.update(dbcon, aLdActivityTimes);
		else
			DBActivityTimesDisplayed.insert(dbcon, aLdActivityTimes); 
		// Insert data about this task sequence from aLdActivityTimes into the database table  TaskSequenceActivity
		DBTaskSequenceActivity.insert(dbcon, aTaskSequence, aLdActivityTimes);
		
		// insert role, task, and task times data into the relevant tables 
		LinkedList<LdTask> oTaskList;
		DBTaskSequenceRole.insert(dbcon, aTaskSequence);
		// Insert into TaskTimes table first to prevent violation of foreign key constraint 
		oTaskList = aTaskSequence.getTaskSequence();
		for (LdTask aTask : oTaskList)	{
			DBTaskTimes.insert(dbcon, aTask);
		}		
		// Now can insert into table TaskSequenceTask
		DBTaskSequenceTask.insert(dbcon, aTaskSequence);
		// Add the task sequence to the list showing those that have been created in this session
		LdTaskSequence.getLdTaskSequenceList().add(aTaskSequence);
		
		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);
		return aLdActivityTimes;
	}
	
	/**
	 * Convenience method to insert the LdTaskSequence oTaskSequence into the 
	 * database. This method inserts the necessary data into the TaskSequenceRole,
	 * TaskSequenceTask, and TaskTimes tables
	 * @param dbcon
	 * @param oTaskSequence
	 * @throws SQLException
	 */
	private void insertTaskSequence(DBConnection dbcon, LdTaskSequence oTaskSequence) 
			throws SQLException	{	
		LinkedList<LdTask> oTaskList;
		DBTaskSequenceRole.insert(dbcon, oTaskSequence);
		// Insert into TaskTimes table first to prevent violation of foreign key constraint 
		oTaskList = oTaskSequence.getTaskSequence();
		for (LdTask aTask : oTaskList)	{
			DBTaskTimes.insert(dbcon, aTask);
		}		
		// Now can insert into table TaskSequenceTask
		DBTaskSequenceTask.insert(dbcon, oTaskSequence);
		// Add the task sequence to the list showing those that have been created in this session
		LdTaskSequence.getLdTaskSequenceList().add(oTaskSequence);
	}
			
	/**
	 * This method updates the data stored about task sequences for a particular 
	 * activity.
	 * @param session
	 * @param aLdActivityTimes
	 * @return
	 * @throws SQLException
	 */
	public boolean updateTaskSequencesForActivity(PCSession oSession, LdActivityTimes aLdActivityTimes)throws SQLException	{
		DBConnection dbcon = this.getDatabaseManager().requestConnection(oSession.getModelName());
		String sActivityId = aLdActivityTimes.getId();
		boolean bDbModSuccess = true;
		boolean bCurrent = true;
		LdActivityTimes oUpdatedActivityTimes = null;

		// Get the set of task sequences for the actvity in question as stored in the database
		LinkedHashSet<LdTaskSequence> oDBTaskSequenceSet = this.getTaskSequencesForActivity(oSession, sActivityId);
		// Get the set of task sequences as stored in the parameter aLdActivityTimes
		LinkedHashSet<LdTaskSequence> oNewTaskSequenceSet = aLdActivityTimes.getTaskSequenceSet();
		// For every task sequence in the new list
		for (LdTaskSequence aTaskSequence : oNewTaskSequenceSet)	{
			// If it is not in the database, insert it
			if (!LdTaskSequence.listContains(oDBTaskSequenceSet, aTaskSequence))
				oUpdatedActivityTimes = DBTaskSequenceActivity.insert(dbcon, aTaskSequence, aLdActivityTimes);
			// Note: if it is in  the database, do not need to update it because each row is only a relation between the Ids
			if (oUpdatedActivityTimes == null)
				bDbModSuccess = false;	
		}
		// Purge the task sequences for this activity from the database
		// For every sequence for this activity in the database 
		for (LdTaskSequence aTaskSequence : oDBTaskSequenceSet)	{
			// If it is not in the new list, purge it
			if (!LdTaskSequence.listContains(oNewTaskSequenceSet, aTaskSequence))
				bCurrent =  DBTaskSequenceActivity.purge(dbcon, sActivityId, aTaskSequence.getId());
			if (bCurrent == false)
				bDbModSuccess = false;		;
			// If it is in  the database, do not need to update it because each row is only a relation between the Ids
		}
		return bDbModSuccess;
	}
	
	/**
	 * This method updates the data stored about task sequences for a particular 
	 * activity. If particular tasks within a sequence have been added or deleted,
	 * this are inserted or purged from the database.
	 * @param session
	 * @param aLdActivityTimes
	 * @return boolean true if database updates were successful, false otherwise
	 * @throws SQLException
	 */
	public boolean updateTasksForTaskSequence(PCSession session, LdTaskSequence oTaskSequence)throws SQLException	{
		DBConnection dbcon = this.getDatabaseManager().requestConnection(session.getModelName());
		String sTSid = oTaskSequence.getId();
		// Get all TaskIds for this task sequence that are currently stored in the db
		LinkedList<LdTask>  oOldTaskList = DBTaskSequenceTask.getTasksForTaskSequence(dbcon, sTSid, session.getUserID());
		// Get the list of LdTask objects in the new sequence
		LinkedList<LdTask>  oNewTaskList = oTaskSequence.getTaskSequence();
		// boolean to indicate if the database has been modified successfully
		boolean bDbModSuccess = true;
		boolean bCurrent = true;
		LdTask oUpdatedTask;
		
		// For every task in the new list
		for (LdTask aTask : oNewTaskList)	{
			// If the old list contains a task in the new list, update the data
//	For some reason, these statements don't work, hence need for listContains(oOldTaskList, aTask) method
//	 if (oOldTaskList.indexOf(aTask) >= 0)	{...		if (oOldTaskList.contains(aTask))	{
			if (LdTask.listContains(oOldTaskList, aTask))	{
				oUpdatedTask = DBTaskTimes.update(dbcon, aTask);				
			}
			// else, it's not in the old list, so insert the data
			else	{
				oUpdatedTask = DBTaskTimes.insert(dbcon, aTask);
			}
			if (oUpdatedTask == null)
				bDbModSuccess = false;	
			dbcon = this.getDatabaseManager().requestConnection(session.getModelName());
		}	
		
		// For every task in the old task list
		for (LdTask aTask : oOldTaskList) {	
			// If it is not in the new task, purge it
			if (!LdTask.listContains(oNewTaskList, aTask))	{
				bCurrent = DBTaskTimes.purge(dbcon, aTask);
				if (bCurrent == false)
					bDbModSuccess = false;
				dbcon = this.getDatabaseManager().requestConnection(session.getModelName());
			}
		}				
		return bDbModSuccess;
	}
	/**
	 * This updates entries in the CompendiumLD database for the activity time 
	 * data represented by the parameter aLdActivityTimes. 
	 * @param session, the database session. The PCSession object holds the association 
	 * between the user and the open database, and it contains a unique session id to identify this association, (again, stemming from when this was the client side of the code) 
	 * @param aLdActivityTimes
	 */
	public LdActivityTimes updateTaskTimes(PCSession oSession, LdActivityTimes aNewLdActivityTimes)throws SQLException	{
		DBConnection dbcon = this.getDatabaseManager().requestConnection(oSession.getModelName());
		boolean bDbModSuccess = true;
		boolean bCurrent = true;
		LdActivityTimes oUpdatedActivityTimes = null;
		String sNewActivityId = aNewLdActivityTimes.getId();
		// Get the existing data in the database (if any) that is to be replaced
		LdActivityTimes oOldLdActivityTimes = this.getLdActivityTimes(oSession, sNewActivityId);

		if (getTaskSequencesForActivity(oSession, aNewLdActivityTimes.getId()).isEmpty()){
			createTaskTimes(oSession, aNewLdActivityTimes);
		}
		else	{
			clearTaskTimes(oSession, oOldLdActivityTimes);
			createTaskTimes(oSession, aNewLdActivityTimes);
		}
		return aNewLdActivityTimes;
	}

	
	/**
	 * Checks if the database connection oConnection has timed out, and if so 
	 * reestablish it. 
	 * @param oConnection
	 * @param oSession
	 * @return a working DBConnection instance
	 */
	public DBConnection checkConnection(DBConnection oConnection, PCSession oSession){
		if (oConnection.busyTimedOut())	{
			oConnection = this.getDatabaseManager().requestConnection(oSession.getModelName());
		}
		return oConnection;
	}
	/**
	 * Returns a LdActivityTimes given its ID. If the Activity times data already
	 * exists in the database, return it. If it does not, return an empty instance
	 * of LdActivityTimes with an id equal to sActivityId.
	 * 
	 * @param oSession
	 * @param sActivityId
	 * @return
	 */
	public LdActivityTimes getLdActivityTimes(PCSession oSession, String sActivityId)	{
		//If a LdActivityTimes instance wth the required id has already been created this session, return it
		LdActivityTimes oLdActivityTimes = null;
/**		oLdActivityTimes = LdActivityTimes.getActivityFromSessionList(sActivityId);
		if (oLdActivityTimes != null)
			return oLdActivityTimes;  
		//Otherwise create one then return it **/
		String sUserID = oSession.getUserID();
		DBConnection dbcon = getDatabaseManager().requestConnection(oSession.getModelName()) ;
		try	{
			// Get the set of 'empty' task sequence objects related to this activity
			LinkedHashSet<LdTaskSequence> oLdTaskSequenceSet  = DBTaskSequenceActivity.getTaskSequencesForActivity(dbcon, sActivityId, sUserID);
			// Create a new set of task sequences to add tge rest of the task data to
			LinkedHashSet<LdTaskSequence> aLdTaskSequenceSet	= new LinkedHashSet<LdTaskSequence>();
			Iterator<LdTaskSequence> oIt = oLdTaskSequenceSet.iterator();
			LdTaskSequence oLdTaskSequence;
			String sTsId;
			LinkedList<LdTask> oLdTaskList;
			oLdActivityTimes = new LdActivityTimes(sActivityId);
			boolean bShowTime = DBActivityTimesDisplayed.getTimesDiplayed(dbcon, sActivityId, sUserID);
			// Set boolean to indicate whether time info should be shown on  activity node
			oLdActivityTimes.setShowTime(bShowTime);
			TimeUnit oTimeUnit = DBActivityTimesDisplayed.getTimeUnit(dbcon, sActivityId, sUserID);
			// Set the time units for the activity
			oLdActivityTimes.setCurrentTaskTimeUnits(oTimeUnit);
			String sRoleId;
			while (oIt.hasNext())	{
				oLdTaskSequence = oIt.next();
				sTsId = oLdTaskSequence.getId();
				sRoleId = DBTaskSequenceRole.getRoleId(dbcon, sTsId, sUserID);
				oLdTaskList = DBTaskSequenceTask.getTasksForTaskSequence(dbcon, sTsId, sUserID);
				// Set the sequence of tasks for the LdTaskSequence 
				oLdTaskSequence.setTaskSequence(oLdTaskList);
				// Set the role id for the LdTaskSequence 
				oLdTaskSequence.setRoleId(sRoleId);
				// Add the task sequence to set for the activity
				aLdTaskSequenceSet.add(oLdTaskSequence);
			}
			oLdActivityTimes.setTaskSequenceSet(aLdTaskSequenceSet);
		}
		catch (SQLException ex)	{
			ex.printStackTrace();
		}

		getDatabaseManager().releaseConnection(oSession.getModelName(),dbcon);

		
		return oLdActivityTimes;
	}
	
	/**
	 * Returns a LdTask given its ID, or null if one does not exist with the id given.
	 *
	 * @param PCSession session, the session object for the database to use.
	 * @param String sTaskID, the Task  id of the task to return.
	 * @return oTask, the LdTask object for the given task id.
	 * @exception java.sql.SQLException
	 */
	public LdTask getLdTask(PCSession session, String sTaskID) throws SQLException {

		DBConnection dbcon = getDatabaseManager().requestConnection(session.getModelName()) ;

		LdTask oTask = DBTaskTimes.getTask(dbcon, sTaskID, session.getUserID());

		getDatabaseManager().releaseConnection(session.getModelName(),dbcon);

		return oTask;
	}

	/**
	 * Create an LinkedHashSet containing LdTaskSequence instances relating to 
	 * the activity id sActivityId. If none exist in the database for the activity, 
	 * an empty  LinkedHashSet will be returned.  If there are LdTaskSequence
	 * objects for the activity, the ones  instantiated will be 'empty' apart from their sId String.
	 */
	public LinkedHashSet<LdTaskSequence>  getTaskSequencesForActivity(PCSession oSession, String sActivityId)	{
		LinkedHashSet<LdTaskSequence> oLdTaskSequenceSet = null;
		DBConnection dbcon = getDatabaseManager().requestConnection(oSession.getModelName()) ;
		String sUserID = oSession.getUserID();
		try	{
			// Get the set of 'empty' task sequence objects related to this activity
			oLdTaskSequenceSet  = DBTaskSequenceActivity.getTaskSequencesForActivity(dbcon, sActivityId, sUserID);
		}
		catch (SQLException ex)	{
			ex.printStackTrace();
		}
		return oLdTaskSequenceSet;
	}
	

}
