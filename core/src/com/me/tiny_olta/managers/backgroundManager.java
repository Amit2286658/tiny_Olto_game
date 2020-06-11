package com.me.tiny_olta.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.sprites.hero;

public class backgroundManager {

    private static Array<Array<backgroundElement>> elements = new Array<>();
    private static backgroundElement bg0, bg1;

    private static float alphaLayer1 = 1f, alphaLayer2 = 1f, alphaLayer3 = 1f;

    public enum ELEMENT_LAYER{
        LAYER0, LAYER1, LAYER2, LAYER3, LAYER4, LAYER5, LAYER6
    }

    public static ELEMENT_LAYER getLayer(int i){
        switch (i){
            case 0:
                return ELEMENT_LAYER.LAYER0;
            case 1:
                return ELEMENT_LAYER.LAYER1;
            case 2:
                return ELEMENT_LAYER.LAYER2;
            case 3:
                return ELEMENT_LAYER.LAYER3;
            case 4:
                return ELEMENT_LAYER.LAYER4;
            case 5:
                return ELEMENT_LAYER.LAYER5;
            case 6:
                return ELEMENT_LAYER.LAYER6;
            default: throw new NullPointerException("identifier is beyond the layer limit, take care of it");
        }
    }

    public static void initiate(){
        //layers = new Array<>();
        bg0 = new backgroundElement(new Texture("background/layer0.png"), 0, ELEMENT_LAYER.LAYER0);
        bg1 = new backgroundElement(new Texture("background/layer1.png"), 0, ELEMENT_LAYER.LAYER1);
        //bg1.setPosition(new Vector2(0, -100));
        for (int i = 2; i < 7; i++){
            Array<backgroundElement> list = new Array<>();
            for (int j = 0; j < constants.BACKGROUND_ELEMENT_COUNT; j++){
                backgroundElement element = new backgroundElement(new Texture("background/layer"+i+".png"),
                        j * constants.GAME_WIDTH,
                        getLayer(i));
                switch (i){
                    case 2:
                        element.setVelocity(constants.PLAYER_VELOCITY - constants.PLAYER_VELOCITY * 25/100f);
                        break;
                    case 3:
                        element.setVelocity(constants.PLAYER_VELOCITY - constants.PLAYER_VELOCITY * 50/100f);
                        break;
                    case 4:
                        element.setVelocity(constants.PLAYER_VELOCITY - constants.PLAYER_VELOCITY * 75/100f);
                        break;
                    case 5:
                        element.setVelocity(0);
                        break;
                    case 6:
                        element.setVelocity(constants.PLAYER_VELOCITY - 2 * constants.PLAYER_VELOCITY * 75/100f);//- 140f);
                        break;
                }
               list.add(element);
            }
            elements.add(list);
        }
    }

    public static void reconfigureBackgroundElementsVelocity(){
        for (Array<backgroundElement> list : elements){
            for (backgroundElement element : list){
                switch (element.getLayer()){
                    case LAYER2:
                        element.setVelocity(constants.PLAYER_VELOCITY - constants.PLAYER_VELOCITY * 25/100f);
                        break;
                    case LAYER3:
                        element.setVelocity(constants.PLAYER_VELOCITY - constants.PLAYER_VELOCITY * 50/100f);
                        break;
                    case LAYER4:
                        element.setVelocity(constants.PLAYER_VELOCITY - constants.PLAYER_VELOCITY * 75/100f);
                        break;
                    case LAYER5:
                        element.setVelocity(0);
                        break;
                    case LAYER6:
                        element.setVelocity(constants.PLAYER_VELOCITY - 2 * constants.PLAYER_VELOCITY * 75/100f);//- 140f);
                        break;
                }
            }
        }
    }

