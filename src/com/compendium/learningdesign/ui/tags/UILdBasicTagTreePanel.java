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

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import com.compendium.ProjectCompendium;
import com.compendium.core.CoreUtilities;
import com.compendium.core.datamodel.Code;
import com.compendium.ui.UIUtilities;
import com.compendium.ui.tags.CheckNode;
import com.compendium.ui.tags.UITagTreePanel;

public class UILdBasicTagTreePanel extends UITagTreePanel {

	
	/**
	 * Create tree of nodes for the tree on the code assignment panel.
	 * Do not include learning design codes/tags in this panel
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
