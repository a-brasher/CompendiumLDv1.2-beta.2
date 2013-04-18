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

package com.compendium.learningdesign.core.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import com.compendium.learningdesign.util.TimeUnit;

import com.compendium.core.db.management.DBConnection;
import com.compendium.learningdesign.core.datamodel.*;

/**
 * 
 * The DBTaskSequenceTask class serves as the interface layer between the 
 * LdTaskSequence  objects and the TaskSequenceTask table in the database.
 * 
 * @author ajb785
 */
public class DBTaskSequenceTask {
	/** SQL statement to insert a new TaskSequenceTask Record into the TaskSequenceTask table.*/
	public final static String INSERT_TASKSEQUENCETASK_QUERY =
		"INSERT INTO TaskSequenceTask (TaskSequenceId, TaskId, TaskOrder) " +		
		"VALUES (?, ?, ?) ";
	
	/** SQL statement to Task information for a particular task sequence.
	 * i.e. TaskSequenceId, TaskId, taskTime, TimeUnit, showtime. Results are 
	 * returned  in the order of the tasks in the sequence. **/
	public final static String  GET_TASKSFORTASKSEQUENCES_QUERY =
		"SELECT  TASKSEQUENCETASK.TASKSEQUENCEID, TASKSEQUENCETASK.TASKID, TASKSEQUENCETASK.TASKORDER,   " +
		"TASKTIMES.TASKTIME, TASKTIMES.TASKUNIT, TASKTIMES.SHOWTIME " +
		"FROM TASKSEQUENCETASK, TASKTIMES " +
		"WHERE TASKSEQUENCETASK.TASKSEQUENCEID = ? " +
		"AND TASKSEQUENCETASK.TASKID = TASKTIMES.TASKID " +
		"ORDER BY TASKSEQUENCETASK.TASKORDER";
	
	/** SQL statement to delete  a  TaskTimes Record from the TaskTimes table.*/
	public final static String PURGE_TASKSEQUENCE_QUERY =
		"DELETE " +
		"FROM TASKSEQUENCETASK " +		
		"WHERE TaskSequenceId = ?";
/** heres the query to be implemented 
 SELECT  TASKSEQUENCETASK.TASKSEQUENCEID, TASKSEQUENCETASK.TASKID,
  TASKTIMES.TASKTIME, TASKTIMES.TASKUNIT, TASKTIMES.SHOWTIME
  FROM TASKSEQUENCETASK, TASKTIMES
  WHERE TASKSEQUENCETASK.TASKSEQUENCEID = '13710849921228819936647'
    AND TASKSEQUENCETASK.TASKID = TASKTIMES.TASKID
 */
	
	/**
	 * Inserts a new TaskSequenceTask row  in the database for each task that 
	 * the LdTaskSequence aTaskSequence includes.  
	 * This method may need to be updated when/if data is imported, to check if 
	 * ids match: see NodeSummary insert(...) method in DBNode class.
	 * @param dbcon
	 * @param aTaskSequence
	 * @throws SQLException
	 */
	public static LdTaskSequence insert(DBConnection dbcon, LdTaskSequence aTaskSequence)	throws SQLException {
		Connection con = dbcon.getConnection();
		int nRowCount = 0;
		// Boolean which indicates if any rows have been changed by this method
		Boolean bTablesUpdated = false;
		if (con == null)
			return null;
		String sTaskSeqId = aTaskSequence.getId();
		PreparedStatement pstmt = con.prepareStatement(INSERT_TASKSEQUENCETASK_QUERY);
		LinkedList<String> sTaskIds = aTaskSequence.getTaskIds();
		//nRows represents the position of the task within the sequence
		int nUpdates = 0; 		int nRows = 0;
	//	System.out.println("DBTaskSequenceTask.insert: \n");
		for (String aTaskId : sTaskIds)	{
			nRows++;
			pstmt.setString(1, sTaskSeqId);
			pstmt.setString(2, aTaskId);
			pstmt.setInt(3, nRows);
	//		System.out.println("sTaskSeqId = " + sTaskSeqId + " aTaskId = " + aTaskId + "nRows = " + nRows + "\n");
			nRowCount = pstmt.executeUpdate();
			if (nRowCount > 0) {
				nUpdates++;
			}
		}
		
		pstmt.close();
		if (nUpdates == nRows)	{
			bTablesUpdated = true;
		}
		
		if (bTablesUpdated) {
			return aTaskSequence;
		}
		else {
			System.out.println("DBTaskSequenceTask.insert ERROR - aTaskSequence id =" + aTaskSequence.getId()+ "\n");
			return null;
		}
	}
	
	/**
	 * 	Helper method to extract and build a LdTask object from a result set item.
	 *  the ResultSet item is expected to contains the follow fields in the following order:
	 *  TasSequenceID, TaskID, TaskTime, TaskUnit, Showtime.
	 *  Note the TasksequenceId in the result set is not used (yet).
	 *
	 */
	public static LdTask processNode(DBConnection dbcon, ResultSet rs, String sUserID) throws SQLException {
		LdTask oLdTask = null;
		String	sSuppliedTaskId			= rs.getString(2);
		int iTaskOrder= rs.getInt(3);
		// Consider throwing an exception instead of just output message to log
		if (iTaskOrder ==0)	{
			System.out.println("DBTaskSequenceTask.processNode(): iTaskOrder  = " + iTaskOrder + "!!!!" );
		}
		long oTaskTime = rs.getLong(4);
		TimeUnit oTaskUnits = TimeUnit.valueOf(rs.getString(5));
		boolean bShowTime = rs.getBoolean(6);
		oLdTask = new LdTask(sSuppliedTaskId, oTaskTime, oTaskUnits, bShowTime );
		return oLdTask;
	}
	
	public static LinkedList<LdTask> getTasksForTaskSequence(DBConnection dbcon, String sTsId, String sUserID)
							throws SQLException 	{
		LinkedList<LdTask> aLdTaskList = new LinkedList<LdTask>();
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;
		PreparedStatement pstmt = con.prepareStatement(GET_TASKSFORTASKSEQUENCES_QUERY);
		pstmt.setString(1, sTsId);
		ResultSet rs = pstmt.executeQuery();
		
		LdTask oLdTask;
		try {
			if (rs != null) {
				while (rs.next()) {
					oLdTask  = (LdTask)processNode(dbcon, rs, sUserID);
					aLdTaskList.add(oLdTask);
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close(); 

		return aLdTaskList;
	}
	
	/**
	 * Delete the records identified by sTaskSequenceID from the TaskSequenceTask table.
	 * @param dbcon
	 * @param sTaskID
	 * @param userID
	 * @return
	 * @throws SQLException
	 */
	public static boolean purge(DBConnection dbcon, String sTaskSequenceID) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		// IF AUDITING, STORE NODE DATA
		

		PreparedStatement pstmt = con.prepareStatement(PURGE_TASKSEQUENCE_QUERY);
		pstmt.setString(1, sTaskSequenceID);
		int nRowCount = pstmt.executeUpdate();
		pstmt.close();

		if (nRowCount > 0) {
			return true;
		}
		else
			return false;
	}

}
