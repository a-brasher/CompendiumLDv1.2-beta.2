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

package com.compendium.learningdesign.io.xml;

import java.awt.Dimension;
import java.awt.Point;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.compendium.ProjectCompendium;
import com.compendium.core.CoreUtilities;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.Code;
import com.compendium.core.datamodel.IModel;
import com.compendium.core.datamodel.INodeSummary;
import com.compendium.core.datamodel.IView;
import com.compendium.core.datamodel.Link;
import com.compendium.core.datamodel.Model;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.View;
import com.compendium.core.datamodel.services.NodeService;
import com.compendium.core.db.DBNode;
import com.compendium.io.xml.XMLImport;
import com.compendium.learningdesign.core.datamodel.LdActivityTimes;
import com.compendium.learningdesign.core.datamodel.LdActivityView;
import com.compendium.learningdesign.core.datamodel.LdTask;
import com.compendium.learningdesign.core.datamodel.LdTaskSequence;
import com.compendium.ui.UILink;
import com.compendium.ui.UINode;
import com.compendium.ui.UIViewFrame;
import com.compendium.ui.UIViewPane;
import com.compendium.ui.plaf.NodeUI;

import com.compendium.learningdesign.core.ILdCoreConstants;
import com.compendium.learningdesign.ui.UILdActivityViewFrame;
import com.compendium.learningdesign.ui.UILdNode;
import com.compendium.learningdesign.ui.UILdTaskNode;
import com.compendium.learningdesign.ui.UILdViewPane;
import com.compendium.learningdesign.util.TimeUnit;
import com.compendium.learningdesign.core.datamodel.services.TaskTimesService;

/**
 * Adds functionality to XMLImport class so that learning design XML data is
 * correctly processed. In particular, where XMLImport processes MAPVIEWs, this
 * LdXMLImport class also processes LDMAPVIEWs.
 * 
 * @author ajb785
 *
 */
public class LdXMLImport extends XMLImport {
	private boolean isLearningDesignXML = false;
	
	
	
	/**	The LdActivityTimes associated with the view that the data is being 
	 * imported into. This is set in method processLdDocument in the view is an
	 * LdActivityView. Otherwise oLdActivityTimes will be null.	**/ 
	private LdActivityTimes oLdActivityTimes;
		
	/*** A count of the total number of TaskSeqences being imported for use with the progress bar counter.	**/	 
	protected int 				nNumberOfTaskSequences = 0;
	
	// FOR UNDOING ON CANCEL
	/*** Holds a list of ActivityTimesDisplayed added so far in case user cancels and we need to undo.*/
	protected Vector 				vtActivityTimesDisplayedList 			= new Vector();

	/** 	A hashtable of the new  LdActivityTimes created, with keys equal to the corresponding  ActivityId string in the imported XML data.	 */
	protected Hashtable<String, LdActivityTimes> 			htLdActivityTimes 			= new Hashtable<String, LdActivityTimes>(51);
	
	/** 	A hashtable of the new LdTaskSequence objects created by the import, with keys equal to the id	 of the task sequence in the imported XML data.	*/
	protected Hashtable<String, LdTaskSequence> 			htNewLdTaskSequences 			= new Hashtable<String, LdTaskSequence>(51);
	
	/** 	A hashtable of the new LdTask objects created by the import, with keys equal to the id	 of the task  in the imported XML data.	*/
	protected Hashtable<String, LdTask> 			htNewLdTasks 			= new Hashtable<String, LdTask>(51);
	/**
	 * @param debug
	 * @param fileName
	 * @param model
	 * @param view
	 * @param isSmartImport
	 * @param includeInDetail
	 * @param  bIsCopying - indicates whether the new map i being created as a copy of another
	 */
	public LdXMLImport(boolean debug, String fileName, IModel model,
			IView view, boolean isSmartImport, boolean includeInDetail, boolean bIsACopyBeingMade) {
		super(debug, fileName, model, view, isSmartImport, includeInDetail, bIsACopyBeingMade);
		
	}

	/**
	 * Process the given XML document and create all the view, nodes, links, codes etc required.
	 * @param document, the XML Document object to process.
	 */
	protected void processDocument( Document document ) {
		
		DocumentType oDocType = document.getDoctype();
		if (oDocType.getSystemId().contains("CompendiumLD.dtd"))	{
			this.isLearningDesignXML = true;
			this.processLdDocument(document);
			return;
		}
		else	{
			super.processDocument(document);
		}

	}

