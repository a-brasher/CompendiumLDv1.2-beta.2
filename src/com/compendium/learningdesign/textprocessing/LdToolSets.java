/**
 * 
 */
package com.compendium.learningdesign.textprocessing;

import java.util.*;

import com.compendium.learningdesign.core.ILdCoreConstants;
/**
 * The LdToolSets class manages sets of Integers. Each integer in each set represents 
 * a learning design tool, and each set represents the set of tools that could support
 * a particular learning activity. These sets are intended to be used by the 
 * ActivityLabelProcessor class.  
 * @author ajb785
 *
 */
public class LdToolSets {
/**
 * @uml.property  name="hsAdjust"
 */
HashSet<Integer> hsAdjust = new HashSet<Integer>();
/**
 * @uml.property  name="hsCollaborate"
 */
HashSet<Integer> hsCollaborate = new HashSet<Integer>();
HashSet<Integer> hsCollate = new HashSet<Integer>();
/**
 * @uml.property  name="hsComment"
 */
HashSet<Integer> hsComment = new HashSet<Integer>();
/**
 * @uml.property  name="hsConfer"
 */
HashSet<Integer> hsConfer = new HashSet<Integer>();
HashSet<Integer> hsConsider = new HashSet<Integer>();
/**
 * @uml.property  name="hsContribute"
 */
HashSet<Integer> hsContribute = new HashSet<Integer>();
/**
 * @uml.property  name="hsDebate"
 */
HashSet<Integer> hsDebate = new HashSet<Integer>();

/**
 * @uml.property  name="hsEvidence"
 */
HashSet<Integer> hsEvidence = new HashSet<Integer>();
/**
 * @uml.property  name="hsDiscuss"
 */
HashSet<Integer> hsDiscuss = new HashSet<Integer>();
/**
 * @uml.property  name="hsFeedback"
 */
HashSet<Integer> hsFeedback = new HashSet<Integer>();
/**
 * @uml.property  name="hsGroup"
 */
HashSet<Integer> hsGroup = new HashSet<Integer>();
/**
 * @uml.property  name="hsJournal"
 */
HashSet<Integer> hsJournal = new HashSet<Integer>();
/**
 * @uml.property  name="hsListen"
 */
HashSet<Integer> hsListen = new HashSet<Integer>();
/**
 * @uml.property  name="hsMeet"
 */
HashSet<Integer> hsMeet = new HashSet<Integer>();
/**
 * @uml.property  name="hsMonitor"
 */
HashSet<Integer> hsMonitor = new HashSet<Integer>();
/**
 * @uml.property  name="hsOrganise"
 */
HashSet<Integer> hsOrganise = new HashSet<Integer>();
/**
 * @uml.property  name="hsPlan"
 */
HashSet<Integer> hsPlan = new HashSet<Integer>();
HashSet<Integer> hsPublish = new HashSet<Integer>();
/**
 * @uml.property  name="hsReflect"
 */
HashSet<Integer> hsReflect = new HashSet<Integer>();
/**
 * @uml.property  name="hsReview"
 */
HashSet<Integer> hsReview = new HashSet<Integer>();
/**
 * @uml.property  name="hsShare"
 */
HashSet<Integer> hsShare = new HashSet<Integer>();


/**
 * 
 */
public LdToolSets() {
	super();
	initialise();
}

 


public void initialise()	{
	List<Integer> iL = null;
	Integer[] a = {new Integer(ILdCoreConstants.iLD_TYPE_VLE_TOOL_SIM), new Integer(ILdCoreConstants.iLD_TYPE_VLE_TOOL_VW)};
	iL = Arrays.asList(a);
	hsAdjust.addAll(iL);
	Integer[] b = {new Integer(ILdCoreConstants.iLD_TYPE_VLE_TOOL_FORUM), new Integer(ILdCoreConstants.iLD_TYPE_VLE_TOOL_IM), 
			new Integer (ILdCoreConstants.iLD_TYPE_VLE_TOOL_WIKI)};
	iL = Arrays.asList(b);
	hsCollaborate.addAll(iL);
	hsCollate.add(new Integer(ILdCoreConstants.iLD_TYPE_VLE_TOOL_EP));
	hsComment = hsCollaborate;
	hsConfer = hsCollaborate;
	hsConsider.add(new Integer(ILdCoreConstants.iLD_TYPE_VLE_TOOL_BLOG));
	hsContribute.addAll(hsCollaborate);  hsContribute.addAll(hsConsider);
	hsDebate = hsCollaborate;
	Integer[] c = {new Integer(ILdCoreConstants.iLD_TYPE_VLE_TOOL_BLOG),new Integer(ILdCoreConstants.iLD_TYPE_VLE_TOOL_EP) };
	iL = Arrays.asList(c);
	hsEvidence.addAll(iL);
	hsDiscuss = hsComment;
	hsFeedback = hsComment;
	hsGroup = hsCollate;
	hsJournal = hsEvidence;
	Integer[] d = {new Integer(ILdCoreConstants.iLD_TYPE_VLE_TOOL_IM), new Integer(ILdCoreConstants.iLD_TYPE_VLE_TOOL_PODCAST)};
	iL = Arrays.asList(d);
	hsListen.addAll(iL);
	Integer[] e = {new Integer(ILdCoreConstants.iLD_TYPE_VLE_TOOL_FORUM), new Integer(ILdCoreConstants.iLD_TYPE_VLE_TOOL_IM)};
	iL = Arrays.asList(e);
	hsMeet.addAll(iL);
	hsMonitor = hsEvidence;
	hsOrganise.addAll(hsCollate); hsOrganise.addAll(hsDebate);
	hsPlan = hsMeet;
	hsPublish.addAll(hsEvidence); hsPublish.add(new Integer (ILdCoreConstants.iLD_TYPE_VLE_TOOL_WIKI));
	hsReflect = hsPublish;
	hsReview = hsJournal;
	hsShare = hsDiscuss;
}	 




/**
 * @return the hsAdjust
 */
public HashSet<Integer> getHsAdjust() {
	return hsAdjust;
}




/**
 * @return the hsCollaborate
 */
public HashSet<Integer> getHsCollaborate() {
	return hsCollaborate;
}




/**
 * @return the hsCollate
 */
public HashSet<Integer> getHsCollate() {
	return hsCollate;
}




/**
 * @return the hsComment
 */
public HashSet<Integer> getHsComment() {
	return hsComment;
}




/**
 * @return the hsConsider
 */
public HashSet<Integer> getHsConsider() {
	return hsConsider;
}




/**
 * @return the hsContribute
 */
public HashSet<Integer> getHsContribute() {
	return hsContribute;
}




/**
 * @return the hsDebate
 */
public HashSet<Integer> getHsDebate() {
	return hsDebate;
}




/**
 * @return the hsEvidence
 */
public HashSet<Integer> getHsEvidence() {
	return hsEvidence;
}




/**
 * @return the hsDiscuss
 */
public HashSet<Integer> getHsDiscuss() {
	return hsDiscuss;
}




/**
 * @return the hsFeedback
 */
public HashSet<Integer> getHsFeedback() {
	return hsFeedback;
}




/**
 * @return the hsGroup
 */
public HashSet<Integer> getHsGroup() {
	return hsGroup;
}




/**
 * @return the hsJournal
 */
public HashSet<Integer> getHsJournal() {
	return hsJournal;
}




/**
 * @return the hsListen
 */
public HashSet<Integer> getHsListen() {
	return hsListen;
}




/**
 * @return the hsMeet
 */
public HashSet<Integer> getHsMeet() {
	return hsMeet;
}




/**
 * @return the hsMonitor
 */
public HashSet<Integer> getHsMonitor() {
	return hsMonitor;
}




/**
 * @return the hsOrganise
 */
public HashSet<Integer> getHsOrganise() {
	return hsOrganise;
}




/**
 * @return the hsPlan
 */
public HashSet<Integer> getHsPlan() {
	return hsPlan;
}




/**
 * @return the hsPublish
 */
public HashSet<Integer> getHsPublish() {
	return hsPublish;
}




/**
 * @return the hsReflect
 */
public HashSet<Integer> getHsReflect() {
	return hsReflect;
}




/**
 * @return the hsReview
 */
public HashSet<Integer> getHsReview() {
	return hsReview;
}




/**
 * @return the hsShare
 */
public HashSet<Integer> getHsShare() {
	return hsShare;
}




/**
 * @return the hsConfer
 */
public HashSet<Integer> getHsConfer() {
	return hsConfer;
}

}
