package mx.kenzie.centurion;

public interface Result {

    CommandResult type();

    Throwable error();

    default boolean successful() {
        return this.type().successful;
    }

    record Error(CommandResult type, Throwable error) implements Result {
    }

}
