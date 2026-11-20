package com.dhabaleswara.temple.repository;

import com.dhabaleswara.temple.model.Festival;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FestivalRepository extends JpaRepository<Festival, Long> {
    List<Festival> findByActiveTrueOrderBySortOrderAsc();
    List<Festival> findAllByOrderBySortOrderAsc();
}
