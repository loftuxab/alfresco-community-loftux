====================================
Alfresco Forms Development Kit (FDK)
====================================

Welcome to the Alfresco Forms Development Kit.

The Alfresco FDK provides support for developers who wish to extend or customise 
forms in Spring Surf based applications, for example, Share.

===============
Getting Started
===============

Installing the AMP
------------------

The AMP can be installed using the normal techniques, either with the Module Management Tool or
using the apply_amps script provided with the installer.

Installing the JAR
------------------

Place the JAR file in Share's WEB-INF/lib folder i.e. <tomcat>/webapps/share/WEB-INF/lib

Installing in the Dev Env
-------------------------

A devenv script called 'fdk' is available, the script allows the FDK repo and client files to be 
deployed in a packaged or exploded form. Run the script without any parameters for usage info.

Using the FDK
-------------

The AMP installs an example model and the JAR provides a 'Form Console' page, form configuration
for the example model and some Share specific configuration overrides.

The 'Change Type' action is Share will now have (for cm:content objects) three options, 'Gadget',
'Company Details' and 'Everything'. The 'Apply Aspect' action in Share will now have an 'Exif' 
aspect available.

The 'Form Console' page can be accessed via http://<host>:<port>/share/page/form-console

The FDK also provides an extension point (alfresco/web-extension/fdk-config-custom.xml) so that 
further form configuration can be provided and tested via the form console page. 

=======
Roadmap
=======

o Create DEV/GAV/FDK branch
o Provide ant targets to build springsurf JAR and AMP plus exploded targets for dev
o Include form console page
o Define custom model containing all types of properties and associations on types and aspects
o Define form config for model
o Include Share override to allow type to be changed and aspects added
o Add devenv command for FDK
o Setup build plan
o Add properties file so that types and aspects appear correctly in Share
o Write Getting Started section above
o Fix pickers in form console page (stop page submission)
- Test AMP/JAR deployment
o Test extension point config (add another form id)
- Merge to HEAD
- Send readme.txt file to Steve and cc. Paul & Mike


