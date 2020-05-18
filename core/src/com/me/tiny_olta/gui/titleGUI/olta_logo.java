package com.me.tiny_olta.gui.titleGUI;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.gui.abstractActor;

public class olta_logo extends abstractActor {

    private Texture texture;
    private boolean timeToMove = false, isInCorrectPosition = false;
    private float moveTimer = 0f;

    public olta_logo(Vector2 position) {
        super();
        texture = new Texture("gui/new_logo1.png");

        setTexture(new TextureRegion(texture));
        setPosition(position);
        setWidth(339);
        setHeight(200);
        setType(TYPE.IMAGE);
        setImage_type(IMAGE_TYPE.STATIC);
    }

    @Override
    public void act(float delta) {
        if (!timeToMove){
            moveTimer += delta;
            if (moveTimer > 0.13f * 10f + 40 * delta){
                timeToMove = true;
            }
        }else {
            if (!isInCorrectPosition) {
                setPositionX(getPosition().x += 20 * delta);
                if (getPosition().x >= constants.WORLD_WIDTH/2f - 339/2f){
                    isInCorrectPosition = true;
                }
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        texture.dispose();
    }
}
