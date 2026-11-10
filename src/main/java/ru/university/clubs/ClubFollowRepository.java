package ru.university.clubs;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubFollowRepository extends JpaRepository<ClubFollow, Long> {
    boolean existsByClubIdAndStudentId(Long clubId, Long studentId);

    List<ClubFollow> findByClubId(Long clubId);

    List<ClubFollow> findByStudentIdOrderByClubNameAsc(Long studentId);

    void deleteByClubIdAndStudentId(Long clubId, Long studentId);

    void deleteByClubId(Long clubId);
}
