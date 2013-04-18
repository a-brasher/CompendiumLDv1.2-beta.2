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

package com.compendium.ui.plaf;

import java.awt.Rectangle;

/**
 * This class holds information about each row of the node label.
 */
public class TextRowElement {

	String text = "";
	// startPos is the index of the first character of this row of text within the whole node label string
	int startPos = 0;
	Rectangle textR = null;
	boolean isRowWithCaret;

	public TextRowElement(String text, int startPos, Rectangle textR, boolean isRowWithCaret) {
		this.text = text;
		this.startPos = startPos;
		this.textR = textR;
		this.isRowWithCaret = isRowWithCaret;
	}

	public String getText() {
		return text;
	}

	public int getStartPosition() {
		return startPos;
	}

	public Rectangle getTextRect() {
		return textR;
	}

	public boolean getIsRowWithCaret() {
		return isRowWithCaret;
	}
}
