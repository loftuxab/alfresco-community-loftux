
package org.alfresco.share.util;

import com.jcraft.jsch.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.SkipException;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author Sergey Kardash
 */
public class SshCommandProcessor extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(SshCommandProcessor.class);
    private final JSch jsch = new JSch();
    private Session session;

    public Session connect()
    {
        int i = 0;
        boolean result = false;
        while (!result)
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
                session.connect(5000);
                result = true;
            }
            catch (JSchException e)
            {
                i++;
                logger.error("Error handled during connection via ssh", e);

                if (i > 5)
                {
                    result = true;
                }
                if (sshHost == null)
                {
                    throw new SkipException("host mustn't be null", e);
                }
                // System.exit(1);
            }
        }
        return session;
    }

    public void disconnect()
    {
        if (session != null)
            session.disconnect();
    }

    public String executeCommand(String command)
    {
        StringBuilder rv = new StringBuilder();
        try
        {
            if (session == null || !session.isConnected())
            {
                connect();
            }
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            InputStream in = channel.getExtInputStream();
            channel.connect();
            byte[] tmp = new byte[1024];
            while (true)
            {
                int bytesRead;
                while ((bytesRead = in.read(tmp)) != -1)
                {
                    if (bytesRead < 0)
                    {
                        break;
                    }
                    rv.append(new String(tmp, 0, bytesRead, "UTF-8"));
                }

                if (channel.isClosed())
                {
                    break;
                }
                try
                {
                    Thread.sleep(100);
                }
                catch (Exception ee)
                {
                    logger.error("Error handled during execution command via ssh", ee);
                }
            }
        }
        catch (Exception ex)
        {
            rv.append(ex.getMessage());
        }
        return rv.toString();
    }

    public void connect(int timeOut)
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
            session.setServerAliveInterval(timeOut * 1000);
            session.connect(timeOut * 1000);
        }
        catch (JSchException e)
        {
            logger.error("Error handled during connection via ssh", e);
        }
    }

}
