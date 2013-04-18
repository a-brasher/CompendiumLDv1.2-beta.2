/******************************************************************************
 *                                                                            *
 *  (c) Copyright 2011 The Open University UK  									*
 *                                                                 		        *
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
 *                                                                    	        *
 ********************************************************************************/
/**
 * Structure of ids in CompendiumLD SVG file:
 * In ViewPane:
 * id="mapview.137108491691323450095536"
 * or 
 *  id="activityview.137108491691311090822321" 
 * In NavPane:
 * id="mapnpv.node.137108491691323450095536"
 * or  (if there are transclusions, the id of the view in which the transclusion 
 * occurs is used to make  the id unique.
 * id="mapnpv.node.137108491691311175379751.view.137108491691323450095536"
 * or 
 * id="activitynpv.node.137108491691311090822321"
 */
/**	Declare global variables	**/
/** showDeactivityNodeGrouptailsFor is an Array to store the ids of node <g>s forwhich the links to transclusion	should be displayed. **/
/**
 * 
 */
var showTransclusionsFor = new Array(); 
/** activityNodeGroup is the String prepended to a node id indicating that the group of elements idenified by it is a single node **/
//var activityNodeGroup = "activity.";
/** mapViewGroup is the String prepended to a mapView id indicating that the group of elements idenified by it is a mapView **/
var mapViewGroup = "mapview.";
var sXlinkNS = 	"http://www.w3.org/1999/xlink";
var currentPlatform = new PlatformOS();
//This code is from the book JavaScript: The Definitive Guide, 6th Edition,
//(ISBN #978-0596805524). Copyright 2011 by David Flanagan.
//inherit() returns a newly created object that inherits properties from the
//prototype object p.  It uses the ECMAScript 5 function Object.create() if
//it is defined, and otherwise falls back to an older technique.
function inherit(p) {
 if (p == null) throw TypeError(); // p must be a non-null object
 if (Object.create)                // If Object.create() is defined...
     return Object.create(p);      //    then just use it.
 var t = typeof p;                 // Otherwise do some more type checking
 if (t !== "object" && t !== "function") throw TypeError();
 function f() {};                  // Define a dummy constructor function.
 f.prototype = p;                  // Set its prototype property to p.
 return new f();                   // Use f() to create an "heir" of p.
}
//Define an extend function that copies the properties of its second and 
//subsequent arguments onto its first argument.
//We work around an IE bug here: in many versions of IE, the for/in loop
//won't enumerate an enumerable property of o if the prototype of o has 
//a nonenumerable property by the same name. This means that properties
//like toString are not handled correctly unless we explicitly check for them.
var extend = (function() {  // Assign the return value of this function 
 // First check for the presence of the bug before patching it.
 for(var p in {toString:null}) {
     // If we get here, then the for/in loop works correctly and we return
     // a simple version of the extend() function
     return function extend(o) {
         for(var i = 1; i < arguments.length; i++) {
             var source = arguments[i];
             for(var prop in source) o[prop] = source[prop];
         }
         return o;
     };
 }
 // If we get here, it means that the for/in loop did not enumerate
 // the toString property of the test object. So return a version
 // of the extend() function that explicitly tests for the nonenumerable
 // properties of Object.prototype.
 return function patched_extend(o) {
     for(var i = 1; i < arguments.length; i++) {
         var source = arguments[i];
         // Copy all the enumerable properties
         for(var prop in source) o[prop] = source[prop];

         // And now check the special-case properties
         for(var j = 0; j < protoprops.length; j++) {
             prop = protoprops[j];
             if (source.hasOwnProperty(prop)) o[prop] = source[prop];
         }
     }
     return o;
 };

 // This is the list of special-case properties we check for
 var protoprops = ["toString", "valueOf", "constructor", "hasOwnProperty",
                   "isPrototypeOf", "propertyIsEnumerable","toLocaleString"];
}());

