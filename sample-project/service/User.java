package service;

public class User {
    public final String name;

    public User(String name) {
        this.name = name;
    }

    /**
     * JAVA_PATCH_VERIFY: uses {@code ==} on interned-looking strings — wrong for value equality / i18n.
     */
    public boolean sameName(User other) {
        return this.name == other.name;
    }

    /** WEBHOOK_SMOKE_VERIFY: tiny helper so User.java appears in multi-file PR diffs. */
    public String smokeRunRef() {
        return name + "@smoke-verify";
    }
}
