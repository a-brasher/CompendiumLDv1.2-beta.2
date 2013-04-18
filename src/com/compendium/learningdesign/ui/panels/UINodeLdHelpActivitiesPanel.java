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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.util.*;

import com.compendium.ProjectCompendium;
import com.compendium.learningdesign.core.ILdCoreConstants;
import com.compendium.learningdesign.mappers.LdActivityMapper;
import com.compendium.learningdesign.mappers.TaskVerbToActivityMapper;
import com.compendium.learningdesign.mappers.ToolsToActivitesMapper;
import com.compendium.learningdesign.ui.ILdUIConstants;
import com.compendium.learningdesign.ui.UILdImages;
import com.compendium.learningdesign.ui.draggable.LdDraggableToolBarIcon;
//import com.compendium.ui.learningdesign.mappers.*;
import com.compendium.ui.stencils.*;
import com.compendium.core.datamodel.*;

/**
 * @author ajb785
 *
 */
public class UINodeLdHelpActivitiesPanel extends UINodeLdHelpItemsPanel {
	/** The set of UINodeHelpToolPanel's that this instance of UINodeLdHelpToolsPanel contains **/
	private  TreeSet<UINodeLdHelpActivityPanel> oActivityPanels = null; 
	
	/**
	 * The number of UINodeHelpToolPanel's that this instance of UINodeLdHelpToolsPanel contains  *
	 * @uml.property  name="nActivityPanels"
	 */
	private int nActivityPanels = 0; 
	
	/**
	 * @uml.property  name="mapper"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private LdActivityMapper mapper = null;
	
	/**
	 * Create a UINodeLdHelpToolsPanel instance for the panel type indicated
	 * by the parameter type, and with tools related to the String inputString.
	 * @param type - an integer indicating the type (tasks help or tools help) of
	 * the parent pane. The value of @type is expected to be either 
	 * ILdUIConstants.iTASKSHELP_TAB or ILdUIConstants.iTOOLSHELP_TAB.
	 * @param inputString - a free text String for which the help is to be generated
	 */
	public UINodeLdHelpActivitiesPanel(int type, String inputString) {
		super(type);
//		gb = (GridBagLayout)this.getLayout();
//		gc = new GridBagConstraints();
//		iParentPaneType = type;
		initialiseMapper(type);
		initLDHelpActivitiesPanel(inputString);;
	}

	/**
	 * Create a UINodeLdHelpToolsPanel instance for the panel type indicated
	 * by the parameter type, and with tools related to node oNode.
	 * @param type - an integer indicating the type (tasks help or tools help) of
	 * the parent pane. The value of @type is expected to be either 
	 * ILdUIConstants.iTASKSHELP_TAB or ILdUIConstants.iTOOLSHELP_TAB.
	 * @param inputString - a free text String for which the help is to be generated
	 */
	public UINodeLdHelpActivitiesPanel(int type, NodeSummary oNode) {
		super(type, oNode);
		initialiseMapper(type);
		initLDHelpActivitiesPanel(oNode.getLabel());
	}
	
	/**
	 * Create a UINodeLdHelpToolsPanel instance for the panel type indicated
	 * by the parameter type, and with activities  to the string inputString.
	 * @param type - an integer indicating the type (tasks help or tools help) of
	 * the parent pane. The value of @type is expected to be either 
	 * ILdUIConstants.iTASKSHELP_TAB or ILdUIConstants.iTOOLSHELP_TAB.
	 * @param oNode - the node to which this help is related. 
	 * @param inputString - a free text String for which the help is to be generated
	 */
	public UINodeLdHelpActivitiesPanel(int type, NodeSummary oNode, String inputString) {
		super(type, oNode);
		initialiseMapper(type);
		initLDHelpActivitiesPanel(inputString);
	}
	
