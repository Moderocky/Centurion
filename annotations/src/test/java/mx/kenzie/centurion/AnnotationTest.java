package mx.kenzie.centurion;

import mx.kenzie.centurion.annotation.Argument;
import mx.kenzie.centurion.annotation.CommandDetails;
import mx.kenzie.centurion.error.CommandGenerationError;
import org.junit.Test;

import static mx.kenzie.centurion.KnownArguments.INTEGER;
import static mx.kenzie.centurion.KnownArguments.STRING;

public class AnnotationTest {

    @Test(expected = CommandGenerationError.class)
    public void hasError() {
        new CommandAssembler<>(Command.class).generate(Simple.class);
    }

    @Test
    public void hasNoError() {
        final Command<Object> command = new CommandAssembler<>(Command.class).generateSingle(new Simple());
        final Object[] storage = new Object[2];
        command.execute(storage, "test hello 10");
        assert storage[0] instanceof String : storage[0].getClass();
        assert storage[1] instanceof Integer : storage[1].getClass();
        assert storage[0].equals("hello") : storage[0];
        assert storage[1].equals(10) : storage[1];
    }

    @CommandDetails("test")
    public static class Simple {

        @Argument({STRING, INTEGER})
        public void test(Object sender, String string, Integer integer) {
            assert sender instanceof Object[];
            final Object[] storage = (Object[]) sender;
            assert storage.length == 2;
            assert string.equals("hello") : string;
            assert integer.equals(10) : integer;
            storage[0] = string;
            storage[1] = integer;
        }

    }

}
