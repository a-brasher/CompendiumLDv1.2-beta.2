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

package com.compendium.learningdesign.core;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.HashSet;

/**
 * This interface defines some global constants used in the Learning Design
 * version of the Compendium application.
 * @author Andrew Brasher
 *
 */
public interface ILdCoreConstants extends Serializable {
	// Learning Design node types
/** These node types are intended to be instance variables stored in a NodeSummary 
 * instance and the corresponding node database table. However, to avoid the extensive 
 * code changes that would require, the following approach has been adopted.
 * The tags listed below (Learning Design tags) will be added to each learning design 
 * node type via the stencil set. Accessor methods e.g. getLDType() will be written to
 * simulate the availability of an ldType instance variable. This acccessor method will 
 * use the tag attached  to the node to generate the ldType value 
 */ 
	/** This represents an node with no LD type.	*/
	public final static int			iLD_TYPE_NO_TYPE	=	 0;
	
	/** This represents an Activity.	*/
	public final static int			iLD_TYPE_ACTIVITY	=	 101;

	/** This represents any type of assessment, subtypes of assessment are e.g. formative and summative.	*/
	public final static int			iLD_TYPE_ASSESSMENT	=	 102;
	
	/** This represents any type of learning outcome.	*/
	public final static int			iLD_TYPE_LEARNING_OUTCOME	=	 103;
	
	/** This represents a Resource.	*/
	public final static int			iLD_TYPE_RESOURCE	=	 104;
	
	/** This represents a Role.	*/
	public final static int			iLD_TYPE_ROLE		=	 105;	
	
	/** This represents a Task.	*/
	public final static int			iLD_TYPE_TASK		=	 106;	
	
	/** This represents a VLE tool.	*/
	public final static int			iLD_TYPE_VLE_TOOL	=	 107;
	
	/** This represents the stop i.e. an activity has finished.	*/
	public final static int			iLD_TYPE_STOP		=	 108;
	
	/** This represents an Assignment, TMA or other type of summative assessment.	*/
	public final static int			iLD_TYPE_ASSESSMENT_SUMMATIVE	=	 201;

	/** This represents student output that is formatively assessed.	*/
	public final static int			iLD_TYPE_ASSESSMENT_FORMATIVE		=	 202;
	
	/** This represents a Blog  VLE tool.	*/
	public final static int		iLD_TYPE_VLE_TOOL_BLOG	= 	701; 

	/** This represents an e-portfolio  VLE tool.	*/
	public final static int		iLD_TYPE_VLE_TOOL_EP	= 	702; 

	/** This represents a forum  VLE tool.	*/
	public final static int		iLD_TYPE_VLE_TOOL_FORUM	= 	703;
	
	/** This represents an instant messaging  VLE tool.	*/
	public final static int		iLD_TYPE_VLE_TOOL_IM	= 	704;
	
	/** This represents a podcast  VLE tool.	*/
	public final static int		iLD_TYPE_VLE_TOOL_PODCAST	= 	705;
	
	/** This represents a simulation  VLE tool.	*/
	public final static int		iLD_TYPE_VLE_TOOL_SIM	= 	706;
	
	/** This represents a virtual world  VLE tool.	*/
	public final static int		iLD_TYPE_VLE_TOOL_VW	= 	707;
	
	/** This represents a wiki  VLE tool.	*/
	public final static int		iLD_TYPE_VLE_TOOL_WIKI	= 	708;

	/** This represents any other tool, which may or may not be a VLE tool.	*/
	public final static int		iLD_TYPE_VLE_TOOL_OTHER	= 	709;
	
	/** This represents a student role.	*/
	public final static int		iLD_TYPE_ROLE_STUDENT = 	501;
	
	/** This represents a tutor role.	*/
	public final static int		iLD_TYPE_ROLE_TUTOR = 	502;
	
	/** This represents a student group role.	(Note no longer used but left 
	 * in just in case	*/
	public final static int		iLD_TYPE_ROLE_GROUP = 	503;
	
	/** This represents the role 'other'	*/
	public final static int		iLD_TYPE_ROLE_OTHER = 	504;
	
