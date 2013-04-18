/********************************************************************************
 *                                                                              *
/*  (c) Copyright 2009 Verizon Communications USA and The Open University UK    *
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
 *  possibility of such damage.   
 *  
 *   Notes
 *  
 *   In class XMLExport
 *	run() calls converToXML().
 * The method convertToXML() creates the XML string which is written to the file. 
 * It does this by creating an initial StrinngBuffer containing the DTD reference. 
 * It then calls either
 * processNodeForExport(oCurrentView, oCurrentView.getParentNode());
 * or 
 * processSelectedNodesForExport()
 * to create the data structures for each NodeSummary or View.
  * It  then calls processDataToXML() which calls
 * processViewsToXML(), 
 * processNodesToXML(), 
 * processLinksToXML() and 
 * processCodesToXML().
 * These processXToXML() methods process the dta structures and append XML data to the StringBuffer to complete the XML document.
 *
 *                                                                              *
 ********************************************************************************/

package com.compendium.learningdesign.io.xml;

import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.compendium.ProjectCompendium;
import com.compendium.core.CoreUtilities;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.Link;
import com.compendium.core.datamodel.MediaIndex;
import com.compendium.core.datamodel.NodeDetailPage;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.View;
import com.compendium.core.datamodel.ViewLayer;
import com.compendium.io.xml.XMLExport;
import com.compendium.ui.UILink;
import com.compendium.ui.UIListViewFrame;
import com.compendium.ui.UIMapViewFrame;
import com.compendium.ui.UINode;
import com.compendium.ui.UIViewFrame;

import com.compendium.learningdesign.core.ILdCoreConstants;
import com.compendium.learningdesign.core.datamodel.*;
import com.compendium.learningdesign.ui.UILdActivityViewFrame;
import com.compendium.learningdesign.util.TimeUnit;

