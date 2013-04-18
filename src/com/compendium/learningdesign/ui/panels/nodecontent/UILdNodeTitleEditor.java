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

package com.compendium.learningdesign.ui.panels.nodecontent;
import java.awt.BorderLayout;
import java.awt.Font;

import java.awt.Dimension;
import javax.swing.BoxLayout;

import javax.swing.WindowConstants;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.compendium.ui.UITextArea;
import com.compendium.ui.ProjectCompendiumFrame;
import com.compendium.ProjectCompendium;

/**
* This panel is for editing the label of a learning activity.
*/
public class UILdNodeTitleEditor extends javax.swing.JPanel {
	/**
	 * @uml.property  name="oLabel"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JLabel oLabel;
	/**
	 * @uml.property  name="jScrollPane1"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private JScrollPane jScrollPane1;
	/**
	 * @uml.property  name="oLabelField"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private UITextArea oLabelField;
	
	/**
	 * @uml.property  name="font"
	 */
	private Font font							= null;

	/**
	* Auto-generated main method to display this 
	* JPanel inside a new JFrame.
	*/
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new UILdNodeTitleEditor(), BorderLayout.NORTH);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public UILdNodeTitleEditor() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			font = ProjectCompendiumFrame.labelFont;
		//	int scale = ProjectCompendium.APP.getToolBarManager().getTextZoom();
		//	font = new Font(font.getName(), font.getStyle(), font.getSize()+ scale);
			BoxLayout thisLayout = new BoxLayout(this, javax.swing.BoxLayout.X_AXIS);
			this.setLayout(thisLayout);
			setPreferredSize(new Dimension(400, 75));
			this.setMaximumSize(new java.awt.Dimension(32767, 100));
			{
				oLabel = new JLabel();
				BoxLayout oLabelLayout = new BoxLayout(oLabel, javax.swing.BoxLayout.X_AXIS);
				oLabel.setLayout(oLabelLayout);
				this.add(getOLabel());
				oLabel.setText("Title");
			}
			{
				jScrollPane1 = new JScrollPane();
				this.add(jScrollPane1);
				jScrollPane1.setPreferredSize(new java.awt.Dimension(380, 75));
				{
					oLabelField = new UITextArea(50,20);
					jScrollPane1.setViewportView(oLabelField);
					oLabelField.setFont(font);
					oLabelField.setAutoscrolls(true);
					BoxLayout oLabelFieldLayout = new BoxLayout(oLabelField, javax.swing.BoxLayout.X_AXIS);
					oLabelField.setLayout(oLabelFieldLayout);
					oLabelField.setText("Enter title here");
					oLabelField.setPreferredSize(new java.awt.Dimension(362, 704));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return
	 * @uml.property  name="oLabel"
	 */
	public JLabel getOLabel() {
		return oLabel;
	}

	/**
	 * Get the panel to recalulate the default font and reset it on the text areas.
	 * Used for presentation font changes.
	 */
	public void refreshFont() {
		font = ProjectCompendiumFrame.labelFont;
		/** replace these two lines when work out what is wrong **/
		//int scale = ProjectCompendium.APP.getToolBarManager().getTextZoom();
	//	font = new Font(font.getName(), font.getStyle(), font.getSize()+ scale);		

		oLabelField.setFont(font);	
						
		repaint();
	}
}
