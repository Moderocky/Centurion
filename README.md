Centurion
=====

### Opus #24

A deployable command resolution framework with modules for various domains.

## Maven Information

Centurion is available from `Kenzie`.

```xml

<repository>
    <id>kenzie</id>
    <url>https://repo.kenzie.mx/releases</url>
</repository>
```

It is split into domain-specific modules. These may contain particular behaviour or handling appropriate to that domain.

```xml

<dependency>
    <groupId>mx.kenzie</groupId>
    <artifactId>centurion-core</artifactId>
    <version>1.0.0</version>
    <scope>compile</scope>
</dependency>
```

```xml

<dependency>
    <groupId>mx.kenzie</groupId>
    <artifactId>centurion-minecraft</artifactId>
    <version>1.0.0</version>
    <scope>compile</scope>
</dependency>
```

## Usage

### Creating a Command

Commands are created in and executed from a `Command` class. \
This has some kind of 'Sender' object that you pass when executing a command, typically to send the feedback to the
correct place. \
The sender could be a system output or log for command-line applications, or a player or user if this is deployed in a
game or some kind of interface.

```java
class MyCommand extends Command<User> {

}
```

#### Creating Behaviour

This must override the `create` method, in which you detail the command's behaviour.

```java
class MyCommand extends Command<User> {

    @Override
    public Behaviour create() {
        return this.command("blob");
    }

}
```

Currently, this code generates an empty, behaviour-less `blob` command. \
Executing this command with `command.execute(user, "blob")` will pass but give no feedback.

#### Adding Lapse Behaviour

Imagine our `User` sender object has a `.reply(message)` method for sending feedback.

```java
class MyCommand extends Command<User> {

    @Override
    public Behaviour create() {
        return this.command("blob")
            .lapse(user -> {
                user.reply("Lapsed!");
                return CommandResult.PASSED;
            });
    }

}
```

We can set up 'lapse' behaviour, which will run by default if nothing else matches. \
If there are no matched argument configurations, or special behaviour found for the input,
our command will lapse to this function.

### Adding Arguments

Most commands will want some sort of arguments to specify sub-functions.

```java
class MyCommand extends Command<User> {

    @Override
    public Behaviour create() {
        return this.command("blob")
            .arg("hello", (user, arguments) -> {
                user.reply("Hello user!");
                return CommandResult.PASSED;
            })
            .lapse(user -> {
                user.reply("Lapsed!");
                return CommandResult.PASSED;
            });
    }

}
```

This example would create the following command tree:

```
blob        -> Lapsed!
blob hello  -> Hello user!
```

If our first argument passed to `blob` is exactly `hello` then it will execute our argument function. \
If we pass a different set of arguments (like `foo` or `hello foo`) it will lapse to our default function.

#### Adding Multiple Arguments

Commands can be specified to take multiple sub-arguments, including overloading.

```java
class MyCommand extends Command<User> {

    @Override
    public Behaviour create() {
        return this.command("blob")
            .arg("hello", "there", (user, arguments) -> {
                user.reply("General Kenobi!");
                return CommandResult.PASSED;
            })
            .arg("hello", "world", (user, arguments) -> {
                user.reply("Hello World!");
                return CommandResult.PASSED;
            })
            .arg("hello", (user, arguments) -> {
                user.reply("Hello user!");
                return CommandResult.PASSED;
            })
            .lapse(user -> {
                user.reply("Lapsed!");
                return CommandResult.PASSED;
            });
    }

}
```

This example would create the following command tree:

```
blob                -> Lapsed!
blob hello          -> Hello user!
blob hello there    -> General Kenobi!
blob hello world    -> Hello World!
```

These are called 'literal' arguments, because they match an exact input and nothing else. \
They do not provide anything to the `arguments` parameter of the command function,
since we know what the user wrote already.

#### Input Arguments

Commands can also pass an input to a sub-function.

```java
class MyCommand extends Command<User> {

    @Override
    public Behaviour create() {
        return this.command("blob")
            .arg("hello", STRING, (user, arguments) -> {
                user.reply("Hello, " + arguments.get(0));
                return CommandResult.PASSED;
            })
            .arg("hello", "there", (user, arguments) -> {
                user.reply("General Kenobi!");
                return CommandResult.PASSED;
            })
            .arg("hello", (user, arguments) -> {
                user.reply("Hello user!");
                return CommandResult.PASSED;
            })
            .lapse(user -> {
                user.reply("Lapsed!");
                return CommandResult.PASSED;
            });
    }

}
```

