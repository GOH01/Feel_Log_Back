package Javaproject.Feellog.controller;

import Javaproject.Feellog.DTO.DiaryContentRequest;
import Javaproject.Feellog.DTO.DiarySummaryResponse;
import Javaproject.Feellog.domain.Diary;
import Javaproject.Feellog.domain.User;
import Javaproject.Feellog.exception.InvalidTokenException;
import Javaproject.Feellog.service.DiaryService;
import Javaproject.Feellog.service.UserService;
import Javaproject.Feellog.utils.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/diaries")
@RequiredArgsConstructor

public class DiaryController {
    private final DiaryService diaryService;
    private final UserService userService;
    private final JwtUtility jwtUtility;

    // 일기 저장
    @PostMapping("/save")
    public ResponseEntity<DiarySummaryResponse> saveDiary(
            @RequestHeader("Authorization") String token,
            @RequestBody DiaryContentRequest request
    ) {
        try {
            String tokenWithoutBearer = token.replace("Bearer ", "").trim();
            String userId = userService.tokenToUser(tokenWithoutBearer).getUserId();

            Diary savedDiary = diaryService.saveDiary(userId, request.getContent());

            return ResponseEntity.ok(new DiarySummaryResponse(savedDiary));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    // 특정 날짜의 일기 수정
    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> updateDiaryByDate(
            @RequestHeader("Authorization") String token,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody DiaryContentRequest request
    ) {
        // 서비스에서 Diary 수정
        Diary updatedDiary = diaryService.updateDiary(request.getContent(), token, date);

        // 반환할 데이터를 Map으로 구성
        Map<String, String> response = new HashMap<>();
        response.put("updatedContent", updatedDiary.getContent());

        return ResponseEntity.ok(response);
    }




    // 특정 날짜의 일기 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteDiaryByDate(
            @RequestHeader("Authorization") String token,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        diaryService.deleteDiary(token, date);
        return ResponseEntity.ok("일기가 성공적으로 삭제되었습니다.");
    }

    // 일기 수정
//    @PutMapping("/update/{id}")
//    public ResponseEntity<DiarySummaryResponse> updateDiary(
//            @RequestHeader("Authorization") String token,
//            @PathVariable Long id,
//            @RequestBody DiaryContentRequest request
//    ) {
//        // "Bearer " 제거
//        String tokenWithoutBearer = token.replace("Bearer ", "").trim();
//        String userId = userService.tokenToUser(tokenWithoutBearer).getUserId(); // 토큰 검증
//        Diary updatedDiary = diaryService.updateDiary(userId, id, request.getContent());
//
//        // 수정된 Diary를 DiarySummaryResponse로 변환하여 반환
//        DiarySummaryResponse response = new DiarySummaryResponse(updatedDiary);
//        return ResponseEntity.ok(response);
//    }
//
//    // 일기 삭제
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<String> deleteDiary(
//            @RequestHeader("Authorization") String token,
//            @PathVariable Long id
//    ) {
//        // "Bearer " 제거
//        String tokenWithoutBearer = token.replace("Bearer ", "").trim();
//        String userId = userService.tokenToUser(tokenWithoutBearer).getUserId(); // 토큰 검증
//        diaryService.deleteDiary(userId, id);
//        return ResponseEntity.ok("Diary deleted successfully.");
//    }

    // 특정 날짜의 일기 조회
    @GetMapping("/user/date")
    public ResponseEntity<DiarySummaryResponse> getDiariesByDate(
            @RequestHeader("Authorization") String token,
            @RequestParam String date
    ) {
        // "Bearer " 제거
        String tokenWithoutBearer = token.replace("Bearer ", "").trim();
        String userId = userService.tokenToUser(tokenWithoutBearer).getUserId(); // 토큰 검증

        // 특정 날짜와 userId로 일기 검색
        Diary diary = diaryService.getDiariesByUserAndDate(userId, date);

        // 응답 객체로 변환
        DiarySummaryResponse response = new DiarySummaryResponse(diary);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/count")
    public ResponseEntity<Long> getUserDiariesCount(@RequestHeader("Authorization") String token) {
        try {
            // Bearer 토큰 처리
            String tokenWithoutBearer = token.replace("Bearer ", "").trim();

            // 디버깅을 위한 로그
            System.out.println("받은 토큰: " + tokenWithoutBearer);

            User user = userService.tokenToUser(tokenWithoutBearer);
            System.out.println("찾은 사용자 ID: " + user.getUserId());

            long diaryCount = diaryService.getUserDiaryCount(user.getUserId());
            System.out.println("조회된 일기 수: " + diaryCount);

            return ResponseEntity.ok(diaryCount);
        } catch (Exception e) {
            // 상세한 에러 로깅
            System.err.println("일기 수 조회 중 에러 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(-1L); // 에러 시 -1 반환
        }
    }

    @GetMapping("/latest")
    public ResponseEntity<LocalDate> getLatestDate(@RequestHeader("Authorization") String token){
        try{
            String userToken= jwtUtility.bearerToken(token);
            LocalDate latestDate=diaryService.getLatestDiary(userToken);
            return ResponseEntity.ok(latestDate);
        }catch (InvalidTokenException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

}
