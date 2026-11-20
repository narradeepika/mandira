package com.dhabaleswara.temple.repository;

import com.dhabaleswara.temple.model.Ritual;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RitualRepository extends JpaRepository<Ritual, Long> {
    List<Ritual> findByActiveTrueOrderBySortOrderAsc();
    List<Ritual> findAllByOrderBySortOrderAsc();
}
