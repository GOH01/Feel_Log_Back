package Javaproject.Feellog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@NoArgsConstructor
@Getter @Setter
@Entity
public class User {
    @Id @GeneratedValue
    private Long id;
    @Setter
    private String userName;
    @Column(unique = true)
    private String userId;
    private String password;

    public User(String userName, String userId, String password)
    {
        this.userName=userName;
        this.userId=userId;
        this.setPassword(password);
    }

    public void updateUser(String userName)
    {
        if(userName!=null)this.userName=userName;
    }

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public void setPassword(String password){this.password=passwordEncoder.encode(password);}
    public boolean checkPassword(String rawPassword){
        System.out.println("passwordEncoder = "+passwordEncoder.matches(rawPassword, this.password));
        return passwordEncoder.matches(rawPassword, this.password);
    }
}
