package com.loltft.rudefriend.repository.board;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loltft.rudefriend.entity.Board;

public interface BoardRepository extends JpaRepository<Board, UUID> {

}
