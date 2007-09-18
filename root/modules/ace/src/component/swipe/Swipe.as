package component.swipe
{
	import mx.containers.Canvas;
	import mx.events.ChildExistenceChangedEvent;
	import mx.events.FlexEvent;
	import flash.events.Event;
	import mx.core.UIComponent;

    /**
     * General purpose Swipe control
     */
	public class Swipe extends Canvas
	{
		/** Internal swipe control */
		private var swipe:SwipeInternal;
		
		/**
		 * Constructor
		 */
		public function Swipe()
		{
			super();
			
			// Register interest in the creation complete event
			this.addEventListener(FlexEvent.CREATION_COMPLETE, onCreationComplete);
		}				
		
		/**
		 * Create complete event handler
		 */
		public function onCreationComplete(event:Event):void
		{
			var children:Array = this.getChildren();
			if (children.length != 2)
			{
				throw new Error("Control expects two child UI objects");
			}
			
			var childOne:UIComponent = children[0];
			var childTwo:UIComponent = children[1];			
			
			this.removeAllChildren();
			
			this.swipe = new SwipeInternal();
			swipe.setChildOne(childOne);
			swipe.setChildTwo(childTwo);
			
			// Add the configured swipe control
			this.addChild(swipe);	
		}
		
		/**	
		 * Shows the primary state
		 */
		public function showPrimaryState():void
		{
			swipe.showPrimaryState();	
		}
		
		/**
		 * Shows the secondard state
		 */
		public function showSecondaryState():void
		{
			swipe.showSecondaryState();	
		}
		
		
	}
}