	private void initLDHelpActivitiesPanel(String inputString)	{
		// Create and initialize the title panel; if it's toll help put the tool name in the label
		if (iParentPaneType == ILdUIConstants.iTOOLSHELP_TAB)	{		
			String toolLabel = ProjectCompendium.APP.getLdTypeTagMaps().getToolTypeToToolNamesTable().get(oNode.getLdToolType());
		initLDHelpItemsPanel(toolLabel, ILdUIConstants.oLDActivitiesHelpBackGroundColour);
		}
		else
			initLDHelpItemsPanel(inputString, ILdUIConstants.oLDActivitiesHelpBackGroundColour);
		// 
		HashSet<NodeSummary>  nodeSet = mapper.findRelevantActivities(inputString);
		int nActiviesPanels = nodeSet.size();
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.gridwidth  = nActiviesPanels;
		add(titlePanel, gc);
		if (nActiviesPanels == 0)	{
			String s = "No relevant activities!!!!";
			add(new JLabel(s), gc);
		}
		else {
			setActivitiesPanels(nodeSet);
			int x = 0;
			gc.gridy = 1;
			gc.fill = GridBagConstraints.HORIZONTAL;
			// Set the grid width for each tool panel 
			gc.gridwidth = 1;
			// Temporary variable to hold activity names until clone or shortcut nodes have been instntiated
			String s = "";
			 
	//		for (Iterator<NodeSummary> i = nodeSet.iterator(); i.hasNext();) {
			for (Iterator<UINodeLdHelpActivityPanel> j = oActivityPanels.iterator(); j.hasNext(); ) {
				gc.gridx = x;
				/**
				s = i.next().getLabel();
				if (i.hasNext())	{
					s = s  + ", ";
				}		
				
				add(new JLabel(s), gc);
				**/
				add(j.next(), gc);
				x++;
			}
		}
	}
	
	/**
	 * This method is a helper metho to initialise the mapper used by this
	 * instance.
	 * @param type , the type of the tab either iTOOLSHELP_TAB or iTASKSHELP_TAB.
	 */
	private void initialiseMapper(int type)	{
		if (type == ILdUIConstants.iTOOLSHELP_TAB)	{
			mapper = new ToolsToActivitesMapper(this.getNode());
			
		}
		else	{
			mapper = new TaskVerbToActivityMapper(this.getNode());
		}
	}
	
	/**
	 * Initialise the layout without adding anything specific to it.
	 * Do notneed this as it stands - just use superclass method
	 */
	public void initLayout()	{
		super.initLayout();
	}
	
	/* (non-Javadoc)
	 * @see com.compendium.ui.learningdesign.UINodeLdHelpItemsPanel#getLabelPrepend()
	 */
	@Override
	public String getLabelPrepend() {	
		int x = getParentPaneType();
		switch (x)	{
			case ILdUIConstants.iTASKSHELP_TAB: return "Activities using task: "; //break; - Do not need because of return
			case ILdUIConstants.iTOOLSHELP_TAB: return "Activities using tool: "; 
			default: return "Activities related to: "; 		
		}
	}

	/**
	 * Method to generate  tool panels containing DraggableStencil icons.
	 * These tool panels are added to the instance variable oToolPanels (a TreeSet).
	 * @param toolSet - a HashSet containing DraggableStencilIcons
	 */
	public void setActivitiesPanels(HashSet<NodeSummary>  nodeSet)	{
		oActivityPanels = new TreeSet<UINodeLdHelpActivityPanel>();
		UIStencilSet oSS = ProjectCompendium.APP.oStencilManager.getStencilSet(ILdCoreConstants.sLD_STENCIL_NAME);
		Vector<DraggableStencilIcon> vtDSI = oSS.getItemsWithTag(ILdCoreConstants.sACTIVITY_TAG);
		// Know there is only going to be one DraggableStencilIcon in the LD set tagged with SActivity_Tag
		DraggableStencilIcon oDiTemp = vtDSI.firstElement();
		String sLabel = "";
		for (Iterator<NodeSummary> it = nodeSet.iterator(); it.hasNext();)	{
			NodeSummary oNS = it.next();
			sLabel = oNS.getLabel();
			//oNS.
			DraggableStencilIcon oDi = oDiTemp.duplicate();
			LdDraggableToolBarIcon oLdTBIcon = new LdDraggableToolBarIcon(oNS,UILdImages.getNodeIcon(ILdCoreConstants.iLD_TYPE_ACTIVITY));
			
			oDi.setLabel(sLabel);
			oDi.setToolTipText(sLabel);
//			DraggableStencilIcon oDi = new DraggableStencilIcon();
			
			//UINodeLdHelpActivityPanel oPanel = new UINodeLdHelpActivityPanel(oDi);
			UINodeLdHelpActivityPanel oPanel = new UINodeLdHelpActivityPanel(oLdTBIcon);
			try	{
				oActivityPanels.add(oPanel);					
			}
			catch (ClassCastException e) {
				 JOptionPane.showMessageDialog(ProjectCompendium.APP, "setToolsPanels()Class cast exception. Can not add: " + oPanel.getClass().getName() + " to " + oActivityPanels.getClass().getName(), "Alert: Class cast exception", JOptionPane.ERROR_MESSAGE);
				System.out.println("setToolsPanels()Class cast exception - class is: " + oPanel.getClass().getName());
			}
		}
	}
}
