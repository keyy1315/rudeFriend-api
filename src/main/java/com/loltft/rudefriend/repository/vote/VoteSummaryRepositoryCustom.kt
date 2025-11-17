package com.loltft.rudefriend.repository.vote

import java.util.UUID

/**
 * QueryDSL 기반 투표 집계 커스텀 쿼리.
 */
interface VoteSummaryRepositoryCustom {
    /**
     * 게시글의 특정 항목 집계를 증분 갱신한다.
     *
     * @return 영향을 받은 행 수
     */
    fun applyDelta(boardId: UUID, voteItem: String, delta: Long): Long

    /**
     * 게시글의 모든 집계를 삭제한다.
     */
    fun deleteAllByBoardId(boardId: UUID)

    /**
     * 게시글 내 지정된 항목 집계를 삭제한다.
     */
    fun deleteAllByBoardIdAndVoteItemIn(boardId: UUID, voteItems: Collection<String>)
}
