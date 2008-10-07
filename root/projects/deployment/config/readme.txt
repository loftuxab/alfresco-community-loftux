Setting up the Alfresco File System Receiver
--------------------------------------------

1. Unzip the deployment zip file into a convenient location. (It does not
   make it's own directory.) For discussion's sake let's say that the zip file
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
        
   D. dep.rmi.port - The port number to use for the RMI registry. Choose this so as not to
      conflict with any other services.
      
   E. dep.rmi.service.port - The port number to use for RMI service. Choose this so as not to
      conflict with any other services.
      
3. Configure application-context.xml. Open the file in a text editor and modify
   the Target Configuration section. 

   You'll want to modify this section:
    <entry key="default">
        <map>
            <entry key="root"><value>target</value></entry>
            <entry key="user"><value>admin</value></entry>
            <entry key="password"><value>admin</value></entry>
        </map>
    </entry>

    Replace 'target' in the key="root" entry with the location of the top
    level directory to deploy to. For example:

            <entry key="root"><value>/opt/www</value></entry>
    
    Replace the "user" and "password" entries with a user name and password of 
    your choosing. (Note: The user name and password does not need to be an os or alfresco
    user and password.)
    
4. Define your custom runnables
   If you have any custom runnables, use the configuration of the sampleProgramRunnable as a template.
   
   Define your runnable bean
   <bean id="myBean" class="xxx.Myclass">
         <property name="exampleProperty">
         	<value>Hello World</value>
         </property>
    </bean>
     
    add your runnable beans to the target, like so
     
                     <entry key="runnables">
                         <list>
                             <ref bean="sampleRunnable"/>
                             <ref bean="myBean"/>
                             <ref bean="superBean"/>
                         </list>
                     </entry>

  
5. Run the receiver. Execute deploy_start.sh (or deploy_start.bat) as the user you want your deployed
   content to be owned by.
   
   