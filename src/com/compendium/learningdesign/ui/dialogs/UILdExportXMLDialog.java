package com.compendium.learningdesign.ui.dialogs;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.compendium.ProjectCompendium;
import com.compendium.io.xml.XMLExport;
import com.compendium.learningdesign.io.xml.LdXMLExport;
import com.compendium.ui.UIFileChooser;
import com.compendium.ui.UIFileFilter;
import com.compendium.ui.UIUtilities;

import com.compendium.ui.dialogs.UIExportXMLDialog;

public class UILdExportXMLDialog extends UIExportXMLDialog {
	
	public UILdExportXMLDialog(JFrame parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8250563267509408222L;
	
	/**
	 * Handle action events coming from the buttons.
	 * @param evt, the associated ActionEvent object.
	 */
	public void actionPerformed(ActionEvent evt) {

		Object source = evt.getSource();
		if (source instanceof JButton) {
			if (source == pbExport) {
				onLdExport();
			}
			else if (source == pbClose) {
				onCancel();
			}
		}
	}

	/**
	 * Handle the learning design export action. Export is configured to be for
	 *  the selected views, to full depth, including images and reference files.
	 */
	public void onLdExport() {

		String fileName = "";
		String directory = "";
		boolean toZip = true;
		if (toZip) {
			UIFileFilter filter = new UIFileFilter(new String[] {"zip"}, "ZIP Files");

			UIFileChooser fileDialog = new UIFileChooser();
			fileDialog.setDialogTitle("Enter the file name to Export to...");
			fileDialog.setFileFilter(filter);
			fileDialog.setApproveButtonText("Save");
			fileDialog.setRequiredExtension(".zip");

		    // FIX FOR MAC - NEEDS '/' ON END TO DENOTE A FOLDER
		    File file = new File(exportDirectory+ProjectCompendium.sFS);
		    if (file.exists()) {
				fileDialog.setCurrentDirectory(file);
			}

			UIUtilities.centerComponent(fileDialog, ProjectCompendium.APP);
			int retval = fileDialog.showSaveDialog(ProjectCompendium.APP);
			if (retval == JFileChooser.APPROVE_OPTION) {
	        	if ((fileDialog.getSelectedFile()) != null) {

	            	fileName = fileDialog.getSelectedFile().getAbsolutePath();
					File fileDir = fileDialog.getCurrentDirectory();
					exportDirectory = fileDir.getPath();

					if (fileName != null) {
						if ( !fileName.toLowerCase().endsWith(".zip") ) {
							fileName = fileName+".zip";
						}
						this.requestFocus();
						setCursor(new Cursor(Cursor.WAIT_CURSOR));

						setVisible(false);
						boolean selectedOnly = true; //rbSelectedNodes.isSelected();
						boolean allDepths = true; //rbAllDepths.isSelected();
						boolean withStencilsAndLinkGroups = false; //cbWithStencilsAndLinkGroups.isSelected();
						boolean withMeetings = false; //cbWithMeetings.isSelected();
						
						XMLExport export = new LdXMLExport(this.getCurrentView(), fileName, allDepths, selectedOnly, toZip, 
								withStencilsAndLinkGroups, withMeetings, true);
						export.start();

						dispose();
					}
				}
			}
		}
			
	}


}
