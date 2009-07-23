<#macro customAssociationJSON association>
<#escape x as jsonUtils.encodeJSONString(x)>
              "isChildAssociation" : ${association.childAssociation?string},
              "title" : "${association.title!""}",
              "description" : "${association.description!""}",
              "sourceRoleName" : "${association.sourceRoleName!""}",
              "sourceMandatory" : ${association.sourceMandatory?string},
              "sourceMany" : ${association.sourceMany?string},
              "targetRoleName" : "${association.targetRoleName!""}",
              "targetMandatory" : ${association.targetMandatory?string},
              "targetMandatoryEnforced" : ${association.targetMandatoryEnforced?string},
              "targetMany" : ${association.targetMany?string},
              "protected" : ${association.protected?string}
</#escape>
</#macro>