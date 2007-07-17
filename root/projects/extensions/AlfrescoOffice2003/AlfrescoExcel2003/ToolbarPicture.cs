using System;
using System.Drawing;
using System.Runtime.InteropServices;
using stdole;

namespace AlfrescoExcel2003
{
   /// <summary>
   /// Implements IPictureDisp so we can use images on the Office toolbar
   /// </summary>
   public class ToolbarPicture : IPictureDisp, IPicture
   {
      Bitmap _image = null;
      IntPtr _handle = IntPtr.Zero;

      public ToolbarPicture(Bitmap image)
      {
         _image = image;
      }

      ~ToolbarPicture()
      {
         if (_handle != IntPtr.Zero)
         {
            DeleteObject(_handle);
         }
      }

      [DllImport("gdi32.dll")]
      static extern void DeleteObject(IntPtr _handle);

      public int Width
      {
         get
         {
            return _image.Width;
         }
      }

      public int Height
      {
         get
         {
            return _image.Height;
         }
      }

      public short Type
      {
         get
         {
            return 1;
         }
      }

      public int Handle
      {
         get
         {
            if (_handle == IntPtr.Zero)
            {
               _handle = _image.GetHbitmap();
            }
            return _handle.ToInt32();
         }
      }

      public int hPal
      {
         get
         {
            return 0;
         }
         set
         {
         }
      }

      public void Render(int hdc, int x, int y, int cx, int cy, int xSrc, int ySrc, int cxSrc, int cySrc, IntPtr prcWBounds)
      {
         Graphics graphics = Graphics.FromHdc(new IntPtr(hdc));
         graphics.DrawImage(_image, new Rectangle(x, y, cx, cy), xSrc, ySrc, cxSrc, cySrc, GraphicsUnit.Pixel);
      }

      #region IPicture Members
      public int Attributes
      {
         get
         {
            return 0;
         }
      }

      public int CurDC
      {
         get
         {
            return 0;
         }
      }

      public bool KeepOriginalFormat
      {
         get
         {
            return false;
         }
         set
         {
         }
      }

      public void PictureChanged()
      {
      }

      public void SaveAsFile(IntPtr pstm, bool fSaveMemCopy, out int pcbSize)
      {
         pcbSize = 0;
      }     
 
      public void SelectPicture(int hdcIn, out int phdcOut, out int phbmpOut)
      {
         phdcOut = 0;
         phbmpOut = 0;
      }

      public void SetHdc(int hdc)
      {
      }
      #endregion
   }
}
