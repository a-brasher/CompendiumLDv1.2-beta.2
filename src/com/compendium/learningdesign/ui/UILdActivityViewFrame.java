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

package com.compendium.learningdesign.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.*;

import javax.help.CSH;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.InternalFrameEvent;

import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;
import com.compendium.ui.IUIConstants;
import com.compendium.ui.UIImages;
import com.compendium.ui.UIMapViewFrame;
import com.compendium.ui.UIViewPane;
import com.compendium.ProjectCompendium;

import com.compendium.learningdesign.ui.dialogs.*;
import com.compendium.learningdesign.core.ILdCoreConstants;
import com.compendium.learningdesign.core.datamodel.*;
import com.compendium.learningdesign.core.datamodel.services.*;

/**
 * UILdActivityViewFrame is a  subclass of UIMapViewFrame, and holds the components 
 * of a learning design activity map.  
 *  
 * 
 * @author ajb785
 *
 */
public class UILdActivityViewFrame extends UIMapViewFrame {
	
	/** The UILdViewPane associated with this map frame **/
//	protected UILdViewPane		oViewPane			= null;
	
	public UILdActivityViewFrame(LdActivityView view, String title) {
		
		super(view, title, "[Activity]: ");
		//setBaseTitle("[Activity]: ");
		//setTitle(title);
		init(view);
	}
	/**
	 * @param view
	 */
	public UILdActivityViewFrame(View view) {
		super(view);
		// TODO Auto-generated constructor stub
	}


	/**
	 * Initialize and draw this frame.
	 * The difference between this class and its superclass is that an instance
	 * of UILdActivityTimesDialog is added and  displayed.
	 * 
	 * @param view com.compendium.core.datamodel.View, the view associated with this frame.
	 */
	private void init(LdActivityView view) {

		oContentPane.setLayout(new BorderLayout());
		this.oView = view;

		setViewPane( new UILdViewPane(view, this));

		getViewPane().setBackground(Color.white);

		updateFrameIcon();

		// A Workaround since the scrollbar never sizes on the JLayeredPane for some reason
		// therefore created a panel and added the viewpane to it and finally added the panel
		// to the scrollpane
		// the setPreferredSize is for the scrollpane to resize .  a high number
		// By overriding getPreferredSize in the JPanel, as the JScrollpane calls to find out how big
		// the JPanel is .

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		//reduced size by factor of 10 since size is too big - bz
		panel.setPreferredSize(new Dimension(30000,30000));

		panel.add(getViewPane(), BorderLayout.CENTER);
		
		scrollpane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
												 JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		(scrollpane.getVerticalScrollBar()).setUnitIncrement(50);
		(scrollpane.getHorizontalScrollBar()).setUnitIncrement(50);

		oViewport = scrollpane.getViewport();

		CSH.setHelpIDString(this,"node.views");

		horizontalBar = scrollpane.getHorizontalScrollBar();
		verticalBar = scrollpane.getVerticalScrollBar();
		
		oContentPane.add(scrollpane, BorderLayout.CENTER);
		
		//Now the view port has been set the activity times can be shown
		
		this.setVisible(true);		
	}
	
	/**
	 * Return the UILdViewPane instance associated with this frame.
	 * @param UILdViewPane, the UILdViewPane instance associated with this frame.
	 */
	public UILdViewPane getViewPane() {
		return (UILdViewPane)oViewPane;
	}
	
	/**
	 * Create the UIViewPane instance with the given view.
	 * @param view com.compendium.core.datamodel.View, the view to associated with the new UIViewPane instance.
	 * @return UIViewPane, the new UIViewPOane instance created.
	 */
	public UILdViewPane createViewPane(LdActivityView view) {

		oView = view;

		oViewPane = new UILdViewPane(view, this);
		oViewPane.setBackground(Color.white);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		//reduced size by factor of 10 since size is too big - bz
		panel.setPreferredSize(new Dimension(60000,60000));
		panel.add(oViewPane, BorderLayout.CENTER);
		scrollpane.setViewportView(panel);

		oContentPane.validate();
		oContentPane.repaint();

		return (UILdViewPane)oViewPane;
	}
	
	/**
	 * @param viewPane the oViewPane to set
	 */
	public void setViewPane(UILdViewPane viewPane) {
		oViewPane = viewPane;
	}
	
	/**
     * Update frame top-upper icon when the skin has changed.
     */
    public void updateFrameIcon(){

   		if(oView.getId().equals(ProjectCompendium.APP.getInBoxID())) {
   			setFrameIcon(UIImages.get(IUIConstants.INBOX_SM));
    	}  	
   		else if (getView().getType() == ICoreConstants.LISTVIEW) {
  			setFrameIcon(UIImages.getNodeIcon(IUIConstants.LIST_SM_ICON));
		}
		else{
			if (this.getView().getLdType() == ILdCoreConstants.iLD_TYPE_ACTIVITY )	{
				setFrameIcon(UILdImages.getNodeIcon(ILdCoreConstants.iLD_TYPE_ACTIVITY));
			}
			else
				setFrameIcon(UIImages.getNodeIcon(IUIConstants.MAP_SM_ICON));
      	}
 	}
    
