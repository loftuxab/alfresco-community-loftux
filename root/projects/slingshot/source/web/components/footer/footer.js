/**
 * Alfresco.Footer
 */
(function()
{
   Alfresco.Footer = function(htmlId)
   {
      this.name = "Alfresco.Footer";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      return this;
   };
   
   Alfresco.Footer.prototype =
   {
   };
})();