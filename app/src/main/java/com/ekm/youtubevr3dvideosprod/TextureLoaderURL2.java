package com.ekm.youtubevr3dvideosprod;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

//NEW: Read texture from bitmap
public class TextureLoaderURL2 {
	public class TextureRawContainer{
		public int width=-1;
		public int height=-1;
		public int[] pixels;
		public TextureRawContainer(int width, int height, int[] pixels) {
			this.width=width;
			this.height=height;
			this.pixels = pixels;
		}
	};
	public Bitmap loadBitmapById(String res)
	{

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inScaled = false;
        options.inDensity = DisplayMetrics.DENSITY_LOW;
        options.inTargetDensity = DisplayMetrics.DENSITY_LOW;
        options.inDither = false;
        return BitmapFactory.decodeFile(res, options);

	}

    public Bitmap loadBitmap(Bitmap bitmap)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inScaled = false;
        options.inDensity = DisplayMetrics.DENSITY_LOW;
        options.inTargetDensity = DisplayMetrics.DENSITY_LOW;
        options.inDither = false;
        return bitmap;
    }

	public TextureRawContainer texImage2DfromRes(String id){
		Bitmap bmp_tex = loadBitmapById(id);
		bmp_tex.recycle();
		return texImage2DfromBitmap(loadBitmapById(id));
	}
	public TextureRawContainer texImage2DfromBitmap(Bitmap bmp_tex)
	{
        System.out.println("reg "+bmp_tex.getHeight()*bmp_tex.getWidth() );
        int[] pixels = new int[bmp_tex.getWidth() * bmp_tex.getHeight()];
		bmp_tex.getPixels(pixels, 0, bmp_tex.getWidth(), 0, 0, bmp_tex.getWidth(), bmp_tex.getHeight());
		for (int i=0;i<pixels.length;i++) {
		    int argb = pixels[i];
		    pixels[i] = argb&0xff00ff00 | ((argb&0xff)<<16) | ((argb>>16)&0xff);
            if (i==pixels.length-2) {
                System.out.println(pixels.length+ " i: "+i+" | "+
                        bmp_tex.getWidth() * bmp_tex.getHeight()+ bmp_tex.getWidth() +" * "+ bmp_tex.getHeight());
            }
            if (i==pixels.length-1) {
                System.out.println(pixels.length+ " i: "+i+" | "+
                        bmp_tex.getWidth() * bmp_tex.getHeight()+ bmp_tex.getWidth() +" * "+ bmp_tex.getHeight());
                System.out.println(bmp_tex.getWidth());
                System.out.println(bmp_tex.getHeight());
                System.out.println(pixels);
            }

		}
		return new TextureRawContainer(bmp_tex.getWidth(),bmp_tex.getHeight(),pixels);
	}


}
