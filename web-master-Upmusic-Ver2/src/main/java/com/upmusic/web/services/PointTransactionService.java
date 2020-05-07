package com.upmusic.web.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.upmusic.web.config.UPMusicConstants.PointTransactionType;
import com.upmusic.web.domain.MemberPlaytime;
import com.upmusic.web.domain.PointTransaction;
import com.upmusic.web.message.PointTransactionResponse;
import com.upmusic.web.message.WebsocketTrackPlayingMessage;

import javax.swing.text.html.Option;


@Transactional
public interface PointTransactionService {

	/**
	 * 회원의 오늘 총 청취시간을 반환
	 * @param memberId
	 * @return MemberPlaytime
	 */
	MemberPlaytime findOrCreatePlaytime(Long memberId);
	
	/**
	 * 회원의 해당일 총 청취시간을 반환
	 * @param member
	 * @param date
	 * @return MemberPlaytime
	 * @throws ParseException 
	 */
	MemberPlaytime findByMemberAndDate(Long memberId, String date) throws ParseException;
	
	/**
	 * 회원의 오늘 총 청취시간이 속한 스트리밍 리워드 단계를 반환
	 * @param playtime
	 * @return int
	 */
	int getStreamingRewardStep(Long playtime);
	
	/**
	 * 회원의 오늘 적립한 스트리밍 리워드 포인트를 반환
	 * @param memberId
	 * @return float
	 */
	float getTodayStreamingPoint(Long memberId);
	
	/**
	 * 회원의 정산내역 목록에서 제외
	 * @param id
	 * @param id2
	 * @return
	 */
	boolean removeFromMember(Long id, Long memberId);

	/**
	 * 회원의 업뮤직 포인트 충전
	 * @param id
	 * @param id2
	 * @return
	 */
	boolean addChargePoint(Long memberId, PointTransactionType transactionType, BigDecimal chargePoint, String serviceCode, String chargeTransactionId, String chargeStatus);

	void updateChargePoint(Long id);

	/**
	 * 청취에 의한 리워드 적립
	 * @param memberId
	 * @param musicTrackId
	 * @return WebsocketTrackPlayingMessage
	 */
	WebsocketTrackPlayingMessage createOrUpdateRewardListen(Long memberId, Long musicTrackId);
	
	/**
	 * 포인트 적립
	 * @param memberId
	 * @param transactionType
	 * @param transactionTypeId
	 * @param point : unsigned
	 * @return PointTransactionResponse
	 */
	PointTransactionResponse accumulatePoints(Long memberId, PointTransactionType transactionType, Long transactionTypeId, float point);
	
	/**
	 * 포인트 사용
	 * @param memberId
	 * @param transactionType
	 * @param transactionTypeId
	 * @param point : unsigned
	 * @return PointTransactionResponse
	 */
	PointTransactionResponse usePoints(Long memberId, PointTransactionType transactionType, Long transactionTypeId, float point);
	
	/**
	 * 거래 취소
	 * @param pointTransactionId
	 * @return PointTransactionResponse
	 */
	PointTransactionResponse cancelTransaction(Long pointTransactionId);
	
	/**
	 * 회원의 전체 거래 페이지 반환
	 * @param memberId
	 * @param pageRequest
	 * @return Page<PointTransaction>
	 */
	Page<PointTransaction> listMemberAllTransaction(Long memberId, PageRequest pageRequest);
	
	/**
	 * 회원의 적립내역 페이지 반환
	 * @param memberId
	 * @param pageRequest
	 * @return
	 */
	Page<PointTransaction> listMemberAccumulatedTransaction(Long memberId, PageRequest pageRequest);
	
	/**
	 * 회원의 사용내역 페이지 반환
	 * @param memberId
	 * @param pageRequest
	 * @return
	 */
	Page<PointTransaction> listMemberUsedTransaction(Long memberId, PageRequest pageRequest);

	/**
	 * 회원의 해당일 정산내역을 반환
	 * @param member
	 * @param date
	 * @return MemberPlaytime
	 * @throws ParseException 
	 */
	JsonObject getDailyTransaction(Long memberId, String date) throws ParseException;

	/**
	 * 회원의 해당일 공유리워드내역을 반환
	 * @param member
	 * @param date
	 * @return MemberPlaytime
	 * @throws ParseException
	 */
	List<PointTransaction> getDailyShareTransaction(Long memberId, String date) throws ParseException;

	/**
	 * 회원의 해당월 사용내역을 반환
	 * @param member
	 * @param month
	 * @return MemberPlaytime
	 * @throws ParseException 
	 */
	Page<PointTransaction> getMonthlyUseTransaction(Long memberId, String month, PageRequest pageRequest) throws ParseException;

	/**
	 * 회원의 해당월 사용,충전내역을 반환
	 * @param member
	 * @param month
	 * @return MemberPlaytime
	 * @throws ParseException
	 */
	Page<PointTransaction> getMonthlyAllTransaction(Long memberId, String month, PageRequest pageRequest) throws ParseException;

	/**
	 * 회원의 해당월 충전내역을 반환
	 * @param member
	 * @param month
	 * @return MemberPlaytime
	 * @throws ParseException
	 */
	Page<PointTransaction> getMonthlyChargeTransaction(Long memberId, String month, PageRequest pageRequest) throws ParseException;



}
