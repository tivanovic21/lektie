package hr.lektie.common.user;

/**
 * Values of {@code users.role} (V2__schema.sql). Stored lowercase in the database (see {@link RoleConverter}),
 * used uppercase in code and in the JWT. EDITOR exists in the schema's CHECK constraint, so it is kept here
 * even though no demo user has it yet.
 */
public enum Role {
    STUDENT,
    PARENT,
    TEACHER,
    EDITOR,
    ADMIN;

    /** Spring Security authority name, e.g. ROLE_ADMIN, which is what hasRole('ADMIN') checks. */
    public String authority() {
        return "ROLE_" + name();
    }
}
