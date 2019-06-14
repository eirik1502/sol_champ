#version 430 core


uniform vec3 color;
uniform float time;

out vec4 outputColor;

float random (vec2 st) {
	return fract(sin(dot(st.xy,
	vec2(12.9898,78.233)))*
	43758.5453123);
}

float noise (in vec2 st) {
	vec2 i = floor(st);
	vec2 f = fract(st);

	// Four corners in 2D of a tile
	float a = random(i);
	float b = random(i + vec2(1.0, 0.0));
	float c = random(i + vec2(0.0, 1.0));
	float d = random(i + vec2(1.0, 1.0));

	// Smooth Interpolation

	// Cubic Hermine Curve.  Same as SmoothStep()
	vec2 u = f*f*(3.0-2.0*f);
	// u = smoothstep(0.,1.,f);

	// Mix 4 coorners percentages
	return mix(a, b, u.x) +
	(c - a)* u.y * (1.0 - u.x) +
	(d - b) * u.x * u.y;
}

void main() {

//	vec2 st = gl_FragCoord.xy/sin(time);
//	vec2 st = gl_FragCoord.xy/sin(time)*sin(time);

	vec2 st = (gl_FragCoord.xy+time*5)/vec2(160/2, 90/2);
	time;

//	float fade = random(st);
	float fade = noise(st);
	fade = fade*0.5 -0.1;

	outputColor = vec4(color, 1 - fade);
//	outputColor = vec4(color, 1.0);
}