// A simple function for creating simple subclasses
function defineSubclass(superclass,  // Constructor of the superclass
                        constructor, // The constructor for the new subclass
                        methods,     // Instance methods: copied to prototype
                        statics)     // Class properties: copied to constructor
{
    // Set up the prototype object of the subclass
    constructor.prototype = inherit(superclass.prototype);
    constructor.prototype.constructor = constructor;
    // Copy the methods and statics as we would for a regular class
    if (methods) extend(constructor.prototype, methods);
    if (statics) extend(constructor, statics);
    // Return the class
    return constructor;
}
//End of: This code is from the book JavaScript: The Definitive Guide, 6th Edition,
//(ISBN #978-0596805524). Copyright 2011 by David Flanagan.
// We can also do this as a method of the superclass constructor
Function.prototype.extend = function(constructor, methods, statics) {
    return defineSubclass(this, constructor, methods, statics);
};
function Node(id) 	{
	this.id = id;
}
var Map = Node.extend( function Map(id, type, isVisible, parentMapId)	{
	this.id = id;
	this.type = type;
	this.isVisible = isVisible;
	this.parentMapId = parentMapId;
},
		{	
			getViewPaneId: function()	{
				if (this.id !== "" && this.type !== "")
					return(this.type + "view." + this.id);
				else
					return;
			},
			getNavPaneId: function()	{
				var sParentMapString = "";
				if (this.parentMapId != "")	{
					sParentMapString = ".view." + this.parentMapId;
				}
				if (this.id !== "" && this.type !== "")
					return(this.type + "npv." + "node." + this.id + sParentMapString);
				else
					return;
			},
			getNavPaneViewLevel: function()	{
				if (this.id !== "" && this.type !== "")
					return(this.type + "npvl" + "." + this.id);
				else 
					return;
			},
			// isHomeLevel() returns true if the map is the top level map in the navigation tree
			isHomeLevel: function()	{
				if (this.id === "" || this.parentMapId === "")
					return false;
				if (this.id === this.parentMapId)
					return true;
				else
					return false;
			}
		}
	);
function show_details(evt)	
{
	var detailMarker = evt.target;
	var detailsGroup = detailMarker.parentNode;
	var taskNode = detailsGroup.parentNode;
	var detailsText1 = taskNode.childNodes[2];
	var detailsTextElem = taskNode.getElementsByTagName("desc");
	// Set detailsText to the textual content of the desc element which has nodeDetails  
	var detailsText = detailsTextElem[0].textContent;
	alert(detailsText);
}

function show_small_map(evt)	
{
	/** Does nothing, but could display a small version of the map **/
}


