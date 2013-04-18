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
import javax.swing.plaf.LabelUI;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JOptionPane;
import java.util.*;

import com.compendium.ProjectCompendium;
import com.compendium.ui.ProjectCompendiumFrame;
//import com.compendium.ui.learningdesign.mappers.*;
import com.compendium.ui.stencils.DraggableStencilIcon;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.learningdesign.mappers.ToolsToOtherToolsMappper;
import com.compendium.learningdesign.ui.*;




/**
 * This class, UINodeLdHelpToolsPanel, draws the panel which offers the
 * user help with respect to tools. Two variants may be instantiated, depending
 * on the value of the instance variable iParentPaneType. This variable is 
 * intended to be set according to the 'type' of the parent pane, i.e. whether
 * the parent pane is help for design of a task, or help for use of a tool. 
 * 
 * Note: this class is not serialized because it is a mechanism for 
 * editing and displaying data: it is the data itself which 
 * should be serialized.
 * @author ajb785
 *
 */
/**
 * @author ajb785
 *
 */
public class UINodeLdHelpToolsPanel extends UINodeLdHelpItemsPanel {
	/**
	 * @uml.property  name="textArea"
	 * @uml.associationEnd  readOnly="true"
	 */
	private JTextArea textArea;
	
	/** The set of UINodeHelpToolPanel's that this instance of UINodeLdHelpToolsPanel contains **/
	private  TreeSet<UINodeLdHelpToolPanel> oToolPanels = null; 
	
	/**
	 * The number of UINodeHelpToolPanel's that this instance of UINodeLdHelpToolsPanel contains  *
	 * @uml.property  name="nToolPanels"
	 */
	private int nToolPanels = 0; 
	
	/**
	 * The node for which this is the help about	*
	 * @uml.property  name="oUILdNode"
	 * @uml.associationEnd  
	 */
	private UILdNode oUILdNode;
	
	

	/**
	 * Constructor
	 */
	public UINodeLdHelpToolsPanel() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Create a UINodeLdHelpToolsPanel instance for the panel type indicated
	 * by the parameter type.
	 * 
	 */
	public UINodeLdHelpToolsPanel(int type) {
		super(type);
		gb = (GridBagLayout)this.getLayout();
		gc = new GridBagConstraints();
		iParentPaneType = type;
	}
	
	/**
	 * Create a UINodeLdHelpToolsPanel instance for the panel type indicated
	 * by the parameter type, and with tools related to the String inputString.
	 * @param type - an integer indicating the type (tasks help or tools help) of
	 * the parent pane. The value of @type is expected to be either 
	 * ILdUIConstants.iTASKSHELP_TAB or ILdUIConstants.iTOOLSHELP_TAB.
	 * @param inputString - a free text String for which the help is to be generated
	 */
	public UINodeLdHelpToolsPanel(int type, String inputString) {
		super(type);
		gb = (GridBagLayout)this.getLayout();
		gc = new GridBagConstraints();
		iParentPaneType = type;
		initLDHelpToolsPanel(inputString);
	}
	
	/**
	 * Create a UINodeLdHelpToolsPanel instance for the panel type indicated
	 * by the parameter type, and with tools related to the String inputString.
	 * @param type - an integer indicating the type (tasks help or tools help) of
	 * the parent pane. The value of @type is expected to be either 
	 * ILdUIConstants.iTASKSHELP_TAB or ILdUIConstants.iTOOLSHELP_TAB.
	 * @param inputString - a free text String for which the help is to be generated
	 */
	public UINodeLdHelpToolsPanel(int type, UILdNode aUILdNode, String inputString) {
		super(type);
		gb = (GridBagLayout)this.getLayout();
		gc = new GridBagConstraints();
		iParentPaneType = type;
		oUILdNode = aUILdNode;
		initLayoutLDHelpToolsPanel(inputString);
	}
	
	/**
	 * Create a UINodeLdHelpToolsPanel instance for the panel type indicated
	 * by the parameter type, and with tools related to the String inputString.
	 * @param type - an integer indicating the type (tasks help or tools help) of
	 * the parent pane. The value of @type is expected to be either 
	 * ILdUIConstants.iTASKSHELP_TAB or ILdUIConstants.iTOOLSHELP_TAB.
	 * @param inputString - a free text String for which the help is to be generated
	 */
	public UINodeLdHelpToolsPanel(int type, UILdNode aUILdNode) {
		super(type);
		gb = (GridBagLayout)this.getLayout();
		gc = new GridBagConstraints();
		iParentPaneType = type;
		oUILdNode = aUILdNode;
	}

