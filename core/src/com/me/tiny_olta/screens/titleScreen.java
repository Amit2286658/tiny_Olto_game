package com.me.tiny_olta.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.me.tiny_olta.OltaRush;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.gui.abstractActor;
import com.me.tiny_olta.gui.guiManager;
import com.me.tiny_olta.gui.titleGUI.animatedOlta;
import com.me.tiny_olta.gui.titleGUI.olta_logo;
import com.me.tiny_olta.gui.titleGUI.settings;

public class titleScreen extends screen {

    private TextureRegion bg;
    private BitmapFont middleFont;
    private TextureRegion settings_icon;
    private float settings_icon_rotation = 0f;
    private ShapeRenderer renderer;

    tapAndPlay play;
    settingsText set;
    private animatedOlta ol;
    olta_logo logo;

    private OltaRush rush;

    public titleScreen(OltaRush rush) {
        super(rush.getBatch());
        this.rush = rush;
    }

    @Override
    public void show() {
        super.show();
        renderer = new ShapeRenderer();

        guiManager.createStageForTitleScreen(getUiPort(), rush.getBatch());

        bg = new TextureRegion(new Texture("ui_background1.png"));
        settings_icon = new TextureRegion(new Texture("gui/settings_icon1.png"));
        middleFont = new BitmapFont(Gdx.files.internal("fonts/gothic.fnt"));
        middleFont.getData().setScale(0.5f);

        final background bcg = new background(bg, new Vector2(0, 0), constants.WORLD_WIDTH, constants.WORLD_HEIGHT);
        bcg.setTouchable(Touchable.enabled);
        bcg.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                rush.setScreen(new gameScreen(rush.getBatch()));
                return false;
            }
        });

        play = new tapAndPlay(middleFont, new Vector2(constants.WORLD_WIDTH/2f - 60,
                constants.WORLD_HEIGHT/2f - 80), "Tap to play");

        final settings setting = new settings(rush.getBatch(), middleFont, getUiPort(), guiManager.getTitleScreenStage());

        set = new settingsText(settings_icon, new Vector2(constants.WORLD_WIDTH - 190, 50), 30, 30,
                middleFont, new Vector2(constants.WORLD_WIDTH - 150, 72),"Settings");
        set.setTouchable(Touchable.enabled);
        set.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.setInputProcessor(null);
                setting.show(getGamePort());
                return false;
            }
        });

        ol = new animatedOlta(new Vector2(0/*constants.WORLD_WIDTH/2f - 339/2f - 85f*/,
                constants.WORLD_HEIGHT/2f - 200/2f + 115));
        logo = new olta_logo(new Vector2(70 + 64, constants.WORLD_HEIGHT/2f - 200/2f + 80));


        guiManager.addChildToTitleScreen(bcg, play, set, logo, ol, setting);

        Gdx.input.setInputProcessor(guiManager.getTitleScreenStage());
    }

    @Override
    public void update(float delta) {
        guiManager.updateTitleScreenUi(delta);
        if (ol.isFinished()){
            play.runAlphaNow();
            set.runAlphaNow();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(getUiCamera().combined);
        guiManager.draw();
    }

    @Override
    public void hidden() {
        Gdx.input.setInputProcessor(null);
    }

    static class background extends abstractActor {
        public background(TextureRegion region, Vector2 position, float width, float height) {
            super(region, position, width, height);
            setType(TYPE.IMAGE);
            setImage_type(IMAGE_TYPE.STATIC);
            setBounds(position.x, position.y, width, height);
        }
    }

    static class tapAndPlay extends abstractActor {
        public tapAndPlay(BitmapFont font, Vector2 fontPosition, String text) {
            super(font, fontPosition, text);
            setType(TYPE.TEXT);
            setAlpha(0f, 1f, 1f, ALPHA_TYPE.ALPHA_IN);
            setAlphaBatchDefaultValue(0f);
        }
    }

    static class settingsText extends abstractActor {
        public settingsText(TextureRegion region, Vector2 position, float width, float height,
                        BitmapFont font, Vector2 fontPosition, String text) {
            super(region, position, width, height, font, fontPosition, text);
            enableRotation(90);
            setType(TYPE.BOTH);
            setImage_type(IMAGE_TYPE.STATIC);
            setBounds(position.x, position.y, width + 100, height);
            setAlpha(0f, 1f, 1f, ALPHA_TYPE.ALPHA_IN);
            setAlphaBatchDefaultValue(0f);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        bg.getTexture().dispose();
        middleFont.dispose();
        settings_icon.getTexture().dispose();
        renderer.dispose();
        guiManager.disposeTitleScreen();
    }
}
