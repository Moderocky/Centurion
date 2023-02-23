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
        assert result.successful();
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
    public void testPatterns() {
        assert Arrays.toString(this.patterns()).equals("[test, test hello, test hello 12, test hello there, test hello <int>, test first <int> second, test general [string], test blob [int] [boolean], test hello <string>, test blob <int> <boolean> <number>, test hello <string...>]") : Arrays.toString(this.patterns());
    }

    @Override
    public Behaviour create() {
        return this.command("test")
            .arg("general", OPTIONAL_STRING, (sender, arguments) -> {
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
            .arg("blob", OPTIONAL_INTEGER, OPTIONAL_BOOLEAN, (sender, arguments) -> {
                assert !arguments.isEmpty();
                assert arguments.get(0) == null;
                assert arguments.get(INTEGER) == null;
                assert arguments.get(1) instanceof Boolean;
                assert !arguments.get(BOOLEAN);
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
