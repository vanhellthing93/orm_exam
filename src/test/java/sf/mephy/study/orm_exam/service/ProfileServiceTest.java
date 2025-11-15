package sf.mephy.study.orm_exam.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import sf.mephy.study.orm_exam.entity.Profile;
import sf.mephy.study.orm_exam.entity.User;
import sf.mephy.study.orm_exam.exception.DuplicateEntityException;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.ProfileRepository;
import sf.mephy.study.orm_exam.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfileService profileService;

    @Test
    public void testGetProfileById_Success() {
        Profile profile = new Profile();
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

        Profile result = profileService.getProfileById(1L);

        assertNotNull(result);
        verify(profileRepository).findById(1L);
    }

    @Test
    public void testGetProfileById_NotFound() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> profileService.getProfileById(1L));
    }

    @Test
    public void testCreateProfile_Success() {
        User user = new User();
        user.setId(1L);
        Profile profile = new Profile();
        profile.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(profileRepository.save(any(Profile.class))).thenAnswer(i -> i.getArgument(0));

        Profile created = profileService.createProfile(profile);

        assertNotNull(created);
        assertEquals(user, created.getUser());
        verify(userRepository).findById(1L);
        verify(profileRepository).save(profile);
    }

    @Test
    public void testCreateProfile_UserNotFound() {
        Profile profile = new Profile();
        User user = new User();
        user.setId(1L);
        profile.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> profileService.createProfile(profile));
    }

    @Test
    public void testCreateProfile_Duplicate() {
        User user = new User();
        user.setId(1L);
        Profile profile = new Profile();
        profile.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(profileRepository.save(profile)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DuplicateEntityException.class, () -> profileService.createProfile(profile));
    }

    @Test
    public void testUpdateProfile_Success() {
        Profile existing = new Profile();
        existing.setBio("Old bio");
        existing.setAvatarUrl("old_url");
        existing.setContactInfo("old_contact");

        Profile updatedDetails = new Profile();
        updatedDetails.setBio("New bio");
        updatedDetails.setAvatarUrl("new_url");
        updatedDetails.setContactInfo("new_contact");

        when(profileRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(profileRepository.save(existing)).thenReturn(existing);

        Profile updated = profileService.updateProfile(1L, updatedDetails);

        assertEquals("New bio", updated.getBio());
        assertEquals("new_url", updated.getAvatarUrl());
        assertEquals("new_contact", updated.getContactInfo());
    }

    @Test
    public void testUpdateProfile_NotFound() {
        Profile updatedDetails = new Profile();
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> profileService.updateProfile(1L, updatedDetails));
    }
}
