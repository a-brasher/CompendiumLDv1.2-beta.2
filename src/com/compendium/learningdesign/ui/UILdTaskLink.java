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

package com.compendium.learningdesign.ui;

import javax.swing.JComponent;
import javax.swing.UIDefaults;

import com.compendium.core.datamodel.Link;
import com.compendium.ui.UILink;
import com.compendium.ui.UINode;

public class UILdTaskLink extends UILink {
	private static final String uiClassID = "LdTaskLinkUI";
	public UILdTaskLink(Link link, UINode fromNode, UINode toNode) {
		super(link, fromNode, toNode);
		setLineThickness(100);
	}

	public UILdTaskLink(UINode fromNode, UINode toNode, String type) {
		super(fromNode, toNode, type);
		setLineThickness(100);
	}

	/**
	* Returns a string that specifies the name of the l&f class
	* that renders this component.
	*
	* @return String "LdNodeUI"
	*
	* @see JComponent#getUIClassID
	* @see UIDefaults#getUI
	*/
	public String getUIClassID() {
		return uiClassID;
	}
}
