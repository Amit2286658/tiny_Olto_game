package com.me.tiny_olta.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.gui.abstractActor;
import com.me.tiny_olta.gui.guiManager;
import com.me.tiny_olta.gui.titleGUI.settings;

public class hudManager {

    private static pauseButton pButton;
    private static coinMeter coin_meter;
    private static enemyKilled enemy_killed;
    private static distanceRun distance_run;
    private static bombCounter bomb_counter;

    private static BitmapFont middleFont;

    private static interruptScreen pausescreen;

    private static boolean isGamePaused = false;

    public static void createOverlayitems(SpriteBatch batch, FitViewport uiPort){
        middleFont = new BitmapFont(Gdx.files.internal("fonts/gothic.fnt"));
        middleFont.getData().setScale(0.4f);

        pButton = new pauseButton(new TextureRegion(new Texture("gui/pause.png")),
                new Vector2(25, 25), 40, 40);
        pButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                hudManager.showPauseScreen();
                if (mButtonClickEvents != null){
                    mButtonClickEvents.pauseButton();
                }
                return true;
            }
        });

        coin_meter = new coinMeter(new TextureRegion(new Texture("coins.png"), 0, 0, 16, 16),
                new Vector2(25, 480 - 25 - 12), 16, 16,
                middleFont, new Vector2(25 + 20, 480 - 25), "0");
        enemy_killed = new enemyKilled(middleFont, new Vector2(720, 460), "0 kills");
        distance_run = new distanceRun(middleFont, new Vector2(750, 420), "0 m");
        bomb_counter = new bombCounter(new TextureRegion(new Texture("bomb.png"), 0, 0, 16, 16),
                new Vector2(20, 410), 24, 24, middleFont, new Vector2(45, 428), "4");

        pausescreen = new interruptScreen(batch, uiPort, middleFont);
    }

    private static buttonClickEvents mButtonClickEvents;

    public static void setButtonListener(buttonClickEvents buttonListener){
        mButtonClickEvents = buttonListener;
        pausescreen.setButtonListener(mButtonClickEvents);
    }

    public interface buttonClickEvents{
        void pauseButton();
        void resumeButton();
        void mainMenuButton();
    }

    public static Array<Actor> getOverlayItems(){
        Array<Actor> actors = new Array<>();
        actors.add(pButton);
        actors.add(pausescreen);
        actors.add(coin_meter);
        actors.add(enemy_killed);
        actors.add(distance_run);
        actors.add(bomb_counter);
        for (abstractActor actor : pausescreen.getChildActors()){
            actors.add(actor);
        }

        return actors;
    }

    public static void showPauseScreen(){
        pausescreen.showPauseScreen();
        isGamePaused = true;
    }

    public static void hidePauseScreen(){
        pausescreen.hidePauseScreen();
        isGamePaused = false;
    }

    static class pauseButton extends abstractActor {
        public pauseButton(TextureRegion region, Vector2 position, float width, float height) {
            super(region, position, width, height);
            setType(TYPE.IMAGE);
            setImage_type(IMAGE_TYPE.STATIC);
            setBounds(position.x, position.y, width, height);
        }
    }

    static class coinMeter extends abstractActor {
        float bitmapFontSize = 0.3f;
        boolean increaseFontSize = false;

        public coinMeter(TextureRegion region, Vector2 position, float width, float height, BitmapFont font,
                         Vector2 fontPosition, String text){
            super(region, position, width, height, font, fontPosition, text);
            setType(TYPE.BOTH);
            setImage_type(IMAGE_TYPE.STATIC);
        }

        public void updateCoinReading(){
            setText(String.valueOf(Integer.parseInt(getText()) + 1));
            if (!increaseFontSize)
                increaseFontSize = true;
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            if (increaseFontSize){
                bitmapFontSize += 0.05f;
                if (bitmapFontSize > 5f) {
                    bitmapFontSize = 5f;
                    increaseFontSize = false;
                }
            }
            else {
                if (bitmapFontSize > 0.3f) {
                    bitmapFontSize -= 0.02f;
                } else bitmapFontSize = 0.3f;
            }
            getFont().getData().setScale(bitmapFontSize);
        }
    }

    static class enemyKilled extends abstractActor {
        public enemyKilled (BitmapFont font, Vector2 fontPosition, String text){
            super(font, fontPosition, text);
            setType(TYPE.TEXT);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            getFont().getData().setScale(0.5f);
            super.draw(batch, parentAlpha);
            getFont().getData().setScale(0.4f);
        }
    }

    static class distanceRun extends abstractActor {
        public distanceRun (BitmapFont font, Vector2 fontPosition, String text){
            super(font, fontPosition, text);
            setType(TYPE.TEXT);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            getFont().getData().setScale(0.3f);
            super.draw(batch, parentAlpha);
            getFont().getData().setScale(0.4f);
        }
    }

    static class bombCounter extends abstractActor {
        public bombCounter(TextureRegion region, Vector2 position, float width, float height, BitmapFont font,
                           Vector2 fontPosition, String text){
            super(region, position, width, height, font, fontPosition, text);
            setType(TYPE.BOTH);
            setImage_type(IMAGE_TYPE.STATIC);
        }
    }

    public static void dispose(){
        pButton.dispose();
        middleFont.dispose();
        pausescreen.dispose();
    }
}

