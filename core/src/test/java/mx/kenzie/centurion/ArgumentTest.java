package mx.kenzie.centurion;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Objects;

import static mx.kenzie.centurion.Arguments.ARGUMENT;
import static mx.kenzie.centurion.Arguments.PATTERN;

public class ArgumentTest extends Command<TestSender> {

    @BeforeClass
    public static void setup() { // warm up
        assert new ArgumentTest().behaviour != null;
        assert new ArgumentTest().execute(null, "") != null;
        assert new ArgumentTest().execute(new TestSender(), "general") != null;
        assert new ArgumentTest().execute(new TestSender(), "test arbitrary input").successful();
        assert new ArgumentTest().execute(new TestSender(), "test argument int").successful();
        assert new ArgumentTest().execute(new TestSender(), "test argument hello there").type() == CommandResult.LAPSED;
    }

    @Test
    public void lapse() {
        final String input = "test arbitrary input";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert result.type() == CommandResult.LAPSED;
        assert Objects.equals("lapsed", sender.output) : sender.output;
    }

    @Test
    public void pattern() {
        final String input = "test pattern argument <argument>";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert result.type() == CommandResult.PASSED;
        assert Objects.equals("argument <argument>", sender.output) : sender.output;
    }

    @Test
    public void self() {
        final Command<TestSender> command = new Command<>() {
            @Override
            public Command<TestSender>.Behaviour create() {
                return command("test").arg(PATTERN, (sender, arguments) -> {
                    sender.output = "yes";
                    return CommandResult.PASSED;
                });
            }
        };
        final String input = "test <pattern>";
        final TestSender sender = new TestSender();
        final Result result = command.execute(sender, input);
        assert result.successful();
        assert result.type() == CommandResult.PASSED;
        assert Objects.equals("yes", sender.output) : sender.output;
        assert command.patterns().length == 1 : Arrays.toString(command.patterns());
        assert command.patterns()[0].equals("test <pattern>");
    }

    @Test
    public void argument() {
        final String input = "test argument int";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert result.type() == CommandResult.PASSED;
        assert Objects.equals("15", sender.output) : sender.output;
    }

    @Test
    public void testPatterns() {
        assert Arrays.toString(this.patterns())
            .equals("[test, test argument <argument>, test literal, test pattern <pattern>]") : Arrays.toString(
            this.patterns());
    }

    @Override
    public Behaviour create() {
        return this.command("test")
            .arg("literal", (sender, arguments) -> {
                assert arguments.isEmpty();
                sender.output = "literal";
                return CommandResult.PASSED;
            })
            .arg("pattern", PatternArgument.of(this), (sender, arguments) -> {
                assert !arguments.isEmpty();
                sender.output = arguments.get(0).toString().trim();
                return CommandResult.PASSED;
            })
            .arg("argument", ARGUMENT, (sender, arguments) -> {
                assert !arguments.isEmpty();
                sender.output = String.valueOf(arguments.<Argument<Integer>>get(0).parse("10") + 5);
                return CommandResult.PASSED;
            })
            .lapse(sender -> {
                sender.output = "lapsed";
                return CommandResult.LAPSED;
            });
    }

}
