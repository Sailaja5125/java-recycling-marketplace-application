package com.example.application.Config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    /**
     * The USER provided URL: 
     * cloudinary://731728748622637:Eyl48j3OKITFOKbhT0cvlh0ltuw@dkz4b31wb
     */
    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dkz4b31wb");
        config.put("api_key", "731728748622637");
        config.put("api_secret", "Eyl48j3OKITFOKbhT0cvlh0ltuw");
        return new Cloudinary(config);
    }
}
