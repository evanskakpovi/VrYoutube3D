uniform mat4 u_Model;
uniform mat4 u_MVP;
uniform mat4 u_MVMatrix;
uniform vec3 u_LightPos;

attribute vec4 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_TexCoords; //NEW: add texture coordinates attribute (for array)

varying vec4 v_Color;
varying vec3 v_Grid;
varying vec2 v_texCoords; //NEW: for interpolate edge texture coords into fragment shader
varying float diffuse;
void main() {
   v_Grid = vec3(u_Model * a_Position);
   v_texCoords = a_TexCoords; //NEW: Pass from array to fragment shader through interpolator
   vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);
   vec3 modelViewNormal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));

   float distance = length(u_LightPos - modelViewVertex);
   vec3 lightVector = normalize(u_LightPos - modelViewVertex);
   diffuse = max(dot(modelViewNormal, lightVector), 0.5);

   diffuse = diffuse * (1.0 / (1.0 + (0.00001 * distance * distance)));
   gl_Position = u_MVP * a_Position;
}
