#version 430 core

uniform mat4 modelMatrix;
uniform sampler2D texSampler;
uniform vec3 lightPos;
uniform vec3 lightIntensities;

in vec3 fragPos;
in vec2 fragUV;
in vec3 fragNormal;

out vec4 finalColor;
void main()
{
  vec3 normal = normalize(transpose(inverse(mat3(modelMatrix))) * fragNormal);
  vec3 surfacePos = vec3(modelMatrix * vec4(fragPos,1));
  vec4 surfaceColor = texture(texSampler, fragUV);
  vec3 surfaceToLight = normalize(lightPos - surfacePos);
  
  float diffuseCoeff = max(0.0,dot(normal,surfaceToLight));
  vec3 diffuse = diffuseCoeff * surfaceColor.rgb * lightIntensities;
  
  finalColor = vec4(diffuse,surfaceColor.a);
}
