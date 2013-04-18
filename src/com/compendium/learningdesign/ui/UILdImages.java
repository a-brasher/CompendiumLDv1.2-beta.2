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

import java.io.File;
import java.util.Hashtable;

import javax.swing.ImageIcon;

import com.compendium.core.ICoreConstants;
import com.compendium.learningdesign.core.ILdCoreConstants;
import com.compendium.ui.FormatProperties;
import com.compendium.ui.IUIConstants;
import com.compendium.ui.UIImages;
import com.compendium.ui.stencils.*;

/**
 * Class UILdImages is for retrieving image files for the learning design
 * node types. These images are used in e.g. the View outline generated
 * by class TreeNoneRenderer within class UIViewOutline.
 * 
 * Need to add getPathString()(and other path type messages) which are used in the HTML exports.
 * @author ajb785
 */
public class UILdImages extends UIImages {
	
	/** A reference to the main stencils directory.*/
	private final static String	sLDSTENCILPATH 			=	UIStencilManager.sPATH+ILdCoreConstants.sLD_STENCIL_NAME;
	
	/** A reference to the LD CONDITIONAL  stencils directory.*/
	private final static String	sLDCONDITIONALSTENCILPATH 			=	UIStencilManager.sPATH+ILdCoreConstants.sLD_CONDITIONAL_STENCIL_NAME;
	
	/**	A reference to the directory storing the small image icons	***/
	private final static String sSMALLIMAGESPATH		=	sLDSTENCILPATH + sFS + "smallimages" + sFS;
	
	/**	A reference to the directory storing the small image icons for the LD CONDITIONAL stencil 	***/
	private final static String sCONDITIONALSMALLIMAGESPATH		=	sLDCONDITIONALSTENCILPATH + sFS + "smallimages" + sFS;
	
	/**	A reference to the directory storing the 'normal' size learning design image icons	***/
	private final static String sNODEIMAGESPATH		=	sLDSTENCILPATH + sFS + "nodeimages" + sFS;
	
	/**	A reference to the directory storing the 'normal' size CONDITIONAL learning design image icons	***/
	private final static String sCONDITIONALNODEIMAGESPATH		=	sLDCONDITIONALSTENCILPATH + sFS + "nodeimages" + sFS;
	
	/**	The prefix used for each image file name	**/
	private final static String sIMAGEPREFIX			= 	"id_";
	
	/**	The postfix used for each image file name	**/
	private final static String sIMAGEPOSTFIX			=	".png";
	
	/**	The postfix used for each image file name	**/
	private final static String sSMALLIMAGEPOSTFIX			=	"_sm.png";
	
