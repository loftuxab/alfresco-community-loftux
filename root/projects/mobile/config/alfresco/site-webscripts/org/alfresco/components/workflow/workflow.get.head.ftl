<script type="text/javascript" src="${url.context}/js/spinningwheel.js"></script>
<script type="text/javascript" charset="utf-8">//<![CDATA[
   window.addEventListener('DOMContentLoaded',function()
      {
         App.registerBehaviour('datePicker',function(rootNode)
            {
               x$(rootNode).find('.datepicker').each(function(el) {
                  function openDate(date) {
                   var date = date || new Date();
                   var days = { };
                   var years = { };
                   var months = { 1: 'Jan', 2: 'Feb', 3: 'Mar', 4: 'Apr', 5: 'May', 6: 'Jun', 7: 'Jul', 8: 'Aug', 9: 'Sep', 10: 'Oct', 11: 'Nov', 12: 'Dec' };
 
                   for( var i = 1; i < 32; i += 1 ) {
                     days[i] = i;
                   }

                   for( i = date.getFullYear(),end=date.getFullYear()+5; i < end; i++ ) {
                     years[i] = i;
                   }

                   SpinningWheel.addSlot(years, 'right', date.getFullYear());
                   SpinningWheel.addSlot(months, '', date.getMonth());
                   SpinningWheel.addSlot(days, 'right', date.getDate());
 
                   SpinningWheel.setCancelAction(function(e) { });
                   SpinningWheel.setDoneAction(function (e) { 
                     function padZeros(value) {
                       return (value<10) ? '0' + value : value;
                     }
                     var results = SpinningWheel.getSelectedValues().keys;
      	             document.getElementById('date').value = results[0]+'/'+padZeros(results[1]-1)+'/'+padZeros(results[2]);
      	             document.getElementById('datePicker').value = padZeros(results[2])+'/'+padZeros(results[1]-1)+'/'+results[0];//i18n
                      // document.getElementById('datePicker').value = new Date(results[0],padZeros(results[1]-1),padZeros(results[2])).toString();
                   });
                   SpinningWheel.open();
                  }

                  x$(el).on('click',function(e) {
                    openDate(new Date());
              
                  });
                });
            });
         App.initBehaviour('datePicker');
      }
   );
//]]></script>
