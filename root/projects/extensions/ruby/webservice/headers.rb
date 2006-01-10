require 'xsd/qname'

# {http://www.alfresco.org/ws/headers/1.0}QueryConfiguration
class QueryConfiguration
  @@schema_type = "QueryConfiguration"
  @@schema_ns = "http://www.alfresco.org/ws/headers/1.0"
  @@schema_element = [["fetchSize", "Int"]]

  attr_accessor :fetchSize

  def initialize(fetchSize = nil)
    @fetchSize = fetchSize
  end
end

# {http://www.alfresco.org/ws/headers/1.0}LocaleConfiguration
class LocaleConfiguration
  @@schema_type = "LocaleConfiguration"
  @@schema_ns = "http://www.alfresco.org/ws/headers/1.0"
  @@schema_element = [["locale", "String"]]

  attr_accessor :locale

  def initialize(locale = nil)
    @locale = locale
  end
end

# {http://www.alfresco.org/ws/headers/1.0}NamespaceConfiguration
class NamespaceConfiguration < ::Array
  @@schema_type = "QualifiedName"
  @@schema_ns = "http://www.alfresco.org/ws/headers/1.0"
end

# {http://www.alfresco.org/ws/headers/1.0}QualifiedName
class QualifiedName
  @@schema_type = "QualifiedName"
  @@schema_ns = "http://www.alfresco.org/ws/headers/1.0"
  @@schema_element = [["prefix", "NCName"], ["uri", "String"]]

  attr_accessor :prefix
  attr_accessor :uri

  def initialize(prefix = nil, uri = nil)
    @prefix = prefix
    @uri = uri
  end
end
