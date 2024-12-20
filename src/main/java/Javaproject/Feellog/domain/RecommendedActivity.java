package Javaproject.Feellog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RecommendedActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 추천 활동 ID (Primary Key)

    @ManyToOne
    @JoinColumn(name = "emotion_id", nullable = false)
    private Emotion emotion; // 감정 ID (Foreign Key)

    private String name; // 추천 활동 이름

    private String description; // 추천 활동 상세 설명

    public RecommendedActivity(Emotion emotion, String name, String description) {
        this.emotion = emotion;
        this.name = name;
        this.description = description;
    }
}
