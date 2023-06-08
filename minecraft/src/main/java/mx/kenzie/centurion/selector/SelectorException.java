package mx.kenzie.centurion.selector;

public class SelectorException extends RuntimeException {

    private boolean quiet;

    public SelectorException() {
        super();
    }

    public SelectorException(String message) {
        super(message);
    }

    public SelectorException(String message, boolean quiet) {
        super(message);
        this.quiet = quiet;
    }

    public SelectorException(String message, Throwable cause) {
        super(message, cause);
    }

    public SelectorException(Throwable cause) {
        super(cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        if (quiet) return this;
        return super.fillInStackTrace();
    }

}