	/**	Enumeration of role types, including iLD_TYPE_NO_TYPE for "no role".
	 *   Note that iLD_TYPE_ROLE_GROUP is not currently include because it is
	 *    no longer used. 		**/
	public final static int[] iROLETYPES = {ILdCoreConstants.iLD_TYPE_ROLE_STUDENT, ILdCoreConstants.iLD_TYPE_ROLE_TUTOR, ILdCoreConstants.iLD_TYPE_ROLE_OTHER};
// Note - could be better to use the enum data type, but this needs thinking about e.g. 	
//	public  final static  enum eLD_ROLE_TYPES	{iLD_TYPE_ROLE_STUDENT, iLD_TYPE_ROLE_TUTOR,  iLD_TYPE_ROLE_OTHER};
	/** This represents the CONDITIONAL TYPE  'condition'	*/
	public final static int		iLD_TYPE_CONDITIONAL_CONDITION = 	801;
	
	/** This represents the CONDITIONAL TYPE  'false'	*/
	public final static int		iLD_TYPE_CONDITIONAL_FALSE = 	802;
	
	/** This represents the CONDITIONAL TYPE  'true'	*/
	public final static int		iLD_TYPE_CONDITIONAL_TRUE = 	803;
	
	// Learning Design tags
	/** The tag given to a Activity icon in the Learning design stencil set */
	public final static String	sACTIVITY_TAG 			= "^_ld^activity_^";
	
	/** The tag given to a Assignment icon in the Learning design stencil set */
	public final static String	sASSESSMENT_TAG 			= "^_ld^assessment_^";
	
	/** The tag given to a Assignment icon in the Learning design stencil set */
	public final static String	sASSESSMENT_SUMMATIVE_TAG 			= "^_ld^assignment_^";
	
	/** The tag given to a Output icon in the Learning design stencil set */
	public final static String	sASSESSMENT_FORMATIVE_TAG 			= "^_ld^output_^";
	
	/** The tag given to a Resource icon in the Learning design stencil set */
	public final static String	sLEARNING_OUTCOME_TAG 			= "^_ld^learning_outcome_^";
	
	/** The tag given to a Resource icon in the Learning design stencil set */
	public final static String	sRESOURCE_TAG 			= "^_ld^resource_^";
	
	/** The tag given to a Role icon in the Learning design stencil set */
	public final static String	sROLE_TAG 			= "^_ld^role_^";
	
	/** The tag given to a Task icon in the Learning design stencil set */
	public final static String	sTASK_TAG 			= "^_ld^task_^";

	/** The tag given to a VLE tool icon in the Learning design stencil set */
	public final static String	sVLE_TOOL_TAG 			= "^_ld^vle^tool_^";
	
	/** The tag given to a STOP  in the Learning design stencil set */
	public final static String	sSTOP_TAG 			= "^_ld^stop_^";
	
	/** The tag given to a VLE blog tool  in the Learning design stencil set */
	public final static String	sVLE_TOOL_BLOG_TAG 			= "^_ld^vle^tool_blog^";
	
	
	/** The tag given to a VLE chat tool  in the Learning design stencil set **** NOT NEEDED **** */
//	public final static String	sVLE_TOOL_CHAT_TAG 			= "^_ld^vle^tool_chat^";
	
	/** The tag given to a VLE e-portfolio tool  in the Learning design stencil set */
	public final static String	sVLE_TOOL_EP_TAG 			= "^_ld^vle^tool_ep^";

	/** The tag given to a VLE forum tool  in the Learning design stencil set */
	public final static String	sVLE_TOOL_FORUM_TAG 			= "^_ld^vle^tool_forum^";
	
	/** The tag given to a VLE instant messaging tool  in the Learning design stencil set */
	public final static String	sVLE_TOOL_IM_TAG 			= "^_ld^vle^tool_im^";

	/** The tag given to a VLE podcast tool  in the Learning design stencil set */
	public final static String	sVLE_TOOL_PODCAST_TAG 			= "^_ld^vle^tool_podcast^";

	/** The tag given to a VLE simulation tool  in the Learning design stencil set */
	public final static String	sVLE_TOOL_SIM_TAG 			= "^_ld^vle^tool_sim^";

	/** The tag given to a VLE virtiual world tool  in the Learning design stencil set */
	public final static String	sVLE_TOOL_VW_TAG 			= "^_ld^vle^tool_vw^";
	
	/** The tag given to a VLE wiki tool  in the Learning design stencil set */
	public final static String	sVLE_TOOL_WIKI_TAG 			= "^_ld^vle^tool_wiki^";
	
	/** The tag given to a VLE other  tool  in the Learning design stencil set */
	public final static String	sVLE_TOOL_OTHER_TAG 			= "^_ld^vle^tool_other^";
	
	/** The tag given to a condition node  in the Learning design CONDITIONAL stencil set */
	public final static String	sCONDITIONAL_CONDITION_TAG 			= "^_ld^condition_^";
	
