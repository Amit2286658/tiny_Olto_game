package com.me.tiny_olta.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.sprites.objects.collidableObjects;

public class coin extends collidableObjects {

    private Texture texture;
    private Array<TextureRegion> regions = new Array<>();
    private int score = 0;
    private boolean hasCoinBeenCollected = false, isCoinActive = false;

    public coin(float x){
        texture = new Texture("coins.png");
        for (int i = 0; i < 8; i ++){
            regions.add(new TextureRegion(texture, i * 16, 0, 16, 16));
        }

        setAnimation(0.13f, regions);

        setPosition(x, constants.GROUND_OFFSET + 5);
        setWidth(8);
        setHeight(8);

        setCollisionCoordinates(getPosition().x, getPosition().y);
        setCollisionWidth(getWidth());
        setCollisionHeight(getHeight());
    }

    public void reposition(float x, float y) {
        setPosition(x, y);
        setCollisionCoordinates(getPosition().x, getPosition().y);
        hasCoinBeenCollected = false;
        score = 0;
    }

    public void setCoinActive(boolean active){
        isCoinActive = active;
    }

    public boolean setCoinCollected() {
        return hasCoinBeenCollected;
    }

    public void update(float delta){
        updateAnimationStateTimer(delta);
    }

    public int getScore() {
        return score;
    }

    /*these function will never be called, as there's no need to check for collision between the coin, and the bomb*/
    @Override
    public boolean collidedWithBomb(throwableBomb bomb) {
        return false;
    }

    /*here are two ways to handle coin collection, simply return false and do everything in this class, or pass true, the
    * coinManager will receive the returned value and then deal with specific properties of the coin, tbh I'd
    * prefer to handle everything right in this class, easy to maintain, you know :)*/
    @Override
    public boolean collidedWithPlayer(olta player) {
        if (isCoinActive) {
            hasCoinBeenCollected = true;
            score = 1;
        }
        return false;
    }

    @Override
    public void drawAnimation(SpriteBatch batch, boolean shouldLoop) {
        if (isCoinActive) {
            if (!hasCoinBeenCollected)
                super.drawAnimation(batch, shouldLoop);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        texture.dispose();
        for (TextureRegion region : regions){
            region.getTexture().dispose();
        }
    }
}
