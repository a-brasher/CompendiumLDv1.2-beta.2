/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2012 Verizon Communications USA and The Open University UK    *
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


package com.compendium.learningdesign.io.xml;

import java.util.ArrayList;

import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.Link;
import com.compendium.core.datamodel.NodePosition;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.View;
import com.compendium.ui.UIViewFrame;
import com.compendium.ui.plaf.ViewPaneUI;

public class LdXMLCopyMaker implements Runnable {
/** The generator which generates the XML rendering of the map to be copied	**/
private LdXMLGenerator oGenerator;

/** The processor which processes the XML produced by OGenerator and creates a copy of the map	**/
private LdXMLProcessor oProcessor;

/** The View that the nodes (and links) are being pasted into.	**/
private View oCopyOfView;	

/** The UI of the ViewPane that the data is being copied into	**/
private ViewPaneUI oViewPaneUI;	


/**
public LdXMLCopyMaker(View oInputView,  View oOutputView, ViewPaneUI oVpUI, boolean allDepths,
		boolean selectedOnly)	{
	oGenerator = new LdXMLGenerator(oInputView,  allDepths, selectedOnly);
	oCopyOfView = oOutputView;
	oViewPaneUI = oVpUI;
}

public LdXMLCopyMaker(UIViewFrame frame,  View oOutputView, ViewPaneUI oVpUI, boolean allDepths,
		boolean selectedOnly)	{
	oGenerator = new LdXMLGenerator(frame,  allDepths, selectedOnly);
	oCopyOfView = oOutputView;
	oViewPaneUI = oVpUI;
}

**/
	/**
	 * Copy the nodes oNodesToBePasted and links oLinksToBePasted from the
	 * oInputView to the oOutputView, with depth and selection controlled by 
	 *  bALlDepths and bSelectedNodesOnly respectively.  aViewPaneUI is the 
	 *  UI of the View that the nodes and links are being pasted into, i.e 
	 *  the UI displaying oOutputView.
	 *  
	 * @param oInputView			- the View that the selected nodes and links are being copied from
	 * @param oNodesToBePasted		- a list of nodes that will be copied from oInputView and pasted into oOutputView 
	 * @param oLinksToBePasted		- a list of links that will be copied from oInputView and pasted into oOutputView
	 * @param oOutputView			- the View that the selected nodes and links are being copied into
	 * @param aViewPaneUI			- the UI of the View that the nodes and links are being pasted into, i.e the UI displaying oOutputView
	 * @param bALlDepths			- true if the copy and paste should descend to all depths of oInputView
	 * @param bSelectedNodesOnly	- true if only selected nodes in oInputView should be copied
	 */
	public LdXMLCopyMaker(View oInputView, ArrayList<NodePosition> oNodesToBePasted,ArrayList<Link> oLinksToBePasted,
			View oOutputView, ViewPaneUI aViewPaneUI, boolean bALlDepths, boolean bSelectedNodesOnly) {
		oGenerator = new LdXMLGenerator(oInputView, oNodesToBePasted, oLinksToBePasted,  bALlDepths, bSelectedNodesOnly);
		oCopyOfView = oOutputView;
		oViewPaneUI = aViewPaneUI;
		
}

	@Override
	public void run() {
		oGenerator.start();
		while (oGenerator.isAlive())	{
			try {
				oGenerator.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}
		}
		String sTemp = oGenerator.getXmlRepresentation();
		oProcessor =  new LdXMLProcessor(sTemp, ProjectCompendium.APP.getModel(), oCopyOfView);
//		System.out.println(sTemp);
		oProcessor.setViewPaneUI(oViewPaneUI);
		oProcessor.start();
		while (oProcessor.isAlive())	{
			try {
				oProcessor.join();
			} catch (InterruptedException e) {
				System.out.println("LdXMLCopyMaker error:" + e.getMessage());
			}
		}
//		ProjectCompendium.APP.refreshIconIndicators();
	}

}
