#!/usr/bin/env ruby

require 'soap/header/simplehandler'

require 'authentication-serviceDriver.rb'
require 'repository-serviceDriver.rb'

#
# WSSecurity header handler class
#
class WSSecurityHandler < SOAP::Header::SimpleHandler
      
      HeaderName        = XSD::QName.new('http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd', 'Security')
      UserNameToken     = XSD::QName.new('http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd', 'UsernameToken')
      UserName          = XSD::QName.new('http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd', 'Username')
      Password          = XSD::QName.new('http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd', 'Password')

      def initialize(ticket, user)
          super(HeaderName)
          @ticket = ticket
          @user = user
      end

      def on_simple_outbound
          {UserNameToken => {UserName => @user, Password => @ticket}}
      end
end

# Get the web services
authenticationService = AuthenticationServiceSoapPort.new()
obj = RepositoryServiceSoapPort.new()

# Uncomment the below line to see SOAP wiredumps.
# obj.wiredump_dev = STDERR

# Get the ticket
startSessionParams = StartSession.new("rwetherall", "31vegaleg")
startSessionResponce = authenticationService.startSession(startSessionParams)
ticket = startSessionResponce.startSessionReturn.ticket

# Print the ticket
print "Ticket: " + ticket + "\n";

# Add the security header
obj.headerhandler << WSSecurityHandler.new(ticket, "rwetherall")

# Get the stores
getStoresResult = obj.getStores(GetStores.new());

getStoresResult.getStoresReturn.each do |store|
   print store.scheme + " - " + store.address + "\n"
end




