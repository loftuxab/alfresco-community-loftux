/*
 * #%L
 * office-application
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.office.application;

import java.io.IOException;

import com.cobra.ldtp.Ldtp;
import com.cobra.ldtp.LdtpExecutionError;

public interface OfficeApplications
{

    /**
     * Method to open Excel application
     * 
     * @throws LdtpExecutionError, IOException
     */
    public abstract Ldtp openOfficeApplication() throws LdtpExecutionError, IOException;

    /**
     * Method to add some lines to the file
     * 
     * @throws LdtpExecutionError
     */
    public abstract void editOffice(Ldtp ldtp, String data) throws LdtpExecutionError;

    /**
     * Method to Save the  file which is already saved to particular location
     * 
     * @throws LdtpExecutionError
     */
    public abstract void saveOffice(Ldtp ldtp) throws LdtpExecutionError;

    /**
     * Method to Save the excel file for the first time in a particular location
     * 
     * @throws LdtpExecutionError
     */
    public abstract void saveOffice(Ldtp ldtp, String location) throws LdtpExecutionError;


    /**
     * Method to SaveAs to a particular location
     * 
     * @throws LdtpExecutionError
     */
    public abstract void saveAsOffice(Ldtp ldtp, String location) throws LdtpExecutionError;

    /**
     *Method to Open a particular file from a location
     * 
     * @throws LdtpExecutionError
     */
    public abstract void openOfficeFromFileMenu(Ldtp ldtp, String location) throws LdtpExecutionError;

    /**
     * Method to close a particular file 
     * 
     * @throws LdtpExecutionError
     */
    public abstract void closeOfficeApplication(String fileName) throws LdtpExecutionError;

    /**
     * Method to find window name for excel
     * 
     * @throws LdtpExecutionError
     */
    public abstract String findWindowName(String fileName) throws LdtpExecutionError;

    
    /**
     * Method to click on any object
     * 
     * @throws LdtpExecutionError
     */
    public abstract void clickOnObject(Ldtp ldtp, String myObject);
    
    /**
     * Method to exit office application (sending ALT + f4)
     * 
     * @throws LdtpExecutionError
     */ 
    public abstract void exitOfficeApplication( Ldtp ldtp);
    
    
    /**
     * Method to navigate to File from office application
     * 
     * @throws LdtpExecutionError
     */ 
    public abstract void goToFile(Ldtp ldtp);
}