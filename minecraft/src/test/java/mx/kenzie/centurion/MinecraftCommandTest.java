package mx.kenzie.centurion;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Objects;

import static mx.kenzie.centurion.CommandResult.PASSED;

public class MinecraftCommandTest extends MinecraftCommand {

    public MinecraftCommandTest() {
        super("Test");
    }

    @BeforeClass
    public static void setup() { // warm up
        assert new MinecraftCommandTest().behaviour != null;
        assert new MinecraftCommandTest().execute(null, "") != null;
        assert new MinecraftCommandTest().execute(new TestCommandSender(), "test") != null;
        assert new MinecraftCommandTest().execute(new TestCommandSender(), "test").successful();
        assert new MinecraftCommandTest().execute(new TestCommandSender(), "test material " + Material.TUFF).successful();
    }

    @Test
    public void basicEnum() {
        final TestCommandSender sender = new TestCommandSender();
        final Result result = this.execute(sender, "test face " + BlockFace.EAST.name());
        assert result.successful() : result;
        assert result.error() == null : result.error().getMessage();
        assert Objects.equals(sender.output, BlockFace.EAST.name()) : sender.output;
    }

    @Test
    public void materialArgument() {
        final TestCommandSender sender = new TestCommandSender();
        final Result result = this.execute(sender, "test material " + Material.TUFF);
        assert result.successful() : result;
        assert result.error() == null : result.error().getMessage();
        assert Objects.equals(sender.output, Material.TUFF.name()) : sender.output;
        this.execute(sender, "test material gravity " + Material.TUFF);
        assert Objects.equals(sender.output, "false") : sender.output;
    }

    @Override
    public Command<CommandSender>.Behaviour create() {
        return command("test")
            .arg("face", BLOCK_FACE, (sender, arguments) -> {
                sender.sendMessage(String.valueOf(arguments.<BlockFace>get(0)));
                return PASSED;
            })
            .arg("material", MATERIAL, (sender, arguments) -> {
                sender.sendMessage(String.valueOf(arguments.<Material>get(0)));
                return PASSED;
            })
            .arg("material", "gravity", MATERIAL, (sender, arguments) -> {
                sender.sendMessage(String.valueOf(arguments.<Material>get(0).hasGravity()));
                return PASSED;
            });
    }
}
