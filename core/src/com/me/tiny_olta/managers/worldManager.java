package com.me.tiny_olta.managers;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.sprites.olta;

public class worldManager {

    private static World world;
    private static Box2DDebugRenderer debugRenderer;
    private static RayHandler rayHandler;
    private static float light_ambiency = 0.5f;
    private static boolean isInitiated = false;
    private static Light dayLight, nightLight, coneLight;
    private static boolean isDay = true;

    public static void init(float player_x){
        if (!isInitiated) {
            world = new World(new Vector2(0, -10), true);
            debugRenderer = new Box2DDebugRenderer();
            rayHandler = new RayHandler(world);
            rayHandler.setShadows(true);
            rayHandler.setBlurNum(3);
            rayHandler.setAmbientLight(light_ambiency);
            isInitiated = true;

            createDayLight();
            createNightLight();
            createTorchLight(player_x);
        }else throw new IllegalStateException("do not attempt to call init method twice, constructing world and other" +
                "physics related stuffs are heavy, and should not be created more than once");
    }

    public static World getWorld(){
        if (!isInitiated) throw new IllegalStateException("call init method first");
        return world;
    }

    public static Box2DDebugRenderer getRenderer(){
        if (!isInitiated) throw new IllegalStateException("call init method first");
        return debugRenderer;
    }

    public static Light createDayLight(){
        if (!isInitiated) throw new IllegalStateException("call init method first");
        if (dayLight == null){
            dayLight = new PointLight(rayHandler, 1000, Color.YELLOW, constants.RAY_CASTING_DISTANCE,
                    constants.SUN_AND_MOON_DISTANCE, constants.SUN_AND_MOON_POSITION_IN_SKY);
        }
        return dayLight;
    }

    public static Light getDayLight() {
        if (dayLight == null) throw new NullPointerException("day light has not been created yet");
        return dayLight;
    }

    public static Light createNightLight(){
        if (!isInitiated) throw new IllegalStateException("call init method first");
        if (nightLight == null){
            nightLight = new PointLight(rayHandler, 1000, new Color(0, 0, 0.3f, 1f), constants.RAY_CASTING_DISTANCE,
                    constants.SUN_AND_MOON_DISTANCE, constants.SUN_AND_MOON_POSITION_IN_SKY);
            nightLight.setActive(false);
        }
        return nightLight;
    }

    public static Light getNightLight() {
        if (nightLight == null) throw new NullPointerException("night light has not been created yet");
        return nightLight;
    }

    public static Light createTorchLight(float player_x){
        if (!isInitiated) throw new IllegalStateException("call init method first");
        if (coneLight == null){
            coneLight = new ConeLight(rayHandler, 1000, Color.WHITE, 350, player_x, 28,
                    0f, 15f);
            coneLight.setContactFilter((short) 2, (short) 4, (short) 8);
            coneLight.setSoftnessLength(2.5f);
            coneLight.setActive(false);
        }
        return coneLight;
    }

    public static Light getTorchLight() {
        if (coneLight == null) throw new NullPointerException("torch light has not been created yet");
        return coneLight;
    }

    public static void update(olta player, float delta){
        if (!player.isDead()){
            if (dayLight.getPosition().x < player.getPosition().x - constants.SUN_AND_MOON_DISTANCE && isDay){
                dayLight.setActive(false);
                nightLight.setPosition(player.getPosition().x + constants.SUN_AND_MOON_DISTANCE,
                        nightLight.getPosition().y);
                nightLight.setActive(true);
                coneLight.setActive(true);
                isDay = false;
            }else if (nightLight.getPosition().x < player.getPosition().x - constants.SUN_AND_MOON_DISTANCE && !isDay){
                rayHandler.setAmbientLight(0.5f);
                dayLight.setPosition(player.getPosition().x + constants.SUN_AND_MOON_DISTANCE, dayLight.getPosition().y);
                dayLight.setActive(true);
                nightLight.setActive(false);
                coneLight.setActive(false);
                isDay = true;
            }
            if (!isDay){
                if (light_ambiency > 0.05f){
                    light_ambiency -= delta/constants.AMBIENT_DIVIDER;
                    rayHandler.setAmbientLight(light_ambiency);
                }else rayHandler.setAmbientLight(0.05f);
                coneLight.setPosition(player.getPosition().x + 10, player.getPosition().y + 12);
                //nightLight.setPosition(nightLight.getPosition().x + 80 * (1/delta), nightLight.getPosition().y);
            }else {
                if (light_ambiency < 0.5f){
                    light_ambiency += delta/constants.AMBIENT_DIVIDER;
                    rayHandler.setAmbientLight(light_ambiency);
                }else rayHandler.setAmbientLight(0.5f);
                //dayLight.setPosition(dayLight.getPosition().x + 80 * (1/delta), dayLight.getPosition().y);
            }
        }
        rayHandler.update();
    }

    public static boolean isDay() {
        return isDay;
    }

    public static void renderLight(OrthographicCamera gameCamera){
        rayHandler.setCombinedMatrix(gameCamera);
        rayHandler.render();
    }

    public static void useCustomViewPort(FitViewport gamePort){
        int gutterLeft = gamePort.getLeftGutterWidth();
        int gutterBot = gamePort.getBottomGutterHeight();
        int gutterWidth = Gdx.graphics.getWidth() - (2 * gutterLeft);
        int gutterHeight = Gdx.graphics.getHeight() - (2 * gutterBot);
        rayHandler.useCustomViewport(gutterLeft, gutterBot, gutterWidth, gutterHeight);
    }

    public static void dispose(){
        dayLight.dispose();
        nightLight.dispose();
        coneLight.dispose();
        rayHandler.dispose();
        debugRenderer.dispose();
        world.dispose();
        isInitiated = false;
    }
}
