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

import com.compendium.core.datamodel.NodePosition;
import com.compendium.ui.UILink;
import com.compendium.ui.UINode;
import com.compendium.ui.UIViewPane;
import com.compendium.ui.plaf.NodeUI;
import com.compendium.ui.plaf.TextRowElement;

import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
// import java.util.concurrent.TimeUnit;
import com.compendium.learningdesign.ui.plaf.LdNodeUI;
import com.compendium.learningdesign.ui.plaf.LdTaskNodeUI;
import com.compendium.learningdesign.util.TimeUnit;
import com.compendium.learningdesign.io.svg.SvgExport;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Comment;
import org.apache.batik.svggen.SVGGraphics2D;
/**
 * UILdTaskNode is a  subclass of UILdNode. It has an extra instance variable,
 * is iTaskTime which stores the duration of task in units specified by the
 * oTaskTimeUnits instance variable.
 * @author ajb785
 *
 */
public class UILdTaskNode extends UILdNode implements ILdTaskTime {
	/**
	 * 
	 */
	private static final String uiClassID = "LdTaskNodeUI";
	
	static	{
		UIManager.getDefaults().put("LdTaskNodeUI",  "com.compendium.learningdesign.ui.plaf.LdTaskNodeUI");
	}
//	private static final long serialVersionUID = 1L;
	/**	 The time the task will take to complete, in units specified by the oTaskTimeUnits variable.
	 * Stored as minutes because 1 minute will be the shortest task time. **/
	/**
	 * @uml.property  name="iTaskTime"
	 */
	
	/** A reference to the timing property for PropertyChangeEvents.*/
    public static final String TIME_PROPERTY 		= "time";
    
    /** A reference to the timing property for PropertyChangeEvents.*/
    public static final String TIME_UNIT_PROPERTY 		= "timeUnit";
    
    /** Time indicator (show/hide) property name for use with property change events  */
	public final static String TIME_INDICATOR_PROPERTY = "timeIndicator";
    
    /** The task time value: the units for this value are given by oCurrentTaskTimeUnits **/
	private long  oTaskTime;
	
	/** The units that the task time is in (one of hours, minutes, days or months)	**/
	/** A TimeUnit represents time durations at a given unit of granularity and
	 *   provides utility methods to convert across units.
	 * @uml.property  name="oTaskTimeUnits"
	 */
	private final TimeUnit oTaskTimeUnits = TimeUnit.MINUTES;
	
	/** The task time units that were set the last time the task time was set. 
	 * These should be used to display the task time.	**/
	private TimeUnit oCurrentTaskTimeUnits = TimeUnit.HOURS;
	
	
	/**	Indicates whether this task is to be carried out by a learner. Set to
	 * false initially, changed when connected to a role node. 		 **/
	private boolean isLearnerTask = false;
	
	/**	Indicates whether this task is to be carried out by a learner. Set to
	 * false initially, changed when connected to a role node. 		 **/
	private boolean isTutorTask = false;
	
	/**	Indicates whether the task time should be shown for this node. Set to
	 * false initially, changed when time is set. WHILE TESTING SET TO TRUE INITIALLY		 **/
	private boolean bShowTime = false;

	public UILdTaskNode(NodePosition nodePos, String author) {
		super(nodePos, author);
		/** Only show the task time by defualt for a tasknode in a UILdViewPane **/
		if (!(this.getViewPane() instanceof UILdViewPane))
			this.setShowTime(false);
		oTaskTime = 0;
	}

	public UILdTaskNode(NodePosition nodePos, String author, int type) {
		super(nodePos, author, type);/** Only show the task time by defualt for a tasknode in a UILdViewPane **/
		if (!(this.getViewPane() instanceof UILdViewPane))
			this.setShowTime(false);
		oTaskTime = 0;
	}
/** Do not need this constructor at present: no sub types of tasks or activities **/
/****************************************
	public UILdTaskNode(NodePosition nodePos, String author, int type,
			int subType) {
		super(nodePos, author, type, subType);
		// TODO Auto-generated constructor stub
	}
***************************************/

	/**
	 * @return the iTaskTime
	 */
	public long getTaskTime() {
		return oTaskTime;
	}
	
	/**
	 * Returns a string representing the task time in appropriate units
	 * @return
	 */
	public String getTaskTimeString() {
		String sTaskTime = "";
		DecimalFormat twoDForm = new DecimalFormat("0.##");

		switch(this.getCurrentTaskTimeUnits())	{
		case HOURS: sTaskTime = String.valueOf(Float.valueOf(twoDForm.format(TimeUnit.MINUTES.toHours(this.getTaskTime())))) + " hrs"; break;
		case DAYS:	sTaskTime = String.valueOf(Float.valueOf(twoDForm.format(TimeUnit.MINUTES.toDays(this.getTaskTime())))) + " days"; break;
		default:	sTaskTime = String.valueOf(getTaskTime()) + " mins"; break;
	}
		
	return sTaskTime;
	}

