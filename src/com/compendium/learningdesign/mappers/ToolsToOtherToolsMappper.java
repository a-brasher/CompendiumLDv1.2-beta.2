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

import com.compendium.ProjectCompendium;
import com.compendium.ui.stencils.*;
import com.compendium.learningdesign.core.*;
import com.compendium.learningdesign.ui.*;

/**
 * This class instantiates relationships between learning design tool types
 * and returns sets of DragggableStencilIcons related to a given tool type.
 * @author ajb785
 * 
 */
public class ToolsToOtherToolsMappper extends LdToolMapper {
	 /**
	 * A map between  ld tool types  and other tool types *
	 * @uml.property  name="typeMap"
	 * @uml.associationEnd  multiplicity="(0 -1)" ordering="true" elementType="java.lang.Integer" qualifier="valueOf:java.lang.Integer java.util.HashSet"
	 */  
	private  	HashMap<Integer, HashSet<Integer>>  typeMap = null;

	/**
	 * 
	 */
	public ToolsToOtherToolsMappper() {
		super();
		createMap();
		typeMap = new HashMap<Integer, HashSet<Integer>>();
		createTypeMap();
	}
	
	/**
	 * Helper method to create the name-to-name map for tools.
	 */
	private void createMap()	{
		String blog = "blog";
		String ep = "e-portfolio";
		String forum = "forum";
		String im = "instant messaging";
		String podcast = "podcast";
		String sim = "simulation";
		String vw = "virtual world";
		String wiki = "wiki";

		// ArrayList<String> list = new ArrayList();

		HashSet<String> hs = new HashSet<String>();
		hs.add(ep);
		hs.add(podcast);
		getMap().put(blog, hs); // blog
		hs = new HashSet<String>();
		hs.add(blog);
		getMap().put(ep, hs); // e-portfolio
		hs = new HashSet<String>();
		hs.add(im);
		hs.add(wiki);
		getMap().put(forum, hs); // forum
		hs = new HashSet<String>();
		hs.add(forum);
		hs.add(wiki);
		getMap().put(im, hs); // instant messaging
		hs = new HashSet<String>();
		hs.add(blog);
		getMap().put(podcast, hs); // podcast
		hs = new HashSet<String>();
		hs.add(vw);
		getMap().put(sim, hs); // simulation
		hs = new HashSet<String>();
		hs.add(sim);
		getMap().put(vw, hs); // virtual world
		hs = new HashSet<String>();
		hs.add(forum);
		hs.add(im);
		getMap().put(wiki, hs); // wiki
	}
	
