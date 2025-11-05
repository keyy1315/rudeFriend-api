package com.loltft.rudefriend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.loltft.rudefriend.dto.board.BoardRequest;
import com.loltft.rudefriend.dto.board.BoardResponse;
import com.loltft.rudefriend.entity.Board;
import com.loltft.rudefriend.repository.board.BoardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

  private final BoardRepository boardRepository;
  private final S3Service s3Service;

  /**
   * 게시글 생성
   *
   * @param files        업로드 할 video/image []
   * @param boardRequest 게시글 생성 요청 DTO
   * @param authUsername Authentication 객체에 저장된 username
   * @return 생성 된 게시글
   */
  public BoardResponse createBoard(List<MultipartFile> files, BoardRequest boardRequest,
      String authUsername) {
    List<String> fileUrls = new ArrayList<>();

    try {
      for (MultipartFile file : files) {
        String uploadUrl = s3Service.uploadFile(file);
        fileUrls.add(uploadUrl);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    Board board = Board.builder()
        .id(UUID.randomUUID())
        .title(boardRequest.getTitle())
        .content(boardRequest.getContent())
        .gameType(boardRequest.getGameType())
        .tags(boardRequest.getTags())
        .createdBy(authUsername)
        .fileUrls(fileUrls)
        .build();

    boardRepository.save(board);

    return null;
  }
}
