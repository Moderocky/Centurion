package mx.kenzie.centurion;

public enum CommandResult implements Result {
    PASSED(true),
    NO_BEHAVIOUR(false),
    /**
     * This result is special - it falls back to the command parser rather than exiting as a failure.
     * This can be used to scrutinise inputs outside an argument matcher.
     */
    WRONG_INPUT(false),
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
