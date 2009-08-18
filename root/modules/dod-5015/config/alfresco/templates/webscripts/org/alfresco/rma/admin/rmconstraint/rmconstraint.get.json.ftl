<#import "rmconstraint.lib.ftl" as rmconstraintLib/>

<#escape x as jsonUtils.encodeJSONString(x)>
{
	"data":
	     {
	        "constraintName" : "${constraintName}",
           "constraintDetails" : [
              <#list constraintDetails as constraintDetail>   
              <@rmconstraintLib.constraintJSON constraintDetail=constraintDetail />        
              <#if constraintDetail_has_next>,</#if>
              </#list>
            ]
        }

}
</#escape>