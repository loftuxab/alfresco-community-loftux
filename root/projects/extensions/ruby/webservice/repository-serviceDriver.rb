require 'repository-service.rb'
require 'types.rb'

require 'soap/rpc/driver'

class RepositoryServiceSoapPort < ::SOAP::RPC::Driver
  DefaultEndpointUrl = "http://localhost:8080/alfresco/api/RepositoryService"
  MappingRegistry = ::SOAP::Mapping::Registry.new

  Methods = [
    ["getStores", "getStores",
      [
        ["in", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/repository/1.0", "getStores"]],
        ["out", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/repository/1.0", "getStoresResponse"]]
      ],
      "http://www.alfresco.org/ws/service/repository/1.0/getStores", nil, :document
    ],
    ["query", "query",
      [
        ["in", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/repository/1.0", "query"]],
        ["out", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/repository/1.0", "queryResponse"]]
      ],
      "http://www.alfresco.org/ws/service/repository/1.0/query", nil, :document
    ],
    ["queryChildren", "queryChildren",
      [
        ["in", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/repository/1.0", "queryChildren"]],
        ["out", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/repository/1.0", "queryChildrenResponse"]]
      ],
      "http://www.alfresco.org/ws/service/repository/1.0/queryChildren", nil, :document
    ],
    ["queryParents", "queryParents",
      [
        ["in", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/repository/1.0", "queryParents"]],
        ["out", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/repository/1.0", "queryParentsResponse"]]
      ],
      "http://www.alfresco.org/ws/service/repository/1.0/queryParents", nil, :document
    ],
    ["queryAssociated", "queryAssociated",
      [
        ["in", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/repository/1.0", "queryAssociated"]],
        ["out", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/repository/1.0", "queryAssociatedResponse"]]
      ],
      "http://www.alfresco.org/ws/service/repository/1.0/queryAssociated", nil, :document
    ],
    ["fetchMore", "fetchMore",
      [
        ["in", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/repository/1.0", "fetchMore"]],
        ["out", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/repository/1.0", "fetchMoreResponse"]]
      ],
      "http://www.alfresco.org/ws/service/repository/1.0/fetchMore", nil, :document
    ],
    ["update", "update",
      [
        ["in", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/repository/1.0", "update"]],
        ["out", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/repository/1.0", "updateResponse"]]
      ],
      "http://www.alfresco.org/ws/service/repository/1.0/update", nil, :document
    ],
    ["describe", "describe",
      [
        ["in", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/repository/1.0", "describe"]],
        ["out", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/repository/1.0", "describeResponse"]]
      ],
      "http://www.alfresco.org/ws/service/repository/1.0/describe", nil, :document
    ],
    ["get", "get",
      [
        ["in", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/repository/1.0", "get"]],
        ["out", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/repository/1.0", "getResponse"]]
      ],
      "http://www.alfresco.org/ws/service/repository/1.0/get", nil, :document
    ]
  ]

  def initialize(endpoint_url = nil)
    endpoint_url ||= DefaultEndpointUrl
    super(endpoint_url, nil)
    self.mapping_registry = MappingRegistry
    init_methods
  end

private

  def init_methods
    Methods.each do |name_as, name, params, soapaction, namespace, style|
      qname = XSD::QName.new(namespace, name_as)
      if style == :document
        @proxy.add_document_method(soapaction, name, params)
        add_document_method_interface(name, params)
      else
        @proxy.add_rpc_method(qname, soapaction, name, params)
        add_rpc_method_interface(name, params)
      end
      if name_as != name and name_as.capitalize == name.capitalize
        ::SOAP::Mapping.define_singleton_method(self, name_as) do |*arg|
          __send__(name, *arg)
        end
      end
    end
  end
end

