precision mediump float;

uniform vec4 vColour;

uniform bool uUseTexture;
uniform sampler2D uTexture;
varying vec2 vTextureCoordinate;



void main() {
   if (uUseTexture) {
      gl_FragColor = texture2D(uTexture, vTextureCoordinate);

   } else {
      gl_FragColor = vColour;
   }
}
