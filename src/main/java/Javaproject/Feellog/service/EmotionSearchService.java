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
        // 토큰에서 사용자 정보 추출
        User user = userService.tokenToUser(token);
        if (user == null) {
            throw new IllegalArgumentException("유효하지 않은 사용자입니다.");
        }

        // EmotionPerRepository에서 사용자 ID와 감정 타입을 기반으로 데이터 조회
        List<Object[]> results = emotionPerRepository.findTopDatesByEmotionAndUser(emotionType, user.getId());

        if (results.isEmpty()) {
            throw new IllegalArgumentException("해당 감정에 대한 데이터가 없습니다.");
        }

        // 조회 결과를 EmotionTopDateDTO로 변환하여 반환
        return results.stream()
                .map(result -> new EmotionTopDateDTO((LocalDate) result[0], (Double) result[1]))
                .collect(Collectors.toList());
    }
}