	/** The tag given to a false  in the Learning design CONDITIONAL stencil set */
	public final static String	sCONDITIONAL_FALSE_TAG 			= "^_ld^false_^";
	
	/** The tag given to a condition  in the Learning design CONDITIONAL stencil set */
	public final static String	sCONDITIONAL_TRUE_TAG 			= "^_ld^true_^";
	
	// Names 
	/** The name given to a VLE blog tool  in the Learning design stencil set */
	public final static String	sVLE_TOOL_BLOG_NAME 			= "Blog";
	
	/** The name given to a VLE chat tool  in the Learning design stencil set **** NOT NEEDED **** */
//	public final static String	sVLE_TOOL_CHAT_NAME 			= "^_ld^vle^tool_chat^";
	
	/** The name given to a VLE e-portfolio tool  in the Learning design stencil set */
	public final static String	sVLE_TOOL_EP_NAME 			= "E-portfolio";

	/** The name given to a VLE forum tool  in the Learning design stencil set */
	public final static String	sVLE_TOOL_FORUM_NAME 			= "Forum";
	
	/** The name given to a VLE instant messaging tool  in the Learning design stencil set */
	public final static String	sVLE_TOOL_IM_NAME 			= "Instant messaging";

	/** The name given to a VLE podcast tool  in the Learning design stencil set */
	public final static String	sVLE_TOOL_PODCAST_NAME 			= "Podcast";

	/** The name given to a VLE simulation tool  in the Learning design stencil set */
	public final static String	sVLE_TOOL_SIM_NAME 			= "Simulation";

	/** The name given to a VLE virtiual world tool  in the Learning design stencil set */
	public final static String	sVLE_TOOL_VW_NAME 			= "Virtual World";
	
	/** The name given to a VLE wiki tool  in the Learning design stencil set */
	public final static String	sVLE_TOOL_WIKI_NAME 			= "Wiki";

	/** The name given to a VLE wiki tool  in the Learning design stencil set */
	public final static String	sVLE_TOOL_OTHER_NAME 			= "Other tool";

	/** The tag given to a student role icon in the Learning design stencil set */
	public final static String	sROLE_STUDENT_TAG 			= "^_ld^role_^student^";
	
	/** The tag given to a tutor role icon in the Learning design stencil set */
	public final static String	sROLE_TUTOR_TAG 			= "^_ld^role_^tutor^";
	
	/** The tag given to a student group role icon in the Learning design stencil set */
	public final static String	sROLE_OTHER_TAG 			= "^_ld^role_^other^";
	
	/** The tag given to a student group role icon in the Learning design stencil set */
	public final static String	sROLE_GROUP_TAG 			= "^_ld^role_^group^";

	// Labels
	/** The label given to a Activity icon in the Learning design stencil set */
	public final static String	sACTIVITY_LABEL 			= "Activity";
	
	/** The label given to a Assignment icon in the Learning design stencil set */
	public final static String	sASSIGNMENT_LABEL 			= "Assignment";
	
		
	/** The label given to a Learner output icon in the Learning design stencil set */
	public final static String	sOUTPUT_LABEL 			= "Learner output";
	
	/** The label given to a Resource icon in the Learning design stencil set */
	public final static String	sRESOURCE_LABEL 			= "Resource";
	
	/** The label given to a Role icon in the Learning design stencil set */
	public final static String	sROLE_LABEL 			= "Role";
	
	/** The label given to a Task icon in the Learning design stencil set */
	public final static String	sTASK_LABEL 			= "Task";

	/** The label given to a VLE tool icon in the Learning design stencil set */
	public final static String	sVLE_TOOL_LABEL 			= "Tool";
	
	/** The label given to a STOP icon in the Learning design stencil set */
	public final static String	sSTOP_LABEL 			= "Stop";
	
	/** The name (or label) given to the Learning design stencil set */
	public final static String	sLD_STENCIL_NAME 			= "LD-OU";
	
	/** The name (or label) given to the Learning design conditional stencil set */
	public final static String	sLD_CONDITIONAL_STENCIL_NAME 			= "LD-CONDITIONAL";
	
	/** The code group identifier given to the Learning design stencil set */
	public final static String	sLD_CODE_GROUP_ID 			= "137108541361203436027328";
			
	/** The ncode group identifier  given to the Learning design stencil set */
	public final static String	sLD_SUB_TYPE_CODE_GROUP_ID 			= "137108541361194622195292";
	
	/** The code group identifier  given to the Learning design stencil set */
	public final static String	sLD_CONDITIONS_CODE_GROUP_ID 			= "137108541361203436092976";
			
}


