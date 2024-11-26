package Javaproject.Feellog.service;

import Javaproject.Feellog.domain.Diary;
import Javaproject.Feellog.domain.Emotion;
import Javaproject.Feellog.domain.EmotionPer;
import Javaproject.Feellog.repository.DiaryRepository;
import Javaproject.Feellog.repository.EmotionPerRepository;
import Javaproject.Feellog.repository.EmotionRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmotionPerService {

    private final EmotionPerRepository emotionPerRepository;
    private final DiaryRepository diaryRepository;
    private final EmotionRepository emotionRepository;

    @Value("${api.url}")
    private String API_URL;
    @Value("${api.key}") @Getter
    private String API_KEY;

    public void analyzeAndSaveEmotionForDate(LocalDate date){
        //1. 특정 날짜의 일기조회
        Diary diary = diaryRepository.findByDate(date);
        if(diary==null) throw new IllegalArgumentException("해당 날짜의 일기를 찾을 수 없습니다.");

        //2. 감정 분석 API 호출
        JsonObject response = callEmotionAnalysisAPI(diary.getContent());

        JsonArray labelsArray = response.getAsJsonArray("labels");
        JsonArray scoresArray = response.getAsJsonArray("scores");


        List<String> labels = convertToStringList(labelsArray);
        List<Double> scores = convertToDoubleList(scoresArray);

        // 4. 감정 비율 정규화
        List<Double> normalizedScores = normalizeScores(scores);

        Map<String, Double> swappedScores = swapJoyAndAnger(labels, scores);

        saveEmotionPer(diary, labels, swappedScores);
    }

    // JsonArray -> List<String> 변환 메서드
    private List<String> convertToStringList(JsonArray jsonArray) {
        List<String> list = new ArrayList<>();
        jsonArray.forEach(element -> list.add(element.getAsString())); // 각 요소를 String으로 변환
        return list;
    }

    // JsonArray -> List<Double> 변환 메서드
    private List<Double> convertToDoubleList(JsonArray jsonArray) {
        List<Double> list = new ArrayList<>();
        jsonArray.forEach(element -> list.add(element.getAsDouble())); // 각 요소를 Double로 변환
        return list;
    }

    private List<Double> normalizeScores(List<Double> scores) {
        double total = scores.stream().mapToDouble(Double::doubleValue).sum(); // 총합 계산
        List<Double> normalized = new ArrayList<>();

        for (double score : scores) {
            double normalizedValue = roundToTwoDecimalPlaces(score / total); // 정규화 및 소수점 반올림
            normalized.add(normalizedValue);
        }

        return normalized;
    }

    private JsonObject callEmotionAnalysisAPI(String diaryContent){
        try(CloseableHttpClient httpClient = HttpClients.createDefault()){
            HttpPost post = new HttpPost(API_URL);
            post.setHeader("Authorization",API_KEY);
            post.setHeader("Content-Type","application/json");

            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("text",diaryContent);

            post.setEntity(new StringEntity(requestBody.toString()));

            try(CloseableHttpResponse response =httpClient.execute(post)){
                int statusCode = response.getCode();

                if(statusCode == 200){
                    return JsonParser.parseReader(
                            new InputStreamReader(response.getEntity().getContent())
                    ).getAsJsonObject();
                }else{
                    throw new RuntimeException("API 호출 실패: "+response.getReasonPhrase());
                }
            }
        }catch(Exception e){
            throw new RuntimeException("감정 분석 API 호출 중 오류 발생",e);
        }
    }

    private Map<String, Double> swapJoyAndAnger(List<String> labels, List<Double> scores) {
        double joyValue = 0.0, angerValue = 0.0;
        for (int i = 0; i < labels.size(); i++) {
            if("joy".equalsIgnoreCase(labels.get(i))){
                joyValue = scores.get(i);
            }else if ("anger".equalsIgnoreCase(labels.get(i))){
                angerValue = scores.get(i);
            }
        }

        Map<String, Double> swappedScores = new HashMap<>();
        for (int i = 0; i < labels.size(); i++) {
            String label = labels.get(i);
            double value = scores.get(i);

            if ("joy".equalsIgnoreCase(label)) {
                swappedScores.put(label, roundToTwoDecimalPlaces(angerValue)); // anger 값을 joy에 저장
            } else if ("anger".equalsIgnoreCase(label)) {
                swappedScores.put(label, roundToTwoDecimalPlaces(joyValue)); // joy 값을 anger에 저장
            } else {
                swappedScores.put(label, roundToTwoDecimalPlaces(value)); // 다른 감정은 그대로
            }
        }

        return swappedScores;
    }

    private void saveEmotionPer(Diary diary, List<String> labels, Map<String, Double> swappedScroes){
        for(String label : labels){
            Emotion emotion = emotionRepository.findByType(label);
            if(emotion==null) throw new IllegalArgumentException("Emotion 타입이 존재하지 않습니다.");

            EmotionPer emotionPer = new EmotionPer();
            emotionPer.setDiary(diary);
            emotionPer.setEmotion(emotion);
            emotionPer.setPer(swappedScroes.get(label));

            emotionPerRepository.save(emotionPer);
        }
    }

    private  double roundToTwoDecimalPlaces(double value){
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
