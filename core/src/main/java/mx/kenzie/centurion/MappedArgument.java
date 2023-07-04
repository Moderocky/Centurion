package mx.kenzie.centurion;

import java.util.Map;

public abstract class MappedArgument<Type> extends TypedArgument<Type> implements Argument<Type> {

    private transient Map<String, Type> map;

    public MappedArgument(Class<Type> type) {
        super(type);
    }

    public abstract Map<String, Type> getMap();

    public boolean shouldRecomputeMap() {
        return false;
    }

    @Override
    public boolean matches(String input) {
        if (map == null || this.shouldRecomputeMap()) map = this.getMap();
        return map.containsKey(input);
    }

    @Override
    public Type parse(String input) {
        final Map<String, Type> map = this.map != null ? this.map : this.getMap();
        if (this.shouldRecomputeMap()) this.map = null;
        return map.get(input);
    }

}
