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
