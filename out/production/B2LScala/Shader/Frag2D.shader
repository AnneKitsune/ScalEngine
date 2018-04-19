#version 430 core
uniform sampler2D texSampler;

in vec2 fragUV;

out vec4 finalColor;
void main()
{
  finalColor = texture(texSampler, fragUV);
}
