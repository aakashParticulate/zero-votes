/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zero.votes.persistence.entities;

/**
 *
 * @author iekadou
 */
import javax.xml.ws.WebFault;

@WebFault
public class ValidationFault extends Exception {

    private String faultInfo;
    
    ValidationFault(String faultInfo) {
	this(ValidationFault.class.getSimpleName(), faultInfo, null);
    }

    public ValidationFault(String message, String faultInfo) {
            this(message, faultInfo, null);
    }

    public ValidationFault(String message, String faultInfo, Throwable cause) {
            super(message, cause);
            this.faultInfo = faultInfo;
    }

    public String getFaultInfo() {
            return faultInfo;
    }
}
