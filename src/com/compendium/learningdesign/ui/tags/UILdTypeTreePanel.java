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

import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicTreeUI;

import com.compendium.ui.tags.UITagTreeGroupPopupMenu;
import com.compendium.ui.tags.UITagTreeLeafPopupMenu;
import com.compendium.ui.tags.UITagTreePanel;
import com.compendium.ui.tags.CheckNode;
import com.compendium.ui.tags.UIWorkingList;
import com.compendium.*;
import com.compendium.ui.*;
import com.compendium.ui.plaf.ViewPaneUI;

import com.compendium.core.CoreUtilities;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;

import com.compendium.learningdesign.core.ILdCoreConstants;

public class UILdTypeTreePanel extends UITagTreePanel {

	public UILdTypeTreePanel() {
		super(ProjectCompendium.APP.getModel());
		showCodePanel();	
		me = this;
// updateSelectionListView();	
//		tfNewCode.requestFocus();
	}
	
	public UILdTypeTreePanel(UIWorkingList aWorkingList, JScrollPane aWorkingScroll, JLabel alblNodes ) {
		super(ProjectCompendium.APP.getModel());
		oWorkingList = aWorkingList;
		oWorkingScroll = aWorkingScroll;
		lblNodes = alblNodes;
		showCodePanel();	
		me = this;
		updateSelectionListView();	
//		tfNewCode.requestFocus();
	}
	//	 INNER CLASSES
		
	/**
	 * Inner class to render a tree item. 
	 * @author   Michelle Bachler
	 */
	private class CheckBoxNodeRenderer extends JPanel implements TreeCellRenderer {
		
		private CheckNode box = null;
		private JCheckBox cbCheckBox = null;
		private JLabel label = new JLabel();		
		/**
		 * @uml.property  name="field"
		 */
		private JTextField field = new JTextField();
		
		private Border border = null;
		
		private Icon leafIcon = null;
		private Icon openIcon = null;
		private Icon closedIcon = null;
		
		private Color oHighlight = Color.yellow;
		private Border oMainBorder = null;
		
		protected BorderLayout layout = null;
	
		Color selectionBorderColor;
		Color selectionForeground;
		Color selectionBackground;
		Color textForeground;
		Color textBackground;
		
		protected JCheckBox getCheckBox() {
			return cbCheckBox;
		}
	
		/**
		 * @return
		 * @uml.property  name="field"
		 */
		protected JTextField getField() {
			return field;
		}
	
		protected CheckNode getNode() {
			return box;
		}
		
		protected Border getFieldBorder() {
			return border;
		}		
		
		protected void setDefaultColors() {
			field.setForeground(textForeground);
			field.setBackground(textBackground);
			label.setForeground(selectionForeground);
			label.setBackground(selectionBackground);				
			setBackground(textBackground);							
		}
		
		public CheckBoxNodeRenderer() {
	    	setFont(ProjectCompendiumFrame.labelFont);
			oMainBorder = getBorder();
			layout = new BorderLayout();
			layout.setHgap(5);
			setLayout(layout);
			add(field, BorderLayout.EAST);
			field.setEditable(false);
			border = field.getBorder();
			field.setBorder(null);
	
			DefaultTreeCellRenderer rend = new DefaultTreeCellRenderer();
			leafIcon = rend.getLeafIcon();
			openIcon = rend.getOpenIcon();
			closedIcon = rend.getClosedIcon();
			
			selectionForeground = UIManager.getColor("List.selectionForeground");
			selectionBackground = UIManager.getColor("List.selectionBackground");
			textForeground = UIManager.getColor("List.textForeground");
			textBackground = UIManager.getColor("List.textBackground");
		}
		
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
						
