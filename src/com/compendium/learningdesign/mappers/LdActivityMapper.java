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

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.*;
import com.compendium.core.db.DBSearch;
import com.compendium.core.ICoreConstants;
import com.compendium.learningdesign.core.ILdCoreConstants;

/**
 * Class to map between  VLE tools and the activities the tool is used in, and between words 
 * describing activities and those activities.
 * The mapping map is implemented using  a HashMap, which maps a number of Strings to
 * HashSets of Strings.
 * 
 * @author ajb785 
 */
public abstract class LdActivityMapper {
	/**
	 * The HashSet of relevant activities  	*
	 * @uml.property  name="oActivitiesSet"
	 */  
	private  	 HashSet<NodeSummary>  oActivitiesSet = null;
	
	/**	The nodes that will be mapped to Activities. These will be either LD tool or LD task nodes.	
	 *  Note that this is a Vector because that is what existing method searchLabel(String text, String nodeid) 
	 *  uses. A Vector is synchronised, so multiple threads can access it. Need to think more about using Vectors 
	 *  instead of  HashSets and HashMaps in other LD code. **/
	private 	Vector<NodeSummary> vtLdNodes;
	
	/**
	 * The model object i.e the base class for the object cache for a given database  and also holds a set of the database service objects *
	 * @uml.property  name="model"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private IModel model;
	
	/**
	 * The Code object added to Activity nodes *
	 * @uml.property  name="activityCode"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	protected Code activityCode = null;
	
	/**
	 * @uml.property  name="session"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private PCSession session;
	
	/**
	 * @uml.property  name="userID"
	 */
	private String userID = "";
	
	/**
	 * The NodeSummary that this Mapper is offering help about *
	 * @uml.property  name="oNode"
	 * @uml.associationEnd  
	 */
	private NodeSummary oNode;
	
	/**	All Activity nodes in the database 	**/
	private Vector<NodeSummary> vtAllActivities;
	
	/**
	 * Default constructor. Creates a LdActivityMapper instance with a default (empty) set of nodes.
	 * Subclasses should populate this map
	 */
	public  LdActivityMapper()	{
		oActivitiesSet = new HashSet<NodeSummary>();
		model = ProjectCompendium.APP.getModel();
		session = model.getSession();
		userID = model.getUserProfile().getId();
		try	{
			activityCode = this.getModel().getCodeService().getCodeForName(this.getSession(), ILdCoreConstants.sACTIVITY_TAG);
			vtAllActivities = this.findAllActivities();
			}
			catch (SQLException ex) {
				System.out.println("Exception initialising  ToolsToActivitesMapper \n\n"+ex.getMessage());
			}
	}
	
	/**
	 * Default constructor. Creates a LdActivityMapper instance with a default (empty) set of nodes.
	 * Subclasses should populate this map
	 */
	public  LdActivityMapper(NodeSummary oNodeSummary)	{
		oActivitiesSet = new HashSet<NodeSummary>();
		model = ProjectCompendium.APP.getModel();
		session = model.getSession();
		userID = model.getUserProfile().getId();
		oNode = oNodeSummary;
		try	{
			activityCode = this.getModel().getCodeService().getCodeForName(this.getSession(), ILdCoreConstants.sACTIVITY_TAG);
			vtAllActivities = this.findAllActivities();
			}
			catch (SQLException ex) {
				System.out.println("Exception initialising  ToolsToActivitesMapper \n\n"+ex.getMessage());
			}
	}
	/**
	 * Constructor. Create a a LdActivityMapper instance with the set of NodeSummaries supplied.
	 * @param oSet - a HashSet of NodeSummray instances.
	 */
	public  LdActivityMapper(HashSet<NodeSummary>  oSet)	{
		oActivitiesSet = oSet;
		model = ProjectCompendium.APP.getModel();
		session = model.getSession();
		userID = model.getUserProfile().getId();
		try	{
			activityCode = this.getModel().getCodeService().getCodeForName(this.getSession(), ILdCoreConstants.sACTIVITY_TAG);
			vtAllActivities = this.findAllActivities();
			
			}
			catch (SQLException ex) {
				System.out.println("Exception initialising  ToolsToActivitesMapper \n\n"+ex.getMessage());
			}
	}
	
