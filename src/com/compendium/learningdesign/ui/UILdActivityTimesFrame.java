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
import java.beans.*;
import java.util.LinkedHashSet;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Iterator;
import com.compendium.learningdesign.util.TimeUnit;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.beans.PropertyChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.WindowConstants;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableColumn;

import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.NodeSummary;

import com.compendium.learningdesign.core.ILdCoreConstants;
import com.compendium.learningdesign.ui.dialogs.UILdTaskTimeDialog;
import com.compendium.learningdesign.ui.panels.UILdTaskTimesTable;
import com.compendium.learningdesign.ui.panels.UILdTaskTimesTable.TaskTimesTableModel;
import com.compendium.learningdesign.core.datamodel.LdActivityView;
import com.compendium.learningdesign.ui.panels.UILdActivityTimesPanel;
import com.compendium.learningdesign.ui.UILdTaskSequence;
import com.compendium.learningdesign.core.datamodel.*;

public class UILdActivityTimesFrame extends javax.swing.JInternalFrame implements PropertyChangeListener, ChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8327905507531015507L;
	
	/** A reference to the time unit changed  property for PropertyChangeEvents.*/
    public static final String TIME_UNIT_CHANGED 		= "timeUnitChanged";
    
	private UILdActivityTimesPanel oUILdActivityTimesPanel;
	private JScrollPane oScrollPane;
	private UILdTaskTimesTable oTaskTimesTable;
	private JSpinner oTaskTimeUnitsSpinner;
	private UILdViewPane oParent = null;
	
	/** The units that the task time is in (one of hours, minutes, days or months)	**/
	/** A TimeUnit represents time durations at a given unit of granularity and
	 *   provides utility methods to convert across units.
	 * @uml.property  name="oTaskTimeUnits"
	 */
	private TimeUnit oTaskTimeUnits = TimeUnit.HOURS;
	

	/**
	* Auto-generated main method to display this 
	* JInternalFrame inside a new JFrame.
	*/
	public static void main(String[] args) {
		JFrame frame = new JFrame();
	//	JInternalFrame iFrame = new JInternalFrame();
		UILdActivityTimesFrame inst = new UILdActivityTimesFrame();
		JDesktopPane jdp = new JDesktopPane();
		jdp.add(inst);
		jdp.setPreferredSize(inst.getPreferredSize());
		frame.setContentPane(jdp);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	
	
	public UILdActivityTimesFrame() {
		super();
		initGUI();
	}
	
	public UILdActivityTimesFrame(UILdViewPane owner) {
		super();
		oParent = owner;		
		initGUI();
	}
	
	private void initGUI() {
		try {
			this.setTitle("Timing info");
			{	
				oTaskTimesTable = new UILdTaskTimesTable();
/**********************
				LdActivityTimes oLdActivityTimes  = ((LdActivityView)oParent.getView()).getLdActivityTimes();
				LinkedHashSet<LdTaskSequence> oTaskSequenceSet = oLdActivityTimes.getTaskSequenceSet();
				Iterator<LdTaskSequence> oIt = oTaskSequenceSet.iterator();
				UILdTaskSequence oUILdTaskSequence;
				while (oIt.hasNext()){
					oUILdTaskSequence = new UILdTaskSequence(oIt.next(), oParent);
					oTaskTimesTable.addTaskSequence(oUILdTaskSequence);
				}
				
	************/
				oScrollPane = new JScrollPane(oTaskTimesTable);
				oTaskTimeUnitsSpinner = this.createTaskTimeUnitsSpinner();
				
				getContentPane().add(getOScrollPane(), BorderLayout.CENTER);
				getContentPane().add(getTaskTimeUnitsSpinner(), BorderLayout.LINE_END);
				int iCPH = getContentPane().getHeight();
				int iCPW = getContentPane().getWidth();
				int iOSPH = oScrollPane.getHeight();
				int iOSPW = oScrollPane.getWidth();
			//	oScrollPane.setPreferredSize(new java.awt.Dimension(207, 18));
				oScrollPane.setPreferredSize(new java.awt.Dimension(207, 38));
				oScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
				oScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				oScrollPane.validate();
				//oScrollPane.setSize(207, 18);
				
			}
			
		/**	Commented 03/07/2009
		
			this.setSize(new java.awt.Dimension(217, 76));
			**/
			
			this.setResizable(true);
			
			this.setFrameIcon(UILdImages.getNodeIcon(ILdCoreConstants.iLD_TYPE_ACTIVITY));
			this.pack();
			this.addPropertyChangeListener(this);
			// Now visibility is set by the caller i.e a UILdViewPane
			// setVisible(true);
			
			/**
			{
				oUILdActivityTimesPanel = new UILdActivityTimesPanel(this);
				getContentPane().add(getOUILdActivityTimesPanel(), BorderLayout.CENTER);
			}
			**/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Create a JSpinner which the user can use to select the units to display
	 * the time values.
	 * @return
	 */
	private JSpinner createTaskTimeUnitsSpinner()	{
		SpinnerListModel oTaskTimeUnitsSpinnerModel = 
			new SpinnerListModel(
					new String[] { 
							UILdTaskTimeDialog.sMINUTES, UILdTaskTimeDialog.sHOURS ,
							UILdTaskTimeDialog.sDAYS});
// Include , UILdTaskTimeDialog.sMONTHS , UILdTaskTimeDialog.sYEARS later if  necessary.		
		oTaskTimeUnitsSpinner = new JSpinner();
		oTaskTimeUnitsSpinner.setModel(oTaskTimeUnitsSpinnerModel);
		oTaskTimeUnitsSpinner.setValue(UILdTaskTimeDialog.sHOURS);
		// Set width so all characters in 'months' will show 
	//	oTaskTimeUnitsSpinner.setSize(62, oTaskTimeUnitsSpinner.getHeight());
//		oTaskTimeUnitsSpinner.setMaximumSize(new Dimension(50, oTaskTimeUnitsSpinner.getHeight()*2));
	//	oTaskTimeUnitsSpinner.setPreferredSize(new Dimension(62, oTaskTimeUnitsSpinner.getHeight()));
		oTaskTimeUnitsSpinner.addChangeListener(this);
		oTaskTimeUnitsSpinner.validate();
		return oTaskTimeUnitsSpinner;		
	}

	public UILdActivityTimesPanel getOUILdActivityTimesPanel() {
		return oUILdActivityTimesPanel;
	}
	
	public JScrollPane getOScrollPane() {
		return oScrollPane;
	}



	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		Object source = evt.getSource();
		UILdTaskTimesTable oTable = this.getTaskTimesTable();
		TaskTimesTableModel oTableModel = this.getTaskTimesTable().getModel();
		

		if (source instanceof UILdTaskSequence) {
			UILdTaskSequence oUILdTaskSequence = (UILdTaskSequence) source; 
			String sTaskSeqId = oUILdTaskSequence.getLdTaskSequence().getId();
			if (prop.equals(UILdTaskSequence.TIME_CHANGED))	{
				Long newVal = (Long) evt.getNewValue();
				this.getTaskTimesTable().getModel().setValueForSequence(newVal, sTaskSeqId);
			}
			
			if (prop.equals(UILdTaskSequence.ROLE_LABEL_CHANGED))	{
				// Change the appropriate column header 
				String aName = (String) evt.getNewValue();
				this.getTaskTimesTable().setColumnNameForSequence(aName, sTaskSeqId);
			}
			
			if (prop.equals(UILdTaskSequence.ROLE_ADDED))	{
				// Change the appropriate column header 
				String aName = (String) evt.getNewValue();
				this.getTaskTimesTable().setColumnNameForSequence(aName, sTaskSeqId);

			}
			
			if (prop.equals(UILdTaskSequence.ROLE_DELETED))	{
				// Change the appropriate column data 
//				UILdTaskSequence oNewTaskSequence = (UILdTaskSequence)evt.getNewValue();
				String sLabel = "Role " + (this.getTaskTimesTable().getNumColumnsWithNoLabel() + 1);
				this.getTaskTimesTable().setNumColumnsWithNoLabel(this.getTaskTimesTable().getNumColumnsWithNoLabel() + 1);
				this.getTaskTimesTable().setColumnNameForSequence(sLabel, sTaskSeqId);
			}

		}

		if (prop.equals(UILdViewPane.TASK_SEQUENCE_ADDED))	{
			// Write a firePrpertyChange(name, LinkedHashSet<UILdTaskSequence>, LinkedHashSet<UILdTaskSequence> ) method in class UILdViewPane to avoid cast warning below 
			//LinkedHashSet<UILdTaskSequence> newTaskSequenceSet =	(LinkedHashSet<UILdTaskSequence>)evt.getNewValue();
			UILdTaskSequence oNewTaskSequence = (UILdTaskSequence)evt.getNewValue();
			//oNewTaskSequence.getRoleType()
			oTable.addTaskSequence(oNewTaskSequence);			
		}
		
		if (prop.equals(UILdViewPane.TASK_SEQUENCE_DELETED))	{
			// Write a firePrpertyChange(name, LinkedHashSet<UILdTaskSequence>, LinkedHashSet<UILdTaskSequence> ) method in class UILdViewPane to avoid cast warning below 
			LinkedHashSet<UILdTaskSequence> oNewTaskSequenceSet = (LinkedHashSet<UILdTaskSequence>)evt.getNewValue();
			this.getTaskTimesTable().initialiseWithData(oNewTaskSequenceSet);			
		}
		
		if (prop.equals(UILdViewPane.TASK_SEQUENCE_SET_CREATED))	{
			// Write a firePrpertyChange(name, LinkedHashSet<UILdTaskSequence>, LinkedHashSet<UILdTaskSequence> ) method in class UILdViewPane to avoid cast warning below 
			LinkedHashSet<UILdTaskSequence> oNewTaskSequenceSet = (LinkedHashSet<UILdTaskSequence>)evt.getNewValue();
			for (UILdTaskSequence oTS: oNewTaskSequenceSet)	{
				this.getTaskTimesTable().addTaskSequence(oTS);
			}			
		}
/**	This is not needed - change to display is made via the TaskTimesTableModel	
		if (prop.equals(UILdActivityTimesFrame.TIME_UNIT_CHANGED))	{
			updateFiguresDisplayed();
		}
**/
		this.repaint();
	}
		

	/**
	 * Initialise oTaskTimesTable by creating a new instance and setting its
	 * column names and data values depending on the contents of the view 
	 * displayed in the UILdViewPane oParent.
	 */
	public void initTimesTable()	{
		oTaskTimesTable = new UILdTaskTimesTable();
		Vector<NodeSummary> vtNodes = oParent.getView().getMemberNodes();
		for (vtNodes.listIterator();vtNodes.listIterator().hasNext();)	{
			if(vtNodes.listIterator().next().isLdRoleNode())	{
				
			}
		}
	}

	/**
	 * @return the oTaskTimesTable
	 */
	public UILdTaskTimesTable getTaskTimesTable() {
		return oTaskTimesTable;
	}



	/**
	 * Return the JSpinner instance used to select the time units
	 * @return the oTaskTimeUnitsSpinner
	 */
	public JSpinner getTaskTimeUnitsSpinner() {
		return oTaskTimeUnitsSpinner;
	}


	/**
	 * Set the task time unit to be the TimeUnit equivalent of the string 
	 * sTimeUnits.
	 * @param taskTimeUnits the oTaskTimeUnits to set
	 */
	public TimeUnit setTaskTimeUnits(String sTimeUnits) {
		TimeUnit oTU = null;
		if (sTimeUnits.equals(UILdTaskTimeDialog.sMINUTES))	{
			oTU = this.setTaskTimeUnits(TimeUnit.MINUTES);			
		}
		else if (sTimeUnits.equals(UILdTaskTimeDialog.sHOURS))	{
			oTU = this.setTaskTimeUnits(TimeUnit.HOURS);			
		} 
		else if (sTimeUnits.equals(UILdTaskTimeDialog.sDAYS))	{
			oTU = this.setTaskTimeUnits(TimeUnit.DAYS);			
		} 
		else 	{
			ProjectCompendium.APP.displayError("Invalid time unit string: " + sTimeUnits);			
		} 
		return oTU;
	}
	
	/**
	 * @param taskTimeUnits the oTaskTimeUnits to set
	 */
	public TimeUnit setTaskTimeUnits(TimeUnit taskTimeUnits) {
		TimeUnit oldUnits = oTaskTimeUnits;
		oTaskTimeUnits = taskTimeUnits;
		this.getTaskTimesTable().getModel().setTaskTimeUnits(oTaskTimeUnits);
		this.firePropertyChange(UILdActivityTimesFrame.TIME_UNIT_CHANGED, oldUnits, oTaskTimeUnits);
		return taskTimeUnits;
	}


	/**
	 * The state of one the components that this instance listens to has changed.
	 * This method implements that actions that are required in response to the
	 * changes of state.
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent evt) {
		Object source = evt.getSource();
		if (source.equals(oTaskTimeUnitsSpinner) )	{
			String sTimeUnits = (String)oTaskTimeUnitsSpinner.getValue();
			this.setTaskTimeUnits(sTimeUnits);
		}
		
	}
	
	/**
	 * @return oTaskTimeUnits, the selected TimeUnit
	 */
	public TimeUnit getTaskTimeUnits()	{
		return oTaskTimeUnits;	
	}
}

