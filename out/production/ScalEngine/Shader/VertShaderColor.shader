#version 430 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec4 color;
layout(location = 10) uniform mat4 projectionMatrix;
layout(location = 11) uniform mat4 viewMatrix;
layout(location = 12) uniform mat4 modelMatrix;
out vec4 fragmentColor;
void main()
{
//    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 1.0);
    gl_Position = position;
    fragmentColor = color;
}
