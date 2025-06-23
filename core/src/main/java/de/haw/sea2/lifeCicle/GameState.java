package de.haw.sea2.lifeCicle;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

import de.haw.sea2.StudentsQuest;

public enum GameState implements State<StudentsQuest> {
    INIT(),
    RUNNING(),
    OVER(),
    RESTART() {
        @Override
        public void enter(StudentsQuest context) {
            for (Restartable restartable : context.restartables) {
                restartable.restart();
            }
            context.stateMachine.changeState(RUNNING);
        }
    };

    @Override
    public void enter(StudentsQuest context) {

    }

    @Override
    public void update(StudentsQuest context) {

    }

    @Override
    public void exit(StudentsQuest context) {

    }

    @Override
    public boolean onMessage(StudentsQuest entity, Telegram telegram) {
        return false;
    }
}
