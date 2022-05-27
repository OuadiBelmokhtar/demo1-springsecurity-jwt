package me.obelmokhtar.demospringsecurityjwt.sec.repositories;

import me.obelmokhtar.demospringsecurityjwt.sec.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
}
