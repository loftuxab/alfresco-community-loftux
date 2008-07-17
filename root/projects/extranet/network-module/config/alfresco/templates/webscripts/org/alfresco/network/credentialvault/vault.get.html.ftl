<#if xml?exists>
${xml}
<#else>
<?xml version="1.0" encoding="UTF-8"?>
<vault id="${vaultId}"></vault>
</#if>