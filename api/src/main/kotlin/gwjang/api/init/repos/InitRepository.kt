package gwjang.api.init.repos

import gwjang.api.init.domain.Init
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface InitRepository : JpaRepository<Init, UUID>
