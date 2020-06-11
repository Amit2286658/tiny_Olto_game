package com.me.tiny_olta.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.sprites.tree;

public class vegetationManager {


    private static Array<tree> trees = new Array<>();
    private static Array<TreeTypeIdTextures> reserveTextureArray = new Array<>(5);

    public static void createTrees(World world){
        for (int i = 1; i < 4; i++){
            TextureRegion region = new TextureRegion(new Texture("veg/trees/circle_tree_"+i+".png"));
            tree.TYPE type = getTreeTypeByInteger(i);
            reserveTextureArray.add(new TreeTypeIdTextures(region, type, world));
        }
        /*creating two extra bush tree objects, to compensate for the lack of bush creation in the vegetation manager, due to
        * the bug that messes up with the width and height*/
        reserveTextureArray.add(new TreeTypeIdTextures(
                new TextureRegion(new Texture("veg/trees/circle_tree_"+3+".png")), getTreeTypeByInteger(3), world));
        reserveTextureArray.add(new TreeTypeIdTextures(
                new TextureRegion(new Texture("veg/trees/circle_tree_"+3+".png")), getTreeTypeByInteger(3), world));

        for (int i = 0; i < constants.TREE_COUNT; i++){
            trees.add(new tree(/*player_x +*/ i * constants.FIXED_TREE_DISTANCE, world));
        }
    }

    public static void update(OrthographicCamera gameCam){
        for (tree t : trees){
            if (t.getPosition().x + t.getWidth() < (gameCam.position.x - gameCam.viewportWidth/2) - 100f){
                t.reposition(t.getPosition().x + constants.TREE_COUNT * constants.FIXED_TREE_DISTANCE
                         * MathUtils.random(0.9f, 1.1f), putIntoReserve(t.getCommunicationObject()));
            }
        }
    }

    public static TreeTypeIdTextures putIntoReserve(TreeTypeIdTextures recycledId){
        TreeTypeIdTextures id = reserveTextureArray.random();
        int pos = reserveTextureArray.indexOf(id, true);
        reserveTextureArray.set(pos, recycledId);
        return id;
    }

    public static void draw(SpriteBatch batch){
        for (tree t : trees){
            t.drawTexture(batch);
        }
    }

    public static void draw(SpriteBatch batch, boolean shouldLookFront){
        if (shouldLookFront){
            for (tree t : trees){
                if (t.getTreeType() == tree.TYPE.TREE){
                    if (!t.isBack){
                        t.drawTexture(batch);
                    }
                }
            }
        }else {
            for (tree t : trees){
                if (t.getTreeType() == tree.TYPE.TREE){
                    if (t.isBack){
                        t.drawTexture(batch);
                    }
                }else {
                    t.drawTexture(batch);
                }
            }
        }
    }

    private static tree.TYPE getTreeTypeByInteger(int i){
        switch (i){
            case 1 :
                return tree.TYPE.TREE;
            case 2 :
                return tree.TYPE.CIRCLE_TREE;
            case 3 :
                return tree.TYPE.BUSH;
        }
        throw new NullPointerException("out of bound at line 60 in vegetation manager, handle yourself");
    }

    public static void dispose(){
        for (tree t : trees){
            t.dispose();
        }
        for (TreeTypeIdTextures id : reserveTextureArray){
            id.dispose();
        }
        trees.clear();
        reserveTextureArray.clear();
    }

    public static class TreeTypeIdTextures{
        private tree.TYPE type;
        private TextureRegion region;
        private Body body;
        private BodyDef bodyDef;
        private FixtureDef fixtureDef;

        public TreeTypeIdTextures(TextureRegion region, tree.TYPE type, World world){
            this.region = region;
            this.type = type;
            if (type == tree.TYPE.TREE) {
                float unitHeight = region.getRegionHeight()/5f;
                float bodyHeight = unitHeight * 2;

                CircleShape shape = new CircleShape();
                shape.setRadius(unitHeight);

                bodyDef = new BodyDef();
                bodyDef.position.set(0, 0);
                bodyDef.type = BodyDef.BodyType.StaticBody;

                body = world.createBody(bodyDef);

                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.shape = shape;
                fixtureDef.density = 0.5f;
                fixtureDef.filter.categoryBits = (short) 2;
                body.createFixture(fixtureDef);

                shape.dispose();
            }else if (type == tree.TYPE.CIRCLE_TREE){
                CircleShape shape = new CircleShape();
                shape.setRadius(region.getRegionWidth()/2f);

                bodyDef = new BodyDef();
                bodyDef.position.set(0, 0);
                bodyDef.type = BodyDef.BodyType.StaticBody;

                body = world.createBody(bodyDef);

                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.shape = shape;
                fixtureDef.density = 0.5f;
                fixtureDef.filter.categoryBits = (short) 2;
                body.createFixture(fixtureDef);

                shape.dispose();
            }
        }

        public TextureRegion getRegion() {
            return region;
        }

        public tree.TYPE getType() {
            return type;
        }

        public void setType(tree.TYPE type) {
            this.type = type;
        }

        public void setRegion(TextureRegion region) {
            this.region = region;
        }

        public Body getBody() throws NullPointerException {
            return body;
        }

        public void setBody(Body body) {
            this.body = body;
        }

        public void dispose(){
            region.getTexture().dispose();
        }
    }

}
