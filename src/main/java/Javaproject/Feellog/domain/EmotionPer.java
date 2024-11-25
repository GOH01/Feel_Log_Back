package Javaproject.Feellog.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@NoArgsConstructor
@Entity
public class EmotionPer {
    @Id @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emotion_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Emotion emotion;

    private Double per;
}
