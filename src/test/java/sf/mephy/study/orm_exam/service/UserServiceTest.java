package sf.mephy.study.orm_exam.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import sf.mephy.study.orm_exam.dto.request.UserRequest;
import sf.mephy.study.orm_exam.entity.User;
import sf.mephy.study.orm_exam.exception.DuplicateEntityException;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(new User(), new User()));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        verify(userRepository).findAll();
    }

    @Test
    public void testGetUserById_Success() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User found = userService.getUserById(1L);

        assertNotNull(found);
        verify(userRepository).findById(1L);
    }

    @Test
    public void testGetUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    public void testCreateUser_Success() {
        User user = new User();
        when(userRepository.save(user)).thenReturn(user);

        User created = userService.createUser(user);

        assertNotNull(created);
        verify(userRepository).save(user);
    }

    @Test
    public void testCreateUser_Duplicate() {
        User user = new User();
        when(userRepository.save(user)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DuplicateEntityException.class, () -> userService.createUser(user));
    }

    @Test
    public void testUpdateUser_Success() {
        User existing = new User();
        existing.setName("Old name");
        existing.setEmail("old@example.com");
        existing.setRole(User.Role.STUDENT);

        UserRequest request = new UserRequest();
        request.setName("New name");
        request.setEmail("new@example.com");
        request.setRole("TEACHER");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        User updated = userService.updateUser(1L, request);

        assertEquals("New name", updated.getName());
        assertEquals("new@example.com", updated.getEmail());
        assertEquals(User.Role.TEACHER, updated.getRole());
        verify(userRepository).findById(1L);
        verify(userRepository).save(existing);
    }

    @Test
    public void testUpdateUser_NotFound() {
        UserRequest request = new UserRequest();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(1L, request));
    }

    @Test
    public void testDeleteUser_Success() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    public void testDeleteUser_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(1L));
    }
}