function show_mapview(evt) {
	/** Hide containing activity map and show destination activity map * */
	/** Find id of view to show, and the id of the view to hide * */
	/** Hide one, then show the other * */
	var mapNodeIconTarget = evt.target;
	var mapNodeIcon	 = evt.currentTarget;
	var navPaneGroup = document.getElementById("NavPane");
	var viewPaneGroup = document.getElementById("ViewPane");
	var allGroupElemsInViewPane = viewPaneGroup.getElementsByTagName("g");
	var navPaneViewItems = document.getElementsByClassName("navpaneviewitem");
	// var n = viewPaneGroup.children.length;
	var n =  allGroupElemsInViewPane.length;
	var z = viewPaneGroup.children;
	
	// eVisibleMapNavPaneGroup is the nav pane group related to the visible map 
	var eVisibleMapNavPaneGroup = "";
	var temp = "";
	
	
	var mapNodeGroup = "";
	var mapNodeClass = "";
	var type = "";
	var visibleMapElem = "";
	var sVisibleMapIdComplete = "";
	var visibleMap = new Map("");
	var targetMap = new Map("");
	//Id of the visible map in the map view pane
	var sVisibleMapId = "";
	// Numerical part of visible map id string
	var sVisibleMapIdNumber = "";
	// Type of the visible map 
	var sVisibleMapType = "";
	
	var sName = mapNodeIcon.nodeName;
	var idString = "";
	var i=0;
	for (i=0; i<n; ++i)	{
		temp = allGroupElemsInViewPane[i];
		if (temp.getAttribute("display")==="inline")	{
			sVisibleMapId = temp.getAttribute("id");
			break;
		}
	}

	temp = sVisibleMapId.split(".");
	sVisibleMapIdNumber = temp[1];
	sVisibleMapType = (temp[0].split("view"))[0];
	try	{idString = mapNodeIcon.getAttributeNS(null, "id");}
	catch (err) {
		var errMessage = "Error on page " + document.location + " : " + err.description;
		mapNodeIcon = mapNodeIcon.correspondingElement;
	}
	visibleMap = new Map(sVisibleMapIdNumber, sVisibleMapType, true, "");	
	var targetMapIdNumber = "";
	
	if (idString.indexOf("npv") >= 0)	{
		temp = idString.split(".");
		// Getthe info about the target map
		targetMapIdNumber =  temp[2];
		temp = idString.split("npv");
		type =  temp[0];
		// Get the info about the target map's parent
		var pN = mapNodeIcon.parentNode;
		idString = pN.getAttributeNS(null, "id");
		var targetMapParentIdNumber = "";
		if (idString === "NavPane")	{
			// It's the Home map so does not have a parent
			targetMapParentIdNumber = targetMapIdNumber;
		}
		else	{
			temp = idString.split(".");
			temp = idString.split(".");
			 targetMapParentIdNumber =  temp[1];
		}
		
		 targetMap = new Map(targetMapIdNumber, type, false, targetMapParentIdNumber);
	}
	else	{
		//Selection of map to open effected via the map pane
		 mapNodeGroup = mapNodeIcon.parentNode; // Map node <g> tag
		 mapNodeClass = mapNodeGroup.getAttributeNS(null, "class");
		temp = mapNodeClass.split("node");
		type =  temp[0];
		visibleMapElem = mapNodeGroup.parentNode;
		if (visibleMapElem.nodeName == 'use')	{
			/** The node that has been clicked is a transclusion, referenced bia a <use>, hence need the
			 *  parent of the parent to get the visibleMap as the parent of the node clicked on is 'use'. **/
			visibleMapElem = visibleMapElem.parentNode;
		}
		
		var sTargetMapId = mapNodeGroup.getAttributeNS(null, "id"); // id of map
		targetMapIdNumber = sTargetMapId.split(".").pop();
		eVisibleMapNavPaneGroup = navPaneGroup;
		targetMap = new Map(targetMapIdNumber, type, false, sVisibleMapIdNumber);
	}
	//	alert("currentTarget: " +  mapNodeIcon + " target: " + mapNodeIconTarget);
	/**
	 * CompendiumLD maps always have Nodes and links as direct children of the
	 * mapView that contains them.
	 **/
	
	try {					
		var targetNPIdtemp =  targetMap.getNavPaneId();
		var tempElem = document.getElementById(targetNPIdtemp);	
		var targetViewId = targetMap.getViewPaneId();
		temp = navPaneGroup.children;
		/** Make current map invisible * */
		visibleMapElem = document.getElementById(visibleMap.getViewPaneId());
		visibleMapElem.setAttributeNS(null, "display", "none");
		/** Make 'target map' visible * */
		var targetMapElement = document.getElementById(targetViewId);
		targetMapElement.setAttributeNS(null, "display", "inline");
	
		var allGroupElemsInNavPane = navPaneGroup.getElementsByTagName("g");
		
		/** Change the selected View in the Nav pane	**/
		 for (var i = 0; i < allGroupElemsInNavPane.length; ++i)	{
			 var x = allGroupElemsInNavPane[i];			 
			 sClass = x.getAttributeNS(null, "class");
			 if (sClass.indexOf("selected", 0) >= 0)	{
				 eVisibleMapNavPaneGroup = x;
				 sClass.replace("selected", "");
				 eVisibleMapNavPaneGroup.setAttributeNS(null, "class", "navpaneviewitem");
				// break;
			 }
		 }
		 sClass = "navpaneviewitem selected";
		 tempElem.setAttributeNS(null, "class", sClass);
		 var sNavPaneViewLevel = targetMap.getNavPaneViewLevel();
		 var eNavPaneViewLevel = document.getElementById(sNavPaneViewLevel);
		 var eLastNavPaneItemInLevel = "";
		 if (eNavPaneViewLevel)	{  //Only set sub-maps visible if there are sub-maps
			 // Find the numberr of items in this view level so others can be shifted out of the way
			 var nlNavPaneItemInLevel = eNavPaneViewLevel.getElementsByClassName("navpaneviewitem");
			 var no = nlNavPaneItemInLevel.length;
			 eLastNavPaneItemInLevel = nlNavPaneItemInLevel.item(no-1);
			 eNavPaneViewLevel.setAttributeNS(null, "display", "inline");
			 var xForm = "translate(0," + (no*32) + ")";
			 var bAfterTarget = false;
			 for (var c=0; c<navPaneViewItems .length; ++c )	{
				 if (navPaneViewItems[c].getAttributeNS(null, "id") === targetNPIdtemp && !targetMap.isHomeLevel())	{
					 bAfterTarget = true;
				 }
				 if (bAfterTarget){
					 if (!(navPaneViewItems[c].getAttributeNS(null, "id") === targetNPIdtemp))	{
						 navPaneViewItems[c].setAttributeNS(null, "transform", xForm);
					 }						 
				 }
			 }
		 }
		 
	} catch (err) {
		var errMessage = "Error on page " + document.location + " : " + err.description;
		alert(errMessage);
	}

}
function show_transclusions(evt)	
{
	var indicatorMarkerCT = evt.currentTarget;
	var indicatorMarker = evt.target;
	/**		alert("currentTarget: " +  indicatorMarkerCT + "Nodename = " + indicatorMarkerCT.nodeName +  " target: " + indicatorMarker);	**/
	/** Fix for Chjrome and Safari **/
	if (indicatorMarkerCT.nodeName == undefined)	{
		indicatorMarker = indicatorMarkerCT.correspondingElement;
	}

	/** var x = indicatorMarkerCT.getAttributeNS(null, "correspondingElement ");	**/
	/** var indicatorMarkerCE = indicatorMarkerCT.correspondingElement; 	**/
	var groupElements = indicatorMarker.getElementsByTagName("g");
	/** Get details group node	**/
	/** var markerParent = indicatorMarker.parentNode;	**/
	var markerParent = indicatorMarker.parentNode;
	var groupElements = markerParent.getElementsByTagName("g");
	var n = groupElements.length;
	var elem, c, cN;
	for  (var i=0; i<n; ++i)	{
		elem = groupElements[i];
		c = elem.className;
		cN = elem.getAttributeNS(null, "class");
		if (cN == "details")	{
			groupElements[i].setAttributeNS(null, "display", "");
		}   	
	}
}