	/**
	 * Find the relevant activities, i.e the activities relevant to the String 
	 * sInput.
	 * @return a HashSet containing the relevant activity nodes 
	 */
	public abstract HashSet<NodeSummary> findRelevantActivities(String sInput);	
	
	/**
	 * Set instance variable vtLdNodes to be a Vector containing the nodes  
	 * which are required to be mapped to Activities. The LD type and nature of these nodes
	 * will vary with the subclass. 
	 *  
	 */
	public void setVtLdNodes(Vector<NodeSummary> vtNodes)	{
		vtLdNodes = vtNodes;
	}
	
	/**
	 * @return the oActivities
	 */
	public HashSet<NodeSummary> getOActivitiesSet() {
		return oActivitiesSet;
	}
	
	/**
	 * Find all the activity nodes in the database
	 * @return
	 */
	public Vector<NodeSummary> findAllActivities()	{
		Vector<NodeSummary> nodes = new Vector<NodeSummary>(1);
		String sContextCondition = DBSearch.CONTEXT_ALLVIEWS;
		String sViewId = ""; //Try this, if it does not search on all views then will have to set it explicitly
		Vector<String> vtSelectedNodeTypes = new Vector(10);
		// Activities are LDMapViews 
		vtSelectedNodeTypes.addElement(new Integer(ICoreConstants.LDMAPVIEW).toString());
		// Want to search all authors, hence do not select any
		Vector vtSelectedAuthors = new Vector();
		// Set up the Vector of codes to be searched - in this case the activity code
		Vector<Code> vtSelectedCodes = new Vector<Code>(10);
		vtSelectedCodes.add(activityCode);
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
		try	{
			nodes = this.getModel().getQueryService().searchNode(this.getSession(), sContextCondition, sViewId, vtSelectedNodeTypes, vtSelectedAuthors, vtSelectedCodes, iMatchCodesCondition, vKeywords, iMatchKeywordCondition, vtAttrib, dBeforeCreationDate, dAfterCreationDate, dBeforeModificationDate, dAfterModificationDate);
			return nodes;
		}
		catch (SQLException ex)	{
			System.out.println("Exception finding activity nodes in ToolsToActivitesMapper \n\n"+ex.getMessage());
			return nodes;
		}
	}
	
	/**
	 * Set instance variable oActivities to be the activities relevant to the input string.
	 * 
	 * @return the oActivities
	 */
	/** Do not need this method because have got findRelevantActivities(String sInput)
	 * and setOActivitiesSet(HashSet<NodeSummary> activitiesSet).
	public HashSet<NodeSummary> getOActivitiesSet(String sInput) {
		if (this.getVtLdNodes().size() == 0)	{
			// There are no nodes to be mapped so set setOActivitiesSet to be an empty set
			return(this.setOActivitiesSet(new HashSet<NodeSummary>()));
		}
		else	{
			
		}
		return oActivitiesSet;
	}
	
	**/
	/**
	 * @return the vtLdNodes
	 */
	public Vector<NodeSummary> getVtLdNodes() {
		return vtLdNodes;
	}
	
	/**
	 * @param activitiesSet the oActivitiesSet to set
	 */
	public HashSet<NodeSummary> setOActivitiesSet(HashSet<NodeSummary> activitiesSet) {
		oActivitiesSet = activitiesSet;
		return(oActivitiesSet);
	}
	/**
	 * @return  the model
	 * @uml.property  name="model"
	 */
	public IModel getModel() {
		return model;
	}
	/**
	 * @return  the session
	 * @uml.property  name="session"
	 */
	public PCSession getSession() {
		return session;
	}
	/**
	 * @return  the userID
	 * @uml.property  name="userID"
	 */
	public String getUserID() {
		return userID;
	}
	/**
	 * @return the oNode
	 */
	public NodeSummary getNode() {
		return oNode;
	}

	/**
	 * Return the Vector containg all the Activity Nodes in the database.
	 * @return the vtAllActivities
	 */
	public Vector<NodeSummary> getAllActivities() {
		return vtAllActivities;
	}

}
