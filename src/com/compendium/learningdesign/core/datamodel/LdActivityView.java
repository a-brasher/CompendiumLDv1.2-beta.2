/******************************************************************************
 *                                                                            *
/*  (c) Copyright 2009 Verizon Communications USA and The Open University UK    *
 *                                                                            *
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
 *                                                                            *
 ******************************************************************************/

package com.compendium.learningdesign.core.datamodel;

import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import com.compendium.learningdesign.util.TimeUnit;

import com.compendium.core.datamodel.Link;
import com.compendium.core.datamodel.ModelSessionException;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.View;
import com.compendium.learningdesign.core.ILdCoreConstants;
import com.compendium.learningdesign.core.datamodel.LdActivityTimes;

/** 
 * This is a version of the View class which stores additional data for the 
 * purpose of modelling learning activities. The additional data stored is 
 * data for the duration of learning activities and tasks.
 * 
 * @author ajb785
 */
public class LdActivityView extends View {
	/**	The set of TaskSequences for this activity; this LdActivityTimes instance 
	 * will refer to task times objects related to nodes in this LdActivityView **/
	private LdActivityTimes oLdActivityTimes = null;
	
	/**	The total time the tutor tasks in this activity will take to complete, in 
	 * 	units specified by the oTaskTimeUnits variable **/
	
	private int  iTutorTaskTime;
	/** The units that the task time is in (one of hours, minutes, days or months)	**/
	
	private TimeUnit oTutorTaskTimeUnits = TimeUnit.HOURS;
	
	/**	The total time the learner tasks in this activity will take to complete, in 
	 * 	units specified by the oTaskTimeUnits variable **/
	
	private int  iLearnerTaskTime;
	/** The units that the learner task time is in (one of hours, minutes, days or months)	**/
	
	private TimeUnit oLearnerTaskTimeUnits = TimeUnit.HOURS;
	
	/**	NOT YET USED Indicates that timing data has been initialised i.e. holds values that are available for writing to the db	**/
	private boolean bActivityTiminingDataInitialised = false;
	
	

