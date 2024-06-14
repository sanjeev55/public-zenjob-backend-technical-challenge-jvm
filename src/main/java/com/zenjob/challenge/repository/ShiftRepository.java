package com.zenjob.challenge.repository;

import com.zenjob.challenge.entity.Shift;
import com.zenjob.challenge.enums.JobStatusEnum;
import com.zenjob.challenge.enums.ShiftStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID> {
    List<Shift> findAllByJobId(UUID jobId);
    List<Shift> findAllByTalentId(UUID talentId);

    @Modifying
    @Transactional
    @Query("UPDATE Shift s set s.shiftStatus = :status where s.id = :shiftId")
    int updateShiftStatus(@Param("shiftId") UUID shiftId, @Param("status") ShiftStatusEnum status);

}
