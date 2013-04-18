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

import java.awt.Color;
import java.io.Serializable;

import com.compendium.ui.stencils.UIStencilManager;

/**
 * This interface defines some global constants used by 
 * Learning Design User Interface components in the Compendium application.
 * @author ajb785
 *
 */
public interface ILdUIConstants extends Serializable {
	/** The tab number for the "Tasks help" tab  */
	public final static int iTASKSHELP_TAB			= 0 ;  	// Was 1
	
	/** The tab number for the "Tools help" tab  */
	public final static int iTOOLSHELP_TAB			= 1 ; 	// Was 2
	
	/** The background colour for the LD tools help title panel	**/ 
	public final static Color oLDToolsHelpBackGroundColour = new Color(178, 210, 169);
	
	/** The background colour for the LD activities help title panel	**/ 
	public final static Color oLDActivitiesHelpBackGroundColour = new Color(186, 198, 220);
	
	public final static String sSEQMAPDIR = "Sequence_mapping_a_stencil_to_help_with_laying_out__learning_activities";
	/**A reference to the Learning design sequence map stencil directory*/
	public final static String	sLDSEQUENCEMAPPATH 		= UIStencilManager.sPATH + sSEQMAPDIR; //  "System"+sFS+"resources"+sFS+"ReferenceNodeIcons"+sFS;
}