	/**
	 * Set the time for the task, and fire a property change to indicate that
	 * the task time has changed.
	 * @param taskTime the TaskTime to set
	 */
	public void setTaskTime(long taskTime) {
		long oldTime = oTaskTime;
		oTaskTime = taskTime;
		//setShowTime(true);
		this.firePropertyChange(TIME_PROPERTY, oldTime, taskTime);
	}

	/**
	 * @return the oTaskTimeUnits
	 */
	public TimeUnit getTaskTimeUnits() {
		return oTaskTimeUnits;
	}

	/**
	 * @param taskTimeUnits the oTaskTimeUnits to set
	 Commented out because this is a final value.
	public void setTaskTimeUnits(TimeUnit taskTimeUnits) {
		oTaskTimeUnits = taskTimeUnits;
	}
*/
	/**
	 * @return the isLearnerTask
	 */
	public boolean isLearnerTask() {
		return isLearnerTask;
	}

	/**
	 * @param isLearnerTask the isLearnerTask to set
	 */
	public void setLearnerTask(boolean isLearnerTask) {
		this.isLearnerTask = isLearnerTask;
	}

	/**
	 * @return the isTutorTask
	 */
	public boolean isTutorTask() {
		return isTutorTask;
	}

	/**
	 * @param isTutorTask the isTutorTask to set
	 */
	public void setTutorTask(boolean isTutorTask) {
		this.isTutorTask = isTutorTask;
	}

	/**
	 * Get the task time units that the user selected for this instance.
	 * @return the oCurrentTaskTimeUnits
	 */
	public TimeUnit getCurrentTaskTimeUnits() {
		return oCurrentTaskTimeUnits;
	}

	/**
	 * Set the task time units that the user has selected for this instance, 
	 * and fire a property change to indicate that the units have been changed.
	 * @param currentTaskTimeUnits the oCurrentTaskTimeUnits to set
	 */
	public void setCurrentTaskTimeUnits(TimeUnit currentTaskTimeUnits) {
		TimeUnit oldTimeUnits = oCurrentTaskTimeUnits;
		oCurrentTaskTimeUnits = currentTaskTimeUnits;
		this.firePropertyChange(TIME_UNIT_PROPERTY, oldTimeUnits, currentTaskTimeUnits);
	}

	/**
	 * Get the boolean value which indicates whether the  node task time should
	 * be shown.
	 * @return the bShowTime
	 */
	public boolean getShowTime() {
		return bShowTime;
	}

	/**
	 * Set the boolean value which indicates whether the  node task time should
	 * be shown.
	 * @param showTime the bShowTime to set
	 */
	public void setShowTime(boolean showTime) {
		boolean bOldShowTime = this.getShowTime();
		bShowTime = showTime;
		//Debug statements
		//System.out.println("********* UILdTaskNode : " + this.getText() + " FIRE TIME_INDICATOR_PROPERTY " + bOldShowTime + " " + bShowTime);
		
		firePropertyChange(TIME_INDICATOR_PROPERTY, bOldShowTime, bShowTime);
	}	
	
	
	/**
	 * Return the UILdTaskLink link from this node to the given node, or null if there is no
	 * UILdTaskLink between the nodes.
	 *
	 * @param to com.compendium.ui.UINode, the node to return the link for.
	 * @return com.compendium.ui.UILink, the link of found else null.
	 */
	public UILdTaskLink getLinkTo(UILdTaskNode oToNode) {
	    for(Enumeration e = htLinks.elements();e.hasMoreElements();) {
			UILink link = (UILink)e.nextElement();
			if (link.getFromNode() == this && link.getToNode() == oToNode) {
				if (link instanceof UILdTaskLink)
					return (UILdTaskLink)link;
			}   
	    }
	    return null;
	}
	
	/**
	 * Create a clone of this node at the given position in in the given UIViewPane.
	 * @param x
	 * @param y
	 * @param oViewPane
	 * @return
	 */
	/**
	public UILdTaskNode createClone(int x, int y, UIViewPane oViewPane)	{
		UILdTaskNode oUITaskNode = (UILdTaskNode) super.createClone(x, y, oViewPane);
		oUITaskNode.setTaskTime( this.getTaskTime());
		oUITaskNode.setCurrentTaskTimeUnits(this.getCurrentTaskTimeUnits());
		oUITaskNode.setShowTime(this.getShowTime());
		return oUITaskNode;
	}
	*/
	/**
	* Returns the L&F object that renders this component.
	*
	* @return LdNodeUI object.
	*/
	public LdTaskNodeUI getUI() {
	    return (LdTaskNodeUI)ui;
	}
	
