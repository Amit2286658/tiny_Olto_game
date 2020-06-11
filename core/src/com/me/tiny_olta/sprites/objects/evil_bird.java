package com.me.tiny_olta.sprites.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.me.tiny_olta.managers.EnemyManager;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.sprites.hero;
import com.me.tiny_olta.sprites.throwableBomb;

public class evil_bird extends collidableObjects {

    private Texture texture, texture1;
    private Array<TextureRegion> regions, regions1;
    private float temp_velo = 150f;
    private TYPE type = TYPE.GEE;

    enum TYPE {
        GEE, BEE
    }

    public evil_bird(float intialPosition){
        super();
        regions = new Array<>();
        regions1 = new Array<>();

        texture = new Texture("fly.png");
        texture1 = new Texture("bee.png");

        for (int i = 0; i < 2; i++){
            regions.add(new TextureRegion(texture, i * 634, 0, 634, 512));
        }

        for (int i = 0; i < 6; i++){
            regions1.add(new TextureRegion(texture1, i * 456, 0, 456, 345));
        }

        setAnimation(0.13f, regions);

        setPosition(intialPosition, constants.IDEAL_FLYING_ENEMY_POSITION);
        setWidth(constants.ENEMY_WIDTH);
        setHeight(constants.ENEMY_HEIGHT);

        setCollisionCoordinates(intialPosition, constants.IDEAL_FLYING_ENEMY_POSITION);
        setCollisionWidth(getWidth());
        setCollisionHeight(getHeight());

        setEnemyType(EnemyManager.ENEMY_TYPE.EVIL_BIRD);
        setType(objectType.DYNAMIC);
    }

    public void update(float delta) {
        if (!isDestroyed()) {
            updateAnimationStateTimer(delta);

            if (isVelocityEnabled()){
                setPosition(type == TYPE.GEE ? getPosition().x - constants.ENEMY_VELOCITY * delta :
                        getPosition().x - constants.ENEMY_VELOCITY * 3 * delta);
                setCollisionX(getPosition().x);
            }
        }
        else {
            setRotationAngle(getRotationAngle() - 360 * delta);
            setPosition(getPosition().x + ((temp_velo -= temp_velo != 100 ? delta * 192 : 0f) * delta),
                    getPosition().y + constants.GRAVITY * 4 * delta);
        }
    }

    @Override
    public boolean collidedWithBomb(throwableBomb bomb) {
        setDestroyed(true);
        disableVelocity();
        turnRotationOn();
        return true;
    }

    @Override
    public boolean collidedWithPlayer(hero player) {
        return true;
    }

    @Override
    public void alive() {
        super.alive();
        temp_velo = 150f;
        setPosition(getPosition().x, constants.IDEAL_FLYING_ENEMY_POSITION);
        turnRotationOff();
        disableVelocity();

        int i = MathUtils.random(1, 2);
        switch (i){
            case 1 :
                setAnimation(0.13f, regions);
                type = TYPE.GEE;
                break;
            case 2 :
                setAnimation(0.05f, regions1);
                type = TYPE.BEE;
                break;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        texture.dispose();
        texture1.dispose();
        for (TextureRegion reg : regions){
            reg.getTexture().dispose();
        }
        for (TextureRegion reg : regions1){
            reg.getTexture().dispose();
        }
    }
}
