package mx.kenzie.centurion;

import java.util.*;
import java.util.function.BiFunction;

public abstract class Command<Sender> {

    protected final Behaviour behaviour;

    protected Command() {
        this.behaviour = this.create();
    }

    public abstract Behaviour create();

    protected Behaviour command(String label, String... aliases) {
        return new Behaviour(label, aliases);
    }

    public Result execute(Sender sender, String input) {
        return behaviour.execute(sender, input);
    }

    public String[] patterns() {
        return behaviour.patterns();
    }

    @FunctionalInterface
    public interface Input<Sender> extends BiFunction<Sender, Arguments, Result> {

        Result run(Sender sender, Arguments arguments) throws Throwable;

        @Override
        default Result apply(Sender sender, Arguments arguments) {
            try {
                return this.run(sender, arguments);
            } catch (Throwable ex) {
                return new Result.Error(CommandResult.FAILED_EXCEPTION, ex);
            }
        }

    }


    @FunctionalInterface
    public interface EmptyInput<Sender> extends Input<Sender>, BiFunction<Sender, Arguments, Result> {

        Result run(Sender sender) throws Throwable;

        @Override
        default Result run(Sender sender, Arguments arguments) throws Throwable {
            return this.run(sender);
        }

    }

    protected record ArgumentContainer(Argument<?>... arguments) {

        public int weight() {
            int weight = 0;
            for (Argument<?> argument : arguments) weight += argument.weight();
            return weight;
        }

        public int size() {
            return arguments.length;
        }

        public boolean hasInput() {
            for (Argument<?> argument : arguments) if (!argument.literal()) return true;
            return false;
        }

        public boolean hasOptional() {
            for (Argument<?> argument : arguments) if (argument.optional()) return true;
            return false;
        }

