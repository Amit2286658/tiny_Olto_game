package com.me.tiny_olta.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.me.tiny_olta.OltaRush;
import com.me.tiny_olta.gui.guiManager;
import com.me.tiny_olta.managers.*;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.sprites.*;
import com.me.tiny_olta.sprites.objects.objects;

public class gameScreen extends screen {

    private hero player;

    private OltaRush rush;

    private ShapeRenderer renderer;

    private GAME_STATE currentState = GAME_STATE.RUNNING;

    enum GAME_STATE {
        PAUSE, RUNNING
    }

    public gameScreen(OltaRush rush) {
        super(rush.getBatch());
        this.rush = rush;
    }

    @Override
    public void show() {
        super.show();

        player = new hero(80f);
        worldManager.init(player.getPosition().x);

        EnemyManager.createEnemies(player.getPosition().x);

        coinManager.createCoins(player.getPosition().x);

        weatherManager.createRain();
        weatherManager.createSky();
        weatherManager.createLightning();

        backgroundManager.initiate();
        backgroundManager.setDimension(constants.GAME_WIDTH + 200,
                constants.GAME_HEIGHT + 100, backgroundManager.ELEMENT_LAYER.LAYER2);
        backgroundManager.setDimension(constants.GAME_WIDTH + 200,
                constants.GAME_HEIGHT+ 100, backgroundManager.ELEMENT_LAYER.LAYER3);
        backgroundManager.setDimension(constants.GAME_WIDTH + 200,
                constants.GAME_HEIGHT+ 100, backgroundManager.ELEMENT_LAYER.LAYER4);
        backgroundManager.setDimension(constants.GAME_WIDTH, constants.GAME_HEIGHT, backgroundManager.ELEMENT_LAYER.LAYER5);
        backgroundManager.setDimension(constants.GAME_WIDTH, constants.GAME_HEIGHT, backgroundManager.ELEMENT_LAYER.LAYER6);

        vegetationManager.createTrees(worldManager.getWorld());

        renderer = new ShapeRenderer();

        guiManager.createStageForGameScreen(getUiPort(), rush.getBatch());
        hudManager.createOverlayitems(rush.getBatch(), getUiPort());

        guiManager.addChildToGameScreenUi(hudManager.getOverlayItems());

        Gdx.input.setInputProcessor(guiManager.getGameScreenStage());

        hudManager.setButtonListener(new hudManager.buttonClickEvents() {
            @Override
            public void pauseButton() {
                currentState = GAME_STATE.PAUSE;
            }

            @Override
            public void resumeButton() {
                currentState = GAME_STATE.RUNNING;
            }

            @Override
            public void mainMenuButton() {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void update(float delta) {
        if (currentState == GAME_STATE.RUNNING) {
            player.update(delta, getGameCamera());

            worldManager.update(player, delta);
            backgroundManager.update(delta, getGameCamera(), player);
            EnemyManager.update(getGameCamera(), delta, player.getPosition().x);
            vegetationManager.update(getGameCamera());
            coinManager.update(getGameCamera(), player, delta);

            weatherManager.update();
            weatherManager.updateRain(delta, getGameCamera());
            weatherManager.updateMist(delta, getGameCamera());
            weatherManager.updateSky(delta, getUiCamera());
            weatherManager.updateLightProperties(delta);
            weatherManager.updateLightning(delta);

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                if (!player.isInAir() && !player.isThrowing()) {
                    player.jump();
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                weatherManager.stopRain();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
                weatherManager.rain();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
                if (!player.isThrowing() && !player.isInAir())
                    player.throwRock();
            }
            if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
                getGameCamera().zoom += 0.02f;
                getUiCamera().zoom += 0.02f;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.X)) {
                getGameCamera().zoom -= 0.02f;
                getUiCamera().zoom -= 0.02f;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.R)) {
                getGameCamera().zoom = 1f;
                getUiCamera().zoom = 1f;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.W)){
                if (!player.isInAir() && !player.isThrowing())
                    player.walk();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.Q)){
                if (!player.isInAir() && !player.isThrowing())
                    player.run();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.I)){
                if (!player.isInAir() && !player.isThrowing())
                    player.idle();
            }

            if (EnemyManager.checkBombCollision(player.getBomb())) {
                player.getBomb().explode();
                player.getBomb().dormant();
            }

            if (EnemyManager.checkPlayerCollision(player, player.getBomb())) {
                //player.die();
            }

            if (!player.isDead()) {
                getGameCamera().position.x = player.getPosition().x + 120;
            }

            getGameCamera().update();
            getUiCamera().update();
        }

        guiManager.actOnGameHud();
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(getUiCamera().combined);
        batch.begin();
        backgroundManager.draw(batch, backgroundManager.ELEMENT_LAYER.LAYER0);
        weatherManager.drawSky(batch);
        backgroundManager.draw(batch, backgroundManager.ELEMENT_LAYER.LAYER1);
        batch.end();

        batch.setProjectionMatrix(getGameCamera().combined);
        batch.begin();
        backgroundManager.draw(batch, backgroundManager.ELEMENT_LAYER.LAYER2);
        backgroundManager.draw(batch, backgroundManager.ELEMENT_LAYER.LAYER3);
        backgroundManager.draw(batch, backgroundManager.ELEMENT_LAYER.LAYER4);
        vegetationManager.draw(batch, false);
        for (EnemyManager.enemyId id : EnemyManager.getEnemyList()){
            if (!id.getObject().isDestroyed()){
                if (id.getObject().getType() == objects.objectType.STATIC){
                    id.getObject().drawTexture(batch);
                }else {
                    id.getObject().drawAnimation(batch, true);
                }
                //id.getObject().drawShape(batch, renderer, getGameCamera());
            }
        }
        player.draw(batch);
        coinManager.draw(batch);
        backgroundManager.draw(batch, backgroundManager.ELEMENT_LAYER.LAYER5);
        vegetationManager.draw(batch, true);
        backgroundManager.draw(batch, backgroundManager.ELEMENT_LAYER.LAYER6);
        for (EnemyManager.enemyId id : EnemyManager.getEnemyList()){
            if (id.getObject().isDestroyed()){
                if (id.getObject().getType() == objects.objectType.STATIC) {
                    id.getObject().drawTexture(batch);
                } else {
                    id.getObject().drawAnimation(batch, true);
                }
                //id.getObject().drawShape(batch, renderer, getGameCamera());
            }
        }
        weatherManager.drawMist(batch);
        weatherManager.drawRain(batch);
        batch.end();

        worldManager.renderLight(getGameCamera());

        batch.setProjectionMatrix(getUiCamera().combined);
        guiManager.drawGameScreenUi();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        worldManager.useCustomViewPort(getGamePort());
    }

    public void drawGameDimension(SpriteBatch batch, ShapeRenderer renderer){
        batch.end();
        renderer.setProjectionMatrix(getGameCamera().combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.BLUE);
        renderer.rect(getGameCamera().position.x - getGameCamera().viewportWidth/2, 0, getGameCamera().viewportWidth,
                getGameCamera().viewportHeight);
        renderer.end();
        batch.begin();
    }

    public static float degToRad(float deg){
        return deg * MathUtils.PI/180;
    }

    @Override
    public void hidden() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        super.dispose();
        player.dispose();
        EnemyManager.dispose();
        backgroundManager.dispose();
        vegetationManager.dispose();
        coinManager.dispose();
        weatherManager.dispose();
        worldManager.dispose();
        hudManager.dispose();
        guiManager.getGameScreenStage().dispose();
    }
}
