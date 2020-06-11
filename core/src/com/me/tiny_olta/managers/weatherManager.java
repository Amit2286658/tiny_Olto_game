package com.me.tiny_olta.managers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.me.tiny_olta.constants;

public class weatherManager {

    private static Array<rainCluster> clusters = new Array<>();
    private static boolean isRaining = false, prepareToStop = false;
    public static boolean shouldDraw = false;
    private static boolean shouldDrawRain = false;
    private static mist mist1, mist2;
    private static sky sky1, sky2;
    private static lightning lightStrike;
    private static float skyAlpha = 0.1f, mistAlpha = 0.1f, rainAlpha = 0.1f;
    private static float r = 0f, g = 0f, b = 0f;

    public static void rain(){
        isRaining = true;
        shouldDraw = true;
        shouldDrawRain = MathUtils.randomBoolean();
        prepareToStop = false;
    }

    public static void stopRain(){
        prepareToStop= true;
    }

    public static boolean isRaining() {
        return isRaining;
    }

    public static void update(){
        if (prepareToStop){
            isRaining = false;
            if (rainAlpha == 0.1f && mistAlpha == 0.1f && skyAlpha == 0.1f){
                shouldDraw = false;
                prepareToStop = false;
            }
        }
    }

    /*create a grid of 2x2 rain cluster, 2 in horizontal and 2 in vertical*/
    public static void createRain(){
        clusters.add(new rainCluster(500, 240));
        clusters.add(new rainCluster(500, 480));
        clusters.add(new rainCluster(900, 240));
        clusters.add(new rainCluster(900, 480));
        mist1 = new mist(500, 0);
        mist2 = new mist(900, 0);
        mist2.flipTexture(true, false);
    }

    public static void createLightning(){
        lightStrike = new lightning();
    }

    public static void updateLightning(float delta){
        lightStrike.update(delta);
    }

    public static void updateLightProperties(float delta){
        Color dayColor = worldManager.getDayLight().getColor();
        r = dayColor.r;
        g = dayColor.g;
        b = dayColor.b;
        if (isRaining){
            if ( r > 0.24f){
                r -= constants.WEATHER_DAYLIGHT_COLOR_FACTOR * delta;
            }else if (r < 0.24f){
                r += constants.WEATHER_DAYLIGHT_COLOR_FACTOR * delta;
            }else r = 0.24f;

            if (g > 0.24f){
                g -= constants.WEATHER_DAYLIGHT_COLOR_FACTOR * delta;
            }else if (g < 0.24f){
                g += constants.WEATHER_DAYLIGHT_COLOR_FACTOR * delta;
            }else g = 0.24f;

            if (b < 0.24f){
                b += constants.WEATHER_DAYLIGHT_COLOR_FACTOR * delta;
            }else if (b > 0.24f){
                b -= constants.WEATHER_DAYLIGHT_COLOR_FACTOR * delta;
            }else b = 0.24f;

            worldManager.getDayLight().setColor(r, g, b, dayColor.a);

            if (constants.RAY_CASTING_DISTANCE > 1000){
                constants.RAY_CASTING_DISTANCE -= 10 * delta;
            }else constants.RAY_CASTING_DISTANCE = 1000;
        }else {
            /*the world manager will take care of the light of the sun, the world manager looks if the raining switch is on,
            * and updates accordingly, therefore there's no need to do anything here.*/
            if (constants.RAY_CASTING_DISTANCE < 1800){
                float LightDistanceDifference = (1800 - 1000)/9f;
                constants.RAY_CASTING_DISTANCE += LightDistanceDifference * delta;
            }else constants.RAY_CASTING_DISTANCE = 1800;
        }
        worldManager.getDayLight().setDistance(constants.RAY_CASTING_DISTANCE);
        worldManager.getNightLight().setDistance(constants.RAY_CASTING_DISTANCE);
    }

    public static void createSky(){
        sky1 = new sky(0, 180);
        sky2 = new sky(800, 180);
        sky2.flipTexture(true, false);
    }