			if (cbCheckBox != null) {
				remove(cbCheckBox);
				cbCheckBox = null;
			}
			remove(label);
			setBorder(oMainBorder);
			
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;			
			Object userObject = node.getUserObject();
			box = (CheckNode)userObject;			
			if (!box.isGroup()) {	
			    Code code = (Code)box.getData();
	
				cbCheckBox = new JCheckBox();
		    	cbCheckBox.setSelected(box.isChecked());	
		    	cbCheckBox.setFont(tree.getFont());
	
		    	if (box.isChecked()) {
		    		if (box.isUniversal()) {
		    			cbCheckBox.setBackground(Color.orange);		    			
		    		} else {
		    			cbCheckBox.setBackground(IUIConstants.DEFAULT_COLOR);
		    		}
		    	} else {
		    		cbCheckBox.setBackground(tree.getBackground());
		    	}
			    add(cbCheckBox, BorderLayout.WEST);
	
				String text = code.getName();
				int count = 0;
				try {
					count = (model.getCodeService()).getNodeCount(model.getSession(), code.getId());
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}	
				field.setText(text+"  ("+count+")");	
	        	field.setFont(tree.getFont());
	
				if (selected) {
					field.setForeground(selectionForeground);
					field.setBackground(selectionBackground);
				} else {
					field.setForeground(textForeground);
					field.setBackground(textBackground);
				}
				setToolTipText("Click to show nodes with this tag (AND any other selected tags)");
				
				layout.setHgap(2);
			} else {
				add(label, BorderLayout.CENTER);
				Icon icon = null;
				if (box.isGroup()) {
					if (expanded)
						icon = openIcon;
					else
						icon = closedIcon;	
				}/* else if (leaf) {
					//icon = leafIcon;
					icon = null;
				}	*/
				label.setIcon(icon);
				label.setForeground(textForeground);
				label.setBackground(textBackground);				
	
				Vector group = (Vector)box.getData();
				field.setText((String)group.elementAt(1));
	        	field.setFont(tree.getFont());
	
				if ( (ProjectCompendium.APP.getActiveCodeGroup()).equals((String)group.elementAt(0)) && !selected ) {
					field.setForeground(IUIConstants.DEFAULT_COLOR);
					field.setBackground(textBackground);				
				} else {												
					if (selected) {
						field.setForeground(selectionForeground);
						field.setBackground(selectionBackground);
					} else {
						field.setForeground(textForeground);
						field.setBackground(textBackground);
					}					
				}		
				
				setToolTipText("Click to show all nodes with one Or more tags in the group");
				if (row == highlightRow) {
					setBorder(new LineBorder(oHighlight, 2));
				}				
			}
			
			setBackground(textBackground);				
			
			htAllRenderers.put(new Integer(row), this);
	
