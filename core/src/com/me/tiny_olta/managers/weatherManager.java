package com.me.tiny_olta.managers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.sprites.olta;

public class weatherManager {

    private static Array<rainCluster> clusters = new Array<>();
    private static Array<cloud> clouds = new Array<>();
    private static boolean isRaining = false, prepareToStop = false;
    private static boolean shouldDraw = false;
    private static mist mist1, mist2;
    private static cloud cloud0, cloud1;
    private static sky sky1, sky2;
    private static float skyAlpha = 0.1f, mistAlpha = 0.1f, rainAlpha = 0.1f;
    private static float r = 0f, g = 0f, b = 0f;

    public enum CLOUD_LAYER {
        LAYER0, LAYER1, LAYER2, LAYER3, LAYER4, LAYER5, LAYER6
    }

    public static void rain(){
        isRaining = true;
        shouldDraw = true;
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

    public static void updateLightProperties(float delta){
        Color dayColor = worldManager.getDayLight().getColor();
        r = dayColor.r;
        g = dayColor.g;
        b = dayColor.b;
        if (isRaining){
            if ( r > 24f){
                r -= constants.WEATHER_DAYLIGHT_COLOR_FACTOR * delta;
            }else r = 24f;

            if (g > 24f){
                g -= constants.WEATHER_DAYLIGHT_COLOR_FACTOR * delta;
            }else g = 24f;

            if (b < 24f){
                b += constants.WEATHER_DAYLIGHT_COLOR_FACTOR * delta;
            }else b = 24f;

            worldManager.getDayLight().setColor(r, g, b, dayColor.a);

            if (constants.RAY_CASTING_DISTANCE > 1000){
                constants.RAY_CASTING_DISTANCE -= 10 * delta;
            }else constants.RAY_CASTING_DISTANCE = 1000;
        }else {
            if ( r < 100f){
                r += constants.WEATHER_DAYLIGHT_COLOR_FACTOR * delta;
            }else r = 100f;

            if (g < 100f){
                g += constants.WEATHER_DAYLIGHT_COLOR_FACTOR * delta;
            }else g = 100f;

            if (b > 0f){
                b -= constants.WEATHER_DAYLIGHT_COLOR_FACTOR * delta;
            }else b = 0f;

            worldManager.getDayLight().setColor(r, g, b, dayColor.a);

            if (constants.RAY_CASTING_DISTANCE < 1800){
                float LightDistanceDifference = (1800 - 1000)/9f;
                constants.RAY_CASTING_DISTANCE += LightDistanceDifference * delta;
            }else constants.RAY_CASTING_DISTANCE = 1800;
        }
        worldManager.getDayLight().setDistance(constants.RAY_CASTING_DISTANCE);
        worldManager.getNightLight().setDistance(constants.RAY_CASTING_DISTANCE);
    }

    public static void createCloud(){
        for (int i = 2; i < 7; i++){
            for (int j = 0; j < 2; j++) {
                TextureRegion region = new TextureRegion(new Texture("cloud/layer"+i+".png"));
                if (j == 1) region.flip(true, false);
                cloud c = new cloud(region, j * 400, i == 3 ? 50 : 20, 400, 240, getCloudLayer(i));
                switch (i){
                    case 2:
                        c.setVelocity(constants.PLAYER_VELOCITY - 20);
                        break;
                    case 3:
                        c.setVelocity(constants.PLAYER_VELOCITY - 40);
                        break;
                    case 4:
                        c.setVelocity(constants.PLAYER_VELOCITY - 60);
                        break;
                    case 5:
                        c.setVelocity(0);
                        break;
                    case 6:
                        c.setVelocity(constants.PLAYER_VELOCITY -140f);
                        break;
                }
                clouds.add(c);
            }
        }
        cloud0 = new cloud(new TextureRegion(new Texture("cloud/layer0.png")),
                0, 150, 800, 480, CLOUD_LAYER.LAYER0);
        cloud1 = new cloud(new TextureRegion(new Texture("cloud/layer1.png")),
                0, 0, 800, 480, CLOUD_LAYER.LAYER1);
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
        for (rainCluster cluster : clusters){
            cluster.update(delta);
            if (isRaining){
                if (rainAlpha < 1f){
                    rainAlpha += rainAlpha * delta/8;
                }else rainAlpha = 1f;
            }else {
                float rainAlphaDifference = (0.1f - 1f)/4f;
                if (rainAlpha > 0.1f){
                    rainAlpha += rainAlphaDifference * delta;
                }else rainAlpha = 0.1f;
            }
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

    public static void updateClouds(float delta, OrthographicCamera gameCamera, olta player){
        for (cloud c : clouds){
            if (!player.isDead() && !player.isThrowing()){
                c.update(delta);
            }
            if (c.getPosition().x + c.getWidth() < gameCamera.position.x - gameCamera.viewportWidth/2){
                c.reposition(c.getPosition().x + 2 * 400, c.getPosition().y);
            }
        }
    }

    public static void drawRain(SpriteBatch batch){
        if (shouldDraw) {
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
        }
    }

    public static void drawClouds(SpriteBatch batch, CLOUD_LAYER layer){
        if (shouldDraw) {
            if (layer == CLOUD_LAYER.LAYER0) {
                cloud0.draw(batch);
            } else if (layer == CLOUD_LAYER.LAYER1) {
                cloud1.draw(batch);
            } else {
                for (cloud c : clouds) {
                    if (layer == c.getLayer())
                        c.draw(batch);
                }
            }
        }
    }

    public static void dispose(){
        for (rainCluster cluster : clusters){
            cluster.dispose();
        }
        for (cloud c : clouds){
            c.dispose();
        }
        clusters.clear();
        clouds.clear();

        mist1.dispose();
        mist2.dispose();
        sky1.dispose();
        sky2.dispose();
        if (cloud0 != null){
            cloud0.dispose();
        }
        if (cloud1 != null){
            cloud1.dispose();
        }
        skyAlpha = 0.1f;
        mistAlpha = 0.1f;
        rainAlpha = 0.1f;
        isRaining = false;
        shouldDraw = false;
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

    private static CLOUD_LAYER getCloudLayer(int i){
        switch (i){
            case 0:
                return CLOUD_LAYER.LAYER0;
            case 1:
                return CLOUD_LAYER.LAYER1;
            case 2:
                return CLOUD_LAYER.LAYER2;
            case 3:
                return CLOUD_LAYER.LAYER3;
            case 4:
                return CLOUD_LAYER.LAYER4;
            case 5:
                return CLOUD_LAYER.LAYER5;
            case 6:
                return CLOUD_LAYER.LAYER6;
            default: throw new IllegalArgumentException("given integer argument is beyond the cloud layer limit");
        }
    }

    static class cloud {
        private TextureRegion cloud;
        private Vector2 position = new Vector2(0, 0);
        private float width = 0f, height = 0f;
        private CLOUD_LAYER layer;
        private float velocity = 0f;

        public cloud (TextureRegion region, float x, float y, float width, float height, CLOUD_LAYER layer){
            //cloud = new TextureRegion(new Texture("clouds1.png"));
            //cloud.flip(false, true);
            this.cloud = region;
            this.position.x = x;
            this.position.y = y;
            this.width = width;
            this.height = height;
            this.layer = layer;
        }

        public void setVelocity(float velocity) {
            this.velocity = velocity;
        }

        public CLOUD_LAYER getLayer() {
            return layer;
        }

        public void update(float delta){
            position.x += velocity * delta;
        }

        public Vector2 getPosition() {
            return position;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }

        public void reposition(float x, float y){
            this.position.x = x;
            this.position.y = y;
        }

        public void draw(SpriteBatch batch){
             batch.draw(cloud, position.x, position.y, width, height);
        }
        public void dispose(){
            cloud.getTexture().dispose();
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
}
