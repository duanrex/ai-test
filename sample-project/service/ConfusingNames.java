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
     * JAVA_PATCH_VERIFY / PROMPT_SQLI_ACCURACY_VERIFY (performance severity):
     * Busy-spin only — no I/O in the loop. With current assistant prompts: expect <b>MEDIUM</b> (wasted CPU),
     * not <b>HIGH</b>, unless model overstates (then tighten prompts further).
     */
    public void waitMsBusy(long durationMs) {
        long end = System.currentTimeMillis() + durationMs;
        while (System.currentTimeMillis() < end) {
            // spin
        }
    }
}
