package com.me.tiny_olta.sprites;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.me.tiny_olta.constants;

public class olta {

    private Vector2 position, velocity;//, rockVelocity;
    private float stateTimer = 0f;

    private boolean inAir = false;
    private boolean isThrowing = false;
    private boolean isDead = false;
    private float throwTimer = 0, jumpTimer = 0, deathTimer = 0f;
    private boolean fallBack = false;
    private throwableBomb bomb = new throwableBomb();

    private Rectangle shape;

    private Texture texture = new Texture("hero_1.png");
    private Texture textureDust = new Texture("run_dust.png");
    private Texture jump = new Texture("jump.png");
    private Texture throwRock = new Texture("throw.png");
    private Texture death = new Texture("death.png");
    private Animation<TextureRegion> animation, dustAnimation, jumpAnimation, throwAnimation, deathAnimation;

    public olta(float x){
        position = new Vector2(x, constants.GROUND_OFFSET);
        velocity = new Vector2(0, 0);
        shape = new Rectangle(x + 6, constants.GROUND_OFFSET, constants.PLAYER_WIDTH - 12,
                constants.PLAYER_HEIGHT - 4);
        bomb.setRotation(360f);
        animation = new Animation<TextureRegion>(0.13f,
                new TextureRegion(texture, 0, 0, 32, 32),
                new TextureRegion(texture, 32, 0, 32, 32),
                new TextureRegion(texture, 2 * 32, 0, 32, 32),
                new TextureRegion(texture, 3 * 32, 0, 32, 32),
                new TextureRegion(texture, 4 * 32, 0, 32, 32),
                new TextureRegion(texture, 5 * 32, 0, 32, 32));
        dustAnimation = new Animation<TextureRegion>(0.13f,
                new TextureRegion(textureDust, 0, 0, 32, 32),
                new TextureRegion(textureDust, 32, 0, 32, 32),
                new TextureRegion(textureDust, 2 * 32, 0, 32, 32),
                new TextureRegion(textureDust, 3 * 32, 0, 32, 32),
                new TextureRegion(textureDust, 4 * 32, 0, 32, 32),
                new TextureRegion(textureDust, 5 * 32, 0, 32, 32));
        jumpAnimation = new Animation<TextureRegion>(0.13f,
                new TextureRegion(jump, 0, 0, 32, 32),
                new TextureRegion(jump, 32, 0, 32, 32),
                new TextureRegion(jump, 2 * 32, 0, 32, 32),
                new TextureRegion(jump, 3 * 32, 0, 32, 32),
                new TextureRegion(jump, 4 * 32, 0, 32, 32),
                new TextureRegion(jump, 5 * 32, 0, 32, 32),
                new TextureRegion(jump, 6 * 32, 0, 32, 32),
                new TextureRegion(jump, 7 * 32, 0, 32, 32));
        throwAnimation = new Animation<TextureRegion>(0.13f,
                new TextureRegion(throwRock, 0, 0, 32, 32),
                new TextureRegion(throwRock, 32, 0, 32, 32),
                new TextureRegion(throwRock, 2 * 32, 0, 32, 32),
                new TextureRegion(throwRock, 3 * 32, 0, 32, 32));
        deathAnimation = new Animation<TextureRegion>(0.13f,
                new TextureRegion(death, 0, 0, 32, 32),
                new TextureRegion(death, 32, 0, 32, 32),
                new TextureRegion(death, 2 * 32, 0, 32, 32),
                new TextureRegion(death, 3 * 32, 0, 32, 32),
                new TextureRegion(death, 4 * 32, 0, 32, 32),
                new TextureRegion(death, 5 * 32, 0, 32, 32),
                new TextureRegion(death, 6 * 32, 0, 32, 32),
                new TextureRegion(death, 7 * 32, 0, 32, 32));
    }

