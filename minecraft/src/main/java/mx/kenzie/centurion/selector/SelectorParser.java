package mx.kenzie.centurion.selector;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectorParser<Type> {

    protected final String input;
    protected final Universe<Type> universe;

    public SelectorParser(String input, Universe<Type> universe) {
        this.input = input;
        this.universe = universe;
    }

    protected Selector.PositionResult suggest() {
        if (input == null) return new Selector.PositionResult(Caret.OTHER);
        final Set<String> set = new HashSet<>();
        final StringReader reader = new StringReader(input);
        final StringBuilder correct = new StringBuilder();
        try {
            //<editor-fold desc="Suggest Finders" defaultstate="collapsed">
            final int first = reader.read();
            if (first != '@' && first != -1) return new Selector.PositionResult(Caret.OTHER);
            else if (first == -1 || !this.validateFinder(reader)) {
                for (final Finder<? extends Type> finder : universe.finders()) set.add("@" + finder.key());
                return new Selector.PositionResult(Caret.START, set.toArray(new String[0]));
            }
            //</editor-fold>
            //<editor-fold desc="Suggest Criteria" defaultstate="collapsed">
            switch (reader.read()) {
                case -1:
                    return new Selector.PositionResult(Caret.AFTER_START, input + "[");
                case '[':
                    break;
                default:
                    return new Selector.PositionResult(Caret.OTHER);
            }
            correct.append(input, 0, input.indexOf('[') + 1);
            Caret caret;
            while (true) {
                caret = Caret.IN_KEY;
                final StringBuilder antecedent = new StringBuilder();
                do {
                    final int c = reader.read();
                    if (c == -1) {
                        //<editor-fold desc="Suggest Keys" defaultstate="collapsed">
                        final String key = antecedent.toString();
                        for (final Criterion<? extends Type, ?> criterion : universe.criteria()) {
                            if (!criterion.label().startsWith(key)) continue;
                            if (criterion.label().length() == key.length())
                                return new Selector.PositionResult(Caret.AFTER_KEY, correct + key + '=');
                            else {
                                set.add(correct + criterion.label() + '=');
                            }
                        }
                        return new Selector.PositionResult(Caret.IN_KEY, set.toArray(new String[0]));
                        //</editor-fold>
                    } else if (c == '=') break;
                    antecedent.append((char) c);
                } while (true);
                final String label = antecedent.toString();
                if (label.isBlank()) {
                    //<editor-fold desc="Suggest Keys" defaultstate="collapsed">
                    for (final Criterion<? extends Type, ?> criterion : universe.criteria())
                        set.add(correct + criterion.label() + '=');
                    return new Selector.PositionResult(Caret.IN_KEY, set.toArray(new String[0]));
                    //</editor-fold>
                }
                caret = Caret.IN_VALUE;
                correct.append(label).append('=');
                final StringBuilder consequent = new StringBuilder();
                boolean invert = false;
                do {
                    final int c = reader.read();
                    if (c == '!' && !invert && consequent.toString().isBlank()) {
                        correct.append('!');
                        invert = true;
                        continue;
                    } else if (c == -1) {
                        //<editor-fold desc="Suggest Values" defaultstate="collapsed">
                        final String value = consequent.toString();
                        if (value.isBlank()) {
                            for (final Criterion<? extends Type, ?> criterion : universe.criteria()) {
                                if (!criterion.label().equals(label)) continue;
                                for (final String possibility : criterion.argument().possibilities()) {
                                    set.add(correct + possibility);
                                    if (!invert) set.add(correct.toString() + '!' + possibility);
                                }
                            }
                            if (set.isEmpty()) set.add(correct.toString() + '!');
                        }
                        for (final Criterion<? extends Type, ?> criterion : universe.criteria()) {
                            if (!criterion.label().equals(label)) continue;
                            for (final String possibility : criterion.argument().possibilities()) {
                                if (possibility.startsWith(value)) set.add(correct + possibility);
                            }
                        }
                        for (final Criterion<? extends Type, ?> criterion : universe.criteria()) {
                            if (!criterion.label().equals(label)) continue;
                            if (!criterion.matches(value)) continue;
                            return new Selector.PositionResult(Caret.AFTER_VALUE, input + "]", input + ",");
                        }
                        return new Selector.PositionResult(Caret.IN_VALUE, set.toArray(new String[0]));
                        //</editor-fold>
                    } else if (c == ']') {
                        return new Selector.PositionResult(Caret.OTHER);
                    } else if (c == ',') break;
                    consequent.append((char) c);
                } while (true);
                final String input = consequent.toString();
                if (input.isBlank()) {
                    //<editor-fold desc="Suggest Values" defaultstate="collapsed">
                    for (final Criterion<? extends Type, ?> criterion : universe.criteria()) {
                        if (!criterion.label().equals(label)) continue;
                        for (final String possibility : criterion.argument().possibilities()) {
                            set.add(correct + possibility);
                        }
                    }
                    return new Selector.PositionResult(Caret.IN_VALUE, set.toArray(new String[0]));
                    //</editor-fold>
                }
                correct.append(input).append(',');
            }
            //</editor-fold>
        } catch (IOException exception) {
            return new Selector.PositionResult(Caret.OTHER);
        }
    }

    @SuppressWarnings("unchecked")
    public boolean validate() {
        final StringReader reader = new StringReader(input);
        try {
            if (reader.read() != '@') return false;
            if (!this.validateFinder(reader)) return false;
            return this.validateFilters(reader);
        } catch (IOException exception) {
            return false;
        }
    }

    boolean validateFinder(StringReader reader) throws IOException {
        final String label = readLabel(reader);
        if (label.isBlank()) return false;
        for (final Finder<? extends Type> test : universe.finders()) if (label.equals(test.key())) return true;
        return false;
    }

    boolean validateFilters(StringReader reader) throws IOException {
        switch (reader.read()) {
            case -1:
                return true;
            case '[':
                break;
            default:
                return false;
        }
        read:
        while (true) {
            final StringBuilder antecedent = new StringBuilder();
            do {
                final int c = reader.read();
                if (c == -1) return false;
                else if (c == '=') break;
                antecedent.append((char) c);
            } while (true);
            final String label = antecedent.toString();
            if (label.isBlank()) return false;
            final StringBuilder consequent = new StringBuilder();
            do {
                final int c = reader.read();
                if (c == -1) return false;
                else if (c == ']') return true;
                else if (c == ',') break;
                consequent.append((char) c);
            } while (true);
            final String input = consequent.toString();
            if (input.isBlank()) return false;
            for (final Criterion<? extends Type, ?> criterion : universe.criteria()) {
                if (!criterion.label().equals(label)) continue;
                if (!criterion.matches(input)) continue;
                continue read;
            }
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public <Result extends Type> Selector<Result> parse() throws SelectorException {
        final StringReader reader = new StringReader(input);
        try {
            if (reader.read() != '@') throw new SelectorException("Selector must start with @");
            final Finder<? extends Type> finder = this.readFinder(reader);
            final Filter<? extends Type>[] criteria = this.readFilters(reader);
            return new Selector<>((Finder<Result>) finder, (Filter<Result>[]) criteria);
        } catch (IOException exception) {
            throw new SelectorException(exception);
        }
    }

    private Filter<? extends Type>[] readFilters(StringReader reader) throws IOException {
        final List<Filter<? extends Type>> list = new ArrayList<>();
        switch (reader.read()) {
            case -1:
                return new Filter[0];
            case '[':
                break;
            default:
                throw new SelectorException("Wrong character expecting opening read.");
        }
        boolean read = true;
        while (read) {
            final StringBuilder antecedent = new StringBuilder();
            do {
                final int c = reader.read();
                if (c == -1) throw new SelectorException("Reached premature end of selector.");
                else if (c == '=') break;
                antecedent.append((char) c);
            } while (true);
            final String label = antecedent.toString();
            if (label.isBlank()) break;
            final StringBuilder consequent = new StringBuilder();
            do {
                final int c = reader.read();
                if (c == -1) throw new SelectorException("Reached premature end of selector.");
                else if (c == ']') {
                    read = false;
                    break;
                } else if (c == ',') break;
                consequent.append((char) c);
            } while (true);
            final String string = consequent.toString(), input;
            if (string.isBlank()) break;
            final boolean invert = string.charAt(0) == '!';
            input = invert ? string.substring(1) : string;
            if (input.isBlank()) break;
            for (final Criterion<? extends Type, ?> criterion : universe.criteria()) {
                if (!criterion.label().equals(label)) continue;
                if (!criterion.matches(input)) continue;
                list.add(criterion.filter(input, invert));
            }
        }
        return list.toArray(new Filter[0]);
    }

    private Finder<? extends Type> readFinder(StringReader reader) throws IOException {
        final String label = readLabel(reader);
        for (final Finder<? extends Type> test : universe.finders()) if (label.equals(test.key())) return test;
        throw new SelectorException("No finder was matched for " + label, true);
    }

    private String readLabel(StringReader reader) throws IOException {
        final StringBuilder builder = new StringBuilder();
        do {
            reader.mark(4);
            final int c = reader.read();
            if (c == -1) break;
            else if (c == '[') {
                reader.reset();
                break;
            }
            builder.append((char) c);
        } while (true);
        return builder.toString();
    }

}
