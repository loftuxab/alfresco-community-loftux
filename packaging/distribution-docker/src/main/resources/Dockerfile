FROM ubuntu
MAINTAINER Gethin James, gethin.james@alfresco.com

ENV LANG C.UTF-8
ARG alfUrl
ARG alfInstallerVer
ARG alfVer
ENV ALF_URL ${alfUrl:-https://nightlybuilds.alfresco.com/Enterprise-${alfVer}/${alfVer}.N/LATEST/ALL/alfresco-content-services-installer-${alfInstallerVer}-linux-x64.bin
ENV ALF_INSTALLER_DIR /alfresco/installer
ENV ALF_INSTALLER_BIN $ALF_INSTALLER_DIR/installer.bin
ENV ALFRESCO_HOME /opt/alfresco
ENV GLOBAL_PROPS $ALFRESCO_HOME/tomcat/shared/classes/alfresco-global.properties

RUN apt-get update && apt-get install -q -y unzip wget curl

RUN mkdir -p $ALF_INSTALLER_DIR && mkdir -p $ALFRESCO_HOME
ADD install_options.txt $ALF_INSTALLER_DIR/
ADD docker_start $ALFRESCO_HOME/
RUN chmod +x $ALFRESCO_HOME/docker_start

# Ensure a log file exists ready for tail
RUN touch $ALFRESCO_HOME/alfresco.log

COPY enablecors*.jar $ALFRESCO_HOME/modules/platform/
COPY api-explorer*.war $ALFRESCO_HOME/tomcat/webapps/api-explorer.war

RUN wget --no-check-certificate -O "$ALF_INSTALLER_BIN" "$ALF_URL" \
   && chmod +x $ALF_INSTALLER_BIN; sync \
   && .$ALF_INSTALLER_BIN --optionfile $ALF_INSTALLER_DIR/install_options.txt \
   && mkdir -p $ALFRESCO_HOME/alf_data \
   && rm -r $ALF_INSTALLER_DIR \
   && $ALFRESCO_HOME/java/bin/java -jar $ALFRESCO_HOME/bin/alfresco-mmt.jar install $ALFRESCO_HOME/amps $ALFRESCO_HOME/tomcat/webapps/alfresco.war -directory -verbose -nobackup

EXPOSE 8080 8443

WORKDIR $ALFRESCO_HOME

# Setup alf_data
VOLUME /opt/alfresco/alf_data
RUN sed -i 's:^[ \t]*dir.root[ \t]*=\([ \t]*.*\)$:dir.root='/opt/alfresco/alf_data':' $GLOBAL_PROPS

# Set Debugging on port 5005
RUN sed -i "s/home=\/opt\/alfresco /home=\/opt\/alfresco -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 /g" $ALFRESCO_HOME/tomcat/bin/setenv.sh
EXPOSE 5005

# Set JMX on with port 7203 , connect via service:jmx:rmi:///jndi/rmi://localhost:7203/jmxrmi
RUN sed -i "s/headless=true /headless=true -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.port=7203 -Dcom.sun.management.jmxremote.rmi.port=7203 -Dcom.sun.management.jmxremote.ssl=false /g" $ALFRESCO_HOME/tomcat/bin/setenv.sh
EXPOSE 7203

# Run the container's process!
CMD /bin/bash -c $ALFRESCO_HOME/docker_start
