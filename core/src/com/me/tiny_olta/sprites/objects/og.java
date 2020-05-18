package com.me.tiny_olta.sprites.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.me.tiny_olta.managers.EnemyManager;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.sprites.olta;
import com.me.tiny_olta.sprites.throwableBomb;

public class og extends collidableObjects{

    private Texture texture, texture2;
    private Array<TextureRegion> regions, regions2;
    private int hitCounter = 0;
    private float lastPos = 0f;
    private float temp_velo = 150f;
    private boolean pushedBack = false;
    private TYPE type = TYPE.OG;

    enum TYPE {
        OG, STONE_OG
    }

    public og(float initialPosition){
        super();
        texture = new Texture("og.png");
        texture2 = new Texture("stone_og.png");
        regions = new Array<>();
        regions2 = new Array<>();

        for (int i = 0; i < 6; i++){
            regions.add(new TextureRegion(texture, i * 299, 0, 299, 281));
        }

        for (int i = 0; i < 8; i++){
            regions2.add(new TextureRegion(texture2, i * 564, 0, 564, 553));
        }

        setAnimation(0.13f, regions);

        setPosition(initialPosition);
        setWidth(constants.ENEMY_WIDTH + 6);
        setHeight(constants.ENEMY_HEIGHT + 6);

        setCollisionX(initialPosition);
        setCollisionWidth(getWidth());
        setCollisionHeight(getHeight());

        setEnemyType(EnemyManager.ENEMY_TYPE.OG);
        setType(objectType.DYNAMIC);
    }

    public void update(float delta) {
        if (!isDestroyed()) {
            updateAnimationStateTimer(delta);

            if (isVelocityEnabled()){
                setPosition(getPosition().x - constants.ENEMY_VELOCITY * delta);
                setCollisionX(getPosition().x);
            }
            if (hitCounter == 1 && pushedBack) {
                if (lastPos + 30f >= getPosition().x) {
                    setPosition(getPosition().x + 80 * delta);
                    setCollisionX(getPosition().x);
                }else pushedBack = false;
            }
        }else {
            setRotationAngle(getRotationAngle() - 360 * delta);
            setPosition(getPosition().x + ((temp_velo -= temp_velo != 100 ? delta * 192 : 0f) * delta),
                    getPosition().y + constants.GRAVITY * 4 * delta);
        }
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    @Override
    public boolean collidedWithBomb(throwableBomb bomb) {
        if (type == TYPE.OG) {
            hitCounter += 1;
            if (hitCounter == 1) {
                lastPos = getPosition().x;
                pushedBack = true;
            } else if (hitCounter == 2) {
                setDestroyed(true);
                turnRotationOn();
            }
            disableVelocity();
        }
        return true;
    }

    @Override
    public boolean collidedWithPlayer(olta player) {
        return true;
    }

    @Override
    public void alive() {
        super.alive();
        temp_velo = 150f;
        lastPos = 0f;
        pushedBack = false;
        hitCounter = 0;
        setPosition(getPosition().x, constants.GROUND_OFFSET);
        turnRotationOff();
        disableVelocity();

        boolean bool = MathUtils.randomBoolean();
        setAnimation(0.13f, bool ? regions : regions2);
        setType(bool ? TYPE.OG : TYPE.STONE_OG);
    }

    @Override
    public void dispose() {
        super.dispose();
        texture.dispose();
        texture2.dispose();
        for (TextureRegion reg : regions){
            reg.getTexture().dispose();
        }
        for (TextureRegion reg : regions2){
            reg.getTexture().dispose();
        }
    }
}
