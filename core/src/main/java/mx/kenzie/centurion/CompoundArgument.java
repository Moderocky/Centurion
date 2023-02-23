package mx.kenzie.centurion;

public abstract class CompoundArgument<Type> implements Argument<Type> {

    protected final Argument<?>[] inputs;

    public CompoundArgument(Argument<?>... inputs) {
        this.inputs = inputs;
    }

    public abstract Type apply(Object... objects);

    @Override
    public boolean matches(String input) {
        return false;
    }

    @Override
    public Type parse(String input) {
        return null;
    }

    @Override
    public String label() {
        return null;
    }

}
