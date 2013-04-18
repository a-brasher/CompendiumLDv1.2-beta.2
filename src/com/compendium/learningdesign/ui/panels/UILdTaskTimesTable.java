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

import java.util.*;
import com.compendium.learningdesign.util.TimeUnit;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.table.*;

import java.awt.Font;
import java.beans.PropertyChangeEvent;

import com.compendium.ui.ProjectCompendiumFrame;
import com.compendium.ProjectCompendium;

import com.compendium.learningdesign.core.ILdCoreConstants;
import com.compendium.learningdesign.ui.UILdTaskSequence;
import com.compendium.learningdesign.core.datamodel.LdTaskSequence;

public class UILdTaskTimesTable extends JTable {
	static String [] defaultColumnNames = {"Role 1", "Role 2"};
	
	/**	The beginning of a default column name. **/
	static String defaultColumnNamePrepend = "Role ";
	
	/** The column number of the column for student data in the tables default (2 column) state **/
	public static int defaultStudentColumnNo = 1;
	
	/** The column number of the column for tutor data in the tables default (2 column) state **/
	public static int defaultTutorColumnNo = 0;

	/**
	 * @uml.property  name="oTableModel"
	 * @uml.associationEnd  inverse="this$0:com.compendium.learningdesign.ui.panels.UILdTaskTimes$TaskTimesTableModel"
	 */
	private TaskTimesTableModel oTableModel; 
	
	private Font font;
	
	/**	columnsWithNoLabel is the number of columns without a label i.e without a role **/
	private int iColumnsWithNoLabel = 0;

	/**
	 * Defult constructor, constructs an instance with the 
	 * instance variable  oTableModel set to be an TaskTimesTableModel. 
	 */
	public UILdTaskTimesTable() {
		super();
		oTableModel = new TaskTimesTableModel();
		this.setModel(oTableModel);
		initGUI();
	}
	
	public UILdTaskTimesTable(TableModel dm) {
		super(dm);
		
	}

	public UILdTaskTimesTable(TableModel dm, TableColumnModel cm) {
		super(dm, cm);
		// TODO Auto-generated constructor stub
	}

	public UILdTaskTimesTable(int numRows, int numColumns) {
		super(numRows, numColumns);
		// TODO Auto-generated constructor stub
	}

	public UILdTaskTimesTable(Vector rowData, Vector columnNames) {
		super(rowData, columnNames);
		// TODO Auto-generated constructor stub
	}

	public UILdTaskTimesTable(Object[][] rowData, String[] columnNames) {
		super(rowData, columnNames);
		// TODO Auto-generated constructor stub
	}

	public UILdTaskTimesTable(TableModel dm, TableColumnModel cm,
			ListSelectionModel sm) {
		super(dm, cm, sm);
		// TODO Auto-generated constructor stub
	}
	
	/* Return the table model for this instance, i.e. an instance of TaskTimesTableModel.
	 * @see javax.swing.JTable#getModel()
	 */
	public TaskTimesTableModel getModel()	{
		return this.oTableModel;
	}
	

	/**
	public void setModel(TaskTimesTableModel oModel){
		Enumeration<TableColumn> oColumns = this.getColumnModel().getColumns();
		TableColumn oCol;
		Vector<String> oColNames = new Vector<String>();
		//Get the current column names
		while (oColumns.hasMoreElements())	{
			oCol = oColumns.nextElement();
			oColNames.add(oCol.getHeaderValue().toString());
		}
		//** Set the model - this will update the columns displayed to include any new colums
		 // but will also reset column names to their default values e.g. A, B, C etc.
		 
		super.setModel(oModel);
		// Rename the columns to their original values if they had values!
		if (oColNames.size() > 0)	{
			oColumns = this.getColumnModel().getColumns();
			int i = 0;
			String sName;
			// Reset the column names to their 'real' values 
			while (oColumns.hasMoreElements())	{
				sName = oColNames.get(i);
				oCol = oColumns.nextElement();
				i++;
				oCol.setHeaderValue(sName);;
			}
		}
	}

**/
/**
	public void tableChanged(TableModelEvent e) {
		TableModel model = (TableModel)e.getSource();
		int column = e.getColumn();

        String columnName = model.getColumnName(column);
        //this.get
	}

		**/
	
