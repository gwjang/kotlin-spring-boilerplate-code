package gwjang.common.domain

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.envers.NotAudited
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import java.util.UUID

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(
    @Id
    var id: UUID = UUID.randomUUID(),
    @CreatedDate
    @NotAudited
    @Column(nullable = false)
    var createdAt: Instant = Instant.now(),
    @CreatedBy
    @NotAudited
    @Column(nullable = false)
    var createdById: UUID = UUID(0, 0),
    @LastModifiedDate
    @Column(nullable = false)
    var modifiedAt: Instant = Instant.now(),
    @LastModifiedBy
    @Column(nullable = false)
    var modifiedById: UUID = UUID(0, 0),
)
