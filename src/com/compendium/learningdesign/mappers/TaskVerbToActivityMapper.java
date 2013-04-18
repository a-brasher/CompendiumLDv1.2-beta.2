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

package com.compendium.learningdesign.mappers;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Vector;

import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.Code;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.services.INodeService;
import com.compendium.core.db.DBSearch;
import com.compendium.learningdesign.core.ILdCoreConstants;

/**
 * This class identifies relationships between Task descriptions and Activity
 * nodes. It 
 * 1	searches the node database to find all nodes representing tasks with a similar description to the string 
 * 2	Find the activities containing those tasks
 * 3	Order by number of similar tasks contained in the activity plus similarity of activity label
 * @author ajb785
 *
 */
public class TaskVerbToActivityMapper extends LdActivityMapper {

	/**
	 * 
	 */
	public TaskVerbToActivityMapper() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param nodeSummary
	 */
	public TaskVerbToActivityMapper(NodeSummary nodeSummary) {
		super(nodeSummary);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param set
	 */
	public TaskVerbToActivityMapper(HashSet<NodeSummary> set) {
		super(set);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.compendium.ui.learningdesign.mappers.LdActivityMapper#findRelevantActivities(java.lang.String)
	 */
	@Override
	public HashSet<NodeSummary> findRelevantActivities(String sInput) {
//		INodeService  nodeService = this.getModel().getNodeService();
		// htStringToToolMap will not be needed once Ld tool type integers are stored in the db
		//Hashtable<String, Integer> htStringToToolMap = LdActivityMapper.htLD_NAMESTRING_TO_TOOL_TABLE;
		Vector<NodeSummary> vtActivityNodes = new Vector<NodeSummary>();			

		/** Find Activity nodes with sInput in the label **/
		// Set parameters for search
		// Set up the Vector of codes to be searched - in this case the activity code
		Vector<Code> vtSelectedCodes = new Vector<Code>(10);
		vtSelectedCodes.add(activityCode);
		// Set search to match any code (only one is being searched for so this should not have any effect)
		int iMatchCodesCondition = DBSearch.MATCH_ALL;
		// Set search to match all keywords (note this will only return nodes which math all, not a ranked list 
		int iMatchKeywordCondition = DBSearch.MATCH_ALL;
		// Note if need to search for keywords use UISearchDialog.parseKeywords(String keywords)
		Vector<String> vKeywords = new Vector<String>(10);
		vKeywords.add(sInput);				
		// Vector indicating whether to search on the node label and or detail. 
		Vector<String> vtAttrib	= new Vector<String>(1);
		vtAttrib.addElement("Label");
		// Dates to limit the search: do not want to limit it by date so set all to null
		java.util.Date dBeforeCreationDate, dAfterCreationDate, dBeforeModificationDate, dAfterModificationDate;
		dBeforeCreationDate = null; dAfterCreationDate = null; dBeforeModificationDate = null; dAfterModificationDate = null;
		Vector<NodeSummary> vtActivitiesWithStringInLabel = new Vector<NodeSummary>();
		String sContextCondition = DBSearch.CONTEXT_ALLVIEWS;
		String sViewId = ""; //Try this, if it does not search on all views then will have to set it explicitly
		// Activities are LDMapViews 
		Vector<String> vtSelectedNodeTypes = new Vector<String>(10);
		String sType = new Integer(ICoreConstants.LDMAPVIEW).toString();
		vtSelectedNodeTypes.addElement(sType);
		// Want to search all authors, hence do not select any
		Vector vtSelectedAuthors = new Vector();
		// End of setting parameters
		try 	{
			vtActivitiesWithStringInLabel = this.getModel().getQueryService().searchNode(this.getSession(), sContextCondition, sViewId, vtSelectedNodeTypes, vtSelectedAuthors, vtSelectedCodes, iMatchCodesCondition, vKeywords, iMatchKeywordCondition, vtAttrib, dBeforeCreationDate, dAfterCreationDate, dBeforeModificationDate, dAfterModificationDate);
			this.getOActivitiesSet().addAll(vtActivitiesWithStringInLabel);
			/** End of find Activity nodes with sInput in the label **/

			/** Find Activities containing Tasks which have sInput in the node label **/
			Vector<NodeSummary> vtActivitiesWithRelevantTasks = new Vector<NodeSummary>();
			// Get all the activity nodes 
			vtActivityNodes = this.getAllActivities();
			int nnCount = vtActivityNodes.size();

			String userID = this.getUserID();
			// Only searching the single view ie. the Activity of interest
			sContextCondition = DBSearch.CONTEXT_SINGLE_VIEW;

			// Tasks  are Reference nodes 
			vtSelectedNodeTypes.clear();
			vtSelectedNodeTypes.addElement(new Integer(ICoreConstants.REFERENCE).toString());
			// Want to search all authors, hence do not select any *** Al;ready done above **

			// Set up the Vector of codes to be searched - in this case the activity code
			vtSelectedCodes.clear();
			Code taskCode = this.getModel().getCodeService().getCodeForName(this.getSession(), ILdCoreConstants.sTASK_TAG);
			String sCodeID = taskCode.getId();
			vtSelectedCodes.add(taskCode);

			/** For each Activity, search it to see if it has any nodes with the 
			 * required Task code and label
			 * If it does, add it to the activity set.
			 */ 
			for (int nn=0; nn<nnCount; ++nn)	{
				String sActivityViewId = vtActivityNodes.elementAt(nn).getId();
				vtActivitiesWithRelevantTasks = this.getModel().getQueryService().searchNode(this.getSession(), sContextCondition, sActivityViewId, vtSelectedNodeTypes, vtSelectedAuthors, vtSelectedCodes, iMatchCodesCondition, vKeywords, iMatchKeywordCondition, vtAttrib, dBeforeCreationDate, dAfterCreationDate, dBeforeModificationDate, dAfterModificationDate);
				if (vtActivitiesWithRelevantTasks.size() > 0)	{
					this.getOActivitiesSet().add(vtActivityNodes.elementAt(nn));
				}					
			}
		}


		catch (SQLException ex) {
			ProjectCompendium.APP.displayError("Exception:" + ex.getMessage());
		}


		return this.getOActivitiesSet();
	}

}
