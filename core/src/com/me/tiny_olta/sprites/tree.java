package com.me.tiny_olta.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.managers.vegetationManager;
import com.me.tiny_olta.sprites.objects.objects;

public class tree extends objects {

    private Body treeBody;
    private BodyDef treeBodyDef;
    public boolean isBack = true;

    public enum TYPE{
        TREE, CIRCLE_TREE, BUSH
    }

    /*an object to communicate with the vegetation manager*/
    vegetationManager.TreeTypeIdTextures communicationObject;

    private TYPE type;

    public tree(float position, World world){
        super();
        /*due to a bug, I'm not trying to add the third tree even randomly, because it messes up with the width and height,
        * it will be solved though, and I'm on it*/

        int i = MathUtils.random(1, 2);
        TextureRegion region = new TextureRegion(new Texture("veg/trees/circle_tree_"+i+".png"));
        switch (i){
            case 1:
                setType(TYPE.TREE);
                break;
            case 2:
                setType(TYPE.CIRCLE_TREE);
                break;
            case 3:
                setType(TYPE.BUSH);
                break;
            default: break;
        }
        init(position, region, world);

        communicationObject = new vegetationManager.TreeTypeIdTextures(region, getTreeType(), world);
    }

    private void init(float position, TextureRegion region, World world){
        /*if (!MathUtils.randomBoolean()){
            region.flip(true, false);
        }*/
        isBack = MathUtils.randomBoolean();
        setTexture(region);

        if (type == TYPE.TREE && !isBack){
            setPosition(position, 0);
        }else
            setPosition(position, constants.GROUND_OFFSET);

        float widthToHeightRatio =  (float) region.getRegionWidth()/ (float) region.getRegionHeight();

        if (getTreeType() != TYPE.BUSH) {
            if (type == TYPE.TREE && !isBack){
                setWidth(MathUtils.random(40, 60));//60, 80));
            }else
                setWidth(MathUtils.random(30, 40));//40, 60));
        }
        else setWidth(MathUtils.random(10, 15));//15, 35));

        setHeight(getWidth()/widthToHeightRatio);

        if (type == TYPE.TREE) {
            float unitHeight = getHeight()/5;
            float bodyHeight = unitHeight * 2;
            float bodyPositionX = getPosition().x + getWidth()/2;
            float bodyPositionY = getHeight() - unitHeight;

            CircleShape shape = new CircleShape();
            shape.setRadius(unitHeight - 2);

            treeBodyDef = new BodyDef();
            treeBodyDef.position.set(bodyPositionX, isBack ? bodyPositionY + 12 : bodyPositionY);
            treeBodyDef.type = BodyDef.BodyType.StaticBody;

            treeBody = world.createBody(treeBodyDef);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 0.5f;
            fixtureDef.filter.categoryBits = (short) 2;
            treeBody.createFixture(fixtureDef);

            shape.dispose();
        }else if (type == TYPE.CIRCLE_TREE){
            CircleShape shape = new CircleShape();
            shape.setRadius(getWidth()/2 - 4);

            treeBodyDef = new BodyDef();
            treeBodyDef.position.set(getPosition().x + getWidth()/2 + 1, getPosition().y + getHeight()/2 - 3);
            treeBodyDef.type = BodyDef.BodyType.StaticBody;

            treeBody = world.createBody(treeBodyDef);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 0.5f;
            fixtureDef.filter.categoryBits = (short) 2;
            treeBody.createFixture(fixtureDef);

            shape.dispose();
        }
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public TYPE getTreeType(){
        return type;
    }

    public vegetationManager.TreeTypeIdTextures getCommunicationObject() {
        return communicationObject;
    }

    public void reposition(float x, vegetationManager.TreeTypeIdTextures id) {
        //reposition(x);
        /*if (!MathUtils.randomBoolean()){
            newRegion.flip(true, false);
        }*/

        setTexture(id.getRegion());
        setType(id.getType());
        this.treeBody = id.getBody();

        isBack = MathUtils.randomBoolean();
        if (!isBack && type == TYPE.TREE){
            reposition(x, 0);
        }else reposition(x, constants.GROUND_OFFSET);

        communicationObject = id;

        float widthToHeightRatio =  (float) id.getRegion().getRegionWidth()/ (float) id.getRegion().getRegionHeight();

        if (getTreeType() != TYPE.BUSH) {
            if (!isBack && type == TYPE.TREE){
                setWidth(MathUtils.random(40, 60));//60, 80));
            }else
                setWidth(MathUtils.random(30, 40));//40, 60));
        }
        else setWidth(MathUtils.random(10, 15));//15, 35));
        setHeight(getWidth()/widthToHeightRatio);

        if (type == TYPE.TREE) {
            float unitHeight = getHeight() / 5;
            float bodyHeight = unitHeight * 2;
            float bodyPositionX = getPosition().x + getWidth() / 2;
            float bodyPositionY = getHeight() - unitHeight;

            treeBody.setTransform(bodyPositionX, isBack ? bodyPositionY + 12 : bodyPositionY, 0);
            treeBody.setAwake(true);
            treeBody.getFixtureList().first().getShape().setRadius(unitHeight - 2);
        }else if (type == TYPE.CIRCLE_TREE){
            treeBody.setTransform(getPosition().x + getWidth()/2 + 1, getPosition().y + getHeight()/2 - 3, 0);
            treeBody.setAwake(true);
            treeBody.getFixtureList().first().getShape().setRadius(getWidth()/2 - 4);
        }
    }
}
