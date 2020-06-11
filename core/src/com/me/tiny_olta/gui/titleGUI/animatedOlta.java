package com.me.tiny_olta.gui.titleGUI;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.gui.abstractActor;

public class animatedOlta extends abstractActor {

    private Texture walk, push, jump, idle, plain;
    private Array<TextureRegion> walking, pushing, jumping, idleing, backWalking;
    private int walkCount = 6, pushCount = 6, idleCount = 4, jumpCount = 8;
    private float idleCounter = 0f;
    private float counter = 0f;
    private float backWalking_counter = 0f;
    private boolean isIdle = false, isPushing = false, isBackWalking = false, isJumping = false, jumpVelocity = false;
    private Vector2 velocity = new Vector2(0, 0);
    private boolean finish = false;
    private int jumpCounter = 0;

    public animatedOlta(Vector2 position) {
        super();
        walk = new Texture("olta_walk.png");
        push = new Texture("olta_push.png");
        jump = new Texture("olta_jump.png");
        idle = new Texture("olta_idle.png");
        plain = new Texture("olta_plain.png");

        walking = new Array<>();
        pushing = new Array<>();
        jumping = new Array<>();
        idleing = new Array<>();
        backWalking = new Array<>();

        for (int i = 0; i < pushCount; i++){
            pushing.add(new TextureRegion(push, i * 32, 0, 32, 32));
        }
        for (int i = 0; i < walkCount; i++){
            walking.add(new TextureRegion(walk, i * 32, 0, 32, 32));
        }
        for (int i = walkCount - 1; i > 0; i--){
            backWalking.add(new TextureRegion(walk, i * 32, 0, 32, 32));
        }
        for (int i = 0; i < idleCount; i++){
            idleing.add(new TextureRegion(idle, i * 32, 0, 32, 32));
        }
        for (int i = idleCount - 1; i > 0; i--){
            idleing.add(new TextureRegion(idle, i * 32, 0, 32, 32));
        }
        for (int i = 0; i < jumpCount; i++){
            jumping.add(new TextureRegion(jump, i * 32, 0, 32, 32));
        }

        setAnimation(walking);
        setWidth(70);
        setHeight(70);
        setPosition(position);
        setType(TYPE.IMAGE);
        setImage_type(IMAGE_TYPE.ANIMATION);
    }

    public boolean isFinished() {
        return finish;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        counter += delta;
        if (!finish) {
            if (getPosition().x < 70 && !isBackWalking && !isJumping)
                setPositionX(getPosition().x += 40 * delta);
            if (getPosition().x >= 70 && !isBackWalking && !isJumping) {
                setAnimation(pushing);
                isPushing = true;
            }
            if (isPushing) {
                setPositionX(getPosition().x += 20 * delta);
            }
            if (getPosition().x + 64f > constants.WORLD_WIDTH / 2f - 339 / 2f) {
                isPushing = false;
                isBackWalking = true;
                setAnimation(backWalking);
            }
            if (isBackWalking) {
                getPosition().x -= 30 * delta;
                backWalking_counter += delta;
                if (backWalking_counter > 0.13f * 8) {
                    isBackWalking = false;
                    backWalking_counter = 0f;
                    isJumping = true;
                    setAnimation(jumping);
                    jumpVelocity = true;
                }
            }

            velocity.add(0, constants.GRAVITY);
            velocity.scl(delta);

            if (isJumping) {
                getPosition().add(velocity.x, velocity.y);
                if (getPosition().y < constants.WORLD_HEIGHT / 2f - 200 / 2f + 118) {
                    setPositionY(constants.WORLD_HEIGHT / 2f - 200 / 2f + 115);
                    jumpCounter++;
                    if (jumpCounter > 1) {
                    setTexture(new TextureRegion(plain));
                    setImage_type(IMAGE_TYPE.STATIC);
                    finish = true;
                    }
                }
            }

            velocity.scl(1 / delta);

            if (jumpVelocity) {
                velocity.x = 130;
                velocity.y = constants.JUMP_IMPULSE + constants.JUMP_IMPULSE / 2f;
                jumpVelocity = false;
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        walk.dispose();
        push.dispose();
        jump.dispose();
        idle.dispose();
        for (TextureRegion region : walking){
            region.getTexture().dispose();
        }
        for (TextureRegion region : pushing){
            region.getTexture().dispose();
        }
        for (TextureRegion region : jumping){
            region.getTexture().dispose();
        }
        for (TextureRegion region : idleing){
            region.getTexture().dispose();
        }
        for (TextureRegion region : backWalking){
            region.getTexture().dispose();
        }
    }
}
