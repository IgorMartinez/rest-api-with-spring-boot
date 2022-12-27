package br.com.igormartinez.restapiwithspringboot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.igormartinez.restapiwithspringboot.data.vo.v1.security.AccountCredentialsVO;
import br.com.igormartinez.restapiwithspringboot.data.vo.v1.security.TokenVO;
import br.com.igormartinez.restapiwithspringboot.model.User;
import br.com.igormartinez.restapiwithspringboot.repositories.UserRepository;
import br.com.igormartinez.restapiwithspringboot.security.jwt.JwtTokenProvider;

@Service
public class AuthService {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository repository;

    @SuppressWarnings("rawtypes")
    public ResponseEntity signin(AccountCredentialsVO accountCredentialsVO) {
        try {
            String username = accountCredentialsVO.getUsername();
            String password = accountCredentialsVO.getPassword();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            User user = repository.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("Username " + username + " not found");
            }
            
            TokenVO tokenVO = new TokenVO();
            tokenVO = tokenProvider.createAccessToken(username, user.getRoles());

            return ResponseEntity.ok(tokenVO);
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }
}
