package com.zenjob.challenge.entity;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class Shift {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Version
    private long version;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "job_id")
    private Job job;

    private UUID talentId;

    private Instant startTime;
    private Instant endTime;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private Status status;

    public enum Status{
        CREATED,
        BOOKED,
        CANCELED,
        COMPLETED
    }
}
