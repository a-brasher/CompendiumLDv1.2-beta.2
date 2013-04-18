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

package com.compendium.ui;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Vector;
import java.beans.*;

import javax.swing.*;
import javax.help.*;
import javax.swing.border.*;

import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.compendium.core.datamodel.*;
import com.compendium.core.ICoreConstants;
import com.compendium.learningdesign.io.svg.SvgExport;

import com.compendium.*;
import com.compendium.ui.plaf.*;
import com.compendium.ui.popups.*;
import com.compendium.ui.linkgroups.*;
import com.compendium.ui.dialogs.UILinkContentDialog;


/**
 * The main class that handles information for a map link.
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UILink extends UILine implements PropertyChangeListener {

	static	{
		UIManager.getDefaults().put("LinkUI",  "com.compendium.ui.plaf.LinkUI");
	}
	/** A reference to the label property for PropertyChangeEvents.*/
    public static final String LABEL_PROPERTY 		= "linktext";

	/** A reference to the link type property for PropertyChangeEvents.*/
    public static final String TYPE_PROPERTY 		= "linktype";

	/** The selection color to use for this link.*/
	private static final Color SELECTED_COLOR = Color.yellow; //basic yellow for white bg

	/** The spacing between the end of a line and .*/
	public static final int LINE_SPACING = 1;
	/**
	 * The associated Link datamodel object.
	 * @uml.property  name="oLink"
	 * @uml.associationEnd  
	 */
	protected Link		oLink			= null;

	/**
	 * The originating UINode for this link.
	 * @uml.property  name="oFromNode"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	protected UINode	oFromNode		= null;

	/**
	 * The destination UINode for this link.
	 * @uml.property  name="oToNode"
	 * @uml.associationEnd  multiplicity="(1 1)" inverse="htLinks:com.compendium.ui.UINode"
	 */
	protected UINode	oToNode		= null;

	/**
	 * The value of the label.
	 * @uml.property  name="sText"
	 */
	protected String	sText		="";

	/**
	 * Constructor. Creates a new instance of UILink with the given parameters.
	 * @param link com.compendium.core.datamodel.Link, the associated Link datamodel object.
	 * @param fromNode com.compendium.ui.UINode, the originating UINode for this link.
	 * @param toNode com.compendium.ui.UINode, the destination UINode for this link.
	 */
  	public UILink(Link link, UINode fromNode, UINode toNode) {
	    setFont(ProjectCompendiumFrame.labelFont);
  		
		oLink = link;
	    oLink.addPropertyChangeListener(this);

		CSH.setHelpIDString(this,"node.links");

		// line coordinates will be absolute
		setCoordinateType(UILine.ABSOLUTE);

		// arrow will be pointing to to-node
		setArrow(link.getArrow());

		// set minimum width to 12 to allow for display of rollover indicator
		setMinWidth(12);

		// set line color
		String type = link.getType();
		setForeground(getLinkColor(type));

		// set selected color;
		setSelectedColor(SELECTED_COLOR);

		// set the nodes that are linked with this link
		setFromNode(fromNode);
		setToNode(toNode);

	    //remove all returns and tabs which show up in the GUI as evil black char
	    String label = "";
	    label = oLink.getLabel();
	    if (label == null || label.equals(ICoreConstants.NOLABEL_STRING))
			label = "";

	    label = label.replace('\n',' ');
	    label = label.replace('\r',' ');
	    label = label.replace('\t',' ');
	    setText(label);

		updateUI();
		//setBorder(new LineBorder(Color.black, 1));
		
	    addFocusListener( new FocusListener() {
			public void focusGained(FocusEvent e) {
			    repaint();
			}
			public void focusLost(FocusEvent e) {
			    ((LinkUI)getUI()).resetEditing();
			    repaint();
			}
	    });
	}

	/**
	 * Constructor. Creates a new instance of UILink with the given parameters.
	 * <p>Used for creating dummy link when creating a new link in ui.
	 * <p>
	 * @param link com.compendium.core.datamodel.Link, the associated Link datamodel object.
	 * @param fromNode com.compendium.ui.UINode, the originating UINode for this link.
	 * @param toNode com.compendium.ui.UINode, the destination UINode for this link.
	 * @param type, the link type for this link.
	 */
	public UILink(UINode fromNode, UINode toNode, String type) {
		setFont(ProjectCompendiumFrame.labelFont);

		// line coordinates will be absolute
		setCoordinateType(UILine.ABSOLUTE);

		// arrow will be pointing to to-node
		setArrow(ICoreConstants.ARROW_TO);

		// set minimum width to 12 to allow for display of rollover indicator
		setMinWidth(12);

		// set line color
		if (type.equals(ICoreConstants.DEFAULT_LINK))
			setForeground(Color.gray);
		else if (type.equals(ICoreConstants.SUPPORTS_LINK))
			setForeground(Color.green);
		else if (type.equals(ICoreConstants.OBJECTS_TO_LINK))
			setForeground(Color.red);

		// set selected color;
		setSelectedColor(SELECTED_COLOR);

		// set the nodes that are linked with this link
		setFromNode(fromNode);
		setToNode(toNode);

		updateUI();

	    addFocusListener( new FocusListener() {
			public void focusGained(FocusEvent e) {
			    repaint();
			}
			public void focusLost(FocusEvent e) {
			    ((LinkUI)getUI()).resetEditing();
			    repaint();
			}
	    });
	}

	/**
	 * Increase the font size displayed by one point.
	 * This does not change the setting in the database.
	 * @return the new size.
	 */
	public int increaseFontSize() {
		Font font = getFont();
		int newSize = font.getSize()+1;
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize()+1);	
		super.setFont(newFont);
		((LinkUI)getUI()).getPreferredSize(this);
		repaint(10);		
		return newSize;
	}
	
	/**
	 * Decrease the font size displayed by one point.
	 * This does not change the setting in the database.
	 * @return the new size.
	 */
	public int decreaseFontSize() {
		Font font = getFont();
		int newSize = font.getSize()-1;
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize()-1);	
		super.setFont(newFont);
		((LinkUI)getUI()).getPreferredSize(this);
		repaint(10);
	   	return newSize;
	}
	
	/**
	 * Restore the font to the default settings.
	 *
	 */
	public void setDefaultFont() {
		setFont(ProjectCompendiumFrame.labelFont);
		((LinkUI)getUI()).getPreferredSize(this);
		repaint(10);
	}
	
	/**
	 * Returns the text string that the link displays.
	 * <p>
	 * @return String, the text string that the link displays.
	 * @see #setText
	 */
	public String getText() {
	    return sText;
	}

	/**
	 * Defines the label text this component will display.
	 * <p>
	 * @param text, the new text to display as the link label.
	 */
	public void setText(String text) {
	    String oldValue = sText;

	    try {
			if (oLink != null) {
				oLink.setLabel(text);
				sText = text;
				firePropertyChange(LABEL_PROPERTY, oldValue, sText);
				repaint();
			}
	    }
	    catch(Exception io) {
			io.printStackTrace();
			ProjectCompendium.APP.displayError("Error: (UILink.setText) Unable to update label.\n\n"+io.getMessage());
	    }
    }

	/**
	 * Returns the datamodel link object.
	 * @return com.compendium.core.datamodel.Link.
	 */
	public Link getLink() {
		return oLink;
	}
	
	
	/**
	 * Return a name for  the color of this link that can be used in a CSS style
	 * definition.
	 * 
	 * @return sColor, the color of this link e.g. red, blue etc..
	 */
	public  String getCssColorName() {

		String sColor = "black";
		UILinkType oLinktype = ProjectCompendium.APP.oLinkGroupManager.getLinkType(this.getLinkType());
		
		if (oLinktype == null) {
			return sColor;
		}
		else	{
			sColor = oLinktype.getName().toLowerCase();
			if (sColor.equals("blue (sky)"))
					sColor = "cyan";
			else if (sColor.equals("blue (dark)"))
				sColor = "darkblue";
			else if (sColor.equals("green"))
				sColor = "greenyellow";
			else if (sColor.equals("green (dark)"))
				sColor = "darkgreen";
			else if (sColor.equals("yellow (pale)"))
				sColor = "palegoldenrod";
		}
		return sColor;
	}
	
	/**
	 * @param aLink
	 * @return true if the Link instance corresponding to this UILink is the 
	 * same Link instance tha corresponds to the UILink aLink.
	 */
	public boolean equals(UILink aLink)	{
			return(this.getLink().getId().equals(aLink.getLink().getId()));
	}

	/**
	 * Notification from the UIFactory that the L&F has changed.
	 *
	 * @see JComponent#updateUI
	 */
	public void updateUI() {
		//setUI((LinkUI)UIManager.getUI(this));
		setUI(new LinkUI());
		invalidate();
	}

  	/**
   	 * Returns a string that specifies the name of the l&f class
   	 * that renders this component.
   	 *
   	 * @return String "LinkUI"
   	 *
   	 * @see JComponent#getUIClassID
   	 * @see UIDefaults#getUI
   	 */
  	public String getUIClassID() {
		return "LinkUI";
  	}
  	
  	/**
	 * Convenience method to return the ascent height of the node's current font.
	 * @return int iFontHeight
	 */
	public int getFontHeight()	{
		Font oFont = this.getFont();
		FontMetrics  oFM = this.getFontMetrics(oFont);
		int iFontHeight = oFM.getAscent();
		return(iFontHeight);
	}

	/**
	 * Return the originating UINode for this link.
	 * @return com.compendium.ui.UINode, the originating UINode for this link.
	 */
	public UINode getFromNode() {
		return oFromNode;
	}

	/**
	 * Set the originating node for this link.  Fires a property change event.
	 * @param node com.compendium.ui.UINode, the originating UINode for this link.
	 */
	public void setFromNode(UINode node) {
		UINode oldValue = oFromNode;
		oFromNode = node;
		firePropertyChange("fromnode", oldValue, oFromNode);

		updateConnectionPoints();
	}

	/**
	 * Return the destination UINode for this link.
	 * @return com.compendium.ui.UINode, the destination UINode for this link.
	 */
	public UINode getToNode() {
		return oToNode;
	}

	/**
	 * Set the destination node for this link. Fires a property change event.
	 * @param node com.compendium.ui.UINode, the destination UINode for this link.
	 */
	public void setToNode(UINode node) {
		UINode oldValue = oToNode;
		oToNode = node;
		firePropertyChange("tonode", oldValue, oToNode);

		updateConnectionPoints();
	}

  	/**
	 * Convenience method that searchs the anscestor heirarchy for a UIViewPane instance.
	 * @return com.compendium.ui.UIViewPane, the parent pane for this link.
   	 */
  	public UIViewPane getViewPane() {
		Container p;

		// Search upward for viewpane
		p = getParent();
		while (p != null && !(p instanceof UIViewPane))
	    	p = p.getParent();

		return (UIViewPane)p;
	}

  	/**
	 * Set the absolute from and to points for this link based on the position
	 * of the from and to nodes.
	 */
  	public void setClosestConnectionPoints() {

  		UINode from = getFromNode();
  		UINode to = getToNode();

  		Rectangle rFromBounds = new Rectangle();
  		Rectangle rToBounds = new Rectangle();

  		if (from == null || to == null)
  		return;

  		// get the bounds for each node
  		rFromBounds = from.getBounds();
  		rToBounds = to.getBounds();
  		
  		// Get the NodeUI instances for the to and from nodes
  		NodeUI oFromUI = from.getUI();
  		NodeUI oToUI = to.getUI();
  		  		  		
  		// Get rectangles in which the node icons reside
  		Rectangle rFromIcon = oFromUI.getIconRectangle();
  		// rFromIconPosition will be set to a rectangle surrounding the icon in the parent container of the icons co-ords i.e in the ViewPanes co-ords
  		Rectangle rFromIconPosition = new Rectangle();
  		Rectangle rToIcon = oToUI.getIconRectangle(); 
  		//rToIconPosition will be set to a rectangle surrounding the icon in the parent container of the icons co-ords i.e in the ViewPanes co-ords
  		Rectangle rToIconPosition = new Rectangle();
  		Point ptFromCenter = new Point(); Point ptToCenter = new Point();
  		
  		// calculate the center and position for each node, used as basis for drawing line
  		// between nodes.  The width and height of rFromIcon and rToIcon will be 0 if there is no icon
  		if (rFromIcon != null && rFromIcon.width != 0 && rFromIcon.height != 0) {
  			int xpos = rFromBounds.x + rFromIcon.x;
  			ptFromCenter = new Point(xpos + rFromIcon.width/2, rFromBounds.y+(rFromIcon.height/2));
  			rFromIconPosition = new Rectangle(xpos, rFromBounds.y, rFromIcon.width, rFromIcon.height);
  		}
  		else
  			ptFromCenter = new Point(rFromBounds.x+(rFromBounds.width/2), rFromBounds.y+(rFromBounds.height/2));
  		
  		if (rToIcon != null && rToIcon.width != 0 && rToIcon.height != 0) {
  			int xpos = rToBounds.x + rToIcon.x;
  			ptToCenter = new Point(xpos +(rToIcon.width/2), rToBounds.y+(rToIcon.height/2));
  			rToIconPosition = new Rectangle(xpos, rToBounds.y, rToIcon.width, rToIcon.height);
  		}
  		else
  			ptToCenter = new Point(rToBounds.x+(rToBounds.width/2), rToBounds.y+(rToBounds.height/2));

  		
  		// calculate the intersecting point between the bounds of the node and
  		// the connecting line. We only want the line to draw to the boundary
  		// of the node, not to the center of the node

  		Point[] pts1; Point[] pts2;
  		
  		if (rFromIcon != null && rFromIcon.width != 0) 
  			pts1 = UILine.intersectionWithRectangle(rFromIconPosition, ptFromCenter, ptToCenter);
  		else
  			pts1 = UILine.intersectionWithRectangle(rFromBounds, ptFromCenter, ptToCenter);
  		
  		if (rToIcon != null && rToIcon.width != 0)
  			pts2 = UILine.intersectionWithRectangle(rToIconPosition, ptFromCenter, ptToCenter);
  		else
  			pts2 = UILine.intersectionWithRectangle(rToBounds, ptFromCenter, ptToCenter);
  		
  		//this is a patch.
  		//if both the rectangles have the same center point, then this above
  		//2 array of points has null.
  		if ( (ptFromCenter.x == ptToCenter.x) &&
  				(ptFromCenter.y == ptToCenter.y) ) {
  			if (rFromBounds.y > rToBounds.y) {
  				pts1[0] = new Point(rFromBounds.x + rFromBounds.width, rFromBounds.y);
  				pts1[1] = pts1[0];
  				pts2[0] = new Point(rToBounds.x + rToBounds.width, rFromBounds.y);
  				pts2[1] = pts2[0];
  			} else {
  				pts1[0] = new Point(rFromBounds.x + rFromBounds.width, rToBounds.y);
  				pts1[1] = pts1[0];
  				pts2[0] = new Point(rToBounds.x + rToBounds.width, rToBounds.y);
  				pts2[1] = pts2[0];
  			}
  		}

  		// Figure out which points to use. Make first estimate of  the points to draw the link line between.
  		Point ptClosestPointFrom = getClosestPoint(pts1[0], pts1[1], ptToCenter);
  		Point ptClosestPointTo = getClosestPoint(pts2[0], pts2[1], ptFromCenter);
  		
  		// Work out if line intersects with node indicators
  		// Get the indicator rectangles for each node. 
  		ArrayList<Rectangle> oFromRectangles = oFromUI.getIndicatorRectangles();
  		ArrayList<Rectangle> oToRectangles = oToUI.getIndicatorRectangles();
  		
 		
  		/**
  		 * Recalculate  ptClosestPointFrom, ptClosestPointTo
  		 * Use getFurthestPoint(..) method because the line needs to be drawn
  		 *  from the furthest point of intersection if it intersects with one of the indicators. 
  		 *  If it does not (ii.e. the intersecting points are null) then the original value that
  		 *  of ptClosestPointFrom or ptClosestPointTo will be used.
  		 */
  		Point[] ptsFrom;
  		for (int i=0; i<oFromRectangles.size(); ++i)	{
  			if (oFromRectangles.get(i) != null)	{
  				ptsFrom = UILine.intersectionWithRectangle(oFromRectangles.get(i), ptClosestPointFrom, ptClosestPointTo);
  				if (ptsFrom[0] != null && ptsFrom[1] != null)	{ 
  					ptClosestPointFrom = getFurthestPoint(ptsFrom[0], ptsFrom[1], ptFromCenter);
 				}  				
  			}
  		}
  		Point[] ptsTo;
  		for (int i=0; i<oToRectangles.size(); ++i)	{
  			if (oToRectangles.get(i) != null)	{
  				ptsTo = UILine.intersectionWithRectangle(oToRectangles.get(i), ptClosestPointFrom, ptClosestPointTo);
  				if (ptsTo[0] != null && ptsTo[1] != null)	{
  					ptClosestPointTo = getFurthestPoint(ptsTo[0], ptsTo[1], ptToCenter);
  				}  				
  			}
  		}
  		
  		// Set the origin and destination points
  		setFrom(ptClosestPointFrom);
  		setTo(ptClosestPointTo);
  	}
	/**
	 * Set the absolute from and to points for this link based on the position
	 * of the from and to nodes , then update preferred bounds.
	 */
  	public void updateConnectionPoints() {
  		setClosestConnectionPoints();
  		setBounds(getPreferredBounds()); // swing calls repaint from setBounds.
  		}

	/**
	 * Given two points and a center point, return the point closest to the
	 * center point.
	 *
	 * @param p1, the first point to check.
	 * @param p2, the second point to check.
	 * @param cp, the centerpoint to check.
	 * @return Point, the point closest to the center point.
	 */
	private Point getClosestPoint(Point p1, Point p2, Point cp) {
		if (p1 == null)
			return p2;
		if (p2 == null)
			return p1;

		double hypo1 = Math.sqrt((cp.x-p1.x)*(cp.x-p1.x)+(cp.y-p1.y)*(cp.y-p1.y));
		double hypo2 = Math.sqrt((cp.x-p2.x)*(cp.x-p2.x)+(cp.y-p2.y)*(cp.y-p2.y));

		if (hypo1 <= hypo2)
			return p1;
		else
			return p2;
	}
	
	/**
	 * Given two points and a center point, return the point furthest from the
	 * center point.
	 *
	 * @param p1, the first point to check.
	 * @param p2, the second point to check.
	 * @param cp, the centerpoint to check.
	 * @return Point, the point closest to the center point.
	 */
	private Point getFurthestPoint(Point p1, Point p2, Point cp) {
		if (p1 == null)
			return p2;
		if (p2 == null)
			return p1;

		double hypo1 = Math.sqrt((cp.x-p1.x)*(cp.x-p1.x)+(cp.y-p1.y)*(cp.y-p1.y));
		double hypo2 = Math.sqrt((cp.x-p2.x)*(cp.x-p2.x)+(cp.y-p2.y)*(cp.y-p2.y));

		if (hypo1 >= hypo2)
			return p1;
		else
			return p2;
	}

	
	public void generateSVG(Document oDoc, boolean bHasTransclusions)	{
		generateComponentSVG(oDoc, bHasTransclusions);
		generateChildrenSVG(oDoc);
	}
	private void generateChildrenSVG(Document oDoc) {
		// TODO Auto-generated method stub
		
	}

	private void generateComponentSVG(Document oDoc, boolean bHasTransclusions) {
		if (oDoc == null )
			return;
		
		Element oSvgRoot = oDoc.getDocumentElement();
		// get from and to points
		//UILink link;
		Point from = this.getFrom();
		Point to   = this.getTo();

		// if one of the points is missing don't draw line
		if (from == null || to == null)
			return;

		// determine relative to and from points
		Point originalFrom = new Point();
		Point originalTo = new Point();
		Point actualFrom = new Point();
		Point actualTo = new Point();

		if (this.getCoordinateType() == UILine.RELATIVE) {
			// coordinates already relative to this components coordinate system
			originalFrom = from;
			originalTo = to;
			actualFrom = ((LinkUI)this.getUI()).getActualFromPoint();
			actualTo = ((LinkUI)this.getUI()).getActualToPoint();
		}
		else {
			// calculate the relative coordinates by converting the coordinates from
			// the parents coordinate system to this components coordinate system
			Container parent = this.getParent();
			if (parent != null) {
				originalFrom = SwingUtilities.convertPoint(parent, from, this);
				originalTo = SwingUtilities.convertPoint(parent, to, this);
				actualFrom = SwingUtilities.convertPoint(parent, actualFrom, this);
				actualTo = SwingUtilities.convertPoint(parent, actualTo, this);
			}
			else {
				System.out.println("UILink.generateComponentSVG no link drawn for UILink id:" + this.getLink().getId());
				return;
			}
		}

			
		// Get the id of the View that this node is in
		String sParentViewId = this.getViewPane().getView().getId();
		// Get the SVG element representing the View that this node is in
		Element oSvgParentView = this.getSvgParentMap(sParentViewId, oDoc);
		Element oLinkGroup = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_G_TAG);
		String sTransLinkIndicator = "";
		if (bHasTransclusions)	{
			sTransLinkIndicator = "trans.";
		}
		oLinkGroup.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_ID_ATTRIBUTE, 
				sTransLinkIndicator + SvgExport.sLinkClass + SvgExport.sIdFragmentConnector + this.getLink().getId());
		oLinkGroup.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_CLASS_ATTRIBUTE, SvgExport.sLinkClass);
		Element oTitle = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_TITLE_TAG);
		oTitle.setTextContent(this.getText());
		oLinkGroup.appendChild(oTitle);
		Element oDesc = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_DESC_TAG);
		oDesc.setTextContent("Link from node '" + this.getFromNode().getText() + 
					"' to node '" + this.getToNode().getText() + "'.");
		oLinkGroup.appendChild(oDesc);
		Element oLine = drawSvgLink(from.x, from.y, to.x, to.y, oDoc);
		oLinkGroup.appendChild(oLine);
		Element oLabel;
		if (!this.getText().isEmpty())	{
			ArrayList<ArrayList<Element>> oLabelElements = drawSvgTextLabel(oDoc);
			ArrayList<Element> oBackgroundElements = oLabelElements.get(0);
			ArrayList<Element> oTextElements = oLabelElements.get(1);
			ListIterator<Element> oIt =  oBackgroundElements.listIterator();
			ListIterator<Element> oItTe =  oTextElements.listIterator();
			while (oIt.hasNext())	{
			oLinkGroup.appendChild(oIt.next());
			}
			while (oItTe.hasNext())	{
				oLinkGroup.appendChild(oItTe.next());
				}
		}
	
		oSvgParentView.appendChild(oLinkGroup);
		
		//Add comments describing the link  to SVG
		String sFromNodeClass = SvgExport.sGeneralClass;
		String sToNodeClass = SvgExport.sGeneralClass;
		try	{
			sFromNodeClass = SvgExport.hmNodeClasses.get(this.getFromNode().getNode().getType());
			sToNodeClass = SvgExport.hmNodeClasses.get(this.getToNode().getNode().getType());
		}
		catch (Exception e) {
			System.out.print("Array SvgExport.sStandardNodeClasses out of bounds: "  + e.getMessage());
		}
		Comment oComment = oDoc.createComment("Link from " + sFromNodeClass + " node '" + this.getFromNode().getText() + 
				"' to " + sToNodeClass + " node '" + this.getToNode().getText() + "'.");
		oSvgParentView.insertBefore(oComment, oLinkGroup);
		Comment oEndComment = oDoc.createComment("End of link");
		oSvgParentView.appendChild(oEndComment);	
	}

	private ArrayList<ArrayList<Element>>  drawSvgTextLabel(Document oDoc) {
		int textWidth = 0;
		ArrayList<ArrayList<Element>> oLabelElements = new ArrayList<ArrayList<Element>>();
		ArrayList<Element> oTextElements = new ArrayList<Element>();
		ArrayList<Element> oBackgroundElements = new ArrayList<Element>(); 
		String sText = this.getText();
		Font font = this.getFont();
		FontMetrics oFm = this.getFontMetrics(font);
		String sTextCssClass = "linklabel" + " text" + font.getSize();
		LinkUI oLinkUI = (LinkUI)this.getUI(); 
		 Vector<TextRowElement> oTREs = oLinkUI.getTextRowElements();
		 Element oText = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_TEXT_TAG);
		 Rectangle oLabelRectangle = oLinkUI.getLabelRectangle();
		 
		 int iYpos = this.getY();	int x;
		 int iXpos = this.getX();	int y; String sYRect = "0";
		 
		 int iFontHeight = this.getFontHeight(); int iRectSpacer = 1;
		 // Sometimes oLabelRectangle is null (this should be trapped elsewhere, but this is a quick hack until that's done
		 if (oLabelRectangle == null)	{
			 oLabelRectangle = new Rectangle(iXpos + this.getWidth()/2,  iYpos + this.getHeight()/2, oFm.stringWidth(sText), iFontHeight);
			 System.out.println("Error: oLabelRectangle is null for link " + sText);
		 }
		 if (oTREs == null || oTREs.isEmpty())	{
			 	textWidth = oFm.stringWidth(sText);
				//Label text will be aligned centrally around the x co-ord 
				x =(int)Math.round(oLabelRectangle.getX() +  iXpos); 
				int xText = x +  ((int)Math.round(oLabelRectangle.getWidth()/2));
				y =  (int)Math.round(oLabelRectangle.getY() + iYpos + iFontHeight);
				//sYRect = Integer.toString(Math.round(((int)(y - oLabelRectangle.getHeight()))));
				sYRect = Integer.toString(y - iFontHeight);
				oText.setAttribute("x", Integer.toString(xText));
				oText.setAttribute("y", Integer.toString(y));
				oText.setAttribute("class", sTextCssClass);
				oText.setTextContent(this.getText());
				 Element oLabelBackgroundRect = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_RECT_TAG);
				oLabelBackgroundRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_X_ATTRIBUTE, Integer.toString(x));
				oLabelBackgroundRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_Y_ATTRIBUTE, sYRect);
				oLabelBackgroundRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_WIDTH_ATTRIBUTE, Long.toString(Math.round(oLabelRectangle.getWidth())));
				oLabelBackgroundRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_HEIGHT_ATTRIBUTE, Long.toString(Math.round(oLabelRectangle.getHeight() + iRectSpacer)));
				oLabelBackgroundRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_CLASS_ATTRIBUTE, "linklabelrect");
				oBackgroundElements.add(oLabelBackgroundRect);
				oLabelElements.add(oBackgroundElements);
				oTextElements.add(oText);
				oLabelElements.add(oTextElements);
				//oLabelBackgroundRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_RX_ATTRIBUTE, "12");
			}
			else	{
				TextRowElement oCurrentElement = null;
				//System.out.println("iXpos = " + iXpos + ", iYpos = " + iYpos);
				int iYVal = 0; String sTextForLine = "";
				//Get width of widest textRowElement  
				double dbMaxWidth = 0; double dbCurrWidth = 0;
				for (int i=0; i<oTREs.size(); ++i)	{
					dbCurrWidth = oTREs.elementAt(i).getTextRect().getWidth();
					if (dbCurrWidth > dbMaxWidth)	
						dbMaxWidth = dbCurrWidth;
				}
				
				for (int i=0; i<oTREs.size(); ++i)	{
					 Element oLabelBackgroundRect = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_RECT_TAG);
					oCurrentElement = oTREs.elementAt(i);
					sTextForLine = oCurrentElement.getText();	
					textWidth = oFm.stringWidth(sTextForLine);
					iYVal = (int)Math.round( oCurrentElement.getTextRect().getY()+iYpos );
					x = (int)Math.round(oCurrentElement.getTextRect().getX()   + iXpos);
					int xText =  x + (int)Math.round( (oCurrentElement.getTextRect().getWidth()/2));
					if (oCurrentElement.getTextRect().getWidth() < dbMaxWidth)	{
						xText  += (dbMaxWidth-textWidth)/2;
						x += (dbMaxWidth-textWidth)/2;
					}
					if (i==0)	{ // If it's the rirst row, set the <text> elements contents and attributes
						//The link label will be aligned centrally by CSS so position it at centre of the text rectangle
						sYRect = Long.toString(Math.round(iYVal - oCurrentElement.getTextRect().getHeight()));
						oText.setAttribute("x", Integer.toString(xText));
						oText.setAttribute("y", Integer.toString(iYVal));
						oText.setAttribute("class", sTextCssClass);
						oText.setTextContent(sTextForLine);
						
						oLabelBackgroundRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_X_ATTRIBUTE, Integer.toString(x));
						oLabelBackgroundRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_Y_ATTRIBUTE, sYRect);
						oLabelBackgroundRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_WIDTH_ATTRIBUTE, Long.toString(Math.round(oCurrentElement.getTextRect().getWidth())));
						oLabelBackgroundRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_HEIGHT_ATTRIBUTE, Long.toString(Math.round(oCurrentElement.getTextRect().getHeight())));
						oLabelBackgroundRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_CLASS_ATTRIBUTE, "linklabelrect");
					}
					else	{ // If it's the second or greater line, create <tspan> children of the <text> element
						Element oTspan = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_TSPAN_TAG);
						//The link label will be aligned centrally by CSS so position it at centre of the text rectangle
						//
						//sYRect = Integer.toString((int)Math.round(oCurrentElement.getTextRect().getY()));
						sYRect = Long.toString(Math.round(iYVal - oCurrentElement.getTextRect().getHeight()));
						oTspan.setAttribute("x", Integer.toString(xText));
						oTspan.setAttribute("y", Integer.toString(iYVal));
						oTspan.setTextContent(sTextForLine);
						oText.appendChild(oTspan);
						
						oLabelBackgroundRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_X_ATTRIBUTE, Integer.toString(x));
						oLabelBackgroundRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_Y_ATTRIBUTE, sYRect);
						oLabelBackgroundRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_WIDTH_ATTRIBUTE, Long.toString(Math.round(oCurrentElement.getTextRect().getWidth())));
						oLabelBackgroundRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_HEIGHT_ATTRIBUTE, Long.toString(Math.round(oCurrentElement.getTextRect().getHeight())));
						oLabelBackgroundRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_CLASS_ATTRIBUTE, "linklabelrect");
					}
					oBackgroundElements.add(oLabelBackgroundRect);
				}
				
				oLabelElements.add(oBackgroundElements);
				oTextElements.add(oText);
				oLabelElements.add(oTextElements);
			}
			
		return oLabelElements;
	}

	private Element drawSvgLink(int x, int y, int x2, int y2, Document oDoc) {			
		
		Element oLine = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_LINE_TAG);
		oLine.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, "x1", Integer.toString(x));
		oLine.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, "y1", Integer.toString(y));
		oLine.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, "x2", Integer.toString(x2));
		oLine.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, "y2", Integer.toString(y2));
		oLine.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_CLASS_ATTRIBUTE, SvgExport.sLinkClass + " " + this.getCssColorName());
		int iArrowType = this.getArrow();
		String sArrowRef = "url(" + SVGGraphics2D.SIGN_POUND+ SvgExport.sArrowHead + ")";
		 // DRAW ARROW
		switch (this.getArrow()) {
			case ICoreConstants.NO_ARROW: {
				break;
			}
			case ICoreConstants.ARROW_TO: {
 				//drawArrow(g, originalFrom, originalTo, this.CURRENT_ARROW_WIDTH);
				oLine.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.CSS_MARKER_END_PROPERTY, sArrowRef);
				break;
			}
			case ICoreConstants.ARROW_FROM: {
				oLine.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.CSS_MARKER_START_PROPERTY, sArrowRef);
				break;
			}
			case ICoreConstants.ARROW_TO_AND_FROM: {
				oLine.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.CSS_MARKER_END_PROPERTY, sArrowRef);
				oLine.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.CSS_MARKER_START_PROPERTY, sArrowRef);
				break;
			}
		}
		return oLine;
	}
	
	/**
	 * Get the SvG element that is the SVG representation of the parent view
	 * for this node in the SVG Document oDoc.
	 * @param sParentViewId
	 * @param oDoc
	 * @return the parent Element, or null if the parent element can not be found
	 */
	protected Element getSvgParentMap(String sParentViewId,  Document oDoc)	{
		String sViewClass = SvgExport.sGeneralClass;
		Element oSvgParentView =  null;
		
		try	{
			sViewClass = SvgExport.hmViewNodeClasses.get(this.getViewPane().getView().getType());
		}
		catch (Exception e) {
			System.out.print("Array SvgExport.hmViewNodeClasses out of bounds: index accessed = "  + this.getViewPane().getView().getType());
		}

		oSvgParentView = oDoc.getElementById(sViewClass+SvgExport.sIdFragmentConnector + sParentViewId);
		if (oSvgParentView == null)	{
			System.out.println("Error generating SVG, no svg parent view  for UILink name " + this.getText()+ ", Id = " + this.getLink().getId());
		}
		return oSvgParentView;

	}

	/**
	 * Open a popup menu  for this link.
	 * @param linkui, the ui instance to pass to the popup menu.
	 * @param x, the x position for the popup menu.
	 * @param y, the y position for this popup menu.
	 */
	public void showPopupMenu(LinkUI linkui, int x, int y) {
		String userID = ProjectCompendium.APP.getModel().getUserProfile().getId();
		UILinkPopupMenu pop = new UILinkPopupMenu("Popup menu", linkui, userID);
		pop.setCoordinates(x,y);
		pop.setViewPane(getViewPane());
		pop.show(this,x,y);
	}

	/**
	 * Open a UILinkDialog instance on the contents tab.
	 */
	public void showEditDialog() {
		UILinkContentDialog dlg = new UILinkContentDialog(ProjectCompendium.APP, this, UILinkContentDialog.CONTENTS_TAB);
		UIUtilities.centerComponent(dlg, ProjectCompendium.APP);
		dlg.setVisible(true);
	}

	/**
	 * Open a UILinkDialog instance on the properties tab.
	 */
	public void showPropertiesDialog() {
		UILinkContentDialog dlg = new UILinkContentDialog(ProjectCompendium.APP, this, UILinkContentDialog.PROPERTIES_TAB);
		UIUtilities.centerComponent(dlg, ProjectCompendium.APP);
		dlg.setVisible(true);
	}

	/**
	 * Convenience method that moves this component to position 0 if it's
	 * parent is a JLayeredPane.
   	 */
  	public void moveToFront() {
		if (getParent() != null && getParent() instanceof JLayeredPane) {
	  		JLayeredPane l =  (JLayeredPane)getParent();
	  		l.moveToFront(this);
		}
  	}

  	/**
	 * Convenience method that moves this component to position -1 if it's
   	 * parent is a JLayeredPane.
   	 */
  	public void moveToBack() {
		if (getParent() != null && getParent() instanceof JLayeredPane) {
	  		JLayeredPane l =  (JLayeredPane)getParent();
	  		l.moveToBack(this);
		}
  	}

	/**
	 * Set the arrow head style for this link.
	 * @param arrow, the arrow head style for this link.
	 */
	public void setArrow(int arrow) {

		super.setArrow(arrow);
	}

	/**
	 * Update the arrow head style for this link.
	 * @param arrow, the arrow head style for this link.
	 */
	public void updateArrow(int arrow) {

		try {
			oLink.setArrow(arrow);
			setArrow(arrow);
		}
		catch(Exception ex) {
			ProjectCompendium.APP.displayError("Error: (UILink.updateArrow) Unable to update arrow\n\n"+ex.getMessage());
		}
	}

	/**
	 * Set the link type for this link.
	 * @param type, the link type for this link.
	 */
	public void setLinkType(String type) {
		try {
			String oldValue = oLink.getType();
			oLink.setType(type);
			setForeground(getLinkColor(type));

			firePropertyChange(TYPE_PROPERTY, oldValue, type);
			repaint();
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Error: (UILink.setLinkType) Unable to update link type\n\n"+ex.getMessage());
		}
	}

	/**
	 * Get the link type for this link.
	 * @return String, the link type for this link.
	 */
	public String getLinkType() {
		return oLink.getType();
	}


   /**
	* Returns the Link type for the given link type description
	* @param type, the link type description to return the link type for.
	* @param int, the link type assoicated with the given description.
	*/
	public static String getLinkType(String type) {

		String linkType = "";

		if(type.equals(ICoreConstants.sRESPONDSTOLINK)) {
			linkType = ICoreConstants.RESPONDS_TO_LINK;
		}
		else if(type.equals(ICoreConstants.sSUPPORTSLINK)) {
			linkType = ICoreConstants.SUPPORTS_LINK;
		}
		else if(type.equals(ICoreConstants.sOBJECTSTOLINK)) {
			linkType = ICoreConstants.OBJECTS_TO_LINK;
		}
		else if(type.equals(ICoreConstants.sCHALLENGESLINK)) {
			linkType = ICoreConstants.CHALLENGES_LINK;
		}
		else if(type.equals(ICoreConstants.sSPECIALIZESLINK)) {
			linkType = ICoreConstants.SPECIALIZES_LINK;
		}
		else if(type.equals(ICoreConstants.sEXPANDSONLINK)) {
			linkType = ICoreConstants.EXPANDS_ON_LINK;
		}
		else if(type.equals(ICoreConstants.sRELATEDTOLINK)) {
			linkType = ICoreConstants.RELATED_TO_LINK;
		}
		else if(type.equals(ICoreConstants.sABOUTLINK)) {
			linkType = ICoreConstants.ABOUT_LINK;
		}
		else if(type.equals(ICoreConstants.sRESOLVESLINK)) {
			linkType = ICoreConstants.RESOLVES_LINK;
		}
		else {
			linkType = ICoreConstants.DEFAULT_LINK;
		}

		return linkType;
	}

	/**
	 * Get the link type description for the given link type.
	 * @param type, the link type to return the description for.
	 * @return String, the description for the given link type.
	 */
