(
function(){
    App = function App(config){
      App.superclass.constructor.call(this,config);
    };
    App = new (Core.util.extend(App,UIControl,
      {
        panelStack : [],
        currentPanelIndex : 0,
        init : function init(config)
        {
          if (config && config.flash)
          {
           x$('#flash').first().innerHTML = config.flash.text//  alert(config.flash.text)
          }
          //initialise panels
          this.panels = x$('.panel');
          this.panels.each(function(el) {
            var p = new Panel({
              el:el,
              id:el.id,
              title:el.id.split('-').join(' '),
              setUpCustomEvents:[]
            });
            p.init();
            App.addPanel(el.id,p);
            // p.hide(-1);
          });
          //move out?
          // handle new panels
          x$('.panelLink').click( function(e){
             e.preventDefault();
            var el = e.srcElement;
            // console.log(el)
            //make sure el is a link (shouldn't it be srcElement??)
            while(el.nodeName.toUpperCase()!='A')
            {
              el = el.parentNode;
            };

            var panel = App.addPanel(el.id,new Panel( 
                { 
                 el : el,
                 id:'panel-'+el.id,
                 title:el.id.split('-').join(' '),
                 buttons : {
                   backText: 'Back'
                 },
                 href:el.href
               }
             )).init();
             App.hideBrowserNavBar();
             App.next();
            
            return false;
          });

          x$('.tabs').each(function(el)
          {
            var tb = new TabPanel({el:el}).init();
          });

          window.addEventListener('load',function() {
            setTimeout(function() {
              App.hideBrowserNavBar();
              App.onOrientationChange();
            },0);
          });
          
          x$('.searchform').each(function(el){

            x$(el).on('submit',function(e)
            {
              new Panel( 
              { 
               el : e.srcElement,
               id:'panel-'+el.id,
               title:el.id.split('-').join(' '),
               buttons : {
                 backText: 'Back'
               },
               href:el.href
              }).init();
              // e.preventDefault();
            })
          });

          x$('.datepicker').each(function(el) {
            function openDate() {
             var now = new Date();
             var days = { };
             var years = { };
             var months = { 1: 'Jan', 2: 'Feb', 3: 'Mar', 4: 'Apr', 5: 'May', 6: 'Jun', 7: 'Jul', 8: 'Aug', 9: 'Sep', 10: 'Oct', 11: 'Nov', 12: 'Dec' };
 
             for( var i = 1; i < 32; i += 1 ) {
               days[i] = i;
             }

             for( i = now.getFullYear()-100; i < now.getFullYear()+1; i += 1 ) {
               years[i] = i;
             }

             SpinningWheel.addSlot(years, 'right', 1999);
             SpinningWheel.addSlot(months, '', 4);
             SpinningWheel.addSlot(days, 'right', 12);
 
             SpinningWheel.setCancelAction(function(e) { alert('cancel')});
             SpinningWheel.setDoneAction(function (e) { 
               function padZeros(value) {
                 return (value<10) ? '0' + value : value;
               }
               var results = SpinningWheel.getSelectedValues().keys;
	             document.getElementById('date').value = results[0]+'/'+padZeros(results[1])+'/'+padZeros(results[2]);
             });
             SpinningWheel.open();
            }

            x$(el).on('click',function(e) {
              openDate();
              
            })
          });
          // x$('#searchBut').on('click',function(e)
          //   {
          //     var el = e.srcElement();
          //      el.parentNode.submit();
          //     })
          if (config && config.flash && config.flash.text)
          {
            setTimeout(function() {
              // alert(x$('#flash').first())
              x$('#flash').css({
                opacity:'0',              
                webkitTransitionProperty: 'opacity',
                webkitTransitionDuration : '1s',
                webkitTransitionTimingFunction: 'ease-in-out',
                opacity:'0.8' //setting to '1' fades it out!
              },100);
            });
            //hide after some time
            setTimeout(function(){
              x$('#flash').css({
                webkitTransitionProperty: 'opacity',
                webkitTransitionDuration : '1s',
                webkitTransitionTimingFunction: 'ease-in-out',
                opacity:'0'
            })},4000);

          }
          
          document.body.addEventListener('orientationchange',this.onOrientationChange);          
          return this;          
        },
        /**
         * 
         *  
         */
        previous : function()
        {
          //slide right -> inactive
          if(this.currentPanelIndex>0)
          {
            this.panelStack[this.currentPanelIndex].o.hide(1);
            this.currentPanelIndex = Math.max(0,(this.currentPanelIndex-1));
            //slide right -> active
            this.panelStack[this.currentPanelIndex].o.show();
          }
          else 
          {
            history.go(-1);
          }
          return this
        },
        next : function() 
        {
          if(this.currentPanelIndex>=0)
          {
            //slide left <- inactive
            this.panelStack[this.currentPanelIndex].o.hide(-1);
            this.currentPanelIndex = Math.min((this.panelStack.length-1),(this.currentPanelIndex+1));
            //slide left <- active
            this.panelStack[this.currentPanelIndex].o.show();
            return this;
          }
        },
        addPanel: function addPanel(name,o) 
        {
          this.panelStack.push({name:name,o:o});
          o.on('beforeHide',this.onBeforePanelHide,this);
          o.on('afterHide',this.onAfterPanelHide,this);
          o.on('beforeShow',this.onBeforePanelShow,this);
          o.on('afterShow',this.onAfterPanelShow,this);
          return o;
        },
        removePanel : function removePanel(name)
        {
          name = name.replace('panel-','');
          var a = [];
          var panelToRemove;
          for (var i=0,len = this.panelStack.length;i<len;i++)
          {
            var panel = this.panelStack[i];
            if (panel.name!==name)
            {
              a.push(panel);
            }
            else {
              panel.o.destroy();
            }
            return this;
          }
          this.panelStack = a;
        },
        onAfterPanelHide : function afterPanelHide() {
          // console.log('aph',arguments);
        },
        onBeforePanelHide : function beforePanelHide() {
          // console.log('bph',arguments);
        },
        onAfterPanelShow : function afterPanelHide() {
          // console.log('aph',arguments);
        },
        onBeforePanelShow : function beforePanelShow() {
          // console.log('bps',arguments);
        },    
        onOrientationChange : function onOrientationChange(e) {
          var bodyEl = x$(document.body);
          (window.orientation ==0 | window.orientation==180) ? bodyEl.removeClass('landscape').addClass('portrait') : bodyEl.removeClass('portrait').addClass('landscape');
          App.hideBrowserNavBar();
        },
        hideBrowserNavBar : function hideBrowserNavBar()
        {
          window.scrollTo(0,1);
          return this;
        }        
      }
    ))();
  }()
);

/*
   window.addEventListener('DOMContentLoaded',function(){
      App.init();
});
*/