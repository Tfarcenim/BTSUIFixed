#version 120

#include "shaders/utils.glsl"

uniform sampler2D u_current_texture;
uniform float u_time;
uniform float u_scale;

void main(void) {
    vec2 coord = gl_TexCoord[0].st;
    vec4 tex_col = texture2D(u_current_texture, coord);
    vec2 pos = (gl_FragCoord.xy * 2.2 - coord) * 0.005 * (1.0 / u_scale);
    float td = u_time * 0.0025;
    float f = fbm(pos * 2.0 * vec2(fbm(pos - td), fbm(pos / 2.0 - td)));
    vec3 base_col = mix((f * 1.5) * vec3(0.04, 0.8, 0.4), tex_col.rgb, 0.8);
    gl_FragColor = vec4(base_col, tex_col.a);// Use alpha from texture
}