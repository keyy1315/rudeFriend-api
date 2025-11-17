package com.loltft.rudefriend.repository.vote

import com.loltft.rudefriend.entity.QVoteSummary
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import java.util.UUID
import org.springframework.stereotype.Repository

/**
 * QueryDSL을 사용해 투표 집계 테이블을 조작한다.
 */
@Repository
class VoteSummaryRepositoryImpl(em: EntityManager) : VoteSummaryRepositoryCustom {
    private val queryFactory = JPAQueryFactory(em)
    private val voteSummary = QVoteSummary.voteSummary

    override fun applyDelta(boardId: UUID, voteItem: String, delta: Long): Long {
        return queryFactory
            .update(voteSummary)
            .set(voteSummary.voteCount, voteSummary.voteCount.add(delta))
            .where(
                voteSummary.id.boardId.eq(boardId)
                    .and(voteSummary.id.voteItem.eq(voteItem))
            )
            .execute()
    }

    override fun deleteAllByBoardId(boardId: UUID) {
        queryFactory
            .delete(voteSummary)
            .where(voteSummary.id.boardId.eq(boardId))
            .execute()
    }

    override fun deleteAllByBoardIdAndVoteItemIn(boardId: UUID, voteItems: Collection<String>) {
        if (voteItems.isEmpty()) {
            return
        }
        queryFactory
            .delete(voteSummary)
            .where(
                voteSummary.id.boardId.eq(boardId)
                    .and(voteSummary.id.voteItem.`in`(voteItems))
            )
            .execute()
    }
}
