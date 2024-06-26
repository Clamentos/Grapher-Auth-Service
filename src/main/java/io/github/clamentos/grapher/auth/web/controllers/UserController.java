package io.github.clamentos.grapher.auth.web.controllers;

///
import io.github.clamentos.grapher.auth.business.services.UserService;

///..
import io.github.clamentos.grapher.auth.error.ErrorCode;
import io.github.clamentos.grapher.auth.error.ErrorFactory;

///..
import io.github.clamentos.grapher.auth.error.exceptions.AuthorizationException;

///..
import io.github.clamentos.grapher.auth.utility.TokenUtils;

///..
import io.github.clamentos.grapher.auth.web.dtos.AuthDto;
import io.github.clamentos.grapher.auth.web.dtos.UserDto;
import io.github.clamentos.grapher.auth.web.dtos.UsernamePassword;

///.
import java.util.List;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.http.ResponseEntity;

///..
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

///
@RestController
@RequestMapping(path = "/v1/grapher/user")

///
public final class UserController {

    ///
    private final UserService service;

    ///
    @Autowired
    public UserController(UserService service) {

        this.service = service;
    }

    ///
    @PostMapping(path = "/register", consumes = "application/json")
    public ResponseEntity<Void> register(@RequestBody UserDto user) {

        service.register(user);
        return(ResponseEntity.ok().build());
    }

    ///..
    @PostMapping(path = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<AuthDto> login(@RequestBody UsernamePassword credentials) {

        return(ResponseEntity.ok(service.login(credentials)));
    }

    ///..
    @DeleteMapping(path = "/logout")
    public ResponseEntity<Void> logout(@RequestAttribute(name = "jwtToken") String jwtToken) {

        service.logout(jwtToken);
        return(ResponseEntity.ok().build());
    }

    ///..
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<UserDto>> getUsers(

        @RequestParam(name = "username", required = false, defaultValue = "") String username,
        @RequestParam(name = "email", required = false, defaultValue = "") String email,
        @RequestParam(name = "createdAtRange", required = false, defaultValue = "") String createdAtRange,
        @RequestParam(name = "updatedAtRange", required = false, defaultValue = "") String updatedAtRange,
        @RequestParam(name = "operations", required = false, defaultValue = "") String operations
    ) {

        return(ResponseEntity.ok(service.getAllUsers(username, email, createdAtRange, updatedAtRange, operations)));
    }

    ///..
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<UserDto> getUser(@PathVariable(name = "id") long id) {

        return(ResponseEntity.ok(service.getUserById(id)));
    }

    ///..
    @PatchMapping(consumes = "application/json")
    public ResponseEntity<Void> updateUser(

        @RequestAttribute(name = "jwtToken") String jwtToken,
        @RequestAttribute(name = "authChecks") boolean[] authChecks,
        @RequestBody UserDto user
    ) {

        boolean canModifyOthers = authChecks.length > 0 ? authChecks[1] : true;
        List<Object> claims = TokenUtils.getClaims(jwtToken, "name", "sub");

        service.updateUser((String)claims.get(0), (long)claims.get(1), user, canModifyOthers);
        return(ResponseEntity.ok().build());
    }

    ///..
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteUser(

        @RequestAttribute(name = "jwtToken") String jwtToken,
        @RequestAttribute(name = "authChecks") boolean[] authChecks,
        @PathVariable(name = "id") long id
    ) {

        boolean canDeleteOthers = authChecks.length > 0 ? authChecks[1] : true;
        List<Object> claims = TokenUtils.getClaims(jwtToken, "name", "sub");

        if(canDeleteOthers == false && id != (long)claims.get(1)) {

            throw new AuthorizationException(ErrorFactory.generate(ErrorCode.ILLEGAL_ACTION));
        }

        service.deleteUser((String)claims.get(0), id);
        return(ResponseEntity.ok().build());
    }

    ///
}
