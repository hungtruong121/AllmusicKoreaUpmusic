package com.upmusic.web.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.upmusic.web.config.UPMusicConstants.PointTransactionType;
import com.upmusic.web.domain.PointTransaction;


@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
	
	Iterable<PointTransaction> findAllByMemberId(Long memberId);

	Iterable<PointTransaction> findAllById(Long id);

	public PointTransaction findOneByMemberIdAndTransactionTypeAndCreatedAtGreaterThan(Long memberId, PointTransactionType transactionType, Date createdAt);

	Page<PointTransaction> findAllByMemberId(Long memberId, Pageable pageable);

	@Query(value = "SELECT pt FROM PointTransaction pt WHERE pt.memberId = :memberId AND (pt.point > 0 OR pt.fundingPoint > 0)") // , nativeQuery = true)
	Page<PointTransaction> findAccumulatedTransactionsByMemberId(@Param("memberId") Long memberId, Pageable pageable);
	
	@Query(value = "SELECT pt FROM PointTransaction pt WHERE pt.memberId = :memberId AND (pt.point < 0 OR pt.fundingPoint < 0)") // , nativeQuery = true)
	Page<PointTransaction> findUsedTransactionsByMemberId(@Param("memberId") Long memberId, Pageable pageable);

	@Query(value = "SELECT pt FROM PointTransaction pt WHERE pt.memberId = :memberId AND pt.transactionType IN (1, 2, 8) AND pt.createdAt >= CURRENT_DATE") // , nativeQuery = true) 1,2,8 모든 스트리밍 수익
	List<PointTransaction> findTodayStreamingTransactionsByMemberId(@Param("memberId") Long memberId);

	@Query(value = "SELECT pt FROM PointTransaction pt WHERE pt.memberId = :memberId AND pt.transactionType IN (1, 8) AND pt.createdAt >= :dayStart AND pt.createdAt < :dayEnd") // , nativeQuery = true) 1,8 자신의 청취로 얻은 수익
	List<PointTransaction> findByDateStreamingTransactionsByMemberId(@Param("memberId") Long memberId, @Param("dayStart") Date dayStart, @Param("dayEnd") Date dayEnd);

	@Query(value = "SELECT pt FROM PointTransaction pt WHERE pt.memberId = :memberId AND pt.transactionType = 2 AND pt.createdAt >= :dayStart AND pt.createdAt < :dayEnd") // , nativeQuery = true) 2 저작곡 재생으로 얻은 수익
	List<PointTransaction> findByDateCopyrightTransactionsByMemberId(@Param("memberId") Long memberId, @Param("dayStart") Date dayStart, @Param("dayEnd") Date dayEnd);

	@Query(value = "SELECT pt FROM PointTransaction pt WHERE pt.memberId = :memberId AND pt.transactionType = 3 AND pt.createdAt >= :dayStart AND pt.createdAt < :dayEnd") // , nativeQuery = true) 3 업로드로 얻은 수익
	List<PointTransaction> findByDateUploadTransactionsByMemberId(@Param("memberId") Long memberId, @Param("dayStart") Date dayStart, @Param("dayEnd") Date dayEnd);
	
	@Query(value = "SELECT pt FROM PointTransaction pt WHERE pt.memberId = :memberId AND pt.transactionType = 4 AND pt.createdAt >= :dayStart AND pt.createdAt < :dayEnd") // , nativeQuery = true) 4 공유로 얻은 수익
	List<PointTransaction> findByDateShareTransactionsByMemberId(@Param("memberId") Long memberId, @Param("dayStart") Date dayStart, @Param("dayEnd") Date dayEnd);
	
	@Query(value = "SELECT pt FROM PointTransaction pt WHERE pt.memberId = :memberId AND pt.transactionType = 5 AND pt.createdAt >= :dayStart AND pt.createdAt < :dayEnd") // , nativeQuery = true) 5 이벤트로 얻은 수익
	List<PointTransaction> findByDateEventTransactionsByMemberId(@Param("memberId") Long memberId, @Param("dayStart") Date dayStart, @Param("dayEnd") Date dayEnd);

	//이용내역
	@Query(value = "SELECT new com.upmusic.web.domain.PointTransaction(pt, CASE WHEN f.crowdFundingId IS NULL THEN '' ELSE f.subject END as subject) FROM PointTransaction pt LEFT JOIN CrowdFunding f ON pt.transactionTypeId = f.crowdFundingId WHERE pt.memberId = :memberId AND (pt.removed IS NULL OR pt.removed = 0) AND pt.transactionType IN (6, 7) AND pt.createdAt >= :monthStart AND pt.createdAt < :monthEnd") // , nativeQuery = true) 6, 7 포인트 사용
	Page<PointTransaction> findByMonthUsedTransactionsByMemberId(@Param("memberId") Long memberId, @Param("monthStart") Date monthStart, @Param("monthEnd") Date monthEnd, Pageable pageable);

	//전체
	@Query(value = "SELECT new com.upmusic.web.domain.PointTransaction(pt, CASE WHEN f.crowdFundingId IS NULL THEN '' ELSE f.subject END as subject) FROM PointTransaction pt LEFT JOIN CrowdFunding f ON pt.transactionTypeId = f.crowdFundingId WHERE pt.memberId = :memberId AND (pt.removed IS NULL OR pt.removed = 0) AND pt.transactionType IN (6, 7, 9) AND pt.createdAt >= :monthStart AND pt.createdAt < :monthEnd") // , nativeQuery = true) 6, 7 포인트 사용
	Page<PointTransaction> findByMonthAllTransactionsByMemberId(@Param("memberId") Long memberId, @Param("monthStart") Date monthStart, @Param("monthEnd") Date monthEnd, Pageable pageable);

	//구매/환불내역
	@Query(value = "SELECT pt FROM PointTransaction pt WHERE pt.memberId = :memberId AND (pt.removed IS NULL OR pt.removed = 0) AND pt.transactionType = 9 AND pt.createdAt >= :monthStart AND pt.createdAt < :monthEnd")
	Page<PointTransaction> findByMonthChargeTransactionsByMemberId(@Param("memberId") Long memberId, @Param("monthStart") Date monthStart, @Param("monthEnd") Date monthEnd, Pageable pageable);

}