	/**
	 * Process the given XML document (which should already have been checked to make sure it is a 
	 * CompendiumLD learning design XML file. Process the document to create all the view, nodes, 
	 * links, codes, and task sequence timing objects etc required.
	 * @param document, the XML Document object to process.
	 */
	protected void processLdDocument( Document document )	{
		// Check the kind of view being imported into
		if (oView instanceof LdActivityView)	{
			/**	If it is an LdActivityView, get the LdActivityTimes  instance, 
			 * and set the initialised flags to false so that data can be updated 
			 * by the import.
			 */
			oLdActivityTimes = ((LdActivityView)oView).getLdActivityTimes();
			((LdActivityView) oView).setIsMembersInitialized(false);
			((LdActivityView) oView).setActivityTiminingDataInitialised(false);
		}
	
		vtNodeList.removeAllElements();
		vtLinkList.removeAllElements();
		vtShortcuts.removeAllElements();
		vtMeetings.removeAllElements();

		htNewNodes.clear();
		htNodes.clear();
		htViews.clear();
		htCodes.clear();
		htNodeView.clear();
		htUINodes.clear();
		htLdActivityTimes.clear();
		htNewLdTaskSequences.clear();
		htNewLdTasks.clear();

 		ProjectCompendium.APP.setWaitCursor();

		Node node = document.getDocumentElement();

		NamedNodeMap attrs = node.getAttributes();
		Attr oRootView = (Attr)attrs.getNamedItem("rootview");
		sRootView = oRootView.getValue();

		// PRE-PROCESS VIEWS i.e. each 'view' is a  viewnode table entries
		NodeList views = document.getElementsByTagName("view");
		int countj = views.getLength();
		for (int j=0; j< countj; j++) {	// Start of 

			Node view = views.item(j);
			attrs = view.getAttributes();

			Attr oViewID = (Attr)attrs.getNamedItem("viewref");
			String viewid = oViewID.getValue();
			Attr oNodeID = (Attr)attrs.getNamedItem("noderef");
			String nodeid = oNodeID.getValue();
			Attr oXPos = (Attr)attrs.getNamedItem("XPosition");
			String xPos = oXPos.getValue();
			Attr oYPos = (Attr)attrs.getNamedItem("YPosition");
			String yPos = oYPos.getValue();

			Attr oCreated = (Attr)attrs.getNamedItem("created");
			Long created = new Long(0);
			if (oCreated != null) {
				created = new Long(oCreated.getValue());
			}

			Attr oLastModified = (Attr)attrs.getNamedItem("lastModified");
			Long lastModified = new Long(0);
			if (oLastModified != null) {
				lastModified = new Long(oLastModified.getValue());
			}
			
			Model model = (Model)oModel;
						
			Attr oShowTags = (Attr)attrs.getNamedItem("showTags");
			Boolean bShowTags = null;
			if (oShowTags != null) {
				bShowTags = new Boolean(oShowTags.getValue());
			} else {
				bShowTags = new Boolean(model.showTagsNodeIndicator);
			}

			Attr oShowText = (Attr)attrs.getNamedItem("showText");
			Boolean bShowText = null;
			if (oShowText != null) {
				bShowText = new Boolean(oShowText.getValue());
			} else {
				bShowText = new Boolean(model.showTextNodeIndicator);
			}

			Attr oShowTrans = (Attr)attrs.getNamedItem("showTrans");
			Boolean bShowTrans = null;
			if (oShowTrans != null) {
				bShowTrans = new Boolean(oShowTrans.getValue());
			} else {
				bShowTrans = new Boolean(model.showTransNodeIndicator);
			}

			Attr oShowWeight = (Attr)attrs.getNamedItem("showWeight");
			Boolean bShowWeight = null;
			if (oShowWeight != null) {
				bShowWeight = new Boolean(oShowWeight.getValue());
			} else {
				bShowWeight = new Boolean(model.showWeightNodeIndicator);
			}

			Attr oSmallIcon = (Attr)attrs.getNamedItem("smallIcon");
			Boolean bSmallIcon = null;
			if (oSmallIcon != null) {
				bSmallIcon = new Boolean(oSmallIcon.getValue());
			} else {
				bSmallIcon = new Boolean(model.smallIcons);
			}

			Attr oHideIcon = (Attr)attrs.getNamedItem("hideIcon");
			Boolean bHideIcon = null;
			if (oHideIcon != null) {
				bHideIcon = new Boolean(oHideIcon.getValue());
			} else {
				bHideIcon = new Boolean(model.hideIcons);
			}
			
			Attr oWrapWidth = (Attr)attrs.getNamedItem("labelWrapWidth");
			Integer nWrapWidth = null;
			if (oWrapWidth != null) {
				nWrapWidth = new Integer(oWrapWidth.getValue());
			} else {
				nWrapWidth = new Integer(model.labelWrapWidth);
			}
			
			Attr oFontSize = (Attr)attrs.getNamedItem("fontsize");
			Integer nFontSize = null;
			if (oFontSize != null) {
				nFontSize = new Integer(oFontSize.getValue());
			} else {
				nFontSize = new Integer(model.fontsize);
			}

			Attr oFontFace = (Attr)attrs.getNamedItem("fontface");
			String sFontFace = "";			
			if (oFontFace != null) {
				sFontFace = oFontFace.getValue();	
			} else {
				sFontFace = model.fontface;
			}
			
			Attr oFontStyle = (Attr)attrs.getNamedItem("fontstyle");
			Integer nFontStyle = null;
			if (oFontStyle != null) {
				nFontStyle = new Integer(oFontStyle.getValue());
			} else {
				nFontStyle = new Integer(model.fontstyle);
			}
			
			Attr oForeground = (Attr)attrs.getNamedItem("foreground");
			Integer nForeground = null;
			if (oForeground != null) {
				nForeground = new Integer(oForeground.getValue());
			} else {
				nForeground = new Integer(Model.FOREGROUND_DEFAULT.getRGB());
			}
			
			Attr oBackground = (Attr)attrs.getNamedItem("background");
			Integer nBackground = null;
			if (oBackground != null) {
				nBackground = new Integer(oBackground.getValue());
			} else {
				nBackground = new Integer(Model.BACKGROUND_DEFAULT.getRGB());
			}				
			
			Vector nodePos = new Vector(18);
			nodePos.add(viewid);
			nodePos.add(nodeid);
			nodePos.add(xPos);
			nodePos.add(yPos);
			nodePos.add(created);
			nodePos.add(lastModified);

			nodePos.add(bShowTags);
			nodePos.add(bShowText);
			nodePos.add(bShowTrans);
			nodePos.add(bShowWeight);
			nodePos.add(bSmallIcon);								
			nodePos.add(bHideIcon);
			nodePos.add(nWrapWidth);
			nodePos.add(nFontSize);
			nodePos.add(sFontFace);
			nodePos.add(nFontStyle);
			nodePos.add(nForeground);
			nodePos.add(nBackground);
			
			if (!htViews.containsKey((Object) viewid))
				htViews.put((Object) viewid, (Object) new Vector(51));

			Vector nextView = (Vector)htViews.get((Object) viewid);
			nextView.add( (Object) nodePos );
			htViews.put((Object) viewid, (Object) nextView);
		}	// End of pre-process viewnode table entries

		// PRE-PROCESS NODES
		NodeList nodes = document.getElementsByTagName("node");
		int counti = nodes.getLength();
		for (int i=0; i< counti; i++) {				// ***************** Loop start
			Node innernode = nodes.item(i);
			attrs = innernode.getAttributes();
			Attr oID = (Attr)attrs.getNamedItem("id");
			String nodeid = oID.getValue();
			// Warn the user if they are importing XML with timing data into a stndard map
			if (nodeid.equals(sRootView))	{
				Attr oType = (Attr)attrs.getNamedItem("type");
				int iThisNodeType = Integer.parseInt(oType.getValue()) ;
				int iViewImportedIntoType = this.oView.getType();
				if (iThisNodeType != iViewImportedIntoType)	{
					if (iThisNodeType == ILdCoreConstants.iLD_TYPE_ACTIVITY)	{	
						this.displayWarning();
					}
				}
			}
			htNodes.put((Object) nodeid, (Object) innernode);
		}  	//	*************** Pre-process nodes Loop end

		NodeList codes = document.getElementsByTagName("code");
		NodeList links = document.getElementsByTagName("link");
		NodeList mediaindexes = document.getElementsByTagName("mediaindex");

		// FOR PROGRESS BAR ONLY
		NodeList shorts = document.getElementsByTagName("shortcutref");
		NodeList meetings = document.getElementsByTagName("meeting");
		
		//LdTaskSequence data
		NodeList tasksequences = document.getElementsByTagName("TaskSequences");
		
		// INITIALISE THE PROGRESS BARS MAXIMUM
		nNumberOfNodes = counti;
		nNumberOfCodes = codes.getLength();
		nNumberOfLinks = links.getLength();
		nNumberOfShorts = shorts.getLength();
		nNumberOfMeetings = meetings.getLength();
		nNumberOfMediaIndexes = meetings.getLength();
		nNumberOfTaskSequences = tasksequences.getLength();
  		oProgressBar.setMaximum(nNumberOfNodes+nNumberOfLinks+nNumberOfCodes+nNumberOfShorts+nNumberOfMeetings+nNumberOfMediaIndexes+nNumberOfTaskSequences);

		// NEED TO DO THIS BEFORE VIEWS ARE PROCESSED SO CODE OBJECTS HAVE BEEN CREATED
  		// Process Code data and add it to the code table in the database 
		processCodes( codes );

		processView( sRootView, this.oView, this.oModel );

		// NEED TO DO THIS AFTER VIEWS HAVE BEEN PROCESSED SO NODE OBJECTS HAVE BEEN CREATED
		processLinks( links );

		// NEED TO DO THIS AFTER VIEWS HAVE BEEN PROCESSED SO NODE OBJECTS HAVE BEEN CREATED
		processShortcuts();

		// NEED TO DO THIS BEFORE MEDIAINDEXES ARE PROCESSED AS THEY NEED TO BE REFERENCED BY THEM
		processMeetings(meetings);

		// NEED TO DO THIS AFTER NODES ARE PROCESSED AS THEY NEED TO REFERENCE THEM
		processMediaIndexes(mediaindexes);
		
		// NEED TO DO THIS AFTER NODES and CODES ARE PROCESSED AS THEY NEED TO REFERENCE THEM
		processTaskSequences(document);

		// INITIALIZE THE NEW VIEW NODES SO THEIR NODE WEIGHT INICATION NUMBERS REFRESH CORRECTLY LATER
		int countk = htUINodes.size();
		int nType = 0;
		UINode oCheckNode = null;
		for(Enumeration e = htUINodes.elements();e.hasMoreElements();) {
			Object obj = e.nextElement();
			if (obj instanceof UINode) {
				oCheckNode = (UINode)obj;
				nType = oCheckNode.getType();
				if(nType == ICoreConstants.MAPVIEW ||  nType == ICoreConstants.MAP_SHORTCUT ||
						nType == ICoreConstants.LISTVIEW || nType == ICoreConstants.LIST_SHORTCUT) {
					try {
						((View)oCheckNode.getNode()).initializeMembers();
					}
					catch(Exception io) {}
				}
				else if(nType == ICoreConstants.LDMAPVIEW ) {
					try {
						((LdActivityView)oCheckNode.getNode()).initializeMembers();
					}
					catch(Exception io) {}
				}
			}
		}
		UIViewFrame  oFrame = this.oViewPaneUI.getViewPane().getViewFrame();
		if (oFrame  instanceof  UILdActivityViewFrame)
			{
			((UILdActivityViewFrame) oFrame).updateLdActivityTimes();
			}
		/** Attempt to get activity timing data shown correctly for nodes created from stencil - not working - need to try again 	**/
		else if (this.oView.getType() == ICoreConstants.LDMAPVIEW)	{
			((LdActivityView)this.oView).setActivityTiminingDataInitialised(false);
			((LdActivityView)this.oView).setIsMembersInitialized(false);
		}
		/** End of Attempt to get activity timing data shown correctly for nodes created from stencil - not working - need to try again 	**/
		ProjectCompendium.APP.refreshIconIndicators();
		ProjectCompendium.APP.setDefaultCursor();
	}
	
	/**
	 * Process the task sequence data in the document oDocument and write it to
	 * the database.
	 * @param oDocument
	 */
	private void processTaskSequences(Document oDocument) {
		NodeList oActivityTimesDisplayed = oDocument.getElementsByTagName("ActivityTimesDisplayed");
		NodeList oTaskSequenceActivity = oDocument.getElementsByTagName("TaskSequenceActivity");
		NodeList oTaskSequenceRoles = oDocument.getElementsByTagName("TaskSequenceRole");
		NodeList oTaskTimes = oDocument.getElementsByTagName("TaskTime");
		NodeList oTaskSequenceTasks = oDocument.getElementsByTagName("TaskSequenceTask");
		
		processActivityTimesDisplayed(oActivityTimesDisplayed);
		processTasksequenceAcvtivity(oTaskSequenceActivity);
		try	{
			procesTaskSequenceRole(oTaskSequenceRoles);
		}
		catch (Exception e)	{
			System.out.println(e.getMessage());
		}
		processTaskTimes(oTaskTimes);
		procesTaskSequenceTask(oTaskSequenceTasks);
		// Update the database
		Set<String> oActivityIds = htLdActivityTimes.keySet();
		Iterator<String> oIt = oActivityIds.iterator();
		while (oIt.hasNext())	{
			String sActId = oIt.next();
			LdActivityTimes  oCurrrentLdActvityTimes = this.htLdActivityTimes.get(sActId);
			try	{
				//LdActivityTimes  oCurrrentLdActvityTimes = this.htLdActivityTimes.get(oIt.next());
				this.oModel.getTaskTimesService().updateTaskTimes(this.oSession, oCurrrentLdActvityTimes);
				LdActivityView oActView = (LdActivityView) htNewNodes.get(sActId);
				if (oActView != null)	{
					oActView.setActivityTiminingDataInitialised(true);
				}
			}
		catch (SQLException eSQLException){
			String sErrMessage = "Exception: (XMLImport.processTaskSequences())Writing activity timing data to database (" 
				+ oCurrrentLdActvityTimes + ") " + eSQLException.getMessage();
			System.err.println(sErrMessage);
			eSQLException.printStackTrace();
			ProjectCompendium.APP.displayError(sErrMessage);
		}
		}		
	}

