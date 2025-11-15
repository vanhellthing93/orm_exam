package sf.mephy.study.orm_exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import sf.mephy.study.orm_exam.dto.request.UserRequest;
import sf.mephy.study.orm_exam.entity.User;
import sf.mephy.study.orm_exam.exception.DuplicateEntityException;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    public User createUser(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateEntityException("User with this email already exists.");
        }
    }

    public User updateUser(Long id, UserRequest userRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));

        if (userRequest.getName() != null) {
            existingUser.setName(userRequest.getName());
        }

        if (userRequest.getEmail() != null) {
            existingUser.setEmail(userRequest.getEmail());
        }

        if (userRequest.getRole() != null) {
            existingUser.setRole(User.Role.valueOf(userRequest.getRole()));
        }

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
        userRepository.delete(user);
    }
}
