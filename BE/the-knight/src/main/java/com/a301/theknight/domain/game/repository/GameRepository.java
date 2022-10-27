package com.a301.theknight.domain.game.repository;

import com.a301.theknight.domain.game.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    @EntityGraph(attributePaths = {"players"})
    Page<Game> findByTitleIsContaining(String keyword, Pageable pageable);
}
