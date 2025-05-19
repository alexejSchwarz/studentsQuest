package de.haw.sea2.view.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;

import de.haw.sea2.StudentsQuest;

public class StageUtils {

    public static void prepareAndDraw(StudentsQuest context, float delta) {
        ScreenUtils.clear(Color.BLACK);
        context.viewport.apply();

        Stage stage = context.getStage();
        stage.getBatch().setProjectionMatrix(context.viewport.getCamera().combined);
        stage.act(delta);
        stage.draw();
    }
}
