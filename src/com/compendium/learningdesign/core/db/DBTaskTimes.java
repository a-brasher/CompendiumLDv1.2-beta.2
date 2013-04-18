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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.compendium.learningdesign.util.TimeUnit;

import com.compendium.core.db.management.*;
import com.compendium.learningdesign.core.datamodel.*;
/**
 * The DBTaskTimes class serves as the interface layer between the TaskTimes Services objects
 * and the Node table in the database.
 *
 * @author Andrew Brasher
 */
public class DBTaskTimes {
	/** SQL statement to insert a new TaskTimes Record into the TaskTimes table.*/
	public final static String INSERT_TASKTIMES_QUERY =
		"INSERT INTO TaskTimes (TaskID, TaskTime, TaskUnit, ShowTime) " +		
		"VALUES (?, ?, ?, ?) ";

	/** SQL statement to update a TaskTimes Record selected by the TaskId in the TaskTimes table.*/
	public final static String UPDATE_TASKTIMES_QUERY =
		"UPDATE TaskTimes " +
		"SET  TaskTime=?, TaskUnit=?, ShowTime=? " +		
		"WHERE TaskID = ? ";
	
	/** SQL statement to get  a  TaskTimes Record from the TaskTimes table.*/
	public final static String GET_TASKTIMES_QUERY =
		"SELECT TaskID, TaskTime, TaskUnit, ShowTime" +
		"FROM TaskTimes " +		
		"WHERE TaskID = ?";
	
	/** SQL statement to delete  a  TaskTimes Record from the TaskTimes table.*/
	public final static String PURGE_TASKTIMES_QUERY =
		"DELETE " +
		"FROM TaskTimes " +		
		"WHERE TaskID = ?";
	
	
	public DBTaskTimes() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Inserts a new TaskTimes row  in the database.  
	 * This method may need to be updated when/if data is imported, to check if 
	 * ids match: see NodeSummary insert(...) method in DBNode class.
	 * @param dbcon
	 * @param aTask
	 * @return the task object inserted, or null if the insert fails 
	 * @throws SQLException
	 */
	public static LdTask insert(DBConnection dbcon, LdTask aTask)	throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;
	/**	short iShowTime = 0;
		if (aTask.getShowTime())	{
			iShowTime = 1;
		}`**/
		PreparedStatement pstmt = con.prepareStatement(INSERT_TASKTIMES_QUERY);
		pstmt.setString(1, aTask.getNodeSummaryTaskId());
		pstmt.setLong(2, aTask.getTaskTime());
		pstmt.setString(3, aTask.getCurrentTaskTimeUnits().toString());
		pstmt.setBoolean(4, aTask.getShowTime());
		//pstmt.setShort(4, iShowTime);
		/* System.out.println("DBTaskTimes.insert: sId = " + aTask.getNodeSummaryTaskId() + " TaskTime = " + aTask.getTaskTime()
			+ " TimeUnits = " + aTask.getCurrentTaskTimeUnits().toString() + " showTime = " + aTask.getShowTime() + "\n");	**/
		int nRowCount = pstmt.executeUpdate();
		pstmt.close();
		if (nRowCount >0) {
			return aTask;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Update a  TaskTimes row  in the database. The row is selected by the TaskId
	 * of the LdTask passed as a parameter.  
	 * This method may need to be updated when/if data is imported, to check if 
	 * ids match: see NodeSummary insert(...) method in DBNode class.
	 * @param dbcon
	 * @param aTask
	 * @throws SQLException
	 */
	public static LdTask update(DBConnection dbcon, LdTask aTask)	throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;
	/**	short iShowTime = 0;
		if (aTask.getShowTime())	{
			iShowTime = 1;
		}`**/
		PreparedStatement pstmt = con.prepareStatement(UPDATE_TASKTIMES_QUERY);
		
		pstmt.setLong(1, aTask.getTaskTime());
		pstmt.setString(2, aTask.getCurrentTaskTimeUnits().toString());
		pstmt.setBoolean(3, aTask.getShowTime());
		pstmt.setString(4, aTask.getNodeSummaryTaskId());
		
		int nRowCount = pstmt.executeUpdate();
		pstmt.close();
		if (nRowCount >0) {
			return aTask;
		}
		else {
			return null;
		}
		}
		
		/**
		 * Get the data from TaskTimes row  in the database. The row is selected by the TaskId
		 * of the LdTask passed as a parameter.  
		 * This method may need to be updated when/if data is imported, to check if 
		 * ids match: see NodeSummary insert(...) method in DBNode class.
		 * @param dbcon
		 * @param aTask
		 * @throws SQLException
		 */
		public static LdTask getTask(DBConnection dbcon, String sTaskId, String sUserID)	throws SQLException {
			Connection con = dbcon.getConnection();
			LdTask oTask = null;
			if (con == null)
				return oTask;
			PreparedStatement pstmt = con.prepareStatement(GET_TASKTIMES_QUERY);
			pstmt.setString(1, sTaskId);
			ResultSet rs = pstmt.executeQuery();
			try {
				if (rs != null) {
					while (rs.next()) {
						oTask  = processNode(dbcon, rs, sUserID);
					}
				}
			} catch (Exception e){
				e.printStackTrace();
			}
			pstmt.close(); 
			return oTask;
	}

		/**
		 * 	Helper method to extract and build a x object from a result set item.
		 *  the ResultSet item is expected to contains the follow fields in the following order:
		 *  NodeID, Type, ExtendedNodeType, OriginalID, Author, CreationDate, ModificationDate,
		 *  Label, DEtail, LastModAuthor.
		 *
		 */
		public static LdTask processNode(DBConnection dbcon, ResultSet rs, String sUserID) throws SQLException {
			LdTask oLdTask = null;
			
			String sId;
			Long oTaskTime;
			TimeUnit oTaskTimeUnits;
			boolean bShowTime;
			
			sId = rs.getString(1);
			oTaskTime = rs.getLong(2);
			oTaskTimeUnits = TimeUnit.valueOf(rs.getString(3));
			bShowTime = rs.getBoolean(4);
			oLdTask = new LdTask(sId, oTaskTime,
					oTaskTimeUnits, bShowTime);
			return oLdTask;
		}
		
		/**
		 * Delete the record identified by sTaskId from the TaskTimes table.
		 * @param dbcon
		 * @param sTaskID
		 * @param userID
		 * @return true if the deletion is successful, false otherwise
		 * @throws SQLException
		 */
		public static boolean purge(DBConnection dbcon, LdTask oTask) throws SQLException {
			String sTaskId = oTask.getId();
			return DBTaskTimes.purge(dbcon, sTaskId);
		}
		
		/**
		 * Delete the record identified by sTaskId from the TaskTimes table.
		 * @param dbcon
		 * @param sTaskID
		 * @param userID
		 * @return true if the deletion is successful, false otherwise
		 * @throws SQLException
		 */
		public static boolean purge(DBConnection dbcon, String sTaskID) throws SQLException {

			Connection con = dbcon.getConnection();
			if (con == null)
				return false;

			// IF AUDITING, STORE NODE DATA
			

			PreparedStatement pstmt = con.prepareStatement(PURGE_TASKTIMES_QUERY);
			pstmt.setString(1, sTaskID);
			int nRowCount = pstmt.executeUpdate();
			pstmt.close();

			if (nRowCount > 0) {
				return true;
			}
			else
				return false;
		}

}
