package mx.kenzie.centurion;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Arguments implements Iterable<Object> {

    private final ArrayList<Object> values;

    public Arguments(Object... values) {
        this.values = new ArrayList<>(List.of(values));
    }

    @SuppressWarnings("unchecked")
    public <Type> Type get(int index) {
        if (index >= values.size()) return null;
        return (Type) values.get(index);
    }

    @NotNull
    @Override
    public Iterator<Object> iterator() {
        return values.iterator();
    }

}
