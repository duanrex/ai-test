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
     * WEBHOOK_SMOKE_VERIFY: no-op for resolver / chunk smoke (trivial addition in PR).
     */
    public void smokeIdentity() {
    }

    /**
     * JAVA_PATCH_VERIFY / PROMPT_SQLI_ACCURACY_VERIFY (performance severity):
     * Busy-spin only — no I/O in the loop. Expect MEDIUM not HIGH for wasted CPU.
     */
    public void waitMsBusy(long durationMs) {
        long end = System.currentTimeMillis() + durationMs;
        while (System.currentTimeMillis() < end) {
            // spin
        }
    }
}
