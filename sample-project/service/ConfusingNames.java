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
}
