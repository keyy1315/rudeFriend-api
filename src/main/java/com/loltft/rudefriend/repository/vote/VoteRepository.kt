package com.loltft.rudefriend.repository.vote

import com.loltft.rudefriend.entity.Board
import com.loltft.rudefriend.entity.Member
import com.loltft.rudefriend.entity.Vote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface VoteRepository : JpaRepository<Vote, UUID> {
    /**
     * 회원과 게시글로 이미 존재하는 투표 기록을 조회한다.
     */
    fun findByBoardAndMember(board: Board, member: Member): Vote?

    /**
     * 특정 게시글에서 IP 기준으로 투표 여부를 확인한다.
     */
    fun findByBoardIdAndIpAddress(boardId: UUID, ipAddress: String): Vote?

    /**
     * 게시글 내 각 항목의 득표수를 그룹화하여 조회한다.
     */
    @Query("select v.voteItem as voteItem, count(v) as count from Vote v where v.board.id = :boardId group by v.voteItem")
    fun countVoteGroupByItem(@Param("boardId") boardId: UUID): List<VoteCountProjection>
}

interface VoteCountProjection {
    val voteItem: String
    val count: Long
}
