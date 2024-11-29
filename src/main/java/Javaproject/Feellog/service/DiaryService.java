package Javaproject.Feellog.service;

import Javaproject.Feellog.domain.Diary;
import Javaproject.Feellog.domain.User;
import Javaproject.Feellog.repository.DiaryRepository;
import Javaproject.Feellog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    // 일기 저장
    @Transactional
    public Diary saveDiary(String userId, String content) {
        // userId로 User 객체 조회
        User user = userRepository.findByUserId(userId);
       /* if (user == null) {
            throw new RuntimeException("User not found with userId: " + userId);
        }*/

        Diary diary = new Diary(user, LocalDate.now(), content);
        return diaryRepository.save(diary);
    }


    // 일기 수정
    @Transactional
    public Diary updateDiary(String userId, Long diaryId, String content) {
        // 일기 ID로 Diary 조회
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Diary not found"));
//        // 요청한 userId와 Diary의 user가 동일한지 확인
//        if (!diary.getUser().getUserId().equals(userId)) {
//            throw new RuntimeException("Permission denied");
//        }
        // 내용 수정 및 저장
        diary.updateDiary(content);
        return diaryRepository.save(diary);
    }

    // 일기 삭제
    @Transactional
    public void deleteDiary(String userId, Long diaryId) {
        // 일기 ID로 Diary 조회
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Diary not found"));
//        // 요청한 userId와 Diary의 user가 동일한지 확인
//        if (!diary.getUser().getUserId().equals(userId)) {
//            throw new RuntimeException("Permission denied");
//        }
        diaryRepository.delete(diary);
    }


    // 특정 유저의 특정 날짜 일기 조회
    public List<Diary> getDiariesByUserAndDate(String userId, String date) {
        // userId를 사용해 User 엔티티 조회
        User user = userRepository.findByUserId(userId);

        // 문자열로 받은 날짜를 LocalDate로 변환
        LocalDate parsedDate = LocalDate.parse(date);

        // 해당 유저와 날짜에 해당하는 일기 검색
        return diaryRepository.findByUserIdAndDate(user.getId(), parsedDate);
    }
    //유저 ID로 해당 유저의 모든 일기를 조회
    public List<Diary> getUserDiaries(String userId) {
        // UserRepository를 이용해 userId(String)를 기반으로 User 엔티티 조회
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        // User의 Long 타입 id를 DiaryRepository로 전달
        return diaryRepository.findByUserId(user.getId());
    }
}
