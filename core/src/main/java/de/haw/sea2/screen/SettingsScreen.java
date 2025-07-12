package de.haw.sea2.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.haw.sea2.StudentsQuest;
import de.haw.sea2.debug.LogCategory;
import de.haw.sea2.debug.LoggerUtil;
import de.haw.sea2.input.GameKey;
import de.haw.sea2.input.InputManager;
import de.haw.sea2.input.ScreenKeyInputListener;
import de.haw.sea2.parsing.JSONManager;
import de.haw.sea2.paths.AssetPaths;
import de.haw.sea2.view.ui.StageUtils;

//TODO Viewport, Texture Size refactoring noetig. verwendung von logischen Einheiten und nicht Pixel. Keine neue ViewportInstanz, wenn moeglich.
// Generelles Refactoring von Butons UI etc. Standart fuer das Projekt finden. Schauen, ob Unitscale eine Rolle spielt. Prozess: Texturen und deren Formate
// festlegen, damit Art Leute feste vorgaben haben und wir deren Custom Sachen einfach und schnell fuer UI uebernehmen koennen
/**
 * Pause-Screen, der angezeigt wird, wenn das Spiel pausiert wird.
 */
public class SettingsScreen implements Screen, ScreenKeyInputListener {

    private final StudentsQuest context;

    Slider slider;

    Slider.SliderStyle sliderStyle;

    Texture sliderBackground;
    Texture sliderKnob;

    TextureRegionDrawable sliderBackgroundRegion;
    TextureRegionDrawable sliderKnobRegion;

    //Texture Region für den Home Button
    private final TextureRegionDrawable homeButtonRegion;

    //Image Button für den Home Knopf
    private final ImageButton homeButton;

    //Hintergrundbild
    private final Texture background;
    private final Image backgroundImage;

    /**
     * Erstellt einen neuen Settings Screen.
     *
     * @param context Der Spielkontext
     */
    public SettingsScreen(StudentsQuest context) {
        this.context = context;

        sliderBackground = context.getAssetManager().get(AssetPaths.SLIDER_BACKGROUND.getPath());
        sliderBackgroundRegion = new TextureRegionDrawable(sliderBackground);

        sliderKnob = context.getAssetManager().get(AssetPaths.SLIDER_KNOB.getPath());
        sliderKnobRegion = new TextureRegionDrawable(sliderKnob);

        sliderStyle = new Slider.SliderStyle(sliderBackgroundRegion, sliderKnobRegion);

        slider = new Slider(0f, 100f, 1f, false, sliderStyle);

        slider.setValue(JSONManager.getVolume());
        slider.setWidth(sliderBackground.getWidth());
        slider.setHeight(sliderBackground.getHeight());

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                JSONManager.updateVolume(slider.getValue());
                context.getAudioManager().setVolume(JSONManager.getVolume());
            }
        });

        homeButtonRegion = new TextureRegionDrawable(context.getAssetManager().get(AssetPaths.HAUPT_MENU_TEXT_BUTTON.getPath(), Texture.class));

        homeButton = new ImageButton(homeButtonRegion);

        homeButton.setSize(500f,200f);

        background = context.getAssetManager().get(AssetPaths.BACKGROUND_SETTINGS_SCREEN.getPath());
        backgroundImage = new Image(background);
        backgroundImage.setSize(1920f, 1080f);


        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LoggerUtil.log(LogCategory.LOG, this, "Home button pressed");
                context.getScreenManager().showScreen(ScreenType.MAIN_MENU);
            }
        });

        LoggerUtil.log(LogCategory.DEBUG, this, "Settings Screen initialisiert.");
    }

    @Override
    public void show() {
        context.getStage().clear();
        context.getStage().setViewport(new FitViewport(1920f,1080f));

        slider.setPosition((context.getStage().getWidth() / 2f) - slider.getWidth() / 2f, context.getStage().getHeight() / 2f);

        homeButton.setPosition((context.getStage().getWidth() / 2f) - homeButton.getWidth() / 2f,200f);

        context.getInputManager().addKeyInputListener(this);

        context.getStage().addActor(backgroundImage);
        context.getStage().addActor(slider);
        context.getStage().addActor(homeButton);
    }


    @Override
    public void render(float delta) {
        StageUtils.prepareAndDraw(context,delta);
    }

    @Override
    public void resize(int width, int height) {
        context.getStage().getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // Keine Aktion erforderlich
    }

    @Override
    public void resume() {
        // Keine Aktion erforderlich
    }

    @Override
    public void hide() {
        context.getStage().clear();
        context.getStage().setViewport(context.viewport);
        context.getInputManager().removeKeyInputListener(this);
    }

    @Override
    public void dispose() {
        sliderBackground.dispose();
        sliderKnob.dispose();
    }

    @Override
    public void keyDown(InputManager manager, GameKey key) {
        if (key == GameKey.PAUSE) {
            context.getScreenManager().showScreen(ScreenType.MAIN_MENU);
        }
    }

    @Override
    public void keyUp(InputManager manager, GameKey key) {

    }
}
