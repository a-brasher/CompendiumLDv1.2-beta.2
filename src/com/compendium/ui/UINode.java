/******************************************************************************
 *                                                                            *
/*  (c) Copyright 2009 Verizon Communications USA and The Open University UK    *
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

package com.compendium.ui;

import java.awt.*;
import java.awt.image.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.sql.SQLException;

import java.beans.*;
import java.util.*;
import java.io.*;

import javax.swing.*;
import javax.help.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileSystemView;

import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.Node;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import com.compendium.ProjectCompendium;
import com.compendium.ui.*;
import com.compendium.ui.edits.*;
import com.compendium.ui.plaf.NodeUI;
import com.compendium.ui.plaf.TextRowElement;
import com.compendium.ui.plaf.ViewPaneUI;
import com.compendium.ui.popups.*;
import com.compendium.ui.linkgroups.*;
import com.compendium.ui.dialogs.UINodeContentDialog;
import com.compendium.core.datamodel.*;
import com.compendium.core.ICoreConstants;
import com.compendium.io.xml.XMLExport;
import com.compendium.learningdesign.core.ILdCoreConstants;
import com.compendium.learningdesign.core.LdTypeTagMaps;
import com.compendium.learningdesign.io.svg.SvgExport;
import com.compendium.learningdesign.io.xml.LdXMLCopyMaker;
import com.compendium.learningdesign.io.xml.LdXMLExport;
import com.compendium.learningdesign.io.xml.LdXMLGenerator;
import com.compendium.learningdesign.io.xml.LdXMLProcessor;
import com.compendium.meeting.*;
import com.compendium.ui.dialogs.*;

import com.compendium.learningdesign.ui.ILdUIConstants;
import com.compendium.learningdesign.ui.panels.UILdInformationDialog;
import com.compendium.learningdesign.ui.popups.*;
/**
 * Holds the data for and handles the events of a node in a map.
 *	All private instance variables change to protected by AJB to facilitate 
 *	construction of subclasses.
 * @author	Mohammed Sajid Ali / Michelle Bachler
 */
public class UINode extends JComponent implements PropertyChangeListener, SwingConstants,
										Transferable, DropTargetListener, DragSourceListener, DragGestureListener{

	static	{
		UIManager.getDefaults().put("NodeUI",  "com.compendium.ui.plaf.NodeUI");
	}
	/** A reference to the text property for PropertyChangeEvents.*/
    public static final String TEXT_PROPERTY 		= "text";

	/** A reference to the icon property for PropertyChangeEvents.*/
    public static final String ICON_PROPERTY 		= "icon";

	/** A reference to the children property for PropertyChangeEvents.*/
    public final static String CHILDREN_PROPERTY 	= "children";

	/** A reference to the rollover property for PropertyChangeEvents.*/
    public final static String ROLLOVER_PROPERTY	= "rollover";

	/** A reference to the node type property for PropertyChangeEvents.*/
    public final static String TYPE_PROPERTY		= "nodetype";

	/** The default font to use for node labels.*/
    private static final Font  NODE_FONT = new Font("Sans Serif", Font.PLAIN, 12);

 	/** The DataFlavour for external string based drag and drop operations.*/
 	public static final DataFlavor plainTextFlavor = DataFlavor.plainTextFlavor;

	/** The DataFlavour for internal string based drag and drop operations.*/
  	public static final DataFlavor localStringFlavor = DataFlavor.stringFlavor;

	/** The DataFlavour for internal object based drag and drop operations.*/
	public static 		DataFlavor nodeFlavor 			= null;

	/**
	 * The deafult node icon for this node.
	 * @uml.property  name="oDefaultIcon"
	 * @uml.associationEnd  
	 */
	protected ImageIcon		oDefaultIcon				= null;

	/**
	 * The current node icon for this node.
	 * @uml.property  name="oCurrentIcon"
	 * @uml.associationEnd  
	 */
	protected ImageIcon		oCurrentIcon				= null;

	/**
	 * Is this node currently selected?
	 * @uml.property  name="bSelected"
	 */
	protected boolean			bSelected					= false;

	/**
	 * Has this node been cut?
	 * @uml.property  name="bCut"
	 */
	protected boolean			bCut						= false;

	/**
	 * Has this node been rolloved over?
	 * @uml.property  name="bRollover"
	 */
	protected boolean			bRollover					= false;

	/**
	 * Indicates if the image has been scaled.
	 * @uml.property  name="bIsImageScaled"
	 */
	protected boolean			bIsImageScaled				= false;

	/**
	 * The label text for this node.
	 * @uml.property  name="sText"
	 */
	protected String			sText						= "";

	/**
	 * The distance for the gap between the node icon and its text.
	 * @uml.property  name="nIconTextGap"
	 */
	protected int			nIconTextGap					= 4;

	/**
	 * The current font to use for the node label text.
	 * @uml.property  name="oFont"
	 */
	protected Font		oFont							= null;

	/**
	 * A List of the Link objects associated with this node.
	 * @uml.property  name="htLinks"
	 * @uml.associationEnd  inverse="oToNode:com.compendium.ui.UILink" qualifier="getId:java.lang.String com.compendium.ui.UILink"
	 */
	protected Hashtable	htLinks							= new Hashtable();

	/**
	 * The drag source object associated with this node.
	 * @uml.property  name="dragSource"
	 */
	protected DragSource dragSource = null;

	/**
	 * The drop target object associated with this node.
	 * @uml.property  name="dropTarget"
	 */
	protected DropTarget dropTarget = null;

	/**
	 * The node right-click popup menu associated with this node - null if one has not been opened yet.
	 * @uml.property  name="nodePopup"
	 * @uml.associationEnd  
	 */
	protected UINodePopupMenu			nodePopup			= null;

	/**
	 * The node contents dialog associated with this node - null if one has not been opened yet.
	 * @uml.property  name="contentDialog"
	 * @uml.associationEnd  inverse="oUINode:com.compendium.ui.dialogs.UINodeContentDialog"
	 */
	protected UINodeContentDialog 	contentDialog		= null;

	/**
	 * The node data object associated with this UINode.
	 * @uml.property  name="oNode"
	 * @uml.associationEnd  
	 */
	protected NodeSummary		oNode						= null;

	/**
	 * The NodePosition ibject associated with this node.
	 * @uml.property  name="oPos"
	 * @uml.associationEnd  
	 */
	protected NodePosition  	oPos						= null;

	/**
	 * The node type of this node.
	 * @uml.property  name="oNodeType"
	 */
	protected int				oNodeType					= -1;

	/**
	 * The current scale factor for this node in its parent view.
	 * @uml.property  name="scale"
	 */
	protected double scale 								= 1.0;

	/**
	 * A local reference to the name of the current computer platform.
	 * @uml.property  name="focusGainedDate"
	 */
	//protected String os 									= "";

	protected Date			focusGainedDate				= null;
	
	/**
	 * @uml.property  name="originalLabel"
	 */
	protected String 			originalLabel				= null;
	
	/**
	 * The user author name of the current user
	 * @uml.property  name="sAuthor"
	 */
	protected String 			sAuthor = "";

	/** Start of added by Andrew	**/
	/**
	 * The learning design information dialog associated with this node - null if one has not been opened yet.
	 * @uml.property  name="ldInfoDialog"
	 * @uml.associationEnd  
	 */
	protected UILdInformationDialog  	ldInfoDialog		= null;
	
	/**	The SVG Document representation of this UINode	**/
	private Document oSvgRepresentation = null; 

	/** End of added by Andrew	**/

	/**
	 * Create a new UINode instance with the given NodePosition object for data.
	 * @param nodePos the object with the node data for this UINode.
	 * @param sAuthor the author anme of the current user.
	 * @param sUserID the id of the current user.
	 */
	public UINode(NodePosition nodePos, String sAuthor) {
	    //os = ProjectCompendium.platform.toLowerCase();
	    
	    dragSource = new DragSource();
	    dragSource.createDefaultDragGestureRecognizer((Component)this, DnDConstants.ACTION_LINK, this);

	    dropTarget = new DropTarget(this, this);
	    nodeFlavor = new DataFlavor(this.getClass(), "UINode");

	    oPos = nodePos;

	    setDefaultFont();

	    this.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
	    this.sAuthor = sAuthor;
	    
	    addFocusListener( new FocusListener() {
			public void focusGained(FocusEvent e) {
				focusGainedDate = new Date();
				originalLabel = oPos.getNode().getLabel();
			    repaint();
			}
			public void focusLost(FocusEvent e) {

				if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents()
						&& (ProjectCompendium.APP.oMeetingManager.getMeetingType() == MeetingManager.RECORDING)) {

					Date focusLostDate = new Date();
					// If the node had the focus for more than 5 seconds, record the event.
					if ( (focusLostDate.getTime()) - (focusGainedDate.getTime()) > 5000) {
						ProjectCompendium.APP.oMeetingManager.addEvent(
								new MeetingEvent(ProjectCompendium.APP.oMeetingManager.getMeetingID(),
												 ProjectCompendium.APP.oMeetingManager.isReplay(),
												 MeetingEvent.NODE_FOCUSED_EVENT,
												 oPos.getView(),
												 oPos.getNode()));
					}
				}
				
				if (oNode == null) {
					oNode = oPos.getNode();
				}
				
				String sSource = oNode.getSource();
				if (FormatProperties.startUDigCommunications &&
						sSource.startsWith("UDIG") && oNode.getType() == ICoreConstants.MAPVIEW) {
					String sCurrentLabel = oPos.getNode().getLabel();
					if (!sCurrentLabel.equals(originalLabel)) {
						ProjectCompendium.APP.oUDigCommunicationManager.editLabel(sSource+"&&"+sCurrentLabel);
					}
				}

			    getUI().resetEditing();
			    repaint();
			}
	    });

	    NodeSummary node = nodePos.getNode();
    	setNode(node);
	    updateUI();
	}


/*** DND EVENTS ***/

// TRANSFERABLE

    /**
     * Returns an array of DataFlavor objects indicating the flavors the data
     * can be provided in.
     * @return an array of data flavors in which this data can be transferred
     */
	public DataFlavor[] getTransferDataFlavors() {
	    DataFlavor[] flavs = {	UINode.nodeFlavor,
	    						UINode.plainTextFlavor,
    							UINode.localStringFlavor};

	    return flavs;
	}

    /**
     * Returns whether or not the specified data flavor is supported for
     * this object.
     * @param flavor the requested flavor for the data
     * @return boolean indicating whether or not the data flavor is supported
     */
	public boolean isDataFlavorSupported(DataFlavor flavor) {
	    if (flavor.getHumanPresentableName().equals("UINode") ||
	        flavor == UINode.plainTextFlavor ||
	        flavor == UINode.localStringFlavor) {
			return true;
		}

	    return false;
	}

    /**
     * Returns an object which represents the data to be transferred.  The class
     * of the object returned is defined by the representation class of the flavor.
     *
     * @param flavor the requested flavor for the data
     * @see DataFlavor#getRepresentationClass
     * @exception IOException                if the data is no longer available in the requested flavor.
     * @exception UnsupportedFlavorException if the requested data flavor is not supported.
     */
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {

	    //System.out.println("in getTransferData flavour = "+flavor.getHumanPresentableName());

		if (flavor.equals(UINode.plainTextFlavor)) {
	    	String charset = flavor.getParameter("charset").trim();
      		if(charset.equalsIgnoreCase("unicode")) {
				return new ByteArrayInputStream(oNode.getId().getBytes("Unicode"));
			}
			else {
				return new ByteArrayInputStream(oNode.getId().getBytes("iso8859-1"));
			}
    	}
    	else if (UINode.localStringFlavor.equals(flavor)) {
      		return oNode.getId();
    	}
	    else if (flavor.getHumanPresentableName().equals("UINode")) {
			return (Object)new String(oPos.getView().getId()+"/"+oNode.getId());
	    }
	    else
			throw new UnsupportedFlavorException(flavor);
	}

