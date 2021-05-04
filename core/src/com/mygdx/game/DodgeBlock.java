package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.math.Rectangle;

import java.util.Iterator;

public class DodgeBlock extends ApplicationAdapter {
	SpriteBatch batch;
	Rectangle redBlock;
	Texture redBlockImage;
	Texture blackBlockImage;
	Music soundtrack;
	Sound impactSound;
	OrthographicCamera camera;
	Array<Rectangle> blackBlocksDown;
	Array<Rectangle> blackBlocksUp;
	Array<Rectangle> blackBlocksRight;
	Array<Rectangle> blackBlocksLeft;
	BitmapFont font;
	double lastDropTime;
	boolean start;
	boolean gameOver;
	int collisions;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		start = false;
		gameOver = false;
		collisions = 0;

		// camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 600, 600);

		// load images
		redBlockImage = new Texture(Gdx.files.internal("sprites/redblock.png"));
		blackBlockImage = new Texture(Gdx.files.internal("sprites/blackblock.png"));


		// add music and sound
		soundtrack = Gdx.audio.newMusic(Gdx.files.internal("music/opening.ogg"));
		impactSound = Gdx.audio.newSound(Gdx.files.internal("sounds/impact.ogg"));
		soundtrack.setLooping(true);
		soundtrack.play();


		// add red block 20x20
		redBlock = new Rectangle();
		redBlock.height = 20;
		redBlock.width = 20;
		redBlock.x = 300 - (20/2);
		redBlock.y = 300 - (20/2);

		// spawn black blocks 20x20
		blackBlocksDown = new Array<Rectangle>();
		blackBlocksUp = new Array<Rectangle>();
		blackBlocksRight = new Array<Rectangle>();
		blackBlocksLeft = new Array<Rectangle>();
		blackBlocksDown.add(spawnBlackBlockDown());
		blackBlocksUp.add(spawnBlackBlockUp());
		blackBlocksRight.add(spawnBlackBlockRight());
		blackBlocksLeft.add(spawnBlackBlockLeft());
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();

