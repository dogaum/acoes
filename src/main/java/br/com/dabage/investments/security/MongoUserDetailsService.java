package br.com.dabage.investments.security;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import br.com.dabage.investments.repositories.UserRepository;
import br.com.dabage.investments.user.RoleTO;
import br.com.dabage.investments.user.UserTO;

/**
 *
 * @author Douglas Araujo
 */
@Component
public class MongoUserDetailsService implements UserDetailsService {

    @Resource
    private UserRepository userRepository;
    
    private User userdetails;

    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        UserTO user = userRepository.findByEmail(username);
        if (user != null) {
            userdetails = new User(user.getUsername(), 
					   user.getPassword(),
     			   enabled,
     			   accountNonExpired,
     			   credentialsNonExpired,
     			   accountNonLocked,
     			   getAuthorities(user.getRoles()));
        }

        return userdetails;
    }

    public List<GrantedAuthority> getAuthorities(List<RoleTO> roles) {
        List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();

        for (RoleTO roleTO : roles) {
        	authList.add(new SimpleGrantedAuthority(roleTO.getName()));
		}

        return authList;
    }

   
}