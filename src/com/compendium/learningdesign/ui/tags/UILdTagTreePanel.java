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

package com.compendium.learningdesign.ui.tags;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.BoxLayout;

import com.compendium.ProjectCompendium;
import com.compendium.core.CoreUtilities;
import com.compendium.core.datamodel.Code;
import com.compendium.ui.FormatProperties;
import com.compendium.ui.ProjectCompendiumFrame;
import com.compendium.ui.UIButton;
import com.compendium.ui.UIUtilities;
import com.compendium.ui.tags.CheckNode;
import com.compendium.ui.tags.UITagTreeGroupPopupMenu;
import com.compendium.ui.tags.UITagTreeLeafPopupMenu;
import com.compendium.ui.tags.UIWorkingList;
//import com.compendium.ui.tags.UITagTreePanel.CheckBoxNodeEditor;
//import com.compendium.ui.tags.UITagTreePanel.CheckBoxNodeRenderer;

public class UILdTagTreePanel extends com.compendium.ui.tags.UITagTreePanel {
	/**
	 * The main panel holding the list and tree.
	 * @uml.property  name="ldTypesPanel"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	protected UILdTypeTreePanel			ldTypesPanel			= null;
	
	/**
	 * The panel holding the tag list and tree (called centerpanel in the superclass).
	 * @uml.property  name="tagpanel"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	protected JPanel			tagpanel			= null;
	
	/**
	 * The scrollpane for the learning design types  tree.
	 * @uml.property  name="ldTypesSp"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JScrollPane 				ldTypesSp 				= null;
	/**
	 * Constructor.
	 */
	public UILdTagTreePanel() {
		super(ProjectCompendium.APP.getModel());
		showCodePanel();	
		me = this;
		updateSelectionListView();	
		tfNewCode.requestFocus();
	}
	/**
	 * Draw the panel contents.
	 */
	private void showCodePanel() {
		
		setLayout(new BorderLayout());
		setFont(ProjectCompendium.APP.labelFont);
		
		JPanel leftpanel = new JPanel(new BorderLayout());
		// Change layout orientation when vertical/ horizontal split changed?
		centerpanel = new JPanel();
		centerpanel.setLayout(new BoxLayout(centerpanel, BoxLayout.Y_AXIS));
		// Tag PANEL - using which the user can tag nodes
		tagpanel = new JPanel(new BorderLayout());

		// TOP PANEL for tag panel
		JPanel toppanel = createTopPanel();

		tagpanel.add(toppanel, BorderLayout.NORTH);
		
		String sOrientation = FormatProperties.getFormatProp("tagsViewOrientation");
		if (sOrientation != null && sOrientation.equals("horizontal")) {
			nOrientation = JSplitPane.HORIZONTAL_SPLIT;
		}
		 
		// Jpanel to hold the Learning design tag tree
		JPanel centerpanelLd = new JPanel(new BorderLayout());
		sp = createTree();
		int ispHeight = sp.getPreferredSize().height;
		sp.addMouseListener(oWorkingList);
		/** Creation of rightpanel moved to here by Andrew because ldTypesPanel 
		 * 	needs a filled oWorkingList.
		 */
		
		JPanel rightpanel = createUsagePanel();
		ldTypesPanel = new UILdTypeTreePanel(oWorkingList, oWorkingScroll, lblNodes );
		ldTypesSp = ldTypesPanel.getSp(); 
		int iLdTypesPanelHeight = ldTypesSp.getPreferredSize().height;
		tree.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				oWorkingList.mouseClicked(e);
			}
		});	
		/** Set heights of the tagging panels 	
		int iTotalHeight = iLdTypesPanelHeight + ispHeight;
		int iSpPrefHeight = ((ispHeight*ispHeight)/iTotalHeight);
		int iLdTypesPanelPreHeight = ((iLdTypesPanelHeight*ispHeight)/iTotalHeight);
		sp.setPreferredSize(new Dimension(250, iSpPrefHeight));
		**/
		tagpanel.add(sp, BorderLayout.CENTER);

		JPanel buttonpanel = new JPanel();
		pbExpand = new UIButton("Expand All Folders!!");
		pbExpand.setMnemonic(KeyEvent.VK_E);		
		pbExpand.addActionListener(this);
		pbExpand.setEnabled(true);
		buttonpanel.add(pbExpand);
		tagpanel.add(buttonpanel, BorderLayout.SOUTH);
		JPanel ldLabelPanel = new JPanel();
		ldLabelPanel.add(new JLabel("Learning design nodes"));
