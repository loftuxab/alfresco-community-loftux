require 'xsd/qname'

# {http://www.alfresco.org/ws/service/repository/1.0}getStores
class GetStores
  @@schema_type = "getStores"
  @@schema_ns = "http://www.alfresco.org/ws/service/repository/1.0"
  @@schema_element = []

  def initialize
  end
end

# {http://www.alfresco.org/ws/service/repository/1.0}getStoresResponse
class GetStoresResponse
  @@schema_type = "getStoresResponse"
  @@schema_ns = "http://www.alfresco.org/ws/service/repository/1.0"
  @@schema_element = [["getStoresReturn", "Store[]"]]

  attr_accessor :getStoresReturn

  def initialize(getStoresReturn = [])
    @getStoresReturn = getStoresReturn
  end
end

# {http://www.alfresco.org/ws/service/repository/1.0}query
class Query
  @@schema_type = "query"
  @@schema_ns = "http://www.alfresco.org/ws/service/repository/1.0"
  @@schema_element = [["store", "Store"], ["query", "Query"], ["includeMetaData", "SOAP::SOAPBoolean"]]

  attr_accessor :store
  attr_accessor :query
  attr_accessor :includeMetaData

  def initialize(store = nil, query = nil, includeMetaData = nil)
    @store = store
    @query = query
    @includeMetaData = includeMetaData
  end
end

# {http://www.alfresco.org/ws/service/repository/1.0}queryResponse
class QueryResponse
  @@schema_type = "queryResponse"
  @@schema_ns = "http://www.alfresco.org/ws/service/repository/1.0"
  @@schema_element = [["queryReturn", "QueryResult"]]

  attr_accessor :queryReturn

  def initialize(queryReturn = nil)
    @queryReturn = queryReturn
  end
end

# {http://www.alfresco.org/ws/service/repository/1.0}queryChildren
class QueryChildren
  @@schema_type = "queryChildren"
  @@schema_ns = "http://www.alfresco.org/ws/service/repository/1.0"
  @@schema_element = [["node", "Reference"]]

  attr_accessor :node

  def initialize(node = nil)
    @node = node
  end
end

# {http://www.alfresco.org/ws/service/repository/1.0}queryChildrenResponse
class QueryChildrenResponse
  @@schema_type = "queryChildrenResponse"
  @@schema_ns = "http://www.alfresco.org/ws/service/repository/1.0"
  @@schema_element = [["queryReturn", "QueryResult"]]

  attr_accessor :queryReturn

  def initialize(queryReturn = nil)
    @queryReturn = queryReturn
  end
end

# {http://www.alfresco.org/ws/service/repository/1.0}queryParents
class QueryParents
  @@schema_type = "queryParents"
  @@schema_ns = "http://www.alfresco.org/ws/service/repository/1.0"
  @@schema_element = [["node", "Reference"]]

  attr_accessor :node

  def initialize(node = nil)
    @node = node
  end
end

# {http://www.alfresco.org/ws/service/repository/1.0}queryParentsResponse
class QueryParentsResponse
  @@schema_type = "queryParentsResponse"
  @@schema_ns = "http://www.alfresco.org/ws/service/repository/1.0"
  @@schema_element = [["queryReturn", "QueryResult"]]

  attr_accessor :queryReturn

  def initialize(queryReturn = nil)
    @queryReturn = queryReturn
  end
end

# {http://www.alfresco.org/ws/service/repository/1.0}queryAssociated
class QueryAssociated
  @@schema_type = "queryAssociated"
  @@schema_ns = "http://www.alfresco.org/ws/service/repository/1.0"
  @@schema_element = [["node", "Reference"], ["association", "Association[]"]]

  attr_accessor :node
  attr_accessor :association

  def initialize(node = nil, association = [])
    @node = node
    @association = association
  end
end

# {http://www.alfresco.org/ws/service/repository/1.0}queryAssociatedResponse
class QueryAssociatedResponse
  @@schema_type = "queryAssociatedResponse"
  @@schema_ns = "http://www.alfresco.org/ws/service/repository/1.0"
  @@schema_element = [["queryReturn", "QueryResult"]]

  attr_accessor :queryReturn

  def initialize(queryReturn = nil)
    @queryReturn = queryReturn
  end
end

# {http://www.alfresco.org/ws/service/repository/1.0}fetchMore
class FetchMore
  @@schema_type = "fetchMore"
  @@schema_ns = "http://www.alfresco.org/ws/service/repository/1.0"
  @@schema_element = [["querySession", "SOAP::SOAPString"]]

  attr_accessor :querySession

  def initialize(querySession = nil)
    @querySession = querySession
  end
end

