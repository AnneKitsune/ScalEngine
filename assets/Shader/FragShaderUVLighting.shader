#version 430 core

#define MAX_LIGHTS 64

uniform int numLights;

uniform mat4 mMatrix;
uniform vec3 cameraPos;
uniform sampler2D texSampler;

uniform vec3 materialSpecularColor;
uniform float materialShininess;

uniform struct Light {
  vec4 position;
  vec3 intensities;
  float ambientCoefficient;
  float attenuation;
  float coneAngle;
  vec3 coneDirection;
} lights[MAX_LIGHTS];

in vec3 fragPos;
in vec2 fragUV;
in vec3 fragNormal;

out vec4 finalColor;

vec3 applyLight(Light light, vec3 surfaceColor, vec3 normal, vec3 surfacePos, vec3 surfaceToCamera){
  vec3 surfaceToLight;
  float attenuation = 1.0;
  if(light.position.w == 0.0){
    //directional light
    surfaceToLight = normalize(light.position.xyz);
    attenuation = 1.0;
  } else {
    //point light
    surfaceToLight = normalize(light.position.xyz - surfacePos);
    float distanceToLight = length(light.position.xyz - surfacePos);
    float attenuation = 1.0 / (1.0 + light.attenuation * pow(distanceToLight,2));
    //Cone restriction
    float lightToSurfaceAngle = degrees(acos(dot(-surfaceToLight,normalize(light.coneDirection))));
    if(lightToSurfaceAngle > light.coneAngle)
      attenuation = 0.0;
  }
  //ambient
  vec3 ambient = light.ambientCoefficient * surfaceColor.rgb * light.intensities;
  //diffuse
  float diffuseCoefficient = max(0.0,dot(normal,surfaceToLight));
  vec3 diffuse = diffuseCoefficient * surfaceColor.rgb * light.intensities;
  //specular
  float specularCoefficient = 0.0;
  if(diffuseCoefficient > 0.0)
    specularCoefficient = pow(max(0.0,dot(surfaceToCamera,reflect(-surfaceToLight,normal))), materialShininess);
  vec3 specular = specularCoefficient * materialSpecularColor * light.intensities;    

  //Linear color
  return ambient + attenuation*(diffuse + specular);
}

void main()
{
  vec3 normal = normalize(transpose(inverse(mat3(mMatrix))) * fragNormal);
  vec3 surfacePos = vec3(mMatrix * vec4(fragPos,1));
  vec4 surfaceColor = texture(texSampler, fragUV);
  vec3 surfaceToCamera = normalize(cameraPos - surfacePos);
  

  //-----Do we need gamma color correction?----
  vec3 linearColor = vec3(0);
  for(int i = 0;i<numLights;++i){
    linearColor += applyLight(lights[i],surfaceColor.rgb,normal,surfacePos,surfaceToCamera);
  }
    linearColor += applyLight(lights[0],surfaceColor.rgb,normal,surfacePos,surfaceToCamera);
  finalColor = vec4(linearColor,surfaceColor.a);
}
