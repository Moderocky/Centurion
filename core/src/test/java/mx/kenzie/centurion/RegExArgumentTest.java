package mx.kenzie.centurion;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

import static mx.kenzie.centurion.Arguments.REGEX;

public class RegExArgumentTest extends Command<TestSender> {

    @BeforeClass
    public static void setup() { // warm up
        assert new RegExArgumentTest().behaviour != null;
        assert new RegExArgumentTest().execute(null, "") != null;
        assert new RegExArgumentTest().execute(null, "simple .+") != null;
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
    public void regex() {
        final String input = "test simple bl.b";
        final TestSender sender = new TestSender();
        final Result result = this.execute(sender, input);
        assert result.successful();
        assert result.type() == CommandResult.PASSED;
        assert Objects.equals("bl.b", sender.output) : sender.output;
        assert this.execute(sender, "test simple ^.+$").successful();
        assert this.execute(sender, "test simple ^blob?$").successful();
        assert this.execute(sender, "test simple [a-z]*").successful();
        assert !this.execute(sender, "test simple ^foo$").successful();
    }

    @Test
    public void testPatterns() {
        assert Arrays.toString(this.patterns())
            .equals("[test, test simple <regex>]") : Arrays.toString(
            this.patterns());
    }

    @Override
    public Behaviour create() {
        return this.command("test")
            .arg("simple", REGEX, (sender, arguments) -> {
                assert !arguments.isEmpty();
                assert arguments.get(0) instanceof Pattern pattern
                    && pattern.matcher("blob").matches();
                sender.output = arguments.<Pattern>get(0).pattern();
                return CommandResult.PASSED;
            })
            .lapse(sender -> {
                sender.output = "lapsed";
                return CommandResult.LAPSED;
            });
    }

}