//SOURCE

    /**
     * A <code>DragGestureRecognizer</code> has detected
     * a platform-dependent drag initiating gesture and
     * is notifying this listener
     * in order for it to initiate the action for the user.
     * <P>Currently only used to create links on the Mac platform.</p>
     * @param e the <code>DragGestureEvent</code> describing the gesture that has just occurred.
     */
	public void dragGestureRecognized(DragGestureEvent e) {

	    /*InputEvent in = e.getTriggerEvent();

	    if (in instanceof MouseEvent) {
			MouseEvent evt = (MouseEvent)in;
			boolean isLeftMouse = SwingUtilities.isLeftMouseButton(evt);
			//boolean isRightMouse = SwingUtilities.isRightMouseButton(evt);

			if (isLeftMouse && evt.getID() == MouseEvent.MOUSE_PRESSED && evt.isAltDown()) {
				try {
					DragSource source = (DragSource)e.getDragSource();
				    source.startDrag(e, DragSource.DefaultCopyDrop, this, this);
				}
				catch(Exception io) {
				    io.printStackTrace();
				}
			}
		}*/

			/*if (os.indexOf("windows") != -1) {
			    if (isRightMouse || (isLeftMouse && isAltDown)) { // creating links
				System.out.println("In dragGestureRecognized = right mouse click recognised");
				DragSource source = (DragSource)e.getDragSource();
				source.addDragSourceListener(this);

				System.out.println("source = "+source);
				try {
				    System.out.println("DragSource.DefaultLinkDrop = "+DragSource.DefaultLinkDrop);
				    source.startDrag(e, DragSource.DefaultLinkDrop, this, this);
				    System.out.println("After source.startDrag");
				}
				catch(Exception io) {
				    System.out.println("IN CATCH "+io.getMessage());
				    io.printStackTrace();
				}
			    }
			}
			else {
			*/

			/*if (ProjectCompendium.isMac) {
				boolean isLeftMouse = SwingUtilities.isLeftMouseButton(evt);
				//boolean isRightMouse = SwingUtilities.isRightMouseButton(evt);
				boolean isAltDown = evt.isAltDown();

				//boolean isMiddleMouse = SwingUtilities.isMiddleMouseButton(evt);

			    if (isLeftMouse && isAltDown) { // creating links
				//if (isRightMouse) {
					DragSource source = (DragSource)e.getDragSource();

					/*DragGestureRecognizer dgr = e.getSourceAsDragGestureRecognizer();
					int act = e.getDragAction();
					Point ori = e.getDragOrigin();
					ArrayList evs = new ArrayList();

					for (Iterator it=e.iterator(); it.hasNext();) {
					    Object obj = it.next();
					    if (obj.equals(evt)) {
						MouseEvent me = new MouseEvent((Component)evt.getSource(), evt.getID(), evt.getWhen(),
							0, evt.getX(), evt.getY(), evt.getClickCount(), false, evt.getButton());
						System.out.println("AFTER CHANGE mouse event "+me.toString());

						evs.add(me);
					    }
					    else {
						evs.add(obj);
					    }
					}

					java.util.List evsList = (java.util.List)evs;
					DragGestureEvent newE = new DragGestureEvent(dgr, act, ori, evsList);
					*/

					//System.out.println("source = "+source);
					/*try {
					    source.startDrag(e, DragSource.DefaultLinkDrop, this, this);
					}
					catch(Exception io) {
					    io.printStackTrace();
					}
				}
			}*/
	    //}
	}

    /**
     * This method is invoked to signify that the Drag and Drop
     * operation is complete. The getDropSuccess() method of
     * the <code>DragSourceDropEvent</code> can be used to
     * determine the termination state. The getDropAction() method
     * returns the operation that the drop site selected
     * to apply to the Drop operation. Once this method is complete, the
     * current <code>DragSourceContext</code> and
     * associated resources become invalid.
	 * <p>Here, clears dummy links draw while creating the new link. </p>
     *
     * @param e the <code>DragSourceDropEvent</code>
     */
	public void dragDropEnd(DragSourceDropEvent e) {
	    //getUI().clearDummyLinks();
	}

    /**
     * Called as the cursor's hotspot enters a platform-dependent drop site.
     * This method is invoked when all the following conditions are true:
     * <UL>
     * <LI>The cursor's hotspot enters the operable part of a platform-
     * dependent drop site.
     * <LI>The drop site is active.
     * <LI>The drop site accepts the drag.
     * </UL>
	 * HERE THE METHOD DOES NOTHING.
     *
     * @param e the <code>DragSourceDragEvent</code>
     */
	public void dragEnter(DragSourceDragEvent e) {
	    //System.out.println("IN drag Enter on Source");
	}

    /**
     * Called as the cursor's hotspot exits a platform-dependent drop site.
     * This method is invoked when any of the following conditions are true:
     * <UL>
     * <LI>The cursor's hotspot no longer intersects the operable part
     * of the drop site associated with the previous dragEnter() invocation.
     * </UL>
     * OR
     * <UL>
     * <LI>The drop site associated with the previous dragEnter() invocation
     * is no longer active.
     * </UL>
     * OR
     * <UL>
     * <LI> The current drop site has rejected the drag.
     * </UL>
	 * HERE THE METHOD DOES NOTHING.
     *
     * @param e the <code>DragSourceEvent</code>
     */
	public void dragExit(DragSourceEvent e) {
	    //System.out.println("IN drag Exit of Source");
	}

    /**
     * Called as the cursor's hotspot moves over a platform-dependent drop site.
     * This method is invoked when all the following conditions are true:
     * <UL>
     * <LI>The cursor's hotspot has moved, but still intersects the
     * operable part of the drop site associated with the previous
     * dragEnter() invocation.
     * <LI>The drop site is still active.
     * <LI>The drop site accepts the drag.
     * </UL>
	 * <p>Draws the dummy links, while link crateion is in progress.</p>
     *
     * @param dsde the <code>DragSourceDragEvent</code>
     */
	public void dragOver(DragSourceDragEvent e) {
	    //System.out.println("draw dummy links and dragsourcedrag event at "+e.getLocation());
	    //getUI().drawDummyLinks(e.getLocation());
	}

    /**
     * Called when the user has modified the drop gesture.
     * This method is invoked when the state of the input
     * device(s) that the user is interacting with changes.
     * Such devices are typically the mouse buttons or keyboard
     * modifiers that the user is interacting with.
	 * HERE THE METHOD DOES NOTHING.
     *
     * @param e the <code>DragSourceDragEvent</code>
     */
	public void dropActionChanged(DragSourceDragEvent e) {
	    //System.out.println("IN dropActionChanged of Source");
	}

// TARGET

    /**
     * Called if the user has modified
     * the current drop gesture.
     * <P>HERE DOES NOTHING</P>
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dropActionChanged(DropTargetDragEvent e) {
	    //System.out.println("IN dropActionChanged of Target");
	}

    /**
     * Called when a drag operation is ongoing, while the mouse pointer is still
     * over the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     * <P>HERE DOES NOTHING</P>
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dragOver(DropTargetDragEvent e) {
	     //System.out.println("dragtargetdrag event at "+e.getLocation());
	}

    /**
     * Called while a drag operation is ongoing, when the mouse pointer has
     * exited the operable part of the drop site for the
     * <code>DropTarget</code> registered with this listener.
     * <P>HERE DOES NOTHING</P>
     * @param e the <code>DropTargetEvent</code>
     */
	public void dragExit(DropTargetEvent e) {
	    //System.out.println("In drag exit of Target");
	}

    /**
     * Called while a drag operation is ongoing, when the mouse pointer enters
     * the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     * <P>HERE DOES NOTHING</P>
     * @param e the <code>DropTargetDragEvent</code>
     */
	public void dragEnter(DropTargetDragEvent e) {
	    //System.out.println("dragEnter - about to accept DnDConstants.ACTION_LINK");
	    //e.acceptDrag(DnDConstants.ACTION_LINK);
	    //e.acceptDrag(DnDConstants.ACTION_MOVE);
	}

    /**
     * Called when the drag operation has terminated with a drop on
     * the operable part of the drop site for the <code>DropTarget</code>
     * registered with this listener.
     * <p>
	 * Process a drop for createing links - CURRENTLY ONLY ON THE MAC (WINDOW/LINUX use mouse events).
     * <P>
     * @param e the <code>DropTargetDropEvent</code>
     */
	public void drop(DropTargetDropEvent e) {
	    /*DropTarget drop = (DropTarget)e.getSource();

	    if (drop.getComponent() instanceof UINode) {

			UINode uinode = null;
			UIViewFrame oViewFrame = null;
			UIViewPane oViewPane = null;
			String sNodeID = "";
			try {
				Transferable trans = e.getTransferable();
				Object obj = trans.getTransferData(UINode.nodeFlavor);

				if (obj != null && (obj instanceof String)) {
				    String path = (String)obj;
				    int index = path.indexOf("/");
				    String sViewID = path.substring(0, index);
				    sNodeID = path.substring(index+1);

					View view  = oNode.getModel().getView(sViewID);
					if (view != null) {
						oViewFrame = ProjectCompendium.APP.getViewFrame(view, view.getLabel());
						oViewPane = ((UIMapViewFrame)oViewFrame).getViewPane();
				    	if (oViewPane != null) {
							Object obj2 = oViewPane.get(sNodeID);
							if (obj2 instanceof UINode) {
					    		uinode = (UINode)obj2;
							}
						}
			    	}
				}
			}
			catch(IOException io) {
				io.printStackTrace();
			}
			catch(UnsupportedFlavorException io) {
				io.printStackTrace();
			}

			final UINode fuinode = uinode;
			final UIViewFrame foViewFrame = oViewFrame;
			final UIViewPane foViewPane = oViewPane;
			final String fsNodeID = sNodeID;
			final DropTargetDropEvent fe = e;

			Thread thread = new Thread() {
				public void run() {

			if (foViewPane != null && fuinode != null && getType() == ICoreConstants.TRASHBIN) {
				fe.acceptDrop(DnDConstants.ACTION_MOVE);

				DeleteEdit edit = new DeleteEdit(foViewFrame);
				NodeUI nodeui = fuinode.getUI();
				nodeui.deleteNodeAndLinks(fuinode, edit);
				//oViewPane.repaint();
				foViewFrame.getUndoListener().postEdit(edit);

				fe.getDropTargetContext().dropComplete(true);
			}

				}
			};
			thread.start();
	    }*/
	}

	/**
	 * Set the help context for this node depending on node type.
	 * @param type, the node type to set the help string for.
	 */
	private void setHelp(int type) {

	    switch (type) {
		case ICoreConstants.ISSUE:
		    CSH.setHelpIDString(this,"node.node_types");
		    break;
		case ICoreConstants.POSITION:
		    CSH.setHelpIDString(this,"node.node_types");
		    break;
		case ICoreConstants.ARGUMENT:
		    CSH.setHelpIDString(this,"node.node_types");
		    break;
		case ICoreConstants.REFERENCE:
		    CSH.setHelpIDString(this,"node.refimage");
		    break;
		case ICoreConstants.DECISION:
		    CSH.setHelpIDString(this,"node.node_types");
		    break;
		case ICoreConstants.NOTE:
		    CSH.setHelpIDString(this,"node.node_types");
		    break;
		case ICoreConstants.MAPVIEW:
		    CSH.setHelpIDString(this,"node.views");
		    break;
		case ICoreConstants.LISTVIEW:
		    CSH.setHelpIDString(this,"node.views");
		    break;
		case ICoreConstants.PRO:
		    CSH.setHelpIDString(this,"node.node_types");
		    break;
		case ICoreConstants.CON:
		    CSH.setHelpIDString(this,"node.node_types");
		    break;
		case ICoreConstants.ISSUE_SHORTCUT:
		    CSH.setHelpIDString(this,"node.node_types");
		    break;
		case ICoreConstants.POSITION_SHORTCUT:
		case ICoreConstants.ARGUMENT_SHORTCUT:
		case ICoreConstants.REFERENCE_SHORTCUT:
		case ICoreConstants.DECISION_SHORTCUT:
		case ICoreConstants.NOTE_SHORTCUT:
		case ICoreConstants.MAP_SHORTCUT:
		case ICoreConstants.LIST_SHORTCUT:
		case ICoreConstants.PRO_SHORTCUT:
		case ICoreConstants.CON_SHORTCUT:
		    CSH.setHelpIDString(this,"node.shortcuts");
		    break;
		case ICoreConstants.TRASHBIN:
		    CSH.setHelpIDString(this,"basics.trashbin");
		    break;
	    }
	}

	/**
	 * Return the standard size icon for the given node type.
	 * @param type, the node type to return the icon for.
	 * @return ImageIcon, the icon for the given node type.
	 */
	public static ImageIcon getNodeImage(int type, boolean isSmall) {

	    if (isSmall) {
	    	return getNodeImageSmall(type);
	    }

	    ImageIcon img = null;
	    switch (type) {
		case ICoreConstants.ISSUE:
		    img = UIImages.getNodeIcon(IUIConstants.ISSUE_ICON);
		    break;

		case ICoreConstants.POSITION:
		    img = UIImages.getNodeIcon(IUIConstants.POSITION_ICON);
		    break;

		case ICoreConstants.ARGUMENT:
		    img = UIImages.getNodeIcon(IUIConstants.ARGUMENT_ICON);
		    break;

		case ICoreConstants.REFERENCE:
			img = UIImages.getNodeIcon(IUIConstants.REFERENCE_ICON);
		    break;

		case ICoreConstants.DECISION:
		    img = UIImages.getNodeIcon(IUIConstants.DECISION_ICON);
		    break;

		case ICoreConstants.NOTE:
		    img = UIImages.getNodeIcon(IUIConstants.NOTE_ICON);
		    break;

		case ICoreConstants.MAPVIEW:
	    	img = UIImages.getNodeIcon(IUIConstants.MAP_ICON);
		    break;

		case ICoreConstants.LISTVIEW:
		    img = UIImages.getNodeIcon(IUIConstants.LIST_ICON);
		    break;

		case ICoreConstants.PRO:
		    img = UIImages.getNodeIcon(IUIConstants.PRO_ICON);
		    break;

		case ICoreConstants.CON:
		    img = UIImages.getNodeIcon(IUIConstants.CON_ICON);
		    break;

		case ICoreConstants.ISSUE_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.ISSUE_SHORTCUT_ICON);
		    break;

		case ICoreConstants.POSITION_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.POSITION_SHORTCUT_ICON);
		    break;

		case ICoreConstants.ARGUMENT_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.ARGUMENT_SHORTCUT_ICON);
		    break;

		case ICoreConstants.REFERENCE_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.REFERENCE_SHORTCUT_ICON);
		    break;

		case ICoreConstants.DECISION_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.DECISION_SHORTCUT_ICON);
		    break;

		case ICoreConstants.NOTE_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.NOTE_SHORTCUT_ICON);
		    break;

		case ICoreConstants.MAP_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.MAP_SHORTCUT_ICON);
		    break;

		case ICoreConstants.LIST_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.LIST_SHORTCUT_ICON);
		    break;

		case ICoreConstants.PRO_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.PRO_SHORTCUT_ICON);
		    break;

		case ICoreConstants.CON_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.CON_SHORTCUT_ICON);
		    break;

		case ICoreConstants.TRASHBIN:
		    img = UIImages.getNodeIcon(IUIConstants.TRASHBIN_ICON);
		    break;
	    }
	    return img;
	}

	/**
	 * Return the small size icon for the given node type.
	 * @param type, the node type to return the icon for.
	 * @return ImageIcon, the icon for the given node type.
	 */
	public static ImageIcon getNodeImageSmall(int type) {
	    ImageIcon img = null;
	    
	    switch (type) {
		case ICoreConstants.ISSUE:
		    img = UIImages.getNodeIcon(IUIConstants.ISSUE_SM_ICON);
		    break;

		case ICoreConstants.POSITION:
		    img = UIImages.getNodeIcon(IUIConstants.POSITION_SM_ICON);
		    break;

		case ICoreConstants.ARGUMENT:
		    img = UIImages.getNodeIcon(IUIConstants.ARGUMENT_SM_ICON);
		    break;

		case ICoreConstants.REFERENCE:
		    img = UIImages.getNodeIcon(IUIConstants.REFERENCE_SM_ICON);
		    break;

		case ICoreConstants.DECISION:
		    img = UIImages.getNodeIcon(IUIConstants.DECISION_SM_ICON);
		    break;

		case ICoreConstants.NOTE:
		    img = UIImages.getNodeIcon(IUIConstants.NOTE_SM_ICON);
		    break;

		case ICoreConstants.MAPVIEW:
		    img = UIImages.getNodeIcon(IUIConstants.MAP_SM_ICON);
		    break;

		case ICoreConstants.LISTVIEW:
		    img = UIImages.getNodeIcon(IUIConstants.LIST_SM_ICON);
		    break;

		case ICoreConstants.PRO:
		    img = UIImages.getNodeIcon(IUIConstants.PRO_SM_ICON);
		    break;

		case ICoreConstants.CON:
		    img = UIImages.getNodeIcon(IUIConstants.CON_SM_ICON);
		    break;

		case ICoreConstants.ISSUE_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.ISSUE_SHORTCUT_SM_ICON);
		    break;

		case ICoreConstants.POSITION_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.POSITION_SHORTCUT_SM_ICON);
		    break;

		case ICoreConstants.ARGUMENT_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.ARGUMENT_SHORTCUT_SM_ICON);
		    break;

		case ICoreConstants.REFERENCE_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.REFERENCE_SHORTCUT_SM_ICON);
		    break;

		case ICoreConstants.DECISION_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.DECISION_SHORTCUT_SM_ICON);
		    break;

		case ICoreConstants.NOTE_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.NOTE_SHORTCUT_SM_ICON);
		    break;

		case ICoreConstants.MAP_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.MAP_SHORTCUT_SM_ICON);
		    break;

		case ICoreConstants.LIST_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.LIST_SHORTCUT_SM_ICON);
		    break;

		case ICoreConstants.PRO_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.PRO_SHORTCUT_SM_ICON);
		    break;

		case ICoreConstants.CON_SHORTCUT:
		    img = UIImages.getNodeIcon(IUIConstants.CON_SHORTCUT_SM_ICON);
		    break;

		case ICoreConstants.TRASHBIN:
		    img = UIImages.getNodeIcon(IUIConstants.TRASHBIN_SM_ICON);
		    break;

		default:
		    img = UIImages.getNodeIcon(IUIConstants.REFERENCE_SM_ICON);
		    break;
	    }
	    return img;
	}

	/**
	 * Return the small size icon for the given node type and ld subtype.
	 * This method has nbeen introduced so that class UILdNode will use
	 * a different implentation to return images which depend on ldType.
	 * Instances of UINode just return images dependent on type, i.e. 
	 * ldType is ignored. 
	 * @param type, the node type to return the icon for.
	 * @param ldType, the learning design type of the node
	 * * @return ImageIcon, the icon for the given node type.
	 */
	///** Not needed because UILdNode subclass handles this behaviour 