	private final static Hashtable<Integer, String> htTypesToImagesTable = new Hashtable<Integer, String>(7);
	static	{
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_ACTIVITY,
				ILdCoreConstants.sACTIVITY_LABEL.toLowerCase());
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_ASSESSMENT_SUMMATIVE,
				ILdCoreConstants.sASSIGNMENT_LABEL.toLowerCase());
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_ASSESSMENT,
				"assessment");
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_ASSESSMENT_FORMATIVE,
				"output");
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_LEARNING_OUTCOME,
				"learning-outcome");
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_RESOURCE,
				ILdCoreConstants.sRESOURCE_LABEL.toLowerCase());
		// Note that all role types use the same icon at present. Change the strings in the following key-value pairs to alter this.
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_ROLE,
				ILdCoreConstants.sROLE_LABEL.toLowerCase());
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_ROLE_STUDENT,
				ILdCoreConstants.sROLE_LABEL.toLowerCase());
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_ROLE_TUTOR,
				ILdCoreConstants.sROLE_LABEL.toLowerCase());
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_ROLE_OTHER,
				ILdCoreConstants.sROLE_LABEL.toLowerCase());
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_ROLE_GROUP,
				ILdCoreConstants.sROLE_LABEL.toLowerCase());
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_STOP,
				ILdCoreConstants.sSTOP_LABEL.toLowerCase());
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_TASK,
				ILdCoreConstants.sTASK_LABEL.toLowerCase());
		// Note all tool nodes use the same icon at present. Change the strings in the following key-value pairs to alter this.
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL,
				"tool");
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_BLOG,
		"tool");
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_EP,
		"tool");
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_FORUM,
		"tool");
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_IM,
		"tool");
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_PODCAST,
		"tool");
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_SIM,
		"tool");
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_VW,
		"tool");
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_WIKI,
		"tool");
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_VLE_TOOL_OTHER,
		"tool");
		// Conditional node images
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_CONDITIONAL_CONDITION,
				"condition");
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_CONDITIONAL_FALSE,
		"false");
		htTypesToImagesTable.put(ILdCoreConstants.iLD_TYPE_CONDITIONAL_TRUE,
		"true");
	}
	/**
	 * Return the ImageIcon associated with the given node type.
	 * This is usually a 32 X 32 image icon.
	 * @param int ldType  identifier for the learning design type of the node .
	 * @return the relevant ImageIcon.
	 * @see IUIConstants
	 */
	public final static ImageIcon getNodeIcon(int ldType) {
		ImageIcon image = null;
		String sPath = sNODEIMAGESPATH + sIMAGEPREFIX + UILdImages.htTypesToImagesTable.get(ldType) + sIMAGEPOSTFIX;
		// The CONDITIONAL stencil is in a different directory so reset the path if it's a contional node
		if (ldType == ILdCoreConstants.iLD_TYPE_CONDITIONAL_CONDITION || 
				ldType == ILdCoreConstants.iLD_TYPE_CONDITIONAL_FALSE || 	
				ldType == ILdCoreConstants.iLD_TYPE_CONDITIONAL_TRUE)
			sPath = sCONDITIONALNODEIMAGESPATH + sIMAGEPREFIX + UILdImages.htTypesToImagesTable.get(ldType)+ sIMAGEPOSTFIX;
		
		String skin = FormatProperties.skin;

		try {
			File fileCheck = new File(sPath);
			/** Need to check that sPath exists  - see super classes implementation
			 * Leave for now while testing
			 */
			if (!fileCheck.exists()) {
				System.out.println("UILdImages.getNodeIcon ldType = " + ldType + " Can not locate file " + sPath);
			}

			image = new ImageIcon(sPath);
		}
		catch(Exception e)	{
			e.printStackTrace();			
		}

		return image;
	}

	/**
	 * Return the path of the  ImageIcon associated with the given node type as a String.
	 * This is usually a 32 X 32 image icon.
	 * @param int ldType  identifier for the learning design type of the node .
	 * @return the path of the relevant ImageIcon as a String.
	 * @see IUIConstants
	 */
	public final static String getNodeIconPath(int ldType) {
		ImageIcon image = null;
		String sPath = sNODEIMAGESPATH + sIMAGEPREFIX + UILdImages.htTypesToImagesTable.get(ldType) + sIMAGEPOSTFIX;
		// The CONDITIONAL stencil is in a different directory so reset the path if it's a contional node
		if (ldType == ILdCoreConstants.iLD_TYPE_CONDITIONAL_CONDITION || 
				ldType == ILdCoreConstants.iLD_TYPE_CONDITIONAL_FALSE || 	
				ldType == ILdCoreConstants.iLD_TYPE_CONDITIONAL_TRUE)
			sPath = sCONDITIONALNODEIMAGESPATH + sIMAGEPREFIX + UILdImages.htTypesToImagesTable.get(ldType)+ sIMAGEPOSTFIX;
		
		String skin = FormatProperties.skin;

		try {
			File fileCheck = new File(sPath);
			/** Need to check that sPath exists  - see super classes implementation
			 * Leave for now while testing
			 */
			if (!fileCheck.exists()) {
				System.out.println("UILdImages.getNodeIcon ldType = " + ldType + " Can not locate file " + sPath);
			}

			
		}
		catch(Exception e)	{
			e.printStackTrace();			
		}

		return sPath;
	}
	
	/**
	 * Return the small ImageIcon associated with the given node type.
	 * This is usually a 16 X 16 image icon.
	 * @param int ldType  identifier for the learning design type of the node .
	 * @return the relevant ImageIcon.
	 * @see IUIConstants
	 */
	public final static ImageIcon getNodeIconSmall(int ldType) {
		ImageIcon image = null;
	
		String sPath = sSMALLIMAGESPATH + sIMAGEPREFIX + UILdImages.htTypesToImagesTable.get(ldType)+ sSMALLIMAGEPOSTFIX;
		// The CONDITIONAL stencil is in a different directory so reset the path if it's a contional node
		if (ldType == ILdCoreConstants.iLD_TYPE_CONDITIONAL_CONDITION || 
				ldType == ILdCoreConstants.iLD_TYPE_CONDITIONAL_FALSE || 	
				ldType == ILdCoreConstants.iLD_TYPE_CONDITIONAL_TRUE)
			sPath = sCONDITIONALSMALLIMAGESPATH + sIMAGEPREFIX + UILdImages.htTypesToImagesTable.get(ldType)+ sSMALLIMAGEPOSTFIX;
		
		String skin = FormatProperties.skin;

		try {
			File fileCheck = new File(sPath);
			/** Need to check that sPath exists  - see super classes implementation
			 * Leave for now while testing
			 */
			if (!fileCheck.exists()) {
				System.out.println("UILdImages.getNodeIconSmall ldType = " + ldType + " Can not locate file " + sPath);
			}

			image = new ImageIcon(sPath);
		}
		catch(Exception e)	{
			e.printStackTrace();			
		}

		return image;
	}

	/**
	 * Return the path of the given icon file small image.
	 * @param int idx, The node type
	 * @return a String representing the path to the given icon file.
	 * @see IUIConstants
	 */
	public final static String getSmallPath(int iLdType) {
		String sPath = sSMALLIMAGESPATH + sIMAGEPREFIX + UILdImages.htTypesToImagesTable.get(iLdType)+ sSMALLIMAGEPOSTFIX;
		// The CONDITIONAL stencil is in a different directory so reset the path if it's a contional node
		if (iLdType == ILdCoreConstants.iLD_TYPE_CONDITIONAL_CONDITION || 
				iLdType == ILdCoreConstants.iLD_TYPE_CONDITIONAL_FALSE || 	
				iLdType == ILdCoreConstants.iLD_TYPE_CONDITIONAL_TRUE)
			sPath = sCONDITIONALSMALLIMAGESPATH + sIMAGEPREFIX + UILdImages.htTypesToImagesTable.get(iLdType)+ sSMALLIMAGEPOSTFIX;

		
		try {
			File fileCheck = new File(sPath);
			/** Check path exists: if it does not use the Argument icon */
			if (!fileCheck.exists())
				sPath = UIImages.sDEFAULTNODEPATH+sFS+DEFAULT_IMG_NAMES[ICoreConstants.ARGUMENT];

			fileCheck = new File(sPath);
			if (!fileCheck.exists())
				sPath = sPATH+IMG_NAMES[ICoreConstants.ARGUMENT];
		}
		catch(Exception e)	{
			e.printStackTrace();
		}
		return sPath;
	}
	
	/**
	 * @return the htToolTypeToToolNamesTable
	 */
	public static Hashtable<Integer, String> getTypeToNamesTable() {
		return htTypesToImagesTable;
	}

}
