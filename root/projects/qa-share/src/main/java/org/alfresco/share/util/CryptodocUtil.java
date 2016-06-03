/*
 * #%L
 * qa-share
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
package org.alfresco.share.util;

import org.alfresco.po.share.adminconsole.NodeBrowserPage;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Maryia Zaichanka
 */
public class CryptodocUtil extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(CryptodocUtil.class);
    private static final String JMX_ENCRYPTED_STORE_CONFIG = "Alfresco:Type=Configuration,Category=ContentStore,id1=managed,id2=encrypted";
    private static final String ENCRYPTED_STOP = "stop";
    private static final String ENCRYPTED_START = "start";
    private static final String REVOKE = "revokeMasterKey";
    private static final String REVOKE_CANCEL = "cancelRevocation";
    private static final String REENCRYPT = "reEncryptSymmetricKeys";
    private static String ALIASES = "cryptodoc.jce.key.aliases";
    private static String KEY_PASSWORDS = "cryptodoc.jce.key.passwords";


    private static String ALIAS = getRandomString(6);
    private static String ALGORITHM = "RSA";
    private static String PASS = getRandomString(6);
    private static String KEY_STORE_PASS;
    private static String SIZE = "2048";

    public static void changeKeyProperties (String nodeUrl, String keyName, String keyPass) throws Exception
    {

        JmxUtils.invokeAlfrescoServerProperty(nodeUrl, JMX_ENCRYPTED_STORE_CONFIG, ENCRYPTED_STOP);
        JmxUtils.setAlfrescoServerProperty(nodeUrl,JMX_ENCRYPTED_STORE_CONFIG, ALIASES, keyName);
        JmxUtils.setAlfrescoServerProperty(nodeUrl,JMX_ENCRYPTED_STORE_CONFIG, KEY_PASSWORDS, keyPass);
        JmxUtils.invokeAlfrescoServerProperty(nodeUrl, JMX_ENCRYPTED_STORE_CONFIG, ENCRYPTED_START);

        logger.info("Key is added");
    }

    public static void revokeKey (String nodeUrl, String prop) throws Exception
    {
        JmxUtils.invokeAlfrescoServerPropertyProp(nodeUrl, JMX_ENCRYPTED_STORE_CONFIG, REVOKE, prop);
        logger.info("Key is revoked");
    }

    public static void reEncryptKey (String nodeUrl, String prop) throws Exception
    {
        JmxUtils.invokeAlfrescoServerPropertyProp(nodeUrl, JMX_ENCRYPTED_STORE_CONFIG, REENCRYPT, prop);
        logger.info("Key is reencrypted");
    }

    public static void cancelRevocation (String nodeUrl, String prop) throws Exception
    {
        JmxUtils.invokeAlfrescoServerPropertyProp(nodeUrl, JMX_ENCRYPTED_STORE_CONFIG, REVOKE_CANCEL, prop);
        logger.info("Revocation is canceled");
    }

    public static String getBinPath(WebDrone drone, String fileName, String nodeRef)
    {

        try
        {
            nodeRef = nodeRef.substring(nodeRef.indexOf("workspace"));
            nodeRef = nodeRef.replaceFirst("/", "://");
            NodeBrowserPageUtil.openNodeBrowserPage(drone);
            NodeBrowserPage nodeBrowserPage = NodeBrowserPageUtil.executeQuery(drone, nodeRef, NodeBrowserPage.QueryType.NODE_REF, NodeBrowserPage.Store.WORKSPACE_SPACE_STORE)
                        .render();
            getCurrentPage(drone).render(maxWaitTime);
            String binPath=null;
            if (nodeBrowserPage.isInResultsByName(fileName) && nodeBrowserPage.isInResultsByNodeRef(nodeRef))
            {
                nodeBrowserPage.getItemDetails(fileName);
                binPath = nodeBrowserPage.getContentUrl();
                binPath = binPath.substring(binPath.indexOf("store"), binPath.lastIndexOf("bin"));
                binPath = binPath.replace("store://", "contentstore/");
            }
            else
            {
                logger.error("Nothing was found or there was found incorrect file by nodeRef");
            }
            return binPath;
        }
        catch (Exception e)
        {
           return null;
        }

    }

    public static void generateKeyStore(String keyStorePath, String keyStorePass, String newKey, String newKeyPassword) throws Exception
    {
        KEY_STORE_PASS = keyStorePass;
        PASS = newKeyPassword;
        ALIAS = newKey;

        String command = "keytool -genkey -dname cn=TestName,ou=QA,o=SomeCompany,L=GhostTown,ST=Uruguay,c=UY -alias " + ALIAS +
                " -keyalg " + ALGORITHM + " -keystore " + keyStorePath + " -keypass " + PASS + " -storepass " + KEY_STORE_PASS +
                " -keysize " + SIZE;

        RemoteUtil.executeCommand(command);

    }
}
