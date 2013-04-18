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

package com.compendium.learningdesign.core.db.management;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import com.compendium.core.db.management.DBAdminDatabase;
import com.compendium.core.db.management.DBEmptyDatabase;
import com.compendium.learningdesign.core.db.management.*;

public class LdDBEmptyDatabase extends DBEmptyDatabase implements LdDBConstantsDerby{
	/** This array holds all the create table sql statements for the Derby database.*/
	public static final String DERBY_CREATE_TABLES[] = {
		CREATE_ACTIVITYTIMESDISPLAYED_TABLE, CREATE_TASKSEQUENCEACTIVITY_TABLE, CREATE_TASKTIMES_TABLE,  
		CREATE_TASKSEQUENCEROLE_TABLE, CREATE_TASKSEQUENCETASK_TABLE
		
	};
	public LdDBEmptyDatabase(int databaseType, DBAdminDatabase admin,
			String databaseName, String databasePassword, String databaseIP) {
		super(databaseType, admin, databaseName, databasePassword, databaseIP);
		// Don't ned to do anything else
	}

	/**
	 * Create all the tables for the new Derby database.
	 *
	 * @param Connection con, the connection to use to create the tables.
	 */
	protected void createDerbyDatabaseTables(Connection con) throws SQLException {
		//Combine the create table arrays so that progress count reaches, instead of going over, 100%
		ArrayList<String> oCombinedCreateTables = new ArrayList<String>(DBEmptyDatabase.DERBY_CREATE_TABLES.length + LdDBEmptyDatabase.DERBY_CREATE_TABLES.length);
		oCombinedCreateTables.addAll(Arrays.asList(DBEmptyDatabase.DERBY_CREATE_TABLES));
		oCombinedCreateTables.addAll(Arrays.asList(LdDBEmptyDatabase.DERBY_CREATE_TABLES));	
		String oTemp[] = (String [])oCombinedCreateTables.toArray(new String[oCombinedCreateTables.size()]);
		createTables(con, oTemp);
		
	}
	
	/**
	 * Create all the tables for the new MySQL database.
	 *
	 * @param Connection con, the connection to use to create the tables.
	 */
	protected void createMySQLDatabaseTables(Connection con) throws SQLException {
		//createTables(con, MYSQL_CREATE_TABLES);
		createDerbyDatabaseTables(con);
	}
	
	/**
	 * Create only the learning design activity timing  tables for the new Derby database.
	 *
	 * @param Connection con, the connection to use to create the tables.
	 */
	protected void createLdDerbyDatabaseTables(Connection con) throws SQLException {
		createTables(con, LdDBEmptyDatabase.DERBY_CREATE_TABLES);
	}
}
