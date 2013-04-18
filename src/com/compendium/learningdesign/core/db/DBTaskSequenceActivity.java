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
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.LinkedHashSet;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.db.management.DBConnection;
import com.compendium.learningdesign.core.datamodel.*;
import com.compendium.learningdesign.ui.UILdTaskSequence;

/**
 * 
 * The DBTaskSequenceActivity class serves as the interface layer between the 
 * LdActivityTimes  objects and the TaskSequenceActivity table in the database.
 * 
 * @author ajb785
 */
public class DBTaskSequenceActivity {
	/** SQL statement to insert a new DBTaskSequenceActivity Record into the TaskSequenceActivity table.*/
	public final static String INSERT_TASKSEQENCEACTIVITY_QUERY =
		"INSERT INTO TaskSequenceActivity (ActivityId, TaskSequenceId) " +		
		"VALUES (?, ?) ";
	
	/** SQL statement to get a  DBTaskSequenceActivity Record with the supplied activity id from the TaskSequenceActivity table.*/
	public final static String  GET_TASKSEQUENCESFORACTIVITY_QUERY =
		"SELECT TaskSequenceId " +
		" FROM TaskSequenceActivity " +
		" WHERE ActivityId = ? ";
	
	/** SQL statement to update a  DBTaskSequenceActivity Record in the TaskSequenceActivity table.*/
	public final static String UPDATE_TASKSEQUENCEACTIVITY_QUERY =
		"UPDATE  TaskSequenceActivity " +
		"SET TaskSequenceId=? " +		
		" WHERE ActivityId = ? ";
	

	/** This query physically deletes all task sequences for the given activity id from the database.*/
	public final static String PURGE_ACTIVITYTASKSEQUENCE_QUERY =
		"DELETE "+
		"FROM TaskSequenceActivity "+
		"WHERE ActivityID = ? "; 
	
	/** This query physically deletes the task sequences for the given task sequence id from the database.*/
	public final static String PURGE_TASKSEQUENCE_QUERY =
		"DELETE "+
		"FROM TaskSequenceActivity "+
		"WHERE TaskSequenceId = ? " +
		"AND ActivityId = ?"; 
	
	/**
	 * 	Helper method to extract and build a x object from a result set item.
	 *  the ResultSet item is expected to contains the follow fields in the following order:
	 *  NodeID, Type, ExtendedNodeType, OriginalID, Author, CreationDate, ModificationDate,
	 *  Label, DEtail, LastModAuthor.
	 *
	 */
	public static LdTaskSequence processNode(DBConnection dbcon, ResultSet rs, String sUserID) throws SQLException {
		LdTaskSequence oLdTaskSequence = null;
		String	sSuppliedId			= rs.getString(1);
		oLdTaskSequence = new LdTaskSequence(sSuppliedId);
		return oLdTaskSequence;
	}
	
	/**
	 * Inserts a new TaskSequenceActivity row  in the database for ALL task 
	 * sequences in the activity.  
	 * This method may need to be updated when/if data is imported, to check if 
	 * ids match: see NodeSummary insert(...) method in DBNode class.
	 * @param dbcon
	 * @param aLdActivityTimes
	 * @throws SQLException
	 */
	public static LdActivityTimes insertAll(DBConnection dbcon, LdActivityTimes aLdActivityTimes)	throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;
			
		int nRowCount = 0;
		// Boolean which indicates if any rows have been changed by this method
		Boolean bTablesUpdated = false;
		if (con == null)
			return null;
		String sActivityId = aLdActivityTimes.getId();
		PreparedStatement pstmt = con.prepareStatement(INSERT_TASKSEQENCEACTIVITY_QUERY);
		int nUpdates = 0; 		int nRows = 0;
		LinkedHashSet<LdTaskSequence> oTaskSequenceSet = aLdActivityTimes.getTaskSequenceSet();
	
