package Javaproject.Feellog.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class RecommendedActivityDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDTO {
        private String emotionType;
        private String name;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDTO {
        private String name;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseDTO {
        private Long id;
        private String emotionType;
        private String name;
        private String description;
    }
}

