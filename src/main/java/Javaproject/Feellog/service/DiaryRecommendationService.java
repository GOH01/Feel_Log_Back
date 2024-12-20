package Javaproject.Feellog.service;

import Javaproject.Feellog.domain.EmotionPer;
import Javaproject.Feellog.domain.RecommendedActivity;
import Javaproject.Feellog.repository.EmotionPerRepository;
import Javaproject.Feellog.repository.RecommendedActivityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryRecommendationService {

    private final EmotionPerRepository emotionPerRepository; // 감정 데이터 조회
    private final RecommendedActivityRepository recommendedActivityRepository; // 추천 활동 조회

    /**
     * 특정 일기의 감정 데이터를 기반으로 추천 활동 반환
     */
    public String getRecommendedActivityForDiary(Long diaryId) {
        // 1. 해당 일기의 감정 데이터 가져오기
        List<EmotionPer> emotionPers = emotionPerRepository.findByDiary_Id(diaryId);

        if (emotionPers.isEmpty()) {
            throw new IllegalArgumentException("해당 일기에 대한 감정 데이터가 없습니다.");
        }

        // 2. 가장 높은 감정 점수를 가진 데이터 찾기
        EmotionPer topEmotion = emotionPers.stream()
                .max(Comparator.comparingDouble(EmotionPer::getPer))
                .orElseThrow(() -> new IllegalArgumentException("감정 데이터를 처리할 수 없습니다."));

        Long emotionId = topEmotion.getEmotion().getId(); // 가장 높은 감정의 ID

        // 3. 해당 감정에 대한 추천 활동 랜덤 조회
        List<RecommendedActivity> activities = recommendedActivityRepository.findByEmotion_Id(emotionId);

        if (activities.isEmpty()) {
            throw new IllegalArgumentException("해당 감정에 대한 추천 활동이 없습니다.");
        }

        // 랜덤으로 추천 활동 선택
        Random random = new Random();
        RecommendedActivity randomActivity = activities.get(random.nextInt(activities.size()));

        return randomActivity.getName() + ": " + randomActivity.getDescription();
    }
}