		for (LdTaskSequence aTaskSequence : oTaskSequenceSet)	{
			nRows++;
			pstmt.setString(1, sActivityId);
			String sTaskSeqId = aTaskSequence.getId();
			pstmt.setString(2, sTaskSeqId);
			nRowCount = pstmt.executeUpdate();
			if (nRowCount > 0) {
				nUpdates++;
			}
			// System.out.println("DBTaskSequenceActivity.insertAll: sActivityId =  " + sActivityId + " sTaskSeqId = " + sTaskSeqId);
		}
		pstmt.close();
		if (nUpdates == nRows)	{
			bTablesUpdated = true;
		}
		
		if (bTablesUpdated) {
			return aLdActivityTimes;
		}
		else {
			System.out.println("DBTaskSequenceActivity.insertAll ERROR - sActivityid =" + sActivityId + "\n");
			return null;
		}
	}
 
	/**
	 * Inserts a new TaskSequenceActivity row  in the database for the task 
	 * sequences aTasakSequence.  
	 * This method may need to be updated when/if data is imported, to check if 
	 * ids match: see NodeSummary insert(...) method in DBNode class.
	 * @param dbcon
	 * @param aLdActivityTimes
	 * @throws SQLException
	 */
	public static LdActivityTimes insert(DBConnection dbcon, LdTaskSequence aTaskSequence, LdActivityTimes aLdActivityTimes)	throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;
			
		int nRowCount = 0;
		// Boolean which indicates if any rows have been changed by this method
		Boolean bTablesUpdated = false;
		if (con == null)
			return null;
		String sActivityId = aLdActivityTimes.getId();
//		String sTaskSeqId = aTaskSequence.getId();
		PreparedStatement pstmt = con.prepareStatement(INSERT_TASKSEQENCEACTIVITY_QUERY);
		
		pstmt.setString(1, sActivityId);
		pstmt.setString(2, aTaskSequence.getId());
		nRowCount = pstmt.executeUpdate();
		if (nRowCount > 0)
			bTablesUpdated = true;	
		pstmt.close();
		if (bTablesUpdated) {
			return aLdActivityTimes;
		}
		else {
			return null;
		}
	}
 
	
	/**
	 * Updates a  TaskSequenceActivity row  in the database for every task 
	 * sequence in the activity.  
	 * This method may need to be updated when/if data is imported, to check if 
	 * ids match: see NodeSummary insert(...) method in DBNode class.
	 * @param dbcon
	 * @param aLdActivityTimes
	 * @throws SQLException
	 */
	public static LdActivityTimes update(DBConnection dbcon, LdActivityTimes aLdActivityTimes)	throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;
//		DBTaskSequenceActivity.getTaskSequencesForActivity(dbcon, sActivtyId, sUserID)
		int nRowCount = 0;
		// Boolean which indicates if any rows have been changed by this method
		Boolean bTablesUpdated = false;
		if (con == null)
			return null;
		String sActivityId = aLdActivityTimes.getId();
//		String sTaskSeqId = aTaskSequence.getId();
		PreparedStatement pstmt = con.prepareStatement(UPDATE_TASKSEQUENCEACTIVITY_QUERY);
		LinkedHashSet<LdTaskSequence> oTaskSequenceSet = aLdActivityTimes.getTaskSequenceSet();
		for (LdTaskSequence aTaskSequence : oTaskSequenceSet)	{
			pstmt.setString(1, aTaskSequence.getId());
			pstmt.setString(2, sActivityId);			
			nRowCount = pstmt.executeUpdate();
			if (nRowCount > 0)
				bTablesUpdated = true;
		}
	
		pstmt.close();
		if (bTablesUpdated) {
			return aLdActivityTimes;
		}
		else {
			return null;
		}
	}
 
	
	/**
	 * Updates a  TaskSequenceActivity row  in the database for the task 
	 * sequence aTaskSequence.  
	 * This method may need to be updated when/if data is imported, to check if 
	 * ids match: see NodeSummary insert(...) method in DBNode class.
	 * @param dbcon
	 * @param aLdActivityTimes
	 * @throws SQLException
	 */
	public static LdActivityTimes update(DBConnection dbcon, LdActivityTimes aLdActivityTimes, LdTaskSequence aTaskSequence)	throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;
