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

/**
 * The com.compendium.ui.learningdesign.mappers package contains classes which
 * instantiate relationships (or map) between nodes of different types.
 */
package com.compendium.learningdesign.mappers;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.sql.SQLException;

import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.*;
import com.compendium.ui.*;
import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.INodeService;
import com.compendium.core.datamodel.services.*;
import com.compendium.core.db.DBSearch;
import com.compendium.learningdesign.core.ILdCoreConstants;

/**
 * The 
 * The ToolsToActivitesMapper class finds activities containing tools of a
 * specified type.
 * @author ajb785
 *
 */
/**
 * @author ajb785
 *
 */
public class ToolsToActivitesMapper extends LdActivityMapper {
	/** The Code object which is assigned to learning design activity nodes **/
	
	
	/**
	 * 
	 */
	public ToolsToActivitesMapper(NodeSummary oNodeSummary) {
		super(oNodeSummary);
		initialise();
	}

	/**
	 * 
	 */
	public ToolsToActivitesMapper() {
		super();	
		initialise();
	}
	
	/**
	 * @param set
	 */
	public ToolsToActivitesMapper(HashSet<NodeSummary> set) {
		super(set);
		initialise();
	}
	/**
	 * Set the instance variables.
	 */
	private void initialise()	{
		// Get the code object associated with the LD activity tag
		try	{
			activityCode = this.getModel().getCodeService().getCodeForName(this.getSession(), ILdCoreConstants.sACTIVITY_TAG);
			}
			catch (SQLException ex) {
				System.out.println("Exception initialising  ToolsToActivitesMapper \n\n"+ex.getMessage());
			}
	}
	/**
	 * Find the relevant activities, i.e the activities containing tools  of the type that the node of interest
	 * is (i.e. this.getNode().geLdToolType()). Functionality to refine the HashSet found by using 
	 * the String sInput has not yet been implemented.
	 * sInput.
	 * @return a HashSet containing the relevant activity nodes 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public HashSet<NodeSummary> findRelevantActivities(String sInput) {
		INodeService  nodeService = this.getModel().getNodeService();
		// htStringToToolMap will not be needed once Ld tool type integers are stored in the db
		//Hashtable<String, Integer> htStringToToolMap = LdActivityMapper.htLD_NAMESTRING_TO_TOOL_TABLE;
		Vector<NodeSummary> vtActivityNodes = new Vector<NodeSummary>();
		// Get the tool type of the node that this help is being generated for 
		int iThisToolType= this.getNode().getLdToolType();						
		// Find all the tool nodes of the type iThisToolType
		if(findToolNodesofType(iThisToolType))	{				
			// Get all the activity nodes 
			vtActivityNodes = this.getAllActivities();
//			vtActivityNodes = nodes;
			int nnCount = vtActivityNodes.size();
			// Set vtToolNodes = the set of tools nodes of type i
			Vector<NodeSummary> vtToolNodes = this.getVtLdNodes();				
			int iCount = vtToolNodes.size();

			// Set parameters for search
			String userID = this.getUserID();
			// Only searching the single view ie. the Activity of interest
			String	sContextCondition = DBSearch.CONTEXT_SINGLE_VIEW;
			Vector<String> vtSelectedNodeTypes = new Vector(10);
			// Tools  are Reference nodes 
			vtSelectedNodeTypes.addElement(new Integer(ICoreConstants.REFERENCE).toString());
			// Want to search all authors, hence do not select any
			Vector vtSelectedAuthors = new Vector();
			// Set up the Vector of codes to be searched - in this case the activity code
			Vector<Code> vtSelectedCodes = new Vector<Code>(10);
			String sToolTag = ProjectCompendium.APP.getLdTypeTagMaps().getTypestoTagsTable().get(iThisToolType);
			try 	{
				Code toolCode = this.getModel().getCodeService().getCodeForName(this.getSession(), ILdCoreConstants.sVLE_TOOL_TAG);
				Code subToolCode = this.getModel().getCodeService().getCodeForName(this.getSession(), sToolTag);
				String sCodeID = toolCode.getId();
				vtSelectedCodes.add(toolCode);
				vtSelectedCodes.add(subToolCode);
				//vtSelectedCodes.add(activityCode);
				// Set search to match any code (only one is being searched for so this should not have any effect)
				int iMatchCodesCondition = DBSearch.MATCH_ALL;
				// Set search to match any keyword (but none are being searched for so this should not have any effect)
				int iMatchKeywordCondition = DBSearch.MATCH_ANY;
				// Note if need to search for keywords use UISearchDialog.parseKeywords(String keywords)
				Vector<String> vKeywords = new Vector<String>(10);
				// Vector indicating whether to search on the node label and or detail. Do not need to specify.
				Vector<String> vtAttrib	= new Vector<String>(2);
				// Dates to limit the search: do not want to limit it by date so set all to null
				java.util.Date dBeforeCreationDate, dAfterCreationDate, dBeforeModificationDate, dAfterModificationDate;
				dBeforeCreationDate = null; dAfterCreationDate = null; dBeforeModificationDate = null; dAfterModificationDate = null;
				Vector<NodeSummary> vtToolsInActivity = new Vector<NodeSummary>(); 
				/** For each Activity, search it to see if it has any nodes with the required tool codes
				 * If it does, add it to the activity set.
				 */ 
				for (int nn=0; nn<nnCount; ++nn)	{
					String sActivityViewId = vtActivityNodes.elementAt(nn).getId();
					vtToolsInActivity = this.getModel().getQueryService().searchNode(this.getSession(), sContextCondition, sActivityViewId, vtSelectedNodeTypes, vtSelectedAuthors, vtSelectedCodes, iMatchCodesCondition, vKeywords, iMatchKeywordCondition, vtAttrib, dBeforeCreationDate, dAfterCreationDate, dBeforeModificationDate, dAfterModificationDate);
					if (vtToolsInActivity.size() > 0)	{
						this.getOActivitiesSet().add(vtActivityNodes.elementAt(nn));
					}					
				}
			}
			

			catch (SQLException ex) {
				ProjectCompendium.APP.displayError("Exception:" + ex.getMessage());
			}
		}

		return this.getOActivitiesSet();
	}

	/**
	 * Find the relevant activities, i.e the activities containing tools  of the type that the node of interest
	 * is (i.e. this.getNode().geLdToolType()). Functionality to refine the HashSet found by using 
	 * the String sInput has not yet been implemented.
	 * sInput.
	 * @return a HashSet containing the relevant activity nodes 
	 */
