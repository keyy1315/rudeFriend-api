package com.loltft.rudefriend.repository.vote

import com.loltft.rudefriend.entity.VoteSummary
import java.util.UUID

/**
 * 투표 엔티티와 집계 테이블을 함께 조작하는 커스텀 리포지토리.
 */
interface VoteRepositoryCustom {
    /**
     * 투표 항목 집계를 일괄 저장한다.
     */
    fun saveAllVoteSummaries(voteSummaries: Collection<VoteSummary>): List<VoteSummary>

    /**
     * 단일 투표 집계를 저장한다.
     */
    fun saveVoteSummary(voteSummary: VoteSummary): VoteSummary

    /**
     * 게시글 전체 투표 집계를 조회한다.
     */
    fun findAllSummariesByBoardId(boardId: UUID): List<VoteSummary>

    /**
     * 게시글 전체 집계를 삭제한다.
     */
    fun deleteAllSummariesByBoardId(boardId: UUID)

    /**
     * 게시글의 특정 항목 집계를 삭제한다.
     */
    fun deleteSummariesByBoardIdAndVoteItemIn(boardId: UUID, voteItems: Collection<String>)

    /**
     * 특정 항목의 집계를 증분 갱신한다.
     *
     * @return 영향을 받은 행 수
     */
    fun applySummaryDelta(boardId: UUID, voteItem: String, delta: Long): Long
}
