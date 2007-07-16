Setting up the Alfresco Deployment Receiver
-------------------------------------------

1. Unzip alfresco-deployment.zip into a convenient location. (It does not
   make it's own directory.) For discussion's sake let's say that alfresco-deployment.zip
   has been unzipped into /opt/deployment.
   
2. Configure deployment.properties. Open deployment.properties in your
   text editor of choice.  Choose locations for each of the following:
   
   A. dep.datadir - This is the location that the deployment receiver stores
      deployed files during a deployment, before committing them to their final
      locations. For example:
        
        dep.datadir=/opt/deployment/depdata
   
   B. dep.logdir - This is the location in which the deployment receiver stores
      deployment time log data. For example:
        
        dep.logdir=/opt/deployment/deplog
        
   C. dep.metadatadir - This is the location in which the deployment receiver stores
      metadata about deployed content. For example:
      
        dep.metadatadir=/opt/deployment/depmetadata
        
   D. dep.rmi.port - The port number to use for RMI. Choose this so as not to 
      conflict with any other services.
      
3. Configure application-context.xml. Open the file in a text editor and modify
   the Target Configuration section. You'll want to modify this section:
   
    <entry key="default">
        <map>
            <entry key="root"><value>target</value></entry>
            <entry key="user"><value>admin</value></entry>
            <entry key="password"><value>admin</value></entry>
            <entry key="runnable"><value>org.alfresco.deployment.SampleRunnable</value></entry>
            <!-- 
            <entry key="program"><value>/path/to/program</value></entry>
             -->
        </map>
    </entry>

    Replace 'target' in the key="root" entry with the location of the top
    level directory to deploy to. For example:

            <entry key="root"><value>/opt/www</value></entry>
    
    Replace the "user" and "password" entries with a user name and password of 
    your choosing. (Note: The user name and password does not need to be an os or alfresco
    user and password.)
    
    Optionally replace the key="runnable" with a custom java class implementing
    the org.alfresco.deployment.FSDeploymentRunnable interface. This will run after each 
    successful deployment. For example:

    <entry key="runnable"><value>uk.co.your.NotifyMe</value></entry>

    or
    
    Optionally uncomment and replace the key="program" entry with the path to an arbitrary 
    program to run after each successful deployment. For example:
    
            <entry key="program"><value>/opt/bin/notify.pl</value></entry>
            
    Leave only one of "runnable" or "program" uncommented. By default a "runnable" is
    looked for and executed. If that is not found then "program" is looked for and
    executed.

4. Configure start.sh (or start.bat). Adjust the classpath so that all the listed jars have
   the correct path prefix. For example:
   
nohup java -server -cp /opt/deployment/alfresco-deployment.jar:/opt/deployment/spring-2.0.2.jar:/opt/deployment/commons-logging-1.0.4.jar:/opt/deployment/alfresco-core.jar:/opt/deployment/jug.jar:/opt/deployment org.alfresco.deployment.Main /opt/deployment/application-context.xml >/opt/deployment/deployment.log 2>&1 &

   Not the final member of the classpath: /opt/deployment. This is necessary for the
   deployment receiver to properly configure itself.

5. Run the receiver. Execute start.sh (or start.bat) as the user you want your deployed
   content to be owned by.
   
   