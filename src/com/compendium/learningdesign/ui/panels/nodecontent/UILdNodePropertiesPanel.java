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

package com.compendium.learningdesign.ui.panels.nodecontent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.ShortCutNodeSummary;
import com.compendium.core.datamodel.View;
import com.compendium.ui.dialogs.UINodeContentDialog;
import com.compendium.ui.panels.UINodePropertiesPanel;

public class UILdNodePropertiesPanel extends UINodePropertiesPanel {

	public UILdNodePropertiesPanel(JFrame parent, NodePosition nodePos,
			UINodeContentDialog tabbedPane) {
		super(parent, nodePos, tabbedPane);
		// TODO Auto-generated constructor stub
	}

	public UILdNodePropertiesPanel(JFrame parent, NodeSummary node,
			UINodeContentDialog tabbedPane) {
		super(parent, node, tabbedPane);
		// TODO Auto-generated constructor stub
	}

	/** Draw the panel gui elements and initialise the data.*/
	private void drawPanel() {

		setLayout(new BorderLayout());

		grid = new GridBagLayout();
		centerpanel = new JPanel();
		//centerpanel.setLayout(grid);
		centerpanel.setLayout( (new BorderLayout()) );

		/*GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.BOTH;
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx = 0;
		gc.weightx=1;*/

		showCommonProperties();
		/*gc.gridy = 0;
		if( (!(oNode instanceof ShortCutNodeSummary)) && (!(oNode instanceof View)) )
			gc.weighty=1;
		grid.setConstraints(mainpanel, gc);*/
		centerpanel.add(mainpanel, BorderLayout.NORTH);

		//gc.weighty=0;

		JPanel oBottomPanel = new JPanel(new BorderLayout());
		oBottomPanel.add(showReadersPanel(), BorderLayout.WEST);
		
		JPanel oInnerBottomPanel = new JPanel(new BorderLayout());
		
		if(oNode instanceof ShortCutNodeSummary) {
			showShortCutNodeEditPanel();
			//gc.gridy = 1;
			//if( !(oNode instanceof View) )
			//	gc.weighty=1;
			//grid.setConstraints(shortspanel, gc);
			oInnerBottomPanel.add(shortspanel, BorderLayout.CENTER);
		} else if(oNode instanceof View) {
			View view = ((View)oNode);
			if (!view.isMembersInitialized()) {
				try {
					view.initializeMembers();
				}
				catch(Exception ex) {
					ProjectCompendium.APP.displayError("Error: (UINodePropertiesPanel) Unable to get view data\n\n"+ex.getMessage());
				}
			}
			showViewProperties(view);
			//gc.gridy = 2;
			//if( !(oNode instanceof ShortCutNodeSummary) )
			//	gc.weighty=1;
			//grid.setConstraints(southpanel, gc);

			oInnerBottomPanel.add(southpanel, BorderLayout.CENTER);
		}

		oBottomPanel.add(oInnerBottomPanel, BorderLayout.EAST);
		centerpanel.add(oBottomPanel, BorderLayout.CENTER);
		
		add(centerpanel, BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);
		
		if(oNode.getState() == ICoreConstants.READSTATE){
			lblStateInfo2.setText("Read");
		} else if(oNode.getState() == ICoreConstants.UNREADSTATE){
			lblStateInfo2.setText("Unread");
		} else if(oNode.getState() == ICoreConstants.MODIFIEDSTATE){
			lblStateInfo2.setText("Modified");
		}
		
		lblModifiedBy2.setText(oNode.getLastModificationAuthor());		
	}
	
	/**
	 * Draw the panel of readers.
	 */
	private JPanel showReadersPanel() {
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new TitledBorder(new EtchedBorder(),
                "Node has been read by",
                TitledBorder.LEFT,
                TitledBorder.TOP,
				new Font("Dialog", Font.BOLD, 12) ));
		
		taReaders = new JTextArea("");
		taReaders.setFont(new Font("Monospaced", Font.PLAIN, 12));
		updateReadersInformation();
		taReaders.setEditable(false);