function hide_transclusions(evt)	
{
	var indicatorMarkerCT = evt.currentTarget;
	var indicatorMarker = evt.target;
	/** Fix for Chrome and Safari (not working) **/
	if (indicatorMarkerCT.nodeName == undefined)	{
		indicatorMarker = indicatorMarkerCT.correspondingElement;
	}
	/** Get details group node	**/
	var markerParent = indicatorMarker.parentNode;
	var groupElements = markerParent.getElementsByTagName("g");
	var node = markerParent.parentNode;
	var nodeId = node.getAttributeNS(null, "id");
	var n = groupElements.length;
	var elem, c, cN;
	if (nodeId != showTransclusionsFor.pop())
	{
		for  (var i=0; i<n; ++i)	{
			elem = groupElements[i];
			c = elem.className;
			cN = elem.getAttributeNS(null, "class");
			if (cN == "details")	{
				groupElements[i].setAttributeNS(null, "display", "none");
			}   	
		}
	}
}

function startShow_transclusions(evt)
{
	var indicatorMarkerTarget = evt.target;
	var indicatorMarker = evt.currentTarget;
	/** Get transclusion groupnode	**/
	var markerParent = indicatorMarker.parentNode;
	/** Get the group defining the node	**/
	var nodeGroup = markerParent.parentNode;
	/** Get the id of the group defining the node	**/
	var id = nodeGroup.getAttributeNS(null, "id");
	showTransclusionsFor.push(id);
}

function show_fullscreen(evt)
{
	var temp = evt.target;
	var oLoc = window.location;	
	window.open(oLoc, "_blank");
}

function PlatformOS () 	{
	this.sPlatformNameFromNav = navigator.platform.toLowerCase();
}

PlatformOS.prototype.isPlatform = function(sPlatform)	{
	if (this.sPlatformNameFromNav.indexOf(sPlatform)>=0)
		return true;
		else
			return false;
};
PlatformOS.prototype.isWindows = function()	{
	return this.isPlatform("win");
};

PlatformOS.prototype.isMac = function()	{
	return this.isPlatform("mac");
};

