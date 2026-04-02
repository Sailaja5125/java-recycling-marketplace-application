package com.example.application.Utils;
import java.util.Random;
public class GenerateOTP {
    public String generateOtp(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // digits 0–9
        }
        return sb.toString();
    }

}
