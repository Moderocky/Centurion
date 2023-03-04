package mx.kenzie.centurion;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Objects;

import static mx.kenzie.centurion.Arguments.INTEGER;
import static mx.kenzie.centurion.Arguments.STRING;

public class CompoundArgumentTest extends Command<TestSender> {

    private static final CompoundArgument<String> COMPOUND = new CompoundArgument<>("bean", String.class)
        .arg("hello", "there", arguments -> "general kenobi")
        .arg(INTEGER, "there", arguments -> arguments.get(0) + " kenobi");

    @BeforeClass
    public static void setup() { // warm up
        assert new CommandTest().behaviour != null;
        assert new CommandTest().execute(null, "") != null;
        assert new CommandTest().execute(new TestSender(), "general") != null;
        assert !new CommandTest().execute(null, "test general kenobi").successful();
        assert !new CommandTest().execute(null, "test blob").successful();
        assert !new CommandTest().execute(null, "test general hello there").successful();
    }

    @Test
    public void basic() {
        final String input = "test general kenobi";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert Objects.equals("kenobi", sender.output) : sender.output;
    }

    @Test
    public void simpleCompound() {
        final String input = "test general hello there";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert Objects.equals("general kenobi", sender.output) : sender.output;
    }

    @Test
    public void inputCompound() {
        final String input = "test general 10 there";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert Objects.equals("10 kenobi", sender.output) : sender.output;
    }

    @Test
    public void testPatterns() {
        assert Arrays.toString(this.patterns()).equals("[test, test general <bean>, test general <string>]") : Arrays.toString(this.patterns());
    }

    @Override
    public Behaviour create() {
        return this.command("test")
            .arg("general", STRING, (sender, arguments) -> {
                assert !arguments.isEmpty();
                sender.output = arguments.get(0);
                return CommandResult.PASSED;
            })
            .arg("general", COMPOUND, (sender, arguments) -> {
                assert !arguments.isEmpty();
                sender.output = arguments.get(0);
                return CommandResult.PASSED;
            })
            .lapse(sender -> {
                sender.output = "lapsed";
                return CommandResult.PASSED;
            });
    }
}
