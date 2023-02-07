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
        assert Objects.equals("beans on toast", sender.output) : sender.output;
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
    public void inputArgument() {
        final String input = "test hello beans";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert Objects.equals("beans", sender.output) : sender.output;
    }

    @Test
    public void testPatterns() {
        assert Arrays.toString(this.patterns()).equals("[test, test hello, test hello there, test hello <int>, test general [string], test hello <string>, test hello <string...>]") : Arrays.toString(this.patterns());
    }

    @Override
    public Behaviour create() {
        return this.command("test")
            .arg("general", OPTIONAL_STRING, (sender, arguments) -> {
                assert !arguments.isEmpty();
                sender.output = arguments.get(0);
                return CommandResult.PASSED;
            })
            .arg("hello", (sender, arguments) -> {
                sender.output = "hello";
                return CommandResult.PASSED;
            })
            .arg("hello", INTEGER, (sender, arguments) -> {
                assert !arguments.isEmpty();
                sender.output = "int " + arguments.get(0);
                return CommandResult.PASSED;
            })
            .arg("hello", GREEDY_STRING, (sender, arguments) -> {
                assert !arguments.isEmpty();
                sender.output = arguments.get(0);
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