/**	
 public static ImageIcon getNodeImageSmall(int type, int ldType) {
		return(getNodeImageSmall(type));
	}
**/

	
	/**
	 * Return the small size icon for the given reference string for file types (not images).
	 * @param sRefString the reference string to get an icon for.
	 * @return ImageIcon the icon for the given node type.
	 */
	public static ImageIcon getReferenceImageSmall(String sRefString) {
		return UIReferenceNodeManager.getSmallReferenceIcon(sRefString);
	}
	
	/**
	 * Return the small size icon for the given reference string for file types (not images).
	 * @param sRefString the reference string to get an icon for.
	 * @return ImageIcon the icon for the given node type.
	 */
	public static ImageIcon getReferenceImage(String sRefString) {
		return UIReferenceNodeManager.getReferenceIcon(sRefString);		
	}
	
	/**
	* Returns the L&F object that renders this component.
	*
	* @return NodeUI object.
	*/
	public NodeUI getUI() {
	    return (NodeUI)ui;
	}

	/**
	* Sets the L&F object that renders this component.
	*
	* @param ui  the NodeUI L&F object
	* @see UIDefaults#getUI
	public void setUI(NodeUI ui) {
	    super.setUI(ui);
	}

	/**
	* Notification from the UIFactory that the L&F has changed.
	*
	* @see JComponent#updateUI
	*/
	public void updateUI() {
	  	NodeUI newNodeUI = (NodeUI)NodeUI.createUI(this);
	  	setUI(newNodeUI);
		invalidate();
	}

	/**
	* Returns a string that specifies the name of the l&f class
	* that renders this component.
	*
	* @return String "NodeUI"
	*
	* @see JComponent#getUIClassID
	* @see UIDefaults#getUI
	*/
	public String getUIClassID() {
	  	return "NodeUI";
	}

	
	
	
	/**
	 * Returns the type of this node.
	 *
	 * @return int, the type of this node.
	 * @see #setType
	 */
	public int getType() {
	    return oNode.getType();
	}
	
	/**
	 * Returns the learning design type of this node: the value
	 * ILdCoreConstants.iLD_TYPE_NO_TYPE which indicates tht this is not a 
	 * learning design node. 
	 *
	 * @return int, the type of this node.
	 * @see #setType
	 */
	public int getLdType() {
	    return ILdCoreConstants.iLD_TYPE_NO_TYPE;
	}

	/**
 	 * Change the type of this node to the given type.
	 * Return if type changed.
	 *
	 * @param nNewType, the new type for this node.
	 * @return boolean, true of the type was changed, else false.
	 * @see #getType
	 */
	public boolean setType( int nNewType ) {
		return setType(nNewType, false, -1);
	}

	

	/**
 	 * Change the type of this node to the given type.
	 * Return if type changed.
	 *
	 * @param newtype, the new type for this node.
	 * @param focus, indicates whether to focus the node after type change.
	 * @param position, indicaes to focus node label for editing after type change at the given point. -1 indicates not to focus label.
	 * @return boolean, true of the type was changed, else false.
	 * @see #getType
	 */
	public boolean setType( int nNewType, boolean focus, int position ) {

	    int nOldType = oNode.getType();
		if (nOldType == nNewType)
			return false;

	    boolean changeType = true;

	    if ( (nOldType == ICoreConstants.LISTVIEW || nOldType == ICoreConstants.MAPVIEW)
		 	&& (nNewType != ICoreConstants.LISTVIEW && nNewType != ICoreConstants.MAPVIEW) ) {

			int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, "WARNING! Nodes inside Maps/Lists will be deleted.\nIf they are not transcluded in another Map/List, they will be placed in the trashbin.\n\nAre you sure you still want to continue?",
						      "Change Type - "+oNode.getLabel(), JOptionPane.YES_NO_OPTION);
			if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION) {
		    	return false;
			}
			else {
		    	try {
					View view = (View)oNode;
					view.clearViewForTypeChange();
					ProjectCompendium.APP.setTrashBinIcon();
					ProjectCompendium.APP.removeView((View)view);
					oNode.setType(nNewType, sAuthor);
				}
		    	catch(Exception io) {
					io.printStackTrace();
					return false;
				}
			}
		}
	    else if ( nNewType == ICoreConstants.LISTVIEW || nNewType == ICoreConstants.MAPVIEW) {
			if (nOldType == ICoreConstants.LISTVIEW || nOldType == ICoreConstants.MAPVIEW) {
				try {
					if (nOldType == ICoreConstants.MAPVIEW) {
						View view = (View)oNode;
						view.purgeAllLinks();
					}
					ProjectCompendium.APP.removeView((View)oNode);
				    oNode.setType(nNewType, sAuthor);
				}
				catch(Exception io){
					io.printStackTrace();
					return false;
				}
			}
			else {
				try {
	   				oNode.setType(nNewType, sAuthor);
				}
				catch(Exception io) {
					return false;
				}
			}
		}
	    else if (nOldType == ICoreConstants.REFERENCE && nNewType != ICoreConstants.REFERENCE) {

			String source = oNode.getSource();
			String image = oNode.getImage();

			if (!image.equals("") || !source.equals("")) {

				int response = JOptionPane.showConfirmDialog(ProjectCompendium.APP, "If you change the type of a reference node,\nany external reference will be lost.\n\nAre you sure you want to continue?",
							      "Change Type - "+oNode.getLabel(), JOptionPane.YES_NO_OPTION);

				if (response == JOptionPane.NO_OPTION || response == JOptionPane.CLOSED_OPTION)
				    changeType = false;
				else
					changeType = true;
			}

			if (changeType) {
		    	try {
					if ( nNewType != ICoreConstants.LISTVIEW && nNewType != ICoreConstants.MAPVIEW)
						oNode.setSource("", "", sAuthor);
					oNode.setType(nNewType, sAuthor);
				}
		    	catch(Exception io) {
					return false;
				}
			}
	    }
	    else if (nOldType > ICoreConstants.PARENT_SHORTCUT_DISPLACEMENT && nNewType <=
	    					ICoreConstants.PARENT_SHORTCUT_DISPLACEMENT) {

			try {
			    oNode.setType(nNewType, sAuthor);
			}
			catch(Exception ex) {
				return false;
			}
		}
	    else {
			try {
			    oNode.setType(nNewType, sAuthor);
			}
			catch(Exception ex) {
				return false;
			}
	    }

	    return changeType;
	}
	
	        
		
	/**
	 * Update the link colours as appropriate after a node type change.
	 * @param type, the type of the node to update the link color for.
	 * @param oldType, the previous node type of this node.
	 */
	protected void changeLinkColour(int type, int oldType) {

		UILinkGroup group = ProjectCompendium.APP.oLinkGroupManager.getLinkGroup(ProjectCompendium.APP.getActiveLinkGroup());

		if (group == null || (group.getID()).equals("1") ) {
		    if ( type == ICoreConstants.PRO ) {

				for(Enumeration e = htLinks.elements();e.hasMoreElements();) {
				    UILink link = (UILink)e.nextElement();
				    if ( (link.getFromNode().getNode().getId()).equals(getNode().getId()) )
						link.setLinkType(new Integer(ICoreConstants.SUPPORTS_LINK).toString());
				}
			}
		    else if ( type == ICoreConstants.CON ) {

				for(Enumeration e = htLinks.elements();e.hasMoreElements();) {
				    UILink link = (UILink)e.nextElement();
				    if ( (link.getFromNode().getNode().getId()).equals(getNode().getId()) )
						link.setLinkType(new Integer(ICoreConstants.OBJECTS_TO_LINK).toString());
				}
		    }
		    else if (oldType == ICoreConstants.PRO || oldType == ICoreConstants.CON) {

				for(Enumeration e = htLinks.elements();e.hasMoreElements();) {
				    UILink link = (UILink)e.nextElement();
				    if ( (link.getFromNode().getNode().getId()).equals(getNode().getId()) ) {
						link.setLinkType(new Integer(ICoreConstants.DEFAULT_LINK).toString());
					}
				}
		    }
		}
	}

	/**
	 * Move first page of detail text into label.
	 */
	public void onMoveDetails() {

	    String details = oNode.getDetail();
	    if (details.equals(ICoreConstants.NODETAIL_STRING))
			details = "";

	    String label = getText();
	    if (label.equals(ICoreConstants.NOLABEL_STRING))
			label = "";

	    if (!details.equals("") && !label.equals(""))
			label = label+" "+details;
	    else if (label.equals("") && !details.equals(""))
			label = details;

	    label = label.replace('\n',' ');
	    label = label.replace('\r',' ');
	    label = label.replace('\t',' ');

	    setText(label);
	    try {
			// shuffle detail pages.
			Vector pages = oNode.getDetailPages(sAuthor);
			if (pages.size() > 1) {
			    pages.removeElementAt(0);
			    NodeDetailPage page = null;
			    int count = pages.size();
			    for (int i=0; i<count; i++) {
					page = (NodeDetailPage)pages.elementAt(i);
					page.setPageNo(i+1);
			    }
			}
			else {
			    NodeDetailPage page = (NodeDetailPage)pages.elementAt(0);
			    page.setText("");
				pages.setElementAt(page, 0);
			}
			oNode.setDetailPages(pages, sAuthor, sAuthor);
		}
	    catch(Exception ex) {
			System.out.println("Error: (UINode.onMoveDetails) \n\n"+ex.getMessage());
	    }

	    ProjectCompendium.APP.refreshNodeIconIndicators(oNode.getId());
	}

	/**
	 * Move label text into the first page of detail.
	 */
	public void onMoveLabel() {

	    String label = getText();
	    if (label.equals(ICoreConstants.NOLABEL_STRING))
			label = "";

	    String details = oNode.getDetail();
	    if (details.equals(ICoreConstants.NODETAIL_STRING))
			details = "";

	    if (!label.equals("") && !details.equals(""))
			details = label+" "+details;
	    else if (details.equals("") && !label.equals(""))
			details = label;

	    try {
			oNode.setDetail(details, sAuthor, sAuthor);
		    setText("");
	    }
	    catch(Exception ex) {
			ex.printStackTrace();
			System.out.println("Error: (UINode.onMoveLabel) \n\n"+ex.getMessage());
	    }

	    ProjectCompendium.APP.refreshNodeIconIndicators(oNode.getId());
	}

	/**
	 * Increase the font size displayed by one point.
	 * This does not change the setting in the database.
	 * @return the new size.
	 */
	public int increaseFontSize() {
		Font font = getFont();
		int newSize = font.getSize()+1;
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize()+1);	
		super.setFont(newFont);
		getUI().refreshBounds();		
		return newSize;
	}
	
	/**
	 * Decrease the font size displayed by one point.
	 * This does not change the setting in the database.
	 * @return the new size.
	 */
	public int decreaseFontSize() {
		Font font = getFont();
		int newSize = font.getSize()-1;
		Font newFont = new Font(font.getName(), font.getStyle(), font.getSize()-1);	
		super.setFont(newFont);
	   	getUI().refreshBounds();
	   	return newSize;
	}
	
	/**
	 * Sets the font used to display the node's text to the given font without scaling
 	 *
	 * @param size The size to set the font.
	 */
	public void setFontSize(int size) {		
		Font font = getFont();
		Font newFont = new Font(font.getName(), font.getStyle(), size);	
		super.setFont(newFont);
	   	getUI().refreshBounds();		
	}
		
	/**
	 * Restore the font to the default settings.
	 *
	 */
	public void setDefaultFont() {
		Font labelFont = new Font(oPos.getFontFace(), oPos.getFontStyle(), oPos.getFontSize());
		setFont(labelFont);
		
		if (getUI() != null) {					
			getUI().refreshBounds();
		}
	}
	
	/**
	 * Sets the font used to display the node's text.
	 * Scales if required.
 	 *
	 * @param font The font to use.
	 */
	public void setFont(Font font) {
		
		if (scale != 0.0 && scale != 1.0) {	
			if (oPos != null) {
				String sFontFace = oPos.getFontFace();
				int nFontSize = oPos.getFontSize();
				int nFontStyle = oPos.getFontStyle();
				Font font2 = new Font(sFontFace, nFontStyle, nFontSize);
				Point p1 = UIUtilities.transformPoint(font2.getSize(), font2.getSize(), scale);
				font = new Font(font2.getName() , font2.getStyle(), p1.x);
			}
		}
		
		super.setFont(font);				
	    repaint(10);
	}
	
	/**
	 * Convenience method to return the ascent height of the node's current font.
	 * @return int iFontHeight
	 */
	public int getFontHeight()	{
		Font oFont = this.getFont();
		FontMetrics  oFM = this.getFontMetrics(oFont);
		int iFontHeight = oFM.getAscent();
		return(iFontHeight);
	}
	/**
	 * Returns the text string that the node displays.
	 *
	 * @return String, the text string that the node displays.
	 * @see #setText
	 */
	public String getText() {
	    return sText;
	}

	/**
	 * Defines the single line of text this component will display.
	 * <p>
	 * @param text, the new text to diaply as the node label.
	 * @see #setIcon
	 */
	public void setText(String text) {

	    String oldValue = sText;

	    //set thte label of the model nodesummary object
	    try {
			if (oNode != null) {
				oNode.setLabel(text, sAuthor);
				sText = text;
				firePropertyChange(TEXT_PROPERTY, oldValue, sText);
				repaint();
			}
	    }
	    catch(Exception io) {
			io.printStackTrace();
			ProjectCompendium.APP.displayError("Error: (UINode.setText) Unable to update label.\n\n"+io.getMessage());
	    }
	}

	/**
	 * Sets the current scaling value for this node.
	 * @param  scale, the scaling value to apply to this node.
	 * @uml.property  name="scale"
	 */
	public void setScale(double scale) {
	    this.scale = scale;
	}

	/**
	 * Gets the current scaling value for this node.
	 * @return  double, the current scaling value for this node.
	 * @uml.property  name="scale"
	 */
	public double getScale() {
	    return scale;
	}

	/**
	 * Returns the graphic image (glyph, icon) that the node displays.
	 *
	 * @return ImageIocn, the icon displayed by this node.
	 * @see #setIcon
	 */
	public ImageIcon getIcon() {
		if (oDefaultIcon == null || oDefaultIcon.getImageLoadStatus() == MediaTracker.ERRORED) {
			return null;
		}
	    return oDefaultIcon;
	}

	/**
	 * Defines the icon this component will display. Fires a PropertyChangeEvent.
	 * <p>
	 * @param icon, the icon for this node to display.
	 * @see #getIcon
	 */
	public void setIcon(ImageIcon icon) {

	    if (scale != 1.0) {
			icon = scaleIcon(icon);
	    }

	    ImageIcon oldValue = oDefaultIcon;
	    oDefaultIcon = icon;

	    firePropertyChange(ICON_PROPERTY, oldValue, oDefaultIcon);
	    repaint();
	}

	/**
	 * Refreshes the icon this component will display. Forces the firing of a PropertyChangeEvent.
	 * <p>
	 * @param icon, the icon for this node to display.
	 * @see #getIcon
	 */
	public void refreshIcon(ImageIcon icon) {

	    if (scale != 1.0)
			icon = scaleIcon(icon);

	    ImageIcon oldValue = oDefaultIcon;
	    oDefaultIcon = icon;

	    // FORCE A PROPERTY CHANGE
	    oldValue = null;

	    firePropertyChange(ICON_PROPERTY, oldValue, oDefaultIcon);
	    repaint();
	}

	
	/**
	 * Refreshes the icon this component will display. Forces the firing of a PropertyChangeEvent.
	 * <p>
	 * @param icon, the icon for this node to display.
	 * @see #getIcon
	 */
	public void refreshIcon() {
		ImageIcon icon = UINode.getNodeImage(this.getNode().getType(), this.getNodePosition().getShowSmallIcon());

	    if (scale != 1.0)
			icon = scaleIcon(icon);

	    ImageIcon oldValue = oDefaultIcon;
	    oDefaultIcon = icon;

	    // FORCE A PROPERTY CHANGE
	    oldValue = null;

	    firePropertyChange(ICON_PROPERTY, oldValue, oDefaultIcon);
	    repaint();
	}
	/**
	 * Scale the given icon to the current scaling factor and return.
	 * @param icon, the icon to scale.
	 * @return ImageIcon, the scaled version of the icon, or the original if something went wrong.
	 */
	public ImageIcon scaleIcon(ImageIcon icon) {

		if (icon == null)
			return icon;

	    int imgWidth = icon.getIconWidth();
	    int imgHeight = icon.getIconHeight();

		if (imgWidth == 0 || imgHeight == 0)
			return icon;

		//System.out.println("original height = "+imgHeight);
		//System.out.println("original Width = "+imgWidth);

		//System.out.println("scale = "+scale);

	    int scaledW = (int)(scale*imgWidth);
	    int scaledH = (int)(scale*imgHeight);

		//System.out.println("scaled height = "+scaledH);
		//System.out.println("scaled Width = "+scaledW);

		if (scaledW == 0 || scaledH == 0)
			return null;

	    ImageFilter filter = new AreaAveragingScaleFilter(scaledW, scaledH);
	    FilteredImageSource filteredSource = new FilteredImageSource((ImageProducer)icon.getImage().getSource(), filter);
	    JLabel comp = new JLabel();
	    Image img = comp.createImage(filteredSource);
	    icon = new ImageIcon(img);

	    return icon;
	}

	/**
	 * Restore the icon on this node to its original type specific icon.
	 */
	public ImageIcon restoreIcon() {

	    int type = oNode.getType();

	    if (type == ICoreConstants.TRASHBIN) {
			ImageIcon icon = ProjectCompendium.APP.setTrashBinIcon();
			if (icon != null) {
				setIcon(icon);
			}
	    }
	    else if (type == ICoreConstants.REFERENCE || type == ICoreConstants.REFERENCE_SHORTCUT) {
			String refString = oNode.getImage();
			if (refString == null || refString.equals(""))
		    	refString = oNode.getSource();
			setReferenceIcon(refString);
	    }
		else if(type == ICoreConstants.MAPVIEW || type == ICoreConstants.MAP_SHORTCUT ||
				type == ICoreConstants.LISTVIEW || type == ICoreConstants.LIST_SHORTCUT) {

			String refString = oNode.getImage();
			if (refString != null && !refString.equals(""))
				setReferenceIcon(refString);
			else
				setIcon(getNodeImage(type, oPos.getShowSmallIcon()));
		}
	    else {
			setIcon(getNodeImage(type, oPos.getShowSmallIcon()));
	    }

	    return oDefaultIcon;
	}

	/**
	 * return if the image on this node has been scaled,
	 * and therefore can be enlarge on rollover.
	 */
	public boolean hasImageBeenScaled() {
		return bIsImageScaled;
	}

	/**
	 * Set the correct reference icon for the given file path or url.
	 *
	 * @param refString, the string for the file path or url for this reference node.
	 */
	public void setReferenceIcon(String refString) {
	    final String imageRef = refString;

		//Thread thread = new Thread("UINode.setReferenceIcon") {
		//	public void run() {
			    ImageIcon icon = null;
		
			    if (imageRef != null) {
					if ( UIImages.isImage(imageRef) ) {
						ImageIcon originalSizeImage = UIImages.createImageIcon(imageRef); 		
						if (originalSizeImage == null) {
							setIcon(UIImages.get(IUIConstants.BROKEN_IMAGE_ICON));
							return;
						}
						
						Image originalIcon = originalSizeImage.getImage();
						int originalWidth = originalIcon.getWidth(null);
						int originalHeight = originalIcon.getHeight(null);
		
						Dimension specifiedSize = getNode().getImageSize();
						if (specifiedSize.width == 0 && specifiedSize.height == 0) {
				    		icon = UIImages.thumbnailIcon(originalSizeImage);
							Image newIcon = icon.getImage();
							int newWidth = newIcon.getWidth(null);
							int newHeight = newIcon.getHeight(null);
				    		if (newWidth < originalWidth || newHeight < originalHeight) {
								bIsImageScaled = true;
							}
						} else if (specifiedSize.width == originalWidth && specifiedSize.height == originalHeight) {
							icon = originalSizeImage;
							bIsImageScaled = false;
						} else {
				    		icon = UIImages.scaleIcon(originalSizeImage, specifiedSize);
							Image newIcon = icon.getImage();
							int newWidth = newIcon.getWidth(null);
							int newHeight = newIcon.getHeight(null);
				    		if (newWidth < originalWidth || newHeight < originalHeight) {
								bIsImageScaled = true;
							}					
						}
					}
					else {
					    //FileSystemView fsv = FileSystemView.getFileSystemView();
				     	//File file = new File(refString);
				     	//icon = (ImageIcon)fsv.getSystemIcon(file);
		
					    // IF USING SMALL ICON MODE, LOAD SMALL VERSION
					    if (oPos.getShowSmallIcon()) {
					    	icon = getReferenceImageSmall(imageRef);
					    }
					    else {
					    	icon = getReferenceImage(imageRef);			    	
					    }
					}
			    }
			    setIcon(icon);
			//}
		//};
		//thread.start();
	}

	/**
	 * Check if the file path or url given is a recognised type for reference nodes.
	 * @param sRefString the string to check.
	 * @return boolean true if the file path or url given is a recognised type, else false.
	 */
	public static boolean isReferenceNode(String sRefString) {
		return UIReferenceNodeManager.isReferenceNode(sRefString);
	}

	/**
	 * Returns the nodedata object that this UINode represents.
	 *
	 * @return com.compendium.core.datamodel.NodeSummary, the associated node.
	 * @see #setNode
	 */
	public NodeSummary getNode() {
	    return oNode;
	}

	/**
	 * Returns the node pos that the node represents.
	 *
	 * @return com.compendium.core.datamodel.NodePosition, the associated node position object.
	 */
	public NodePosition getNodePosition() {
	    return oPos;
	}

	/**
 	 * Set the NodePosition of this node
	 * @param x, the x position of this node in the map.
	 * @param y, the y position of this node in the map.
	 */
	public void setNodePosition(int x, int y) {
	    oPos.setPos(x, y);
	}

	/**
 	 * Set the NodeSummary data object for this node.
	 * <p>
	 * @param node com.compendium.core.datamodel.NodeSummary, the node data object for this UINode.
	 */
	public void setNode(NodeSummary node) {

	    oNode = node;
		oNodeType = node.getType();
/** Added by Andrew 	**/
//		int nodeLDType = oNode.getLDType();
		
/** End of added by Andrew 	**/		
	    oNode.addPropertyChangeListener(this);
	    oPos.addPropertyChangeListener(this);

	    setHelp(oNode.getType());

	    //remove all returns and tabs which show up in the GUI as evil black char
	    String label = "";
	    label = oNode.getLabel();
	    if (label.equals(ICoreConstants.NOLABEL_STRING))
			label = "";

	    label = label.replace('\n',' ');
	    label = label.replace('\r',' ');
	    label = label.replace('\t',' ');
	    setText(label);

	    int type = oNode.getType();

	    //if the default icon has not already been set, then set it here.
	    //otherwise leave the icon image alone since it may have been changed.
	    ImageIcon icon = null;

	    if (oDefaultIcon == null) {
			if (type == ICoreConstants.REFERENCE || type == ICoreConstants.REFERENCE_SHORTCUT) {
			    String refString = oNode.getImage();
			    if (refString == null || refString.equals(""))
					refString = oNode.getSource();
			    
			    if (refString == null || refString.equals("")) {
			    	setIcon(getNodeImage(type, oPos.getShowSmallIcon()));
			    } else {
			    	setReferenceIcon(refString);
			    }
			}
			else if(type == ICoreConstants.MAPVIEW || type == ICoreConstants.MAP_SHORTCUT ||
					type == ICoreConstants.LISTVIEW || type == ICoreConstants.LIST_SHORTCUT) {
			    String refString = oNode.getImage();
			    if (refString == null || refString.equals("") || refString.endsWith("meeting_big.gif"))
				    setIcon(getNodeImage(type, oPos.getShowSmallIcon()));
				else
				    setReferenceIcon(refString);
			}
			else {
			    setIcon(getNodeImage(type, oPos.getShowSmallIcon()));
			}
	    }
	    /** Added by Andrew 	**/
//		int nodeLDType = oNode.getLDType();
		
		/** End of added by Andrew 	**/	
	    oNode.updateMultipleViews();
	}

	/**
	 * Returns the selected state of the node.
	 *
	 * @return boolean, the selected state of the node
	 */
	public boolean isSelected() {
	    return bSelected;
	}

	/**
	 * Sets the selected state of the node.
	 * <p>
	 * @param selected, the selected state
	 */
	public void setSelected(boolean selected) {
	    boolean oldValue = bSelected;
	    bSelected = selected;
	    firePropertyChange("selected", oldValue, bSelected);

	    repaint();
	}

	/**
	 * Returns the state of the cut node.
	 *
	 * @return boolean, true if the node has been cut, else false.
	 */
	public boolean isCut() {
	    return bCut;
	}

	/**
	 * Sets the state of the node to cut.
	 *
	 * @param cut, has this node been cut.
	 */
	public void setCut(boolean cut) {
	    boolean oldValue = bCut;
	    bCut = cut;
	    firePropertyChange("selected", oldValue, bSelected);

	    repaint();
	}

	/**
	 * Returns the roll-over state of the node.
	 *
	 * @return boolean, true if the node is currently rolled over, else false.
	 */
	public boolean isRollover() {
	    return bRollover;
	}

	/**
	* Sets the roll over state of the node.
	* <p>
	* @param rollover, the roll over state of the node.
	*/
	public void setRollover(boolean rollover) {

	    boolean oldValue = bRollover;
	    bRollover = rollover;

	    firePropertyChange(ROLLOVER_PROPERTY, new Boolean(oldValue), new Boolean(bRollover));

	    repaint();
	}

	/**
	 * Adds a reference to a link this node is linked to.
	 *
	 * @param link com.compendium.ui.UILink, the link this node is linked to.
	 */
	public void addLink(UILink link) {
	    if(!htLinks.containsKey((link.getLink()).getId()))
			htLinks.put((link.getLink()).getId(),link);
	}

	/**
	 * Removes a reference to a link this node is linked to.
	 *
	 * @param link com.compenduim.ui.UILink, the link to be removed.
	 */
	public void removeLink(UILink link) {
	    if(htLinks.containsKey((link.getLink()).getId()))
			htLinks.remove((link.getLink()).getId());
	}

	/**
	 * Removes references to all links this node is linked to.
	 */
	public void removeAllLinks() {

	    for(Enumeration keys = htLinks.keys();keys.hasMoreElements();) {
			String key = (String)keys.nextElement();
			htLinks.remove(key);;
	    }
	}

	/**
	 * Updates the connection points of the links.
	 */
	public void updateLinks() {
		if (htLinks != null && htLinks.size() > 0) {
		    for(Enumeration e = htLinks.elements();e.hasMoreElements();) {
				UILink link = (UILink)e.nextElement();
				if (link != null)
					link.updateConnectionPoints();
	   	 	}
		}
	}

	/**
	 * Scale links and update the connection points of the links.
	 */
	public void scaleLinks(AffineTransform trans) {

	    for(Enumeration e = htLinks.elements();e.hasMoreElements();) {
			UILink link = (UILink)e.nextElement();
			link.scaleArrow(trans);
			link.updateConnectionPoints();
	    }
	}

	/**
	 * Returns the enumeration of UILinks to this node.
	 * @return Enumeration, the enumeration of UILinks to this node.
	 */
	public Enumeration getLinks() {
	    return htLinks.elements();
	}

	/**
	 * Checks whether this node is linked to the given node.
	 *
	 * @param to com.compendium.ui.UINode, the node to test for.
	 * @return boolean, true if this node is linked to the given node, false otherwise.
	 */
	public boolean containsLink(UINode to) {
	    for(Enumeration e = htLinks.elements();e.hasMoreElements();) {
			UILink link = (UILink)e.nextElement();
			if (link.getFromNode() == to || link.getToNode() == to)
			    return true;
	    }
	    return false;
	}

	/**
	 * Return the link associated with the given node or null.
	 *
	 * @param to com.compendium.ui.UINode, the node to return the link for.
	 * @return com.compendium.ui.UILink, the link of found else null.
	 */
	public UILink getLink(UINode to) {

	    for(Enumeration e = htLinks.elements();e.hasMoreElements();) {
			UILink link = (UILink)e.nextElement();
			if (link.getFromNode() == to || link.getToNode() == to)
			    return link;
	    }
	    return null;
	}

	/**
	 * Convenience method that moves this component to position 0 if it's
	 * parent is a JLayeredPane.
	 */
	public void moveToFront() {
	    if (getParent() != null && getParent() instanceof JLayeredPane) {
			JLayeredPane l =  (JLayeredPane)getParent();
			l.moveToFront(this);
	    }
	}

	/**
	 * Convenience method that moves this component to position -1 if it's
	 * parent is a JLayeredPane.
	 */
	public void moveToBack() {
	    if (getParent() != null && getParent() instanceof JLayeredPane) {
			JLayeredPane l =  (JLayeredPane)getParent();
			l.moveToBack(this);
	    }
	}

	/**
	 * Returns the amount of space between the text and the icon
	 * displayed in this node.
	 *
	 * @return int, an int equal to the number of pixels between the text and the icon.
	 * @see #setIconTextGap
	 */
	public int getIconTextGap() {
	    return nIconTextGap;
	}

	/**
	 * If both the icon and text properties are set, this property
	 * defines the space between them.
	 * <p>
	 * The default value of this property is 4 pixels.
	 * <p>
	 *
	 * @see #getIconTextGap
	 */
	public void setIconTextGap(int iconTextGap) {
	    int oldValue = nIconTextGap;
	    nIconTextGap = iconTextGap;
	    firePropertyChange("iconTextGap", oldValue, nIconTextGap);
	    invalidate();
	    repaint(10);
	}

	/**
	 * Set the location of this node, scaling if required.
	 * Location must be passed in at the original 100% scale value.
	 */
	public void setLocation(Point loc) {
		
		if (scale != 0.0 && scale != 1.0) {		
			loc = UIUtilities.transformPoint(loc.x, loc.y, scale);
		}
		
		super.setLocation(loc);		
	}
	
	/**
	 * Sets the font used to display the node's text. Fires a PropertyChangeEvent.
 	 *
	 * @param font,  The font to use.
	 */
	public void setLabelFont(Font font) {

	    super.setFont(font);
	    firePropertyChange(TEXT_PROPERTY, "", sText);
	    repaint();
	}

	/**
	 * Return the current reference to the content dialog for this node or null.
	 * @return com.compendium.ui.dialogs.UINodeContentDialog, the content dialog for this node.
	 */
	public UINodeContentDialog getCurrentContentDialog() {
		return contentDialog;
	}

	/**
	 * Return the current reference to the content dialog for this node. If the content dialog is null, create a new one, but don't show it.
	 * @return  com.compendium.ui.dialogs.UINodeContentDialog, the content dialog for this node.
	 * @uml.property  name="contentDialog"
	 */
	public UINodeContentDialog getContentDialog() {
	    if(getNode().getType() == ICoreConstants.TRASHBIN || 
	    		getNode().getId().equals(ProjectCompendium.APP.getInBoxID())) {
	    	return null;
	    }
		
	    if (contentDialog == null)
			contentDialog = new UINodeContentDialog(ProjectCompendium.APP, oPos.getView(), this, UINodeContentDialog.CONTENTS_TAB);

	    return contentDialog;
	}

	/**
	 * Open and return the content dialog and select the Edit/Contents tab.
	 * @return com.compendium.ui.dialogs.UINodeContentDialog, the content dialog for this node.
	 */
	public UINodeContentDialog showEditDialog() {
		return showContentDialog(UINodeContentDialog.CONTENTS_TAB);
	}

	/**
	 * Open and return the content dialog and select the Properties tab.
	 * @return com.compendium.ui.dialogs.UINodeContentDialog, the content dialog for this node.
	 */
	public UINodeContentDialog showPropertiesDialog() {
		return showContentDialog(UINodeContentDialog.PROPERTIES_TAB);
	}

	/**
	 * Open and return the content dialog and select the View tab.
	 * @return com.compendium.ui.dialogs.UINodeContentDialog, the content dialog for this node.
	 */
	public UINodeContentDialog showViewsDialog() {
		return showContentDialog(UINodeContentDialog.VIEW_TAB);
	}

	/**
	 * Open and return the content dialog and select the given tab.
	 *
	 * @param int tab, the tab on the dialog to select.
	 * @return com.compendium.ui.dialogs.UINodeContentDialog, the content dialog for this node.
	 */
	private UINodeContentDialog showContentDialog(int tab) {
	    if(getNode().getType() == ICoreConstants.TRASHBIN || 
	    		getNode().getId().equals(ProjectCompendium.APP.getInBoxID())) {
	    	return null;
	    }
	    	
		if (contentDialog != null && contentDialog.isVisible())
			return contentDialog;

		contentDialog = new UINodeContentDialog(ProjectCompendium.APP, oPos.getView(), this, tab);
   		contentDialog.setVisible(true);
   		//Lakshmi (4/19/06) - if the contents dialog is opened set state as read in NodeUserState DB
   		int state = this.getNode().getState();
   		if(state != ICoreConstants.READSTATE){
   			try {
				this.getNode().setState(ICoreConstants.READSTATE);
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Error: (UINode.showContentDialog) \n\n"+e.getMessage());
			} catch (ModelSessionException e) {
				e.printStackTrace();
				System.out.println("Error: (UINode.showContentDialog) \n\n"+e.getMessage());
			}
   		}
	    return contentDialog;
	}