This example would create the following command tree:

```
blob                -> Lapsed!
blob hello          -> Hello user!
blob hello there    -> General Kenobi!
blob hello <string> -> Hello, %input%
```

These command patterns are automatically weighted according to their input.
This means the literal pattern `hello there` will always be checked before the input pattern `hello <string>`.

#### Other Input Types

Raw text is not the only input that a command can take.

```java
class MyCommand extends Command<User> {

    @Override
    public Behaviour create() {
        return this.command("blob")
            .arg("hello", STRING, (user, arguments) -> {
                user.reply("String " + arguments.get(0));
                return CommandResult.PASSED;
            })
            .arg("hello", BOOLEAN, (user, arguments) -> {
                user.reply("Boolean " + arguments.get(0));
                return CommandResult.PASSED;
            })
            .arg("hello", INTEGER, (user, arguments) -> {
                user.reply("Number " + arguments.get(0));
                return CommandResult.PASSED;
            })
            .lapse(user -> {
                user.reply("Lapsed!");
                return CommandResult.PASSED;
            });
    }

}
```

This example would create the following command tree:

```
blob                 -> Lapsed!
blob hello <boolean> -> Boolean %boolean%
blob hello <int>     -> Number %int%
blob hello <string>  -> String %string%
```

There are several built-in input types available in the `Arguments` class, covering most of the appropriate primitive
types.

These patterns are automatically weighted to reduce the chances of an unwanted parse.
For example, an all-accepting `STRING` will always be checked after a more rigid `BOOLEAN` or `INTEGER` type.

#### Greedy Inputs

Arguments can be marked greedy, in order to take the entire remainder of the user input.

```java
class MyCommand extends Command<User> {

    @Override
    public Behaviour create() {
        return this.command("blob")
            .arg("hello", "there", (user, arguments) -> {
                user.reply("General Kenobi!");
                return CommandResult.PASSED;
            })
            .arg(STRING_END, (user, arguments) -> {
                user.reply(arguments.get(0));
                return CommandResult.PASSED;
            })
            .lapse(user -> {
                user.reply("Lapsed!");
                return CommandResult.PASSED;
            });
    }

}
```

This example would create the following command tree:

```
blob                -> Lapsed!
blob hello there    -> General Kenobi!
blob <string...>    -> %string...%
```

Greedy arguments will eat the entire remaining input -
they cannot be followed by another argument since there is nothing left to parse. \
These are weighted very heavily by default to try and arrange them as the last option to be checked,
but due to their nature they are prone to unwanted false positives and should be used carefully.

### Compound Arguments

The compound argument is a special feature of Centurion that allows a command input to function like a miniature
grammar.
A program may specify a compound argument, e.g. centurion-minecraft's `vector`, that is made from its own set of
multi-argument structures.

This argument may be used as normal within a command, e.g.

```java
class MyCommand extends Command<Player> {

    @Override
    public Behaviour create() {
        return this.command("blob")
            .arg("teleport", VECTOR, (player, arguments) -> {
                final Vector vector = arguments.get(0);
                player.teleportTo(vector);
                return CommandResult.PASSED;
            });
    }

}
```

This example would create the following command tree:

```
blob teleport <vector>  -> Teleports player
```

However, the compound `vector` argument may specify its own patterns, such as:

```
<number> meters <direction>
<x> <y> <z>
```

Both of these are verified and then converted to a `Vector` using their own functions.
This `Vector` is then passed back as the input for the command.

In essence, the command's real tree would look like:

```
blob teleport <number> meters <direction>
blob teleport <x> <y> <z>
```

However, the command does not need to manually parse the number and direction or x, y and z to a vector.

This means that both the inputs `teleport 10 meters north` and `teleport 4 32 17` would provide a single `Vector` input
to the same function.

#### Compound Parsing Differences

Compound arguments have a similar design and structure to commands themselves, and function as a sort of sub-command.
However, the compound argument does not care about trailing input at the end.

If a compound looks for `<number> meters` and the user inputs `10 meters south`,
the remaining `south` will be sent back to the command parser to check against the next argument.

### Reading Patterns

The full set of command patterns generated from a command is available in `command.patterns()`.

