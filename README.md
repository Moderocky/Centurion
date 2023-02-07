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
command("test")
    .arg("hello",
        arg("there", sender -> {
            System.out.println("General Kenobi!");
        })
    )
    .arg("hello",
        arg((sender, args) -> {
            System.out.println("Hello, " + args[0] + "!");
        }, new ArgString())
    )
```

### Centurion

```java 
command("test")
    .arg("hello", "there", (sender, arguments) -> {
        System.out.println("General Kenobi!");
    })
    .arg("hello", STRING, (sender, arguments) -> {
        System.out.println("Hello, " + arguments.get(0) + "!");
    })
```

## Licensing

The core of Centurion is available under MIT as seen in the repository.

Some classes in the 'Minecraft' submodule depend transitively on Bukkit and so are covered by GPL-3 instead.
