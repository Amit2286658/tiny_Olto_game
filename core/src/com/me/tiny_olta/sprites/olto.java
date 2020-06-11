package com.me.tiny_olta.sprites;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class olto extends hero {

    private boolean isLookingBack = false;

    public olto(float x) {
        super(x);
    }

    @Override
    public void initTextures() {
        runTexture = new Texture("olta_run.png");
        textureDust = new Texture("run_dust.png");
        jump = new Texture("olta_jump.png");
        throwRock = new Texture("olta_throw.png");
        death = new Texture("olta_death.png");
        walk = new Texture("olta_walk.png");
        idle = new Texture("olta_idle.png");
        plainRegion = new TextureRegion(new Texture("olta_plain.png"));
    }

    @Override
    public void update(float delta, OrthographicCamera gameCamera) {
        super.update(delta, gameCamera);
        if (isIdle){
            if (!isLookingBack) {
                plainRegion.flip(true, false);
                isLookingBack = true;
            }
        }else {
            if (isLookingBack){
                plainRegion.flip(true, false);
                isLookingBack = false;
            }
        }
    }
}
