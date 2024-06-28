package com.Overseas.overseasproject.repository;


import com.Overseas.overseasproject.model.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {
}
