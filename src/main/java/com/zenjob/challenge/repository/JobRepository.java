package com.zenjob.challenge.repository;

import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.enums.JobStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {

}
