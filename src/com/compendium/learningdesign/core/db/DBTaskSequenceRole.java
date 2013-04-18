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
import java.util.LinkedHashSet;
import java.util.concurrent.TimeUnit;

import com.compendium.core.db.management.DBConnection;
import com.compendium.learningdesign.core.datamodel.LdTaskSequence;

/**
 * 
 * The DBTaskSequenceRole class serves as the interface layer between the 
 * LdTaskSequence  objects and the TaskSequenceRole table in the database.
 * 
 * @author ajb785
 */
public class DBTaskSequenceRole {
	/** SQL statement to insert a new TaskSequenceRole Record into the TaskSequenceRole table.*/
	public final static String INSERT_TASKSEQUENCEROLE_QUERY =
		"INSERT INTO TaskSequenceRole (TaskSequenceId, RoleId, TotalTimeforRole) " +		
		"VALUES (?, ?, ?) ";
	
	public final static String  GET_ROLEFORTASKSEQUENCE_QUERY =
		"SELECT RoleId, TotalTimeforRole " +
		" FROM TaskSequenceRole " +
		" WHERE TaskSequenceId = ? ";
	
	/** SQL statement to update  the record selected by the TasskSequenceId in the TaskSequenceRole table.*/
	public final static String UPDATE_ROLEFORTASKSEQUENCE_QUERY =
		"UPDATE TaskSequenceRole " +
		"SET RoleId=?, TotalTimeforRole=? " +		
		"WHERE TaskSequenceId = ? ";
	
	/** SQL statement to purge  the record selected by the TasskSequenceId in the TaskSequenceRole table.*/
	public final static String PURGE_ROLETASKSEQUENCE_QUERY =
		"DELETE FRom TaskSequenceRole " +	
		"WHERE TaskSequenceId = ? ";
	
	/**
	 * Inserts a new TaskSequenceRole row  in the database.  
	 * This method may need to be updated when/if data is imported, to check if 
	 * ids match: see NodeSummary insert(...) method in DBNode class.
	 * @param dbcon
	 * @param aTask
	 * @throws SQLException
	 */
	public static LdTaskSequence insert(DBConnection dbcon, LdTaskSequence aTaskSequence)	throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(INSERT_TASKSEQUENCEROLE_QUERY);
		pstmt.setString(1, aTaskSequence.getId());
		pstmt.setString(2, aTaskSequence.getRoleId());
		pstmt.setLong(3, aTaskSequence.getTaskSequenceTime());
		//System.out.println("DBTaskSequenceRole.insert: TsId = " + aTaskSequence.getId() + " RoledId = " + aTaskSequence.getRoleId() + " TsTime = " + aTaskSequence.getTaskSequenceTime());
		//pstmt.setShort(4, iShowTime);
		
		int nRowCount = pstmt.executeUpdate();
		pstmt.close();
		if (nRowCount >0) {
			return aTaskSequence;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Purges a  TaskSequenceRole row  from the database.  
	 * @param dbcon
	 * @param aTaskSequence
	 * @return true if the row is purged from the database, false otherwise
	 * @throws SQLException
	 */
	public static boolean purge(DBConnection dbcon, LdTaskSequence aTaskSequence)	throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return false;

		PreparedStatement pstmt = con.prepareStatement(PURGE_ROLETASKSEQUENCE_QUERY);
		pstmt.setString(1, aTaskSequence.getId());
		//pstmt.setShort(4, iShowTime);
		
		int nRowCount = pstmt.executeUpdate();
		pstmt.close();
		if (nRowCount >0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Inserts a new TaskSequenceRole row  in the database.  
	 * This method may need to be updated when/if data is imported, to check if 
	 * ids match: see NodeSummary insert(...) method in DBNode class.
	 * @param dbcon
	 * @param aTask
	 * @throws SQLException
	 */
	public static LdTaskSequence update(DBConnection dbcon, LdTaskSequence aTaskSequence)	throws SQLException {
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;

		PreparedStatement pstmt = con.prepareStatement(UPDATE_ROLEFORTASKSEQUENCE_QUERY);
		pstmt.setString(1, aTaskSequence.getRoleId());
		pstmt.setLong(2, aTaskSequence.getTaskSequenceTime());
		pstmt.setString(3, aTaskSequence.getId());
		
		int nRowCount = pstmt.executeUpdate();
		pstmt.close();
		if (nRowCount >0) {
			return aTaskSequence;
		}
		else {
			return null;
		}
	}
	
	
	/**
	 * Get the role Id for the task sequence with id sTaskSequenceId. 
	 * 
	 * @param dbcon
	 * @param sTaskSequenceId
	 * @param sUserID
	 * @return
	 * @throws SQLException
	 */
	public static String getRoleId(DBConnection dbcon, 
											String sTaskSequenceId, String sUserID) throws SQLException {
		Connection con = dbcon.getConnection();
		String sId = null;
		// Default to displaying the times in hours if can not connect to the database
		if (con == null)
			return sId;
		PreparedStatement pstmt = con.prepareStatement(GET_ROLEFORTASKSEQUENCE_QUERY);
		pstmt.setString(1, sTaskSequenceId);
		ResultSet rs = pstmt.executeQuery();
		try {
			if (rs != null) {
				while (rs.next()) {
					sId = rs.getString(1);
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close(); 
		return sId;
	}
}
