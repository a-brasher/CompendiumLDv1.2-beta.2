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

import  java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;

import javax.swing.*;
import java.awt.event.ActionListener;

import com.compendium.core.CoreUtilities;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.learningdesign.ui.draggable.LdDraggableToolBarIcon;
import com.compendium.ui.ExecuteControl;
import com.compendium.ui.UINode;
import com.compendium.ui.stencils.DraggableStencilIcon;
import com.compendium.ui.stencils.UIStencilSet;

/**
 * @author ajb785
 *
 */
public class UINodeLdHelpActivityPanel extends JPanel implements  ActionListener, Comparable{
	
	/**
	 * The current UINode the help contents are generated for
	 * @uml.property  name="oUINode"
	 * @uml.associationEnd  
	 */
	private UINode			oUINode			= null;

	/**
	 * The current node data this is the help for.
	 * @uml.property  name="oNode"
	 * @uml.associationEnd  
	 */
	private NodeSummary		oNode			= null;
	
	/**
	 * The  panel holding the icon and link etc.
	 * @uml.property  name="panel"
	 * @uml.associationEnd  
	 */
	private JPanel				panel			= null;
	
	/**
	 * The link to help about the tool	*
	 * @uml.property  name="oHelpLink"
	 * @uml.associationEnd  
	 */
	private JEditorPane	oHelpLink = null;
	
	/**
	 * The stencil icon that the user can drag and drop *
	 * @uml.property  name="oIcon"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
//	private DraggableStencilIcon oIcon = null; 
	private LdDraggableToolBarIcon oIcon = null;
	/**
	 * The Learning design stencil set 	*
	 * @uml.property  name="oLdStencilSet"
	 * @uml.associationEnd  readOnly="true"
	 */
	private UIStencilSet oLdStencilSet;
	
	/**
	 * The title of this panel	*
	 * @uml.property  name="sTitle"
	 */
	private String sTitle = "";
	
	/**
	 * The help button for this panel *
	 * @uml.property  name="pbHelp"
	 * @uml.associationEnd  
	 */
	private JButton pbHelp = null;
	
	/**	The maximum number of chaaracters froma label which will be displayed	**/
	private static final int MAX_LABEL_LENGTH = 12;
	
	/**	The maximum number of chaaracters froma label which will be displayed ithout being abridged	**/
	private static final int MAX_LABEL_STRING = 14;
	
	/**	Dots added to an overlong label to indicate it's tool long **/
	private static final String sTRAILING_DOTS = "...";
		
	/** Constructor for a UINodeLdHelpToolPanel for the DraggableStencilIcon oIcon 	**/		
	public UINodeLdHelpActivityPanel(LdDraggableToolBarIcon oLdIcon) {
		super();
		oIcon = oLdIcon;
		initLdHelpActivityPanel();
	}
	/**
	 * Initialize and draw this panel's contents.
	 * @param node com.compendium.core.datamodel.NodeSummary, the current node to draw the contents for.
	 */
	private void initLdHelpActivityPanel() {
		// set title and background
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBackground(Color.white);
		sTitle = oIcon.getLabel();
		String sLabel = "";
		if (sTitle.length() > UINodeLdHelpActivityPanel.MAX_LABEL_STRING)	{
			sLabel = sTitle.substring(0, UINodeLdHelpActivityPanel.MAX_LABEL_LENGTH)
								+ UINodeLdHelpActivityPanel.sTRAILING_DOTS;
		}
		else	{
			sLabel = sTitle;
		}
		
		/** Set the DraggableStencilIcon's palette image to be the same image as the node image **/
	//	oIcon.setPaletteImage(oIcon.getImage());
		this.add(oIcon,Component.CENTER_ALIGNMENT);
		JLabel lbl = new JLabel(sLabel);
		lbl.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		
		this.add(lbl, Component.CENTER_ALIGNMENT);
		
		Dimension oMinSize = oIcon. getSize();
		/** Do not need the help button
		pbHelp = new JButton("About " + sTitle);
		pbHelp.addActionListener(this);
		
		this.add(pbHelp, Component.CENTER_ALIGNMENT);
		**/
		Dimension d = new Dimension(50, 50);
//		this.setPreferredSize(d);
		this.setMinimumSize(d);
		//Border oBorder = new Border(;)
		this.setBorder(BorderFactory.createLineBorder(Color.black));
	//	showLdHelpToolPanel();
		addComponentListener(new ComponentAdapter() {});		
	}
	
	private void showLdHelpToolPanel() {
	//	panel = new JPanel();
//		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//Delete this?:		centerpanel.setBorder(new EmptyBorder(5,5,5,5));
		// Add the DRaggableStencilIcon
		
// Do not need setVisible(true) because it's done by the caller of UILdInformationDialog() ?
//		this.setVisible(true);

		
	}
	/**
	 * Return the title of this panel.
	 * @return sTitle
	 */
	public String getTitle()	{
		return sTitle;
	}
	/**
	 * Compare two instances of UINodeLdHelpToolPanel.
	 * The comparison is on their titles i.e. the names of the icons contained within
	 * @param o1
	 * @param o2
	 * @return
	 */
	public int compare(UINodeLdHelpActivityPanel o1, UINodeLdHelpActivityPanel o2) {
	    return(o1.getTitle().compareToIgnoreCase(o2.getTitle()));
		
	}

	public int compareTo(Object o) {
		if (o.getClass().equals(this.getClass()))	{
		return(((UINodeLdHelpActivityPanel)o).getTitle().compareToIgnoreCase(this.getTitle()));
		}
		else	{
			return 0;
		}
		
	}

	public void actionPerformed(ActionEvent evt) {
		// TODO Auto-generated method stub
		Object source = evt.getSource();
		if (source.equals(pbHelp))	{
			String sLabel = "";
			try {
				sLabel = CoreUtilities.cleanURLText(sTitle);
			} catch (Exception e) {}
			ExecuteControl.launch( "http://kn.open.ac.uk/public/search.cfm?method=DispSR&searchterm=" + sLabel );			
		}
	}
	
	
	
	
	

}



