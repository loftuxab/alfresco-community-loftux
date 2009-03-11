/**
 *  Adapter for tinyMCE html editor (http://tinymce.moxiecode.com).
 * 
 */
Alfresco.util.RichEditorManager.addEditor('tinyMCE',
  function(id,config)
  {
    var editor;
    return {
      init : function RichEditorManager_init(id,config) {
        config.mode = 'exact';
        config.elements = id;
        config.plugins = (config.plugins && config.plugins!='') ? config.plugins + ', safari': 'safari';
        if (!config.init_instance_callback) 
        {
           config.init_instance_callback = function(o) {
             return function(inst) {
               YAHOO.Bubbling.fire("editorInitialized", o);
             };
           }(this);
        }
        editor = new tinymce.Editor(id,config);
        return this;
      },
      clear : function RichEditorManager_clear() 
      {
        editor.setContent('');
      },
      render : function RichEditorManager_render() 
      {
        editor.render();
      },
      execCommand : 'execCommand',
      disable : function RichEditorManager_disable()
      {
        editor.hide();
      },
      enable : function RichEditorManager_enable()
      {
        editor.show();
      },
      getContent : function RichEditorManager_getContent() 
      { 
        return editor.getContent();
      }, 
      setContent : function RichEditorManager_setContent(html) 
      { 
        editor.setContent(html);
      }, 
      save : function RichEditorManager_save()
      {
        editor.save();
      },
      getEditor : function RichEditorManager_getEditor(){
        return editor;
      }
    };
  }
);