These follow a distinct structure:

```
label literal <required> [optional] <greedy...>
```

The `label` is the main command. \
Literal arguments have no brackets. \
Angle brackets are used for `<required>` inputs. \
Square brackets are used for `[optional]` inputs, which will pass `null` to the function if not set. \
An ellipsis is used for `greedy...` inputs.

### Receiving Inputs

Command argument inputs are parsed and stored in the `arguments` function parameter. \
These are available by zero-index, using `arguments.get(index)`.

Literal arguments do **not** take up a slot in the argument container. \
The command `/blob hello <int> <string>` would have its `int` input at index `0` and its `string` input at index `1`.
There is no index for the literal `hello` argument, since this is already known before execution.

Argument values are `null` __if and only if__ the argument type was `[optional]` and no input was provided.

Argument values are automatically typed by the `get` method. If the type is somehow unknown (e.g. an argument type can
give multiple result types),
a type parameter should be specified and the result should be checked manually with `arguments.<Object>get(2)`.

If the wrong type is asked for, the cast will fail and throw an error.

### Command Results

Commands return a result with some information about its execution.
These have a boolean `successful()` value and a `CommandResult` enum `type()` for switching. \
They may also provide a `Throwable` error that occurred when trying to dispatch, parse or run the command function.

#### Failures

A failure result will be marked `successful() == false`. This means that the command failed in its regular execution in
one of several ways.

Example failure standards:

1. No lapse behaviour was defined for the command and the input matched no argument.
2. An uncaught error was thrown during either parsing or execution. This error is provided in the result.
    - N.B. this may have halted a command *part way through* execution - safety checks are advised.
3. A command function returned a failure result.

#### Input Validation

Functions may apply additional validation to inputs through the use of the `WRONG_INPUT` or `LAPSE` failure results.
These results are a __special case__; they are _not_ a failure condition for the command execution.

If the `WRONG_INPUT` result is returned, the line of argument parsing will be abandoned and the iteration will move on.
If no subsequent argument patterns match the input this will fall to the lapse case.

Please note that this sort of fall-through can be dangerous and may lead to unexpected conditions being met.\
Exempli gratia:

```
input    test hello there
case #1  test hello <string>  -> WRONG_INPUT // continues
case #1  test <string...>     -> PASSED      // accepted here
lapse                                        // never reaches lapse
```

Alternatively, the `LAPSE` result will break parsing and go straight to the `lapse` function.

```
input    test hello there
case #1  test hello <string>  -> LAPSE       // jumps
case #1  test <string...>     ->             // skipped
lapse                         -> PASSED      // accepted here
```

## Motivations

