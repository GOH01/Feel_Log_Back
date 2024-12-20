package Javaproject.Feellog.domain;

import Javaproject.Feellog.repository.EmotionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmotionDataInitializer implements CommandLineRunner {

    private final EmotionRepository emotionRepository;
    public EmotionDataInitializer(EmotionRepository emotionRepository){
        this.emotionRepository=emotionRepository;
    }

    @Override
    public void run(String...args) throws Exception{
        //아마 데아터가 있는 경우 초기화를 건너뜀
        if(emotionRepository.count()==0){
            List<Emotion> emotion = List.of(
                    new Emotion(1L,"joy"),
                    new Emotion(2L,"anger"),
                    new Emotion(3L,"fear"),
                    new Emotion(4L,"sadness"),
                    new Emotion(5L,"love"),
                    new Emotion(6L,"surprise")
            );
            emotionRepository.saveAll(emotion);
            System.out.println("감정 데이터를 초기화 했습니다.");
        }
    }
}
