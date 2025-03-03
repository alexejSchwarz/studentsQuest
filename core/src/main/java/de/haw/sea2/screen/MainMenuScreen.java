package de.haw.sea2.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;

import de.haw.sea2.StudentsQuest;

public class MainMenuScreen implements Screen {

    private final StudentsQuest context;

    //TODO placeholder apply custom font at some point
    private final BitmapFont font;


    public MainMenuScreen(StudentsQuest context) {
        this.context = context;
        this.font = new BitmapFont();
    }

    /**
     * Called when this screen becomes the current screen for a Game
     */
    @Override
    public void show() {
        // eventuell hier oder im ctor

        //font has 15pt, but we need to scale it to our viewport by ratio of viewport height to screen height
        this.font.setUseIntegerPositions(false);
        // default scled 15p                                scaled 30p
        this.font.getData().setScale((this.context.viewport.getWorldHeight() / Gdx.graphics.getHeight()) * 2);
        this.font.setColor(Color.WHITE);
    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(Color.BLACK);
        this.context.viewport.apply();
        this.context.getSpriteBatch().setProjectionMatrix(this.context.viewport.getCamera().combined);

        this.context.getSpriteBatch().begin();

        this.font.draw(this.context.getSpriteBatch(), "StudentQuest",
            (this.context.viewport.getWorldWidth() / 2) - 1f, (this.context.viewport.getWorldHeight() / 2) + 1f
        );
        this.font.draw(this.context.getSpriteBatch(), "Click any Key to start!",
            (this.context.viewport.getWorldWidth() / 2) - 2f, (this.context.viewport.getWorldHeight() / 2) - 1f
        );

        this.context.getSpriteBatch().end();

        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            //TODO change to LoadingScreen later, do initial asset loading there with progress bar in background
            this.context.setScreen(new GameScreen(this.context));
            //TODO apply correct Logging
            System.out.println("dispose from MainMenu instance was called!");
            dispose();
        }

    }

    @Override
    public void resize(int width, int height) {
        this.context.viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        this.font.dispose();
    }
}
