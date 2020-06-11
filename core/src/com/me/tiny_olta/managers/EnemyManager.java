package com.me.tiny_olta.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.sprites.objects.*;
import com.me.tiny_olta.sprites.hero;
import com.me.tiny_olta.sprites.throwableBomb;

import java.util.Random;

public class EnemyManager {

    private static Array<enemyId> enemyList;
    private static Random rand = new Random();
    private static Vector2 collisionPosition;
    private static collidableObjects collidedObject;

    public enum ENEMY_TYPE {
        CRATE, SPIKE, EVIL_BIRD, BAT, OG, ROCKET_BOMBER, SHADOW, SPUNGIE
    }

    public static void createEnemies(int size, float player_x){
        enemyList = new Array<>(size);

        for (int i = 0; i < size; i++){
            int enemyIdentifier = rand.nextInt(size);
            ENEMY_TYPE type = getEnemyById(enemyIdentifier);
            addEnemy(type, 500 + player_x + i * constants.ENEMY_DISTANCE, i);
        }
    }

    public static void createEnemies(float player_x){
        enemyList = new Array<>(3);

        reservedEnemyPool.createReservedEnemyPool();

        for (int i = 0; i < 3; i++){
            int enemyIdentifier = rand.nextInt(constants.ENEMY_COUNT);
            ENEMY_TYPE type = getEnemyById(enemyIdentifier);
            addEnemy(type, 500 + player_x + i * constants.ENEMY_DISTANCE, i);
        }
    }

    private static ENEMY_TYPE getEnemyById(int id){
        switch (id){
            case 0 :
                return ENEMY_TYPE.CRATE;
            case 1:
                return ENEMY_TYPE.SPIKE;
            case 2:
                return ENEMY_TYPE.EVIL_BIRD;
            case 3:
                return ENEMY_TYPE.BAT;
            case 4:
                return ENEMY_TYPE.OG;
            default: throw new NullPointerException("out of enemy Array bounds");
        }
    }

    public static void update(OrthographicCamera gameCamera, float delta, float x){

        for (enemyId id : enemyList){
            collidableObjects objects = id.getObject();
            switch (objects.getEnemyType()){
                case EVIL_BIRD:
                    ((evil_bird)objects).update(delta);
                    break;
                case BAT:
                    ((bat)objects).update(delta);
                    break;
                case OG:
                    ((og)objects).update(delta);
                    break;
                case SPIKE:
                    ((spikes)objects).update(gameCamera, x, delta);
                    break;
                case ROCKET_BOMBER:
                    ((rocketBomber)objects).update(delta, gameCamera);
                    break;
                case SHADOW:
                    ((shadow)objects).update(delta);
                    break;
                case SPUNGIE:
                    ((spungie)objects).update(delta, x);
                    break;
            }
        }

        for (enemyId id : enemyList){
            if (id.getObject().getPosition().x < gameCamera.position.x + gameCamera.viewportWidth/2){
                id.getObject().enableVelocity();
                id.getObject().setActive(true);
            }
        }

        for (enemyId id : enemyList){
            collidableObjects ob = id.getObject();
            if ((gameCamera.position.x - gameCamera.viewportWidth / 2) >= ob.getPosition().x + ob.getWidth()
            || ob.getPosition().y + ob.getHeight() <= 0){

                /*id object keeps its own separate position data, which is equal to the previous position before the recycling
                * event is triggered, therefore, the new updated position of the recycled enemy will not be according to the
                * position of the enemy when it died, but rather according to the last position known to the id object*/
                id.setPosition(id.getPosition() + constants.ENEMY_DISTANCE * 3);

                id.setObject(reservedEnemyPool.getFromReserve(id.getObject()));
                id.getObject().reposition(id.getPosition());
            }
        }
    }

    public static void draw(SpriteBatch batch){
        for (enemyId id : enemyList){
            collidableObjects objects = id.getObject();
            switch (objects.getEnemyType()){
                case CRATE:
                case SPIKE:
                case SHADOW:
                    objects.drawTexture(batch);
                    break;
                case EVIL_BIRD:
                case BAT:
                case OG:
                case ROCKET_BOMBER:
                case SPUNGIE:
                    objects.drawAnimation(batch, true);
                    break;
            }
        }
    }

    public static Array<enemyId> getEnemyList() {
        return enemyList;
    }

    public static collidableObjects getCollidedObject() {
        return collidedObject;
    }

