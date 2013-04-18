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
package com.compendium.learningdesign.core.db.management;

import java.io.Serializable;

public interface LdDBConstantsDerby extends Serializable {

	// STATEMENTS TO CREATE NEW TABLES TO HOLD THE LEARNING DESIGN TASK SEQUENCE DATA
	
	/** The SQL statement to create a new TaskSequenceActivity table */
	public static final String CREATE_TASKSEQUENCEACTIVITY_TABLE = "CREATE TABLE TaskSequenceActivity (" +	
	"	    ActivityId VARCHAR(50),	" 		+
	"	    TaskSequenceId VARCHAR(50), " 	+
	"	    PRIMARY KEY(TaskSequenceId), " 	+
	"		CONSTRAINT TaskSequenceActivity_fk_ActivityTimesDisplayed_ActivityId FOREIGN KEY (ActivityId) " +
    "		REFERENCES ActivityTimesDisplayed (ActivityId) "	+
	")";

	
	/** The SQL statement to create a new TaskTimes table */
	public static final String CREATE_TASKTIMES_TABLE = "CREATE TABLE TaskTimes (" +	
	"	    TaskId  VARCHAR(50), "			+
	"	    TaskTime BIGINT, "					+
	"		TaskUnit VARCHAR(32) default 'MINUTES', "		+
	"		ShowTime SMALLINT default 0 NOT NULL, " +		
	"	    PRIMARY KEY(TaskId) "	+
	")";
	
	
	/** The SQL statement to create a new TaskSequenceRole table */
	public static final String CREATE_TASKSEQUENCEROLE_TABLE = "CREATE TABLE TaskSequenceRole (" +	
	"	    TaskSequenceId VARCHAR(50), "			+
	"	    RoleId VARCHAR(50), "					+
	"		TotalTimeforRole BIGINT default 0, "	+
	"	    PRIMARY KEY(TaskSequenceId,RoleId), "	+ // Shouldn't the primary key be just the TaskSequenceId
	"		CONSTRAINT TaskSequenceRole_fk_TaskSequenceActivity_TaskSequenceId FOREIGN KEY (TaskSequenceId) " +
    "		REFERENCES TaskSequenceActivity (TaskSequenceId) " +
	")";

	/** The SQL statement to create a new TaskSequenceTask table */
	public static final String CREATE_TASKSEQUENCETASK_TABLE = "CREATE TABLE TaskSequenceTask (" +	
	"	    TaskSequenceId VARCHAR(50), "			+
	"	    TaskId  VARCHAR(50), "					+
	"	    TaskOrder  INTEGER default 0, "					+
	"	    PRIMARY KEY(TaskSequenceId,TaskId), "	+
	"		CONSTRAINT TaskSequenceTask_fk_TaskTimes_TaskId FOREIGN KEY (TaskId) "	+
    "		REFERENCES TaskTimes (TaskId), "	+
    "		CONSTRAINT TaskSequenceTask_fk_ActivityTaskSequence_TasKsequenceId FOREIGN KEY (TaskSequenceId) " +
    "		REFERENCES TaskSequenceActivity (TaskSequenceId) " 	+ 
	")";

	/** The SQL statement to update  the ActivityTimesDisplayed table */
	public static final String UPDATE_TASKSEQUENCETASK_TABLE = 	
	"		ALTER TABLE TaskSequenceTask  ADD COLUMN TaskOrder INTEGER DEFAULT 0";
	
	
	/** The SQL statement to create a new ActivityTimesDisplayed table */
	public static final String CREATE_ACTIVITYTIMESDISPLAYED_TABLE = "CREATE TABLE ActivityTimesDisplayed (" +	
	"	    TimesDisplayed SMALLINT  default 1, "			+
	"	    ActivityId   VARCHAR(50), "					+
	"	    TimeUnit VARCHAR(32) default 'MINUTES', "					+
	"		XPos INTEGER NOT NULL DEFAULT 0, "	+
	"		YPos INTEGER NOT NULL DEFAULT 0, "	+
	"	    PRIMARY KEY(ActivityId) "	+
	")";
	//Separate statements to add x and ypos because derby does not like ; separating individual statements  (might be fixed in derby 10.5, but not working in 10.4
	/** The SQL statement to update  the ActivityTimesDisplayed table by adding xpos column*/
	public static final String UPDATE_ACTIVITYTIMESDISPLAYED_TABLE_ADD_XPOS = 	
	"		ALTER TABLE ActivityTimesDisplayed  ADD COLUMN XPos INTEGER NOT NULL DEFAULT 0";
	/** The SQL statement to update  the ActivityTimesDisplayed table by adding Ypos column*/
	public static final String UPDATE_ACTIVITYTIMESDISPLAYED_TABLE_ADD_YPOS =
	"		ALTER TABLE ActivityTimesDisplayed  ADD COLUMN YPos INTEGER NOT NULL DEFAULT 0 ";
	
	/** The SQL statement to drop a TaskSequenceActivity table if it exists */
	public final static String DROP_TASKSEQUENCEACTIVITY_TABLE	= "DROP TABLE TaskSequenceActivity";

	/** The SQL statement to drop a TaskSequenceRole table if it exists */
	public final static String DROP_TASKSEQUENCEROLE_TABLE	= "DROP TABLE TaskSequenceRole";
	
	/** The SQL statement to drop a TaskTimes table if it exists */
	public final static String DROP_TASKTIMES_TABLE	= "DROP TABLE TaskTimes";
	
	/** The SQL statement to drop a TaskSequenceTask table if it exists */
	public final static String DROP_TASKSEQUENCETASK_TABLE	= "DROP TABLE TaskSequenceTask";
	
	/** The SQL statement to drop a ActivityTimesDisplayed table if it exists */
	public final static String DROP_ACTIVITYTIMESDISPLAYED_TABLE	= "DROP TABLE ActivityTimesDisplayed";

	/** The SQL statement to select all the data from the ActivityTimesDisplayed table */
	public final static String GET_ACTIVITYTIMESDISPLAYED_QUERY 			= "SELECT * FROM ActivityTimesDisplayed";
	
	/** The SQL statement to select all the data from the TaskSequenceActivity table */
	public final static String GET_TASKSEQUENCEACTIVITY_QUERY 			= "SELECT * FROM TaskSequenceActivity";

	/** The SQL statement to select all the data from the TaskSequenceRole table */
	public final static String GET_TASKSEQUENCEROLE_QUERY 			= "SELECT * FROM TaskSequenceRole";
	
	/** The SQL statement to select all the data from the TaskTimes table */
	public final static String GET_TASKTIMES_QUERY 			= "SELECT * FROM TaskTimes";
	
	/** The SQL statement to select all the data from the TaskSequenceTask table */
	public final static String GET_TASKSEQUENCETASK_QUERY 			= "SELECT * FROM TaskSequenceTask";
}
