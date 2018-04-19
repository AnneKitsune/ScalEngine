#version 430 core

layout(location = 0) in vec3 vertposition;
//layout(location = 1) in vec3 color;
layout(location=1) in vec2 vertexUV;
layout(location = 10) uniform mat4 mvp;
//out vec4 fragmentColor;
out vec2 UV;
void main()
{
//    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(vertposition, 1.0);
    gl_Position = mvp * vec4(vertposition,1.0);//Calculate the matrices in the program
    UV = vertexUV;
}
