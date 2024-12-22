package Javaproject.Feellog.service;

import Javaproject.Feellog.DTO.EmotionTopDateDTO;
import Javaproject.Feellog.domain.User;
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
    private final UserService userService;

    // 토큰을 사용하여 특정 사용자의 감정 데이터를 조회
    public List<EmotionTopDateDTO> getTopDatesByEmotionAndToken(String token, String emotionType) {
        Long userId = userService.tokenToUser(token).getId(); // 사용자 ID 추출

        // 쿼리 결과 가져오기
        List<Object[]> results = emotionPerRepository.findTopDatesByEmotionAndUser(emotionType, userId);

        if (results == null || results.isEmpty()) {
            throw new IllegalArgumentException("선택한 감정의 최고 비율 날짜가 없습니다.");
        }

        // 결과를 DTO로 변환
        return results.stream()
                .map(result -> new EmotionTopDateDTO((LocalDate) result[0], (Double) result[1]))
                .collect(Collectors.toList());
    }

}
