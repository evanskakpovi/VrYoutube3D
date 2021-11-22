package com.ekm.youtubevr3dvideosprod;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//NEW: Read texture from bitmap
public class TextureLoaderURL {
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
//	public Bitmap loadBitmapById(int ids, Resources res)
//	{
//		BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inPurgeable = true;
//		options.inScaled = false;
//		options.inDensity = DisplayMetrics.DENSITY_LOW;
//		options.inTargetDensity = DisplayMetrics.DENSITY_LOW;
//		options.inDither = false;
//		return BitmapFactory.decodeResource(res, ids, options);
//	}

    public Bitmap loadBitmap(String src)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inScaled = false;
        options.inDensity = DisplayMetrics.DENSITY_LOW;
        options.inTargetDensity = DisplayMetrics.DENSITY_LOW;
        options.inDither = false;

        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input, null, options);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }

    }

	public TextureRawContainer texImage2DfromRes(String src){
		Bitmap bmp_tex = loadBitmap(src);
		bmp_tex.recycle();
		return texImage2DfromBitmap(loadBitmap(src));
	}
	public TextureRawContainer texImage2DfromBitmap(Bitmap bmp_tex)
	{
		int[] pixels = new int[bmp_tex.getWidth() * bmp_tex.getHeight()];
		bmp_tex.getPixels(pixels, 0, bmp_tex.getWidth(), 0, 0, bmp_tex.getWidth(), bmp_tex.getHeight());
		for (int i=0;i<pixels.length;i+=1) {
		    int argb = pixels[i];
		    pixels[i] = argb&0xff00ff00 | ((argb&0xff)<<16) | ((argb>>16)&0xff);
		}
		return new TextureRawContainer(bmp_tex.getWidth(),bmp_tex.getHeight(),pixels);
	}


}
