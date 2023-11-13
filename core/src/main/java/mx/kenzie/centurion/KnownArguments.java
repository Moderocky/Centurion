package mx.kenzie.centurion;

public enum KnownArguments implements ArgumentHolder {
    BOOLEAN(Arguments.BOOLEAN),
    INTEGER(Arguments.INTEGER),
    LONG(Arguments.LONG),
    DOUBLE(Arguments.DOUBLE),
    CLASS(Arguments.CLASS),
    STRING(Arguments.STRING),
    GREEDY_STRING(Arguments.GREEDY_STRING),
    QUOTE_STRING(Arguments.QUOTE_STRING),
    ARGUMENT(Arguments.ARGUMENT),
    PATTERN(Arguments.PATTERN);
    private final TypedArgument<?> argument;

    KnownArguments(TypedArgument<?> argument) {this.argument = argument;}

    public TypedArgument<?> getArgument() {
        return argument;
    }
}

interface ArgumentHolder {

    Argument<?> getArgument();

}
