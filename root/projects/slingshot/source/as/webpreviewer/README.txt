ANOUT THIS FOLDER
=================

This folder contains the source code for the WebPreviewer Flash/FLEX/Actionscript component 
and also a few test file that will make developent easier.

The source files can be found in the current folder and the org/ folder.

Skinning graphics can be found in assets/ where also the folder test/ exists that has a pregenerated
.swf file that can be used to test your changes with some real data.

After you have created a Flex project in Flex Builder move the files found in
copy-to-html-template into the html-template folder created by Flex Builder.
Here comes a short explanation what they do:

extMouseWheel.js
 - makes mouse wheel scrolling work on mac browsers.

index.template.html
- looks pretty much like the one Flex Builder created for you but its has a few WebPreviewr specifics.
- imports the extMouseWheel.js into the test page so scrolling works
- will put in parameters to allow fullscreen & javascript-actionscript communication
- set the flashVars read by the WebPreviewer.mxml so you can test your changes against
  a pre-genererated preview-swf and turn on the paging for it.

If you hit a security violation its probably because you view the files on
your local filesystem instead of on a web browser.