	/**
	 * @param viewID
	 * @param type
	 * @param nodeType
	 * @param originalID
	 * @param permission
	 * @param state
	 * @param author
	 * @param creationDate
	 * @param modificationDate
	 * @param label
	 * @param detail
	 * @param lastModAuthor
	 */
	public LdActivityView(String viewID, int type, String nodeType,
			String originalID, int permission, int state, String author,
			Date creationDate, Date modificationDate, String label,
			String detail, String lastModAuthor) {
		super(viewID, type, nodeType, originalID, permission, state, author,
				creationDate, modificationDate, label, detail, lastModAuthor);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param viewID
	 * @param type
	 * @param nodeType
	 * @param originalID
	 * @param permission
	 * @param state
	 * @param author
	 * @param creationDate
	 * @param modificationDate
	 * @param label
	 * @param detail
	 */
	public LdActivityView(String viewID, int type, String nodeType,
			String originalID, int permission, int state, String author,
			Date creationDate, Date modificationDate, String label,
			String detail) {
		super(viewID, type, nodeType, originalID, permission, state, author,
				creationDate, modificationDate, label, detail);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param viewID
	 * @param type
	 * @param nodeType
	 * @param originalID
	 * @param state
	 * @param author
	 * @param creationDate
	 * @param modificationDate
	 * @param label
	 * @param detail
	 * @param lastModAuthor
	 */
	public LdActivityView(String viewID, int type, String nodeType,
			String originalID, int state, String author, Date creationDate,
			Date modificationDate, String label, String detail,
			String lastModAuthor) {
		super(viewID, type, nodeType, originalID, state, author, creationDate,
				modificationDate, label, detail, lastModAuthor);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param viewID
	 * @param type
	 * @param nodeType
	 * @param originalID
	 * @param state
	 * @param author
	 * @param creationDate
	 * @param modificationDate
	 * @param label
	 * @param detail
	 */
	public LdActivityView(String viewID, int type, String nodeType,
			String originalID, int state, String author, Date creationDate,
			Date modificationDate, String label, String detail) {
		super(viewID, type, nodeType, originalID, state, author, creationDate,
				modificationDate, label, detail);
		// TODO Auto-generated constructor stub
	}

	public LdActivityView(String nodeID) {
		super(nodeID);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns whether or not the passed node type is a LdActivityView node type.
	 * Modified by Andrew 12.12.2008 to include LDMAPVIEW type.
	 * @return boolean, true if the given type is a view type, false otherwise.
	 */
	public static boolean isLdActivityViewType(int type) {
		if (type == LDMAPVIEW)
			return true;
		else
			return false;
	}
	
	/**
	 * Return a node summary object with the given id and details.
	 * If a view node with the given id has already been created in this session, update its data and return that,
	 * else create a new one, and add it to the list.
	 *
	 *	@param sViewID the id of the view node.
	 *	@param nType the type of this node.
	 *	@param sXNodeType the extended node type id of the node - NOT CURRENTLY USED.
	 *	@param sOriginalID the original id of the node if it was imported.
	 *	@param sAuthor the author of the node.
	 *	@param dCreationDate the creation date of this node.
	 *	@param dModificationDate the date the node was last modified.
	 *	@param sLabel the label of this node.
	 *	@param sDetail the first page of detail for this node.
	 *	@param sLastModAuthor the author who last modified this object.
	 *  @return View, a view node object with the given id.
	 */


		/*** 
		 * Return a node summary object with the given id and details.
		 * If a view node with the given id has already been created in this session, update its data and return that,
		 * else create a new one, and add it to the list.
		 *
		 ******  This is just a copy the getView method in the View class at the moment !!!
		 *	It returns a NodeSummary object from the model if one exists, or creates
		 * 	then return sone if not..
		 *
		 *	@param sViewID the id of the view node.
		 *	@param nType the type of this node.
		 *	@param sXNodeType the extended node type id of the node - NOT CURRENTLY USED.
		 *	@param sOriginalID the original id of the node if it was imported.
		 *	@param sAuthor the author of the node.
		 *	@param dCreationDate the creation date of this node.
		 *	@param dModificationDate the date the node was last modified.
		 *	@param sLabel the label of this node.
		 *	@param sDetail the first page of detail for this node.
		 *	@param sLastModAuthor the author who last modified this object.
		 *  @return View, a view node object with the given id.
		 */
		public static LdActivityView getView(String sViewID, int nType, String sXNodeType, String sOriginalID,
					int nState, String sAuthor, Date dCreationDate, Date dModificationDate, 
					String sLabel, String sDetail, String sLastModAuthor)
		{
			int i = 0;
			Vector nodeSummaryList = NodeSummary.getNodeSummaryList();
			for (i = 0; i < nodeSummaryList.size(); i++) {
				if (sViewID.equals(((NodeSummary)nodeSummaryList.elementAt(i)).getId())) {
					break;
				}
			}

			LdActivityView ns = null;
			if (i == nodeSummaryList.size()) {
				ns = new LdActivityView(sViewID, nType, sXNodeType, sOriginalID,
									 nState,
									 sAuthor, dCreationDate,
									 dModificationDate, sLabel, sDetail, sLastModAuthor);
				nodeSummaryList.addElement(ns);
			}
			else {
				Object obj = nodeSummaryList.elementAt(i);
				if (obj instanceof LdActivityView) {
					ns = (LdActivityView)obj;

					// UPDATE THE DETAILS
					ns.setLabelLocal(sLabel);
					ns.setDetailLocal(sDetail);
					ns.setTypeLocal(nType);
					ns.setStateLocal(nState);
					ns.setAuthorLocal(sAuthor);
					ns.setCreationDateLocal(dCreationDate);
					ns.setModificationDateLocal(dModificationDate);
					ns.setOriginalIdLocal(sOriginalID);
					ns.setExtendedNodeTypeLocal(sXNodeType);
					ns.setLastModificationAuthorLocal(sLastModAuthor);				
				}
				else {
					nodeSummaryList.removeElement(obj);
					ns = new LdActivityView(sViewID, nType, sXNodeType, sOriginalID,
									 nState, sAuthor, dCreationDate,
									 dModificationDate, sLabel, sDetail);
					nodeSummaryList.addElement(ns);
				}
			}
		return ns;
	}

	/**
	 * Loads all the nodes and links into this view from the DATABASE.
	 *
	 * @exception java.sql.SQLException
	 * @exception ModelSessionException
	 */
	public void initializeMembers() throws SQLException, ModelSessionException {

		if (!bMembersInitialized) {

			if (oModel == null)
				throw new ModelSessionException("Model is null in View.initializeMembers");
			if (oSession == null) {
				oSession = oModel.getSession();
				if (oSession == null)
					throw new ModelSessionException("Session is null in View.initializeMembers");
			}

			/** Added by Andrew - to add  timing info  **/	
			if (!this.getActivityTiminingDataInitialised())	{
				LdActivityTimes oNewLdActivityTimes = oModel.getTaskTimesService().getLdActivityTimes(oModel.getSession(), this.getId());
				oLdActivityTimes = oNewLdActivityTimes;
				this.setActivityTiminingDataInitialised(true);
			}
			/** End of added by Andrew	**/
			Vector vtNodePos = oModel.getViewService().getNodePositions(oModel.getSession(), this.getId());
		
			for(Enumeration e = vtNodePos.elements(); e.hasMoreElements();) {
				NodePosition nodePos = (NodePosition)e.nextElement();
				nodePos.initialize(oModel.getSession(), oModel);
				NodeSummary node1 = nodePos.getNode();
				int xPos = nodePos.getXPos();
				int yPos = nodePos.getYPos();
				nodePos.setView(this);

				addMemberNode(nodePos);

				node1.initialize(oModel.getSession(), oModel);
			}

			//Get Links DO AFTER GET NODES SO APPROPRIATE NodeSummary entries created.
			Vector vtLinks = oModel.getViewService().getLinks(oModel.getSession(),this.getId());
			for(Enumeration e = vtLinks.elements(); e.hasMoreElements();) {
				Link link = (Link)e.nextElement();

				if (link != null) {
					link = addMemberLink(link);
					link.initialize(oModel.getSession(), oModel);
				}
			}
		
			loadViewLayer();
		}
		
		bMembersInitialized = true;
	}

	/**
	 * Get the set of TaskSequences for this activity
	 * @return the oLdActivityTimes
	 */
	public LdActivityTimes getLdActivityTimes() {
		return oLdActivityTimes;
	}

	/**
	 * Set the oLdActivityTimes variable for this instance to be equal to the 
	 * parameter oLdActivityTimes.
	 * @param ldActivityTimes the oLdActivityTimes to set
	 */
	public void setLdActivityTimes(LdActivityTimes oNewLdActivityTimes) {
		oLdActivityTimes = oNewLdActivityTimes;
	//	LdActivityTimes.getLdActivityTimesList().add(oLdActivityTimes);
	}

	/**
	 * @return the bActivityTiminingDataInitialised
	 */
	public boolean getActivityTiminingDataInitialised() {
		return bActivityTiminingDataInitialised;
	}

	/**
	 * @param activityTiminingDataInitialised the bActivityTiminingDataInitialised to set
	 */
	public void setActivityTiminingDataInitialised(
			boolean activityTiminingDataInitialised) {
		bActivityTiminingDataInitialised = activityTiminingDataInitialised;
	}
}
