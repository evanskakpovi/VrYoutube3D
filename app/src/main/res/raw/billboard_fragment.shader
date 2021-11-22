precision mediump float;
uniform sampler2D u_TextureSampler;
uniform vec4 u_Highlighter;

varying vec2 v_texCoords;
varying float diffuse;

void main() {
    vec4 v_Color = texture2D(u_TextureSampler, v_texCoords);
    gl_FragColor = v_Color * diffuse + u_Highlighter;
}
