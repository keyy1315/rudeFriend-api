package com.loltft.rudefriend.repository.vote

import com.loltft.rudefriend.entity.QVoteSummary
import com.loltft.rudefriend.entity.VoteSummary
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import java.util.UUID
import org.springframework.stereotype.Repository

/**
 * Vote 엔티티와 집계 테이블을 함께 다루는 커스텀 구현체.
 */
@Repository
class VoteRepositoryImpl(
    private val entityManager: EntityManager
) : VoteRepositoryCustom {
    private val queryFactory = JPAQueryFactory(entityManager)
    private val voteSummary = QVoteSummary.voteSummary

    override fun saveAllVoteSummaries(voteSummaries: Collection<VoteSummary>): List<VoteSummary> {
        if (voteSummaries.isEmpty()) {
            return emptyList()
        }
        return voteSummaries.map { saveVoteSummary(it) }
    }

    override fun saveVoteSummary(voteSummary: VoteSummary): VoteSummary {
        return entityManager.merge(voteSummary)
    }

    override fun findAllSummariesByBoardId(boardId: UUID): List<VoteSummary> {
        return queryFactory
            .selectFrom(voteSummary)
            .where(voteSummary.id.boardId.eq(boardId))
            .fetch()
    }

    override fun deleteAllSummariesByBoardId(boardId: UUID) {
        queryFactory
            .delete(voteSummary)
            .where(voteSummary.id.boardId.eq(boardId))
            .execute()
    }

    override fun deleteSummariesByBoardIdAndVoteItemIn(boardId: UUID, voteItems: Collection<String>) {
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

    override fun applySummaryDelta(boardId: UUID, voteItem: String, delta: Long): Long {
        return queryFactory
            .update(voteSummary)
            .set(voteSummary.voteCount, voteSummary.voteCount.add(delta))
            .where(
                voteSummary.id.boardId.eq(boardId)
                    .and(voteSummary.id.voteItem.eq(voteItem))
            )
            .execute()
    }
}
