package com.bishnah.springrestdemo.controller;

import com.bishnah.springrestdemo.model.Account;
import com.bishnah.springrestdemo.payload.auth.*;
import com.bishnah.springrestdemo.service.AccountService;
import com.bishnah.springrestdemo.service.TokenService;
import com.bishnah.springrestdemo.util.constants.AccountError;
import com.bishnah.springrestdemo.util.constants.AccountSuccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth Controller", description = "Controller for Account Management")
@Slf4j
public class AuthController {


    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AccountService accountService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService, AccountService accountService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.accountService = accountService;
    }


    @PostMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TokenDTO> token(@Valid @RequestBody UserLoginDTO userLogin) throws AuthenticationException {
        try {           
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLogin.getEmail(),
                            userLogin.getPassword()));  
            return ResponseEntity.ok(new TokenDTO(tokenService.generateToken(authentication)));
        } catch (Exception e) {
            log.debug(AccountError.TOKEN_GENERATION_ERROR.toString()+" "+ e.getMessage());
            return new ResponseEntity<>(new TokenDTO(null), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/users/add", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED) //optional
    @ApiResponse(responseCode = "401", description = "Please enter a valid email and Password length btw 6 and 20 " +
            "characters")

    @ApiResponse(responseCode = "200", description = "Account Added")
    @Operation(summary = "Add a new User")
    public ResponseEntity<String> addUser(@Valid @RequestBody AccountDTO accountDTO) {

        try {
            Account account = new Account();
            account.setEmail(accountDTO.getEmail());
            account.setPassword(accountDTO.getPassword());
//            account.setRole("ROLE_USER");
            accountService.createAccount(account);
            return ResponseEntity.ok(AccountSuccess.ACCOUNT_ADDED.toString());

        } catch (Exception e) {
            log.debug(AccountError.ADD_ACCOUNT_ERROR.toString() + " " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        }

    }


    @GetMapping(value = "/users", produces = "application/json")
    @ApiResponse(responseCode = "201", description = "List of Users")
    @ApiResponse(responseCode = "401", description = "Please check Access Token")
    @ApiResponse(responseCode = "403", description = "Token/Scope Error")
    @Operation(summary = "List users Api")
    @SecurityRequirement(name = "Spring-Demo-Api")
    public List<AccountViewDTO> Users() {
        List<AccountViewDTO> accountViewDTOS = new ArrayList<>();
        for (Account account:  accountService.findAllAccounts()){
            accountViewDTOS.add(new AccountViewDTO(account.getId(),account.getEmail(),account.getAuthorities()));
        }

        return accountViewDTOS;

    }

    @PutMapping(value = "/users/{user_id}/update-authorities", produces = "application/json", consumes = "application" +
            "/json")
    @ApiResponse(responseCode = "200", description = "Update Authorities")
    @ApiResponse(responseCode = "400", description = "Invalid User")
    @ApiResponse(responseCode = "401", description = "Please check Access Token")
    @ApiResponse(responseCode = "403", description = "Token/Scope Error")
    @Operation(summary = "Update authorities")
    @SecurityRequirement(name = "Spring-Demo-Api")
    public ResponseEntity<AccountViewDTO> update_authorities(@Valid @RequestBody AuthoritiesDTO authoritiesDTO,
    @PathVariable Long user_id) {
        Optional<Account> optionalAccount = accountService.findById(user_id);

        if(optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            account.setAuthorities(authoritiesDTO.getAuthorities());
            accountService.createAccount(account);
            AccountViewDTO accountViewDTO = new AccountViewDTO(account.getId(), account.getEmail(), account.getAuthorities());
            return ResponseEntity.ok(accountViewDTO);

        }
        return new ResponseEntity<AccountViewDTO>(new AccountViewDTO(), HttpStatus.BAD_REQUEST);


    }



    @GetMapping(value = "/profile", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "View Profile")
    @ApiResponse(responseCode = "401", description = "Please check Access Token")
    @ApiResponse(responseCode = "403", description = "Token/Scope Error")
    @Operation(summary = "View Profile")
    @SecurityRequirement(name = "Spring-Demo-Api")
    public ProfileDTO profile(Authentication authentication) {
            String email = authentication.getName();
            Optional<Account> optionalAccount = accountService.findByEmail(email);
                Account account = optionalAccount.get();
                ProfileDTO profileDTO = new ProfileDTO(account.getId(), account.getEmail(), account.getAuthorities());
                return profileDTO;



    }

    @PutMapping(value = "/profile/update-password", produces = "application/json", consumes = "application/json")
    @ApiResponse(responseCode = "200", description = "Update Password")
    @ApiResponse(responseCode = "401", description = "Please check Access Token")
    @ApiResponse(responseCode = "403", description = "Token/Scope Error")
    @Operation(summary = "Update Password")
    @SecurityRequirement(name = "Spring-Demo-Api")
    public AccountViewDTO update_password(@Valid @RequestBody PasswordDTO passwordDTO,Authentication authentication) {
            String email = authentication.getName();
            Optional<Account> optionalAccount = accountService.findByEmail(email);
                Account account = optionalAccount.get();
                account.setPassword(passwordDTO.getPassword());
                accountService.createAccount(account);
                AccountViewDTO accountViewDTO = new AccountViewDTO(account.getId(), account.getEmail(), account.getAuthorities());
                return accountViewDTO;

    }


    @DeleteMapping(value = "/profile/delete")
    @ApiResponse(responseCode = "200", description = "Delete Password")
    @ApiResponse(responseCode = "401", description = "Please check Access Token")
    @ApiResponse(responseCode = "403", description = "Token/Scope Error")
    @Operation(summary = "Delete Profile")
    @SecurityRequirement(name = "Spring-Demo-Api")
    public ResponseEntity<String> delete_profile(Authentication authentication) {
            String email = authentication.getName();
            Optional<Account> optionalAccount = accountService.findByEmail(email);
            if(optionalAccount.isPresent()) {
                accountService.deleteById(optionalAccount.get().getId());
                return ResponseEntity.ok("User deleted successfully");
            }



        return new ResponseEntity<>("Bad Request",HttpStatus.BAD_REQUEST);

    }


}











