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


package com.compendium.learningdesign.ui.draggable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import com.compendium.core.datamodel.NodeSummary;
import com.compendium.learningdesign.core.ILdCoreConstants;
import com.compendium.ui.toolbars.system.DraggableToolBarIcon;

public class LdDraggableToolBarIcon extends DraggableToolBarIcon implements DragSourceListener, DragGestureListener,  Transferable {
	/** The NodeSummary object associated with this draggable toolbar icon.*/
	private NodeSummary oNodeSummary = null;
	
	/** The data flavors supported by this class.*/
    public static final 		DataFlavor[] supportedFlavors = { null };
	static    {
		try { supportedFlavors[0] = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType); }
		catch (Exception ex) { ex.printStackTrace(); }
/**		try { String dataType = DataFlavor.javaJVMLocalObjectMimeType +
		    ";class=com.compendium.core.datamodel.NodeSummary"; 
			supportedFlavors[0] = new DataFlavor(dataType); }
		catch (Exception ex) { ex.printStackTrace(); } 	**/
	}



	public LdDraggableToolBarIcon(String sIdentifier, ImageIcon oIcon)  {
		super( sIdentifier,  oIcon) ;
	}
	
	
	/**
	 * The Constructor.
	 *
	 * @param String identifier of this draggable icon.
	 * @param ImageIcon oIcon, the image to draw for this toolbar icon button.
	 */
  	public LdDraggableToolBarIcon(NodeSummary oNS, ImageIcon oIcon) {

		super(oIcon);

		this.setSIdentifier(oNS.getId());
		this.setDragSource(new DragSource());
		oNodeSummary = oNS;
		this.getDragSource().createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);
  	}
	/**public LdDraggableToolBarIcon(NodeSummary oNS, ImageIcon oIcon)  {
		this( ILdCoreConstants.sACTIVITY_TAG,  oIcon) ;
		oNodeSummary = oNS;
		this.getDragSource().createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);
	}
	**/
	public String getLabel()	{
		if (oNodeSummary != null)
			return this.oNodeSummary.getLabel();
		else
			return "";
	}
	
	 public NodeSummary getNodeSummary() {
		return oNodeSummary;
	}


	/**
     * A <code>DragGestureRecognizer</code> has detected
     * a platform-dependent drag initiating gesture and
     * is notifying this listener
     * in order for it to initiate the action for the user.
     * <P>
     * @param e the <code>DragGestureEvent</code> describing
     * the gesture that has just occurred
     */
	public void dragGestureRecognized(DragGestureEvent e) {
	    InputEvent in = e.getTriggerEvent();
	    if (in instanceof MouseEvent) {
			MouseEvent evt = (MouseEvent)in;
			boolean isLeftMouse = SwingUtilities.isLeftMouseButton(evt);

		    if (isLeftMouse && !evt.isAltDown()) {
				StringSelection text = new StringSelection(this.oNodeSummary.getId());
				//this.getDragSource().startDrag(e, DragSource.DefaultCopyDrop, this, this);
				this.getDragSource().startDrag(e, DragSource.DefaultCopyDrop, this, this);
			}
		}
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
		if (flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType))
			return this;
		else return null;
	}


	 /**
     * Returns an array of DataFlavor objects indicating the flavors the data
     * can be provided in.
     * @return an array of data flavors in which this data can be transferred
     */
	public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}


	 /**
     * Returns whether or not the specified data flavor is supported for
     * this object.
     * @param flavor the requested flavor for the data
     * @return boolean indicating whether or not the data flavor is supported
     */
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType);
	}
}
