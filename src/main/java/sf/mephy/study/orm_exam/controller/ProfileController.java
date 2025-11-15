package sf.mephy.study.orm_exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sf.mephy.study.orm_exam.dto.request.ProfileRequest;
import sf.mephy.study.orm_exam.dto.response.ProfileResponse;
import sf.mephy.study.orm_exam.entity.Profile;
import sf.mephy.study.orm_exam.mapper.ProfileMapper;
import sf.mephy.study.orm_exam.service.ProfileService;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final ProfileMapper profileMapper;

    @GetMapping("/{id}")
    public ProfileResponse getProfileById(@PathVariable Long id) {
        Profile profile = profileService.getProfileById(id);
        return profileMapper.toResponse(profile);
    }

    @PostMapping
    public ProfileResponse createProfile(@RequestBody ProfileRequest profileRequest) {
        Profile profile = profileMapper.toEntity(profileRequest);
        Profile createdProfile = profileService.createProfile(profile);
        return profileMapper.toResponse(createdProfile);
    }

    @PutMapping("/{id}")
    public ProfileResponse updateProfile(@PathVariable Long id, @RequestBody ProfileRequest profileRequest) {
        Profile profile = profileMapper.toEntity(profileRequest);
        Profile updatedProfile = profileService.updateProfile(id, profile);
        return profileMapper.toResponse(updatedProfile);
    }
}