	/**
	 * @param isDoubleBuffered
	 */
	public UINodeLdHelpToolsPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param layout
	 * @param isDoubleBuffered
	 */
	public UINodeLdHelpToolsPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param layout
	 */
	public UINodeLdHelpToolsPanel(LayoutManager layout) {
		super(layout);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Initialise the layout without adding anything specific to it.
	 * 
	 */
	/** Use supercalss method
	public void initLayout()	{
		initLDHelpItemsPanel(inputString, ILdUIConstants.oLDToolsHelpBackGroundColour);
	}
	**/
	/**
	 * Initialise the panel, generating and adding icons sets and corresponding
	 * tool panels.
	 */
	public void initLDHelpToolsPanel(String inputString)	{
		initLDHelpItemsPanel(inputString, ILdUIConstants.oLDToolsHelpBackGroundColour);
					
		ToolsToOtherToolsMappper toolMapper = new ToolsToOtherToolsMappper();
		HashSet<DraggableStencilIcon> toolSet = toolMapper.getToolStencils(inputString, oUILdNode);
		nToolPanels =  toolSet.size();
		// Generate the tool panels and set variable oToolPanels equal to the set
		setToolsPanels(toolSet);
		layoutToolPanels();

		Dimension size = this.getSize();
		
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		
	}
	
	/**
	 * Initialise the layout of the panel, but do not add  icon sets and tool panels.
	 */
	private void initLayoutLDHelpToolsPanel(String inputString)	{
		initLDHelpItemsPanel(inputString, ILdUIConstants.oLDToolsHelpBackGroundColour);
	}
	
	/**
	 * Generate the beginning of the string for the label of the panel. The content of the label
	 * varies depending on whether it's part of a tools  or tasks help panel.
	 * @return - the String to be used as the first part of the label
	 */
	public String getLabelPrepend()	{
		int x = getParentPaneType();
		switch (x)	{
			case ILdUIConstants.iTASKSHELP_TAB: return "Tools to support: "; //break; - Do not need because of return
			case ILdUIConstants.iTOOLSHELP_TAB: return "Tools related to : "; 
			default: return "Tools related to: "; 		
		}
	}
	
	/**
	 * Method to generate  tool panels containing DraggableStencil icons.
	 * These tool panels are added to the instance variable oToolPanels (a TreeSet).
	 * @param toolSet - a HashSet containing DraggableStencilIcons
	 */
	public TreeSet<UINodeLdHelpToolPanel> setToolsPanels(HashSet<DraggableStencilIcon> toolSet)	{
		oToolPanels = new TreeSet<UINodeLdHelpToolPanel>();

		for (Iterator<DraggableStencilIcon> it = toolSet.iterator(); it.hasNext();)	{
			DraggableStencilIcon oDi = it.next();
			UINodeLdHelpToolPanel oPanel = new UINodeLdHelpToolPanel(oDi);
			try	{
				oToolPanels.add(oPanel);					
			}
			catch (ClassCastException e) {
				 JOptionPane.showMessageDialog(ProjectCompendium.APP, "setToolsPanels()Class cast exception. Can not add: " + oPanel.getClass().getName() + " to " + oToolPanels.getClass().getName(), "Alert: Class cast exception", JOptionPane.ERROR_MESSAGE);
				System.out.println("setToolsPanels()Class cast exception - class is: " + oPanel.getClass().getName());
			}
		}
		return oToolPanels;
	}

	/**
	 * Layout the tool panels assigned to the instance variable  oToolPanels
	 */
	public boolean layoutToolPanels()	{
		if (oToolPanels.size() == 0)
			return false;
		else	{
			//Specify the width  that the label should be displayed across
			gc.anchor = GridBagConstraints.FIRST_LINE_START;
			gc.gridwidth  = nToolPanels;
			// Make the title panel fill the width of the tools panel
			add(titlePanel, gc);
//			gc.anchor = GridBagConstraints.PAGE_END;

			// Set the constraints and add the tool panel(s) 
			int x = 0;
			gc.gridy = 1;
			gc.fill = GridBagConstraints.NONE;
			// Set the grid width for each tool panel 
			gc.gridwidth = 1;
			for (Iterator<UINodeLdHelpToolPanel> it = oToolPanels.iterator(); it.hasNext(); )	{
//				GridBagConstraints oGC = new GridBagConstraints();			
				gc.gridx = x;
//				oGC.weighty = 0.5; oGC.weightx = 0.5;
				add(it.next(), gc);
				x++;
			}
			return true;
		}
	}
	/**
	 * @return  the nToolPanels
	 * @uml.property  name="nToolPanels"
	 */
	public int getNToolPanels() {
		return nToolPanels;
	}
}
