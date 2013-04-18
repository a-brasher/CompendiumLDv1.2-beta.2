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

import java.util.Vector;

/**
 * Class Union
 *
 *     This class is used for the BYacc parser. The current
 *     version only supports integer return values from the
 *     lexer. This union class will enable the parser to
 *     support other types as well. After generating the
 *     parser code from BYacc the definition of the
 *     semantic values have to be altered to be of type
 *     Union. The attributes are made public to allow for
 *     minimal changes in the generated code. The generated
 *     code makes direct references.
 *
 * @author ?
 */
public class Union {

 	/**
	 * @uml.property  name="ival"
	 */
 	public int ival;
	/**
	 * @uml.property  name="lval"
	 */
	public long lval;
	/**
	 * @uml.property  name="cval"
	 */
	public char cval;
  	/**
	 * @uml.property  name="fval"
	 */
  	public float fval;
 	/**
	 * @uml.property  name="dval"
	 */
 	public double dval;
  	/**
	 * @uml.property  name="bval"
	 */
  	public boolean bval;
	/**
	 * @uml.property  name="relopval"
	 */
	public int relopval;
  	/**
	 * @uml.property  name="sval"
	 */
  	public String sval;
  	/**
	 * @uml.property  name="oval"
	 */
  	public Object oval;
	/**
	 * @uml.property  name="vval"
	 */
	public Vector vval;

  	public Union() {
		ival = 0;
		lval = 0;
		fval = 0.0f;
		dval = 0.0;
		bval = true;
		sval = "";
		relopval = 0;
		oval = null;
		vval = null;
  	} 
}
