(  
  function(){
    Button = function Button(config)
    {
      Button.superclass.constructor.call(this,config);
    };
    Button = Core.util.extend(Button,UIControl,{
      init : function init()
      {        
        return this;
      },
      destroy : function destroy()
      {
        this.elements.first().removeEventListener(this.onClick);
      }
    });
  }()
);
(
  function()
  {

    TabPanel = function TabPanel(config)
    {
      TabPanel.superclass.constructor.call(this,config);
    };
    TabPanel = Core.util.extend(TabPanel,UIControl,{
      init : function()
      {
        //this.config.el should reference to .tabs element
        this.elements = x$(this.config.el);
        if (!(this.elements.is('.tabs')))
        {
          this.config.el = this.elements.find('.tabs').first();
          this.elements = x$(this.config.el);
        };
        this.activeEls = x$(this.config.el).find('.active');
        console.log(this.activeEls.elements)

        //this.elements is .tabs element
        var that = this;
        this.elements.on('click',function(e)
        {

          if (e.srcElement.className.indexOf('button')!=-1)
          {
            e.preventDefault();
            that.onClick(e);
          }

        });
        //TODO - change to Button instance create new button for each .tabs.button 
        // console.log(x$(this.config.el).find('.button').each(function(el){
        // console.log(el);
        // //todo create new button instance and add click handler in config.
        // //button mus read config and assign as required. But what about activeels? keep handler on TB but create buttons too. just make handler call button mewthods when required
        // }));
      },
      onClick : function(e)
      {
        var el = e.srcElement;
        if (x$(el).is('.button'))
        {
          var href =  el.href;
          // console.log(href)
          if (href.indexOf('#')!==-1)
          { 
            var contentId = href.split('#')[1];
            var contentEl = x$('#'+contentId);

            if (contentEl.elements.length>0)
            {
              this.activeEls.removeClass('active');
              this.activeEls = x$(el,contentEl.first()).addClass('active');
            }
          }
        }
        // console.log(e.srcElement);
      }
    });
 }()
);
