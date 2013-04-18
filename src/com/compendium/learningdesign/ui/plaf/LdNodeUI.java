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

package com.compendium.learningdesign.ui.plaf;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.*;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;

import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.Node;

import java.util.concurrent.TimeUnit;

import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.Link;
import com.compendium.core.datamodel.Model;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.ShortCutNodeSummary;
import com.compendium.core.datamodel.View;
import com.compendium.meeting.MeetingEvent;
import com.compendium.meeting.MeetingManager;
import com.compendium.ui.ExecuteControl;
import com.compendium.ui.FormatProperties;
import com.compendium.ui.UIAudio;
import com.compendium.ui.UIImages;
import com.compendium.ui.UILink;
import com.compendium.ui.UINode;
import com.compendium.ui.UIUtilities;
import com.compendium.ui.UIViewFrame;
import com.compendium.ui.UIViewPane;
import com.compendium.ui.dialogs.UITrashViewDialog;
import com.compendium.ui.panels.UIHintNodeLabelPanel;
import com.compendium.ui.plaf.NodeUI;
import com.compendium.ui.plaf.TextRowElement;

import com.compendium.learningdesign.core.datamodel.LdActivityView;
import com.compendium.learningdesign.core.ILdCoreConstants;
import com.compendium.learningdesign.ui.*;
import com.compendium.learningdesign.core.datamodel.*;

/**
 * This class extends NodeUI, the class which draws the node and its labels and
 * indicators. LdNodeUI adds extra indicators for learning design nodes such 
 * as task time.
 *  
 * @author ajb785
 *
 */
public class LdNodeUI extends NodeUI {
	/**	Indicates if the paint methods should paint learner task time **/
	private boolean hasTaskTime	=	false;
	/** Rectangle surrounding area where task time is displayed	***/
	private Rectangle rTimeRectangle = null;
	
	private int iLeftWidestExtra = 0;
	
	private int iRightWidestExtra = 0;

