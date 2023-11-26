#version 120

#include "shaders/utils.glsl"

uniform sampler2D u_current_texture;
uniform float u_time;
uniform float u_scale;
uniform float u_hurt_factor;

void main(void) {
    vec2 coord = gl_TexCoord[0].st;
    vec4 color = texture2D(u_current_texture, coord);

    float d = clamp(sin(u_time * (0.25 + u_hurt_factor)), 0.0, 0.5) * u_hurt_factor;
    color.r += d;
    color.g += d;
    color.b += d;

    gl_FragColor = color;
}