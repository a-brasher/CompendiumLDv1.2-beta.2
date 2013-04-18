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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.HashSet; 
import java.util.Vector;
import com.compendium.ProjectCompendium;
import com.compendium.ui.stencils.*;
import com.compendium.core.datamodel.IModel;
import com.compendium.core.datamodel.PCSession;
import com.compendium.core.datamodel.Code;
import com.compendium.core.datamodel.services.CodeService;;


/**
 * This class provides data structures which map between the strings used to tag learning
 * design nodes as being different learning design node types and
 *  1 the codes in which those tags string names are stored
 *  2 the integer identifiers used to identify ld node types.
 * 
 * Tables for both forward and reverse lookups are provided.
 * @author ajb785
 *
 * Once the LD icon set is finalised, all the mapping tables can be made static.
 */
/**
 * @author ajb785
 *
 */
public class LdTypeTagMaps {
	/**
	 * Hashtable of ALL the LD type tag strings (keys)  to LD type integers	(values) for learning design nodes, and role and tool subtypes *
	 * @uml.property  name="htTagToTypesTable"
	 * @uml.associationEnd  qualifier="sTag:java.lang.String java.lang.Integer"
	 */
	private   Hashtable<String, Integer> htTagToTypesTable = new Hashtable<String, Integer>(7);
	
	/**
	 * Hashtable of the upper LD type tag strings (keys)  to LD type integers	(values) for main learning design node types only: not role and tool subtypes *
	 * This includes the icons in the LD-OU stencil and those in the LD-CONDITIONAL stencil *
	 * @uml.property  name="htUpperTagToTypesTable"
	 * @uml.associationEnd  qualifier="tag:java.lang.String java.lang.Integer"
	 */
	private   Hashtable<String, Integer> htUpperTagToTypesTable = new Hashtable<String, Integer>(7);
	
	/**
	 * Hashtable of the LD type tag strings (keys)  to LD sub type integers	(values) for the subtypes (i.e roles and tools)  *
	 * @uml.property  name="htTagToSubTypesTable"
	 * @uml.associationEnd  qualifier="sTag:java.lang.String java.lang.Integer"
	 */
	private   Hashtable<String, Integer> htTagToSubTypesTable = new Hashtable<String, Integer>(11);
	
	/**
	 * Hashtable of the upper LD type Integers (keys) to LD type tag strings (values) for main learning design node types only: not role and tool subtypes 
	 * This includes the icons in the LD-OU stebncil and those in the LD-CONDITIONAL stencil *
	 * @uml.property  name="htUpperTypesToTagsTable"
	 * @uml.associationEnd  qualifier="valueOf:java.lang.Integer java.lang.String"
	 */
	private   Hashtable<Integer, String> htUpperTypesToTagsTable = new Hashtable<Integer, String>(7);
	
	/**
	 * Hashtable of all the LD type Integers (keys) to LD type tag strings (values)*
	 * @uml.property  name="htTypesToTagsTable"
	 * @uml.associationEnd  qualifier="valueOf:java.lang.Integer java.lang.String"
	 */
	private   Hashtable<Integer, String> htTypesToTagsTable = new Hashtable<Integer, String>(7);
	
	/**
	 * Hashtable of the code ids of the LD type tags (keys) to LD tag strings (values) *
	 * @uml.property  name="htCodesToTagsTable"
	 * @uml.associationEnd  qualifier="codeId:java.lang.String java.lang.String"
	 */
	private Hashtable<String, String> htCodesToTagsTable = new Hashtable<String, String>(7);
	
	/**
	 * Hashtable of the  LD type tag strings (keys) to code ids of the LD type tags  (values) *
	 * @uml.property  name="htTagsToCodesTable"
	 * @uml.associationEnd  qualifier="sTag:java.lang.String java.lang.String"
	 */
	private Hashtable<String, String> htTagsToCodesTable = new Hashtable<String, String>(7);
	
