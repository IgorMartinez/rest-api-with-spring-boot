package br.com.igormartinez.restapiwithspringboot.services;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.igormartinez.restapiwithspringboot.repositories.UserRepository;
import br.com.igormartinez.restapiwithspringboot.model.User;

@Service
public class UserService implements UserDetailsService {
    private Logger logger = Logger.getLogger(UserService.class.getName());

    @Autowired
    UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Finding one user by username " + username);
        User user = repository.findByUsername(username);
        
        if (user == null) 
            throw new UsernameNotFoundException("Username " + username + " not found");

        return user;
    }
}
