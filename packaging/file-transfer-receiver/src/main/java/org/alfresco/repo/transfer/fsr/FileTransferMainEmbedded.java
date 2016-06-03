/*
 * #%L
 * Alfresco File Transfer Receiver Distribution
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
package org.alfresco.repo.transfer.fsr;

import org.apache.catalina.startup.Tomcat;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FileTransferMainEmbedded
{
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        ApplicationContext appCtx = new ClassPathXmlApplicationContext("classpath*:ftr-launcher-context.xml");
        Tomcat tomcat = (Tomcat) appCtx.getBean("embeddedTomcat");
        tomcat.getConnector().setProperty("maxSwallowSize", "-1");
        tomcat.addWebapp("/alfresco-ftr", System.getProperty("user.dir")+"/webapps/file-transfer-receiver.war");
        tomcat.start();
        tomcat.getServer().await();
    }
}
