package com.me.tiny_olta.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.sprites.coin;
import com.me.tiny_olta.sprites.hero;

public class coinManager {

    private final static float SPACING = 10f;
    private static Array<coin> activeCoins = new Array<>();

    public static void createCoins(float player_x){
        for (int i = 0; i < 8; i++){
            coin c = new coin(500 + player_x + i * SPACING);
            c.setCoinActive(MathUtils.randomBoolean());
            activeCoins.add(c);
        }
    }

    public static void update(OrthographicCamera gameCam, hero player, float delta){
        for (coin c : activeCoins){
            c.update(delta);
            if (player.getShape().overlaps(c.getCollisionShape())){
                c.collidedWithPlayer(player);
            }
        }
        if (activeCoins.get(activeCoins.size - 1).getPosition().x < gameCam.position.x - gameCam.viewportWidth/2){
            repositionWholeGroup(player.getPosition().x);
        }
    }

    private static void repositionWholeGroup(float player_x){
        int randomFirstDistance = MathUtils.random(constants.GAME_WIDTH, constants.WORLD_WIDTH);
        float fixedRandomHeight = MathUtils.random(constants.GROUND_OFFSET,  constants.GROUND_OFFSET * 4);
        for (int i = 0; i < activeCoins.size; i++){
            activeCoins.get(i).setCoinActive(MathUtils.randomBoolean());
            activeCoins.get(i).reposition(randomFirstDistance + player_x + i * SPACING, fixedRandomHeight);
        }
    }

    public static void draw(SpriteBatch batch){
        for (coin c : activeCoins){
            c.drawAnimation(batch, true);
        }
    }

    public static void dispose(){
        for (coin c : activeCoins){
            c.dispose();
        }
    }
}