	/**
	 * Process the tasksequence task data from the XML file and add the relevant 
	 * LdTask objects to the relevant LdTaskSequence objects.
	 * @param taskSequenceTasks
	 */
	private void procesTaskSequenceTask(NodeList taskSequenceTasks) {
		int nTaskSequenceTasks = taskSequenceTasks.getLength();
		// List of task ids in a specific task sequence
		LinkedList<String> oTaskIdList =  new LinkedList<String>();
		/** Hashtable htTaskSequenceTask is a hashtable with key =  tasksequenceid 
		 * , value = list of tasks ids in the sequence, for the imported data. */		 
		Hashtable<String, LinkedList<String>> htTaskSequenceTask = new Hashtable<String, LinkedList<String>>();
		for (int i=0; i<nTaskSequenceTasks; ++i)	{
			Node oTaskSequenceTask = taskSequenceTasks.item(i);
			NamedNodeMap oTaskSequenceTaskAttribs = oTaskSequenceTask.getAttributes();
			String sImportedTaskSequenceId =  oTaskSequenceTaskAttribs.getNamedItem("TaskSequenceId").getNodeValue();
			
			String sImportedTaskId =  oTaskSequenceTaskAttribs.getNamedItem("TaskId").getNodeValue();
			// Get or create the list of imported task ids for the imported task sequence id
			LinkedList<String> oImportedTaskIdList = htTaskSequenceTask.get(sImportedTaskSequenceId);
			if (oImportedTaskIdList == null)	{
				oImportedTaskIdList = new LinkedList<String>();
			}
			oImportedTaskIdList.add(sImportedTaskId);

			htTaskSequenceTask.put(sImportedTaskSequenceId, oImportedTaskIdList);
		}

		Iterator<String> idListIterator = oTaskIdList.iterator();
		Enumeration<String> oImportedTaskSeqIds = htTaskSequenceTask.keys();
		String sImportedTaskSeqId = ""; LinkedList<String> oImportedTaskIdsList;
		
		while (oImportedTaskSeqIds.hasMoreElements())	{
			sImportedTaskSeqId = oImportedTaskSeqIds.nextElement();
			// Get the relevant task sequence; This should have already been created.
			LdTaskSequence oLdTaskSequence = htNewLdTaskSequences.get(sImportedTaskSeqId);
			oImportedTaskIdsList = htTaskSequenceTask.get(sImportedTaskSeqId);
			LdTask oLdTask;		
			LinkedList<LdTask> oLdTaskList =  new LinkedList<LdTask>();
			Iterator<String> oImportedTaskListIt = oImportedTaskIdsList.iterator();
			while (oImportedTaskListIt.hasNext())	{
				String sTaskId = oImportedTaskListIt.next();
				/** If the task nodes are being created in the root view, there should be a corresponding UILdTaskNodes 
				 * which will contain the task data. These will have been created in processTaskTimes(NodeList taskTimes)	**/
				// UILdTaskNode oUILdTaskNode = (UILdTaskNode)htUINodes.get(sTaskId);
				/** if nodes are being created in the root view, process them as UINodes otherwise just create LdTask data **/
				Object oObj = htUINodes.get(sTaskId); UILdTaskNode oUILdTaskNode = null;
				if (oObj instanceof UILdTaskNode)	{
					oUILdTaskNode = (UILdTaskNode)oObj;
					/* Do I need to create the LdTask data even if there is a UILdTaskNode??  YES!! - should create LdTask data **/  
				}
				
//				if (oUILdTaskNode == null)	{
					oLdTask = this.htNewLdTasks.get(sTaskId);
					oLdTaskList.add(oLdTask);
//				}				
			}			
			oLdTaskSequence.setTaskSequence(oLdTaskList);			
		}

	}

	private void procesTaskSequenceRole(NodeList taskSequenceRoles) throws Exception {
		int nTaskSequenceRoles = taskSequenceRoles.getLength();

		for (int i=0; i<nTaskSequenceRoles; ++i){
			Node oTaskSequenceRole = taskSequenceRoles.item(i);
			NamedNodeMap oTaskSequenceRoleAttribs = oTaskSequenceRole.getAttributes();
			String sImportedTaskSequenceId = oTaskSequenceRoleAttribs.getNamedItem("TaskSequenceId").getNodeValue();
			String sImportedRoleId = oTaskSequenceRoleAttribs.getNamedItem("RoleId").getNodeValue();
			// Get the relevant task sequence; This should have already been created.
			LdTaskSequence oLdTaskSequence = htNewLdTaskSequences.get(sImportedTaskSequenceId);
			// Get the id of the relevant Role NodeSummary
			NodeSummary oRoleNode = (NodeSummary) htNewNodes.get(sImportedRoleId);
			String sNewRoleId = oRoleNode.getId();
			if(sNewRoleId.length() == 0 )	{
				throw new Exception("Zero length Role Id for sImportedTaskSequenceId = " + sImportedTaskSequenceId.toString()
						+ 	", oRoleNode = " + oRoleNode.getLabel()	
				);
			}
			else	{
				oLdTaskSequence.setRoleId(sNewRoleId);
				oLdTaskSequence.setRoleName(oRoleNode.getLabel());
			}
		}

	}

	/**
	 * Create LdTask objects from the data in the XML file. Also update the 
	 * UILdTaskNode objects in the root view (if any) wuth the time data. 
	 * @param taskTimes
	 */
	private void processTaskTimes(NodeList taskTimes) {
		int nTaskTimes = taskTimes.getLength();
		/** need to deal with preserveNodeIds case???? /**/
		for (int i=0; i<nTaskTimes; ++i)	{
			Node oTaskTime = taskTimes.item(i);
			NamedNodeMap oAttribs = oTaskTime.getAttributes();
			String sImportedTaskId = oAttribs.getNamedItem("TaskId").getNodeValue();
			String sTimeUnit = oAttribs.getNamedItem("TimeUnit").getNodeValue();
			String sShowTime = oAttribs.getNamedItem("ShowTime").getNodeValue();
			String sTimeValue = oTaskTime.getFirstChild().getNodeValue();
			/** if nodes are being created in the root view, process them as UINodes otherwise just create LdTask data **/
			Object oObj = htUINodes.get(sImportedTaskId); UILdTaskNode oUILdTaskNode = null;
			if (oObj instanceof UILdTaskNode)	{
				oUILdTaskNode = (UILdTaskNode)oObj;
			}
			//htUINodes.get(sImportedTaskId)
			LdTask oNewLdTask;	String sTaskNodeId = "";
			/** Check taht there are Task Nodes either UI or NS, and if so create the relevant task timing information **/
			if (oUILdTaskNode != null)	{
				oUILdTaskNode.setTaskTime( Long.valueOf(sTimeValue));
				oUILdTaskNode.setCurrentTaskTimeUnits(TimeUnit.valueOf(sTimeUnit));
				oUILdTaskNode.setShowTime(Boolean.valueOf(sShowTime));
				sTaskNodeId = oUILdTaskNode.getNode().getId();
			}
			else	{
				NodeSummary oTaskNodeNS = (NodeSummary)htNewNodes.get(sImportedTaskId);
				if (oTaskNodeNS != null)
					sTaskNodeId = oTaskNodeNS.getId();
				//oNewLdTask = new LdTask(sTaskNodeId, Long.valueOf(sTimeValue), TimeUnit.valueOf(sTimeUnit), Boolean.valueOf(sShowTime));
			}
			if (sTaskNodeId.length() != 0)	 {
			oNewLdTask = new LdTask(sTaskNodeId, Long.valueOf(sTimeValue), TimeUnit.valueOf(sTimeUnit), Boolean.valueOf(sShowTime));
			htNewLdTasks.put(sImportedTaskId, oNewLdTask);
			}
		}
		
	}

	/**
	 * Add Empty LdTasksequences to the relevant LdActivityTimes  instances, 
	 * based on the relationships specified in the XML data passed via the 
	 * parameter taskSequenceActivity.
	 * @param taskSequenceActivity, a list of the TaskSequenceActivity XML 
	 * nodes in the document being processed.
	 */
	private void processTasksequenceAcvtivity(NodeList taskSequenceActivity) {
		int nTaskSequenceActivity = taskSequenceActivity.getLength();
		LdActivityTimes oLdActivityTimes;
		for (int i=0; i<nTaskSequenceActivity; ++i)	{
			Node otaskSequenceActivity = taskSequenceActivity.item(i);
			NamedNodeMap oAttribs = otaskSequenceActivity.getAttributes();
//			String sNewActivityId = getActivityId(oAttribs);
			String sImportedActivityId = this.getImportedActivityId(oAttribs);
			oLdActivityTimes = htLdActivityTimes.get(sImportedActivityId);	
			// Get an id for the task sequence based on the options selected by the user e.g. preserve imported node ids 
			String sNewTaskSequenceId = this.getTaskSequenceId(oAttribs);
			LdTaskSequence oLdTaskSequence = new LdTaskSequence(sNewTaskSequenceId);
			String sImportedTaskSequenceId = this.getImportedTaskSequenceId(oAttribs);
			htNewLdTaskSequences.put(sImportedTaskSequenceId, oLdTaskSequence);
			oLdActivityTimes.addTaskSequence(oLdTaskSequence);
		}
		
	}

