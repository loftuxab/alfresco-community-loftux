<#include "wef-boot.js" />

<#if url.args?contains("debug=true")><#assign debug="true"><#else><#assign debug="false"></#if>

WEF.init(
{
   /**
    * Debug mode
    * 
    * @type Boolean
    */
   debugMode : ${debug},

   /**
    * constants
    * @type Object 
    */
   constants : {},

   /**
    * Object literal of applications to render.
    * 
    * @type Object 
    */ 
   applications: {},
   
   /**
    * Configuration for loader
    *  
    */
   loaderConfig: 
   {
      /**
       * Server port of awe app
       *
       * @type String
       */
      serverPort: window.location.protocol + "//" + window.location.host,
      
      /**
       * Context path of awe app
       *
       * @type String
       */
      urlContext: window.location.protocol + "//" + window.location.host + "${url.context}" + "/res",
      
      /**
       * Use sandbox to load files
       *
       * @type Boolean
       */
      useSandboxLoader: false,
      
      /**
       * Path to yuiloader. This is loaded via script tags so can be absolute or relative
       * 
       * @type String 
       */
      yuiloaderPath: "/yui/yuiloader/yuiloader-debug.js",
      
      /**
       * Base path to yui files. Use empty string to use YDN 
       * 
       * @type string
       */
      yuibase: "${url.context}/res/yui/",
      
      /**
       * A filter to apply to loader.
       * 3rd party plugins need to be available in -[filter].js versions too
       * Defaults to min if not supplied.
       *
       * @type String 
       */
      filter : "min",
      
      /**
       * Flag for yui loader to determine whether to load extra optional resources as well
       *
       * @type Boolean 
       */
      loadOptional : true,
      
      /**
       * Skin overrides for YUI
       * 
       * @type Object 
       */
      skin: 
      {
         base: "/assets/skins/",
         defaultSkin: "sam" 
      }               
   }
});