/** 	Added by Andrew		**/
	/**
	 * Open and return the UILdInformationDialog dialog and select the given tab.
	 *
	 * @param int tab, the tab on the dialog to select.
	 * @return com.compendium.ui.dialogs.UILdInformationDialog, the Ld information dialog for this node.
	 */
	
	/**
	private UILdInformationDialog showLDInfoDialog(int tab) {
	    if(getNode().getType() == ICoreConstants.TRASHBIN || 
	    		getNode().getId().equals(ProjectCompendium.APP.getInBoxID())) {
	    	return null;
	    }
	    	
		if (ldInfoDialog != null && ldInfoDialog.isVisible())
			return ldInfoDialog;

		ldInfoDialog = new UILdInformationDialog(ProjectCompendium.APP, oPos.getView(), this, tab);
		ldInfoDialog.setVisible(true);
   		//Lakshmi (4/19/06) - if the contents dialog is opened set state as read in NodeUserState DB
		/// Andrew writes:
		 // Should state be set to read when the UILdInformationDialog is shown? 
		 // Not sure - need to check and delete this code if appropriate.
		 // 
   		int state = this.getNode().getState();
   		if(state != ICoreConstants.READSTATE){
   			try {
				this.getNode().setState(ICoreConstants.READSTATE);
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Error: (UINode.showLDInfoDialog) \n\n"+e.getMessage());
			} catch (ModelSessionException e) {
				e.printStackTrace();
				System.out.println("Error: (UINode.showLDInfoDialog) \n\n"+e.getMessage());
			}
   		}
	    return ldInfoDialog;
	}	
	****/
	
	/**
	 * Generate the Learning design help dialog for this node.
	 * @return - an instance of UILdInformationDialog
	 */
	/** Now in UILdNode class 
	public UILdInformationDialog showLdHelpDialog() {
		UILdInformationDialog oLdDialog = null;
		switch (this.getNode().getLdType())	{
		case ILdCoreConstants.iLD_TYPE_VLE_TOOL: oLdDialog =  showLDInfoDialog(ILdUIConstants.iTOOLSHELP_TAB); break;
		case ILdCoreConstants.iLD_TYPE_TASK: oLdDialog =  showLDInfoDialog(ILdUIConstants.iTASKSHELP_TAB); break;
		default:  oLdDialog =  showLDInfoDialog(ILdUIConstants.iTOOLSHELP_TAB); break;
		}
		return oLdDialog;
	}
	***/
