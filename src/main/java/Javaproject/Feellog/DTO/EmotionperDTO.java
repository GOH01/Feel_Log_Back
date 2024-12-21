package Javaproject.Feellog.DTO;

import lombok.Data;

public class EmotionperDTO {

    @Data
    public static class EmotionPerResponse{
        private Long emotionId;
        private Double per;
    }
}
