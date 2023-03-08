package mx.kenzie.centurion;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Objects;

import static mx.kenzie.centurion.CommandResult.LAPSED;
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

    @Test
    public void vectorCompound() {
        final TestCommandSender sender = new TestCommandSender();
        final Result result = this.execute(sender, "test vector 10 5 4");
        assert result.successful() : result;
        assert result.error() == null : result.error().getMessage();
        assert Objects.equals(sender.raw, new Vector(10, 5, 4)) : sender.raw;
    }

    @Test
    public void vectorCompoundAlt() {
        final TestCommandSender sender = new TestCommandSender();
        final Result result = this.execute(sender, "test vector 10 meters up");
        assert result.successful() : result;
        assert result.error() == null : result.error().getMessage();
        assert Objects.equals(sender.raw, new Vector(0, 10, 0)) : sender.raw;
    }

    @Test
    public void offsetCompound() {
        final TestCommandSender sender = new TestCommandSender();
        final Result result = this.execute(sender, "test offset 10 ~5 ~-3");
        assert result.successful() : result;
        assert result.error() == null : result.error().getMessage();
        assert Objects.equals(sender.raw, new Vector(10, 5, -3)) : sender.raw;
        final RelativeVector vector = (RelativeVector) sender.raw;
        assert !vector.isRelativeX();
        assert vector.isRelativeY();
        assert vector.isRelativeZ();
    }

    @Test
    public void localCompound() {
        final TestCommandSender sender = new TestCommandSender();
        final Result result = this.execute(sender, "test local ^ ^2 ^0");
        assert result.successful() : result;
        assert result.error() == null : result.error().getMessage();
        assert Objects.equals(sender.raw, new Vector(0, 2, 0)) : sender.raw;
        final LocalVector vector = (LocalVector) sender.raw;
        assert vector.isRelativeX();
        assert vector.isRelativeY();
        assert vector.isRelativeZ();
        assert this.execute(sender, "test local ^ 2 ^0").type() == LAPSED;
    }

    @Test
    public void colorArgument() {
        final TestCommandSender sender = new TestCommandSender();
        final Result result = this.execute(sender, "test color #ff0000");
        assert result.successful() : result;
        assert result.error() == null : result.error().getMessage();
        assert Objects.equals(sender.raw, TextColor.fromCSSHexString("#ff0000")) : sender.output;
        assert this.execute(sender, "test color dark_purple").successful();
        assert Objects.equals(sender.raw, NamedTextColor.DARK_PURPLE) : sender.output;
    }

    @Test
    public void testPatterns() {
        assert Arrays.toString(this.patterns()).equals("[test, test vector <*vector>, test offset <*offset>, test local <*local>, test color <color>, test face <blockface>, test material <material>, test material gravity <material>]") : Arrays.toString(this.patterns());
    }

    @Override
    public Command<CommandSender>.Behaviour create() {
        return command("test")
            .arg("vector", VECTOR, (sender, arguments) -> {
                ((TestCommandSender) sender).raw = arguments.<Vector>get(0);
                return PASSED;
            })
            .arg("offset", OFFSET, (sender, arguments) -> {
                ((TestCommandSender) sender).raw = arguments.<RelativeVector>get(0);
                return PASSED;
            })
            .arg("local", LOCAL_OFFSET, (sender, arguments) -> {
                ((TestCommandSender) sender).raw = arguments.<LocalVector>get(0);
                return PASSED;
            })
            .arg("color", COLOR, (sender, arguments) -> {
                ((TestCommandSender) sender).raw = arguments.<Color>get(0);
                return PASSED;
            })
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