        public Object[] check(String input) {
            final List<Object> inputs = new ArrayList<>(8);
            for (Argument<?> argument : arguments) {
                final String part;
                final int space = input.indexOf(' ');
                if (argument.plural() || space < 0) {
                    part = input.trim();
                    input = "";
                } else {
                    part = input.substring(0, space).trim();
                    input = input.substring(space + 1).stripLeading();
                }
                if (part.isEmpty() && argument.optional()) {
                    inputs.add(argument.lapse());
                    continue;
                }
                if (!argument.matches(part)) return null;
                if (argument.literal()) continue;
                inputs.add(argument.parse(part));
            }
            if (!input.isBlank()) return null;
            return inputs.toArray(new Object[0]);
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            for (Argument<?> argument : arguments) {
                builder.append(' ');
                final boolean optional = argument.optional(), literal = argument.literal(), plural = argument.plural();
                if (optional) builder.append('[');
                else if (!literal) builder.append('<');
                builder.append(argument.label());
                if (plural) builder.append("...");
                if (optional) builder.append(']');
                else if (!literal) builder.append('>');
            }
            return builder.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ArgumentContainer that)) return false;
            return Arrays.equals(arguments, that.arguments);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(arguments);
        }

    }

    protected class Execution {
        protected final Input<Sender> function;
        protected final Arguments arguments;

        protected Execution(Input<Sender> function, Arguments arguments) {
            this.function = function;
            this.arguments = arguments;
        }
    }

    @SuppressWarnings("unchecked")
    public class Behaviour {
        public static final Input<?> DEFAULT_LAPSE = (sender, arguments) -> CommandResult.NO_BEHAVIOUR;

        protected final String label;
        protected final Set<String> aliases;
        protected final Map<ArgumentContainer, Input<Sender>> functions;
        protected final List<ArgumentContainer> arguments;
        protected Input<Sender> lapse = (Input<Sender>) DEFAULT_LAPSE;
        protected boolean sorted;
        protected String[] patterns;

        protected Behaviour(String label, String... aliases) {
            this.label = label.toLowerCase();
            this.aliases = new HashSet<>(List.of(aliases));
            this.arguments = new LinkedList<>();
            this.functions = new LinkedHashMap<>();
        }

        protected Execution match(String input) {
            this.sort();
            remove_name:
            {
                if (input.startsWith(label)) {
                    input = input.substring(label.length());
                    break remove_name;
                }
                for (String alias : aliases)
                    if (input.startsWith(alias)) {
                        input = input.substring(alias.length());
                        break remove_name;
                    }
            }
            input = input.stripLeading();
            if (input.isEmpty()) return new Execution(lapse, new Arguments());
            for (ArgumentContainer argument : arguments) {
                final Object[] inputs = argument.check(input);
                if (inputs == null) continue;
                final Input<Sender> function = functions.get(argument);
                assert function != null;
                return new Execution(function, new Arguments(inputs));
            }
            return new Execution(lapse, new Arguments());
        }

        public Behaviour lapse(EmptyInput<Sender> function) {
            if (function == null) lapse = (Input<Sender>) DEFAULT_LAPSE;
            else lapse = function;
            return this;
        }

        public Behaviour arg(Object arg1, Input<Sender> function) {
            return this.arg(List.of(arg1), function);
        }

        public Behaviour arg(Object arg1, Object arg2, Input<Sender> function) {
            return this.arg(List.of(arg1, arg2), function);
        }

        public Behaviour arg(Object arg1, Object arg2, Object arg3, Input<Sender> function) {
            return this.arg(List.of(arg1, arg2, arg3), function);
        }

        public Behaviour arg(Object arg1, Object arg2, Object arg3, Object arg4, Input<Sender> function) {
            return this.arg(List.of(arg1, arg2, arg3, arg4), function);
        }

        public Behaviour arg(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Input<Sender> function) {
            return this.arg(List.of(arg1, arg2, arg3, arg4, arg5), function);
        }

        public Behaviour arg(Collection<Object> arguments, Input<Sender> function) {
            final List<Argument<?>> list = new ArrayList<>(arguments.size());
            for (Object argument : arguments) {
                if (argument instanceof String string) list.add(new LiteralArgument(string));
                else if (argument instanceof Argument<?> arg) list.add(arg);
                else if (argument == Boolean.class) list.add(Arguments.BOOLEAN);
                else if (argument == String.class) list.add(Arguments.STRING);
                else throw new RuntimeException("Unknown argument acceptor provided. " + argument);
            }
            final ArgumentContainer container = new ArgumentContainer(list.toArray(new Argument[0]));
            this.arguments.add(container);
            this.functions.put(container, function);
            this.sorted = false;
            return this;
        }

        protected void sort() {
            if (sorted) return;
            this.sorted = true;
            this.arguments.sort(Comparator.comparing(ArgumentContainer::weight));
            this.patterns = new String[arguments.size() + 1];
            this.patterns[0] = label;
            int index = 0;
            for (ArgumentContainer argument : arguments) patterns[++index] = label + argument.toString();
        }

        public String[] patterns() {
            this.sort();
            return patterns;
        }

        @Override
        public String toString() {
            return "Behaviour{" +
                "label='" + label + '\'' +
                ", aliases=" + aliases +
                ", functions=" + functions +
                ", arguments=" + arguments +
                ", lapse=" + lapse +
                ", sorted=" + sorted +
                ", patterns=" + Arrays.toString(patterns) +
                '}';
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(label, aliases, functions, arguments, lapse, sorted);
            result = 31 * result + Arrays.hashCode(patterns);
            return result;
        }

        public Result execute(Sender sender, String input) {
            this.sort();
            remove_name:
            {
                if (input.startsWith(label)) {
                    input = input.substring(label.length());
                    break remove_name;
                }
                for (String alias : aliases)
                    if (input.startsWith(alias)) {
                        input = input.substring(alias.length());
                        break remove_name;
                    }
            }
            input = input.stripLeading();
            if (input.isEmpty()) return lapse.apply(sender, new Arguments());
            for (ArgumentContainer argument : arguments) {
                final Object[] inputs = argument.check(input);
                if (inputs == null) continue;
                final Input<Sender> function = functions.get(argument);
                assert function != null;
                final Result result = function.apply(sender, new Arguments(inputs));
                if (result.type().endParsing) return result;
                if (result == CommandResult.LAPSE) break;
            }
            return lapse.apply(sender, new Arguments());
        }
    }
}
