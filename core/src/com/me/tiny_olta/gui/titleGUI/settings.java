package com.me.tiny_olta.gui.titleGUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.me.tiny_olta.constants;
import com.me.tiny_olta.gui.abstractActor;
import com.me.tiny_olta.gui.guiManager;

/*this whole class is very weirdly wired up, i just coded it as I went, things are just messy here,
* refer to this comment, if I happen to forget what does what in this class, hell is wrong with me,
* menu table isn't part of the stage, dispose them separately, even though disposing the stage will
* take care of removing all the buttons, it's always better to call dispose method of the
* abstract super class first, it would remove the texture data attached with the actor,
* and dispose the texture regions even before to ensure no memory space remains occupied
* by the unused texture data. Although this class extends the abstract actor class, it does not
* have any text or texture data attached to it, therefore calling either super.act() or
* super.draw() is guaranteed to throw NullPointerException, the only purpose that the super
* class serves is to provide with the act and draw function on each frame.
*/
public class settings extends abstractActor {

    private BitmapFont font;
    private String musicString = "Music", soundString = "Sound";
    private Label.LabelStyle style;
    private Table settingsTable;
    private settingsText text;
    private closeButton closeBut;
    private onButton musicOn;
    private offButton musicOff;
    private onButton soundOn;
    private offButton soundOff;
    private Label musicLabel, soundLabel;
    private boolean shouldShow = false;

    private float alphaCounter = 0f;
    private float alphaValue = 0f;

    private Pixmap blurredBackground;
    private Texture blurredTexture;

    private Stage settingsScreenStage, mainMenuStage;

    private TextureRegion settings_icon, closeButton, on_button, off_button, on_button_unelevated, off_button_unelevated;

    public settings(SpriteBatch batch, BitmapFont font, FitViewport uiPort, Stage mainMenuStage){
        this.font = font;
        this.mainMenuStage = mainMenuStage;
        init(batch, uiPort);
        setBounds(0, 0, 0, 0);
    }

