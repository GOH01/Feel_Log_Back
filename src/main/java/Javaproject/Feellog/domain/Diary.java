package Javaproject.Feellog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter @Setter
@Entity
public class Diary {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private LocalDate date;
    private String content;

    private LocalDateTime updateAt;

    public Diary(User user, LocalDate date, String content){
        this.user=user;
        this.date=LocalDate.now();
        this.content=content;
        this.updateAt=LocalDateTime.now();
    }

    public void updateDiary(String content){
        this.content=content;
        this.updateAt=LocalDateTime.now();
    }
}
