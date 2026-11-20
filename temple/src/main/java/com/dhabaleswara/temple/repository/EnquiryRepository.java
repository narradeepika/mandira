package com.dhabaleswara.temple.repository;

import com.dhabaleswara.temple.model.Enquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EnquiryRepository extends JpaRepository<Enquiry, Long> {
    long countByResolved(boolean resolved);

    @Query("select e from Enquiry e where :q is null or :q = '' "
         + "or lower(e.name) like lower(concat('%', :q, '%')) "
         + "or lower(e.email) like lower(concat('%', :q, '%')) "
         + "or lower(e.subject) like lower(concat('%', :q, '%')) "
         + "or lower(e.message) like lower(concat('%', :q, '%'))")
    Page<Enquiry> search(@Param("q") String q, Pageable pageable);
}
