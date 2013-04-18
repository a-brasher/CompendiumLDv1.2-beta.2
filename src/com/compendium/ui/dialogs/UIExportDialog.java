/******************************************************************************
 *                                                                            *
 *  (c) Copyright 2006 Verizon Communications USA and The Open University UK  *
 *                                                                            *
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
 *                                                                            *
 ******************************************************************************/

package com.compendium.ui.dialogs;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.datamodel.services.*;
import com.compendium.core.ICoreConstants;
import com.compendium.core.CoreUtilities;

import com.compendium.*;
import com.compendium.io.html.*;

import com.compendium.ui.plaf.*;
import com.compendium.ui.*;
import com.compendium.ui.panels.*;

/**
 * UIExportDialog defines the export dialog, that allows
 * the user to export PC Map/List Views to a MS-Word format document
 *
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UIExportDialog extends UIDialog implements ActionListener, ItemListener, IUIConstants {

	/** The name of the property file holding the suers export settings.*/
	public static final String	EXPORT_OPTIONS_FILE_NAME = "System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"ExportOptions.properties";

	public static String sBaseAnchorPath = "System"+ProjectCompendium.sFS+"resources"+ProjectCompendium.sFS+"Images"+ProjectCompendium.sFS;

	/** The default directory to export to.*/
	private static String		exportDirectory = ProjectCompendium.sHOMEPATH+ProjectCompendium.sFS+"Exports";

	/**
	 * The pane for the dialog's content to be placed in.
	 * @uml.property  name="contentPane"
	 */
	private Container contentPane = null;

	/**
	 * The button to start the export.
	 * @uml.property  name="pbExport"
	 * @uml.associationEnd  
	 */
	private UIButton			pbExport	= null;

	/**
	 * The button to close the dialog.
	 * @uml.property  name="pbClose"
	 * @uml.associationEnd  
	 */
	private UIButton			pbClose		= null;

	/**
	 * The button to open the help.
	 * @uml.property  name="pbHelp"
	 * @uml.associationEnd  
	 */
	private UIButton			pbHelp 		= null;

	/**
	 * The button to open the HTML export formatting dialog.
	 * @uml.property  name="pbFormatOutput"
	 * @uml.associationEnd  
	 */
	private UIButton			pbFormatOutput = null;

	/**
	 * The button to open the view dialog.
	 * @uml.property  name="pbViews"
	 * @uml.associationEnd  
	 */
	private UIButton			pbViews 	= null;

	/**
	 * The button to browse for a image to use for anchors in the export.
	 * @uml.property  name="pbBrowse"
	 * @uml.associationEnd  
	 */
	private UIButton			pbBrowse 	= null;

	/**
	 * Indicates whether to include the author details in the export.
	 * @uml.property  name="includeNodeAuthor"
	 * @uml.associationEnd  
	 */
	private JCheckBox 		includeNodeAuthor = null;

	/**
	 * Indicates whether to inlucde the images in the export.
	 * @uml.property  name="includeImage"
	 * @uml.associationEnd  
	 */
	private JCheckBox 		includeImage = null;

	/**
	 * Indicates whether to inlucde the links in the export.
	 * @uml.property  name="includeLinks"
	 * @uml.associationEnd  
	 */
	private JCheckBox 		includeLinks = null;

	/**
	 * Indicates whether to inlcude a user assigned title for the main export file.
	 * @uml.property  name="includeTitle"
	 * @uml.associationEnd  
	 */
	private JCheckBox 		includeTitle = null;

	/**
	 * Indicates whether the inlcude a navigation bar with the export.
	 * @uml.property  name="includeNavigationBar"
	 * @uml.associationEnd  
	 */
	private JCheckBox 		includeNavigationBar = null;

	/**
	 * Indicates whether to diapl the node detail page dates on export.
	 * @uml.property  name="displayDetailDates"
	 * @uml.associationEnd  
	 */
	private JCheckBox 		displayDetailDates = null;

	/**
	 * Indicates that the node detail page dates should not be displayed.
	 * @uml.property  name="hideNodeNoDates"
	 * @uml.associationEnd  
	 */
	private JCheckBox 		hideNodeNoDates = null;

	/**
	 * Indicates that each view should be exported in a separate HTML file.
	 * @uml.property  name="displayInDifferentPages"
	 * @uml.associationEnd  
	 */
	private JCheckBox 		displayInDifferentPages = null;

	/**
	 * Indicates whether to include node anchors.
	 * @uml.property  name="includeNodeAnchor"
	 * @uml.associationEnd  
	 */
	private JCheckBox 		includeNodeAnchor = null;

	/**
	 * Indicates whether to include anchors on node detail pages.
	 * @uml.property  name="includeDetailAnchor"
	 * @uml.associationEnd  
	 */
	private JCheckBox 		includeDetailAnchor = null;

	/**
	 * Indicates whether to export all files to a zip file.
	 * @uml.property  name="cbToZip"
	 * @uml.associationEnd  
	 */
	private JCheckBox       cbToZip				= null;

	/**
	 * Indicates whether to include external local reference files in the export.
	 * @uml.property  name="cbWithRefs"
	 * @uml.associationEnd  
	 */
	private JCheckBox       cbWithRefs			= null;

	/**
	 * Lets the user indicate whether to open the export file after completion (only if not zipped).
	 * @uml.property  name="cbOpenAfter"
	 * @uml.associationEnd  
	 */
	private JCheckBox		cbOpenAfter			= null;

	/**
	 * Holds the user assigned title for the main export file.
	 * @uml.property  name="titlefield"
	 * @uml.associationEnd  
	 */
	private JTextField		titlefield = null;

	/**
	 * Holds the name of the anchor image file to use.
	 * @uml.property  name="anchorImage"
	 * @uml.associationEnd  
	 */
	private JTextField		anchorImage = null;

	/**
	 * Holds choice boxes to enter the from date for filtering node detail pages.
	 * @uml.property  name="fromPanel"
	 * @uml.associationEnd  
	 */
	private UIDatePanel 	fromPanel = null;

	/**
	 * Holds choice boxes to enter the to date for filtering node detail pages.
	 * @uml.property  name="toPanel"
	 * @uml.associationEnd  
	 */
	private UIDatePanel 	toPanel = null;

	/**
	 * Should parent view data be placed in line in the main text body?
	 * @uml.property  name="inlineView"
	 * @uml.associationEnd  
	 */
	private JRadioButton 	inlineView = null;

	/**
	 * Indicates whether to include tags in the export.
	 * @uml.property  name="cbIncludeTags"
	 * @uml.associationEnd  
	 */
	private JCheckBox       cbIncludeTags			= null;

	/**
	 * Indicates whether to include parent views in the export.
	 * @uml.property  name="cbIncludeViews"
	 * @uml.associationEnd  
	 */
	private JCheckBox		cbIncludeViews			= null;

	/**
	 * Should node parent view data be inlcuded in the export?
	 * @uml.property  name="noView"
	 * @uml.associationEnd  
	 */
	private JRadioButton 	noView = null;

	/**
	 * should parent view data be placed in separate files?
	 * @uml.property  name="newView"
	 * @uml.associationEnd  
	 */
	private JRadioButton 	newView = null;

	/**
	 * Should node detail detail pages be included in the export?
	 * @uml.property  name="noNodeDetail"
	 * @uml.associationEnd  
	 */
	private JRadioButton 	noNodeDetail = null;

	/**
	 * Should node detail pages should be filtered in given dates?
	 * @uml.property  name="includeNodeDetail"
	 * @uml.associationEnd  
	 */
	private JRadioButton 	includeNodeDetail = null;

	/**
	 * Should node detail pages dates be included in the export?
	 * @uml.property  name="includeNodeDetailDate"
	 * @uml.associationEnd  
	 */
	private JRadioButton 	includeNodeDetailDate = null;

	/**
	 * Should images be used for anchors?
	 * @uml.property  name="useAnchorImages"
	 * @uml.associationEnd  
	 */
	private JRadioButton 	useAnchorImages = null;

	/**
	 * Should purple numbers be used for anchors.
	 * @uml.property  name="useAnchorNumbers"
	 * @uml.associationEnd  
	 */
	private JRadioButton 	useAnchorNumbers = null;

	/**
	 * Should the views being exported be exported to thier full depth?
	 * @uml.property  name="fullDepth"
	 * @uml.associationEnd  
	 */
	private JRadioButton	fullDepth = null;

	/**
	 * Should view being exported only export themselves and not thier child nodes?
	 * @uml.property  name="currentDepth"
	 * @uml.associationEnd  
	 */
	private	JRadioButton	currentDepth = null;

	/**
	 * Should views being export be export to a sinlge level of depth only?
	 * @uml.property  name="oneDepth"
	 * @uml.associationEnd  
	 */
	private	JRadioButton	oneDepth = null;

	/**
	 * Should all nodes in the current view be export?
	 * @uml.property  name="allNodes"
	 * @uml.associationEnd  
	 */
	private JRadioButton	allNodes = null;

	/**
	 * Should only the selected views in the current view be exported.
	 * @uml.property  name="selectedViews"
	 * @uml.associationEnd  
	 */
	private	JRadioButton	selectedViews = null;

	/**
	 * Should only views selected through the views dialog be exported.
	 * @uml.property  name="otherViews"
	 * @uml.associationEnd  
	 */
	private	JRadioButton	otherViews = null;

	/**
	 * The label for the title field.
	 * @uml.property  name="titleLabel"
	 * @uml.associationEnd  
	 */
	private JLabel			titleLabel = null;
	
	/**
	 * The text area to list the views selected for export.
	 * @uml.property  name="oTextArea"
	 * @uml.associationEnd  
	 */
	private JTextArea 		oTextArea  = null;

	// EXPORT SETTINGS
	/**
	 * Stores if node detail pages should be included in the export.
	 * @uml.property  name="bIncludeNodeDetail"
	 */
	private boolean			bIncludeNodeDetail 		= true;

	/**
	 * Stores if node detail pages should be filterd on certain dates.
	 * @uml.property  name="bIncludeNodeDetailDate"
	 */
	private boolean			bIncludeNodeDetailDate 	= false;

	/**
	 * Stores if node author information should be included in the export.
	 * @uml.property  name="bIncludeNodeAuthor"
	 */
	private boolean			bIncludeNodeAuthor 		= false;

	/**
	 * Stores if link label information should be included in the export.
	 * @uml.property  name="bIncludeLinks"
	 */
	private boolean			bIncludeLinks 			= false;

	/**
	 * Stores if node images should be included in the export.
	 * @uml.property  name="bIncludeImage"
	 */
	private boolean			bIncludeImage 			= true;

	/**
	 * Stores if node detail page dates should be diaplyed in the export.
	 * @uml.property  name="bDisplayDetailDates"
	 */
	private boolean			bDisplayDetailDates 	= false;

	/**
	 * Stores if exported views should be exported to separate pages.
	 * @uml.property  name="bDisplayInDifferentPages"
	 */
	private boolean			bDisplayInDifferentPages = true;

	/**
	 * No node detail dates should be included.
	 * @uml.property  name="bHideNodeNoDates"
	 */
	private boolean			bHideNodeNoDates 		= false;

	/**
	 * Stores if the export should include a navigation bar.
	 * @uml.property  name="bIncludeNavigationBar"
	 */
	private boolean			bIncludeNavigationBar 	= true;

	/**
	 * Stores if the parent view information should be diaplyed in the main text body.
	 * @uml.property  name="bInlineView"
	 */
	private boolean			bInlineView 			= false;

	/** Stores if no parent view data should be included in the export.*/
	//private boolean			bNoView 				= false;

	/**
	 * Stores if parent views should be included in the export.
	 * @uml.property  name="bIncludeViews"
	 */
	private boolean			bIncludeViews 			= true;

	/**
	 * Stores if tags should be included in the export.
	 * @uml.property  name="bIncludeTags"
	 */
	private boolean			bIncludeTags 			= true;
	
	/**
	 * Stores if parent view data should be exported to separate pages.
	 * @uml.property  name="bNewView"
	 */
	private boolean			bNewView 				= false;

	/**
	 * Stores if only the selected views should be exported.
	 * @uml.property  name="bSelectedViewsOnly"
	 */
	private boolean			bSelectedViewsOnly 		= false;

	/**
	 * Stores if views selected from the views dialog should be exported.
	 * @uml.property  name="bOtherViews"
	 */
	private boolean			bOtherViews 			= false;

	/**
	 * Stores if node anchors should be inlucded in the export.
	 * @uml.property  name="bIncludeNodeAnchors"
	 */
	private boolean			bIncludeNodeAnchors 	= false;

	/**
	 * Stores if node detail anchors should be inlcuded in the export.
	 * @uml.property  name="bIncludeDetailAnchors"
	 */
	private boolean			bIncludeDetailAnchors	= false;

	/**
	 * Stores if purple numbers hsoul be used for the anchors.
	 * @uml.property  name="bUseAnchorNumbers"
	 */
	private boolean			bUseAnchorNumbers		= false;

	/**
	 * Stores if images should be used for the acnhors.
	 * @uml.property  name="bUseAnchorImages"
	 */
	private boolean			bUseAnchorImages		= true;

	/**
	 * Stores if the exported files should be exported to a zip file.
	 * @uml.property  name="bToZip"
	 */
	private boolean 		bToZip 					= false;

	/**
	 * Stores if external local reference files should be included in the export.
	 * @uml.property  name="bIncludeReferences"
	 */
	private boolean			bIncludeReferences 		= false;

	/**
	 * Indicates whether to open the export file after completion (only if not zipped).
	 * @uml.property  name="bOpenAfter"
	 */
	private boolean			bOpenAfter			= false;

	/**
	 * Used to hold the depth chosen to export views to.
	 * @uml.property  name="depth"
	 */
	private int				depth = 0;

	/**
	 * Stores the to date for filtering node detail pages.
	 * @uml.property  name="toDate"
	 */
	private long			toDate = 0;

	/**
	 * Stores the from date for filtering node detail pages.
	 * @uml.property  name="fromDate"
	 */
	private long			fromDate = 0;

	/**
	 * Used while processing nodes for export.
	 * @uml.property  name="nodeLevelList"
	 * @uml.associationEnd  
	 */
	private Vector			nodeLevelList = null;

	/**
	 * Used while processing nodes for export.
	 * @uml.property  name="htNodesLevel"
	 * @uml.associationEnd  qualifier="getId:java.lang.String java.lang.Integer"
	 */
	private Hashtable		htNodesLevel = new Hashtable(51);

	/**
	 * Holds nodes being processed for export.
	 * @uml.property  name="htNodes"
	 * @uml.associationEnd  qualifier="nodeToPrintId:java.lang.String com.compendium.core.datamodel.NodeSummary"
	 */
	private	Hashtable		htNodes = new Hashtable(51);

	/**
	 * Used wile processing nodes for export.
	 * @uml.property  name="htNodesBelow"
	 * @uml.associationEnd  multiplicity="(0 -1)" ordering="true" elementType="java.lang.String" qualifier="nodeToPrintId:java.lang.String java.util.Vector"
	 */
	private Hashtable		htNodesBelow = new Hashtable(51);

	/**
	 * Used while processing nodes for export.
	 * @uml.property  name="htCheckDepth"
	 * @uml.associationEnd  qualifier="getId:java.lang.String com.compendium.core.datamodel.View"
	 */
	private Hashtable		htCheckDepth = new Hashtable(51);

	/**
	 * Used while processing nodes for export.
	 * @uml.property  name="htChildrenAdded"
	 * @uml.associationEnd  qualifier="getId:java.lang.String com.compendium.core.datamodel.View"
	 */
	private Hashtable		htChildrenAdded = new Hashtable(51);

	/**
	 * The level to start the export at.
	 * @uml.property  name="nStartExportAtLevel"
	 */
	private int				nStartExportAtLevel 	= 0;

	/**
	 * Used while processing nodes for export.
	 * @uml.property  name="nodeIndex"
	 */
	private int				nodeIndex 				= -1;

	/**
	 * The file name for the main export file.
	 * @uml.property  name="fileName"
	 */
	private String			fileName 		= "";

	/**
	 * The anchor image to use.
	 * @uml.property  name="sAnchorImage"
	 */
	private String	sAnchorImage	= sBaseAnchorPath+"anchor0.gif";

	/**
	 * the parent frame for this dialog.
	 * @uml.property  name="oParent"
	 * @uml.associationEnd  
	 */
	private JFrame			oParent	= null;

	/**
	 * Holds the anchor options.
	 * @uml.property  name="innerAnchorPanel"
	 * @uml.associationEnd  
	 */
	private JPanel			innerAnchorPanel = null;

	/**
	 * Holds the saved export options.
	 * @uml.property  name="optionsProperties"
	 * @uml.associationEnd  qualifier="constant:java.lang.String java.lang.String"
	 */
	private Properties		optionsProperties = null;

	/**
	 * The main pane for the dialog's contents.
	 * @uml.property  name="oContentPane"
	 */
	private Container		oContentPane = null;

	/**
	 * The file browser dialog instance to select the export file name.
	 * @uml.property  name="fdgExport"
	 */
	private	FileDialog		fdgExport = null;

	/**
	 * The class that will process the export and create the HTML files etc. for the export.
	 * @uml.property  name="oHTMLExport"
	 * @uml.associationEnd  
	 */
	private HTMLOutline		oHTMLExport = null;

	/**
	 * The current view being exported.
	 * @uml.property  name="currentView"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private View			currentView = null;

	/**
	 * The frame of the current view being exported.
	 * @uml.property  name="currentFrame"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private UIViewFrame		currentFrame = null;

	/**
	 * Used to order the nodes being exported.
	 * @uml.property  name="arrange"
	 * @uml.associationEnd  
	 */
	private IUIArrange		arrange = null;

	/**
	 * The model of the currently open database.
	 * @uml.property  name="model"
	 * @uml.associationEnd  
	 */
	private IModel 			model 	= null;

	/**
	 * The session for the current user in the current model
	 * @uml.property  name="session"
	 * @uml.associationEnd  
	 */
	private PCSession 		session = null;

	/**
	 * The IViewService instance to access the database.
	 * @uml.property  name="vs"
	 * @uml.associationEnd  
	 */
	private IViewService 	vs = null;

	/**
	 * The font to use for labels.
	 * @uml.property  name="font"
	 */
	private Font 			font = null;

	/**
	 * The tabbedpane holding all the various option panels.
	 * @uml.property  name="tabbedPane"
	 * @uml.associationEnd  
	 */
	private JTabbedPane		tabbedPane = null;

	/**
	 * The scrollpane holding the list of default anhor images.
	 * @uml.property  name="imagescroll"
	 * @uml.associationEnd  
	 */
	private JScrollPane 	imagescroll = null;

	/**
	 * The renderer used to render the list of default anchor imags.
	 * @uml.property  name="anchorImageListRenderer"
	 * @uml.associationEnd  inverse="this$0:com.compendium.ui.dialogs.UIExportDialog$AnchorImageCellRenderer"
	 */
	private AnchorImageCellRenderer anchorImageListRenderer = null;

	/**
	 * The list of default anchor images.
	 * @uml.property  name="lstAnchorImages"
	 * @uml.associationEnd  
	 */
	private UINavList 		lstAnchorImages		= null;

	/**
	 * The dialog diaplying all views avilable to export.
	 * @uml.property  name="viewsDialog"
	 * @uml.associationEnd  
	 */
	private UIExportMultipleViewDialog viewsDialog = null;

	/**
	 * The label which tells the user which format the export will use.
	 * @uml.property  name="lblFormatUsed"
	 * @uml.associationEnd  
	 */
	private JLabel			lblFormatUsed = null;
		
	/**
	 * List of style names to be displayed in the choice box.
	 * @uml.property  name="vtStyles"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.String"
	 */
	private Vector 					vtStyles = new Vector();	

	/**
	 * Holds a list of existing styles.
	 * @uml.property  name="oStyles"
	 * @uml.associationEnd  
	 */
	private JComboBox				oStyles	= null;
	
	/**
	 * Initializes and sets up the dialog.
	 * @param frame, the view frame being exported.
	 */
	public UIExportDialog(UIViewFrame frame) {		
		super(ProjectCompendium.APP, true);
		this.currentFrame = frame;
		this.currentView = frame.getView();
	}
	
	/**
	 * Initializes and sets up the dialog.
	 * @param parent, the parent frame for this dialog.
	 * @param frame, the view frame being exported.
	 */
	public UIExportDialog(JFrame parent, UIViewFrame frame) {

		super(parent, true);

		this.currentFrame = frame;
		this.currentView = frame.getView();
	  	this.setTitle("Web Outline Export");

		font = new Font("Dialog", Font.PLAIN, 12);
	  	oParent = parent;

		JPanel mainPanel = new JPanel(new BorderLayout());

		oContentPane = getContentPane();
		oContentPane.setLayout(new BorderLayout());

		tabbedPane = new JTabbedPane();
		tabbedPane.setFont(new Font("Dialog", Font.BOLD, 12));

		JPanel contentPanel = createContentPanel();
		JPanel optionsPanel = createOptionsPanel();
		//JPanel detailPanel = createDetailPanel();
		JPanel tagPanel = createAnchorPanel();

		JPanel outer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		outer.add(contentPanel);
		tabbedPane.add(outer, "Node Selection");

		outer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		outer.add(optionsPanel);
		tabbedPane.add(outer, "Format & Content");

		//outer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		//outer.add(detailPanel);
		//tabbedPane.add(outer, "Node Detail Pages");

		outer = new JPanel(new FlowLayout(FlowLayout.LEFT));
		outer.add(tagPanel);
		tabbedPane.add(outer, "Node Anchors");

		mainPanel.add(tabbedPane, BorderLayout.CENTER);

		JPanel buttonpanel = createButtonPanel();

		oContentPane.add(mainPanel, BorderLayout.CENTER);
		oContentPane.add(buttonpanel, BorderLayout.SOUTH);

		loadProperties();
		applyLoadedProperties();

		pack();
		setResizable(false);
	}

	/**
	 * Draw the button panel for the bottom of the dialog.
	 */
	private JPanel createButtonPanel() {

		UIButtonPanel oButtonPanel = new UIButtonPanel();

		pbExport = new UIButton("Export...");
		pbExport.setMnemonic(KeyEvent.VK_E);
		pbExport.addActionListener(this);
		getRootPane().setDefaultButton(pbExport);
		oButtonPanel.addButton(pbExport);

		pbClose = new UIButton("Cancel");
		pbClose.setMnemonic(KeyEvent.VK_C);
		pbClose.addActionListener(this);
		oButtonPanel.addButton(pbClose);

		pbHelp = new UIButton("Help");
		pbHelp.setMnemonic(KeyEvent.VK_H);
		ProjectCompendium.APP.mainHB.enableHelpOnButton(pbHelp, "io.export_html_outline", ProjectCompendium.APP.mainHS);
		oButtonPanel.addHelpButton(pbHelp);

		return oButtonPanel;
	}

	/**
	 * Draw the first tabbed panel with the primary export options.
	 */
	private JPanel createContentPanel() {

		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(10,10,10,10));

		//STAGE ONE
		
		GridBagLayout gb1 = new GridBagLayout();
		GridBagConstraints gc1 = new GridBagConstraints();
		contentPanel.setLayout(gb1);
		int y=0;
		gc1.anchor = GridBagConstraints.WEST;

		JPanel innerpanel = new JPanel(gb1);
		//innerpanel.setBorder(new TitledBorder("Views to Export"));
		
		JLabel lbltitle1 = new JLabel("Views to Export");
		lbltitle1.setFont(font);
		lbltitle1.setForeground(Color.blue);
		gc1.gridy = y;
		gc1.gridwidth=1;
		y++;
		gb1.setConstraints(lbltitle1, gc1);
		innerpanel.add(lbltitle1);
		
		allNodes = new JRadioButton("Current View only");
		allNodes.setSelected(false);
		allNodes.addItemListener(this);
		allNodes.setFont(font);
		gc1.gridy = y;
		gc1.gridx = 0;
		gc1.gridheight = 1;		
		gc1.gridwidth=2;
		y++;
		gb1.setConstraints(allNodes, gc1);
		innerpanel.add(allNodes);

		selectedViews = new JRadioButton("Selected Views");
		selectedViews.setSelected(false);
		selectedViews.addItemListener(this);
		selectedViews.setFont(font);
		gc1.gridy = y;
		gc1.gridx = 0;		
		gc1.gridheight = 1;
		gc1.gridwidth=2;
		y++;
		gb1.setConstraints(selectedViews, gc1);
		innerpanel.add(selectedViews);

		otherViews = new JRadioButton("Other Views: ");
		otherViews.setSelected(false);
		otherViews.addItemListener(this);
		otherViews.setFont(font);
		gc1.gridy = y;
		gc1.gridx = 0;		
		gc1.gridheight = 1;		
		gc1.gridwidth=1;
		//y++;
		gb1.setConstraints(otherViews, gc1);
		innerpanel.add(otherViews);

		pbViews = new UIButton("Choose Views");
		pbViews.setEnabled(false);
		pbViews.addActionListener(this);
		pbViews.setFont(font);
		gc1.gridy = y;
		gc1.gridx = 1;
		gc1.gridwidth=1;
		gc1.gridheight = 1;
		y++;
		gb1.setConstraints(pbViews, gc1);
		innerpanel.add(pbViews);

		JPanel textpanel = new JPanel(new BorderLayout());
		textpanel.setBorder(new EmptyBorder(0,10,0,0));
		
		JLabel label = new JLabel("Chosen Views:");
		label.setFont(font);
		label.setAlignmentX(SwingConstants.LEFT);
		textpanel.add(label, BorderLayout.NORTH);
					
		oTextArea = new JTextArea("");
		oTextArea.setEditable(false);
		JScrollPane scrollpane = new JScrollPane(oTextArea);
		scrollpane.setPreferredSize(new Dimension(220,120));
		textpanel.add(scrollpane, BorderLayout.CENTER);
		
		gc1.gridy = 0;
		gc1.gridx = 2;		
		gc1.gridwidth=1;
		gc1.gridheight = 4;
		gb1.setConstraints(textpanel, gc1);		
		innerpanel.add(textpanel);
		
		ButtonGroup group1 = new ButtonGroup();
		group1.add(allNodes);
		group1.add(selectedViews);
		group1.add(otherViews);

		//STAGE TWO		
		GridBagLayout gb2 = new GridBagLayout();
		GridBagConstraints gc2 = new GridBagConstraints();
		contentPanel.setLayout(gb2);
		y=0;
		gc2.anchor = GridBagConstraints.WEST;
		JPanel innerpanel2 = new JPanel(gb2);
		
		//innerpanel2.setBorder(new TitledBorder("Depth to Export Views at"));
		
		JSeparator sep2 = new JSeparator();
		gc2.gridy = y;
		gc2.gridwidth=2;
		gc2.insets = new Insets(5,0,2,0);
		y++;
		gc2.fill = GridBagConstraints.HORIZONTAL;
		gb2.setConstraints(sep2, gc2);
		innerpanel2.add(sep2);
		gc2.fill = GridBagConstraints.NONE;
		
		gc2.insets = new Insets(0,0,0,0);
		
		JLabel lbltitle2 = new JLabel("Depth To Export Views To");
		lbltitle2.setFont(font);
		lbltitle2.setForeground(Color.blue);
		gc2.gridy = y;
		gc2.gridwidth=2;
		y++;
		gb2.setConstraints(lbltitle2, gc2);
		innerpanel2.add(lbltitle2);
		
		currentDepth = new JRadioButton("Nodes in view only");
		currentDepth.setSelected(true);
		currentDepth.addItemListener(this);
		currentDepth.setFont(font);
		gc2.gridy = y;
		gc2.gridwidth=1;
		gb2.setConstraints(currentDepth, gc2);
		innerpanel2.add(currentDepth);

		JLabel lbl = new JLabel("");
		lbl.setFont(font);
		gc2.gridy = y;
		gc2.gridwidth=1;
		y++;
		gb2.setConstraints(lbl, gc2);
		innerpanel2.add(lbl);
		
		oneDepth = new JRadioButton("One level down"); 
		oneDepth.setSelected(true);
		oneDepth.addItemListener(this);
		oneDepth.setFont(font);
		gc2.gridy = y;
		gc2.gridwidth=1;
		gb2.setConstraints(oneDepth, gc2);
		innerpanel2.add(oneDepth);

		JLabel lbl1 = new JLabel("(nodes in view and any child view contents)");
		lbl1.setFont(font);
		gc2.gridy = y;
		gc2.gridwidth=1;
		y++;
		gb2.setConstraints(lbl1, gc2);
		innerpanel2.add(lbl1);
		
		fullDepth = new JRadioButton("Full depth");
		fullDepth.setSelected(false);
		fullDepth.addItemListener(this);
		fullDepth.setFont(font);
		gc2.gridwidth=1;
		gc2.gridy = y;
		gb2.setConstraints(fullDepth, gc2);
		innerpanel2.add(fullDepth);

		JLabel lbl2 = new JLabel("(nodes in view, child view contents, their child view contents etc..)");
		lbl2.setFont(font);
		gc2.gridy = y;
		gc2.gridwidth=1;
		y++;
		gb2.setConstraints(lbl2, gc2);
		innerpanel2.add(lbl2);

		ButtonGroup rgGroup = new ButtonGroup();
		rgGroup.add(currentDepth);
		rgGroup.add(oneDepth);
		rgGroup.add(fullDepth);

		// MAIN PANEL
		GridBagLayout gb = new GridBagLayout();
		contentPanel.setLayout(gb);
		GridBagConstraints gc = new GridBagConstraints();
		gc.anchor = GridBagConstraints.WEST;
		y=0;
						
		gc.gridy = y;
		gc.gridwidth=2;
		y++;
		gb.setConstraints(innerpanel, gc);
		contentPanel.add(innerpanel);

		gc.gridy = y;
		gc.gridwidth=2;
		y++;
		gb.setConstraints(innerpanel2, gc);
		contentPanel.add(innerpanel2);
		
		JSeparator sep = new JSeparator();
		gc.gridy = y;
		gc.gridwidth=2;
		gc.insets = new Insets(5,0,2,0);
		y++;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(sep, gc);
		contentPanel.add(sep);
		gc.fill = GridBagConstraints.NONE;

		displayInDifferentPages = new JCheckBox("Export each view in a separate HTML file");
		displayInDifferentPages.addItemListener(this);
		displayInDifferentPages.setFont(font);
		gc.gridy = y;
		gc.gridwidth=2;
		y++;
		gb.setConstraints(displayInDifferentPages, gc);
		contentPanel.add(displayInDifferentPages);

		titleLabel = new JLabel("HTML title for the base web page: ");
		titleLabel.setFont(font);
		titleLabel.setEnabled(false);
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(titleLabel, gc);
		contentPanel.add(titleLabel);

		titlefield = new JTextField("");
		titlefield.setEditable(false);
		titlefield.setColumns(20);
		titlefield.setMargin(new Insets(2,2,2,2));
		titlefield.setEnabled(true);
		gc.gridy = y;
		gc.gridwidth=1;
		y++;
		gb.setConstraints(titlefield, gc);
		contentPanel.add(titlefield);

		sep = new JSeparator();
		gc.gridy = y;
		gc.gridwidth=2;
		gc.insets = new Insets(3,0,5,0);
		y++;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(sep, gc);
		contentPanel.add(sep);
		
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0,0,0,0);

      	cbWithRefs = new JCheckBox("Include referenced files?");
      	cbWithRefs.setSelected(false);
		cbWithRefs.addItemListener(this);
		cbWithRefs.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbWithRefs, gc);
      	contentPanel.add(cbWithRefs);

      	cbToZip = new JCheckBox("Export to Zip Archive?");
      	cbToZip.setSelected(false);
		cbToZip.addItemListener(this);
		cbToZip.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbToZip, gc);
      	contentPanel.add(cbToZip);

      	cbOpenAfter = new JCheckBox("Open Export after completion?");
      	cbOpenAfter.setSelected(false);
		cbOpenAfter.addItemListener(this);
		cbOpenAfter.setFont(font);
		gc.gridy = y;
		gb.setConstraints(cbOpenAfter, gc);
      	contentPanel.add(cbOpenAfter);

		return contentPanel;
	}

	/**
	 *	Create a panel holding the node detail page export options.
	 */
	//private JPanel createDetailPanel() {

		/*JPanel detailPanel = new JPanel();
		detailPanel.setBorder(new EmptyBorder(10,10,10,10));
		detailPanel.setFont(new Font("Dialog", Font.PLAIN, 12));

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		detailPanel.setLayout(gb);
		gc.anchor = GridBagConstraints.WEST;

		int y=0;

		// CREATE DATE PANEL FIRST FOR REFERENCE REASONS
		JPanel datePanel = createDatePanel();*/

		//JLabel label = new JLabel("Node Details");
		//label.setFont(new Font("Arial", Font.BOLD, 12));
		//gc.gridy = y;
		//y++;
		//gb.setConstraints(label, gc);
		//detailPanel.add(label);

		/*noNodeDetail = new JRadioButton("No node detail pages");
		noNodeDetail.addItemListener(this);
		noNodeDetail.setFont(font);
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(noNodeDetail, gc);
		detailPanel.add(noNodeDetail);

		includeNodeDetail = new JRadioButton("Include all node detail pages");
		includeNodeDetail.addItemListener(this);
		includeNodeDetail.setFont(font);
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(includeNodeDetail, gc);
		detailPanel.add(includeNodeDetail);

		includeNodeDetailDate = new JRadioButton("Include node detail pages for Dates: ");
		includeNodeDetailDate.addItemListener(this);
		includeNodeDetailDate.setFont(font);
		gc.gridy = y;
		y++;
		gc.gridwidth=2;
		gb.setConstraints(includeNodeDetailDate, gc);
		detailPanel.add(includeNodeDetailDate);

		ButtonGroup detailGroup = new ButtonGroup();
		detailGroup.add(noNodeDetail);
		detailGroup.add(includeNodeDetail);
		detailGroup.add(includeNodeDetailDate);

		// ADD DATE PANEL
		gc.gridy = y;
		y++;
		gc.gridwidth=2;
		gb.setConstraints(datePanel, gc);
		detailPanel.add(datePanel);

		JLabel other = new JLabel(" ");
		gc.gridy = y;
		y++;
		gb.setConstraints(other, gc);
		detailPanel.add(other);

		displayDetailDates = new JCheckBox("Display detail page dates");
		displayDetailDates.addItemListener(this);
		displayDetailDates.setSelected(false);
		displayDetailDates.setFont(font);
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(displayDetailDates, gc);
		detailPanel.add(displayDetailDates);*/

		/*
		hideNodeNoDates = new JCheckBox("Hide nodes outside of dates");
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(hideNodeNoDates, gc);
		hideNodeNoDates.addItemListener(this);
		hideNodeNoDates.setSelected(false);
		detailPanel.add(hideNodeNoDates);
		*/

		//return detailPanel;
	//}

	/**
	 *	Create a panel holding the anchor export options (i.e. purple numbers stuff).
	 */
	private JPanel createAnchorPanel() {

		JPanel anchorPanel = new JPanel();
		anchorPanel.setLayout(new BorderLayout());
		anchorPanel.setBorder(new EmptyBorder(10,10,10,10));
		anchorPanel.setFont(new Font("Dialog", Font.PLAIN, 12));

		JPanel innerAnchorPanelTop = new JPanel();
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		innerAnchorPanelTop.setLayout(gb);
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(5,5,5,5);

		int y=0;

		includeNodeAnchor = new JCheckBox("Include anchors on node labels");
		includeNodeAnchor.addItemListener(this);
		includeNodeAnchor.setFont(font);
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth=1;
		gb.setConstraints(includeNodeAnchor, gc);
		innerAnchorPanelTop.add(includeNodeAnchor);

		includeDetailAnchor = new JCheckBox("Include anchors on node detail pages");
		includeDetailAnchor.addItemListener(this);
		includeDetailAnchor.setFont(font);
		gc.gridy = y;
		gc.gridx = 1;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(includeDetailAnchor, gc);
		innerAnchorPanelTop.add(includeDetailAnchor);

		useAnchorImages = new JRadioButton("Use images for anchors");
		useAnchorImages.addItemListener(this);
		useAnchorImages.setFont(font);
		gc.gridy = y;
		gc.gridx = 0;
		gc.gridwidth=1;
		gb.setConstraints(useAnchorImages, gc);
		innerAnchorPanelTop.add(useAnchorImages);

		useAnchorNumbers = new JRadioButton("Use purple numbers");
		useAnchorNumbers.addItemListener(this);
		useAnchorNumbers.setFont(font);
		gc.gridy = y;
		gc.gridx = 1;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(useAnchorNumbers, gc);
		innerAnchorPanelTop.add(useAnchorNumbers);

		ButtonGroup anchorGroup = new ButtonGroup();
		anchorGroup.add(useAnchorImages);
		anchorGroup.add(useAnchorNumbers);

		innerAnchorPanel = new JPanel();
		innerAnchorPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		gb = new GridBagLayout();
		gc = new GridBagConstraints();
		innerAnchorPanel.setLayout(gb);
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(5,5,5,5);

		y=0;

		createAnchorImageList();
		gc.gridy = y;
		//y++;
		gc.gridwidth=2;
		gb.setConstraints(lstAnchorImages, gc);
		innerAnchorPanel.add(lstAnchorImages);

		JTextArea area = new JTextArea("Select one of the default anchor images from this list or use the browse button below to select your own anchor image");
		area.setBackground(innerAnchorPanel.getBackground());
		area.setColumns(20);
		area.setRows(7);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setEnabled(false);
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(area, gc);
		innerAnchorPanel.add(area);

		JLabel label = new JLabel("Anchor image: ");
		label.setFont(font);
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(label, gc);
		innerAnchorPanel.add(label);

		anchorImage = new JTextField("");
		anchorImage.setEditable(false);
		anchorImage.setColumns(25);
		anchorImage.setMargin(new Insets(2,2,2,2));
		anchorImage.setEnabled(true);
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(anchorImage, gc);
		innerAnchorPanel.add(anchorImage);

		pbBrowse = new UIButton("Browse");
		pbBrowse.addActionListener(this);
		pbBrowse.setEnabled(false);
		gc.gridy = y;
		gc.gridwidth=1;
		gb.setConstraints(pbBrowse, gc);
		innerAnchorPanel.add(pbBrowse);

		anchorPanel.add(innerAnchorPanelTop, BorderLayout.NORTH);
		anchorPanel.add(innerAnchorPanel, BorderLayout.CENTER);

		return anchorPanel;
	}

	/**
	 *	Create a panel holding other export options.
	 */
	private JPanel createOptionsPanel() {

		JPanel optionsPanel = new JPanel();
		optionsPanel.setBorder(new EmptyBorder(10,10,10,10));
		optionsPanel.setFont(new Font("Dialog", Font.PLAIN, 12));

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		optionsPanel.setLayout(gb);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridwidth=1;

		int y=0;

		lblFormatUsed = new JLabel("Outline Format   ");
		lblFormatUsed.setFont(font);
		gc.gridy = y;
		gc.gridwidth = 1;
		gb.setConstraints(lblFormatUsed, gc);
		optionsPanel.add(lblFormatUsed);
	
		this.createStylesChoiceBox();
		gc.gridy = y;
		gc.gridwidth = 1;
		gb.setConstraints(oStyles, gc);
		optionsPanel.add(oStyles);

		pbFormatOutput = new UIButton("Create/Edit Format...");
		pbFormatOutput.setMnemonic(KeyEvent.VK_F);
		pbFormatOutput.addActionListener(this);		
		gc.gridy = y;
		//gc.weightx = 10;
		gb.setConstraints(pbFormatOutput, gc);
		optionsPanel.add(pbFormatOutput);
		y++;
		
		gc.gridwidth = 3;

		JSeparator sep = new JSeparator();
		gc.gridy = y;
		gc.insets = new Insets(3,0,5,0);
		y++;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(sep, gc);
		optionsPanel.add(sep);
		gc.insets = new Insets(0,0,0,0);

		includeNavigationBar = new JCheckBox("Include a navigation menu");
		includeNavigationBar.addItemListener(this);
		includeNavigationBar.setSelected(false);
		includeNavigationBar.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(includeNavigationBar, gc);
		optionsPanel.add(includeNavigationBar);

		JLabel label = new JLabel(" ");
		gc.gridy = y;
		y++;
		gb.setConstraints(label, gc);
		optionsPanel.add(label);

		includeLinks = new JCheckBox("Include link labels");
		includeLinks.addItemListener(this);
		includeLinks.setSelected(false);
		includeLinks.setFont(font);
		gc.gridy = y;
		y++;
		//gb.setConstraints(includeLinks, gc);
		//optionsPanel.add(includeLinks);

		includeNodeAuthor = new JCheckBox("Include node authors");
		includeNodeAuthor.addItemListener(this);
		includeNodeAuthor.setSelected(false);
		includeNodeAuthor.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(includeNodeAuthor, gc);
		optionsPanel.add(includeNodeAuthor);

		includeImage = new JCheckBox("Include images");
		includeImage.addItemListener(this);
		includeImage.setSelected(true);
		includeImage.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(includeImage, gc);
		optionsPanel.add(includeImage);

		cbIncludeTags = new JCheckBox("Include tags");
		cbIncludeTags.addItemListener(this);
		cbIncludeTags.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbIncludeTags, gc);
		optionsPanel.add(cbIncludeTags);
		
		cbIncludeViews = new JCheckBox("Include views");
		cbIncludeViews.addItemListener(this);
		cbIncludeViews.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(cbIncludeViews, gc);
		optionsPanel.add(cbIncludeViews);
		
		ButtonGroup bg = new ButtonGroup();

		inlineView = new JRadioButton("   Include tags / views as inline text");
		inlineView.addItemListener(this);
		inlineView.setSelected(false);
		inlineView.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(inlineView, gc);
		bg.add(inlineView);
		optionsPanel.add(inlineView);

		newView = new JRadioButton("   Show tags / views in new window");
		newView.addItemListener(this);
		newView.setSelected(false);
		newView.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(newView, gc);
		bg.add(newView);
		optionsPanel.add(newView);

		sep = new JSeparator();
		gc.gridy = y;
		gc.insets = new Insets(3,0,5,0);
		y++;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gb.setConstraints(sep, gc);
		optionsPanel.add(sep);
		gc.insets = new Insets(0,0,0,0);

		// DETAIL PAGES SECTION

		JPanel detailPanel = new JPanel();
		detailPanel.setBorder(new EmptyBorder(10,10,10,10));
		detailPanel.setFont(new Font("Dialog", Font.PLAIN, 12));

		// CREATE DATE PANEL FIRST FOR REFERENCE REASONS
		JPanel datePanel = createDatePanel();

		noNodeDetail = new JRadioButton("No node detail pages");
		noNodeDetail.addItemListener(this);
		noNodeDetail.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(noNodeDetail, gc);
		optionsPanel.add(noNodeDetail);

		includeNodeDetail = new JRadioButton("Include all node detail pages");
		includeNodeDetail.addItemListener(this);
		includeNodeDetail.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(includeNodeDetail, gc);
		optionsPanel.add(includeNodeDetail);

		includeNodeDetailDate = new JRadioButton("Include node detail pages for Dates: ");
		includeNodeDetailDate.addItemListener(this);
		includeNodeDetailDate.setFont(font);
		gc.gridy = y;
		y++;
		gb.setConstraints(includeNodeDetailDate, gc);
		optionsPanel.add(includeNodeDetailDate);

		ButtonGroup detailGroup = new ButtonGroup();
		detailGroup.add(noNodeDetail);
		detailGroup.add(includeNodeDetail);
		detailGroup.add(includeNodeDetailDate);

		// ADD DATE PANEL
		gc.gridy = y;
		y++;
		gb.setConstraints(datePanel, gc);
		optionsPanel.add(datePanel);

		JLabel other = new JLabel(" ");
		gc.gridy = y;
		y++;
		gb.setConstraints(other, gc);
		optionsPanel.add(other);

		displayDetailDates = new JCheckBox("Display detail page dates");
		displayDetailDates.addItemListener(this);
		displayDetailDates.setSelected(false);
		displayDetailDates.setFont(font);
		gc.gridy = y;
		gb.setConstraints(displayDetailDates, gc);
		optionsPanel.add(displayDetailDates);

		/*
		hideNodeNoDates = new JCheckBox("Hide nodes outside of dates");
		gc.gridy = y;
		y++;
		gc.gridwidth=1;
		gb.setConstraints(hideNodeNoDates, gc);
		hideNodeNoDates.addItemListener(this);
		hideNodeNoDates.setSelected(false);
		detailPanel.add(hideNodeNoDates);
		*/

		return optionsPanel;
	}

	/**
	 * Create the styles choicebox.
	 */
	private JComboBox createStylesChoiceBox() {

		oStyles = new JComboBox();
		oStyles.setOpaque(true);
		oStyles.setEditable(false);
		oStyles.setEnabled(true);
		oStyles.setMaximumRowCount(30);
		oStyles.setFont( new Font("Dialog", Font.PLAIN, 12 ));

		reloadData();
 
		DefaultListCellRenderer comboRenderer = new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(
   		     	JList list,
   		        Object value,
            	int modelIndex,
            	boolean isSelected,
            	boolean cellHasFocus)
            {
 		 		if (isSelected) {
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
				}
				else {
					setBackground(list.getBackground());
					setForeground(list.getForeground());
				}

				setText((String)value);
				return this;
			}
		};
		oStyles.setRenderer(comboRenderer);
		
		ActionListener choiceaction = new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
            	Thread choiceThread = new Thread("UIHTMLFormatDialog.createStylesChoiceBox") {
                	public void run() {
						if (oStyles != null) {
							String selected = (String)oStyles.getSelectedItem();
							FormatProperties.outlineFormat = selected;
							FormatProperties.setFormatProp("outlineFormat", selected);
							FormatProperties.saveFormatProps();
						}
                	}
               	};
	            choiceThread.start();
        	}
		};
		oStyles.addActionListener(choiceaction);

		return oStyles;
	}
	
	private void reloadData() {
		try {
			vtStyles.clear();
			File main = new File(UIHTMLFormatDialog.DEFAULT_FILE_PATH);
			File styles[] = main.listFiles();
			File file = null;
			String sName = "";
			String value = "";
			String sFileName = "";
			int index = 0;
			int j = 0;
			if (styles.length > 0) {			
				for (int i=0; i<styles.length; i++) {
					file = styles[i];
					sFileName = file.getName();
					if (!sFileName.startsWith(".") && sFileName.endsWith(".properties")) {
						Properties styleProp = new Properties();
						styleProp.load(new FileInputStream(file));
						value = styleProp.getProperty("status");
						if (value.equals("active")) {
							value = styleProp.getProperty("name");
							if (value != null) {
								sName = value;
								if (sName.equals(FormatProperties.outlineFormat)) {
									index = j+1;
								}
								vtStyles.add(sName);
							}
							j++;
						}
					}
				}
				vtStyles = UIUtilities.sortList(vtStyles);				
				vtStyles.insertElementAt("< Select An Outline Format >", 0);
				DefaultComboBoxModel comboModel = new DefaultComboBoxModel(vtStyles);
				oStyles.setModel(comboModel);
				oStyles.setSelectedIndex(index);				
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Exception: (UIExportDialog.reloadData) " + ex.getMessage());
		}		
	}
	
	/**
	 * Crate the panel hold the node detail pages date filter options.
	 */
	private JPanel createDatePanel() {

		JPanel panel = new JPanel(new BorderLayout());

		fromPanel = new UIDatePanel("From: ");
		panel.add(fromPanel, BorderLayout.WEST);

		toPanel = new UIDatePanel("To: ");
		panel.add(toPanel, BorderLayout.EAST);

		return panel;
	}

	/**
	 * Create the list to display anchor images.
	 */
	private void createAnchorImageList() {

   	 	String[] images = {sBaseAnchorPath+"anchor0.gif", sBaseAnchorPath+"anchor1.gif", sBaseAnchorPath+"anchor2.gif", sBaseAnchorPath+"anchor3.gif", sBaseAnchorPath+"anchor4.gif",
							sBaseAnchorPath+"anchor5.gif", sBaseAnchorPath+"anchor6.gif", sBaseAnchorPath+"anchor7.gif"};

		lstAnchorImages = new UINavList(images);
		lstAnchorImages.setEnabled(false);
		lstAnchorImages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        anchorImageListRenderer = new AnchorImageCellRenderer();
		lstAnchorImages.setCellRenderer(anchorImageListRenderer);
		lstAnchorImages.setBorder(new CompoundBorder(new LineBorder(Color.gray ,1), new EmptyBorder(5,5,5,5)));
		imagescroll = new JScrollPane(lstAnchorImages);
		imagescroll.setPreferredSize(new Dimension(150, 60));

		MouseListener fontmouse = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					String image = (String)lstAnchorImages.getSelectedValue();
					setAnchorImage(image);
				}
			}
		};
		KeyListener fontkey = new KeyAdapter() {
           	public void keyPressed(KeyEvent e) {
				if ((e.getKeyCode() == KeyEvent.VK_ENTER) && (e.getModifiers() == 0)) {
					String image = (String)lstAnchorImages.getSelectedValue();
					setAnchorImage(image);
				}
			}
		};

		lstAnchorImages.addKeyListener(fontkey);
		lstAnchorImages.addMouseListener(fontmouse);
	}

	/**
	 * Helper class to render the anchor image list.
	 */
	public class AnchorImageCellRenderer extends JLabel implements ListCellRenderer {

	  	protected Border noFocusBorder;

		public AnchorImageCellRenderer() {
			super();
			noFocusBorder = new EmptyBorder(1, 1, 1, 1);
			setOpaque(true);
			setBorder(noFocusBorder);
		}

		public Component getListCellRendererComponent(
        	JList list,
            Object value,
            int modelIndex,
            boolean isSelected,
            boolean cellHasFocus)
            {

 	 		if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}
			else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			setText((String)value);
			setHorizontalTextPosition(SwingConstants.TRAILING);
			setIconTextGap(6);
			setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

			ImageIcon image = new ImageIcon((String)value);
   			setIcon(image);

			return this;
		}
	}

	/******* EVENT HANDLING METHODS *******/

	/**
	 * Handle action events coming from the buttons.
 	 * @param evt, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();

		// Handle button events
		if (source instanceof JButton) {
			if (source == pbExport) {
				onExport();
			}
			else if (source == pbViews) {
				onViews();
			}
			else if (source == pbFormatOutput) {
				UIHTMLFormatDialog dialog2 = new UIHTMLFormatDialog(ProjectCompendium.APP);
				dialog2.setVisible(true);
				while (dialog2.isVisible()) {}
				reloadData();
			}
			else if (source == pbBrowse) {
				onBrowse();
			}
			else if (source == pbClose) {
				onCancel(false);
			}
		}
	}

	/**
	 * Open the file browser dialog for the user to select an anchor image.
	 */
	private void onBrowse() {

		UIFileFilter gifFilter = new UIFileFilter(new String[] {"gif"}, "GIF Image Files");

		UIFileChooser fileDialog = new UIFileChooser();
		fileDialog.setDialogTitle("Select image for anchor...");
		fileDialog.setFileFilter(gifFilter);
		fileDialog.setDialogType(JFileChooser.OPEN_DIALOG);
		fileDialog.setRequiredExtension(".gif");

		// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
		// AND MUST USE ABSOUTE PATH, AS RELATIVE PATH REMOVES THE '/'
		/*File filepath = new File("");
		String sPath = filepath.getAbsolutePath();
		File file = new File(sPath+ProjectCompendium.sFS+"Linked Files"+ProjectCompendium.sFS);
		if (file.exists()) {
			fileDialog.setCurrentDirectory(file);
		}*/

		String fileName = "";
		UIUtilities.centerComponent(fileDialog, this);
		int retval = fileDialog.showDialog(this, null);

		if (retval == JFileChooser.APPROVE_OPTION) {
        	if ((fileDialog.getSelectedFile()) != null) {

            	fileName = fileDialog.getSelectedFile().getAbsolutePath();

				if (fileName != null) {
					if ( fileName.toLowerCase().endsWith(".gif") ) {
						setAnchorImage(fileName);
					}
				}
			}
		}
	}

	/**
	 * Open the views dialog for the user to select views to export.
	 */
	private void onViews() {

		if (viewsDialog == null) {
			viewsDialog = new UIExportMultipleViewDialog(this);
			viewsDialog.setVisible(true);
		}
		else {
			viewsDialog.setVisible(true);
		}
	}

	/**
	 * Apply the export options previously saved, to the various ui elements.
	 */
	private void applyLoadedProperties() {

		displayInDifferentPages.setSelected(bDisplayInDifferentPages);

		if (depth == 2) {
			fullDepth.setSelected(true);
		}
		else if (depth == 1) {
			oneDepth.setSelected(true);
		}
		else {
			currentDepth.setSelected(true);
		}

		anchorImage.setText(sAnchorImage);
		includeNodeAnchor.setSelected(bIncludeNodeAnchors);
		includeDetailAnchor.setSelected(bIncludeDetailAnchors);
		if (bUseAnchorNumbers)
			useAnchorNumbers.setSelected(true);
		else
			useAnchorImages.setSelected(true);

		//toPanel.setDate(toDate);
		//fromPanel.setDate(fromDate);

		includeNodeDetail.setSelected(bIncludeNodeDetail);
		includeNodeDetailDate.setSelected(bIncludeNodeDetailDate);
		if (!bIncludeNodeDetail && !bIncludeNodeDetailDate)
			noNodeDetail.setSelected(true);

		displayDetailDates.setSelected(bDisplayDetailDates);
		includeNodeAuthor.setSelected(bIncludeNodeAuthor);
		includeImage.setSelected(bIncludeImage);
		includeLinks.setSelected(bIncludeLinks);

		//hideNodeNoDates.setSelected(bHideNodeNoDates);
		
		cbIncludeViews.setSelected(bIncludeViews);		
		cbIncludeTags.setSelected(bIncludeTags);		
		
		includeNavigationBar.setSelected(bIncludeNavigationBar);
		inlineView.setSelected(bInlineView);
		newView.setSelected(bNewView);

		cbOpenAfter.setSelected(bOpenAfter);
		cbToZip.setSelected(bToZip);
		cbWithRefs.setSelected(bIncludeReferences);

		if (!hasSelectedViews()) {
			bSelectedViewsOnly = false;
			selectedViews.setEnabled(false);
		} 
				
		selectedViews.setSelected(bSelectedViewsOnly);		
		otherViews.setSelected(bOtherViews);

		if (!bSelectedViewsOnly && !bOtherViews)
			allNodes.setSelected(true);

	   	lstAnchorImages.setSelectedValue((Object)sAnchorImage, true);
	}


	/**
	 * Return the to date for filtering node detail pages.
	 * @return GregorianCalendar, the to date for filtering node detail pages.
	 */
	public GregorianCalendar getToDate() {
		return toPanel.getDateEnd();
	}

	/**
	 * Return the from date for filtering node detail pages.
	 * @return GregorianCalendar, the from date for filtering node detail pages.
	 */
	public GregorianCalendar getFromDate() {
		return fromPanel.getDate();
	}

	/**
	 * Set the anchor image to use.
 	 * @param sImage, the path of the anchor image to use.
	 */
	public void setAnchorImage(String sImage) {
		if (sImage != null && !sImage.equals("")) {
			sAnchorImage = sImage;
			anchorImage.setText(sImage);
		}
	}

	/**
	 * Set the current view to being exported.
	 * @param view  com.compendium.core.datamodel.View, the current view being exported.
	 * @uml.property  name="currentView"
	 */
	public void setCurrentView(View view) {
		currentView = view;
	}

	/**
	 * Check that the dates for filtering node detail pages have been entered correctly.
	 */
	public boolean checkDates() {
		if (fromPanel.checkDate() && toPanel.checkDate())
			return true;

		return false;
	}

	/**
	 * Records the fact that a checkbox / radio button state has been changed and stores the new data.
	 * @param e, the associated ItemEvent.
	 */
	public void itemStateChanged(ItemEvent e) {

		Object source = e.getItemSelectable();


		if (source == displayInDifferentPages) {
			bDisplayInDifferentPages = displayInDifferentPages.isSelected();
			if (bDisplayInDifferentPages) {
				if (titlefield != null) {
					titlefield.setEditable(true);
					titleLabel.setEnabled(true);
					titlefield.repaint();
				}
			}
			else {
				if (titlefield != null) {
					titlefield.setText("");
					titlefield.setEditable(false);
					titleLabel.setEnabled(false);
					titlefield.repaint();
				}
			}
		}
		else if (source == cbWithRefs) {
			bIncludeReferences = cbWithRefs.isSelected();
		}
		else if (source == cbToZip) {
			bToZip = cbToZip.isSelected();
			if (bToZip) {
				cbOpenAfter.setSelected(false);
				cbOpenAfter.setEnabled(false);
				bOpenAfter = false;
			}
			else {
				cbOpenAfter.setEnabled(true);
			}
		}
		else if (source == cbOpenAfter) {
			bOpenAfter = cbOpenAfter.isSelected();
		}
		else if (source == includeDetailAnchor) {
			bIncludeDetailAnchors = includeDetailAnchor.isSelected();
		}
		else if (source == includeNodeAnchor) {
			bIncludeNodeAnchors = includeNodeAnchor.isSelected();
		}
		else if (source == useAnchorNumbers) {
			bUseAnchorNumbers = useAnchorNumbers.isSelected();
			if (useAnchorNumbers.isSelected()) {
				pbBrowse.setEnabled(false);
				lstAnchorImages.setEnabled(false);
			}
			else if (!useAnchorNumbers.isSelected() && !useAnchorNumbers.isSelected()) {
				pbBrowse.setEnabled(true);
				lstAnchorImages.setEnabled(true);
			}
		}
		else if (source == useAnchorImages) {
			bUseAnchorImages = useAnchorImages.isSelected();
			if (useAnchorImages.isSelected()) {
				pbBrowse.setEnabled(true);
				lstAnchorImages.setEnabled(true);
			}
			else if (!useAnchorImages.isSelected() && !useAnchorImages.isSelected()) {
				pbBrowse.setEnabled(false);
				lstAnchorImages.setEnabled(false);
			}
		}
		else if (source == fullDepth && fullDepth.isSelected()) {
			depth = 2;

			displayInDifferentPages.setEnabled(true);
			displayInDifferentPages.repaint();
		}
		else if (source == oneDepth && oneDepth.isSelected()) {
			depth = 1;

			displayInDifferentPages.setEnabled(true);
			displayInDifferentPages.repaint();
		}
		else if (source == currentDepth && currentDepth.isSelected()) {
			depth = 0;

			if (allNodes.isSelected()) {
				displayInDifferentPages.setSelected(false);
				displayInDifferentPages.setEnabled(false);
				titlefield.setEditable(false);
				titleLabel.setEnabled(false);
			}
			else {
				displayInDifferentPages.setEnabled(true);
				displayInDifferentPages.repaint();
			}
		}

		else if (source == selectedViews && selectedViews.isSelected()) {
			bOtherViews = false;
			bSelectedViewsOnly = true;

			pbViews.setEnabled(false);
			displayInDifferentPages.setEnabled(true);
			displayInDifferentPages.repaint();
			updateViewsList();
		}
		else if (source == allNodes && allNodes.isSelected()) {
			bOtherViews = false;
			bSelectedViewsOnly = false;

			pbViews.setEnabled(false);

			if (currentDepth.isSelected()) {
				displayInDifferentPages.setSelected(false);
				displayInDifferentPages.setEnabled(false);
				titlefield.setEditable(false);
				titleLabel.setEnabled(false);
			}
			else {
				displayInDifferentPages.setEnabled(true);
				displayInDifferentPages.repaint();
			}
			updateViewsList();
		}
		else if (source == otherViews && otherViews.isSelected()) {
			bOtherViews = true;
			bSelectedViewsOnly = false;

			pbViews.setEnabled(true);
			displayInDifferentPages.setEnabled(true);
			displayInDifferentPages.repaint();
			updateViewsList();
		}
		else if (source == includeNodeAuthor) {
			bIncludeNodeAuthor = includeNodeAuthor.isSelected();
		}
		else if (source == displayDetailDates) {
			bDisplayDetailDates = displayDetailDates.isSelected();
		}
		else if (source == hideNodeNoDates) {
			bHideNodeNoDates = hideNodeNoDates.isSelected();
		}
		else if (source == noNodeDetail && noNodeDetail.isSelected()) {
			bIncludeNodeDetail = false;
			bIncludeNodeDetailDate = false;

			toPanel.setDateEnabled(false);
			fromPanel.setDateEnabled(false);
		}
		else if (source == includeNodeDetail && includeNodeDetail.isSelected()) {
			bIncludeNodeDetail= true;
			bIncludeNodeDetailDate = false;

			toPanel.setDateEnabled(false);
			fromPanel.setDateEnabled(false);
		}
		else if (source == includeNodeDetailDate && includeNodeDetailDate.isSelected()) {
			bIncludeNodeDetail = false;
			bIncludeNodeDetailDate = true;

			toPanel.setDateEnabled(true);
			fromPanel.setDateEnabled(true);
		}
		else if (source == includeImage) {
			bIncludeImage = includeImage.isSelected();
		}
		else if (source == includeLinks) {
			bIncludeLinks = includeLinks.isSelected();
		}
		else if (source == includeNavigationBar) {
			bIncludeNavigationBar = includeNavigationBar.isSelected();
		}
		else if (source == cbIncludeTags) {
			bIncludeTags = cbIncludeTags.isSelected();
			if ((cbIncludeViews != null && !cbIncludeViews.isSelected()) && !cbIncludeTags.isSelected()) {
				inlineView.setEnabled(false);
				newView.setEnabled(false);
			} else {
				inlineView.setEnabled(true);
				newView.setEnabled(true);				
			}			
		}
		else if (source == cbIncludeViews) {
			bIncludeViews = cbIncludeViews.isSelected();
			if (!cbIncludeViews.isSelected() && (cbIncludeTags != null && !cbIncludeTags.isSelected())) {
				inlineView.setEnabled(false);
				newView.setEnabled(false);
			} else {
				inlineView.setEnabled(true);
				newView.setEnabled(true);				
			}
		}
		else if (source == inlineView) {
			bInlineView = inlineView.isSelected();
		}
		else if (source == newView) {
			bNewView = newView.isSelected();
		}
	}

	/******* EXPORT *******************************************************/

	/**
	 * Handle the export action. Rquest the export file be selected.
	 * @see #processExport
	 */
	public void onExport() {
		
		// CHECK ALL DATE INFORMATION ENTERED, IF REQUIRED
		if (bIncludeNodeDetailDate) {
			if (!checkDates()) {
				ProjectCompendium.APP.displayMessage("Please complete all date information", "Date Error");
				return;
			}
		}

		if (otherViews.isSelected()) {
			if(viewsDialog == null || (viewsDialog.getTable().getSelectedRows()).length <= 0) {
				ProjectCompendium.APP.displayMessage("Please select at least one view to export", "Web Outline Export");
				return;
			}
		}

		boolean toZip = cbToZip.isSelected();
		if (toZip) {
			UIFileFilter filter = new UIFileFilter(new String[] {"zip"}, "ZIP Files");

			UIFileChooser fileDialog = new UIFileChooser();
			fileDialog.setDialogTitle("Enter the file name to Export to...");
			fileDialog.setFileFilter(filter);
			fileDialog.setApproveButtonText("Save");
			fileDialog.setRequiredExtension(".zip");

		    // FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
		    File file = new File(exportDirectory+ProjectCompendium.sFS);
		    if (file.exists()) {
				fileDialog.setCurrentDirectory(file);
			}

			int retval = fileDialog.showSaveDialog(ProjectCompendium.APP);
			if (retval == JFileChooser.APPROVE_OPTION) {
	        	if ((fileDialog.getSelectedFile()) != null) {

	            	fileName = fileDialog.getSelectedFile().getAbsolutePath();
					File fileDir = fileDialog.getCurrentDirectory();
					exportDirectory = fileDir.getPath();

					if (fileName != null) {
						if ( !fileName.toLowerCase().endsWith(".zip") ) {
							fileName = fileName+".zip";
						}
					}
				}
			}
		}
		else {						
			UIFileFilter filter = new UIFileFilter(new String[] {"html"}, "HTML Files");

			UIFileChooser fileDialog = new UIFileChooser();
			fileDialog.setDialogTitle("Enter the file name to Export to...");
			fileDialog.setFileFilter(filter);
			fileDialog.setApproveButtonText("Save");
			fileDialog.setRequiredExtension(".html");

		    // FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
		    File file = new File(exportDirectory+ProjectCompendium.sFS);
		    if (file.exists()) {
				fileDialog.setCurrentDirectory(file);
			}

			int retval = fileDialog.showSaveDialog(ProjectCompendium.APP);
			if (retval == JFileChooser.APPROVE_OPTION) {
	        	if ((fileDialog.getSelectedFile()) != null) {

	            	fileName = fileDialog.getSelectedFile().getAbsolutePath();
					File fileDir = fileDialog.getCurrentDirectory();
					exportDirectory = fileDir.getPath();

					if (fileName != null) {
						if ( !fileName.toLowerCase().endsWith(".html") ) {
							fileName = fileName+".html";
						}
					}
				}
			}
		}

		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		if (fileName != null && !fileName.equals("")) {
			if (!processExport())
				onCancel(false);
			else {
				if (bOpenAfter) {
					ExecuteControl.launch(fileName);
				}
				onCancel(true);
			}
		}
		setCursor(Cursor.getDefaultCursor());
	}


	/**
	 *	Process the export.
	 */
	public boolean processExport() {
		oHTMLExport = new HTMLOutline(bIncludeNodeDetail,
										bIncludeNodeDetailDate,
									  	bIncludeNodeAuthor,
										nStartExportAtLevel,
										fileName, bToZip);

		if (bIncludeNodeDetailDate) {
			GregorianCalendar fDate = getFromDate();
			GregorianCalendar tDate = getToDate();
			fromDate = fDate.getTime().getTime();
			toDate = tDate.getTime().getTime();
			if (tDate != null && fDate != null) {
				oHTMLExport.setFromDate(fDate);
				oHTMLExport.setToDate(tDate);
			}
		}

		oHTMLExport.setIncludeLinks(bIncludeLinks);
		oHTMLExport.setIncludeImage(bIncludeImage);
		oHTMLExport.setIncludeNodeAnchors(bIncludeNodeAnchors);
		oHTMLExport.setIncludeDetailAnchors(bIncludeDetailAnchors);
		oHTMLExport.setUseAnchorNumbers(bUseAnchorNumbers);
		if (!bUseAnchorNumbers)
			oHTMLExport.setAnchorImage(sAnchorImage);

		oHTMLExport.setTitle(titlefield.getText());
		oHTMLExport.setDisplayInDifferentPages(bDisplayInDifferentPages);
		oHTMLExport.setDisplayDetailDates(bDisplayDetailDates);
		oHTMLExport.setHideNodeNoDates(bHideNodeNoDates);
		oHTMLExport.setIncludeNavigationBar(bIncludeNavigationBar);
		oHTMLExport.setInlineView(bInlineView);
		oHTMLExport.setNewView(bNewView);
		oHTMLExport.setIncludeViews(bIncludeViews);
		oHTMLExport.setIncludeTags(bIncludeTags);

		oHTMLExport.setIncludeFiles(bIncludeReferences);

		boolean sucessful = false;
		
		if (printExport(oHTMLExport, otherViews.isSelected(), bSelectedViewsOnly, depth)) {
			oHTMLExport.print();
			sucessful = true;
		}

		return sucessful;
	}

	/**
	 * Update the list of view to export;
	 */
	public void updateViewsList() {
		String sViews = "";
		Vector views = checkSelectedViews();
		int count = views.size();
		for (int i = 0; i < count; i++) {
			View view = (View)views.elementAt(i);
			sViews += view.getLabel()+"\n";
		}
		oTextArea.setText(sViews);											
	}
	
	/** Return true if any views are selected, else false;*/
	private boolean hasSelectedViews() {

		Enumeration nodes = null;

		if (currentFrame instanceof UIMapViewFrame) {
			UIViewPane uiViewPane = ((UIMapViewFrame)currentFrame).getViewPane();
			nodes = uiViewPane.getSelectedNodes();
			for(Enumeration en = nodes; en.hasMoreElements();) {
				UINode uinode = (UINode)en.nextElement();
				if (uinode.getNode() instanceof View) {
					return true;
				}
			}
		}
		else {
			UIList uiList = ((UIListViewFrame)currentFrame).getUIList();
			nodes = uiList.getSelectedNodes();
			for(Enumeration en = nodes; en.hasMoreElements();) {
				NodePosition nodepos = (NodePosition)en.nextElement();
				if (nodepos.getNode() instanceof View) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Get the views to export depending on user options to display
	 */
	private Vector checkSelectedViews() {

		model = ProjectCompendium.APP.getModel();
		session = model.getSession();
		vs = model.getViewService();

		Vector selectedViews = new Vector();

		// IF MULTIPLE VIEWS
		if (otherViews.isSelected()) {
			if (viewsDialog != null) {
				JTable table = viewsDialog.getTable();
				int [] selection = table.getSelectedRows();
				for (int i = 0; i < selection.length; i++) {
					View view = (View)table.getModel().getValueAt(selection[i],0);
					selectedViews.addElement(view);
				}
			}
		}
		else if (bSelectedViewsOnly) {
			Enumeration nodes = null;
			Vector vtTemp = new Vector();
			if (currentFrame instanceof UIMapViewFrame) {
				UIViewPane uiViewPane = ((UIMapViewFrame)currentFrame).getViewPane();
				nodes = uiViewPane.getSelectedNodes();
				for(Enumeration en = nodes; en.hasMoreElements();) {
					UINode uinode = (UINode)en.nextElement();
					if (uinode.getNode() instanceof View) {
						vtTemp.addElement(uinode.getNodePosition());
					}
				}
			}
			else {
				UIList uiList = ((UIListViewFrame)currentFrame).getUIList();
				nodes = uiList.getSelectedNodes();
				for(Enumeration en = nodes; en.hasMoreElements();) {
					NodePosition nodepos = (NodePosition)en.nextElement();
					if (nodepos.getNode() instanceof View) {
						vtTemp.addElement(nodepos);
					}
				}
			}
			
			//SORT VIEWS VECTOR BY DECENDING Y POSITION
			for (int i = 0; i < vtTemp.size(); i++) {
				int yPosition = ((NodePosition)vtTemp.elementAt(i)).getYPos();
				for (int j = i+1; j < vtTemp.size(); j++) {
					int secondYPosition = ((NodePosition)vtTemp.elementAt(j)).getYPos();

					if (secondYPosition < yPosition) {
						NodePosition np = (NodePosition)vtTemp.elementAt(i);
						vtTemp.setElementAt(vtTemp.elementAt(j), i);
						vtTemp.setElementAt(np, j);
						yPosition = ((NodePosition)vtTemp.elementAt(i)).getYPos();
					}
				}
			}

			for(int j=0; j < vtTemp.size(); j++) {
				NodePosition nodePos = (NodePosition)vtTemp.elementAt(j);
				View innerview = (View)nodePos.getNode();
				selectedViews.addElement(innerview);
			}			
		}
		else {
			selectedViews.addElement(currentView);
		}
		
		return selectedViews;
	}	
	
	/**
	 * Get the views to export depending on user options.
	 */
	private Vector getSelectedViews(HTMLOutline oHTMLExport, boolean otherViews, boolean bSelectedViewsOnly, int depth) {
		model = ProjectCompendium.APP.getModel();
		session = model.getSession();
		vs = model.getViewService();

		Vector selectedViews = new Vector();

		// IF MULTIPLE VIEWS
		if (otherViews) {
			oHTMLExport.setCurrentViewAsHomePage(false);

			JTable table = viewsDialog.getTable();
			int [] selection = table.getSelectedRows();
			for (int i = 0; i < selection.length; i++) {
				View view = (View)table.getModel().getValueAt(selection[i],0);
				selectedViews.addElement(view);
			}

			if (depth == 1) {
				for (int i = 0; i < selection.length; i++) {
					View view = (View)table.getModel().getValueAt(selection[i],0);
					htCheckDepth.put((Object)view.getId(), view);
					selectedViews = getChildViews(view, selectedViews, false);
				}
			}
			else if (depth == 2) {
				for (int i = 0; i < selection.length; i++) {
					View view = (View)table.getModel().getValueAt(selection[i],0);
					htCheckDepth.put((Object)view.getId(), view);
					selectedViews = getChildViews(view, selectedViews, true);
				}
			}
		}
		else if (bSelectedViewsOnly) {

			oHTMLExport.setCurrentViewAsHomePage(false);
			Enumeration nodes = null;
			Vector vtTemp = new Vector();

			if (currentFrame instanceof UIMapViewFrame) {
				UIViewPane uiViewPane = ((UIMapViewFrame)currentFrame).getViewPane();
				nodes = uiViewPane.getSelectedNodes();
				for(Enumeration en = nodes; en.hasMoreElements();) {
					UINode uinode = (UINode)en.nextElement();
					if (uinode.getNode() instanceof View) {
						vtTemp.addElement(uinode.getNodePosition());
					}
				}
			}
			else {
				UIList uiList = ((UIListViewFrame)currentFrame).getUIList();
				nodes = uiList.getSelectedNodes();
				for(Enumeration en = nodes; en.hasMoreElements();) {
					NodePosition nodepos = (NodePosition)en.nextElement();
					if (nodepos.getNode() instanceof View) {
						vtTemp.addElement(nodepos);
					}
				}
			}

			//SORT VIEWS VECTOR BY DECENDING Y POSITION
			for (int i = 0; i < vtTemp.size(); i++) {
				int yPosition = ((NodePosition)vtTemp.elementAt(i)).getYPos();
				for (int j = i+1; j < vtTemp.size(); j++) {
					int secondYPosition = ((NodePosition)vtTemp.elementAt(j)).getYPos();

					if (secondYPosition < yPosition) {
						NodePosition np = (NodePosition)vtTemp.elementAt(i);
						vtTemp.setElementAt(vtTemp.elementAt(j), i);
						vtTemp.setElementAt(np, j);
						yPosition = ((NodePosition)vtTemp.elementAt(i)).getYPos();
					}
				}
			}

			for(int j=0; j < vtTemp.size(); j++) {
				NodePosition nodePos = (NodePosition)vtTemp.elementAt(j);
				View innerview = (View)nodePos.getNode();
				selectedViews.addElement(innerview);
			}

			//ADD THE CHILD VIEWS TO THE childViews VECTOR
			if (depth == 1) {
				for (int i = 0; i < vtTemp.size(); i++) {
					NodePosition nodePos = (NodePosition)vtTemp.elementAt(i);
					View view = (View)nodePos.getNode();
					htCheckDepth.put((Object)view.getId(), view);
					selectedViews = getChildViews(view, selectedViews, false);
				}
			} else if (depth == 2) {
				for (int i = 0; i < vtTemp.size(); i++) {
					NodePosition nodePos = (NodePosition)vtTemp.elementAt(i);
					View view = (View)nodePos.getNode();
					htCheckDepth.put((Object)view.getId(), view);
					selectedViews = getChildViews(view, selectedViews, true);
				}
			}
		}
		else {
			// IF JUST CURRENT VIEW
			oHTMLExport.setCurrentViewAsHomePage(true);

			selectedViews.addElement(currentView);

			if (depth == 1) {
				htCheckDepth.put((Object)currentView.getId(), currentView);
				selectedViews = getChildViews(currentView, selectedViews, false);
			}
			else if (depth == 2) {
				htCheckDepth.put((Object)currentView.getId(), currentView);
				selectedViews = getChildViews(currentView, selectedViews, true);
			}
		}

		return selectedViews;
	}

	/**
	 * Helper method when getting view to export.
	 * @param view com.compendium.core.datamodel.View, the view to get the child nodes for.
	 * @param childViews, the list of views aquired.
	 * @param fullDepth, are we searching to full depth?
	 */
	private Vector getChildViews(View view, Vector childViews, boolean fullDepth) {
		
		try {
			Vector vtTemp = vs.getNodePositions(session, view.getId());
			Vector nodePositionList = new Vector();

			//EXTRACT THE VIEWS AND ADD TO nodePositionList VECTOR
			for(Enumeration en = vtTemp.elements();en.hasMoreElements();) {
				NodePosition nodePos = (NodePosition)en.nextElement();
				NodeSummary node = nodePos.getNode();
				if (node instanceof View) {
					nodePositionList.addElement(nodePos);
				}
			}

			//SORT VIEWS VECTOR BY DECENDING Y POSITION
			for (int i = 0; i < nodePositionList.size(); i++) {
				int yPosition = ((NodePosition)nodePositionList.elementAt(i)).getYPos();
				for (int j = i+1; j < nodePositionList.size(); j++) {
					int secondYPosition = ((NodePosition)nodePositionList.elementAt(j)).getYPos();

					if (secondYPosition < yPosition) {
						NodePosition np = (NodePosition)nodePositionList.elementAt(i);
						nodePositionList.setElementAt(nodePositionList.elementAt(j), i);
						nodePositionList.setElementAt(np, j);
						yPosition = ((NodePosition)nodePositionList.elementAt(i)).getYPos();
					}
				}
			}
			
			//ADD THE CHILD VIEWS TO THE childViews VECTOR
			for (int k = 0; k < nodePositionList.size(); k++) {
				NodePosition np = (NodePosition)nodePositionList.elementAt(k);
				View innerview = (View)np.getNode();

				if (!htCheckDepth.containsKey((Object)innerview.getId())) {
					htCheckDepth.put((Object)innerview.getId(), innerview);
					childViews.addElement(np.getNode());
				}
			}

			if (fullDepth) {
				//GET CHILD VIEWS CHILDREN
				for (int j = 0; j < nodePositionList.size(); j++) {
					NodePosition np = (NodePosition)nodePositionList.elementAt(j);
					View innerview = (View)np.getNode();

					if (!htChildrenAdded.containsKey((Object)innerview.getId())) {
						htChildrenAdded.put((Object)innerview.getId(), innerview);
						childViews = getChildViews(innerview, childViews, fullDepth);
					}
				}
			}
		}
		catch (Exception e) {
			ProjectCompendium.APP.displayError("Exception: (UIExportDialog.getChildViews) \n\n" + e.getMessage());
		}

		return childViews;
	}


	/**
	 * Create the HTML files.
	 */
	public boolean printExport(HTMLOutline oHTMLExport, boolean bOtherViews, boolean bSelectedViewsOnly, int depth) {
		ProjectCompendium.APP.setWaitCursor();
		Vector selectedViews = getSelectedViews(oHTMLExport, bOtherViews, bSelectedViewsOnly, depth);
		if (selectedViews.size() == 0)
			return true;

		arrange = new UIArrangeLeftRight();

   		// CYCLE THROUGH selectedViews VECTOR
		try {
			int count = selectedViews.size();
			for(int i=0; i < count; i++) {

				//clear the hashtables and vectors for a new export
				htNodesLevel.clear();
				htNodes.clear();
				htNodesBelow.clear();

				View view = (View)selectedViews.elementAt(i);
				if (view == null)
					continue;
				
				if (!view.isMembersInitialized()) {
					view.initializeMembers();
				}

				oHTMLExport.runGenerator((NodeSummary)view, 0, -1);
				ProjectCompendium.APP.setStatus("Calculating export data ......");

				if (!arrange.processView(view)) {
					return false;
				}

				htNodes = arrange.getNodes();
				htNodesLevel = arrange.getNodesLevel();
				htNodesBelow = arrange.getNodesBelow();

				nodeLevelList = arrange.getNodeLevelList();

				//now print the nodes
				ProjectCompendium.APP.setStatus("Generating export file ......");

				if (nodeLevelList.size() > 0) {
					// CYCLE THROUGH NODES SORTED BY YPOS AND PRINT THEM AND THIER CHILDREN
					for(Enumeration f = ((Vector)nodeLevelList.elementAt(0)).elements();f.hasMoreElements();) {

						String nodeToPrintId = (String)f.nextElement();
						NodeSummary nodeToPrint = (NodeSummary)htNodes.get(nodeToPrintId);						
						if (view.getType() == ICoreConstants.LISTVIEW) {
							printNode(nodeToPrintId, true, oHTMLExport);
						}
						else {
							printNode(nodeToPrintId, false, oHTMLExport);
						}
					}
				}
				ProjectCompendium.APP.setStatus("Finished exporting " + view.getLabel() + " to HTML.");
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ProjectCompendium.APP.displayError("Exception: (UIExportDialog.printExport) \n\n" + ex.getMessage());
		}

		ProjectCompendium.APP.setDefaultCursor();
		ProjectCompendium.APP.setStatus("");

		return true;
	}

	/**
	 * Create the HTML files fot eh given node.
 	 * @param nodeToPrintId, the id of the node to process.
	 * @param printingList, is the current view a list.
	 */
	private void printNode(String nodeToPrintId, boolean printingList, HTMLOutline oHTMLExport) {

		if (!printingList) {
			nodeIndex = -1;
		} else {
			nodeIndex++;
		}

		NodeSummary nodeToPrint = (NodeSummary)htNodes.get(nodeToPrintId);

		int lev = ((Integer)htNodesLevel.get(nodeToPrint.getId())).intValue();

		oHTMLExport.runGenerator(nodeToPrint, lev, nodeIndex);

		Vector nodeChildren = (Vector)htNodesBelow.get(nodeToPrintId);
		if (nodeChildren != null) {
			//System.out.println("printing children for "+nodeToPrint.getLabel());

			for (int i = 0; i < nodeChildren.size(); i++) {
				printNode((String)nodeChildren.elementAt(i), printingList, oHTMLExport);
			}
		}
	}

	/**
	 * Load the user saved options for exporting.
	 */
	private void loadProperties() {

		File optionsFile = new File(EXPORT_OPTIONS_FILE_NAME);
		optionsProperties = new Properties();
		if (optionsFile.exists()) {
			try {
				optionsProperties.load(new FileInputStream(EXPORT_OPTIONS_FILE_NAME));

				String value = optionsProperties.getProperty("anchorimage");
				if (value != null) {
					setAnchorImage(value);
				}

				value = optionsProperties.getProperty("includerefs");
				if (value != null) {
					if (value.toLowerCase().equals("yes")) {
						bIncludeReferences = true;
					} else {
						bIncludeReferences = false;
					}
				}

				value = optionsProperties.getProperty("zip");
				if (value != null) {
					if (value.toLowerCase().equals("yes")) {
						bToZip = true;
					} else {
						bToZip = false;
					}
				}

				value = optionsProperties.getProperty("includenodeanchors");
				if (value != null) {
					if (value.toLowerCase().equals("yes")) {
						bIncludeNodeAnchors = true;
					} else {
						bIncludeNodeAnchors = false;
					}
				}

				value = optionsProperties.getProperty("includedetailanchors");
				if (value != null) {
					if (value.toLowerCase().equals("yes")) {
						bIncludeDetailAnchors = true;
					} else {
						bIncludeDetailAnchors = false;
					}
				}

				value = optionsProperties.getProperty("useanchornumbers");
				if (value != null) {
					if (value.toLowerCase().equals("yes")) {
						bUseAnchorNumbers = true;
						bUseAnchorImages = false;
					} else {
						bUseAnchorNumbers = false;
						bUseAnchorImages = true;
					}
				}

				value = optionsProperties.getProperty("depth");
				if (value != null) {
					if (value.equals("1"))
						depth = 1;
					else if (value.equals("2"))
						depth = 2;
					else
						depth = 0;
				}

				value = optionsProperties.getProperty("selectedviewsonly");
				if (value != null) {
					if (value.toLowerCase().equals("yes")) {
						bSelectedViewsOnly = true;
					}
					else {
						bSelectedViewsOnly = false;
					}
				}

				value = optionsProperties.getProperty("otherviews");
				if (value != null) {
					if (value.toLowerCase().equals("yes")) {
						bOtherViews = true;
					}
					else {
						bOtherViews = false;
					}
				}

				value = optionsProperties.getProperty("nodedetail");
				if (value != null) {
					if (value.toLowerCase().equals("yes")) {
						bIncludeNodeDetail = true;
					} else {
						bIncludeNodeDetail = false;
					}
				}

				value = optionsProperties.getProperty("nodedetaildate");
				if (value != null) {
					if (value.toLowerCase().equals("yes"))
						bIncludeNodeDetailDate = true;
					else
						bIncludeNodeDetailDate = false;
				}

				value = optionsProperties.getProperty("hidenodenodate");
				if (value != null) {
					if (value.toLowerCase().equals("yes"))
						bHideNodeNoDates = true;
					else
						bHideNodeNoDates = false;
				}

				value = optionsProperties.getProperty("todate");
				if (value != null) {
					try  {
						toDate = new Long(value).longValue();
					}
					catch(Exception io){
						System.out.println("cannot convert todate = "+value);
					}
				}

				value = optionsProperties.getProperty("fromdate");
				if (value != null) {
					try  {
						fromDate = new Long(value).longValue();
					}
					catch(Exception io){
						System.out.println("cannot convert fromdate = "+value);
					}
				}

				value = optionsProperties.getProperty("nodeauthor");
				if (value != null) {
					if (value.toLowerCase().equals("yes")) {
						bIncludeNodeAuthor = true;
					} else {
						bIncludeNodeAuthor = false;
					}
				}

				value = optionsProperties.getProperty("nodeimage");
				if (value != null) {
					if (value.toLowerCase().equals("yes")) {
						bIncludeImage = true;
					} else {
						bIncludeImage = false;
					}
				}

				value = optionsProperties.getProperty("includelinks");
				if (value != null) {
					if (value.toLowerCase().equals("yes")) {
						bIncludeLinks = true;
					} else {
						bIncludeLinks = false;
					}
				}

				value = optionsProperties.getProperty("displaydetaildates");
				if (value != null) {
					if (value.toLowerCase().equals("yes")) {
						bDisplayDetailDates = true;
					} else {
						bDisplayDetailDates = false;
					}
				}

				value = optionsProperties.getProperty("displayindifferentpages");
				if (value != null) {
					if (value.toLowerCase().equals("yes")) {
						bDisplayInDifferentPages = true;
					} else {
						bDisplayInDifferentPages = false;
					}
				}

				value = optionsProperties.getProperty("includenavigationbar");
				if (value != null) {
					if (value.toLowerCase().equals("yes")) {
						bIncludeNavigationBar = true;
					} else {
						bIncludeNavigationBar = false;
					}
				}

				value = optionsProperties.getProperty("inlineview");
				if (value != null) {
					if (value.toLowerCase().equals("yes")) {
						bInlineView = true;
					} else {
						bInlineView = false;
					}
				}

				value = optionsProperties.getProperty("includeviews");
				if (value != null) {
					if (value.toLowerCase().equals("yes")) {
						bIncludeViews = true;
					} else {
						bIncludeViews = false;
					}
				}

				value = optionsProperties.getProperty("includetags");
				if (value != null) {
					if (value.toLowerCase().equals("yes")) {
						bIncludeTags = true;
					} else {
						bIncludeTags = false;
					}
				}

				value = optionsProperties.getProperty("newview");
				if (value != null) {
					if (value.toLowerCase().equals("yes")) {
						bNewView = true;
					} else {
						bNewView = false;
					}
				}

				value = optionsProperties.getProperty("openafter");
				if (value != null) {
					if (value.toLowerCase().equals("yes")) {
						bOpenAfter = true;
					} else {
						bOpenAfter = false;
					}
				}

			} catch (IOException e) {
				ProjectCompendium.APP.displayError("Error reading export options properties. Default values will be used");
			}
		}
	}

	/**
	 * Handle the close action. Closes the export dialog.
	 */
	public void onCancel() {
		onCancel(false);
	}

	/**
	 * Handle the close action. Saves the current setting and closes the export dialog.
	 */
	public void onCancel(boolean successful) {

		if (viewsDialog != null)
			viewsDialog.dispose();

		setVisible(false);

		try {
			if (bIncludeReferences == true) {
				optionsProperties.put("includerefs", "yes");
			}
			else {
				optionsProperties.put("includerefs", "no");
			}

			if (bToZip == true) {
				optionsProperties.put("zip", "yes");
			}
			else {
				optionsProperties.put("zip", "no");
			}

			if (bIncludeNodeAnchors == true) {
				optionsProperties.put("includenodeanchors", "yes");
			}
			else {
				optionsProperties.put("includenodeanchors", "no");
			}

			if (bIncludeDetailAnchors == true) {
				optionsProperties.put("includedetailanchors", "yes");
			}
			else {
				optionsProperties.put("includedetailanchors", "no");
			}

			optionsProperties.put("anchorimage", sAnchorImage);

			if (bUseAnchorNumbers == true) {
				optionsProperties.put("useanchornumbers", "yes");
			}
			else {
				optionsProperties.put("useanchornumbers", "no");
			}

			if (depth == 2) {
				optionsProperties.put("depth", "2");
			}
			else if (depth == 1) {
				optionsProperties.put("depth", "1");
			}
			else {
				optionsProperties.put("depth", "0");
			}

			if (bSelectedViewsOnly == true) {
				optionsProperties.put("selectedviewsonly", "yes");
			}
			else {
				optionsProperties.put("selectedviewsonly", "no");
			}

			if (bOtherViews == true) {
				optionsProperties.put("otherviews", "yes");
			}
			else {
				optionsProperties.put("otherviews", "no");
			}

			if (bIncludeNodeAuthor == true) {
				optionsProperties.put("nodeauthor", "yes");
			}
			else {
				optionsProperties.put("nodeauthor", "no");
			}

			if (bIncludeNodeDetail == true) {
				optionsProperties.put("nodedetail", "yes");
			}
			else {
				optionsProperties.put("nodedetail", "no");
			}

			if (bIncludeNodeDetailDate == true) {
				optionsProperties.put("nodedetaildate", "yes");
			}
			else {
				optionsProperties.put("nodedetaildate", "no");
			}

			if (bHideNodeNoDates == true) {
				optionsProperties.put("hidenodenodate", "yes");
			}
			else {
				optionsProperties.put("hidenodenodate", "no");
			}

			optionsProperties.put("todate", new Long(toDate).toString());
			optionsProperties.put("fromdate", new Long(fromDate).toString());

			if (bIncludeImage == true) {
				optionsProperties.put("nodeimage", "yes");
			}
			else {
				optionsProperties.put("nodeimage", "no");
			}

			if (bIncludeLinks == true) {
				optionsProperties.put("includelinks", "yes");
			}
			else {
				optionsProperties.put("includelinks", "no");
			}

			if (bDisplayDetailDates == true) {
				optionsProperties.put("displaydetaildates", "yes");
			}
			else {
				optionsProperties.put("displaydetaildates", "no");
			}

			if (bDisplayInDifferentPages == true) {
				optionsProperties.put("displayindifferentpages", "yes");
			}
			else {
				optionsProperties.put("displayindifferentpages", "no");
			}

			if (bIncludeNavigationBar == true) {
				optionsProperties.put("includenavigationbar", "yes");
			}
			else {
				optionsProperties.put("includenavigationbar", "no");
			}

			if (bIncludeViews == true) {
				optionsProperties.put("includeviews", "yes");
			}
			else {
				optionsProperties.put("includeviews", "no");
			}

			if (bIncludeTags == true) {
				optionsProperties.put("includetags", "yes");
			}
			else {
				optionsProperties.put("includetags", "no");
			}

			if (bInlineView == true) {
				optionsProperties.put("inlineview", "yes");
			}
			else {
				optionsProperties.put("inlineview", "no");
			}

			if (bNewView == true) {
				optionsProperties.put("newview", "yes");
			}
			else {
				optionsProperties.put("newview", "no");
			}

			if (bOpenAfter == true) {
				optionsProperties.put("openafter", "yes");
			}
			else {
				optionsProperties.put("openafter", "no");
			}


			optionsProperties.store(new FileOutputStream(EXPORT_OPTIONS_FILE_NAME), "Export Options");
		}
		catch (IOException e) {
			ProjectCompendium.APP.displayError("IO error occured while saving export options.");
		}

		dispose();

		if (fileName != null && successful && !bOpenAfter) {
			ProjectCompendium.APP.displayMessage("Finished exporting into " + fileName, "Export Finished");
		}
	}
}