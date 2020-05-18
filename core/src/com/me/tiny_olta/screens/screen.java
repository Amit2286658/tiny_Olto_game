package com.me.tiny_olta.screens;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.me.tiny_olta.constants;

public abstract class screen extends ScreenAdapter implements InputProcessor {

    public abstract void update(float delta);
    public abstract void render(SpriteBatch batch);
    public abstract void hidden();

    private SpriteBatch batch;
    private OrthographicCamera gameCamera, uiCamera;
    private FitViewport gamePort, uiPort;

    public screen(SpriteBatch batch){
        this.batch = batch;
    }

    @Override
    public void show() {
        gameCamera = new OrthographicCamera();
        uiCamera = new OrthographicCamera();
        gamePort = new FitViewport(constants.GAME_WIDTH, constants.GAME_HEIGHT, gameCamera);
        uiPort = new FitViewport(constants.WORLD_WIDTH, constants.WORLD_HEIGHT, uiCamera);

        gameCamera.position.set(gamePort.getWorldWidth()/2, gamePort.getWorldHeight()/2, 0);
        gameCamera.update();

        uiCamera.position.set(uiPort.getWorldWidth()/2, uiPort.getWorldHeight()/2, 0);
        uiCamera.update();
    }

    public OrthographicCamera getGameCamera() {
        return gameCamera;
    }

    public OrthographicCamera getUiCamera() {
        return uiCamera;
    }

    public FitViewport getGamePort() {
        return gamePort;
    }

    public FitViewport getUiPort() {
        return uiPort;
    }

    @Override
    public void render(float delta) {
        update(delta);
        render(batch);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        gamePort.update(width, height);
        uiPort.update(width, height);
    }

    @Override
    public void hide() {
        super.hide();
        hidden();
        dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
