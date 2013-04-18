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

package com.compendium.learningdesign.core.datamodel.services;

import java.util.*;

import com.compendium.core.datamodel.IModel;
import com.compendium.core.datamodel.Model;
import com.compendium.core.datamodel.PCSession;
import com.compendium.core.datamodel.UserProfile;
import com.compendium.core.datamodel.services.IService;
import com.compendium.core.datamodel.services.IViewService;
import com.compendium.core.datamodel.services.LinkService;
import com.compendium.core.datamodel.services.ServiceCache;
import com.compendium.core.datamodel.services.ServiceManager;
import com.compendium.core.db.management.*;

/**
*	The LdServiceManager class manages the Services which are used to access 
*	and talk to the database. The LdServiceManager class extends the 
*	ServiceManager class by adding extra services to deal with the learning
*	design tables.
**/
public class LdServiceManager extends ServiceManager implements ILdServiceManager {
	/** The <code>ServiceCache</code> instance holding <code>NodeService</code> objects and their LOAD counts.*/
	private static ServiceCache oTaskTimesServiceCache = new ServiceCache(ServiceManager.getSERVICELOAD());

	/** int representing a TaskTimesService.*/
	private static final int TASKTIMESSERVICE				= 17;
	
	/** The current load count on the <code>TaskTimesService</code> object.*/
	private static int taskTimesCount 				= 0;
	
	public LdServiceManager(int databaseType) {
		super(databaseType);
		// TODO Auto-generated constructor stub
	}

	public LdServiceManager(int databaseType, String userName, String password) {
		super(databaseType, userName, password);
		// TODO Auto-generated constructor stub
	}

	public LdServiceManager(int databaseType, String userName, String password,
			String databaseIP) {
		super(databaseType, userName, password, databaseIP);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Called by the register method for a client to get his model object set for a new project.
	 * Create an instance of each service for use by the Model.
	 * @param session com.compendium.datamodel.PCSession, the Sesion object for the current database session.
	 * @param up com.compendium.datamodel.UserProfile, the user registering.
	 */
	protected Model createModel(PCSession session, UserProfile up) {
		Model model = super.createModel(session, up);
		
		TaskTimesService taskTimesService =  (TaskTimesService)getTaskTimesService();
		taskTimesService.addSession(session);
		model.setTaskTimesService(taskTimesService);
		return model;
	}
	
	/**
	 * Look for a free task times service in the relevant cache, else create a new service, and return it.
	 * @return com.compendium.core.datamodel.services.INodeService.
	 */
	
	public ITaskTimesService getTaskTimesService() {
		String sName = "";
		TaskTimesService oTaskTimesService = null;
		DBDatabaseManager oThisDbMgr = this.getDatabaseManager();
		// look for a free task times service, else create new service
		if(oTaskTimesServiceCache.isEmpty()) {
			sName = generateLdServiceName(TASKTIMESSERVICE);
			oTaskTimesService = new TaskTimesService(sName, this, oThisDbMgr) ;
			oTaskTimesServiceCache.put(sName, oTaskTimesService, new Integer(1));
		}
		else	{
			// check for a task times service that can support a new client
			Vector v = oTaskTimesServiceCache.getLowestCount() ;
			if (v != null ) {
				int count = ((Integer)v.elementAt(2)).intValue() ;
				count++ ;
				oTaskTimesService = (TaskTimesService)v.elementAt(1) ;
				oTaskTimesServiceCache.put(oTaskTimesService.getServiceName(), oTaskTimesService, new Integer(count));
				return (TaskTimesService)v.elementAt(1) ;
			}
			// no free services available, so create new one
			sName = generateLdServiceName(TASKTIMESSERVICE);
			oTaskTimesService = new TaskTimesService(sName, this, oThisDbMgr) ;
			oTaskTimesServiceCache.put(sName,oTaskTimesService,new Integer(1));

		}
		return oTaskTimesService;
	}
	
	/**
	 * Generate a new unique service name for the given service type.
	 *
	 * @param serviceType, the type of service to generate the new name for.
	 * @return java.lang.String, the new unqiue name for the given service.
	 */
	private String generateLdServiceName(int serviceType) {
		String name = "";
		switch (serviceType) {
		case TASKTIMESSERVICE:
			name = "taskTimesService_" + ++taskTimesCount;
			break;
		}
		return name;
		
	}

	/**
	 * Recreate the ServiceCache objects, and zero out the service load counts.
	 */
	public void cleanUp() {
		super.cleanUp();
		oTaskTimesServiceCache = new ServiceCache(ServiceManager.getSERVICELOAD());
		taskTimesCount 	= 0;
	}

	
	/**
	 * Clean up the Service instances when the application is closed.
	 *
	 * @param sessionId, the id of the session running when the application was closed.
	 * @param sUserID, the id of the user whose was logged in when the application was closed.
	 */
	public synchronized void cleanupServices(String sessionId, String sUserID) {
		//	IModel model = (IModel)htModels.get(sessionId);
		super.cleanupServices(sessionId, sUserID);
		IModel model = this.getModel(sessionId);

		try {
			/////////////// remove the assigned Task times  service
			ITaskTimesService oTaskTimesService = (ITaskTimesService)model.getTaskTimesService();
			if (oTaskTimesService != null) {
				oTaskTimesServiceCache.remove((ITaskTimesService)oTaskTimesService);
			}		
		}
		catch(Exception ex) {
			System.out.println("Exception trying to clearup services");
		}
		//remove all the dbconnections if the user was the last one 'using' the db connections
		int modelCount = 0;
		for(Enumeration e = this.getHtModels().elements();e.hasMoreElements();) {
			Model m = (Model)e.nextElement();
			if(model.getModelName().equals(m.getModelName())) {
				modelCount++;
			}
		}

		//CLOSING ALL CONNECTIONS WAS CAUSING PROBLEMS.
		//AS THIS IS NOW ONLY CALLED WHEN APPLICATION EXITED (not client-server anymore)
		//LET THEM JUST BE GARBAGE COLLECTED
		//if(modelCount < 2) {
		//	oDbMgr.removeAllConnections(model.getModelName());
		//}

		//remove the model from the hashtable
		this.getHtModels().remove(model.getSession().getSessionID());

		//print the service status
		//printServicesStatus();
	}

}
