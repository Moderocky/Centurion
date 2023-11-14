package mx.kenzie.centurion;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Objects;

import static mx.kenzie.centurion.Arguments.*;

public class AnyArgumentTest extends Command<TestSender> {

    private static final AnyArgument<?> NOTHING = new AnyArgument<>(Object.class);
    private static final AnyArgument<String> TEXT = AnyArgument.of(STRING, QUOTE_STRING);
    private static final AnyArgument<Integer> INT = new AnyArgument<>(Integer.class)
        .with(INTEGER).with(DOUBLE, Double::intValue);

    @BeforeClass
    public static void setup() { // warm up
        assert new AnyArgumentTest().behaviour != null;
        assert new AnyArgumentTest().execute(null, "") != null;
        assert new AnyArgumentTest().execute(null, "simple \"blob\"") != null;
        assert new AnyArgumentTest().execute(null, "simple blob") != null;
        assert new AnyArgumentTest().execute(null, "number 10.4") != null;
        assert new AnyArgumentTest().execute(null, "number -5") != null;
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
    public void simpleString() {
        final String input = "test simple blob";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert result.type() == CommandResult.PASSED;
        assert Objects.equals("blob", sender.output) : sender.output;
    }

    @Test
    public void quoteString() {
        final String input = "test simple \"blob\"";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert result.type() == CommandResult.PASSED;
        assert Objects.equals("blob", sender.output) : sender.output;
    }

    @Test
    public void numberInteger() {
        final String input = "test number 10";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert result.type() == CommandResult.PASSED;
        assert Objects.equals("10", sender.output) : sender.output;
    }

    @Test
    public void numberDouble() {
        final String input = "test number 10.8";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert result.type() == CommandResult.PASSED;
        assert Objects.equals("10", sender.output) : sender.output;
    }

    @Test
    public void testPatterns() {
        assert Arrays.toString(this.patterns())
            .equals("[test, test nothing <unknown>, test simple <string/quote>, test number <int/number>]")
            : Arrays.toString(this.patterns());
    }

    @Override
    public Behaviour create() {
        return this.command("test")
            .arg("nothing", NOTHING, (sender, arguments) -> {
                assert false;
                return CommandResult.FAILED_UNKNOWN;
            })
            .arg("simple", TEXT, (sender, arguments) -> {
                assert !arguments.isEmpty();
                assert arguments.get(0) instanceof String;
                sender.output = arguments.get(0);
                return CommandResult.PASSED;
            })
            .arg("number", INT, (sender, arguments) -> {
                assert !arguments.isEmpty();
                assert arguments.get(0) instanceof Integer;
                sender.output = arguments.get(0) + "";
                return CommandResult.PASSED;
            })
            .lapse(sender -> {
                sender.output = "lapsed";
                return CommandResult.LAPSED;
            });
    }

}
