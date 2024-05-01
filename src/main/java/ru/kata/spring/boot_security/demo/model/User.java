package ru.kata.spring.boot_security.demo.model;

import javax.persistence.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


@Data //создаёт геттеры, сеттеры и метод toString
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(min = 2, max = 30, message = "First Name should be between 2 and 30 characters")
    @Column(name = "First_Name")
    private String firstName;

    @Size(min = 2, max = 30, message = "Last Name should be between 2 and 30 characters")
    @Column(name = "Last_Name")
    private String lastName;

    @Min(value = 0, message = "Age should be equal or greater than 0")
    @Max(value = 127, message = "Age should be equal or less than 127")
    @Column(name = "Age")
    private byte age;

//    @UniqueElements(message = "The email address is already occupied")
    @Email(message = "Invalid email address format")
    @Column(name = "Email", unique = true)
    private String email;

    @Size(min = 5, max = 100, message = "Password should be between 5 and 100 characters")
    @Column(name = "password")
    private String password;

    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    public User() {
        //
    }

    public User(String firstName, String lastName, byte age, String email, String password, Set<Role> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { //Возвращает коллекцию прав доступа (не ролей)
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getAuthority()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}