/*	public static String getLinkTypeName(String type) {

		String linkType = "";
		Color linkColor = null;
		UILinkType oLinktype = ProjectCompendium.APP.oLinkGroupManager.getLinkType(type);

		if (oLinktype == null) {
			int nType = 0;
			try {
				nType = new Integer(type).intValue();
			catch(NumberFormatException ex) {
				nType = -1;
			}

			switch (nType) {
				case ICoreConstants.RESPONDS_TO_LINK: {
					linkType = ICoreConstants.sRESPONDSTOLINK;
					break;
				}
				case ICoreConstants.SUPPORTS_LINK: {
					linkType = ICoreConstants.sSUPPORTSLINK;
					break;
				}
				case ICoreConstants.OBJECTS_TO_LINK: {
					linkType = ICoreConstants.sOBJECTSTOLINK;
					break;
				}
				case ICoreConstants.CHALLENGES_LINK: {
					linkType = ICoreConstants.sCHALLENGESLINK;
					break;
				}
				case ICoreConstants.SPECIALIZES_LINK: {
					linkType = ICoreConstants.sSPECIALIZESLINK;
					break;
				}
				case ICoreConstants.EXPANDS_ON_LINK: {
					linkType = ICoreConstants.sEXPANDSONLINK;
					break;
				}
				case ICoreConstants.RELATED_TO_LINK: {
					linkType = ICoreConstants.sRELATEDTOLINK;
					break;
				}
				case ICoreConstants.ABOUT_LINK: {
					linkType = ICoreConstants.sABOUTLINK;
					break;
				}
				case ICoreConstants.RESOLVES_LINK: {
					linkType = ICoreConstants.sRESOLVESLINK;
					break;
				}
				default : {
					linkType = ICoreConstants.sDEFAULTLINK;
					break;
				}
			}
		}
		else {
			return oLinktype.getName();
		}

		return linkType;
	}
*/

	/**
	 * Get the link type label for the given link type.
	 * @param type, the link type to return the description for.
	 * @return String, the description for the given link type.
	 */
	public static String getLinkTypeLabel(String type) {

		UILinkType oLinktype = ProjectCompendium.APP.oLinkGroupManager.getLinkType(type);
		String linkType = "";

		if (oLinktype == null) {
			if (type.equals(ICoreConstants.RESPONDS_TO_LINK))
				linkType = ICoreConstants.sRESPONDSTOLINKLABEL;
			else if (type.equals(ICoreConstants.SUPPORTS_LINK))
				linkType = ICoreConstants.sSUPPORTSLINKLABEL;
			else if (type.equals(ICoreConstants.OBJECTS_TO_LINK))
				linkType = ICoreConstants.sOBJECTSTOLINKLABEL;
			else if (type.equals(ICoreConstants.CHALLENGES_LINK))
				linkType = ICoreConstants.sCHALLENGESLINKLABEL;
			else if (type.equals(ICoreConstants.SPECIALIZES_LINK))
				linkType = ICoreConstants.sSPECIALIZESLINKLABEL;
			else if (type.equals(ICoreConstants.EXPANDS_ON_LINK))
				linkType = ICoreConstants.sEXPANDSONLINKLABEL;
			else if (type.equals(ICoreConstants.RELATED_TO_LINK))
				linkType = ICoreConstants.sRELATEDTOLINKLABEL;
			else if (type.equals(ICoreConstants.ABOUT_LINK))
				linkType = ICoreConstants.sABOUTLINKLABEL;
			else if (type.equals(ICoreConstants.RESOLVES_LINK))
				linkType = ICoreConstants.sRESOLVESLINKLABEL;
			else
				linkType = "Unknown";
		}
		else {
			return oLinktype.getName();
		}

		return linkType;
	}

	/**
	 * Get the link color based on its type.
	 * @param type, the link type to return the color for.
	 * @return Color, the color for the given link type.
	 */
	public static Color getLinkColor(String type) {

		Color linkColor = null;
		UILinkType oLinktype = ProjectCompendium.APP.oLinkGroupManager.getLinkType(type);

		if (oLinktype == null) {
			if (type.equals(ICoreConstants.RESPONDS_TO_LINK))
				linkColor = Color.magenta;
			else if (type.equals(ICoreConstants.SUPPORTS_LINK))
				linkColor = Color.green;
			else if (type.equals(ICoreConstants.OBJECTS_TO_LINK))
				linkColor = Color.red;
			else if (type.equals(ICoreConstants.CHALLENGES_LINK))
				linkColor = Color.pink;
			else if (type.equals(ICoreConstants.SPECIALIZES_LINK))
				linkColor = Color.blue;
			else if (type.equals(ICoreConstants.EXPANDS_ON_LINK))
				linkColor = Color.orange;
			else if (type.equals(ICoreConstants.RELATED_TO_LINK))
				linkColor = Color.black;
			else if (type.equals(ICoreConstants.ABOUT_LINK))
				linkColor = Color.cyan;
			else if (type.equals(ICoreConstants.RESOLVES_LINK))
				linkColor = Color.gray;
			else
				linkColor = Color.black;
		}
		else
			linkColor = oLinktype.getColour();

		return linkColor;
	}

	/**
	 * Toggle selection.
	 */
	public void controlClick() {

		if(this.isSelected()) {
			//deselect from the group
			setSelected(false);
			getViewPane().removeLink(this);
		}
		else {
			//select and add to the group
			setSelected(true);
			getViewPane().setSelectedLink(this,ICoreConstants.MULTISELECT);
			moveToFront();
		}
  	}

	/**
	 * Handle a PropertyChangeEvent.
	 * @param evt, the associated PropertyChangeEvent to handle.
	 */
	public void propertyChange(PropertyChangeEvent evt) {

	    String prop = evt.getPropertyName();
	    Object newvalue = evt.getNewValue();

	    if (prop.equals(Link.LABEL_PROPERTY)) {
			setText((String)evt.getNewValue());
	    }
	    else if (prop.equals(Link.TYPE_PROPERTY)) {
			setLinkType( (String)evt.getNewValue() );
	    }
	    else if (prop.equals(Link.ARROW_PROPERTY)) {
			updateArrow( ((Integer)evt.getNewValue()).intValue() );
	    }

	    repaint();
	}

	/**
	 * Create a SVG representation of  this link, for occurences when the SVG for 
	 * transcluded occurrences have already been created, i.e. a representation which
	 * refers to the original via a <use> tag.
	 * @param oDoc, the SVG document to which the SVG representation of this link will be added.
	 */
	public void generateTranclusionUseSVG(Document oDoc) {
		String sIdThisLink = this.getLink().getId();
		if (oDoc == null )
			return;
		//Need to generate  code akin to: <use x="316" y="263" xlink:href="#link.137108491691311175409673"/>
		// Only have one link class in SvgExport.hmLinkClasses at the moment 
		String sLinkType = ICoreConstants.RESPONDS_TO_LINK;
		String sLinkClass = "";
		try	{
			sLinkClass = SvgExport.hmLinkClasses.get(sLinkType);
		}
		catch (Exception e) {
			System.out.print("Array SvgExport.sStandardNodeClasses out of bounds: index accessed = "  + this.getLink().getType());
		}
	String sIdFromNode = 	this.getFromNode().getNode().getId();
	String sIdToNode = 	this.getToNode().getNode().getId();
	//Get the NodePositions this link should be drawn between
	NodePosition oNpFrom = this.getFromNode().getNodePosition();
	NodePosition oNpTo = this.getToNode().getNodePosition();
	//Get the co-ordinates of the from node that this link starts from
	int iXPosFromTrans = oNpFrom.getXPos(); int iYPosFromTrans = oNpFrom.getYPos();
	int iXPosToTrans = oNpTo.getXPos(); int iYPosToTrans = oNpTo.getYPos();
	// Find the transcluded from node for this link whose representation has already been written
	//String sIdofViewTranscludedInto = SvgExport.hmDocToTransLinkWrittenInView.get(oDoc).get(sIdFromNode);
	String sIdofViewTranscludedInto = SvgExport.getHmDocToTransLinkWrittenInView().get(oDoc).get(sIdThisLink);
	View  oViewTransCludedInto = View.getView(sIdofViewTranscludedInto);
	NodePosition oNpFromSource = oViewTransCludedInto.getNodePosition(sIdFromNode);
	NodePosition oNpToSource = oViewTransCludedInto.getNodePosition(sIdToNode);

	int iXPosFromSource = oNpFromSource.getXPos(); int iYPosFromSource = oNpFromSource.getYPos();
	int iXPosToSource = oNpToSource.getXPos(); int iYPosToSource = oNpToSource.getYPos();
	// Element oSvgParentView = oDoc.getElementById(SvgExport.sMapViewClass+SvgExport.sIdFragmentConnector + this.getViewPane().getView().getId());
	Element oSvgParentView = this.getSvgParentMap(this.getViewPane().getView().getId(), oDoc);
	
	//int iXPosTrans = oNpFrom.getXPos(); int iYPosTrans = oNpFrom.getYPos();
	Element oUse = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_USE_TAG);
	oUse.setAttribute(SVGGraphics2D.XLINK_HREF_QNAME, org.apache.batik.svggen.SVGSyntax.SIGN_POUND + sLinkClass + SvgExport.sIdFragmentConnector + sIdThisLink); 
	oUse.setAttribute("x",  Integer.toString(iXPosFromTrans - iXPosFromSource - (iXPosToTrans - iXPosToSource)));
