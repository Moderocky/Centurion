package mx.kenzie.centurion;

import java.util.function.Predicate;

/**
 * This handles number inequality testing.
 * It is primarily intended as an input for selectors, allowing patterns like Minecraft's `distance=..5`
 * but is also available for commands in either standard or Minecraft's format.
 */
public class InequalityArgument extends HashedArg<Predicate<Double>> {

    @SuppressWarnings("unchecked")
    public InequalityArgument() {
        super((Class<Predicate<Double>>) (Object) Predicate.class);
        this.label = "number";
    }

    @Override
    public boolean matches(String input) {
        try {
            this.lastValue = this.parseNew(input);
            this.lastHash = input.hashCode();
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    @Override
    public Predicate<Double> parseNew(String input) {
        if (input.startsWith("..")) {
            final double test = Double.parseDouble(input.substring(2));
            return number -> number <= test;
        } else if (input.startsWith("<")) {
            final double test = Double.parseDouble(input.substring(1));
            return number -> number < test;
        } else if (input.endsWith("..")) {
            final double test = Double.parseDouble(input.substring(0, input.length() - 2));
            return number -> number >= test;
        } else if (input.startsWith(">")) {
            final double test = Double.parseDouble(input.substring(1));
            return number -> number > test;
        } else if (input.contains("..")) {
            final int cut = input.indexOf("..");
            final String start = input.substring(0, cut), end = input.substring(cut + 2);
            final double low = Double.parseDouble(start), high = Double.parseDouble(end);
            return number -> number >= low && number <= high;
        } else {
            final double test = Double.parseDouble(input);
            return number -> number == test;
        }
    }

}
