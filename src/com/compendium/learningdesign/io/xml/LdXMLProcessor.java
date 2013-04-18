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

import java.awt.Dialog;

import javax.swing.JProgressBar;

import org.w3c.dom.Document;

import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.IModel;
import com.compendium.core.datamodel.IView;
import com.compendium.core.db.DBNode;
import com.compendium.io.xml.XMLReader;
//import com.compendium.io.xml.XMLImport.ProgressThread;

public class LdXMLProcessor extends LdXMLImport {
	/** String version of the  XML document being imported   	**/
	private String sFileContent = null;

	public LdXMLProcessor(String sFile, IModel model, IView view) {
		super(false, "", model, view, false, false,true);
		sFileContent = sFile;
		DBNode.setImportAsTranscluded(false);
		DBNode.setPreserveImportedIds(false);
		DBNode.setUpdateTranscludedNodes(false);
		DBNode.setNodesMarkedSeen(true);
	//	 this.start();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Load the XML from the given uri and read it.
	 * Object the loaded document and process it.
	 *
	 * @param uri, the uri of the XML document to load/process.
	 */
    protected void createDom( String sDocText ){

        try {
			XMLReader reader = new XMLReader();
			Document document = reader.readText(sDocText, true);			
			if (document != null) {
				processDocument( document );
			} else {
				ProjectCompendium.APP.displayError("Exception: Your document cannot be imported.\n" 
						+ "Document: " + sDocText.substring(0, 100));	
			}
			document = null;
        }
		catch ( Exception e ) {
			ProjectCompendium.APP.displayError(" *** Exception: Your document cannot be imported.\n" 
					+ "Document: " + sDocText.substring(40, 140) + "Error: " + e.getMessage());	
			e.printStackTrace();
        }
    }
	
	
	/**
	 * Start the import thread and progress bar, and calls <code>createDom</code>
	 * to begin the import.
	 */
	public void run() {

  		oProgressBar = new JProgressBar();
  		oProgressBar.setMinimum(0);
		oProgressBar.setMaximum(100);

		oThread = new ProgressThread("Map Copy Progress..", "Paste completed", Dialog.ModalityType.MODELESS);
		oThread.start();

		DBNode.setImporting(true);
        createDom( sFileContent );
		DBNode.restoreImportSettings();

		ProjectCompendium.APP.setTrashBinIcon();

		oProgressDialog.setVisible(false);

		ProjectCompendium.APP.scaleAerialToFit();

		oProgressDialog.dispose();
	}
}
