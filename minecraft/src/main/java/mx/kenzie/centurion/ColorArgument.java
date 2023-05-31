package mx.kenzie.centurion;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

class ColorArgument extends HashedArg<TextColor> {

    public ColorArgument() {
        super(TextColor.class);
    }

    @Override
    public boolean matches(String input) {
        this.lastHash = input.hashCode();
        this.lastValue = null;
        try {
            return this.parseNew(input) != null;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    @Override
    public TextColor parseNew(String input) {
        final TextColor color = NamedTextColor.NAMES.value(input.toLowerCase());
        return lastValue = color != null ? color : TextColor.fromHexString(input);
    }

    @Override
    public String[] possibilities() {
        if (possibilities.length > 0) return possibilities;
        return NamedTextColor.NAMES.keys().toArray(new String[0]);
    }

}
