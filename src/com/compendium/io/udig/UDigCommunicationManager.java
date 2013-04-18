/******************************************************************************
 *                                                                            *
 *  (c) Copyright 2006 The Open University UK  								  *
 *                                                                            *
 *          This program code may not be used or distributed except in        *
 *                accordance with the license published at                    *
 *            http://www.ecosensus.info/software/license.htm    		      *
 *                                                                            *
 ******************************************************************************/

package com.compendium.io.udig;

import java.net.*;
import java.io.*;
import java.util.Properties;
import java.util.Stack;

import com.compendium.*;



/**
 * This class handles the communications back and forth with uDIG.
 * It creates and manages the two Sockets, the one to send message on and the one to recieve them on.
 * 
 * @author Michelle Bachler
 */
public class UDigCommunicationManager extends Thread {

	public static final String FILENAME = System.getProperty("user.home")+ProjectCompendium.sFS+".ecosensus_ports.properties";
	
	/**
	 * @uml.property  name="oServerSocket"
	 * @uml.associationEnd  inverse="oManager:com.compendium.io.udig.UDigServerSocket"
	 */
	UDigServerSocket oServerSocket = null;
	/**
	 * @uml.property  name="oClientSocket"
	 * @uml.associationEnd  inverse="oManager:com.compendium.io.udig.UDigClientSocket"
	 */
	UDigClientSocket oClientSocket = null;
	
	/**
	 * @uml.property  name="oOpenMapCommandStack"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.String"
	 */
	Stack oOpenMapCommandStack = new Stack();
	/**
	 * @uml.property  name="oAddPropertyCommandStack"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.String"
	 */
	Stack oAddPropertyCommandStack = new Stack();
	/**
	 * @uml.property  name="oEditLabelCommandStack"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.String"
	 */
	Stack oEditLabelCommandStack = new Stack();
	
	/**
	 * @uml.property  name="nServerPort"
	 */
	private int nServerPort = UDigServerSocket.PORT;
	/**
	 * @uml.property  name="nClientPort"
	 */
	private int nClientPort = UDigClientSocket.PORT;
	
	/**
	 * Start up two sockets, one to listen one and to send on.
	 */
	public UDigCommunicationManager() throws UnknownHostException, IOException, SecurityException {
	
		loadPorts();
				
		final UDigCommunicationManager me = this;
		Thread thread = new Thread() {
			public void run() {
				oServerSocket = new UDigServerSocket(me, nServerPort);
				oServerSocket.start();
			}
		};
		thread.start();

		createSendSocket();
	}
	
	public void destroyServerSocket() {

		if (oServerSocket != null) {
			oServerSocket.close();
			oServerSocket.clearSpace();
			oServerSocket = null;
		}
	}
	
	public void destroyClientSocket() {

		if (oClientSocket != null) {
			oClientSocket.close();
			oClientSocket.clearSpace();
			oClientSocket = null;
		}
	}
	
	public void createSendSocket() {

		if (oClientSocket != null) {
			return;
		}
		
		try {
			// IF YOU CAN ESTABLISH A CONNECTION TO THE SERVER END, COMPENDIUM MUST BE RUNNING
			oClientSocket = new UDigClientSocket(this, nClientPort);
		} catch (UnknownHostException e) {
			oClientSocket = null;
			//System.out.println("Exception: " + e.getMessage());
		} catch (java.net.BindException e) {			
			oClientSocket = null;			
			// IF IT CAN'T BIND TO PORT	
			// CHECK FILE to see if port changed and try again.
			int nOldPort = nClientPort;			
			loadPorts();
			if (nClientPort != nOldPort) {
				createSendSocket();
			}
		} catch (ConnectException e) {			
			oClientSocket = null;			
			// IF IT CAN'T FIND THE SERVER END OF THE SOCKET, COMPENDIUM CAN'T BE RUNNUING			
		} catch (IOException e) {
			oClientSocket = null;			
			//e.printStackTrace();			
			//System.out.println("Exception: " + e.getMessage());		
		} catch (SecurityException e) {
			oClientSocket = null;			
			//e.printStackTrace();			
			//System.out.println("Exception: " + e.getMessage());
		}				
	}
	
	private boolean connectToUDig() {
		
		if (oClientSocket == null) {
			//if (isUDigRunning()) {
				createSendSocket();
			//}
			if (oClientSocket == null) {				
				LaunchUDig locate = new LaunchUDig();
				if (!locate.launch(null)) {
					return false;
				}
				else {
					return true;
				}
			}
		}
		return true;
	}
	
	public void runCommands() {
		while (!oOpenMapCommandStack.empty()) {
			openMap((String)oOpenMapCommandStack.pop());
		}
		
		while (!oAddPropertyCommandStack.empty()) {
			addProperty((String)oAddPropertyCommandStack.pop());
		}
		while (!oEditLabelCommandStack.empty()) {
			editLabel((String)oEditLabelCommandStack.pop());
		}				
	}
	
	/**
	 * Clear any commands waiting in the Command stacks 
	 */
	public void clearCommands() {
		oOpenMapCommandStack.clear();
		oAddPropertyCommandStack.clear();	
		oEditLabelCommandStack.clear();
	}	
	