	/**
	 * Hashtable of the  LD type Integers (keys) to code ids of the LD type tags  (values) *
	 * @uml.property  name="htTypesToCodesTable"
	 * @uml.associationEnd  qualifier="valueOf:com.compendium.learningdesign.core.LdTypeTagMaps java.lang.String"
	 */
	private Hashtable<Integer, String> htTypesToCodesTable = new Hashtable<Integer, String>(7);

	/**	Hashset of Learnng Design Code group IDs		**/
	private HashSet<String> hsLdCodeGroups = new HashSet<String>();
	
	/**
	 * Hashtable of the LD tool type tag strings (keys)  to LD tool type Integers	(values) *
	 * @uml.property  name="htToolTagToToolTypesTable"
	 */
	Hashtable<String, Integer> htToolTagToToolTypesTable = new Hashtable<String, Integer>(8);
	
	/**
	 * Hashtable of the LD tool type integers  (keys)  to LD tool type name strings	(values) *
	 * @uml.property  name="htToolTypeToToolNamesTable"
	 * @uml.associationEnd  qualifier="valueOf:java.lang.Integer java.lang.String"
	 */
	Hashtable<Integer, String> htToolTypeToToolNamesTable = new Hashtable<Integer, String>(8);
	
	/**
	 * Hashtable of the LD tool type name strings (keys)  to LD tool type Integers	(values) *
	 * @uml.property  name="htToolToolNamesToTypeTable"
	 */
	Hashtable<String, Integer> htToolToolNamesToTypeTable = new Hashtable<String, Integer>(8);

	
	private final  Hashtable<Integer, String> htTypesToLabelsTable = new Hashtable<Integer, String>(7);
	  

	/**
	 * 
	 * 
	 */
	public LdTypeTagMaps() {
		super();
		initialise();
	}
	
	/**
	 * Create a set of types to tags maps including data for the StencilSet oSS 
	 * and the Model oModel.
	 * @param oSS
	 * @param oModel
	 */
	public LdTypeTagMaps(UIStencilSet oSS, IModel oModel) {
		super();
		/** Initialise the htTagtoTypesTable and htTypestoTagsTable with data for the 
		 *  7 learning design node types
		 */
		initialise();
		/** Initialise the htTagtoTypesTable and htTypestoTagsTable with data for the 
		 *  7 learning design tool node subtypes and 3 ld role node subtypes
		 */
		initialiseTypes();
		/** Initialise the htTagtoTypesTable and htTypestoTagsTable with data for the 
		 *  7 learning design tool node subtypes and 4 ld role node subtypes
		 */
		initialiseTagToSubTypes();
		
		initialiseToolTypesToNames();
		PCSession oSession = oModel.getSession();
		
		
		for (Enumeration<String> e = htTypesToTagsTable.elements(); e.hasMoreElements(); )	{
			String sTag = e.nextElement();
			
			try	{
				Code oCode = oModel.getCodeService().getCodeForName(oSession, sTag);
				if (oCode == null)	{
					System.out.println("Problems accessing Code for tag: "+ sTag);
				}
				else	{
					String codeId = oCode.getId();
					htCodesToTagsTable.put(codeId, sTag);
					htTagsToCodesTable.put(sTag, codeId);
					htTypesToCodesTable.put(htTagToTypesTable.get(sTag), codeId);
				}
			}
			catch (java.sql.SQLException e1)	{
				System.out.println("Problems accessing system settings: "+e1.getMessage());
			}
		}
		initialiseCodeGroups();
		
		initialiseLabels();
	}
	
	

	public LdTypeTagMaps(UIStencilSet oSS) {
		super();
		initialise();
		//Now get codes from LdStencilSet
		
		for (Enumeration<String> e = htTypesToTagsTable.elements(); e.hasMoreElements(); )	{
			String sTag = e.nextElement();
			Vector<DraggableStencilIcon> vtTemp = oSS.getItemsWithTag(sTag);
			DraggableStencilIcon iTemp = vtTemp.firstElement();
			// Get a vector of all the Tags (Codes) associated with this DraggableStencilIcon
			Vector vtTagsTemp = iTemp.getTags();
			// Get the Code vector 
			Vector vtCode = (Vector) vtTagsTemp.firstElement();
			// The element at position 0 is the codeId
			String codeId = (String) vtCode.firstElement();

			htCodesToTagsTable.put(codeId, sTag);
			htTagsToCodesTable.put(sTag, codeId);
			htTypesToCodesTable.put(htTagToTypesTable.get(sTag), codeId);			
		}
	}

