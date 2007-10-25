/*  
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.ace.control.gradient
{
	import flash.display.*;
	import flash.geom.*;
	import flash.utils.*;
	
	import mx.core.EdgeMetrics;
	import mx.skins.halo.HaloBorder;
	import mx.utils.ColorUtil;
	import mx.utils.GraphicsUtil;
	
	public class SimpleGradientBorder extends HaloBorder 
	{
		
		private var topCornerRadius:Number;		// top corner radius
		private var bottomCornerRadius:Number;	// bottom corner radius
		private var fillColors:Array;			// fill colors (two)
		private var setup:Boolean;
		
		// ------------------------------------------------------------------------------------- //
		
		private function setupStyles():void
		{
			fillColors = getStyle("fillColors") as Array;
			if (!fillColors) fillColors = [0xFFFFFF, 0xFFFFFF];
			
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
						
			g.beginGradientFill("linear", fillColors, [1, 1], [0, 255], m);
			
			var tr:Number = Math.max(topCornerRadius-2, 0);
			var br:Number = Math.max(bottomCornerRadius-2, 0);
			
			GraphicsUtil.drawRoundRectComplex(g, b.left, b.top, w, h, tr, tr, br, br);
			g.endFill();
				
		}
		
	}
}
