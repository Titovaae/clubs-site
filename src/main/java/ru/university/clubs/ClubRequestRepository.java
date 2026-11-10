package ru.university.clubs;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubRequestRepository extends JpaRepository<ClubRequest, Long> {
    boolean existsByClubIdAndStudentId(Long clubId, Long studentId);

    List<ClubRequest> findByClubOwnerIdOrderByCreatedAtDesc(Long ownerId);

    void deleteByClubId(Long clubId);
}