Centurion is the follow-on from [Commander](https://github.com/Moderocky/Commander), my previous command framework.

While its structure is similar in some ways, it has a few key differences.

Commander had a core defect: it was originally built for 'Minecraft' and then extracted and republished as a standalone
library.
That meant a lot of its behaviour was either idiomatic or unnecessary for other environments.

Centurion was built first as a standalone framework and the 'Minecraft' and other modules were built on top of its core.

Centurion also has a more intuitive framework, avoiding the nesting structure that Commander relied on.
Centurion also supports automatic typing when retrieving an argument.

### Commander

```java
class MyCommand extends Commander {
    public Command create() {
        return command("test")
            .arg("hello",
                arg("there", sender -> {
                    System.out.println("General Kenobi!");
                })
            ).arg("hello",
                arg((sender, args) -> {
                    System.out.println("Hello, " + args[0] + "!");
                }, new ArgString())
            );
    }
}
```

### Centurion

```java
class MyCommand extends Command<Sender> {
    public Behaviour<Sender> create() {
        return command("test")
            .arg("hello", "there", (sender, arguments) -> {
                System.out.println("General Kenobi!");
            })
            .arg("hello", STRING, (sender, arguments) -> {
                System.out.println("Hello, " + arguments.get(0) + "!");
            });
    }
}
```

## Provided Domain Support

Centurion provides support for some domains out-of-the-box. This is not built in to the core library, but comes as
separate modules that include the core.
These modules can be used as dependencies in place of the core library.

### Discord

TBD.

## Licensing

The core of Centurion is available under MIT as seen in the repository.

Some classes in the 'Minecraft' submodule depend transitively on Bukkit and so are covered by GPL-3 instead.

# Minecraft

Commands for Minecraft's Bukkit server are available.
These are registered in the `minecraft` module and available through `MinecraftCommand`.
Some basic argument types for the domain are available, along with placeholder formatting support and interaction
utilities.

This support was built for a recent version of the game -- it will not support older versions, it may not support future
versions without modification.

## Arguments

Rudimentary support is provided for the following argument types.

### Block Face

The block face argument accepts identifiers from `org.bukkit.block.BlockFace`, such as cardinal directions, up, down and
partial directions.

### Material

The material argument accepts identifiers from `org.bukkit.Material`, which appear to correspond to the minecraft IDs,
without the `minecraft:` namespace.

### Entity Type

The entity type argument accepts identifiers from `org.bukkit.entity.EntityType`, corresponding to the minecraft entity
IDs without their `minecraft:` namespace.

### Color

The color argument accepts the legacy named colours from `net.kyori.adventure.text.format.NamedTextColor`, as well as
hash-preceded hex colour codes like `#ff0000`.
These colour classes are provided from a third-party library included in PaperMC.

### Block Data

The block data argument accepts a valid identifier in the form `namespace:key[property=value]`, where the namespace and
property sections are optional.
This will only accept material types that are blocks, e.g. `stone` is acceptable but `stick` is not.
This will only accept material types registered with the Minecraft server, so `my_block` will not be accepted unless a
modification has registered it.

### Player

The player argument accepts the name of an online player. It may accept an unambiguous partial name.

### Selector

The selector argument parses an entity selector that can be used to pick out one or more entities from a given context,
e.g. the command sender.

This can also parse complex selectors (e.g. `@e[distance=..10,limit=1]`) which are evaluated from the perspective of the
command sender where possible.
These selectors are parsed by Minecraft's server.

### World

The world argument accepts a world by name.

### Key

The key argument accepts a resource key, such as `minecraft:stone` or `my_mod:resource/path`.

### Tag

Tag arguments (Material, Item, Entity) accept a set of built-in tags, which select multiple of a thing, such as
materials or entity types.
These can be used to provide blanket selection, e.g. `#raiders` picking out `Pillager`, `Vindicator`, `Witch`, etc.

### Relative Number

Relative numbers come in the potential format `~X`, where either the preceding tilde `~` or following number `0` are
optional.
A no-number value `~` equates to relative zero.

Relative numbers do not have to be relative, e.g. `~10` is marked as relative whereas `10` is not.

What the relativity of the number means depends entirely on the implementation of a command. Minecraft typically uses
these for relativising a position.

### Local Number

Local numbers must be preceded by a local marker circumflex `^`.
A no-number value `^` equates to local zero.

What the locality of the number means depends entirely on the implementation of a command. Minecraft typically uses
these for localising a position.

### Vector

A vector is a quantity with direction and magnitude, representing an offset from an origin, such as `10 5 -4`.
These are typically used to indicate position coordinates, or the length, width and height of an area, but they are not
attached to a world.

The vector argument accepts either three numerical values, e.g. `3.5 0 -9`, or a length with a direction,
e.g. `10 meters north`. Both will be evaluated to a three-value vector.

The origin of the vector is left up to the implementation of a command.

### Location

A location is a position within a world.

The location argument accepts any of the following:

```
<x> <y> <z> in <world>
spawn of <world>
bed of <player>
<offset> of <entity>
<offset> of <location>
```

Example inputs include:

```
10 64 -45 in world_nether
spawn of world
bed of Mackenbee
10 meters north of @p
5 meters east of spawn of world
```

### Offset

An offset is a special relative form of the vector argument.
It supports relative numbers as values, e.g. `~ ~10 ~5` instead of fixed coordinates.
This relative vector can be mapped on to a position, in which case the relative `~` inputs will be added on to the new
origin, whereas the non-relative numbers will replace the origin coordinates.

The offset argument accepts:

```
<x> <y> <z>
<number> meters <direction>
```

Example inputs include:

```
~ ~5 ~         // 5 meters above origin
10 ~ -42       // 10, height of origin, -42
5 meters down  // 5 meters below origin
```

### Local Offset

A local offset represents a position relative to an origin's orientation, e.g. the direction an entity is looking in.
This accepts only local `^x` numbers.

```
<left> <up> <forwards>
```

Example inputs include:

```
^ ^ ^10
^-2 ^0 ^4
```
