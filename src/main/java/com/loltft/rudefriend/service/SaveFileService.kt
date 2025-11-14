package com.loltft.rudefriend.service

import com.loltft.rudefriend.entity.SaveFile
import com.loltft.rudefriend.repository.saveFile.SaveFileRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.*

@Transactional
@Service
class SaveFileService(
    private val fileRepository: SaveFileRepository,
    private val s3Service: S3Service,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 여러 개의 파일을 S3에 업로드하고 메타데이터를 저장한다.
     * 실패 시 이미 업로드된 파일을 롤백한다.
     *
     * @param gameType    S3 내 게임별 디렉터리 이름
     * @param files       업로드 대상 멀티파트 파일 목록
     * @param boardId     파일이 속한 게시글 ID
     * @return 저장된 [SaveFile] 엔티티 목록
     * @throws IllegalStateException 업로드 또는 저장 중 오류가 발생한 경우
     */
    fun uploadFiles(gameType: String, files: List<MultipartFile>, boardId: UUID): List<SaveFile> {
        if (files.isEmpty()) return emptyList()

        validateFiles(files)

        val uploadResults = mutableListOf<UploadResult>()

        try {
            files.forEach { multipartFile ->
                uploadResults += uploadSingleFile(gameType, multipartFile, boardId)
            }

            val saveFiles = persistUploadedFiles(uploadResults)

            log.info("파일 업로드 완료 - boardId: $boardId, 파일 수: ${saveFiles.size}")
            return saveFiles

        } catch (ex: Exception) {
            log.error("파일 업로드 실패 - boardId: $boardId", ex)
            rollbackUploadedFiles(uploadResults)
            throw IllegalStateException("파일 업로드 중 문제가 발생하였습니다: ${ex.message}", ex)
        }
    }

    /**
     * 모든 파일이 비어 있지 않고 파일명을 가지고 있는지 검증한다.
     *
     * @param files 검증 대상 파일 목록
     */
    private fun validateFiles(files: List<MultipartFile>) {
        files.forEachIndexed { index, file ->
            require(!file.isEmpty) { "빈 파일입니다 - index: $index" }
            require(!file.originalFilename.isNullOrBlank()) { "파일명이 없습니다 - index: $index" }
        }
    }

    /**
     * 단일 파일을 업로드하고 [SaveFile] 정보를 생성한다.
     *
     * @param gameType    S3 디렉터리 이름
     * @param file        업로드할 파일
     * @param boardId     파일이 속한 게시글 ID
     */
    private fun uploadSingleFile(
        gameType: String,
        file: MultipartFile,
        boardId: UUID
    ): UploadResult {
        val fileUuid = UUID.randomUUID()
        val uploadedFileUrl = s3Service.uploadFile(gameType, file, fileUuid)

        val savedFile = SaveFile(
            fileUuid = fileUuid,
            originalFileName = file.originalFilename!!,
            uploadDateTime = LocalDateTime.now(),
            dirName = gameType,
            fullUrl = uploadedFileUrl,
            boardId = boardId
        )

        return UploadResult(uploadedFileUrl, savedFile)
    }

    /**
     * 업로드된 파일 메타데이터를 저장하고 결과를 반환한다.
     *
     * @param uploadResults 업로드된 파일 정보
     * @return 저장된 [SaveFile] 목록
     */
    private fun persistUploadedFiles(uploadResults: List<UploadResult>): List<SaveFile> {
        val saveFiles = uploadResults.map { it.saveFile }
        fileRepository.saveAll(saveFiles)
        return saveFiles
    }

    /**
     * 업로드 중 예외가 발생했을 때 S3에 올라간 파일을 롤백한다.
     *
     * @param uploadResults 롤백 대상 업로드 이력
     */
    private fun rollbackUploadedFiles(uploadResults: List<UploadResult>) {
        if (uploadResults.isEmpty()) return

        try {
            val urls = uploadResults.map { it.url }
            s3Service.deleteFiles(urls)
            log.info("S3 파일 롤백 완료 - ${urls.size}개")
        } catch (ex: Exception) {
            log.error("S3 파일 롤백 실패", ex)
        }
    }

    private data class UploadResult(
        val url: String,
        val saveFile: SaveFile
    )

    /**
     * 게시글에 연결된 모든 파일을 S3와 DB에서 삭제한다.
     *
     * @param boardId 게시글 ID
     */
    fun deleteFilesByBoardId(boardId: UUID) {
        val filesToDelete = fileRepository.findAllByBoardId(boardId)
        if (filesToDelete.isEmpty()) {
            log.info("삭제할 파일이 없습니다 - boardId: $boardId")
            return
        }

        val targetUrls = filesToDelete.map { it.fullUrl }

        deleteFilesInternal(
            filesToDelete = filesToDelete,
            requestedUrls = targetUrls,
            logContext = "boardId: $boardId",
            throwOnFailure = false
        )
    }

    /**
     * 파일 URL 목록으로 파일을 삭제한다.
     * DB에 없는 URL은 경고 로그만 남긴다.
     *
     * @param fullUrls 삭제 대상 파일 URL 목록
     * @throws IllegalStateException S3 삭제가 실패한 경우
     */
    fun deleteFilesByFullUrls(fullUrls: List<String>) {
        if (fullUrls.isEmpty()) return

        val filesToDelete = fileRepository.findAllByFullUrlIn(fullUrls)

        if (filesToDelete.isEmpty()) {
            log.warn("DB에 존재하지 않는 파일들 - urls: $fullUrls")
            return
        }

        deleteFilesInternal(
            filesToDelete = filesToDelete,
            requestedUrls = fullUrls,
            logContext = "urls: $fullUrls",
            throwOnFailure = true
        )
    }

    /**
     * 게시글과 연결된 모든 파일 메타데이터를 조회한다.
     *
     * @param boardId 게시글 ID
     */
    @Transactional(readOnly = true)
    fun findByBoardId(boardId: UUID): List<SaveFile> {
        return fileRepository.findAllByBoardId(boardId)
    }

    /**
     * S3 삭제 결과에 따라 DB 데이터까지 제거하는 공통 삭제 흐름이다.
     *
     * @param filesToDelete    삭제 대상 [SaveFile] 목록
     * @param requestedUrls    삭제 요청에 사용한 URL 리스트
     * @param logContext       로그 구분용 컨텍스트 문자열
     * @param throwOnFailure   실패 시 예외를 던질지 여부
     */
    private fun deleteFilesInternal(
        filesToDelete: List<SaveFile>,
        requestedUrls: List<String>,
        logContext: String,
        throwOnFailure: Boolean
    ) {
        if (filesToDelete.isEmpty()) {
            log.info("삭제할 파일이 없습니다 - $logContext")
            return
        }

        log.info("파일 삭제 시작 - $logContext, 파일 수: ${filesToDelete.size}")

        val deletedUrls = try {
            s3Service.deleteFiles(requestedUrls).toSet()
        } catch (ex: Exception) {
            log.error("S3 파일 삭제 중 오류 발생 - $logContext", ex)
            if (throwOnFailure) {
                throw IllegalStateException("파일 삭제 중 오류가 발생했습니다", ex)
            }
            return
        }

        val successFiles = filesToDelete.filter { it.fullUrl in deletedUrls }
        if (successFiles.isNotEmpty()) {
            fileRepository.deleteAll(successFiles)
            log.info("파일 삭제 완료 - 성공: ${successFiles.size}/${filesToDelete.size}")
        }

        val failedUrls = requestedUrls.filterNot { it in deletedUrls }
        if (failedUrls.isNotEmpty()) {
            log.warn("파일 삭제 실패 - ${failedUrls.size}개: $failedUrls")
        }
    }
}
