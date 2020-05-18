package com.me.tiny_olta.sprites.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.me.tiny_olta.constants;

public abstract class objects implements Cloneable{

    public enum objectType{
        STATIC, DYNAMIC
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private TextureRegion region;

    public objectType type = objectType.STATIC;

    public Vector2 position = new Vector2(0, constants.GROUND_OFFSET), velocity = new Vector2(0, 0);

    private Animation<TextureRegion> animation;
    public float width = 0, height = 0;
    private float timer = 0f;
    private boolean isRotated = false;
    private float rotation = 0f;
    private boolean isActive = false;

    public objects(Texture texture, float x, int width, int height){
        this.region = new TextureRegion(texture);
        this.width = width;
        this.height = height;
    }

    public objects(float duration, int width, int height, TextureRegion... regions){
        this.width = width;
        this.height = height;
        animation = new Animation<TextureRegion>(duration, regions);
    }

    /*ensure for NullPointerExceptions, since this constructor does not initialize anything*/
    public objects(){
        /*initialize everything via individual methods, position.y wil be #constants.GROUND_OFFSET in this case, change it via the methods*/
    }

    public void setTexture(Texture texture){
        this.region = new TextureRegion(texture);
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public void setTexture(TextureRegion region){
        this.region = region;
    }

    public void setAnimation(float duration, TextureRegion... regions){
        animation = new Animation<TextureRegion>(duration, regions);
    }

    public void setAnimation(float duration, Array<TextureRegion> regions){
        animation = new Animation<TextureRegion>(duration, regions);
    }

    public void addPosition(float x, float y){
        position.add(x, y);
    }

    public void setAnimation(Animation<TextureRegion> animation){
        this.animation = animation;
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

    public void setVelocity(float x, float y){
        this.velocity.x = x;
        this.velocity.y = y;
    }

    public TextureRegion getTexture() {
        return region;
    }

    public void updateAnimationStateTimer(float delta){
        timer += delta;
    }

    public void resetAnimationStateTimer(){
        this.timer = 0f;
    }

    public Animation<TextureRegion> getAnimation() {
        return animation;
    }

    public TextureRegion getAnimationTexture(boolean should_loop){
        return animation.getKeyFrame(timer, should_loop);
    }

    public TextureRegion getAnimationTexture(float stateTimer, boolean should_loop){
        return animation.getKeyFrame(stateTimer, should_loop);
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isActive() {
        return isActive;
    }

    public void drawTexture(SpriteBatch batch){
        if (!isRotated)
            batch.draw(region, position.x, position.y, width, height);
        else
            batch.draw(region, position.x, position.y, width/2f, height/2f, width, height,
                    1f, 1f, getRotationAngle());
    }
    public void drawAnimation(SpriteBatch batch, boolean shouldLoop){
        if (!isRotated)
            batch.draw(animation.getKeyFrame(timer, shouldLoop), position.x, position.y, width, height);
        else {
            batch.draw(animation.getKeyFrame(timer, shouldLoop), position.x, position.y, width/2f, height/2f,
                    width, height, 1f, 1f, getRotationAngle());
        }
    }

    public void setRotationAngle(float rotation) {
        this.rotation = rotation;
    }

    public float getRotationAngle() {
        return rotation;
    }

    public void turnRotationOn(){
        isRotated = true;
    }

    public boolean getRotation(){
        return isRotated;
    }

    public void turnRotationOff(){
        isRotated = false;
    }

    public void setType(objectType type){
        this.type = type;
    }

    public objectType getType() {
        return type;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity(){
        return velocity;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setPosition(float x){
        position.x = x;
    }

    public void setPosition(float x, float y){
        position.x = x;
        position.y = y;
    }

    public void dispose(){
        if (region != null)
            region.getTexture().dispose();
        if (animation != null) {
            for (TextureRegion tex : animation.getKeyFrames()) {
                tex.getTexture().dispose();
            }
        }
    }

    public void dormant(){
        position.x = 0f;
        position.y = constants.GROUND_OFFSET;
        velocity.x = 0f;
        velocity.y = 0f;
        type = objectType.STATIC;
        width = 0;
        height = 0;
        timer = 0f;
    }
}
