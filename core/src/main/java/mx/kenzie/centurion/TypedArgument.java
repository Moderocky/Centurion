package mx.kenzie.centurion;

public abstract class TypedArgument<Type> implements Argument<Type> {

    private final Class<Type> type;

    public TypedArgument(Class<Type> type) {
        this.type = type;
    }

    @Override
    public String label() {
        return type.getSimpleName().toLowerCase();
    }

}
