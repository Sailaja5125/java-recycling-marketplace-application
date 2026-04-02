package com.example.application.Utils;

import com.example.application.Model.User;
import com.example.application.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Component
public class LoginCheck {
    @Autowired
    UserRepository userRepository;

    public boolean loginCheck(@RequestBody Map<String , String> request){
        String email = request.get("email");

        java.util.Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            return optionalUser.get().getisLoggedIn();
        }
        return false;
    }
}
