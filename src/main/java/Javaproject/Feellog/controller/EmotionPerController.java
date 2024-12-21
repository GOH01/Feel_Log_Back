package Javaproject.Feellog.controller;

import Javaproject.Feellog.DTO.EmotionperDTO.*;
import Javaproject.Feellog.domain.Diary;
import Javaproject.Feellog.domain.EmotionPer;
import Javaproject.Feellog.service.DiaryService;
import Javaproject.Feellog.service.EmotionPerService;
import Javaproject.Feellog.service.UserService;
import Javaproject.Feellog.utils.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class EmotionPerController {

    private final EmotionPerService emotionPerService;
    private final UserService userService;
    private final DiaryService diaryService;
    private final JwtUtility jwtUtility;

    @PostMapping("/api/emotion/analyze")
    public String analyzeEmotionForDiary(@RequestHeader("Authorization") String token, @RequestParam String date){
        String userToken= jwtUtility.bearerToken(token);
        LocalDate diaryDate = LocalDate.parse(date);
        emotionPerService.analyzeAndSaveEmotionForDate(userToken,diaryDate);
        return "감정 분석 및 저장 완료: "+date;
    }

    @GetMapping("/statistics/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyStatistics(@RequestParam int year, @RequestParam int month) {
        Map<String, Double> statistics = emotionPerService.getMonthlyEmotionStatistics(year, month);
        Map<String, Object> response = new HashMap<>();
        response.put("month", year + "-" + (month < 10 ? "0" + month : month));
        response.put("statistics", statistics);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/emotion/result")
    public ResponseEntity<List<EmotionPerResponse>> getEmotionsByDate(
            @RequestHeader("Authorization") String token,
            @RequestParam String date) {

        try {
            String userId = userService.tokenToUser(token).getUserId();
            Diary diary = diaryService.getDiariesByUserAndDate(userId, date);

            if (diary==null) {
                return ResponseEntity.noContent().build();
            }

            List<EmotionPer> emotions = emotionPerService.getEmotionPer(diary);

            if (emotions.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList()); // 감정 데이터가 없을 경우 빈 리스트 반환
            }

            // EmotionPer를 EmotionPerResponse로 변환
            List<EmotionPerResponse> response = emotions.stream()
                    .map(emotion -> {
                        EmotionPerResponse emotionResponse = new EmotionPerResponse();
                        emotionResponse.setEmotionId(emotion.getEmotion().getId());
                        emotionResponse.setPer(emotion.getPer());
                        return emotionResponse;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 예외 발생 시 로그 출력 및 JSON 형식으로 오류 메시지 반환
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonList(new EmotionPerResponse())); // 오류 발생 시 기본값 반환
        }
    }
}