    public static boolean checkBombCollision(throwableBomb bomb){
        for (enemyId id : enemyList){
            if (bomb.getShape().x + bomb.getShape().radius > id.getObject().getPosition().x &&
            bomb.getShape().x - bomb.getShape().radius < id.getObject().getPosition().x + id.getObject().getWidth() &&
            !id.getObject().isDestroyed()) {
                boolean col = Intersector.overlaps(bomb.getShape(), id.getObject().getCollisionShape());
                if (col) {
                    collisionPosition = new Vector2(id.getObject().getPosition());
                    collidedObject = id.getObject();
                    return id.getObject().collidedWithBomb(bomb);
                }
            }
        }
        return false;
    }


    /*due to looping restrictions, i kind of have to find another path to check for enemy collision with the bomb,
    * further attempts will be regardless*/
    public static void checkAnyObjectBomberCollision(){
        for (enemyId id : enemyList){
            if (id.getObject().getEnemyType() == ENEMY_TYPE.ROCKET_BOMBER){
                rocketBomber bomber = ((rocketBomber)id.getObject());
                for (enemyId id1 : enemyList){
                    if (bomber.checkAnyObjectBombCollision(id1.getObject())){
                        id1.getObject().collidedWithBomb(null);
                    }
                }
            }
        }
    }

    public static boolean checkPlayerCollision(hero player, throwableBomb bomb){
        for (enemyId id : enemyList){
            if (id.getObject().isActive()) {
                if (player.getPosition().x + constants.PLAYER_WIDTH > id.getObject().getPosition().x &&
                        player.getPosition().x < id.getObject().getPosition().x + id.getObject().getWidth() &&
                        !id.getObject().isDestroyed()) {
                    if (id.getObject().getEnemyType() == ENEMY_TYPE.ROCKET_BOMBER) {
                        return  ((rocketBomber) id.getObject()).checkPlayerBombCollision(player);
                    }
                    if (id.getObject().getCollisionShape().overlaps(player.getShape())){
                        return id.getObject().collidedWithPlayer(player);
                    }
                }
            }
        }
        if (bomb.getHorizontalVelocity() < 0) {
            if (bomb.getPosition().x < player.getPosition().x + constants.PLAYER_WIDTH) {
                boolean bool = Intersector.overlaps(bomb.getShape(), player.getShape());
                if (bool){
                    bomb.explode();
                    bomb.dormant();
                    return bool;
                }
            }
        }
        return false;
    }

    public static Vector2 getCollisionPosition() {
        return collisionPosition;
    }

    public static void drawShape(SpriteBatch batch, ShapeRenderer renderer, OrthographicCamera cam){
        for(enemyId id : enemyList){
            collidableObjects object = id.getObject();
            object.drawShape(batch, renderer, cam);
        }
    }

    private static void addEnemy(ENEMY_TYPE type, float position, int id){
        switch (type){
            case CRATE:
                enemyList.add(new enemyId(new crate(position), id, position));
                break;
            case SPIKE:
                enemyList.add(new enemyId(new spikes(position), id, position));
                break;
            case EVIL_BIRD:
                enemyList.add(new enemyId(new evil_bird(position), id, position));
                break;
            case BAT:
                enemyList.add(new enemyId(new bat(position), id, position));
                break;
            case OG:
                enemyList.add(new enemyId(new og(position), id, position));
                break;
        }
    }

    static class reservedEnemyPool{
        private static Array<collidableObjects> reservedArray = new Array<>(constants.ENEMY_COUNT + 2);

        public static void createReservedEnemyPool(){
            reservedArray.add(new bat(0));
            reservedArray.add(new crate(0));
            reservedArray.add(new evil_bird(0));
            reservedArray.add(new og(0));
            reservedArray.add(new spikes(0));
            reservedArray.add(new rocketBomber(0));
            reservedArray.add(new shadow(0));
            reservedArray.add(new spungie(0));
        }

        public static collidableObjects getFromReserve(collidableObjects object){
            object.alive();
            object.setActive(false);
            object.setRotationAngle(0);

            collidableObjects ob = reservedArray.random();

            int pos = reservedArray.indexOf(ob, true);
            reservedArray.set(pos, object);
            return ob;
        }

        public static void dispose(){
            for (collidableObjects object : reservedArray){
                object.dispose();
            }
            reservedArray.clear();
        }
    }

    public static void dispose(){
        for(enemyId id : enemyList){
            id.getObject().dispose();
        }
        enemyList.clear();
        reservedEnemyPool.dispose();
    }

    public static class enemyId{

        collidableObjects objects;
        int id;
        float position = 0f;

        enemyId(collidableObjects objects, int id){
            this.objects = objects;
            this.id = id;
        }

        enemyId(collidableObjects objects, int id, float position){
            this.objects = objects;
            this.id = id;
            this.position = position;
        }

        public void setPosition(float position) {
            this.position = position;
        }

        public float getPosition() {
            return position;
        }

        public collidableObjects getObject() {
            return objects;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setObject(collidableObjects objects) {
            this.objects = objects;
        }
    }
}
