package com.back.banka.Services.Impl;

import com.back.banka.Exceptions.Custom.UserNotFoundException;
import com.back.banka.Model.SecurityUser;
import com.back.banka.Model.User;
import com.back.banka.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * buscar usuario por username y valida  contra la base de datos
     * Este metodo carga usuarios de la base de datos en el contexto de spring securiity
     * */
@Autowired
private  UserRepository userRepository;





    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user =  this.userRepository.findByEmail(username).orElseThrow(()
                -> new UserNotFoundException("usuario no encontrado"));

        //devolver el usuario en envuelto en un UserDetails
        return new SecurityUser(user);
    }
}