//	oUse.setAttribute("x",  Integer.toString(iXPosFromTrans - iXPosFromSource + iXPosToTrans - iXPosToSource));
//	oUse.setAttribute("x",  Integer.toString(iXPosFromTrans));
//	oUse.setAttribute("y", Integer.toString(iYPosFromTrans - iYPosFromSource + iYPosToTrans - iYPosToSource));
	oUse.setAttribute("y", Integer.toString(iYPosFromTrans - iYPosFromSource - (iYPosToTrans - iYPosToSource)));
//	oUse.setAttribute("y", Integer.toString(iYPosFromTrans ));
	oSvgParentView.appendChild(oUse);
	Comment oComment = oDoc.createComment("Start of Link Transclusion: " + sLinkClass  + " " + this.getLink().getId());
	oSvgParentView.insertBefore(oComment, oUse);
	oComment = oDoc.createComment("End  of Link Transclusion " + sLinkClass  + " " + this.getLink().getId());
	oSvgParentView.appendChild(oComment);
	}
	
	
	/**
	 * Return the preferred bounds for this object.
	 * This is just a copy of the super classes method at the moment, but need to edit this 
	 * to make links draw correctly before updateConnectionPoints() is called.
	 * @return Rectangle, the preferred bounds for this object.
	 */
/**	public Rectangle getPreferredBounds() {
		LineUI oUI = getUI();
		this.setClosestConnectiionPoints();
		
		if (getUI() != null)
			return getUI().getPreferredBounds(this);
		else
			return getBounds();
	}
	
**/
	
}
