/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.share.util;

import com.jcraft.jsch.*;
import org.alfresco.po.share.util.PageUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.TimeoutException;

import java.io.*;
import java.util.Properties;
import java.util.Vector;

/**
 * Created by olga Lokhach
 */
public class NfsUtil extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(NfsUtil.class);
    private static final String JMX_FILE_SERVERS_CONFIG = "Alfresco:Type=Configuration,Category=fileServers,id1=default";
    private static final String NFS_STOP = "stop";
    private static final String NFS_START = "start";
    private static final String NFS_ENABLED = "nfs.enabled";
    private static final String NFS_MOUNT_PORT = "nfs.mountServerPort";
    private static final String NFS_SERVER_PORT = "nfs.nfsServerPort";
    private static final String NFS_USER_MAPPINGS = "nfs.user.mappings";
    private static final String NFS_PORT_MAPPER_ENABLED = "nfs.portMapperEnabled";
    private static final String USER_GID = "gid";
    private static final String USER_UID = "uid";
    private static final JSch jsch = new JSch();
    private static Session session;
    private static ChannelSftp channel;

    /**
     * Config Nfs server
     */
    public static void configNfsServer(String shareUrl, String superUser, String userName)
    {
        String server = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "");

        JmxUtils.invokeAlfrescoServerProperty(server, JMX_FILE_SERVERS_CONFIG, NFS_STOP);
        JmxUtils.setAlfrescoServerProperty(server, JMX_FILE_SERVERS_CONFIG, NFS_SERVER_PORT, nfsPort);
        JmxUtils.setAlfrescoServerProperty(server, JMX_FILE_SERVERS_CONFIG, NFS_MOUNT_PORT, nfsMountPort);
        JmxUtils.setAlfrescoServerProperty(server, JMX_FILE_SERVERS_CONFIG, NFS_PORT_MAPPER_ENABLED, true);
        JmxUtils.setAlfrescoServerProperty(server, JMX_FILE_SERVERS_CONFIG, NFS_USER_MAPPINGS, superUser + "," + userName);
        JmxUtils.setAlfrescoServerProperty(server, JMX_FILE_SERVERS_CONFIG, NFS_ENABLED, true);
        JmxUtils.invokeAlfrescoServerProperty(server, JMX_FILE_SERVERS_CONFIG, NFS_START);
    }

    /**
     * Config Nfs user
     */
    public static void configNfsUser(String shareUrl, String userName)
    {
        String server = PageUtils.getAddress(shareUrl).replaceAll("(:\\d{1,5})?", "");

        JmxUtils.invokeAlfrescoServerProperty(server, "Alfresco:Type=Configuration,Category=fileServers,id1=default,id2=nfs.user.mappings,id3=" + userName, NFS_STOP);
        JmxUtils.setAlfrescoServerProperty(server,"Alfresco:Type=Configuration,Category=fileServers,id1=default,id2=nfs.user.mappings,id3=" + userName, USER_GID,
            "500");
        JmxUtils
            .setAlfrescoServerProperty(server,"Alfresco:Type=Configuration,Category=fileServers,id1=default,id2=nfs.user.mappings,id3=" + userName, USER_UID, "1000");
        JmxUtils.invokeAlfrescoServerProperty(server,"Alfresco:Type=Configuration,Category=fileServers,id1=default,id2=nfs.user.mappings,id3=" + userName, NFS_START);
        JmxUtils.invokeAlfrescoServerProperty(server, JMX_FILE_SERVERS_CONFIG, NFS_STOP);
        JmxUtils.invokeAlfrescoServerProperty(server, JMX_FILE_SERVERS_CONFIG, NFS_START);
    }

    private static ChannelSftp SFTPConnection(String sshHost, String serverUser, String serverPass)
    {

        try
        {
            if (isSecureSession)
            {
                jsch.addIdentity(pathToKeys, "passphrase");
                session = jsch.getSession(serverUser, sshHost, serverShhPort);
            }
            else
            {
                session = jsch.getSession(serverUser, sshHost, serverShhPort);
                session.setPassword(serverPass);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setServerAliveInterval(50000);
            logger.info("try ssh connect");
            session.connect(60000);
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            boolean success = channel.isConnected();
            if (!success)
            {
                throw new RuntimeException("Can't connect to " + sshHost);
            }

        }
        catch (JSchException e)
        {
            e.printStackTrace();
            logger.info(e);
        }
        return channel;
    }

    /**
     * Method to get list of remote objects from NFS
     *
     * @param serverIP
     * @param user
     * @param password
     * @param remoteObject
     * @param pathToNFS
     * @return true if object is exist
     */

    public static boolean isObjectExists(String serverIP, String user, String password, String pathToNFS, String remoteObject)
    {

        try
        {
            channel = SFTPConnection(serverIP, user, password);

            if (channel.isConnected())
            {
                channel.cd(pathToNFS);
                Vector<ChannelSftp.LsEntry> list = channel.ls("*");

                for (ChannelSftp.LsEntry entry : list)
                {
                    if (entry.getFilename().endsWith(remoteObject))
                    {
                        return true;
                    }
                }
                channel.disconnect();
                session.disconnect();
            }
        }
        catch (SftpException ex)
        {
            logger.error(ex.getMessage());
        }
        return false;
    }

    public static boolean renameFileOrFolder(String serverIP, String user, String password, String pathToNFS, String oldName, String newName)
    {
        boolean successful = false;

        try
        {
            channel = SFTPConnection(serverIP, user, password);

            if (channel.isConnected())
            {
                channel.cd(pathToNFS);
                channel.rename(oldName, newName);
                logger.info("Item renamed!");
                successful = true;
            }

        }
        catch (SftpException ex)
        {
            logger.error(ex.getMessage());
        }
        finally
        {
            channel.disconnect();
            session.disconnect();
        }
        return successful;
    }

    public static boolean editContent(String serverIP, String user, String password, String pathToNFS, String filename, String content)
    {
        boolean successful = false;
        OutputStream outputStream;

        try
        {
            channel = SFTPConnection(serverIP, user, password);

            if (channel.isConnected())
            {
                channel.cd(pathToNFS);
            }
            if (isObjectExists(serverIP, user, password, pathToNFS, filename))
            {
                try
                {
                    outputStream = channel.put(filename);
                    outputStream.write(content.getBytes());
                    outputStream.flush();
                    outputStream.close();
                    successful = true;
                }
                catch (SftpException ex)
                {
                    logger.error(ex.getMessage());
                }
            }
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage());
        }
        finally
        {
            channel.disconnect();
            session.disconnect();
        }

        return successful;
    }

    /**
     * Method to delete a remote content
     *
     * @param serverIP
     * @param user
     * @param password
     * @param pathToNFS
     * @param filename
     * @return true if a content is deleted
     */

    public static boolean deleteContentItem(String serverIP, String user, String password, String pathToNFS, String filename)
    {

        boolean successful = false;

        try
        {
            channel = SFTPConnection(serverIP, user, password);

            if (channel.isConnected())
            {
                channel.cd(pathToNFS);
                channel.rm(filename);
                logger.info("Content deleted!");
                successful = true;
            }
        }
        catch (SftpException ex)
        {
            logger.error(ex.getMessage());
            successful = false;
        }
        finally
        {
            channel.disconnect();
            session.disconnect();
        }
        return successful;
    }

    /**
     * Method to create a remote folder
     *
     * @param serverIP
     * @param user
     * @param password
     * @param pathToNFS
     * @param folderName
     * @return true if folder is created
     */

    public static boolean createSpace(String serverIP, String user, String password, String pathToNFS, String folderName)
    {

        boolean successful = false;

        try
        {
            channel = SFTPConnection(serverIP, user, password);

            if (channel.isConnected())
            {
                channel.cd(pathToNFS);
                channel.mkdir(folderName);
                logger.info("Folder created!");
                successful = true;
            }
        }
        catch (SftpException ex)
        {
            logger.error("Seem access denied");
            successful = false;
        }
        finally
        {
            channel.disconnect();
            session.disconnect();
        }
        return successful;
    }

    /**
     * Method to upload a content
     *
     * @param serverIP
     * @param user
     * @param password
     * @param pathToNFS
     * @param contentName
     * @return true if content is uploaded
     */
    public static boolean uploadContent(String serverIP, String user, String password, String pathToNFS, File contentName)
    {

        InputStream inputStream;
        OutputStream outputStream;
        boolean result = false;



        try
        {
            channel = SFTPConnection(serverIP, user, password);

            if (channel.isConnected())
            {
                channel.cd(pathToNFS);
                inputStream = new FileInputStream(contentName);
                outputStream = channel.put(contentName.getName());

                if (outputStream != null)
                {

                    byte[] buffer = new byte[4096];
                    int l;
                    logger.info("Starting upload file[" + contentName.getName() + "]");
                    while ((l = inputStream.read(buffer)) != -1)
                    {
                        outputStream.write(buffer, 0, l);
                    }
                    logger.info("Content uploaded!");
                    inputStream.close();
                    outputStream.flush();
                    outputStream.close();
                    result = true;
                }
                else
                {
                    logger.error("Seem access denied");
                }
            }
        }
        catch (SftpException e)
        {
            logger.error(e.getMessage());
        }

        catch (IOException ex)
        {
            logger.error(ex.getMessage());
        }
        finally
        {
            channel.disconnect();
            session.disconnect();
        }

        return result;
    }

    /**
     * Method to delete a remote folder
     *
     * @param serverIP
     * @param user
     * @param password
     * @param pathToNFS
     * @param folderName
     * @return true if folder is deleted
     */

    public static boolean deleteFolder(String serverIP, String user, String password, String pathToNFS, String folderName)
    {

        boolean successful = false;

        try
        {
            channel = SFTPConnection(serverIP, user, password);

            if (channel.isConnected())
            {
                channel.cd(pathToNFS + folderName);
                Vector<ChannelSftp.LsEntry> list = channel.ls("*");

                if (list.size() == 0)
                {
                    channel.cd("..");
                    channel.rmdir(folderName);
                    logger.info("Folder deleted!");
                    successful = true;
                }

                else
                {
                    for (ChannelSftp.LsEntry listItem : list)
                    {
                        if (!listItem.getAttrs().isDir())
                        {
                            channel.rm(listItem.getFilename());
                            logger.info("Content deleted!");
                        }
                        else
                        {
                            emptyFolderContents(channel, listItem.getFilename());
                        }
                    }
                }
                channel.cd("..");
                channel.rmdir(folderName);
                logger.info("Folder deleted!");
                successful = true;
            }

        }
        catch (SftpException ex)
        {
            logger.error(ex.getMessage());
            successful = false;
        }
        finally
        {
            channel.disconnect();
            session.disconnect();
        }
        return successful;
    }

    private static void emptyFolderContents(ChannelSftp channel, String folderName)
    {
        try
        {
            if (channel.isConnected())
            {
                channel.cd(folderName);
                Vector<ChannelSftp.LsEntry> list = channel.ls("*");

                if (list.size() == 0)
                {
                    channel.cd("..");
                    channel.rmdir(folderName);
                    logger.info("Folder deleted!");
                    return;
                }

                else
                {

                    for (ChannelSftp.LsEntry listItem : list)
                    {
                        if (!listItem.getAttrs().isDir())
                        {
                            channel.rm(listItem.getFilename());
                        }
                        else
                        {
                            emptyFolderContents(channel, listItem.getFilename());
                        }
                    }
                }
            }

        }
        catch (SftpException ex)
        {
            logger.error("Seem access denied");
        }
    }

    /**
     * Method to get a remote content
     *
     * @param serverIP
     * @param user
     * @param password
     * @param remoteContentName
     * @param pathToNFS
     * @return String message
     */

    public static String getContent(String serverIP, String user, String password, String pathToNFS, String remoteContentName)
    {
        StringBuilder content = new StringBuilder();
        BufferedReader reader;
        String inputLine;

        try
        {
            channel = SFTPConnection(serverIP, user, password);

            if (channel.isConnected())
            {
                channel.cd(pathToNFS);
            }
            if (isObjectExists(serverIP, user, password, pathToNFS, remoteContentName))
            {
                try
                {
                    InputStream inputStream = channel.get(remoteContentName);
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    while ((inputLine = reader.readLine()) != null)
                    {
                        content.append(inputLine);
                    }
                    reader.close();
                }
                catch (IOException ex)
                {
                    throw new RuntimeException(ex.getMessage());
                }
            }
        }
        catch (SftpException ex)
        {
            logger.error(ex.getMessage());
        }

        finally
        {
            channel.disconnect();
            session.disconnect();
        }

        return content.toString();
    }

    /**
     * Method to copy a folder
     *
     * @param serverIP
     * @param user
     * @param password
     * @param remoteFolderName
     * @param remoteFolderPath
     * @param destination
     */

    public static boolean copyFolder(String serverIP, String user, String password, String remoteFolderPath, String remoteFolderName, String destination)

    {
        boolean successful = false;
        InputStream inputStream;
        ByteArrayOutputStream outputStream;

        try
        {
            channel = SFTPConnection(serverIP, user, password);

            if (channel.isConnected())
            {
                channel.cd(remoteFolderPath + remoteFolderName);
                Vector<ChannelSftp.LsEntry> list = channel.ls("*");

                if (list.size() == 0)
                {
                    channel.cd(destination);
                    channel.mkdir(remoteFolderName);
                    successful = true;
                }

                else

                {
                    for (ChannelSftp.LsEntry listItem : list)
                    {
                        if (!channel.pwd().startsWith(remoteFolderPath))
                        {
                            channel.cd(remoteFolderPath + remoteFolderName);
                        }

                        if (!listItem.getAttrs().isDir())
                        {
                            try
                            {
                                outputStream = new ByteArrayOutputStream();
                                channel.get(listItem.getFilename(), outputStream);
                                inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                                channel.cd(destination);
                                list = channel.ls("*");
                                if (!list.contains(remoteFolderName))
                                {
                                    channel.mkdir(remoteFolderName);
                                }
                                channel.cd(destination + remoteFolderName);
                                channel.put(inputStream, listItem.getFilename());
                                inputStream.close();
                                outputStream.close();

                            }
                            catch (SftpException ex)
                            {
                                throw new RuntimeException(ex.getMessage());
                            }
                            catch (IOException e)
                            {
                                throw new RuntimeException(e.getMessage());
                            }
                        }
                        else
                        {
                            copyFolderContents(channel, remoteFolderPath, listItem.getFilename(), destination);
                        }
                    }
                }
            }
            successful = true;
        }
        catch (SftpException ex)
        {
            throw new RuntimeException(ex.getMessage());

        }
        finally
        {
            channel.disconnect();
            session.disconnect();
        }
        return successful;
    }

    private static void copyFolderContents(ChannelSftp channel, String remoteFolderPath, String folderName, String destination)
    {
        InputStream inputStream;
        ByteArrayOutputStream outputStream;

        try
        {
            if (channel.isConnected())
            {
                String newDestination = destination + channel.pwd().replace(remoteFolderPath, "");
                channel.cd(folderName);
                Vector<ChannelSftp.LsEntry> list = channel.ls("*");

                if (list.size() == 0)
                {
                    channel.cd(newDestination);
                    channel.mkdir(folderName);
                    return;
                }

                else

                {
                    for (ChannelSftp.LsEntry listItem : list)
                    {
                        if (!listItem.getAttrs().isDir())
                        {
                            try
                            {
                                outputStream = new ByteArrayOutputStream();
                                channel.get(listItem.getFilename(), outputStream);
                                inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                                channel.cd(newDestination);
                                list = channel.ls("*");
                                if (!list.contains(folderName))
                                {
                                    channel.mkdir(folderName);
                                }
                                channel.cd(newDestination + folderName);
                                channel.put(inputStream, listItem.getFilename());
                                inputStream.close();
                                outputStream.close();
                                return;
                            }
                            catch (SftpException ex)
                            {
                                throw new RuntimeException(ex.getMessage());
                            }
                            catch (IOException e)
                            {
                                throw new RuntimeException(e.getMessage());
                            }
                        }
                        else
                        {
                            copyFolderContents(channel, remoteFolderPath, listItem.getFilename(), destination);
                        }
                    }
                }
            }
        }

        catch (SftpException ex)
        {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * Method to move a folder
     *
     * @param serverIP
     * @param user
     * @param password
     * @param remoteFolderName
     * @param remoteFolderPath
     * @param destination
     */

    public static boolean moveFolder(String serverIP, String user, String password, String remoteFolderPath, String remoteFolderName, String destination)
    {
        boolean successful = false;

        try
        {
            successful = copyFolder(serverIP, user, password, remoteFolderPath, remoteFolderName, destination)
                && deleteFolder(serverIP, user, password, remoteFolderPath, remoteFolderName);
            return successful;
        }
        catch (TimeoutException e)
        {

        }
        return successful;

    }




}