	/**
	 * Initialise the tags to types table htTagtoTypesTable 
	 * and the  types to tags table htTypestoTagsTable
	 * for the 7 core LD nodes (i.e. activity, assignment, output,
	 * resource, role, task, tool).
	 */
	public  void initialise() {
		/** Initialise the tAgs to types and the upper tags to types table 	**/
		htTagToTypesTable.put(ILdCoreConstants.sACTIVITY_TAG,
				ILdCoreConstants.iLD_TYPE_ACTIVITY);
		htTagToTypesTable.put(ILdCoreConstants.sASSESSMENT_TAG,
				ILdCoreConstants.iLD_TYPE_ASSESSMENT);
		htTagToTypesTable.put(ILdCoreConstants.sLEARNING_OUTCOME_TAG,
				ILdCoreConstants.iLD_TYPE_LEARNING_OUTCOME);
/**
		htTagToTypesTable.put(ILdCoreConstants.sASSESSMENT_SUMMATIVE_TAG,
				ILdCoreConstants.iLD_TYPE_ASSESSMENT_SUMMATIVE);
		htTagToTypesTable.put(ILdCoreConstants.sASSESSMENT_FORMATIVE_TAG, ILdCoreConstants.iLD_TYPE_ASSESSMENT_FORMATIVE);
		**/
		htTagToTypesTable.put(ILdCoreConstants.sRESOURCE_TAG,
				ILdCoreConstants.iLD_TYPE_RESOURCE);
		htTagToTypesTable.put(ILdCoreConstants.sROLE_TAG, ILdCoreConstants.iLD_TYPE_ROLE);
		htTagToTypesTable.put(ILdCoreConstants.sTASK_TAG, ILdCoreConstants.iLD_TYPE_TASK);
		htTagToTypesTable.put(ILdCoreConstants.sVLE_TOOL_TAG,
				ILdCoreConstants.iLD_TYPE_VLE_TOOL);
		htTagToTypesTable.put(ILdCoreConstants.sSTOP_TAG,
				ILdCoreConstants.iLD_TYPE_STOP);

		// Upper
	htUpperTagToTypesTable.put(ILdCoreConstants.sACTIVITY_TAG,
				ILdCoreConstants.iLD_TYPE_ACTIVITY);
	htUpperTagToTypesTable.put(ILdCoreConstants.sASSESSMENT_TAG,
				ILdCoreConstants.iLD_TYPE_ASSESSMENT);
	htUpperTagToTypesTable.put(ILdCoreConstants.sLEARNING_OUTCOME_TAG, ILdCoreConstants.iLD_TYPE_LEARNING_OUTCOME);
	htUpperTagToTypesTable.put(ILdCoreConstants.sRESOURCE_TAG,
				ILdCoreConstants.iLD_TYPE_RESOURCE);
	htUpperTagToTypesTable.put(ILdCoreConstants.sROLE_TAG, ILdCoreConstants.iLD_TYPE_ROLE);
	htUpperTagToTypesTable.put(ILdCoreConstants.sTASK_TAG, ILdCoreConstants.iLD_TYPE_TASK);
	htUpperTagToTypesTable.put(ILdCoreConstants.sVLE_TOOL_TAG,
				ILdCoreConstants.iLD_TYPE_VLE_TOOL);
	htUpperTagToTypesTable.put(ILdCoreConstants.sSTOP_TAG,
			ILdCoreConstants.iLD_TYPE_STOP);
	htUpperTagToTypesTable.put(ILdCoreConstants.sCONDITIONAL_CONDITION_TAG,
			ILdCoreConstants.iLD_TYPE_CONDITIONAL_CONDITION);
	htUpperTagToTypesTable.put(ILdCoreConstants.sCONDITIONAL_FALSE_TAG,
			ILdCoreConstants.iLD_TYPE_CONDITIONAL_FALSE);
	htUpperTagToTypesTable.put(ILdCoreConstants.sCONDITIONAL_TRUE_TAG,
			ILdCoreConstants.iLD_TYPE_CONDITIONAL_TRUE);
				
		/**	Initialise the types to tags table 	**/
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_ACTIVITY,
				ILdCoreConstants.sACTIVITY_TAG);
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_ASSESSMENT, ILdCoreConstants.sASSESSMENT_TAG);
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_LEARNING_OUTCOME, ILdCoreConstants.sLEARNING_OUTCOME_TAG);
	/**	htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_ASSESSMENT_SUMMATIVE,
				ILdCoreConstants.sASSESSMENT_SUMMATIVE_TAG);
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_ASSESSMENT_FORMATIVE,
				ILdCoreConstants.sASSESSMENT_FORMATIVE_TAG); 	**/
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_RESOURCE,
				ILdCoreConstants.sRESOURCE_TAG);
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_ROLE,
				ILdCoreConstants.sROLE_TAG);
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_TASK,
				ILdCoreConstants.sTASK_TAG);
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL,
				ILdCoreConstants.sVLE_TOOL_TAG);
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_STOP,
				ILdCoreConstants.sSTOP_TAG);
		//Upper
		htUpperTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_ACTIVITY,
				ILdCoreConstants.sACTIVITY_TAG);
		htUpperTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_ASSESSMENT,
				ILdCoreConstants.sASSESSMENT_TAG);
		htUpperTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_LEARNING_OUTCOME,
				ILdCoreConstants.sLEARNING_OUTCOME_TAG);
		htUpperTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_RESOURCE,
				ILdCoreConstants.sRESOURCE_TAG);
		htUpperTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_ROLE,
				ILdCoreConstants.sROLE_TAG);
		htUpperTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_TASK,
				ILdCoreConstants.sTASK_TAG);
		htUpperTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL,
				ILdCoreConstants.sVLE_TOOL_TAG);
		htUpperTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_STOP,
				ILdCoreConstants.sSTOP_TAG);
		htUpperTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_CONDITIONAL_CONDITION,
				ILdCoreConstants.sCONDITIONAL_CONDITION_TAG);
		htUpperTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_CONDITIONAL_TRUE,
				ILdCoreConstants.sCONDITIONAL_TRUE_TAG);
		htUpperTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_CONDITIONAL_FALSE,
				ILdCoreConstants.sCONDITIONAL_FALSE_TAG);
		
	}
	
	/**
	 * Initialise the tags to types table htTagtoTypesTable 
	 * and the  types to tags table htTypestoTagsTable
	 * for the 8 tool sub-types (i.e. blog, e-portfolio, forum,
	 * instant messaging, podcast, simulation, virtual world, wiki)
	 * and the 3 role sub-types (i.e. student, student group, tutor). 
	 */
	public void initialiseTypes()	{
		
		htTagToTypesTable.put(ILdCoreConstants.sVLE_TOOL_BLOG_TAG, ILdCoreConstants.iLD_TYPE_VLE_TOOL_BLOG);
		htTagToTypesTable.put(ILdCoreConstants.sVLE_TOOL_EP_TAG, ILdCoreConstants.iLD_TYPE_VLE_TOOL_EP);
		htTagToTypesTable.put(ILdCoreConstants.sVLE_TOOL_FORUM_TAG, ILdCoreConstants.iLD_TYPE_VLE_TOOL_FORUM);
		htTagToTypesTable.put(ILdCoreConstants.sVLE_TOOL_IM_TAG, ILdCoreConstants.iLD_TYPE_VLE_TOOL_IM);
		htTagToTypesTable.put(ILdCoreConstants.sVLE_TOOL_PODCAST_TAG, ILdCoreConstants.iLD_TYPE_VLE_TOOL_PODCAST);
		htTagToTypesTable.put(ILdCoreConstants.sVLE_TOOL_SIM_TAG, ILdCoreConstants.iLD_TYPE_VLE_TOOL_SIM);
		htTagToTypesTable.put(ILdCoreConstants.sVLE_TOOL_VW_TAG, ILdCoreConstants.iLD_TYPE_VLE_TOOL_VW);
		htTagToTypesTable.put(ILdCoreConstants.sVLE_TOOL_WIKI_TAG, ILdCoreConstants.iLD_TYPE_VLE_TOOL_WIKI);
		htTagToTypesTable.put(ILdCoreConstants.sVLE_TOOL_OTHER_TAG, ILdCoreConstants.iLD_TYPE_VLE_TOOL_OTHER);
		
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_BLOG, ILdCoreConstants.sVLE_TOOL_BLOG_TAG);
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_EP, ILdCoreConstants.sVLE_TOOL_EP_TAG);
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_FORUM, ILdCoreConstants.sVLE_TOOL_FORUM_TAG);
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_IM, ILdCoreConstants.sVLE_TOOL_IM_TAG);
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_PODCAST, ILdCoreConstants.sVLE_TOOL_PODCAST_TAG);
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_SIM, ILdCoreConstants.sVLE_TOOL_SIM_TAG);
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_VW, ILdCoreConstants.sVLE_TOOL_VW_TAG);
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_WIKI, ILdCoreConstants.sVLE_TOOL_WIKI_TAG);
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_OTHER, ILdCoreConstants.sVLE_TOOL_OTHER_TAG);
		
		htTagToTypesTable.put(ILdCoreConstants.sROLE_STUDENT_TAG, ILdCoreConstants.iLD_TYPE_ROLE_STUDENT);
		htTagToTypesTable.put(ILdCoreConstants.sROLE_GROUP_TAG, ILdCoreConstants.iLD_TYPE_ROLE_GROUP);
		htTagToTypesTable.put(ILdCoreConstants.sROLE_TUTOR_TAG, ILdCoreConstants.iLD_TYPE_ROLE_TUTOR);
		htTagToTypesTable.put(ILdCoreConstants.sROLE_OTHER_TAG, ILdCoreConstants.iLD_TYPE_ROLE_OTHER );
		
		htTagToTypesTable.put(ILdCoreConstants.sCONDITIONAL_CONDITION_TAG, ILdCoreConstants.iLD_TYPE_CONDITIONAL_CONDITION);
		htTagToTypesTable.put(ILdCoreConstants.sCONDITIONAL_FALSE_TAG, ILdCoreConstants.iLD_TYPE_CONDITIONAL_FALSE);
		htTagToTypesTable.put(ILdCoreConstants.sCONDITIONAL_TRUE_TAG, ILdCoreConstants.iLD_TYPE_CONDITIONAL_TRUE);
		
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_ROLE_STUDENT, ILdCoreConstants.sROLE_STUDENT_TAG);
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_ROLE_GROUP, ILdCoreConstants.sROLE_GROUP_TAG);
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_ROLE_TUTOR, ILdCoreConstants.sROLE_TUTOR_TAG);
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_ROLE_OTHER, ILdCoreConstants.sROLE_OTHER_TAG);
		
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_CONDITIONAL_CONDITION, ILdCoreConstants.sCONDITIONAL_CONDITION_TAG);
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_CONDITIONAL_FALSE, ILdCoreConstants.sCONDITIONAL_FALSE_TAG);
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_CONDITIONAL_TRUE, ILdCoreConstants.sCONDITIONAL_TRUE_TAG);
		
		htTagToTypesTable.put(ILdCoreConstants.sASSESSMENT_TAG, ILdCoreConstants.iLD_TYPE_ASSESSMENT);
		htTagToTypesTable.put(ILdCoreConstants.sASSESSMENT_FORMATIVE_TAG, ILdCoreConstants.iLD_TYPE_ASSESSMENT_FORMATIVE);
		htTagToTypesTable.put(ILdCoreConstants.sASSESSMENT_SUMMATIVE_TAG, ILdCoreConstants.iLD_TYPE_ASSESSMENT_SUMMATIVE);
		
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_ASSESSMENT, ILdCoreConstants.sASSESSMENT_TAG);
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_ASSESSMENT_FORMATIVE, ILdCoreConstants.sASSESSMENT_FORMATIVE_TAG);
		htTypesToTagsTable.put(ILdCoreConstants.iLD_TYPE_ASSESSMENT_SUMMATIVE, ILdCoreConstants.sASSESSMENT_SUMMATIVE_TAG);
	}
	
	public void initialiseToolTypesToNames()	{
		
		htToolTypeToToolNamesTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_BLOG, ILdCoreConstants.sVLE_TOOL_BLOG_NAME);
		htToolTypeToToolNamesTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_EP, ILdCoreConstants.sVLE_TOOL_EP_NAME);
		htToolTypeToToolNamesTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_FORUM, ILdCoreConstants.sVLE_TOOL_FORUM_NAME);
		htToolTypeToToolNamesTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_IM, ILdCoreConstants.sVLE_TOOL_IM_NAME);
		htToolTypeToToolNamesTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_PODCAST, ILdCoreConstants.sVLE_TOOL_PODCAST_NAME);
		htToolTypeToToolNamesTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_SIM, ILdCoreConstants.sVLE_TOOL_SIM_NAME);
		htToolTypeToToolNamesTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_VW, ILdCoreConstants.sVLE_TOOL_VW_NAME);
		htToolTypeToToolNamesTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_WIKI, ILdCoreConstants.sVLE_TOOL_WIKI_NAME);
		htToolTypeToToolNamesTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_OTHER, ILdCoreConstants.sVLE_TOOL_OTHER_NAME);		
	}
	
	/**
	 * The htTagtoSubTypesTable hols a mapping between LD node tags and 
	 * LD sub-types. It is needed because a LD node will always have a type 
	 * tag, and may have a sub-type tag. Use of this table is the quickest
	 * way of finding a LD nodes sub-type (if any). 
	 */
	public void initialiseTagToSubTypes()	{
		htTagToSubTypesTable.put(ILdCoreConstants.sVLE_TOOL_BLOG_TAG, ILdCoreConstants.iLD_TYPE_VLE_TOOL_BLOG);
		htTagToSubTypesTable.put(ILdCoreConstants.sVLE_TOOL_EP_TAG, ILdCoreConstants.iLD_TYPE_VLE_TOOL_EP);
		htTagToSubTypesTable.put(ILdCoreConstants.sVLE_TOOL_FORUM_TAG, ILdCoreConstants.iLD_TYPE_VLE_TOOL_FORUM);
		htTagToSubTypesTable.put(ILdCoreConstants.sVLE_TOOL_IM_TAG, ILdCoreConstants.iLD_TYPE_VLE_TOOL_IM);
		htTagToSubTypesTable.put(ILdCoreConstants.sVLE_TOOL_PODCAST_TAG, ILdCoreConstants.iLD_TYPE_VLE_TOOL_PODCAST);
		htTagToSubTypesTable.put(ILdCoreConstants.sVLE_TOOL_SIM_TAG, ILdCoreConstants.iLD_TYPE_VLE_TOOL_SIM);
		htTagToSubTypesTable.put(ILdCoreConstants.sVLE_TOOL_VW_TAG, ILdCoreConstants.iLD_TYPE_VLE_TOOL_VW);
		htTagToSubTypesTable.put(ILdCoreConstants.sVLE_TOOL_WIKI_TAG, ILdCoreConstants.iLD_TYPE_VLE_TOOL_WIKI);
		htTagToSubTypesTable.put(ILdCoreConstants.sVLE_TOOL_OTHER_TAG, ILdCoreConstants.iLD_TYPE_VLE_TOOL_OTHER);
		
		htTagToSubTypesTable.put(ILdCoreConstants.sROLE_STUDENT_TAG, ILdCoreConstants.iLD_TYPE_ROLE_STUDENT);
		htTagToSubTypesTable.put(ILdCoreConstants.sROLE_GROUP_TAG, ILdCoreConstants.iLD_TYPE_ROLE_GROUP);
		htTagToSubTypesTable.put(ILdCoreConstants.sROLE_TUTOR_TAG, ILdCoreConstants.iLD_TYPE_ROLE_TUTOR);
		htTagToSubTypesTable.put(ILdCoreConstants.sROLE_OTHER_TAG, ILdCoreConstants.iLD_TYPE_ROLE_OTHER );
		
		htTagToSubTypesTable.put(ILdCoreConstants.sASSESSMENT_FORMATIVE_TAG, ILdCoreConstants.iLD_TYPE_ASSESSMENT_FORMATIVE);
		htTagToSubTypesTable.put(ILdCoreConstants.sASSESSMENT_SUMMATIVE_TAG, ILdCoreConstants.iLD_TYPE_ASSESSMENT_SUMMATIVE);
	}	

	public void initialiseCodeGroups()	{
		hsLdCodeGroups.add(ILdCoreConstants.sLD_CODE_GROUP_ID);
		hsLdCodeGroups.add(ILdCoreConstants.sLD_SUB_TYPE_CODE_GROUP_ID);
		hsLdCodeGroups.add(ILdCoreConstants.sLD_CONDITIONS_CODE_GROUP_ID);
	}
	
	private void initialiseLabels() {
		String sAssessment = "Assessment (";
		String sTool = "Tool (";
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_ACTIVITY,
				ILdCoreConstants.sACTIVITY_LABEL.toLowerCase());
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_ASSESSMENT_SUMMATIVE,
				sAssessment + "summative)");
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_ASSESSMENT,
				sAssessment + "other)");
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_ASSESSMENT_FORMATIVE,
				sAssessment + "formative)");
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_LEARNING_OUTCOME,
				"Learning outcome");
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_RESOURCE,
				ILdCoreConstants.sRESOURCE_LABEL);
		// Note that all role types use the same icon at present. Change the strings in the following key-value pairs to alter this.
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_ROLE,
				ILdCoreConstants.sROLE_LABEL + " (any)");
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_ROLE_STUDENT,
				ILdCoreConstants.sROLE_LABEL + " (student)");
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_ROLE_TUTOR,
				ILdCoreConstants.sROLE_LABEL + " (tutor)");
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_ROLE_OTHER,
				ILdCoreConstants.sROLE_LABEL + " (other)");
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_ROLE_GROUP,
				ILdCoreConstants.sROLE_LABEL + " (group)");
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_STOP,
				ILdCoreConstants.sSTOP_LABEL);
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_TASK,
				ILdCoreConstants.sTASK_LABEL);
		// Note all tool nodes use the same icon at present. Change the strings in the following key-value pairs to alter this.
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL,
				sTool + "any)");
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_BLOG,
				ILdCoreConstants.sVLE_TOOL_BLOG_NAME);
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_EP,
				ILdCoreConstants.sVLE_TOOL_EP_NAME);
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_FORUM,
				ILdCoreConstants.sVLE_TOOL_FORUM_NAME);
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_IM,
				ILdCoreConstants.sVLE_TOOL_IM_NAME);
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_PODCAST,
				ILdCoreConstants.sVLE_TOOL_PODCAST_NAME);
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_SIM,
				ILdCoreConstants.sVLE_TOOL_SIM_NAME);
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_VW,
				ILdCoreConstants.sVLE_TOOL_VW_NAME);
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_WIKI,
				ILdCoreConstants.sVLE_TOOL_WIKI_NAME);
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_OTHER,
				ILdCoreConstants.sVLE_TOOL_OTHER_NAME);
		// Conditional node images
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_CONDITIONAL_CONDITION,
				"condition");
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_CONDITIONAL_FALSE,
		"false");
		htTypesToLabelsTable.put(ILdCoreConstants.iLD_TYPE_CONDITIONAL_TRUE,
		"true");
		
	}
	
	/**
	 * Return the Hashtable which maps between LD node tags and LD
	 * node types and sub-types.
	 * @return Hashtable htTagtoTypesTable
	 */
	public  Hashtable<String, Integer> getTagToTypeTable()	{
		return htTagToTypesTable;
	}
	
	public  Hashtable<Integer, String> getTypestoTagsTable()	{
		return htTypesToTagsTable;
	}
	/**
	 * Return Hashtable of the code ids of the LD type tags (keys) 
	 * to LD tag strings (values)
	 * @return the htCodesToTagsTable
	 */
	public Hashtable<String, String> getCodesToTagsTable() {
		return htCodesToTagsTable;
	}
	/**
	 * Returns a Hashtable of the  LD type Integers (keys) to code ids of the 
	 * LD type tags  (values)
	 * @return the htTypestoCodesTable
	 */
	public Hashtable<Integer, String> getTypesToCodesTable() {
		return htTypesToCodesTable;
	}
	/**
	 * @return the htToolTagtoToolTypesTable
	 */
	public Hashtable<String, Integer> getToolTagToToolTypesTable() {
		return htToolTagToToolTypesTable;
	}
	/**
	 * @param htToolTagtoToolTypesTable the htToolTagtoToolTypesTable to set
	 */
	public void setToolTagToToolTypesTable(
			Hashtable<String, Integer> htToolTagtoToolTypesTable) {
		this.htToolTagToToolTypesTable = htToolTagtoToolTypesTable;
	}
	/**
	 * @return the htTagsToCodesTable
	 */
	public Hashtable<String, String> getTagsToCodesTable() {
		return htTagsToCodesTable;
	}
	
	/**
	 * Return the tag that the integer value type is mapped to, or null if it is
	 * not in the map.
	 * @param type
	 * @return
	 */
	public String getTagForType(int type)	{
		return htTypesToTagsTable.get(type);
	}

	/**
	 * @return the htToolTypeToToolNamesTable
	 */
	public Hashtable<Integer, String> getToolTypeToToolNamesTable() {
		return htToolTypeToToolNamesTable;
	}

	/**
	 * @param htToolTypeToToolNamesTable the htToolTypeToToolNamesTable to set
	 */
	public void setHtToolTypeToToolNamesTable(
			Hashtable<Integer, String> htToolTypeToToolNamesTable) {
		this.htToolTypeToToolNamesTable = htToolTypeToToolNamesTable;
	}

	/**
	 * @return the htTagtoSubTypesTable
	 */
	public Hashtable<String, Integer> getTagtoSubTypesTable() {
		return htTagToSubTypesTable;
	}

	/**
	 * @return the htUpperTagtoTypesTable
	 */
	public Hashtable<String, Integer> getUpperTagtoTypesTable() {
		return htUpperTagToTypesTable;
	}

	/**
	 * @param htUpperTagtoTypesTable the htUpperTagtoTypesTable to set
	 */
	public void setUpperTagtoTypesTable(
			Hashtable<String, Integer> htUpperTagtoTypesTable) {
		this.htUpperTagToTypesTable = htUpperTagtoTypesTable;
	}

	/**
	 * @return the htUpperTypestoTagsTable
	 */
	public Hashtable<Integer, String> getUpperTypestoTagsTable() {
		return htUpperTypesToTagsTable;
	}

	/**
	 * @param htUpperTypestoTagsTable the htUpperTypestoTagsTable to set
	 */
	public void setUpperTypestoTagsTable(
			Hashtable<Integer, String> htUpperTypestoTagsTable) {
		this.htUpperTypesToTagsTable = htUpperTypestoTagsTable;
	}

	/**
	 * Return the HashSet containing the CodeGroup IDs for the learning
	 * design code groups.
	 * @return the hsLdCodeGroups
	 */
	public HashSet<String> getHsLdCodeGroups() {
		return hsLdCodeGroups;
	}

	/**
	 * @return the htTypesToLabelsTable
	 */
	public Hashtable<Integer, String> getHtTypesToLabelsTable() {
		return htTypesToLabelsTable;
	}
}

