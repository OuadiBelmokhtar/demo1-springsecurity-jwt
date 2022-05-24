package me.obelmokhtar.demospringsecurityjwt.sec.services;

import me.obelmokhtar.demospringsecurityjwt.sec.entities.AppRole;
import me.obelmokhtar.demospringsecurityjwt.sec.entities.AppUser;

import java.util.List;

public interface UserAccountService{
    AppUser addNewUser(AppUser appUser);
    AppRole addNewRole(AppRole appRole);
    void addRoleToUser(String roleName, String username);
    AppUser loadUserByUserName(String username);
    List<AppUser> getAllUsers();

}
