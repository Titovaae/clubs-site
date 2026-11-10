package ru.university.clubs;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class FollowService {
    private final ClubRepository clubs;
    private final ClubFollowRepository follows;

    public FollowService(ClubRepository clubs, ClubFollowRepository follows) {
        this.clubs = clubs;
        this.follows = follows;
    }

    @Transactional
    public void follow(Long clubId, AppUser student) {
        if (follows.existsByClubIdAndStudentId(clubId, student.getId())) return;
        ClubFollow f = new ClubFollow();
        f.setClub(clubs.findById(clubId).orElseThrow());
        f.setStudent(student);
        f.setCreatedAt(LocalDateTime.now());
        follows.save(f);
    }

    @Transactional
    public void unfollow(Long clubId, AppUser student) {
        follows.deleteByClubIdAndStudentId(clubId, student.getId());
    }
}
