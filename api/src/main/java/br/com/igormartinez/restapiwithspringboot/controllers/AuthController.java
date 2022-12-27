package br.com.igormartinez.restapiwithspringboot.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.igormartinez.restapiwithspringboot.data.vo.v1.security.AccountCredentialsVO;
import br.com.igormartinez.restapiwithspringboot.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Authentication Endpoint")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @Operation(summary = "Authenticates a user and returns a token")
    @PostMapping(value = "/signin")
    @SuppressWarnings("rawtypes")
    public ResponseEntity signin(@RequestBody AccountCredentialsVO accountCredentialsVO) {
        if (checkIfParamsIsNotNull(accountCredentialsVO)) 
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");
        
        ResponseEntity token = authService.signin(accountCredentialsVO);

        if (token == null) 
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");

        return token;
    }

    private boolean checkIfParamsIsNotNull(AccountCredentialsVO accountCredentialsVO) {
        return accountCredentialsVO == null 
            || accountCredentialsVO.getUsername() == null || accountCredentialsVO.getUsername().isBlank()
            || accountCredentialsVO.getPassword() == null || accountCredentialsVO.getPassword().isBlank();
    }
}
