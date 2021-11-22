/*
 * Copyright 2014 Google Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ekm.youtubevr3dvideosprod;

/**
 * Contains vertex, normal and color data.
 */
public final class WorldLayoutData {

  
  public static final float[] FLOOR_COORDS = new float[] {
      200f, 0, -200f,
      -200f, 0, -200f,
      -200f, 0, 200f,
      200f, 0, -200f,
      -200f, 0, 200f,
      200f, 0, 200f,
  };

  public static final float[] FLOOR_NORMALS = new float[] {
      0.0f, 1.0f, 0.0f,
      0.0f, 1.0f, 0.0f,
      0.0f, 1.0f, 0.0f,
      0.0f, 1.0f, 0.0f,
      0.0f, 1.0f, 0.0f,
      0.0f, 1.0f, 0.0f,
  };
    //0.85, 0.7, 140
  public static final float[] FLOOR_COLORS = new float[] {
      0.85f, 0.7f, 0.55f, 1.0f,
          0.85f, 0.7f, 0.55f, 1.0f,
          0.85f, 0.7f, 0.55f, 1.0f,
          0.85f, 0.7f, 0.55f, 1.0f,
          0.85f, 0.7f, 0.55f, 1.0f,
          0.85f, 0.7f, 0.55f, 1.0f,
  };
  
  //<---NEW: Texture coordinates for two triangles    
  public static final float[] FLOOR_TEX_COORDS = new float[] {
      1f, 0f,//
      0f, 0f,//  00------10
      0f, 1f,//  |     /  |
      1f, 0f,//  |   /    |
      0f, 1f,//  | /      |
      1f, 1f,//  01------11

//          0f, 0f,//
//          0f, 0f,//  00------10
//          1f, 0f,//  |     /  |
//          0f, 1f,//  |   /    |
//          1f, 0f,//  | /      |
//          0f, 0f,//  01------11
  };
  //NEW--->
}
