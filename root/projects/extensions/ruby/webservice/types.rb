require 'xsd/qname'

# {http://www.alfresco.org/ws/model/content/1.0}Store
class Store
  @@schema_type = "Store"
  @@schema_ns = "http://www.alfresco.org/ws/model/content/1.0"
  @@schema_element = [["scheme", "SOAP::SOAPString"], ["address", "SOAP::SOAPString"]]

  attr_accessor :scheme
  attr_accessor :address

  def initialize(scheme = nil, address = nil)
    @scheme = scheme
    @address = address
  end
end

# {http://www.alfresco.org/ws/model/content/1.0}Reference
class Reference
  @@schema_type = "Reference"
  @@schema_ns = "http://www.alfresco.org/ws/model/content/1.0"
  @@schema_element = [["store", "Store"], ["uuid", "SOAP::SOAPString"], ["path", "SOAP::SOAPString"]]

  attr_accessor :store
  attr_accessor :uuid
  attr_accessor :path

  def initialize(store = nil, uuid = nil, path = nil)
    @store = store
    @uuid = uuid
    @path = path
  end
end

# {http://www.alfresco.org/ws/model/content/1.0}ParentReference
class ParentReference
  @@schema_type = "ParentReference"
  @@schema_ns = "http://www.alfresco.org/ws/model/content/1.0"
  @@schema_element = []

  def initialize
  end
end

# {http://www.alfresco.org/ws/model/content/1.0}ClassDefinition
class ClassDefinition
  @@schema_type = "ClassDefinition"
  @@schema_ns = "http://www.alfresco.org/ws/model/content/1.0"
  @@schema_element = [["name", "SOAP::SOAPString"], ["superClass", "SOAP::SOAPString"], ["isAspect", "SOAP::SOAPBoolean"], ["properties", "PropertyDefinition[]"], ["associations", "AssociationDefinition[]"]]

  attr_accessor :name
  attr_accessor :superClass
  attr_accessor :isAspect
  attr_accessor :properties
  attr_accessor :associations

  def initialize(name = nil, superClass = nil, isAspect = nil, properties = [], associations = [])
    @name = name
    @superClass = superClass
    @isAspect = isAspect
    @properties = properties
    @associations = associations
  end
end

# {http://www.alfresco.org/ws/model/content/1.0}ValueDefinition
class ValueDefinition
  @@schema_type = "ValueDefinition"
  @@schema_ns = "http://www.alfresco.org/ws/model/content/1.0"
  @@schema_element = [["name", "SOAP::SOAPString"], ["dataType", "SOAP::SOAPString"]]

  attr_accessor :name
  attr_accessor :dataType

  def initialize(name = nil, dataType = nil)
    @name = name
    @dataType = dataType
  end
end

# {http://www.alfresco.org/ws/model/content/1.0}PropertyDefinition
class PropertyDefinition
  @@schema_type = "PropertyDefinition"
  @@schema_ns = "http://www.alfresco.org/ws/model/content/1.0"
  @@schema_element = []

  def initialize
  end
end

# {http://www.alfresco.org/ws/model/content/1.0}AssociationDefinition
class AssociationDefinition
  @@schema_type = "AssociationDefinition"
  @@schema_ns = "http://www.alfresco.org/ws/model/content/1.0"
  @@schema_element = [["name", "SOAP::SOAPString"], ["isChild", "SOAP::SOAPBoolean"], ["sourceRole", "RoleDefinition"], ["targetRole", "RoleDefinition"], ["targetClass", "SOAP::SOAPString"]]

  attr_accessor :name
  attr_accessor :isChild
  attr_accessor :sourceRole
  attr_accessor :targetRole
  attr_accessor :targetClass

  def initialize(name = nil, isChild = nil, sourceRole = nil, targetRole = nil, targetClass = nil)
    @name = name
    @isChild = isChild
    @sourceRole = sourceRole
    @targetRole = targetRole
    @targetClass = targetClass
  end
end

# {http://www.alfresco.org/ws/model/content/1.0}RoleDefinition
class RoleDefinition
  @@schema_type = "RoleDefinition"
  @@schema_ns = "http://www.alfresco.org/ws/model/content/1.0"
  @@schema_element = [["name", "SOAP::SOAPString"], ["cardinality", "SOAP::SOAPString"]]

  attr_accessor :name
  attr_accessor :cardinality

  def initialize(name = nil, cardinality = nil)
    @name = name
    @cardinality = cardinality
  end
end

# {http://www.alfresco.org/ws/model/content/1.0}NodeDefinition
class NodeDefinition
  @@schema_type = "NodeDefinition"
  @@schema_ns = "http://www.alfresco.org/ws/model/content/1.0"
  @@schema_element = [["type", "ClassDefinition"], ["aspects", "ClassDefinition[]"]]

  attr_accessor :type
  attr_accessor :aspects

  def initialize(type = nil, aspects = [])
    @type = type
    @aspects = aspects
  end
end

# {http://www.alfresco.org/ws/model/content/1.0}Node
class Node
  @@schema_type = "Node"
  @@schema_ns = "http://www.alfresco.org/ws/model/content/1.0"
  @@schema_element = [["reference", "Reference"], ["properties", "NamedValue[]"]]

  attr_accessor :reference
  attr_accessor :properties

  def initialize(reference = nil, properties = [])
    @reference = reference
    @properties = properties
  end
end

# {http://www.alfresco.org/ws/model/content/1.0}ContentFormat
class ContentFormat
  @@schema_type = "ContentFormat"
  @@schema_ns = "http://www.alfresco.org/ws/model/content/1.0"
  @@schema_element = [["mimetype", "SOAP::SOAPString"], ["encoding", "SOAP::SOAPString"]]

  attr_accessor :mimetype
  attr_accessor :encoding

  def initialize(mimetype = nil, encoding = nil)
    @mimetype = mimetype
    @encoding = encoding
  end
