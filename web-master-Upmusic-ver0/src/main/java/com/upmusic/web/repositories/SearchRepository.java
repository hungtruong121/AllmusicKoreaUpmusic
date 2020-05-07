package com.upmusic.web.repositories;

import com.upmusic.web.domain.Search;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface SearchRepository extends JpaRepository<Search, Long> {

	//List<Search> findBy(String name);
	List<Search> findByMemberId(Long memberId);

	@Query(value = "SELECT s FROM Search s WHERE s.member.id = :memberId ORDER BY s.id DESC") // , nativeQuery = true)
	public Page<Search> findTop10ByMemberId(@Param("memberId") Long memberId, Pageable pageable);

	/*@Query(value = "SELECT search_text,count(search_text) as sc FROM Search s GROUP BY search_text ORDER BY sc DESC" , nativeQuery = true)
	public Page<Search> findTop10ByPopularKeyword(Pageable pageable);*/

	@Query(value = "SELECT s.searchText,count(s.searchText) as sc FROM Search s GROUP BY s.searchText ORDER BY sc DESC")
	public Page<Object[]> findTop10ByPopularKeyword(Pageable pageable);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM Search s WHERE s.member.id = :memberId") // , nativeQuery = true)
	void deleteByMemberId(@Param("memberId") Long memberId);

}