    private void init(SpriteBatch batch, FitViewport uiPort){
        settingsScreenStage = new Stage(uiPort, batch);

        style = new Label.LabelStyle(font, Color.WHITE);
        settingsTable = new Table();
        settingsTable.setBounds(constants.WORLD_WIDTH/2f - 200/2f, -440,
                200, 200);

        musicLabel = new Label(musicString, style);
        soundLabel = new Label(soundString, style);

        settings_icon = new TextureRegion(new Texture("gui/settings_icon2.png"));
        text = new settingsText(settings_icon, new Vector2(constants.WORLD_WIDTH/2f - 60/2f - 100, -100),
                60, 60,
                font, new Vector2(constants.WORLD_WIDTH/2f + 60/2f + 30 - 80, -160),"Settings");

        closeButton = new TextureRegion(new Texture("gui/close_button_white.png"));
        closeBut = new closeButton(closeButton, new Vector2(constants.WORLD_WIDTH - 190, 50), 30, 30,
                font, new Vector2(constants.WORLD_WIDTH - 150, 72),"Close");
        closeBut.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                hide(mainMenuStage);
                return false;
            }
        });

        on_button = new TextureRegion(new Texture("gui/on_button.png"));
        off_button = new TextureRegion(new Texture("gui/off_button.png"));
        off_button_unelevated = new TextureRegion(new Texture("gui/off_black_button_unelevated.png"));
        on_button_unelevated = new TextureRegion(new Texture("gui/on_black_button_unelevated.png"));

        musicOn = new onButton(on_button, new Vector2(constants.WORLD_WIDTH/2f - 100 + 30, -252),
                80, 34);
        musicOn.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("music on is clicked");
                return false;
            }
        });
        musicOff = new offButton(off_button_unelevated, new Vector2(constants.WORLD_WIDTH/2f, -252),
                80, 34);
        musicOff.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("music off is clicked");
                return false;
            }
        });
        soundOn = new onButton(on_button, new Vector2(constants.WORLD_WIDTH/2f - 100 + 30, -339),
                80, 34);
        soundOn.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("sound on is clicked");
                return false;
            }
        });
        soundOff = new offButton(off_button_unelevated, new Vector2(constants.WORLD_WIDTH/2f, -339),
                80, 34);
        soundOff.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("sound off is clicked");
                return false;
            }
        });
        settingsTable.add(musicLabel).pad(20).expandX();
        settingsTable.row();
        settingsTable.row();
        settingsTable.row();
        settingsTable.add(soundLabel).pad(46).expandX();
        settingsTable.row();

        settingsScreenStage.addActor(closeBut);
        settingsScreenStage.addActor(musicOn);
        settingsScreenStage.addActor(musicOff);
        settingsScreenStage.addActor(soundOn);
        settingsScreenStage.addActor(soundOff);
    }

    /*don't call super method, unless want to deal with the nullPointerException*/
    @Override
    public void act(float delta) {
        text.act(delta);
        settingsTable.act(delta);
        settingsScreenStage.act();
        if (shouldShow) {
            updateAlphaFactor(delta);
            if (settingsTable.getY() < (constants.WORLD_HEIGHT / 2f - 200 / 2f + 60) / 2){
                settingsTable.setY(settingsTable.getY() + constants.SETTINGS_TITLE_SCREEN_REVEAL_VELOCITY * delta);
            }else settingsTable.setY((constants.WORLD_HEIGHT / 2f - 200 / 2f + 60) / 2);

            if (text.getPosition().y < 360){
                text.setPositionY(text.getPosition().y + constants.SETTINGS_TITLE_SCREEN_REVEAL_VELOCITY * delta);
            }else text.setPositionY(360);

            if (text.getFontPosition().y < 396){
                text.setFontPositionY(text.getFontPosition().y + constants.SETTINGS_TITLE_SCREEN_REVEAL_VELOCITY * delta);
            }else text.setFontPositionY(396);

            if (musicOn.getPosition().y < constants.WORLD_HEIGHT/2f - 21f - 10){
                musicOn.setPositionY(musicOn.getPosition().y + constants.SETTINGS_TITLE_SCREEN_REVEAL_VELOCITY * delta);
            }else {
                musicOn.setPositionY(constants.WORLD_HEIGHT/2f - 21f - 10);
            }
            musicOn.setBoundY(musicOn.getPosition().y);

            if (musicOff.getPosition().y < constants.WORLD_HEIGHT/2f - 21f - 10){
                musicOff.setPositionY(musicOff.getPosition().y + constants.SETTINGS_TITLE_SCREEN_REVEAL_VELOCITY * delta);
            }else {
                musicOff.setPositionY(constants.WORLD_HEIGHT/2f - 21f - 10);
            }
            musicOff.setBoundY(musicOff.getPosition().y);

            if (soundOn.getPosition().y < constants.WORLD_HEIGHT/2f - 108f - 20){
                soundOn.setPositionY(soundOn.getPosition().y + constants.SETTINGS_TITLE_SCREEN_REVEAL_VELOCITY * delta);
            }else {
                soundOn.setPositionY(constants.WORLD_HEIGHT/2f - 108f - 20);
            }
            soundOn.setBoundY(soundOn.getPosition().y);

            if (soundOff.getPosition().y < constants.WORLD_HEIGHT/2f - 108f - 20){
                soundOff.setPositionY(soundOff.getPosition().y + constants.SETTINGS_TITLE_SCREEN_REVEAL_VELOCITY * delta);
            }else {
                soundOff.setPositionY(constants.WORLD_HEIGHT/2f - 108f - 20);
            }
            soundOff.setBoundY(soundOff.getPosition().y);

        }else {
            updateAlphaFactor(delta);
            if (settingsTable.getY() > -460){
                settingsTable.setY(settingsTable.getY() - constants.SETTINGS_TITLE_SCREEN_REVEAL_VELOCITY * delta);
            }
            if (text.getPosition().y > -100){
                text.setPositionY(text.getPosition().y - constants.SETTINGS_TITLE_SCREEN_REVEAL_VELOCITY * delta);
            }
            if (text.getFontPosition().y > -140){
                text.setFontPositionY(text.getFontPosition().y - constants.SETTINGS_TITLE_SCREEN_REVEAL_VELOCITY * delta);
            }
            if (musicOn.getPosition().y > -252){
                musicOn.setPositionY(musicOn.getPosition().y - constants.SETTINGS_TITLE_SCREEN_REVEAL_VELOCITY * delta);
                musicOn.setBoundY(musicOn.getPosition().y);
            }
            if (musicOff.getPosition().y > -252){
                musicOff.setPositionY(musicOff.getPosition().y - constants.SETTINGS_TITLE_SCREEN_REVEAL_VELOCITY * delta);
                musicOff.setBoundY(musicOff.getPosition().y);
            }
            if (soundOn.getPosition().y > -339){
                soundOn.setPositionY(soundOn.getPosition().y - constants.SETTINGS_TITLE_SCREEN_REVEAL_VELOCITY * delta);
                soundOn.setBoundY(soundOn.getPosition().y);
            }
            if (soundOff.getPosition().y > -339){
                soundOff.setPositionY(soundOff.getPosition().y - constants.SETTINGS_TITLE_SCREEN_REVEAL_VELOCITY * delta);
                soundOff.setBoundY(soundOff.getPosition().y);
            }
        }
    }

    /*hell is wrong with the scene2d, had to get this working this way,
        do not call the super method, or else you will face the scary nullPointerException*/
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, alphaValue);
        if (getBlurredTexture() != null)
            batch.draw(getBlurredTexture(), 0, 0, constants.WORLD_WIDTH, constants.WORLD_HEIGHT);
        batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1f);
        font.setColor(Color.BLACK);
        font.getData().setScale(0.7f);
        text.draw(batch, parentAlpha);
        font.getData().setScale(0.5f);
        font.setColor(Color.WHITE);
        settingsTable.draw(batch, parentAlpha);
        batch.end();
        settingsScreenStage.draw();
        batch.begin();
    }

    public void hide(Stage mainMenuStage){
        shouldShow = false;
        closeBut.setAlpha(1f, 0f, 0.8f, ALPHA_TYPE.ALPHA_OUT);
        closeBut.runAlphaNow();

        setBounds(0, 0, 0, 0);

        Gdx.input.setInputProcessor(mainMenuStage);
    }

    public void show(FitViewport gamePort){
        blurredBackground = guiManager.takeScreenShot(gamePort);
        shouldShow = true;
        closeBut.setAlpha(0f, 1f, 0.8f, ALPHA_TYPE.ALPHA_IN);
        closeBut.runAlphaNow();

        setBounds(0, 0, constants.WORLD_WIDTH, constants.WORLD_HEIGHT);

        Gdx.input.setInputProcessor(settingsScreenStage);
    }

    public Texture getBlurredTexture(){
        if (blurredTexture == null && blurredBackground != null) {
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
                alphaValue += 0.1f;
                alphaCounter = 0f;
            }
        }else {
            alphaCounter += 5 * delta;
            if (alphaCounter > 0.1f && alphaValue > 0) {
                alphaValue -= 0.1f;
                alphaCounter = 0f;
            }
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
        }
    }

    static class closeButton extends abstractActor {
        public closeButton(TextureRegion region, Vector2 position, float width, float height,
                           BitmapFont font, Vector2 fontPosition, String text) {
            super(region, position, width, height, font, fontPosition, text);
            setType(TYPE.BOTH);
            setAlphaBatchDefaultValue(0f);
            setBounds(position.x, position.y, width + 100, height);
        }
    }

    static class onButton extends abstractActor {
        public onButton(TextureRegion region, Vector2 position, float width, float height) {
            super(region, position, width, height);
            setType(TYPE.IMAGE);
            setImage_type(IMAGE_TYPE.STATIC);
            setBounds(position.x, position.y, width, height);
        }

        public void setBoundY(float y){
            setBounds(getPosition().x, y,getWidth(), getHeight());
        }
    }

    static class offButton extends abstractActor {
        public offButton(TextureRegion region, Vector2 position, float width, float height) {
            super(region, position, width, height);
            setType(TYPE.IMAGE);
            setImage_type(IMAGE_TYPE.STATIC);
            setBounds(position.x, position.y, width, height);
        }

        public void setBoundY(float y){
            setBounds(getPosition().x, y,getWidth(), getHeight());
        }
    }

    @Override
    public void dispose(){
        text.dispose();
        closeBut.dispose();
        musicOn.dispose();
        musicOff.dispose();
        soundOn.dispose();
        soundOff.dispose();
        settings_icon.getTexture().dispose();
        closeButton.getTexture().dispose();
        on_button_unelevated.getTexture().dispose();
        off_button_unelevated.getTexture().dispose();
        on_button.getTexture().dispose();
        off_button.getTexture().dispose();
        musicLabel.clear();
        soundLabel.clear();
        settingsTable.clear();
        settingsScreenStage.dispose();
    }
}