	/**
	 * This method returns the id of the activity to be used to create the 
	 * LdTask data, by generating it according to the data in the XML  file 
	 * and the import options selected. If the "preserve node ids" option is
	 * selected, the relevant id from the XML file is returned, unless the 
	 * view is the  root view in which case the id of the new view created by the 
	 * user is returned. 
	 * If the "preserve node ids" option is NOT selected,  the id of the node 
	 * created by the import is returned from the hash table of newly created nodes
	 * htNewNodes. 
	 
	 * @param oAttribs - a collection of nodes containing a Named item "ActivityId"
	 * @return sActivityId - a String representation of the activity id to be used
	 * to generate the new representation in the model of the task sequence data in the 
	 * XML file, or null if there is no ActivityId attribute in the XML data 
	 *  passed to this method.
	 */
	private String getActivityId(NamedNodeMap oAttribs) {
		String sActivityId = "";
		String sActivityIdImported = oAttribs.getNamedItem("ActivityId").getNodeValue();
		/** return null if there is not an ActivityId attribute in the XML data passed to this method **/
		if (sActivityIdImported.equals(null))
			return null;
		if (DBNode.getPreserveImportedIds() && !sActivityIdImported.equals(sRootView))	{
			sActivityId = oAttribs.getNamedItem("ActivityId").getNodeValue();
		}
		else	{
			if (sActivityIdImported.equals(sRootView))	{
				sActivityId = oView.getId();
			}
			else	{
				sActivityId = htNewNodes.get(sActivityIdImported).getId();
			}	
		}
		return sActivityId;
	}
	
	private String getTaskSequenceId(NamedNodeMap oAttribs) {
		String sTaskSequenceId = "";
		String sTaskSequenceIdImported = oAttribs.getNamedItem("TaskSequenceId").getNodeValue();
		/** return null if there is not an ActivityId attribute in the XML data passed to this method **/
		if (sTaskSequenceIdImported.equals(null))
			return null;
		if (DBNode.getPreserveImportedIds()) 	{
			sTaskSequenceId = sTaskSequenceIdImported;
		}
		else	{
			sTaskSequenceId = oModel.getUniqueID();
		}
		return sTaskSequenceId;
	}
	
		
	/**
	 * Get the activity id value from the imported data
	 * @param oAttribs
	 * @return a String representation of the activity id in the imported data 
	 * represented by the parameter oAttribs , or null if 
	 * there is no ActivityId attribute in the XML data passed to this method.
	 */
	private String getImportedActivityId(NamedNodeMap oAttribs) {
		String sActivityIdImported = oAttribs.getNamedItem("ActivityId").getNodeValue();
		/** return null if there is not an ActivityId attribute in the XML data passed to this method **/
		
		return sActivityIdImported;
	}
	
	/**
	 * Get the Tasksequence id value from the imported data
	 * @param oAttribs
	 * @return a String representation of the Tasksequence id in the imported data 
	 * represented by the parameter oAttribs , or null if 
	 * there is no Tasksequence attribute in the XML data passed to this method.
	 */
	private String getImportedTaskSequenceId(NamedNodeMap oAttribs) {
		String sATaskSequenceIdImported = oAttribs.getNamedItem("TaskSequenceId").getNodeValue();
		/** return null if there is not an ActivityId attribute in the XML data passed to this method **/
		
		return sATaskSequenceIdImported;
	}

	/**
	 * Generates LdActivityTimes objects from the data in the supplied NodeList.
	 * The LdActivityTimes objects generated have only an id, TimesDisplayed 
	 * and timeUnit values. The method adds these objects to the hashtable 
	 * htLdActivityTimes.
	 * @param activityTimesDisplayed
	 */
	private void processActivityTimesDisplayed(NodeList activityTimesDisplayed) {
		int nActTimesDisp = activityTimesDisplayed.getLength();
		Vector<String> vtActivitytimesDisplayed = new Vector<String>(3); 
		String sNewIdForActivity = "";
		LdActivityTimes oCurrentActivityTimes;
		for (int i=0; i< nActTimesDisp; ++i)	{
			Node oActivitytimesDisplayed = activityTimesDisplayed.item(i);
			NamedNodeMap oAttribs = oActivitytimesDisplayed.getAttributes();
			/** Get an id for the activity depernding on the options selected by the user e.g., preserve node ids etc. 	**/
			sNewIdForActivity = this.getActivityId(oAttribs);
			// Get the activity id from the imported data
			String sActivityIdImported = oAttribs.getNamedItem("ActivityId").getNodeValue();
			/** If it's the root view, and it is not a copy , we need to update the LdActivityTimes instance for that view i.e. oLdActivityTimes.
			 * 	Otherwise create a new instance of LdActivityTimes.
			 */
			if (sActivityIdImported.equals(sRootView))	{
	//		if (sActivityIdImported.equals(sRootView))	{
				oCurrentActivityTimes = oLdActivityTimes;
			}
			
			else	{
					oCurrentActivityTimes = new LdActivityTimes(sNewIdForActivity);
					LdActivityView oLdActivityNode = (LdActivityView) htNewNodes.get(sActivityIdImported);
					oLdActivityNode.setLdActivityTimes(oCurrentActivityTimes);
			}
//			LdActivityView oLdActivityNode = (LdActivityView) htNewNodes.get(sActivityIdImported);
			String sTimesDisplayed = oAttribs.getNamedItem("TimesDisplayed").getNodeValue();
			oCurrentActivityTimes.setShowTime(new Boolean(sTimesDisplayed));		
			String sTimeUnit = oAttribs.getNamedItem("TimeUnit").getNodeValue();
			oCurrentActivityTimes.setCurrentTaskTimeUnits( TimeUnit.valueOf(sTimeUnit));
			this.htLdActivityTimes.put(sActivityIdImported, oCurrentActivityTimes);	
		}
	}

	/**
	 * Process the view with the given view id.
	 *
	 * @param viewid, the id of the view to process.
	 * @param view com.compendium.datamodel.IView, the parent view.
	 * @param oModel com.compendium.datamodel.IModel, - NOT CURRENTLY USED.
	 */
	protected void processView( String viewid, IView view, IModel model ) {

		// DO NOT CONITUNE IF EMPTY VIEW
		if (!htViews.containsKey((Object) viewid))
			return;

		Vector innerviews = new Vector(51);
		Vector nodes = (Vector)htViews.get( (Object) viewid );
		int counti = nodes.size();
		Date creationDate = null;
		Date modificationDate = null;

		for (int i=0; i<counti; i++) {

			Vector node = (Vector)nodes.elementAt(i);
			Object nodeid = node.elementAt(1);
			
			int xPos = new Integer((String)node.elementAt(2)).intValue();
			int yPos = new Integer((String)node.elementAt(3)).intValue();
			long lCreationDate = ((Long)node.elementAt(4)).longValue();
			long lModificationDate = ((Long)node.elementAt(5)).longValue();

			creationDate = new Date();
			creationDate.setTime(lCreationDate);
			modificationDate = new Date();
			modificationDate.setTime(lModificationDate);

			boolean bShowTags = ((Boolean)node.elementAt(6)).booleanValue();
			boolean bShowText = ((Boolean)node.elementAt(7)).booleanValue();
			boolean bShowTrans = ((Boolean)node.elementAt(8)).booleanValue();
			boolean bShowWeight = ((Boolean)node.elementAt(9)).booleanValue();
			boolean bSmallIcon = ((Boolean)node.elementAt(10)).booleanValue();
			boolean bHideIcon = ((Boolean)node.elementAt(11)).booleanValue();
			int nWrapWidth = ((Integer)node.elementAt(12)).intValue();
			int nFontSize = ((Integer)node.elementAt(13)).intValue();
			String sFontFace = (String)node.elementAt(14);
			int nFontStyle = ((Integer)node.elementAt(15)).intValue();
			int nForeground = ((Integer)node.elementAt(16)).intValue();
			int nBackground = ((Integer)node.elementAt(17)).intValue();			
			
			//ProjectCompendium.APP.displayError("Processing node = "+nodeid);

			// IF THIS NODE HAS ALREADY BEEN ADDED OR IT IS AN INNER REFERENCE TO THE ROOT VIEW
			if ( (!htNewNodes.containsKey(nodeid)) && !nodeid.equals(sRootView) ) {

				Node nextnode = (Node)htNodes.get(nodeid);
				if (nextnode != null) {
					
					processNode( model, view, nextnode, xPos, yPos, viewid, creationDate, modificationDate, 
							bShowTags, bShowText, bShowTrans, bShowWeight, bSmallIcon, bHideIcon,
							nWrapWidth, nFontSize, sFontFace, nFontStyle, nForeground, nBackground);

					NodeSummary newNode = (NodeSummary)htNewNodes.get((Object)nodeid);
					int nodeType = newNode.getType();

					if (nodeType == ICoreConstants.MAPVIEW || nodeType == ICoreConstants.LDMAPVIEW
								|| nodeType == ICoreConstants.LISTVIEW ) {

						//|| nodeType == ICoreConstants.MAP_SHORTCUT
						//|| nodeType == ICoreConstants.LIST_SHORTCUT)

						innerviews.add((Object)nodeid);
					}
				}

				//set the node count for progress bar
				nNodeCount++;
				oProgressBar.setValue(getCurrentCount());
				oProgressDialog.setStatus(getCurrentCount());
			}
			else {
				processNodeView( viewid, (String)nodeid, xPos, yPos, creationDate, modificationDate, 
						bShowTags, bShowText, bShowTrans, bShowWeight, bSmallIcon, bHideIcon,
						nWrapWidth, nFontSize, sFontFace, nFontStyle, nForeground, nBackground);
			}
		}

		int countj = innerviews.size();
		for (int j=0; j< countj; j++) {

			String nextviewid = (String)innerviews.elementAt(j);
			View nextView = (View) htNewNodes.get((Object)nextviewid);

			// ONLY PROCESS VIEWS CONTENTS IF IT IS NOT A TRANSCLUSION
			// IF THE NEXTVIEW ID MATCHES THE VIEW NODE ID, THEN ITS BEEN TRANSCLUDED
			// BUT CHECK YOUR NOT PRESERVING IDS
			if ( !nextviewid.equals(nextView.getId()) || DBNode.getPreserveImportedIds() ) {
				processView (nextviewid, nextView, model );
			}
		}
	}

