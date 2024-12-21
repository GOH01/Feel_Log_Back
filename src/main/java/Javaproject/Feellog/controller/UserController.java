package Javaproject.Feellog.controller;

import Javaproject.Feellog.DTO.UserDTO.*;
import Javaproject.Feellog.domain.User;
import Javaproject.Feellog.exception.DuplicateUserException;
import Javaproject.Feellog.exception.IdNotFoundException;
import Javaproject.Feellog.exception.InvalidCredentialException;
import Javaproject.Feellog.exception.InvalidTokenException;
import Javaproject.Feellog.service.UserService;
import Javaproject.Feellog.utils.JwtUtility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Controller", description = "사용자 관리 API")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtility jwtUtility;

    @Operation(summary="회원가입", description = "정보를 입력하고 회원가입 시도",
            responses = {@ApiResponse(responseCode = "201", description = "생성 성공 후 토큰 변환"),
                    @ApiResponse(responseCode = "409", description = "중복 아이디로 인한 생성 실패")})
    @PostMapping("/user/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest request){
        try{
            User user = userService.signUp(request.getUserName(), request.getUserId(), request.getPassword());
            String token = userService.login(request.getUserId(), request.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body(token);
        }catch(DuplicateUserException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Operation(summary="로그인", description = "아이디와 패스워드를 입력하고 로그인 시도",
            responses = {@ApiResponse(responseCode = "200", description = "로그인 성공"),
                        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다."),
                        @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호 오류")})
    @PostMapping("/user/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        try{
            String token = userService.login(request.getUserId(), request.getPassword());
            String userName = userService.tokenToUser(token).getUserName();
            return ResponseEntity.ok(new LoginResponse(token, userName));
        }catch(IdNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }catch(InvalidCredentialException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @Operation(summary="사용자 조회", description = "id로 사용자를 조회",
            responses = {@ApiResponse(responseCode = "200", description = "성공"),
                        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")})
    @GetMapping("/user/{id}")
    public ResponseEntity<UserResponse> getUser(@Parameter(description = "사용자 ID", example = "test123")@PathVariable("id") String id){
        try{
            User user = userService.findByUserId(id);
            UserResponse response = new UserResponse(user.getUserName(), user.getUserId());
            return ResponseEntity.ok(response);
        }catch(IdNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary="사용자 업데이트", description = "사용자 정보 업데이트",
            responses = {@ApiResponse(responseCode = "200", description = "업데이트 성공"),
                    @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰"),
                    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")})
    @PutMapping("/user/update")
    public ResponseEntity<UserResponse> updateUser(@RequestHeader("Authorization")String token, @RequestBody UserUpdateRequest request){
        try{
            String userToken = jwtUtility.bearerToken(token);
            User updateUser = userService.updateUser(userToken, request.getUserName());
            return ResponseEntity.ok(new UserResponse(updateUser.getUserName(), updateUser.getUserId()));
        }catch(InvalidTokenException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }catch(IdNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary="사용자 삭제", description = "사용자 삭제",
            responses = {@ApiResponse(responseCode = "204", description = "성공"),
                    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
                    @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰")})
    @DeleteMapping("/user/delete")
    public ResponseEntity<Void> deleteUser(@RequestHeader("Authorization") String token){
        try{
            String userToken = jwtUtility.bearerToken(token);
            userService.deleteUser(userToken);
            return ResponseEntity.noContent().build();
        }catch (InvalidTokenException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }catch (IdNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/user/username")
    public ResponseEntity<String> getUserName(@RequestHeader("Authorization") String token){
        try{
            String userToken=jwtUtility.bearerToken(token);
            User user = userService.tokenToUser(userToken);
            return ResponseEntity.ok(user.getUserName());
        }catch (InvalidTokenException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