	private void initGUI() {
		try {
			//this.createDefaultTableHeader();		
			initialiseColumnNames();
			this.setPreferredSize(new java.awt.Dimension(165, 18));
			this.setVisible(true);
			this.setBorder(new LineBorder(new java.awt.Color(0,0,0), 1, false));
			font = ProjectCompendiumFrame.labelFont;
			int scale = ProjectCompendium.APP.getToolBarManager().getTextZoom();
			font = new Font(font.getName(), font.getStyle(), font.getSize()+ scale);
		//	this.getTableHeader().setDefaultRenderer(new TaskTimesTableHeaderRenderer);
			this.setFont(font);
			

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Initialise the table with the data contained in the set aTaskSequenceSet, 
	 * or the default data if aTaskSequenceSet is empty.
	 * @param aTaskSequenceSet
	 */
	public void setData(LinkedHashSet<LdTaskSequence> aTaskSequenceSet )	{
		int nColumns = this.getModel().getColumnCount();
		Iterator<LdTaskSequence> oIt = aTaskSequenceSet.iterator();
		LdTaskSequence oTaskSequence;
		while (oIt.hasNext())	{
			oTaskSequence = oIt.next();
		}
	}
	
	/**
	 * Initialise the table with the data contained in the set aTaskSequenceSet, 
	 * or the default data if aTaskSequenceSet is empty.
	 * @param aTaskSequenceSet
	 */
	public void initialiseWithData(LinkedHashSet<UILdTaskSequence> aTaskSequenceSet )	{
		int nColumns = this.getModel().getColumnCount();
		// ***** This needs to be rewriiten to that its if 0 size else loop through the set
		switch (aTaskSequenceSet.size() )	{
		// If aTaskSequenceSet is empty, then initialise with the default data.
		case (0): this.initialise(); break;
		// If a TaskSequenceSet contains one TaskSequence, show that and initialise other default column with default data  
		case (1):{
			UILdTaskSequence oTaskSequence = aTaskSequenceSet.iterator().next();
			// Id of the sequence to be included
			String sId = oTaskSequence.getLdTaskSequence().getId();
			int iColinUse = -1;
			if (this.getModel().getMapTaskSequenceToCol().containsKey(sId))	{
				// the task sequence is in the table
				iColinUse = this.getModel().getMapTaskSequenceToCol().get(sId);
				for (int i=0; i<nColumns; ++i)	{
					if (!(i == iColinUse))	{
						this.initialiseColumn(i);
					}
				}
			}
			else	{
				// The task sequence is not in the table
				
			}
			;

		}; break;
		default: ; break;
		}
		
		/**
		if (aTaskSequenceSet.isEmpty())
			// If aTaskSequenceSet is empty, then initialise with the default data.
			this.getTableModel().initialise();
		else	{
			Iterator<UILdTaskSequence> it = aTaskSequenceSet.iterator();
			int iColCount = this.getColumnModel().getColumnCount();
			TableColumn oColumn;
			for (int i=0; i<iColCount; ++i)	{
				oColumn = this.getColumnModel().getColumn(i);
				this.getColumnModel().removeColumn(oColumn);
			}
			while (it.hasNext())	{
				this.getModel().addTaskSequence(it.next());
			}
		}
		**/
	}
	
	/**
	 * Set the name for the column related to the sequence identified by
	 * the id sId to the String  aName, in both the column model and the bale model.
	 * @param aName
	 * @param sId
	 */
	public void setColumnNameForSequence(String aName,  String sId ) {
		// Get the column no in the model
		int iCol = this.getModel().getMapTaskSequenceToCol().get(sId).intValue();
		//this.getModel().setColumnName(iCol, aName);
		// Convert this to the column no in the view
		//  iColInView can be -1 if column is not yet displayed
		int iColInView = this.convertColumnIndexToView(iCol);
		if (iColInView >= 0)	{
			this.getColumnModel().getColumn(iColInView).setHeaderValue(aName);
		}			
		this.getTableHeader().repaint();
	} 
	
	
	/**
	 * This constructor is used by the getGUIBuilderInstance method to
	 * provide an instance of this class which has not had it's GUI elements
	 * initialized (ie, initGUI is not called in this constructor).
	 */
	public UILdTaskTimesTable(Boolean initGUI) {
		super();
	}

	public class TaskTimesTableModel extends AbstractTableModel	{
		private String [] columnNames = UILdTaskTimesTable.defaultColumnNames;
		
		/** The data in the table, stored as a Vector of Integers. It is a Vector
		 * to enable columns to be added and removed from the table 
		 */  
		private Vector<Integer> data;
		
		private Integer [][] dataOld = new Integer[1][2];
		/** 	A map between the column number and the task sequence id that column represents.	**/
		private HashMap<Integer, String> oMapColToTaskSequence;
		
		/** 	A map between the task sequence id the column number which stores  its data.	**/
		private HashMap<String, Integer> oMapTaskSequenceToCol;
		
		/** The number of columns in the table, initially set to 2 i.e. tutor, student **/
		private int iColumnCount;
		
		/** boolean to indicate that no data has been added to the table  **/
		private boolean bIsTableUninitialised;
		
		/** boolean to indicate that no 'real data has been added to student 
		 * column, i.e the column  contains only its initial default value of zero.**/
		private boolean bStudentColContainsNoData;
		
		/** boolean to indicate that no 'real data has been added to tutor 
		 * column, i.e the column  contains only its initial default value of zero.  **/
		private boolean bTutorColContainsNoData;
		
		/** The units that the task time is in (one of hours, minutes, days or months)	**/
		/** A TimeUnit represents time durations at a given unit of granularity and
		 *   provides utility methods to convert across units.
		 * @uml.property  name="oTaskTimeUnits"
		 */
		private TimeUnit oTaskTimeUnits = TimeUnit.HOURS;
		/**
		 * @return the oTaskTimeUnits
		 */
		public TimeUnit getTaskTimeUnits() {
			return oTaskTimeUnits;
		}

		/**
		 * @param taskTimeUnits the oTaskTimeUnits to set
		 */
		public void setTaskTimeUnits(TimeUnit taskTimeUnits) {
			oTaskTimeUnits = taskTimeUnits;
		}

		/**
		 * Constructor which initialises data values to 0, 0.
		 */
		TaskTimesTableModel()	{
			super();
			data = new Vector<Integer>();
			initialise();
		}
		
		/**
		 * Initialise this instance with default values.
		 */
		public void initialise()	{
			bStudentColContainsNoData = true;
			bTutorColContainsNoData = true;
			bIsTableUninitialised = true;
			data.add(new Integer(0));
			data.add(new Integer(0));
			this.iColumnCount = 2;
			oMapColToTaskSequence = new HashMap<Integer, String>();
			oMapTaskSequenceToCol = new HashMap<String, Integer>();
		}
		public int getColumnCount() {
	        return this.data.size();
	    }
	 

		public Number getValueAt(int row, int column){	
			/* The table only has one row so return zero if caller asks for another row.
			 * Should really through an exception **/
			if (row != 0)	
				return Float.valueOf(0);
			
			float valInUnits = 0;
			float hoursInDay = 24;
			float minutesInhour = 60;
			switch(this.getTaskTimeUnits())	{
			case HOURS: valInUnits = data.get(column)/minutesInhour; break;
			case DAYS:	valInUnits = data.get(column)/(hoursInDay*minutesInhour); break;
			default:	valInUnits = data.get(column); break;
		}
			// Calculate if valInUnits has a fractional part. If it does, fDx will be > 0
			float fDx = valInUnits - (int)valInUnits;
			if (fDx == 0)	{
				//Return an Integer if no frctional part
				return Integer.valueOf((int)valInUnits);
			}
			//Other wise return a Float
			return Float.valueOf(valInUnits) ;
		}
				
		
		/**
		 * @param aValue
		 * @param row
		 * @param column
		 */
		public void setValueAt(Integer aValue, int row, int iColumn) {
			//this.data[row][column] = aValue;
			if (iColumn >= this.data.size())
				this.data.add(iColumn, aValue);
			else	{
				this.data.set(iColumn, aValue);
				if (iColumn == UILdTaskTimesTable.defaultStudentColumnNo )
					this.setStudentColContainsNoData(false);
				else if (iColumn == UILdTaskTimesTable.defaultTutorColumnNo )
					this.setTutorColContainsNoData(false);
			}
		}
		
		/**
		 * Set the data in the column given by the parameter iColumn.
		 *  If the column iColumn does not currently exist, it is added to the model.
		 * @param aValue, Integer, the value to set
		 * @param iColumn, int, the column whose value is to be set
		 */
		public void setValueAt(Integer aValue,  int iColumn) {
			if (iColumn >= this.getColumnCount())
				this.data.add(iColumn, aValue);
			else	{
				this.data.set(iColumn, aValue);
			if (iColumn == UILdTaskTimesTable.defaultStudentColumnNo )
				this.setStudentColContainsNoData(false);
			else if (iColumn == UILdTaskTimesTable.defaultTutorColumnNo )
				this.setTutorColContainsNoData(false);
			}
		}
		
		/**
		 * Add the data in the task sequence to the column iColumn in the model.
		 * @param oNewTaskSequence
		 * @param iColumn
		 */
		public void addTaskSequence(UILdTaskSequence oNewTaskSequence, int iColumn)	{
			// Set the task time
			this.setValueAt((new Long(oNewTaskSequence.getTaskSequenceTime())).intValue(), iColumn);
			// Put the sequence id and the column index in both maps
			this.getMapColToTaskSequence().put(new Integer(iColumn), oNewTaskSequence.getLdTaskSequence().getId());
			this.getMapTaskSequenceToCol().put(oNewTaskSequence.getLdTaskSequence().getId(), new Integer(iColumn));
		}
		
		public void addTaskSequence(UILdTaskSequence oNewTaskSequence)	{
			/* nCols start from zero, so nCols will be the number of  the next column
			 * unless the table is one or more of holding just its initial values, i.e
			 * zero for the 'tutor' column and zero for the 'student' column.
			 */
			int nCols = this.getColumnCount();
			if (this.isTutorColContainsNoData())	{
				this.addTaskSequence(oNewTaskSequence, UILdTaskTimesTable.defaultTutorColumnNo);
			}
			else if (this.isStudentColContainsNoData())	{
				this.addTaskSequence(oNewTaskSequence, UILdTaskTimesTable.defaultStudentColumnNo);
			}
			else	{
				this.addTaskSequence(oNewTaskSequence, nCols);
				// A column has been added - let listeners know
				this.fireTableCellUpdated(0, nCols);
				//this.fireTableStructureChanged();
			}			
		}
		/**
		 * Set the value for the column related to the sequence identified by
		 * the id sId to the value aValue.
		 * @param aValue, Long, the value to be set
		 * @param sId, String, the identifier of the TaskSequence whose value is to be set
		 */
		public void setValueForSequence(Long aValue,  String sId ) {
			int iColumn = this.getMapTaskSequenceToCol().get(sId).intValue();			
			this.setValueAt(aValue.intValue(), iColumn);
			if (iColumn == UILdTaskTimesTable.defaultStudentColumnNo )
				this.setStudentColContainsNoData(false);
			else if (iColumn == UILdTaskTimesTable.defaultTutorColumnNo )
				this.setTutorColContainsNoData(false);
		}
		
		/**
		 * Set the name for the column related to the sequence identified by
		 * the id sId to the String  aName.
		 * @param aName
		 * @param sId
		 */
		/**
		public void setColumnNameForSequence(String aName,  String sId ) {
			int iCol = this.getMapTaskSequenceToCol().get(sId).intValue();			
			this.setColumnName(iCol, aName);
		} 
		**/
		
		/**
		 * @param columnNames the columnNames to set
		 */
		public void setColumnNames(String[] columnNames) {
			this.columnNames = columnNames;
		}
		
		
		/**
		 * Set the name of the col column to be sColName. 
		 * @param col, int value which specifies the column 
		 * @param sColName, String value - the name to be set
		 */
		public void setColumnName(int col, String sColName) {
            columnNames[col] = sColName;
//            this.getColumnModel().getColumn(colNo).setHeaderValue(UILdTaskTimesTable.defaultColumnNamePrepend + colNo);
        }
		/**
		 * Returns 'true' if the student column contains its initial value of zero, 
		 * false otherwise.
		 * @return the bIsStudentColUnitialised
		 */
		public boolean isStudentColContainsNoData() {
			return bStudentColContainsNoData;
		}
		/**
		 * Set the boolean value which indicates that the data in the default student column
		 * is (or has been) different to its default value of zero.
		 * @param isStudentColUnitialised the bIsStudentColUnitialised to set
		 */
		public void setStudentColContainsNoData(boolean isStudentColUnitialised) {
			bStudentColContainsNoData = isStudentColUnitialised;
		}
		
		/**
		 * Initialise the data value for the column colNo to zero, and if it's 
		 * the default student column or the default tutor column, set the booleans 
		 * to indicate that these columns are uninitialised. 
		 * @param colNo
		 */
		public void initialiseColumn(int colNo){
			this.setValueAt(new Integer(0), colNo);
			if (colNo == UILdTaskTimesTable.defaultStudentColumnNo )
				this.setStudentColContainsNoData(true);
			else if (colNo == UILdTaskTimesTable.defaultTutorColumnNo )
				this.setTutorColContainsNoData(true);
		}
		/**
		 * Returns 'true' if the tutor column contains its initial value of zero, 
		 * false otherwise.
		 * @return the bIsStudentColUnitialised
		 */
		public boolean isTutorColContainsNoData() {
			return bTutorColContainsNoData;
		}
		
		/**
		 * Set the boolean value which indicates that the data in the default tutor column
		 * is (or has been) different to its default value of zero.
		 * @param isStudentColUnitialised the bIsStudentColUnitialised to set
		 */
		public void setTutorColContainsNoData(boolean isTutorColUnitialised) {
			bTutorColContainsNoData = isTutorColUnitialised;
		}

		/**
		 * @return the oMapColToTaskSequence
		 */
		public HashMap<Integer, String> getMapColToTaskSequence() {
			return oMapColToTaskSequence;
		}

		/**
		 * @return the oMapTaskSequenceToCol
		 */
		public HashMap<String, Integer> getMapTaskSequenceToCol() {
			return oMapTaskSequenceToCol;
		}

		/* Returns the number of rows in this model. Currently set to 1 
		 * because there is only one row in the table.
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount() {
			return 1;
		}		
	}

	/**
	 * Add the task sequence to this table.
	 * @param oNewTaskSequence, UILdTaskSequence the sequence to be added.
	 */
	public void addTaskSequence(UILdTaskSequence oNewTaskSequence)	{
		// Get the type of the task sequence: one of student, tutor,  other or no type (if the role has not been set)
		int iTsType = oNewTaskSequence.getRoleType();
		// Get the task sequence id
		String sTaskSeqId = oNewTaskSequence.getLdTaskSequence().getId();
		//Get the number of columns in the model before this sdequence is added
		int nColsOld = this.getModel().getColumnCount();
		// Add the task sequence to this tables datamodel 
		this.getModel().addTaskSequence(oNewTaskSequence);
		// Get the number of columns in the model after this sequence has been added
		int nColsNew = this.getModel().getColumnCount();
		// if a new column has been added, add that column to the view
		if (nColsNew > nColsOld )	{			
			this.getColumnModel().addColumn(new TableColumn(nColsOld));
		}
		/** Repaint the table so that if necessary, columns are added. Need to  
		do this because setColumnNameForSequence() method requires table to know 
		which columns data is in so right header can be updated. **/  
		this.getTableHeader().repaint();
		// If it's a student or tutor or other set the column name
		if (iTsType == ILdCoreConstants.iLD_TYPE_ROLE_STUDENT || iTsType == ILdCoreConstants.iLD_TYPE_ROLE_TUTOR
				|| iTsType == ILdCoreConstants.iLD_TYPE_ROLE_OTHER  )	{						
			// Set the column name
			this.setColumnNameForSequence(oNewTaskSequence.getRoleName(), sTaskSeqId);
			//this.setColumnNameForSequence(sLabel, sTaskSeqId);
		}
		// If it is an 'no type' sequence, ie. a task-to-task only sequence generate a column name, then add it
		if  (iTsType == ILdCoreConstants.iLD_TYPE_NO_TYPE )	{
			String sLabel = "Role " + (this.getNumColumnsWithNoLabel() + 1);
			this.setNumColumnsWithNoLabel(this.getNumColumnsWithNoLabel() + 1);
			this.setColumnNameForSequence(sLabel, sTaskSeqId);
		}		
		
		
				
	}
	
	/**
	 * Initialise the data value for the default table: data, booleans and 
	 * headers for both the default student column and the default tutor column. 
	 * 
	 */
	public void initialise() {
		//Initialise the data
		this.getModel().initialiseColumn(UILdTaskTimesTable.defaultStudentColumnNo);
		this.getModel().initialiseColumn(UILdTaskTimesTable.defaultTutorColumnNo);
		// Initialise the table header
		this.getColumnModel().getColumn(UILdTaskTimesTable.defaultStudentColumnNo).setHeaderValue(UILdTaskTimesTable.defaultColumnNamePrepend + 
				UILdTaskTimesTable.defaultStudentColumnNo);
		this.getColumnModel().getColumn(UILdTaskTimesTable.defaultTutorColumnNo).setHeaderValue(UILdTaskTimesTable.defaultColumnNamePrepend + 
				UILdTaskTimesTable.defaultTutorColumnNo);
	}
	/**
	 * Initialise the data value for the column colNo to zero, and if it's 
	 * the default student column or the default tutor column, set the booleans 
	 * to indicate that these columns are uninitialised. 
	 * @param colNo
	 */
	public void initialiseColumn(int colNo){
		//Initialise the data
		this.getModel().initialiseColumn(colNo);
		// Initialise the table header
		this.getColumnModel().getColumn(colNo).setHeaderValue(UILdTaskTimesTable.defaultColumnNamePrepend + colNo);
	}
	
	
	/**
	 * Return the column in the view for the task sequence with the id sTaskSeqId.
	 * @param sTaskSeqId
	 * @return
	 */
	public int getViewColumn(String sTaskSeqId)	{
		Integer iModelIndex = this.getModel().getMapTaskSequenceToCol().get(sTaskSeqId);
		return (this.convertColumnIndexToView(iModelIndex));
	}

	/**
	 * Get the number of columns with no label. This value should be incremented
	 * every time a column with no header is added i.e. a column without a role node.
	 * @return the columnsWithNoLabel
	 */
	public int getNumColumnsWithNoLabel() {
		return iColumnsWithNoLabel;
	}

	/**
	 * Set the number of columns with no label. This value should be incremented
	 * every time a column with no header is added i.e. a column without a role node.
	 * @param columnsWithNoLabel the columnsWithNoLabel to set
	 */
	public void setNumColumnsWithNoLabel(int columnsWithNoLabel) {
		this.iColumnsWithNoLabel = columnsWithNoLabel;
	}
	
	/**
	 * Set the names for the two default columns to their default values (i.e. 
	 * "Role 1", "Role 2".
	 */
	public void initialiseColumnNames()	{
		this.getColumnModel().getColumn(UILdTaskTimesTable.defaultStudentColumnNo).setHeaderValue(UILdTaskTimesTable.defaultColumnNames[UILdTaskTimesTable.defaultStudentColumnNo]);
		this.getColumnModel().getColumn(UILdTaskTimesTable.defaultTutorColumnNo).setHeaderValue(UILdTaskTimesTable.defaultColumnNames[UILdTaskTimesTable.defaultTutorColumnNo]);
	}
	
	

}