	/**
	 * Creates an INodeSummary object and adds it to the model and view
	 *
	 * @param iLdType the learning design type of the node to add
	 * @param oModel NOT CURRENTLY USED.
	 * @param view the view to add the node to.
	 * @param currentViewId the id of the current view being imported into.
	 * @param nType the type of the node to add.
	 * @param importedid the id of the node in the importation information.
	 * @param sOriginalID the first orignal id of this node.
	 * @param author the author of this node.
	 * @param lCreationDate the date in milliseconds when this node was created.
	 * @param lModDate the date in milliseconds when this node was last modified.
	 * @param sLabel the label of the node to add.
	 * @param sDeail the first/main page of detail text for the node to add.
	 * @param sSource the path for external reference / url this node point to.
	 * @param sImage a path to any image file this node references.
	 * @param imagewidth the width to draw the node image.
	 * @param imageheight the height to draw the node image.
	 * @param ptPos the position in the given view to add to node at.
	 * @param transCreationDate the date the node was put into the view.
	 * @param transModDate the date the view-node data was last modified.
	 * @param sLastModAuthor the author who last modified this node.
	 * @param bShowTags true if this node has the tags indicator draw.
	 * @param bShowText true if this node has the text indicator drawn
	 * @param bShowTrans true if this node has the transclusion indicator drawn
	 * @param bShowWeight true if this node has the weight indicator displayed
	 * @param bSmallIcon true if this node is using a small icon
	 * @param bHideIcons true if this node is not displaying its icon
	 * @param nWrapWidth the node label wrap width used for this node in this view.
	 * @param nFontSize	the font size used for this node in this view
	 * @param sFontFace the font face used for this node in this view
	 * @param nFontStyle the font style used for this node in this view
	 * @param nForeground the foreground color used for this node in this view
	 * @param nBackground the background color used for this node in this view.
	 * 
	 * @exception Exception, if something goes wrong.
	 */
	protected INodeSummary createNode(int iLdType, int ldSubType, IModel oModel, IView  view, String currentViewId,
													 int		nType,
													 String		importedId,
													 String		sOriginalID,
													 String 	author,
													 long		lCreationDate,
													 long		lModDate,
													 String 	label,
													 String 	detail,
													 String		sSource,
													 String		sImage,
													 int		imagewidth,
													 int		imageheight,
													 String		background,
													 Point		ptPos,
													 Date 		transCreationDate,
													 Date 		transModDate,
													 String 	sLastModAuthor,
													 boolean 	bShowTags, 
													 boolean 	bShowText, 
													 boolean 	bShowTrans, 
													 boolean 	bShowWeight, 
													 boolean 	bSmallIcon, 
													 boolean 	bHideIcon, 
													 int 		nWrapWidth, 
													 int 		nFontSize, 
													 String 	sFontFace, 
													 int 		nFontStyle, 
													 int 		nForeground, 
													 int 		nBackground) throws Exception {


		if (iLdType == ILdCoreConstants.iLD_TYPE_NO_TYPE)	{
			return (this.createNode(oModel, view, currentViewId, nType, importedId, sOriginalID, author, lCreationDate, lModDate, label, detail, sSource, sImage, imagewidth, imageheight, background, ptPos, transCreationDate, transModDate, sLastModAuthor, bShowTags, bShowText, bShowTrans, bShowWeight, bSmallIcon, bHideIcon, nWrapWidth, nFontSize, sFontFace, nFontStyle, nForeground, nBackground));
		}
		else	{  // It's a Ld node
			//include the details only if import profile says so..
			if(bIncludeInDetail) {
				detail += includeInDetails(detail, author, lCreationDate, lModDate);
			}

			if (sOriginalID.equals("-1"))
				sOriginalID = "";

			Date oCreationDate = new Date(lCreationDate);
			Date oModfificationDate = new Date(lModDate);
			if (!bIsSmartImport) {
				Date date = new Date();
				oCreationDate = date;
				oModfificationDate = date;
				transCreationDate = date;
				transModDate = date;
				author = sCurrentAuthor;
				sLastModAuthor = sCurrentAuthor;
			}
			
			UILdNode uinode = oViewPaneUI.createLdNode(nType, importedId, sOriginalID, author, oCreationDate, 
					oModfificationDate, label, detail, ptPos.x, ptPos.y, transCreationDate, 
					transModDate, sLastModAuthor, bShowTags, bShowText, bShowTrans, bShowWeight, 
					bSmallIcon, bHideIcon, nWrapWidth, nFontSize, sFontFace, nFontStyle, nForeground, nBackground, iLdType);
			uinode.refineLdType(ldSubType);

			if(nType == ICoreConstants.REFERENCE || nType == ICoreConstants.REFERENCE_SHORTCUT) {
				uinode.getNode().setSource(sSource, sImage, author);
				uinode.getNode().setImageSize(new Dimension(imagewidth, imageheight), author);
				if (sImage == null || sImage.equals(""))
					uinode.setReferenceIcon(sSource);
				else
					uinode.setReferenceIcon(sImage);
			}
			else if(nType == ICoreConstants.MAPVIEW || nType == ICoreConstants.LDMAPVIEW || nType == ICoreConstants.MAP_SHORTCUT ||
					nType == ICoreConstants.LISTVIEW || nType == ICoreConstants.LIST_SHORTCUT) {

				uinode.getNode().setSource("", sImage, author);
				uinode.getNode().setImageSize(new Dimension(imagewidth, imageheight), author);			
				if (sImage != null && !sImage.equals(""))
					uinode.setReferenceIcon(sImage);
			}

			if (!background.equals("") && ( nType == ICoreConstants.MAPVIEW || nType == ICoreConstants.LDMAPVIEW || nType == ICoreConstants.MAP_SHORTCUT)) {
				View newview = (View)uinode.getNode();
				newview.setBackground(background);
				try { newview.updateViewLayer(); }
				catch(Exception ex) { System.out.println("Unable to update database with background image "+background);}
			}

			uinode.setRollover(false);
			uinode.setSelected(true);
			oViewPaneUI.getViewPane().setSelectedNode(uinode,ICoreConstants.MULTISELECT);

			// UNDO LIST
			vtNodeList.addElement(uinode);

			// FOR LINKS
			htUINodes.put((Object)importedId, (Object) uinode);

			// FOR CHECKING IN LOOP
			htNewNodes.put(importedId,  uinode.getNode());

			return uinode.getNode();
		}
		
		
		
	}
/**	
	protected INodeSummary createNodeObject(int iLdType)	{
		
	}
**/
	/**
	 * Creates an INodeSummary object and adds it to the model and view
	 *
	 * @param oModel com.compendium.datamodel.IModel, - NOT CURRENTLY USED.
	 * @param view com.compendium.datamodel.View, the view to add the node to.
	 * @param currentViewId, the id of the current view being imported into.
	 * @param nType, the type of the node to add.
	 * @param importedid, the id of the node in the importation information.
	 * @param sOriginalID, the first orignal id of this node.
	 * @param author, the author of this node.
	 * @param lCreationDate, the date in milliseconds when this node was created.
	 * @param lModDate, the date in milliseconds when this node was last modified.
	 * @param sLabel, the label of the node to add.
	 * @param sDeail, the first/main page of detail text for the node to add.
	 * @param sSource, the path for external reference / url this node point to.
	 * @param sImage, a path to any image file this node references.
	 * @param imagewidth the width to draw the node image.
	 * @param imageheight the height to draw the node image.
	 * @param ptPos, the position in the given view to add to node at.
	 * @param transCreationDate, the date the node was put into the view.
	 * @param transModDate, the date the view-node data was last modified.
	 * @param sLastModAuthor the author who last modified this node.
	 * @param bShowTags true if this node has the tags indicator draw.
	 * @param bShowText true if this node has the text indicator drawn
	 * @param bShowTrans true if this node has the transclusion indicator drawn
	 * @param bShowWeight true if this node has the weight indicator displayed
	 * @param bSmallIcon true if this node is using a small icon
	 * @param bHideIcons true if this node is not displaying its icon
	 * @param nWrapWidth the node label wrap width used for this node in this view.
	 * @param nFontSize	the font size used for this node in this view
	 * @param sFontFace the font face used for this node in this view
	 * @param nFontStyle the font style used for this node in this view
	 * @param nForeground the foreground color used for this node in this view
	 * @param nBackground the background color used for this node in this view. 
	 * @exception Exception, if something goes wrong.
	 */
	protected INodeSummary createListNode( IModel oModel, IView  view, String currentViewId,
													 int		nType,
													 String		importedId,
													 String		sOriginalID,
													 String 	author,
													 long		lCreationDate,
													 long		lModDate,
													 String 	label,
													 String 	detail,
													 String		sSource,
													 String 	sImage,
													 int		imagewidth,
													 int		imageheight,													 
													 String		background,
													 Point		ptPos,
													 Date 		transCreationDate,
													 Date 		transModDate,
													 String 	sLastModAuthor,
													 boolean 	bShowTags, 
													 boolean 	bShowText, 
													 boolean 	bShowTrans, 
													 boolean 	bShowWeight, 
													 boolean 	bSmallIcon, 
													 boolean 	bHideIcon, 
													 int 		nWrapWidth, 
													 int 		nFontSize, 
													 String 	sFontFace, 
													 int 		nFontStyle, 
													 int 		nForeground, 
													 int 		nBackground) throws Exception {

		//ptPos = adjustPosition( ptPos );

		if(bIncludeInDetail)
			detail += includeInDetails(detail, author, lCreationDate, lModDate);

		if (sOriginalID.equals("-1"))
			sOriginalID = "";
		else if (!sOriginalID.equals("") && lCreationDate < ICoreConstants.MYSQLDATE)
			sOriginalID = "QM"+sOriginalID;

		Date oCreationDate = new Date(lCreationDate);
		Date oModfificationDate = new Date(lModDate);
		if (!bIsSmartImport) {
			Date date = new Date();
			oCreationDate = date;
			oModfificationDate = date;
			transCreationDate = date;
			transModDate = date;
			author = sCurrentAuthor;
			sLastModAuthor = sCurrentAuthor;
		}

		NodePosition npTemp = oUIList.getListUI().createNode (nType, importedId, sOriginalID, author, oCreationDate, 
											oModfificationDate, label, detail, ptPos.x,
											(oUIList.getNumberOfNodes() + vtNodeList.size() + 1) * 10,
											transCreationDate, transModDate, sLastModAuthor, bShowTags, 
											bShowText, bShowTrans, bShowWeight, bSmallIcon, bHideIcon, 
											nWrapWidth, nFontSize, sFontFace, nFontStyle, nForeground, nBackground);

		INodeSummary node = npTemp.getNode();

		if(nType == ICoreConstants.REFERENCE || nType == ICoreConstants.REFERENCE_SHORTCUT) {
			node.setSource(sSource, sImage, author);
			node.setImageSize(new Dimension(imagewidth, imageheight), author);			
		}
		else if(nType == ICoreConstants.MAPVIEW ||nType == ICoreConstants.LDMAPVIEW || nType == ICoreConstants.MAP_SHORTCUT ||
				nType == ICoreConstants.LISTVIEW || nType == ICoreConstants.LIST_SHORTCUT) {
			node.setSource("", sImage, author);
			node.setImageSize(new Dimension(imagewidth, imageheight), author);			
		}

		if (!background.equals("") && (nType == ICoreConstants.MAPVIEW || nType == ICoreConstants.LDMAPVIEW || nType == ICoreConstants.MAP_SHORTCUT)) {
			((View)node).setBackground(background);
			try { ((View)node).updateViewLayer(); }
			catch(Exception ex) { System.out.println("Unable to update database with background image "+background);}
		}

		//setAuthorDate( (NodeSummary) node, author, lCreationDate, lModDate );

		// FOR UNDO
		vtNodeList.addElement(node);

		// FOR CHECKING IN LOOP
		htNewNodes.put( importedId,  node);

		return node;
	}

