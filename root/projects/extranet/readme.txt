The following files will need to be edited before using the application:


* /config/alfresco/extranet-application-context.xml

	-> adjust application properties to match your system
	-> adjust jdbc data source for web helpdesk
	-> adjust jdbc data source for extranet database
	-> adjust ldap context source
	-> adjust email properties (for network email account)
	-> adjust jira integration properties

* /config/alfresco/web-framework-config-application.xml

	-> adjust 'alfresco-system' endpoint properties
	-> adjust 'alfresco' endpoint properties

