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
import java.sql.ParameterMetaData;
import java.sql.SQLWarning;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import com.compendium.learningdesign.util.TimeUnit;

import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.db.management.DBConnection;

import com.compendium.learningdesign.core.datamodel.*;

/**
 * The DBActivityTimesDisplayed class serves as the interface layer between the 
 * LdActivityTimes  objects and the ActivityTimesDisplayed table in the database.
 * @author ajb785
 *
 */
public class DBActivityTimesDisplayed {
	/** SQL statement to insert a new DBActivityTimesDisplayed Record into the DBActivityTimesDisplayed table.*/
	public final static String INSERT_DBACTIVITYTIMESDISPLAYED_QUERY =
		"INSERT INTO ActivityTimesDisplayed (ActivityID, TimesDisplayed, TimeUnit) " +		
		"VALUES (?, ?, ?) ";
	
	/** SQL statement to update a  ActivityTimesDisplayed Record in the ctivityTimesDisplayed table.*/
	// This seems to cause a problem so I've rewritten it in case there's some invisible control character in this text
	public final static String UPDATE_DBACTIVITYTIMESDISPLAYED_QUERY_ORIG =
		"UPDATE ActivityTimesDisplayed " +
		"SET TimesDisplayed=?, TimeUnit=? " +		
		"WHERE ActivityId = ? ";
	
	/** SQL statement to update a  ActivityTimesDisplayed Record in the ctivityTimesDisplayed table.*/
	public final static String UPDATE_DBACTIVITYTIMESDISPLAYED_QUERY =
		"UPDATE ActivityTimesDisplayed " +
		"SET TimesDisplayed=?, TimeUnit=? " +
		"WHERE ActivityId = ?"; 
	
	public final static String  GET_DISPLAYEDINFOFORACTIVITY_QUERY =
		"SELECT TimesDisplayed, TimeUnit " +
		" FROM ActivityTimesDisplayed " +
		" WHERE ActivityId = ? ";
	
	public final static String  GET_ACTIVITY_QUERY =
		"SELECT ActivityId, TimesDisplayed, TimeUnit " +
		" FROM ActivityTimesDisplayed " +
		" WHERE ActivityId = ? ";
	
	public final static String  PURGE_ACTIVITY_QUERY =
		"DELETE " +
		" FROM ActivityTimesDisplayed " +
		" WHERE ActivityId = ? ";


	/**
	 *  Inserts a new node in the database, creates a new NodeSummary object and returns it.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param id, the node id.
	 * 	@param type, the node type.
	 * 	@param xNodeType, the extended node type - not currently used.
	 * 	@param importedId, the current id of the node being imported.
	 *	@param sOriginalID, the original imported of this link.
	 *	@param author, the author of this link.
	 *	@param label, the label of the node.
	 *	@param detail, the first detail page of the node.
	 *	@param creationDate, the date of creation of the node.
	 *	@param modificationDate, the date of modification of the node.
	 *	@return com.compendium.core.datamode.INodeSummary, the node object.
	 *	@throws java.sql.SQLException
	 */	
 
	public static LdActivityTimes insert(DBConnection dbcon, LdActivityTimes aLdActivityTimes)
			throws SQLException  {
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;
		PreparedStatement pstmt = con.prepareStatement(INSERT_DBACTIVITYTIMESDISPLAYED_QUERY);
		pstmt.setString(1, aLdActivityTimes.getId());
		pstmt.setBoolean(2, aLdActivityTimes.getShowTime());
		pstmt.setString(3, aLdActivityTimes.getCurrentTaskTimeUnits().toString());
		
		int nRowCount = pstmt.executeUpdate();
		pstmt.close();
		if (nRowCount >0) {
			return aLdActivityTimes;
		}
		else {
			return null;
		}
	}
	
	
 
