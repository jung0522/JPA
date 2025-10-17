package io.goorm.jpa.one2one.b.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_one2one_b_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserB {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    
    private String email;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id", unique = true, nullable = false)
    private UserProfileB profile;
    
    public UserB(String username, String email) {
        this.username = username;
        this.email = email;
    }
    
    public void setProfile(UserProfileB profile) {
        this.profile = profile;
    }
    
    public void updateInfo(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
