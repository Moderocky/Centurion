package mx.kenzie.centurion.error;

public class CommandGenerationError extends Error {

    public CommandGenerationError() {
        super();
    }

    public CommandGenerationError(String message) {
        super(message);
    }

    public CommandGenerationError(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandGenerationError(Throwable cause) {
        super(cause);
    }

}