	/**
	 * Creates an INodeSummary object and adds it to the database
	 *
	 * @param oModel - NOT CURRENTLY USED.
	 * @param view the view to add the node to.
	 * @param currentViewId the id of the current view being imported into.
	 * @param nType the type of the node to add.
	 * @param importedid the id of the node in the importation information.
	 * @param sOriginalID the first orignal id of this node.
	 * @param author the author of this node.
	 * @param lCreationDate the date in milliseconds when this node was created.
	 * @param lModDate the date in milliseconds when this node was last modified.
	 * @param sLabel the label of the node to add.
	 * @param sDeail the first/main page of detail text for the node to add.
	 * @param sSource the path for external reference / url this node point to.
	 * @param sImage a path to any image file this node references.
	 * @param imagewidth the width to draw the node image.
	 * @param imageheight the height to draw the node image. 
	 * @param ptPos the position in the given view to add to node at.
	 * @param transCreationDate the date the node was put into the view.
	 * @param transModDate the date the view-node data was last modified.
	 * @param sLastModAuthor the author who last modified this imported node.
	 * @param bShowTags true if this node has the tags indicator draw.
	 * @param bShowText true if this node has the text indicator drawn
	 * @param bShowTrans true if this node has the transclusion indicator drawn
	 * @param bShowWeight true if this node has the weight indicator displayed
	 * @param bSmallIcon true if this node is using a small icon
	 * @param bHideIcons true if this node is not displaying its icon
	 * @param nWrapWidth the node label wrap width used for this node in this view.
	 * @param nFontSize	the font size used for this node in this view
	 * @param sFontFace the font face used for this node in this view
	 * @param nFontStyle the font style used for this node in this view
	 * @param nForeground the foreground color used for this node in this view
	 * @param nBackground the background color used for this node in this view.
	 * 
	 * @exception Exception, if something goes wrong.
	 */
	protected INodeSummary addNode( IModel oModel, IView  view, String currentViewId,
													 int		nType,
													 String		importedId,
													 String		sOriginalID,
													 String 	author,
													 long		lCreationDate,
													 long		lModDate,
													 String 	label,
													 String 	detail,
													 String		source,
													 String 	image,
													 int		imagewidth,
													 int		imageheight,													 
													 String		background,
													 Point		ptPos,
													 Date 		transCreationDate,
													 Date 		transModDate,
													 String 	sLastModAuthor,
													 boolean 	bShowTags, 
													 boolean 	bShowText, 
													 boolean 	bShowTrans, 
													 boolean 	bShowWeight, 
													 boolean 	bSmallIcon, 
													 boolean 	bHideIcon, 
													 int 		nWrapWidth, 
													 int 		nFontSize, 
													 String 	sFontFace, 
													 int 		nFontStyle, 
													 int 		nForeground, 
													 int 		nBackground) throws Exception {

		//System.out.println("ADDING NODE "+importedId);

		//Adjust the x and y coordinates so node close to the border can be fully seen
		//ptPos = adjustPosition( ptPos );

		//include the details only if import profile says so..
		if(bIncludeInDetail) {
			detail += includeInDetails(detail, author, lCreationDate, lModDate);
		}

		if (sOriginalID.equals("-1"))
			sOriginalID = "";
		else if (!sOriginalID.equals("") && lCreationDate < ICoreConstants.MYSQLDATE)
			sOriginalID = "QM"+sOriginalID;

		Date oCreationDate = new Date(lCreationDate);
		Date oModfificationDate = new Date(lModDate);
		if (!bIsSmartImport) {
			Date date = new Date();
			oCreationDate = date;
			oModfificationDate = date;
			transCreationDate = date;
			transModDate = date;
			author = sCurrentAuthor;
			sLastModAuthor = sCurrentAuthor;
		}

		NodePosition nodePos = view.addMemberNode(nType, "", importedId, sOriginalID, author, oCreationDate, 
				oModfificationDate, label, detail, ptPos.x, ptPos.y, transCreationDate, transModDate, 
				sLastModAuthor, bShowTags, bShowText, bShowTrans, bShowWeight, bSmallIcon, bHideIcon,
				nWrapWidth, nFontSize, sFontFace, nFontStyle, nForeground, nBackground);

		if(nType == ICoreConstants.REFERENCE || nType == ICoreConstants.REFERENCE_SHORTCUT) {
			nodePos.getNode().setSource(source, image, author);
			nodePos.getNode().setImageSize(new Dimension(imagewidth, imageheight), author);						
		}
		else if(nType == ICoreConstants.MAPVIEW || nType == ICoreConstants.LDMAPVIEW ||nType == ICoreConstants.MAP_SHORTCUT ||
				nType == ICoreConstants.LISTVIEW || nType == ICoreConstants.LIST_SHORTCUT) {
			nodePos.getNode().setSource("", image, author);
			nodePos.getNode().setImageSize(new Dimension(imagewidth, imageheight), author);			
		}

		if (!background.equals("") && (nType == ICoreConstants.MAPVIEW || nType == ICoreConstants.LDMAPVIEW ||nType == ICoreConstants.MAP_SHORTCUT)) {
			((View)nodePos.getNode()).setBackground(background);
			try { ((View)nodePos.getNode()).updateViewLayer(); }
			catch(Exception ex) { System.out.println("Unable to update database with background image "+background);}
		}

		// UNDO LIST
		vtNodeList.addElement(nodePos);

		// FOR CHECKING IN LOOP
		htNewNodes.put( importedId,  nodePos.getNode());

		// FOR LINKS
		htUINodes.put((Object)importedId, (Object) nodePos);

		// STORE NODEPOSITION FOR USE WITH INNERLINKS
		if (!htNodeView.containsKey((Object) currentViewId))
			htNodeView.put((Object) currentViewId, (Object) new Hashtable(51));

		Hashtable nextView = (Hashtable)htNodeView.get((Object) currentViewId);
		nextView.put( (Object) importedId, (Object) nodePos );
		htNodeView.put( (Object) currentViewId, (Object) nextView);

		return nodePos.getNode();
	}

	/**
	 * Creates a ILink object and adds it to the model and view
	 *
	 * @param oModel com.compendium.datamodel.IModel, - NOT CURRENTLY USED.
	 * @param view com.compendium.datamodel.View, the view to add the link to.
	 * @param currentViewId, the id of the current view being imported into.
	 * @param sType, the type of the link to add.
	 * @param importedid, the id of the link in the importation information.
	 * @param sOriginalID, the first orignal id of this link.
	 * @param sFromId, the id number of the node the link comes from.
	 * @param sToId, the id number of the node the link goes to.
	 * @param nArrow, the type of arrow heads to draw.
	 */
	protected void createLink( IModel oModel, IView  view, String currentViewId,
													String 	sType,
													String	sImportedId,
													String 	sOriginalID,
													String	sFromId,
													String	sToId,
													String  sLabel,
													int 	nArrow ) {

		if(view.getType() == ICoreConstants.MAPVIEW || view.getType() == ICoreConstants.LDMAPVIEW) {

			UINode fromUINode = (UINode)htUINodes.get((Object)sFromId);
			UINode toUINode = (UINode)htUINodes.get((Object)sToId);

			//int type = UILink.getLinkType(sType);

			NodeUI nodeui = toUINode.getUI();

			UILink uiLink = nodeui.createLink(sImportedId, fromUINode, toUINode, sType, sLabel, nArrow);

			if (oViewPaneUI != null) {
				uiLink.setRollover(false);
				uiLink.setSelected(true);
				oViewPaneUI.getViewPane().setSelectedLink(uiLink,ICoreConstants.MULTISELECT);
			}

			// FOR UNDO ON CANCEL
			vtLinkList.addElement(uiLink);
		}
	}