# {http://www.alfresco.org/ws/service/repository/1.0}fetchMoreResponse
class FetchMoreResponse
  @@schema_type = "fetchMoreResponse"
  @@schema_ns = "http://www.alfresco.org/ws/service/repository/1.0"
  @@schema_element = [["queryReturn", "QueryResult"]]

  attr_accessor :queryReturn

  def initialize(queryReturn = nil)
    @queryReturn = queryReturn
  end
end

# {http://www.alfresco.org/ws/service/repository/1.0}update
class Update
  @@schema_type = "update"
  @@schema_ns = "http://www.alfresco.org/ws/service/repository/1.0"
  @@schema_element = [["statements", "CML"]]

  attr_accessor :statements

  def initialize(statements = nil)
    @statements = statements
  end
end

# {http://www.alfresco.org/ws/service/repository/1.0}updateResponse
class UpdateResponse
  @@schema_type = "updateResponse"
  @@schema_ns = "http://www.alfresco.org/ws/service/repository/1.0"
  @@schema_element = [["updateReturn", "UpdateResult[]"]]

  attr_accessor :updateReturn

  def initialize(updateReturn = [])
    @updateReturn = updateReturn
  end
end

# {http://www.alfresco.org/ws/service/repository/1.0}describe
class Describe
  @@schema_type = "describe"
  @@schema_ns = "http://www.alfresco.org/ws/service/repository/1.0"
  @@schema_element = [["items", "Predicate"]]

  attr_accessor :items

  def initialize(items = nil)
    @items = items
  end
end

# {http://www.alfresco.org/ws/service/repository/1.0}describeResponse
class DescribeResponse
  @@schema_type = "describeResponse"
  @@schema_ns = "http://www.alfresco.org/ws/service/repository/1.0"
  @@schema_element = [["describeReturn", "NodeDefinition[]"]]

  attr_accessor :describeReturn

  def initialize(describeReturn = [])
    @describeReturn = describeReturn
  end
end

# {http://www.alfresco.org/ws/service/repository/1.0}get
class Get
  @@schema_type = "get"
  @@schema_ns = "http://www.alfresco.org/ws/service/repository/1.0"
  @@schema_element = [["where", "Predicate"]]

  attr_accessor :where

  def initialize(where = nil)
    @where = where
  end
end

# {http://www.alfresco.org/ws/service/repository/1.0}getResponse
class GetResponse
  @@schema_type = "getResponse"
  @@schema_ns = "http://www.alfresco.org/ws/service/repository/1.0"
  @@schema_element = [["getReturn", "Node[]"]]

  attr_accessor :getReturn

  def initialize(getReturn = [])
    @getReturn = getReturn
  end
end

# {http://www.alfresco.org/ws/service/repository/1.0}QueryResult
class QueryResult
  @@schema_type = "QueryResult"
  @@schema_ns = "http://www.alfresco.org/ws/service/repository/1.0"
  @@schema_element = [["querySession", "SOAP::SOAPString"], ["resultSet", "ResultSet"]]

  attr_accessor :querySession
  attr_accessor :resultSet

  def initialize(querySession = nil, resultSet = nil)
    @querySession = querySession
    @resultSet = resultSet
  end
end

# {http://www.alfresco.org/ws/service/repository/1.0}UpdateResult
class UpdateResult
  @@schema_type = "UpdateResult"
  @@schema_ns = "http://www.alfresco.org/ws/service/repository/1.0"
  @@schema_element = [["statement", "SOAP::SOAPString"], ["updateCount", "ResultSet"], ["source", "Reference"], ["destination", "Reference"]]

  attr_accessor :statement
  attr_accessor :updateCount
  attr_accessor :source
  attr_accessor :destination

  def initialize(statement = nil, updateCount = nil, source = nil, destination = nil)
    @statement = statement
    @updateCount = updateCount
    @source = source
    @destination = destination
  end
end

# {http://www.alfresco.org/ws/service/repository/1.0}Association
class Association
  @@schema_type = "Association"
  @@schema_ns = "http://www.alfresco.org/ws/service/repository/1.0"
  @@schema_element = [["associationType", "SOAP::SOAPString"], ["direction", "SOAP::SOAPString"]]

  attr_accessor :associationType
  attr_accessor :direction

  def initialize(associationType = nil, direction = nil)
    @associationType = associationType
    @direction = direction
  end
end

# {http://www.alfresco.org/ws/service/repository/1.0}RepositoryFault
class RepositoryFault
  @@schema_type = "RepositoryFault"
  @@schema_ns = "http://www.alfresco.org/ws/service/repository/1.0"
  @@schema_element = [["errorCode", "SOAP::SOAPInt"], ["message", "SOAP::SOAPString"]]

  attr_accessor :errorCode
  attr_accessor :message

  def initialize(errorCode = nil, message = nil)
    @errorCode = errorCode
    @message = message
  end
end