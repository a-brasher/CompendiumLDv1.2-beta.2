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
 
package com.compendium.learningdesign.ui;

import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.ShortCutNodeSummary;
import com.compendium.core.datamodel.View;
import com.compendium.core.datamodel.ViewLayer;

import com.compendium.learningdesign.core.datamodel.*;
import com.compendium.learningdesign.io.xml.LdXMLCopyMaker;
import com.compendium.meeting.MeetingEvent;
import com.compendium.meeting.MeetingManager;
import com.compendium.ui.FormatProperties;
import com.compendium.ui.UINode;
import com.compendium.ui.UIUtilities;
import com.compendium.ui.UIViewPane;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.LinkedHashSet;


/**
 * Class UILdActivityNode is the UI class for learning design activity nodes.
 * In addition to the standard data and methods for a node, it has additional
 * instance variables to store total learner time and total tutor time for the 
 * activity.
 * @author ajb785
 *
 */
public class UILdActivityNode extends UILdNode implements ILdTaskTime {
	// All the data variables for times and time units are now stored in the OTaskSequenceSet
	/**	The total time learners should spend on this activity. Declared as a
	 * float to allow times such as 7.5 hours.	**/
//	private float  	fLearnerTaskTime	= 0;

	/**	The total time tutors should spend on this activity	**/
//	private float  	fTutorTaskTime		= 0;
	
	/** The units that the learner task time is in (one of hours, minutes, days or months)	**/
//	private TimeUnit oLearnerTaskTimeUnits = TimeUnit.HOURS;
	
	/** The units that the tutor task time is in (one of hours, minutes, days or months)	**/
//	private TimeUnit oTutorTaskTimeUnits = TimeUnit.HOURS;
	
	/** Not sure if this oActivityViewNode instance variable is needed		1/08/2008	**/
//	protected LdActivityView		oActivityViewNode						= null;
	
	/** The set of task sequences in this activity **/
	private LinkedHashSet<LdTaskSequence> oTaskSequenceSet;
	
	/**	Indicates whether the task time should be shown for this node. Set to
	 * false initially, changed when time is set. 		 **/
	private boolean bShowTime = false;

	
	/**
	 * @param nodePos
	 * @param author
	 * @param type
	 */
	public UILdActivityNode(NodePosition nodePos, String author, int type) {
		super(nodePos, author);
		this.setLdType(type);
		this.setTaskSequenceSet(new LinkedHashSet() );	
		this.setShowTime(false);
	}

	
	/**
	 * @param nodePos
	 * @param author
	 * @param type
	 * @param subType
	 */
	public UILdActivityNode(NodePosition nodePos, String author, int type,
			int subType) {
		super(nodePos, author, type, subType);
		this.setShowTime(false);
	}



	

	/**
	 * @return the oTaskSequenceSet
	 */
	public LinkedHashSet<LdTaskSequence> getTaskSequenceSet() {
		return oTaskSequenceSet;
	}

	/**
	 * @param taskSequenceSet the oTaskSequenceSet to set
	 */
	public void setTaskSequenceSet(LinkedHashSet<LdTaskSequence> taskSequenceSet) {
		oTaskSequenceSet = taskSequenceSet;
	}
	
	/** Add a task seqence to this activities task sequence set **/
	public void addTaskSequence(LdTaskSequence aTaskSequence)	{
		this.getTaskSequenceSet().add(aTaskSequence);
	}
	/**
	 * Returns the nodedata object that this UINode represents.
	 *
	 * @return com.compendium.core.datamodel.NodeSummary, the associated node.
	 * @see #setNode
	 */

	
	public boolean getShowTime() {
		return bShowTime;
	}

	public void setShowTime(boolean showTime) {
		bShowTime = showTime;
		
	}
	
	/** is this causing problems 13/08/2008
	public LdActivityView getNode() {
	    return oNode;
	}
	**/
	
}
