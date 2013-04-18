/********************************************************************
 *              BELL ATLANTIC CONFIDENTIAL INFORMATION              *
 *          (c)1997 Bell Atlantic Science & Technology, Inc.        *
 *                        All Rights Reserved                       *
 *                                                                  *
 *  This program contains confidential and proprietary information  *
 *  of the Bell Atlantic Corporation, any reproduction, disclosure, *
 *  or use in whole or in part is expressly prohibited, except as   *
 *  may be specifically authorized by prior written agreement.      *
 *                                                                  *
 ********************************************************************/

package com.compendium.io.questmap;

import java.util.*;

/**
 * class CodeTable
 *
 * @author  Ron van Hoof
 */
public class CodeTable {

	/**
	 * @uml.property  name="symboltable"
	 * @uml.associationEnd  qualifier="key:java.lang.Object java.lang.Integer"
	 */
	protected Hashtable symboltable;
	/**
	 * @uml.property  name="keytable"
	 * @uml.associationEnd  multiplicity="(0 -1)" ordering="true" elementType="java.lang.Object" qualifier="new:java.lang.Integer java.lang.Object"
	 */
	protected Hashtable keytable;

	public CodeTable() {
		this(10);
  	}  

	public CodeTable(int size) {
		symboltable = new Hashtable(size);
		keytable = new Hashtable(size);
  	}  

	public void addCode(Object key, int sym) {
		if (!symboltable.containsKey(key)) {
	  		symboltable.put(key, new Integer(sym));
	  		keytable.put(new Integer(sym), key);
		}
	}  

	public void removeCode(Object key) {
		if (symboltable.containsKey(key)) {
	  		keytable.remove(symboltable.get(key));
	  		symboltable.remove(key);
		}
	}  

	public int getCode(Object key) {
		Integer result = null;

		result = (Integer)symboltable.get(key);
		if (result != null) {
	  		return result.intValue();
		} 
		else {
	  		return -1;
		}
	}  
  
	public Object getKey(int sym) {
		return keytable.get(new Integer(sym));
  	} 
}
