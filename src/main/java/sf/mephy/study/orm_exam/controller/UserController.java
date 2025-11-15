package sf.mephy.study.orm_exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sf.mephy.study.orm_exam.dto.request.UserRequest;
import sf.mephy.study.orm_exam.dto.response.UserResponse;
import sf.mephy.study.orm_exam.entity.User;
import sf.mephy.study.orm_exam.mapper.UserMapper;
import sf.mephy.study.orm_exam.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return userMapper.toResponse(user);
    }

    @PostMapping
    public UserResponse createUser(@RequestBody UserRequest userRequest) {
        User user = userMapper.toEntity(userRequest);
        User createdUser = userService.createUser(user);
        return userMapper.toResponse(createdUser);
    }
    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        User updatedUser = userService.updateUser(id, userRequest);
        return userMapper.toResponse(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
