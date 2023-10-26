package mx.kenzie.centurion;

import mx.kenzie.centurion.annotation.Argument;
import mx.kenzie.centurion.annotation.CommandDetails;
import mx.kenzie.centurion.error.CommandGenerationError;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.objectweb.asm.Opcodes.*;

public class CommandAssembler<CommandType extends Command<?>> extends ClassLoader {

    protected static final AtomicInteger index = new AtomicInteger(0);

    protected final Class<CommandType> type;
    protected final Method command, create;

    public CommandAssembler(Class<CommandType> type) {
        this.type = type;
        try {
            this.command = type.getDeclaredMethod("command", String.class, String[].class);
            this.create = type.getMethod("create");
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException("No matching 'command' method found in this override.", ex);
        }
    }

    @SuppressWarnings("unchecked")
    public CommandType[] generate(Object... targets) {
        final List<CommandType> list = new ArrayList<>();
        for (final Object target : targets) {
            if (target == null) continue;
            list.addAll(List.of(this.generate(target)));
        }
        return list.toArray((CommandType[]) Array.newInstance(type, list.size()));
    }

    @SuppressWarnings("unchecked")
    public CommandType[] generate(Object target) {
        //<editor-fold desc="Extract command details from class" defaultstate="collapsed">
        if (target == null) throw new CommandGenerationError("No target was provided.");
        final boolean isStaticOnly = target instanceof Class<?>, detailsKnownForClass;
        final Class<?> source;
        final CommandDetails global;
        source = target instanceof Class<?> thing ? thing : target.getClass();
        detailsKnownForClass = source.isAnnotationPresent(CommandDetails.class);
        if (detailsKnownForClass) global = source.getAnnotation(CommandDetails.class);
        else global = null;
        final Map<CommandDetails, CommandData> map = new HashMap<>();
        for (final Method method : source.getMethods()) {
            if (!method.isAnnotationPresent(Argument.class)) continue;
            if (isStaticOnly && !Modifier.isStatic(method.getModifiers()))
                throw new CommandGenerationError(
                    "Method '" + method + "' is not static but generator was given a class target.");
            final CommandDetails local;
            if (method.isAnnotationPresent(CommandDetails.class)) local = method.getAnnotation(CommandDetails.class);
            else if (detailsKnownForClass) local = global;
            else throw new CommandGenerationError(
                    "No command details are known for '" + method + "'; add these to the method or the class.");
            final Argument argument = method.getAnnotation(Argument.class);
            assert local != null;
            final ArgumentData data = new ArgumentData(argument, method);
            if (map.containsKey(local)) map.get(local).list.add(data);
            else map.put(local, new CommandData(local == global
                ? this.commandNames(local, source)
                : this.commandNames(local, method), new ArrayList<>(List.of(data))));
        }
        final List<CommandType> list = new ArrayList<>();
        for (final Map.Entry<CommandDetails, CommandData> entry : map.entrySet()) {
            list.add(this.createCommand(source, target, entry.getKey(), entry.getValue()));
        }
        return list.toArray((CommandType[]) Array.newInstance(type, list.size()));
        //</editor-fold>
    }

    @SuppressWarnings("unchecked")
    public <Adjusted extends CommandType> Adjusted generateSingle(Object target) {
        final CommandType[] found = this.generate(target);
        return (Adjusted) found[0];
    }

