package com.compendium.learningdesign.io.svg;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SwingSVGPrettyPrint;
import org.apache.batik.util.XMLResourceDescriptor;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.output.DOMOutputter;
import org.jdom2.output.XMLOutputter;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGElement;

import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.Model;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.PCSession;
import com.compendium.core.datamodel.View;
import com.compendium.core.datamodel.Link;
import com.compendium.core.datamodel.services.NodeService;
import com.compendium.io.xml.XMLReader;
import com.compendium.learningdesign.core.ILdCoreConstants;
import com.compendium.learningdesign.io.xml.LdXMLGenerator;
import com.compendium.learningdesign.ui.svggen.SVGLDGraphics2D;
import com.compendium.ui.UIImages;
import com.compendium.ui.UIList;
import com.compendium.ui.UIListViewFrame;
import com.compendium.ui.UIMapViewFrame;
import com.compendium.ui.UIUtilities;
import com.compendium.ui.UIViewFrame;
import com.compendium.ui.UIViewPane;


/**
 * @author ajb785
 *
 */
public class SvgExport {
	/** Connector to use between class string (see below) and node id to create valid id for SVG file **/
	public	final static String sIdFragmentConnector =  ".";
/** Compendium standard nodes	***/
	/** Descriptive string used to denote identifiers and CSS classes for list nodes in  SVG output **/
	public	final static String sGeneralClass =  "general";
	/** Descriptive string used to denote identifiers and CSS classes for list view groups in  SVG output **/
	public	final static String sListViewClass =  "listview";
	/** Descriptive string used to denote identifiers and CSS classes for list nodes in  SVG output **/
	public	final static String sListNodeClass =  "listnode";
	/** Descriptive string used to denote identifiers and CSS classes for mapview groups in  SVG output **/
	public	final static String sMapViewClass =  "mapview";
	/** Descriptive string used to denote identifiers and CSS classes for map nodes in  SVG output **/
	public	final static String sMapNodeClass =  "mapnode";
	/** Descriptive string used to denote identifiers and CSS classes for issue (or question)  nodes in  SVG output **/
	public	final static String sIssueClass =  "issue";
	/** Descriptive string used to denote identifiers and CSS classes for position (or answer) nodes in  SVG output **/
	public	final static String sPositionClass =  "position";
	/** Descriptive string used to denote identifiers and CSS classes for position  nodes in  SVG output **/
	public	final static String sArgumentClass =  "argument";
	/** Descriptive string used to denote identifiers and CSS classes for pro nodes in  SVG output **/
	public	final static String  sProClass =  "pro";
	/** Descriptive string used to denote identifiers and CSS classes for con nodes in  SVG output **/
	public	final static String  sConClass =  "con";
	/** Descriptive string used to denote identifiers and CSS classes for decision nodes in  SVG output **/
	public	final static String sDecisionClass =  "decision";
	/** Descriptive string used to denote identifiers and CSS classes for reference nodes in  SVG output **/
	public	final static String sReferenceClass =  "reference";
	/** Descriptive string used to denote identifiers and CSS classes for note nodes in  SVG output **/
	public	final static String sNoteClass =  "note";
	/** Descriptive string used to denote identifiers and CSS classes for Trashbin nodes in  SVG output **/
	public	final static String sTrashbinClass =  "trashbin";
	/** Compendium standard link arrows	***/
	/** Descriptive string used to denote identifiers and CSS classes for arrow heads in  SVG output **/
	public	final static String sArrowHead =  "arrowhead";
	
