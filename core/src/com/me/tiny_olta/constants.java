package com.me.tiny_olta;

public class constants {

    private static float stateTimer = 0f;

    public static final int WORLD_WIDTH = 800;
    public static final int WORLD_HEIGHT = 480;
    public static final int GAME_WIDTH = WORLD_WIDTH/2;
    public static final int GAME_HEIGHT = WORLD_HEIGHT/2;
    public static final float PLAYER_WIDTH = 24f;//32f;
    public static final float PLAYER_HEIGHT = 24f;//32f;
    public static final int ENEMY_WIDTH = 14;//18;
    public static final int ENEMY_HEIGHT = 14;//18;
    public static final float IDEAL_FLYING_ENEMY_POSITION = constants.GROUND_OFFSET + 11;
    public static final float PLAYER_VELOCITY = 80f;
    public static final float ROCK_VELOCITY = 270f;
    public static final int GRAVITY = -15;
    public static final float GROUND_OFFSET = 15;
    public static final int JUMP_IMPULSE = 350;
    public static final float ENEMY_DISTANCE = 300f;
    public static final int ENEMY_COUNT = 5;
    public static float ENEMY_VELOCITY = 20f;
    public static final int EXPLOSION_RADIUS = 12;
    public static final int TREE_COUNT = 28;
    public static final int FIXED_TREE_DISTANCE = 30;
    public static final int BACKGROUND_ELEMENT_COUNT = 3;
    public static final int SUN_AND_MOON_DISTANCE = 2800;
    public static final int AMBIENT_DIVIDER = 20;
    public static int RAY_CASTING_DISTANCE = 1800;
    public static final int SUN_AND_MOON_POSITION_IN_SKY = 800;
    public static final int SETTINGS_TITLE_SCREEN_REVEAL_VELOCITY = 1000;

    public static final float RAIN_CLUSTER_WIDTH = 400;
    public static final float RAIN_CLUSTER_HEIGHT = 240;

    public static final float CLOUDS_DISTANCE = 80;

    public static final float WEATHER_DAYLIGHT_COLOR_FACTOR = 1f;

    public static final float MIST_WIDTH = 400;
    public static final float MIST_HEIGHT = 240;
    public static void increaseEnemyVelocity(float delta){
        stateTimer += delta;
        if (stateTimer > 2f){
            ENEMY_VELOCITY += 5f;
            stateTimer = 0;
        }
    }
}
