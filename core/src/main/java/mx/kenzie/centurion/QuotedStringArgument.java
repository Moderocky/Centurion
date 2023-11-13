package mx.kenzie.centurion;

public class QuotedStringArgument extends TypedArgument<String> {

    protected final char quote;

    public QuotedStringArgument() {
        this('"');
    }

    public QuotedStringArgument(char quote) {
        super(String.class);
        this.label = "quote";
        this.quote = quote;
    }

    @Override
    public ParseResult read(String input) {
        final boolean isQuoted = input.indexOf(quote) == 0;
        if (!isQuoted) return null;
        int next, start = 1;
        do {
            next = input.indexOf(quote, start);
            if (next == -1) return null;
            ++next;
            if (input.length() <= next || input.charAt(next) == ' ') break;
            start = next;
        } while (true);
        final String selected = input.substring(0, next), rest = input.substring(next);
        return new ParseResult(selected.trim(), rest.stripLeading());
    }

    @Override
    public boolean matches(String input) {
        return input.length() > 1 && input.charAt(0) == quote && input.charAt(input.length() - 1) == quote;
    }

    @Override
    public String parse(String input) {
        return input.substring(1, input.length() - 1);
    }

    @Override
    public int weight() {
        return super.weight() + 5;
    }

    @Override
    public String[] possibilities() {
        final String[] strings = super.possibilities();
        if (strings != null && strings.length > 0) return strings;
        final Command<?>.Context context = Command.getContext();
        if (context == null) return new String[]{Character.toString(quote)};
        final String input = context.getRawInput();
        if (input.endsWith(" ")) return new String[]{Character.toString(quote)};
        final String[] args = input.split(" ");
        final String source = args[args.length - 1];
        if (source.endsWith("\"") && source.startsWith("\"")) return new String[]{source};
        return new String[]{source + quote};
    }

}