	/**
	 * Add a link to the datamodel view only.
	 *
	 * @param oModel com.compendium.datamodel.IModel, - NOT CURRENTLY USED.
	 * @param view com.compendium.datamodel.View, the view to add the link to.
	 * @param currentViewId, the id of the current view being imported into.
	 * @param sType, the type of the link to add.
	 * @param importedid, the id of the link in the importation information.
	 * @param sOriginalID, the first orignal id of this link.
	 * @param sFromId, the id number of the node the link comes from.
	 * @param sToId, the id number of the node the link goes to.
	 * @param nArrow, the type of arrow heads to draw.
	 */
	protected void addLink( IModel oModel, IView  view, String currentViewId,
													String	 	sType,
													String		sImportedId,
													String 		sOriginalID,
													String		sFromId,
													String		sToId,
													String 		sLabel,
													int nArrow) {

		if(view.getType() == ICoreConstants.MAPVIEW || view.getType() == ICoreConstants.LDMAPVIEW) {

			Hashtable viewNodePositions = (Hashtable)htNodeView.get((Object) currentViewId);
			if (viewNodePositions == null)
				return;

			NodePosition fromNode = (NodePosition) viewNodePositions.get((Object) sFromId);
			NodePosition toNode = (NodePosition) viewNodePositions.get((Object) sToId);

			if (sOriginalID.equals("-1"))
				sOriginalID = "";

			try {
				Link link = (Link)view.addMemberLink(sType,
												sImportedId,
												sOriginalID,
												sCurrentAuthor,
												fromNode.getNode(),
												toNode.getNode(),
												sLabel,
												nArrow);

				// FOR UNDO ON CANCEL
				vtLinkList.addElement(link);
			}
			catch(Exception ex) {
				ex.printStackTrace();
				System.out.println("Error: (XMLImport.addLink) \n\n"+ex.getMessage());
			}
		}
	}

	
	/**
	 * Process the given node data and create, as required, the associated NodeSummary objects and additional data.
	 *
	 * @param oModel com.compendium.datamodel.IModel, - NOT CURRENTLY USED.
	 * @param view com.compendium.datamodel.IView, the view to add the node to.
	 * @param node, the XML Node of Node data to process.
	 * @param xPos, the x position to add this node at.
	 * @param yPos, the y position to add this node at.
	 * @param currentViewId, the id of the current view being imported into.
	 * @param transCreationDate, the date the node was put into the view.
	 * @param transModDate, the date the view-node data was last modified.
	 * @param bShowTags true if this node has the tags indicator draw.
	 * @param bShowText true if this node has the text indicator drawn
	 * @param bShowTrans true if this node has the transclusion indicator drawn
	 * @param bShowWeight true if this node has the weight indicator displayed
	 * @param bSmallIcon true if this node is using a small icon
	 * @param bHideIcons true if this node is not displaying its icon
	 * @param nWrapWidth the node label wrap width used for this node in this view.
	 * @param nFontSize	the font size used for this node in this view
	 * @param sFontFace the font face used for this node in this view
	 * @param nFontStyle the font style used for this node in this view
	 * @param nForeground the foreground color used for this node in this view
	 * @param nBackground the background color used for this node in this view.
	 */
	protected void processNode( IModel model, IView view, Node node, int xPos, int yPos, String currentViewId, 
			Date transCreationDate, Date transModDate, boolean bShowTags, boolean bShowText, boolean bShowTrans, boolean bShowWeight, 
			boolean bSmallIcon, boolean bHideIcon, int nWrapWidth, int nFontSize, String sFontFace, 
			int nFontStyle, int nForeground, int nBackground) {

		NodeService nodeService = (NodeService)model.getNodeService();

  		NamedNodeMap attrs = node.getAttributes();
		Attr oType = (Attr)attrs.getNamedItem("type");

		int type = new Integer(oType.getValue()).intValue();
		String id = ((Attr)attrs.getNamedItem("id")).getValue();
		String extendedtype = ((Attr)attrs.getNamedItem("extendedtype")).getValue();

		String sOriginalID = "";
		if ( (Attr)attrs.getNamedItem("questmapid") != null) {
		 	sOriginalID	= "QM"+((Attr)attrs.getNamedItem("questmapid")).getValue();
		}
		else {
			sOriginalID = ((Attr)attrs.getNamedItem("originalid")).getValue();
		}

		String author = ((Attr)attrs.getNamedItem("author")).getValue();
		long created = new Long( ((Attr)attrs.getNamedItem("created")).getValue() ).longValue();
		long lastModified = new Long( ((Attr)attrs.getNamedItem("lastModified")).getValue() ).longValue();
		String label = ((Attr)attrs.getNamedItem("label")).getValue();
		String state = ((Attr)attrs.getNamedItem("state")).getValue();
		Point position = new Point( xPos, yPos );
		
		String sLastModAuthor = "";		
		if ((Attr)attrs.getNamedItem("lastModAuthor") != null) {
			sLastModAuthor = ((Attr)attrs.getNamedItem("lastModAuthor")).getValue();
		}
		if (sLastModAuthor == null || sLastModAuthor.equals("")) {
			sLastModAuthor = author;
		}

		String detail = "";
		String source = "";
		String image = "";
  		int imagewidth = 0;
		int imageheight = 0;
		String background = "";

		Node codes = null;
		Node shortcuts = null;
		Node details = null;
		Node mediaindexes = null;

		NodeList mychildren = node.getChildNodes();
		int mycount = mychildren.getLength();
      	for ( int j = 0; j < mycount; j++ ) {
			Node mychild = mychildren.item(j);
			String myname = mychild.getNodeName();

			if ( myname.equals("detail") ) {
				Node first = mychild.getFirstChild();
				if (first != null)
					detail = first.getNodeValue();
			}
			else if ( myname.equals("details") ) {
				details = mychild;
			}
			else if ( myname.equals("source") ) {
				Node first = mychild.getFirstChild();
				if (first != null) {
					source = first.getNodeValue();
					if (CoreUtilities.isFile(source))
						source = CoreUtilities.cleanPath(source);
				}
			}
			else if ( myname.equals("image") ) {
				Node first = mychild.getFirstChild();
				if (first != null) {
					image = first.getNodeValue();
					image = CoreUtilities.cleanPath( image );
			  		NamedNodeMap imageattrs = mychild.getAttributes();
					Attr oWidth = (Attr)imageattrs.getNamedItem("width");
					if (oWidth != null) {
						imagewidth = new Integer(oWidth.getValue()).intValue();
					}
					Attr oHeight = (Attr)imageattrs.getNamedItem("height");
					if (oHeight != null) {
						imageheight = new Integer(oHeight.getValue()).intValue();
					}					
				}
			}
			else if ( myname.equals("background" )) {
				Node first = mychild.getFirstChild();
				if (first != null) {
					background = first.getNodeValue();
					background = CoreUtilities.cleanPath( background );
				}
			}
			else if ( myname.equals("coderefs") )
				codes = mychild;
			else if ( myname.equals("shortcutrefs") )
				shortcuts = mychild;
		}

		boolean didExist = false;
		try {
			if (sOriginalID.startsWith("QM") && DBNode.getImportAsTranscluded()) {
				didExist = nodeService.doesNodeExist(oSession, sOriginalID);
			}
			else if (!id.equals("0") && !id.equals("-1") && !id.equals("") && DBNode.getImportAsTranscluded()) {
				didExist = nodeService.doesNodeExist(oSession, id);
			}
		}
		catch(Exception ex) {
			System.out.println("Exception: trying to find out if nodes exist already in XMLImport");
		}

		//System.out.println("Did exist = "+didExist);
		// Work out if it's  Ld node, and if so, what type
		Hashtable<String, Code> oCodesForNodes = this.getCodes(codes, didExist);
		// Code oCode = this.getCodes(codes, didExist);
		int ldType = ILdCoreConstants.iLD_TYPE_NO_TYPE;
		int ldSubType = ILdCoreConstants.iLD_TYPE_NO_TYPE;
		//Get the ldType - retrieve via the upper table
		Enumeration<Code> oCodeElements =oCodesForNodes.elements();
		if (!oCodesForNodes.isEmpty())	{
			while ( oCodeElements.hasMoreElements())	{
				String sCodeName = oCodeElements.nextElement().getName();
				if (ProjectCompendium.APP.getLdTypeTagMaps().getUpperTagtoTypesTable().containsKey(sCodeName))
					ldType = ProjectCompendium.APP.getLdTypeTagMaps().getUpperTagtoTypesTable().get(sCodeName);
				if (ProjectCompendium.APP.getLdTypeTagMaps().getTagtoSubTypesTable().containsKey(sCodeName))
					ldSubType = ProjectCompendium.APP.getLdTypeTagMaps().getTagtoSubTypesTable().get(sCodeName);
			}
			
			
		}
		// CREATE NEW NODE OBJECT
		
		NodeSummary newNode = null;
		try	{
			// CHECK TO SEE IF WANT TO DRAW NODE OR JUST ADD TO DATA STRUCTURE
			//if (currentViewId.equals(sRootView) && !bIsCopying) {
			if (currentViewId.equals(sRootView)) {	
				if (bIsListImport) {
					newNode = (NodeSummary) createListNode(model, view, currentViewId, type, id, sOriginalID, 
							author, created, lastModified, label, detail, source, image, imagewidth, imageheight, background, position, 
							transCreationDate, transModDate, sLastModAuthor,
							bShowTags, bShowText, bShowTrans, bShowWeight, bSmallIcon, bHideIcon,
							nWrapWidth, nFontSize, sFontFace, nFontStyle, nForeground, nBackground);
				}
				else {
					newNode = (NodeSummary) createNode(ldType, ldSubType, model, view, currentViewId, type, id, sOriginalID, 
							author, created, lastModified, label, detail, source, image, imagewidth, imageheight, background, 
							position, transCreationDate, transModDate, sLastModAuthor,
							bShowTags, bShowText, bShowTrans, bShowWeight, bSmallIcon, bHideIcon,
							nWrapWidth, nFontSize, sFontFace, nFontStyle, nForeground, nBackground);
				}
			}
			else {
				/** Need to create addLdNode(...) method to account for link between activity node and task sequences	**/
				newNode = (NodeSummary) addNode(model, view, currentViewId, type, id, sOriginalID,
						author, created, lastModified, label, detail, source, image, imagewidth, imageheight, background, 
						position, transCreationDate, transModDate, sLastModAuthor,
						bShowTags, bShowText, bShowTrans, bShowWeight, bSmallIcon, bHideIcon,
						nWrapWidth, nFontSize, sFontFace, nFontStyle, nForeground, nBackground);
			}
		}
		catch	(Exception e)	{
			e.printStackTrace();
			ProjectCompendium.APP.displayError("Exception: (XMLImport.processNode) ("+id+") "+e.getMessage());
		}

		if (newNode == null)
			return;

		if ( codes != null )
			processCodeRefs( codes, newNode, didExist );

		if ( details != null )
			processDetailPages( details, newNode, didExist, created, lastModified );

		// CHECK DONE IN process Shortcuts AS Might be a transcluded node to new shortcut
		if (shortcuts != null)
			processShortcutRefs( shortcuts, id );
	}

