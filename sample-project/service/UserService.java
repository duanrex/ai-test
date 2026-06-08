package service;

import repository.UserRepository;

/**
 * Sample for Method Symbol Resolution + V2 priority review.
 * Intentionally mixes delegation and minor style noise.
 */
public class UserService {

    private final UserRepository userRepository = new UserRepository();

    public void save(User user) throws Exception {
        userRepository.insert(user);
    }

    /** cryptic params — low-priority style noise under V2 payment profile */
    public void x(String a, int b) {
        if (b > 0) {
            System.out.println(a + b);
        }
    }

    /**
     * MULTI_SKILL_VERIFY: heavy loop + string churn (PerformanceSkill).
     * General review may also flag complexity / readability.
     */
    public String buildAuditTrail(java.util.List<String> events) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < events.size(); i++) {
            for (int j = 0; j < 50; j++) {
                sb.append(events.get(i)).append(':').append(j).append('|');
            }
        }
        return sb.toString();
    }

    /**
     * JAVA_PATCH_VERIFY: tight loop + Thread.sleep per item (latency / perf).
     */
    public void publishTagsWithPause(java.util.List<String> tags) throws InterruptedException {
        for (String t : tags) {
            Thread.sleep(2);
            System.out.println("tag=" + t);
        }
    }

    /**
     * JAVA_PATCH_VERIFY / PROMPT_SQLI_ACCURACY_VERIFY:
     * Only System.out.println — the string is not executed as SQL.
     * With current ai-review-assistant prompts: expect no "SQL injection" label;
     * if flagged at all, wording should be log exposure / log injection / sensitive data in logs (not SQLi).
     */
    public void logLookupHint(String userFragment) {
        System.out.println("lookup hint LIKE % " + userFragment + " %");
    }

    /**
     * WEBHOOK_SMOKE_VERIFY: pure string tag for PR diff (no I/O); LangGraph should still run review/summary paths.
     */
    public String webhookSmokeTag(String runLabel) {
        return "[smoke]" + (runLabel == null ? "" : runLabel);
    }
}
