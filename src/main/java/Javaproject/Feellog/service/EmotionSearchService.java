package Javaproject.Feellog.service;

import Javaproject.Feellog.DTO.EmotionTopDateDTO;
import Javaproject.Feellog.repository.EmotionPerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmotionSearchService {
    private final EmotionPerRepository emotionPerRepository;

    public List<EmotionTopDateDTO> getTopDatesByEmotion(String emotionType) {
        // EmotionPerRepository에서 감정별로 가장 높은 비율을 가진 날짜 조회
        List<Object[]> results = emotionPerRepository.findTopDatesByEmotion(emotionType);

        if (results.isEmpty()) {
            throw new IllegalArgumentException("해당 감정에 대한 데이터가 없습니다.");
        }

        // Object[] 결과를 DTO로 변환
        return results.stream()
                .map(result -> new EmotionTopDateDTO((LocalDate) result[0], (Double) result[1]))
                .collect(Collectors.toList());
    }
}
