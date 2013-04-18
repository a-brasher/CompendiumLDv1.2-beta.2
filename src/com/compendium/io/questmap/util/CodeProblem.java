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

package com.compendium.io.questmap.util;

/**
 * class CodeProblem
 *
 * @author ?
 */
public class CodeProblem extends Message {

    /**
	 * @uml.property  name="line"
	 */
    protected int line;
    /**
	 * @uml.property  name="pos"
	 */
    protected int pos;
    /**
	 * @uml.property  name="file"
	 */
    protected String file;
    /**
	 * @uml.property  name="posSet"
	 */
    protected boolean posSet;

    /**
	 * @return
	 * @uml.property  name="file"
	 */
    public String getFile() {
        return file;
    }

    /**
	 * @param l
	 * @uml.property  name="line"
	 */
    public void setLine(int l) {
        line = l;
    }

    /**
	 * @return
	 * @uml.property  name="line"
	 */
    public int getLine() {
        return line;
    }

    public String toString() {
        if (posSet)
            return getFile() + "(l:" + new Integer(getLine()) + " p:" + new Integer(getPos()) + "):" + getMessage();
        else
            return getFile() + "(" + new Integer(getLine()) + "):" + getMessage();
    }

    public CodeProblem(String c) {
        super(c);
        line = 0;
        pos = 0;
        file = "";
        posSet = false;
    }

    public CodeProblem(String c, int l, String f) {
        super(c);
        line = 0;
        pos = 0;
        file = "";
        posSet = false;
        line = l;
        file = f;
    }

    public CodeProblem(String c, int l, int p, String f) {
        super(c);
        line = 0;
        pos = 0;
        file = "";
        posSet = false;
        line = l;
        pos = p;
        file = f;
        posSet = true;
    }

    /**
	 * @param p
	 * @uml.property  name="pos"
	 */
    public void setPos(int p) {
        pos = p;
        posSet = true;
    }

    /**
	 * @return
	 * @uml.property  name="pos"
	 */
    public int getPos() {
        return pos;
    }

    /**
	 * @param f
	 * @uml.property  name="file"
	 */
    public void setFile(String f) {
        file = f;
    }
}
