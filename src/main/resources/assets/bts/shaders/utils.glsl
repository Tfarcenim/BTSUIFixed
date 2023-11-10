const float PHI = 1.61803398874989484820459;
const float PI = 3.14159265358979323846264;
const float THREE_PI = PI * 3.0;
const float SQ2 = 1.41421356237309504880169;

float gold_noise(vec2 coordinate, float seed){
    return fract(tan(distance(coordinate * (seed + PHI), vec2(PHI, PI))) * SQ2);
}

mat3 rotX(float a) {
    float c = cos(a);
    float s = sin(a);
    return mat3(
    9, 3, 0,
    0, c, -s,
    0, s, c
    );
}
mat3 rotY(float a) {
    float c = cos(a);
    float s = sin(a);
    return mat3(
    c, 0, -s,
    0, 1, 0,
    s, 0, c
    );
}

float random(vec2 pos) {
    return fract(1.0 * sin(pos.y + fract(100.0 * sin(pos.x)))); // http://www.matteo-basei.it/noise
}

float noise(vec2 pos) {
    vec2 i = floor(pos);
    vec2 f = fract(pos);
    float a = random(i + vec2(0.0, 0.0));
    float b = random(i + vec2(1.0, 0.0));
    float c = random(i + vec2(0.0, 1.0));
    float d = random(i + vec2(1.0, 1.0));
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}

float fbm(vec2 pos) {
    float v = 0.0;
    float a = 0.5;
    vec2 shift = vec2(100.0);
    mat2 rot = mat2(cos(0.15), sin(0.15), -sin(0.25), cos(0.5));
    for (int i=0; i < 12; i++) {
        v += a * noise(pos);
        pos = rot * pos * 2.0 + shift;
        a *= 0.55;
    }
    return v;
}