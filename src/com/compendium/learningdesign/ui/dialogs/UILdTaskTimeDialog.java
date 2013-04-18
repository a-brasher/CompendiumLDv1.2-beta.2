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

/**
This class works to a point  change to JOptionPane because can not easiliy set the parent and icon of a  JDIalog.
**/
package com.compendium.learningdesign.ui.dialogs;
import java.awt.Frame;
import java.awt.Point;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;

import java.text.NumberFormat;
import com.compendium.learningdesign.util.TimeUnit;

import javax.swing.JDialog;
import javax.swing.JSpinner;
import javax.swing.JFormattedTextField;
import javax.swing.SpinnerListModel;
import javax.swing.JFrame;

import com.compendium.ui.dialogs.UIDialog;
import com.compendium.ProjectCompendium;
import com.compendium.learningdesign.core.ILdCoreConstants;
import com.compendium.learningdesign.ui.*;

/**
 * Dialog to enter the learning time for a task.
 * Note that the icon displayed is the icon of the parent component i.e. of
 * ProjectCompendiumFrame.App . Need to set this to be a learning design icon.
 * @author ajb785
 *
 */
public class UILdTaskTimeDialog extends JDialog implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8036644528306030213L;

	/** The node for which the time is being set	**/ 
	private UILdTaskNode oTaskNode = null;
	
	/**	The ActivityViewFrame that this task time dialog is contained in.	**/
	private UILdActivityViewFrame oLdActivityViewFrame;
	
	/** Format for the field in which the time value number is entered. **/ 
	private NumberFormat timeFormat = NumberFormat.getNumberInstance();

	/** The time value, this will be  entered by the user. **/
	private double dbTime = 0;
	
	/** The units that the task time is in (one of hours, minutes, days or months)	**/
	/** A TimeUnit represents time durations at a given unit of granularity and
	 *   provides utility methods to convert across units.
	 * @uml.property  name="oTaskTimeUnits"
	 */
	private TimeUnit oTaskTimeUnits = TimeUnit.HOURS;
	/**
	 * @uml.property  name="txtLearningTime"
	 * @uml.associationEnd  
	 */
	private JFormattedTextField txtTaskTimeField;
	/**
	 * @uml.property  name="oLearningTimeUnitsSpinner"
	 * @uml.associationEnd  
	 */
	private JSpinner oTaskTimeUnitsSpinner;
	/**
	 * @uml.property  name="pbSave"
	 * @uml.associationEnd  
	 */
	private JButton pbSave;
	
	/** The String used to denote that the time unit is minutes. */
	public final static String	sMINUTES 			= "mins";
	
	/** The String used to denote that the time unit is hours. */
	public final static String	sHOURS 			= "hours";
	
	/** The String used to denote that the time unit is days. */
	public final static String	sDAYS 			= "days";

	// Note Months and years not used yet because class TimeUnit does not handle them; would need to subclass TimeUnit
	/** The String used to denote that the time unit is months. */
	// public final static String	sMONTHS 		= "months";
	
	/** The String used to denote that the time unit is months. */
	// public final static String	sYEARS 			= "years";
	/**
	 * @param owner - the parent JFrame
	 * @param title - the title of the dialog which should indicate whether it's 
	 * learner time or tutor time 
	 * @param modal - boolean which sets whether to allow the user to interact 
	 * with other windows before closing this dialog. 
	 * @throws HeadlessException
	 */
	public UILdTaskTimeDialog(JFrame owner, String title, boolean modal)
			throws HeadlessException {
		super(owner, title, modal);
		initGUI(title);
	}
	
	/**
	 * @param owner - the parent JFrame
	 * @param title - the title of the dialog which should indicate whether it's 
	 * learner time or tutor time 
	 * @param modal - boolean which sets whether to allow the user to interact 
	 * with other windows before closing this dialog. 
	 * @throws HeadlessException
	 */
	public UILdTaskTimeDialog(UILdActivityViewFrame aParentActivity, String title, boolean modal)
			throws HeadlessException {
		super(ProjectCompendium.APP, title, modal);
		oLdActivityViewFrame = aParentActivity;
		initGUI(title);
	}

	/**
	 * @param owner - the parent JFrame
	 * @param title - the title of the dialog which should indicate whether it's 
	 * learner time or tutor time 
	 * @param modal - boolean which sets whether to allow the user to interact
	 * @param  oUINode - the UILdNode for which the time is being set
	 * with other windows before closing this dialog. 
	 * @throws HeadlessException
	 */
	public UILdTaskTimeDialog(UILdActivityViewFrame aParentActivity, String title, boolean modal, UILdTaskNode aUILdNode)
			throws HeadlessException {
		super(ProjectCompendium.APP, title, modal);
		oLdActivityViewFrame = aParentActivity;
		oTaskNode = aUILdNode;
		initGUI(title);
	}
	
	/**
	 * @param owner - the parent JFrame
	 * @param title - the title of the dialog which should indicate whether it's 
	 * learner time or tutor time 
	 * @param modal - boolean which sets whether to allow the user to interact
	 * @param  oUINode - the UILdNode for which the time is being set
	 * with other windows before closing this dialog. 
	 * @throws HeadlessException
	 */
	public UILdTaskTimeDialog(UILdTaskNode aUILdNode, String title, boolean modal, UILdActivityViewFrame aParentActivity)
			throws HeadlessException {
		super(ProjectCompendium.APP, title, modal);
		oLdActivityViewFrame = aParentActivity;
		oTaskNode = aUILdNode;
		initGUI(title);
	}
	
	/**
	 * @param owner - the parent JFrame
	 * @param title - the title of the dialog which should indicate whether it's 
	 * learner time or tutor time 
	 * @param modal - boolean which sets whether to allow the user to interact
	 * @param  oUINode - the UILdNode for which the time is being set
	 * with other windows before closing this dialog. 
	 * @throws HeadlessException
	 */
	public UILdTaskTimeDialog(JFrame owner, String title, boolean modal, UILdTaskNode aUILdNode)
			throws HeadlessException {
		super(owner, title, modal);
		
		oTaskNode = aUILdNode;
		initGUI(title);
	}
	/**
	 * @param owner - the parent JFrame
	 * @param title - the title of the dialog which should indicate whether it's 
	 * learner time or tutor time 
	 * @throws HeadlessException
	 */
	public UILdTaskTimeDialog(JFrame owner, String title)
			throws HeadlessException {
		super(owner, title);
		initGUI(title);
	}

	/**
	 * @return
	 * @uml.property  name="txtLearningTime"
	 */
	public JFormattedTextField getTxtLearningTimeField() {
		if(txtTaskTimeField == null) {
			txtTaskTimeField = new JFormattedTextField(timeFormat);
			txtTaskTimeField.setValue(getTaskTime());
			txtTaskTimeField.setPreferredSize(new java.awt.Dimension(37, 21));
			txtTaskTimeField.setSize(37, 21);
		}
		return txtTaskTimeField;
	}
	
	private void initGUI(String sTitle) {
		try {
			BoxLayout thisLayout = new BoxLayout(getContentPane(), javax.swing.BoxLayout.X_AXIS);
			getContentPane().setLayout(thisLayout);
			//this.setIcon(UILdImages.getNodeIcon(ILdCoreConstants.iLD_TYPE_ACTIVITY));
			
			
			this.setTitle(sTitle);
			this.setSize(151, 62);
			double dbTimeToSet = convertTimeFromMinutesToDouble(this.getTaskNode().getTaskTime());
			this.setTaskTime(dbTimeToSet );
			this.add(getTxtLearningTimeField());
			this.add(createTaskTimeUnitsSpinner());
			this.add(getPbSave());
			this.getRootPane().setDefaultButton(getPbSave());
			// Add a property change listener on the time units 
			this.getTaskTimeUnitsSpinner().getEditor().addPropertyChangeListener("timeUnits", oLdActivityViewFrame.getViewPane());
			// Add a property change listener on the time value 
			this.getTxtLearningTimeField().addPropertyChangeListener("timeValue", oLdActivityViewFrame.getViewPane());
			this.pack();
			this.setLocationRelativeTo(this.getTaskNode());
			this.setVisible(true);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
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
		oTaskTimeUnitsSpinner.setSize(37, oTaskTimeUnitsSpinner.getHeight());		
		
		return oTaskTimeUnitsSpinner;		
	}
	
	/**
	 * @return
	 * @uml.property  name="oLearningTimeUnitsSpinner"
	 */
	public JSpinner getTaskTimeUnitsSpinner() {
		return oTaskTimeUnitsSpinner;
	}
	
	/**
	 * @return
	 * @uml.property  name="pbSave"
	 */
	public JButton getPbSave() {
		if(pbSave == null) {
			pbSave = new JButton();
			pbSave.setText("OK");
			pbSave.addActionListener(this);
			
		}
		return pbSave;
	}

	
	
	/**
	 * Handles the event of an option being selected.
	 * @param evt, the event associated with the option being selected.
	 */
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();
		
		if (source.equals(pbSave)) {
//			long time = ((Number)(this.getTxtLearningTimeField().getValue())).longValue();
			double time = ((Number)(this.getTxtLearningTimeField().getValue())).doubleValue();
			this.setTaskTime(time);
			String sTimeUnits = (String)this.getTaskTimeUnitsSpinner().getValue();
			TimeUnit oTU = this.setTaskTimeUnits(sTimeUnits);
			// Convert the task time value to minutes before storing it in oTaskNode 
			oTaskNode.setTaskTime(this.convertTimeToMinutes(time));
			oTaskNode.setCurrentTaskTimeUnits(oTU);
			this.dispose();
		}					
	}

	/**
	 * Returns the UILdTaskNode instance that this dialog is setting the time
	 * for.
	 * @return the oTaskNode
	 */
	public UILdTaskNode getTaskNode() {
		return oTaskNode;
	}

	/**
	 * Get the  value representing the time.
	 * @return the dbTime
	 */
	public double getTaskTime() {
		return dbTime;
	}

	/**
	 * Set the task time to be displayed.
	 * @param oTime the dbTime to set
	 */
	public void setTaskTime(long oTime) {
		double oldTime = getTaskTime();
		this.dbTime = oTime;
		Double dValue = new Double(oTime);
		/**
		if (this.getTaskNode().getViewPane() instanceof UILdViewPane)	{
			((UILdViewPane)this.getTaskNode().getViewPane()).getActivityTimesFrame().getTaskTimesTable().getTableModel().setValueAt(new Integer(dValue.intValue()), 0, 0);
		}
		**/
		firePropertyChange("timeValue", oldTime, oTime);	
	}
	
	/**
	 * Set the task time to be displayed.
	 * @param oTime the dbTime to set
	 */
	public void setTaskTime(double varDbTime) {
		double oldTime = getTaskTime();
		this.dbTime = varDbTime;
//		Double dValue = new Double(oTime);
		/**
		if (this.getTaskNode().getViewPane() instanceof UILdViewPane)	{
			((UILdViewPane)this.getTaskNode().getViewPane()).getActivityTimesFrame().getTaskTimesTable().getTableModel().setValueAt(new Integer(dValue.intValue()), 0, 0);
		}
		**/
		firePropertyChange("timeValue", oldTime, varDbTime);	
	}

	/**
	 * Convert the parameter oTimeValue from HOURS or DAYS to its equivalent  
	 * value in MINUTES.
	 * @param oTimeValue
	 * @return long valInMinutes, a long integer value equal to the task time 
	 * in units of minutes. 
	 */
	private long convertTimeToMinutes(long oTimeValue)	{
		long valInMinutes = 0;
		switch(this.getTaskTimeUnits())	{
			case HOURS: valInMinutes = 60*oTimeValue; break;
			case DAYS:	valInMinutes = 24*60*oTimeValue; break;
			default:	valInMinutes = oTimeValue; break;
		}
		return valInMinutes;
	}
	
	/**
	 * Convert the parameter oTimeValue from HOURS or DAYS to its equivalent  
	 * value in MINUTES.
	 * @param oTimeValue
	 * @return long valInMinutes, a long integer value equal to the task time 
	 * in units of minutes. 
	 */
	private long convertTimeToMinutes(double oTimeValue)	{
		long valInMinutes = 0;
		switch(this.getTaskTimeUnits())	{
			case HOURS: valInMinutes = Math.round(60*oTimeValue); break;
			case DAYS:	valInMinutes = Math.round(24*60*oTimeValue); break;
			default:	valInMinutes = Math.round(oTimeValue); break;
		}
		return valInMinutes;
	}
	
	/**
	 * Convert the parameter oTimeValue from MINUTES (as retrieved from a task node) 
	 * to its equivalent value in HOURS or DAYS or MINUTES, according to the value
	 * of the task nodes oTaskTimeUnits time unit attribute.
	 * @param oTimeValue
	 * @return long valInMinutes, a long integer value equal to the task time 
	 * in units of minutes. 
	 */
	private long convertTimeFromMinutes(long oTimeValue)	{
		long valToDisplay = 0;
		
		switch(this.getTaskNode().getCurrentTaskTimeUnits())	{
			case HOURS: valToDisplay = oTimeValue/60; break;
			case DAYS:	valToDisplay = oTimeValue/(24*60); break;
			default:	valToDisplay = oTimeValue; break;
		}
		return valToDisplay;
	}

	/**
	 * Convert the parameter oTimeValue from MINUTES (as retrieved from a task node) 
	 * to its equivalent value in HOURS or DAYS or MINUTES, according to the value
	 * of the task nodes oTaskTimeUnits time unit attribute.
	 * @param oTimeValue
	 * @return long valInMinutes, a long integer value equal to the task time 
	 * in units of minutes. 
	 */
	private double convertTimeFromMinutesToDouble(long oTimeValue)	{
		double valToDisplay = 0;
		
		switch(this.getTaskNode().getCurrentTaskTimeUnits())	{
			case HOURS: valToDisplay = oTimeValue/60.0; break;
			case DAYS:	valToDisplay = oTimeValue/(24.0*60.0); break;
			default:	valToDisplay = oTimeValue; break;
		}
		return valToDisplay;
	}
	/**
	 * @return the oTaskTimeUnits
	 */
	public TimeUnit getTaskTimeUnits() {
		return oTaskTimeUnits;
	}

	/**
	 * @param taskTimeUnits the oTaskTimeUnits to set
	 */
	public TimeUnit setTaskTimeUnits(TimeUnit taskTimeUnits) {
		oTaskTimeUnits = taskTimeUnits;
		return taskTimeUnits;
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
		// Time Units have been changed so put the focus on the value so user can change the value if they want
		//This does not work - not critical so leave until user evaluations have been done
		this.getTxtLearningTimeField().requestFocusInWindow();
		return oTU;
	}

}