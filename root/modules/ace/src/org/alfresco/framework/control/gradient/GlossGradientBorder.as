package org.alfresco.framework.control.gradient
{
	import flash.display.*;
	import flash.geom.*;
	import flash.utils.*;
	
	import mx.core.EdgeMetrics;
	import mx.skins.halo.HaloBorder;
	import mx.utils.ColorUtil;
	import mx.utils.GraphicsUtil;
	
	public class GlossGradientBorder extends HaloBorder 
	{
		
		private var topCornerRadius:Number;		// top corner radius
		private var bottomCornerRadius:Number;	// bottom corner radius
		private var fillColors:Array;			// fill colors (two)
		private var setup:Boolean;
		
		// ------------------------------------------------------------------------------------- //
		
		private function setupStyles():void
		{
			fillColors = getStyle("fillColors") as Array;
			if (!fillColors) fillColors = [0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF];
			
			if (getStyle("cornerRadius") != null)
			{
				topCornerRadius = getStyle("cornerRadius") as Number;
			}
			else
			{				
				topCornerRadius = 0;	
			}

			if (getStyle("bottomCornerRadius") != null)
			{
				bottomCornerRadius = getStyle("bottomCornerRadius") as Number;
			}
			else
			{
				bottomCornerRadius = topCornerRadius;		
			}		
		}
		
		// ------------------------------------------------------------------------------------- //
		
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void
		{
			super.updateDisplayList(unscaledWidth, unscaledHeight);	
			
			setupStyles();
			
			var g:Graphics = graphics;
			var b:EdgeMetrics = borderMetrics;
			var w:Number = unscaledWidth - b.left - b.right;
			var h:Number = unscaledHeight - b.top - b.bottom;
			var m:Matrix = verticalGradientMatrix(0, 0, w, h);
				
		
			g.beginGradientFill("linear", fillColors, [1, 1, 1, 1], [0, 127, 127, 255], m);
			
			var tr:Number = Math.max(topCornerRadius-2, 0);
			var br:Number = Math.max(bottomCornerRadius-2, 0);
			
			GraphicsUtil.drawRoundRectComplex(g, b.left, b.top, w, h, tr, tr, br, br);
			g.endFill();
				
		}
		
	}
}