		if (!gameOver) {
			// red block - keyboard movement
			if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
				redBlock.x -= 300 * Gdx.graphics.getDeltaTime();
				start = true;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
				redBlock.x += 300 * Gdx.graphics.getDeltaTime();
				start = true;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
				redBlock.y += 300 * Gdx.graphics.getDeltaTime();
				start = true;
			}
			if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
				redBlock.y -= 300 * Gdx.graphics.getDeltaTime();
				start = true;
			}

			// red block - screen edge limits
			if (redBlock.x < 0)
				redBlock.x = 0;
			if (redBlock.x > 600 - 20)
				redBlock.x = 600 - 20;
			if (redBlock.y < 0)
				redBlock.y = 0;
			if (redBlock.y > 600 - 20)
				redBlock.y = 600 - 20;

			if (start) {
				// black blocks - add to screen
				if(TimeUtils.nanoTime() - lastDropTime > 700000000) {
					blackBlocksDown.add(spawnBlackBlockDown());
					blackBlocksUp.add(spawnBlackBlockUp());
					blackBlocksRight.add(spawnBlackBlockRight());
					blackBlocksLeft.add(spawnBlackBlockLeft());
				}

				// black blocks - down movement
				for (Iterator<Rectangle> iter = blackBlocksDown.iterator(); iter.hasNext(); ) {
					Rectangle blackBlock = iter.next();
					blackBlock.y -= 200 * Gdx.graphics.getDeltaTime();
					if(blackBlock.y + 20 < 0)
						iter.remove();

					if(blackBlock.overlaps(redBlock)) {
						impactSound.play();
						collisions++;
						iter.remove();
					}
				}

				// black blocks - up movement
				for (Iterator<Rectangle> iter = blackBlocksUp.iterator(); iter.hasNext(); ) {
					Rectangle blackBlock = iter.next();
					blackBlock.y += 200 * Gdx.graphics.getDeltaTime();
					if(blackBlock.y > 600)
						iter.remove();

					if(blackBlock.overlaps(redBlock)) {
						impactSound.play();
						collisions++;
						iter.remove();
					}
				}

				// black blocks - right movement
				for (Iterator<Rectangle> iter = blackBlocksRight.iterator(); iter.hasNext(); ) {
					Rectangle blackBlock = iter.next();
					blackBlock.x += 200 * Gdx.graphics.getDeltaTime();
					if(blackBlock.x > 600)
						iter.remove();

					if(blackBlock.overlaps(redBlock)) {
						impactSound.play();
						collisions++;
						iter.remove();
					}
				}

				// black blocks - up movement
				for (Iterator<Rectangle> iter = blackBlocksLeft.iterator(); iter.hasNext(); ) {
					Rectangle blackBlock = iter.next();
					blackBlock.x -= 200 * Gdx.graphics.getDeltaTime();
					if(blackBlock.x + 20 < 0)
						iter.remove();

					if(blackBlock.overlaps(redBlock)) {
						impactSound.play();
						collisions++;
						iter.remove();
					}
				}
			}



			// draw sprites
			batch.setProjectionMatrix(camera.combined);
			batch.begin();

			// draw red block
			batch.draw(redBlockImage, redBlock.x, redBlock.y);

			// draw black blocks
			for (Rectangle blackBlock: blackBlocksDown) {
				batch.draw(blackBlockImage, blackBlock.x, blackBlock.y);
			}
			for (Rectangle blackBlock: blackBlocksUp) {
				batch.draw(blackBlockImage, blackBlock.x, blackBlock.y);
			}
			for (Rectangle blackBlock: blackBlocksRight) {
				batch.draw(blackBlockImage, blackBlock.x, blackBlock.y);
			}
			for (Rectangle blackBlock: blackBlocksLeft) {
				batch.draw(blackBlockImage, blackBlock.x, blackBlock.y);
			}

			batch.end();

			if (collisions > 9) gameOver = true;
		}
		else { // game over screen
			Gdx.gl.glClearColor(0, 0, 0, 0);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			camera.update();

			font = new BitmapFont();

			// draw text
			batch.setProjectionMatrix(camera.combined);
			batch.begin();

			font.getData().setScale(3);
			font.draw(batch, "GAME OVER",160, 340);


			batch.end();

		}


	}
	
	@Override
	public void dispose () {
		batch.dispose();
		soundtrack.dispose();
		impactSound.dispose();
		redBlockImage.dispose();
		blackBlockImage.dispose();
		font.dispose();
	}

	public Rectangle spawnBlackBlockDown () {
		Rectangle blackBlock = new Rectangle();
		blackBlock.x = MathUtils.random(0, 600-20);
		blackBlock.y = 600;
		blackBlock.width = 20;
		blackBlock.height = 20;
		lastDropTime = TimeUtils.nanoTime();
		return blackBlock;
	}

	public Rectangle spawnBlackBlockUp () {
		Rectangle blackBlock = new Rectangle();
		blackBlock.x = MathUtils.random(0, 600-20);
		blackBlock.y = 0 - 20;
		blackBlock.width = 20;
		blackBlock.height = 20;
		lastDropTime = TimeUtils.nanoTime();
		return blackBlock;
	}

	public Rectangle spawnBlackBlockRight () {
		Rectangle blackBlock = new Rectangle();
		blackBlock.y = MathUtils.random(0, 600-20);
		blackBlock.x = 0 - 20;
		blackBlock.width = 20;
		blackBlock.height = 20;
		lastDropTime = TimeUtils.nanoTime();
		return blackBlock;
	}

	public Rectangle spawnBlackBlockLeft () {
		Rectangle blackBlock = new Rectangle();
		blackBlock.y = MathUtils.random(0, 600-20);
		blackBlock.x = 600;
		blackBlock.width = 20;
		blackBlock.height = 20;
		lastDropTime = TimeUtils.nanoTime();
		return blackBlock;
	}
}
