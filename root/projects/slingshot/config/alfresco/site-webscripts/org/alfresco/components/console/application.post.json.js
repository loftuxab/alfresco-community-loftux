/**
 * Admin Console Application Tool POST method
 */
function main()
{
   var themeId = json.get("console-options-theme-menu");
   context.setThemeId(new String(themeId));
   
   model.success = true;
}

main();