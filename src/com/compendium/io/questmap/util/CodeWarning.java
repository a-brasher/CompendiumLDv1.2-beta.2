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
 * class CodeWarning
 *
 * @author ?
 */
public class CodeWarning extends CodeProblem {

    public String toString() {
        return "Warning: " + super.toString();
    }

    public CodeWarning(String w) {
        super(w);
    }

    public CodeWarning(String w, int line, String file) {
        super(w, line, file);
    }

    public CodeWarning(String w, int line, int pos, String file) {
        super(w, line, pos, file);
    }
}