	/**
	 * Helper method to create the type(integer)-to-type(integer) map for tools.
	 */
	private void createTypeMap()	{
		Integer blog = ILdCoreConstants.iLD_TYPE_VLE_TOOL_BLOG;
		Integer ep = ILdCoreConstants.iLD_TYPE_VLE_TOOL_EP;
		Integer forum = ILdCoreConstants.iLD_TYPE_VLE_TOOL_FORUM;
		Integer im = ILdCoreConstants.iLD_TYPE_VLE_TOOL_IM;
		Integer podcast = ILdCoreConstants.iLD_TYPE_VLE_TOOL_PODCAST;
		Integer sim = ILdCoreConstants.iLD_TYPE_VLE_TOOL_SIM;
		Integer vw = ILdCoreConstants.iLD_TYPE_VLE_TOOL_VW;
		Integer wiki = ILdCoreConstants.iLD_TYPE_VLE_TOOL_WIKI;
		
		HashSet<Integer> hs = new HashSet<Integer>();
		hs.add(ep);
		hs.add(podcast);
		getTypeMap().put(blog, hs); // blog
		hs = new HashSet<Integer>();
		hs.add(blog);
		getTypeMap().put(ep, hs); // e-portfolio
		hs = new HashSet<Integer>();
		hs.add(im);
		hs.add(wiki);
		getTypeMap().put(forum, hs); // forum
		hs = new HashSet<Integer>();
		hs.add(forum);
		hs.add(wiki);
		getTypeMap().put(im, hs); // instant messaging
		hs = new HashSet<Integer>();
		hs.add(blog);
		getTypeMap().put(podcast, hs); // podcast
		hs = new HashSet<Integer>();
		hs.add(vw);
		getTypeMap().put(sim, hs); // simulation
		hs = new HashSet<Integer>();
		hs.add(sim);
		getTypeMap().put(vw, hs); // virtual world
		hs = new HashSet<Integer>();
		hs.add(forum);
		hs.add(im);
		getTypeMap().put(wiki, hs); // wiki
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.compendium.ui.learningdesign.mappers.LdToolMapper#getToolNames(java.lang.String)
	 */
	@Override
	public HashSet<String> getToolNames(String label) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/* 
	 * Get a set of DraggableStencilIcon tools that are related to the single word label
	 * and the learning design tool node aUILdNode. 
	 * Note that code to identify relations to the label is not yet implemented, this method
	 * only relates between types of tools.
	 * 
	 */
//	@Override
	public HashSet<DraggableStencilIcon> getToolStencils(String word, UILdNode aUILdNode) {
		HashSet<DraggableStencilIcon> oIconSet = new HashSet<DraggableStencilIcon>();
		// If there is no map, return an empty HashSet
		if (getMap() == null)
			return oIconSet;
		else {
			int ldToolType = aUILdNode.getLdSubType();
			HashSet<Integer> relatedToolTypes = getTypeMap().get(ldToolType);
			// Set the iterator it to iterate over the keys (tool names) in the
			// map
			// Iterator<String> it = getMap().keySet().iterator();

			int relatedToolType = 0;
			String sTag = "";
			for (Iterator<Integer> i = relatedToolTypes.iterator(); i.hasNext();) {
				// Need to get relevant LD Stencil Set icon
				// then duplicate it
				relatedToolType = i.next();
				// Get LD Stencil Icon Set currently open
				UIStencilSet  oIconTempSet = this.getLDStencilSet();						
				// Get the Vector of DraggableStenciIcons that are a VLE Tool: should only be one 
				Vector vItems = oIconTempSet.getItemsWithTag(ILdCoreConstants.sVLE_TOOL_TAG);
				DraggableStencilIcon oIconTemp = null;
				/** Note that in Stencil set LD-AB there is only one VLE Tool stencil item.
				 * 	The code below will need to be altered if more than one VLE Tool is included
				 * in the stencil set.
				 */
				if (vItems.size() == 1)	{
					// Get the mappings between Ld type, tags and codes							
					LdTypeTagMaps oLdTypeTagMaps = ProjectCompendium.APP.getLdTypeTagMaps();
					// Get the DraggableStencilIcon that is a VLE tool and make a copy of it.. 
					oIconTemp = ((DraggableStencilIcon) vItems.get(0)).duplicate();
					// Set the label of the copy to be the name of the related tool.
					String relatedToolName = oLdTypeTagMaps.getToolTypeToToolNamesTable().get(relatedToolType);
					oIconTemp.setLabel(relatedToolName);
					// Get the codeId for the related tool name
					sTag = oLdTypeTagMaps.getTagForType(relatedToolType);
					String sCodeId = oLdTypeTagMaps.getTagsToCodesTable().get(sTag);
					oIconTemp.addTag(sCodeId);
					oIconTemp.setToolTipText(relatedToolName);
					// Add this new DraggableStencilIcon to oIconSet
					oIconSet.add(oIconTemp);
				}
				
			}				
		}
		return oIconSet;
	}
	
	/* 
	 * Get a set of DraggableStencilIcon tools that are related to the single word label.
	 * This method was implemented to test the principle. Should use method 
	 * getToolStencils(String word, UILdNode aUILdNode) in real applications.
	 */
	@Override
	public HashSet<DraggableStencilIcon> getToolStencils(String word) {
		HashSet<DraggableStencilIcon> oIconSet = new HashSet<DraggableStencilIcon>();
		// If there is no map, return an empty HashSet
		if (getMap() == null)
			return oIconSet;
		else {
			// Set the iterator it to iterate over the keys (tool names) in the
			// map
			// Iterator<String> it = getMap().keySet().iterator();
			for (Iterator<String> it = getMap().keySet().iterator(); it.hasNext();) {
				String name = it.next();
				String relatedToolName = "";
				int relatedToolType = 0;
				if (name.equalsIgnoreCase(word)) {
					HashSet<String> toolNames = getMap().get(name);
					for (Iterator<String> i = toolNames.iterator(); i.hasNext();) {
						// Need to get relevant LD Stencil Set icon
						// then duplicate it
						relatedToolName = i.next();
						// Get LD Stencil Icon Set currently open
						UIStencilSet  oIconTempSet = this.getLDStencilSet();						
						// Get the Vector of DraggableStenciIcons that are a VLE Tool: should only be one 
						Vector vItems = oIconTempSet.getItemsWithTag(ILdCoreConstants.sVLE_TOOL_TAG);
						DraggableStencilIcon oIconTemp = null;
						/** Note that in Stencil set LD-AB there is only one VLE Tool stencil item.
						 * 	The code below will need to be altered if more than one VLE Tool is included
						 * in the stencil set.
						 */
						if (vItems.size() == 1)	{
							// Get the DraggableStencilIcon that is a VLE tool and make a copy of it.. 
							oIconTemp = ((DraggableStencilIcon) vItems.get(0)).duplicate();							 
							// Set the label of the copy to be the name of the related tool.
							oIconTemp.setLabel(relatedToolName);
							// Get the mappings between Ld type, tags and codes							
							LdTypeTagMaps oLdTypeTagMaps = ProjectCompendium.APP.getLdTypeTagMaps();
							// Get the codeId for the related tool name
							// oLdTypeTagMaps.
							//oIconTemp.addTag()
							oIconTemp.setToolTipText(relatedToolName);
							// Add this new DraggableStencilIcon to oIconSet
							oIconSet.add(oIconTemp);
						}
					}
				}
			}				
		}
		return oIconSet;
	}

	/**
	 * Returns the typeMap, a map between  ld tool types  and other tool types
	 * (both are Integers). 
	 * @return the typeMap
	 */
	public HashMap<Integer, HashSet<Integer>> getTypeMap() {
		return typeMap;
	}

	/**
	 * @param typeMap the typeMap to set
	 */
	public void setTypeMap(HashMap<Integer, HashSet<Integer>> typeMap) {
		this.typeMap = typeMap;
	}
	
}