		JScrollPane scrollpane = new JScrollPane(taReaders);
		scrollpane.setPreferredSize(new Dimension(200,100));
		panel.add(scrollpane, BorderLayout.CENTER);
		
		return panel;
	}
	
	/**
	 * Draw the panel of additional data for map and list view nodes.
	 * @param view com.compendium.core.datamodel.View, the view to draw the data for.
	 */
	private void showViewProperties(View view) {

		String type = "Map";
		if (view.getType() == ICoreConstants.LISTVIEW)
			type="List";

		southpanel = new JPanel();
		southpanel.setBorder(new TitledBorder(new EtchedBorder(),
                    (type+" Contents"),
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
					new Font("Dialog", Font.BOLD, 12) ));

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		southpanel.setLayout(gb);
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		JLabel lblCount = new JLabel("Number of Nodes:");
		gc.gridy = 0;
		gc.gridx = 0;
		gb.setConstraints(lblCount, gc);
		southpanel.add(lblCount);

		lblCount = new JLabel(String.valueOf(view.getNumberOfNodes()));
		gc.gridy = 0;
		gc.gridx = 1;
		gc.weightx=1.0;
		gb.setConstraints(lblCount, gc);
		southpanel.add(lblCount);

		JLabel lblTypes = new JLabel("Types :");
		gc.gridy = 1;
		gc.gridx = 0;
		gc.gridwidth = 2;
		gb.setConstraints(lblTypes, gc);
		southpanel.add(lblTypes);

		taTypes = new JTextArea("");
		taTypes.setFont(new Font("Monospaced", Font.PLAIN, 12));
		updateTypesInformation(view);
		taTypes.setEditable(false);

		JScrollPane scrollpane = new JScrollPane(taTypes);
		scrollpane.setPreferredSize(new Dimension(200,100));
		gc.gridy = 2;
		gc.gridx = 0;
		gc.gridheight=4;
		gc.weighty=500;		
		gc.anchor = GridBagConstraints.NORTH;
		gb.setConstraints(scrollpane, gc);

		southpanel.add(scrollpane);
	}
	
	/**
	 * Draw the panel of additional property data for shortcuts.
	 */
	private void showShortCutNodeEditPanel() {

		// COPIED OVER FROM THE EDIT, BUT WAS NOT BEING USED
		//else if (source == pbShortCut) {
		//	UINodeTabbedPane dialog = new UINodeTabbedPane(ProjectCompendium.APP,(NodeSummary)((ShortCutNodeSummary)oNode).getReferredNode(), UINodeTabbedPane.CONTENTS_TAB);
		//	dialog.setVisible(true);
		//}

		shortspanel = new JPanel();
		shortspanel.setBorder(new TitledBorder(new EtchedBorder(),
                    "Shortcut To Node",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
					new Font("Dialog", Font.BOLD, 12) ));

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		shortspanel.setLayout(gb);
		gc.insets = new Insets(5,5,5,5);
		gc.anchor = GridBagConstraints.WEST;

		pbShortCut = new JLabel("Label:");
		gc.gridy = 0;
		gc.gridx = 0;
		gb.setConstraints(pbShortCut, gc);
		shortspanel.add(pbShortCut);

		NodeSummary referredNode = ((ShortCutNodeSummary)oNode).getReferredNode();
		String referredNodeLabel = "";
		if(referredNode != null)
			referredNodeLabel = referredNode.getLabel();

		JTextArea tfLabel = new JTextArea(referredNodeLabel);
		tfLabel.setEditable(false);
		tfLabel.setLineWrap(true);
		tfLabel.setWrapStyleWord(true);
		tfLabel.setAutoscrolls(true);

		JScrollPane scrollpane = new JScrollPane(tfLabel);
		scrollpane.setPreferredSize(new Dimension(200,70));
		gc.gridx = 1;
		gc.weightx=1.0;
		gb.setConstraints(scrollpane, gc);
		shortspanel.add(scrollpane);

		JLabel label = new JLabel("Node ID:");
		gc.gridy = 1;
		gc.gridx = 0;
		gc.weightx=0;
		gc.weighty=1.0;
		gb.setConstraints(label, gc);
		shortspanel.add(label);

		String referredNodeID = "";
		if(referredNode != null)
			referredNodeID = referredNode.getId();

		JTextField tfNodeID = new JTextField(referredNodeID);
		gc.gridx = 1;
		gc.weightx=1.0;
		gc.weighty=100;
		gb.setConstraints(tfNodeID, gc);
		tfNodeID.setEditable(false);
		shortspanel.add(tfNodeID);
	}
	
	/**
	 * Draw the panel of additional data for map and list view nodes.
	 * @param view com.compendium.core.datamodel.View, the view to draw the data for.
	 */
	/** This method to be completed
	private void showImageProperties() {
		int nodeType;
		nodeType = oNode.getType();
		JPanel imagePanel;
		imagePanel = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		if(nodeType == ICoreConstants.MAPVIEW) {

			gc.fill = GridBagConstraints.NONE;
			gc.anchor = GridBagConstraints.WEST;
			gc.gridwidth = 1;

			JLabel lblBackground = new JLabel("Background Image:");
			gc.gridy = y;
			gc.gridx = 0;
			gb.setConstraints(lblBackground, gc);
			centerpanel.add(lblBackground);

			if (oNode instanceof View) {
				ViewLayer layer  = ((View)oNode).getViewLayer();
				if (layer == null) {
					try { ((View)oNode).initializeMembers();
						sBackground = layer.getBackground();
					}
					catch(Exception ex) {
						sBackground = "";
					}
				}
				else {
					sBackground = layer.getBackground();
				}
			}

			txtBackground = new JTextField(sBackground);
			txtBackground.setFont(new Font("Dialog", Font.PLAIN, 12));
			txtBackground.setColumns(23);
			txtBackground.setMargin(new Insets(2,2,2,2));
			txtBackground.setSize(txtBackground.getPreferredSize());
			gc.gridy = y;
			gc.gridx = 1;
			gc.fill=GridBagConstraints.HORIZONTAL;
			gc.weightx=4.0;
			gb.setConstraints(txtBackground, gc);
			centerpanel.add(txtBackground);

			oBackgroundDoc = txtBackground.getDocument();
			oBackgroundDoc.addDocumentListener(this);

			// other initializations
			fdgBrowse3 = new JFileChooser();
			fdgBrowse3.setDialogTitle("Choose a background image");
			UIUtilities.centerComponent(fdgBrowse3, ProjectCompendium.APP);
			UIFileFilter filter2 = new UIFileFilter(new String[] {"gif","jpg","jpeg","png"}, "Image Files");
			fdgBrowse3.setFileFilter(filter2);

			pbBrowse3 = new UIButton("./.");
			pbBrowse3.setFont(new Font("Dialog", Font.BOLD, 14));
			pbBrowse3.setMargin(new Insets(0,0,0,0));
			pbBrowse3.setToolTipText("Browse");
			pbBrowse3.addActionListener(this);
			gc.gridy = y;
			gc.gridx = 2;
			gc.weightx=0.0;
			gc.fill=GridBagConstraints.NONE;
			gc.gridwidth = 1;
			gb.setConstraints(pbBrowse3, gc);
			centerpanel.add(pbBrowse3);

			pbView2 = new UIButton("View");
			if (sBackground == null || sBackground.equals(""))
				pbView2.setEnabled(false);

			pbView2.setMnemonic(KeyEvent.VK_I);
			gc.gridy = y;
			gc.gridx = 3;
			gb.setConstraints(pbView2, gc);
			pbView2.addActionListener(this);
			centerpanel.add(pbView2);

			y++;
		}
				
		if(nodeType == ICoreConstants.REFERENCE || nodeType == ICoreConstants.REFERENCE_SHORTCUT
				|| nodeType == ICoreConstants.MAPVIEW || nodeType == ICoreConstants.MAP_SHORTCUT ||
					nodeType == ICoreConstants.LISTVIEW || nodeType == ICoreConstants.LIST_SHORTCUT){
			
			lblImage = new JLabel("Icon Image:");
			gc.gridy = y;
			gc.gridx = 0;
			gb.setConstraints(lblImage, gc);
			centerpanel.add(lblImage);

			sImage = oNode.getImage();

			txtImage = new JTextField(sImage);
			txtImage.setFont(new Font("Dialog", Font.PLAIN, 12));
			txtImage.setColumns(23);
			txtImage.setMargin(new Insets(2,2,2,2));
			txtImage.setSize(txtImage.getPreferredSize());
			gc.gridy = y;
			gc.gridx = 1;
			gc.fill=GridBagConstraints.HORIZONTAL;
			gc.weightx=4.0;
			gb.setConstraints(txtImage, gc);
			centerpanel.add(txtImage);

			oImageDoc = txtImage.getDocument();
			oImageDoc.addDocumentListener(this);

			// other initializations
			fdgBrowse2 = new JFileChooser();
			fdgBrowse2.setDialogTitle("Choose an image");
			UIUtilities.centerComponent(fdgBrowse2, ProjectCompendium.APP);
			UIFileFilter filter = new UIFileFilter(new String[] {"gif","jpg","jpeg","png"}, "Image Files");
			fdgBrowse2.setFileFilter(filter);

			pbBrowse2 = new UIButton("./.");
			pbBrowse2.setFont(new Font("Dialog", Font.BOLD, 14));
			pbBrowse2.setMargin(new Insets(0,0,0,0));
			pbBrowse2.setToolTipText("Browse");
			pbBrowse2.addActionListener(this);
			gc.gridy = y;
			gc.gridx = 2;
			gc.weightx=0.0;
			gc.fill=GridBagConstraints.NONE;
			gc.gridwidth = 1;
			gb.setConstraints(pbBrowse2, gc);
			centerpanel.add(pbBrowse2);

			pbView = new UIButton("View");
			pbView.setMnemonic(KeyEvent.VK_V);
			gc.gridy = y;
			gc.gridx = 3;
			gb.setConstraints(pbView, gc);
			pbView.addActionListener(this);
			centerpanel.add(pbView);
		
			y++;						
			
			JPanel sizePanel = new JPanel();
			gc.gridy = y;
			gc.gridx = 1;
			gc.gridwidth=2;
			gb.setConstraints(sizePanel, gc);
			centerpanel.add(sizePanel);
			
			pbThumbNail = new JRadioButton("Display as Thumbnail");
			pbThumbNail.addItemListener(this);			
			sizePanel.add(pbThumbNail);			

			pbActualSize = new JRadioButton("Actual Size");
			pbActualSize.addItemListener(this);
			sizePanel.add(pbActualSize);

			pbSpecifiedSize = new JRadioButton("Specified Size");
			pbSpecifiedSize.addItemListener(this);
			sizePanel.add(pbSpecifiedSize);			

			ButtonGroup group = new ButtonGroup();
			group.add(pbThumbNail);
			group.add(pbActualSize);
			group.add(pbSpecifiedSize);
			
			pbSize = new UIButton("Specify");
			pbSize.setToolTipText("Specify the image size to be used.");
			pbSize.addActionListener(this);		
			
			if (sImage == null || sImage.equals("")) {
				pbView.setEnabled(false);
				pbThumbNail.setEnabled(false);
				pbActualSize.setEnabled(false);
				pbSpecifiedSize	.setEnabled(false);			
			}
			
			gc.gridy = y;
			gc.gridx = 3;
			gc.gridwidth=1;
			gb.setConstraints(pbSize, gc);
			centerpanel.add(pbSize);			
		}
		
	}
	
	**/
}