/**	End of added by Andrew	**/
	
	/**
	 * Return the right-click node menu for this node.
	 * @return com.compendium.ui.popups.UINodePopupMenu, the right-click node menu for this node.
	 */
	public UINodePopupMenu getPopupMenu() {
	    if(getNode().getType() == ICoreConstants.TRASHBIN || 
	    		getNode().getId().equals(ProjectCompendium.APP.getInBoxID())) {
			return null;
	    }

	    if (nodePopup == null)
			nodePopup = new UINodePopupMenu("Popup menu", getUI());

	    return nodePopup;
	}

	/**
	 * Create and show the right-click node popup menu for the given nodeui.
	 * @param nodeui com.compendium.ui.plad.NodeUI, the node to create the popup for.
	 * @param x, the x position of the mouse event that triggered this request.
	 * @param y, the y position of the mouse event that triggered this request.
	 */
	public UINodePopupMenu showPopupMenu(NodeUI nodeui,  int x, int y) {

	    if(getNode().getType() == ICoreConstants.TRASHBIN || 
	    		getNode().getId().equals(ProjectCompendium.APP.getInBoxID())) {
			return null;
	    }
	 // Get the LDType of the node 
	    int ldType = getNode().getLdType();
	    nodePopup = new UINodePopupMenu("Popup menu", nodeui, ldType);
	    UIViewFrame viewFrame = getViewPane().getViewFrame();

	    Dimension dim = ProjectCompendium.APP.getScreenSize();
	    int screenWidth = dim.width - 50; //to accomodate for the scrollbar
	    int screenHeight = dim.height -200; //to accomodate for the menubar...

	    Point point = viewFrame.getViewPosition();
	    int realX = Math.abs(point.x - getX())+50;
	    int realY = Math.abs(point.y - getY())+50;

	    int endXCoordForPopUpMenu = realX + nodePopup.getWidth();
	    int endYCoordForPopUpMenu = realY + nodePopup.getHeight();

	    int offsetX = (screenWidth) - endXCoordForPopUpMenu;
	    int offsetY = (screenHeight) - endYCoordForPopUpMenu;

	    if(offsetX > 0)
		offsetX = 0;
	    if(offsetY > 0)
		offsetY = 0;

	    nodePopup.setCoordinates(realX+offsetX, realY+offsetY);
	    nodePopup.setViewPane(getViewPane());
	    nodePopup.show(viewFrame, realX+offsetX, realY+offsetY);

	    return nodePopup;
	}


	
