require 'content-service.rb'

require 'soap/rpc/driver'

class ContentServiceSoapPort < ::SOAP::RPC::Driver
  DefaultEndpointUrl = "http://localhost:8080/alfresco/api/ContentService"
  MappingRegistry = ::SOAP::Mapping::Registry.new

  Methods = [
    ["read", "read",
      [
        ["in", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/content/1.0", "read"]],
        ["out", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/content/1.0", "readResponse"]]
      ],
      "http://www.alfresco.org/ws/service/content/1.0/read", nil, :document
    ],
    ["write", "write",
      [
        ["in", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/content/1.0", "write"]],
        ["out", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/content/1.0", "writeResponse"]]
      ],
      "http://www.alfresco.org/ws/service/content/1.0/write", nil, :document
    ],
    ["clear", "clear",
      [
        ["in", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/content/1.0", "clear"]],
        ["out", "parameters", ["::SOAP::SOAPElement", "http://www.alfresco.org/ws/service/content/1.0", "clearResponse"]]
      ],
      "http://www.alfresco.org/ws/service/content/1.0/clear", nil, :document
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

