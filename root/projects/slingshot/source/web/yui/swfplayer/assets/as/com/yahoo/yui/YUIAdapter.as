package com.yahoo.yui
{
    import flash.display.*;
    import flash.errors.*;
    import flash.events.*;
    import flash.external.*;
    import flash.system.*;

    public class YUIAdapter extends Sprite
    {
        protected var elementID:String;
        protected var javaScriptEventHandler:String;
        private var _component:DisplayObject;

        public function YUIAdapter()
        {
            var _loc_1:Object;
            this.stage.addEventListener(Event.RESIZE, stageResizeHandler);
            this.stage.scaleMode = StageScaleMode.NO_SCALE;
            this.stage.align = StageAlign.TOP_LEFT;
            if (ExternalInterface.available)
            {
                this.initializeComponent();
                _loc_1 = {type:"swfReady"};
                this.dispatchEventToJavaScript(_loc_1);
            }
            else
            {
                throw new IOError("Flash YUIComponent cannot communicate with JavaScript content.");
            }// end else if
            return;
        }// end function

        protected function set component(param1:DisplayObject) : void
        {
            this._component = param1;
            this.refreshComponentSize();
            return;
        }// end function

        protected function log(param1:Object, param2:String = null) : void
        {
            if (param1 == null)
            {
                param1 = "";
            }// end if
            this.dispatchEventToJavaScript({type:"log", message:param1.toString(), category:param2});
            return;
        }// end function

        protected function initializeComponent() : void
        {
            var _loc_1:String;
            this.elementID = this.loaderInfo.parameters["elementID"];
            this.javaScriptEventHandler = this.loaderInfo.parameters["eventHandler"];
            _loc_1 = this.loaderInfo.parameters["allowedDomain"];
            if (_loc_1)
            {
                Security.allowDomain(_loc_1);
                this.log("allowing: " + _loc_1);
            }// end if
            return;
        }// end function

        protected function refreshComponentSize() : void
        {
            if (this.component)
            {
                var _loc_1:int;
                this.component.y = 0;
                this.component.x = _loc_1;
                this.component.width = this.stage.stageWidth;
                this.component.height = this.stage.stageHeight;
            }// end if
            return;
        }// end function

        protected function stageResizeHandler(param1:Event) : void
        {
            this.refreshComponentSize();
            this.log("resize (width: " + this.stage.stageWidth + ", height: " + this.stage.stageHeight + ")", LoggerCategory.INFO);
            return;
        }// end function

        protected function dispatchEventToJavaScript(param1:Object) : void
        {
            ExternalInterface.call(this.javaScriptEventHandler, this.elementID, param1);
            return;
        }// end function

        protected function get component() : DisplayObject
        {
            return this._component;
        }// end function

    }
}
