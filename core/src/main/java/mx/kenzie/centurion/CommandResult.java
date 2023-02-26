package mx.kenzie.centurion;

public enum CommandResult implements Result {
    /**
     * The command passed successfully, meeting expected conditions.
     */
    PASSED(true, true),
    /**
     * No behaviour was met, the command fell through to an empty lapse function.
     */
    NO_BEHAVIOUR(false, true),
    /**
     * This result is special - it falls back to the command parser rather than exiting as a failure.
     * This can be used to scrutinise inputs outside an argument matcher.
     */
    WRONG_INPUT(false, false),
    /**
     * This result is special - it causes the parser to fall straight to the `lapse` option.
     */
    LAPSE(false, false),
    /**
     * The command passed successfully, but it hit the lapse criteria (whether by fault or by design.)
     */
    LAPSED(true, true),
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
