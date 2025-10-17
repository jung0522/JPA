package io.goorm.jpa.one2one.a.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_one2one_a_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserA {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    
    private String email;
    
    public UserA(String username, String email) {
        this.username = username;
        this.email = email;
    }
    
    public void updateInfo(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
