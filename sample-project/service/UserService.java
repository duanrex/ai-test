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
}
