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
 * class Message
 *
 * @author ?
 */
public class Message {

    /**
	 * @uml.property  name="msg"
	 */
    protected String msg;

    public String toString() {
        return msg;
    }

    public Message() {
        msg = "";
    }

    public Message(String m) {
        msg = "";
        msg = m;
    }

    public void setMessage(String m) {
        msg = m;
    }

    public String getMessage() {
        return msg;
    }
}
