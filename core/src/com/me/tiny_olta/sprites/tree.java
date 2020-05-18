package com.me.tiny_olta.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.me.tiny_olta.managers.vegetationManager;
import com.me.tiny_olta.sprites.objects.objects;

public class tree extends objects {

    private Body treeBody;
    private BodyDef treeBodyDef;

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

        setTexture(region);

        setPosition(position);

        float widthToHeightRatio =  (float) region.getRegionWidth()/ (float) region.getRegionHeight();

        if (getTreeType() != TYPE.BUSH)
            setWidth(MathUtils.random(40, 80));
        else setWidth(MathUtils.random(10, 20));
        setHeight(getWidth()/widthToHeightRatio);

        if (type == TYPE.TREE) {
            float unitHeight = getHeight()/5;
            float bodyHeight = unitHeight * 2;
            float bodyPositionX = getPosition().x + getWidth()/2;
            float bodyPositionY = getHeight() - unitHeight;

            CircleShape shape = new CircleShape();
            shape.setRadius(unitHeight - 2);

            treeBodyDef = new BodyDef();
            treeBodyDef.position.set(bodyPositionX, bodyPositionY + 12);
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
        reposition(x);
        /*if (!MathUtils.randomBoolean()){
            newRegion.flip(true, false);
        }*/
        setTexture(id.getRegion());
        setType(id.getType());
        this.treeBody = id.getBody();

        communicationObject = id;

        float widthToHeightRatio =  (float) id.getRegion().getRegionWidth()/ (float) id.getRegion().getRegionHeight();

        if (getTreeType() != TYPE.BUSH)
            setWidth(MathUtils.random(40, 80));
        else setWidth(MathUtils.random(15, 35));
        setHeight(getWidth()/widthToHeightRatio);

        if (type == TYPE.TREE) {
            float unitHeight = getHeight() / 5;
            float bodyHeight = unitHeight * 2;
            float bodyPositionX = getPosition().x + getWidth() / 2;
            float bodyPositionY = getHeight() - unitHeight;

            treeBody.setTransform(bodyPositionX, bodyPositionY + 12, 0);
            treeBody.setAwake(true);
            treeBody.getFixtureList().first().getShape().setRadius(unitHeight - 2);
        }else if (type == TYPE.CIRCLE_TREE){
            treeBody.setTransform(getPosition().x + getWidth()/2 + 1, getPosition().y + getHeight()/2 - 3, 0);
            treeBody.setAwake(true);
            treeBody.getFixtureList().first().getShape().setRadius(getWidth()/2 - 4);
        }
    }
}
