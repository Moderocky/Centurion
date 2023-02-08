package mx.kenzie.centurion;

public enum CommandResult implements Result {
    PASSED(true, true),
    NO_BEHAVIOUR(false, true),
    /**
     * This result is special - it falls back to the command parser rather than exiting as a failure.
     * This can be used to scrutinise inputs outside an argument matcher.
     */
    WRONG_INPUT(false, false),
    LAPSE(false, false),
    FAILED_UNKNOWN(false, true),
    FAILED_EXCEPTION(false, true);
    public final boolean successful, endParsing;

    CommandResult(boolean successful, boolean endParsing) {
        this.successful = successful;
        this.endParsing = endParsing;
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