	/** Empty descriptive string used to fill sAllNodeClasses ArrayList **/
	public	final static String sNoClass =  "";
	/** Array of class identifier strings in order of Node type integers defined in ICoreConstants	**/
public final static String[] sStandardNodeClasses = {SvgExport.sGeneralClass, SvgExport.sListViewClass, 
		SvgExport.sMapViewClass, SvgExport.sIssueClass, SvgExport.sPositionClass, SvgExport.sArgumentClass, 
		SvgExport.sProClass, SvgExport.sConClass, SvgExport.sDecisionClass, SvgExport.sReferenceClass,
		SvgExport.sNoteClass}; 


public final static HashMap<Integer, String> hmViewNodeClasses = new HashMap<Integer, String>();
static	{ hmViewNodeClasses.put(ILdCoreConstants.iLD_TYPE_NO_TYPE, SvgExport.sGeneralClass);
hmViewNodeClasses.put(ICoreConstants.LISTVIEW, SvgExport.sListViewClass);
hmViewNodeClasses.put(ICoreConstants.MAPVIEW, SvgExport.sMapViewClass);
hmViewNodeClasses.put(ICoreConstants.LDMAPVIEW, SvgExport.sActivityViewClass);
};

/*** End of Compendium standard nodes	**/
/** LD nodes and links	*****************************************************/
	/** Descriptive string used to denote identifiers and CSS classes for activity view groups in SVG output **/
	public	final static String sActivityViewClass =  "activityview";
	/** Descriptive string used to denote identifiers and CSS classes for activity nodes in SVG output **/
	public	final static String sActivityNodeClass =  "activitynode";
	/** Descriptive string used to denote identifiers and CSS classes for learner output formative nodes in SVG output **/
	public	final static String sLearnerOutputForamativeClass =  "learnerouputformative";
	/** Descriptive string used to denote identifiers and CSS classes for learner output other nodes in SVG output **/
	public	final static String sLearnerOutputOtherClass =  "learnerouputother";
	/** Descriptive string used to denote identifiers and CSS classes for learner output summative nodes in SVG output **/
	public	final static String sLearnerOutputSummativeClass =  "learnerouputsummative";
	/** Descriptive string used to denote identifiers and CSS classes for learning outcome nodes in SVG output **/
	public	final static String sLearningOutcomeClass =  "learningoutcome";
	/** Descriptive string used to denote identifiers and CSS classes for resource nodes in SVG output **/
	public	final static String sResourceClass =  "resource";
	/** Descriptive string used to denote identifiers and CSS classes for role nodes in SVG output **/
	public	final static String sRoleClass =  "role";
	/** Descriptive string used to denote identifiers and CSS classes for stop nodes in SVG output **/
	public	final static String sStopClass =  "stop";
	/** Descriptive string used to denote identifiers and CSS classes for task nodes in SVG output **/
	public	final static String sTaskClass =  "task";
	/** Descriptive string used to denote identifiers and CSS classes for tool nodes in SVG output **/
	public	final static String sToolClass =  "tool";
	/** Descriptive string used to denote identifiers and CSS classes for task links in SVG output **/
	public	final static String sTaskLinkClass =  "tasklink";
	/*** End of LD nodes and links	**/
	/** Sequence mapping nodes *****************************************************/
	/** Descriptive string used to denote identifiers and CSS classes for Sequence Mapping  intent nodes in SVG output **/
	public	final static String sSmIntentNodeClass =  "SMT_Intent";
	/** Descriptive string used to denote identifiers and CSS classes for Sequence Mapping  what is to be learnt  nodes in SVG output **/
	public	final static String sSmLearntNodeClass =  "SMT_Learnt";
	/** Descriptive string used to denote identifiers and CSS classes for Sequence Mapping output nodes in SVG output **/
	public	final static String sSmOutputNodeClass =  "SMT_Output";
	/** Descriptive string used to denote identifiers and CSS classes for Sequence Mapping resources nodes in SVG output **/
	public	final static String sSmResourcesNodeClass =  "SMT_Resources";
	/** Descriptive string used to denote identifiers and CSS classes for Sequence Mapping student activity nodes in SVG output **/
	public	final static String sSmStudentActivityNodeClass =  "SMT_SActivity";
	/** Descriptive string used to denote identifiers and CSS classes for Sequence Mapping support role nodes in SVG output **/
	public	final static String sSmSupportRoleNodeClass =  "SMT_Support";
	/** Descriptive string used to denote identifiers and CSS classes for Sequence Mapping media and tools nodes in SVG output **/
	public	final static String sSmMediaToolNodeClass =  "SMT_Tools";
	/** Descriptive string used to denote identifiers and CSS classes for Sequence Mapping clocknodes in SVG output **/
	public	final static String sSmClockNodeClass =  "SMT_Clock";
	/*** End of Sequence mapping  nodes **/
	/** Conditional nodes *****************************************************/
	/** Descriptive string used to denote identifiers and CSS classes for Conditional true nodes in SVG output **/
	public	final static String sConditionalTrueNodeClass =  "true";
	/** Descriptive string used to denote identifiers and CSS classes for Conditional false nodes in SVG output **/
	public	final static String sConditionalFalseNodeClass =  "false";
	/** Descriptive string used to denote identifiers and CSS classes for Conditional condition nodes in SVG output **/
	public	final static String sConditionalCondNodeClass =  "condition";
	/*** End of Conditional  nodes **/
	/** hmNodeClasses maps node type integers to class descriptors for nodes used 
	 * in maps or lists. For map or list groups, use hmViewNodeClasses.	**/
	public final static HashMap<Integer, String> hmNodeClasses = new HashMap<Integer, String>();
	static	{
	// @general' node type. This is used as a catch all if no match to the others listed below is found.
	hmNodeClasses.put(ILdCoreConstants.iLD_TYPE_NO_TYPE, SvgExport.sGeneralClass);
	// Standard nodes
	hmNodeClasses.put(ICoreConstants.LISTVIEW, SvgExport.sListNodeClass);
	hmNodeClasses.put(ICoreConstants.MAPVIEW, SvgExport.sMapNodeClass);
	hmNodeClasses.put(ICoreConstants.ISSUE, SvgExport.sIssueClass);
	hmNodeClasses.put(ICoreConstants.POSITION, SvgExport.sPositionClass);
	hmNodeClasses.put(ICoreConstants.ARGUMENT, SvgExport.sArgumentClass);
	hmNodeClasses.put(ICoreConstants.PRO, SvgExport.sProClass);
	hmNodeClasses.put(ICoreConstants.CON, SvgExport.sConClass);
	hmNodeClasses.put(ICoreConstants.DECISION, SvgExport.sDecisionClass);
	hmNodeClasses.put(ICoreConstants.REFERENCE, SvgExport.sReferenceClass);
	hmNodeClasses.put(ICoreConstants.NOTE, SvgExport.sNoteClass);
	hmNodeClasses.put(ICoreConstants.TRASHBIN, SvgExport.sTrashbinClass);
	// Learning design nodes
	hmNodeClasses.put(ILdCoreConstants.iLD_TYPE_ACTIVITY, SvgExport.sActivityNodeClass);
	hmNodeClasses.put(ILdCoreConstants.iLD_TYPE_ASSESSMENT_FORMATIVE, SvgExport.sLearnerOutputForamativeClass);
	hmNodeClasses.put(ILdCoreConstants.iLD_TYPE_ASSESSMENT, SvgExport.sLearnerOutputOtherClass);
	hmNodeClasses.put(ILdCoreConstants.iLD_TYPE_ASSESSMENT_SUMMATIVE, SvgExport.sLearnerOutputSummativeClass);
	hmNodeClasses.put(ILdCoreConstants.iLD_TYPE_LEARNING_OUTCOME, SvgExport.sLearningOutcomeClass);
	hmNodeClasses.put(ILdCoreConstants.iLD_TYPE_RESOURCE, SvgExport.sResourceClass);
	hmNodeClasses.put(ILdCoreConstants.iLD_TYPE_ROLE, SvgExport.sRoleClass);
	hmNodeClasses.put(ILdCoreConstants.iLD_TYPE_STOP, SvgExport.sStopClass);
	hmNodeClasses.put(ILdCoreConstants.iLD_TYPE_TASK, SvgExport.sTaskClass);
	hmNodeClasses.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL, SvgExport.sToolClass);
	// Conditional nodes
	hmNodeClasses.put(ILdCoreConstants.iLD_TYPE_CONDITIONAL_CONDITION, SvgExport.sConditionalCondNodeClass);
	hmNodeClasses.put(ILdCoreConstants.iLD_TYPE_CONDITIONAL_FALSE, SvgExport.sConditionalFalseNodeClass);
	hmNodeClasses.put(ILdCoreConstants.iLD_TYPE_CONDITIONAL_TRUE, SvgExport.sConditionalTrueNodeClass);
	};
	
	/** Descriptive string used to denote identifiers and CSS classes for links in SVG output **/
	public	final static String sLinkClass =  "link";

	/** hmLinkClasses maps link type integers to class descriptors for nodes used 
	 * in maps or lists. For map or list groups, use hmViewNodeClasses.	**/
	public final static HashMap<String, String> hmLinkClasses = new HashMap<String, String>();
	static	{
	// 'general' link type. This is used as a catch all if no match to the others listed below is found.
//	hmLinkClasses.put(ILdCoreConstants.iLD_TYPE_NO_TYPE, SvgExport.sGeneralClass);
	hmLinkClasses.put(ICoreConstants.RESPONDS_TO_LINK, SvgExport.sLinkClass);
	}
	
	/** String used as  identifier for Adobe filter in SVG icon definitions and SVG output **/
	public	final static String sAdobeFilterId = "Adobe_OpacityMaskFilter";
	
	/** Descriptive string used to denote identifiers and CSS classes for node adornments in  SVG output **/
	public	final static String sNodeDetails =  "nodeDetails";
	public	final static String sNodeLabel =  "nodelabel";
	public	final static String sIndicator =  "indicator";
	
	/** Various constants used to layout and space elements in SVG file	***/
	public	final static int iTransclusionIndicatorSpacerX = 5;
	public	final static int iTransclusionIndicatorSpacerY = 10;
	public	final static int iTransclusionIndicatorRowHeight = 14;
	/** The default Style sheet instruction	**/
	public	final static String sDefaultStleSheetURL = "href=\"http://compendiumld.open.ac.uk/svg/styles/styles-cld-svg.css\"";
	
	public static final String sJavascriptURL = "http://compendiumld.open.ac.uk/svg/scripts/cld.js";
	
	/**	Comment written just before  root <svg>  element	**/	
	public static final String sHeaderComment = "This SVG file was generated by " + ICoreConstants.sAPPNAME + " version " + ICoreConstants.sLdAPPVERSION +  " (http://compendiumld.open.ac.uk).";
	
	/**	Comment to indicate where the <defs> start 	**/	
	public static final String sIconMarkerDefsStartComment = "** Start of icon, marker and clip-path DEFS **";
	
	/**	Comment to indicate where the views (maps or lists) start	**/	
	public static final String sMapsStartComment = "Start of maps or list VIEWS";
	
	/**	Comment to indicate where the views (maps or lists) end	**/	
	public static final String sMapsEndComment = "End of maps or list VIEWS";
    /** The ViewPaneUI to export    **/
	
	/**	Comment to indicate where the <defs> end	**/	
	public static final String sIconMarkerDefsEndComment = "** End of icon, marker and clpi-path DEFS **";
    
	/**	Prefix string for an Svg id attribute indicates that it is 
	 * a link for the node from the map view identified by the id given after this prefix.
	 * The id = sTransclusionLinkNode.NodeId.sTransclusionLinkFromPrefix.FromViewId.sTransclusionLinkToPrefix.ToViewId **/	
	public static final String sTransclusionLinkNode = "Node.";
	
	/**	Prefix string to indicate that the part an Svg id attribute  that  is 
	 * a link to the map view identified by the id given after this prefix. **/	
	public static final String sTransclusionLinkToPrefix = ".tlto.";
	
	/**	Prefix string to indicate the part of  an Svg id attribute  that it is 
	 * a link from the map view identified by the id given after this prefix. **/	
	public static final String sTransclusionLinkFromPrefix = ".tlfrom.";
	
	/**	String to use in the identifier for the group that includes all the Svg elements representing the map and list
	 *  views, i.e the right hand panel in the 	final Svg output. **/
	public static final String sSvgViewPaneGroup = "ViewPane";
	
	/**	String to use in the identifier for the  group that includes all the Svg elements representing the navigation
	 *  panel, i.e the left hand panel in the 	final Svg output. **/
	public static final String sSvgNavPaneGroup = "NavPane"; 
	
	/**	String to use in the identifier for the  group that includes all the Svg elements representing the help
	 *  panel, i.e the bottom panel in the 	final Svg output. **/
	public static final String sSvgHelpPaneGroup = "helppane";
	
	/**	X position of the  left hand side of the navigation pane (to avoid getting cropped by browser window)	**/
	public static final int iNavPaneXPos = 1;
	
	/**	Width of the navigation pane	**/
	public static final int iNavPaneWidth = 160;
	
	/**	Height of one item (a View) in the navigation pane	**/
	public static final int iNavPaneItemHeight = 32;
	
	
	/**	Factor to scale the navigation pane so that it occupies less space **/
	private static final double dbScaleFactor = 0.5;
	
	/**	Height of the navigation pane (100% of whatever view box it's in)	**/
	public static final String NavPaneHeight = Math.round(100/dbScaleFactor)+"%";
	
	/**	How much the ViewPane panel needs	to be shifted to the left to alow for the NavPane	**/
	public static final int ViewPaneTranslateX = (int)(Math.round(iNavPaneWidth*dbScaleFactor) + 5);
	
	/**	Translation to move the ViewPane to the right of the Nav Pane	**/
	public static final String sSvgViewPaneTranslate = "translate(" + ViewPaneTranslateX + ",0)";
	
	/**	Prefix to indicate the string following the connector is the id of a level of Views presented in the navigation pane	**/
	public final static String sNavPaneViewLevelIdPrefix = "npvl";
	
	/**	Prefix to indicate the string following the connector is the id of a View presented in the navigation pane	**/
	public final static String sNavPaneViewIdPrefix = "npv";
	
	/**	Postfix to indicate the string following is a transclusion presented in the navigation pane. this stops duplicate ids being written into the Svg file.	**/
	public final static String sNavPaneViewIdTransPostfix= "-t";
	
	/**	Postfix to indicate the string following the connector is the id of a map View presented in the navigation pane	**/
	public final static String sNavPaneMapViewIdPostfix = "map";
	
	/**	Postfix to indicate the string following the connector is the id of a map View presented in the navigation pane	**/
	public final static String sNavPaneActivityViewIdPostfix = "activity";
	
	/**	Postfix to indicate the string following the connector is the id of a map View presented in the navigation pane	**/
	public final static String sNavPaneListViewIdPostfix = "list";
	
	/**	Javascript function calls **/	
	public static final String sShowMapViewFunction = "show_mapview(evt)";
	public static final String sStartShowTransclusionsFunction = "startShow_transclusions(evt)";
	public static final String sShowTransclusionsFunction = "show_transclusions(evt)";
	public static final String sHideTransclusionsFunction = "hide_transclusions(evt)";
	
	/**	Namespace prefix for CompendiumLD xml data 	**/
	public final static String sCldNsPf= "cld";
	
	/**	Namespace uri for CompendiumLD xml data 	**/
	public final static String sCldNsUri = "http://compendiumld.open.ac.uk/schemas/CompendiumLD";
	
	/**	The top level UIViewPane being exported **/
    private UIViewPane oUIViewPane = null;
    
    private SVGGraphics2D svgGenerator = null;
    private SVGLDGraphics2D svgLDGenerator = null;
    private SVGGeneratorContext ctx = null;;
    
    /** The SVGDocument being created by this export **/
    private SVGDocument svgDocument;
    
    /** The SVGDocument factory being used to create SVG doc fragments by this export **/
    private SVGDocument svgDocFactory;
    /**	The SVGDocument which holds all the standard Compendium icon definitions	**/
    private SVGDocument oCompendiumSvgIconsDoc;
    /**	The SVGDocument which holds all the standard Compendium icon definitions	**/
    private SVGDocument oCompendiumSvgLdIconsDoc;
    
    /**	The SVGDocument which holds all the SVG window element definitions	**/
    private SVGDocument oCompendiumSvgWindowElementsDoc;

    /**	The SVGDocument which holds all the SVG sequence mapping element definitions	**/
    private SVGDocument oCompendiumSvgSequenceMappingElementsDoc;
    
    /**	The SVGDocument which holds all the SVG conditional element definitions	**/
    private SVGDocument oCompendiumSvgConditionalElementsDoc;
    
    /** The XML stylesheet declaration to be used	**/
    private ProcessingInstruction oXMLStyleSheet;
    
    /** hsNodesCheck is a set containing all the nodes already 	written into the SVG document. Its purpose is to facilitate creation of transclusions. **/
    private HashSet<String> hsNodesCheck = new HashSet<String>();
    
    /** hmDocToNodesMap is a map between the SVGDocument being created and the set containing all the nodes already written into the SVG document. Its purpose is to facilitate creation of transclusions. **/
    public static HashMap<SVGDocument, HashSet<String>> hmDocToNodesMap = new HashMap<SVGDocument, HashSet<String>>();
    
    /** hsLinksCheck is a set containing all the links already 	written into the SVG document. Its purpose is to facilitate creation of transclusions. **/
    private HashSet<String> hsLinksCheck = new HashSet<String>();
    
    /** hmDocToLinksMap is a map between the SVGDocument being created and the set containing all the links already written into the SVG document. Its purpose is to facilitate creation of transclusions. **/
    public static HashMap<SVGDocument, HashSet<String>> hmDocToLinksMap = new HashMap<SVGDocument, HashSet<String>>();
    
    /** hsViewNodesWritten is a set containing all the View nodes whose top level children have been written into the SVG document. Its purpose is to facilitate creation of transclusions. **/
    private HashSet<String> hsViewNodesWritten = new HashSet<String>();
    
    /** hmDocToViewNodesMap is a map between the SVGDocument being created and the set containing all the View nodes already written into the SVG document. Its purpose is to facilitate creation of transclusions. **/
    public static HashMap<SVGDocument, HashSet<String>> hmDocToViewNodesMap = new HashMap<SVGDocument, HashSet<String>>(); 
    
    /** vtViewNodesToBeWritten is a Vecor containing all the View nodes whose top level children have NOT YET been written into the SVG document. View nodes are added to this set, and removed once their top level children have been written.. **/
    private Vector<String> vtViewNodesToBeWritten = new Vector<String>();
    
    /** hmDocToViewNodesToBeWrittenMap is a map between the SVGDocument being created and the set containing all the View nodes YET TO BE written into the SVG document.  **/
    public static HashMap<SVGDocument, Vector<String>> hmDocToViewNodesToBeWrittenMap = new HashMap<SVGDocument, Vector<String>>(); ;
    
	/**
	 * hmTrancludedNodeWrittenIntoView is a map between node ids whose SVG has
	 * already been written to the View that the SVG representation has been
	 * written for.
	 **/
    private HashMap<String, String> hmTrancludedNodeWrittenIntoView = new HashMap<String, String>();
    
    /**	iMaxWidth is the width of the widest map being exported to SVG. Its value
     * is set in the method generateSvg(String). 
    **/
    private int iMaxWidth = 0;
    /**	iMaxHeight is the height of the tallest map being exported to SVG. Its value
     * is set in the method generateSvg(String). 
    **/
    private int iMaxHeight = 0;

	/**
	 * hmDocToTransWrittenInView is a map between the SVGDocument being created
	 * and a map mapping node ids whose SVG has been written to the View that
	 * the SVG representation has been written for. hmDocToTransWrittenInView is
	 * used to facilitate the creation of SVG representations of transcluded nodes.
	 * A pseudo code representation of hmDocToTransWrittenInView is:
	 *  Map <SVGDocument to Map<IdOfTranscludedNode,  IdofViewWrittenInto>>.
	 *  Although there can be many transclusions of a node, i.e. a node can exist 
	 *  in many Views, there will only be on View containing the original SVG representation
	 *  of the node, hence HasMap>String, String> to represent this relationship.
	 **/
    public static HashMap<SVGDocument, HashMap<String, String>> hmDocToTransWrittenInView = new HashMap<SVGDocument, HashMap<String, String>>(); ;
    /** Note: to keep a record of all instances of transclusions, need to use this code
     *  public static HashMap<SVGDocument, HashMap<String, String>> hmDocToTransWrittenInView = new HashMap<SVGDocument, HashMap<String, HashSet<String>>>(); ;
     *  i.e. a hasher of Views in which the transclusion occurs. Howver, because at the moment, we only need
     *  to know that a transclusion has occurred, the HashMap is ok.
     */
   
    
	/**
	 * hmTrancludedLinkWrittenIntoView is a map between link ids whose SVG has
	 * already been written to the View that the SVG representation has been
	 * written for.
	 **/
    private HashMap<String, String> hmTrancludedLinkWrittenIntoView = new HashMap<String, String>();
	

	/**
	 * hmDocToTransLinkWrittenInView is a map between the SVGDocument being created
	 * and a map mapping link ids whose SVG has been written to the View that
	 * the SVG representation has been written for. hmDocToTransLinkWrittenInView is
	 * used to facilitate the creation of SVG representations of transcluded links.
	 * A pseudo code representation of hmDocToTransWrittenInView is:
	 *  Map <SVGDocument to Map<IdOfTranscludedLink, IdofViewWrittenInto>.
	 **/
    public static HashMap<SVGDocument, HashMap<String, String>> hmDocToTransLinkWrittenInView = new HashMap<SVGDocument, HashMap<String, String>>(); ;
    
    /**	Map between a Svg document and the id of the topmost View represented in the document	**/
    public static HashMap<SVGDocument,String> hmDocToTopLevelViewId = new HashMap<SVGDocument, String>();
    
    public static String getDefaultStyleSheetURL()	{
    	return sDefaultStleSheetURL;
    }
    
   /**
 * Method to import defs  - NOT CURRENTLY USED.  
 * @param oFromDef
 * @param oToDoc
 * @param sErrMessagePart
 * @return
 */