/** 	End of added by Andrew	**/
	/**
	 * Convenience method that searchs the anscestor heirarchy for a UIViewPane instance.
	 * @return com.compendium.ui.UIViewPane, the parent pane for this node.
	 */
	public UIViewPane getViewPane() {
	    Container p;

	    // Search upward for viewpane
	    p = getParent();
	    while (p != null && !(p instanceof UIViewPane)) {
	    	p = p.getParent();
	    }

	    return (UIViewPane)p;
	}

	/**
	 * Create a SVG representation of this node.
	 * 
	 * @param oDoc
	 *            , the SVG document to which the SVG representation of this
	 *            node will be added.
	 */
	public void generateSVG(Document oDoc) {
		generateComponentSVG(oDoc);
		generateChildrenSVG(oDoc);
	}
	
	/**
	 * Create a SVG representation of all the components of this node.
	 * @param oDoc, the SVG document to which the SVG representation of this node will be added.
	 */
	public void generateChildrenSVG( Document oDoc) {
		if (oDoc == null )
			return;
		// Get the id of the View that this node is in
		String sParentViewId = this.getNodePosition().getView().getId();
		// Get the SVG element representing the View that this node is in
		//	Element oSvgParentView = oDoc.getElementById(SvgExport.sMapViewClass+SvgExport.sIdFragmentConnector + sParentViewId);
		Element oSvgParentView = this.getSvgParentMap(sParentViewId, oDoc);
		if (oSvgParentView == null)	{
			System.out.println("Error generating SVG for UINode name " + this.getText()+ ", Id = " + this.getNode().getId());
			return;
		}
		// Get the NodeUI so can get text reactangle etc
		NodeUI oNodeUI = this.getUI();

		int iXpos = this.getNodePosition().getXPos();
		int iYpos = this.getNodePosition().getYPos();

		double dbIconRXpos =  oNodeUI.getIconRectangle().getX() + iXpos;
		double dbIconRYpos =  oNodeUI.getIconRectangle().getY() + iYpos;
		int iIconRXpos = (int) Math.round(dbIconRXpos);
		int iIconRYpos = (int) Math.round(dbIconRYpos);
		double dbIconRWidth = oNodeUI.getIconRectangle().getWidth();
		/** iIconCentreXPos, the centre  of the icon in the X plane, is used to 
		 * layout the node label. This is the easiest way to lay out the label
		 * because node labels are always centred on the node icon.**/ 
		int iIconCentreXPos = (int)Math.round(dbIconRXpos + (dbIconRWidth/2));
		// Create the node class description text 
		String sNodeClass = this.getSvgClass();

		Comment oComment = oDoc.createComment("Start of Node " + sNodeClass  + " " + this.getNode().getId());
		Element oTitle = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_TITLE_TAG);
		oTitle.setTextContent(this.getNode().getLabel());

		Element oDesc = oDoc.createElement("desc");
		oDesc.setAttribute("class", "nodeDetails");
		oDesc.setTextContent(this.getNode().getDetail());
		// oGroup is the SVG <g> element to contain all sub-elements for this UINode
		Element oGroup = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_G_TAG);
		oGroup.setAttribute("id", sNodeClass+SvgExport.sIdFragmentConnector + this.getNode().getId());
		oGroup.setAttribute("class", sNodeClass );
		oGroup.appendChild(oTitle);
		oGroup.appendChild(oDesc);
		// Add the icon if it is displaid 
		if (!(this.getNodePosition().getHideIcon() || (this.getIcon() == null ))) {
			Element oUse = this.createSvgIconReference(oDoc);
			oGroup.appendChild(oUse);
		}
		/**	Layout the text	**/
		Element oText = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_TEXT_TAG);
		Vector<TextRowElement> oTREs = oNodeUI.getTextRowElements();
		String sTextCssClass = "nodelabel" + " text" + this.getNodePosition().getFontSize();
		if (!this.getNodePosition().getFontFace().equals("Dialog"))
			sTextCssClass = sTextCssClass + " " + this.getNodePosition().getFontFace();
		if (oTREs.isEmpty())	{
			//System.out.println("oTREs.isEmpty!!!: " + this.getText() + " in View " + this.getNodePosition().getView().getLabel());
			int iFontHeight = this.getFontHeight();
			//oText.setAttribute("x", Integer.toString((int)Math.round(oNodeUI.getLabelRectangle().getX() + iXpos)));
			oText.setAttribute("x", Integer.toString(iIconCentreXPos));
			oText.setAttribute("y", Integer.toString( (int)Math.round(oNodeUI.getLabelRectangle().getY() + iYpos + iFontHeight)));
			oText.setAttribute("class", sTextCssClass);
			oText.setTextContent(this.getText());
			oGroup.appendChild(oText);
		}
		else	{
			TextRowElement oCurrentElement = null;
			//System.out.println("iXpos = " + iXpos + ", iYpos = " + iYpos);
			int iYVal = 0; String sTextForLine = "";
			for (int i=0; i<oTREs.size(); ++i)	{
				oCurrentElement = oTREs.elementAt(i);
				sTextForLine = oCurrentElement.getText();						
				iYVal = (int)Math.round( oCurrentElement.getTextRect().getY()+iYpos );
				if (i==0)	{ // If it's the rirst row, set the <text> elements contents and attributes
					oText.setAttribute("x", Integer.toString(iIconCentreXPos));
					oText.setAttribute("y", Integer.toString(iYVal));
					oText.setAttribute("class", sTextCssClass);
					oText.setTextContent(sTextForLine);
				}
				else	{ // If it's the second or greater line, create <tspan> children of the <text> element
					Element oTspan = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_TSPAN_TAG);
					oTspan.setAttribute("x", Integer.toString(iIconCentreXPos));
					oTspan.setAttribute("y", Integer.toString(iYVal));
					oTspan.setTextContent(sTextForLine);
					oText.appendChild(oTspan);
				}
			}
			oGroup.appendChild(oText);
		}

		oSvgParentView.appendChild(oGroup);
		oSvgParentView.insertBefore(oComment, oGroup);

		if (oNodeUI.hasText())	{ // DRAW * IF HAS DETAILS
			/** Example code
			 * <g class="details" onmouseover="show_details(evt)">
			 *  <text x="274" y="110" class="indicator">*</text>
			 *  </g>
			 **/
			Element oDetailsGroup = oNodeUI.createSVGDetailsGroup(oDoc);
			oGroup.appendChild(oDetailsGroup);
		}
		if (oNodeUI.hasTrans())	{ // DRAW TRANSCLUSION INDICATOR  IF NODE HAS TRANSCLUIONS 
			/**	Example SVG Code. The id's of the transclusionLink tpsan elements are 
			* the id of the target map prefixed with 'tlto.' (short for 'tranclusion
			* link to'.	*****
			*  <g class="transclusion indicator"   onmouseover="show_transclusions(evt)" onmouseout="hide_transclusions(evt)" onclick="startShow_transclusions(evt)" >
	  		*	<title>Transclusions</title>
	  		*	<text class="indicator" x="94" y="132" >2</text>
	  		*		<g class="details" display="none">
	    	*		<rect class="border" x="100" y="162" width="120" height="30"></rect>
	    	*		<!-- Note that <text> element does not have x and y co-ordinates so all text must be palce within the <tspan>s to show up in the correct place. -->
	   		*		<text class="transclusionLinks">
			*		<tspan id="Node.137108491691311175409673.tlfrom.137108491691323450095536.tlto.137108491691311175379751" class="transclusionLink" x="202" y="124">A map (top level)</tspan>
			*		<tspan id="Node.137108491691311175409673.tlfrom.137108491691323450095536.tlto.137108491691311088809139" class="transclusionLink" x="202"
			*					y="136">Another map (2nd level)</tspan>
			*		</text>
	  		*		</g>
	  		*	</g>
			*/
			Element oTransGroup = oNodeUI.createSVGTransclusionGroup(oDoc);
			oGroup.appendChild(oTransGroup);
		}
		// If it is transcluded but indicator is not showing update SvgExport.hmDocToTransWrittenInView
		if (!oNodeUI.hasTrans()&& this.getNode().isInMultipleViews())	{
			SvgExport.hmDocToTransWrittenInView.get(oDoc).put(this.getNode().getId(), this.getViewPane().getView().getId());
		}
		if (oNodeUI.hasCodes())	{
			Element oTagsGroup = oNodeUI.createSVGTagsGroup(oDoc);
			oGroup.appendChild(oTagsGroup);
		}
		if (oNodeUI.hasWeight())	{
			Element oWeightGroup = oNodeUI.createSVGWeightGroup(oDoc);
			oGroup.appendChild(oWeightGroup);
		}
		oSvgParentView.appendChild(oGroup);
		//oParentMapElement.appendChild(oGroup);
		oComment = oDoc.createComment("End  of Node " + sNodeClass  + " " + this.getNode().getId());
		oSvgParentView.appendChild(oComment);
	}
	

	/**
	 * Generate the String to be used in an SVG file as part of the identifier,
	 * or the selectors for CSS classes for this node. 
	 * @return String denote the class of this node for use in SVG files, 
	 * or SvgExport.sGeneralClass if the relevant class can not be determined.
	 */
	public String getSvgClass() {
		//Hashmap.get(key) returns null if no mapping to the key can be found
		/// Return the class denoted by this node's type
		String sNodeClass = SvgExport.hmNodeClasses.get(this.getNode().getType()); 
		// If it's a reference node it might be part of a Ld stencil
		if (sNodeClass.equals(SvgExport.sReferenceClass)) 	{
			String sNodeClassStencil = this.getSvgSequenceMapStencilClass();
			if (!sNodeClassStencil.equals(SvgExport.sGeneralClass))	{
				sNodeClass = sNodeClassStencil;
			}
		}
		return sNodeClass;
	}
	
	/**
	 * If this node has a  Sequnece map node image, generate the String to be 
	 * used in an SVG file as part of the identifier, or the selectors for CSS 
	 * classes for this node. If it is not a sequence map node, return 
	 * SvgExport.sGeneralClass
	 * 
	 * @return String denote the class of this node for use in SVG files, 
	 * or SvgExport.sGeneralClass if the relevant class can not be determined.
	 */
	protected String getSvgSequenceMapStencilClass() {
		//Hashmap.get(key) returns null if no mapping to the key can be found
		/// Return the class denoted by this node's type
		String sNodeClass = SvgExport.sGeneralClass;
		String sImageRef = this.getNode().getImage();
		String sSource = this.getNode().getSource();
		//UIReferenceNodeManager.getReferenceIconPath(sRefString);
		String sImagePath  = UIImages.getReferencePath(sSource, sImageRef, false);
		if (sImagePath.contains(ILdUIConstants.sLDSEQUENCEMAPPATH))	{
			// It's a sequence mapping node
			int iStartPos = sImagePath.lastIndexOf(UIImages.sFS);
			int iEndPos = sImagePath.lastIndexOf(".");
			String sSeqType = sImagePath.substring(iStartPos + 1);
			sNodeClass = sImagePath.substring(iStartPos + 1, iEndPos);
		}
		
		return sNodeClass;
	}
	
	
	/**
	 * Create the SVG code to reference the relevant icon definition. 
	 * @param oDoc
	 * @return
	 */
	public Element createSvgIconReference(Document oDoc)	{
		String sNodeClass = this.getSvgClass();
		int iXpos = this.getNodePosition().getXPos();
		int iYpos = this.getNodePosition().getYPos();
		NodeUI oNodeUI = this.getUI();
		double dbIconRXpos =  oNodeUI.getIconRectangle().getX() + iXpos;
		double dbIconRYpos =  oNodeUI.getIconRectangle().getY() + iYpos;
		int iIconRXpos = (int) Math.round(dbIconRXpos);
		int iIconRYpos = (int) Math.round(dbIconRYpos);
		Element oUse = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_USE_TAG);
	//	Element oImage = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_IMAGE_TAG);
		//if (this.getNode().getType() == ICoreConstants.REFERENCE  && this.getLdType() == ILdCoreConstants.iLD_TYPE_NO_TYPE)	{
			if (this.getNode().getType() == ICoreConstants.REFERENCE  )	{
			//create the references to the appropraite reference node icon 
			String sImageRef = this.getNode().getImage();
			String sSource = this.getNode().getSource();
			//UIReferenceNodeManager.getReferenceIconPath(sRefString);
			String sImagePath  = UIImages.getReferencePath(sSource, sImageRef, false);
			//If it's one of the reference node types
			if (sImagePath.contains(UIReferenceNodeManager.sPATH))	{
				oUse = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_IMAGE_TAG);
				int pos = sImagePath.lastIndexOf(UIImages.sFS);
				String sImageFile = sImagePath.substring(pos + 1);
				oUse.setAttribute(SVGGraphics2D.XLINK_HREF_QNAME, UIImages.sRASTERREFERENCENODEICONURL +  sImageFile);
				oUse.setAttribute(SVGGraphics2D.SVG_WIDTH_ATTRIBUTE, "32");
				oUse.setAttribute(SVGGraphics2D.SVG_HEIGHT_ATTRIBUTE, "32");
				if ((sSource != null) && (sSource != ""))	{
					//Open the Link sSource 
					oUse.setAttribute(SVGGraphics2D.SVG_ONCLICK_ATTRIBUTE, "show_source(\'"+ sSource+ "\')");
				}
			}