	/**
	 * Process the given XML Node which contains a list of all Code data to add.
	 *
	 * @param node com.compendium.datamodel.NodeSummary, the Node the codes should be added to..
	 * @param didExist, indicates if a node with the same id as the node passed already existed in this database.
	 */
	protected Hashtable<String, Code> getCodes(Node node,  boolean didExist) {

		NodeList children = node.getChildNodes();
		int count = children.getLength();
		Code code = null;
		Hashtable<String, Code> htNodeCodes = new Hashtable<String, Code>();
		try {
	      	for ( int i = 0; i < count; i++ ) {

				Node child = children.item(i);
				String name = child.getNodeName();
				if ( name.equals("coderef") ) {

			 		NamedNodeMap attrs = child.getAttributes();
					String id = ((Attr)attrs.getNamedItem("coderef")).getValue();

					if (htCodes.containsKey((Object)id)) {
						code = (Code)htCodes.get((Object)id);
//						if (!didExist || (didExist && DBNode.getUpdateTranscludedNodes()))
						//return code;
						htNodeCodes.put(id, code);
					}
				}
			}	
		}
		
		catch(Exception ex) {
			System.out.println("Error: (XMLImport.processCodeRefs) \n\n"+ex.getMessage());
		}
		return htNodeCodes;	
	}
	
	/**
	 * Creates an INodeSummary object and adds it to the model and view
	 *
	 * @param oModel NOT CURRENTLY USED.
	 * @param view the view to add the node to.
	 * @param currentViewId the id of the current view being imported into.
	 * @param nType the type of the node to add.
	 * @param importedid the id of the node in the importation information.
	 * @param sOriginalID the first orignal id of this node.
	 * @param author the author of this node.
	 * @param lCreationDate the date in milliseconds when this node was created.
	 * @param lModDate the date in milliseconds when this node was last modified.
	 * @param sLabel the label of the node to add.
	 * @param sDeail the first/main page of detail text for the node to add.
	 * @param sSource the path for external reference / url this node point to.
	 * @param sImage a path to any image file this node references.
	 * @param imagewidth the width to draw the node image.
	 * @param imageheight the height to draw the node image.
	 * @param ptPos the position in the given view to add to node at.
	 * @param transCreationDate the date the node was put into the view.
	 * @param transModDate the date the view-node data was last modified.
	 * @param sLastModAuthor the author who last modified this node.
	 * @param bShowTags true if this node has the tags indicator draw.
	 * @param bShowText true if this node has the text indicator drawn
	 * @param bShowTrans true if this node has the transclusion indicator drawn
	 * @param bShowWeight true if this node has the weight indicator displayed
	 * @param bSmallIcon true if this node is using a small icon
	 * @param bHideIcons true if this node is not displaying its icon
	 * @param nWrapWidth the node label wrap width used for this node in this view.
	 * @param nFontSize	the font size used for this node in this view
	 * @param sFontFace the font face used for this node in this view
	 * @param nFontStyle the font style used for this node in this view
	 * @param nForeground the foreground color used for this node in this view
	 * @param nBackground the background color used for this node in this view.
	 * 
	 * @exception Exception, if something goes wrong.
	 */
	private INodeSummary createNode( IModel oModel, IView  view, String currentViewId,
													 int		nType,
													 String		importedId,
													 String		sOriginalID,
													 String 	author,
													 long		lCreationDate,
													 long		lModDate,
													 String 	label,
													 String 	detail,
													 String		sSource,
													 String		sImage,
													 int		imagewidth,
													 int		imageheight,
													 String		background,
													 Point		ptPos,
													 Date 		transCreationDate,
													 Date 		transModDate,
													 String 	sLastModAuthor,
													 boolean 	bShowTags, 
													 boolean 	bShowText, 
													 boolean 	bShowTrans, 
													 boolean 	bShowWeight, 
													 boolean 	bSmallIcon, 
													 boolean 	bHideIcon, 
													 int 		nWrapWidth, 
													 int 		nFontSize, 
													 String 	sFontFace, 
													 int 		nFontStyle, 
													 int 		nForeground, 
													 int 		nBackground) throws Exception {


		//Adjust the x and y coordinates so node close to the border can be fully seen
		//ptPos = adjustPosition( ptPos );
		
		//include the details only if import profile says so..
		if(bIncludeInDetail) {
			detail += includeInDetails(detail, author, lCreationDate, lModDate);
		}

		if (sOriginalID.equals("-1"))
			sOriginalID = "";

		Date oCreationDate = new Date(lCreationDate);
		Date oModfificationDate = new Date(lModDate);
		if (!bIsSmartImport) {
			Date date = new Date();
			oCreationDate = date;
			oModfificationDate = date;
			transCreationDate = date;
			transModDate = date;
			author = sCurrentAuthor;
			sLastModAuthor = sCurrentAuthor;
		}

		UINode uinode = oViewPaneUI.createNode(nType, importedId, sOriginalID, author, oCreationDate, 
				oModfificationDate, label, detail, ptPos.x, ptPos.y, transCreationDate, 
				transModDate, sLastModAuthor, bShowTags, bShowText, bShowTrans, bShowWeight, 
				bSmallIcon, bHideIcon, nWrapWidth, nFontSize, sFontFace, nFontStyle, nForeground, nBackground);

		if(nType == ICoreConstants.REFERENCE || nType == ICoreConstants.REFERENCE_SHORTCUT) {
			uinode.getNode().setSource(sSource, sImage, author);
			uinode.getNode().setImageSize(new Dimension(imagewidth, imageheight), author);
			if (sImage == null || sImage.equals(""))
				uinode.setReferenceIcon(sSource);
			else
				uinode.setReferenceIcon(sImage);
		}
		else if(nType == ICoreConstants.MAPVIEW || nType == ICoreConstants.LDMAPVIEW || nType == ICoreConstants.MAP_SHORTCUT ||
				nType == ICoreConstants.LISTVIEW || nType == ICoreConstants.LIST_SHORTCUT) {

			uinode.getNode().setSource("", sImage, author);
			uinode.getNode().setImageSize(new Dimension(imagewidth, imageheight), author);			
			if (sImage != null && !sImage.equals(""))
				uinode.setReferenceIcon(sImage);
		}

		if (!background.equals("") && ( nType == ICoreConstants.MAPVIEW || nType == ICoreConstants.LDMAPVIEW || nType == ICoreConstants.MAP_SHORTCUT)) {
			View newview = (View)uinode.getNode();
			newview.setBackground(background);
			try { newview.updateViewLayer(); }
			catch(Exception ex) { System.out.println("Unable to update database with background image "+background);}
		}

		uinode.setRollover(false);
		uinode.setSelected(true);
		if (oViewPaneUI != null)	{
			oViewPaneUI.getViewPane().setSelectedNode(uinode,ICoreConstants.MULTISELECT);
		}


		// UNDO LIST
		vtNodeList.addElement(uinode);

		// FOR LINKS
		htUINodes.put((Object)importedId, (Object) uinode);

		// FOR CHECKING IN LOOP
		htNewNodes.put( importedId,  uinode.getNode());

		return uinode.getNode();
	}
	
	/**
	 * Display a warning message about importing learning activity data into a
	 * standard compendium map.
	 */
	private void displayWarning()	{
		String message = 	"You are importing learning activity data into a standard Compendium map.\n" +
		"If you want to use the full  functionality of CompendiumLD (e.g. for\n " +
		"showing timing data) please create a learning activity map then import\n" +
		"this data into the learning activity map." ;
		ProjectCompendium.APP.displayMessage(message, "Warning!!!");
	}
	
}
