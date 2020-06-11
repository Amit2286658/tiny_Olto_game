package com.me.tiny_olta.sprites.objects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.me.tiny_olta.managers.EnemyManager;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.sprites.hero;
import com.me.tiny_olta.sprites.throwableBomb;

public class rocketBomber extends collidableObjects {

    private Texture texture;
    private Array<TextureRegion> regions;

    private Array<throwableBomb> bombs = new Array<>();
    private float bombTimer = 0f;
    private int bombCounter = 0;

    public rocketBomber(float initialPosition){
        super();

        texture = new Texture("rocket.png");
        regions = new Array<>();
        for (int i = 0; i < 6; i++){
            regions.add(new TextureRegion(texture, i * 958, 0, 958, 586));
        }

        for (int i = 0; i < 5; i++){
            throwableBomb bomb = new throwableBomb();
            bomb.setHorizontal(false);
            bomb.setVertical(true);
            bomb.getExplosive().width = 48;
            bomb.getExplosive().height = 48;
            bomb.getExplosive().getExplosiveCircle().setRadius(14);
            bombs.add(bomb);
        }

        setAnimation(0.05f, regions);

        setPosition(initialPosition, constants.GAME_HEIGHT - 40f);
        setWidth(constants.ENEMY_WIDTH + 27);
        setHeight(constants.ENEMY_HEIGHT + 10);

        setCollisionCoordinates(getPosition().x, getPosition().y);
        setCollisionWidth(getWidth());
        setCollisionHeight(getHeight());

        setEnemyType(EnemyManager.ENEMY_TYPE.ROCKET_BOMBER);
        setType(objectType.DYNAMIC);
    }

    public void update(float delta, OrthographicCamera cam){
        updateAnimationStateTimer(delta);
        if (isVelocityEnabled()){
            setPosition(getPosition().x - 16f * 2.2f/*1.5f*/ * delta);
            setCollisionX(getPosition().x);
        }
        if (isActive()){
            bombTimer += delta;
            if (bombTimer >= 0.37f/*0.3f*/ && bombCounter < 5){
                throwableBomb bomb = bombs.get(bombCounter);
                bomb.throwRock(getPosition().x, getPosition().y);
                bomb.setRotation(360);
                bomb.setTimer(0.13f * 3);
                bombCounter++;
                bombTimer = 0f;
            }
        }

        for (throwableBomb bomb : bombs){
            bomb.update(delta, getPosition().x, 150, cam);
        }

        for (throwableBomb bomb : bombs){
            if (bomb.getPosition().y <= constants.GROUND_OFFSET && !bomb.isDormant()){
                if (!bomb.hasExploded()) {
                    bomb.explode();
                }
                bomb.dormant();
            }
        }
    }

    public Array<throwableBomb> getBombs() {
        return bombs;
    }

    public boolean checkPlayerBombCollision(hero player){
        for (throwableBomb bomb : bombs){
            Circle circle = bomb.getExplosive().getExplosiveCircle();
            if (bomb.hasExploded()) {
                if (player.getShape().y <= circle.y + circle.radius &&
                        player.getShape().x + player.getShape().width >= circle.x - circle.radius &&
                player.getShape().x <= circle.x + circle.radius) {
                    return Intersector.overlaps(bomb.getExplosive().getExplosiveCircle(), player.getShape());
                }
            }
        }
        return false;
    }

    public boolean checkAnyObjectBombCollision(collidableObjects objects){
        for (throwableBomb bomb : bombs){
            if (objects.getEnemyType() == EnemyManager.ENEMY_TYPE.ROCKET_BOMBER)
                continue;

            Circle circle = bomb.getExplosive().getExplosiveCircle();
            Rectangle rect = objects.getCollisionShape();
            if (circle.y <= rect.y && circle.x - circle.radius < rect.x + rect.width && circle.x + circle.radius > rect.x)
                return Intersector.overlaps(bomb.getExplosive().getExplosiveCircle(), objects.getCollisionShape());
        }
        return false;
    }

    @Override
    public boolean collidedWithBomb(throwableBomb bomb) {
        return true;
    }

    @Override
    public boolean collidedWithPlayer(hero player) {
        return true;
    }

    @Override
    public void alive() {
        super.alive();
        bombCounter = 0;
        bombTimer = 0f;
        for (throwableBomb bomb : bombs){
            bomb.setHasExploded(false);
        }
    }

    @Override
    public void drawShape(SpriteBatch batch, ShapeRenderer renderer, OrthographicCamera gameCamera) {
        super.drawShape(batch, renderer, gameCamera);
        for (throwableBomb bomb : bombs){
            bomb.drawShape(batch, renderer, gameCamera);
        }
    }

    @Override
    public void drawAnimation(SpriteBatch batch, boolean shouldLoop) {
        super.drawAnimation(batch, shouldLoop);
        for (throwableBomb bomb : bombs){
            bomb.draw(batch);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        texture.dispose();
        for (TextureRegion region : regions){
            region.getTexture().dispose();
        }
        for (throwableBomb bomb : bombs){
            bomb.dispose();
        }
    }

    /*static class exposureBomb extends throwableBomb {
        private float exposureTimer = 0f;
        public boolean exposure = false;

        public void setExplosionExposureTimer(float delta){
            this.exposureTimer += delta;
        }

        public float getExposureTimer() {
            return exposureTimer;
        }

        @Override
        public void dormant() {
            super.dormant();
            exposureTimer = 0f;
            exposure = false;
        }
    }*/
}
