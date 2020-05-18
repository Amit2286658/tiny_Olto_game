package com.me.tiny_olta.sprites;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.me.tiny_olta.constants;

public class explosion {

    private Animation<TextureRegion> animation;
    private Texture texture;
    private Vector2 position;
    private float stateTimer = 0f;
    private Array<TextureRegion> regions;
    private boolean explodeNow = false;
    private Circle explosiveCircle;
    public int width = 24, height = 24;

    public explosion(){
        position = new Vector2(0, 0);
        texture = new Texture("exp.png");

        regions = new Array<>();
        for (int i = 0; i < 9; i++){
            regions.add(new TextureRegion(texture, i * 111, 0, 111, 109));
        }

        explosiveCircle = new Circle();
        explosiveCircle.setRadius(constants.EXPLOSION_RADIUS);
        explosiveCircle.setPosition(position.x, position.y);
        animation = new Animation<TextureRegion>(0.05f, regions);
    }

    public void update(float delta){
        if (explodeNow){
            stateTimer += delta;
            if (stateTimer >= 0.05f * 9){
                explodeNow = false;
                stateTimer = 0f;
            }
        }
    }

    public void reset(){
        this.stateTimer = 0f;
        this.position.x = 0;
        explodeNow = false;
    }

    public void show(float x, float y){
        this.position.x = x;
        this.position.y = y;
        explosiveCircle.setPosition(position.x, position.y);
        this.explodeNow = true;
    }

    public Circle getExplosiveCircle() {
        return explosiveCircle;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void hide(){
        this.explodeNow = false;
    }

    public TextureRegion getTexture(){
        return animation.getKeyFrame(stateTimer, false);
    }

    public void drawShape(SpriteBatch batch, ShapeRenderer renderer, OrthographicCamera gameCamera){
        batch.end();
        renderer.setProjectionMatrix(gameCamera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.BLACK);
        renderer.circle(explosiveCircle.x, explosiveCircle.y, explosiveCircle.radius);
        renderer.end();
        batch.begin();
    }

    public void draw(SpriteBatch batch){
        if (explodeNow)
            batch.draw(getTexture(), position.x, position.y, width, height);
    }

    public void dispose(){
        this.texture.dispose();
        texture.dispose();
        for (TextureRegion region : animation.getKeyFrames()){
            region.getTexture().dispose();
        }
        for (TextureRegion region : regions){
            region.getTexture().dispose();
        }
    }
}
