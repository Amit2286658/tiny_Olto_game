package com.me.tiny_olta.sprites.objects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.me.tiny_olta.managers.EnemyManager;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.sprites.hero;
import com.me.tiny_olta.sprites.throwableBomb;

public class spikes extends collidableObjects {

    private Texture texture, texture1, bity;
    private TextureRegion bity_1, bity_2;
    private boolean hasBityOpenedHisMouth = false;

    public boolean isTextureReversed = false;

    enum TYPE {
        SPIKE, BUG, BITY
    }

    private TYPE type = TYPE.SPIKE;

    public spikes(float initialPosition) {
        super();
        texture = new Texture("spikes.png");
        texture1 = new Texture("bug_spikes.png");
        bity = new Texture("bity.png");
        bity_1 = new TextureRegion(bity, 0, 0, 369, 369);
        bity_2 = new TextureRegion(bity, 369, 0, 369, 369);
        setTexture(texture);
        setPosition(initialPosition);
        setWidth(constants.ENEMY_WIDTH);
        setHeight(constants.ENEMY_HEIGHT);

        setCollisionX(initialPosition);
        setCollisionWidth(getWidth());
        setCollisionHeight(getHeight());

        setEnemyType(EnemyManager.ENEMY_TYPE.SPIKE);
        setType(objectType.STATIC);
    }

    public void update(OrthographicCamera gameCamera, float player_x, float delta) {
        if ((gameCamera.position.x - gameCamera.viewportWidth / 2) >= position.x + getWidth()){
            isTextureReversed = false;
        }
        if (player_x + constants.PLAYER_WIDTH/2 > getPosition().x){
            if (!isTextureReversed) {
                getTexture().flip(true, false);
                isTextureReversed = true;
            }
        }
        if (player_x > getPosition().x - 140 && !hasBityOpenedHisMouth && !isTextureReversed && type == TYPE.BITY){
            setTexture(bity_2);
            hasBityOpenedHisMouth = true;
        }
        if (type == TYPE.BITY){
            if (player_x > getPosition().x - 250){
                if (getWidth() < constants.ENEMY_WIDTH * 2f){
                    setWidth(getWidth() + getWidth() * delta);
                    setHeight(getHeight() + getHeight() * delta);
                    setCollisionWidth(getWidth());
                    setCollisionHeight(getHeight());
                }
            }
        }
    }

    @Override
    public void alive() {
        super.alive();
        int iden = MathUtils.random(1, 3);
        switch (iden){
            case 1:
                setTexture(texture);
                setHeight(constants.ENEMY_HEIGHT);
                setWidth(constants.ENEMY_WIDTH);
                setCollisionHeight(getHeight());
                setCollisionWidth(getWidth());
                type = TYPE.SPIKE;
                break;
            case 2:
                setTexture(texture1);
                setHeight(constants.ENEMY_HEIGHT - 3);
                setWidth(constants.ENEMY_WIDTH);
                setCollisionHeight(getHeight());
                setCollisionWidth(getWidth());
                type = TYPE.BUG;
                break;
            case 3:
                setTexture(bity_1);
                setHeight(constants.ENEMY_HEIGHT);
                setCollisionHeight(getHeight());
                setWidth(constants.ENEMY_WIDTH);
                setCollisionWidth(getWidth());
                type = TYPE.BITY;
                break;
        }
        hasBityOpenedHisMouth = false;
        isTextureReversed = false;
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
        texture1.dispose();
        bity.dispose();
        bity_1.getTexture().dispose();
        bity_2.getTexture().dispose();
    }
}
