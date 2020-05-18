package com.me.tiny_olta;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.me.tiny_olta.screens.gameScreen;
import com.me.tiny_olta.screens.titleScreen;

public class OltaRush extends Game {

	SpriteBatch batch;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new titleScreen(this));
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