	public void launchUDig() {
		if (oClientSocket == null) {
			if (!connectToUDig()) {
				ProjectCompendium.APP.displayError("Unable to launch uDig.\nPlease launch manually.", "uDig");				
			}
			else {
				ProjectCompendium.APP.displayMessage("uDig is being launched. Please wait...", "uDig");			
			}						
		} else {
			ProjectCompendium.APP.displayMessage("Compendium believes uDig is already running", "uDig");						
		}		
	}
	
	public String openMap(String sMapID) {
		String reply = "";
		if (oClientSocket == null) {
			oOpenMapCommandStack.push(sMapID);
			if (!connectToUDig()) {
				ProjectCompendium.APP.displayError("Unable to launch uDig.\nPlease launch manually.", "uDig");				
			}
			else {
				ProjectCompendium.APP.displayMessage("uDig is being launched. Please wait...\n\nYour request will be forwarded to uDig when it has opened", "uDig");			
			}						
		} else {
			System.out.println("About to send openMap command");
			reply = oClientSocket.openMap(sMapID);			
		}

		return reply;
	}
	
	public String addProperty(String sData) {
		String reply = "";
		if (oClientSocket == null) {
			oAddPropertyCommandStack.push(sData);
			if (!connectToUDig()) {
				ProjectCompendium.APP.displayError("Unable to launch uDig.\nPlease launch manually.", "uDig");				
			}
			else {
				ProjectCompendium.APP.displayMessage("uDig is being launched. Please wait...\n\nYour request will be forwarded to uDig when it has opened", "uDig");			
			}						
		} else {
			System.out.println("About to send addProperty command");
			reply = oClientSocket.addProperty(sData);			
		}

		return reply;
	}
	
	public String editLabel(String sData) {
		String reply = "";
		if (oClientSocket == null) {
			oEditLabelCommandStack.push(sData);
			if (!connectToUDig()) {
				ProjectCompendium.APP.displayError("Unable to launch uDig.\nPlease launch manually.", "uDig");				
			}
			else {
				ProjectCompendium.APP.displayMessage("uDig is being launched. Please wait...\n\nYour request will be forwarded to uDig when it has opened", "uDig");			
			}						
		} else {
			System.out.println("About to send editLabel command");
			reply = oClientSocket.editLabel(sData);			
		}

		return reply;
	}

	public void sendHello() {
		Thread thread = new Thread("UDigConnectionMananger.sendHello") {
			public void run() {		
				if (oClientSocket != null) {
					oClientSocket.sendHello();
				}
			}
		};
		thread.start();
	}

	public void sendGoodbye() {
		//Thread thread = new Thread("UDigConnectionMananger.sendGoodbye") {
		//	public void run() {		
				if (oClientSocket != null) {
					oClientSocket.sendGoodbye();
				}
		//	}
		//};
		//thread.start();
	}

	public void openProject() {
		Thread thread = new Thread("UDigConnectionMananger.openProject") {
			public void run() {		
				if (oClientSocket != null) {
					oClientSocket.openProject();
				}
			}
		};
		thread.start();
	}
	
	public void closeProject() {
		Thread thread = new Thread("UDigConnectionMananger.closeProject") {
			public void run() {		
				if (oClientSocket != null) {
					oClientSocket.closeProject();
				}
			}
		};
		thread.start();
	}
	
	public boolean isUDigRunning() {		
		return oServerSocket.isUDigRunning(); 
	}
	
	public UDigClientSocket getSendSocket() {
		return oClientSocket;
	}
	
	public UDigServerSocket getReceiveSocket() {
		return oServerSocket;
	}	
	
	public void loadPorts() {		
		try {
			File optionsFile = new File(FILENAME);
			Properties connectionProperties = new Properties();
			if (optionsFile.exists()) {
				connectionProperties.load(new FileInputStream(FILENAME));

				String value = connectionProperties.getProperty("compendium-port");
				if (value != null && !value.equals("")) {
					nServerPort = (new Integer(value)).intValue();
				}
				value = connectionProperties.getProperty("udig-port");
				if (value != null && !value.equals("")) {
					nClientPort = (new Integer(value)).intValue();
				}
			}
		} catch (Exception ex) {
			System.out.println("Unable to load external reference to Ecosensus Communication ports.\n\nUsing default ports.\n");
		}
	}
	
	/**
	 * Save the Ecosensus Connection data to a property file.
	 * @throws IO exception.
	 */
	public void savePort(int nPort) throws IOException {

		File optionsFile = new File(FILENAME);
		Properties oConnectionProperties = new Properties();
		
		if (optionsFile.exists()) {
			oConnectionProperties.load(new FileInputStream(FILENAME));			
			oConnectionProperties.put("compendium-port", String.valueOf(nPort));
			oConnectionProperties.store(new FileOutputStream(FILENAME), "Ecosensus Connection Ports");
		} else {
			oConnectionProperties.put("compendium-port", String.valueOf(nPort));
			oConnectionProperties.put("udig-port", String.valueOf(UDigClientSocket.PORT));
			oConnectionProperties.store(new FileOutputStream(FILENAME), "Ecosensus Connection Ports");			
		}
	}
}
