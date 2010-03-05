/**
 * Supported Forms controls
 */
var supportedControlTypes =
{
   "association": false,
   "category": true,
   "tag": true,
   "checkbox": false,
   "content": false,
   "date": false,
   "encoding": false,
   "mimetype": false,
   "period": false,
   "readonly": false,
   "richtext": false,
   "selectmany": false,
   "selectone": false,
   "size": false,
   "textarea": false,
   "textfield": false
};

/**
 * Return control and required parameters given a control type
 *
 * @method getControlFields
 * @param type {string} Control type
 */
function getControlFields(type)
{
   if (!(type in supportedControlTypes && supportedControlTypes[type] === true))
   {
      return null;
   }
   
   var control =
   {
      template: "controls/" + type + ".ftl"
   };
   
   switch (String(type).toLowerCase())
   { //multipleSelectMode
      case "category":
         control.params =
         {
            compactMode: true,
         };
         if (args.multipleSelectMode !== null)
         {
            control.params.multipleSelectMode = args.multipleSelectMode;
         }
         break;

      case "tag":
         control.template = "controls/category.ftl";
         control.params =
         {
            compactMode: true,
            params: "aspect=cm:taggable"
         };
         if (args.multipleSelectMode !== null)
         {
            control.params.multipleSelectMode = args.multipleSelectMode;
         }
         break;
   }
   
   return control;
}

/**
 * Main entrypoint
 *
 * @method main
 */
function main()
{
   // Input arguments
   var type = decodeURIComponent(args.type || page.url.args.type),
      name = decodeURIComponent(args.name) || ("wrapped-" + type),
      label = decodeURIComponent(args.label) || "",
      value = decodeURIComponent(args.value) || "";

   var control = getControlFields(type);
   if (control !== null)
   {
      model.field =
      {
         configName: name,
         control: getControlFields(type),
         disabled: false,
         id: name,
         label: label,
         mandatory: false,
         name: name,
         value: value
      };

      model.form =
      {
         mode: "edit",
         data: {}
      };
   }
}

main();
