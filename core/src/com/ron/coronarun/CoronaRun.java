package com.ron.coronarun;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoronaRun extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	int manState = 0;
	int pause = 0;
	float gravity = 0.2f;
	float velocity = 0;
	int manY = 0;
	Rectangle manRectangle;
	BitmapFont font;
	Texture dizzy;
	int score = 0;
	int gameState = 0;

	Random random;

	ArrayList<Integer> coinXs = new ArrayList<Integer>();
	ArrayList<Integer> coinYs = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangles =  new ArrayList<Rectangle>();
	Texture coin;
	int coinCount;

	ArrayList<Integer> coronaXs = new ArrayList<Integer>();
	ArrayList<Integer> coronaYs = new ArrayList<Integer>();
	ArrayList<Rectangle> coronaRectangles =  new ArrayList<Rectangle>();
	Texture corona;
	int coronaCount;


	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");

		manY = Gdx.graphics.getHeight() / 2;

		coin = new Texture("coin.png");
		corona = new Texture("corona.png");
		random = new Random();

		dizzy = new Texture("dizzy-1.png");

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
	}

	public void makeCoin() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	public void makecorona() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coronaYs.add((int)height);
		coronaXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if (gameState == 1) {
			// GAME IS LIVE
			// corona
			if (coronaCount < 250) {
				coronaCount++;
			} else {
				coronaCount = 0;
				makecorona();
			}

			coronaRectangles.clear();
			for (int i=0;i < coronaXs.size();i++) {
				batch.draw(corona, coronaXs.get(i), coronaYs.get(i));
				coronaXs.set(i, coronaXs.get(i) - 8);
				coronaRectangles.add(new Rectangle(coronaXs.get(i), coronaYs.get(i), corona.getWidth(), corona.getHeight()));
			}

			// COINS
			if (coinCount < 100) {
				coinCount++;
			} else {
				coinCount = 0;
				makeCoin();
			}

			coinRectangles.clear();
			for (int i=0;i < coinXs.size();i++) {
				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				coinXs.set(i, coinXs.get(i) - 4);
				coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
			}

			if (Gdx.input.justTouched()) {
				velocity = -10;
			}

			if (pause < 8) {
				pause++;
			} else {
				pause = 0;
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}

			velocity += gravity;
			manY -= velocity;

			if (manY <= 0) {
				manY = 0;
			}
		} else if (gameState == 0) {
			// Waitng to start
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}
		} else if (gameState == 2) {
			// GAME OVER
			if (Gdx.input.justTouched()) {
				gameState = 1;
				manY = Gdx.graphics.getHeight() / 2;
				score = 0;
				velocity = 0;
				coinXs.clear();
				coinYs.clear();
				coinRectangles.clear();
				coinCount = 0;
				coronaXs.clear();
				coronaYs.clear();
				coronaRectangles.clear();
				coronaCount = 0;
			}
		}

		if (gameState == 2) {
			batch.draw(dizzy, Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		} else {
			batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		}
		manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY, man[manState].getWidth(), man[manState].getHeight());

		for (int i=0; i < coinRectangles.size();i++) {
			if (Intersector.overlaps(manRectangle, coinRectangles.get(i))) {
				score++;

				coinRectangles.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}

		for (int i=0; i < coronaRectangles.size();i++) {
			if (Intersector.overlaps(manRectangle, coronaRectangles.get(i))) {
				Gdx.app.log("corona!", "Collision!");
				gameState = 2;
			}
		}

		font.draw(batch, String.valueOf(score),100,200);

		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
