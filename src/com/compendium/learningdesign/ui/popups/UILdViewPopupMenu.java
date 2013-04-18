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

package com.compendium.learningdesign.ui.popups;

import java.awt.event.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;

import com.compendium.LanguageProperties;
import com.compendium.ProjectCompendium;
import com.compendium.ui.FormatProperties;
import com.compendium.ui.IUIArrange;
import com.compendium.ui.IUIConstants;
import com.compendium.ui.UIAlign;
import com.compendium.ui.UIImages;
import com.compendium.ui.UILink;
import com.compendium.ui.UINode;
import com.compendium.ui.UIUtilities;
import com.compendium.ui.dialogs.UIImportFlashMeetingXMLDialog;
import com.compendium.ui.plaf.NodeUI;
import com.compendium.ui.plaf.ViewPaneUI;
import com.compendium.ui.popups.UIViewPopupMenu;
import com.compendium.ui.stencils.UIStencilDialog;

import com.compendium.core.CoreCalendar;
import com.compendium.core.CoreUtilities;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.Code;
import com.compendium.core.datamodel.IModel;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.View;
import com.compendium.io.udig.UDigClientSocket;
import com.compendium.learningdesign.ui.UILdViewPane;

public class UILdViewPopupMenu extends UIViewPopupMenu implements ActionListener, ItemListener {
	/** The default width for this popup menu.*/
	private static final int WIDTH		= 100;

	/** The default height for this popup menu.*/
	private static final int HEIGHT		= 316;
	
	/** The JMenuItem to change the selected nodes to Decision nodes.*/
	private JMenuItem		miMenuItemShowTimes		= null;
	
