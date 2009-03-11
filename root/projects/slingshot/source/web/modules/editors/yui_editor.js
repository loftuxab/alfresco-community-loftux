/**
 *  Adapter for YUI html editor (http://developer.yahoo.com/yui/editor/).
 * 
 */
Alfresco.util.RichEditorManager.addEditor('YAHOO.widget.SimpleEditor',
  function(id,config)
  {
    var editor;
    return {
      init : function RichEditorManager_init(id,config) {
        editor = new YAHOO.widget.SimpleEditor(id,config);
        YAHOO.Bubbling.fire("editorInitialized", this);        
        return this;
      },
      clear : function RichEditorManager_clear() 
      {
        editor.clearEditorDoc();
      },
      render : function RichEditorManager_render() 
      {
        editor.render();
      },
      disable : function RichEditorManager_disable()
      {
         editor._disableEditor(true);
      },
      enable : function RichEditorManager_enable()
      {
         editor._disableEditor(false);
      },
      getContent : function RichEditorManager_getContent() 
      { 
        return editor.getEditorHTML();
      }, 
      setContent : function RichEditorManager_setContent(html) 
      { 
        editor.setEditorHTML(html);
      },
      save : function RichEditorManager_save()
      {
        editor.saveHTML();
      },
      getEditor : function RichEditorManager_getEditor(){
        return editor;
      }
    };
  }
);