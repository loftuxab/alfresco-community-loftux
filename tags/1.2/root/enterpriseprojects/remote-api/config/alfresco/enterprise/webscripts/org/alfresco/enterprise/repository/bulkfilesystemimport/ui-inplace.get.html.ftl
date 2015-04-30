[#ftl]
<!DOCTYPE HTML>
<html>
  <head>
    <title>${msg("bfsit.page.title")}</title>
    <link rel="stylesheet" href="${url.context}/css/main.css" TYPE="text/css">

    <!-- YUI 3.x -->
    <link rel="stylesheet" type="text/css" href="${url.context}/css/yui-3.3.0-dependencies.css">
    <style type="text/css">
      .yui3-aclist-content {
        background-color   : white;
        border             : 1px solid darkgrey;
        box-shadow         : 3px 3px 4px lightgrey;
        -webkit-box-shadow : 3px 3px 4px lightgrey; /* Safari and Chrome */

       }
    </style>

    <script type="text/javascript" src="${url.context}/scripts/yui-3.3.0-dependencies.js"></script>

    <!-- Validation functions -->
    <script type="text/javascript">
      function validateRequired(field, errorMessageElement, errorMessage)
      {
        var result = true;

        if (field.value == null || field.value == "")
        {
          errorMessageElement.textContent = errorMessage;
          result = false;
        }
        else
        {
          errorMessageElement.textContent = "";
        }

        return result;
      }


      function validateForm(form)
      {
        var sourceDirectoryValid = false;
        var contentStoreValid = false;
        var targetPathValid = false;

        sourceDirectoryValid = validateRequired(form.sourceDirectory, document.getElementById("sourceDirectoryMessage"), "${msg("bfsit.error.sourceDirectory")}");
        contentStoreValid = validateRequired(form.contentStore, document.getElementById("contentStoreMessage"), "${msg("bfsit.error.contentStore")}");
        targetPathValid = validateRequired(form.targetPath, document.getElementById("targetPathMessage"), "${msg("bfsit.error.targetPath")}");

        return (sourceDirectoryValid && contentStoreValid && targetPathValid);
      }
    </script>
  </head>
  <body class="yui-skin-sam">
    <table>
      <tr>
        <td><img src="${url.context}/images/logo/AlfrescoLogo32.png" alt="Alfresco" /></td>
        <td><nobr>${msg("bfsit.page.title")}</nobr></td>
      </tr>
      <tr><td><td>Alfresco ${server.edition} v${server.version}
    </table>
    <form action="${url.service}/initiate" method="post" enctype="multipart/form-data" charset="utf-8" onsubmit="return validateForm(this);">
      <table>
        <tr>
          <td><label for="sourceDirectory">${msg("bfsit.label.sourceDirectory")} :</label></td>
          <td><input type="text" id="sourceDirectory" name="sourceDirectory" size="128" /></td>
          <td id="sourceDirectoryMessage" style="color:red"></td>
        </tr>

        <tr>
          <td><label for="contentStore">${msg("bfsit.label.contentStore")} :</label></td>
          <td>
            <div id="contentStoreAutoComplete">
                <input id="contentStore" type="text" name="contentStore" size="128" />
                [#-- <div id="contentStoreAutoSuggestContainer" style="background-color:yellow;"></div> --]
            </div>
          </td>
          <td id="contentStoreMessage" style="color:red"></td>
        </tr>

        <tr>
          <td><br/><label for="targetPath">${msg("bfsit.label.targetPath")} :</label></td>
          <td>
            <div id="targetNodeRefAutoComplete">
              <input id="targetPath" type="text" name="targetPath" size="128" />
              [#-- <div id="targetPathAutoSuggestContainer" style="background-color:yellow;"></div> --]
            </div>
          </td>
          <td id="targetPathMessage" style="color:red"></td>
        </tr>

        <tr>
          <td colspan="3">&nbsp;</td>
        </tr>
        <tr>
          <td><label for="disableRules">Disable rules:</label></td><td><input type="checkbox" id="disableRules" name="disableRules" value="disableRules" unchecked/> (unchecked means rules are enabled during the import)</td><td></td>
        </tr>
        <tr>
          <td>Batch Size:</td>
          <td colspan="2"><input type="text" name="batchSize" size="5"></td>
        </tr>
        <tr>
          <td>Number of Threads:</td>
          <td colspan="2"><input type="text" name="numThreads" size="5"></td>
        </tr>
        <tr>
          <td colspan="3">&nbsp;</td>
        </tr>
        <tr>
          <td colspan="3"><input type="submit" name="submit" value="${msg("bfsit.submit")}"></td>
        </tr>
      </table>
      <br/>
    </form>
    <script type="text/javascript">
        [#-- chrome may output a warning about missing css. the issue is that they are actually loaded, and won't be loaded twice --]
        [#-- see http://yuilibrary.com/projects/yui3/ticket/2530020 about using multiple autocompletes --]
        [#-- targeted to be fixed in YUI 3.4.0 --]
        YUI().use("autocomplete", "autocomplete-highlighters", "datasource-get", function(Y)
        {
          Y.one('#targetPath').plug(Y.Plugin.AutoComplete,
          {
            source            : '${url.serviceContext}/bulkfsimport/ajax/suggest/spaces.json?query={query}',
            maxResults        : 25,
            resultHighlighter : 'phraseMatch',
            resultListLocator : 'data',
            resultTextLocator : 'path'
          });

          Y.one('#contentStore').plug(Y.Plugin.AutoComplete,
          {
            source            : '${url.serviceContext}/bulkfsimport/ajax/suggest/inplacestores.json?query={query}',
            maxResults        : 25,
            resultHighlighter : 'phraseMatch',
            resultListLocator : 'data',
            resultTextLocator : 'name'
          });
        });
    </script>    
  </body>
</html>
