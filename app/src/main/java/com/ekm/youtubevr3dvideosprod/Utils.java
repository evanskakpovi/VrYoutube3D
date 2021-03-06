package com.ekm.youtubevr3dvideosprod;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utils {
	  public static final String TAG = "MainActivity";
	/**
	   * Converts a raw text file, saved as a resource, into an OpenGL ES shader.
	   *
	   * @param type The type of shader we will be creating.
	   * @param resId The resource ID of the raw text file about to be turned into a shader.
	   * @return The shader object handler.
	   */
	  public static int loadGLShader(Context context, int type, int resId) {
	    String code = readRawTextFile(context, resId);
	    int shader = GLES20.glCreateShader(type);
	    GLES20.glShaderSource(shader, code);
	    GLES20.glCompileShader(shader);

	    // Get the compilation status.
	    final int[] compileStatus = new int[1];
	    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

	    // If the compilation failed, delete the shader.
	    if (compileStatus[0] == 0) {
	      Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
	      GLES20.glDeleteShader(shader);
	      shader = 0;
	    }

	    if (shader == 0) {
	      throw new RuntimeException("Error creating shader.");
	    }

	    return shader;
	  }
	  /**
	   * Converts a raw text file into a string.
	   *
	   * @param resId The resource ID of the raw text file about to be turned into a shader.
	   * @return The context of the text file, or null in case of error.
	   */
	  public static String readRawTextFile(Context context, int resId) {
	    InputStream inputStream = context.getResources().openRawResource(resId);
	    try {
	      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	      StringBuilder sb = new StringBuilder();
	      String line;
	      while ((line = reader.readLine()) != null) {
	        sb.append(line).append("\n");
	      }
	      reader.close();
	      return sb.toString();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return null;
	  }

    public static String readRawTextFile(String src) {
        try {
        URL url = new URL(src);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.connect();
//        InputStream input = connection.getInputStream();

        InputStream inputStream =  connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
	   * Checks if we've had an error inside of OpenGL ES, and if so what that error is.
	   *
	   * @param label Label to report in case of error.
	   */
	  public static void checkGLError(String label) {
	    int error;
	    while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
	      Log.e(TAG, label + ": glError " + error);
	      throw new RuntimeException(label + ": glError " + error);
	    }
	  }


}
