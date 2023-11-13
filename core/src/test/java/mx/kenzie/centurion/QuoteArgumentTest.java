package mx.kenzie.centurion;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Objects;

import static mx.kenzie.centurion.Arguments.QUOTE_STRING;

public class QuoteArgumentTest extends Command<TestSender> {

    @BeforeClass
    public static void setup() { // warm up
        assert new QuoteArgumentTest().behaviour != null;
        assert new QuoteArgumentTest().execute(null, "") != null;
        assert new QuoteArgumentTest().execute(null, "quote \"c\"") != null;
        assert new QuoteArgumentTest().execute(null, "after \"c\" test") != null;
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
    public void simpleQuote() {
        final String input = "test quote \"hello\"";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert result.type() == CommandResult.PASSED;
        assert Objects.equals("hello", sender.output) : sender.output;
    }

    @Test
    public void multiWordQuote() {
        final String input = "test quote \"hello there\"";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert result.type() == CommandResult.PASSED;
        assert Objects.equals("hello there", sender.output) : sender.output;
    }

    @Test
    public void multiWordQuoteAfter() {
        final String input = "test after \"general kenobi\" test";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert result.type() == CommandResult.PASSED;
        assert Objects.equals("general kenobi", sender.output) : sender.output;
    }

    @Test
    public void testPatterns() {
        assert Arrays.toString(this.patterns())
            .equals("[test, test quote <quote>, test after <quote> test]") : Arrays.toString(
            this.patterns());
    }

    @Override
    public Behaviour create() {
        return this.command("test")
            .arg("quote", QUOTE_STRING, (sender, arguments) -> {
                assert !arguments.isEmpty();
                sender.output = arguments.get(0);
                return CommandResult.PASSED;
            })
            .arg("after", QUOTE_STRING, "test", (sender, arguments) -> {
                assert !arguments.isEmpty();
                sender.output = arguments.get(0);
                return CommandResult.PASSED;
            })
            .lapse(sender -> {
                sender.output = "lapsed";
                return CommandResult.LAPSED;
            });
    }

}
