package sf.mephy.study.orm_exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sf.mephy.study.orm_exam.entity.Profile;
import sf.mephy.study.orm_exam.exception.EntityNotFoundException;
import sf.mephy.study.orm_exam.repository.ProfileRepository;
import sf.mephy.study.orm_exam.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public Profile getProfileById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Profile with id " + id + " not found"));
    }

    public Profile createProfile(Profile profile) {
        return profileRepository.save(profile);
    }

    public Profile updateProfile(Long id, Profile profileDetails) {
        Profile profile = getProfileById(id);
        profile.setBio(profileDetails.getBio());
        profile.setAvatarUrl(profileDetails.getAvatarUrl());
        profile.setContactInfo(profileDetails.getContactInfo());
        return profileRepository.save(profile);
    }
}
