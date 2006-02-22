#!/usr/bin/env ruby

require 'soap/header/simplehandler'

require 'authentication-serviceDriver.rb'
require 'repository-serviceDriver.rb'

#
# WSSecurity header handler class
#
class WSSecurityHandler < SOAP::Header::SimpleHandler
      
      HeaderName        = XSD::QName.new('http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd', 'Security')
      TimeStamp         = XSD::QName.new('http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd', 'Timestamp')
      Created           = XSD::QName.new('http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd', 'Created')
      Expires           = XSD::QName.new('http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd', 'Expires')
      UserNameToken     = XSD::QName.new('http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd', 'UsernameToken')
      UserName          = XSD::QName.new('http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd', 'Username')
      Password          = XSD::QName.new('http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd', 'Password')

      def initialize(ticket, user)
          super(HeaderName)
          @ticket = ticket
          @user = user
          @mustunderstand = true;
      end

      def on_simple_outbound
          
          ## I've has to do this as a string because the the timestamp always returns in the incorrect order if 
          ## you return a hashtable from here!

          created = Time.now.strftime("%Y-%m-%dT%H:%M:%SZ")
          expires = (Time.now + 60*60).strftime("%Y-%m-%dT%H:%M:%SZ")

          "<" + TimeStamp.name + " xmlns='" + TimeStamp.namespace + "'>
            <" + Created.name + ">" + created + "</" + Created.name + ">
            <" + Expires.name + ">" + expires + "</" + Expires.name + ">
          </" + TimeStamp.name + ">
          <" + UserNameToken.name + ">
            <" + Password.name + ">" + @ticket + "</" + Password.name + ">
            <" + UserName.name + ">" + @user + "</" + UserName.name + ">
          </" + UserNameToken.name + ">"
      end
end

# Get the web services
authenticationService = AuthenticationServiceSoapPort.new()
obj = RepositoryServiceSoapPort.new()

# Uncomment the below line to see SOAP wiredumps.
# obj.wiredump_dev = STDERR

# Get the ticket
startSessionParams = StartSession.new("admin", "admin")
startSessionResponce = authenticationService.startSession(startSessionParams)
ticket = startSessionResponce.startSessionReturn.ticket

# Print the ticket
print "Ticket: " + ticket + "\n";

# Add the security header
obj.headerhandler << WSSecurityHandler.new(ticket, "admin")

# Get the stores
getStoresResult = obj.getStores(GetStores.new());

getStoresResult.getStoresReturn.each do |store|
   print store.scheme + " - " + store.address + "\n"
end




