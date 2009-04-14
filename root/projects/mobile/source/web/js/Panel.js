( 
  function(){
    Panel = function Panel(config){
      Panel.superclass.constructor.call(this,config);
    };
    Panel = Core.util.extend(Panel,UIControl,{
      init : function init() 
      {
        // this.elements=x$(this.config.elements);
        // if (this.config.el)
        // {
        //   var el = this.config.el;
        //   //these should be passed in          
        //   // this.config.id = el.id.replace('panel-','');
        //   // this.config.title = el.id.split('-').join(' ');
        // }
        this.id = Core.util.trim(this.config.id);
        // console.log('panel.init()',this);
        this.elements = [];
        this.events = this.events || [];
        // this.init();

        this.initEls = function initEls()
        {
          // this.panelEl=document.getElementById('panel-'+this.id);
          //          this.titleEl=document.getElementById('panelTitle-'+this.id);
          //          this.panelBodyEl = document.getElementById('panelBody-'+this.id);
          //          this.backButtonEl = document.getElementById('panelBackButton-'+this.id);
          //          if (this.backButtonEl)
          //          {
          //            this.backButton = new Button({el:this.backButtonEl}).init();          
          //            // this.backButton.on('click',function(e) {
          //            //   history.go(-1);
          //            // })            
          //          }
          this.panelEl = document.getElementById(this.id);
          x$('#'+this.id + ' .back').on('click',function(e) {
            // App.hideBrowserNavBar();
            App.previous();
          })
        };
        this.initEls();
        // templates
        var t = '<div id="{panelId}" class="panel loading">'+
                  '<div class="toolbar">' +
                    '<h1>{title}</h1>' +
                    '<a href="#" class="back button">{backButtonText}</a>' +
                  '</div>' +
                  '<div class="content">' +
                  '</div>' +
                '</div>';
        //create panel
        if (!this.panelEl && this.config.buttons)
        {
          var div = document.createElement('div');
          var dt = t;
          dt = dt.replace(/{panelId}/g,this.id);
          dt = dt.replace(/{title}/g,this.config.title);
          dt = dt.replace(/{backButtonText}/g,this.config.buttons.backText||'back');
          div.innerHTML = dt;
          // document.getElementById('viewport').appendChild(div.firstChild);
          document.getElementById('container').appendChild(div.firstChild);
          this.initEls();
          var contentEl = x$('#' + this.panelEl.id +' .content');
          if (contentEl)
          {
            contentEl.first().innerHTML = '<div style=""><img src="/mobile/themes/default/images/loading.gif"/> Loading</div>';
          }
        };

        // this.elements = x$(this.panelBodyEl,this.titleEl,'#panelBackButton-'+this.id);
        this.elements = x$(this.panelEl);
        return this;
      },
      render : function render()
      {
        //attempt to hide scroll bar appearing when js handled links are clicked
        //iUI has the problem too
        // App.hideBrowserNavBar();
        // if (!Constants.doNotLoadPage)
        // {
          if (this.config.el.nodeName.toUpperCase()==='A')
          {
            var href = this.config.el.href.split('#');
            //get url
            href = (href.length>1) ? href[1] : href[0];
            // if (Constants.animate)
            // {
            //   href+='#doAnim';
            // };r
            setTimeout(function (){window.location=href;},100);
            //Remove Panel as this is still shown sometimes when user 
            //clicks 'back' on next page
            var that = this;
            window.addEventListener('unload',function(e){
              that.destroy();
            });
          }
        // }
      },
      destroy : function destroy()
      {
        if (this.panelEl)
        {
          App.removePanel(this.panelEl.id);
          this.panelEl.parentNode.removeChild(this.panelEl);          
        }
       
      },
      slideOut : function(direction) {
        var currentWidth = window.innerWidth;
        //set timeout otherwise flicker occurs
        setTimeout(function(o){
            o.elements.css({
              webkitTransitionProperty: '-webkit-transform',
              webkitTransform : "translateX("+direction*currentWidth+"px)",
            
              webkitTransitionDuration : '0.35s',
              webkitTransitionTimingFunction: 'ease-in-out',
            });
            x$('#'+o.id + ' .toolbar h1','#'+o.id + ' .toolbar .back').css({
              webkitTransitionProperty: 'opacity',
              webkitTransitionDuration : '0.1s',
              webkitTransitionTimingFunction: 'ease-in-out',                          
              opacity:0
            });
          },
          0,this);
          
        setTimeout(function(o){
          o.deactivate();
          },350,this);

      },
      slideIn : function() {
        var currentWidth = window.innerWidth;
        //set timeout otherwise flicker occurs
        setTimeout(function(o){
            o.elements.css({
               webkitTransitionProperty: '-webkit-transform',
               webkitTransitionDuration : '0.35s',
               webkitTransitionTimingFunction: 'ease-in-out',
               webkitTransform : "translateX("+0+"px)",          
             });

            x$('#'+o.id + ' .toolbar h1','#'+o.id + ' .toolbar .back').css({
              webkitTransitionProperty: 'opacity',
              webkitTransitionDuration : '0.1s',
              webkitTransitionTimingFunction: 'ease-in-out',                          
              opacity:1
            });
          }
        ,0,this)
      

        setTimeout(function(o){
          o.activate();
          o.render();
          },350,this);
      },
      show : function show() {
        this.slideIn();
      },
      hide : function hide(direction) {
        this.slideOut(direction)
      }      
    });
  }()
);