end

# {http://www.alfresco.org/ws/model/content/1.0}NamedValue
class NamedValue
  @@schema_type = "NamedValue"
  @@schema_ns = "http://www.alfresco.org/ws/model/content/1.0"
  @@schema_element = [["name", "SOAP::SOAPString"], ["value", "SOAP::SOAPString"]]

  attr_accessor :name
  attr_accessor :value

  def initialize(name = nil, value = nil)
    @name = name
    @value = value
  end
end

# {http://www.alfresco.org/ws/model/content/1.0}Query
class Query
  @@schema_type = "Query"
  @@schema_ns = "http://www.alfresco.org/ws/model/content/1.0"
  @@schema_element = [["language", "SOAP::SOAPString"], ["statement", "SOAP::SOAPString"]]

  attr_accessor :language
  attr_accessor :statement

  def initialize(language = nil, statement = nil)
    @language = language
    @statement = statement
  end
end

# {http://www.alfresco.org/ws/model/content/1.0}ResultSet
class ResultSet
  @@schema_type = "ResultSet"
  @@schema_ns = "http://www.alfresco.org/ws/model/content/1.0"
  @@schema_element = [["rows", "ResultSetRow[]"], ["totalRowCount", "SOAP::SOAPLong"], ["metaData", "ResultSetMetaData"]]

  attr_accessor :rows
  attr_accessor :totalRowCount
  attr_accessor :metaData

  def initialize(rows = [], totalRowCount = nil, metaData = nil)
    @rows = rows
    @totalRowCount = totalRowCount
    @metaData = metaData
  end
end

# {http://www.alfresco.org/ws/model/content/1.0}ResultSetRow
class ResultSetRow
  @@schema_type = "ResultSetRow"
  @@schema_ns = "http://www.alfresco.org/ws/model/content/1.0"
  @@schema_element = [["rowIndex", "SOAP::SOAPLong"], ["columns", "NamedValue[]"], ["score", "SOAP::SOAPFloat"], ["node", nil]]

  attr_accessor :rowIndex
  attr_accessor :columns
  attr_accessor :score
  attr_accessor :node

  def initialize(rowIndex = nil, columns = [], score = nil, node = nil)
    @rowIndex = rowIndex
    @columns = columns
    @score = score
    @node = node
  end
end

# {http://www.alfresco.org/ws/model/content/1.0}ResultSetMetaData
class ResultSetMetaData
  @@schema_type = "ResultSetMetaData"
  @@schema_ns = "http://www.alfresco.org/ws/model/content/1.0"
  @@schema_element = [["valueDefs", "ValueDefinition[]"], ["classDefs", "ClassDefinition[]"]]

  attr_accessor :valueDefs
  attr_accessor :classDefs

  def initialize(valueDefs = [], classDefs = [])
    @valueDefs = valueDefs
    @classDefs = classDefs
  end
end

# {http://www.alfresco.org/ws/model/content/1.0}Predicate
class Predicate < ::Array
  @@schema_type = "Reference"
  @@schema_ns = "http://www.alfresco.org/ws/model/content/1.0"
end

# {http://www.alfresco.org/ws/model/content/1.0}Version
class Version
  @@schema_type = "Version"
  @@schema_ns = "http://www.alfresco.org/ws/model/content/1.0"
  @@schema_element = [["id", "Reference"], ["created", "SOAP::SOAPDateTime"], ["creator", "SOAP::SOAPString"], ["label", "SOAP::SOAPString"], ["major", "SOAP::SOAPBoolean"], ["commentaries", "NamedValue[]"]]

  attr_accessor :id
  attr_accessor :created
  attr_accessor :creator
  attr_accessor :label
  attr_accessor :major
  attr_accessor :commentaries

  def initialize(id = nil, created = nil, creator = nil, label = nil, major = nil, commentaries = [])
    @id = id
    @created = created
    @creator = creator
    @label = label
    @major = major
    @commentaries = commentaries
  end
end

# {http://www.alfresco.org/ws/model/content/1.0}VersionHistory
class VersionHistory < ::Array
  @@schema_type = "Version"
  @@schema_ns = "http://www.alfresco.org/ws/model/content/1.0"
end

# {http://www.alfresco.org/ws/model/content/1.0}Category
class Category
  @@schema_type = "Category"
  @@schema_ns = "http://www.alfresco.org/ws/model/content/1.0"
  @@schema_element = [["id", "Reference"]]

  attr_accessor :id

  def initialize(id = nil)
    @id = id
  end
end

# {http://www.alfresco.org/ws/model/content/1.0}Classification
class Classification
  @@schema_type = "Classification"
  @@schema_ns = "http://www.alfresco.org/ws/model/content/1.0"
  @@schema_element = [["classification", "SOAP::SOAPString"], ["rootCategory", "Category"]]

  attr_accessor :classification
  attr_accessor :rootCategory

  def initialize(classification = nil, rootCategory = nil)
    @classification = classification
    @rootCategory = rootCategory
  end
end

# {http://www.alfresco.org/ws/model/content/1.0}StoreEnum
module StoreEnum
  Http = "http"
  Search = "search"
  System = "system"
  User = "user"
  VersionStore = "versionStore"
  Workspace = "workspace"
end

# {http://www.alfresco.org/ws/model/content/1.0}Cardinality
module Cardinality
  C_ = "*"
  C_01 = "0..1"
  C_1 = "1"
  C_1_2 = "1..*"
end

# {http://www.alfresco.org/ws/model/content/1.0}QueryLanguageEnum
module QueryLanguageEnum
  Cql = "cql"
  Lucene = "lucene"
  Xpath = "xpath"
end