    /**
     * Invoked when an internal frame is in the process of being closed.
     * This method overrides the method in UIViewFrame, by adding 
     * functionality to save the activity timing information.
     * Saves the view properties and remove the view from the desktop.
     * @see javax.swing.JInternalFrame#setDefaultCloseOperation
     */
	public void internalFrameClosing(InternalFrameEvent e) {

		if (ProjectCompendium.APP != null) {
			ProjectCompendium.APP.saveViewProperties(this);		
			String viewId = this.getView().getId();
			// Should not be necessary but, BUG_FIX
			ProjectCompendium.APP.removeView(getView());
			//Update the timing info
			updateLdActivityTimes();
			
			// To set the viewnode state.
			boolean read = false;
			boolean unread = false;
			int i = 0;
			if (getView() == ProjectCompendium.APP.getHomeView()){
				read = true;
			} else {
				Vector nodes = getView().getMemberNodes();
				for(; i < nodes.size(); i ++){
					NodeSummary node = (NodeSummary) nodes.get(i);
					int state = node.getState();
					if(state == ICoreConstants.UNREADSTATE){
						unread = true;
					} else if(state == ICoreConstants.READSTATE){
						read = true;
					}
				}
			}
			try {
				if((read && !unread) ||(i == 0)){
					getView().setState(ICoreConstants.READSTATE);
				} else {
					getView().setState(ICoreConstants.MODIFIEDSTATE);
				}
				
			} catch (SQLException e1) {
				e1.printStackTrace();
				ProjectCompendium.APP.displayError("Exception 1: (UILdActivityViewFrame.internalFrameClosing) " + e1.getMessage());
			} catch (ModelSessionException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Update the activity timing information in the database with data that has been created in the UI. Called when an instance of UILdActivityViewFrame is closed, or when
	 * XML data is exported.
	 */
	public void updateLdActivityTimes()	{
		String viewId = this.getView().getId();
		UILdViewPane oViewPane = this.getViewPane();
		boolean bShowActivityTimes = oViewPane.getShowTimingInfo();
		// Get the set of task sequences for the activity shown in this UILDActivityViewFrame 
		LinkedHashSet<UILdTaskSequence> oUILdTaskSequenceSet = oViewPane.getTaskSequenceSet();
		//
		LdActivityView oLdActivityView = ((LdActivityView)this.getView());
		/** Get the original activity times data, i.e. the data that existed
		 * when the map frame was opened, i.e. before any editing occurred.
		 */ 
		LdActivityTimes oOldLdActivityTimes = oLdActivityView.getLdActivityTimes();
		// The new activity times data i.e, that which exists after editing 
		LdActivityTimes oNewLdActivityTimes = new LdActivityTimes();
		if (oOldLdActivityTimes == null)
			oNewLdActivityTimes = new LdActivityTimes(viewId, oUILdTaskSequenceSet, bShowActivityTimes, oViewPane.getActivityTimesFrame().getTaskTimeUnits());
		else	{
			
			if (oNewLdActivityTimes.isEmpty())	{
				oNewLdActivityTimes.setId(oOldLdActivityTimes.getId());
			}
			oNewLdActivityTimes.setTaskSequenceSet(LdTaskSequence.createLdTaskSequenceSet(oUILdTaskSequenceSet));
			oNewLdActivityTimes.setShowTime(bShowActivityTimes);
			oNewLdActivityTimes.setCurrentTaskTimeUnits(oViewPane.getActivityTimesFrame().getTaskTimeUnits());
		}
		oLdActivityView.setLdActivityTimes(oNewLdActivityTimes);
		/**	Debugging data		***/
	//	System.out.println("*** NEW DATA *******************");
	//	System.out.println(oNewLdActivityTimes.toString());
	//	System.out.println("*** END OF NEW DATA *******************");
		/**  End of Debugging data	***/
		IModel oModel = this.getView().getModel();
		PCSession oSession = oModel.getSession();
		ITaskTimesService oTaskTimesService = oModel.getTaskTimesService();
		try {
			if (oTaskTimesService.getTaskSequencesForActivity(oSession, oNewLdActivityTimes.getId()).isEmpty()){
				oTaskTimesService.createTaskTimes(oSession, oNewLdActivityTimes);
			}
			else	{
				oTaskTimesService.clearTaskTimes(oSession, oOldLdActivityTimes);
				oTaskTimesService.createTaskTimes(oSession, oNewLdActivityTimes);
			}
			// Make sure the old activity times data is no longer related to the activity through its idd just in case
			oOldLdActivityTimes.setId("");
		}
		
		catch (SQLException ex)	{
			ProjectCompendium.APP.displayError("Exception 0: (UILdActivityViewFrame.updateLdActivityTimes) " + ex.getMessage());
			 System.out.println(ex.getStackTrace());
			 System.out.println(ex.getCause());
			 System.out.println(ex.fillInStackTrace());
			 System.out.println(ex.getSQLState());
		}
	}

}
