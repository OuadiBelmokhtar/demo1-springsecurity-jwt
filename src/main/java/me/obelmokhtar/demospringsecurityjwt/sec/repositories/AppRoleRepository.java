package me.obelmokhtar.demospringsecurityjwt.sec.repositories;

import me.obelmokhtar.demospringsecurityjwt.sec.entities.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRoleRepository extends JpaRepository<AppRole, Long> {
    AppRole findByRoleName(String roleName);

}