    public static void updateSky(float delta, OrthographicCamera uiCamera){
        sky1.update(delta);
        sky2.update(delta);

        if (isRaining) {
            if (worldManager.isDay()) {
                if (skyAlpha < 1f) {
                    skyAlpha += skyAlpha * delta/4;
                } else skyAlpha = 1f;
            } else {
                float alphaDifference = (0.1f - 1f) / 8f;
                if (skyAlpha > 0.1f) {
                    skyAlpha += alphaDifference * delta;
                } else skyAlpha = 0.1f;
            }
        }else {
            float alphaDifference = (0.1f - 1f) / 8f;
            if (skyAlpha > 0.1f) {
                skyAlpha += alphaDifference * delta;
            } else skyAlpha = 0.1f;
        }

        if (sky1.getPosition().x + constants.WORLD_WIDTH < uiCamera.position.x - uiCamera.viewportWidth/2){
            sky1.reposition(sky1.getPosition().x + 2 * constants.WORLD_WIDTH);
        }
        if (sky2.getPosition().x + constants.WORLD_WIDTH < uiCamera.position.x - uiCamera.viewportWidth/2){
            sky2.reposition(sky2.getPosition().x + 2 * constants.WORLD_WIDTH);
        }
    }

    public static void updateRain(float delta, OrthographicCamera gameCamera){
        if (isRaining && shouldDrawRain){
            if (rainAlpha < 1f){
                rainAlpha += rainAlpha * delta/4;
            }else rainAlpha = 1f;
        }else {
            float rainAlphaDifference = (0.1f - 1f)/4f;
            if (rainAlpha > 0.1f){
                rainAlpha += rainAlphaDifference * delta;
            }else rainAlpha = 0.1f;
        }
        for (rainCluster cluster : clusters){
            cluster.update(delta);
            if (cluster.getPosition().y + cluster.getHeight() < 0){
                cluster.reposition(cluster.getPosition().x, cluster.getPosition().y + 2 * constants.RAIN_CLUSTER_HEIGHT);
            }
            if (cluster.getPosition().x + cluster.getWidth() < gameCamera.position.x - gameCamera.viewportWidth/2){
                cluster.reposition(cluster.getPosition().x + 2 * constants.RAIN_CLUSTER_WIDTH, cluster.getPosition().y);
            }
        }
    }

    public static void updateMist(float delta, OrthographicCamera gameCamera){
        mist1.update(delta);
        mist2.update(delta);
        if (isRaining){
            if (mistAlpha < 1f){
                mistAlpha += mistAlpha * delta/4;
            }else mistAlpha = 1f;
        }else {
            float alphaDifference = (0.1f - 1f)/12f;
            if (mistAlpha > 0.1f){
                mistAlpha += alphaDifference * delta;
            }else mistAlpha = 0.1f;
        }
        if (mist1.getPosition().x + constants.MIST_WIDTH < gameCamera.position.x - gameCamera.viewportWidth/2){
            mist1.reposition(mist1.getPosition().x + 2 * constants.MIST_WIDTH);
        }
        if (mist2.getPosition().x + constants.MIST_WIDTH < gameCamera.position.x - gameCamera.viewportWidth/2){
            mist2.reposition(mist2.getPosition().x + 2 * constants.MIST_WIDTH);
        }
    }

