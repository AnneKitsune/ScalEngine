#version 430 core

uniform mat4 vpMatrix;
uniform mat4 mMatrix;

in vec3 vertPos;
in vec2 vertUV;
in vec3 vertNormal;

out vec3 fragPos;
out vec2 fragUV;
out vec3 fragNormal;
void main()
{
  fragPos = vertPos;
  fragUV = vertUV;
  fragNormal = vertNormal;

  gl_Position = vpMatrix * mMatrix * vec4(vertPos,1.0);
}
