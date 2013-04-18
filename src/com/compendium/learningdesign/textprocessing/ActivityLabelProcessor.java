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

package com.compendium.learningdesign.textprocessing;

import java.util.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.regex.Pattern;

import com.compendium.ProjectCompendium;
import com.compendium.ui.UINode;
import com.compendium.learningdesign.core.ILdCoreConstants;
import com.compendium.learningdesign.core.LdTypeTagMaps;
import com.compendium.learningdesign.ui.UILdTaskNode;

/**
 * This class matches words entered into a learning design task or activity
 * node's label with learning design tool types. It uses HashMaps: these are 
 * not synchronised but that should not matter because once it is initialised
 * it will not be modified. 
 * @author ajb785
 *
 */
public class ActivityLabelProcessor implements PropertyChangeListener {
	/**
	 * Map of words to sets of ints, each int represents a learning design tool type	*
	 * @uml.property  name="hmWordToToolsMap"
	 * @uml.associationEnd  qualifier="toLowerCase:java.lang.String java.util.HashSet"
	 */
	HashMap<String, HashSet<Integer>> hmWordToToolsMap = new HashMap<String, HashSet<Integer>>();
	
	/**
	 * ldToolSets manages the sets of Integers which are to be mapped to words within hmWordToToolsMap	*
	 * @uml.property  name="ldToolSets"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	LdToolSets ldToolSets = new LdToolSets();
	
	


/**
 * 
 */
public ActivityLabelProcessor()  {
	super();
	initialise();
}
/**
 * @param hmWordToToolsMap
 */
public ActivityLabelProcessor(HashMap hmWordToToolsMap) {
	super();
	this.hmWordToToolsMap = hmWordToToolsMap;
}

/**
 * Add default values to hmWordToToolsMap/ 
 */
public void initialise()	{
	hmWordToToolsMap.put("adjust", ldToolSets.getHsAdjust());
	hmWordToToolsMap.put("brainstorm", ldToolSets.getHsCollaborate());
	hmWordToToolsMap.put("collaborate", ldToolSets.getHsCollaborate());
	hmWordToToolsMap.put("collate", ldToolSets.getHsCollate());
	hmWordToToolsMap.put("comment", ldToolSets.getHsComment());
	hmWordToToolsMap.put("confer", ldToolSets.getHsConfer());
	hmWordToToolsMap.put("consider", ldToolSets.getHsConsider());
	hmWordToToolsMap.put("contribute", ldToolSets.getHsContribute());
	hmWordToToolsMap.put("debate", ldToolSets.getHsDebate());
	hmWordToToolsMap.put("demonstrate", ldToolSets.getHsAdjust());
	hmWordToToolsMap.put("discuss", ldToolSets.getHsDiscuss());
	hmWordToToolsMap.put("evidence", ldToolSets.getHsEvidence());	
	hmWordToToolsMap.put("feedback", ldToolSets.getHsFeedback());
	hmWordToToolsMap.put("gather", ldToolSets.getHsEvidence());
	hmWordToToolsMap.put("group", ldToolSets.getHsGroup());
	hmWordToToolsMap.put("interview", ldToolSets.getHsMeet());
	hmWordToToolsMap.put("journal", ldToolSets.getHsJournal());
	hmWordToToolsMap.put("listen", ldToolSets.getHsListen());
	hmWordToToolsMap.put("manipulate", ldToolSets.getHsAdjust());
	hmWordToToolsMap.put("meet", ldToolSets.getHsMeet());
	hmWordToToolsMap.put("model", ldToolSets.getHsAdjust());
	hmWordToToolsMap.put("monitor", ldToolSets.getHsMonitor());
	hmWordToToolsMap.put("organise", ldToolSets.getHsOrganise());
	hmWordToToolsMap.put("plan", ldToolSets.getHsPlan());
	hmWordToToolsMap.put("publish", ldToolSets.getHsPublish());
	hmWordToToolsMap.put("reflect", ldToolSets.getHsReflect());
	hmWordToToolsMap.put("review", ldToolSets.getHsReview());
	hmWordToToolsMap.put("share", ldToolSets.getHsShare());
	hmWordToToolsMap.put("simulate", ldToolSets.getHsAdjust());
	
}

/**
 * Get a tool set related to the String sWord. The tools are represented by 
 * Integers in class ILdCoreConstants e.g. ILdCoreConstants.iLD_TYPE_VLE_TOOL_FORUM.
 * The matching is not sensitive to the case of sWord.
 * @param sWord
 * @return a HashSet of Integers represnting tools
 */
public HashSet<Integer> getToolSetForWord(String sWord){
	// Convert input string to lowercase to match keys in the hashmap
	String sText = sWord.toLowerCase();
	return this.getHmWordToToolsMap().get(sText);
}

/**
 * Get a tool set related to the String sWord. The tools are represented by 
 * tool tags  i.e. the tag IDs of the tags used to denote LD nodes s9such as 
 * ILdCoreConstants.sVLE_TOOL_BLOG_TAG).
 * @param sWord
 * @return a HashSet of tag Strings representing tools
 */
public HashSet<String> getTagSetForWord(String sWord){
	HashSet<Integer> hsToolSet = this.getToolSetForWord(sWord);
	HashSet<String> hsToolTagSet = new HashSet<String>();
	LdTypeTagMaps oLdTypeTagMaps = ProjectCompendium.APP.getLdTypeTagMaps();
	Iterator<Integer> it = hsToolSet.iterator();
	while (it.hasNext() )	{
		int n = it.next();
		hsToolTagSet.add(oLdTypeTagMaps.getTypesToCodesTable().get(n));
	}
	return hsToolTagSet;
}
/**
 * @return the hmWordToToolsMap
 */
public HashMap<String, HashSet<Integer>> getHmWordToToolsMap() {
	return hmWordToToolsMap;
}
/**
 * @return  the ldToolSets
 * @uml.property  name="ldToolSets"
 */
public LdToolSets getLdToolSets() {
	return ldToolSets;
}
//@Override

/* Method to initiate label processing for node label events.
 * This is a palceholder, it does nothing currently.
 * (non-Javadoc)
 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
 */

public void propertyChange(PropertyChangeEvent evt) {
	String prop = evt.getPropertyName();
	Object source = evt.getSource();

	if (source instanceof UILdTaskNode) {
		if (prop.equals(UINode.TEXT_PROPERTY)) {
			 String sNewText = (String)evt.getNewValue();
			 String sOldText = (String)evt.getOldValue();
			 /**
			 if (!sNewText.equalsIgnoreCase(sOldText))	{
				 System.out.println("sNewText = " + sNewText + " sOldText = " + sOldText + " compareTo = " + sNewText.compareToIgnoreCase(sOldText));
				String[] sArr = sNewText.split("\b");
				for (int i=0; i<sArr.length; ++i)	{
					System.out.println("sArr." + i +  " = " + sArr[i]);
				}
			 }
			 **/ 
		}
	}
}


}
