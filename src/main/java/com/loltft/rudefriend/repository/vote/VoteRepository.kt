package com.loltft.rudefriend.repository.vote

import com.loltft.rudefriend.entity.Board
import com.loltft.rudefriend.entity.Member
import com.loltft.rudefriend.entity.Vote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface VoteRepository : JpaRepository<Vote, UUID> {
    fun findByBoardAndMember(board: Board, member: Member): Vote?

    fun findByBoardIdAndIpAddress(boardId: UUID, ipAddress: String): Vote?

    @Query("select v.voteItem as voteItem, count(v) as count from Vote v where v.board.id = :boardId group by v.voteItem")
    fun countVoteGroupByItem(@Param("boardId") boardId: UUID): List<VoteCountProjection>
}

interface VoteCountProjection {
    val voteItem: String
    val count: Long
}