//	@SuppressWarnings("unchecked")
//	@Override
	public HashSet<NodeSummary> findRelevantActivities_orig(String sInput) {
			INodeService  nodeService = this.getModel().getNodeService();
			// htStringToToolMap will not be needed once Ld tool type integers are stored in the db
			//Hashtable<String, Integer> htStringToToolMap = LdActivityMapper.htLD_NAMESTRING_TO_TOOL_TABLE;
			Vector<NodeSummary> vtActivityNodes = new Vector<NodeSummary>();
			// Get the tool type of the node that this help is being generated for 
			int iThisToolType= this.getNode().getLdToolType();						
			// Find all the tool nodes of the type iThisToolType
			if(findToolNodesofType(iThisToolType))	{				
				// Get all the activity nodes 
				vtActivityNodes = this.findAllActivities();
//				vtActivityNodes = nodes;
				int nnCount = vtActivityNodes.size();
				// Set vtToolNodes = the set of tools nodes of type i
				Vector<NodeSummary> vtToolNodes = this.getVtLdNodes();				
				int iCount = vtToolNodes.size();
				// Get data for search
				String sContextCondition = DBSearch.CONTEXT_ALLVIEWS;
				String sViewId = ""; //Try this, if it does not search on all views then will have to set it explicitly
				
				// For all the tool nodes of type i
				for (int n=0; n<iCount; ++n)	{
					String sLabel = vtToolNodes.elementAt(n).getLabel();
					try {											
						for (int nn=0; nn<nnCount; ++nn)	{
							
							// for each activity node, find it's children
							Enumeration<NodeSummary> activityChildnodes = nodeService.getChildNodes(this.getSession(), vtActivityNodes.elementAt(nn).getId());
							// for each child of each activity node
							for(Enumeration<NodeSummary> e = activityChildnodes;activityChildnodes.hasMoreElements();) {
								NodeSummary  nodeSummary = (NodeSummary)e.nextElement();
								// if the child is the right LD tool type add the activity to the result set
								if (nodeSummary.getLdToolType() == iThisToolType)
									this.getOActivitiesSet().add(vtActivityNodes.elementAt(nn));
							}			
						}
					}
					catch (SQLException ex) {
						ProjectCompendium.APP.displayError("Exception:" + ex.getMessage());
					}
				}

			}
		
		return this.getOActivitiesSet();
	}	
	
