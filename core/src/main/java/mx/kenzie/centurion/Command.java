package mx.kenzie.centurion;

import java.util.*;
import java.util.function.BiFunction;

public abstract class Command<Sender> {

    private static final ThreadLocal<Object> context = new ThreadLocal<>();
    protected final Behaviour behaviour;

    protected Command() {
        this.behaviour = this.create();
    }

    static List<Argument<?>> coerce(Collection<Object> arguments) {
        final List<Argument<?>> list = new ArrayList<>(arguments.size());
        for (Object argument : arguments) {
            if (argument instanceof String string) list.add(new LiteralArgument(string));
            else if (argument instanceof Argument<?> arg) list.add(arg);
            else if (argument == Boolean.class) list.add(Arguments.BOOLEAN);
            else if (argument == String.class) list.add(Arguments.STRING);
            else throw new RuntimeException("Unknown argument acceptor provided. " + argument);
        }
        return list;
    }

    /**
     * Command context is available during the parsing and execution phase, and discarded once complete.
     * This is a thread-safe value and a strong reference.
     */
    @SuppressWarnings("unchecked")
    public static <Sender> Command<Sender>.Context getContext() {
        return (Command<Sender>.Context) context.get();
    }

    protected static void setContext(Command<?>.Context context) {
        Command.context.set(context);
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

    //<editor-fold desc="Input" defaultstate="collapsed">
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
        protected boolean passAllArguments;

        protected Behaviour(String label, String... aliases) {
            this.label = label.toLowerCase();
            this.aliases = new HashSet<>(List.of(aliases));
            this.arguments = new LinkedList<>();
            this.functions = new LinkedHashMap<>();
        }

        private String prepareArguments(String input) {
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
            return input;
        }

        public Behaviour passAllArguments() {
            this.passAllArguments = true;
            return this;
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
            final List<Argument<?>> list = coerce(arguments);
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
            Command.setContext(new Context(sender, input));
            try {
                input = this.prepareArguments(input);
                if (input.isEmpty()) return lapse.apply(sender, new Arguments());
                for (ArgumentContainer argument : arguments) {
                    final Object[] inputs = argument.check(input, passAllArguments);
                    if (inputs == null) continue;
                    final Input<Sender> function = functions.get(argument);
                    assert function != null;
                    final Result result = function.apply(sender, new Arguments(inputs));
                    if (result.type().endParsing) return result;
                    if (result == CommandResult.LAPSE) break;
                }
                return lapse.apply(sender, new Arguments());
            } finally {
                Command.setContext(null);
            }
        }
    }

    public class Context {

        protected final Sender sender;
        protected final String rawInput;

        public Context(Sender sender, String input) {
            this.sender = sender;
            this.rawInput = input;
        }

        public Sender getSender() {
            return sender;
        }

        public String getRawInput() {
            return rawInput;
        }

    }

}
