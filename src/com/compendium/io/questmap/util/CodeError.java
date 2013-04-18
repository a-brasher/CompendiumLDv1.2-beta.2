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
 * class CodeError
 *
 * @author ?
 */
public class CodeError extends CodeProblem {

    public String toString() {
        return "Error: " + super.toString();
    }

    public CodeError(String e) {
        super(e);
    }

    public CodeError(String e, int line, String file) {
        super(e, line, file);
    }

    public CodeError(String e, int line, int pos, String file) {
        super(e, line, pos, file);
    }
}
