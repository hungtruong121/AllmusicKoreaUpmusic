package com.upmusic.web.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.upmusic.web.domain.Role;


@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {
	
	Role findByName(String name);
}
