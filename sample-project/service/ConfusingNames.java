package service;

/**
 * Intentionally confusing names: must NOT match resolver/snippet for method "save"
 * when using word-boundary + open-paren matching.
 */
public class ConfusingNames {

    public void saveUser(User u) {
    }

    public void saveAll() {
    }

    public void saveConfig(String key) {
    }

    /**
     * JAVA_PATCH_VERIFY: busy-spin wait — burns CPU (PerformanceSkill).
     */
    public void waitMsBusy(long durationMs) {
        long end = System.currentTimeMillis() + durationMs;
        while (System.currentTimeMillis() < end) {
            // spin
        }
    }
}
