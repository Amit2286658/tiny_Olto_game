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
import com.me.tiny_olta.constants;

public class throwableBomb {

    private Texture texture;
    private Vector2 position;
    private boolean throw_now = false;
    private float startTime = 0f, endTime = 0f;
    private Circle shape;
    private Animation<TextureRegion> animation;
    private float rotation = 0f, rotate = 0f;
    private boolean goHorizontal= true;
    private boolean goVertical = false;

    private boolean isDormant = false;
    private boolean hasExploded = false;

    private explosion exp;

    private float horizontalVelocity = constants.ROCK_VELOCITY;
    private float verticalVelocity = constants.GRAVITY;


    public throwableBomb(){
        position = new Vector2(0, constants.GROUND_OFFSET + 10);
        texture = new Texture("bomb.png");
        animation = new Animation<TextureRegion>(0.13f,
                new TextureRegion(texture, 0, 0, 16, 16),
                new TextureRegion(texture, 16, 0, 16, 16),
                new TextureRegion(texture, 32, 0, 16, 16));
        shape = new Circle();
        shape.setPosition(position.x + 4, position.y + 4);
        shape.setRadius(4f);

        exp = new explosion();
    }

    public void update(float delta, OrthographicCamera gameCamera){
        if (throw_now){
            startTime += delta;
            rotate = rotate - rotation * delta;
            if (startTime >= endTime) {
                if ((gameCamera.position.x + gameCamera.viewportWidth / 2) <= position.x ||
                position.y <= 0f) {
                    dormant();
                } else {
                    if (goHorizontal) {
                        position.x += horizontalVelocity * delta;
                        shape.setX(position.x + 4);
                    }
                    if (goVertical){
                        position.y += verticalVelocity * delta;
                        shape.setY(position.y + 4);
                    }
                }
            }
        }
        exp.update(delta);
    }

    public void update(float delta, float horizontalSpeed, float verticalSpeed, OrthographicCamera gameCamera){
        if (throw_now){
            startTime += delta;
            rotate = rotate - rotation * delta;
            if (startTime >= endTime) {
                if ((gameCamera.position.x + gameCamera.viewportWidth / 2) <= position.x ||
                        position.y <= 0f || position.x <= gameCamera.position.x - gameCamera.viewportWidth/2) {
                    dormant();
                } else {
                    if (goHorizontal) {
                        position.x +=horizontalSpeed * delta;
                        shape.setX(position.x + 4);
                    }
                    if (goVertical){
                        position.y -= verticalSpeed * delta;
                        shape.setY(position.y + 4);
                    }
                }
            }
        }
        exp.update(delta);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void explode(float x, float y){
        exp.show(x, y);
        hasExploded = true;
    }

    public void explode(){
        exp.show(getPosition().x - 12, getPosition().y - 8);
        hasExploded = true;
    }

    public void throwRock(float x){
        isDormant = false;
        position.x = x;
        throw_now = true;
    }

    public void throwRock(float x, float y){
        isDormant = false;
        position.x = x;
        position.y = y;
        throw_now = true;
    }

    public boolean hasExploded(){
        return hasExploded;
    }

    public void setHorizontalVelocity(float horizontalVelocity) {
        this.horizontalVelocity = horizontalVelocity;
    }

    public void setVerticalVelocity(float verticalVelocity) {
        this.verticalVelocity = verticalVelocity;
    }

    public float getHorizontalVelocity() {
        return horizontalVelocity;
    }

    public float getVerticalVelocity() {
        return verticalVelocity;
    }

    public void setHasExploded(boolean hasExploded) {
        this.hasExploded = hasExploded;
    }

    public boolean isDormant() {
        return isDormant;
    }

    public Circle getShape() {
        return shape;
    }

    public void setTimer(float timer){
        startTime = 0f;
        endTime = timer;
    }

    public void setVertical(boolean goVertical) {
        this.goVertical = goVertical;
    }

    public void setHorizontal(boolean goHorizontal) {
        this.goHorizontal = goHorizontal;
    }

    public boolean isGoingHorizontal() {
        return goHorizontal;
    }

    public boolean isGoingVertical() {
        return goVertical;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public explosion getExplosive() {
        return exp;
    }

    public void drawShape(SpriteBatch batch, ShapeRenderer renderer, OrthographicCamera gameCamera){
        batch.end();
        renderer.setProjectionMatrix(gameCamera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.BLACK);
        renderer.circle(shape.x, shape.y, shape.radius);
        renderer.end();
        batch.begin();

        exp.drawShape(batch, renderer, gameCamera);

    }

    public TextureRegion getTexture(){
        return animation.getKeyFrame(startTime, true);
    }

    public void draw(SpriteBatch batch){
        if (throw_now) {
            if (startTime >= endTime)
                batch.draw(getTexture(), position.x, position.y, 4f, 4f, 8, 8,
                        1f, 1f, rotate);
        }
        exp.draw(batch);
    }



    public void dispose(){
        texture.dispose();
        for (TextureRegion tex : animation.getKeyFrames()){
            tex.getTexture().dispose();
        }
        exp.dispose();
    }

    public void dormant(){
        position.x = 0;
        position.y = 0;
        shape.x = 0f;
        throw_now = false;
        startTime = 0f;
        endTime = 0f;
        isDormant = true;
        horizontalVelocity = constants.ROCK_VELOCITY;
        verticalVelocity = constants.GRAVITY;
    }

}
