package Javaproject.Feellog.service;

import Javaproject.Feellog.domain.Emotion;
import Javaproject.Feellog.domain.RecommendedActivity;
import Javaproject.Feellog.DTO.RecommendedActivityDTO;
import Javaproject.Feellog.repository.EmotionRepository;
import Javaproject.Feellog.repository.RecommendedActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RecommendedActivityService {

    private final RecommendedActivityRepository recommendedActivityRepository;
    private final EmotionRepository emotionRepository;

    /**
     * 감정 유형에 따른 랜덤 추천 활동 조회
     * @param emotionType 감정 유형 (예: joy)
     * @return 랜덤 추천 활동
     */
    public RecommendedActivityDTO.ResponseDTO getRandomActivity(String emotionType) {
        // 감정 유형으로 Emotion 조회
        Emotion emotion = emotionRepository.findByEmotionType(emotionType);
        if (emotion == null) {
            throw new IllegalArgumentException("존재하지 않는 감정 유형입니다.");
        }

        // Emotion ID를 기반으로 추천 활동 리스트 조회
        List<RecommendedActivity> activities = recommendedActivityRepository.findByEmotion_Id(emotion.getId());
        if (activities.isEmpty()) {
            throw new IllegalArgumentException("해당 감정에 대한 추천 활동이 없습니다.");
        }

        // 랜덤으로 하나의 추천 활동 선택
        Random random = new Random();
        RecommendedActivity randomActivity = activities.get(random.nextInt(activities.size()));

        return new RecommendedActivityDTO.ResponseDTO(
                randomActivity.getId(),
                emotion.getEmotionType(),
                randomActivity.getName(),
                randomActivity.getDescription()
        );
    }

    /**
     * 새로운 추천 활동 생성
     * @param createDTO 생성 요청 데이터
     * @return 생성된 추천 활동
     */
    public RecommendedActivityDTO.ResponseDTO createActivity(RecommendedActivityDTO.CreateDTO createDTO) {
        Emotion emotion = emotionRepository.findByEmotionType(createDTO.getEmotionType());
        if (emotion == null) {
            throw new IllegalArgumentException("존재하지 않는 감정 유형입니다.");
        }

        RecommendedActivity activity = new RecommendedActivity(
                emotion,
                createDTO.getName(),
                createDTO.getDescription()
        );

        RecommendedActivity savedActivity = recommendedActivityRepository.save(activity);

        return new RecommendedActivityDTO.ResponseDTO(
                savedActivity.getId(),
                emotion.getEmotionType(),
                savedActivity.getName(),
                savedActivity.getDescription()
        );
    }

    /**
     * 추천 활동 수정
     * @param id 수정할 추천 활동 ID
     * @param updateDTO 수정 요청 데이터
     * @return 수정된 추천 활동
     */
    public RecommendedActivityDTO.ResponseDTO updateActivity(Long id, RecommendedActivityDTO.UpdateDTO updateDTO) {
        RecommendedActivity activity = recommendedActivityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 추천 활동이 존재하지 않습니다."));

        // 이름과 설명 업데이트
        activity.setName(updateDTO.getName());
        activity.setDescription(updateDTO.getDescription());
        RecommendedActivity updatedActivity = recommendedActivityRepository.save(activity);

        return new RecommendedActivityDTO.ResponseDTO(
                updatedActivity.getId(),
                updatedActivity.getEmotion().getEmotionType(),
                updatedActivity.getName(),
                updatedActivity.getDescription()
        );
    }

    /**
     * 추천 활동 삭제
     * @param id 삭제할 추천 활동 ID
     */
    public void deleteActivity(Long id) {
        if (!recommendedActivityRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 추천 활동이 존재하지 않습니다.");
        }
        recommendedActivityRepository.deleteById(id);
    }
}
