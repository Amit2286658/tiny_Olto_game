package com.me.tiny_olta.sprites.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.me.tiny_olta.managers.EnemyManager;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.sprites.olta;
import com.me.tiny_olta.sprites.throwableBomb;

public class shadow extends collidableObjects {

    private Texture texture;
    private TextureRegion region_1, region_2, region_3;
    private boolean fluctuationUp = false;
    private float temp_velo = 150f;
    private Vector2 velo = new Vector2(0, 0);

    public shadow(float initialPosition){
        super();
        texture = new Texture("shadow.png");
        region_1 = new TextureRegion(texture, 0, 0, 305, 280);
        region_2 = new TextureRegion(texture, 305, 0, 305, 280);
        region_3 = new TextureRegion(texture, 2 * 305, 0, 305, 280);

        setTexture(region_1);

        setPosition(initialPosition, constants.GROUND_OFFSET);
        setWidth(constants.ENEMY_WIDTH);
        setHeight(constants.ENEMY_HEIGHT);

        setCollisionCoordinates(getPosition().x, getPosition().y);
        setCollisionWidth(getWidth());
        setCollisionHeight(getHeight());

        setEnemyType(EnemyManager.ENEMY_TYPE.SHADOW);
        setType(objectType.STATIC);
    }

    public void update(float delta) {
        if (!isDestroyed()) {

            setPosition(getPosition().x, fluctuatePosition(getPosition().y, delta));
            setCollisionY(getPosition().y);

            /*velo.add(0, constants.GRAVITY);
            velo.scl(delta);

            if (fluctuationUp){
                setPosition(getPosition().x, velo.y);
            }

            velo.scl(1/delta);

            if (getPosition().y < constants.GROUND_OFFSET){
                velo.y = constants.JUMP_IMPULSE;
                fluctuationUp = true;
            }*/
        }else {
            if (getRotationAngle() <= 90) {
                setRotationAngle(getRotationAngle() - 360 * delta);
            }
            setPosition(getPosition().x + ((temp_velo -= temp_velo != 100 ? delta * 192 : 0f) * delta),
                    getPosition().y + constants.GRAVITY * 4 * delta);
        }
    }

    public float fluctuatePosition(float y, float delta){
        int fluctuationRate = 80;

        if (y <= constants.GROUND_OFFSET && !fluctuationUp){
            fluctuationUp = true;
        }else if (y >= constants.GROUND_OFFSET * 4 && fluctuationUp){
            fluctuationUp = false;
        }

        if (fluctuationUp) {
            y += (fluctuationRate -= delta * 50) * delta;
        } else {
            y += -(fluctuationRate += delta * 50) * delta;
        }
        return y;
    }

    @Override
    public void drawTexture(SpriteBatch batch) {
        if (!isDestroyed()){
            if (fluctuationUp){
                batch.draw(region_2, getPosition().x, getPosition().y, getWidth(), getHeight());
            }else
                batch.draw(region_1, getPosition().x, getPosition().y, getWidth(), getHeight());
        } else
            batch.draw(region_3, getPosition().x, getPosition().y, getWidth()/2, getHeight()/2, getWidth(),
                    getHeight(), 1f, 1f, getRotationAngle());
    }

    @Override
    public boolean collidedWithBomb(throwableBomb bomb){
        setDestroyed(true);
        disableVelocity();
        turnRotationOn();
        return true;
    }

    @Override
    public boolean collidedWithPlayer(olta player) {
        return true;
    }

    @Override
    public void alive(){
        super.alive();
        temp_velo = 150f;
        setPosition(getPosition().x, 21);
        turnRotationOff();
        disableVelocity();
    }

    @Override
    public void dispose() {
        super.dispose();
        texture.dispose();
        region_1.getTexture().dispose();
        region_2.getTexture().dispose();
        region_3.getTexture().dispose();
    }
}
