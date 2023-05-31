package mx.kenzie.centurion;

public class EnumArgument<Type extends Enum<Type>> extends HashedArg<Type> {

    public EnumArgument(Class<Type> type) {
        super(type);
    }

    @Override
    public boolean matches(String input) {
        this.lastHash = input.hashCode();
        this.lastValue = null;
        try {
            return (lastValue = this.parseNew(input)) != null;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    @Override
    public Type parseNew(String input) {
        return Enum.valueOf(type, input.trim().toUpperCase());
    }

    @Override
    public String[] possibilities() {
        if (possibilities != null && possibilities.length > 0) return possibilities;
        final Type[] enums = type.getEnumConstants();
        this.possibilities = new String[enums.length];
        for (int i = 0; i < enums.length; i++) possibilities[i] = enums[i].name().toLowerCase();
        return possibilities;
    }

}
