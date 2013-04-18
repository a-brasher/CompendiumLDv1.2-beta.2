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

package com.compendium.learningdesign.ui.plaf;

import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.compendium.ProjectCompendium;
import com.compendium.ui.UILink;
import com.compendium.ui.plaf.LinkUI;
import com.compendium.learningdesign.ui.UILdTaskLink;

/**
 * This class is not yet used. Not sure if it is going to be necessary.
 * @author ajb785
 *
 */
public class LdTaskLinkUI extends LinkUI {

	/**
	 * 
	 */
	public LdTaskLinkUI() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Paint the node.
	 *
	 * @param g, the Graphics object to use to do the paint.
	 * @param c, the component being painted.
	 * @see #paintEnabledText
 	 * @see #paintDisabledText
	 * @see #drawText
	 */
  	public void paint(Graphics g, JComponent c) {
  		super.paint(g, c);
  		// Add another methods if necessary 		
  	}
	/**
	 * Create a new LdTaskLinkUI instance.
	 * @param c, the component this is the ui to install for.
	 */
  	public static ComponentUI createUI(JComponent c) {
		return new LdTaskLinkUI();
  	}
  	
  	/**
	 * Run any install instructions for installing this UI.
	 * @param c, the component this is the ui for.
	 */
	public void installUI(JComponent c) {
		super.installUI(c);
	    oLink = (UILdTaskLink)c;

	}
}
