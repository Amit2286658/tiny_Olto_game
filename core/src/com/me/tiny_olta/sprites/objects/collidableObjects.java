package com.me.tiny_olta.sprites.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.me.tiny_olta.managers.EnemyManager;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.sprites.olta;
import com.me.tiny_olta.sprites.throwableBomb;

public abstract class collidableObjects extends objects{

    private Rectangle collisionShape;
    private EnemyManager.ENEMY_TYPE enemyType;

    // a rather cheap alternative to the velocity vector of the objects class, don't mind :)
    private boolean enableVelocity = false;

    private boolean destroyed = false;

    public collidableObjects(){
        super();
        collisionShape = new Rectangle();
        collisionShape.y = constants.GROUND_OFFSET;
    }

    public abstract boolean collidedWithBomb(throwableBomb bomb);
    public abstract boolean collidedWithPlayer(olta player);

    public void alive(){
        destroyed = false;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void enableVelocity(){
        enableVelocity = true;
    }

    public void disableVelocity(){
        enableVelocity = false;
    }

    public boolean isVelocityEnabled(){
        return enableVelocity;
    }

    public collidableObjects(float x, float y, float width, float height){
        super();
        collisionShape = new Rectangle(x, y, width, height);
    }

    public void setCollisionShape(float x, float y, float width, float height){
        collisionShape = new Rectangle(x, y, width, height);
    }

    public void drawShape(SpriteBatch batch, ShapeRenderer renderer, OrthographicCamera gameCamera){
        batch.end();
        renderer.setProjectionMatrix(gameCamera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.BLACK);
        renderer.rect(collisionShape.x, collisionShape.y, collisionShape.width, collisionShape.height);
        renderer.end();
        batch.begin();
    }

    public Rectangle getCollisionShape(){
        return this.collisionShape;
    }

    @Override
    public void reposition(float x) {
        super.reposition(x);
        setCollisionX(x);
    }

    public void setCollisionShape(Rectangle collisionShape) {
        this.collisionShape = collisionShape;
    }

    public void setCollisionWidth(float width){
        collisionShape.width = width;
    }

    public void setCollisionHeight(float height){
        collisionShape.height = height;
    }

    public void setCollisionCoordinates(float x, float y){
        collisionShape.setX(x);
        collisionShape.setY(y);
    }

    public void setEnemyType(EnemyManager.ENEMY_TYPE enemyType) {
        this.enemyType = enemyType;
    }

    public EnemyManager.ENEMY_TYPE getEnemyType() {
        return enemyType;
    }

    public void setCollisionX(float x){
        collisionShape.setX(x);
    }

    public void setCollisionY(float y){
        collisionShape.setY(y);
    }

    public boolean checkCollisionDetection(Rectangle rectangle){
        return collisionShape.overlaps(rectangle);
    }

    public void collisionShapeRotation(float f){
    }
}
