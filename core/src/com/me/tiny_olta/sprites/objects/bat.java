package com.me.tiny_olta.sprites.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.me.tiny_olta.managers.EnemyManager;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.sprites.hero;
import com.me.tiny_olta.sprites.throwableBomb;

public class bat extends collidableObjects{

    private Texture texture;
    private TextureRegion region_1, region_2;
    private boolean fluctuationUp = false;
    private float temp_velo = 150f;

    public bat(float initialPosition){
        super();
        texture = new Texture("bats.png");
        region_1 = new TextureRegion(texture, 0, 0, 492, 409);
        region_2 = new TextureRegion(texture, 492, 0, 492, 409);

        setAnimation(0.13f, region_1, region_2);

        setPosition(initialPosition, 32);
        setWidth(constants.ENEMY_WIDTH);
        setHeight(constants.ENEMY_HEIGHT);

        setCollisionCoordinates(initialPosition, 32);
        setCollisionWidth(getWidth());
        setCollisionHeight(getHeight());

        setEnemyType(EnemyManager.ENEMY_TYPE.BAT);
        setType(objectType.DYNAMIC);
    }

    public void update(float delta) {
        if (!isDestroyed()) {
            updateAnimationStateTimer(delta);

            setPosition(getPosition().x, fluctuatePosition(getPosition().y, delta));
            if (isVelocityEnabled()){
                setPosition(getPosition().x - constants.ENEMY_VELOCITY * delta);
            }
            setCollisionX(getPosition().x);
            setCollisionY(getPosition().y);
        }else {
            setRotationAngle(getRotationAngle() - 360 * delta);
            setPosition(getPosition().x + ((temp_velo -= temp_velo != 100 ? delta * 192 : 0f) * delta),
                    getPosition().y + constants.GRAVITY * 4 * delta);
        }
    }

    public float fluctuatePosition(float y, float delta){
        int fluctuationRate = 32;

        if (y <= constants.IDEAL_FLYING_ENEMY_POSITION && !fluctuationUp){
            fluctuationUp = true;
        }else if (y >= constants.IDEAL_FLYING_ENEMY_POSITION * 2 && fluctuationUp){
            fluctuationUp = false;
        }

        if (fluctuationUp) {
            y += fluctuationRate * delta;
        } else {
            y += -fluctuationRate * delta;
        }
        return y;
    }


    @Override
    public boolean collidedWithBomb(throwableBomb bomb){
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
    public void alive(){
        super.alive();
        temp_velo = 150f;
        setPosition(getPosition().x, 32);
        turnRotationOff();
        disableVelocity();
    }

    @Override
    public void dispose() {
        super.dispose();
        texture.dispose();
        region_1.getTexture().dispose();
        region_2.getTexture().dispose();
    }
}
