package com.me.tiny_olta.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.me.tiny_olta.OltaRush;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "tiny Olto";
		config.width = 800;
		config.height = 480;
		new LwjglApplication(new OltaRush(), config);
	}
}
