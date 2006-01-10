require 'xsd/qname'

# {http://www.alfresco.org/ws/service/authentication/1.0}startSession
class StartSession
  @@schema_type = "startSession"
  @@schema_ns = "http://www.alfresco.org/ws/service/authentication/1.0"
  @@schema_element = [["username", "SOAP::SOAPString"], ["password", "SOAP::SOAPString"]]

  attr_accessor :username
  attr_accessor :password

  def initialize(username = nil, password = nil)
    @username = username
    @password = password
  end
end

# {http://www.alfresco.org/ws/service/authentication/1.0}startSessionResponse
class StartSessionResponse
  @@schema_type = "startSessionResponse"
  @@schema_ns = "http://www.alfresco.org/ws/service/authentication/1.0"
  @@schema_element = [["startSessionReturn", "AuthenticationResult"]]

  attr_accessor :startSessionReturn

  def initialize(startSessionReturn = nil)
    @startSessionReturn = startSessionReturn
  end
end

# {http://www.alfresco.org/ws/service/authentication/1.0}endSession
class EndSession
  @@schema_type = "endSession"
  @@schema_ns = "http://www.alfresco.org/ws/service/authentication/1.0"
  @@schema_element = []

  def initialize
  end
end

# {http://www.alfresco.org/ws/service/authentication/1.0}endSessionResponse
class EndSessionResponse
  @@schema_type = "endSessionResponse"
  @@schema_ns = "http://www.alfresco.org/ws/service/authentication/1.0"
  @@schema_element = []

  def initialize
  end
end

# {http://www.alfresco.org/ws/service/authentication/1.0}AuthenticationResult
class AuthenticationResult
  @@schema_type = "AuthenticationResult"
  @@schema_ns = "http://www.alfresco.org/ws/service/authentication/1.0"
  @@schema_element = [["username", "SOAP::SOAPString"], ["ticket", "SOAP::SOAPString"]]

  attr_accessor :username
  attr_accessor :ticket

  def initialize(username = nil, ticket = nil)
    @username = username
    @ticket = ticket
  end
end

# {http://www.alfresco.org/ws/service/authentication/1.0}AuthenticationFault
class AuthenticationFault
  @@schema_type = "AuthenticationFault"
  @@schema_ns = "http://www.alfresco.org/ws/service/authentication/1.0"
  @@schema_element = [["errorCode", "SOAP::SOAPInt"], ["message", "SOAP::SOAPString"]]

  attr_accessor :errorCode
  attr_accessor :message

  def initialize(errorCode = nil, message = nil)
    @errorCode = errorCode
    @message = message
  end
end
