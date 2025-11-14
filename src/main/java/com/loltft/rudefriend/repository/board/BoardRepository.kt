package com.loltft.rudefriend.repository.board

import com.loltft.rudefriend.entity.Board
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

/**
 * 게시글 엔티티를 관리하는 표준 JPA 저장소.
 */
interface BoardRepository : JpaRepository<Board?, UUID?>, BoardRepositoryCustom
