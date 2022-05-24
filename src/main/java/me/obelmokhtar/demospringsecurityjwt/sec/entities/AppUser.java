package me.obelmokhtar.demospringsecurityjwt.sec.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor @AllArgsConstructor
// le nom de la classe User est reserve par Spring
public class AppUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    // association unidirectionnel
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<AppRole> userRoles=new ArrayList<>();
}