public static Node importDef(Element oFromDef, Document oToDoc, String sErrMessagePart)	{
	   Node oToDef = null;
    if (oFromDef != null)	{
    			  oToDef = oToDoc.importNode(oFromDef, true);
    			  return oToDef;
    			}
    	else	{System.out.println("Can not import " + sErrMessagePart);
    	return oToDef;
    	}
}
	/**
	 * Generate the standard icon <defs> needed. 
	 * Note that the parameter is a SVGDocument because that interface includes 
	 * DocumentEvents which may be necessary for later developments.	
	 * @param oSVGIconsDoc
	 */
	public static Element createSvgDefs(SVGDocument oSVGIconsDoc, Document oDoc)	{
		// Get the SVG Element <svg> 
		Element oSvgRootElement =  oDoc.getDocumentElement();
		
		Element oDefs = null;
		NodeList oDefsNodes = oSvgRootElement.getElementsByTagNameNS(SVGGraphics2D.SVG_NAMESPACE_URI, "defs");
		if (oDefsNodes.getLength() > 0)	{
			oDefs = (Element)oDefsNodes.item(0);
		}
		else	{
			oDefs = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_DEFS_TAG);
		}
		NodeList classNodeList = oSVGIconsDoc.getElementsByTagName("class");
		NodeList oGroupNodeList = oSVGIconsDoc.getElementsByTagName("g");
		//Get standard node classes
		Element oArgumentDefTemp = oSVGIconsDoc.getElementById(SvgExport.sArgumentClass);
		Element oConDefTemp = oSVGIconsDoc.getElementById(SvgExport.sConClass);
		Element oDecisionDefTemp = oSVGIconsDoc.getElementById(SvgExport.sDecisionClass);
		Element oIssueDefTemp = oSVGIconsDoc.getElementById(SvgExport.sIssueClass);
		Element oListDefTemp = oSVGIconsDoc.getElementById(SvgExport.sListNodeClass);
		Element oMapDefTemp = oSVGIconsDoc.getElementById(SvgExport.sMapNodeClass);
		Element oNoteDefTemp = oSVGIconsDoc.getElementById(SvgExport.sNoteClass);
		Element oProDefTemp = oSVGIconsDoc.getElementById(SvgExport.sProClass);
		Element oPositionDefTemp = oSVGIconsDoc.getElementById(SvgExport.sPositionClass);
		Element oReferenceDefTemp = oSVGIconsDoc.getElementById(SvgExport.sReferenceClass);
		Element oTrashBinDefTemp = oSVGIconsDoc.getElementById(SvgExport.sTrashbinClass);
		//Get markers (i.e. arrow heads)
		Element oArrowDefTemp = oSVGIconsDoc.getElementById(SvgExport.sArrowHead);
		// Get filter
		Element oFilterDefTemp = oSVGIconsDoc.getElementById(SvgExport.sAdobeFilterId);
	// Need to find these use elements and extract corresponding icon definitions <use xlink:href="#decision"
		// the ‘true’ means to perform a deep importation (the whole subtree)
		Node  oArgumentDef = null;	Node  oConDef = null;
		Node  oDecisionDef = null;	Node  oIssueDef = null;
		Node  oListDef = null;	Node  oMapDef = null;
		Node  oNoteDef = null;	Node  oProDef = null;
		Node  oPositionDef = null;	Node  oReferenceDef = null;
		Node  oArrowDef = null;		Node oTrashBinDef = null;
		Node oFilterDef = null;
				
		//Need to test if not null before importing!!!
if (oArgumentDefTemp != null)	{
			  oArgumentDef = oDoc.importNode(oArgumentDefTemp, true);
			  oDefs.appendChild(oArgumentDef);
			}
		else	{System.out.println("Can not import  Argument node definition from " + oSVGIconsDoc.getDocumentURI());} 