//		DBTaskSequenceActivity.getTaskSequencesForActivity(dbcon, sActivtyId, sUserID)
		int nRowCount = 0;
		// Boolean which indicates if any rows have been changed by this method
		Boolean bTablesUpdated = false;
		if (con == null)
			return null;
		String sActivityId = aLdActivityTimes.getId();
		PreparedStatement pstmt = con.prepareStatement(UPDATE_TASKSEQUENCEACTIVITY_QUERY);
		pstmt.setString(1, aTaskSequence.getId());
		pstmt.setString(2, sActivityId);			
		nRowCount = pstmt.executeUpdate();
		if (nRowCount > 0)
			bTablesUpdated = true;
		pstmt.close();
		if (bTablesUpdated) {
			return aLdActivityTimes;
		}
		else {
			return null;
		}
	}
	
	/**
	 * 
	 * Create an LinkedHashSet containing LdTaskSequence instances relating to 
	 * the activity id sActivityId by retrieving them from the database if any exist.
	 *  These LdTaskSequence objects will be 'empty' apart from their sId String.
	 *  If none exist in the database for this activity, an empty LinkedHashSet will be 
	 *  returned.
	 * 
	 * @param dbcon
	 * @param sActivtyId
	 * @param sUserID
	 * @return
	 * @throws SQLException
	 */
	public static LinkedHashSet<LdTaskSequence> getTaskSequencesForActivity(DBConnection dbcon, 
											String sActivtyId, String sUserID) throws SQLException {
		LinkedHashSet<LdTaskSequence> aLdTaskSequenceSet = new LinkedHashSet<LdTaskSequence>();
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;
		PreparedStatement pstmt = con.prepareStatement(GET_TASKSEQUENCESFORACTIVITY_QUERY);
		pstmt.setString(1, sActivtyId);
		ResultSet rs = pstmt.executeQuery();
		LdTaskSequence oLdTaskSequence;
		try {
			if (rs != null) {
				while (rs.next()) {
					oLdTaskSequence  = (LdTaskSequence)processNode(dbcon, rs, sUserID);
					aLdTaskSequenceSet.add(oLdTaskSequence);
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close(); 
		return aLdTaskSequenceSet;
	}
	
	/**
	 *  Purges the task sequence with id  sTaskSequenceId for the activity with the given id from the database - completely removes the record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the node Summary id of the node to delete from the Node table.
	 *	@return boolean value, the success or failure of the purge operation
	 *	@exception java.sql.SQLException
	 */
	public static boolean purge(DBConnection dbcon, String sActivityId, String sTaskSequenceId) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		PreparedStatement pstmt = con.prepareStatement(PURGE_TASKSEQUENCE_QUERY);
		pstmt.setString(1, sTaskSequenceId);
		pstmt.setString(2, sActivityId);
		int nRowCount = pstmt.executeUpdate();
		pstmt.close();
		if (nRowCount > 0) 
			return true;
		else
			return false;
		
	}

	/**
	 *  Purges the task sequences  for the activity with the given id from the database - completely removes the record.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param sNodeID, the node Summary id of the node to delete from the Node table.
	 *	@return boolean value, the success or failure of the purge operation
	 *	@exception java.sql.SQLException
	 */
	public static boolean purge(DBConnection dbcon, String sActivityId) throws SQLException {

		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		
		PreparedStatement pstmt = con.prepareStatement(PURGE_ACTIVITYTASKSEQUENCE_QUERY);
		//System.out.println("DBTaskSequenceActivity.purge sActivityId = " + sActivityId);
		pstmt.setString(1, sActivityId);
		int nRowCount = pstmt.executeUpdate();
		pstmt.close();
		if (nRowCount > 0) 
			return true;
		else
			return false;
		
	}

}