import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class LdXMLExport extends XMLExport {
	
	/** Holds processed task sequences for eliminating duplication on export. */
	protected	Hashtable<String, String>			htActivityTaskSequenceCheck 		= new Hashtable<String, String>(51);
	
	/** Vector to store ActivityTimesDisplayed data	**/
	private Vector<Vector<String>> vtActivityTimesDisplayed = new Vector<Vector<String>>(51);
	
	/** Vector to store the TaskSequenceTask data	**/
	private Vector<Vector<String>> vtTaskSequencesTasks = new Vector<Vector<String>>(51) ;

	/** Vector to store the TaskTimes data	**/
	private Vector<Vector<String>> vtTaskTimes = new Vector<Vector<String>>(51) ;
	
	/** Vector to store the TaskSequenceRole data	**/
	private Vector<Vector<String>> vtTaskSequenceRoles= new Vector<Vector<String>>(51) ;
	
	/** Vector to store the TaskSequenceActivity data	**/
	private Vector<Vector<String>> vtTaskSequencesActivities= new Vector<Vector<String>>(51) ;
	
	/** XMLOutputter used to render jdom XML objects as Strings	**/
	XMLOutputter oXMLOutputter = new XMLOutputter();
	
	public LdXMLExport(UIViewFrame frame, String path, boolean allDepths,
			boolean selectedOnly, boolean withResources,
			boolean withStencilAndLinkGroups, boolean withMeetings,
			boolean showFinalMessage) {
		super(frame, path, allDepths, selectedOnly, withResources,
				withStencilAndLinkGroups, withMeetings, showFinalMessage);
		/* If it's a UILdActivityViewFrame update the activity timing information before the export is started so that any changes made
		 * are in the data to be exported 
		 */
		if (frame  instanceof  UILdActivityViewFrame)
			{
			((UILdActivityViewFrame) frame).updateLdActivityTimes();
			}
		Format oFormat = Format.getPrettyFormat();
		oFormat.setEncoding("UTF16");
		oXMLOutputter.setFormat(oFormat);
	}

	/**
	 * Constructor which carries out the export without setting up a thread to 
	 * complete the export. This is for use in a power export, and requires the 
	 * boolean noThread to be true for this constructor to carry out the export.
	 * @param frame
	 * @param path
	 * @param allDepths
	 * @param selectedOnly
	 * @param bWithResources
	 * @param bWithStencilAndLinkGroups
	 * @param bWithMeetings
	 * @param bShowFinalMessage
	 * @param noThread
	 */
	public LdXMLExport(UIViewFrame frame, String path, boolean allDepths, boolean selectedOnly,
			boolean bWithResources, boolean bWithStencilAndLinkGroups, boolean bWithMeetings, 
			boolean bShowFinalMessage, boolean noThread) {
		super(frame, path, allDepths, selectedOnly, bWithResources,
				bWithStencilAndLinkGroups, bWithMeetings, bShowFinalMessage);
		oThread = new ProgressThread();
		oThread.start();
		if (noThread)	{
			convertToXML();
			onCompletion();
			bExportComplete = true;	
		}
	}
	
	public LdXMLExport(View oView, String path, boolean allDepths,
			boolean selectedOnly, boolean showFinalMessage) {
		super(oView, path, allDepths, selectedOnly, false,
				false, false, showFinalMessage);
		/* If it's a UILdActivityViewFrame update the activity timing information before the export is started so that any changes made
		 * are in the data to be exported 
		 */
/**
		if (View.isLdActivityViewType(oView.getLdType())  )
			{
			((UILdActivityViewFrame) frame).updateLdActivityTimes();
			}
**/
		Format oFormat = Format.getPrettyFormat();
		oFormat.setEncoding("UTF16");
		oXMLOutputter.setFormat(oFormat);
	}
	/**
	 * Convert Compendium node/s into xml output.
	 * This is an implementation of the convertToXML() method from the XMLExport class
	 * uusing the jdom library.
	 */
	public void convertToXML() {
		/** Note even though the root view being exported may not be an LdActivityView,
		 *  still have to export all as if they were in case views within views are 
		 *  LdActivity views	**/ 
		
		StringBuffer root = new StringBuffer(1000); // This variable can be deleted once conversion to jdom is complete
		// The jdom xml document to be created
		Document doc = new Document();  // Not used yet
		//Need to change DTD reference to compendiumld.dtd at a URL
		DocType xmlDocType = new DocType("model", "CompendiumLD.dtd"); // Not used yet
		ProjectCompendium.APP.setWaitCursor();
		Element rootElem = new Element("model");  // Not used yet		
		
		root.append("<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n");

		//root.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		root.append("<!DOCTYPE model SYSTEM \"CompendiumLD.dtd\">\n");

		root.append("<model ");

		htNodesCheck.clear();
		htCodesCheck.clear();
		htLinksCheck.clear();
		htMeetings.clear();
		vtNodes.removeAllElements();
		vtCodes.removeAllElements();
		vtLinks.removeAllElements();
		
		try {
			if (this.getCurrentView() != null) {
				doc.setDocType(xmlDocType); //Not used yet
				rootElem.setAttribute("rootview", oCurrentView.getId()); //Not used yet
				doc.setRootElement(rootElem); //Not used yet
				root.append( "rootview=\""+oCurrentView.getId()+"\">\n");
				// PROCESS SELECTED NODES AND LINKS ONLY
				if (bSelectedOnly) {
					processSelectedNodesForExport();
				}
				else {	// PROCESS ALL NODES AND LINKS
					int count = 0;

					if (!bAllDepths)
						count = oCurrentView.getNumberOfNodes();
					else {
						nCount += 2;
						oProgressBar.setValue(nCount);
						oProgressDialog.setStatus(nCount);
						count = countDepth(oCurrentView);
					}

			  		oProgressBar.setMaximum(count+12);

					processNodeForExport(oCurrentView, oCurrentView.getParentNode());
				}
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Exception: (XMLExport.convertToXML) " + ex.getMessage());
			oProgressDialog.setVisible(false);
			oProgressDialog.dispose();
			ProjectCompendium.APP.setStatus("");
			bHasFailed = true;
			return;
		}

		if (bXMLExportCancelled || checkProgress()) {
			root = null;
			bHasFailed = true;
			return;
		}
		nCount += 3;
		oProgressBar.setValue(nCount);
		oProgressDialog.setStatus(nCount);

		root.append( processDataToXML() );
		/** For when jdom is used for output of all node and view data
		String sXmlDoc = oXMLOutputter.outputString(doc);
		String sXMLDocContents = processDataToXML();
		sXmlDoc += sXMLDocContents;
		root = new StringBuffer(sXmlDoc);
		**/
		if (bXMLExportCancelled || checkProgress()) {
			root = null;
			bHasFailed = true;
			return;
		}
		nCount +=3;
		oProgressBar.setValue(nCount);
		oProgressDialog.setStatus(nCount);

		root.append("</model>");
//		sXmlDoc += "</model>";

		// SAVE TO FILE
		if (bWithResources) {

			if (bWithStencilAndLinkGroups) {
				addLinkGroupsToResources();
				addStencilsToResources();
			}

			nCount += 3;
			oProgressBar.setValue(nCount);
			oProgressDialog.setStatus(nCount);

			nCount = 0;
			oProgressBar.setValue(0);
	  		oProgressBar.setMinimum(0);
			oProgressBar.setMaximum(htResources.size()+1);
			oProgressDialog.setMessage("Writing to zip..");
			oProgressDialog.setStatus(0);

			// ZIP ALL TOGETHER
			try {
				int BUFFER = 2048;
				BufferedInputStream origin = null;
				FileInputStream fi = null;

				FileOutputStream dest = new FileOutputStream(oZipfile.getAbsolutePath());
				ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
				out.setMethod(ZipOutputStream.DEFLATED);
				byte data2[] = new byte[BUFFER];

				ZipEntry entry = null;
				
				//ADD SQL FILE
				String sXMLFilePath = "Exports/"+sBackupName+".xml";
				String sqlFile = root.toString();
				// NEED TO WRITE MAIN XML FILE OUT TO FILE FIRST AS NEED TO ENCODE IT TO UTF16
				try {
					FileOutputStream fos = new FileOutputStream(sXMLFilePath);
					Writer out2 = new OutputStreamWriter(fos, "UTF16");
					out2.write(sqlFile);
					out2.close();
					
					fi = new FileInputStream(sXMLFilePath);
					origin = new BufferedInputStream(fi, BUFFER);
					entry = new ZipEntry(sXMLFilePath);
					out.putNextEntry(entry);

					int count = 0;
					while((count = origin.read(data2, 0, BUFFER)) != -1) {
						out.write(data2, 0, count);
					}
					origin.close();

					CoreUtilities.deleteFile(new File(sXMLFilePath));
					
					nCount +=1;
					oProgressBar.setValue(nCount);
					oProgressDialog.setStatus(nCount);					
				}
				catch (IOException e) {
					ProjectCompendium.APP.displayError("Exception:" + e.getMessage());
				}

				// ADD RESOURCES
				int count = 0;
				for (Enumeration e = htResources.keys(); e.hasMoreElements() ;) {
					String sOldFilePath = (String)e.nextElement();
					String sNewFilePath = (String)htResources.get(sOldFilePath);
					try {
						fi = new FileInputStream(sOldFilePath);
						origin = new BufferedInputStream(fi, BUFFER);

						entry = new ZipEntry(sNewFilePath);
						out.putNextEntry(entry);

						while((count = origin.read(data2, 0, BUFFER)) != -1) {
							out.write(data2, 0, count);
						}
						origin.close();

						nCount +=1;
						oProgressBar.setValue(nCount);
						oProgressDialog.setStatus(nCount);
					}
					catch (Exception ex) {
						System.out.println("Unable to backup database resource: \n\n"+sOldFilePath+"\n\n"+ex.getMessage());
					}
				}
				out.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}

			oProgressDialog.setVisible(false);
			oProgressDialog.dispose();

			if (sFilePath != null && bShowFinalMessage) {
				Thread thread = new Thread("LdXMLExport.convertToXML") {
					public void run() {
						String sMessage = "Finished exporting into " + sFilePath;
						if (isBNotFound())
							sMessage += "\n\nOne or more reference files could not be found.\n\nPlease check the log file for details.";

						ProjectCompendium.APP.displayMessage(sMessage, "Export Finished");
					}
				};
				thread.start();
			}
		}

		// SAVE TO XML file without linked resources
		else {
			try {
				FileOutputStream fos = new FileOutputStream(sFilePath);
				Writer out = new OutputStreamWriter(fos, "UTF16");
				out.write(root.toString());
				out.close();

				//FileWriter fileWriter = new FileWriter(sFilePath);
				//fileWriter.write(root.toString());
				//fileWriter.close();

				nCount += 3;
				oProgressBar.setValue(nCount);
				oProgressDialog.setStatus(nCount);

			}
			catch (IOException e) {
				ProjectCompendium.APP.displayError("Exception:" + e.getMessage());
			}

			oProgressDialog.setVisible(false);
			oProgressDialog.dispose();

			if (sFilePath != null && bShowFinalMessage) {
				Thread thread = new Thread("XMLExport.convertToXML") {
					public void run() {
						ProjectCompendium.APP.displayMessage("Finished exporting into " + sFilePath, "Export Finished");
					}
				};
				thread.start();
			}
		}

		ProjectCompendium.APP.setStatus("");
	}

	/**
	 * Process the data gathered into XML output
	 *
	 * @return String, the xml formatted string representing a Compendium map/list or group of nodes/links
	 */
	public String processDataToXML() {
		StringBuffer xml = new StringBuffer(1000);

		xml.append( processViewsToXML() );
		xml.append( processNodesToXML() );
		xml.append( processLinksToXML() );
		xml.append( processCodesToXML() );
		xml.append(processTaskSequencesToXML()); 

		if (bWithMeetings) {
			xml.append( processMeetingsToXML() );
		}

		return xml.toString();
	}
	
	/**
	 * @return String, representing the TaskSequence data as XML
	 */
	private String processTaskSequencesToXML() {
		// Initialsise the String which will be returned.
		String sTaskSequences = "";
	
		//* Create TaskSequences element
		Element elementTaskSequences = new Element("TaskSequences");
		elementTaskSequences.addContent(new Comment("Learning design task timing data"));
		//* Create TaskSequenceTasks element
		Element elementTaskSequenceTasks = processTaskSequenceTaskToXML();
		// Add ActivityTimesDisplays element as a child of TaskSequences element if it is not empty
		if (elementTaskSequenceTasks.getChildren().size() != 0)
			elementTaskSequences.addContent(elementTaskSequenceTasks);
		//* Create TaskTimes element
		Element elementTaskTimes = this.processTaskTimesToXML();
		// Add elementTaskTimes as a child of TaskSequences element
		if (elementTaskTimes.getChildren().size() != 0)
			elementTaskSequences.addContent(elementTaskTimes);
		//* Create TaskSequencesRoles element
		Element elementTaskSequencesRoles =  processTaskSequenceRolesToXML();
		// Add element TaskSequencesRoles as a child of TaskSequences element
		if (elementTaskSequencesRoles.getChildren().size() != 0)
			elementTaskSequences.addContent(elementTaskSequencesRoles);
		// Create TaskSequencesActivities element
		Element elementTaskSequencesActivities = processTaskSequenceActivitiesToXML();
		// Add element TaskSequencesActivities as a child of TaskSequences element
		if (elementTaskSequencesActivities.getChildren().size() != 0)
			elementTaskSequences.addContent(elementTaskSequencesActivities);
		// Create ActivityTimesDisplays element
		Element elementActivityTimesDisplays = this.processActivityTimesDisplayToXML();
		// Add ActivityTimesDisplays elemenmt as a child of TaskSequences element
		if (elementActivityTimesDisplays.getChildren().size() != 0)	{
			elementTaskSequences.addContent(elementActivityTimesDisplays);
		/** Render jdom elementActivityTimesDisplays as a String **/
			 sTaskSequences = oXMLOutputter.outputString(elementTaskSequences);
			}
		return sTaskSequences;		
	}
	
	/**
	 * Generate jdom XML elements representing the ActivityTimesDisplayed data
	 * held within the Vector vtActivityTimesDisplayed.
	 * @return Element elementActivityTimesDisplays (containg sub elements, attributes etc).
	 */
	private Element processActivityTimesDisplayToXML() {
		/* vtThisActivityTimesDisplayed VECTOR FOR REFERENCE (all data as Strings)
		0 = id
		1 = bShowTime
		2 = oCurrentTimeUnits
		*/
		Element elementActivityTimesDisplays = new Element("ActivityTimesDisplays");
		Vector<String> vtNextActivityTimesDisplayed = null;
		int counti = vtActivityTimesDisplayed.size();
		Iterator<Vector<String>> it = vtActivityTimesDisplayed.iterator();
		
		while (it.hasNext())	{
			vtNextActivityTimesDisplayed = it.next();
			Element elementActivityTimesDisplayed = new Element("ActivityTimesDisplayed");
			elementActivityTimesDisplayed.setAttribute("ActivityId", vtNextActivityTimesDisplayed.get(0));
			elementActivityTimesDisplayed.setAttribute("TimesDisplayed", vtNextActivityTimesDisplayed.get(1));
			elementActivityTimesDisplayed.setAttribute("TimeUnit", vtNextActivityTimesDisplayed.get(2));
			elementActivityTimesDisplays.addContent(elementActivityTimesDisplayed);
		}
		
		/** Render jdom elementActivityTimesDisplays as a String **/
		//String sActivityTimes = oXMLOutputter.outputString(elementActivityTimesDisplays);
		return elementActivityTimesDisplays;
	}

	/**
	 * Generate jdom XML elements representing the TaskSequenceTask data
	 * held within the Vector vtTaskSequencesTasks.
	 * @return Element elementTaskSequenceTasks (containg sub elements, attributes etc).
	 */
	private Element processTaskSequenceTaskToXML() {
		/* vtThisTaskSequenceTask VECTOR FOR REFERENCE (all data as Strings)
		0 = TaskSequenceId
		1 = TaskId
		*/
		Element elementTaskSequenceTasks = new Element("TaskSequencesTasks");
		Vector<String> vtNextTaskSequenceTask = null;
		Iterator<Vector<String>> it = vtTaskSequencesTasks.iterator();
		
		while (it.hasNext())	{
			vtNextTaskSequenceTask = it.next();
			Element elementTaskSequenceTask = new Element("TaskSequenceTask");
			elementTaskSequenceTask.setAttribute("TaskSequenceId", vtNextTaskSequenceTask.get(0));
			elementTaskSequenceTask.setAttribute("TaskId", vtNextTaskSequenceTask.get(1));
			elementTaskSequenceTasks.addContent(elementTaskSequenceTask);
		}
		
	return elementTaskSequenceTasks;	
	}
	
	/**
	 * Generate jdom XML elements representing the TaskTimes data
	 * held within the Vector vtTaskTimes.
	 * @return Element elementTaskTimes (containg sub elements, attributes etc).
	 */
	private Element processTaskTimesToXML() {
		/* vtThisTaskTime VECTOR FOR REFERENCE (all data as Strings)
		0 = TaskId
		1 = TimeUnit
		2 = Time
		3 = showTime 
		*/
		Element elementTaskTimes = new Element("TaskTimes");
		Vector<String> vtNextTaskTime = null;
		Iterator<Vector<String>> it = vtTaskTimes.iterator();
		
		while (it.hasNext())	{
			vtNextTaskTime = it.next();
			Element elementTaskTime = new Element("TaskTime");
			elementTaskTime.setAttribute("TaskId", vtNextTaskTime.get(0));
			elementTaskTime.setAttribute("TimeUnit", vtNextTaskTime.get(1));
			elementTaskTime.setText(vtNextTaskTime.get(2));
			elementTaskTime.setAttribute("ShowTime", vtNextTaskTime.get(3));
			elementTaskTimes.addContent(elementTaskTime);
		}
		
	return elementTaskTimes;	
	}
	
	/**
	 * Generate jdom XML elements representing the TaskSequenceRole data
	 * held within the Vector vtTaskSequenceRoles.
	 * @return Element elementTaskSequenceRoles (containing sub elements, attributes etc).
	 */
	private Element processTaskSequenceRolesToXML() {
		/* vtThisTaskSequenceRole VECTOR FOR REFERENCE (all data as Strings)
		0 = TaskSequenceId
		1 = RoleId
		*/
		Element elementTaskSequenceRoles = new Element("TaskSequencesRoles");
		Vector<String> vtNextTaskTaskSequenceRole = null;
		Iterator<Vector<String>> it = this.vtTaskSequenceRoles.iterator();
		
		while (it.hasNext())	{
			vtNextTaskTaskSequenceRole = it.next();
			Element elementTaskSequenceRole = new Element("TaskSequenceRole");
			elementTaskSequenceRole.setAttribute("TaskSequenceId", vtNextTaskTaskSequenceRole.get(0));
			elementTaskSequenceRole.setAttribute("RoleId", vtNextTaskTaskSequenceRole.get(1));
			elementTaskSequenceRoles.addContent(elementTaskSequenceRole);
		}
	return elementTaskSequenceRoles;	
	}
	
	/**
	 * Generate jdom XML elements representing the TaskSequenceActivity data
	 * held within the Vector vtTaskSequenceActivities.
	 * @return Element elementTaskSequenceRoles (containing sub elements, attributes etc).
	 */
	private Element processTaskSequenceActivitiesToXML() {
		/* vtThisTaskSequenceActivity VECTOR FOR REFERENCE (all data as Strings)
		0 = ActivityId
		1 = TaskSequenceId
		*/
		Element elementTaskSequenceActivities = new Element("TaskSequencesActivities");
		Vector<String> vtNextTaskTaskSequenceActivity = null;
		Iterator<Vector<String>> it = this.vtTaskSequencesActivities.iterator();
		
		while (it.hasNext())	{
			vtNextTaskTaskSequenceActivity = it.next();
			Element elementTaskSequenceActivity = new Element("TaskSequenceActivity");
			elementTaskSequenceActivity.setAttribute("ActivityId", vtNextTaskTaskSequenceActivity.get(0));
			elementTaskSequenceActivity.setAttribute("TaskSequenceId", vtNextTaskTaskSequenceActivity.get(1));
			elementTaskSequenceActivities.addContent(elementTaskSequenceActivity);
		}
	return elementTaskSequenceActivities;	
	}
	
	/**
	 * Process the data gathered into a JDOM XML Document.
	 * THIS IS NOT FINISHED
	 * @param doc, the empty document to be completed
	 * @return the competed document
	 */
	public Document processDataToXMLDocument(Document doc) {
		Element oTempElement = processViewsToXMLElement();
		doc.addContent(oTempElement);
	//	xml.append( processNodesToXML() );
	//	xml.append( processLinksToXML() );
	//	xml.append( processCodesToXML() );

		if (bWithMeetings) {
	//		xml.append( processMeetingsToXML() );
		}

		return doc;
	}
	/**
	 * Process view information into XML output
	 *
	 * @return String, the xml formatted string representing views
	 */
	public Element processViewsToXMLElement() {

		/* VECTOR FOR REFERENCE
			0 = viewid
			1 = nodeid
			2 = xPos (Integer)
			3 = yPos (Integer)
			4 = created (Long)
			5 = lastModified (Long)
			6 = showTags
			7 = showText
			8 = showTrans
			9 = showWeight
			10 = smallNode
			11 = hideNode
			12 = wrapWidth
			13 = fontsize
			14 = fontface
			15 = fonstyle
			16 = foreground
			17 = background
		*/
		/* DATBASE 'ViewNode' TABLE FOR REFERENCE
			ViewID	= Text 50
			NodeID	= Text 50
			XPos	= Number Long Integer
			YPos	= Number Long Integer
			CreationDate		= Number Double
			ModificationDate	= Number Double
		*/

		Element oViewsXMLElement = new Element("views");
		Vector nextView= null;
		int count = vtViews.size();
		
		for (int i = 0; i < count; i++) {
	
			nextView = (Vector)vtViews.elementAt(i);
			Element oCurrentViewXML = new Element("view");	
//			xmlViews.append("\t\t<view ");
			oCurrentViewXML.setAttribute("viewref", (String)nextView.elementAt(0));
			oCurrentViewXML.setAttribute("noderef", (String)nextView.elementAt(1));
			oCurrentViewXML.setAttribute("XPosition", ((Integer)nextView.elementAt(2)).toString());
			oCurrentViewXML.setAttribute("YPosition", ((Integer)nextView.elementAt(3)).toString());
			oCurrentViewXML.setAttribute("created", ((Long)nextView.elementAt(4)).toString());
			oCurrentViewXML.setAttribute("lastModified", ((Long)nextView.elementAt(5)).toString());
			oCurrentViewXML.setAttribute("showTags", ((Boolean)nextView.elementAt(6)).toString());
			oCurrentViewXML.setAttribute("showText", ((Boolean)nextView.elementAt(7)).toString());
			oCurrentViewXML.setAttribute("showTrans", ((Boolean)nextView.elementAt(8)).toString());
			oCurrentViewXML.setAttribute("showWeight", ((Boolean)nextView.elementAt(9)).toString());
			oCurrentViewXML.setAttribute("smallIcon", ((Boolean)nextView.elementAt(10)).toString());
			oCurrentViewXML.setAttribute("hideIcon", ((Boolean)nextView.elementAt(11)).toString());
			oCurrentViewXML.setAttribute("labelWrapWidth", ((Integer)nextView.elementAt(12)).toString());
			oCurrentViewXML.setAttribute("fontsize", ((Integer)nextView.elementAt(13)).toString());
			oCurrentViewXML.setAttribute("fontface", (String)nextView.elementAt(14));
			oCurrentViewXML.setAttribute("fontstyle", ((Integer)nextView.elementAt(15)).toString());
			oCurrentViewXML.setAttribute("foreground", ((Integer)nextView.elementAt(16)).toString());
			oCurrentViewXML.setAttribute("background", ((Integer)nextView.elementAt(17)).toString());
			//Add the current element to the content of the oViewsXMLElement
			oViewsXMLElement.addContent(oCurrentViewXML);
		}

		return oViewsXMLElement;
	}

	/**
	 * Process node information into XML output
	 *
	 * @return String, the xml formatted string representing nodes
	 */
	public String processNodesToXMLElement() {

		StringBuffer xmlNodes = new StringBuffer(500);

		/* VECTOR FOR REFERENCE
			0 = id
			1 = type (Integer)
			2 = extendedType
			3 = sOriginalID
			4 = author
			5 = creationDate (Long)
			6 = modificationDate (Long)
			7 = label
			8 = details (Vector)
			9 = state (Integer)
			10 = source
			11 = image
			12 = background image
			13 = sLastModAuthor
			14 = codes (Vector)
			15 = shortcuts (Vector)
			16 = meetings(Vector)
		*/
		/* DATABASE 'Node' TABLE FOR REFERENCE
			NodeID					= Text 50
			NodeType				= Number Byte
			ExtendedNodeType		= Text 50
			ImportedI	D			= Number Long Integer
			Author					= Text 50
			CreationDate			= Number Double
			ModificationDate		= Number Double
			Label					= Text 100
			Detail					= Memo
			CurrentStauts			= Integer
			sLastModAuthor			= Text 50

		  DATABASE 'ReferenceNode' TABLE
			NodeID			= Text 50
			Source			= Text 250
			ImageSource		= VARCHAR 255
			ImageWidth		= INT 11
			ImageHeight		= INT 11

		  DATABASE 'ShortutNode' TABLE
			NodeID			= Text 50
			ReferenceID		= Text 50

		  DATABASE 'NodeCode' TABLE
			NodeID			= Text 50
			CodeID			= Text 50

		  DATABASE 'NodeDetail'
			NodeID				= Text 50
			UserID				= Text 50
			PageNo				= Integer
			CreationDate		= Number Double
			ModificationDate	= Number Double

		  DATABASE 'MediaIndex'
			ViewID 					= Text 50
			NodeID 					= Text 50
			MeetingID 				= Text 255
			MediaIndex 				= Number Double
			CreationDate			= Number Double
			ModificationDate		= Number Double

		*/
		
		Element oNodesXMLElement = new Element("nodes");
//		xmlNodes.append("\t<nodes>\n");

		Vector nextNode = null;
		int counti = vtNodes.size();

		for (int i = 0; i < counti; i++) {
			nextNode = (Vector)vtNodes.elementAt(i);
			Element oCurrentNodeXML = new Element("node");
		// 	xmlNodes.append("\t\t<node ");
						
			oCurrentNodeXML.setAttribute("id", (String)nextNode.elementAt(0));
			oCurrentNodeXML.setAttribute("type", ((Integer)nextNode.elementAt(1)).toString());
			oCurrentNodeXML.setAttribute("extendedtype", (String)nextNode.elementAt(2));
			oCurrentNodeXML.setAttribute("originalid", (String)nextNode.elementAt(3));
			oCurrentNodeXML.setAttribute("author", (String)nextNode.elementAt(4));
			oCurrentNodeXML.setAttribute("created", ((Long)nextNode.elementAt(5)).toString());
			oCurrentNodeXML.setAttribute("lastModified", ((Long)nextNode.elementAt(6)).toString());
			oCurrentNodeXML.setAttribute("label", (String)nextNode.elementAt(7));
			oCurrentNodeXML.setAttribute("state", ((Integer)nextNode.elementAt(9)).toString());
			oCurrentNodeXML.setAttribute("lastModificationAuthor", (String)nextNode.elementAt(15));
						
	//		Create details element
			Element oDetailsNodeXML = new Element("details");
	
			Vector details = (Vector)nextNode.elementAt(8);
			int count = details.size();
			String detail = "";
			// Add page child elements to details element
			for (int j=0; j<count; j++) {
				Element oPageNodeXML = new Element("page");
				NodeDetailPage page = (NodeDetailPage)details.elementAt(j);
				detail = page.getText();

				if (detail.equals(ICoreConstants.NODETAIL_STRING) )
					detail = "";
				detail = CoreUtilities.cleanXMLText(detail);
				oPageNodeXML.setAttribute("nodeid", page.getNodeID());
//				xmlNodes.append("\t\t\t\t<page ");
				oPageNodeXML.setAttribute("author", page.getAuthor());
				oPageNodeXML.setAttribute("created",  new Long( (page.getCreationDate()).getTime() ).toString());
				oPageNodeXML.setAttribute("lastModified",  new Long( (page.getModificationDate()).getTime() ).toString());
				oPageNodeXML.setAttribute("pageno",  new Integer(page.getPageNo()).toString());
				oDetailsNodeXML.addContent(oPageNodeXML);
			}
			// Add details element to the current node
			oCurrentNodeXML.addContent(oDetailsNodeXML);
			// Add the source element
			Element oSourceNodeXML = new Element("source");
			oSourceNodeXML.setText((String)nextNode.elementAt(10));
			oCurrentNodeXML.addContent(oSourceNodeXML);
			// Add the image element
			Element oImageNodeXML = new Element("image");
			oImageNodeXML.setAttribute("width", ((Integer)nextNode.elementAt(12)).toString());
			oImageNodeXML.setAttribute("height", ((Integer)nextNode.elementAt(13)).toString());
			oImageNodeXML.setText((String)nextNode.elementAt(11));
			oCurrentNodeXML.addContent(oImageNodeXML);
			// Add the background  element
			Element oBackgroundNodeXML = new Element("background");
			oBackgroundNodeXML.setText((String)nextNode.elementAt(14));
			oCurrentNodeXML.addContent(oBackgroundNodeXML);
			// Add the coderefs
			Element oCodeRefsXML = new Element("coderefs");

			Vector codes = (Vector)nextNode.elementAt(16);
			int countj = codes.size();
			for (int j=0; j<countj; j++) {
				if (codes.elementAt(j) instanceof String) {
					Element oCodeRefXML = new Element("coderef");
					oCodeRefXML.setText((String)codes.elementAt(j));
					oCodeRefsXML.addContent(oCodeRefXML);
				}
			}
			oCurrentNodeXML.addContent(oCodeRefsXML);
			
			// Add the short cut refs
			Element oShortcutRefsXML = new Element("shortcutrefs");
			
			Vector shorts = (Vector)nextNode.elementAt(17);
			int countk = shorts.size();
			for (int k=0; k<countk; k++) {
				if (shorts.elementAt(k) instanceof NodeSummary) {
					Element oShortCutRefXML = new Element("shortcutref");
					oShortCutRefXML.setAttribute("shortcutref", ((NodeSummary)shorts.elementAt(k)).getId());
					oShortcutRefsXML.addContent(oShortCutRefXML);
				}
			}

			if (bWithMeetings) {
				Element oMediaindexesXML = new Element("mediaindexes");
				xmlNodes.append("\t\t\t<mediaindexes>");
				Vector meetings = (Vector)nextNode.elementAt(18);
				int countl = meetings.size();

				String sMeetingID = "";
				String sMeetingMapID = "";
				for (int l=0; l<countl; l++) {
					if (meetings.elementAt(l) instanceof MediaIndex) {
						MediaIndex mediaIndex = (MediaIndex)meetings.elementAt(l);
						sMeetingID = mediaIndex.getMeetingID();
						sMeetingMapID = mediaIndex.getViewID();
						if (htViewsCheck.containsKey(sMeetingMapID)) {
							xmlNodes.append("\n\t\t\t\t<mediaindex mediaindex=\""+mediaIndex.getMediaIndex().getTime()+"\" ");
							xmlNodes.append("noderef=\""+(String)nextNode.elementAt(0)+"\" ");
							xmlNodes.append("viewref=\""+sMeetingMapID+"\" ");
							xmlNodes.append("meetingref=\""+sMeetingID+"\" ");
							xmlNodes.append("created=\""+mediaIndex.getCreationDate().getTime()+"\" ");
							xmlNodes.append("lastModified=\""+mediaIndex.getModificationDate().getTime()+"\" />");
							htMeetings.put(sMeetingID, sMeetingID);
						}
					}
				}
				xmlNodes.append("\n\t\t\t</mediaindexes>\n");
			}

			/*
			xmlNodes.append("\t\t\t<views>\n");
			Vector views = htViews.get((Object)(String)nextNode.elementAt(0));
			int countm = views.size();
			for (int m=0; m<countm; m++) {
				Vector next = views.elementAt(m);
				xmlNodes.append("\t\t\t\t<view noderef=\""+next.elementAt(0)+"\" XPosition=\""+next.elementAt(2)+"\" YPosition=\""+next.elementAt(3)+"\" />\n");
			}
			xmlNodes.append("\t\t\t</views>\n");
			*/

			xmlNodes.append("\t\t</node>\n");
		}

		xmlNodes.append("\t</nodes>\n");

		return xmlNodes.toString();
	}
	/**
	 * Process the given node for export i.e. add all the data 
	 *
	 * @param NodeSummary nodeToExport, the top level node to export (usually a map or list).
	 * @param NodeSummary parentNode, the parent node to the node to exprt.
	 */
	public void processNodeForExport(NodeSummary nodeToExport, NodeSummary parentNode) {
		// Create and standard Compendium data for nodeToExport
		super.processNodeForExport(nodeToExport, parentNode);
		//Check if it is a LdActivityView. If so, add  task sequence data for nodeToExport
		int ntype = nodeToExport.getType();
		if (View.isLdActivityViewType(ntype))	{
			// HAVE I ALREADY ADDED THIS VIEW?
			if (!htActivityTaskSequenceCheck.containsKey(nodeToExport.getId())) {
				LdActivityView view = (LdActivityView)nodeToExport;
				try {
					if (!view.isMembersInitialized())
						view.initializeMembers();
				}
				catch(Exception ex) {
					System.out.println("Error: (XMLExport.processNodeForExport) \n\n"+ex.getMessage());
				}
			
			processTaskSequencesForExport(view);
				
			}
		}		
	}


	/**
	 * Process the node given to extract the information required for export
	 *
	 * @param NodeSummary node, the node to process for export.
	 */
	/**
	protected void processNodeSummary(NodeSummary nodeSummary) {

		// PROCESS LABEL AND DETAILS AND SOURCE THROUGH CHECK XML CHARS
		Vector nodeData = new Vector(20);

		String id = nodeSummary.getId();
		int type = nodeSummary.getType();
		String extendedType = nodeSummary.getExtendedNodeType();
		String sOriginalID = nodeSummary.getOriginalID();
		if (sOriginalID.equals("-1"))
			sOriginalID = "";

		String author = nodeSummary.getAuthor();
		author = CoreUtilities.cleanXMLText(author);

		Date creationDate = nodeSummary.getCreationDate();
		long creationDateSecs = creationDate.getTime();

		Date modificationDate = nodeSummary.getModificationDate();
		long modificationDateSecs = modificationDate.getTime();

		String label = nodeSummary.getLabel();
		label = CoreUtilities.cleanXMLText(label);
		
		String sLastModAuthor = nodeSummary.getLastModificationAuthor();
		sLastModAuthor = CoreUtilities.cleanXMLText(sLastModAuthor);
				
		Vector details = null;
		try {
			details = nodeSummary.getDetailPages(author);
			int state = nodeSummary.getState();

			String sSource = nodeSummary.getSource();
			String sSourceImage = nodeSummary.getImage();
			Dimension oImageSize = nodeSummary.getImageSize();
			int nImageWidth = oImageSize.width;
			int nImageHeight = oImageSize.height;
			String sBackground = "";
			if (nodeSummary instanceof View) {
				ViewLayer layer  = ((View)nodeSummary).getViewLayer();
				if (layer == null) {
					try { ((View)nodeSummary).initializeMembers();
						sBackground = layer.getBackground();
					}
					catch(Exception ex) {
						sBackground = "";
					}
				}
				else {
					sBackground = layer.getBackground();
				}
			}

			if (bWithResources) {
				if (!sBackground.equals("")) {
					File file3 = new File(sBackground);
					if (file3.exists()) {
						String sOldBackground = sBackground;
						if (!htResources.containsKey(sOldBackground)) {
							sBackground = sBackupPath + "/" + file3.getName();
							htResources.put(sOldBackground, sBackground);
						}
					}
					else if (sBackground != null && !sBackground.equals("")) {
						setBNotFound(true);
						System.out.println("NOT FOUND ON EXPORT: "+sBackground);
					}
				}

				if (!sSource.equals("") && CoreUtilities.isFile(sSource)) {
					File file = new File(sSource);
					if (file.exists()) {
						String sOldSource = sSource;
						if (!htResources.containsKey(sOldSource)) {
							sSource = sBackupPath + "/" + file.getName();
							htResources.put(sOldSource, sSource);
						}
					}
					else if (sSource != null && !sSource.equals("")) {
						setBNotFound(true);
						System.out.println("NOT FOUND ON EXPORT: "+sSource);
					}
				}
				if (!sSourceImage.equals("") && CoreUtilities.isFile(sSourceImage)) {
					File file2 = new File(sSourceImage);
					if (file2.exists()) {
						String sOldSourceImage = sSourceImage;
						if (!htResources.containsKey(sOldSourceImage)) {
							sSourceImage = sBackupPath + "/" + file2.getName();
							htResources.put(sOldSourceImage, sSourceImage);
						}
					}
					else if (sSourceImage != null && !sSourceImage.equals("")) {
						setBNotFound(true);
						System.out.println("NOT FOUND ON EXPORT: "+sSourceImage);
					}
				}
			}

			sSource = CoreUtilities.cleanXMLText(sSource);
			sSourceImage = CoreUtilities.cleanXMLText(sSourceImage);
			sBackground = CoreUtilities.cleanXMLText(sBackground);

			//String parentID = "";
			//if (nodeSummary.getParentNode() != null)
			//	parentID = (nodeSummary.getParentNode()).getId();

			//int permission = nodeSummary.getPermission();

			Vector codes = processCodes( (Enumeration)nodeSummary.getCodes() );
			Vector shortcuts = nodeSummary.getShortCutNodes();
			if (shortcuts == null)
				shortcuts = new Vector(1);

			Vector vtMeetings = new Vector(1);
			try {
				vtMeetings = (oModel.getMeetingService()).getAllMediaIndexes(oModel.getSession(), id);
			}
			catch(Exception ex) {
				System.out.println("Unable to get media index data for node = "+id+"\nDue to:\n\n"+ex.getMessage());
			}

			//int viewCount = nodeSummary.getViewCount();

			nodeData.add((Object) id );
			nodeData.add((Object) new Integer(type) );
			nodeData.add((Object) extendedType );
			nodeData.add((Object) sOriginalID );
			nodeData.add((Object) author );
			nodeData.add((Object) new Long(creationDateSecs) );
			nodeData.add((Object) new Long(modificationDateSecs) );
			nodeData.add((Object) label );
			nodeData.add((Object) details );
			nodeData.add((Object) new Integer(state) );
			//nodeData.add((Object) parentID );
			//nodeData.add((Object) new Integer(permission) );

			nodeData.add((Object) sSource );
			nodeData.add((Object) sSourceImage );
			nodeData.add((Object) new Integer(nImageWidth) );
			nodeData.add((Object) new Integer(nImageHeight) );			
			nodeData.add((Object) sBackground );
			nodeData.add((Object) sLastModAuthor );

			nodeData.add((Object) codes );
			nodeData.add((Object) shortcuts );
			nodeData.add((Object) vtMeetings );
		}
		catch(Exception ex) {
			System.out.println("Error: (XMLExport.processNodeSummary) \n\n"+ex.getMessage());
		}

		if ( !htNodesCheck.containsKey((Object) id)) {
			htNodesCheck.put((Object) id, (Object) id);
			vtNodes.add((Object) nodeData);
		}
	}
**/	


	/**
	 * Generate a Vector containing String versions of all the TaskSequence data
	 * for the LdActivityView oView. 
	 * @param oView - the LdActivityView to generate the data for.
	 */
	protected void processTaskSequencesForExport(LdActivityView oView) {
		String sActivityId = oView.getId();
		LdActivityTimes oLdActivityTimes = oView.getLdActivityTimes();
		LinkedHashSet<LdTaskSequence> oTaskSequenceSet = oLdActivityTimes.getTaskSequenceSet();
/*** ActivityTimesDisplays data processing	****************************************************/		
		boolean bShowTime = oLdActivityTimes.getShowTime();
		TimeUnit oCurrentTimeUnits = oLdActivityTimes.getCurrentTaskTimeUnits();
		Vector<String> vtThisActivityTimesDisplayed = new Vector<String>(3);
		/* vtThisActivityTimesDisplayed VECTOR FOR REFERENCE (all data as Strings)
		0 = id
		1 = bShowTime
		2 = oCurrentTimeUnits
		*/
		vtThisActivityTimesDisplayed.add(sActivityId);
		vtThisActivityTimesDisplayed.add(Boolean.toString(bShowTime));
		vtThisActivityTimesDisplayed.add(oCurrentTimeUnits.toString());
		// Add data for this View to the Vector for this export
		vtActivityTimesDisplayed.add(vtThisActivityTimesDisplayed);
/*** End of ActivityTimesDisplays data processing	****************************************************/		
/*** TaskSequenceTasks data processing ****************************************************************/
		Iterator<LdTaskSequence> oTsIt = oTaskSequenceSet.iterator();
		ListIterator<String> idListIt;
		LdTaskSequence oNextTaskSequence;
		LinkedList<String> oTaskIds;
		String sNextTSid;
		Vector<String> vtThisTaskSequenceTask;
		/* vtThisTaskSequenceTask VECTOR FOR REFERENCE (all data as Strings)
		0 = TaskSequenceId
		1 = TaskId
		*/
		while (oTsIt.hasNext())	{
			oNextTaskSequence = oTsIt.next();
			sNextTSid = oNextTaskSequence.getId();
			oTaskIds = oNextTaskSequence.getTaskIds();
			idListIt = oTaskIds.listIterator(); 
			while (idListIt.hasNext())	{
				vtThisTaskSequenceTask = new Vector<String>(2);
				vtThisTaskSequenceTask.add(sNextTSid);
				vtThisTaskSequenceTask.add(idListIt.next());
				vtTaskSequencesTasks.add(vtThisTaskSequenceTask);
			}			
		}
/*** End of TaskSequenceTasks data processing *********************************************************/
/*** TaskTimes data processing ************************************************************************/
		/* vtThisTaskTime VECTOR FOR REFERENCE (all data as Strings)
		0 = TaskId
		1 = TimeUnit
		2 = Time
		3 = showTime 
		*/
		// Re initialise the iterator over the TaskSequuence set
		oTsIt = oTaskSequenceSet.iterator();
		LinkedList<LdTask> oLdTaskList;
		Iterator<LdTask> oTLit;		LdTask oNextTask;
		Vector<String> vtThisTask;
		while (oTsIt.hasNext())	{
			oNextTaskSequence = oTsIt.next();
			oLdTaskList = oNextTaskSequence.getTaskSequence();
			oTLit = oLdTaskList.iterator();
			while(oTLit.hasNext())	{
				oNextTask = oTLit.next();
				vtThisTask = new Vector<String>(4);
				vtThisTask.add(oNextTask.getId());
				vtThisTask.add(oNextTask.getCurrentTaskTimeUnits().toString());
				vtThisTask.add( Long.valueOf(oNextTask.getTaskTime()).toString() );
				vtThisTask.add(Boolean.valueOf( oNextTask.getShowTime()).toString());
				vtTaskTimes.add(vtThisTask);
			}
		}	
/*** End of TaskTimes data processing *****************************************************************/		
/*** TaskSequenceRoles data processing ****************************************************************/		
		/* vtThisTaskSequenceRole VECTOR FOR REFERENCE (all data as Strings)
		0 = TasksequenceId
		1 = RoleId
		*/
		// Re initialise the iterator over the TaskSequuence set
		oTsIt = oTaskSequenceSet.iterator();
		
		while (oTsIt.hasNext())	{
			oNextTaskSequence = oTsIt.next();
			// if there is a role id for this TaskSequence
			 if (oNextTaskSequence.getRoleId().length()!= 0)	{
				 Vector<String> vtThisTaskSequenceRole = new Vector<String>(2);
				 vtThisTaskSequenceRole.add(oNextTaskSequence.getId());
				 vtThisTaskSequenceRole.add(oNextTaskSequence.getRoleId());
				 vtTaskSequenceRoles.add(vtThisTaskSequenceRole);
			 }			
		}	
/*** End of TaskSequenceRoles data processing *********************************************************/
/*** TaskSequenceActivity data processing ****************************************************************/		
		/* vtThisTaskSequenceActivity VECTOR FOR REFERENCE (all data as Strings)
		0 = ActivityId
		1 = TasksequenceId
		*/
		// Re initialise the iterator over the TaskSequuence set
		oTsIt = oTaskSequenceSet.iterator();	
		
		while (oTsIt.hasNext())	{
			oNextTaskSequence = oTsIt.next();
			Vector<String> vtThisTaskSequenceActivity = new Vector<String>(2);
			vtThisTaskSequenceActivity.add(sActivityId);
			vtThisTaskSequenceActivity.add(oNextTaskSequence.getId());
			this.vtTaskSequencesActivities.add(vtThisTaskSequenceActivity);
		}		
/*** End of TaskSequenceActivity data processing *********************************************************/
		
	}

	
	/**
	 * Process the node given to extract the information required for export.
	 * This method differs from the superclass method in that it does not store 
	 * the LD node stencil images even if bResources is true, i.e.  if 
	 *
	 * @param NodeSummary node, the node to process for export.
	 */
	protected void processNodeSummary(NodeSummary nodeSummary) {

		// PROCESS LABEL AND DETAILS AND SOURCE THROUGH CHECK XML CHARS
		Vector nodeData = new Vector(20);

		String id = nodeSummary.getId();
		int type = nodeSummary.getType();
		String extendedType = nodeSummary.getExtendedNodeType();
		String sOriginalID = nodeSummary.getOriginalID();
		if (sOriginalID.equals("-1"))
			sOriginalID = "";

		String author = nodeSummary.getAuthor();
		author = CoreUtilities.cleanXMLText(author);

		Date creationDate = nodeSummary.getCreationDate();
		long creationDateSecs = creationDate.getTime();

		Date modificationDate = nodeSummary.getModificationDate();
		long modificationDateSecs = modificationDate.getTime();

		String label = nodeSummary.getLabel();
		label = CoreUtilities.cleanXMLText(label);
		
		String sLastModAuthor = nodeSummary.getLastModificationAuthor();
		sLastModAuthor = CoreUtilities.cleanXMLText(sLastModAuthor);
				
		Vector details = null;
		try {
			details = nodeSummary.getDetailPages(author);
			int state = nodeSummary.getState();

			String sSource = nodeSummary.getSource();
			String sSourceImage = nodeSummary.getImage();
			Dimension oImageSize = nodeSummary.getImageSize();
			int nImageWidth = oImageSize.width;
			int nImageHeight = oImageSize.height;
			String sBackground = "";
			if (nodeSummary instanceof View) {
				ViewLayer layer  = ((View)nodeSummary).getViewLayer();
				if (layer == null) {
					try { ((View)nodeSummary).initializeMembers();
						sBackground = layer.getBackground();
					}
					catch(Exception ex) {
						sBackground = "";
					}
				}
				else {
					sBackground = layer.getBackground();
				}
			}

			if (bWithResources) {
				if (!sBackground.equals("")) {
					File file3 = new File(sBackground);
					if (file3.exists()) {
						String sOldBackground = sBackground;
						if (!htResources.containsKey(sOldBackground)) {
							sBackground = sBackupPath + "/" + file3.getName();
							htResources.put(sOldBackground, sBackground);
						}
					}
					else if (sBackground != null && !sBackground.equals("")) {
						setBNotFound(true);
						System.out.println("NOT FOUND ON EXPORT: "+sBackground);
					}
				}

				if (!sSource.equals("") && CoreUtilities.isFile(sSource)) {
					File file = new File(sSource);
					if (file.exists()) {
						String sOldSource = sSource;
						if (!htResources.containsKey(sOldSource)) {
							sSource = sBackupPath + "/" + file.getName();
							htResources.put(sOldSource, sSource);
						}
					}
					else if (sSource != null && !sSource.equals("")) {
						setBNotFound(true);
						System.out.println("NOT FOUND ON EXPORT: "+sSource);
					}
				}
				if (nodeSummary.getLdType()== ILdCoreConstants.iLD_TYPE_NO_TYPE && !sSourceImage.equals("") && CoreUtilities.isFile(sSourceImage)) {
					File file2 = new File(sSourceImage);
					if (file2.exists()) {
						String sOldSourceImage = sSourceImage;
						if (!htResources.containsKey(sOldSourceImage)) {
							sSourceImage = sBackupPath + "/" + file2.getName();
							htResources.put(sOldSourceImage, sSourceImage);
						}
					}
					else if (sSourceImage != null && !sSourceImage.equals("")) {
						setBNotFound(true);
						System.out.println("NOT FOUND ON EXPORT: "+sSourceImage);
					}
				}
			}

			sSource = CoreUtilities.cleanXMLText(sSource);
			sSourceImage = CoreUtilities.cleanXMLText(sSourceImage);
			sBackground = CoreUtilities.cleanXMLText(sBackground);

			//String parentID = "";
			//if (nodeSummary.getParentNode() != null)
			//	parentID = (nodeSummary.getParentNode()).getId();

			//int permission = nodeSummary.getPermission();

			Vector codes = processCodes( (Enumeration)nodeSummary.getCodes() );
			Vector shortcuts = nodeSummary.getShortCutNodes();
			if (shortcuts == null)
				shortcuts = new Vector(1);

			Vector vtMeetings = new Vector(1);
			try {
				vtMeetings = (oModel.getMeetingService()).getAllMediaIndexes(oModel.getSession(), id);
			}
			catch(Exception ex) {
				System.out.println("Unable to get media index data for node = "+id+"\nDue to:\n\n"+ex.getMessage());
			}

			//int viewCount = nodeSummary.getViewCount();

			nodeData.add((Object) id );
			nodeData.add((Object) new Integer(type) );
			nodeData.add((Object) extendedType );
			nodeData.add((Object) sOriginalID );
			nodeData.add((Object) author );
			nodeData.add((Object) new Long(creationDateSecs) );
			nodeData.add((Object) new Long(modificationDateSecs) );
			nodeData.add((Object) label );
			nodeData.add((Object) details );
			nodeData.add((Object) new Integer(state) );
			//nodeData.add((Object) parentID );
			//nodeData.add((Object) new Integer(permission) );

			nodeData.add((Object) sSource );
			nodeData.add((Object) sSourceImage );
			nodeData.add((Object) new Integer(nImageWidth) );
			nodeData.add((Object) new Integer(nImageHeight) );			
			nodeData.add((Object) sBackground );
			nodeData.add((Object) sLastModAuthor );

			nodeData.add((Object) codes );
			nodeData.add((Object) shortcuts );
			nodeData.add((Object) vtMeetings );
		}
		catch(Exception ex) {
			System.out.println("Error: (XMLExport.processNodeSummary) \n\n"+ex.getMessage());
		}

		if ( !htNodesCheck.containsKey((Object) id)) {
			htNodesCheck.put((Object) id, (Object) id);
			vtNodes.add((Object) nodeData);
		}
	}
	

	/**
	 * @param vtActiveTimesDisplayed the vtActiveTimesDisplayed to set
	 */
	public void setVtActiveTimesDisplayed(Vector vtActiveTimesDisplayed) {
		this.vtActivityTimesDisplayed = vtActiveTimesDisplayed;
	}


	/**
	 * @return the vtActiveTimesDisplayed
	 */
	public Vector getVtActiveTimesDisplayed() {
		return vtActivityTimesDisplayed;
	}
	
}
