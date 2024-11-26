package Javaproject.Feellog.repository;

import Javaproject.Feellog.domain.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {
    Emotion findByType(String type);
}
