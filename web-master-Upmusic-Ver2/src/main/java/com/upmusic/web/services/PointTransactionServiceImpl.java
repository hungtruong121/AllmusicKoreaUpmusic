package com.upmusic.web.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.upmusic.web.config.UPMusicConstants;
import com.upmusic.web.config.UPMusicConstants.PointTransactionType;
import com.upmusic.web.domain.Member;
import com.upmusic.web.domain.MemberPlaytime;
import com.upmusic.web.domain.MusicTrack;
import com.upmusic.web.domain.PointRewardCondition;
import com.upmusic.web.domain.PointTransaction;
import com.upmusic.web.helper.UPMusicHelper;
import com.upmusic.web.message.PointTransactionResponse;
import com.upmusic.web.message.WebsocketTrackPlayingMessage;
import com.upmusic.web.repositories.MemberPlaytimeRepository;
import com.upmusic.web.repositories.MemberRepository;
import com.upmusic.web.repositories.MusicTrackRepository;
import com.upmusic.web.repositories.PointRewardConditionRepository;
import com.upmusic.web.repositories.PointTransactionRepository;


@Service
public class PointTransactionServiceImpl implements PointTransactionService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private PointTransactionRepository pointTransactionRepository;
	
	@Autowired
	private MemberPlaytimeRepository playtimeRepository;
	
	@Autowired
	private PointRewardConditionRepository pointRewardConditionRepository;
	
	@Autowired
	private MusicTrackRepository trackRepository;
	
	@Autowired
	private MemberRepository memberRepository;
	
	
	// --------------------------------------------------------------------------------------------
 	// PLAYTIME
	
	@Override
    public MemberPlaytime findOrCreatePlaytime(Long memberId) {
    	logger.debug("findOrCreatePlaytime called");
    	MemberPlaytime playtime = playtimeRepository.findOneByMemberIdAndCreatedAtGreaterThan(memberId, UPMusicHelper.getTodayStart());
    	if (playtime == null) {
    		playtime = new MemberPlaytime();
    		playtime.setMemberId(memberId);
    		playtime.setPlaytime(Long.valueOf(0));
    		playtimeRepository.save(playtime);
    	}
    	return playtime;
    }
	
	@Override
	public MemberPlaytime findByMemberAndDate(Long memberId, String date) throws ParseException {
		logger.debug("findByMemberAndDate called");
    	MemberPlaytime playtime = playtimeRepository.findOneByMemberIdAndCreatedAtGreaterThan(memberId, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(date + " 00:00:00.0"));
    	return playtime;
	}
	
	@Override
	public int getStreamingRewardStep(Long playtime) {
		PointRewardCondition condition = pointRewardConditionRepository.findById(1).orElse(null);
    	if (condition != null) {
    		// 1단계 : 제한시간이 0일 경우에는 무제한
    		if (0 == condition.getListenFirstStepLimit() || playtime < condition.getListenFirstStepLimit() * 60) {
    			return 1;
    		// 2단계
    		} else if (0 == condition.getListenSecondStepLimit() || playtime < (condition.getListenFirstStepLimit() + condition.getListenSecondStepLimit()) * 60) {
    			return 2;
    		// 3단계
    		} else if (0 == condition.getListenThirdStepLimit() || playtime < (condition.getListenFirstStepLimit() + condition.getListenSecondStepLimit() + condition.getListenThirdStepLimit()) * 60) {
    			return 3;
    		}
    	}
    	return 0;
	}
	
	@Override
	public float getTodayStreamingPoint(Long memberId) {
		BigDecimal todaySum = BigDecimal.ZERO;
		List<PointTransaction> transactions = pointTransactionRepository.findTodayStreamingTransactionsByMemberId(memberId);
		for (PointTransaction transaction : transactions) {
			todaySum = todaySum.add(transaction.getPointDecimal()).add(transaction.getFundingPointDecimal());
		}
		return todaySum.floatValue();
	}
	
	@Override
	public boolean removeFromMember(Long id, Long memberId) {
		PointTransaction transaction = pointTransactionRepository.findById(id).orElse(null);
		if (transaction != null && 0 == transaction.getMemberId().compareTo(memberId)) {
			transaction.setRemoved(true);
			pointTransactionRepository.save(transaction);
			return true;
		}
		return false;
	}
	
	@Override
	public synchronized WebsocketTrackPlayingMessage createOrUpdateRewardListen(Long memberId, Long musicTrackId) {
		logger.debug("createOrUpdateRewardListen called");
		MemberPlaytime playtime = findOrCreatePlaytime(memberId);
        // 유효성 검사를 위해 최신 업데이트와 1초 정도의 차이 확인 - 실서버에서 시간차가 로컬 보다 크므로 900 이 아닌 500으로 조정
        logger.debug("time compare : {}", (new Date().getTime() - playtime.getUpdatedAt().getTime()));
        if (0 == playtime.getPlaytime().compareTo(0L) || 500 < new Date().getTime() - playtime.getUpdatedAt().getTime()) {
        	
        	// 청취자 재생시간 업데이트
    		playtime.setPlaytime(playtime.getPlaytime() + 1);
    		playtime = playtimeRepository.save(playtime);
    		
        	/*
        	 * 청취 리워드는 1초마다 적립되므로 개별 생성 보다는 거래 유형의 아이디를 제거하고 업데이트
        	 */
    		MusicTrack track = trackRepository.findById(musicTrackId).orElse(null);
    		if (track != null) {
    			logger.debug("createOrUpdateRewardListen : has track");
    			// 저작권자 자신의 곡 청취
        		if (0 == track.getArtistId().compareTo(memberId)) {

//					synchronized(this){

        			float selfPoint = getListenArtistSelfPoint(playtime.getPlaytime());
        			logger.debug("createOrUpdateRewardListen : 저작권자 자신의 곡 청취 - selfPoint is {}", selfPoint);
                	if (0f < selfPoint) {
                		BigDecimal selfPointDecimal = new BigDecimal(String.valueOf(selfPoint));
                		logger.debug("createOrUpdateRewardListen : 저작권자 자신의 곡 청취 - selfPointDecimal is {}", selfPointDecimal);
                		float fundingPercent = UPMusicConstants.FUNDING_POINT_RATE / 100f;
                		logger.debug("createOrUpdateRewardListen : 저작권자 자신의 곡 청취 - fundingPercent is {}", fundingPercent);
                		logger.debug("createOrUpdateRewardListen : 저작권자 자신의 곡 청취 - fundingPercent Decimal is {}", new BigDecimal(String.valueOf(fundingPercent)));
                		BigDecimal fundingPointDecimal = selfPointDecimal.multiply(new BigDecimal(String.valueOf(fundingPercent)));
                		logger.debug("createOrUpdateRewardListen : 저작권자 자신의 곡 청취 - fundingPointDecimal is {}", fundingPointDecimal);

						PointTransaction pointTransaction;

							pointTransaction = pointTransactionRepository.findOneByMemberIdAndTransactionTypeAndCreatedAtGreaterThan
									(memberId, PointTransactionType.LISTEN_ARTIST_SELF, UPMusicHelper.getTodayStart());


                		if (pointTransaction == null) {
                			pointTransaction = new PointTransaction();
                			pointTransaction.setMemberId(memberId);
                			pointTransaction.setTransactionType(PointTransactionType.LISTEN_ARTIST_SELF);
                			pointTransaction.setFundingPoint(fundingPointDecimal.floatValue());
                			pointTransaction.setPoint(selfPointDecimal.subtract(fundingPointDecimal).floatValue());
                		} else {
                			pointTransaction.setFundingPoint(pointTransaction.getFundingPointDecimal().add(fundingPointDecimal).floatValue());
                			pointTransaction.setPoint(pointTransaction.getPointDecimal().add(selfPointDecimal).subtract(fundingPointDecimal).floatValue());
                		}

						pointTransaction.setChargeType(UPMusicConstants.ChargeType.CREDIT);
                		pointTransactionRepository.save(pointTransaction);
                		this.increaseMembersPoint(memberId, selfPointDecimal, fundingPointDecimal, false);
                		}
//					}
        		} else {
        			// 청취자 리워드
                	float point = this.getListenPoint(playtime.getPlaytime());
                	if (0f < point) {
                		logger.debug("createOrUpdateRewardListen : 청취자 리워드 - point is {}", point);
                		BigDecimal pointDecimal = new BigDecimal(String.valueOf(point));
                		BigDecimal fundingPointDecimal = pointDecimal.multiply(new BigDecimal(String.valueOf(UPMusicConstants.FUNDING_POINT_RATE / 100f)));

						PointTransaction pointTransaction;
//						synchronized(this){
							pointTransaction = pointTransactionRepository.findOneByMemberIdAndTransactionTypeAndCreatedAtGreaterThan
									(memberId, PointTransactionType.LISTEN, UPMusicHelper.getTodayStart());
//						}

						if (pointTransaction == null) {
                			pointTransaction = new PointTransaction();
                			pointTransaction.setMemberId(memberId);
                			pointTransaction.setTransactionType(PointTransactionType.LISTEN);
                			pointTransaction.setFundingPoint(fundingPointDecimal.floatValue());
                			pointTransaction.setPoint(pointDecimal.subtract(fundingPointDecimal).floatValue());
                		} else {
                			pointTransaction.setFundingPoint(pointTransaction.getFundingPointDecimal().add(fundingPointDecimal).floatValue());
                			pointTransaction.setPoint(pointTransaction.getPointDecimal().add(pointDecimal).subtract(fundingPointDecimal).floatValue());
                		}
						pointTransaction.setChargeType(UPMusicConstants.ChargeType.CREDIT);
						pointTransaction.setChargePoint(0);
                		pointTransactionRepository.save(pointTransaction);
                		this.increaseMembersPoint(memberId, pointDecimal, fundingPointDecimal, false);
                	}
                	
                	// 저작권자 리워드
                	float artistPoint = this.getListenArtistPoint();
                	if (0f < artistPoint) {
//						synchronized (this) {
							logger.debug("createOrUpdateRewardListen : 저작권자 리워드 - point is {}", artistPoint);
							BigDecimal artistPointDecimal = new BigDecimal(String.valueOf(artistPoint));
							BigDecimal fundingPointDecimal = artistPointDecimal.multiply(new BigDecimal(String.valueOf(UPMusicConstants.FUNDING_POINT_RATE / 100f)));
							Long artistId = track.getArtistId();

							logger.debug("artistId : {}", artistId);

							PointTransaction pointTransaction;

							pointTransaction = pointTransactionRepository.findOneByMemberIdAndTransactionTypeAndCreatedAtGreaterThan
									(artistId, PointTransactionType.LISTEN_ARTIST, UPMusicHelper.getTodayStart());


							if (pointTransaction == null) {
								pointTransaction = new PointTransaction();
								pointTransaction.setMemberId(artistId);
								pointTransaction.setTransactionType(PointTransactionType.LISTEN_ARTIST);
								pointTransaction.setFundingPoint(fundingPointDecimal.floatValue());
								pointTransaction.setPoint(artistPointDecimal.subtract(fundingPointDecimal).floatValue());
							} else {
								pointTransaction.setFundingPoint(pointTransaction.getFundingPointDecimal().add(fundingPointDecimal).floatValue());
								pointTransaction.setPoint(pointTransaction.getPointDecimal().add(artistPointDecimal).subtract(fundingPointDecimal).floatValue());
							}
						pointTransaction.setChargeType(UPMusicConstants.ChargeType.CREDIT);
						pointTransaction.setChargePoint(0);
							pointTransactionRepository.save(pointTransaction);
							this.increaseMembersPoint(artistId, artistPointDecimal, fundingPointDecimal, false);
//						}
					}
        		}
    		}
        }
		return new WebsocketTrackPlayingMessage(playtime.getPlaytime(), getTargetRewardtime(playtime.getPlaytime()), getStreamingRewardStep(playtime.getPlaytime()), "",0 ,0, 0);
	}
	
	@Override
	public PointTransactionResponse accumulatePoints(Long memberId, PointTransactionType transactionType, Long transactionTypeId, float point) {
		logger.debug("accumulatePoints called");
		PointTransactionResponse response = new PointTransactionResponse();
		if (0 >= point) {
			response.setStatus(false);
			response.setMessage("적립할 포인트는 0보다 큰 값이어야 합니다");
			return response;
		}
		try {
			BigDecimal pointDecimal = new BigDecimal(String.valueOf(point));
    		BigDecimal fundingPointDecimal = pointDecimal.multiply(new BigDecimal(String.valueOf(UPMusicConstants.FUNDING_POINT_RATE / 100f)));
			PointTransaction pointTransaction = new PointTransaction();
			pointTransaction.setMemberId(memberId);
			pointTransaction.setTransactionType(transactionType);
			pointTransaction.setFundingPoint(fundingPointDecimal.floatValue());
			pointTransaction.setPoint(pointDecimal.subtract(fundingPointDecimal).floatValue());
			pointTransaction.setChargeType(UPMusicConstants.ChargeType.CREDIT);
			pointTransactionRepository.save(pointTransaction);
			this.increaseMembersPoint(memberId, pointDecimal, fundingPointDecimal, false);
			response.setStatus(true);
			response.setMessage("true");
		} catch (Exception e) {
			response.setStatus(false);
			response.setMessage(e.toString());
		}
		return response;
	}
	
	@Override
	public PointTransactionResponse usePoints(Long memberId, PointTransactionType transactionType, Long transactionTypeId, float point) {
		logger.debug("usePoints called");
		PointTransactionResponse response = new PointTransactionResponse();
		if (0 >= point) {
			response.setStatus(false);
			response.setMessage("적립할 포인트는 0보다 큰 값이어야 합니다");
			return response;
		}
		Member member = memberRepository.findById(memberId).orElse(null);
    	if (member == null) {
    		response.setStatus(false);
			response.setMessage("회원 아이디가 정확하지 않습니다");
			return response;
    	}
    	BigDecimal pointDecimal = new BigDecimal(String.valueOf(point));
    	if ((transactionType.equals(PointTransactionType.FUNDING) && 0 > member.getUpmPointDecimal().compareTo(pointDecimal)) ||
    		(!transactionType.equals(PointTransactionType.FUNDING) && 0 > member.getUsablePointDecimal().compareTo(pointDecimal))) {
    		response.setStatus(false);
			response.setMessage("포인트가 충분하지 않습니다");
			return response;
    	}
    	
		try {
    		BigDecimal fundingPointDecimal = new BigDecimal("0");
			PointTransaction pointTransaction = new PointTransaction();
			pointTransaction.setMemberId(memberId);
			pointTransaction.setTransactionType(transactionType);
			if (transactionType.equals(PointTransactionType.FUNDING)) {
				// 현재 보유한 펀딩 포인트보다 금액이 큰 경우, 펀딩 포인트를 모두 사용하고 나머지는 일반 포인트에서 차감
				if (0 > member.getFundingPointDecimal().compareTo(pointDecimal)) {
					fundingPointDecimal = member.getFundingPointDecimal();
					pointTransaction.setFundingPoint(fundingPointDecimal.multiply(new BigDecimal("-1")).floatValue());
					pointTransaction.setPoint(pointDecimal.subtract(fundingPointDecimal).multiply(new BigDecimal("-1")).floatValue());
				} else {
					pointTransaction.setFundingPoint(pointDecimal.multiply(new BigDecimal("-1")).floatValue());
					pointTransaction.setPoint(0f);
				}
			} else {
				pointTransaction.setFundingPoint(0f);
				pointTransaction.setPoint(point);
			}
			pointTransaction.setTransactionTypeId(transactionTypeId);
			pointTransaction.setChargeType(UPMusicConstants.ChargeType.CREDIT);
			pointTransactionRepository.save(pointTransaction);
			this.decreaseMembersPoint(memberId, pointDecimal, fundingPointDecimal, true);
			response.setStatus(true);
			response.setMessage("true");
		} catch (Exception e) {
			response.setStatus(false);
			response.setMessage(e.toString());
		}
		return response;
	}
	
	@Override
	public PointTransactionResponse cancelTransaction(Long pointTransactionId) {
		logger.debug("cancelTransaction called");
		PointTransactionResponse response = new PointTransactionResponse();
		PointTransaction pointTransaction = pointTransactionRepository.findById(pointTransactionId).orElse(null);
		if (pointTransaction == null) {
			response.setStatus(false);
			response.setMessage("해당 거래를 찾을 수 없습니다");
			return response;
		}
		try {
			BigDecimal pointDecimal = pointTransaction.getPoint() != 0f ? pointTransaction.getPointDecimal().multiply(new BigDecimal("-1")) : new BigDecimal("0");
    		BigDecimal fundingPointDecimal = pointTransaction.getFundingPoint() != 0f ? pointTransaction.getFundingPointDecimal().multiply(new BigDecimal("-1")) : new BigDecimal("0");
			PointTransaction pointTransactionCancel = new PointTransaction();
			pointTransactionCancel.setMemberId(pointTransaction.getMemberId());
			pointTransactionCancel.setTransactionType(pointTransaction.getTransactionType());
			pointTransactionCancel.setTransactionTypeId(pointTransaction.getTransactionTypeId());
			pointTransactionCancel.setFundingPoint(fundingPointDecimal.floatValue());
			pointTransactionCancel.setPoint(pointDecimal.floatValue());
			pointTransactionRepository.save(pointTransactionCancel);
			this.increaseMembersPoint(pointTransaction.getMemberId(), pointDecimal.add(fundingPointDecimal), fundingPointDecimal, pointTransaction.getTransactionType().equals(PointTransactionType.HYC));
			response.setStatus(true);
			response.setMessage("true");
		} catch (Exception e) {
			response.setStatus(false);
			response.setMessage(e.toString());
		}
		return response;
	}
	
	@Override
	public Page<PointTransaction> listMemberAllTransaction(Long memberId, PageRequest pageRequest) {
		logger.debug("listMemberAllTransaction called");
		return pointTransactionRepository.findAllByMemberId(memberId, pageRequest);
	}
	
	@Override
	public Page<PointTransaction> listMemberAccumulatedTransaction(Long memberId, PageRequest pageRequest) {
		logger.debug("listMemberAccumulatedTransaction called");
		return pointTransactionRepository.findAccumulatedTransactionsByMemberId(memberId, pageRequest);
	}
	
	@Override
	public Page<PointTransaction> listMemberUsedTransaction(Long memberId, PageRequest pageRequest) {
		logger.debug("listMemberUsedTransaction called");
		return pointTransactionRepository.findUsedTransactionsByMemberId(memberId, pageRequest);
	}
	
	@Override
	public JsonObject getDailyTransaction(Long memberId, String date) throws ParseException {
		JsonObject jsonObject = new JsonObject();
		Date dayStart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(date + " 00:00:00.0");
		Date dayEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(date + " 24:00:00.0");
		// 청취 리워드
		MemberPlaytime playtime = playtimeRepository.findOneByMemberIdAndCreatedAtGreaterThanAndUpdatedAtLessThan(memberId, dayStart, dayEnd);
		if (playtime != null) {
			jsonObject.addProperty("listenTime", playtime.formattedDuration());
			BigDecimal dailyListenSum = BigDecimal.ZERO;
			List<PointTransaction> transactions = pointTransactionRepository.findByDateStreamingTransactionsByMemberId(memberId, dayStart, dayEnd);
			for (PointTransaction transaction : transactions) {
				dailyListenSum = dailyListenSum.add(transaction.getPointDecimal()).add(transaction.getFundingPointDecimal());
			}
			jsonObject.addProperty("listenPoint", UPMusicHelper.floatToPrice(dailyListenSum.floatValue()));
		} else {
			jsonObject.addProperty("listenTime", "0");
			jsonObject.addProperty("listenPoint", "0");
		}
		// 저작자 리워드
		BigDecimal dailyCopyrightSum = BigDecimal.ZERO;
		List<PointTransaction> copyrightTransactions = pointTransactionRepository.findByDateCopyrightTransactionsByMemberId(memberId, dayStart, dayEnd);
		for (PointTransaction transaction : copyrightTransactions) {
			dailyCopyrightSum = dailyCopyrightSum.add(transaction.getPointDecimal()).add(transaction.getFundingPointDecimal());
		}
		jsonObject.addProperty("copyrightPoint", UPMusicHelper.floatToPrice(dailyCopyrightSum.floatValue()));
		float copyrightPlaytime = 0f;
		if (0 < dailyCopyrightSum.compareTo(BigDecimal.ZERO)) {
			PointRewardCondition condition = pointRewardConditionRepository.findById(1).orElse(null);
	    	if (condition != null && 0f < condition.getListenArtistPoint()) {
	    		copyrightPlaytime = dailyCopyrightSum.floatValue() / condition.getListenArtistPoint();
	    	}
		}
		jsonObject.addProperty("copyrightTime", UPMusicHelper.secondsToTime(copyrightPlaytime));
		// 업로드 리워드
		BigDecimal dailyUploadSum = BigDecimal.ZERO;
		List<PointTransaction> uploadTransactions = pointTransactionRepository.findByDateUploadTransactionsByMemberId(memberId, dayStart, dayEnd);
		for (PointTransaction transaction : uploadTransactions) {
			dailyUploadSum = dailyUploadSum.add(transaction.getPointDecimal()).add(transaction.getFundingPointDecimal());
		}
		jsonObject.addProperty("uploadCount", uploadTransactions.size());
		jsonObject.addProperty("uploadPoint", UPMusicHelper.floatToPrice(dailyUploadSum.floatValue()));
		// 공유 리워드
		BigDecimal dailyShareSum = BigDecimal.ZERO;
		List<PointTransaction> shareTransactions = pointTransactionRepository.findByDateShareTransactionsByMemberId(memberId, dayStart, dayEnd);
		for (PointTransaction transaction : shareTransactions) {
			dailyShareSum = dailyShareSum.add(transaction.getPointDecimal()).add(transaction.getFundingPointDecimal());
		}
		jsonObject.addProperty("shareCount", shareTransactions.size());
		jsonObject.addProperty("sharePoint", UPMusicHelper.floatToPrice(dailyShareSum.floatValue()));
		// 이벤트 리워드
		BigDecimal dailyEventSum = BigDecimal.ZERO;
		List<PointTransaction> eventTransactions = pointTransactionRepository.findByDateEventTransactionsByMemberId(memberId, dayStart, dayEnd);
		for (PointTransaction transaction : eventTransactions) {
			dailyEventSum = dailyEventSum.add(transaction.getPointDecimal()).add(transaction.getFundingPointDecimal());
		}
		jsonObject.addProperty("eventCount", eventTransactions.size());
		jsonObject.addProperty("eventPoint", UPMusicHelper.floatToPrice(dailyEventSum.floatValue()));
		
		return jsonObject;
	}

	@Override
	public List<PointTransaction> getDailyShareTransaction(Long memberId, String date) throws ParseException {
		Date dayStart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(date + " 00:00:00.0");
		Date dayEnd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(date + " 24:00:00.0");

		// 공유 리워드
		List<PointTransaction> shareTransactions = pointTransactionRepository.findByDateShareTransactionsByMemberId(memberId, dayStart, dayEnd);

		return shareTransactions;
	}
	
	@Override
	public Page<PointTransaction> getMonthlyUseTransaction(Long memberId, String month, PageRequest pageRequest) throws ParseException {
		logger.debug("listMemberUsedTransaction called");
		Date mon = new SimpleDateFormat("yyyy-MM-dd").parse(month + "-01");
		
		Calendar cStart = Calendar.getInstance();
		cStart.setTime(mon);
		cStart.set(Calendar.HOUR_OF_DAY, 0);
		cStart.set(Calendar.MINUTE, 0);
		cStart.set(Calendar.SECOND, 0);
		logger.debug("listMemberUsedTransaction : cStart is {}", cStart.getTime());
		
		Calendar cEnd = Calendar.getInstance();
		cEnd.set(Calendar.YEAR, cStart.get(Calendar.YEAR));
		cEnd.set(Calendar.MONTH, cStart.get(Calendar.MONTH) + 1);
		cEnd.set(Calendar.DAY_OF_MONTH, 1);
		cEnd.set(Calendar.HOUR_OF_DAY, 0);
		cEnd.set(Calendar.MINUTE, 0);
		cEnd.set(Calendar.SECOND, 0);
		logger.debug("listMemberUsedTransaction : cEnd is {}", cEnd.getTime());
		
		return pointTransactionRepository.findByMonthUsedTransactionsByMemberId(memberId, cStart.getTime(), cEnd.getTime(), pageRequest);
	}

	@Override
	public Page<PointTransaction> getMonthlyAllTransaction(Long memberId, String month, PageRequest pageRequest) throws ParseException {
		logger.debug("listMemberUsedTransaction called");
		Date mon = new SimpleDateFormat("yyyy-MM-dd").parse(month + "-01");

		Calendar cStart = Calendar.getInstance();
		cStart.setTime(mon);
		cStart.set(Calendar.HOUR_OF_DAY, 0);
		cStart.set(Calendar.MINUTE, 0);
		cStart.set(Calendar.SECOND, 0);
		logger.debug("listMemberUsedTransaction : cStart is {}", cStart.getTime());

		Calendar cEnd = Calendar.getInstance();
		cEnd.set(Calendar.YEAR, cStart.get(Calendar.YEAR));
		cEnd.set(Calendar.MONTH, cStart.get(Calendar.MONTH) + 1);
		cEnd.set(Calendar.DAY_OF_MONTH, 1);
		cEnd.set(Calendar.HOUR_OF_DAY, 0);
		cEnd.set(Calendar.MINUTE, 0);
		cEnd.set(Calendar.SECOND, 0);
		logger.debug("listMemberUsedTransaction : cEnd is {}", cEnd.getTime());

		return pointTransactionRepository.findByMonthAllTransactionsByMemberId(memberId, cStart.getTime(), cEnd.getTime(), pageRequest);
	}

	@Override
	public Page<PointTransaction> getMonthlyChargeTransaction(Long memberId, String month, PageRequest pageRequest) throws ParseException {
		logger.debug("listMemberUsedTransaction called");
		Date mon = new SimpleDateFormat("yyyy-MM-dd").parse(month + "-01");

		Calendar cStart = Calendar.getInstance();
		cStart.setTime(mon);
		cStart.set(Calendar.HOUR_OF_DAY, 0);
		cStart.set(Calendar.MINUTE, 0);
		cStart.set(Calendar.SECOND, 0);
		logger.debug("listMemberUsedTransaction : cStart is {}", cStart.getTime());

		Calendar cEnd = Calendar.getInstance();
		cEnd.set(Calendar.YEAR, cStart.get(Calendar.YEAR));
		cEnd.set(Calendar.MONTH, cStart.get(Calendar.MONTH) + 1);
		cEnd.set(Calendar.DAY_OF_MONTH, 1);
		cEnd.set(Calendar.HOUR_OF_DAY, 0);
		cEnd.set(Calendar.MINUTE, 0);
		cEnd.set(Calendar.SECOND, 0);
		logger.debug("listMemberUsedTransaction : cEnd is {}", cEnd.getTime());

		return pointTransactionRepository.findByMonthChargeTransactionsByMemberId(memberId, cStart.getTime(), cEnd.getTime(), pageRequest);
	}

	@Override
	public boolean addChargePoint(Long memberId, PointTransactionType transactionType, BigDecimal chargePoint, String serviceCode, String chargeTransactionId, String chargeStatus) {
		PointTransaction pointTransaction = new PointTransaction();

		//신용카드
		if (serviceCode.equals("0900")) {
			pointTransaction.setChargeType(UPMusicConstants.ChargeType.CREDIT);
		//핸드폰
		} else if (serviceCode.equals("1100")) {
			pointTransaction.setChargeType(UPMusicConstants.ChargeType.MOBILE);
		//계좌이체
		} else if (serviceCode.equals("1000")) {
			pointTransaction.setChargeType(UPMusicConstants.ChargeType.ACCOUNT);
		}

		pointTransaction.setMemberId(memberId);
		pointTransaction.setTransactionType(transactionType);

		pointTransaction.setChargePoint(chargePoint.floatValue());
		pointTransaction.setChargeTransactionId(chargeTransactionId);
		pointTransaction.setChargeStatus(chargeStatus);
		pointTransactionRepository.save(pointTransaction);
		return false;
	}

	@Override
	public void updateChargePoint(Long id) {
		PointTransaction pointTransaction = pointTransactionRepository.findById(id).orElse(null);
		pointTransaction.setChargeStatus("refund");
		pointTransactionRepository.save(pointTransaction);
	}

	/**
	 * 하루 총 재생시간을 기준으로 포인트 비율을 반환
	 * @param playtime
	 * @return float
	 */
    private float getListenPoint(Long playtime) {
    	PointRewardCondition condition = pointRewardConditionRepository.findById(1).orElse(null);
    	if (condition != null) {
    		// 1단계 : 제한시간이 0일 경우에는 무제한
    		if (0 == condition.getListenFirstStepLimit() || playtime < condition.getListenFirstStepLimit() * 60) {
    			return condition.getListenFirstStepPoint();
    		// 2단계
    		} else if (0 == condition.getListenSecondStepLimit() || playtime < (condition.getListenFirstStepLimit() + condition.getListenSecondStepLimit()) * 60) {
    			return condition.getListenSecondStepPoint();
    		// 3단계
    		} else if (0 == condition.getListenThirdStepLimit() || playtime < (condition.getListenFirstStepLimit() + condition.getListenSecondStepLimit() + condition.getListenThirdStepLimit()) * 60) {
    			return condition.getListenThirdStepPoint();
    		}
    	}
    	return 0f;
    }
    
    /**
     * 청취곡의 저작권자에게 지급할 포인트 비율을 반환
     * @return float
     */
    private float getListenArtistPoint() {
    	PointRewardCondition condition = pointRewardConditionRepository.findById(1).orElse(null);
    	if (condition != null) return condition.getListenArtistPoint();
    	return 0f;
    }
    
    /**
     * 저작권자가 자신의 곡을 청취할 때 지급할 포인트 비율을 반환
     * @return float
     */
    private float getListenArtistSelfPoint(Long playtime) {
    	PointRewardCondition condition = pointRewardConditionRepository.findById(1).orElse(null);
    	if (condition != null && (0 == condition.getListenArtistSelfLimit() || playtime < condition.getListenArtistSelfLimit() * 60)) {
    		return condition.getListenArtistSelfPoint();
    	}
    	return 0f;
    }
    
    /**
     * 회원의 포인트 적립 또는 거래 취소로 인한 반환
     * @param memberId
     * @param upmPoint
     * @param fundingPoint
     * @param transactionHYC : 하이콘 거래 취소
     */
    private void increaseMembersPoint(Long memberId, BigDecimal upmPoint, BigDecimal fundingPoint, boolean transactionHYC) {
    	Member member = memberRepository.findById(memberId).orElse(null);
    	if (member != null) {
    		logger.debug("increaseMembersPoint : member.getUpmPoint() is {}", member.getUpmPointDecimal());
    		logger.debug("increaseMembersPoint : member add point is {}", upmPoint);
    		logger.debug("increaseMembersPoint : member new upmPoint is {}", member.getUpmPointDecimal().add(upmPoint).floatValue());
    		member.setUpmPoint(member.getUpmPointDecimal().add(upmPoint));
    		member.setFundingPoint(member.getFundingPointDecimal().add(fundingPoint));
    		if (transactionHYC && upmPoint.intValue() < member.getHyc()) member.setHyc(member.getHyc() - upmPoint.intValue());
    		memberRepository.save(member);
    	}
    }
    
    /**
     * 회원의 포인트 사용
     * @param memberId
     * @param upmPoint
     * @param fundingPoint
     * @param transactionHYC : 하이콘 거래
     */
    private void decreaseMembersPoint(Long memberId, BigDecimal upmPoint, BigDecimal fundingPoint, boolean transactionHYC) {
    	Member member = memberRepository.findById(memberId).orElse(null);
    	if (member != null) {
    		if (0 < member.getUpmPointDecimal().compareTo(BigDecimal.ZERO)) member.setUpmPoint(member.getUpmPointDecimal().subtract(upmPoint));
    		if (0 < member.getFundingPointDecimal().compareTo(BigDecimal.ZERO)) member.setFundingPoint(member.getFundingPointDecimal().subtract(fundingPoint));
    		if (transactionHYC) member.setHyc(member.getHyc() + upmPoint.intValue());
    		memberRepository.save(member);
    	}
    }
    
    private Long getTargetRewardtime(Long playtime) {
    	Long rewardtime = 0L;
    	PointRewardCondition condition = pointRewardConditionRepository.findById(1).orElse(null);
    	if (condition != null) {
    		// 1단계 : 제한시간이 0일 경우에는 무제한
    		if (0 == condition.getListenFirstStepLimit() || playtime <= condition.getListenFirstStepLimit() * 60) {
    			return (long) condition.getListenFirstStepLimit();
    		// 2단계
    		} else if (0 == condition.getListenSecondStepLimit() || playtime <= (condition.getListenFirstStepLimit() + condition.getListenSecondStepLimit()) * 60) {
    			return (long) condition.getListenSecondStepLimit();
    		}
    	}
    	return rewardtime;
    }
}