class interruptScreen extends abstractActor {

    private Pixmap blurredBackground;

    private Texture blurredTexture, pauseScreenFilter;

    private boolean shouldShow = false;

    private float alphaCounter = 0f;
    private float alphaValue = 0f;

    private FitViewport uiPort;

    private settings setting;

    private resumeButton resumebutton;
    private mainMenuButton main_menu_button;
    private mainStats statsTitle;
    private settingsButton settings_button;

    private Table stats_items_container, stats_items_value_container;
    private Label stats_item_1, stats_item_2, stats_item_3, stats_item_4, stats_items_value_1,
            stats_items_value_2, stats_items_value_3, stats_items_value_4;
    private Label.LabelStyle stats_items_style;

    private BitmapFont font;

    private hudManager.buttonClickEvents clickEvents;

    public interruptScreen(SpriteBatch batch, final FitViewport uiPort, BitmapFont font){
        this.uiPort = uiPort;
        pauseScreenFilter = new Texture("gui/pauseScreenFilter.png");

        this.font = font;

        setting = new settings(batch, this.font, uiPort, guiManager.getGameScreenStage()){
            @Override
            public void draw(Batch batch, float parentAlpha) {
                if (getBlurredTexture() != null){
                    super.draw(batch, parentAlpha);
                }
            }
        };

        resumebutton = new resumeButton(font, new Vector2(680, 50), "Resume", 0, 0);
        resumebutton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                hudManager.hidePauseScreen();
                if (clickEvents != null){
                    clickEvents.resumeButton();
                }
                return true;
            }
        });

        main_menu_button = new mainMenuButton(font, new Vector2(40, 50), "Exit", 0, 0);
        main_menu_button.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                hudManager.hidePauseScreen();
                if (clickEvents != null)
                    clickEvents.mainMenuButton();
                return true;
            }
        });

        settings_button = new settingsButton(font, new Vector2(200, 50), "Settings", 0, 0);
        settings_button.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                setting.show(uiPort);
                return true;
            }
        });

        statsTitle = new mainStats(font, new Vector2(380, 420), "STATS");

        stats_items_style = new Label.LabelStyle(this.font, Color.WHITE);

        class genericLabel extends Label {
            public genericLabel(CharSequence text, LabelStyle style) {
                super(text, style);
                setFontScale(0.4f);
            }

            @Override
            public void draw(Batch batch, float parentAlpha) {
                 setColor(getColor().r, getColor().g, getColor().b, alphaValue);
                 super.draw(batch, parentAlpha);
                 setColor(getColor().r, getColor().g, getColor().b, 1f);
            }
        }

        stats_item_1 = new genericLabel("Enemies killed : ", stats_items_style);
        stats_item_2 = new genericLabel("Coins Collected : ", stats_items_style);
        stats_item_3 = new genericLabel("Meters Run : ", stats_items_style);
        stats_item_4 = new genericLabel("bombs thrown : ", stats_items_style);
        stats_items_value_1 = new genericLabel("0", stats_items_style);
        stats_items_value_2 = new genericLabel("0", stats_items_style);
        stats_items_value_3 = new genericLabel("0", stats_items_style);
        stats_items_value_4 = new genericLabel("0", stats_items_style);

        stats_items_container = new Table();
        stats_items_container.add(stats_item_1).pad(10);
        stats_items_container.row();
        stats_items_container.add(stats_item_2).pad(10);
        stats_items_container.row();
        stats_items_container.add(stats_item_3).pad(10);
        stats_items_container.row();
        stats_items_container.add(stats_item_4).pad(10);
        stats_items_container.row();
        stats_items_container.setPosition(320, 250);

        stats_items_value_container = new Table();
        stats_items_value_container.add(stats_items_value_1).pad(10);
        stats_items_value_container.row();
        stats_items_value_container.add(stats_items_value_2).pad(10);
        stats_items_value_container.row();
        stats_items_value_container.add(stats_items_value_3).pad(10);
        stats_items_value_container.row();
        stats_items_value_container.add(stats_items_value_4).pad(10);
        stats_items_value_container.row();
        stats_items_value_container.setPosition(520, 250);
    }

    public void setButtonListener(hudManager.buttonClickEvents clickEvents1){
        clickEvents = clickEvents1;
    }

    public Array<abstractActor> getChildActors(){
        Array<abstractActor> childsActors = new Array<>();
        childsActors.add(resumebutton);
        childsActors.add(main_menu_button);
        childsActors.add(statsTitle);
        childsActors.add(settings_button);
        childsActors.add(setting);

        return childsActors;
    }

    @Override
    public void act(float delta) {
        updateAlphaFactor(delta);
        if (!shouldShow){
            if (alphaValue <= 0 && blurredBackground != null && blurredTexture != null){
                blurredBackground.dispose();
                blurredTexture.dispose();
                blurredBackground = null;
                blurredTexture = null;
            }
        }
        stats_items_container.act(delta);
        stats_items_value_container.act(delta);
        //setting.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (getBlurredTexture() != null){
            batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, alphaValue);
            batch.draw(getBlurredTexture(), 0, 0, constants.WORLD_WIDTH, constants.WORLD_HEIGHT);
            batch.draw(pauseScreenFilter, 0, 0, constants.WORLD_WIDTH, constants.WORLD_HEIGHT);
            batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1f);
            stats_items_container.draw(batch, parentAlpha);
            stats_items_value_container.draw(batch, parentAlpha);
        }
    }

    public void hidePauseScreen(){
        shouldShow = false;
        resumebutton.setDrawing(false);
        resumebutton.setBounds(resumebutton.getFontPosition().x, resumebutton.getFontPosition().y/2, 0, 0);

        main_menu_button.setDrawing(false);
        main_menu_button.setBounds(main_menu_button.getFontPosition().x, main_menu_button.getFontPosition().y/2,
                0, 0);

        settings_button.setDrawing(false);
        settings_button.setBounds(settings_button.getFontPosition().x, settings_button.getFontPosition().y/2,
                0, 0);

        statsTitle.setDrawing(false);
    }

    public void showPauseScreen(){
        blurredBackground = guiManager.takeScreenShot(uiPort);
        shouldShow = true;
        resumebutton.setDrawing(true);
        resumebutton.setBounds(resumebutton.getFontPosition().x, resumebutton.getFontPosition().y/2, 100, 40);

        main_menu_button.setDrawing(true);
        main_menu_button.setBounds(main_menu_button.getFontPosition().x, main_menu_button.getFontPosition().y/2,
                50, 40);

        settings_button.setDrawing(true);
        settings_button.setBounds(settings_button.getFontPosition().x, settings_button.getFontPosition().y/2,
                100, 40);

        statsTitle.setDrawing(true);
    }

    public Texture getBlurredTexture(){
        if (blurredBackground != null && blurredTexture == null) {
            int blurRadius = 10;
            blurredBackground = guiManager.blur(blurredBackground, blurRadius, 8, true);
            blurredTexture = new Texture(blurredBackground);
        }
        return blurredTexture;
    }

    public void updateAlphaFactor(float delta){
        if (shouldShow) {
            alphaCounter += 5 * delta;
            if (alphaCounter > 0.1f && alphaValue <= 1) {
                alphaValue += 0.05f;
                alphaCounter = 0f;
            }
        }else {
            alphaCounter += 5 * delta;
            if (alphaCounter > 0.1f && alphaValue > 0) {
                alphaValue -= 0.05f;
                alphaCounter = 0f;
            }
        }
    }

    public void dispose(){
        blurredBackground.dispose();
        blurredTexture.dispose();
        pauseScreenFilter.dispose();
        resumebutton.dispose();
        main_menu_button.dispose();
    }

    static class resumeButton extends abstractActor {
        private boolean draw = false;
        public resumeButton(BitmapFont font, Vector2 position, String text, float width, float height) {
            super(font, position, text);
            setType(TYPE.TEXT);
            setBounds(position.x, position.y/2, width, height);
        }

        public void setDrawing(boolean draw){
            this.draw = draw;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (draw) {
                getFont().getData().setScale(0.5f);
                super.draw(batch, parentAlpha);
                getFont().getData().setScale(0.4f);
            }
        }
    }

    static class mainMenuButton extends resumeButton {
        public mainMenuButton(BitmapFont font, Vector2 position, String text, float width, float height) {
            super(font, position, text, width, height);

        }
    }

    static class settingsButton extends resumeButton {
        public settingsButton(BitmapFont font, Vector2 position, String text, float width, float height) {
            super(font, position, text, width, height);
        }
    }

    static class mainStats extends abstractActor {

        private boolean draw = false;

        public mainStats (BitmapFont font, Vector2 fotPosition, String text){
            super(font, fotPosition, text);
            setType(TYPE.TEXT);
        }

        public void setDrawing(boolean draw) {
            this.draw = draw;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (draw){
                getFont().getData().setScale(0.6f);
                super.draw(batch, parentAlpha);
                getFont().getData().setScale(0.4f);
            }
        }
    }
}
