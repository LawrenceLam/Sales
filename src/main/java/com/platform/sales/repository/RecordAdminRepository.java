package com.platform.sales.repository;

import com.platform.sales.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecordAdminRepository extends JpaRepository<Record, Integer> {
     List<Record> findAllByStatus(String status);

}