	/**
	* Returns a string that specifies the name of the l&f class
	* that renders this component.
	*
	* @return String "LdNodeUI"
	*
	* @see JComponent#getUIClassID
	* @see UIDefaults#getUI
	*/
	public String getUIClassID() {
		return uiClassID;
	}

	/**
	 * Create a SVG representation of all the components of this node.
	 * @param oDoc, the SVG document to which the SVG representation of this node will be added.
	 */
	public void generateChildrenSVG( Document oDoc) {
		if (oDoc == null )
			return;
		// Get the id of the View that this node is in
		String sParentViewId = this.getNodePosition().getView().getId();
		// Get the SVG element representing the View that this node is in
		//	Element oSvgParentView = oDoc.getElementById(SvgExport.sMapViewClass+SvgExport.sIdFragmentConnector + sParentViewId);
		Element oSvgParentView = this.getSvgParentMap(sParentViewId, oDoc);
		if (oSvgParentView == null)	{
			System.out.println("Error generating SVG for UILdTaskNode name " + this.getText()+ ", Id = " + this.getNode().getId());
			return;
		}
		// Get the NodeUI so can get text reactangle etc
		LdTaskNodeUI oNodeUI = this.getUI();

		int iXpos = this.getNodePosition().getXPos();
		int iYpos = this.getNodePosition().getYPos();

		double dbIconRXpos =  oNodeUI.getIconRectangle().getX() + iXpos;
		double dbIconRYpos =  oNodeUI.getIconRectangle().getY() + iYpos;
		int iIconRXpos = (int) Math.round(dbIconRXpos);
		int iIconRYpos = (int) Math.round(dbIconRYpos);
		double dbIconRWidth = oNodeUI.getIconRectangle().getWidth();
		/** iIconCentreXPos, the centre  of the icon in the X plane, is used to 
		 * layout the node label. This is the easiest way to lay out the label
		 * because node labels are always centred on the node icon.**/ 
		int iIconCentreXPos = (int)Math.round(dbIconRXpos + (dbIconRWidth/2));
		// Create the node class description text 
		String sNodeClass = this.getSvgClass();

		Comment oComment = oDoc.createComment("Start of Node " + sNodeClass  + " " + this.getNode().getId());
		Element oTitle = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_TITLE_TAG);
		oTitle.setTextContent(this.getNode().getLabel());

