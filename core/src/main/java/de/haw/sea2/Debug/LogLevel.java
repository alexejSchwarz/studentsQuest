package de.haw.sea2.Debug;

public enum LogLevel {
    DEBUG("DEBUG"),
    INFO("INFO"),
    ERROR("ERROR");

    public final String logLevel;

    LogLevel(String level) {
        this.logLevel = level;
    }

    public String getLogLevel() {
        return this.logLevel;
    }
}
