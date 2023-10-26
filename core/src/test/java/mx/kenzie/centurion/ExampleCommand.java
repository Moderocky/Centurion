package mx.kenzie.centurion;

import java.io.PrintStream;
import java.util.Scanner;

import static mx.kenzie.centurion.Arguments.INTEGER;

public class ExampleCommand extends Command<PrintStream> {

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final ExampleCommand command = new ExampleCommand();
        while (true) {
            if (!scanner.hasNext()) continue;
            final String line = scanner.nextLine();
            if (!command.execute(System.out, line).successful()) {
                System.err.println("Unsuccessful command '" + line + "'");
                break;
            }
        }
    }

    @Override
    public Command<PrintStream>.Behaviour create() {
        return command("test")
            .arg(INTEGER, (sender, arguments) -> {
                sender.println("your number was " + arguments.<Integer>get(0));
                return CommandResult.PASSED;
            })
            .arg("break", (sender, arguments) -> {
                sender.println("Stopping...");
                return CommandResult.FAILED_UNKNOWN;
            })
            .lapse(sender -> {
                sender.println("lapsed");
                return CommandResult.LAPSED;
            });
    }

}
