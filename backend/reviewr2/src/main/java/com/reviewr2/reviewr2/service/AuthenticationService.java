package com.reviewr2.reviewr2.service;

import com.reviewr2.reviewr2.dto.AuthenticationRequest;
import com.reviewr2.reviewr2.dto.AuthenticationResponse;
import com.reviewr2.reviewr2.dto.RegisterRequest;
import com.reviewr2.reviewr2.entity.User;
import com.reviewr2.reviewr2.repository.UserRepository;
import com.reviewr2.reviewr2.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // handles creating a new user
    public AuthenticationResponse register(RegisterRequest request) {
        // build new user form incoming dto
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        // hash the password before it goes to database
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        // save user in postgresql
        userRepository.save(user);
        // generate fresh jwt token for their account
        String jwtToken = jwtService.generateToken(user);

        return new AuthenticationResponse(jwtToken, user.getUsername(), user.getRankTier());
    }

    // handles login of existing user
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // the bouncer checks the id
        // this method automatically hashes the raw password and compares it to the database
        // if fails, it throws an exception and stops execution immediately
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        // code reaches this line, password is correct
        // now fetch actual user record from database
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // generate fresh jwt token
        String jwtToken = jwtService.generateToken(user);

        return new AuthenticationResponse(jwtToken, user.getUsername(), user.getRankTier());
    }
}
