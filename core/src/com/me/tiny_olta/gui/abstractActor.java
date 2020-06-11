package com.me.tiny_olta.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/*All basic actors must extend this abstract actor class to prevent repetitive code, the child actors could be static class
* extending this abstract class inside parent classes.
* for multiple texts, use line breaks, and for multiple images use different actors extending this base class.
*/

public abstract class abstractActor extends Actor implements Disposable {

    private TextureRegion region;
    private Animation<TextureRegion> animation;
    private Vector2 position = new Vector2(0, 0);
    private float width, height;
    private BitmapFont font;
    private String text;
    private Vector2 fontPosition;
    public float rotation = 0f, angle = 0f, stateTimer = 0f;
    public boolean isRotationEnabled = false;
    private TYPE type = TYPE.IMAGE;
    private IMAGE_TYPE image_type = IMAGE_TYPE.STATIC;
    private boolean animationLooping = true;
    private float alphaStart = 0f, alphaEnd = 0f, alphaDuration = 0f, alphaBatchValue = 1f;
    private boolean isAlphaEnabled = false;
    private ALPHA_TYPE alphaType = ALPHA_TYPE.ALPHA_IN;

    public enum TYPE{
        IMAGE, TEXT, BOTH
    }

    public enum IMAGE_TYPE{
        STATIC, ANIMATION
    }

    public enum ALPHA_TYPE{
        ALPHA_IN, ALPHA_OUT
    }

    public abstractActor(TextureRegion region, Vector2 position, float width, float height){
        this.region = region;
        this.position = position;
        this.width = width;
        this.height = height;
    }

    public abstractActor(Animation<TextureRegion> regionAnimation, Vector2 position, float width, float height){
        this.animation = regionAnimation;
        this.position = position;
        this.width = width;
        this.height = height;
    }

    public abstractActor(Vector2 position, float width, float height, TextureRegion... regions){
        this.position = position;
        this.width = width;
        this.height = height;
        this.animation = new Animation<TextureRegion>(0.13f, regions);
    }

    public abstractActor(Array<TextureRegion> regions, Vector2 position, float width, float height){
        this.position = position;
        this.width = width;
        this.height = height;
        this.animation = new Animation<TextureRegion>(0.13f, regions);
    }

    public abstractActor(BitmapFont font, Vector2 fontPosition, String text){
        this.font = font;
        this.fontPosition = fontPosition;
        this.text = text;
    }

    public abstractActor(TextureRegion region, Vector2 position, float width, float height,
                         BitmapFont font, Vector2 fontPosition, String text){
        this.region = region;
        this.position = position;
        this.width = width;
        this.height = height;
        this.font = font;
        this.fontPosition = fontPosition;
        this.text = text;
    }

    public abstractActor(){
        //handle everything individually.
    }

    public void enableRotation(float angle){
        this.isRotationEnabled = true;
        this.angle = angle;
    }

    public void setAlpha(float alphaStart, float alphaEnd, float alphaDuration, ALPHA_TYPE type){
        this.alphaStart = alphaStart;
        setAlphaBatchDefaultValue(alphaStart);
        this.alphaEnd = alphaEnd;
        this.alphaDuration = alphaDuration;
        this.alphaType = type;
        /*if (alphaStart < alphaEnd){
            this.alphaType = ALPHA_TYPE.ALPHA_IN;
        }else this.alphaType = ALPHA_TYPE.ALPHA_OUT;*/
    }

    public void setAlphaBatchDefaultValue(float value){
        if (value < 0f && value > 1f) throw new IllegalStateException("default batch value must not be less than 0f," +
                "and greater than 1f");
        alphaBatchValue = value;
    }

    public void runAlphaNow(){
        isAlphaEnabled = true;
    }

    public void disableAlpha(){
        isAlphaEnabled = false;
    }

    public void resetAlphaValue(){
        alphaStart = 0f;
        alphaEnd = 0f;
        alphaDuration = 0f;
        alphaType = ALPHA_TYPE.ALPHA_IN;
    }

    public void setAlphaType(ALPHA_TYPE alphaType) {
        this.alphaType = alphaType;
    }

    public void setAnimation(Animation<TextureRegion> animation) {
        this.animation = animation;
    }

    public void setAnimation(Array<TextureRegion> regions){
        this.animation = new Animation<TextureRegion>(0.13f, regions);
    }

    public void setAnimation(TextureRegion... regions){
        this.animation = new Animation<TextureRegion>(0.13f, regions);
    }

    public void setAnimationLooping(boolean animationLooping) {
        this.animationLooping = animationLooping;
    }

