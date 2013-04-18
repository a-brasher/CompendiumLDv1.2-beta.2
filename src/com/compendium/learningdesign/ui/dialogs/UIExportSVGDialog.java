package com.compendium.learningdesign.ui.dialogs;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.compendium.ProjectCompendium;
import com.compendium.io.xml.XMLExport;
import com.compendium.learningdesign.io.svg.SvgExport;

import com.compendium.ui.UIFileChooser;
import com.compendium.ui.UIFileFilter;
import com.compendium.ui.UIMapViewFrame;
import com.compendium.ui.UIUtilities;
import com.compendium.ui.UIViewFrame;
import com.compendium.ui.UIViewPane;
import com.compendium.ui.dialogs.UIExportXMLDialog;

public class UIExportSVGDialog extends UIFileChooser {

	private static final String sDEFUALTTITLE		= "Export to SVG";
	/** The default directory to export to.*/
	protected static String		exportDirectory = ProjectCompendium.sHOMEPATH+ProjectCompendium.sFS+"Exports";


	/**
	 * @param parent
	 */
	public UIExportSVGDialog(JFrame parent) {
		this(parent, sDEFUALTTITLE);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param parent
	 */
	public UIExportSVGDialog(JFrame parent, String sTitle) {
		this.setDialogTitle(sTitle);

		String directory = "";

		UIFileFilter filter = new UIFileFilter(new String[] {"svg"}, "SVG Files");


		this.setDialogTitle("Enter the file name to Export to...");
		this.setFileFilter(filter);
		this.setApproveButtonText("Save");
		this.setRequiredExtension(".svg");

		// FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
		File file = new File(exportDirectory+ProjectCompendium.sFS);
		if (file.exists()) {
			this.setCurrentDirectory(file);
		}

		UIUtilities.centerComponent(this, ProjectCompendium.APP);
		int retval = this.showSaveDialog(ProjectCompendium.APP);
		if (retval == JFileChooser.APPROVE_OPTION) {
			this.onExport();	
		}
	}



	/**
	 * Handle the export action.
	 */
	public void onExport() {

		String fileName = "";

		if ((this.getSelectedFile()) != null) {

			fileName = this.getSelectedFile().getAbsolutePath();
			File fileDir = this.getCurrentDirectory();
			exportDirectory = fileDir.getPath();

			if (fileName != null) {
				if ( !fileName.toLowerCase().endsWith(".svg") ) {
					fileName = fileName+".svg";
				}
				this.requestFocus();
				setCursor(new Cursor(Cursor.WAIT_CURSOR));

				setVisible(false);
				/**				boolean selectedOnly = rbSelectedNodes.isSelected();
					boolean allDepths = rbAllDepths.isSelected();
					boolean withStencilsAndLinkGroups = cbWithStencilsAndLinkGroups.isSelected();
					boolean withMeetings = cbWithMeetings.isSelected();
				 **/
				final String fFilename = fileName;
				UIViewFrame oFrame = ProjectCompendium.APP.getCurrentFrame();
				if (oFrame instanceof UIMapViewFrame) {
					final UIViewPane oViewPane  = ((UIMapViewFrame)oFrame).getViewPane();

					Thread thread = new Thread("UIExportSVGDialog.onExport") {
						public void run() {
							SvgExport export = null;
							export = new SvgExport(oViewPane, fFilename, false);
							String sMessage = "Finished saving SVG file into " + fFilename;
							ProjectCompendium.APP.displayMessage(sMessage, "Export Finished");
						}
					};
					thread.start();
				}
				else	{
					// Warn user that viewFrame is not appropriate
					ProjectCompendium.APP.displayMessage("You can only export a map in SVG (Scaleable Vector Graphics) format:\\n" +
							"please select a map to export" , "Sorry!");
				}
			}
		}
	}

}
