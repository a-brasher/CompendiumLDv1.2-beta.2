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

import java.sql.SQLException;
import java.util.LinkedHashSet;

import com.compendium.core.datamodel.PCSession;
import com.compendium.core.datamodel.services.IClientService;
import com.compendium.learningdesign.core.datamodel.LdActivityTimes;
import com.compendium.learningdesign.core.datamodel.LdTaskSequence;

/**
 * This interface ITaskTimesService specifies methods which services to manipulate
 * TaskTimes objects must implement. For example, methods to create, retrieve and
 * update database tables.
 * @author ajb785
 *
 */
public interface ITaskTimesService extends IClientService {
	/** Create task times data in the database **/
	public LdActivityTimes createTaskTimes(PCSession session, LdActivityTimes aLdActivityTimes) throws SQLException;
	
	/** Extract  task times data from the database and craete a LdActivityTimes object **/
	public LdActivityTimes getLdActivityTimes(PCSession oSession, String sActivityId);
	
	/**
	 * Create an LinkedHashSet containing LdTaskSequence instances relating to 
	 * the activity id sActivityId. If none exist for the activity, null will be 
	 * returned. If there are LdTaskSequence objects for the activity, the ones 
	 * instantiated will be 'empty' apart from their sId String.
	 */
	public LinkedHashSet<LdTaskSequence>  getTaskSequencesForActivity(PCSession oSession, String sActivityId);
	
	/**
	 * This updates entries in the CompendiumLD database for the activity time 
	 * data represented by the parameter aLdActivityTimes.
	 * @param session, the database session. The PCSession object holds the association 
	 * between the user and the open database, and it contains a unique session id to identify this association, (again, stemming from when this was the client side of the code) 
	 * @param aLdActivityTimes
	 */
	public LdActivityTimes updateTaskTimes(PCSession session, LdActivityTimes aLdActivityTimes)throws SQLException;
	
	/**
	 * This clears entries in the CompendiumLD database for the activity time 
	 * data represented by the parameter aLdActivityTimes.
	 * @param session, the database session. The PCSession object holds the association 
	 * between the user and the open database, and it contains a unique session id to identify this association, (again, stemming from when this was the client side of the code) 
	 * @param aLdActivityTimes
	 */
	public void clearTaskTimes(PCSession session, LdActivityTimes aLdActivityTimes)	throws SQLException;
}