/**			
			else if (sImagePath.contains(ILdUIConstants.sLDSEQUENCEMAPPATH))	{
				// It's a sequence mapping node
				int pos = sImagePath.lastIndexOf(UIImages.sFS);
				String sSeqType = sImagePath.substring(pos + 1);
				oUse.setAttribute(SVGGraphics2D.XLINK_HREF_QNAME, org.apache.batik.svggen.SVGSyntax.SIGN_POUND + sNodeClass);
			}
**/			
			else	{
				//Use the default reference node icon
				oUse.setAttribute(SVGGraphics2D.XLINK_HREF_QNAME, org.apache.batik.svggen.SVGSyntax.SIGN_POUND + sNodeClass);
			}
		}
		else 	{	
			oUse.setAttribute(SVGGraphics2D.XLINK_HREF_QNAME, org.apache.batik.svggen.SVGSyntax.SIGN_POUND + sNodeClass); 
		}
		if (View.isViewType( this.getNode().getType()))	{
			oUse.setAttribute(SVGGraphics2D.SVG_ONCLICK_ATTRIBUTE, SvgExport.sShowMapViewFunction);
		}
		if (this.getNodePosition().getShowSmallIcon())	{
			// If te node is using a small icon, scale the icon image, and compensate for scalling of co-ordinates
			oUse.setAttribute(SVGGraphics2D.SVG_TRANSFORM_ATTRIBUTE, "scale(0.5)");
			iIconRXpos = (int) Math.round(2*dbIconRXpos);
			iIconRYpos = (int) Math.round(2*dbIconRYpos);
		}
		oUse.setAttribute("x",  Integer.toString(iIconRXpos));
		oUse.setAttribute("y", Integer.toString(iIconRYpos));
		return oUse;
	}
	/**
	 * Get the SvG element that is the SVG representation of the parent view
	 * for this node in the SVG Document oDoc.
	 * @param sParentViewId
	 * @param oDoc
	 * @return the parent Element, or null if the parent element can not be found
	 */
	protected Element getSvgParentMap(String sParentViewId,  Document oDoc)	{
		String sViewClass = SvgExport.sGeneralClass;
		Element oSvgParentView =  null;
		try	{
			sViewClass = SvgExport.hmViewNodeClasses.get(this.getNodePosition().getView().getType());
		}
		catch (Exception e) {
			System.out.print("Array SvgExport.hmViewNodeClasses out of bounds: index accessed = "  + this.getNodePosition().getView().getType());
		}

		oSvgParentView = oDoc.getElementById(sViewClass+SvgExport.sIdFragmentConnector + sParentViewId);
		if (oSvgParentView == null)	{
			System.out.println("Error generating SVG, no svg parent view  for UINode name " + this.getText()+ ", Id = " + this.getNode().getId());
		}
		return oSvgParentView;

	}
	
	
	/**
	 * Calculate the requred dimensions of the given node.
	 * Checks for icon and various node indicator extras when calculating.
	 * @param node com.compendium.ui.UINode, the node to calculate the dimensions for.
	 * @return Dimension, the dimension for the given node.
	 */
	/**
	private Dimension calculateDimension() {

		boolean hasTrans = false;
		boolean hasText = false;
		boolean hasCodes = false;
		boolean hasWeight = false;
		boolean hasMovie = false;

		String text = this.getText();
		String id = this.getNode().getId();

		Insets insets = this.getInsets();
		int dx = insets.left + insets.right;
		int dy = insets.top + insets.bottom;

		Font font = this.getFont();
		FontMetrics fm = this.getFontMetrics(font);

		Rectangle iconR = new Rectangle();
		Rectangle textR = new Rectangle();
		Rectangle viewR = new Rectangle(this.getSize());

		viewR.x = insets.left;
		viewR.y = insets.top;

		NodePosition pos = this.getNodePosition();		
		
		Icon icon = this.getIcon();
		if (pos.getHideIcon() || icon == null) {
			iconR.width=0;
			iconR.height=0;
			iconR.y = 1;
			textR.y = iconR.y + iconR.height + fm.getAscent();
		}
		else {
			iconR.width = icon.getIconWidth()+1;
			iconR.height = icon.getIconHeight()+1;
			iconR.y = viewR.y+1;
			textR.y = iconR.y + iconR.height + this.getIconTextGap() + fm.getAscent();

			// FOR EXTRA BIT ON SIDE
			//AffineTransform trans = font.getTransform();
			//Font newFont = (new Font("Dialog", Font.BOLD, 10)).deriveFont(trans);
			// work around for Mac BUG with derive Font
			AffineTransform trans=new AffineTransform();
			trans.setToScale(this.getScale(), this.getScale());
			Point p1 = new Point(10, 10);
			try { p1 = (Point)trans.transform(p1, new Point(0, 0));}
			catch(Exception e) {System.out.println("can't convert font size (UINode.calculateDimension)\n\n"+e.getMessage()); } //$NON-NLS-1$
			Font newFont = new Font("Dialog" , Font.BOLD, p1.x); //$NON-NLS-1$

			NodeSummary nodeSumm = this.getNode();
			FontRenderContext frc = UIUtilities.getDefaultFontRenderContext();

			//LineMetrics metrics = newFont.getLineMetrics(message, frc);
			//float lineheight = metrics.getHeight();      // Total line height
			//float ascent = metrics.getAscent();          // Top of text to baseline
			
			String detail = nodeSumm.getDetail();
			detail = detail.trim();
			int type = this.getType();

			float widestExtra = 0;
			
			// ADD EXTRA WIDTH FOR BITS ON SIDE IF REQUIRED
			if (ProjectCompendium.APP.oMeetingManager != null && ProjectCompendium.APP.oMeetingManager.captureEvents() &&
					ProjectCompendium.APP.oMeetingManager.getMeetingType() == MeetingManager.REPLAY ) {
				hasMovie = true;
				Rectangle2D bounds = newFont.getStringBounds("M", frc); //$NON-NLS-1$
				float width = (float) bounds.getWidth(); 
				if (width > widestExtra) {
					widestExtra = width;
				}
			}			
			if (pos.getShowTrans()
					&&  (nodeSumm.isInMultipleViews()) && (nodeSumm.getViewCount() > 1)) {
				hasTrans = true;
				Rectangle2D bounds = newFont.getStringBounds(String.valueOf(nodeSumm.getViewCount()), frc);
				float width = (float) bounds.getWidth(); 
				if (width > widestExtra) {
					widestExtra = width;
				}
			}
			if (pos.getShowText()
					&& (type != ICoreConstants.TRASHBIN 
							&& !detail.equals("")  //$NON-NLS-1$
							&& !detail.equals(ICoreConstants.NODETAIL_STRING) 
							&& !id.equals(ProjectCompendium.APP.getInBoxID()))) {
				hasText = true;				
				Point p2 = new Point(18, 18);
				try { p2 = (Point)trans.transform(p2, new Point(0, 0));}
				catch(Exception e) {}
				Font tFont = new Font("Dialog", Font.BOLD, p2.x); //$NON-NLS-1$
				Rectangle2D bounds = tFont.getStringBounds("*", frc); //$NON-NLS-1$
				float width = (float) bounds.getWidth(); 
				if (width > widestExtra) {
					widestExtra = width;
				}
			}
			if  (pos.getShowWeight()
					&& View.isViewType(type)) {
				hasWeight = true;
				View view  = (View)this.getNode();
				try { 
					Rectangle2D bounds = newFont.getStringBounds(String.valueOf(view.getNodeCount()), frc);
					float width = (float) bounds.getWidth(); 
					if (width > widestExtra) {
						widestExtra = width;
					}
				} catch(Exception e){}
			}
			try {
				if (pos.getShowTags() && nodeSumm.getCodeCount() > 0) {
					hasCodes = true;
					Rectangle2D bounds = newFont.getStringBounds("T", frc); //$NON-NLS-1$
					float width = (float) bounds.getWidth(); 
					if (width > widestExtra) {
						widestExtra = width;
					}
				}
			}
			catch(Exception ex) {
				System.out.println("Error: (NodeUI.calculateDimension) \n\n"+ex.getMessage()); //$NON-NLS-1$
			}

		//	if (hasMovie || hasTrans || hasText || hasWeight || hasCodes) {
				//add 4 to allow for drawing borders
				iconR.width += new Float(widestExtra).intValue()+4; 
//			}
		}

		int wrapWidth = pos.getLabelWrapWidth();
		if (wrapWidth <= 0) {
			wrapWidth = ((Model)ProjectCompendium.APP.getModel()).labelWrapWidth;
		}
		wrapWidth = wrapWidth+1; // Needs this for some reason.
		int textWidth = text.length();		
		
		textR.width = textWidth;
		int widestLine = 0;

		textR.height = 0;
		textR.x = dx;

		if (textWidth > wrapWidth) {

			int loop = -1;
			String textLeft = text;

			while ( textLeft.length() > 0 ) {
				loop ++;
				int textLen = textLeft.length();
				int curLen = wrapWidth;
				if (textLen < wrapWidth ) {
					curLen = textLen;
				}
				String nextText = textLeft.substring(0, curLen);
				if (curLen < textLen) {
					int lastSpace = nextText.lastIndexOf(" "); //$NON-NLS-1$
					if (lastSpace != -1 && lastSpace != textLen) {
						curLen = lastSpace+1;
						nextText = textLeft.substring(0, curLen);
						textLeft = textLeft.substring(curLen);
					}
					else {
						nextText = textLeft.substring(0, curLen);
						textLeft = textLeft.substring(curLen);
					}

					textR.height += fm.getAscent()+fm.getDescent();
				}
				else {
					if (!textLeft.equals("")) { //$NON-NLS-1$
						textR.height += (fm.getDescent())*2;
					}
					nextText = textLeft;
					textLeft = ""; //$NON-NLS-1$
				}

				int thisWidth = fm.stringWidth( nextText );
				if ( thisWidth > widestLine) {
					widestLine = thisWidth;
				}
			}
		}
		else {
			widestLine = fm.stringWidth( text );
			textR.height += (fm.getDescent()*2);
		}

		textR.width = widestLine;
		if (iconR.width > textR.width) {
			textR.width = iconR.width;
		}
		
		Dimension rv = iconR.union(textR).getSize();
		
		//Add extra space for new node creation arrows
		rv.width += dx+1;
		rv.height += dy;

		nodeWidth = rv.width;
		
		int maxWidth = nodeWidth;
		// Calculate x poistion of the node icon
		if (pos.getHideIcon() || icon == null) {
			iconR.x = maxWidth/2;
		}
		else {
			iconR.x = (maxWidth - iconR.width)/2;
		}

		// Next two lines added by Andrew
		this.setLabelRectangle(textR);
		this.setIconRectangle(iconR);
		System.out.println("*** Node: " + oNode.getText() + " ***");
		System.out.println("NP : x = " +oNode.getNodePosition().getXPos() + ", Y = " + oNode.getNodePosition().getYPos());
		if (iconRectangle != null)
			System.out.println("IconR : x = " +  this.getIconRectangle().x + ", y = " + this.getIconRectangle().y);
		if (labelRectangle != null)
			System.out.println("LabelR : x = " +  this.getLabelRectangle().x + ", y = " + this.getLabelRectangle().y);
		if (textRectangle != null)
			System.out.println("TextR : x = " +  this.getTextRectangle().x + ", y = " + this.getTextRectangle().y);
		System.out.println("***************************");
		return rv;
	}
	**/
	/**
	 * Create a SVG representation of  this node, for occurences when the SVG for 
	 * transcluded occurences have already been created, i.e. a representation which
	 * refers to the original via a <use> tag.
	 * @param oDoc, the SVG document to which the SVG representation of this node will be added.
	 */
	public void generateTranclusionUseSVG( Document oDoc) {
		String sIdThisNode = this.getNode().getId();
		if (oDoc == null )
			return;
		//Need to generate  code akin to: <use x="316" y="263" xlink:href="#pro.137108491691311175409673"/>
		String sNodeClass = "";
		try	{
			sNodeClass = SvgExport.hmNodeClasses.get(this.getNode().getType());
		}
		catch (Exception e) {
			System.out.print("Array SvgExport.sStandardNodeClasses out of bounds: index accessed = "  + this.getNode().getType());
		}
		// Find the transcluded node whose representtion has already been written
		String sIdofViewTranscludedInto = SvgExport.hmDocToTransWrittenInView.get(oDoc).get(sIdThisNode);
		if (sIdofViewTranscludedInto != null)	{
			View  oViewTransCludedInto = View.getView(sIdofViewTranscludedInto);
			NodePosition oNpSource = oViewTransCludedInto.getNodePosition(sIdThisNode);
			int iXPosSource = oNpSource.getXPos(); int iYPosSource = oNpSource.getYPos();
			// Element oSvgParentView = oDoc.getElementById(SvgExport.sMapViewClass+SvgExport.sIdFragmentConnector + this.getViewPane().getView().getId());
			Element oSvgParentView = this.getSvgParentMap(this.getViewPane().getView().getId(), oDoc);
			NodePosition oNp = this.getNodePosition();
			int iXPosTrans = oNp.getXPos(); int iYPosTrans = oNp.getYPos();
			Element oUse = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_USE_TAG);
			oUse.setAttribute(SVGGraphics2D.XLINK_HREF_QNAME, org.apache.batik.svggen.SVGSyntax.SIGN_POUND + sNodeClass + SvgExport.sIdFragmentConnector + sIdThisNode); 
			oUse.setAttribute("x",  Integer.toString(iXPosTrans - iXPosSource));
			oUse.setAttribute("y", Integer.toString(iYPosTrans - iYPosSource));
			oSvgParentView.appendChild(oUse);
			Comment oComment = oDoc.createComment("Start of Transclusion: " + sNodeClass  + " " + this.getNode().getId());
			oSvgParentView.insertBefore(oComment, oUse);
			oComment = oDoc.createComment("End  of Transclusion " + sNodeClass  + " " + this.getNode().getId());
			oSvgParentView.appendChild(oComment);
		}
	}
	
	/**
	 * Create a SVG representation of  the main component of this node.
	 * Does nothing at the moment, included so as to mimic the structure
	 * of paint() methods, and in case it is useful for further development.
	 * @param oDoc, the SVG document to which the SVG representation of this node will be added.
	 */
	public void generateComponentSVG(Document oDoc)	{
		
	}
	/**
	 * Paint on a SVGGraphics2D graphics context, allowing addition of SVG elements.
	 * 
	 * @param g
	 */
	public void paint(SVGGraphics2D g)	{ 
		
		// Get a DOMImplementation.
        DOMImplementation domImpl =
        	SVGDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
     // Create an SVG DOM document
		oSvgRepresentation = domImpl.createDocument(svgNS, "svg", null);
	// Paint to create SVG code representing this UINode
		paintComponent( g);
		paintBorder( g);
		paintChildren( g);
	}
	/**
	 * Paint on a SVGGraphics2D graphics context, allowing addition of SVG elements.
	 * Note - only a comment is added so far, just to show it can be done.
	 * @param g
	 */
	public void paintComponent(SVGGraphics2D svgGen)	{
		if (oSvgRepresentation == null )
			return;
		// Get the NodeUI so can get text reactanle etc
		NodeUI oNodeUI = this.getUI();
		int iXpos = this.getX();
		int iYpos = this.getY();
		
		double iconRXpos =  oNodeUI.getIconRectangle().getX() + iXpos;
		double iconRYpos =  oNodeUI.getIconRectangle().getY() + iYpos;
		Document oDoc = svgGen.getDOMFactory();
		/** 
		// Spawn a new Graphics2D for this component
	       SVGGraphics2D gTemp = (SVGGraphics2D)svgGen.create();
		NodeList oNLg = gTemp.getTopLevelGroup().getElementsByTagNameNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_G_TAG);
		int count = oNLg.getLength(); Node oTemp;
		for (int i=0; i<count; ++i)	{
			oTemp = oNLg.item(i);
			String sVal = oTemp.getNodeValue();
			oTemp.getNodeType();
		}
		 
      Element oRoot = g.getRoot();
       
       NodeList oNLg = oRoot.getElementsByTagName(SVGGraphics2D.SVG_G_TAG);
       NodeList oNLdefs = oRoot.getElementsByTagName(SVGGraphics2D.SVG_DEFS_TAG);
       NodeList oNLsvg = oRoot.getElementsByTagName(SVGGraphics2D.SVG_SVG_TAG);
       
       oNLg =  oRoot.getElementsByTagNameNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_G_TAG);
       oNLdefs =  oRoot.getElementsByTagNameNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_DEFS_TAG);
       oNLsvg = oRoot.getElementsByTagNameNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_SVG_TAG);
       **/
		// Spawn a new Graphics2D for this component
	       SVGGraphics2D g = (SVGGraphics2D)svgGen.create();
       // Get the id of the View that this node is in
      String sViewId =  this.getNodePosition().getView().getId();
     Element oParentMapElement =  oDoc.getElementById(SvgExport.sMapViewClass + SvgExport.sIdFragmentConnector + sViewId);
     // Create the node class description text 
    // Make sure type does not fall outside bounds of SvgExport.sStandardNodeClasses array
		String sNodeClass = SvgExport.sGeneralClass;
		try	{
			sNodeClass = SvgExport.hmNodeClasses.get(this.getNode().getType());
		}
		catch (Exception e) {
			System.out.print("Array SvgExport.sStandardNodeClasses out of bounds: index accessed = "  + this.getNode().getType());
			}
		
       g.setColor(this.getForeground());
       g.setFont(this.getFont());
       Element topLevelGroup = g.getTopLevelGroup();
       
       Comment oComment = oDoc.createComment("Start of Node " + sNodeClass  + " " + this.getNode().getId());
		Element oTitle = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_TITLE_TAG);
		oTitle.setTextContent(this.getNode().getLabel());
		
		Element oDesc = oDoc.createElement("desc");
		oDesc.setAttribute("class", "nodeDetails");
		oDesc.setTextContent(this.getNode().getDetail());
		Element oGroup = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_G_TAG);
		oGroup.setAttribute("id", sNodeClass+SvgExport.sIdFragmentConnector + this.getNode().getId());
		oGroup.setAttribute("class", sNodeClass );
		oGroup.appendChild(oTitle);
		oGroup.appendChild(oDesc);
		
		Element oUse = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_USE_TAG);
		oUse.setAttribute(SVGGraphics2D.XLINK_HREF_QNAME, org.apache.batik.svggen.SVGSyntax.SIGN_POUND + sNodeClass); 
		oUse.setAttribute("x", Double.toString(iconRXpos));
		oUse.setAttribute("y", Double.toString(iconRYpos));
		oGroup.appendChild(oUse);
		/**	Layout the text	**/
		Element oText = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_TEXT_TAG);
		Vector<TextRowElement> oTREs = oNodeUI.getTextRowElements();
		if (oTREs.isEmpty())	{
			oText.setAttribute("x", Double.toString(oNodeUI.getLabelRectangle().getX() + iXpos));
			oText.setAttribute("y", Double.toString( oNodeUI.getLabelRectangle().getY() + iYpos));
			oText.setAttribute("class", "nodelabel");
			oText.setTextContent(this.getText());
			oGroup.appendChild(oText);
		}
		else	{
			TextRowElement oCurrentElement = null;
			Element oTspan = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_TSPAN_TAG);

			for (int i=0; i<oTREs.size(); ++i)	{
				oCurrentElement = oTREs.elementAt(i);
				oCurrentElement.getStartPosition();
				if (i==0)	{
					oText.setAttribute("x", Double.toString( oCurrentElement.getTextRect().getX()+ iXpos) );
					oText.setAttribute("y", Double.toString( oCurrentElement.getTextRect().getY()+iYpos ) );
					oText.setAttribute("class", "nodelabel");
					oText.setTextContent(oCurrentElement.getText());
					oGroup.appendChild(oText);
				}
				else	{
					oTspan.setAttribute("x", Double.toString( oCurrentElement.getTextRect().getX()+ iXpos) );
					oTspan.setAttribute("y", Double.toString( oCurrentElement.getTextRect().getY() + iYpos));
					oTspan.setTextContent(oCurrentElement.getText());
					oText.appendChild(oTspan);
				}
			}
		}
		topLevelGroup.appendChild(oComment);
		topLevelGroup.appendChild(oGroup);
		
	//	oParentMapElement.appendChild(oComment);  DOES NOT WORK because oParentMapElement is null
		//oParentMapElement.appendChild(oGroup);
		
		//Next line is here just so effect of line above can be viwed in debugger
		if (oNodeUI.hasText())	{ // DRAW * IF HAS DETAILS
			 // Spawn a new Graphics2D for this component  -*** IS THIS NECESSARY ?????
		      // g = (SVGGraphics2D)svgGen.create();
		       /** Example code
		        * <g class="details" onmouseover="show_details(evt)">
		        *  <text x="274" y="110" class="indicator">*</text>
		        *  </g>
		        **/
			Element oDetailsGroup = oNodeUI.createSVGDetailsGroup(oDoc);
		/**       Element oDetailsGroup = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_G_TAG);
		       oDetailsGroup.setAttribute(SVGGraphics2D.SVG_CLASS_ATTRIBUTE, "details");
		       oDetailsGroup.setAttribute(SVGGraphics2D.SVG_ONMOUSEOVER_ATTRIBUTE, "show_details(evt)");
		       Element oDetailsIndicator =  oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_TEXT_TAG);
		       oDetailsIndicator.setAttribute("x", Double.toString(oNodeUI.getTextRectangle() .getX() + iXpos));
		       oDetailsIndicator.setAttribute("y", Double.toString(oNodeUI.getTextRectangle() .getY() + iYpos));
		       oDetailsIndicator.setTextContent("*");
		       oDetailsGroup.appendChild(oDetailsIndicator);
		       **/
		       oGroup.appendChild(oDetailsGroup);
		}
		if (oNodeUI.hasTrans())	{ // DRAW TRANSCLUSION INDICATOR  IF NODE HAS TRANSCLUIONS 
			/**	Example SVG Code	*****
			 *  <g class="transclusion indicator"   onmouseover="show_transclusions(evt)" onmouseout="hide_transclusions(evt)" onclick="startShow_transclusions(evt)" >
      		*	<title>Transclusions</title>
      		*	<text class="indicator" x="94" y="132" >2</text>
      		*		<g class="details" display="none">
        	*		<rect class="border" x="100" y="162" width="120" height="30"></rect>
        	*		<!-- Note that <text> element does not have x and y co-ordinates so all text must be palce within the <tspan>s to show up in the correct place. -->
       		*		<text  class="transclusionLinks"><tspan x="104" y="172">1.This map</tspan>.
            *       <tspan x="104" y="186">2. <a xlink:href="mapactivity1.svg "> Activity to develop..</a>.</tspan> 
        	*		</text>
      		*		</g>
      		*	</g>
			*/
			Element oTransGroup = oNodeUI.createSVGTransclusionGroup(oDoc);
			oGroup.appendChild(oTransGroup);
		}
		
		if (oNodeUI.hasCodes())	{
			Element oTagsGroup = oNodeUI.createSVGTagsGroup(oDoc);
			oGroup.appendChild(oTagsGroup);
		}
		if (oNodeUI.hasWeight())	{
			Element oWeightGroup = oNodeUI.createSVGWeightGroup(oDoc);
			oGroup.appendChild(oWeightGroup);
		}
		topLevelGroup.appendChild(oGroup);
		//oParentMapElement.appendChild(oGroup);
		oComment = oDoc.createComment("End  of Node " + sNodeClass  + " " + this.getNode().getId());
		topLevelGroup.appendChild(oComment);
		//oParentMapElement.appendChild(oComment);
		svgGen.setTopLevelGroup(topLevelGroup);
		oDoc.getClass();
	