	/** * The menu item to paste any node and links on the clipbaord into the current view as transclusions. */
	private JMenuItem			miEditPasteSpecial				= null;
	/**
	 * @param title
	 * @param viewpaneUI
	 */
	public UILdViewPopupMenu(String title, ViewPaneUI viewpaneUI) {
/**		super(title, viewpaneUI);
		this.setViewPane(viewpaneUI.getViewPane());	*/
		/** super class stuff ***/
		super(title);
		this.setViewPane(viewpaneUI.getViewPane());
		setViewPaneUI(viewpaneUI);

		View view = viewpaneUI.getViewPane().getView();
		Vector refNodes = view.getReferenceNodes();

		shortcutKey = ProjectCompendium.APP.shortcutKey;

// Begin edit, Lakshmi (11/3/05)
// include Top - Down and Left - Right Option in Arrange Menu.
		mnuArrange = new JMenu("Arrange");
		mnuArrange.setMnemonic(KeyEvent.VK_R);
		mnuArrange.addActionListener(this);

		miMenuItemLeftRightArrange = new JMenuItem("Left To Right");
		miMenuItemLeftRightArrange.addActionListener(this);
		miMenuItemLeftRightArrange.setMnemonic(KeyEvent.VK_R);
		mnuArrange.add(miMenuItemLeftRightArrange);

		mnuArrange.addSeparator();

		miMenuItemTopDownArrange = new JMenuItem("Top-Down");
		miMenuItemTopDownArrange.addActionListener(this);
		miMenuItemTopDownArrange.setMnemonic(KeyEvent.VK_W);
		mnuArrange.add(miMenuItemTopDownArrange);

		add(mnuArrange);

		mnuViewAlign = new JMenu("Align");
		mnuViewAlign.setMnemonic(KeyEvent.VK_A);
		mnuViewAlign.setEnabled(true);

		miMenuItemAlignLeft = new JMenuItem("Left");
		miMenuItemAlignLeft.addActionListener(this);
		miMenuItemAlignLeft.setMnemonic(KeyEvent.VK_L);
		mnuViewAlign.add(miMenuItemAlignLeft);

		miMenuItemAlignCenter = new JMenuItem("Center");
		miMenuItemAlignCenter.addActionListener(this);
		miMenuItemAlignCenter.setMnemonic(KeyEvent.VK_C);
		mnuViewAlign.add(miMenuItemAlignCenter);

		miMenuItemAlignRight = new JMenuItem("Right");
		miMenuItemAlignRight.addActionListener(this);
		miMenuItemAlignRight.setMnemonic(KeyEvent.VK_R);
		mnuViewAlign.add(miMenuItemAlignRight);

		mnuViewAlign.addSeparator();

		miMenuItemAlignTop = new JMenuItem("Top");
		miMenuItemAlignTop.addActionListener(this);
		miMenuItemAlignTop.setMnemonic(KeyEvent.VK_T);
		mnuViewAlign.add(miMenuItemAlignTop);

		miMenuItemAlignMiddle = new JMenuItem("Middle");
		miMenuItemAlignMiddle.addActionListener(this);
		miMenuItemAlignMiddle.setMnemonic(KeyEvent.VK_M);
		mnuViewAlign.add(miMenuItemAlignMiddle);

		miMenuItemAlignBottom = new JMenuItem("Bottom");
		miMenuItemAlignBottom.addActionListener(this);
		miMenuItemAlignBottom.setMnemonic(KeyEvent.VK_B);
		mnuViewAlign.add(miMenuItemAlignBottom);

		add(mnuViewAlign);

		addSeparator();

		
		miMenuItemCopy = new JMenuItem("Copy", UIImages.get(IUIConstants.COPY_ICON));
		miMenuItemCopy.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_C, shortcutKey));
		miMenuItemCopy.addActionListener(this);
		miMenuItemCopy.setMnemonic(KeyEvent.VK_C);
		add(miMenuItemCopy);
		
		miMenuItemCut = new JMenuItem("Cut", UIImages.get(IUIConstants.CUT_ICON));
		miMenuItemCut.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_X, shortcutKey));
		miMenuItemCut.addActionListener(this);
		miMenuItemCut.setMnemonic(KeyEvent.VK_U);
		add(miMenuItemCut);

		miMenuItemPaste = new JMenuItem("Paste", UIImages.get(IUIConstants.PASTE_ICON));
		miMenuItemPaste.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_V, shortcutKey));
		miMenuItemPaste.addActionListener(this);
		miMenuItemPaste.setMnemonic(KeyEvent.VK_A);
		add(miMenuItemPaste);
		
		miEditPasteSpecial = new JMenuItem(LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.pasteSpecial"));  //$NON-NLS-1$
		miEditPasteSpecial.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_V, InputEvent.SHIFT_MASK +InputEvent.CTRL_MASK ));
		miEditPasteSpecial.setMnemonic((LanguageProperties.getString(LanguageProperties.MENUS_BUNDLE, "UIMenuEdit.pasteSpecialMnemonic")).charAt(0)); //$NON-NLS-1$
		miEditPasteSpecial.addActionListener(this);
		add(miEditPasteSpecial);

		addSeparator();
		
		// create IMPORT options
		mnuImport = new JMenu("Import");
		mnuImport.setMnemonic(KeyEvent.VK_I);

		miImportXMLView = new JMenuItem("XML File...");
		miImportXMLView.setMnemonic(KeyEvent.VK_X);
		miImportXMLView.addActionListener(this);
		mnuImport.add(miImportXMLView);

		miImportXMLFlashmeeting = new JMenuItem("FlashMeeting XML...");
		miImportXMLFlashmeeting.setMnemonic(KeyEvent.VK_F);
		miImportXMLFlashmeeting.addActionListener(this);
		mnuImport.add(miImportXMLFlashmeeting);

		miFileImport = new JMenu("Questmap File...");
		miFileImport.setMnemonic(KeyEvent.VK_Q);
		miFileImport.addActionListener(this);

		miImportCurrentView = new JMenuItem("Current View..");
		miImportCurrentView.setMnemonic(KeyEvent.VK_C);
		miImportCurrentView.addActionListener(this);
		miFileImport.add(miImportCurrentView);

		miImportMultipleViews = new JMenuItem("Multiple Views..");
		miImportMultipleViews.setMnemonic(KeyEvent.VK_M);
		miImportMultipleViews.addActionListener(this);
		miFileImport.add(miImportMultipleViews);

		mnuImport.add(miFileImport);

		miImportImageFolder = new JMenuItem("Image Folder Into Current Map...");
		miImportImageFolder.setMnemonic(KeyEvent.VK_I);
		miImportImageFolder.addActionListener(this);
		mnuImport.add(miImportImageFolder);

		add(mnuImport);

		// create EXPORT options
		mnuExport = new JMenu("Export");
		mnuExport.setMnemonic(KeyEvent.VK_X);

		miExportXMLView = new JMenuItem("XML File...");
		miExportXMLView.setMnemonic(KeyEvent.VK_X);
		miExportXMLView.addActionListener(this);
		mnuExport.add(miExportXMLView);
				
		miExportHTMLOutline = new JMenuItem("Web Outline...");
		miExportHTMLOutline.setMnemonic(KeyEvent.VK_O);
		miExportHTMLOutline.addActionListener(this);
		mnuExport.add(miExportHTMLOutline);

		miExportHTMLView = new JMenuItem("Web Maps...");
		miExportHTMLView.setMnemonic(KeyEvent.VK_M);
		miExportHTMLView.addActionListener(this);
		mnuExport.add(miExportHTMLView);

		miExportHTMLViewXML = new JMenuItem("Power Export...");
		miExportHTMLViewXML.setToolTipText("Integrated Web Map and Outline Export with XML zip export inlcuded");
		miExportHTMLViewXML.setMnemonic(KeyEvent.VK_P);
		miExportHTMLViewXML.addActionListener(this);
		mnuExport.add(miExportHTMLViewXML);

		miSaveAsJpeg = new JMenuItem("Jpeg File...");
		miSaveAsJpeg.setMnemonic(KeyEvent.VK_J);
		miSaveAsJpeg.addActionListener(this);
		mnuExport.add(miSaveAsJpeg);

		add(mnuExport);
		addSeparator();

		// END IMPORT / EXPORT / SEND OPTIONS

