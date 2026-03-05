package com.example.banking_application_spring_boot.entity;

import java.util.Set;

public enum Role {
    ADMIN(Set.of(Permissions.ACCOUNT_READ, Permissions.ACCOUNT_WRITE, Permissions.ACCOUNT_DELETE)),
    USER(Set.of(Permissions.ACCOUNT_READ));

    private final Set<Permissions> permissions;

    Role(Set<Permissions> permissions) {
        this.permissions = permissions;
    }

    public Set<Permissions> getPermissions() {
        return permissions;
    }
}
