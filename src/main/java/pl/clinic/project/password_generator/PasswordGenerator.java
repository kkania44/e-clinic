package pl.clinic.project.password_generator;

import java.util.Random;

public class PasswordGenerator {

    public static String generate() {
        int randomizeBound = 26;
        Random random = new Random();
        StringBuilder passBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int code = random.nextInt(randomizeBound) + 97;
            String signInPass = Character.toString((char)code);
            passBuilder.append(signInPass);
        }
        passBuilder.append(random.nextInt(89)+10);
        return passBuilder.toString();
    }

}