    @SuppressWarnings("unchecked")
    protected CommandType createCommand(Class<?> source, Object target, CommandDetails details, CommandData commandData) {
        final int number = index.incrementAndGet();
        final String simpleName = "MagicCommand" + number,
            internalName = "mx/kenzie/centurion/" + simpleName,
            classPath = internalName.replace('/', '.');
        final Strings strings = commandData.strings;
        final ArgumentData[] arguments = commandData.list.toArray(new ArgumentData[0]);
        final ClassWriter writer = new ClassWriter(0);
        //<editor-fold desc="Class Meta" defaultstate="collapsed">
        writer.visit(V17, ACC_PUBLIC | ACC_SUPER, internalName,
            null, "mx/kenzie/centurion/Command", null);
        writer.visitInnerClass("mx/kenzie/centurion/Command$Behaviour", "mx/kenzie/centurion/Command", "Behaviour",
            ACC_PUBLIC);
        writer.visitInnerClass("mx/kenzie/centurion/Command$Input", "mx/kenzie/centurion/Command", "Input",
            ACC_PUBLIC | ACC_STATIC | ACC_ABSTRACT | ACC_INTERFACE);
        writer.visitInnerClass("mx/kenzie/centurion/Command$EmptyInput", "mx/kenzie/centurion/Command", "EmptyInput",
            ACC_PUBLIC | ACC_STATIC | ACC_ABSTRACT | ACC_INTERFACE);
        writer.visitField(ACC_PRIVATE | ACC_STATIC, "target", "Ljava/lang/Object;", null, null).visitEnd();
        //</editor-fold>
        //<editor-fold desc="Constructor calling super" defaultstate="collapsed">
        int constructorSize = 1;
        final MethodVisitor constructor = writer.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/Object;)V", null, null);
        constructor.visitCode();
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(type), "<init>", "()V", false);
        constructor.visitVarInsn(ALOAD, 1);
        constructor.visitFieldInsn(PUTSTATIC, internalName, "target", "Ljava/lang/Object;");
        if (!details.description().isBlank()) {
            ++constructorSize;
            constructor.visitVarInsn(ALOAD, 0);
            constructor.visitLdcInsn(details.description());
            constructor.visitFieldInsn(PUTFIELD, internalName, "description", "Ljava/lang/String;");
        }
        constructor.visitInsn(RETURN);
        constructor.visitMaxs(constructorSize, 2);
        constructor.visitEnd();
        //</editor-fold>
        //<editor-fold desc="Create method" defaultstate="collapsed">
        final MethodVisitor create = writer.visitMethod(ACC_PUBLIC, "create",
            Type.getMethodDescriptor(this.create), null, null);
        create.visitCode();
        create.visitVarInsn(ALOAD, 0);
        create.visitLdcInsn(strings.label);
        create.visitIntInsn(BIPUSH, strings.aliases.length);
        create.visitTypeInsn(ANEWARRAY, "java/lang/String");
        for (int i = 0; i < strings.aliases.length; i++) {
            final String alias = strings.aliases[i];
            create.visitInsn(DUP);
            create.visitIntInsn(BIPUSH, i);
            create.visitLdcInsn(alias);
            create.visitInsn(AASTORE);
        }
        create.visitMethodInsn(INVOKEVIRTUAL, internalName, "command",
            "(Ljava/lang/String;[Ljava/lang/String;)Lmx/kenzie/centurion/Command$Behaviour;", false);
        final String bootstrap = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;";
        final String targetSignature = "(Ljava/lang/Object;Lmx/kenzie/centurion/Arguments;)Lmx/kenzie/centurion/Result;";
        int maxArgsOnStack = 1;
        for (int i = 0; i < arguments.length; i++) {
            //<editor-fold desc="Create argument overload" defaultstate="collapsed">
            int argsOnStack = 0;
            final ArgumentData data = arguments[i];
            final Argument argument = data.argument();
            final String[] literals;
            if (argument.pattern().isBlank()) literals = new String[0];
            else literals = argument.pattern().trim().split(" ");
            for (final String literal : literals) {
                create.visitLdcInsn(literal);
                ++argsOnStack;
            }
            for (final KnownArguments knownArguments : argument.value()) {
                create.visitFieldInsn(GETSTATIC, "mx/kenzie/centurion/KnownArguments", knownArguments.name(),
                    "Lmx/kenzie/centurion/KnownArguments;");
                create.visitMethodInsn(INVOKEVIRTUAL, "mx/kenzie/centurion/KnownArguments", "getArgument",
                    "()Lmx/kenzie/centurion/Argument;", false);
                ++argsOnStack;
            }
            create.visitMethodInsn(INVOKESTATIC, "java/util/List", "of",
                "(" + ("Ljava/lang/Object;".repeat(argsOnStack)) + ")Ljava/util/List;", true);
            create.visitInvokeDynamicInsn("run", "()Lmx/kenzie/centurion/Command$Input;",
                new Handle(H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", bootstrap, false),
                Type.getType(targetSignature),
                new Handle(H_INVOKESTATIC, internalName, "argument$" + i, targetSignature, false),
                Type.getType(targetSignature));
            create.visitMethodInsn(INVOKEVIRTUAL, "mx/kenzie/centurion/Command$Behaviour", "arg",
                "(Ljava/util/Collection;Lmx/kenzie/centurion/Command$Input;)Lmx/kenzie/centurion/Command$Behaviour;",
                false);
            if (!argument.description().isBlank()) {
                create.visitLdcInsn(argument.description());
                create.visitMethodInsn(INVOKEVIRTUAL, "mx/kenzie/centurion/Command$Behaviour", "description",
                    "(Ljava/lang/String;)Lmx/kenzie/centurion/Command$Behaviour;", false);
            }
            maxArgsOnStack = Math.max(maxArgsOnStack, argsOnStack);
            //</editor-fold>
        }
        create.visitTypeInsn(CHECKCAST, Type.getInternalName(this.create.getReturnType()));
        create.visitInsn(ARETURN);
        create.visitMaxs(4 + maxArgsOnStack, 1 + this.create.getParameterCount());
        create.visitEnd();
        //</editor-fold>
        //<editor-fold desc="Create argument methods" defaultstate="collapsed">
        for (int i = 0; i < arguments.length; i++) {
            final MethodVisitor caller = writer.visitMethod(ACC_PUBLIC | ACC_STATIC, "argument$" + i, targetSignature,
                null, null);
            caller.visitCode();
            int argsOnStack = 0;
            final ArgumentData data = arguments[i];
            final Method method = data.method();
            final int opcode = Modifier.isStatic(method.getModifiers()) ? INVOKESTATIC : INVOKEVIRTUAL;
            final Class<?>[] parameters = method.getParameterTypes();
            if (!Modifier.isStatic(method.getModifiers())) {
                caller.visitFieldInsn(GETSTATIC, internalName, "target", "Ljava/lang/Object;");
                caller.visitTypeInsn(CHECKCAST, Type.getInternalName(source));
                ++argsOnStack;
            }
            if (parameters.length > 0) {
                final Class<?> sender = method.getParameterTypes()[0];
                caller.visitVarInsn(ALOAD, 0);
                caller.visitTypeInsn(CHECKCAST, Type.getInternalName(sender));
                ++argsOnStack;
            }
            for (int index = 1; index < parameters.length; index++) {
                final Class<?> parameter = method.getParameterTypes()[index];
                caller.visitVarInsn(ALOAD, 1);
                caller.visitIntInsn(BIPUSH, index - 1);
                caller.visitMethodInsn(INVOKEVIRTUAL, "mx/kenzie/centurion/Arguments", "get",
                    "(I)Ljava/lang/Object;", false);
                caller.visitTypeInsn(CHECKCAST, Type.getInternalName(parameter));
                ++argsOnStack;
            }
            caller.visitMethodInsn(opcode, Type.getInternalName(source), method.getName(),
                Type.getMethodDescriptor(method), source.isInterface());
            if (method.getReturnType() != void.class) caller.visitInsn(POP);
            caller.visitFieldInsn(GETSTATIC, "mx/kenzie/centurion/CommandResult", "PASSED",
                "Lmx/kenzie/centurion/CommandResult;");
            caller.visitInsn(ARETURN);
            caller.visitMaxs(++argsOnStack, 2);
            caller.visitEnd();
        }
        //</editor-fold>
        writer.visitEnd();
        final Class<?> loaded = this.loadClass(classPath, writer.toByteArray());
        try {
            return (CommandType) loaded.getConstructor(Object.class).newInstance(target);
        } catch (Throwable ex) {
            throw new CommandGenerationError("Unable to create command object.", ex);
        }
    }

    private Strings commandNames(CommandDetails details, AnnotatedElement element) {
        //<editor-fold desc="Make up names for the command" defaultstate="collapsed">
        final String label;
        final String[] aliases;
        if (details.value().length == 0) {
            if (element instanceof Method method) label = method.getName().toLowerCase();
            else if (element instanceof Class<?> owner) {
                final String part = owner.getSimpleName();
                if (part.endsWith("Command") && part.length() > 7) label = part.substring(0, part.length() - 7);
                else label = part;
            } else label = element.toString().trim().replace(" ", "");
            aliases = new String[0];
        } else if (details.value().length == 1) {
            label = details.value()[0];
            aliases = new String[0];
        } else {
            label = details.value()[0];
            aliases = new String[details.value().length - 1];
            System.arraycopy(details.value(), 1, aliases, 0, aliases.length);
        }
        return new Strings(label, aliases);
        //</editor-fold>
    }

    protected Class<?> loadClass(String path, byte[] bytecode) {
        return super.defineClass(path, bytecode, 0, bytecode.length);
    }

    protected record CommandData(Strings strings, List<ArgumentData> list) {
    }

    protected record Strings(String label, String... aliases) {
    }

    protected record ArgumentData(Argument argument, Method method) {

    }

}
