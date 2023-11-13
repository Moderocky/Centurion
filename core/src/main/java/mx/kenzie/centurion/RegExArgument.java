package mx.kenzie.centurion;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegExArgument extends HashedArg<Pattern> {

    public RegExArgument() {
        super(Pattern.class);
        this.label = "regex";
    }

    @Override
    public boolean matches(String input) {
        try {
            return !input.isEmpty() && this.parse(input) != null;
        } catch (PatternSyntaxException ex) {
            return false;
        }
    }

    @Override
    public Pattern parseNew(String input) {
        return Pattern.compile(input);
    }

    @Override
    public int weight() {
        return super.weight() + 5;
    }

}
