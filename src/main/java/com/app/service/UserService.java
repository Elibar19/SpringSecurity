package com.app.service;


import com.app.Auth.AuthResponse;
import com.app.Auth.LoginRequest;
import com.app.Auth.RegisterRequest;
import com.app.Jwt.JwtService;
import com.app.persistence.entity.RoleEntity;
import com.app.persistence.entity.RoleEnum;
import com.app.persistence.entity.UserEntity;
import com.app.persistence.repository.RoleRepository;
import com.app.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    /* Se crea el metodo login. Se recupera el usuario y se autentica buscandolo en la bd y se devuelve el usuario con el JWT. */
    public AuthResponse login(LoginRequest request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserEntity user = userRepository.findUserEntityByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("El usuario no existe"));
        String token = jwtService.getToken(user);
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    /* Creamos el metodo para registrar usuarios. Se devuelve el JWT del nuevo usuario. */
    public AuthResponse register(RegisterRequest request){

        //En este caso mapeamos los roles del request para pasarlos a un Set de RoleEntity.
        Set<RoleEntity> roles = request.getRoles().stream()
                .map(roleEnum -> roleRepository.findByRoleEnum(roleEnum)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Role: " + roleEnum)))
                .collect(Collectors.toSet());

        UserEntity userEntity = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .build();

        userRepository.save(userEntity);

        return AuthResponse.builder()
                .token(jwtService.getToken(userEntity))
                .build();
    }
}