//		pack();
//		setSize(nWidth,nHeight);
		
		
		/** End of super class stuff **/
		
		boolean bCurrentState = ((UILdViewPane)this.getViewPane()).getShowTimingInfo();
		miMenuItemShowTimes = new JCheckBoxMenuItem("Show task times", bCurrentState);
		miMenuItemShowTimes.addItemListener(this);
		add(miMenuItemShowTimes);
		
		pack();
		setSize(WIDTH,HEIGHT);
	}
	
	/**
	 * Handles the event of an option being selected.
	 * @param evt, the event associated with the option being selected.
	 */
	/** Additional actin now done via itemEvevent hence don't need this method *** 
	public void actionPerformed(ActionEvent evt) {
		super.actionPerformed(evt);
		Object source = evt.getSource();

		if(source.equals(miMenuItemShowTimes)) {
			onImportFile(false);
		}
	}
**/
//	@Override
	public void itemStateChanged(ItemEvent e) {
		String newline = "\n";
//		 JMenuItem source = (JMenuItem)(e.getSource());
		 Object source = e.getSource();
	        String s = "Item event detected."
	                   + newline
//	                   + "    Event source: " + source.getText()
	                   + " (an instance of " + source.getClass().toString() + ")"
	                   + newline
	                   + "    New state: "
	                   + ((e.getStateChange() == ItemEvent.SELECTED) ?
	                     "selected":"unselected");
	        
	    if (source.equals(miMenuItemShowTimes))	{
	    	onUpdateShowTaskTimes();
	    }
/*********************
		if (e.getStateChange() == ItemEvent.DESELECTED) {
			((UILdViewPane)this.getViewPane()).setShowTimingInfo(false);
			((UILdViewPane)this.getViewPane()).hideActivityTimes();
		}
		else	{
			((UILdViewPane)this.getViewPane()).setShowTimingInfo(true);
			((UILdViewPane)this.getViewPane()).showActivityTimes();
		}			
*************************/		
	}

	private void onUpdateShowTaskTimes() {
		boolean bShowTimes = miMenuItemShowTimes.isSelected();
		((UILdViewPane)this.getViewPane()).setShowTimingInfo(bShowTimes);			
	}
	
	/**
	 * Clone the currently selected nodes. Copied from Compendium 2.0 code and 
	 * modified a little i.e. use link.getArrow(0 instead of link.getLinkProperties().
	 */
	protected void cloneNodes() {
		int nOffset = 55;

		Hashtable cloneNodes = new Hashtable();
		Vector uinodes = new Vector(50);

		for(Enumeration e = oViewPane.getSelectedNodes();e.hasMoreElements();) {
			UINode uinode = (UINode)e.nextElement();
			NodeUI nodeui = (uinode.getUI());
			ProjectCompendium.APP.setStatus(LanguageProperties.getString(LanguageProperties.POPUPS_BUNDLE, "UIBasePopupMenu.cloning") + nodeui.getUINode().getNode().getLabel()); //$NON-NLS-1$
			int x = uinode.getX();
			int y = uinode.getY();

			UINode tmpuinode = oViewPaneUI.createCloneNode(uinode, x+nOffset, y+nOffset);

			cloneNodes.put(uinode,tmpuinode);
			uinodes.addElement(tmpuinode);
		}

		oViewPane.setSelectedNode(null, ICoreConstants.DESELECTALL);

		for(int i=0;i<uinodes.size();i++) {
			UINode uinode = (UINode)uinodes.elementAt(i);
			uinode.requestFocus();
			uinode.setSelected(true);
			oViewPane.setSelectedNode(uinode,ICoreConstants.MULTISELECT);
		}
		ProjectCompendium.APP.setStatus(""); //$NON-NLS-1$

		Vector linkList = new Vector();
		for(Enumeration e = oViewPane.getSelectedLinks();e.hasMoreElements();) {
			UILink link = (UILink)e.nextElement();
			UINode uiFrom = link.getFromNode();
			UINode uiTo = link.getToNode();
			
			if ((cloneNodes.get(uiFrom) != null) && (cloneNodes.get(uiTo) != null) ) {
				UILink tmpLink = (uiFrom.getUI()).createLink(
							(UINode)cloneNodes.get(uiFrom),
							(UINode)cloneNodes.get(uiTo),
							link.getLink().getType(),
							link.getArrow());
				/*UILink tmpLink = (uiFrom.getUI()).createLink(
							(UINode)cloneNodes.get(uiFrom),
							(UINode)cloneNodes.get(uiTo),
							link.getLink().getType(),
							link.getLinkProperties());
							*/
				linkList.addElement(tmpLink);
			}
		}
		oViewPane.setSelectedLink(null, ICoreConstants.DESELECTALL);

		for(int i=0;i<linkList.size();i++) {
			UILink uiLink = (UILink)linkList.elementAt(i);
			uiLink.setSelected(true);
			oViewPane.setSelectedLink(uiLink,ICoreConstants.MULTISELECT);
		}
	}

	/**
	 * Copy the currently selected nodes. 
	 */
	protected void pasteClones(NodeUI oNodeui)	{
		ViewPaneUI  oViewPaneUI = oViewPane.getUI();
		oViewPaneUI.copyToClipboard(oNodeui);
	}
	
	/**
	 * Handles the event of an option being selected.
	 * @param evt, the event associated with the option being selected.
	 */
	public void actionPerformed(ActionEvent evt) {
		ProjectCompendium.APP.setWaitCursor();

		Object source = evt.getSource();

		if(source.equals(miImportCurrentView)) {
			onImportFile(false);
		}
								
		else if(source.equals(miImportMultipleViews)) {
			onImportFile(true);
		}
		else if(source.equals(miMenuItemCopy)) {
			ProjectCompendium.APP.onEditCopy();
		}
		else if(source.equals(miMenuItemCut)) {
			ProjectCompendium.APP.onEditCut();
		}
		else if(source.equals(miMenuItemPaste)) {
			ProjectCompendium.APP.onEditPaste();
		}
		
		else if(source.equals(miEditPasteSpecial)) {
			ProjectCompendium.APP.onEditPasteSpecial();
		}
// begin edit, Lakshmi (11/3/05)

		else if(source.equals(miMenuItemTopDownArrange)) {
			ProjectCompendium.APP.onViewArrange(IUIArrange.TOPDOWN);
		}
		else if(source.equals(miMenuItemLeftRightArrange)) {
			ProjectCompendium.APP.onViewArrange(IUIArrange.LEFTRIGHT);
		}
		else if(source.equals(miMenuItemAlignTop))
			ProjectCompendium.APP.onViewAlign(UIAlign.TOP);
		else if(source.equals(miMenuItemAlignCenter))
			ProjectCompendium.APP.onViewAlign(UIAlign.CENTER);
		else if(source.equals(miMenuItemAlignBottom))
			ProjectCompendium.APP.onViewAlign(UIAlign.BOTTOM);
		else if(source.equals(miMenuItemAlignRight))
			ProjectCompendium.APP.onViewAlign(UIAlign.RIGHT);
		else if(source.equals(miMenuItemAlignMiddle))
			ProjectCompendium.APP.onViewAlign(UIAlign.MIDDLE);
		else if(source.equals(miMenuItemAlignLeft))
			ProjectCompendium.APP.onViewAlign(UIAlign.LEFT);

//end edit
		
		else if (source.equals(miImportCurrentView))
			onImportFile(false);
		else if (source.equals(miImportMultipleViews))
			onImportFile(true);
		else if (source.equals(miImportImageFolder))
			ProjectCompendium.APP.onFileImportImageFolder(oViewPane.getViewFrame());

		else if (source.equals(miSaveAsJpeg))
			ProjectCompendium.APP.onSaveAsJpeg();
		else if (source.equals(miExportHTMLOutline))
			onExportFile();
		else if (source.equals(miExportHTMLView))
			onExportView();

		else if (source.equals(miExportXMLView)) {
			onXMLExport(false);
		} else if (source.equals(miExportHTMLViewXML)) {
			ProjectCompendium.APP.onFileExportPower();			
		} else if (source.equals(miImportXMLView)) {
			onXMLImport();
		} else if (source.equals(miImportXMLFlashmeeting)) {
			UIImportFlashMeetingXMLDialog dlg = new UIImportFlashMeetingXMLDialog(ProjectCompendium.APP);
			UIUtilities.centerComponent(dlg, ProjectCompendium.APP);
			dlg.setVisible(true);							
	
		}

		ProjectCompendium.APP.setDefaultCursor();
	}
	
	
}
