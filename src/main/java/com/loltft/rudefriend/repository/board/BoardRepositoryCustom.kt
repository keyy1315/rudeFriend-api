package com.loltft.rudefriend.repository.board

import com.loltft.rudefriend.dto.board.BoardResponse
import com.loltft.rudefriend.dto.enums.DateOption
import com.loltft.rudefriend.dto.enums.GameType
import com.loltft.rudefriend.entity.Board
import java.time.LocalDateTime

/**
 * 게시글 목록 조회를 위한 커스텀 쿼리를 정의한다.
 */
interface BoardRepositoryCustom {
    /**
     * 검색/필터 조건에 맞는 게시글을 페이지 단위로 조회한다.
     *
     * @param dateFrom     조회 시작일시
     * @param dateTo       조회 종료일시
     * @param dateOption   날짜 기준(등록/수정)
     * @param search       제목/내용 검색어
     * @param gameType     게임 타입 필터
     * @param pageNo       페이지 번호
     * @param tags         태그 필터
     * @param author       작성자 필터
     * @return 게시글 응답 목록과 전체 건수
     */
    fun findPageWithTotal(
        dateFrom: LocalDateTime?,
        dateTo: LocalDateTime?,
        dateOption: DateOption?,
        search: String?,
        gameType: GameType?,
        pageNo: Int,
        tags: List<String>?,
        author: String?
    ): Pair<MutableList<BoardResponse>, Long>
}
