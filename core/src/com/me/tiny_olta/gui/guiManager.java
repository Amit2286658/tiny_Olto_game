package com.me.tiny_olta.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class guiManager {

    private static Stage titleScreenStage, gameScreenStage;

    public static void createStageForTitleScreen(FitViewport uiViewPort, SpriteBatch batch){
        titleScreenStage = new Stage(uiViewPort, batch);
    }

    public static void createStageForGameScreen(FitViewport uiViewPort, SpriteBatch batch){
        gameScreenStage = new Stage(uiViewPort, batch);
    }

    public static void addChildToTitleScreen(Actor... actors){
        for (Actor act : actors){
            titleScreenStage.addActor(act);
        }
    }

    public static void addChildToGameScreenUi(Array<Actor> actors){
        for (Actor actor : actors){
            gameScreenStage.addActor(actor);
        }
    }

    public static Stage getTitleScreenStage() {
        return titleScreenStage;
    }

    public static Stage getGameScreenStage() {
        return gameScreenStage;
    }

    public static void updateTitleScreenUi(float delta){
        for (Actor act : titleScreenStage.getActors()){
            act.act(delta);
        }
    }

    public static void drawTitleScreen(){
        titleScreenStage.draw();
    }

    public static void drawGameScreenUi(){
        gameScreenStage.draw();
    }

    public static void actOnGameHud(){
        gameScreenStage.act();
    }

    public static void disposeTitleScreen(){
        for (Actor actor : titleScreenStage.getActors()){
            try{
                ((abstractActor)actor).dispose();
            }catch (ClassCastException e){
                System.out.println(e.getMessage());
            }
        }
        titleScreenStage.dispose();
    }

    public static Pixmap takeScreenShot(FitViewport gamePort){

        int gutterLeft = gamePort.getLeftGutterWidth();
        int gutterBot = gamePort.getBottomGutterHeight();
        int gutterWidth = Gdx.graphics.getWidth() - (2 * gutterLeft);
        int gutterHeight = Gdx.graphics.getHeight() - (2 * gutterBot);

        byte[] pixels = ScreenUtils.getFrameBufferPixels(gutterLeft, gutterBot, gutterWidth,
                gutterHeight, true);
        for (int i = 4; i < pixels.length; i += 4){
            pixels[i - 1] = (byte) 255;
        }

        Pixmap pixmap = new Pixmap(gutterWidth, gutterHeight,
                Pixmap.Format.RGBA8888);
        BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
        return pixmap;
    }

    public static Pixmap blur(Pixmap pixmap, int radius, int iterations, boolean shouldDispose){
        return BlurUtils.blur(pixmap, radius, iterations, shouldDispose);
    }

}
