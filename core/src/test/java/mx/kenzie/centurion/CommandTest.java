package mx.kenzie.centurion;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Objects;

import static mx.kenzie.centurion.Arguments.*;

public class CommandTest extends Command<TestSender> {

    @BeforeClass
    public static void setup() { // warm up
        assert new CommandTest().behaviour != null;
        assert new CommandTest().execute(null, "") != null;
        assert new CommandTest().execute(new TestSender(), "general") != null;
        assert !new CommandTest().execute(null, "test general").successful();
        assert !new CommandTest().execute(null, "test blob").successful();
    }

    @Test
    public void context() {
        final Object sender = "Foo";
        assert Command.getContext() == null;
        final Command<Object> command = new Command<>() {
            @Override
            public Command<Object>.Behaviour create() {
                return command("blob").arg("test", (o, arguments) -> {
                    assert Command.getContext() != null;
                    assert Command.getContext().getSender() == sender;
                    return Command.getContext().getSender() == sender ? CommandResult.PASSED : CommandResult.FAILED_UNKNOWN;
                });
            }
        };
        assert Command.getContext() == null;
        final Result result = command.execute(sender, "blob test");
        assert Command.getContext() == null;
        assert result != null;
        assert result.successful();
        assert result.type() == CommandResult.PASSED;
    }

    @Test
    public void basic() {
        final String input = "test";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert Objects.equals("lapsed", sender.output) : sender.output;
    }

    @Test
    public void singleArgument() {
        final String input = "test hello";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert Objects.equals("hello", sender.output) : sender.output;
    }

    @Test
    public void intArgument() {
        final String input = "test hello 12";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert Objects.equals("int 12", sender.output) : sender.output;
    }

    @Test
    public void pluralArgument() {
        final String input = "test hello beans on toast";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert Objects.equals("greedy beans on toast", sender.output) : sender.output;
        this.execute(sender, "test hello a b c d e");
        assert Objects.equals("greedy a b c d e", sender.output) : sender.output;
    }

    @Test
    public void twoArgument() {
        final String input = "test hello there";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert Objects.equals("general kenobi", sender.output) : sender.output;
    }

    @Test
    public void optionalArgumentMet() {
        final String input = "general kenobi";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert Objects.equals("kenobi", sender.output) : sender.output;
    }

    @Test
    public void optionalArgumentNotMet() {
        final String input = "general";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert Objects.equals(null, sender.output) : sender.output;
    }

    @Test
    public void wrongInputFallThrough() {
        final String input = "test hello 12";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert !Objects.equals("bad", sender.output) : sender.output;
        assert Objects.equals("int 12", sender.output) : sender.output;
        this.execute(sender, "test hello -4");
        assert Objects.equals("int -4", sender.output) : sender.output;
    }

    @Test
    public void inputArgument() {
        final String input = "test hello beans";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert Objects.equals("beans", sender.output) : sender.output;
    }

    @Test
    public void multiInputArgument() {
        final String input = "test blob 1 true 2.0";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful() : result.error().getMessage();
        assert Objects.equals("lot 1 2.0", sender.output) : sender.output;
    }

    @Test
    public void optionalLapseArgument() {
        final String input = "test blob";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert Objects.equals("lot null false", sender.output) : sender.output;
    }

    @Test
    public void innerInput() {
        final String input = "test first 64 second";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert Objects.equals("middle 64", sender.output) : sender.output;
    }

    @Test
    public void stressTestGreedy() {
        final TestSender sender = new TestSender();
        assert this.execute(sender, "hello hello there").successful();
        assert Objects.equals("greedy hello there", sender.output) : sender.output;
        assert this.execute(sender, "hello c there").successful();
        assert Objects.equals("greedy c there", sender.output) : sender.output;
        assert this.execute(sender, "test hello c there").successful();
        assert Objects.equals("greedy c there", sender.output) : sender.output;
        assert this.execute(sender, "test hello blob blob blob blob blob").successful();
        assert Objects.equals("greedy blob blob blob blob blob", sender.output) : sender.output;
        assert this.execute(sender, "test hello 10").successful();
        assert Objects.equals("int 10", sender.output) : sender.output;
    }

    @Test
    public void testPatterns() {
        assert Arrays.toString(this.patterns()).equals("[test, test hello, test hello 12, test hello there, test hello <int>, test first <int> second, test general [string], test blob [int] [boolean], test hello <string>, test blob <int> <boolean> <number>, test hello <string...>]") : Arrays.toString(this.patterns());
    }

    @Override
    public Behaviour create() {
        final TypedArgument<Integer> optionalInteger = INTEGER.asOptional();
        final TypedArgument<Boolean> optionalBoolean = BOOLEAN.asOptional();
        final TypedArgument<String> optionalString = STRING.asOptional();
        return this.command("test")
            .arg("general", optionalString, (sender, arguments) -> {
                assert !arguments.isEmpty();
                sender.output = arguments.get(0);
                return CommandResult.PASSED;
            })
            .arg("first", INTEGER, "second", (sender, arguments) -> {
                assert !arguments.isEmpty();
                sender.output = "middle " + arguments.get(0);
                return CommandResult.PASSED;
            })
            .arg("hello", (sender, arguments) -> {
                sender.output = "hello";
                return CommandResult.PASSED;
            })
            .arg("hello", "12", (sender, arguments) -> {
                sender.output = "bad";
                return CommandResult.WRONG_INPUT;
            })
            .arg("hello", INTEGER, (sender, arguments) -> {
                assert !arguments.isEmpty();
                sender.output = "int " + arguments.get(0);
                return CommandResult.PASSED;
            })
            .arg("blob", optionalInteger, optionalBoolean, (sender, arguments) -> {
                assert !arguments.isEmpty() : arguments;
                assert arguments.get(0) == null : arguments.get(0);
                assert arguments.get(optionalInteger) == null : arguments.get(optionalInteger);
                assert arguments.get(1) instanceof Boolean : arguments.get(1);
                assert !arguments.get(optionalBoolean) : arguments.get(optionalBoolean);
                sender.output = "lot " + arguments.get(0) + " " + arguments.get(1);
                return CommandResult.PASSED;
            })
            .arg("blob", INTEGER, BOOLEAN, DOUBLE, (sender, arguments) -> {
                assert !arguments.isEmpty();
                assert arguments.get(0) instanceof Integer;
                assert arguments.get(Integer.class) != null;
                assert arguments.get(INTEGER) != null;
                assert arguments.get(1) instanceof Boolean;
                assert arguments.get(BOOLEAN) == arguments.get(1);
                assert arguments.get(Boolean.class) == arguments.get(1);
                assert arguments.get(2) instanceof Double;
                assert arguments.get(DOUBLE) == arguments.get(2);
                assert arguments.get(Double.class) == arguments.get(2);
                sender.output = "lot " + arguments.get(0) + " " + arguments.get(2);
                return CommandResult.PASSED;
            })
            .arg("hello", GREEDY_STRING, (sender, arguments) -> {
                assert !arguments.isEmpty();
                sender.output = "greedy " + arguments.get(0);
                return CommandResult.PASSED;
            })
            .arg("hello", String.class, (sender, arguments) -> {
                assert !arguments.isEmpty();
                sender.output = arguments.get(0);
                return CommandResult.PASSED;
            })
            .arg("hello", "there", (sender, arguments) -> {
                sender.output = "general kenobi";
                return CommandResult.PASSED;
            })
            .lapse(sender -> {
                sender.output = "lapsed";
                return CommandResult.PASSED;
            });
    }

}
