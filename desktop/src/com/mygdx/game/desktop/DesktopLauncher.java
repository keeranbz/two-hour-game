package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.DodgeBlock;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Dodge Block";
		config.height = 600;
		config.width = 600;
		config.resizable = false;
		new LwjglApplication(new DodgeBlock(), config);

		// how to center window in middle of screen?
		config.x = 450;
		config.y = 100;
	}
}
