package com.sheemab.socialmedia.Feed.System.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    /*
    write the controller in clean and Swagger-friendly format , using consistent annotations and structure. for the given service
     */

}
