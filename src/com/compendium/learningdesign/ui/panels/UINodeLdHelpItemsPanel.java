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

package com.compendium.learningdesign.ui.panels;

import java.awt.*;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.compendium.learningdesign.ui.ILdUIConstants;
import com.compendium.core.datamodel.NodeSummary;

/**
 * This abstract class defines the common attributes and behaviour for the UINodeLdHelpToolsPanel, UINodeLdHelpActivitiesPanel, and UINodeLdHelpcaseStudiesPanel classes. E.g., the title panel components of these classes.
 * @author  ajb785
 */
public abstract class UINodeLdHelpItemsPanel extends JPanel {
	/**
	 * paneType indicates which type of pane this panel belongs to (is part of) i.e. Tasks help or Tools help	*
	 * @uml.property  name="iParentPaneType"
	 */
	protected int iParentPaneType	=	0;
	
	/**
	 * titlePanel is the topmost panel containing the title and text area *
	 * @uml.property  name="titlePanel"
	 * @uml.associationEnd  
	 */
	protected	JPanel	titlePanel = null; 
	
	/**
	 * The layout manager used for this panel.
	 * @uml.property  name="gb"
	 */
	protected GridBagLayout 		gb 					= null;

	/**
	 * The constaints used with the layout manager.
	 * @uml.property  name="gc"
	 */
	protected GridBagConstraints 	gc 					= null; 
	
	/**
	 * The node for which this panel is offering help	*
	 * @uml.property  name="oNode"
	 * @uml.associationEnd  
	 */
	protected NodeSummary oNode;
	
	/** Font used for headings in all instances 	**/
	public static Font 			headingFont 		= new Font("Dialog", Font.BOLD, 12);

	
	/**
	 * 
	 */
	public UINodeLdHelpItemsPanel() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param isDoubleBuffered
	 */
	public UINodeLdHelpItemsPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param layout
	 * @param isDoubleBuffered
	 */
	public UINodeLdHelpItemsPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param layout
	 */
	public UINodeLdHelpItemsPanel(LayoutManager layout) {
		super(layout);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Create a UINodeLdHelpItemsPanel instance for the panel type indicated
	 * by the parameter type. Create it with a GribBagLayout, and set 
	 * gb = the GridBagLayout of the instance, and 
	 * gc = the GridBagConstraints of the instance.
	 * 
	 */
	public UINodeLdHelpItemsPanel(int type) {
		super(new GridBagLayout());
		gb = (GridBagLayout)this.getLayout();
		gc = new GridBagConstraints();
		iParentPaneType = type;
		//initLDHelpItemsPanel("");
	}
	
	/**
	 * Create a UINodeLdHelpItemsPanel instance for the panel type indicated
	 * by the parameter type. Create it with a GribBagLayout, and set 
	 * gb = the GridBagLayout of the instance, and 
	 * gc = the GridBagConstraints of the instance.
	 * 
	 */
	public UINodeLdHelpItemsPanel(int type, NodeSummary oNodeSummary) {
		super(new GridBagLayout());
		gb = (GridBagLayout)this.getLayout();
		gc = new GridBagConstraints();
		iParentPaneType = type;
		oNode = oNodeSummary;
		//initLDHelpItemsPanel("");
	}
	
	/**
	 * Initialise the panel by creating the title panel. This should be called by subclasses of
	 * UINodeLdHelpItemsPanel with the relevant colour as a parameter.
	 * 
	 */
	public void initLDHelpItemsPanel(String inputString, Color oTitleBackgroundColor)	{
		initLayout(oTitleBackgroundColor);
		
		// Create and add the label supplied by the parameter
		//If the node is a tool node, add the tool type to the label
		JLabel lblTemp = new JLabel(inputString);
		lblTemp.setFont(UINodeLdHelpToolsPanel.headingFont);
		lblTemp.setBackground(Color.white);
		gc.fill = GridBagConstraints.HORIZONTAL;
		titlePanel.add(lblTemp, gc);
		
	}
	
	/**
	 * Initialise the layout with a specific background color but without 
	 * adding anything else specific to it.
	 */
	public void initLayout(Color oTitleBackgroundColor)	{
		initLayout();
		// set the background colour
		titlePanel.setBackground(oTitleBackgroundColor);
		this.setBackground(Color.white);
	}
	
	/**
	 * Initialise the layout without adding anything specific to it.
	 */
	public void initLayout()	{
		// Set the maximum size to be massive so that it will fill its parent panel				
		this.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		// Create the titlePanel with default layout manager i.e. FlowLayout
		titlePanel = new JPanel( );
		JLabel lblPrepend = new JLabel(getLabelPrepend());
		Font font = UINodeLdHelpToolsPanel.headingFont;
		lblPrepend.setFont(font);
		titlePanel.add(lblPrepend);
	}
	
	/**
	 * Get the type of the parent pane, i.e the pane that this UINodeLdHelpToolsPanel
	 * is a part of
	 * @return int
	 */
	public int getParentPaneType()	{
		return iParentPaneType;
	}
	
	/**
	 * Generate the beginning of the string for the label of the panel. The content of the label varies depending on whether it's part of a tools  or tasks help panel.
	 * @return  - the String to be used as the first part of the label
	 * @uml.property  name="labelPrepend"
	 */
	public abstract String getLabelPrepend() ;

	/**
	 * Return the NodeSummary for which the panel is offering help
	 * @return the oNode
	 */
	public NodeSummary getNode() {
		return oNode;
	}		
}