if (oConDefTemp != null)	{
			oConDef = oDoc.importNode(oConDefTemp, true);
			oDefs.appendChild(oConDef);
		}
		else	{System.out.println("Can not import Con node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oDecisionDefTemp != null)	{
			oDecisionDef = oDoc.importNode(oDecisionDefTemp, true);
			oDefs.appendChild(oDecisionDef );
		}
		else	{System.out.println("Can not import Decision node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oIssueDefTemp != null)	{
			oIssueDef = oDoc.importNode(oIssueDefTemp, true);
			oDefs.appendChild(oIssueDef );
		}
		else	{System.out.println("Can not import Issue node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oListDefTemp != null)	{
			oListDef = oDoc.importNode(oListDefTemp, true);
			oDefs.appendChild(oListDef );
		}
		else	{System.out.println("Can not import List node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oMapDefTemp != null)	{
			oMapDef = oDoc.importNode(oMapDefTemp, true);
			oDefs.appendChild(oMapDef );
		}
		else	{System.out.println("Can not import Map node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oNoteDefTemp != null)	{
			oNoteDef = oDoc.importNode(oNoteDefTemp, true);
			oDefs.appendChild(oNoteDef  );
		}
		else	{System.out.println("Can not import Decision node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oProDefTemp != null)	{
			oProDef = oDoc.importNode(oProDefTemp, true);
			oDefs.appendChild(oProDef  );
		}
		else	{System.out.println("Can not import Pro node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oPositionDefTemp  != null)	{
			oPositionDef = oDoc.importNode(oPositionDefTemp , true);
			oDefs.appendChild(oPositionDef);
		}
		else	{System.out.println("Can not import Position node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oReferenceDefTemp  != null)	{
			oReferenceDef = oDoc.importNode(oReferenceDefTemp , true);
			oDefs.appendChild(oReferenceDef);
		}
		else	{System.out.println("Can not import Reference node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oTrashBinDefTemp  != null)	{
			oTrashBinDef = oDoc.importNode(oTrashBinDefTemp , true);
			oDefs.appendChild(oTrashBinDef);
		}
		else	{System.out.println("Can not import Trashbin node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oArrowDefTemp  != null)	{
			oArrowDef = oDoc.importNode(oArrowDefTemp , true);
			oDefs.appendChild(oArrowDef);
		}
		else	{System.out.println("Can not import Arrow head definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oFilterDefTemp  != null)	{
			oFilterDef = oDoc.importNode(oFilterDefTemp , true);
			oDefs.appendChild(oFilterDef);
		}
		else	{System.out.println("Can not import Arrow head definition from " + oSVGIconsDoc.getDocumentURI());}
			
		return oDefs;
	}

	/**
	 * Generate the learning design icon <defs> needed. 
	 * Note that the parameter is a SVGDocument because that interface includes 
	 * DocumentEvents which may be necessary for later developments.	
	 * @param oSVGIconsDoc  The SVGDocument which holds all the standard Compendium icon definitions
	 * @param oDoc	the  document being created that the icons are copied into
	 */
	public static Element createSvgLdDefs(SVGDocument oSVGIconsDoc, Document oDoc)	{
		// Get the SVG Element <svg> 
		Element oSvgRootElement =  oDoc.getDocumentElement();
		
		Element oDefs = null;
		NodeList oDefsNodes = oSvgRootElement.getElementsByTagNameNS(SVGGraphics2D.SVG_NAMESPACE_URI, "defs");
		if (oDefsNodes.getLength() > 0)	{
			oDefs = (Element)oDefsNodes.item(0);
		}
		else	{
			oDefs = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_DEFS_TAG);
		}
		NodeList classNodeList = oSVGIconsDoc.getElementsByTagName("class");
		NodeList oGroupNodeList = oSVGIconsDoc.getElementsByTagName("g");
		//Get standard node classes
		Element oActivityNodeDefTemp = oSVGIconsDoc.getElementById(SvgExport.sActivityNodeClass);
		Element oLearnerOutputFormativeDefTemp = oSVGIconsDoc.getElementById(SvgExport.sLearnerOutputForamativeClass);
		Element oLearnerOutputOtherDefTemp = oSVGIconsDoc.getElementById(SvgExport.sLearnerOutputOtherClass);
		Element oLearnerOutputSummativeDefTemp = oSVGIconsDoc.getElementById(SvgExport.sLearnerOutputSummativeClass);
		Element oLearningOutcomeDefTemp = oSVGIconsDoc.getElementById(SvgExport.sLearningOutcomeClass);
		Element oResourceDefTemp = oSVGIconsDoc.getElementById(SvgExport.sResourceClass);
		Element oRoleDefTemp = oSVGIconsDoc.getElementById(SvgExport.sRoleClass);
		Element oStopDefTemp = oSVGIconsDoc.getElementById(SvgExport.sStopClass);
		//Element oStopDefTemp = oSVGIconsDoc.getElementById(SvgExport.sStopClass);
		Element oTaskDefTemp = oSVGIconsDoc.getElementById(SvgExport.sTaskClass);
		Element oToolDefTemp = oSVGIconsDoc.getElementById(SvgExport.sToolClass);
		
	// Need to find these use elements and extract corresponding icon definitions <use xlink:href="#decision"
		// the ‘true’ means to perform a deep importation (the whole subtree)
		Node  oActivityNodeDef = null;	Node  oLearnerOutputFormativeDef = null;
		Node  oLearnerOutputOtherDef = null;	Node  oLearnerOutputSummative = null;
		Node  oLearningOutcomeDef = null;	Node  oResourceDef = null;
		Node  oRoleDef = null;	Node  oStopDef = null;
		Node  oTaskDef = null;	Node  oToolDef = null;
		Node  oArrowDef = null;
		//Need to test if not null before importing!!!
		if (oActivityNodeDefTemp != null)	{
			  oActivityNodeDef = oDoc.importNode(oActivityNodeDefTemp, true);
			  oDefs.appendChild(oActivityNodeDef);
			}
		else	{System.out.println("Can not import Activity node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oLearnerOutputFormativeDefTemp != null)	{
			oLearnerOutputFormativeDef = oDoc.importNode(oLearnerOutputFormativeDefTemp, true);
			oDefs.appendChild(oLearnerOutputFormativeDef);
		}
		else	{System.out.println("Can not import Learner Output Formative node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oLearnerOutputOtherDefTemp != null)	{
			oLearnerOutputOtherDef = oDoc.importNode(oLearnerOutputOtherDefTemp, true);
			oDefs.appendChild(oLearnerOutputOtherDef );
		}
		else	{System.out.println("Can not import Learner Output Other node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oLearnerOutputSummativeDefTemp != null)	{
			oLearnerOutputSummative = oDoc.importNode(oLearnerOutputSummativeDefTemp, true);
			oDefs.appendChild(oLearnerOutputSummative );
		}
		else	{System.out.println("Can not import Learner Output Summative node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oLearningOutcomeDefTemp != null)	{
			oLearningOutcomeDef = oDoc.importNode(oLearningOutcomeDefTemp, true);
			oDefs.appendChild(oLearningOutcomeDef );
		}
		else	{System.out.println("Can not import Learning Outcome node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oResourceDefTemp != null)	{
			oResourceDef = oDoc.importNode(oResourceDefTemp, true);
			oDefs.appendChild(oResourceDef );
		}
		else	{System.out.println("Can not import Resource node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oRoleDefTemp != null)	{
			oRoleDef = oDoc.importNode(oRoleDefTemp, true);
			oDefs.appendChild(oRoleDef  );
		}
		else	{System.out.println("Can not import Role node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oStopDefTemp != null)	{
			oStopDef = oDoc.importNode(oStopDefTemp, true);
			oDefs.appendChild(oStopDef  );
		}
		else	{System.out.println("Can not import Stop node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oTaskDefTemp  != null)	{
			oTaskDef = oDoc.importNode(oTaskDefTemp , true);
			oDefs.appendChild(oTaskDef);
		}
		else	{System.out.println("Can not import Task node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oToolDefTemp  != null)	{
			oToolDef = oDoc.importNode(oToolDefTemp , true);
			oDefs.appendChild(oToolDef);
		}
		else	{System.out.println("Can not import Tool node definition from " + oSVGIconsDoc.getDocumentURI());}

				
		return oDefs;
	}
	
	/**
	 * Generate the  <defs> needed for the help part of the window. 
	 * Note that the parameter is a SVGDocument because that interface includes 
	 * DocumentEvents which may be necessary for later developments.	
	 * @param oSVGIconsDoc
	 */
	public static Element createSvgWindowDefs(SVGDocument oSVGIconsDoc, Document oDoc)	{
		// Get the SVG Element <svg> 
		Element oSvgRootElement =  oDoc.getDocumentElement();
		
		Element oDefs = null;
		NodeList oDefsNodes = oSvgRootElement.getElementsByTagNameNS(SVGGraphics2D.SVG_NAMESPACE_URI, "defs");
		if (oDefsNodes.getLength() > 0)	{
			oDefs = (Element)oDefsNodes.item(0);
		}
		else	{
			oDefs = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_DEFS_TAG);
		}
		NodeList classNodeList = oSVGIconsDoc.getElementsByTagName("class");
		NodeList oGroupNodeList = oSVGIconsDoc.getElementsByTagName("g");
		//Get standard node classes
		Element oArrowDefTemp = oSVGIconsDoc.getElementById("fullscreenarrow");
		Element oGradientDefTemp = oSVGIconsDoc.getElementById("PurpleGradient");
		
		
	// Need to find these use elements and extract corresponding icon definitions <use xlink:href="#decision"
		// the ‘true’ means to perform a deep importation (the whole subtree)
		Node  oArrowDef = null;	Node  oGradientDef = null;
		
		//Need to test if not null before importing!!!
		if (oArrowDefTemp != null)	{
			oArrowDef = oDoc.importNode(oArrowDefTemp, true);
			  oDefs.appendChild(oArrowDef);
			}
		else	{System.out.println("Can not import Arrow  definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oGradientDefTemp != null)	{
			oGradientDef = oDoc.importNode(oGradientDefTemp, true);
			oDefs.appendChild(oGradientDef);
		}
		else	{System.out.println("Can not import Gradient definition from " + oSVGIconsDoc.getDocumentURI());}
						
		return oDefs;
	}
	
	
	public static Element createSvgHelpPanel(SVGDocument oSVGIconsDoc, Document oDoc)	{
		
		Element oHelpPaneGroupTemp = oSVGIconsDoc.getElementById(SvgExport.sSvgHelpPaneGroup);
		//Get standard node classes
			
	// Need to find these use elements and extract corresponding icon definitions <use xlink:href="#decision"
		// the ‘true’ means to perform a deep importation (the whole subtree)
		Node  oHelpPaneGroup = null;	
		
		//Need to test if not null before importing!!!
		if (oHelpPaneGroupTemp != null)	{
			oHelpPaneGroup = oDoc.importNode(oHelpPaneGroupTemp, true);
			}
		else	{System.out.println("Can not import Help Pane Group <g id=\"helpane\"> from " + oSVGIconsDoc.getDocumentURI());}
					
		return (Element)oHelpPaneGroup;
	}
	
	/**
	 * Generate the learning design sequence mapping icon <defs> needed. 
	 * Note that the parameter is a SVGDocument because that interface includes 
	 * DocumentEvents which may be necessary for later developments.	
	 * @param oSVGIconsDoc  The SVGDocument which holds all the sequence mapping  icon definitions
	 * @param oDoc	the  document being created that the icons are copied into
	 */
	public static Element createSvgSequenceMappingDefs(SVGDocument oSVGIconsDoc, Document oDoc)	{
		// Get the SVG Element <svg> 
		Element oSvgRootElement =  oDoc.getDocumentElement();
		
		Element oDefs = null;
		NodeList oDefsNodes = oSvgRootElement.getElementsByTagNameNS(SVGGraphics2D.SVG_NAMESPACE_URI, "defs");
		if (oDefsNodes.getLength() > 0)	{
			oDefs = (Element)oDefsNodes.item(0);
		}
		else	{
			oDefs = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_DEFS_TAG);
		}
		NodeList classNodeList = oSVGIconsDoc.getElementsByTagName("class");
		NodeList oGroupNodeList = oSVGIconsDoc.getElementsByTagName("g");
		//Get standard node classes
		Element oIntentNodeDefTemp = oSVGIconsDoc.getElementById(SvgExport.sSmIntentNodeClass);
		Element oLearntDefTemp = oSVGIconsDoc.getElementById(SvgExport.sSmLearntNodeClass);
		Element oOutputDefTemp = oSVGIconsDoc.getElementById(SvgExport.sSmOutputNodeClass);
		Element oResourceDefTemp = oSVGIconsDoc.getElementById(SvgExport.sSmResourcesNodeClass);
		Element oStudentActivityDefTemp = oSVGIconsDoc.getElementById(SvgExport.sSmStudentActivityNodeClass);
		Element oSupportRoleDefTemp = oSVGIconsDoc.getElementById(SvgExport.sSmSupportRoleNodeClass);
		Element oMediaToolDefTemp = oSVGIconsDoc.getElementById(SvgExport.sSmMediaToolNodeClass);
		Element oClockDefTemp = oSVGIconsDoc.getElementById(SvgExport.sSmClockNodeClass);
		
	// Need to find these use elements and extract corresponding icon definitions <use xlink:href="#decision"
		// the ‘true’ means to perform a deep importation (the whole subtree)
		Node  oIntentNodeDef = null;	Node  oLearntDef = null;
		Node  oOutputDef = null;		Node  oStudentActivityDef = null;
		Node  oSupportRoleDef = null;	Node  oResourceDef = null;
		Node  oToolDef = null;			Node  oClockDef = null;
		//Need to test if not null before importing!!!
		if (oIntentNodeDefTemp != null)	{
			  oIntentNodeDef = oDoc.importNode(oIntentNodeDefTemp, true);
			  oDefs.appendChild(oIntentNodeDef);
			}
		else	{System.out.println("Can not import Intent node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oLearntDefTemp != null)	{
			oLearntDef = oDoc.importNode(oLearntDefTemp, true);
			oDefs.appendChild(oLearntDef);
		}
		else	{System.out.println("Can not import Learnt node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oOutputDefTemp != null)	{
			oOutputDef = oDoc.importNode(oOutputDefTemp, true);
			oDefs.appendChild(oOutputDef );
		}
		else	{System.out.println("Can not import  Output  node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oStudentActivityDefTemp != null)	{
			oStudentActivityDef = oDoc.importNode(oStudentActivityDefTemp, true);
			oDefs.appendChild(oStudentActivityDef );
		}
		else	{System.out.println("Can not import Student Activity node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oSupportRoleDefTemp != null)	{
			oSupportRoleDef = oDoc.importNode(oSupportRoleDefTemp, true);
			oDefs.appendChild(oSupportRoleDef );
		}
		else	{System.out.println("Can not import Support Role node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oResourceDefTemp != null)	{
			oResourceDef = oDoc.importNode(oResourceDefTemp, true);
			oDefs.appendChild(oResourceDef );
		}
		else	{System.out.println("Can not import Resource node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oMediaToolDefTemp  != null)	{
			oToolDef = oDoc.importNode(oMediaToolDefTemp , true);
			oDefs.appendChild(oToolDef);
		}
		else	{System.out.println("Can not import Media and Tool node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oClockDefTemp  != null)	{
			oClockDef = oDoc.importNode(oClockDefTemp , true);
			oDefs.appendChild(oClockDef);
		}
		else	{System.out.println("Can not import Clock node definition from " + oSVGIconsDoc.getDocumentURI());}
				
		return oDefs;
	}
	
	/**
	 * Generate the learning design conditional icon <defs> needed. 
	 * Note that the parameter is a SVGDocument because that interface includes 
	 * DocumentEvents which may be necessary for later developments.	
	 * @param oSVGIconsDoc  The SVGDocument which holds all the conditional icon definitions
	 * @param oDoc	the  document being created that the icons are copied into
	 */
	public static Element createSvgLdConditionalDefs(SVGDocument oSVGIconsDoc, Document oDoc)	{
		// Get the SVG Element <svg> 
		Element oSvgRootElement =  oDoc.getDocumentElement();
		
		Element oDefs = null;
		NodeList oDefsNodes = oSvgRootElement.getElementsByTagNameNS(SVGGraphics2D.SVG_NAMESPACE_URI, "defs");
		if (oDefsNodes.getLength() > 0)	{
			oDefs = (Element)oDefsNodes.item(0);
		}
		else	{
			oDefs = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_DEFS_TAG);
		}
		
		//Get standard node classes
		Element oConditionNodeDefTemp = oSVGIconsDoc.getElementById(SvgExport.sConditionalCondNodeClass);
		Element oFalseDefTemp = oSVGIconsDoc.getElementById(SvgExport.sConditionalFalseNodeClass);
		Element oTrueDefTemp = oSVGIconsDoc.getElementById(SvgExport.sConditionalTrueNodeClass);
		/**
		Element oResourceDefTemp = oSVGIconsDoc.getElementById(SvgExport.sSmResourcesNodeClass);
		Element oStudentActivityDefTemp = oSVGIconsDoc.getElementById(SvgExport.sSmStudentActivityNodeClass);
		Element oSupportRoleDefTemp = oSVGIconsDoc.getElementById(SvgExport.sSmSupportRoleNodeClass);
		Element oMediaToolDefTemp = oSVGIconsDoc.getElementById(SvgExport.sSmMediaToolNodeClass);
		Element oClockDefTemp = oSVGIconsDoc.getElementById(SvgExport.sSmClockNodeClass);
		**/
	// Need to find these use elements and extract corresponding icon definitions <use xlink:href="#decision"
		// the ‘true’ means to perform a deep importation (the whole subtree)
		Node  oConditiontNodeDef = null;	Node  oFalseDef = null;
		Node  oTrueDef = null;		
		//Need to test if not null before importing!!!
		if (oConditionNodeDefTemp != null)	{
			  oConditiontNodeDef = oDoc.importNode(oConditionNodeDefTemp, true);
			  oDefs.appendChild(oConditiontNodeDef);
			}
		else	{System.out.println("Can not import Condition node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oFalseDefTemp != null)	{
			oFalseDef = oDoc.importNode(oFalseDefTemp, true);
			oDefs.appendChild(oFalseDef);
		}
		else	{System.out.println("Can not import False node definition from " + oSVGIconsDoc.getDocumentURI());}
		if (oTrueDefTemp != null)	{
			oTrueDef = oDoc.importNode(oTrueDefTemp, true);
			oDefs.appendChild(oTrueDef );
		}
		else	{System.out.println("Can not import  True  node definition from " + oSVGIconsDoc.getDocumentURI());}
	
				
		return oDefs;
	}
	
	/**
	 * Open the Compendium SVG icons file at the  location given by 
	 * the parameter uriPath. If the file cannot be opened, null is
	 * returned.
	 * @param uriPath - String 
	 * @return the SVGDocument file, or null if unsuccessful.
	 */
	
	public static SVGDocument openCompendiumIconsFile(String uriPath)	{
		Document doc = null;
		String uri = "";
		try { 
		String parser = XMLResourceDescriptor.getXMLParserClassName(); 
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser); 
		 
		File fileCheck = new File(uriPath);
		if (fileCheck.exists())	{
			/** Have to convert toURI to reach the file	**/
			uri = fileCheck.toURI().toString();
		}
		doc = f.createDocument(uri); 
		return ((SVGDocument)doc);
		} 
		catch (IOException ex) { 
			System.out.println("Error opening Compendium SVG icons fiile " + ex.getMessage());
			 return ((SVGDocument)doc);
			}
		}
	
    
    /**
     * Constructor
     * @param aUIviewPane - the UIViewPane displaying the root View of the export
     * @param sFileName - the name of the file into which the SVG representation will be written
     * @param bPrettyPrint - boolean which determines the method for rendering 
     * the SVG. This should be set to false for structured output, true for 
     * non-structured Batik generated output. Note the pretty print option is not 
     * currently implemented, so bPrettyPrint should be set to false.
     */
    public SvgExport(UIViewPane aUIviewPane, String sFileName, boolean bPrettyPrint) {
    	//Open the file containing the standard Compendium icons and create a SVG doc to store the data
    	oCompendiumSvgIconsDoc = SvgExport.openCompendiumIconsFile(UIImages.sSVGCOMPENDIUMICONSFILE);
    	//Open the file containing the  Compendium learning design icons and create a SVG doc to store the data
    	oCompendiumSvgLdIconsDoc = SvgExport.openCompendiumIconsFile(UIImages.sSVGCOMPENDIUMLDICONSFILE);
    	//Open the file containing the  Compendium window elements and create a SVG doc to store the data
    	oCompendiumSvgWindowElementsDoc = SvgExport.openCompendiumIconsFile(UIImages.sSVGCOMPENDIUMLDWINDOWELEMENTSFILE);
        oUIViewPane = aUIviewPane;
        //Open the file containing the  Compendium sequence mapping icons and create a SVG doc to store the data
    	oCompendiumSvgSequenceMappingElementsDoc = SvgExport.openCompendiumIconsFile(UIImages.sSVGCOMPENDIUMLDSEQUENCEMAPPINGICONSFILE);
    	//Open the file containing the  Compendium conditionalicons and create a SVG doc to store the data
    	oCompendiumSvgConditionalElementsDoc = SvgExport.openCompendiumIconsFile(UIImages.sSVGCOMPENDIUMLDCONDITIONALICONSFILE);
        // Get a DOMImplementation.
        DOMImplementation domImpl =
        	SVGDOMImplementation.getDOMImplementation();
        
        
        // Create an SVG DOM document
        String svgNS = "http://www.w3.org/2000/svg";
        try    {    
        // Create a SVGDocument to be used to generate the SVG output,  and a factory to be used in the pretty print paint methods
        svgDocFactory = (SVGDocument)domImpl.createDocument(svgNS, "svg", null);
        svgDocument = (SVGDocument)domImpl.createDocument(svgNS, "svg", null);
        
        
        // Get the root elemnt of the doc o be output
        Element oSvgDocRoot = svgDocument.getDocumentElement();
        //oSvgDocRoot.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_WIDTH_ATTRIBUTE, "100%");
        //oSvgDocRoot.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_HEIGHT_ATTRIBUTE, "100%");
        // Have to use the non-namespace version of this method so as to overwrte the default value of the version attribute 
        oSvgDocRoot.setAttribute(SVGGraphics2D.SVG_VERSION_ATTRIBUTE, "1.1");
        Element oJavascript = svgDocument.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI,SVGGraphics2D.SVG_SCRIPT_TAG);
        oJavascript.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_TYPE_ATTRIBUTE, SVGGraphics2D.SVG_SCRIPT_TYPE_DEFAULT_VALUE);
        oJavascript.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.XLINK_HREF_QNAME, sJavascriptURL);
        oXMLStyleSheet = svgDocument.createProcessingInstruction("xml-stylesheet", "type=\"text/css\" " + SvgExport.getDefaultStyleSheetURL() );
       
        svgDocument.insertBefore(oXMLStyleSheet,oSvgDocRoot);
        oSvgDocRoot.appendChild(oJavascript);
       
        // Initialise the map between the SVG document and the (currently empty) set of nodes written to the document
       SvgExport.hmDocToNodesMap.put(svgDocument, this.getHsNodesCheck());
       
       
       // Initialise the map between the SVG document and the (currently empty) set of links written to the document
       SvgExport.hmDocToLinksMap.put(svgDocument, this.getHsLinksCheck());
       
       // Initialise the map between the SVG document and the (currently empty) set of View nodes already written to the document
       SvgExport.hmDocToViewNodesMap.put(svgDocument, this.getHsViewNodesCheck());
       
       // Initialise the map between the SVG document and the (currently empty) set of transcluded  nodes already written to View specified
       SvgExport.hmDocToTransWrittenInView.put(svgDocument, this.getHmTrancludedNodeWrittenIntoView());
      
    // Initialise the map between the SVG document and the (currently empty) set of transcluded  links already written to View specified
       SvgExport.hmDocToTransLinkWrittenInView.put(svgDocument, this.getHmTrancludedLinkWrittenIntoView());
      
       // Initialise the map between the SVG document and the id of the top most view
       SvgExport.hmDocToTopLevelViewId.put(svgDocument, aUIviewPane.getView().getId());
    /**	IS THIS VARIABLE NEEDED ANYMORE????? 
     * Initialise the set of Views to be written with the topmost  View, then
     *  initialise the map between the SVG document and this set of View nodes to 
     *  be written to the document. */
       this.getVtViewNodesToBeWritten().add(oUIViewPane.getView().getId());
       SvgExport.hmDocToViewNodesToBeWrittenMap.put(svgDocument, this.getVtViewNodesToBeWritten());
       
        if (bPrettyPrint)
        	this.generatePrettySvg(sFileName);
        else
        	this.generateSvg(sFileName);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
        	System.out.println("Error :" +  e.getMessage() );
        	 e.printStackTrace();
        }

       
    }
    
    public void generateSvg(String sFile)    {
    	Element oSvgDocRoot = svgDocument.getDocumentElement();
    	//Set the default font size
    	oSvgDocRoot.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_FONT_SIZE_ATTRIBUTE, Integer.toString(Model.FONTSIZE_DEFAULT));
    	//oSvgDocRoot.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.XMLNS_PREFIX + ':' + SvgExport.sCldNsPf, SvgExport.sCldNsUri);
    	Comment oComment = svgDocument.createComment(SvgExport.sHeaderComment);
    	svgDocument.insertBefore(oComment, oSvgDocRoot);
         // Append the icon definitions to the <svg> document element
         Element oDefs = SvgExport.createSvgDefs(oCompendiumSvgIconsDoc,  svgDocument);
         Element oLdDefs = SvgExport.createSvgLdDefs(oCompendiumSvgLdIconsDoc,  svgDocument);
         Element oWindowDefs = SvgExport.createSvgWindowDefs(oCompendiumSvgWindowElementsDoc,  svgDocument);
         SVGElement oHelpPaneGroup = (SVGElement)SvgExport.createSvgHelpPanel(oCompendiumSvgWindowElementsDoc, svgDocument);
         Element oSequenceMapDefs = SvgExport.createSvgSequenceMappingDefs(oCompendiumSvgSequenceMappingElementsDoc,  svgDocument);
         Element oConditionalDefs = SvgExport.createSvgLdConditionalDefs(oCompendiumSvgConditionalElementsDoc,  svgDocument);
         oComment = svgDocument.createComment(SvgExport.sIconMarkerDefsStartComment);
         oSvgDocRoot.appendChild(oDefs);
         oSvgDocRoot.appendChild(oLdDefs);
         oSvgDocRoot.appendChild(oWindowDefs);
         oSvgDocRoot.appendChild(oSequenceMapDefs);
         oSvgDocRoot.appendChild(oConditionalDefs);
         Element oClipPathDef = generateNavigationPaneClipPathDefs(svgDocument);
         oSvgDocRoot.appendChild(oClipPathDef);
         oSvgDocRoot.insertBefore(oComment, oDefs);
         oSvgDocRoot.appendChild(svgDocument.createComment(SvgExport.sIconMarkerDefsEndComment));
         oSvgDocRoot.appendChild(svgDocument.createComment(SvgExport.sMapsStartComment));
         
        //Generate the SVG representation of the UIViewPane
       Vector<String> vtViewNodesToBeWritten = SvgExport.getHmDocToViewNodesToBeWrittenMap().get(svgDocument);
      // Iterator<String> oViewNodesToBeWrittenIt = this.vtViewNodesToBeWritten.iterator();
       NodeService oNodeService = (NodeService) oUIViewPane.getView().getModel().getNodeService();
       PCSession session = oUIViewPane.getView().getModel().getSession();
       Vector<View> vtChildViews = new Vector<View>();
       try {
    	   vtChildViews = (Vector<View>)oNodeService.getAllChildViews(session, oUIViewPane.getView().getId());
	} catch (SQLException e1) {
		System.out.println("Exception: (SvgExport - oNodeService.getAllChildViews View" + oUIViewPane.getView().getId()+ " ) :"  + e1.getMessage());
		e1.printStackTrace();
	}
       Vector<View> vtChildViewsOrig = new Vector<View>();
       
       Iterator<View> oViewNodesToBeWrittenIt = vtChildViews.iterator(); 
       UIViewFrame frame =  oUIViewPane.getViewFrame();
       Element oViewPaneGroup = svgDocument.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_G_TAG);
      /** Don't use oViewPaneGroup.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_ID_ATTRIBUTE, SvgExport.sSvgViewPaneGroup);
       * because the getElementById(String id) returns null	**/
      oViewPaneGroup.setAttribute(SVGGraphics2D.SVG_ID_ATTRIBUTE, SvgExport.sSvgViewPaneGroup);
      oViewPaneGroup.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_TRANSFORM_ATTRIBUTE, SvgExport.sSvgViewPaneTranslate );
      Node oTemp =  oSvgDocRoot.appendChild(oViewPaneGroup);
       oUIViewPane.generateSVG(svgDocument);
      Dimension oViewPaneSize = oUIViewPane.calculateSize();
      this.setMaxHeight( oViewPaneSize.height);
      this.setMaxWidth(oViewPaneSize.width);
    while (oViewNodesToBeWrittenIt.hasNext())	{
    //   while (oViewNodesToBeWrittenItOrig.hasNext())	{
    	   String sViewId = oViewNodesToBeWrittenIt.next().getId();
    	  // String sViewId = oViewNodesToBeWrittenItOrig.next().getId();
    		   View oView = View.getView(sViewId);
    		   frame = ProjectCompendium.APP.addViewToDesktop(oView, oView.getLabel());
    		   frame.validate();
    		   if (View.isListType(oView.getType()))	{
    			   UIList oCurrentUIList = ((UIListViewFrame)frame).getUIList(); 
    			   oCurrentUIList.generateSVG(svgDocument);
    		   }
    		   else	{
    			   UIViewPane oCurrentUIViewpane = ((UIMapViewFrame)frame).getViewPane();    		
        		   oCurrentUIViewpane.generateSVG(svgDocument);
        		   // Update max width and height needed 
        		   oViewPaneSize = oCurrentUIViewpane.calculateSize();
        		   if (oViewPaneSize.width > iMaxWidth)
        			   iMaxWidth = oViewPaneSize.width;
        		   if (oViewPaneSize.height > iMaxHeight)
        			   iMaxHeight = oViewPaneSize.height;
    		   }
    		   
    		   try {frame.setClosed(true);}
    		   catch(Exception io){
    			   System.out.println("error closing open View " +frame.getView().getLabel());
    		   }
    	   }
    
    	int iBorderRectWidth = iMaxWidth + 4; //+2 to allow for variations in font sizes and rounding (4 is a guess!)
    	int iWidthViewPaneAndNavPane = iMaxWidth + SvgExport.ViewPaneTranslateX; 
    	// Do not include width and height attributes because Safari will not handle the embedded image correctly. See http://www.open.ac.uk/blogs/brasherblog/?p=714
    	//oSvgDocRoot.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_WIDTH_ATTRIBUTE, Integer.toString(iMaxWidth));
        //oSvgDocRoot.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_HEIGHT_ATTRIBUTE, Integer.toString(iMaxHeight+50));
        
        oSvgDocRoot.appendChild(svgDocument.createComment(SvgExport.sMapsEndComment));
        Element oMapPaneBorderRect = svgDocument.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_RECT_TAG);
        /** Add border rect to View pane	**/
		oMapPaneBorderRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_X_ATTRIBUTE, "-2");
		oMapPaneBorderRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_Y_ATTRIBUTE, "0" );
		oMapPaneBorderRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_WIDTH_ATTRIBUTE, Integer.toString(iBorderRectWidth));
		oMapPaneBorderRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_HEIGHT_ATTRIBUTE, Integer.toString(iMaxHeight));
		oMapPaneBorderRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_CLASS_ATTRIBUTE, "navpaneborderrect");
		oMapPaneBorderRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_RX_ATTRIBUTE, "12");
		//oSvgDocRoot.appendChild(oMapPaneBorderRect);
		svgDocument.getElementById(SvgExport.sSvgViewPaneGroup).appendChild(oMapPaneBorderRect);
		/**	End of Add border rect to View pane	**/
        /**	Create the navigation panel	**/
        this.createNavigationPane();
        /** Navigation panel created	**/
       
        oHelpPaneGroup.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_TRANSFORM_ATTRIBUTE, "translate(0," + iMaxHeight + ")" );
        oSvgDocRoot.appendChild(svgDocument.createComment("Start of help pane"));
        oSvgDocRoot.appendChild(oHelpPaneGroup);
        oSvgDocRoot.appendChild(svgDocument.createComment("End of help pane"));
        Element oFulScreenGroup = svgDocument.getElementById("fullscreen");
        oFulScreenGroup.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_TRANSFORM_ATTRIBUTE, "translate(" + iWidthViewPaneAndNavPane + ", 0)" );
      //Add square ViewBox; note that the help pane is 50 px high
        int iDim = Math.max(iMaxWidth, (iMaxHeight + 50));
        oSvgDocRoot.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_VIEW_BOX_ATTRIBUTE, "0 0 " + iWidthViewPaneAndNavPane + " " + (iMaxHeight + 50));

        /**  The need to put explicit dimensions  into font size settings so svg files are rendered 
         * ok by Firefox (eg.  style="font-size:18px;) is now handled in the CSS style sheet */
        
        //Generate CompendiumLD XML
        ArrayList<Link> oLinks = this.generateLinkList();
        ArrayList<NodePosition> oNodePositions = this.generateNpList();
        LdXMLGenerator oXmlGenerator = new LdXMLGenerator(oUIViewPane.getView(), oNodePositions, oLinks,  true, false);
        oXmlGenerator.start();
		while (oXmlGenerator.isAlive())	{
			try {
				oXmlGenerator.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}
		}
		
		/** generate the Cld xml data and append it to the svg data**/
		Element sQualTemp = oXmlGenerator.generateXmlWithNameSpace();
		Node sQualXmlTemp = svgDocument.importNode(sQualTemp, true);
		oSvgDocRoot.appendChild(svgDocument.createComment("Start of CompendiumLD XML data in cld namespace"));
		oSvgDocRoot.appendChild(sQualXmlTemp);
		oSvgDocRoot.appendChild(svgDocument.createComment("End of CompendiumLD XML data in cld namespace"));
		
		// Add the Cld xml data namespace declaration using Dom
    	oSvgDocRoot.setAttributeNS(SVGGraphics2D.XMLNS_NAMESPACE_URI,  SVGGraphics2D.XMLNS_PREFIX + ":" +  SvgExport.sCldNsPf, SvgExport.sCldNsUri);
    	//oSvgDocRoot.getAttributeNS(SVGGraphics2D.XML_NAMESPACE_URI,  SvgExport.sCldNsPf);
         // Finally, stream out SVG to the standard output using UTF-8 encoding.      
        Writer out;
        try {          
        	FileOutputStream oFoPStr = new FileOutputStream(sFile);
            out = new OutputStreamWriter(oFoPStr, "UTF-8");                   
            DOMUtilities.writeDocument(svgDocument, out);
            out.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
   

    /**
	 * Generate a list of the node positions in the root view being exported.
	 * 
	 * @return ArrayList<NodePosition> oNpsInRootView (an empty list if there are no NodePositions).
	 */
	private ArrayList<NodePosition> generateNpList() {
		ArrayList<NodePosition> oNpsInRootView = new ArrayList<NodePosition>();
		
		for(Enumeration e = oUIViewPane.getView().getPositions();e.hasMoreElements();) {
			Object o = e.nextElement();
			if (o instanceof NodePosition)	{
				NodePosition oNp = (NodePosition)o;
				oNpsInRootView.add(oNp);
			}
		}
		return oNpsInRootView;
	}

	/**
	 * Generate a list of the links in the root view being exported.
	 * 
	 * @return ArrayList<Link> oLinksInView (an empty list if there are no links).
	 */
	private ArrayList<Link> generateLinkList() {
		ArrayList<Link> oLinksInRootView = new ArrayList<Link>();
		oUIViewPane.getView().getLinks();
		for(Enumeration e = oUIViewPane.getView().getLinks();e.hasMoreElements();) {
			Object o = e.nextElement();
			if (o instanceof Link)	{
				Link oLink = (Link)o;
				oLinksInRootView.add(oLink);
			}
		}
		return oLinksInRootView;
	}

	public void generatePrettySvg(String sFile)    {
		Node rootElement = svgDocFactory.getDocumentElement();
		 //Creates an instance of SVGGeneratorContext with the given domFactory and with the default values for the other information
        // A SVGGeneratorContext instance contains all non graphical contextual information that are needed by the SVGGraphics2D to generate SVG from Java 2D primitives. You can subclass it to change the defaults. 
        ctx = SVGGeneratorContext.createDefault(svgDocFactory);
        ctx.setComment("Generated by CompendiumLD with the Batik SVG Generator");
        
        // Create an instance of the SVG Generator.
        svgLDGenerator = new SVGLDGraphics2D(ctx, false);
    	//Paint the UIViewPane on the SVG canvas
       // oUIViewPane.paint(svgGenerator);
     // Now try using SwingSVGPrettyPrint
         SwingSVGPrettyPrint.print(oUIViewPane, svgLDGenerator);
        // Get the dimensions of the viewpane for scaling purposes (not yet used)
        Rectangle oViewPaneBRect = oUIViewPane.getBounds();
        // Need to put px into font size settings so svg files are rendered ok by Firefox eg.  style="font-size:18px;...
 /**    
  * Initial attempts to get font info cvchanged. See notes in blog on Modifying the Document 
  * for indication of how to do this without creating a nearly empty svg document 
  *    Node oNode, oAttribute; 
        Node oFirstElement = svgGenerator.getDOMFactory().getDocumentElement();
        SVGSVGElement root = (SVGSVGElement) svgGenerator.getRoot();
        Element defs = root.getElementById(SVGSyntax.ID_PREFIX_GENERIC_DEFS);
        NodeList oXNodeList = root.getElementsByTagName("style");
        String sStyleAttrib = root.getAttribute("font-size");
   **/
         // Finally, stream out SVG to the standard output using
        // UTF-8 encoding.
        boolean useCSS = true; // we want to use CSS style attributes
       
        Writer out;
        try {           
        	FileOutputStream oFoPStr = new FileOutputStream(sFile);
            out = new OutputStreamWriter(oFoPStr, "UTF-8");
            svgLDGenerator.stream(out, useCSS);
            out.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
	private  static Element generateNavigationPaneClipPathDefs(Document oDoc)	{
		Element oDefs = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_DEFS_TAG);
		Element oClipPath = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_CLIP_PATH_TAG);
		oClipPath.setAttribute("id", SvgExport.sSvgNavPaneGroup+"ClipPath");
		Element oClipRect = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_RECT_TAG);
		// Width is iNavPaneWidth + 1 (the + 1 is to allow for the border rectangle to be seen)
		oClipRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_WIDTH_ATTRIBUTE, Integer.toString(SvgExport.iNavPaneWidth + 1));
		oClipRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_HEIGHT_ATTRIBUTE, "" + SvgExport.NavPaneHeight);
		oClipRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_X_ATTRIBUTE, "0");
		oClipRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_Y_ATTRIBUTE, "0");
		oClipPath.appendChild(oClipRect);
		oDefs.appendChild(oClipPath);
		return oDefs;
	}
	 /**
	 * @return the hsNodesCheck
	 */
	public HashSet<String> getHsNodesCheck() {
		return hsNodesCheck;
	}

	/**
	 * This method returns the map relating a SVGDocument to all the nodes that
	 * have been written as SVG up to the time this method was called into that
	 * document.
	 * 
	 * @return the hmDocToNodesMap a map relating a SVGDocument to all the nodes
	 *         that have been written up to the time this method was called as
	 *         SVG into that document.
	 */
	public static HashMap<SVGDocument, HashSet<String>> getHmDocToNodesMap() {
		return hmDocToNodesMap;
	}

	/**
	 * @return the hsLinksCheck
	 */
	public HashSet<String> getHsLinksCheck() {
		return hsLinksCheck;
	}

	/**
	 * @return the hmDocToLinksMap
	 */
	public static HashMap<SVGDocument, HashSet<String>> getHmDocToLinksMap() {
		return hmDocToLinksMap;
	}

	/**
	 * @return the hsViewNodesCheck
	 */
	public HashSet<String> getHsViewNodesCheck() {
		return hsViewNodesWritten;
	}

	/**
	 * @return the hmDocToViewNodesMap
	 */
	public static HashMap<SVGDocument, HashSet<String>> getHmDocToViewNodesMap() {
		return hmDocToViewNodesMap;
	}

	/**
	 * @return the hmDocToTransWrittenInView
	 */
	public static HashMap<SVGDocument, HashMap<String, String>> getHmDocToTransWrittenInView() {
		return hmDocToTransWrittenInView;
	}

	/**
	 * @return the hmTrancludedNodeWrittenIntoView
	 */
	public HashMap<String, String> getHmTrancludedNodeWrittenIntoView() {
		return hmTrancludedNodeWrittenIntoView;
	}
	
	/**
	 * @return the hmTrancludedLinkWrittenIntoView
	 */
	public HashMap<String, String> getHmTrancludedLinkWrittenIntoView() {
		return hmTrancludedLinkWrittenIntoView;
	}

	/**
	 * @return the hmDocToTransLinkWrittenInView
	 */
	public static HashMap<SVGDocument, HashMap<String, String>> getHmDocToTransLinkWrittenInView() {
		return hmDocToTransLinkWrittenInView;
	}

	/**
	 * @return the hsViewNodesToBeWritten
	 */
	public Vector<String> getVtViewNodesToBeWritten() {
		return vtViewNodesToBeWritten;
	}

	/**
	 * @return the hmDocToViewNodesToBeWrittenMap
	 */
	public static HashMap<SVGDocument, Vector<String>> getHmDocToViewNodesToBeWrittenMap() {
		return hmDocToViewNodesToBeWrittenMap;
	}
	
	/**
	 * Returns the factor by which the navigation panel is scaled (set to 0.5
	 * at present).
	 * @return the dbScaleFactor
	 */
	public static double getScaleFactor() {
		return dbScaleFactor;
	}

	/**
	 * Get iMaxWidth the width of the widest map exported by this instance of SvgExport.
	 * @return the iMaxWidth
	 */
	public int getMaxWidth() {
		return iMaxWidth;
	}

	/**
	 * Get iMaxHeight the height of the longest map exported by this instance of SvgExport.
	 * @return the iMaxHeight
	 */
	public int getMaxHeight() {
		return iMaxHeight;
	}

	/**
	 * Set the value of  iMaxHeight the height of the longest map exported by this instance of SvgExport. 
	 * @param iMaxHeight the iMaxHeight to set
	 */
	public void setMaxHeight(int iMaxHeight) {
		this.iMaxHeight = iMaxHeight;
	}

	/**
	  * Set the value of  iMaxWidth the width of the widest map exported by this instance of SvgExport.
	 * @param iMaxWidth the iMaxWidth to set
	 */
	public void setMaxWidth(int iMaxWidth) {
		this.iMaxWidth = iMaxWidth;
	}

	private void createNavigationPane()	{
		double scaleFactor = SvgExport.getScaleFactor();
		//String sScaleFactor = Double.toString(scaleFactor);
		Element oSvgDocRoot = svgDocument.getDocumentElement();
		oSvgDocRoot.appendChild(svgDocument.createComment("Start of navigation panel"));
		// Set sSvgClass to one of mapnode,listnode, activitynode
		String sSvgClass = oUIViewPane.getView().getSvgClass();
		String sIdPrefix = sSvgClass.substring(0, sSvgClass.indexOf("node"));
		//Edit 1
		Element oNavePaneGroup = svgDocument.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_G_TAG);
		oNavePaneGroup.setAttribute(SVGGraphics2D.SVG_ID_ATTRIBUTE, SvgExport.sSvgNavPaneGroup);
		// Set the clip path - should be in CSS class but I can not work out how to refer to the URL!
		oNavePaneGroup.setAttribute(SVGGraphics2D.SVG_STYLE_ATTRIBUTE, "clip-path: url(#NavPaneClipPath)");
		oNavePaneGroup.setAttribute(SVGGraphics2D.SVG_TRANSFORM_ATTRIBUTE, "scale(" + scaleFactor + ")");
		Element oNavPaneBorderRect = svgDocument.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_RECT_TAG);

		oNavPaneBorderRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_X_ATTRIBUTE, Integer.toString(SvgExport.iNavPaneXPos));
		oNavPaneBorderRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_Y_ATTRIBUTE, "0" );
		oNavPaneBorderRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_WIDTH_ATTRIBUTE, Integer.toString(SvgExport.iNavPaneWidth));
		oNavPaneBorderRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_HEIGHT_ATTRIBUTE, SvgExport.NavPaneHeight );
		oNavPaneBorderRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_CLASS_ATTRIBUTE, "navpaneborderrect");
		oNavPaneBorderRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_RX_ATTRIBUTE, "12");
		//Edit 2
			
		int oHomeRectYPos = 0;  int iDescent = 0;
		Element oHomeRect = svgDocument.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_RECT_TAG);
		oHomeRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_X_ATTRIBUTE, Integer.toString(SvgExport.iNavPaneXPos));
		oHomeRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_Y_ATTRIBUTE, "" + oHomeRectYPos);
		// When the SVG file is first viewed the home window will be visible, hence selected in the nav pane
		oHomeRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_CLASS_ATTRIBUTE, "navpanehomerect");
		String sHomeText = "Home";
		FontRenderContext frc = UIUtilities.getDefaultFontRenderContext();
		Font oHomeFont = new Font("Dialog" , Font.BOLD, 20); 
		Rectangle2D oBoundsRect = oHomeFont.getStringBounds(sHomeText, frc);
		long iStringRectHeight = Math.round(oBoundsRect.getHeight()) + 8;
		// oViewItemGroup is the group of elements representing a View in the navigation pane
		Element oViewItemGroupHome = svgDocument.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_G_TAG);
		// When the SVG file is opened the home view wikl be visible therefore should be selected in the nav pane
		oViewItemGroupHome.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_CLASS_ATTRIBUTE, "navpaneviewitem selected");
		// This is the home view so has no parent: use own id as parent id
		oViewItemGroupHome.setAttribute(SVGGraphics2D.SVG_ID_ATTRIBUTE, sIdPrefix + 
				SvgExport.sNavPaneViewIdPrefix + SvgExport.sIdFragmentConnector + "node" +  
				SvgExport.sIdFragmentConnector +  oUIViewPane.getView().getId() +
				 SvgExport.sIdFragmentConnector + sIdPrefix + SvgExport.sIdFragmentConnector + oUIViewPane.getView().getId());
		oViewItemGroupHome.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_ONCLICK_ATTRIBUTE, SvgExport.sShowMapViewFunction);
		oHomeRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_WIDTH_ATTRIBUTE, "" + Math.max(SvgExport.iNavPaneWidth,  Math.round(oHomeFont.getStringBounds(sHomeText, frc).getWidth())));
		oHomeRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_HEIGHT_ATTRIBUTE, "" + SvgExport.iNavPaneItemHeight);
		oHomeRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_CLASS_ATTRIBUTE, "viewitemrect");
		oHomeRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_RX_ATTRIBUTE, "5");

		Element oText = svgDocument.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_TEXT_TAG);
		oText.setAttribute("class", "navPane");
		oText.setAttribute("x", Integer.toString(SvgExport.iNavPaneXPos + 5));
		iDescent = Math.round(oHomeFont.getLineMetrics(sHomeText, frc).getDescent());
		oText.setAttribute("y", "" + (oHomeRectYPos + SvgExport.iNavPaneItemHeight - iDescent));		
		oText.setTextContent(sHomeText);
		oViewItemGroupHome.appendChild(oHomeRect);
		oViewItemGroupHome.appendChild(oText);
		oNavePaneGroup.appendChild(oNavPaneBorderRect);
		oNavePaneGroup.appendChild(oViewItemGroupHome);
		
		// Edit 3
		int xPos = 0; int yPos = 0;
		createNavPaneViewLevel( oNavePaneGroup, oUIViewPane.getView(), 0, SvgExport.iNavPaneItemHeight, xPos, yPos);
		
		oNavePaneGroup.appendChild(oNavPaneBorderRect);
		oSvgDocRoot.appendChild(oNavePaneGroup);
		oSvgDocRoot.appendChild(svgDocument.createComment("End of navigation panel"));
	}	
	
	/**
	 * Recursive method which generates the SVG elements for the navigation pane
	 * for a View oParentView. The method calls the same method for any child 
	 * Views of this parent View.
	 * 
	 * @param oNavePaneGroup
	 * @param oParentView
	 * @param nLevel
	 * @param iHomeRectHeight
	 * @param xRoot
	 * @param yRoot
	 */
	private void createNavPaneViewLevel(Element oNavePaneGroup, View oParentView, int nLevel, int iHomeRectHeight, int xRoot, int yRoot)	{
		NodeService oNodeService = (NodeService) oParentView.getModel().getNodeService();
		PCSession session = oParentView.getModel().getSession();
		Vector<View> vtTopViewChildViews = new Vector<View>();
		int xPosRoot = 0; 	int yPosRoot = 0;
		try {
			vtTopViewChildViews = (Vector<View>)oNodeService.getChildViews(session, oParentView.getId());
		} catch (SQLException e1) {
			System.out.println("Exception: (SvgExport - oNodeService.getChildViews View" + oParentView.getId()+ " ) :"  + e1.getMessage());
			e1.printStackTrace();
		}
		//Return if there are no child views of this parent view
		if (vtTopViewChildViews.isEmpty())	{
			return;
		}
		else	{
			++nLevel;		
		}
		Iterator<View> oChildViewsToBeWrittenIt = vtTopViewChildViews.iterator();
		String sSvgClassForParent = oParentView.getSvgClass();
		String sIdPrefix = sSvgClassForParent.substring(0, sSvgClassForParent.indexOf("node"));
		//String to hold the id of the Svg element representing the ViewItemGroup in the navigation pane
		String sIdString = "";
		//Element oParentViewItemGroup = this.svgDocument.getElementById( sIdPrefix + SvgExport.sNavPaneViewIdPrefix + SvgExport.sIdFragmentConnector + oParentView.getId());
		//NodeList oUseList = oParentViewItemGroup.getElementsByTagName("use");
		// oViewLevelGroup is the group of elements representing a View level (i.e a collection of other Views) in the navigation pane
		Element oViewLevelGroup = svgDocument.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_G_TAG);
		oViewLevelGroup.setAttribute(SVGGraphics2D.SVG_ID_ATTRIBUTE, sIdPrefix + SvgExport.sNavPaneViewLevelIdPrefix + SvgExport.sIdFragmentConnector + oParentView.getId());
		
		// If this is not the top level view for this document, don't display the navigation pane
		if (!oParentView.getId().equals(SvgExport.hmDocToTopLevelViewId.get(svgDocument) ))	{
			oViewLevelGroup.setAttribute(SVGGraphics2D.CSS_DISPLAY_PROPERTY, "none");
		}
		else	{
			oViewLevelGroup.setAttribute(SVGGraphics2D.CSS_DISPLAY_PROPERTY, "inline");
		}
		
		int nRowSpace = 5;		int nRow = 0; int xMult = 0;
		if (nLevel > 0)	{
			xMult = nLevel -1;
		}
		int iTransId = 0;	
		HashSet<String> hsDocToNodeIdsInDoc = SvgExport.getHmDocToNodesMap().get(svgDocument);
		HashMap<String, String> hmTranscludedNodeIds = SvgExport.getHmDocToTransWrittenInView().get(svgDocument); 
		while (oChildViewsToBeWrittenIt.hasNext())	{
			View oView =  oChildViewsToBeWrittenIt.next();
			String sLabel = oView.getLabel();
			String sSvgClass = oView.getSvgClass();
			sIdPrefix = sSvgClass.substring(0, sSvgClass.indexOf("node"));
			String sParentSvgClass = oParentView.getSvgClass();
			String sParentIdPrefix = sParentSvgClass.substring(0, sParentSvgClass.indexOf("node"));
			if (hmTranscludedNodeIds.containsKey(oView.getId()))	{
				// There are transclusions of the node therefore add id of parent view to make id unique within this svg document
				sIdString = sIdPrefix + 
				SvgExport.sNavPaneViewIdPrefix + SvgExport.sIdFragmentConnector + 
				"node" +  SvgExport.sIdFragmentConnector +  oView.getId() + SvgExport.sIdFragmentConnector + sParentIdPrefix +
				 SvgExport.sIdFragmentConnector + oParentView.getId();
			}
			else	{
				// make id the same structure as for a transcluded node because this makes Javascript easier
				sIdString = sIdPrefix + 
				SvgExport.sNavPaneViewIdPrefix + SvgExport.sIdFragmentConnector + 
				"node" +  SvgExport.sIdFragmentConnector +  oView.getId() + SvgExport.sIdFragmentConnector + sParentIdPrefix +
				 SvgExport.sIdFragmentConnector + oParentView.getId();
		/**		sIdString = sIdPrefix + SvgExport.sNavPaneViewIdPrefix  
				+ SvgExport.sIdFragmentConnector + "node" +  SvgExport.sIdFragmentConnector + oView.getId(); **/
			}

			
			FontRenderContext frc = UIUtilities.getDefaultFontRenderContext();
			Font oHomeFont = new Font("Dialog" , Font.BOLD, 20); 
			int iDescent = Math.round(oHomeFont.getLineMetrics(sLabel, frc).getDescent());
			// oViewItemGroup is the group of elements representing one particular View in the navigation pane
			Element oViewItemGroup = svgDocument.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_G_TAG);
			oViewItemGroup.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_CLASS_ATTRIBUTE, "navpaneviewitem");

			oViewItemGroup.setAttribute(SVGGraphics2D.SVG_ID_ATTRIBUTE, sIdString);
			oViewItemGroup.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_ONCLICK_ATTRIBUTE, SvgExport.sShowMapViewFunction);
			/** oViewItemRect is a Rectangle for each View item in the navigation panel 	**/
			Element oViewItemRect = svgDocument.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_RECT_TAG);
			// Make the Rectangle as wide as the string (or the navigation pane if the string is short)even though it will be clipped if not selected.
			oViewItemRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_WIDTH_ATTRIBUTE, "" + Math.max(SvgExport.iNavPaneWidth,  Math.round(oHomeFont.getStringBounds(sLabel, frc).getWidth())));
			oViewItemRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_HEIGHT_ATTRIBUTE, "" + SvgExport.iNavPaneItemHeight);
			oViewItemRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_CLASS_ATTRIBUTE, "viewitemrect");
			oViewItemRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_RX_ATTRIBUTE, "5");
			Element oUse = svgDocument.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_USE_TAG);
			String sViewClass = oView.getSvgClass();
			oUse.setAttribute(SVGGraphics2D.XLINK_HREF_QNAME, org.apache.batik.svggen.SVGSyntax.SIGN_POUND + sViewClass);
			int xPos = SvgExport.iNavPaneXPos + 1 + xRoot + xMult*32;
			oUse.setAttribute("x",  Integer.toString(xPos));
			// Yposition = nRow*(height of icon + space) + 1 space  + height of Home rect;
			int yPos = nRow*(32  + nRowSpace) + nRowSpace + iHomeRectHeight + yRoot;
			Element oViewItemText = svgDocument.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_TEXT_TAG);
			oViewItemText.setAttribute("class", "navPane");
			//oViewItemText.setAttribute("x", Integer.toString(SvgExport.iNavPaneXPos + 40 + xRoot));
			oViewItemText.setAttribute("x", Integer.toString(xPos +40));
			oViewItemText.setAttribute("y", "" + Integer.toString(yPos + SvgExport.iNavPaneItemHeight - iDescent));
			oViewItemText.setTextContent(sLabel);
			oViewItemRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_Y_ATTRIBUTE, Integer.toString(yPos));
			oViewItemRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_X_ATTRIBUTE, Integer.toString(SvgExport.iNavPaneXPos));
			//oViewItemRect.setAttributeNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_CLASS_ATTRIBUTE, "selected");
			//A scaling transformation never changes a graphic object's grid coordinates or its stroke width; rather, it changes the size of the coordinate system (grid) with respect to the canvas. http://commons.oreilly.com/wiki/index.php/SVG_Essentials/Transforming_the_Coordinate_System#The_scale_Transformation 
			oUse.setAttribute("y", Integer.toString(yPos));
			oViewItemGroup.appendChild(oViewItemRect);
			oViewItemGroup.appendChild(oUse);
			oViewItemGroup.appendChild(oViewItemText);			
			// oNavePaneGroup.appendChild(oViewItemGroup);
			createNavPaneViewLevel(oNavePaneGroup, oView,  nLevel, 16, xPos, yPos);	
			oViewLevelGroup.appendChild(oViewItemGroup);
			nRow++;
		}
		oNavePaneGroup.appendChild(oViewLevelGroup);
	}
}
