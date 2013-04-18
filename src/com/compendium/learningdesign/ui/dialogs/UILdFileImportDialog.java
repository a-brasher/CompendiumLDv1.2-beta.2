/******************************************************************************
 *                                                                            *
 *  (c) Copyright 2009 Verizon Communications USA and The Open University UK  *
 *                                                                            *
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
 *                                                                            *
 ******************************************************************************/

package com.compendium.learningdesign.ui.dialogs;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.compendium.ProjectCompendium;
import com.compendium.ui.UIFileFilter;
import com.compendium.ui.UIUtilities;
import com.compendium.ui.dialogs.UIDialog;
import com.compendium.ui.dialogs.UIImportXMLDialog;

public class UILdFileImportDialog extends JFileChooser {
	/** The last directory the user selected to import a file from.*/
	public static String 		lastFileDialogDir = ProjectCompendium.sHOMEPATH+ProjectCompendium.sFS+"Exports";
	
	private File  oImportedFile;
	private JFileChooser oFileDialog;
	private String sFileName;
	/**
	 * 
	 */
	public UILdFileImportDialog(JFrame oParent) {
		//super(oParent, true);
		super(lastFileDialogDir);
		UIFileFilter filter = new UIFileFilter(new String[] {"zip"}, "ZIP Files");

		
		this.setDialogTitle("Select learning design zip file to load");
		this.setFileFilter(filter);
		this.setApproveButtonText("Import");
		
		// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
	//	this.fixForMacs();
				
//		this.add(oFileDialog);
		UIUtilities.centerComponent(this, ProjectCompendium.APP);
		int retval = this.showOpenDialog(ProjectCompendium.APP);
		if (retval == JFileChooser.APPROVE_OPTION) {
			processFileThread();
	//		oImportedFile = oFileDialog.getSelectedFile();
//			processFile(oImportedFile);
		}
		if (retval == JFileChooser.CANCEL_OPTION) {
			onCancel();
		}
	}
	private void processFile() {
		oImportedFile = this.getSelectedFile();
		if (oImportedFile != null)	{
			String fileName = oImportedFile.getAbsolutePath();
			File fileDir = this.getCurrentDirectory();
			String dir = fileDir.getPath();
			if (fileName != null) {
				UIImportXMLDialog.lastFileDialogDir = dir;
				sFileName = fileName;
				try {
					// Import original author and date information, and mark nodes seen, but flse for every other option 
					UIUtilities.unzipXMLZipFile(fileName, true, false, false, false, false, true);
				} catch (IOException io) {
					ProjectCompendium.APP.displayError("Unable to process zip file due to:\n\n"+io.getMessage());
				}
			}
		}
		
	}
	
	private void processFileThread()	{
		oImportedFile = this.getSelectedFile();
		//processFile(oImportedFile);
		Thread thread = new Thread("UIImportLdXMLDialog: Import") {
			public void run() {
				processFile();
			}
		};
		thread.run();

	}
	
	/**
	 * Handle the close action. Closes the import dialog.
	 */
	public void onCancel() {
		setVisible(false);
		//dispose();
	}
	
	/**
	 * Convenience method. FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
	 */
	private void fixForMacs()	{
		if (!UILdFileImportDialog.lastFileDialogDir.equals("")) {
			File file = new File(UILdFileImportDialog.lastFileDialogDir+ProjectCompendium.sFS);
			if (file.exists()) {
				oFileDialog.setCurrentDirectory(file);
			}
		}
	}


}
