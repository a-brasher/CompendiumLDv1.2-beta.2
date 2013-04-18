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
 * class TokenTable
 *
 * @author  Ron van Hoof
 */
public class TokenTable extends CodeTable {

	public TokenTable() {
		this(10);
  	}  

	public TokenTable(int size) {
		super(size);
	}  

	public int isToken(String key) {
		return getCode(key);
	}  
}
