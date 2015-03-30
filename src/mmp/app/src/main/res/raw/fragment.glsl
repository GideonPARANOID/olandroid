precision mediump float;

uniform vec4 vColour;
uniform bool uUseTexture;
uniform sampler2D uTexture;

varying vec2 vTextureCoords;


void main() {
   if (uUseTexture) {
      gl_FragColor = texture2D(uTexture, vTextureCoords);

   } else {
      gl_FragColor = vColour;
   }
}