/**
 * This method makes copies of all the relevant activities. The copies are intended 
 * to be presented to the user so that they can drag and drop them into their maps.
 * @param sInput
 * @return
 */
	public HashSet<UINode> getCopiesOfRelevantActivities(String sInput)	{
		
		HashSet<NodeSummary> hsNS = findRelevantActivities( sInput);
		int count = hsNS.size();
		HashSet<UINode> hsUINodes = new HashSet<UINode>(count);
	
		for (Iterator<NodeSummary> i = hsNS.iterator(); i.hasNext();) {
			i.next();
		}
		return hsUINodes;
	}
	
	/**
	 * Find tool nodes of type iToolType and add set   the instance variable
	 * vtLdNodes equal to the set of tools nodes found.
	 *  
	 * @param iToolType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private  boolean findToolNodesofType(int iToolType)	{
		// Check that iToolType is in the correct range to be a tool
			if ((iToolType < ILdCoreConstants.iLD_TYPE_VLE_TOOL_BLOG) || 
					(iToolType > ILdCoreConstants.iLD_TYPE_VLE_TOOL_WIKI))	{
				//If it is not, return false
				return false;
			}
			// Set parameters for search
			String sToolTag = ProjectCompendium.APP.getLdTypeTagMaps().getTypestoTagsTable().get(iToolType);
			String userID = this.getUserID();
			String	sContextCondition = DBSearch.CONTEXT_ALLVIEWS;
			String sViewId = "";
			Vector<String> vtSelectedNodeTypes = new Vector(1);
			vtSelectedNodeTypes.addElement(new Integer(ICoreConstants.REFERENCE).toString());
			// Vector indicating whether to search on the node label and or detail. Do not need to specify.
			Vector<String> vtAttrib	= new Vector<String>(2);
			// Set search to match any keyword (but none are being searched for so this should not have any effect)
			int iMatchKeywordCondition = DBSearch.MATCH_ANY;
			// Set search to match any code (only one is being searched for so this should not have any effect)
			int iMatchCodesCondition = DBSearch.MATCH_ANY;
			// No keywords at present - can use this vector to combine node type and ketword searches
			Vector<String> vKeywords = new Vector(1);
			// Want to search all authors, hence do not select any
			Vector vtSelectedAuthors = new Vector();
			// Set up the Vector of codes to be searched - the code is added below
			Vector<Code> vtSelectedCodes = new Vector<Code>(10);
			// Dates to limit the search: do not wnat to limit it by date so set all to null
			java.util.Date dBeforeCreationDate, dAfterCreationDate, dBeforeModificationDate, dAfterModificationDate;
			dBeforeCreationDate = null; dAfterCreationDate = null; dBeforeModificationDate = null; dAfterModificationDate = null;
			// Get all the codes (tag objects) for nodes tagged as VLE tools i.e. nodes with tag sVLE_TOOL_TAG
			// There should only be one tag code for VLE tools
			try {
				Code toolCode = this.getModel().getCodeService().getCodeForName(this.getSession(), ILdCoreConstants.sVLE_TOOL_TAG);
				Code subToolCode = this.getModel().getCodeService().getCodeForName(this.getSession(), sToolTag); 
				String sCodeID = toolCode.getId();
				vtSelectedCodes.add(toolCode);
				vtSelectedCodes.add(subToolCode);
				// Get all tool nodes 
				Vector<NodeSummary> vtNodes = this.getModel().getCodeService().getNodes(this.getSession(), sCodeID, userID);
				// Create the Vector to contain the tool nodes of interest
				Vector<NodeSummary> vtToolNodes = new Vector<NodeSummary>();		 
				vtToolNodes = this.getModel().getQueryService().searchNode(this.getSession(), sContextCondition, sViewId, vtSelectedNodeTypes, vtSelectedAuthors, vtSelectedCodes, iMatchCodesCondition, vKeywords, iMatchKeywordCondition, vtAttrib, dBeforeCreationDate, dAfterCreationDate, dBeforeModificationDate, dAfterModificationDate);
				// add all the relevant tool nodes
//				this.getModel().getQueryService().searchNode(session, sContextCondition, sViewID, vtSelectedNodeTypes, vtSelectedAuthors, vtSelectedCodes, sMatchCodesCondition, vKeywords, nMatchKeywordCondition, attrib, dBeforeCreationDate, dAfterCreationDate, dBeforeModificationDate, dAfterModificationDate)
				/**
				for (Iterator<NodeSummary> i = vtNodes.iterator(); i.hasNext();) {
					NodeSummary oNS = i.next();					
					if (oNS.getLdToolType() == iToolType)	{
						vtToolNodes.add(oNS);
					}	

				}
				**/
				setVtLdNodes(vtToolNodes);
				return true;		
			}
		catch(SQLException ex) {
			ProjectCompendium.APP.displayError("Exception:" + ex.getMessage());
		}
		return false;

	}

	
}

	

