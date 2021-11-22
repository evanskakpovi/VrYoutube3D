package com.ekm.youtubevr3dvideosprod;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Billboard {
	private static FloatBuffer billboardVertices;
	private static FloatBuffer billboardNormals;
	private static FloatBuffer billboardTextureCoords;// Texture coordinates array
	private static int billboardProgram;

	private static int billboardPositionParam;
	private static int billboardNormalParam;
	private static int billboardTextureCoordsParam;//Shader param  for access to Texture Coordinates 
	private static int billboardTextureSamplerParam;//Shader Texture Sampler param for access to texture
	private static int billboardHighlighterParam;//Shader Hightlight Color param
	private static int billboardModelParam;
	private static int billboardModelViewParam;
	private static int billboardModelViewProjectionParam;
	private static int billboardLightPosParam;
	
	private float x, y, z, width, height;
	private boolean highlight = false;
	private int billboardTexture, resTexture;	
	private float[] modelMatrix,  modelViewMatrix,  modelViewProjectionMatrix;
	
	  public static final float[] BILLBOARD_COORDS = new float[] {
		  -1f,1f,0f,
		   1f,1f,0f,
		  -1f,-1f,0f,
		  -1f,-1f,0f,
		   1f,1f,0f,
		   1f,-1f,-0f,
	  };

	  public static final float[] BILLBOARD_NORMALS = new float[] {
	      0.0f, 0.0f, 1.0f,
	      0.0f, 0.0f, 1.0f,
	      0.0f, 0.0f, 1.0f,
	      0.0f, 0.0f, 1.0f,
	      0.0f, 0.0f, 1.0f,
	      0.0f, 0.0f, 1.0f,
	  };

	  //<---NEW: Texture coordinates for two triangles    
	  public static final float[] BILLBOARD_TEX_COORDS = new float[] {
//	      0f, 0f,//  00-------10
//	      0f, 1f,//  |       / |
//	      1f, 0f,//  |     /   |
//	      1f, 0f,//  |   /     |
//	      0f, 1f,//  | /       |
//	      1f, 1f,//  01-------11

              1f, 0f,//  00-------10
              0f, 0f,//  |       / |
              1f, 1f,//  |     /   |
              1f, 1f,//  |   /     |
              0f, 0f,//  | /       |
              0f, 1f,//  01-------11

	  };
	  
	  public void setHighlighting(boolean val){
		  highlight= val;
	  }
	  public static void whenSurfaceCreated(Context context){
		    ByteBuffer bbFloorVertices = ByteBuffer.allocateDirect(BILLBOARD_COORDS.length * 4);
		    bbFloorVertices.order(ByteOrder.nativeOrder());
		    billboardVertices = bbFloorVertices.asFloatBuffer();
		    billboardVertices.put(BILLBOARD_COORDS);
		    billboardVertices.position(0);

		    ByteBuffer bbFloorNormals = ByteBuffer.allocateDirect(BILLBOARD_NORMALS.length * 4);
		    bbFloorNormals.order(ByteOrder.nativeOrder());
		    billboardNormals = bbFloorNormals.asFloatBuffer();
		    billboardNormals.put(BILLBOARD_NORMALS);
		    billboardNormals.position(0);

		    
		    //<---NEW: Create Buffer for Texture Coordinates
		    ByteBuffer bbTextureCoords = ByteBuffer.allocateDirect(BILLBOARD_TEX_COORDS.length * 4);
		    bbTextureCoords.order(ByteOrder.nativeOrder());
		    billboardTextureCoords = bbTextureCoords.asFloatBuffer();
		    billboardTextureCoords.put(BILLBOARD_TEX_COORDS);
		    billboardTextureCoords.position(0);
		    //NEW--->
		    
		    int vertexShader = Utils.loadGLShader(context, GLES20.GL_VERTEX_SHADER, R.raw.billboard_vertex);
		    int fragmentShader = Utils.loadGLShader(context, GLES20.GL_FRAGMENT_SHADER, R.raw.billboard_fragment); //NEW: now use Texture fragment shader
		    billboardProgram = GLES20.glCreateProgram();

			GLES20.glAttachShader(billboardProgram, vertexShader);
		    GLES20.glAttachShader(billboardProgram, fragmentShader);
		    GLES20.glLinkProgram(billboardProgram);
		    GLES20.glUseProgram(billboardProgram);
		    
		    billboardModelParam = GLES20.glGetUniformLocation(billboardProgram, "u_Model");
		    billboardModelViewParam = GLES20.glGetUniformLocation(billboardProgram, "u_MVMatrix");
		    billboardModelViewProjectionParam = GLES20.glGetUniformLocation(billboardProgram, "u_MVP");
		    billboardLightPosParam = GLES20.glGetUniformLocation(billboardProgram, "u_LightPos");
		    billboardTextureSamplerParam = GLES20.glGetUniformLocation(billboardProgram, "u_TextureSampler");
		    
		    billboardHighlighterParam = GLES20.glGetUniformLocation(billboardProgram, "u_Highlighter");
		    
		    billboardPositionParam = GLES20.glGetAttribLocation(billboardProgram, "a_Position");
		    billboardNormalParam = GLES20.glGetAttribLocation(billboardProgram, "a_Normal");
		    
		    billboardTextureCoordsParam = GLES20.glGetAttribLocation(billboardProgram, "a_TexCoords");//NEW: Get TextureCoords param ids from shader

		    GLES20.glEnableVertexAttribArray(billboardPositionParam);
		    GLES20.glEnableVertexAttribArray(billboardNormalParam);
		    GLES20.glEnableVertexAttribArray(billboardTextureCoordsParam);
		    GLES20.glUseProgram(0);
	  }
    Context c;
	  public void init(Context context){
          c=context;
		int texPool[] = new int[1];//Now you need so one texture
		GLES20.glGenTextures(1, texPool, 0);
		billboardTexture = texPool[0];
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, billboardTexture);

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

		//Load image from resource and put in video memory
		TextureLoader texLoader = new TextureLoader();
		
		TextureLoader.TextureRawContainer texRaw = texLoader.texImage2DfromRes(this.resTexture, context.getResources());
		
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, texRaw.width, texRaw.height,
                0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, IntBuffer.wrap(texRaw.pixels));
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		 
		//NEW--->

	 }
	Billboard(float x, float y, float z, float width, float height, int texture){
		this.x=x;
		this.y=y;
		this.z=z;
		this.width=width;
		this.height=height;
		this.resTexture=texture;
		this.modelMatrix = new float[16];
		this.modelViewMatrix = new float[16];
		this.modelViewProjectionMatrix = new float[16];
	}
	void draw(float[] viewMatrix, float[] projectionMatrix, float[] lightPosInEyeSpace){
		
		float angle = (System.currentTimeMillis()%360000)*0.001f;
//		y=(float) Math.cos(angle + x)*3.0f; // So for fun
		
		//Now calculate billboard matrix
		float eyeX=viewMatrix[12];
		float eyeY=viewMatrix[13];
		float eyeZ=viewMatrix[14]; //Extract camera position from view matrix
		
		float upX=0;
		float upY=1;
		float upZ=0;
		
		float lookX = x-eyeX;
		float lookY = y-eyeY;
		float lookZ = z-eyeZ; //Direction to billboard
		
		float norm = (float) Math.sqrt(lookX * lookX + lookY * lookY + lookZ * lookZ);
		lookX/=norm;
		lookY/=norm;
		lookZ/=norm;

		// Cross product    up x look 
		float rightX = upY*lookZ-upZ*lookY;
		float rightY = upZ*lookX-upX*lookZ;
		float rightZ = upX*lookY-upY*lookX;
		
		
		modelMatrix[0] = rightX;//
		modelMatrix[1] = rightY;// Vector RIGHT
		modelMatrix[2] = rightZ;//
		modelMatrix[3] = 0;//
		modelMatrix[4] = upX;//
		modelMatrix[5] = upY;// Vector UP
		modelMatrix[6] = upZ;//
		modelMatrix[7] = 0;//
		modelMatrix[8] =  lookX;//
		modelMatrix[9] =  lookY;// Vector LOOK
		modelMatrix[10] = lookZ;//
		modelMatrix[11] = 0;    //
		// Add the translation in as well.
		modelMatrix[12] = x;
		modelMatrix[13] = y;
		modelMatrix[14] = z;
		modelMatrix[15] = 1;

		
		Matrix.scaleM(modelMatrix, 0, width, height, 1); //Scale simple square to needed rectangle
		
		Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);
		
		
		GLES20.glUseProgram(billboardProgram);
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0); //NEW: Begin setup texture 0
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, billboardTexture); //NEW: Use billboard texture (UID in GPU)
	    
	    // Set ModelView, MVP, position, normals, and color.
	    GLES20.glUniform3fv(billboardLightPosParam, 1, lightPosInEyeSpace, 0);
	    GLES20.glUniformMatrix4fv(billboardModelParam, 1, false, modelMatrix, 0);
	    GLES20.glUniformMatrix4fv(billboardModelViewParam, 1, false, modelViewMatrix, 0);
	    GLES20.glUniformMatrix4fv(billboardModelViewProjectionParam, 1, false,
                modelViewProjectionMatrix, 0);
	    
		GLES20.glUniform1i(billboardTextureSamplerParam, 0); //NEW: use GL_TEXTURE_0 (with billboard texture) as shader uniform


		if(highlight){ //Send yellow highlight to shader
			GLES20.glUniform4f(billboardHighlighterParam, 0.3f, 0.3f, 0.3f, 0.0f); //NEW: Highlight texture by custom color
		}
		else { 		   //Send black highlight to shader (no highlighting)
			GLES20.glUniform4f(billboardHighlighterParam, 0.0f, 0.0f, 0.0f, 0.0f);

//            route = true;
			 //NEW: Highlight texture by custom color
		}
	    GLES20.glVertexAttribPointer(billboardPositionParam, 3, GLES20.GL_FLOAT,
                false, 0, billboardVertices);
	    GLES20.glVertexAttribPointer(billboardNormalParam, 3, GLES20.GL_FLOAT, false, 0,
                billboardNormals);
		
	    GLES20.glVertexAttribPointer(billboardTextureCoordsParam, 2, GLES20.GL_FLOAT, false, 0,
                billboardTextureCoords); //NEW: set textureCoords pointer to use in shader

	    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0); //NEW: Reset texture
		
	    GLES20.glUseProgram(0);

	    Utils.checkGLError("drawing billboard");
	}
	
	public boolean isLookingAtObject(float[] headView, Float pitch_limit, float yaw_limit){
	    float[] initVec = { 0, 0, 0, 1.0f };
	    float[] objPositionVec = new float[4];

	    // Convert object space to camera space. Use the headView from onNewFrame.
	    Matrix.multiplyMM(modelViewMatrix, 0, headView, 0, modelMatrix, 0);
	    Matrix.multiplyMV(objPositionVec, 0, modelViewMatrix, 0, initVec, 0);

	    float pitch = (float) Math.atan2(objPositionVec[1], -objPositionVec[2]);
	    float yaw = (float) Math.atan2(objPositionVec[0], -objPositionVec[2]);

	    return Math.abs(pitch) < pitch_limit && Math.abs(yaw) < yaw_limit;


	}
	
	public void hideObject(float objectDistance) {
		double angle = (Math.random() * 2 * Math.PI);
		x=(float)(objectDistance* Math.cos(angle));
		y=(float)(Math.random()*2-4);
		z=(float)(objectDistance* Math.sin(angle));
	}
    public void hide() {
        x=0;
        y=0;
        z=0;
        height=0f;
        width=0f;
    }
}
