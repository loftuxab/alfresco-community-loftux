[#ftl]
{
  "data" :
  [
[#list stores as store]
        {
          "name"    : "${store}"
        }[#if store != stores?last],[/#if]
[/#list]
  ]
}