    public boolean isAnimationLooping() {
        return animationLooping;
    }

    public void resetAnimationTimer(){
        this.stateTimer = 0f;
    }

    public Animation<TextureRegion> getAnimation() {
        return animation;
    }

    public void disableRotation(){
        this.isRotationEnabled = false;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    private void updateRotation(float delta){
        rotation -= angle * delta;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setFont(BitmapFont font) {
        this.font = font;
    }

    public void setFontPosition(Vector2 fontPosition) {
        this.fontPosition = fontPosition;
    }

    public void setFontPosition(float x, float y){
        this.fontPosition.x = x;
        this.fontPosition.y = y;
    }

    public void setFontPositionX(float x){
        this.fontPosition.x = x;
    }

    public void setFontPositionY(float y){
        this.fontPosition.y = y;
    }

    public BitmapFont getFont() {
        if (font == null)
            throw new NullPointerException("the font is null, maybe this actor does not draw any font");
        return font;
    }

    public String getText() {
        return text;
    }

    public Vector2 getFontPosition() {
        return fontPosition;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public TYPE getType() {
        return type;
    }

    public void setImage_type(IMAGE_TYPE image_type) {
        this.image_type = image_type;
    }

    public IMAGE_TYPE getImage_type() {
        return image_type;
    }

    public void setTexture(TextureRegion region){
        this.region = region;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setPosition(float x, float y){
        this.position.x = x;
        this.position.y = y;
    }

    public void setPositionX(float x){
        this.position.x = x;
    }

    public void setPositionY(float y){
        this.position.y = y;
    }

    @Override
    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public void setHeight(float height) {
        this.height = height;
    }

    public TextureRegion getRegion() {
        if (type == TYPE.IMAGE || type == TYPE.BOTH) {
            if (image_type == IMAGE_TYPE.STATIC) {
                return region;
            } else {
                return animation.getKeyFrame(stateTimer, animationLooping);
            }
        }else throw new NullPointerException("this actor is not configured to draw textures or animation.");
    }



    public Vector2 getPosition() {
        return position;
    }

    public void resetRotationAngle(){
        this.rotation = 0f;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (isRotationEnabled){
            updateRotation(delta);
        }
        if (type == TYPE.IMAGE || type == TYPE.BOTH){
            if (image_type == IMAGE_TYPE.ANIMATION){
                stateTimer += delta;
            }
        }
        if (isAlphaEnabled){
            float alphaDifference = (alphaEnd - alphaStart)/alphaDuration;
            if (alphaType == ALPHA_TYPE.ALPHA_IN) {
                if (alphaBatchValue < alphaEnd) {
                    alphaBatchValue += alphaDifference * delta;
                } else {
                    setAlphaBatchDefaultValue(alphaEnd);
                    isAlphaEnabled = false;
                }
            }
            if (alphaType == ALPHA_TYPE.ALPHA_OUT) {
                if (alphaBatchValue > alphaEnd) {
                    alphaBatchValue += alphaDifference * delta;
                } else {
                    setAlphaBatchDefaultValue(alphaEnd);
                    isAlphaEnabled = false;
                }
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        switch (type){
            case IMAGE:
                batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, alphaBatchValue);
                batch.draw(getRegion(), position.x, position.y, width / 2, height / 2,
                        width, height, 1f, 1f, rotation);
                batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1f);
                break;
            case TEXT:
                font.setColor(font.getColor().r, font.getColor().g, font.getColor().b, alphaBatchValue);
                font.draw(batch, text, fontPosition.x, fontPosition.y);
                font.setColor(font.getColor().r, font.getColor().g, font.getColor().b, 1f);
                break;
            case BOTH:
                batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, alphaBatchValue);
                batch.draw(getRegion(), position.x, position.y, width / 2, height / 2,
                        width, height, 1f, 1f, rotation);
                batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1f);
                font.setColor(font.getColor().r, font.getColor().g, font.getColor().b, alphaBatchValue);
                font.draw(batch, text, fontPosition.x, fontPosition.y);
                font.setColor(font.getColor().r, font.getColor().g, font.getColor().b, 1f);
                break;
            default: throw new IllegalStateException("this case is completely unexpected, please find the bug and solve it," +
                    "it shouldn't happen this way");
        }
    }

    @Override
    public void dispose() {
        if (region != null)
            region.getTexture().dispose();
        if (animation != null){
            for (TextureRegion region : animation.getKeyFrames()){
                region.getTexture().dispose();
            }
        }
    }
}
