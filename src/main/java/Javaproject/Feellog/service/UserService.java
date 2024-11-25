package Javaproject.Feellog.service;

import Javaproject.Feellog.domain.User;
import Javaproject.Feellog.exception.DuplicateUserException;
import Javaproject.Feellog.exception.IdNotFoundException;
import Javaproject.Feellog.exception.InvalidCredentialException;
import Javaproject.Feellog.repository.UserRepository;
import Javaproject.Feellog.utils.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtility jwtUtility;

    public User tokenToUser(String token){
        return userRepository.findByUserId(jwtUtility.validateToken(token).getSubject());
    }

    public User findByUserId(String userId){
        User user=userRepository.findByUserId(userId);
        if(user==null)throw new IdNotFoundException("사용자를 찾을 수 없음");
        return user;
    }

    public List<User> findByUserName(String userName){
        List<User> user = userRepository.findByUserName(userName);
        if(user.isEmpty()) throw new IdNotFoundException("사용자를 찾을 수 없음");
        return user;
    }

    @Transactional
    public User updateUser(String token, String userName){
        User user = tokenToUser(token);
        if(user==null){
            throw new IdNotFoundException("사용자를 찾을수 없음");
        }
        user.updateUser(userName);
        return userRepository.save(user);
    }

    @Transactional
    public User signUp(String userName, String userId, String password){
        User user = userRepository.findByUserId(userId);
        if(user!=null){
            throw new DuplicateUserException("이미 존재하는 사용자");
        }
        return userRepository.save(new User(userName, userId, password));
    }

    public String login(String userId, String password){
        User user = findByUserId(userId);
        if(user != null && user.checkPassword(password)){
            return jwtUtility.generateToken(user.getUserId());
        }
        throw new InvalidCredentialException("아이디 또는 비밀번호가 잘못되었습니다.");
    }

    @Transactional
    public void deleteUser(String token){
        User user = tokenToUser(token);
        if(user==null) throw new IdNotFoundException("사용자를 찾을 수 없음");
        userRepository.delete(user);
    }
}


