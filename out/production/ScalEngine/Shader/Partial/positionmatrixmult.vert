#header
in vec3 position;
uniform mat4 positionmatrix;
#main
gl_Position = positionmatrix * vec4(position,1.0);