PlatformOS.prototype.isLinux = function()	{
	return this.isPlatform("linux");
};
function getPlatformString()	{
	var sPlatform = "win";
	var sPlatformFromNav = navigator.platform.toLowerCase();
	if (sPlatformFromNav.indexOf(sPlatform)>0)	{
		return sPlatform;
	}
	else  {
		sPlatform = "mac";
		if (sPlatformFromNav.indexOf(sPlatform)>0)	{
			return sPlatform;
		}
	}
}

function show_download_help(evt)	{
	var oLoc = window.location;
	var downloadGroup = document.getElementById("download");
	var downloadLink = downloadGroup.getElementsByTagName("a").item(0);
	downloadLink.setAttributeNS(sXlinkNS, "href", oLoc);
	var oTitleElem = downloadGroup.getElementsByTagName("title").item(0);
	var sText = "";
	if (currentPlatform.isMac())	{
		sText = oTitleElem.textContent += ": use 'File-Save as' or Cmd+s to save in page source format";
	}
	if (currentPlatform.isWindows()|| currentPlatform.isLinux())	{
		sText = oTitleElem.textContent += ": right-click then 'Save as'";
		//oTitleElem.nodeValue.appendData(": right-click then 'Save as'");
	}
	oTitleElem.textContent = sText;
	var x = "";
}

function show_embedcode(evt)
{
	var oLoc = window.location;
	var sCopyText = "";
	var sCopyTextEnd = " to copy): ";
	if (currentPlatform.isWindows()|| currentPlatform.isLinux())
		sCopyText = "(Ctrl+C" + sCopyTextEnd; 
	if (currentPlatform.isMac())
		sCopyText = "(Cmd &#8984;+C" + sCopyTextEnd;;

		var sUrlData = "&quot;" + oLoc + " 	&quot;";
		var sCommentCode = "&lt;br/&gt;Your browser can not display SVG. Browsers which display SVG include Chrome, Firefox, Opera, Safari and Internet Explorer 9.&lt;br/&gt;";
		var sEmbedStart = "&lt;object  data=";
		var sEmbedMid = " width=&quot;350&quot; height=&quot;350&quot; type=&quot;image/svg+xml&quot;&gt;";
		var sEmbedEnd = "&lt;/object&gt;";

		var sEmbedCode = sEmbedStart + sUrlData + sEmbedMid + sCommentCode + sEmbedEnd;

		var  sCommentCodeRaw = "<br/>Your browser can not display SVG. Browsers which display SVG include Chrome, Firefox, Opera, Safari and Internet Explorer 9.<br/>";
		var sEmbedCodeRawStart = "<object data=";
		var sEmbedCodeRawMid = " width=\"350\" height=\"350\" type=\"image/svg+xml>";
		var sEmbedCodeRawEnd = "</object>";
		var sEmbedCodeRaw = sEmbedCodeRawStart + sUrlData + sEmbedCodeRawMid + sCommentCodeRaw + sEmbedCodeRawEnd;
		var sHtmlHead = "<html> <head> <title>CompendiumLD Embed Code</title>  <link href=\"./styles/styles-cld-svg.css\" rel=\"stylesheet\" type=\"text/css\"/>  <script type=\"text/javascript\">window.onload = function(){  var text_input = document.getElementById (\"embedCode\");  text_input.focus ();  text_input.select ();}</script></head>";
		var sHelpInfoStart = "<body><p class=\"embedinfo\">Copy and paste this code into your website or blog " + sCopyText + " </p><input id=\"embedCode\" readonly=\"readonly\" size=\"60\" value=\"";
		var sHelpInfoEnd = "\"</input></body>";
		var sPageContent = sHtmlHead + sHelpInfoStart + sEmbedCode + sHelpInfoEnd;
		var newWin = window.open("", "CompendiumLD Embed Code", "Width=350,height=120,satus=no,resizable=no");
		newWin.document.write(sPageContent);

		var text_input = newWin.document.getElementById('embedCode');
		text_input.focus ();
		text_input.select ();
		newWin.focus();
		//var sPrompt = "Copy and paste this code into your website or blog: " + sCopyText; 
//		window.prompt (sPrompt, sEmbedCodeRaw);
}