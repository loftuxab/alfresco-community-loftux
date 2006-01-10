require 'xsd/qname'

# {http://www.alfresco.org/ws/service/content/1.0}read
class Read
  @@schema_type = "read"
  @@schema_ns = "http://www.alfresco.org/ws/service/content/1.0"
  @@schema_element = [["items", "Predicate"], ["property", "SOAP::SOAPString"]]

  attr_accessor :items
  attr_accessor :property

  def initialize(items = nil, property = nil)
    @items = items
    @property = property
  end
end

# {http://www.alfresco.org/ws/service/content/1.0}readResponse
class ReadResponse
  @@schema_type = "readResponse"
  @@schema_ns = "http://www.alfresco.org/ws/service/content/1.0"
  @@schema_element = [["content", "Content[]"]]

  attr_accessor :content

  def initialize(content = [])
    @content = content
  end
end

# {http://www.alfresco.org/ws/service/content/1.0}write
class Write
  @@schema_type = "write"
  @@schema_ns = "http://www.alfresco.org/ws/service/content/1.0"
  @@schema_element = [["node", "Reference"], ["property", "SOAP::SOAPString"], ["content", "SOAP::SOAPBase64"], ["format", "ContentFormat"]]

  attr_accessor :node
  attr_accessor :property
  attr_accessor :content
  attr_accessor :format

  def initialize(node = nil, property = nil, content = nil, format = nil)
    @node = node
    @property = property
    @content = content
    @format = format
  end
end

# {http://www.alfresco.org/ws/service/content/1.0}writeResponse
class WriteResponse
  @@schema_type = "writeResponse"
  @@schema_ns = "http://www.alfresco.org/ws/service/content/1.0"
  @@schema_element = [["content", "Content"]]

  attr_accessor :content

  def initialize(content = nil)
    @content = content
  end
end

# {http://www.alfresco.org/ws/service/content/1.0}clear
class Clear
  @@schema_type = "clear"
  @@schema_ns = "http://www.alfresco.org/ws/service/content/1.0"
  @@schema_element = [["items", "Predicate"], ["property", "SOAP::SOAPString"]]

  attr_accessor :items
  attr_accessor :property

  def initialize(items = nil, property = nil)
    @items = items
    @property = property
  end
end

# {http://www.alfresco.org/ws/service/content/1.0}clearResponse
class ClearResponse
  @@schema_type = "clearResponse"
  @@schema_ns = "http://www.alfresco.org/ws/service/content/1.0"
  @@schema_element = [["content", "Content[]"]]

  attr_accessor :content

  def initialize(content = [])
    @content = content
  end
end

# {http://www.alfresco.org/ws/service/content/1.0}ContentSegment
class ContentSegment
  @@schema_type = "ContentSegment"
  @@schema_ns = "http://www.alfresco.org/ws/service/content/1.0"
  @@schema_element = [["offset", "SOAP::SOAPLong"], ["length", "SOAP::SOAPLong"]]

  attr_accessor :offset
  attr_accessor :length

  def initialize(offset = nil, length = nil)
    @offset = offset
    @length = length
  end
end

# {http://www.alfresco.org/ws/service/content/1.0}Content
class Content
  @@schema_type = "Content"
  @@schema_ns = "http://www.alfresco.org/ws/service/content/1.0"
  @@schema_element = [["node", "Reference"], ["property", "SOAP::SOAPString"], ["length", "SOAP::SOAPLong"], ["format", "ContentFormat"], ["url", "SOAP::SOAPString"]]

  attr_accessor :node
  attr_accessor :property
  attr_accessor :length
  attr_accessor :format
  attr_accessor :url

  def initialize(node = nil, property = nil, length = nil, format = nil, url = nil)
    @node = node
    @property = property
    @length = length
    @format = format
    @url = url
  end
end

# {http://www.alfresco.org/ws/service/content/1.0}ContentFault
class ContentFault
  @@schema_type = "ContentFault"
  @@schema_ns = "http://www.alfresco.org/ws/service/content/1.0"
  @@schema_element = [["errorCode", "SOAP::SOAPInt"], ["message", "SOAP::SOAPString"]]

  attr_accessor :errorCode
  attr_accessor :message

  def initialize(errorCode = nil, message = nil)
    @errorCode = errorCode
    @message = message
  end
end
