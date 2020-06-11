package com.me.tiny_olta.sprites.objects;

import com.badlogic.gdx.graphics.Texture;
import com.me.tiny_olta.managers.EnemyManager;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.sprites.hero;
import com.me.tiny_olta.sprites.throwableBomb;

public class crate extends collidableObjects {

    private Texture texture;

    public crate(float initialPosition) {
        super();
        texture = new Texture("crate.png");
        setTexture(texture);
        setPosition(initialPosition);
        setWidth(constants.ENEMY_WIDTH);
        setHeight(constants.ENEMY_HEIGHT);

        setCollisionX(initialPosition);
        setCollisionWidth(getWidth());
        setCollisionHeight(getHeight());

        setEnemyType(EnemyManager.ENEMY_TYPE.CRATE);
        setType(objectType.STATIC);
    }

    @Override
    public boolean collidedWithBomb(throwableBomb bomb) {
        return true;
    }

    @Override
    public boolean collidedWithPlayer(hero player) {
        return true;
    }

    @Override
    public void dispose() {
        super.dispose();
        texture.dispose();
    }
}
