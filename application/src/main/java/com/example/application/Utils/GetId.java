package com.example.application.Utils;

import com.example.application.Model.User;
import com.example.application.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetId {
    @Autowired
    UserRepository userRepository;

    public Long getId(String email) {
        java.util.Optional<com.example.application.Model.User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getisLoggedIn()) {
                return user.getId();
            }
            return -1L;
        }
        return -1L;
    }
}