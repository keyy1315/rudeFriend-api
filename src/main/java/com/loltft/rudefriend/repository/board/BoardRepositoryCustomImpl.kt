package com.loltft.rudefriend.repository.board

import com.loltft.rudefriend.dto.board.BoardResponse
import com.loltft.rudefriend.dto.enums.DateOption
import com.loltft.rudefriend.dto.enums.GameType
import com.loltft.rudefriend.entity.QBoard
import com.loltft.rudefriend.entity.QSaveFile
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.dsl.DateTimePath
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.max

@Repository
class BoardRepositoryCustomImpl(em: EntityManager) : BoardRepositoryCustom {
    private val board = QBoard.board
    private val saveFile = QSaveFile.saveFile
    private val queryFactory = JPAQueryFactory(em)

    /**
     * 검색 조건에 맞는 게시글과 전체 건수를 한 번에 조회한다.
     *
     * @param dateFrom     조회 시작일시
     * @param dateTo       조회 종료일시
     * @param dateOption   날짜 기준(등록/수정)
     * @param search       제목/내용 검색어
     * @param gameType     게임 타입 필터
     * @param pageNo       페이지 번호
     * @param tags         태그 필터
     * @param author       작성자 필터
     * @return 게시글 응답과 전체 건수
     */
    override fun findPageWithTotal(
        dateFrom: LocalDateTime?,
        dateTo: LocalDateTime?,
        dateOption: DateOption?,
        search: String?,
        gameType: GameType?,
        pageNo: Int,
        tags: List<String>?,
        author: String?
    ): Pair<MutableList<BoardResponse>, Long> {
        val safePage = max(pageNo, 1)
        val totalExpression = Expressions.numberTemplate(
            Long::class.javaObjectType,
            "COUNT(*) OVER ()"
        )

        val tuples = queryFactory
            .select(
                board,
                totalExpression
            )
            .from(board)
            .where(buildConditions(dateFrom, dateTo, dateOption, search, gameType, tags, author))
            .orderBy(board.createdAt.desc())
            .offset((safePage - 1L) * PAGE_SIZE)
            .limit(PAGE_SIZE)
            .fetch()

        val boards = tuples.mapNotNull { it.get(board) }
        val filesByBoardId = fetchFileUrlsByBoardIds(boards.map { it.id })

        val responses = boards.mapTo(mutableListOf()) { boardEntity ->
            BoardResponse.of(
                boardEntity,
                filesByBoardId[boardEntity.id]?.toMutableList() ?: mutableListOf()
            )
        }

        val total = tuples.firstOrNull()?.get(totalExpression) ?: 0L

        return responses to total
    }

    /**
     * 동적 조회 조건을 생성한다.
     *
     * @param dateFrom     조회 시작일시
     * @param dateTo       조회 종료일시
     * @param dateOption   날짜 기준(등록/수정)
     * @param search       제목/내용 검색어
     * @param gameType     게임 타입 필터
     * @param tags         태그 필터
     * @param author       작성자 필터
     * @return BooleanBuilder 조건
     */
    private fun buildConditions(
        dateFrom: LocalDateTime?,
        dateTo: LocalDateTime?,
        dateOption: DateOption?,
        search: String?,
        gameType: GameType?,
        tags: List<String>?,
        author: String?
    ): BooleanBuilder {
        val builder = BooleanBuilder()

        if (!search.isNullOrBlank()) {
            builder.and(
                board.title.containsIgnoreCase(search)
                    .or(board.content.containsIgnoreCase(search))
            )
        }

        if (gameType != null) {
            builder.and(board.gameType.eq(gameType))
        }

        if (!tags.isNullOrEmpty()) {
            builder.and(board.tags.any().`in`(tags))
        }

        if (!author.isNullOrBlank()) {
            builder.and(board.createdBy.eq(author))
        }

        val datePath: DateTimePath<LocalDateTime?> =
            if (dateOption == DateOption.UPDATE) board.updatedAt else board.createdAt
        applyDateCondition(builder, datePath, dateFrom, dateTo)

        return builder
    }

    /**
     * 게시글 ID 목록으로 저장된 파일 URL을 조회한다.
     *
     * @param boardIds 파일을 조회할 게시글 ID 목록
     * @return 게시글 ID별 파일 URL 맵
     */
    private fun fetchFileUrlsByBoardIds(boardIds: List<UUID>): Map<UUID, List<String>> {
        if (boardIds.isEmpty()) return emptyMap()

        val tuples = queryFactory
            .select(saveFile.boardId, saveFile.fullUrl)
            .from(saveFile)
            .where(saveFile.boardId.`in`(boardIds))
            .fetch()

        return tuples.groupBy(
            { it.get(saveFile.boardId)!! },
            { it.get(saveFile.fullUrl)!! }
        )
    }

    /**
     * 날짜 필터를 BooleanBuilder에 적용한다.
     *
     * @param builder    조건이 누적될 BooleanBuilder
     * @param path       비교 대상 필드
     * @param dateFrom   시작 일시
     * @param dateTo     종료 일시
     */
    private fun applyDateCondition(
        builder: BooleanBuilder,
        path: DateTimePath<LocalDateTime?>,
        dateFrom: LocalDateTime?,
        dateTo: LocalDateTime?
    ) {
        when {
            dateFrom != null && dateTo != null -> builder.and(path.between(dateFrom, dateTo))
            dateFrom != null -> builder.and(path.goe(dateFrom))
            dateTo != null -> builder.and(path.loe(dateTo))
        }
    }

    companion object {
        private const val PAGE_SIZE = 20L
    }
}