    public static void update(float delta, OrthographicCamera gameCamera, hero player){
        for (Array<backgroundElement> list : elements){
            for (backgroundElement item : list){
                if (!player.isDead() && !player.isThrowing())
                    item.update(delta);
                if (item.getPosition().x + item.getWidth() <= gameCamera.position.x - gameCamera.viewportWidth/2){
                    item.reposition(item.getPosition().x + constants.BACKGROUND_ELEMENT_COUNT * constants.GAME_WIDTH);
                }
            }
        }
        if (worldManager.isDay()){
            if (alphaLayer1 < 1f){
                alphaLayer1 += alphaLayer1 * delta;
            }else
                alphaLayer1 = 1f;

            if (alphaLayer2 < 1f)
                alphaLayer2 += alphaLayer2 * delta;
            else
                alphaLayer2 = 1f;

            if (alphaLayer3 < 1f)
                alphaLayer3 += alphaLayer3 * delta;
            else
                alphaLayer3 = 1f;
        }else {
            float alphaDifference1 = (0.2f - 1f)/9f, alphaDifference2 = (0.5f - 1f)/9f, alphaDifference3 = (0.9f - 1f)/9f;
            if (alphaLayer1 >= 0.2f)
                alphaLayer1 += alphaDifference1 * delta;
            else alphaLayer1 = 0.2f;

            if (alphaLayer2 >= 0.5f)
                alphaLayer2 += alphaDifference2 * delta;
            else alphaLayer2 = 0.5f;

            if (alphaLayer3 >= 0.9f)
                alphaLayer3 += alphaDifference3 * delta;
            else alphaLayer3 = 0.9f;
        }
    }

    public static void setDimension(float width, float height, ELEMENT_LAYER layer){
        for (Array<backgroundElement> layers : elements) {
            for (backgroundElement element : layers) {
                if (element.getLayer() == layer) {
                    element.setWidth(width);
                    element.setHeight(height);
                }
            }
        }
    }

    public static void draw(SpriteBatch batch){
        for (Array<backgroundElement> layers : elements) {
            for (backgroundElement element : layers) {
                element.draw(batch);
            }
        }
    }

    public static void draw(SpriteBatch batch, ELEMENT_LAYER layer){
        if (layer == ELEMENT_LAYER.LAYER0){
            batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, alphaLayer1);
            bg0.draw(batch);
            batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1f);
        }
        if (layer == ELEMENT_LAYER.LAYER1){
            batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, alphaLayer1);
            bg1.draw(batch);
            batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1f);
        }
        for (Array<backgroundElement> layers : elements) {
            for (backgroundElement element : layers) {
                if (element.getLayer() == layer) {
                    element.draw(batch);
                }
            }
        }
    }

    public static void dispose(){
        for (Array<backgroundElement> layers : elements){
            for (backgroundElement element : layers){
                element.dispose();
            }
        }
        elements.clear();
        bg0.dispose();
        bg1.dispose();
        alphaLayer1 = 1f;
        alphaLayer2 = 1f;
        alphaLayer3 = 1f;
    }

    static class backgroundElement{
        private ELEMENT_LAYER layer;
        private Texture region;
        private float width = constants.WORLD_WIDTH, height = constants.WORLD_HEIGHT;
        private Vector2 position = new Vector2(0, 0);
        private float velocity = 0f;

        public backgroundElement(Texture region, float x, ELEMENT_LAYER layer){
            this.region = region;
            this.layer = layer;
            position.x = x;
        }

        public Texture getRegion() {
            return region;
        }

        public ELEMENT_LAYER getLayer() {
            return layer;
        }

        public void reposition(float x){
            position.x = x;
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

        public void setPosition(Vector2 position) {
            this.position = position;
        }

        public void setVelocity(float velocity) {
            this.velocity = velocity;
        }

        public float getVelocity() {
            return velocity;
        }

        public void update(float delta){
            position.x += velocity * delta;
        }

        public void draw(SpriteBatch batch){
            batch.draw(region, position.x, position.y, width, height);
        }

        public void dispose(){
            this.region.dispose();
        }
    }
}
