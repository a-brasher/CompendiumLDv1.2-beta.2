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
 * class CharSymbolTable
 *
 * @author  Ron van Hoof
 */
public class CharSymbolTable extends CodeTable {

	public CharSymbolTable() {
		this(10);
  	} 
  
	public CharSymbolTable(int size) {
		super(size);
  	}  

	public void addCode(int key, int sym) {
		super.addCode(new Integer(key), sym);
  	} 

	public void removeCode(int key) {
		super.removeCode(new Integer(key));
	}  

	public int getCode(int key) {
		return super.getCode(new Integer(key));
	}  
  
	public int isCharSymbol(int key) {
		return getCode(key);
	}  
}