/**		Element oLinkElement = oSVGDoc.createElement("A");
		oLinkElement.setAttribute("href", this.getNode().getId());
		g.getDOMFactory().appendChild(oLinkElement);
		**/		
	}
	
	/**
	 * Handle a PropertyChangeEvent.
	 * @param evt, the associated PropertyChangeEvent to handle.
	 */
	public void propertyChange(PropertyChangeEvent evt) {

	    String prop = evt.getPropertyName();
   		Object source = evt.getSource();
	    Object oldvalue = evt.getOldValue();
	    Object newvalue = evt.getNewValue();
	    
		if (source instanceof NodePosition) {
		    if (prop.equals(NodePosition.POSITION_PROPERTY)) {
				firePropertyChange(NodePosition.POSITION_PROPERTY, oldvalue, newvalue);
			}
		    else if (prop.equals(NodePosition.FONTFACE_PROPERTY)) {
				Font font = getFont();
				Font newFont = new Font((String)newvalue, font.getStyle(), font.getSize());
				setFont(newFont);		
		    	getUI().refreshBounds();
			}	
		    else if (prop.equals(NodePosition.FONTSTYLE_PROPERTY)) {
				Font font = getFont();
				Font newFont = new Font(font.getName(), ((Integer)newvalue).intValue(), font.getSize());	
				setFont(newFont);	
		    	getUI().refreshBounds();
			}		    
		    else if (prop.equals(NodePosition.FONTSIZE_PROPERTY)) {
				Font font = getFont();
				int newsize = ((Integer)newvalue).intValue();
				Font newFont = new Font(font.getName(), font.getStyle(), newsize);				
				setFont(newFont);	//scales	
				
				int adjustment = ProjectCompendium.APP.getToolBarManager().getTextZoom();
				font = getFont();
				Font adjustedFont = new Font(font.getName(), font.getStyle(), font.getSize()+adjustment);	
				super.setFont(adjustedFont);
				
				getUI().refreshBounds();		
			}	
		    else if (prop.equals(NodePosition.TEXT_FOREGROUND_PROPERTY)) {
		    	//setForeground(new Color( ((Integer)newvalue).intValue() ));
		    	getUI().refreshBounds();
			}		    
		    else if (prop.equals(NodePosition.TEXT_BACKGROUND_PROPERTY)) {
		    	//setBackground(new Color( ((Integer)newvalue).intValue() ));
		    	getUI().refreshBounds();
			}		    		    
		    else if (prop.equals(NodePosition.TAGS_INDICATOR_PROPERTY)) {
		    	getUI().refreshBounds();
			}		    
		    else if (prop.equals(NodePosition.TEXT_INDICATOR_PROPERTY)) {
		    	getUI().refreshBounds();
			}		    
		    else if (prop.equals(NodePosition.TRANS_INDICATOR_PROPERTY)) {
		    	getUI().refreshBounds();
			}		    
		    else if (prop.equals(NodePosition.WEIGHT_INDICATOR_PROPERTY)) {
		    	getUI().refreshBounds();
			}		    
		    else if (prop.equals(NodePosition.HIDE_ICON_PROPERTY)) {
		    	getUI().refreshBounds();
			}		    
		    else if (prop.equals(NodePosition.SMALL_ICON_PROPERTY)) {
		    	int nType = oNode.getType();
		    	ImageIcon icon = null;
				if (nType == ICoreConstants.REFERENCE || nType == ICoreConstants.REFERENCE_SHORTCUT) {
					String image  = oNode.getImage();
					if ( image != null && !image.equals(""))	{
						if (oNode.getLdType() == ILdCoreConstants.iLD_TYPE_RESOURCE && !oNode.getSource().equals("") )
							setReferenceIcon( oNode.getSource() );
						else
							setReferenceIcon( image );
					}
					else {
						setReferenceIcon( oNode.getSource() );
					}
				}
				else if(nType == ICoreConstants.MAPVIEW || nType == ICoreConstants.LDMAPVIEW || nType == ICoreConstants.MAP_SHORTCUT ||
						nType == ICoreConstants.LISTVIEW || nType == ICoreConstants.LIST_SHORTCUT) {
					String image  = oNode.getImage();
					if ( image != null && !image.equals(""))
						setReferenceIcon( image );
					else {
						icon = getNodeImage(oNode.getType(), oPos.getShowSmallIcon());
						refreshIcon( icon );
					}
				}
				else {
					icon = getNodeImage(oNode.getType(), oPos.getShowSmallIcon());
					refreshIcon( icon );
				}
				updateLinks();
			}	
		    else if (prop.equals(NodePosition.WRAP_WIDTH_PROPERTY)) {
		    	getUI().refreshBounds();
			}		    		    
		}
		else if (source instanceof NodeSummary) {

		    if (prop.equals(NodeSummary.LABEL_PROPERTY)) {
				setText((String)newvalue);
				updateLinks();
		    }
		    else if (prop.equals(NodeSummary.TAG_PROPERTY)) {
		    	firePropertyChange(NodeSummary.TAG_PROPERTY, oldvalue, newvalue);
		    }
		    else if (prop.equals(NodeSummary.DETAIL_PROPERTY)) {
		    	firePropertyChange(NodeSummary.DETAIL_PROPERTY, oldvalue, newvalue);
		    }
		    /* Selected by NODE_TYPE_PROPERTY or LD_NODE_TYPE_PROPERTY so that 
		     * once all ld node type integers  are are use to create 'real' 
		     * NodeSummary (and corresponding UI class) objects, the || can be removed
		     * and the handleTypeChange() method implemented or inherited for each 
		     * UI class.
		     * 
		     */
		    else if (prop.equals(NodeSummary.NODE_TYPE_PROPERTY) || prop.equals(NodeSummary.LD_NODE_TYPE_PROPERTY)) {
		    	handleTypeChange(evt, oldvalue, newvalue);
		    }

		    else if (prop.equals(NodeSummary.VIEW_NUM_PROPERTY)) {
				firePropertyChange(NodeSummary.VIEW_NUM_PROPERTY, oldvalue, newvalue);
		    }
		    else if (prop.equals(NodeSummary.STATE_PROPERTY)) {
		    	firePropertyChange(NodeSummary.STATE_PROPERTY, oldvalue, newvalue);
		    }

		    else if (prop.equals(NodeSummary.IMAGE_PROPERTY)) {
		    	// THIS DOES NOT WORK - THE EVENT DOES NOT GET CALLED AS EXPECTED
		    	// USE ProjectCOmpendiumFrame.refreshIcons(String sNodeID)
				//String image = (String)newvalue;
				//if (image != null && !image.equals("")) {
				//	setReferenceIcon(image);
				//}
		    }
		    else if (prop.equals(NodeSummary.SOURCE_PROPERTY)) {
		    	// THIS DOES NOT WORK - THE EVENT DOES NOT GET CALLED AS EXPECTED
		    	// USE ProjectCOmpendiumFrame.refreshIcons(String sNodeID)
				//String sReference = (String)newvalue;
				//if (oNode.getImage().equals("") ) {
				//	setReferenceIcon( sReference );
		    	//}
		    }
		    else if (prop.equals(View.CHILDREN_PROPERTY)) {
				firePropertyChange(CHILDREN_PROPERTY, oldvalue, newvalue);
		    }
		}

	    repaint();
	}

	/**
	 * This method carries out the processing required to handle node type 
	 * changes.
	 * 
	 * @param oldvalue, the old value of the node type (as an Object)
	 * @param newvalue, the new value of the node type (as an Object)
	 */
	protected void handleTypeChange(PropertyChangeEvent evt, Object oldvalue, Object newvalue) {
		//Don't need to use evt, it's only needed for Ld Sub classes

		NodeSummary oldnode = oNode;
		NodeSummary newnode = NodeSummary.getNodeSummary(oldnode.getId());

		int nNewType = ((Integer)newvalue).intValue();
		int nOldType = ((Integer)oldvalue).intValue();

		// IF THE NODE SHOULD CHANGE CLASS AND HAS NOT YET, CHANGE IT.
		// ONLY WANT THE DATABASE READ TO HAPPEN ONCE.
		// AFTER THAT, THE NEW OBJECT CAN BE RETRIEVED FROM CACHE
		String oldClassName = oldnode.getClass().getName();
		String newClassName = newnode.getClass().getName();
		IModel model = oNode.getModel();
//Change by Andrew - LDMAPVIEW added to types consider in the && and || statements
	   	if ( (nOldType > ICoreConstants.PARENT_SHORTCUT_DISPLACEMENT && nNewType <=
					ICoreConstants.PARENT_SHORTCUT_DISPLACEMENT)

			|| ( (nOldType == ICoreConstants.LISTVIEW || nOldType == ICoreConstants.MAPVIEW || nOldType == ICoreConstants.LDMAPVIEW)
 					&& (nNewType != ICoreConstants.LISTVIEW && nNewType != ICoreConstants.MAPVIEW && nNewType != ICoreConstants.LDMAPVIEW) )

 			|| ( (nOldType != ICoreConstants.LISTVIEW && nOldType != ICoreConstants.MAPVIEW && nOldType != ICoreConstants.LDMAPVIEW)
 					&& (nNewType == ICoreConstants.LISTVIEW || nNewType == ICoreConstants.MAPVIEW || nNewType == ICoreConstants.LDMAPVIEW) ) ) {

			// IF NOT BEEN RECREATED YET, DO IT.
			if (oldClassName.equals(newClassName)) {
				try {
					newnode = model.getNodeService().getNodeSummary(model.getSession(), oNode.getId());
				}
				catch(Exception ex) {
					ex.printStackTrace();
					System.out.println("Exception (UINode.propertyChange)\n\n"+ex.getMessage());
				}
			}
		}

		if (nOldType == ICoreConstants.LISTVIEW || nOldType == ICoreConstants.MAPVIEW || nOldType == ICoreConstants.LDMAPVIEW) {
			if (oldnode instanceof View) {
				ProjectCompendium.APP.removeViewFromHistory((View) oldnode);
			}
		}

		// IF THE NODE OBJECT HAS BEEN CHANGED e.g to/from View, ShortcutNodeSummary, ReferenceNode
		if (!oNode.equals(newnode)) {
			//oNode.removePropertyChangeListener(this); // BREAKS LOOP SENDING CHANGE EVENTS
			newnode.addPropertyChangeListener(this);
		}

		newnode.initialize(model.getSession(), model);

		oPos.setNode(newnode);
		oNode = newnode;
		oNodeType = newnode.getType();

		setHelp(newnode.getType());

		restoreIcon();

		changeLinkColour(nNewType, nOldType);
		UIViewPane pane = getViewPane();

		if (pane != null) {
			pane.validateComponents();
			pane.repaint();
		}		
	}


	/**
	 * Clean up class variables to help with garbage collection.
	 */
	public void cleanUp() {

	    NodeUI nodeui = getUI();
	    nodeui.uninstallUI(this);

	    sText						= null;
	    oDefaultIcon				= null;
	    oNode						= null;
	    oPos						= null;

	    dragSource = null;
	    dropTarget = null;

	    if (htLinks != null)
		htLinks.clear();
	    htLinks = null;
	}
}
