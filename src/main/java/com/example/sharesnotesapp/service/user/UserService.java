package com.example.sharesnotesapp.service.user;

import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.request.UserNameDto;
import com.example.sharesnotesapp.model.dto.request.UserRequestDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    void validateEmail(String email);
    User saveUser(UserRequestDto userRequestDto);
    void deleteUser(Long id);
    User updateUserCredentials(Long id, UserNameDto userRequestDto);
    List<User> getUserFriends(User user);
    void removeFromFriendList(User user, Long friendId);
    List<User> searchUsers(String string, Long currentUserId);
    List<User> searchUserFriends(String string, Long currentUserId);
}
