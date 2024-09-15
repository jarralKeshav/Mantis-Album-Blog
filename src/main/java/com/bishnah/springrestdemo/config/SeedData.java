package com.bishnah.springrestdemo.config;

import com.bishnah.springrestdemo.model.Account;
import com.bishnah.springrestdemo.service.AccountService;
import com.bishnah.springrestdemo.util.constants.Authority;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SeedData implements CommandLineRunner {

    private AccountService accountService;

    public SeedData(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void run(String... args) throws Exception {

        Account account01 = new Account();
        Account account02 = new Account();


        account01.setEmail("user01@user.com");
        account01.setPassword("password$01");
        account01.setAuthorities(Authority.USER.toString());
        accountService.createAccount(account01);


        account02.setEmail("admin@admin.com");
        account02.setPassword("adminpassword$01");
        account02.setAuthorities(Authority.ADMIN.toString() + " " + Authority.USER.toString());
        accountService.createAccount(account02);

    }
}
