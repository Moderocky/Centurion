package mx.kenzie.centurion;

public abstract class TypedArgument<Type> implements Argument<Type>, Cloneable {

    public final Class<Type> type;
    protected boolean optional, greedy;
    protected Type lapse = null;
    protected String label;

    public TypedArgument(Class<Type> type) {
        this.type = type;
        this.label = type.getSimpleName().toLowerCase();
    }

    @Override
    public String label() {
        return label;
    }

    @Override
    public boolean optional() {
        return optional;
    }

    @Override
    public boolean plural() {
        return greedy;
    }

    @Override
    public Type lapse() {
        return lapse;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TypedArgument<Type> clone() {
        try {
            return (TypedArgument<Type>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public TypedArgument<Type> asOptional() {
        final TypedArgument<Type> argument = this.clone();
        argument.optional = true;
        return argument;
    }

    public TypedArgument<Type> asPlural() {
        final TypedArgument<Type> argument = this.clone();
        argument.greedy = true;
        return argument;
    }

    public TypedArgument<Type> withLapse(Type lapse) {
        final TypedArgument<Type> argument = this.clone();
        argument.lapse = lapse;
        return argument;
    }

    public TypedArgument<Type> labelled(String label) {
        final TypedArgument<Type> argument = this.clone();
        argument.label = label;
        return argument;

    }

}