	/**
	 * Purges data from the database for the activity represented bu the
	 *  LdActivityTimes instance aLdActivityTimes.
	 * @param dbcon
	 * @param aLdActivityTimes
	 * @return
	 * @throws SQLException
	 */
	public static boolean purge(DBConnection dbcon, LdActivityTimes aLdActivityTimes)
			throws SQLException  {
		Connection con = dbcon.getConnection();
		if (con == null)
			return false;
		PreparedStatement pstmt = con.prepareStatement(PURGE_ACTIVITY_QUERY);
		pstmt.setString(1, aLdActivityTimes.getId());
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
	 *  Updates the record corresponding to the  parameter aLdActivityTimes in the
	 *   ActivityTimesDisplayed table. Returns the aLdActivityTimes instance if an 
	 *   update is made, null otherwise.
	 *
	 *	@param DBConnection dbcon com.compendium.core.db.management.DBConnection, the DBConnection object to access the database with.
	 *	@param id, the node id.
	 * 	@param type, the node type.
	 * 	@param xNodeType, the extended node type - not currently used.
	 * 	@param importedId, the current id of the node being imported.
	 *	@param sOriginalID, the original imported of this link.
	 *	@param author, the author of this link.
	 *	@param label, the label of the node.
	 *	@param detail, the first detail page of the node.
	 *	@param creationDate, the date of creation of the node.
	 *	@param modificationDate, the date of modification of the node.
	 *	@return com.compendium.core.datamode.INodeSummary, the node object.
	 *	@throws java.sql.SQLException
	 */	
 
	public static LdActivityTimes update(DBConnection dbcon, LdActivityTimes aLdActivityTimes)
			throws SQLException  {
		boolean bShowTime = aLdActivityTimes.getShowTime();
		String sTimeUnits = aLdActivityTimes.getCurrentTaskTimeUnits().toString();
		String sId = aLdActivityTimes.getId();
		Connection con = dbcon.getConnection();
		if (con == null)
			return null;
		SQLWarning oConWarning = con.getWarnings();
		PreparedStatement pstmt = con.prepareStatement(UPDATE_DBACTIVITYTIMESDISPLAYED_QUERY);
		// Test connection
		Connection conTemp = pstmt.getConnection();
		ParameterMetaData oSQLMetadata = pstmt.getParameterMetaData();
		SQLWarning oWarning = pstmt.getWarnings();
		pstmt.setBoolean(1, aLdActivityTimes.getShowTime());
		pstmt.setString(2, aLdActivityTimes.getCurrentTaskTimeUnits().toString());
		pstmt.setString(3, aLdActivityTimes.getId());
		
		int nRowCount = pstmt.executeUpdate();
		pstmt.close();
		if (nRowCount >0) {
			return aLdActivityTimes;
		}
		else {
			return null;
		}
	}
/**	DELETE OR EDIT ??
	public static int update(DBConnection dbcon, String sActivityId, String sTimeUnits, boolean bShowTime )
	throws SQLException  {



		Connection con = dbcon.getConnection();
		if (con == null)
			return null;
		SQLWarning oConWarning = con.getWarnings();
		PreparedStatement pstmt = con.prepareStatement(UPDATE_DBACTIVITYTIMESDISPLAYED_QUERY);
		// Test connection
		Connection conTemp = pstmt.getConnection();
		ParameterMetaData oSQLMetadata = pstmt.getParameterMetaData();
		SQLWarning oWarning = pstmt.getWarnings();
		pstmt.setBoolean(1, bShowTime);
		pstmt.setString(2, sTimeUnits);
		pstmt.setString(3, sActivityId);

		int nRowCount = pstmt.executeUpdate();
		pstmt.close();
		if (nRowCount >0) {
			return nRowCount;
		}
		else {
			return null;
		}
	}
**/
	
	/**
	 * Get the time unit value which indicates the units to be used for display 
	 * of time values for the activity id sActivityId. 
	 * 
	 * @param dbcon
	 * @param sActivityId
	 * @param sUserID
	 * @return
	 * @throws SQLException
	 */
	public static TimeUnit getTimeUnit(DBConnection dbcon, 
											String sActivityId, String sUserID) throws SQLException {
		LinkedHashSet<LdTaskSequence> aLdTaskSequenceSet = new LinkedHashSet<LdTaskSequence>();
		Connection con = dbcon.getConnection();
		// Default to displaying the times in hours if can not connect to the database
		if (con == null)
			return TimeUnit.HOURS;
		PreparedStatement pstmt = con.prepareStatement(GET_DISPLAYEDINFOFORACTIVITY_QUERY);
		pstmt.setString(1, sActivityId);
		ResultSet rs = pstmt.executeQuery();
		TimeUnit oTimeUnit = TimeUnit.HOURS;
		try {
			if (rs != null) {
				while (rs.next()) {
					oTimeUnit = TimeUnit.valueOf(rs.getString(2));
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close(); 
		return oTimeUnit;
	}

	/**
	 * Get the boolean value which indicates if times should be displayed for 
	 * the activity id sActivityId. If there is no value in the database (which will 
	 * happen for newly created activities) a value of false will be returned, 
	 * unless no connection to the database can be established, in which case a 
	 * value of true will be returned.
	 * 
	 * @param dbcon
	 * @param sActivityId
	 * @param sUserID
	 * @return
	 * @throws SQLException
	 */
	public static boolean getTimesDiplayed(DBConnection dbcon, 
											String sActivityId, String sUserID) throws SQLException {
		LinkedHashSet<LdTaskSequence> aLdTaskSequenceSet = new LinkedHashSet<LdTaskSequence>();
		Connection con = dbcon.getConnection();
		// Default to displaying the times if can not connect to the database
		if (con == null)
			return true;
		PreparedStatement pstmt = con.prepareStatement(GET_DISPLAYEDINFOFORACTIVITY_QUERY);
		pstmt.setString(1, sActivityId);
		ResultSet rs = pstmt.executeQuery();
		boolean bIsTimeDislayed = false;
		try {
			if (rs != null) {
				while (rs.next()) {
					bIsTimeDislayed = rs.getBoolean(1);
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close(); 
		return bIsTimeDislayed;
	}
	
	/**
	 * Return true if there is an entry in the ActivityTimesDisplayed table for
	 * the activity id sActivityId. 
	 * 
	 * @param dbcon
	 * @param sActivityId
	 * @param sUserID
	 * @return
	 * @throws SQLException
	 */
	public static boolean hasActivityTimesDiplayed(DBConnection dbcon, 
											String sActivityId) throws SQLException {
		Connection con = dbcon.getConnection();
		// Default to false if can not connect to the database
		if (con == null)
			return false;
		PreparedStatement pstmt = con.prepareStatement(GET_ACTIVITY_QUERY );
		pstmt.setString(1, sActivityId);
		ResultSet rs = pstmt.executeQuery();
		String sDbActivityId = "";
		try {
			if (rs != null) {
				while (rs.next()) {
					sDbActivityId = rs.getString(1);
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		pstmt.close(); 
		
		if (sDbActivityId.equals(sActivityId))
			return true;
		else
			return false;
	}
}

