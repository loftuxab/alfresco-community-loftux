package component.textAccordian
{
	import mx.containers.Canvas;
	import mx.containers.VBox;
	import mx.core.UIComponent;
	import mx.core.IUIComponent;
	import flash.display.DisplayObject;
	import flash.events.MouseEvent;
	import flash.events.Event;

	/**
	 * Text accoridan UI control
	 * 
	 * @author Roy Wetherall
	 */
	public class TextAccordian extends VBox
	{
		/** Array containing text accordian items */
		private var _items:Array;
		
		/** The currently selected text accordian item */
		private var _selectedItem:TextAccordianItem;
		
		/**
		 * Create children method override
		 */
		protected override function createChildren():void
		{
			super.createChildren();
			
			var numChildren:int = this.getChildren().length;
			var i:int;
			this._items = new Array(numChildren);
			for (i = 0; i < numChildren; i++) 
			{
	    		var child:DisplayObject = getChildAt(i);	
				if (child is TextAccordianItem)
				{
					var item:TextAccordianItem = child as TextAccordianItem;
					this._items.push(item);	
					
					if (i == 0)
					{
						this.selectedItem = item;
					}
					
					item.addEventListener(MouseEvent.CLICK, onClick);
				}
				else
				{
					throw new Error("All children of the TextAccordian control must be TextAccordianItem's");
				}
			}
		}	
		
		/**
		 * Set the currently selected item
		 */
		public function set selectedItem(value:TextAccordianItem):void
		{
			if (this._selectedItem != null)
			{
				this._selectedItem.showContent = false;
			}
			if (value != null)
			{
				this._selectedItem = value;
				this._selectedItem.showContent = true;
				
				// Dispatch the selection change event
				dispatchEvent(new TextAccordianSelectionChangeEvent(TextAccordianSelectionChangeEvent.SELECTION_CHANGE, this._selectedItem));
			}
		}
		
		/**
		 * Get the currently selected item
		 */
		public function get selectedItem():TextAccordianItem
		{
			return this._selectedItem;
		}
		
		/**
		 * The click event handler
		 */
		private function onClick(event:Event):void
		{
			if (event.target as TextAccordianItem)
			{
				var clickedItem:TextAccordianItem = event.target as TextAccordianItem;
				if (clickedItem != this._selectedItem)
				{
					this.selectedItem = clickedItem;
				}
			}	
		}
	}
}