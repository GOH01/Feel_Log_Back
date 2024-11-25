package Javaproject.Feellog.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class Emotion {
    @Id
    private Long id;
    private String emotionType;

    public Emotion(String emotionType){
        this.emotionType=emotionType;
    }
}
