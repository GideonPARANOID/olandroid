uniform mat4 uMVPMatrix;

attribute vec4 vPosition;
attribute vec2 aTextureCoords;

varying vec2 vTextureCoords;


void main() {
   vTextureCoords = aTextureCoords;
   gl_Position = uMVPMatrix * vPosition;
}
