package mx.kenzie.centurion;

import org.bukkit.NamespacedKey;

import java.util.regex.Pattern;

class KeyArgument extends TypedArgument<NamespacedKey> {
    private static final Pattern VALID_NAMESPACE = Pattern.compile("[a-z0-9._-]+");
    private static final Pattern VALID_KEY = Pattern.compile("[a-z0-9/._-]+");

    public KeyArgument() {
        super(NamespacedKey.class);
    }

    @Override
    public boolean matches(String input) {
        final String namespace, key;
        if (input.indexOf(NamespacedKey.DEFAULT_SEPARATOR) > 0) {
            final String[] parts = input.trim().split(":");
            namespace = parts[0];
            key = parts[1];
        } else {
            namespace = "minecraft";
            key = input.trim();
        }
        return VALID_KEY.matcher(key).matches() && VALID_NAMESPACE.matcher(namespace).matches();
    }

    @Override
    public NamespacedKey parse(String input) {
        return NamespacedKey.fromString(input.trim());
    }

}
