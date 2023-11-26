#version 120

#include "shaders/utils.glsl"

uniform sampler2D u_current_texture;
uniform float u_time;
uniform float u_scale;

void main(void) {
    vec2 coord = gl_TexCoord[0].st;
    gl_FragColor = texture2D(u_current_texture, coord);
}