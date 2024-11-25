package Javaproject.Feellog.DTO;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

public class UserDTO {
    @Data
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SignUpRequest{
        @Schema(description = "닉네임", example = "test")
        private String userName;
        @Schema(description = "아이디", example = "test123")
        private String userId;
        @Schema(description = "비밀번호", example = "test123")
        private String password;
    }
    @Data
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class UserResponse{
        @Schema(description = "닉네임", example = "test")
        private String userName;
        @Schema(description = "아이디", example = "test123")
        private String userId;
    }

    @Data
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class LoginRequest{
        private String userId;
        private String password;
    }

    @Data
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class LoginResponse{
        private String token;
        private String userName;

        public LoginResponse(String token, String userName){
            this.token=token;
            this.userName=userName;
        }
    }

    @Data
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class UserUpdateRequest{
        private String userName;
    }

    @Data
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class UserDeleteRequest{
        private String token;
    }
}