		Element oDesc = oDoc.createElement("desc");
		oDesc.setAttribute("class", "nodeDetails");
		oDesc.setTextContent(this.getNode().getDetail());
		// oGroup is the SVG <g> element to contain all sub-elements for this UINode
		Element oGroup = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_G_TAG);
		oGroup.setAttribute("id", sNodeClass+SvgExport.sIdFragmentConnector + this.getNode().getId());
		oGroup.setAttribute("class", sNodeClass );
		oGroup.appendChild(oTitle);
		oGroup.appendChild(oDesc);
		// Add the icon if it is displaid 
		if (!(this.getNodePosition().getHideIcon() || (this.getIcon() == null ))) {
			Element oUse = this.createSvgIconReference(oDoc);
			oGroup.appendChild(oUse);
		}
		/**	Layout the text	**/
		Element oText = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_TEXT_TAG);
		Vector<TextRowElement> oTREs = oNodeUI.getTextRowElements();
		String sTextCssClass = "nodelabel" + " text" + this.getNodePosition().getFontSize();
		if (!this.getNodePosition().getFontFace().equals("Dialog"))
			sTextCssClass = sTextCssClass + " " + this.getNodePosition().getFontFace();
		if (oTREs.isEmpty())	{
			//System.out.println("oTREs.isEmpty!!!: " + this.getText() + " in View " + this.getNodePosition().getView().getLabel());
			int iFontHeight = this.getFontHeight();
			//oText.setAttribute("x", Integer.toString((int)Math.round(oNodeUI.getLabelRectangle().getX() + iXpos)));
			oText.setAttribute("x", Integer.toString(iIconCentreXPos));
			oText.setAttribute("y", Integer.toString( (int)Math.round(oNodeUI.getLabelRectangle().getY() + iYpos + iFontHeight)));
			oText.setAttribute("class", sTextCssClass);
			oText.setTextContent(this.getText());
			oGroup.appendChild(oText);
		}
		else	{
			TextRowElement oCurrentElement = null;
			//System.out.println("iXpos = " + iXpos + ", iYpos = " + iYpos);
			int iYVal = 0; String sTextForLine = "";
			for (int i=0; i<oTREs.size(); ++i)	{
				oCurrentElement = oTREs.elementAt(i);
				sTextForLine = oCurrentElement.getText();						
				iYVal = (int)Math.round( oCurrentElement.getTextRect().getY()+iYpos );
				if (i==0)	{ // If it's the rirst row, set the <text> elements contents and attributes
					oText.setAttribute("x", Integer.toString(iIconCentreXPos));
					oText.setAttribute("y", Integer.toString(iYVal));
					oText.setAttribute("class", sTextCssClass);
					oText.setTextContent(sTextForLine);
				}
				else	{ // If it's the second or greater line, create <tspan> children of the <text> element
					Element oTspan = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_TSPAN_TAG);
					oTspan.setAttribute("x", Integer.toString(iIconCentreXPos));
					oTspan.setAttribute("y", Integer.toString(iYVal));
					oTspan.setTextContent(sTextForLine);
					oText.appendChild(oTspan);
				}
			}
			oGroup.appendChild(oText);
		}

		oSvgParentView.appendChild(oGroup);
		oSvgParentView.insertBefore(oComment, oGroup);

		if (oNodeUI.hasText())	{ // DRAW * IF HAS DETAILS
			/** Example code
			 * <g class="details" onmouseover="show_details(evt)">
			 *  <text x="274" y="110" class="indicator">*</text>
			 *  </g>
			 **/
			Element oDetailsGroup = oNodeUI.createSVGDetailsGroup(oDoc);
			oGroup.appendChild(oDetailsGroup);
		}
		if (oNodeUI.hasTrans())	{ // DRAW TRANSCLUSION INDICATOR  IF NODE HAS TRANSCLUIONS 
			/**	Example SVG Code. The id's of the transclusionLink tpsan elements are 
			* the id of the target map prefixed with 'tlto.' (short for 'tranclusion
			* link to'.	*****
			*  <g class="transclusion indicator"   onmouseover="show_transclusions(evt)" onmouseout="hide_transclusions(evt)" onclick="startShow_transclusions(evt)" >
	  		*	<title>Transclusions</title>
	  		*	<text class="indicator" x="94" y="132" >2</text>
	  		*		<g class="details" display="none">
	    	*		<rect class="border" x="100" y="162" width="120" height="30"></rect>
	    	*		<!-- Note that <text> element does not have x and y co-ordinates so all text must be palce within the <tspan>s to show up in the correct place. -->
	   		*		<text class="transclusionLinks">
			*		<tspan id="Node.137108491691311175409673.tlfrom.137108491691323450095536.tlto.137108491691311175379751" class="transclusionLink" x="202" y="124">A map (top level)</tspan>
			*		<tspan id="Node.137108491691311175409673.tlfrom.137108491691323450095536.tlto.137108491691311088809139" class="transclusionLink" x="202"
			*					y="136">Another map (2nd level)</tspan>
			*		</text>
	  		*		</g>
	  		*	</g>
			*/
			Element oTransGroup = oNodeUI.createSVGTransclusionGroup(oDoc);
			oGroup.appendChild(oTransGroup);
		}
		// If it is transcluded but indicator is not showing update SvgExport.hmDocToTransWrittenInView
		if (!oNodeUI.hasTrans()&& this.getNode().isInMultipleViews())	{
			SvgExport.hmDocToTransWrittenInView.get(oDoc).put(this.getNode().getId(), this.getViewPane().getView().getId());
		}
		if (oNodeUI.hasCodes())	{
			Element oTagsGroup = oNodeUI.createSVGTagsGroup(oDoc);
			oGroup.appendChild(oTagsGroup);
		}
		if (oNodeUI.hasWeight())	{
			Element oWeightGroup = oNodeUI.createSVGWeightGroup(oDoc);
			oGroup.appendChild(oWeightGroup);
		}
		
		if (oNodeUI.hasTaskTime())	{
			Element oTimesGroup =  oNodeUI.createSVGTaskTimesGroup(oDoc);
			oGroup.appendChild(oTimesGroup);
		}
		oSvgParentView.appendChild(oGroup);
		//oParentMapElement.appendChild(oGroup);
		oComment = oDoc.createComment("End  of Node " + sNodeClass  + " " + this.getNode().getId());
		oSvgParentView.appendChild(oComment);
	}
	
}
