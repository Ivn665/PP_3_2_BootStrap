package ru.kata.spring.boot_security.demo.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "roles")
@Data
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", unique = true)
    @Enumerated(EnumType.STRING)
    private RoleType name;

    public Role() {
        //
    }


    public Role(RoleType name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return name.toString();
    }

    // We need to override toString() method so role names will be shown in the form
    @Override
    public String toString() {
        return name.toString();
    }

    //equals() and hashCode() must be overridden so Spring MVC and Thymeleaf will show
    // the check marks correctly when the form is in edit mode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return id == role.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public enum RoleType {
        ROLE_USER,
        ROLE_ADMIN
    }
}
