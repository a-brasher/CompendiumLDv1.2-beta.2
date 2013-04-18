/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2010 Verizon Communications USA and The Open University UK    *
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
package com.compendium.learningdesign.ui.plaf;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.compendium.core.datamodel.View;
import com.compendium.learningdesign.ui.UILdNode;
import com.compendium.learningdesign.ui.UILdTaskNode;
import com.compendium.ui.UINode;

/**
 * @author ajb785
 *
 */
public class LdTaskNodeUI extends LdNodeUI {

	
	/**
	 * Create a new NodeUI instance.
	 * @param c, the component this is the ui for - NOT REALLY USED AT PRESENT HERE.
	 */
  	public static ComponentUI createUI(JComponent c) {

  		LdTaskNodeUI nodeui = new LdTaskNodeUI();
	  	return nodeui;
  	}
  	
  	/**
	 * Return the node's preferred size.
	 * @param c, the component to return the preferred size for.
	 * @return Dimension, the preferred size for the given node.
	 */
 	public Dimension getPreferredSize(JComponent c) {

		UINode node = (UILdTaskNode)c;

		String text = node.getText();
		Icon icon = node.getIcon();
		Insets insets = node.getInsets();
		Font font = node.getFont();

		int dx = insets.left + insets.right;
		int dy = insets.top + insets.bottom;

		if ((icon == null) && ((text == null) || ((text != null) && (font == null)))) {
			return new Dimension(dx, dy);
		}
		else if ((text == null) || ((icon != null) && (font == null))) {
			return new Dimension(icon.getIconWidth() + dx, icon.getIconHeight() + dy);
		}
		else {
			return calculateDimension(node);
		}
  	}
 	
 	/**
 	 * Helper method to generate the SVG code to represent the task times  for a task node.
 	 * @param oDoc
 	 * @return - SVG <g> tag representing the weight of the node
 	 */
 	public Element createSVGTaskTimesGroup(Document oDoc)	{
 		// Font for Indicators 
 		Font indicatorFont = new Font("Dialog" , Font.BOLD, 10);
 		int iIndicatorFontHeight = 10; // size is set to 10 in line above!
 		Font oInitialFont = this.getUINode().getFont();
 		this.getUINode().setFont(indicatorFont);
 		// Get the height of the font
 		int iFontHeight = this.getUINode().getFontHeight();
 		
 	//	int iXpos = this.getUINode().getX();
 		double iYpos = this.getUINode().getY() + + this.getIconRectangle().getY();
 		double iXpos = this.getUINode().getX() + this.getIconRectangle().getX();
 		
 		Element oTimeGroup = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_G_TAG);
 	// Get the task time - this is always stored in minutes on a UILdTaskNode
		String sTime = ((UILdTaskNode)this.getUINode()).getTaskTimeString();
		
 		oTimeGroup.setAttribute(SVGGraphics2D.SVG_CLASS_ATTRIBUTE, "time indicator");
 	    //oWeightGroup.setAttribute(SVGGraphics2D.SVG_ONMOUSEOVER_ATTRIBUTE, "show_tags(evt)");
 	    Element oTimeIndicator =  oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_TEXT_TAG);
 	    oTimeIndicator.setAttribute("x", Integer.toString((int)Math.round(this.getTimeRectangle().getX() + iXpos)));
 	    oTimeIndicator.setAttribute("y", Integer.toString((int)Math.round(this.getTimeRectangle().getY() + iYpos)));
 	    oTimeIndicator.setAttribute(SVGGraphics2D.SVG_CLASS_ATTRIBUTE, "indicator");
 	    oTimeIndicator.setTextContent(sTime);
 	    oTimeGroup.appendChild(oTimeIndicator);
 	    //Reset font
 	    this.getUINode().setFont(oInitialFont);
 		return oTimeGroup;
 	}
 	
 	/**
	 * Returns a rectangle into which the task time indicator will be drawn,
	 * or null if there is no task time  for this node. Note that the task time
	 * indicator is positioned at the middle left-hand side of the node. 
	 * @param iconR
	 * @param nodeSumm
	 * @param sfm
	 * @param bSmallIcon
	 * @return
	 */
 	/**
	public Rectangle calculateTaskTimeIndicatorRectangle(Rectangle iconR,  Font oFont, FontRenderContext oFrc, boolean bSmallIcon)	{
		Rectangle rTemp = null;		
		String sTime = ((UILdTaskNode)this.getUINode()).getTaskTimeString();
		
		int extra = 2;
		int back = 8;
		int yOffset = iconR.height/2;
		if (oNode.getNodePosition().getShowSmallIcon())  {
			extra = 4;
			back = 6;
		}

		Rectangle2D bounds = oFont.getStringBounds(sTime, oFrc); //$NON-NLS-1$
		float fTimeWidth = (float) bounds.getWidth(); 
		int h = new Float(bounds.getHeight()).intValue();
		int w = new Float(fTimeWidth).intValue();
		//		int iMaxAdv = sfm.getMaxAdvance();
		int iMaxAdv = new Float(oFont.getMaxCharBounds(oFrc).getWidth()).intValue();
		int iSpacing = Math.min(3, iMaxAdv) ;
		int iFudgeFactor = 0;
		fTimeWidth += iSpacing;
		fTimeWidth += iFudgeFactor;
		if (fTimeWidth >fLeftWidestExtra )
			fLeftWidestExtra  = fTimeWidth;
		rTimeRectangle = new Rectangle(iconR.x-(w + iSpacing + iFudgeFactor),  iconR.y+yOffset + extra, w, h+extra);
		
				return rTemp;
	}
	**/
}
