package com.me.tiny_olta.sprites.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.me.tiny_olta.managers.EnemyManager;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.sprites.olta;
import com.me.tiny_olta.sprites.throwableBomb;

public class spungie extends collidableObjects {

    private Texture texture;
    private Array<TextureRegion> regions = new Array<>();
    private Array<TextureRegion> regions1 = new Array<>();

    private boolean isSpungieCrying = false;
    
    public spungie(float initialPosition){
        texture = new Texture("spungie.png");

        for (int i = 0; i < 8; i++){
            regions.add(new TextureRegion(texture, i * 553, 0, 553, 411));
        }
        for (int i = 8; i < 10; i++){
            regions1.add(new TextureRegion(texture, i * 553, 0, 553, 411));
        }

        setAnimation(0.05f, regions);

        setPosition(initialPosition);
        setWidth(constants.ENEMY_WIDTH + 6);
        setHeight(constants.ENEMY_HEIGHT);

        setCollisionX(getPosition().x);
        setCollisionWidth(getWidth());
        setCollisionHeight(getHeight());

        setEnemyType(EnemyManager.ENEMY_TYPE.SPUNGIE);
        setType(objectType.DYNAMIC);
    }

    public void update(float delta, float player_x){
        updateAnimationStateTimer(delta);
        if (player_x > getPosition().x - 140 && !isSpungieCrying){
            setAnimation(0.05f, regions1);
            isSpungieCrying = true;
        }
    }

    @Override
    public boolean collidedWithBomb(throwableBomb bomb) {
        bomb.setHorizontalVelocity(-constants.ROCK_VELOCITY);
        return false;
    }

    @Override
    public boolean collidedWithPlayer(olta player) {
        return true;
    }

    @Override
    public void alive() {
        super.alive();
        setAnimation(0.05f, regions);
        isSpungieCrying = false;
    }

    @Override
    public void dispose() {
        super.dispose();
        texture.dispose();
        for (TextureRegion reg : regions){
            reg.getTexture().dispose();
        }
        for (TextureRegion reg : regions1){
            reg.getTexture().dispose();
        }
    }
}