	public void paint(SVGGraphics2D graphics, JComponent c)	{
		graphics.getGeneratorContext().setComment("From LdNodeUI");
		this.paint((Graphics)graphics, c);
		//graphics.getRoot().getLastChild().appendChild(new Node());
	}
  	/**
	 * Paint the node. Copied from superclass and modified to add code to display 
	 * task timing information.
	 *
	 * @param g, the Graphics object to use to do the paint.
	 * @param c, the component being painted.
	 * @see #paintEnabledText
 	 * @see #paintDisabledText
	 * @see #drawText
	 */
  	public void paint(Graphics graphics, JComponent c)	{
  	// Switch on anti-aliasing and quality rendering for the painting of this component
  		Graphics2D g = (Graphics2D)graphics;
  		RenderingHints rh = new RenderingHints(
  		RenderingHints.KEY_ANTIALIASING,
  		RenderingHints.VALUE_ANTIALIAS_ON);
  		g.setRenderingHints(rh);
  		g.getRenderingHints().put( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		// CLEAR VARIABLES
		transRectangle = null;
		textRectangle = null;
		this.setCodeRectangle(null);
		weightRectangle = null;
		this.setLabelRectangle(null);	
		movieRectangle = null;
		textRowElements = new Vector();

		Color oldColor = null;

		UINode node = (UINode)c;

		String text = node.getText();
		Font nodeFont = g.getFont();

		FontMetrics fm = g.getFontMetrics();
		Rectangle iconR = new Rectangle();
		Rectangle textR = new Rectangle();
		Rectangle viewR = new Rectangle(c.getSize());
		Insets viewInsets = c.getInsets();

		int maxWidth = this.getNodeWidth();

		viewR.x = viewInsets.left;
		viewR.y = viewInsets.top;
		viewR.width -= (viewInsets.left + viewInsets.right);
		viewR.height -= (viewInsets.top + viewInsets.bottom);

		// DRAW ICON IF NOT HIDDEN
		int imageHeight = 0;
		int imageWidth = 0;

		ImageIcon icon = node.getIcon();
		NodePosition position = node.getNodePosition();
		boolean bSmallIcon = position.getShowSmallIcon();
		
		if (position.getHideIcon() || icon == null) {
			if (text == null)
				return;

			iconR.width = 0;
			iconR.height = 0;
			iconR.x = maxWidth/2;
			iconR.y = viewR.y+1;

			textR.y = iconR.y + fm.getAscent();
		}
		else {
			if ((icon == null) && (text == null))
				return;

			setHasIcon(true);

			imageHeight = icon.getIconHeight();
			imageWidth = icon.getIconWidth();

			iconR.width = imageWidth+1;
			iconR.height = imageHeight+1;
			
			//iconR.x = this.iLeftWidestExtra;
			iconR.x = (maxWidth - imageWidth)/2;;
			int iOldIconRX = (maxWidth - imageWidth)/2;
			//iconR.x = (maxWidth - this.getExtraIconWidth() -imageWidth)/2;
			iconR.y = viewR.y+1;

			int type = node.getNode().getType();
			if (type == ICoreConstants.ARGUMENT || type == ICoreConstants.ARGUMENT_SHORTCUT) {
				this.setPlusRectangle( new Rectangle(iconR.x, iconR.y, imageWidth/2, imageHeight));
				//g.fillRect(iconR.x, iconR.y+2, imageWidth/2, imageHeight/2);
				this.setMinusRectangle(new Rectangle(iconR.x+imageWidth/2, iconR.y+imageHeight/2, imageWidth/2, imageHeight/2));
				//g.fillRect(iconR.x+imageWidth/2, iconR.y+imageHeight/2, imageWidth/2, imageHeight/2);
			}

			// icon background will always be opaque
			oldColor = g.getColor();

			if (node.isSelected()) {
				g.setColor(SELECTED_COLOR);
				g.drawRect(iconR.x-1, iconR.y-1, iconR.width, iconR.height);
			}

			g.setColor(oldColor);
			icon.paintIcon(c, g, iconR.x, iconR.y);

			//AffineTransform trans = nodeFont.getTransform();
			//Font newFont = (new Font("Dialog", Font.BOLD, 10)).deriveFont(trans);

			// work around for Mac BUG with derive Font
			AffineTransform trans=new AffineTransform();
			trans.setToScale(node.getScale(), node.getScale());

			// FONT FOR THE ICON INDICATORS
			Point p1 = new Point(10, 10);
			try { p1 = (Point)trans.transform(p1, new Point(0, 0));}
			catch(Exception e) {System.out.println("can't convert font size (NodeUI.paint 1) \n\n"+e.getMessage()); }
			Font newFont = new Font("Dialog" , Font.BOLD, p1.x);

			g.setFont(newFont);
			FontRenderContext frc = g.getFontRenderContext();
			FontMetrics sfm = g.getFontMetrics();

			NodeSummary nodeSumm = node.getNode();

			// IF THIS NODE IS IN A MAP ASSOCIATED WITH A VIDEAO
			// DRAW AN V
			if (hasMovie()) {
				g.setColor(Color.red);
				int twidth = new Double((newFont.getStringBounds("M", frc)).getWidth()).intValue(); //$NON-NLS-1$

				int pos = 20;
				int height = 12;
				int extra = 2;
				if (bSmallIcon) {
					extra = 3;
					pos = 12;
					height = 8;
				}

				movieRectangle = new Rectangle(iconR.x+iconR.width, (iconR.y+pos-(height/2)-extra), twidth, height);
				//g.fillRect(iconR.x+iconR.width, (iconR.y+pos-(height/2)-extra), twidth, height);
				g.drawString("M", iconR.x+iconR.width+1, iconR.y+pos); //$NON-NLS-1$
				g.setFont(new Font("Dialog", Font.BOLD, 10)); //$NON-NLS-1$
			}

			// DRAW * IF HAS DETAILS
			String detail = nodeSumm.getDetail();
			detail = detail.trim();
			if(hasText()) {
				g.setColor(new Color(0, 91, 183));

				//Font tFont = (new Font("Dialog", Font.BOLD, 18)).deriveFont(trans);
				// work around for Mac BUG with deriveFont
				Point p2 = new Point(18, 18);
				try { p2 = (Point)trans.transform(p2, new Point(0, 0));}
				catch(Exception e) {System.out.println("can't convert font size (NodeUI.paint 2) \n\n"+e.getMessage());} //$NON-NLS-1$

				Font tFont = new Font("Dialog", Font.BOLD, p2.x); //$NON-NLS-1$
				g.setFont(tFont);
				FontRenderContext frc2 = g.getFontRenderContext();
				int twidth = new Double((newFont.getStringBounds("*", frc2)).getWidth()).intValue(); //$NON-NLS-1$
				//int twidth = rfm.stringWidth("*")+3;

				int pos = 13;
				int height = 16;
				if (bSmallIcon) {
					pos = 11;
					height = 13;
				}

				textRectangle = new Rectangle(iconR.x+iconR.width, (iconR.y-5), twidth, height);
				//g.fillRect(iconR.x+iconR.width, (iconR.y-5), twidth, height);
				g.drawString("*", iconR.x+iconR.width+1, iconR.y+pos); //$NON-NLS-1$

				g.setFont(newFont);
			}

			// DRAW TRANCLUSION NUMBER
			if(hasTrans()) {
				int ncount = nodeSumm.getViewCount();
				if (ncount > 1) {

					g.setColor(new Color(0, 0, 106));
					String count = String.valueOf(ncount);
					//int nwidth = sfm.stringWidth(count)+2;
					int nwidth = new Double((newFont.getStringBounds(count, frc)).getWidth()).intValue();

					int extra = 2;
					int back = 8;
					int theight = 14;
					if (bSmallIcon) {
						theight = 11;
						extra =4;
						back = 5;
					}

					transRectangle = new Rectangle(iconR.x+iconR.width, iconR.y+(iconR.height-back), nwidth, theight);
					//g.fillRect(iconR.x+iconR.width, iconR.y+(iconR.height-back), nwidth, theight);
					g.drawString(count, iconR.x+iconR.width+1, iconR.y+(iconR.height)+extra);
				}
			}

			textR.y = iconR.y + iconR.height + node.getIconTextGap() + fm.getAscent();
			sfm = g.getFontMetrics();

			// DRAW VIEW WEIGHT COUNT IF REQUESTED
			if (hasWeight() && node.getNode() instanceof View) {

				g.setColor(new Color(0, 91, 183));

				View view  = (View)node.getNode();
				String sCount = ""; //$NON-NLS-1$
				try { sCount = String.valueOf(view.getNodeCount()); }
				catch(Exception ex) { System.out.println("Error: (NodeUI.paint)\n\n"+ex.getMessage());} //$NON-NLS-1$

				int w = new Double((newFont.getStringBounds(sCount, frc)).getWidth()).intValue();
				//int w = sfm.stringWidth(sCount);
				int h = sfm.getAscent();

				int extra = 2;
				int back = 8;
				if (oNode.getNodePosition().getShowSmallIcon())  {
					extra = 4;
					back = 6;
				}

				weightRectangle = new Rectangle(iconR.x-(w+2), iconR.y+(iconR.height-back), w, h);
				//g.fillRect(iconR.x-(w+2), iconR.y+(iconR.height-back), w, h);
				g.drawString(sCount, iconR.x-(w+1), iconR.y+(iconR.height)+extra);
			}
			
			//if (node.getNode().getId().equals(ProjectCompendium.APP.getTrashBinID())) {
			//	
			//}
 
			// DRAW 'T', if has Tags
			if (hasCodes()) {
				if (!oNode.getNode().hasLdTagsOnly())	{
					g.setColor(new Color(0, 0, 106));
					int twidth = new Double((newFont.getStringBounds("T", frc)).getWidth()).intValue(); //$NON-NLS-1$
					//int twidth = sfm.stringWidth("T")+2;
					int pos = sfm.getAscent()-3;

					int theight = 14;
					if (bSmallIcon) {
						pos = 6;
						theight = 11;
					}


					Rectangle oTempCodeRectangle = new Rectangle(iconR.x-(twidth+2), (iconR.y-3), twidth, theight);
					this.setCodeRectangle(oTempCodeRectangle);
					//g.fillRect(iconR.x-(twidth+2), (iconR.y-3), twidth, theight);
					g.drawString("T", iconR.x-(twidth+1), iconR.y+pos);
				}
			}
			
/**			if (this.hasTaskTime())	{
				this.showTaskTime(graphics, node);
			}
			**/
		}

		// This made the image rollover extend too far to the right and obscured the icon indicator hot spots.
		//iconR.width = imageWidth;
		this.setIconRectangle(iconR);
		//iconRectangle = iconR;

		// DRAW TEXT
		//int textWidth = fm.stringWidth( text );
		int textWidth = text.length();
		this.setLabelRectangle(viewR);
		
		Rectangle oTempLabelRectangle = viewR;
		oTempLabelRectangle.y = textR.y-fm.getAscent();
		oTempLabelRectangle.height = viewR.height-textR.y+fm.getAscent();
		this.setLabelRectangle(oTempLabelRectangle);
		textR.width = fm.stringWidth( text );
		textR.height = fm.getAscent()+fm.getDescent();
		textR.x = viewR.x;
		//textR.x = iconR.x - (textR.width/2);
		
		int startPos = 0;
		int stopPos = 0;

		// RE_SET THE FONT AND COLOR FOR TEXT
		g.setColor(Color.black);
		g.setFont(nodeFont);

		int wrapWidth = node.getNodePosition().getLabelWrapWidth();
		if (wrapWidth <= 0) {
			wrapWidth = ((Model)ProjectCompendium.APP.getModel()).labelWrapWidth;
		}
		wrapWidth = wrapWidth+1; // Needs this for some reason.		
		
		if (textWidth > wrapWidth) {

			int row = -1;
			String textLeft = text;
			boolean isRowWithCaret = false;

			while ( textLeft.length() > 0 ) {
				row ++;
				isRowWithCaret = false;

				startPos = stopPos;

				//int thisTextWidth = fm.stringWidth( textLeft );
				int textLen = textLeft.length();
				int curLen = wrapWidth;
				if (textLen < wrapWidth ) {
					curLen = textLen;
				}
				
				String nextText = textLeft.substring(0, curLen);
				if (curLen < textLen) {
					int lastSpace = nextText.lastIndexOf(" ");
					if (lastSpace != -1 && lastSpace != textLen) {
						curLen = lastSpace+1;
						nextText = textLeft.substring(0, curLen);
						textLeft = textLeft.substring(curLen);
					}
					else {
						nextText = textLeft.substring(0, curLen);
						textLeft = textLeft.substring(curLen);
					}
				}
				else {
					if (!textLeft.equals(""))
						nextText = textLeft;
					textLeft = "";
				}

				stopPos += nextText.length();

				int mousePosition = -1;

				// for dragging mouse to select
				if (node.hasFocus() && this.isEditing()  && (this.isDragging() || this.isDoubleClicked())) {

					if (getEditY() >= textR.y-fm.getAscent() && getEditY() < (textR.y+fm.getDescent()) ) {

						int tX = textR.x;
						int tY = textR.y;

						int tWidth = fm.stringWidth( nextText );
						if (tWidth < iconR.width && iconR.width > maxWidth) {
							tX += (iconR.width-tWidth)/2;
						}
						else if (tWidth < maxWidth) {
							tX += (maxWidth-tWidth)/2;
						}

						int caretPos = 0;
						if (getEditX() <= tX)
							caretPos = 0;
						else if (getEditX() >= tX+tWidth && startPos+nextText.length() == text.length()) {
							caretPos = nextText.length();
						}
						else if (getEditX() >= tX+tWidth) {
							caretPos = nextText.length()-1;
						}
						else {
							int ind = 1;
							int prev = 0;
							while(ind <= nextText.length()) {
								String n = nextText.substring(0, ind);
								int charX = fm.stringWidth(n);
								if (getEditX() >= (tX+prev) && getEditX() <= (tX+charX) ) {
									if ( (tX+prev) - getEditX() < getEditX() - (tX+charX))
										caretPos = ind-1;
									else
										caretPos = ind;
									break;
								}
								prev = charX;
								ind++;
							}
						}

						if (bDragging) {
							if (currentCaretPosition == -1) {
								currentCaretPosition = startPos+caretPos;
								startSelection = currentCaretPosition;
								stopSelection = currentCaretPosition;
							}
							else {
								// IF DRAGGING LEFT
								if (startPos+caretPos < currentCaretPosition) {

									if (stopSelection == -1)
										stopSelection = currentCaretPosition;

									currentCaretPosition = startPos+caretPos;

									if (startSelection == -1 || startSelection >= currentCaretPosition)
										startSelection = currentCaretPosition;
									else
										stopSelection = currentCaretPosition;
								}
								// IF DRAGGING RIGHT
								else {
									if (startSelection == -1)
										startSelection = currentCaretPosition;

									currentCaretPosition = startPos+caretPos;

									if (stopSelection == -1 || stopSelection < currentCaretPosition)
										stopSelection = currentCaretPosition;
								}
							}
						}

						// DOUBLE CLICK TO SELECT WORD
						if (isDoubleClicked()) {
							int index = nextText.indexOf(" ", caretPos);
							if (index == -1)
								stopSelection = startPos + nextText.length();
							else {
								stopSelection = startPos + index;
							}

							currentCaretPosition = stopSelection;

							String bit = nextText.substring(0, caretPos);
							index = bit.lastIndexOf(" ");
							if (index == -1)
								startSelection = startPos;
							else {
								startSelection = startPos + index+1;
							}
							setDoubleClicked(false);							
						}
					}
				}
				
				drawText(g, fm, node, nextText, textR, iconR, maxWidth, startPos);
			
				// If mouse just clicked
				if (node.hasFocus() && currentCaretPosition == -1 && isEditing()) {
					// IS THE CLICK IN THIS ROW
					if (getEditY() >= textR.y-fm.getAscent() && getEditY() < (textR.y+fm.getDescent()) ) {

						int tX = textR.x;
						int tY = textR.y;

						int tWidth = fm.stringWidth( nextText );
						if (tWidth < iconR.width && iconR.width > maxWidth) {
							tX += (iconR.width-tWidth)/2;
						}
						else if (tWidth < maxWidth) {
							tX += (maxWidth-tWidth)/2;
						}

						int caretPos = 0;
						if (getEditX() <= tX)
							caretPos = 0;
						else if (getEditX() >= tX+tWidth && startPos+nextText.length() == text.length()) {
							caretPos = nextText.length();
						}
						else if (getEditX() >= tX+tWidth) {
							caretPos = nextText.length()-1;
						}
						else {
							int ind = 1;
							int prev = 0;
							while(ind <= nextText.length()) {

								String n = nextText.substring(0, ind);
								int charX = fm.stringWidth(n);
								if (getEditX() >= (tX+prev) && getEditX() <= (tX+charX) ) {
									if ( (tX+prev) - getEditX() < getEditX() - (tX+charX))
										caretPos = ind-1;
									else
										caretPos = ind;
									break;
								}
								prev = charX;
								ind++;
							}
						}

						currentCaretPosition = startPos+caretPos;
						currentRow = row;
						isRowWithCaret = true;
						setCaretRectangle(g, fm, textR, nextText, caretPos, iconR, maxWidth);
					}
				}
				else if (node.hasFocus() && isEditing()) {
					if (caretUp) {
						// IF WE ARE ALREADY ON THE FIRST ROW
						if (currentRow == 0) {
							currentCaretPosition = 0;
							caretUp = false;
						}
					}

					if (caretDown) {
						// IF WE ARE ALREADY ON THE LAST ROW
						if (stopPos == text.length() && currentRow == row) {
							currentCaretPosition = text.length();

							if (FormatProperties.autoSearchLabel) {
								UIHintNodeLabelPanel panel = oViewPane.getLabelPanel(oNode.getNode().getId());
								if (panel != null)
									panel.focusList();
							}

							caretDown = false;
						}
					}

					if (currentCaretPosition >= startPos &&
							(currentCaretPosition < stopPos || currentCaretPosition == stopPos && stopPos == text.length())) {
						int caretPos = currentCaretPosition - startPos;
						setCaretRectangle(g, fm, textR, nextText, caretPos, iconR, maxWidth);
						currentRow = row;
					}
					isRowWithCaret = true;
				}

				TextRowElement element = new TextRowElement(nextText, startPos, new Rectangle(textR.x, textR.y, textR.width, textR.height), isRowWithCaret);
				textRowElements.addElement(element);

				textR.y += fm.getAscent() + fm.getDescent();
			}

			if (caretUp) {
				if (currentRow > 0) {
					caretUp = false;
					recalculateCaretRectangle(g, fm, iconR, maxWidth, true);
				}
			}
			else if (caretDown) {
				if (currentRow < textRowElements.size()-1) {
					recalculateCaretRectangle(g, fm, iconR, maxWidth, false);

					if (FormatProperties.autoSearchLabel) {
						UIHintNodeLabelPanel panel = oViewPane.getLabelPanel(oNode.getNode().getId());
						if (panel != null)
							panel.focusList();
					}

					caretDown = false;
				}
			}
		}
		else {
			// if draggin mouse to select text or double clicked to select word calculate selection
			if (node.hasFocus() && isEditing() && (bDragging || isDoubleClicked())) {
				int tX = textR.x;
				int tY = textR.y;

				int tWidth = fm.stringWidth( text );
				if (tWidth < iconR.width && iconR.width > maxWidth) {
					tX += (iconR.width-tWidth)/2;
				}
				else if (tWidth < maxWidth) {
					tX += (maxWidth-tWidth)/2;
				}

				int caretPos = 0;
				if (getEditX() <= tX)
					caretPos = 0;
				else if (getEditX() >= tX+tWidth)
					caretPos = text.length();
				else {
					int ind = 1;
					int prev = 0;
					while(ind <= text.length()) {
						String n = text.substring(0, ind);
						int charX = fm.stringWidth(n);
						if (getEditX() >= (tX+prev) && getEditX() <= (tX+charX) ) {
							if ( (tX+prev) - getEditX() < getEditX() - (tX+charX))
								caretPos = ind-1;
							else
								caretPos = ind;
							break;
						}
						prev = charX;
						ind++;
					}
				}

				if (bDragging) {
					if (currentCaretPosition == -1) {
						currentCaretPosition = caretPos;
						startSelection = currentCaretPosition;
						stopSelection = currentCaretPosition;
					}
					else {
						// IF DRAGGING LEFT
						if (caretPos < currentCaretPosition) {

							if (stopSelection == -1)
								stopSelection = currentCaretPosition;

							currentCaretPosition = caretPos;

							if (startSelection == -1 || startSelection >= currentCaretPosition)
								startSelection = currentCaretPosition;
							else
								stopSelection = currentCaretPosition;
						}
						// IF DRAGGING RIGHT
						else {
							if (startSelection == -1)
								startSelection = currentCaretPosition;

							currentCaretPosition = caretPos;

							if (stopSelection == -1 || stopSelection < currentCaretPosition)
								stopSelection = currentCaretPosition;
						}
					}
				}

				// DOUBLE CLICK TO SELECT WORD
				if (isDoubleClicked()) {
					int index = text.indexOf(" ", caretPos);
					if (index == -1)
						stopSelection = text.length();
					else {
						stopSelection = index;
					}

					currentCaretPosition = stopSelection;

					String bit = text.substring(0, caretPos);
					index = bit.lastIndexOf(" ");
					if (index == -1)
						startSelection = 0;
					else {
						startSelection = index+1;
					}
					setDoubleClicked(false);
				}
			}			
			
			drawText(g, fm, node, text, textR, iconR, maxWidth, 0);

			// If mouse just clicked
			if (node.hasFocus() && isEditing() && currentCaretPosition == -1) {

				int tX = textR.x;
				int tY = textR.y;

				int tWidth = fm.stringWidth( text );
				if (tWidth < iconR.width && iconR.width > maxWidth) {
					tX += (iconR.width-tWidth)/2;
				}
				else if (tWidth < maxWidth) {
					tX += (maxWidth-tWidth)/2;
				}

				int caretPos = 0;
				if (getEditX() <= tX)
					caretPos = 0;
				else if (getEditX() >= tX+tWidth)
					caretPos = text.length();
				else {
					int ind = 1;
					int prev = 0;
					while(ind <= text.length()) {
						String n = text.substring(0, ind);
						int charX = fm.stringWidth(n);
						if (getEditX() >= (tX+prev) && getEditX() <= (tX+charX) ) {
							if ( (tX+prev) - getEditX() < getEditX() - (tX+charX))
								caretPos = ind-1;
							else
								caretPos = ind;
							break;
						}
						prev = charX;
						ind++;
					}
				}

				currentCaretPosition = caretPos;
				currentRow = 0;
				setCaretRectangle(g, fm, textR, text, currentCaretPosition, iconR, maxWidth);
			}
			else if (node.hasFocus() && isEditing()) {
				// IF UP/DOWN KEY PRESSED ON A SINGLE LINE LABEL GO HOME/END
				if (caretUp) {
					currentCaretPosition = 0;
					caretUp = false;
				}
				if (caretDown) {
					currentCaretPosition = text.length();

					if (FormatProperties.autoSearchLabel) {
						UIHintNodeLabelPanel panel = oViewPane.getLabelPanel(oNode.getNode().getId());
						if (panel != null)
							panel.focusList();
					}

					caretDown = false;
				}
				currentRow = 0;
				setCaretRectangle(g, fm, textR, text, currentCaretPosition, iconR, maxWidth);
			}
		}

		if (isEditing()) {
			// PAINT CARET
			if (getCaretRectangle() != null) {
				Color oldCol = g.getColor();
				g.setColor(Color.red);
   		         g.fillRect(getCaretRectangle().x, getCaretRectangle().y, getCaretRectangle().width, getCaretRectangle().height);
				g.setColor(oldCol);
			}

			// PAINT SUROUNDING BOX
			g.setColor(Color.blue);
			g.drawRect(getLabelRectangle().x, getLabelRectangle().y-1, getLabelRectangle().width, getLabelRectangle().height+1);
		}
		
		//For debugging 
		//g.drawRect(labelRectangle.x, labelRectangle.y-1, labelRectangle.width, labelRectangle.height+1);
		//  Display timing info 
		if (this.hasTaskTime())
  			this.showTaskTime(g, node); 
		
		if (hasText() || hasTrans() || hasWeight() || hasCodes())
			imageWidth += this.getExtraIconWidth() + 1;
			// Added by Andrew - this should not be necessary once have moved rectangle data code from paint(..) to calculateDimensions()
			oNode.updateLinks();
	/**	if (iconR != null)
			g.drawRect(iconR.x, iconR.y, this.getNodeWidth(), iconR.union(textR).height);
			**/
  	}
  	/**
	 * Create a new NodeUI instance.
	 * @param c, the component this is the ui for - NOT REALLY USED AT PRESENT HERE.
	 */
  	public static ComponentUI createUI(JComponent c) {

		LdNodeUI nodeui = new LdNodeUI();
	  	return nodeui;
  	}
	/**
	 * Open this node depending on type.
	 * If a map/list node, open the view.
	 * If the trashbin open the Trashbin dialog.
	 * If a reference node, open any associated reference in an external application.
	 * If any other node, open the UINodeContentDialog for this node.
	 */
	public void openNode() {

		releaseFocusAndRollover();

		int type = oNode.getNode().getType();
		int ldType = oNode.getNode().getLdType();

		if ( (type == ICoreConstants.LDMAPVIEW) ||
			(type == ICoreConstants.MAPVIEW) ||
			(type == ICoreConstants.LISTVIEW) ||
			(type == ICoreConstants.MAP_SHORTCUT) ||
			(type == ICoreConstants.LIST_SHORTCUT))
		{
			ProjectCompendium.APP.getAudioPlayer().playAudio(UIAudio.ABOUT_ACTION);

			View view = null;
			if( type == ICoreConstants.MAP_SHORTCUT || type == ICoreConstants.LIST_SHORTCUT ) {
				view = (View)(((ShortCutNodeSummary)oNode.getNode()).getReferredNode());
			}
			else {
				/**
				if (ldType == ILdCoreConstants.iLD_TYPE_ACTIVITY )
					view = (LdActivityView)oNode.getNode();
				else
				**/
					view = (View)oNode.getNode();
			}

			UIViewFrame frame = ProjectCompendium.APP.addViewToDesktop(view, oNode.getText());
			frame.setNavigationHistory(oNode.getViewPane().getViewFrame().getChildNavigationHistory());
		}
		else if(type  == ICoreConstants.TRASHBIN) {
			UITrashViewDialog dlgTrash = new UITrashViewDialog(ProjectCompendium.APP, this);
			UIUtilities.centerComponent(dlgTrash, ProjectCompendium.APP);
			dlgTrash.setVisible(true);
		}
		else if (type == ICoreConstants.REFERENCE || type == ICoreConstants.REFERENCE_SHORTCUT) {
			String path = oNode.getNode().getSource();

			if (path == null || path.equals("")) {
				openEditDialog(false);
			} else if (path.startsWith(ICoreConstants.sINTERNAL_REFERENCE)) {
				path = path.substring(ICoreConstants.sINTERNAL_REFERENCE.length());
				int ind = path.indexOf("/");
				if (ind != -1) {
					String sGoToViewID = path.substring(0, ind);
					String sGoToNodeID = path.substring(ind+1);		
					UIUtilities.jumpToNode(sGoToViewID, sGoToNodeID, 
							oNode.getViewPane().getViewFrame().getChildNavigationHistory());
				}
			} else if (path.startsWith("http:") || path.startsWith("https:") || path.startsWith("www.") ||
					ProjectCompendium.isLinux && (path.startsWith("fish:") || path.startsWith("ssh:") || path.startsWith("ftp:") || path.startsWith("smb:"))) {				
				if (!ExecuteControl.launch( path )) {
					openEditDialog(false);
				}
				else {
					// IF WE ARE RECORDING A MEETING, RECORD A REFERENCE LAUNCHED EVENT.
					if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents()
							&& (ProjectCompendium.APP.oMeetingManager.getMeetingType() == MeetingManager.RECORDING)) {

						ProjectCompendium.APP.oMeetingManager.addEvent(
							new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
											 ProjectCompendium.APP.oMeetingManager.isReplay(),
											 MeetingEvent.REFERENCE_LAUNCHED_EVENT,
											 oNode.getNodePosition().getView(),
											 oNode.getNode()));
					}
				}
			}
			else {
				File file = new File(path);
				String sPath = path;
				if (file.exists()) {
					sPath = file.getAbsolutePath();
				}
				// It the reference is not a file, just pass the path as is, as it is probably a special type of url.
				if (!ExecuteControl.launch( sPath ))
					openEditDialog(false);
				else {
					// IF WE ARE RECORDING A MEETING, RECORD A REFERENCE LAUNCHED EVENT.
					if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents()
							&& (ProjectCompendium.APP.oMeetingManager.getMeetingType() == MeetingManager.RECORDING)) {

						ProjectCompendium.APP.oMeetingManager.addEvent(
							new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
											 ProjectCompendium.APP.oMeetingManager.isReplay(),
											 MeetingEvent.REFERENCE_LAUNCHED_EVENT,
											 oNode.getNodePosition().getView(),
											 oNode.getNode()));
					}
				}
			}
		}
		else {
			openEditDialog(false);
		}
	}
	
	/**
	 * Calculate the requred dimensions of the given node.
	 * Checks for icon and various node indicator extras when calculating.
	 * 
	 * @param node com.compendium.ui.UINode, the node to calculate the dimensions for.
	 * @return Dimension, the dimension for the given node.
	 */
	protected Dimension calculateDimension(UINode node) {

		this.setHasTrans(false);
		this.setHasText(false);
		this.setHasCodes(false);
		this.setHasWeight(false);
		this.setHasMovie(false);
		this.setHasTaskTime(false);

		boolean bSmallIcon = node.getNodePosition().getShowSmallIcon();
		String text = node.getText();
		String id = node.getNode().getId();

		Insets insets = node.getInsets();
		//dx is the total of the left and right insets
		int dx = insets.left + insets.right;
		//dy is the total of the top and bottom insets
		int dy = insets.top + insets.bottom;

		Font font = node.getFont();
		FontMetrics fm = node.getFontMetrics(font);

		Rectangle iconR = new Rectangle();
		Rectangle textR = new Rectangle();
		Rectangle viewR = new Rectangle(node.getSize());
// Set the x co-ord of the position of viewR to be 
		viewR.x = insets.left;
		viewR.y = insets.top;

		NodePosition pos = node.getNodePosition();		
		
		Icon icon = node.getIcon();
		
		if (pos.getHideIcon() || icon == null) {
			// The node does not have an icon or the icon is not being displayed
			iconR.width=0;
			iconR.height=0;
			iconR.y = 1;
			textR.y = iconR.y + iconR.height + fm.getAscent();
		}
		else {
			// Set the dimensions of the iconR icon rectangle to be one bigger than the icon in both x and y directions
			iconR.width = icon.getIconWidth()+1;
			iconR.height = icon.getIconHeight()+1;
			iconR.y = viewR.y+1;
			textR.y = iconR.y + iconR.height + node.getIconTextGap() + fm.getAscent();

			// FOR EXTRA BIT ON SIDE
			AffineTransform trans=new AffineTransform();
			trans.setToScale(node.getScale(), node.getScale());
			Point p1 = new Point(10, 10);
			try { p1 = (Point)trans.transform(p1, new Point(0, 0));}
			catch(Exception e) {System.out.println("can't convert font size (LdNodeUI.calculateDimension)\n\n"+e.getMessage()); } //$NON-NLS-1$
			Font newFont = new Font("Dialog" , Font.BOLD, p1.x); 
			NodeSummary nodeSumm = node.getNode();
			FontRenderContext frc = UIUtilities.getDefaultFontRenderContext();
			String detail = nodeSumm.getDetail();
			detail = detail.trim();
			int type = node.getType();
			// Tag, Weight and time indicators are displayed on the LHS
			// Detail and Transclusion indicators are displayed on the RHS
			// Widest extra distances needed on the left and right hand sides of the node
			float fLeftWidestExtra = 0; float fRightWidestExtra = 0;
			float widestExtra = 0; float fTimeWidth = 0;
			Rectangle2D bounds = null;	float width = 0;

			// SET Flag and calculate EXTRA WIDTH FOR BITS ON SIDE IF REQUIRED
			if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents() &&
					ProjectCompendium.APP.oMeetingManager.getMeetingType() == MeetingManager.REPLAY ) {
				this.setHasMovie(true);
			
			bounds = newFont.getStringBounds("M", frc); //$NON-NLS-1$
			width = (float) bounds.getWidth(); 
			if (width > widestExtra) {
				widestExtra = width;
			}
			}
			if (pos.getShowTrans()
					&&  (nodeSumm.isInMultipleViews()) && (nodeSumm.getViewCount() > 1)) {
				this.setHasTrans( true);
			
			//bounds = newFont.getStringBounds(String.valueOf(nodeSumm.getViewCount()), frc);
			//width = (float) bounds.getWidth(); 
			// Rectangle rTemp = this.calculateTransIndicatorRectangle(iconR, nodeSumm, fm, bSmallIcon);
			Rectangle rTemp = this.calculateTransIndicatorRectangle(iconR, nodeSumm, newFont, frc, bSmallIcon);
			this.setTransRectangle(rTemp);
			if (rTemp.width > fRightWidestExtra ) {
				fRightWidestExtra  = rTemp.width;
			}
			}

			if (pos.getShowText()
					&& (type != ICoreConstants.TRASHBIN 
							&& !detail.equals("")  //$NON-NLS-1$
							&& !detail.equals(ICoreConstants.NODETAIL_STRING) 
							&& !id.equals(ProjectCompendium.APP.getInBoxID()))) {
				this.setHasText(true);
			
			Point p2 = new Point(18, 18);
			try { p2 = (Point)trans.transform(p2, new Point(0, 0));}
			catch(Exception e) {}
			Font tFont = new Font("Dialog", Font.BOLD, p2.x); //$NON-NLS-1$
			bounds = tFont.getStringBounds("*", frc); //$NON-NLS-1$
			width = (float) bounds.getWidth(); 
			Rectangle rTemp = this.calculateDetailsIndicatorRectangle(iconR,  tFont, frc, bSmallIcon);
			this.setTextRectangle(rTemp);
			if (rTemp.width > fRightWidestExtra ) {
				fRightWidestExtra  = rTemp.width;
			}
			}
	/**		
	  	if  (View.isViewType(type) ) {
					View view  = (View)node.getNode();
					try { 
						bounds = newFont.getStringBounds(String.valueOf(view.getNodeCount()), frc);
						width = (float) bounds.getWidth(); 
						if (width > fLeftWidestExtra ) {
							fLeftWidestExtra  = width;
						}
					} catch(Exception e){}
					if (pos.getShowWeight())	{
						this.setHasWeight( true);
					}
				}
	**/		
			if  (pos.getShowWeight()
					&& View.isViewType(type)) {
				this.setHasWeight( true);
				View view  = (View)node.getNode();
				try { 
					 bounds = newFont.getStringBounds(String.valueOf(view.getNodeCount()), frc);
					 width = (float) bounds.getWidth(); 
					if (width > fLeftWidestExtra) {
						fLeftWidestExtra = width;
					}
				} catch(Exception e){}
				Rectangle rTemp = this.calculateWeightIndicatorRectangle(iconR, newFont, frc, bSmallIcon, nodeSumm);
				this.setWeightRectangle(rTemp);
			}
			try {
				if (pos.getShowTags() && nodeSumm.getCodeCount() > 0) {
					if (!node.getNode().hasLdTagsOnly())	{
						this.setHasCodes(true);
						bounds = newFont.getStringBounds("T", frc); //$NON-NLS-1$
						width = (float) bounds.getWidth(); 
						Rectangle rTemp = this.calculateTagIndicatorRectangle(iconR, newFont, frc, bSmallIcon);
						this.setCodeRectangle(rTemp);
						if (rTemp.width > fLeftWidestExtra ) {
							fLeftWidestExtra  = rTemp.width;
						}
					}
				}
			}
				catch(Exception ex) {
					System.out.println("Error: (NodeUI.calculateDimension) \n\n"+ex.getMessage()); //$NON-NLS-1$
				}

				/** This is a mess - need to reimplement interfaces or inheritance of 
				 *  UILdActivityNode and UILdTaskNode, or implement LdTaskNodeUI, LdActivityNodeUI.
				 */
				int iLdType = nodeSumm.getLdType();
				if (iLdType == ILdCoreConstants.iLD_TYPE_ACTIVITY)	{
					if (((UILdActivityNode)this.getUINode()).getShowTime())	{
						this.setHasTaskTime(true);
					}
				}
				else if (((UILdNode)node).getLdType() == ILdCoreConstants.iLD_TYPE_TASK)	{
					boolean bST = false;
					//		 if (((UILdTaskNode)this.getUINode()).getShowTime())	{
					if (pos.getView().getLdType() == ILdCoreConstants.iLD_TYPE_ACTIVITY)	{
						bST = ((LdActivityView)pos.getView()).getLdActivityTimes().getShowTime();
						this.setHasTaskTime(bST);
					}
				}
				if (((UILdNode)node).getLdType() == ILdCoreConstants.iLD_TYPE_TASK )	{
					//iExtraIconWidth = sfm.charWidth(ch)
					String sTime = ((UILdTaskNode)this.getUINode()).getTaskTimeString();
					// Do not know why the value below is multiplied by 2.5 (works), just copying way iW is set

					// iconR = this.getIconRectangle();
					int extra = 2;
					int back = 8;
					int yOffset = iconR.height/2;
					if (oNode.getNodePosition().getShowSmallIcon())  {
						extra = 4;
						back = 6;
					}

					bounds = newFont.getStringBounds(sTime, frc); //$NON-NLS-1$
					fTimeWidth = (float) bounds.getWidth(); 
					int h = new Float(bounds.getHeight()).intValue();
					int w = new Float(fTimeWidth).intValue();
					//		int iMaxAdv = sfm.getMaxAdvance();
					int iMaxAdv = new Float(newFont.getMaxCharBounds(frc).getWidth()).intValue();
					int iSpacing = Math.min(3, iMaxAdv) ;
					int iFudgeFactor = 0;
					fTimeWidth += iSpacing;
					fTimeWidth += iFudgeFactor;
					if (fTimeWidth >fLeftWidestExtra )
						fLeftWidestExtra  = fTimeWidth;
					rTimeRectangle = new Rectangle(iconR.x-(w + iSpacing + iFudgeFactor),  iconR.y+yOffset + extra, w, h+extra);

				}
				widestExtra = fLeftWidestExtra + fRightWidestExtra ;
				iconR.width += new Float(widestExtra).intValue()+4; 
				this.iLeftWidestExtra = new Float(fLeftWidestExtra).intValue();
				this.iRightWidestExtra = new Float(fRightWidestExtra).intValue();
				/**		if (hasMovie() || hasTrans() || hasText() || hasWeight() || hasCodes() || hasTaskTime() ) {
				iconR.width += new Float(widestExtra).intValue()+4; 
				}
				 **/
		}
		
		int wrapWidth = pos.getLabelWrapWidth();
		if (wrapWidth <= 0) {
			wrapWidth = ((Model)ProjectCompendium.APP.getModel()).labelWrapWidth;
		}
		wrapWidth = wrapWidth+1; // Needs this for some reason.
		int textWidth = text.length();		
		
		textR.width = textWidth;
		int widestLine = 0;

		textR.height = 0;
		textR.x = dx;
		textR.y = iconR.y + iconR.height + node.getIconTextGap();
		Rectangle rlastTextRow = new Rectangle();
		if (textWidth > wrapWidth) {
			int iHeight = 0;
			rlastTextRow = this.calculateTextRowRectangles( fm, node, text,  viewR, iconR);
			TextRowElement oCurrentElement = null;
			for (int i=0; i<textRowElements.size(); ++i)	{
				oCurrentElement = textRowElements.elementAt(i);
				iHeight += oCurrentElement.getTextRect().height;
				int thisWidth = fm.stringWidth( oCurrentElement.getText() );
				if ( thisWidth > widestLine) {
					widestLine = thisWidth;
				}
			}
			textR.height = iHeight;			
		}
		else {
			widestLine = fm.stringWidth( text );
			textR.height += (fm.getDescent()+ fm.getAscent());
			textR.y = iconR.y + iconR.height + node.getIconTextGap();
		}

		textR.width = widestLine;
		if (iconR.width > textR.width) {
			textR.width = iconR.width;
		}
		int iTextXDisp = widestLine/2;
		textR.x = iconR.x - iTextXDisp;
		Dimension rv = iconR.union(textR).getSize();
		rv.width += dx+1;
		int iNodeWidth = rv.width;
		this.setNodeWidth(iNodeWidth);

		rv.height += dy;
		
		int maxWidth = this.getNodeWidth();
		// Calculate x postion of the node icon
		if (pos.getHideIcon() || icon == null) {
			iconR.x = maxWidth/2;
		}
		else {
			iconR.x = (maxWidth - iconR.width)/2;
		}

		//super.calculateLabelRectanglePositions(node);
		// Added by Andrew
		this.setLabelRectangle(textR);
		this.setIconRectangle(iconR);
		
		return rv;
	}	
	
	/**
	 * This draws the value(s) for the task time on the node.
	 */
	public void showTaskTimeOLd(Graphics graphics)	{
		Graphics2D g = (Graphics2D)graphics;
		//System.out.println("LdNodeUI - isEventDispatchThread: " + SwingUtilities.isEventDispatchThread());
		int ldType = this.oNode.getNode().getLdType();
		switch (ldType)	{
		case (ILdCoreConstants.iLD_TYPE_ACTIVITY):	{
			
		}
		break;
		case (ILdCoreConstants.iLD_TYPE_TASK):	{
			//Recalculate for scaling
			AffineTransform trans=new AffineTransform();
			trans.setToScale(oNode.getScale(), oNode.getScale());

			Point p1 = new Point(10, 10);
			try { p1 = (Point)trans.transform(p1, new Point(0, 0));}
			catch(Exception e) {System.out.println("can't convert font size (UINode.calculateDimension)\n\n"+e.getMessage()); }


			Font newFont = new Font("Dialog" , Font.BOLD, p1.x);
			g.setFont(newFont);
			FontRenderContext frc = g.getFontRenderContext();
			FontMetrics sfm = g.getFontMetrics();
			g.setColor(new Color(0, 128, 128));
			// Get the task time - this is always stored in minutes on a UILdTaskNode
			String sTime = ((UILdTaskNode)this.getUINode()).getTaskTimeString();
			int iMaxAdv = sfm.getMaxAdvance();
			int iSpacing = Math.min(3, iMaxAdv) ;
			int oldw = sfm.stringWidth(sTime);
			int w = new Double((newFont.getStringBounds(sTime, frc)).getWidth()).intValue(); 
			Rectangle2D rTime = sfm.getStringBounds(sTime, g);
			int h = sfm.getAscent();
			Rectangle iconR = this.getIconRectangle();
			int extra = 2;
			int back = 8;
			int yOffset = iconR.height/2;
			if (oNode.getNodePosition().getShowSmallIcon())  {
				extra = 4;
				back = 6;
			}
			Rectangle viewR = new Rectangle(oNode.getSize());
			//weightRectangle = new Rectangle(iconR.x-(w+2), iconR.y+(iconR.height-back), w, h);
			//g.fillRect(iconR.x-(w+2), iconR.y+(iconR.height-back), w, h);
			g.drawString(sTime, iconR.x-(w + iSpacing), iconR.y+yOffset+extra);
			//g.drawString(sTime, viewR.x, iconR.y+yOffset+extra);
		//	rTimeRectangle = new Rectangle(iconR.x-(w + iSpacing+1),  iconR.y+yOffset-h, w, h+extra);
			rTimeRectangle = new Rectangle(iconR.x-(w + iSpacing),  iconR.y+yOffset-h, w, h+extra);
			//g.drawRect(iconR.x-(w + iSpacing),  iconR.y+yOffset-h, w, h+1);
		}
		break;
		default:	{
			
		}
		}
	}
	
	
	/**
	 * This draws the value(s) for the task time on the node.
	 */
	public void showTaskTime(Graphics graphics, UINode node)	{
		Graphics2D g = (Graphics2D)graphics;
		Rectangle iconR = this.getIconRectangle();
		//Recalculate for scaling
		AffineTransform trans=new AffineTransform();
		
		trans.setToScale(node.getScale(), node.getScale());
		Point p1 = new Point(10, 10);
		try { p1 = (Point)trans.transform(p1, new Point(0, 0));}
		catch(Exception e) {System.out.println("can't convert font size (UINode.calculateDimension)\n\n"+e.getMessage()); }
		Font newFont = new Font("Dialog" , Font.BOLD, p1.x);
		g.setFont(newFont);
		FontRenderContext frc = g.getFontRenderContext();
		FontMetrics sfm = g.getFontMetrics();
		g.setColor(new Color(0, 128, 128));
		
		//System.out.println("LdNodeUI - isEventDispatchThread: " + SwingUtilities.isEventDispatchThread());
		int ldType = this.oNode.getNode().getLdType();
		switch (ldType)	{
		case (ILdCoreConstants.iLD_TYPE_ACTIVITY):	{
		//Do nothing currently	
		}
		break;
		case (ILdCoreConstants.iLD_TYPE_TASK):	{
			
			// Get the task time - this is always stored in minutes on a UILdTaskNode
			String sTime = ((UILdTaskNode)this.getUINode()).getTaskTimeString();
			// Draw the time string - note that it is drawn with the character baseline at the x, y values specified
			g.drawString(sTime, iconR.x + rTimeRectangle.x, iconR.y + rTimeRectangle.y);
			//g.drawString(sTime, iconR.x-(w + iSpacing), iconR.y+yOffset+extra);
		}
		break;
		default:	{
			
		}
		}
	}
	
	/**
     * Creates a link. This method adds timing data if the nodes being connected are Ld
     * task nodes, or a link between a Ld role and task node. 
	 * @param uifrom com.compendium.ui.UINode, the originating node for the link to create.
	 * @param uito com.compendium.ui.UINode, the destination node for the link to create.
	 * @param type, the type of link to create.
	 * @param sLabel, the labe for this node.
	 * @param arrow, the type of arrow heads to draw.
	 * @return com.compendium.ui.UILink, the newly created link.
	 * @see com.compendium.core.ICoreConstants
     */
	public UILink createLink(UINode uifrom, UINode uito, String type, String sLabel, int arrow) {

		oViewPane = oNode.getViewPane();
		NodeSummary from = uifrom.getNode();
		NodeSummary to	= uito.getNode();

		if (oViewPane == null || from == null || to == null)
			return null;

		View view = oViewPane.getView();
		if (view == null)
			return null;

		int permission = ICoreConstants.WRITE;

		String sOriginalID = "";

		Link link = null;
		try {
			//add the link to the datamodel view
			link = (Link)view.addMemberLink(type,
					"", //ImportedID
					sOriginalID,
					ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
					from,
					to,
					sLabel,
					arrow);
		}
		catch(Exception ex){
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Error: (NodeUI.createLink) Unable to create link\n\n"+ex.getMessage());
			return null;
		}

		link.initialize(view.getModel().getSession(), view.getModel());

		//create a link in UI layer - NOW DONE BY PROPERTY CHANGE EVENT
		UILink uilink = (UILink)oViewPane.get(link.getId());
		if (uilink == null) {
			if ((to.getLdType() == ILdCoreConstants.iLD_TYPE_TASK) &&
					(from.getLdType() == ILdCoreConstants.iLD_TYPE_TASK) )	{
				uilink = new UILdTaskLink(link, uifrom, uito);
			}
			else	{
				uilink = new UILink(link, uifrom, uito);
			}

			double currentScale = oViewPane.getZoom();
			AffineTransform trans=new AffineTransform();
			trans.setToScale(currentScale, currentScale);
			uilink.scaleArrow(trans);

			uito.getViewPane().add(uilink, (UIViewPane.LINK_LAYER));
			uilink.setBounds(uilink.getPreferredBounds());
			uifrom.addLink(uilink);
			uito.addLink(uilink);
		}

		/** Add task timing data	- this will only be done when the LdNodeUI is in a UILdViewPanes	**/
		addTaskSequenceData( uifrom,  uito,  uilink);	

		return uilink;
	}
  	
  	/**
  	 * Add task sequence  data related to the uilink and the uinodes 
  	 * @param uifrom
  	 * @param uito
  	 * @param uilink
  	 */
  	private void addTaskSequenceData(UINode uifrom, UINode uito, UILink uilink)	{
  		// If the link is NOT being added to a UILdViewPane, just return because other ViewPanes can not store timing data
  		if (!oViewPane.getClass().getName().equals("com.compendium.learningdesign.ui.UILdViewPane"))	{
  			return;
  		}
  		/** If the link is being added in a UILDViewPane which displays task times
		 * and it's a link between a role and a task node, or a link between two task nodes.
		 */
		// Get the set of task sequences for this instance of UILdViewPane
		UILdViewPane oUILdViewPane = ((UILdViewPane)oViewPane);
		UILdTaskSequence aLdTaskSequence, aLdTaskSequenceContainsFromNode, aLdTaskSequenceContainsToNode;
		if (UILdTaskSequence.isSuitableLink(uilink) )	{
			// Get the first task sequence containing the from node, or an empty task sequence
			aLdTaskSequenceContainsFromNode = oUILdViewPane.getTaskSequenceContaining((UILdNode)uifrom); // getTaskSequenceContainingANodeInLink(uifrom, uito);
			// Get the first task sequence containing the to node, or an empty task sequence
			aLdTaskSequenceContainsToNode = oUILdViewPane.getTaskSequenceContaining((UILdNode)uito);
			// bContainsFrom is true if a task sequence that contains the from node exists in the view pane   
			boolean bContainsFrom = !aLdTaskSequenceContainsFromNode.isEmpty();
			// bContainsTo is true if a task sequence that contains the to node exists in the view pane
			boolean bContainsTo = !aLdTaskSequenceContainsToNode.isEmpty();
			if (!bContainsTo && !bContainsFrom)	{
				// No task sequences containing the linked nodes so create a new one
				aLdTaskSequence = new UILdTaskSequence(uilink);
				// It's now added to a OUILdViewPane via a propertyChange method 
//				oUILdViewPane.addToTaskSequenceSet(aLdTaskSequence);
			}
			else	{		
				/*
				 * A task sequence already exists which contains at least one of the
				 * nodes, so add this link to it or create a new task
				 * sequence if a fork has been created.
				 */
				if (UILdTaskSequence.isTasktoTaskLink(uilink)  )	{
				// If it's a task to task link
					if (!bContainsTo && bContainsFrom)	{
					/*	 If the task sequence containing the from node has it as its last node, and this link to the end 
						other wise create a new 'sub sequence'	**/
						//aLdTaskSequenceContainsFromNode.add(uilink);
						if (aLdTaskSequenceContainsFromNode.isLastTaskNode((UILdNode)uifrom))	{
							aLdTaskSequenceContainsFromNode.add(uilink);
						}
						else	{
							// Create a new Task sequence 
							aLdTaskSequence = new UILdTaskSequence(uilink);
						}
					}
					else if (bContainsTo && !bContainsFrom)	{
						// If the task sequence containing the to node has it as its first node and does not have a role node
						if (aLdTaskSequenceContainsToNode.isFirstTaskNode((UILdNode)uito) && 
								!aLdTaskSequenceContainsToNode.containsRoleNode())	{
							aLdTaskSequenceContainsToNode.addFirst(uilink);
						}
						else	{
							// Create a new Task sequence 
							aLdTaskSequence = new UILdTaskSequence(uilink);
						}
					}
					else if (bContainsTo && bContainsFrom)	{
						/** Need to join the two sequences: add the sequence containing the 'to' node to that containing the 'from' node
						 * However, if the user is trying to join sequences in a way that CompendiumLD may not be able to make sense of,
						 * warn the user.
						 */
						String sCLdAppVersion = ICoreConstants.sAPPNAME + " version " + ICoreConstants.sLdAPPVERSION;
						String sJoinMessage = "";
						String sDisplayTimingInfo = "";
						if (!this.getUINode().getViewPane().getShowTimingInfo())
							sDisplayTimingInfo = "Note: to display timing information, right-click on the activity and select 'Show task timess'.";
						if (!aLdTaskSequenceContainsFromNode.isLastTaskNode((UILdNode)uifrom))	{
							// If the from node is not the last task node in its sequence warn the user
							sJoinMessage = "You can leave the link in place, but task timing calculations may not work.\n ";
							ProjectCompendium.APP.displayMessage("Warning: if you join two sequences together in this way " + sCLdAppVersion + 
									 " may not be able to do anything sensible with the timing information in this activity map.\n" + sJoinMessage +
									 sDisplayTimingInfo, "Warning!!!");
						}
						else if (!aLdTaskSequenceContainsToNode.isFirstTaskNode((UILdNode)uito))	{
							// If the to node is not the first task node in its sequence warn the user
							sJoinMessage = "You can leave the link in place, but  task timing calculations may not work.\n";
							ProjectCompendium.APP.displayMessage("Warning: if you join two sequences together in this way " + sCLdAppVersion + 
									 " may not be able to do anything sensible with the timing information in this activity map:\n" + sJoinMessage +
									 sDisplayTimingInfo, "Warning!!!");
						}
						else {
							aLdTaskSequenceContainsFromNode.addUILdTaskSequence(aLdTaskSequenceContainsToNode);
							// Remove the sequence containing the 'to' node from the UILdViewPanes TaskSequenceSet.
							LinkedHashSet<UILdTaskSequence> oSequencesToRemove = new LinkedHashSet<UILdTaskSequence>();
							oSequencesToRemove.add(aLdTaskSequenceContainsToNode);
							oUILdViewPane.removeTaskSequences(oSequencesToRemove);
						}
					}
				}
				else	{
					// The link is  a role to task link so add it to  the beginning of the LdTasksequence 
					aLdTaskSequenceContainsToNode.addFirst(uilink);
				}
				
			}
		}
  	}
  	
  	/**
     * Creates a link, and includes sImportedID parameter to account for id 
     * creation  from imported XML data. This method adds timing data if the 
     * nodes being connected are Ld task nodes, or a link between a Ld role 
     * and task node. 
	 * @param uifrom com.compendium.ui.UINode, the originating node for the link to create.
	 * @param uito com.compendium.ui.UINode, the destination node for the link to create.
	 * @param type, the type of link to create.
	 * @param sLabel, the labe for this node.
	 * @param arrow, the type of arrow heads to draw.
	 * @return com.compendium.ui.UILink, the newly created link.
	 * @see com.compendium.core.ICoreConstants
     */
  	public UILink createLink(String sImportedID,UINode uifrom, UINode uito, String type, String sLabel, int arrow) {

		oViewPane = oNode.getViewPane();
		NodeSummary from = uifrom.getNode();
		NodeSummary to	= uito.getNode();

		if (oViewPane == null || from == null || to == null)
			return null;

		View view = oViewPane.getView();
		if (view == null)
			return null;

		int permission = ICoreConstants.WRITE;

		String sOriginalID = "";

		Link link = null;
		try {
			//add the link to the datamodel view
			link = (Link)view.addMemberLink(type,
											sImportedID,
											sOriginalID,
											ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
											from,
											to,
											sLabel,
											arrow);
		}
		catch(Exception ex){
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Error: (NodeUI.createLink) Unable to create link\n\n"+ex.getMessage());
			return null;
		}

		link.initialize(view.getModel().getSession(), view.getModel());

		//create a link in UI layer - NOW DONE BY PROPERTY CHANGE EVENT
		UILink uilink = (UILink)oViewPane.get(link.getId());
		if (uilink == null) {
			if ((to.getLdType() == ILdCoreConstants.iLD_TYPE_TASK) &&
				(from.getLdType() == ILdCoreConstants.iLD_TYPE_TASK) )	{
				uilink = new UILdTaskLink(link, uifrom, uito);
			}
			else	{
				uilink = new UILink(link, uifrom, uito);
			}

			double currentScale = oViewPane.getZoom();
			AffineTransform trans=new AffineTransform();
			trans.setToScale(currentScale, currentScale);
			uilink.scaleArrow(trans);

			uito.getViewPane().add(uilink, (UIViewPane.LINK_LAYER));
			uilink.setBounds(uilink.getPreferredBounds());
			uifrom.addLink(uilink);
			uito.addLink(uilink);
		}
		/** Add the task sequence data if the cuurent node is in a UILdViewPane, otherwise do nothing 	 */		
		addTaskSequenceData( uifrom,  uito,  uilink);
		return uilink;
  	}
  
  	
  	/**
     * Creates a link.
	 * @param sLinkID, the id to give this link.
	 * @param uifrom com.compendium.ui.UINode, the originating node for the link to create.
	 * @param uito com.compendium.ui.UINode, the destination node for the link to create.
	 * @param type, the type of link to create.
	 * @param sLabel, the labe for this node.
	 * @param arrow, the type of arrow heads to draw.
	 * @return com.compendium.ui.UILink, the newly created link.
	 * @see com.compendium.core.ICoreConstants
     */
  	public UILink createLinkNodeUIVersion(String sImportedID, UINode uifrom, UINode uito, String type, String sLabel, int arrow) {

 		oViewPane = oNode.getViewPane();
		NodeSummary from = uifrom.getNode();
		NodeSummary to	= uito.getNode();

		if (oViewPane == null || from == null || to == null)
			return null;

		View view = oViewPane.getView();
		if (view == null)
			return null;

		int permission = ICoreConstants.WRITE;

		String sOriginalID = "";

		Link link = null;
		try {
			//add the link to the datamodel view
			link = (Link)view.addMemberLink(type,
											sImportedID,
											sOriginalID,
											ProjectCompendium.APP.getModel().getUserProfile().getUserName(),
											from,
											to,
											sLabel,
											arrow);
		}
		catch(Exception ex){
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Error: (NodeUI.createLink) Unable to create link\n\n"+ex.getMessage());
			return null;
		}

		link.initialize(view.getModel().getSession(), view.getModel());

		//create a link in UI layer - NOW DONE BY PROPERTY CHANGE EVENT
		UILink uilink = (UILink)oViewPane.get(link.getId());
		if (uilink == null) {
			uilink = new UILink(link, uifrom, uito);

			double currentScale = oViewPane.getZoom();
			AffineTransform trans=new AffineTransform();
			trans.setToScale(currentScale, currentScale);
			uilink.scaleArrow(trans);

			uito.getViewPane().add(uilink, (UIViewPane.LINK_LAYER));
			uilink.setBounds(uilink.getPreferredBounds());
			uifrom.addLink(uilink);
			uito.addLink(uilink);
		}
		return uilink;
  	}


	/**
	 * Return true if this node has  task time set, false otherwise
	 * @return the hasTaskTime
	 */
	public boolean hasTaskTime() {
		return hasTaskTime;
	}

	
	/**
	 * Set the boolean value to indicate if this node has a task time value 
	 * set.
	 * @param hasTaskTime the hasTaskTime to set
	 */
	public void setHasTaskTime(boolean hasTaskTime) {
		this.hasTaskTime = hasTaskTime;
	}

	/**
	 * Invoked when a mouse is moved in a component.
	 * @param evt, the associated MouseEvent.
	 */
	public void mouseMoved(MouseEvent evt) {

		//System.out.println("In mouse moved");
		Point p = new Point(0, 0);

		if (oNode != null)
			p = SwingUtilities.convertPoint((Component)evt.getSource(), evt.getX(), evt.getY(), oNode);
		else
			p = SwingUtilities.convertPoint((Component)evt.getSource(), evt.getX(), evt.getY(), null);

		updateMousePosition(p);

		int nX = evt.getX();
		int nY = evt.getY();

		oViewPane = oNode.getViewPane();
		if (oViewPane != null) {

			Point p2 = SwingUtilities.convertPoint((Component)evt.getSource(), evt.getX(), evt.getY(), oViewPane);

			if (FormatProperties.imageRollover &&
							hasIcon() && getIconRectangle() != null && getIconRectangle().contains(nX, nY) ) {

				NodeSummary node = oNode.getNode();

				boolean showImage = false;
				int nodeType = node.getType();

				if (nodeType == ICoreConstants.REFERENCE || nodeType == ICoreConstants.REFERENCE_SHORTCUT) {

					String img = node.getImage();
					if (img == null || img.equals("")) {
						String ref = node.getSource();
						if ( UIImages.isImage(ref) && oNode.hasImageBeenScaled())
							showImage = true;
					}
					else {
						if (oNode.hasImageBeenScaled()) {
							showImage = true;
						}
					}
				}
				else if (nodeType == ICoreConstants.LDMAPVIEW || nodeType == ICoreConstants.MAPVIEW || nodeType == ICoreConstants.MAP_SHORTCUT ||
						nodeType == ICoreConstants.LISTVIEW || nodeType == ICoreConstants.LIST_SHORTCUT) {

					String img = node.getImage();
					if (img != null && !img.equals("") && oNode.hasImageBeenScaled()) {
						showImage = true;
					}
				}

				if (showImage) {
					oViewPane.hideCodes();
					oViewPane.hideViews();
					oViewPane.hideDetail();

					oViewPane.showImage(oNode.getNode(), p2.x, p2.y);
				}
			}
			else {
				oViewPane.hideDetail();
			}

			if (hasText() && getTextRectangle() != null && getTextRectangle().contains(nX, nY) ) {
				//oNode.setToolTipText("Click to open node details");
				oViewPane.hideCodes();
				oViewPane.hideViews();
				oViewPane.hideImages();
				oViewPane.showDetail(oNode.getNode(), p2.x, p2.y);
			}
			else {
				oViewPane.hideDetail();
			}

			if (hasTrans() && getTransRectangle() != null && getTransRectangle().contains(nX, nY) ) {
				//oNode.setToolTipText("Number of transclusion. Click to open Views list");
				oViewPane.hideCodes();
				oViewPane.hideDetail();
				oViewPane.hideImages();
				oViewPane.hideViews();
				oViewPane.showViews(oNode.getNode(), p2.x, p2.y);
			}
			else {
				//oViewPane.hideViews();
			}

			if (hasCodes() && getCodeRectangle() != null && getCodeRectangle().contains(nX, nY) ) {
				//oNode.setToolTipText("Click to open codes list");
				oViewPane.hideViews();
				oViewPane.hideDetail();
				oViewPane.hideImages();
				oViewPane.showCodes(oNode.getNode(), p2.x, p2.y);
			}
			else {
				oViewPane.hideCodes();
			}
		}
	}
	
	/**
	 * Handles a property change to the UILdNode.
	 * @param evt, the associated PropertyChagenEvent object.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		String prop = evt.getPropertyName();

		if (prop.equals(UILdTaskNode.TIME_INDICATOR_PROPERTY) ) {
			refreshBounds();
		}
	}

	/**
		 * IN DEVELOPMENT - RENAME TO addCharToLabel(String key) when finished
		 * Add the passed string to the node label at the point of the current caret position.
		 * If the user set label length has been reached, and they have request it,
		 * open the NodeContentDialog and place the text in the details box.
		 *
		 * @param key, the string to add to the label.
		 */
		protected void devAddCharToLabel(String key) {
	
			if (oNode.getNode().getType() != ICoreConstants.TRASHBIN && 
					!oNode.getNode().getId().equals(ProjectCompendium.APP.getInBoxID())) {
				boolean bWordAdded = false;
				String oldText = oNode.getText();
				
	/** Start of added by Andrew 	**/		
				String sWord="";
				// Note \b = word boundary, \S = A non-whitespace character, \s = A whitespace character
				// Test if oldText has a least one non-whitespace character i.e. a word boundary followed by one or more whtespace characters
				
	//			If old text is a bounded word
	//			if (oldText.matches("\\b[\\S]+\\b"))	{				
				if (oldText.matches("(\\b[\\S]+)+"))	{
					// And if the new char is  white space, set sWord tobe oldText
					if (key.matches("\\s"))	{
						// oldText is  the first word,  use for the LD operations to test
						sWord = oldText;
						bWordAdded = true;
					}
				}
	/** The above only matches the first word. To match new words use something like
	 * [\\b[\\S]+]+]
	 */
	/** End of added by Andrew	**/			
	/**
	 * Andrew's notes
	 * startSelection and stopSelection are initialised to -1.
	 * This sets text equal to all the label text except the selection.
	 */
				int iStartSelection = this.getStartSelection();
				int iStopSelection = this.getStopSelection();
				if (iStartSelection  > -1 && iStopSelection  > -1 && iStopSelection > iStartSelection) {
					String text = oldText.substring(0, iStartSelection) + oldText.substring(iStopSelection);
				
					this.setCurrentCaretPosition(iStartSelection);				
					this.setStartSelection(-1);
					this.setStopSelection(-1);
					oldText = text;
				}
	
				if(oldText.equals(ICoreConstants.NOLABEL_STRING)) {
					oldText = "";
					this.setCurrentCaretPosition(0);
				}
	
				String newText = "";
	
				//newText = oldText + key;
				if (this.getCurrentCaretPosition() < oldText.length())
					newText = oldText.substring(0, this.getCurrentCaretPosition()) + key + oldText.substring(this.getCurrentCaretPosition());
				else
					newText = oldText + key;
	
				int iCurrentCaretPosition = this.getCurrentCaretPosition();
				this.setCurrentCaretPosition(iCurrentCaretPosition++);
				
				if (bWordAdded)	{
					
				}
	/*** Added by Andrew: First attempt at detecting new words as they are added by the user	 ***/	
				// This needs to be completed. Use this instead of code at beginning of method to set words to create help for
				// I'm not sure, but I think the code below only works if there's a string in the label already?
				String[] oNewTokens = newText.split("\\s");
				String[] oOldTokens = this.getPreviousString().split("\\s");
				HashSet<String> oNewTokenSet = new HashSet<String>(Arrays.asList(oNewTokens));
				HashSet<String> oOldTokenSet = new HashSet<String>(Arrays.asList(oOldTokens));
				HashSet<String> oDifferenceTokenSet = new HashSet<String>(oNewTokenSet);
				//oDifferenceTokenSet is the newly added tokens 
				oDifferenceTokenSet.removeAll(oOldTokenSet); 
	/** End of added by Andrew. This section of code is not used yet. **/			
				
				Model oModel = (Model)ProjectCompendium.APP.getModel();
				boolean bDetailPopup = oModel.detailPopup;					
				int nLabelPopupLength = oModel.labelPopupLength;					
	/** this is where I lose the focus on the label ??? **/
				if(bDetailPopup
						&& newText.length() >= nLabelPopupLength) {
	
					if ( this.isOpeningDialog() ) {
						this.setDialogBuffer(this.getDialogBuffer() + key);
					}
					else if (this.getEditDialog() != null && this.getEditDialog().isVisible()) {
						JTextArea textArea = this.getEditDialog().getDetailField();
						textArea.setText(textArea.getText()+key);
						textArea.setCaretPosition(textArea.getText().length());
					}
					else {
						// TIMING HEAR WAS REALLY IMPORTANT HENCE VARIOUS ODDITIES
						this.setOpeningDialog(true);
						this.setDialogBuffer(this.getDialogBuffer()+ key );
						openEditDialog(true);
					}
					return;
				}
	

				this.setPreviousString(oldText);
				
				oNode.setText(newText);
				ProjectCompendium.APP.setStatus(newText);
				
				if (FormatProperties.autoSearchLabel && (!bDetailPopup ||
						bDetailPopup && newText.length() < nLabelPopupLength)) {
					/** Changed by Andrew 		***/
					if (oViewPane != null && oNode != null)	{
	/**					if (oNode.getNode().getLdType() == ILdCoreConstants.iLD_TYPE_NO_TYPE) 
							oViewPane.showLabels(oNode, newText);	**/
						switch (oNode.getNode().getLdType())	{
						case ILdCoreConstants.iLD_TYPE_NO_TYPE: oViewPane.showLabels(oNode, newText); break;
						case ILdCoreConstants.iLD_TYPE_ACTIVITY: {
							// Check tht the word is in the mapping between tasks verbs and tools
							if (ProjectCompendium.APP.getActivityLabelProcessor().getHmWordToToolsMap().containsKey(sWord.toLowerCase()))
								oViewPane.showTasksHelp(oNode, sWord); 
							break;
						}
						case ILdCoreConstants.iLD_TYPE_TASK: {
							// Check tht the word is in the mapping between tasks verbs and tools
							if (ProjectCompendium.APP.getActivityLabelProcessor().getHmWordToToolsMap().containsKey(sWord.toLowerCase()))
								oViewPane.showTasksHelp(oNode, sWord); 
							break;
						}
						case ILdCoreConstants.iLD_TYPE_VLE_TOOL:	{
							// Check tht the word is in the mapping between tasks verbs and tools
							if (ProjectCompendium.APP.getActivityLabelProcessor().getHmWordToToolsMap().containsKey(sWord.toLowerCase())) 
								oViewPane.showToolsHelp(oNode, sWord); 
							break;
						}
						default:  oViewPane.showLabels(oNode, newText); break;
						}
					/**	End of changed by Andrew	**/
					}
				}
			}
		}
		
	
	
		/**
		 * This method returns an array of information about where each piece of information
		 * the node is displaying as an indicator. E,.g. weight, transclusions, text,
		 * no of items in view. This information is presented as a Rectangle for each indicator; 
		 * if the node is not displaying a particular piece of information a null is recorded.
		 * The information is returned in array in this order:
		 *  {tag, node detail, transclusion, items within view}.
		 * @return an array of Rectangles
		 */
		public ArrayList<Rectangle> getIndicatorRectangles()	{
			ArrayList<Rectangle>  rRectangleInfo;
			Rectangle oTimeRect = null;
			Rectangle oUINodeBoundary = oNode.getBounds();
			rRectangleInfo =  super.getIndicatorRectangles();
			
			/** Check if node has the required data AND that the relevant rectangle has been drawn 	**/
				
			if (this.hasTaskTime())	{
		//		The x, y of tthe time rectangle rTimeRectangle records the poistion of the baseline of the characters hence adjust for height of charcaters
				oTimeRect = new Rectangle(oUINodeBoundary.x + getIconRectangle().x + rTimeRectangle.x, oUINodeBoundary.y +  rTimeRectangle.y - rTimeRectangle.height ,  rTimeRectangle.width , rTimeRectangle.height);
			}
			
			rRectangleInfo.add(oTimeRect);
			return rRectangleInfo;
		}
		
		/**
		 * This method returns an array of information about where each piece of information
		 * the node is displaying as an indicator. E,.g. weight, transclusions, text,
		 * no of items in view. This information is presented as a Rectangle for each indicator; 
		 * if the node is not displaying a particular piece of information a null is recorded.
		 * The information is returned in array in this order:
		 *  {tag, node detail, transclusion, items within view, timing info}.
		 * @return an array of Rectangles
		 */
		public ArrayList<Rectangle> getAllIndicatorRectangles()	{
			ArrayList<Rectangle>  rRectangleInfo;
			Rectangle oTimeRect = null;
			Rectangle oUINodeBoundary = oNode.getBounds();
			rRectangleInfo =  super.getIndicatorRectangles();
			
			/** Check if node has the required data AND that the relevant rectangle has been drawn 	**/
				
			if (this.hasTaskTime())	{
		//		The x, y of tthe time rectangle rTimeRectangle records the poistion of the baseline of the characters hence adjust for height of charcaters
				oTimeRect = new Rectangle(oUINodeBoundary.x + getIconRectangle().x + rTimeRectangle.x, oUINodeBoundary.y +  rTimeRectangle.y - rTimeRectangle.height ,  rTimeRectangle.width , rTimeRectangle.height);
			}
			
			rRectangleInfo.add(oTimeRect);
			return rRectangleInfo;
		}
		
		/**
		 * Return the node's preferred size.
		 * @param c, the component to return the preferred size for.
		 * @return Dimension, the preferred size for the given node.
		 */
	 	public Dimension getPreferredSize(JComponent c) {

			UINode node = (UILdNode)c;

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
		 * @return the timeRectangle. This should be null unless the node is a 
		 * LdTaskNode.
		 */
		public Rectangle getTimeRectangle() {
			return rTimeRectangle;
		}
}

