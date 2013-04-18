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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ConcurrentModificationException;

import com.compendium.core.CoreUtilities;
import com.compendium.core.datamodel.UserProfile;
import com.compendium.core.db.DBSystem;
import com.compendium.core.db.management.DBAdminDatabase;
import com.compendium.core.db.management.DBConnection;
import com.compendium.core.db.management.DBConnectionManager;
import com.compendium.core.db.management.DBDatabaseNameException;
import com.compendium.core.db.management.DBDatabaseTypeException;
import com.compendium.core.db.management.DBEmptyDatabase;
import com.compendium.core.db.management.DBNewDatabase;
import com.compendium.core.db.management.DBProjectListException;

public class LdDBNewDatabase extends DBNewDatabase {

	public LdDBNewDatabase(int databaseType, DBAdminDatabase admin,
			String databaseUserName, String databasePassword, String databaseIP) {
		super(databaseType, admin, databaseUserName, databasePassword,
				databaseIP);
		// TODO Auto-generated constructor stub
	}

	public LdDBNewDatabase(int databaseType, DBAdminDatabase admin,
			UserProfile up, boolean isDefaultUser, String databaseUserName,
			String databasePassword, String databaseIP) {
		super(databaseType, admin, up, isDefaultUser, databaseUserName,
				databasePassword, databaseIP);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Create a new database, and then load the default data.
	 * If a user was specified in the constructor, add the new user details to the new database.
	 *
	 * @param String sFriendlyName, the name of the database as seen by a Compendium user.
	 * This name is 'cleaned' to remove illegal characters and a time/date stamp is addded.
	 * @exception java.sql.SQLException
	 * @exception java.io.IOException
	 * @exception java.io.FileNotFoundLException
	 * @exception java.lang.ClassNotFoundException
	 * @throws DBProjectListException 
	 * @see com.compendium.core.CoreUtilities#cleanDatabaseName
	 */
	public String createNewDatabase(String sFriendlyName)
			throws DBDatabaseNameException, DBDatabaseTypeException, ClassNotFoundException, IOException, SQLException, FileNotFoundException, DBProjectListException  {

		String sHomeViewID = "";
		String sCleanName = CoreUtilities.cleanDatabaseName(sFriendlyName);

		DBEmptyDatabase empty = new LdDBEmptyDatabase(nDatabaseType, adminDatabase, sDatabaseUserName, sDatabasePassword, sDatabaseIP);
		empty.addProgressListener(this);

		Connection 	connection = null;

		empty.createEmptyDatabase(sCleanName);
		connection = DBConnectionManager.getPlainConnection(nDatabaseType, sCleanName, sDatabaseUserName, sDatabasePassword, sDatabaseIP);
		if (connection == null) {
			throw new DBDatabaseTypeException("Database type "+nDatabaseType+" not found");
		}

		fireProgressCount(DEFAULT_DATA_COUNT);
		/** File changed by Andrew now includes Code data 	**/
		insertDefaultData(connection);
		/** Added by Andrew: insert default data for learning design applications **/
		// Not needed- Code data is now in default data file
		//insertDefaultLdData(connection);
		/** End of added by Andrew	****/
		fireProgressUpdate(increment, "Finished");
		fireProgressComplete();

		adminDatabase.addNewDatabase(sFriendlyName, sCleanName);
		if (userProfile != null) {
			sHomeViewID  = insertNewUser(connection);
			if (isDefaultUser) {
				//System.out.println("About to add default user as"+userProfile.getId());
				DBSystem.setDefaultUser(new DBConnection(connection, true, nDatabaseType), userProfile.getId());
			}
		}

		try {
			connection.close();
		}
		catch(ConcurrentModificationException io) {
			System.out.println("Exception closing connection for new database:\n\n"+io.getMessage());
		}
		return sHomeViewID;
	}


}
