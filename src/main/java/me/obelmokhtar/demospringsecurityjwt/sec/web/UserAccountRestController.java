package me.obelmokhtar.demospringsecurityjwt.sec.web;

import lombok.Data;
import me.obelmokhtar.demospringsecurityjwt.sec.entities.AppRole;
import me.obelmokhtar.demospringsecurityjwt.sec.entities.AppUser;
import me.obelmokhtar.demospringsecurityjwt.sec.repositories.AppRoleRepository;
import me.obelmokhtar.demospringsecurityjwt.sec.repositories.AppUserRepository;
import me.obelmokhtar.demospringsecurityjwt.sec.services.UserAccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserAccountRestController {

    private UserAccountService userAccountService;

    public UserAccountRestController(UserAccountService userAccountService) {
        this.userAccountService=userAccountService;
    }

    @GetMapping(path = "/users")
    List<AppUser> getAllUsers() {
        return userAccountService.getAllUsers();
    }


    @PostMapping(path = "/users")
    AppUser addUser(@RequestBody AppUser appUser){
        return userAccountService.addNewUser(appUser);
    }

    @PostMapping(path = "roles")
    AppRole addRole(@RequestBody AppRole appRole){
        return userAccountService.addNewRole(appRole);
    }

    @PostMapping(path = "/addRoleToUser")
    void addRoleToUser(@RequestBody AppUserRoleForm appUserRoleForm){
       userAccountService.addRoleToUser(appUserRoleForm.getRoleName(), appUserRoleForm.getUsername());
    }
}

@Data
class AppUserRoleForm{
    private String username;
    private String roleName;
}