//		ldTypesSp.setPreferredSize(new Dimension(250, iLdTypesPanelPreHeight));
		centerpanelLd.add(ldTypesSp, BorderLayout.CENTER);
		centerpanel.add(centerpanelLd);
		centerpanel.add(tagpanel);
		leftpanel.add(centerpanel, BorderLayout.CENTER);
//		leftpanel.add(tagpanel, BorderLayout.NORTH);
//		leftpanel.add(centerpanelLd, BorderLayout.SOUTH);
		
		// JPanel rightpanel = createUsagePanel();

		oSplitter = new JSplitPane(nOrientation, true, leftpanel, rightpanel) {
			boolean isPainted = false;
			boolean hasProportionalLocation = false;
			double proportionalLocation;
			public void setDividerLocation(double proportionalLocation) {
				if (!isPainted) {
					hasProportionalLocation = true;
			        this.proportionalLocation = proportionalLocation;
			    } else {
			    	super.setDividerLocation(proportionalLocation);
			    }
			}
			public void paint(Graphics g) {
				if (!isPainted) {
					if (hasProportionalLocation) {
						super.setDividerLocation(proportionalLocation);
					}
			        isPainted = true;
			    }
			    super.paint(g);
			}			
		};
		oSplitter.setOneTouchExpandable(true);
		oSplitter.setDividerSize(8);
		oSplitter.setContinuousLayout(true);
		oSplitter.setMinimumSize(new Dimension(200, oSplitter.getPreferredSize().height));
		oSplitter.setDividerLocation(0.5);

		add(oSplitter, BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);

		addFocusListener( new FocusListener() {
        	public void focusGained(FocusEvent e) {
				tree.requestFocus();
			}
            public void focusLost(FocusEvent e) {

			}
		});

		validate();		
	}
	
	protected JPanel createUsagePanel() {
		JPanel panel = new JPanel(new BorderLayout());

		// Add labels
		JPanel labelpanel = new JPanel(new BorderLayout());
		
		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.setBorder(new EmptyBorder(5,5,0,5));		
		lblViews = new JLabel("Working Tags Area");
		lblViews.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		panel1.add(lblViews, BorderLayout.CENTER);
		
		JPanel panel2 = new JPanel(new BorderLayout());
		panel2.setBorder(new EmptyBorder(5,5,5,5));		
		lblNodes = new JLabel("Nodes:");
		lblNodes.setAlignmentX(JLabel.LEFT_ALIGNMENT);		
		panel2.add(lblNodes, BorderLayout.CENTER);
		
		labelpanel.add(panel1, BorderLayout.NORTH);
		labelpanel.add(panel2, BorderLayout.CENTER);
		
		oWorkingList = new UIWorkingList();
		oWorkingList.getList().getSelectionModel().addListSelectionListener(this);
		
		oWorkingScroll = new JScrollPane(oWorkingList.getList());
		oWorkingScroll.addMouseListener(oWorkingList);
		
		//if (this.nOrientation == JSplitPane.HORIZONTAL_SPLIT) {
			Dimension size = tree.getPreferredSize();
			oWorkingScroll.setPreferredSize(new Dimension(250, size.height));
		//} else {
		//	oWorkingScroll.setPreferredSize(new Dimension(250, 250));			
		//}
				
		JPanel buttonpanel = new JPanel();
		
		pbInsert = new UIButton("Insert into View");
		pbInsert.setMnemonic(KeyEvent.VK_I);
		pbInsert.addActionListener(this);
		buttonpanel.add(pbInsert);
		
		pbDeselectAll = new UIButton("Deselect All");
		pbDeselectAll.setMnemonic(KeyEvent.VK_D);		
		pbDeselectAll.addActionListener(this);
		buttonpanel.add(pbDeselectAll);

		//buttonpanel.add(pbSelectAll);
		buttonpanel.add(pbDeselectAll);
		
		panel.add(labelpanel, BorderLayout.NORTH);		
		panel.add(oWorkingScroll, BorderLayout.CENTER);		
		panel.add(buttonpanel, BorderLayout.SOUTH);
		
		return panel;
	}
	
	/**
	 * Create tree of nodes for the tree on the code assignment panel.
	 *
	 * @return DefaultMutableTreeNode, the top tree node for the tree.
	 */
	public DefaultMutableTreeNode getTreeData() {
		
		// TOP NEEDS TO MATCH THE STRUCTURE OF THE REST OF THE TREE DATA
		Vector topdata = new Vector(2);
		topdata.addElement(new String(""));
		topdata.addElement(new String("Tags"));
		DefaultMutableTreeNode activeGroupNode = null;
		CheckNode check = new CheckNode(topdata);
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(check);

		try {
			Vector order = new Vector(51);
			Hashtable groups = model.getCodeGroups();

			DefaultMutableTreeNode group = null;
			DefaultMutableTreeNode code = null;
			check = null;
			// Get the Learning design code group IDs
			HashSet hsLdCodeGroups = ProjectCompendium.APP.getLdTypeTagMaps().getHsLdCodeGroups();
			// ADD CODE GROUPS
			for(Enumeration e = groups.elements();e.hasMoreElements();) {
				Hashtable nextGroup = (Hashtable)e.nextElement();

				if (nextGroup.containsKey("group")) {
					Vector groupdata = (Vector)nextGroup.get("group");
					String sCodeGroupID = (String)groupdata.elementAt(0);
					// If the group is not a LD group
					if (!hsLdCodeGroups.contains(sCodeGroupID) )	{
						check = new CheckNode(groupdata);
						group = new DefaultMutableTreeNode(check);

						Hashtable children = null;

						if (nextGroup.containsKey("children")) {
							children = (Hashtable)nextGroup.get("children");

							// ADD ALL CHILD CODES FOR THIS GROUP
							Vector childorder = new Vector(51);

							for(Enumeration e2 = children.elements();e2.hasMoreElements();) {
								Code nextcode = (Code)e2.nextElement();
								check = new CheckNode(nextcode);
								code = new DefaultMutableTreeNode(check);
								childorder.addElement(code);
							}

							childorder = UIUtilities.sortList(childorder);

							if (childorder != null) {
								int count  = childorder.size();
								for (int i=0; i<count; i++) {
									group.add((DefaultMutableTreeNode)childorder.elementAt(i));
								}
							}
						}

						// DON'T SHOW EMPTY GROUPS
						//if (children != null && children.size() > 0) {
						// MAKE SURE ACTIVE GROUPS IS AT THE TOP OF THE TREE LATER
						//if (sCodeGroupID.equals(ProjectCompendium.APP.getActiveCodeGroup()))
						//	activeGroupNode = group;
						//else
						order.addElement(group);
						//}
					}
				}
			}

			// ADD NODES TO ROOT NODE
			int jcount  = order.size();
			if (jcount > 0)
				order = UIUtilities.sortList(order);

			// ADD THE ACTIVE GROUP AT THE TOP
			if (activeGroupNode != null)
				top.add(activeGroupNode);

			// ADD ALL OTHER GROUPS
			for (int j=0; j<jcount; j++)
				top.add((DefaultMutableTreeNode)order.elementAt(j));

			// ADD ALL CODES NOT IN GROUPS
			Hashtable ungroupedCodes = model.getUngroupedCodes();

			Vector sortedUngroupedCodes = new Vector(51);
			for(Enumeration un = ungroupedCodes.elements();un.hasMoreElements();) {
				sortedUngroupedCodes.addElement((Code)un.nextElement());
			}
			sortedUngroupedCodes = CoreUtilities.sortList(sortedUngroupedCodes);

			int lcount = sortedUngroupedCodes.size();
			for (int l=0; l<lcount; l++) {
				check = new CheckNode((Code)sortedUngroupedCodes.elementAt(l));
				top.add(new DefaultMutableTreeNode(check));
			}
		}
		catch (Exception io) {
			io.printStackTrace();
		}

		this.top = top;
		return top;
	}
				
}
