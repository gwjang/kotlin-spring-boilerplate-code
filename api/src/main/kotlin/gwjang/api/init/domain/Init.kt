package gwjang.api.init.domain

import gwjang.common.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity

@Entity
class Init : BaseEntity() {
    @Column
    var init: String? = null
}
