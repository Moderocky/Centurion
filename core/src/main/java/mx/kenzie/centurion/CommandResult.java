package mx.kenzie.centurion;

public enum CommandResult implements Result {
    PASSED(true),
    NO_BEHAVIOUR(false),
    FAILED_UNKNOWN(false),
    FAILED_EXCEPTION(false);
    public final boolean successful;

    CommandResult(boolean successful) {
        this.successful = successful;
    }

    @Override
    public CommandResult type() {
        return this;
    }

    @Override
    public Throwable error() {
        return null;
    }
}
