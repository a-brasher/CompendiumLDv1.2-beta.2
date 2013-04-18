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

package com.compendium.learningdesign.mappers;

import java.util.*;

import com.compendium.learningdesign.core.ILdCoreConstants;
import com.compendium.learningdesign.core.LdTypeTagMaps;
import com.compendium.ui.stencils.*;
import com.compendium.ProjectCompendium;

/**
 * Class to map between word(s) describing VLE tools and representations of the tools.
 * The mapping map is implemented using  a HashMap, which maps a number of Strings to
 * HashSets of Strings.
 * 
 * @author ajb785
 *
 */
public abstract class LdToolMapper {
	/**
	 * A map between  Strings and HashSets of strings 	*
	 * @uml.property  name="map"
	 * @uml.associationEnd  multiplicity="(0 -1)" ordering="true" elementType="java.lang.String" qualifier="im:java.lang.String java.util.HashSet"
	 */  
	private  	HashMap<String, HashSet<String>>  map = null;
	
	/**
	 * The stencil set from which stencils will be selected by the mapper and copies created *
	 * @uml.property  name="oLdStencilSet"
	 * @uml.associationEnd  
	 */
	private		UIStencilSet oLdStencilSet = null;
	
	
	/**
	 * Default constructor. Creates a LdToolMapper instance with a default (empty) map.
	 * Subclasses should populate this map
	 */
	public  LdToolMapper()	{
		map = new HashMap<String, HashSet<String>>();
		setLDStencilSet();		
	}
	/**
	 * Constructor. Create a a LdToolMapper instance with the map supplied.
	 * @param map - a mapping between Strings and HashSets of Strings.
	 */
	public  LdToolMapper(HashMap<String, HashSet<String>> aMap)	{
		map = aMap;
	}
	
	
	
	/**
	 * @param label - a String that a set of tools may be related or mapped to. 
	 * @return the HashSet of tool names (Strings) associated with the String label, or
	 * null if there are none related/mapped to label.
	 */
	public abstract HashSet<String> getToolNames(String label);	
	
	/**
	 * @param label - a String that a set of tools may be related or mapped to. 
	 * @return a HashSet of DraggableStencilIcon tools  associated with the String label, or
	 * null if there are none related/mapped to label.
	 */
	public abstract HashSet<DraggableStencilIcon> getToolStencils(String label);
	
	
	/** 
	 * Add the relation between the label and set of tools toolNames to the map. 
	 * If a mapping for label already exists it will be replaced. 
	 * @param label - the label for the mapping, i.e. the task verb or tool. 
	 * @param tools - the set of tools which are mapped to the label.
	 * @return map, the new updated map
	 */
	public HashMap<String, HashSet<String>> addRelation(String label, HashSet<String> toolNames)	{
		map.put(label, toolNames);
		return map;
	}
	/**
	 * @return the map
	 */
	public HashMap<String, HashSet<String>> getMap() {		
		return map;		 
	}
	
	/**
	 * Set the variable oLdStencilSet to the 
	 * appplication's learning design stencil set.
	 */
	private UIStencilSet setLDStencilSet()	{
		oLdStencilSet = ProjectCompendium.APP.oStencilManager.getStencilSet(ILdCoreConstants.sLD_STENCIL_NAME);
		if (oLdStencilSet == null)	{		
			// 	Need to load it from file, and throw exception if this can not be done.
			}		
		return oLdStencilSet;
	}
	
	public UIStencilSet getLDStencilSet()	{
		return oLdStencilSet;
	}
	

}
