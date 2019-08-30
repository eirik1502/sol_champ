#version 430 core

layout(location=0) in vec3 position;

uniform mat4 modelTransform;
uniform mat4 viewTransform;
uniform mat4 projectionTransform;

void main() {
	vec4 pos = vec4(position, 1.0);
    vec4 portPos = projectionTransform * viewTransform * modelTransform * pos;

    gl_Position = portPos;
}