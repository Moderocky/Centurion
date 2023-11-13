package mx.kenzie.centurion;

import org.bukkit.permissions.PermissionDefault;

@FunctionalInterface
public interface PermissionProvider {

    String qualifiedName();

    default PermissionDefault getDefault() {
        return PermissionDefault.OP;
    }

}
