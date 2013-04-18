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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.NodeDetailPage;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.View;
import com.compendium.core.datamodel.ViewLayer;
import com.compendium.ui.ProjectCompendiumFrame;
import com.compendium.ui.UIButton;
import com.compendium.ui.UIFileFilter;
import com.compendium.ui.UIImages;
import com.compendium.ui.UINode;
import com.compendium.ui.UITextArea;
import com.compendium.ui.UIUtilities;
import com.compendium.ui.dialogs.UINodeContentDialog;
import com.compendium.ui.panels.UINodeEditPanel;
import com.compendium.ui.panels.UITimePanel;

public class UILdNodeEditPanel extends UINodeEditPanel {

	/**
	 * @param parent
	 * @param node
	 * @param tabbedPane
	 */
	public UILdNodeEditPanel(JFrame parent, NodeSummary node,
			UINodeContentDialog tabbedPane) {
		super(parent, node, tabbedPane);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param parent
	 * @param uinode
	 * @param tabbedPane
	 */
	public UILdNodeEditPanel(JFrame parent, UINode uinode,
			UINodeContentDialog tabbedPane) {
		super(parent, uinode, tabbedPane);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Draw the contents of the panel.
	 */
	protected void showNodeEditPanel() {

		centerpanel = new JPanel();
		centerpanel.setBorder(new EmptyBorder(5,5,5,5));

		gb = new GridBagLayout();
		gc = new GridBagConstraints();
		centerpanel.setLayout(gb);
		gc.insets = new Insets(3,3,3,3);
		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.weightx=1;
		gc.weighty=1;

		JPanel labelPanel = new JPanel(new BorderLayout());

		font = ProjectCompendiumFrame.labelFont;
		int scale = ProjectCompendium.APP.getToolBarManager().getTextZoom();
		font = new Font(font.getName(), font.getStyle(), font.getSize()+ scale);		


		// WHEN NODE LEVEL FONT SETTING IS INTRODUCED, WILL NEED TO DO THIS
		/*if (oUINode != null) {
			double scale = oUINode.getScale();
			if (scale != 0.0 && scale != 1.0) {
				font = oUINode.getFont();
				Point p1 = UIUtilities.scalePoint(font.getSize(), font.getSize(), scale);
				Font font2 = new Font(font.getName() , font.getStyle(), p1.x);
				font = font2;
			}
		}*/

		txtLabel = new UITextArea(50, 20);
		txtLabel.setFont(font);
		txtLabel.setAutoscrolls(true);
		txtLabel.addFocusListener( new FocusListener() {
        	public void focusGained(FocusEvent e) {
				txtLabel.setCaretPosition(txtLabel.getCaretPosition());
 			}
            public void focusLost(FocusEvent e) {}
		});
		txtLabel.addKeyListener( new KeyAdapter() {
        	public void keyPressed(KeyEvent e) {
        		int keyCode = e.getKeyCode();
        		int modifiers = e.getModifiers();
     			if (modifiers == java.awt.Event.SHIFT_MASK && keyCode == KeyEvent.VK_TAB) {
     				txtDetail.requestFocus();
     				txtDetail.setCaretPosition(0);
     			}
 			}
		});

		if (oNode.getId().equals(ProjectCompendium.APP.getInBoxID())) {
			txtLabel.setEditable(false);
		}
		
		scrollpane2 = new JScrollPane(txtLabel);
		scrollpane2.setPreferredSize(new Dimension(500,70));
		labelPanel.add(scrollpane2, BorderLayout.CENTER);

		gc.gridy = 0;
		gc.gridx = 0;
		gc.gridwidth=4;
		gc.weighty=100;
		gc.fill = GridBagConstraints.BOTH;
		gb.setConstraints(labelPanel, gc);
		centerpanel.add(labelPanel);

		gc.weighty=1;
		gc.fill = GridBagConstraints.NONE;

		oLabelDoc = txtLabel.getDocument();
		oLabelDoc.addDocumentListener(this);

		pageLabel = new JLabel();
		datePanel = new UITimePanel("Entered: ");
		modLabel = new JLabel();

		infopanel = new JPanel();

		JPanel inner = new JPanel();
		inner.setBackground(Color.white);
		inner.add(pageLabel);
		infopanel.add(inner);

		inner = new JPanel();
		inner.setBackground(Color.white);
		inner.add(modLabel);
		infopanel.add(inner);

		NodeDetailPage page = (NodeDetailPage)detailPages.elementAt(0);
		createInfo(page);
		gc.gridy = 1;
		gc.gridx = 0;
		gc.gridwidth = 4;
		gb.setConstraints(infopanel, gc);
		centerpanel.add(infopanel);

		JToolBar tool = createToolBar();
		gc.gridy = 2;
		gc.gridx = 0;
		gc.gridwidth = 4;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.WEST;
		gb.setConstraints(tool, gc);
		centerpanel.add(tool);

		txtDetail = new UITextArea(50, 50);
		txtDetail.setFont(font);

		txtDetail.setAutoscrolls(true);
		txtDetail.addFocusListener( new FocusListener() {
        	public void focusGained(FocusEvent e) {
				if (firstFocus) {
	 				txtDetail.setCaretPosition(txtDetail.getText().length());
					firstFocus = false;
				}
			}
            public void focusLost(FocusEvent e) {}
		});
		txtDetail.addKeyListener( new KeyAdapter() {
        	public void keyPressed(KeyEvent e) {
        		int keyCode = e.getKeyCode();
        		int modifiers = e.getModifiers();
     			if (modifiers == java.awt.Event.SHIFT_MASK && keyCode == KeyEvent.VK_TAB) {
     				txtLabel.requestFocus();
     				txtLabel.setCaretPosition(0);
     			}
 			}
		});

		scrollpane = new JScrollPane(txtDetail);
		scrollpane.setPreferredSize(new Dimension(500,200));

		editPanel = new JPanel(new BorderLayout());
		editPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

		editPanel.add(datePanel, BorderLayout.NORTH);
		editPanel.add(scrollpane, BorderLayout.CENTER);

		gc.gridy = 3;
		gc.gridx = 0;
		gc.gridwidth=4;
		gc.weighty=100;
		gc.fill = GridBagConstraints.BOTH;
		gb.setConstraints(editPanel, gc);
		centerpanel.add(editPanel);

		gc.weighty=1;

		oDetailDoc = txtDetail.getDocument();
		oDetailDoc.addDocumentListener(this);

		int y=4;

		nodeType = oNode.getType();

		if(nodeType == ICoreConstants.REFERENCE || nodeType == ICoreConstants.REFERENCE_SHORTCUT) {
			gc.fill = GridBagConstraints.NONE;
			gc.anchor = GridBagConstraints.WEST;

			lblReference = new JLabel("Ref:");
			gc.gridy = y;
			gc.gridx = 0;
			gc.gridwidth = 1;
			gb.setConstraints(lblReference, gc);
			centerpanel.add(lblReference);

			sReference = oNode.getSource();

			txtReference = new JTextField(sReference);
			txtReference.setFont(new Font("Dialog", Font.PLAIN, 12));
			txtReference.setColumns(23);
			txtReference.setMargin(new Insets(2,2,2,2));
			txtReference.setSize(txtReference.getPreferredSize());
			gc.gridy = y;
			gc.gridx = 1;
			gc.fill=GridBagConstraints.HORIZONTAL;
			gc.weightx=4.0;
			gb.setConstraints(txtReference, gc);
			centerpanel.add(txtReference);

			oRefDoc = txtReference.getDocument();
			oRefDoc.addDocumentListener(this);

			// other initializations
			fdgBrowse = new JFileChooser();
			fdgBrowse.setDialogTitle("Choose a reference");
			UIUtilities.centerComponent(fdgBrowse, ProjectCompendium.APP);

			pbBrowse = new UIButton("./.");
			pbBrowse.setFont(new Font("Dialog", Font.BOLD, 14));
			pbBrowse.setMargin(new Insets(0,0,0,0));
			pbBrowse.setToolTipText("Browse");
			pbBrowse.addActionListener(this);
			gc.gridy = y;
			gc.gridx = 2;
			gc.fill=GridBagConstraints.NONE;
			gc.weightx=0.0;
			gc.gridwidth = 1;
			gb.setConstraints(pbBrowse, gc);
			centerpanel.add(pbBrowse);

			pbExecute = new UIButton("Launch");
			if (sReference == null || sReference.equals(""))
				pbExecute.setEnabled(false);

			pbExecute.setMnemonic(KeyEvent.VK_L);
			gc.gridy = y;
			gc.gridx = 3;
			gb.setConstraints(pbExecute, gc);
			pbExecute.addActionListener(this);
			centerpanel.add(pbExecute);

			y++;
			
		} 
		else if(nodeType == ICoreConstants.MAPVIEW) {
			/** This section is needed to initialise the members and make sure the 
			 * viewlayer is not initially null 	**/ 
			if (oNode instanceof View) {
				ViewLayer layer  = ((View)oNode).getViewLayer();
				if (layer == null) {
					try { ((View)oNode).initializeMembers();
						// Need to set layer again because it was null 
						layer  = ((View)oNode).getViewLayer();
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

		}

		setNode(oNode);

		//reset the flag to false as the label, detail and/or reference info is taken from the DB for the first time
		bLabelChange = false;
		bDetailChange = false;
		bRefChange = false;
		bImageChange = false;
		bBackgroundChange = false;

		add(centerpanel, BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);
	}
	
	/**
	 * Update the displayed data for the given node.
	 * This version differs from the superclass version in that the node images are not editable
	 * using this panel.
	 * @param node com.compendium.core.datamodel.NodeSummary, the node to diaply the data for.
	 */
	
	protected void setNode(NodeSummary node) {

		if (pbThumbNail != null) {
			pbThumbNail.setSelected(true);
		}
		oNode = node;
		if (node != null) {
			try {
				String label = node.getLabel();
				if(label.equals(ICoreConstants.NOLABEL_STRING))
					label = "";
				txtLabel.setText( label );

				String detail = ((NodeDetailPage)detailPages.elementAt(0)).getText();
				if(detail.equals(ICoreConstants.NODETAIL_STRING))
					detail = "";
				txtDetail.setText( detail );

				if(nodeType == ICoreConstants.REFERENCE || nodeType == ICoreConstants.REFERENCE_SHORTCUT) {
					txtReference.setText(oNode.getSource());
				}
				/** Not setting images using this panel therefore do not need txtBackground
				 * or txtImage	**/
				/**
				else if (nodeType == ICoreConstants.MAPVIEW) {
					txtBackground.setText(((View)oNode).getViewLayer().getBackground());					
				}
				
				if (nodeType == ICoreConstants.REFERENCE || nodeType == ICoreConstants.REFERENCE_SHORTCUT ||
						nodeType == ICoreConstants.MAPVIEW || nodeType == ICoreConstants.MAP_SHORTCUT ||
						nodeType == ICoreConstants.LISTVIEW || nodeType == ICoreConstants.LIST_SHORTCUT) {

					txtImage.setText(oNode.getImage());
					String imageRef = oNode.getImage();
					oLastImageSize = node.getImageSize();					
					if (oLastImageSize.width > 0 && oLastImageSize.height > 0 && !imageRef.equals("")) {
						if ( UIImages.isImage(imageRef) ) {
							ImageIcon originalSizeImage = UIImages.createImageIcon(imageRef); 								
							if (originalSizeImage != null) {
								Image originalIcon = originalSizeImage.getImage();
								int originalWidth = originalIcon.getWidth(null);
								int originalHeight = originalIcon.getHeight(null);
								oActualImageSize = new Dimension(originalWidth, originalHeight);															
								if (oLastImageSize.width == originalWidth && oLastImageSize.height == originalHeight) {
									this.pbActualSize.setSelected(true);
								} else {
									this.pbSpecifiedSize.setSelected(true);
								}
							}							
						}
					}					
				}
***********************************************/
			}
			catch (Exception e) {
				e.printStackTrace();
				ProjectCompendium.APP.displayError("Exception: (UINodeEditPanel.setNode) \n"+e.getMessage() 
						+ "\n View Layer id = " + ((View)oNode).getViewLayer().getViewID()
						+ "\n Background image = " + ((View)oNode).getViewLayer().getBackground());
			}
		}
	}



}