    public static void drawRain(SpriteBatch batch){
        if (shouldDraw && shouldDrawRain) {
            batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, rainAlpha);
            for (rainCluster cluster : clusters) {
                cluster.draw(batch);
            }
            batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1f);
        }
    }

    public static void drawMist(SpriteBatch batch){
        if (shouldDraw) {
            batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, mistAlpha);
            mist1.draw(batch);
            mist2.draw(batch);
            batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1f);
        }
    }

    public static void drawSky(SpriteBatch batch){
        if (shouldDraw) {
            batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, skyAlpha);
            sky1.draw(batch);
            sky2.draw(batch);
            batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1f);

            drawLightning(batch);
        }
    }

    public static void drawLightning(SpriteBatch batch){
        lightStrike.draw(batch);
    }

    public static void dispose(){
        for (rainCluster cluster : clusters){
            cluster.dispose();
        }
        clusters.clear();
        mist1.dispose();
        mist2.dispose();
        sky1.dispose();
        sky2.dispose();
        lightStrike.dispose();
        skyAlpha = 0.1f;
        mistAlpha = 0.1f;
        rainAlpha = 0.1f;
        isRaining = false;
        shouldDraw = false;
        shouldDrawRain = false;
        prepareToStop = false;
    }

    static class rainCluster{
        private TextureRegion cluster;
        private Vector2 position = new Vector2(0, 0);
        private float width = constants.RAIN_CLUSTER_WIDTH, height = constants.RAIN_CLUSTER_HEIGHT;

        public rainCluster(float x, float y){
            cluster = new TextureRegion(new Texture("rain_particle_cluster.png"));
            this.position.x = x;
            this.position.y = y;
        }

        public void setWidth(float width) {
            this.width = width;
        }

        public void setHeight(float height) {
            this.height = height;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }

        public Vector2 getPosition() {
            return position;
        }

        public void update(float delta){
            this.position.y -= 480 * delta;
        }

        public void reposition(float x, float y){
            position.x = x;
            position.y = y;
        }

        public void draw(SpriteBatch batch){
                batch.draw(cluster, position.x, position.y, width, height);
        }

        public void dispose(){
            cluster.getTexture().dispose();
        }
    }

    static class mist{
        private TextureRegion region;
        private Vector2 position = new Vector2(0, 0);

        public mist(float x, float y){
            region = new TextureRegion(new Texture("rain_mist.png"));
            this.position.x = x;
            this.position.y = y;
        }

        public void flipTexture(boolean x, boolean y){
            this.region.flip(x, y);
        }

        public Vector2 getPosition() {
            return position;
        }

        public TextureRegion getRegion() {
            return region;
        }

        public void reposition(float x){
            position.x = x;
        }

        public void update(float delta){
            this.position.x += -20 * delta;
        }

        public void draw(SpriteBatch batch){
                batch.draw(region, position.x, position.y, constants.MIST_WIDTH, constants.MIST_HEIGHT);
        }

        public void dispose(){
            region.getTexture().dispose();
        }
    }

    static class sky {
        private TextureRegion sky;
        private Vector2 position;
        float width = constants.WORLD_WIDTH, height = 300;

        public sky(float x, float y){
            this.sky = new TextureRegion(new Texture("cloud/sky.jpg"));
            position = new Vector2(x, y);
        }

        public Vector2 getPosition() {
            return position;
        }

        public void reposition(float x){
            this.position.x = x;
        }

        public void flipTexture(boolean x, boolean y){
            sky.flip(x, y);
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }

        public void update(float delta){
            position.x += -20 * delta;
        }

        public void draw(SpriteBatch batch){
             batch.draw(sky, position.x, position.y, width, height);
        }

        public void dispose(){
            sky.getTexture().dispose();
        }
    }

    static class lightning{
        private TextureRegion lightning;
        private Vector2 lightningPosition = new Vector2(0, 0);
        private float lightningWidth = 0, lightningHeight = 0f;
        private float counter = 0f;
        private float flashCounter = 0f;
        private boolean shouldFlash = false, isLightingRandomized = false;

        public lightning(){
            lightning = new TextureRegion(new Texture("cloud/lightning.png"));
            lightningWidth = 80f;
            lightningHeight = 220f;
            lightningPosition.x = constants.WORLD_WIDTH/2f - 40f;
            lightningPosition.y = constants.WORLD_HEIGHT - 220f;
        }

        public void update(float delta){
            if (isRaining) {
                counter += delta;
                if (counter > 3f) {
                    shouldFlash = MathUtils.randomBoolean();
                    isLightingRandomized = false;
                    counter = 0f;
                }
                if (shouldFlash) {
                    if (!isLightingRandomized) {
                        lightningWidth = MathUtils.random(40f, 80f);
                        lightningHeight = MathUtils.random(120f, 240f);
                        lightningPosition.x = MathUtils.random(0f, constants.WORLD_WIDTH - lightningWidth);
                        lightningPosition.y = constants.WORLD_HEIGHT - lightningHeight;
                        lightning.flip(MathUtils.randomBoolean(), false);
                        isLightingRandomized = true;
                    }

                    flashCounter += delta;
                    if (flashCounter > 0.2f) {
                        flashCounter = 0f;
                        shouldFlash = false;
                    }
                }
            }else {
                counter = 0f;
                shouldFlash = false;
                flashCounter = 0f;
            }
        }

        public void draw(SpriteBatch batch){
            if (shouldFlash) {
                batch.draw(lightning, lightningPosition.x, lightningPosition.y, lightningWidth, lightningHeight);
            }
        }

        public void dispose(){
            lightning.getTexture().dispose();
        }
    }
}
