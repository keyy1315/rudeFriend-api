package com.loltft.rudefriend.repository.vote

import com.loltft.rudefriend.entity.VoteSummary
import com.loltft.rudefriend.entity.VoteSummaryId
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

/**
 * 투표 집계 테이블을 조작하는 리포지토리.
 */
interface VoteSummaryRepository :
    JpaRepository<VoteSummary, VoteSummaryId>,
    VoteSummaryRepositoryCustom {
    /**
     * 게시글의 모든 항목 집계를 조회한다.
     */
    fun findAllByIdBoardId(boardId: UUID): List<VoteSummary>
}
