package com.loltft.rudefriend.repository.board

import com.loltft.rudefriend.entity.Board
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface BoardRepository : JpaRepository<Board?, UUID?>
