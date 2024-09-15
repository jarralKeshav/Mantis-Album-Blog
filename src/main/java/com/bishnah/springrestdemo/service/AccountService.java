package com.bishnah.springrestdemo.service;

import com.bishnah.springrestdemo.model.Account;
import com.bishnah.springrestdemo.repository.AccountRepository;
import com.bishnah.springrestdemo.util.constants.Authority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService implements UserDetailsService {


    private AccountRepository accountRepository;
    private PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Account createAccount(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        if(account.getAuthorities()==null){
            account.setAuthorities(Authority.USER.toString());
        }
        return accountRepository.save(account);
    }

    public List<Account> findAllAccounts(){
        return accountRepository.findAll();
    }

    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public Optional<Account> findById(Long id) {
        return accountRepository.findById(id);
    }
    public void deleteById(Long id) {
         accountRepository.deleteById(id);
    }



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Account> optionalAccount = accountRepository.findByEmail(email);
        if (!optionalAccount.isPresent()) {
            throw new UsernameNotFoundException("Account not found");
        }

        Account account = optionalAccount.get();

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(account.getAuthorities()));
        return new User(account.getEmail(), account.getPassword(), grantedAuthorities);
    }


}
