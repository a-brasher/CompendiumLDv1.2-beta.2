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

import java.awt.*;

import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.BoxLayout;

import java.awt.event.MouseAdapter;
import javax.swing.BorderFactory;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import com.compendium.ui.UITextArea;
import com.compendium.ui.ProjectCompendiumFrame;
import com.compendium.ProjectCompendium;



public class UILdNodeLearningOutcomeEntryPanel extends javax.swing.JPanel {
	/**
	 * The panel to display the learining outcome number  *
	 * @uml.property  name="oLONumberPanel"
	 * @uml.associationEnd  
	 */
	private JLabel oLONumberPanel;
	
	/**
	 * UITextArea in which user enters or edits a learning outcome	*
	 * @uml.property  name="oLearningOutcomeTextArea"
	 * @uml.associationEnd  
	 */
	private UITextArea oLearningOutcomeTextArea;	
	
	/**
	 * The font used to display the learning outcome *
	 * @uml.property  name="font"
	 */
	private Font font							= null;
	
	/**	
	* Auto-generated main method to display this 
	* JPanel inside a new JFrame.
**/	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new UILdNodeLearningOutcomeEntryPanel());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	/**
	 * 
	 */
	public UILdNodeLearningOutcomeEntryPanel() {
		super();
		BoxLayout oLONumberPanelLayout1 = new BoxLayout(oLONumberPanel, javax.swing.BoxLayout.X_AXIS);
		BoxLayout oLONumberPanelLayout = new BoxLayout(oLONumberPanel, javax.swing.BoxLayout.Y_AXIS);
		initGUI();
	}
	/**
	 * @param layout
	 */
	public UILdNodeLearningOutcomeEntryPanel(LayoutManager layout) {
		super(layout);
		// TODO Auto-generated constructor stub
	}
	private void initGUI()	{
		try	{
			{
				this.setBackground(new java.awt.Color(255,128,128));
				this.setPreferredSize(new java.awt.Dimension(400, 30));
				this.setBorder(new LineBorder(new java.awt.Color(0,0,0), 1, false));
			}
			{
				oLONumberPanel = new JLabel();
				
				this.add(getOLONumberPanel());
			// oLONumberPanel.setText("1");
			//	oLONumberPanel.setLayout(layout);
				oLONumberPanel.setBackground(new java.awt.Color(255,128,128));
				oLONumberPanel.setPreferredSize(new java.awt.Dimension(16, 30));
				oLONumberPanel.setForeground(new java.awt.Color(0,0,0));
				oLONumberPanel.setOpaque(true);
				
			}
			{
				font = ProjectCompendiumFrame.labelFont;
				/** The next two lines commented because are causing an error. Replace when have
				 * worked out what is wrong. 		**/
				//int scale = ProjectCompendium.APP.getToolBarManager().getTextZoom();
				// font = new Font(font.getName(), font.getStyle(), font.getSize()+ scale);	
				
				oLearningOutcomeTextArea = new UITextArea("Describe a learning outcome");
				oLearningOutcomeTextArea.setFont(font);
				oLearningOutcomeTextArea.setLayout(null);
				this.add(getOLearningOutcomeTextArea());
				oLearningOutcomeTextArea.setBorder(new LineBorder(new java.awt.Color(0,0,0), 1, false));
				oLearningOutcomeTextArea.setPreferredSize(new java.awt.Dimension(370, 30));
				oLearningOutcomeTextArea.addMouseListener(new MouseAdapter() {
				});
				oLearningOutcomeTextArea.addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent e) {
						int keyCode = e.getKeyCode();
						int modifiers = e.getModifiers();
						if (modifiers == java.awt.Event.SHIFT_MASK && keyCode == KeyEvent.VK_TAB) {
							oLearningOutcomeTextArea.requestFocus();
							oLearningOutcomeTextArea.setCaretPosition(0);
						}
					}
					
				});
				oLearningOutcomeTextArea.addCaretListener(new CaretListener() {
					public void caretUpdate(CaretEvent evt) {
						System.out.println("oLearningOutcomeTextArea.caretUpdate, event="+evt);
						//TODO add your code for oLearningOutcomeTextArea.caretUpdate
					}
				});
				//oLearningOutcomeTextArea.setText("Describe the learning outcome");
			}
		


		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
		/**
		 * @return  the oLONumberPanel
		 * @uml.property  name="oLONumberPanel"
		 */
		public JLabel getOLONumberPanel() {
			return oLONumberPanel;
		}
		/**
		 * @return the oLearningOutcomeTextArea
		 */
		public JTextArea getOLearningOutcomeTextArea() {
			return oLearningOutcomeTextArea;
		}
		/** Set the number of the Learning outcome to be displayed ***/
		public void setOutcomeNumber(int i) {			
			oLONumberPanel.setText(Integer.toString(i));
		}
		
		/**
		 * Get the dialog to recalulate the default font and reset it on the text areas.
		 * Used for presentation font changes.
		 */
		public void refreshFont() {
			font = ProjectCompendiumFrame.labelFont;
			/** replace these two lines when work out what is wrong **/
		//	int scale = ProjectCompendium.APP.getToolBarManager().getTextZoom();
		//	font = new Font(font.getName(), font.getStyle(), font.getSize()+ scale);		

				
			oLearningOutcomeTextArea.setFont(font);
					
			repaint();
		}
	
}
