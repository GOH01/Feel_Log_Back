package Javaproject.Feellog.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Emotion {
    @Id
    private Long id;
    private String emotionType;

    public Emotion(Long id, String emotionType){
        this.id=id;
        this.emotionType=emotionType;
    }

}
