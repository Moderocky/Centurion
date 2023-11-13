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
    PATTERN(Arguments.PATTERN),
    BLOCK_FACE(MinecraftCommand.BLOCK_FACE),
    MATERIAL(MinecraftCommand.MATERIAL),
    ENTITY_TYPE(MinecraftCommand.ENTITY_TYPE),
    GAME_MODE(MinecraftCommand.GAME_MODE),
    COLOR(MinecraftCommand.COLOR),
    BLOCK_DATA(MinecraftCommand.BLOCK_DATA),
    PLAYER(MinecraftCommand.PLAYER),
    KNOWN_PLAYER(MinecraftCommand.KNOWN_PLAYER),
    ANY_PLAYER(MinecraftCommand.ANY_PLAYER),
    INEQUALITY(MinecraftCommand.INEQUALITY),
    SELECTOR(MinecraftCommand.SELECTOR),
    WORLD(MinecraftCommand.WORLD),
    UUID(MinecraftCommand.UUID),
    KEY(MinecraftCommand.KEY),
    MATERIAL_TAG(MinecraftCommand.MATERIAL_TAG),
    ITEM_TAG(MinecraftCommand.ITEM_TAG),
    ENTITY_TAG(MinecraftCommand.ENTITY_TAG),
    RELATIVE_NUMBER(MinecraftCommand.RELATIVE_NUMBER),
    LOCAL_NUMBER(MinecraftCommand.LOCAL_NUMBER),
    VECTOR(MinecraftCommand.VECTOR),
    LOCATION(MinecraftCommand.LOCATION),
    OFFSET(MinecraftCommand.OFFSET),
    LOCAL_OFFSET(MinecraftCommand.LOCAL_OFFSET);
    private final TypedArgument<?> argument;

    KnownArguments(TypedArgument<?> argument) {this.argument = argument;}

    public TypedArgument<?> getArgument() {
        return argument;
    }
}

