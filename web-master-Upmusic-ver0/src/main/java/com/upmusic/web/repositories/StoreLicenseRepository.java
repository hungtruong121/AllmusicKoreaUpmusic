package com.upmusic.web.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.StoreLicense;


@Repository
public interface StoreLicenseRepository extends CrudRepository<StoreLicense, Integer> {
}