			return this;
		}
	}

	protected JScrollPane createTree() {

		top = getTreeData();

		if (top != null) {

			// CREATE TREE
   	     	tree = new JTree(top);
   	    	treemodel = new DefaultTreeModel(top);
			tree.setRootVisible(false);
   	     	tree.setModel(treemodel);
   	     	tree.setFont(ProjectCompendiumFrame.labelFont);
   	     	   	     	   	     	
   		    CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
   		    tree.setCellRenderer(renderer);

   		    //tree.setCellEditor(new CheckBoxNodeEditor(tree));
   		    //Unlike a UITagTreePanel, the Learning design type tree is not editable
   		    tree.setEditable(false);
 
   		    //UIDraggableTreeCellRenderer renderer = new UIDraggableTreeCellRenderer();   	     	
   	     	tree.setCellRenderer(renderer);
   			tree.setShowsRootHandles(true);   			
   	        tree.setToggleClickCount(4);   	          
   			tree.getSelectionModel().setSelectionMode
   	        	(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

  	     	MouseListener[] mice = tree.getMouseListeners();
   	     	MouseListener mouse = null;
   	     	for( int i=0; i<mice.length; i++){
   	     		mouse = mice[i];
       			tree.removeMouseListener(mouse);
   	     	}   	        			
/** Do not need dragSource or dropTarget as the panel contents are not editable **/   			
//   			dragSource = new DragSource();
//   			dragSource.createDefaultDragGestureRecognizer((JComponent)tree, DnDConstants.ACTION_COPY_OR_MOVE, this);   	
//  		    DropTarget dropTarget = new DropTarget((JComponent)tree, this);   
  	     	
    	    // Enable tool tips.
   			ToolTipManager.sharedInstance().registerComponent(tree);
   			
			tree.expandRow(0);
			
			/*tree.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					tree.setSelectionRow(0);
				}				
				public void focusLost(FocusEvent e) {
					tree.clearSelection();
				}
			});*/
			
			MouseListener ml = new MouseAdapter() {
     			public void mouseReleased(MouseEvent e) {
					boolean isRightMouse = SwingUtilities.isRightMouseButton(e);
					boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);
					if (ProjectCompendium.isMac &&
						(e.getButton() == 3 && e.isShiftDown())) {
						isRightMouse = true;
						isLeftMouse = false;
					}
					     									
					if (tree.isEditing()) {
						tree.stopEditing();
					}				
					
					/*if (!tree.hasFocus()) {
						tree.requestFocus();
					}*/
					
					int nClicks = e.getClickCount();
        			TreePath path = tree.getPathForLocation(e.getX(), e.getY());
        			//int row = tree.getRowForLocation(e.getX(), e.getY());
					if (path != null) {
	  					DefaultMutableTreeNode thenode = (DefaultMutableTreeNode)path.getLastPathComponent();
						CheckNode check = (CheckNode)thenode.getUserObject();		
						if (check.getData() instanceof Code) {
							Code code = (Code)check.getData();		
							if (isRightMouse) {
								/** Do not do anything with right-clicks
								if(nClicks == 1) {
									tree.setSelectionPath(path);
									DefaultMutableTreeNode parent = (DefaultMutableTreeNode)thenode.getParent();
									String sCodeGroupID = "";
									if (parent != null) {
										CheckNode group = (CheckNode)parent.getUserObject();
										if (group.isGroup()) {									
											sCodeGroupID = (String)((Vector)group.getData()).elementAt(0);
										}
									}
									UITagTreeLeafPopupMenu pop = new UITagTreeLeafPopupMenu(me, code, sCodeGroupID);
									pop.show(tree, e.getX(), e.getY());																				
								}	
								**/															
							} else if (isLeftMouse) {
								if (nClicks == 1) {
									/** Do not need to 'check' nodes i.e. to put tick in box 
									if(e.getX() < tree.getPathBounds(path).x + hotspot) {
										if (oWorkingList.getList().getSelectedRowCount() > 0) {
											check.setChecked(!check.isChecked());
											if (check.isChecked()) {
												onAddCodeToNodes(code);
												checkTagsForSelectedNodes();
											} else {
												onRemoveCodeFromNodes(code);
												if (isFilter) {
													updateFilterListView();
												}												
												checkTagsForSelectedNodes();
											}
											tree.repaint();											
										} else {
											ProjectCompendium.APP.displayMessage("Please select some nodes to assign tags to first.", "Tags");
										}
									} else {
									**/
										boolean isSelected = tree.isPathSelected(path);										
										if (isSelected && !e.isShiftDown()) {
											tree.startEditingAtPath(path);
										} else {
											if (!e.isShiftDown()) {
												tree.clearSelection();	
											}							
										
											if (isSelected && e.isShiftDown()) {
												tree.removeSelectionPath(path);
											} else if (!isSelected) {
												tree.addSelectionPath(path);
											}
											clearChecks();
											tree.validate();
											tree.repaint();											
											updateFilterListView();
							//			}
									} 									
								}
	   		          		}
						} 
/*********************  Not needed because there is no need to edit 
						else {
							if (isRightMouse) {						
								UITagTreeGroupPopupMenu pop = new UITagTreeGroupPopupMenu(me, (Vector)check.getData());
								pop.show(tree, e.getX(), e.getY());
							} else if (isLeftMouse) {
								boolean isSelected = tree.isPathSelected(path);
								if (isSelected) {
									tree.startEditingAtPath(path);
								} else {
									tree.clearSelection();	
									tree.addSelectionPath(path);
									updateFilterListGroupView();									
									checkTagsForSelectedNodes();
									tree.repaint();
								}
							}
						}
*****	End of not needed because there is no need to edit		*************/
					} else {
						/*if (!isRightMouse) {
							// have they clicked the folder expand/collpase icon?
							int mouseX = e.getX();
							path = tree.getClosestPathForLocation(mouseX, e.getY());
							if(path != null){
							    int                     boxWidth;
							    Insets                  i = tree.getInsets();
							    BasicTreeUI ui = (BasicTreeUI)tree.getUI();
							    if(ui.getExpandedIcon() != null)
							    	boxWidth = ui.getExpandedIcon().getIconWidth();
							    else
							    	boxWidth = 8;

							    int depthOffset = 0;
								if(tree.isRootVisible()) {
								    if(tree.getShowsRootHandles())
								    	depthOffset = 1;
								    else
								    	depthOffset = 0;
								}
								else if(!tree.getShowsRootHandles())
								    depthOffset = -1;
								else
								    depthOffset = 0;							    
							    int nRowX = (ui.getLeftChildIndent()+ui.getRightChildIndent()) * ((path.getPathCount() - 1) + depthOffset);
							    int boxLeftX = nRowX - ui.getRightChildIndent() - boxWidth / 2;

				                boxLeftX += i.left;
							    int boxRightX = boxLeftX + boxWidth;

							    if (mouseX >= boxLeftX && mouseX <= boxRightX) {
							    	if (tree.isExpanded(path)) {
										tree.collapsePath(path);
									} else {
										tree.expandPath(path);
									}							    	
							    }
							}														
						}*/
					}
     			}
				
     			public void mousePressed(MouseEvent e) {     				     		
					boolean isRightMouse = SwingUtilities.isRightMouseButton(e);
					boolean isLeftMouse = SwingUtilities.isLeftMouseButton(e);
					if (ProjectCompendium.isMac &&
						(e.getButton() == 3 && e.isShiftDown())) {
						isRightMouse = true;
						isLeftMouse = false;
					}
					
	       			TreePath path = tree.getPathForLocation(e.getX(), e.getY());
					
					if (!isRightMouse && path == null) {
						// have they clicked the folder expand/collpase icon?
						int mouseX = e.getX();
						path = tree.getClosestPathForLocation(mouseX, e.getY());
						if(path != null){
						    int                     boxWidth;
						    Insets                  i = tree.getInsets();
						    BasicTreeUI ui = (BasicTreeUI)tree.getUI();
						    if(ui.getExpandedIcon() != null)
						    	boxWidth = ui.getExpandedIcon().getIconWidth();
						    else
						    	boxWidth = 8;

						    int depthOffset = 0;
							if(tree.isRootVisible()) {
							    if(tree.getShowsRootHandles())
							    	depthOffset = 1;
							    else
							    	depthOffset = 0;
							}
							else if(!tree.getShowsRootHandles())
							    depthOffset = -1;
							else
							    depthOffset = 0;							    
						    int nRowX = (ui.getLeftChildIndent()+ui.getRightChildIndent()) * ((path.getPathCount() - 1) + depthOffset);
						    int boxLeftX = nRowX - ui.getRightChildIndent() - boxWidth / 2;

			                boxLeftX += i.left;
						    int boxRightX = boxLeftX + boxWidth;

						    if (mouseX >= boxLeftX && mouseX <= boxRightX) {
						    	if (tree.isExpanded(path)) {
									tree.collapsePath(path);
								} else {
									tree.expandPath(path);
								}							    	
						    }
						}														
					}
     			}
 			};
 			tree.addMouseListener(ml);

 			tree.addKeyListener(new KeyAdapter() { 				
 				
				public void keyPressed(KeyEvent e) {						
					int keyCode = e.getKeyCode();
					int modifiers = e.getModifiers();
					if(keyCode == KeyEvent.VK_DELETE) {
						TreePath path = tree.getSelectionPath();
	     				if (path != null) {
							DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();		
							CheckNode check = (CheckNode)node.getUserObject();
							if(check.getData() instanceof Code) {	
								Code code = (Code)check.getData();									
								onDeleteCode(code);
							}
						}
						e.consume();	     				
			 		}
					else if (keyCode == KeyEvent.VK_ENTER ) {
	             		TreePath path = tree.getSelectionPath();
	     				if (path != null) {
		  					int row = tree.getRowForPath(path);
							boolean isSelected = tree.isPathSelected(path);
							if (isSelected) {
								tree.startEditingAtPath(path);
							} else {
								if (modifiers != KeyEvent.VK_CONTROL) {
									tree.clearSelection();	
								}										
								if (tree.isRowSelected(row)) {
									tree.removeSelectionRow(row);
								} else {
									tree.addSelectionRow(row);											
								}
								tree.repaint();
							}
						}					
					} else if (keyCode == KeyEvent.VK_SPACE && modifiers == 0) {
						TreePath path = tree.getSelectionPath();
	     				if (path != null) {
							clearChecks();
							tree.validate();
							tree.repaint();											
							updateFilterListView();
						}					
	     			} else if (keyCode == KeyEvent.VK_SPACE && modifiers != KeyEvent.VK_SHIFT) {
						TreePath path = null;
						DefaultMutableTreeNode thenode = null;
						CheckNode check = null;
						Code code =  null;
						boolean updateFilterList = false;
						TreePath[] paths = tree.getSelectionPaths();
			/*******  Not needed - not adding or deleting codes/tags 
						if (oWorkingList.getList().getSelectedRowCount() > 0) {
	     					int count = paths.length;
	     					for (int i=0; i<count; i++) {
	     						path = paths[i];
			  					thenode = (DefaultMutableTreeNode)path.getLastPathComponent();
								check = (CheckNode)thenode.getUserObject();								
								if (check.getData() instanceof Code) {
									code = (Code)check.getData();			     					
									check.setChecked(!check.isChecked());
									if (check.isChecked()) {
										onAddCodeToNodes(code);
									} else {
										onRemoveCodeFromNodes(code);
										updateFilterList = true;
									}
									tree.repaint();		
								}
	     					}
	     					
							checkTagsForSelectedNodes();
							if (isFilter && updateFilterList) {
								updateFilterListView();
							}												
						} else {
							ProjectCompendium.APP.displayMessage("Please select some nodes to assign tags to first.", "Tags");
						}
			**********/
	     			}
	     			
				}
            });            
		}

		sp = new JScrollPane(tree);		
		Dimension size = tree.getPreferredSize();		
		sp.setPreferredSize(new Dimension(250, size.height));
		return sp;
    }
	
	/**
	 * Create and draw the panel contents.
	 */
	private void showCodePanel() {
		
		setLayout(new BorderLayout());
		setFont(ProjectCompendium.APP.labelFont);
		
			
		String sOrientation = FormatProperties.getFormatProp("tagsViewOrientation");
		if (sOrientation != null && sOrientation.equals("horizontal")) {
			nOrientation = JSplitPane.HORIZONTAL_SPLIT;
		}
		
		// MAIN PANEL
		sp = createTree();
		sp.addMouseListener(oWorkingList);
		tree.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				oWorkingList.mouseClicked(e);
			}
		});		
			
	}
	
	/**
	 * Create tree of nodes for the tree on the code assinment panel.
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
			HashSet hsLdCodeGroups = ProjectCompendium.APP.getLdTypeTagMaps().getHsLdCodeGroups();
			// ADD CODE GROUPS
			for(Enumeration e = groups.elements();e.hasMoreElements();) {
				Hashtable nextGroup = (Hashtable)e.nextElement();

				if (nextGroup.containsKey("group")) {
					Vector groupdata = (Vector)nextGroup.get("group");
					String sCodeGroupID = (String)groupdata.elementAt(0);
					if (hsLdCodeGroups.contains(sCodeGroupID) )	{
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
			// Not needed for UILdTypeTreePanel

		}
		catch (Exception io) {
			io.printStackTrace();
		}

		this.top = top;
		return top;
	}
}
