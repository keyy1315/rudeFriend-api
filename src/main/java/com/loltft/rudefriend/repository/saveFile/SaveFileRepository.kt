package com.loltft.rudefriend.repository.saveFile

import com.loltft.rudefriend.entity.SaveFile
import org.springframework.data.repository.CrudRepository
import java.util.UUID

/**
 * 업로드된 파일 메타데이터를 관리하는 저장소.
 */
interface SaveFileRepository : CrudRepository<SaveFile?, UUID?> {

    /**
     * 게시글 ID로 모든 파일을 조회한다.
     */
    fun findAllByBoardId(boardId: UUID): List<SaveFile>

    /**
     * URL 목록에 포함된 파일을 모두 조회한다.
     */
    fun findAllByFullUrlIn(fullUrls: List<String>): List<SaveFile>
}