    public void update(float delta, OrthographicCamera gameCamera){
        this.stateTimer += delta;
        bomb.update(delta, gameCamera);
        if (!isDead) {
            velocity.add(0, constants.GRAVITY);
            velocity.scl(delta);
            if (inAir) {
                jumpTimer += delta;
            }
            if (!isThrowing) {
                position.add(constants.PLAYER_VELOCITY * delta, velocity.y);
            } else {
                //todo : check if its looking fine
                //position.add(constants.PLAYER_VELOCITY * delta, velocity.y);
                throwTimer += delta;
                if (throwTimer > 0.13 * 4) {
                    isThrowing = false;
                    throwTimer = 0f;
                }
            }
            velocity.scl(1 / delta);
            if (position.y < constants.GROUND_OFFSET) {
                position.y = constants.GROUND_OFFSET;
                inAir = false;
                jumpTimer = 0;
            }

            if (!isThrowing) {
                shape.setPosition(position.x + 6, position.y);
            }
        }else {
            deathTimer += delta;
            if (fallBack) {
                position.x += -constants.PLAYER_VELOCITY/4 * delta;
                shape.x = position.x + 6;
            }
            if (deathTimer >= 0.13 * 8){
                fallBack = false;
            }
        }
    }

    public void drawShape(SpriteBatch batch, ShapeRenderer renderer, OrthographicCamera gameCamera){
        batch.end();
        renderer.setProjectionMatrix(gameCamera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.BLACK);
        renderer.rect(shape.x, shape.y, shape.width, shape.height);
        renderer.end();
        batch.begin();
    }


    public Rectangle getShape() {
        return shape;
    }

    public void jump(){
        velocity.y = constants.JUMP_IMPULSE;
        inAir = true;
    }

    public void throwRock(){
        isThrowing = true;
        bomb.setHorizontalVelocity(constants.ROCK_VELOCITY);
        bomb.throwRock(this.position.x + 20, constants.GROUND_OFFSET + 10);
        bomb.setTimer(0.13f * 3);
    }

    public throwableBomb getBomb() {
        return bomb;
    }

    public void die(){
        isDead = true;
        fallBack = true;
    }

    public void alive(){
        position.x = position.x - 100;
        shape.x = position.x;
        isDead = false;
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean isInAir() {
        return inAir;
    }

    public boolean isThrowing() {
        return isThrowing;
    }

    public Vector2 getPosition() {
        return position;
    }

    public TextureRegion getTexture() {
        if (!isDead) {
            if (!isThrowing) {
                if (!inAir)
                    return animation.getKeyFrame(stateTimer, true);
                else
                    return jumpAnimation.getKeyFrame(jumpTimer, true);
            } else {
                return throwAnimation.getKeyFrame(throwTimer, false);
            }
        }else {
            return deathAnimation.getKeyFrame(deathTimer, false);
        }
    }

    public TextureRegion getDust(){
        return dustAnimation.getKeyFrame(stateTimer, true);
    }

    public void draw(SpriteBatch batch){
        batch.draw(getTexture(), position.x, position.y, constants.PLAYER_WIDTH, constants.PLAYER_HEIGHT);
        bomb.draw(batch);
        if (!inAir && !isThrowing && !isDead)
            batch.draw(getDust(), position.x - 4, position.y, constants.PLAYER_WIDTH, constants.PLAYER_HEIGHT);
    }

    public void dispose(){
        bomb.dispose();
        texture.dispose();
        textureDust.dispose();
        throwRock.dispose();
        death.dispose();
        jump.dispose();
        for (TextureRegion reg : animation.getKeyFrames()){
            reg.getTexture().dispose();
        }
        for (TextureRegion reg : jumpAnimation.getKeyFrames()){
            reg.getTexture().dispose();
        }
        for (TextureRegion reg : throwAnimation.getKeyFrames()){
            reg.getTexture().dispose();
        }
        for (TextureRegion reg : deathAnimation.getKeyFrames()){
            reg.getTexture().dispose();
        }
        for (TextureRegion reg : dustAnimation.getKeyFrames()){
            reg.getTexture().dispose();
        }
    }
}
