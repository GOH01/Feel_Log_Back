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
import java.time.format.DateTimeParseException;
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



    @PostMapping("/analyze")
    public ResponseEntity<String> analyzeEmotionForDiary(
            @RequestHeader("Authorization") String token,
            @RequestBody String content
    ) {
        try {
            String userToken = jwtUtility.bearerToken(token);
            emotionPerService.analyzeAndSaveEmotionForDate(userToken, content);
            return ResponseEntity.ok("감정 분석 및 저장이 완료되었습니다.");

        } catch (RuntimeException e) {
            // 중복 일기 작성 등 일반적인 오류 처리
            if (e.getMessage().contains("작성한 일기")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }

            // 감정 분석 API 오류
            if (e.getMessage().contains("감정 분석 중 오류")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("감정 분석 중 오류가 발생하여 저장이 취소되었습니다.");
            }

            // 기타 오류
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("알 수 없는 오류가 발생했습니다.");
        }
    }
    @PutMapping("/diary/update")
    public ResponseEntity<String> updateEmotionForDiary(@RequestHeader("Authorization") String token, @RequestParam String date, @RequestBody String content) {
        try {
            // 1. Bearer 토큰 처리
            String userToken = jwtUtility.bearerToken(token);

            // 2. 날짜 파싱
            LocalDate diaryDate = LocalDate.parse(date);

            // 3. 감정 분석 및 저장 수행
            emotionPerService.updateEmotionDiary(userToken, diaryDate, content);

            // 4. 성공 메시지 반환
            return ResponseEntity.ok("감정 분석 및 저장 완료: " + date);
        } catch (DateTimeParseException e) {
            // 잘못된 날짜 형식 처리
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 날짜 형식입니다. yyyy-MM-dd 형식으로 입력해주세요.");
        } catch (RuntimeException e) {
            // 429 요청 제한 초과 처리
            if (e.getMessage().contains("429")) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("API 요청 제한 초과. 잠시 후 다시 시도해주세요.");
            }
            // 기타 런타임 오류 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("감정 분석 중 오류가 발생했습니다.");
        } catch (Exception e) {
            // 기타 예상치 못한 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알 수 없는 오류가 발생했습니다.");
        }
    }

    @GetMapping("/statistics/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyStatistics(@RequestHeader("Authorization") String token, @RequestParam int year, @RequestParam int month) {
        String userToken = jwtUtility.bearerToken(token);
        Map<String, Double> statistics = emotionPerService.getMonthlyEmotionStatistics(userToken, year, month);
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
