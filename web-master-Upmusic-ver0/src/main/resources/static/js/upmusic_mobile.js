/**
 * upmusic mobile main javascript
 */
var UPMusic = {
	_config : {
		_is_debug : true,
		_cloudfront_url: "https://d1jordkjn05iom.cloudfront.net",
		_user_id : '',
		_api_album : '/api/album',
		_api_artist : '/api/artist',
		_api_auth : '/api/auth',
		_auth : '/auth',
		_api_check_email : '/api/auth/check_email',
		_api_collection : '/api/collection',
		_api_guide_vocal : '/api/guide_vocal',
		_api_login : '/api/auth/login',
		_api_my_heartlist : '/api/my_heartlist',
		_api_my_playlist : '/api/my_playlist',
		_api_player : '/api/player',
		_api_request : '/api/store/request',
		_api_registration : '/api/auth/registration',
		_api_store : '/api/store',
		_api_track : '/api/track',
		_api_transaction : '/api/point_transaction',
		_api_video : '/api/video',
		_api_vocal_casting : '/api/vocal_casting',
		_api_request_phone_authentication_code : '/api/auth/request_phone_authentication_code',
		_api_request_pw_email_authentication_code : '/api/auth/request_pw_email_authentication_code',
		_api_request_pw_phone_authentication_code : '/api/auth/request_pw_phone_authentication_code',
		_api_search_member : '/api/member/search',
		_api_withdraw : '/api/auth/withdraw',
		_api_search : '/api/search'
	},
	init : function() {
		// init
	},
	log : function(msg) {
		if (UPMusic._config._is_debug) console.log(msg);
	},
	alert : function(msg, error) {
		UPMMessageModal.showModal(msg);
// var flash_msg = '';
// if (error) {
// flash_msg = '<div id="error">';
// } else {
// flash_msg = '<div>';
// }
// flash_msg += msg + '</div>';
// $('#message').html('<div id="message-body">' + flash_msg + '</div>');
// $('#message #message-body').slideToggle(1000).delay(3000).slideToggle(1000);
	},
	confirm : function(msg, okCallback) {
		UPMConfirmModal.showModal(msg, okCallback);
	},
	startLoading : function() {
		$('.loading_pop').show();
	},
	stopLoading : function() {
		$('.loading_pop').hide();
	},
	loadPage: function(elementId, paginationUrl, page) {
		var url = paginationUrl + '?page=' + page;
		$.get(url, function(data) {
			$('#' + elementId).replaceWith(data);
		});
	},
	appendPage: function(elementId, paginationUrl, page, totalPages) {
		var url = paginationUrl + '?page=' + page;
		$.get(url, function(data) {
			$('#' + elementId + ' .item-list').append(data);
		});
		if (page + 1 >= totalPages) {
			if (0 < $('#' + elementId + ' #list-more-button').length) {
				$('#' + elementId + ' #list-more-button').remove();
			} else {
				$('#' + elementId).siblings('#list-more-button').remove();
			}

		} else {
			var moreButton = '<div id="list-more-button" class="showmore_wrap" onclick="UPMusic.appendPage(\'' + elementId + '\', \'' + paginationUrl + '\', ' + (page + 1) + ', ' + totalPages + ');"><p>SHOW MORE <i><img src="/img/show_ar.png" alt="" /></i></p></div>';
			if (0 < $('#' + elementId + ' #list-more-button').length) {
				$('#' + elementId + ' #list-more-button').replaceWith(moreButton);
			} else {
				$('#' + elementId).siblings('#list-more-button').replaceWith(moreButton);
			}
		}
	},
	goto: function(url) {
        UPMusic.startLoading();
        window.location.href = url;
    },
	goto: function(url, e) {
		if (!e) var e = window.event;
	    e.cancelBubble = true;
	    if (e.stopPropagation) e.stopPropagation();
        UPMusic.startLoading();
        // window.location.replace(url);
        window.location.href = url;
//    	var xhttp = new XMLHttpRequest();
//        xhttp.onreadystatechange = function() {
//            if (this.readyState == 4 && this.status == 200) {
//            	if (this.responseURL.includes('login')) {
//            		UPMLoginModal.showModal();
//            	} else {
//            		window.location = url;
//            	}
//            }
//        };
//        xhttp.open("GET", url, true);
//        xhttp.send();
	},
	goBack: function() {
		history.go(-1);
	},
	ajaxSubmit : function(frm, callback) {
		UPMusic.log("UPMusic.ajaxSubmit");
		var submit = $(frm).find('#submit');
		var progress = $(frm).find('.progress');
		submit.hide();
		progress.show();
		var bar = $(frm).find('.bar');
		var percent = $(frm).find('.percent');
		$(frm).ajaxSubmit({
			beforeSend: function() {
		        var percentVal = '0%';
		        bar.width(percentVal);
		        percent.html(percentVal);
		    },
		    uploadProgress: function(event, position, total, percentComplete) {
		    	if (50 < percentComplete) percentComplete -= 1;
	    		var percentVal = percentComplete + '%';
		        bar.width(percentVal);
		        percent.html(percentVal);
		    },
			success: function(data) {
				var percentVal = '100%';
		        bar.width(percentVal);
		        percent.html(percentVal);
				var obj = jQuery.parseJSON(data);
				var result = obj.result;
				var message = obj.message;
				progress.hide();
	    		submit.show();
				if (result && callback) {
					callback(message);
		    	} else {
		    		UPMusic.alert(message, true);
		    	}
		    }
		});
		return false;
	},
	ajaxPost: function(url, headers, params, callback) {
		UPMusic.log("UPMusic.ajaxPost");
		UPMusic.log(headers);
		UPMusic.log(params);
		$.ajax({
		    url: url,
		    type: 'post',
		    headers: headers,
		    data: JSON.stringify(params),
		    contentType: "application/json; charset=UTF-8",
		    dataType: 'json',
		    cache: false,
		    success: function (data) {
		    	UPMusic.log(data);
		        var result = data.status;
				var message = data.message;
				var object = data.object;
				if (result) {
					callback(message, object);
		    	} else {
		    		UPMusic.alert(message, true);
		    		UPMusicCommon._config.loading = false;
                    UPMusic.stopLoading();
		    	}
		    },
            error: function (request,status,error) {
            	UPMusic.alert("code:"+request.status+"<br>"+"message:"+request.responseText+"<br>"+"error:"+error, true);
            	UPMusicCommon._config.loading = false;
            }
		});
	},
	ajaxPostFormData: function(url, headers, formData, callback) {
		UPMusicCommon._config.loading = true;
		$.ajax({
		    url: url,
		    type: 'post',
		    headers: headers,
		    data: formData,
		    cache: false,
		    processData: false,
		    contentType: false,
		    success: function (data) {
		    	UPMusic.log(data);
		        var result = data.status;
				var message = data.message;
				var object = data.object;
				if (result) {
					callback(message, object);
		    	} else {
		    		UPMusic.alert(message, true);
		    		UPMusicCommon._config.loading = false;
		    	}
		    },
            error: function (e) {
            	UPMusic.alert(e.message, true);
            	UPMusicCommon._config.loading = false;
            }
		});
	},
	ajaxPostResObj: function(url, headers, params, callback) {
		UPMusic.log("UPMusic.ajaxPostResObj");
		UPMusic.log(headers);
		UPMusic.log(params);
		$.ajax({
		    url: url,
		    type: 'post',
		    headers: headers,
		    data: JSON.stringify(params),
		    contentType: "application/json; charset=UTF-8",
		    dataType: 'json',
		    cache: false,
		    success: function (data) {
		    	UPMusic.log(data);
				callback(data);
		    },
            error: function (e) {
            	callback(null);
            }
		});
	},
	ajaxDelete: function(url, headers, callback) {
		UPMusic.log("UPMusic.ajaxDelete");
		$.ajax({
		    url: url,
		    type: 'delete',
		    headers: headers,
		    contentType: "application/json; charset=UTF-8",
		    dataType: 'json',
		    cache: false,
		    success: function (data) {
		    	UPMusic.log(data);
		        var result = data.status;
				var message = data.message;
				var object = data.object;
				if (result) {
					callback(message, object);
		    	} else {
		    		UPMusic.alert(message, true);
		    	}
		    },
            error: function (e) {
            	UPMusic.alert(e.message, true);
            }
		});
	},
	ajaxDeleteItems: function(url, headers, params, callback) {
		UPMusic.log("UPMusic.ajaxDelete");
		$.ajax({
		    url: url,
		    type: 'delete',
		    headers: headers,
		    contentType: "application/json; charset=UTF-8",
		    dataType: 'json',
		    data: JSON.stringify(params),
		    cache: false,
		    success: function (data) {
		    	UPMusic.log(data);
		        var result = data.status;
				var message = data.message;
				var object = data.object;
				if (result) {
					callback(message, object);
		    	} else {
		    		UPMusic.alert(message, true);
		    	}
		    },
            error: function (e) {
            	UPMusic.alert(e.message, true);
            }
		});
	},
	loadPrepend: function(url, div_id) {
		div_id,
		$.post(url, function(data) {
		    $(data).prependTo("#" + div_id).fadeIn("slow");
		});
	},
	loadAppend: function(url, headers, params, div_id) {
		UPMusic.log("UPMusic.loadAppend");
		UPMusic.log(headers);
		UPMusic.log(params);
		$.ajax({
		    url: url,
		    type: 'post',
		    headers: headers,
		    data: JSON.stringify(params),
		    contentType: "application/json; charset=UTF-8",
		    dataType: 'json',
		    cache: false,
		    success: function (data) {
		    	UPMusic.log(data);
		    	$(data).appendTo("#" + div_id).fadeIn("slow");
		    },
            error: function (e) {
            	callback(null);
            }
		});
	},
	checkCallback: function(msg) {
		UPMusic.alert(msg);
	},
	checkCallback: function(msg, object) {
		UPMusic.alert(msg);
	},
	checkboxAllToggle: function(prefix, checked) {
		$(prefix + ' input[type=checkbox]').prop('checked', checked);
	},
	checkboxEachToggle: function(prefix, checked) {
		if (!checked) {
			$(prefix + ' #select-all-tracks').prop('checked', false);
			if ($(prefix + ' #demo-form-inline-checkbox').length) $(prefix + ' #demo-form-inline-checkbox').prop('checked', false);
		}
	},
	selectAllTracks : function(fragmentId) {
		var selectedTracks = $(fragmentId + ' dl dd.on').map(function() {
			if (this.id) return this.id;
		}).get();
		if (0 < selectedTracks.length) {
			$(fragmentId + ' dl dd.on').each(function( index ) {
				$(this).removeClass('on');
			});
			UPMusic.hideTrackButtons();
		} else {
			$(fragmentId + ' dl dd').each(function( index ) {
				$(this).addClass('on');
			});
			UPMusic.showTrackButtons();
		}
		UPMusic.checkHasSelectedTrack(fragmentId);
	},
	selectItem : function(fragmentId, item) {
		if ($(item).hasClass("on")) {
			$(item).removeClass('on');
		} else {
			$(item).addClass("on");
		}
		UPMusic.checkHasSelectedTrack(fragmentId);
	},
	checkHasSelectedTrack : function(fragmentId) {
		var selectedTracks = $(fragmentId + ' dl dd.on').map(function() {
			if (this.id) return this.id;
		}).get();
		if (0 < selectedTracks.length) {
			UPMusic.showTrackButtons(fragmentId);
		} else {
			UPMusic.hideTrackButtons(fragmentId);
		}
	},
	showTrackButtons : function(fragmentId) {
		UPMTrackMenuModal.showTrackButtons(fragmentId);
	},
	hideTrackButtons : function(fragmentId) {
		UPMTrackMenuModal.hideTrackButtons(fragmentId);
	},
	secondsToTime: function(timeSeconds) {
    	var minutes = Math.floor(timeSeconds / 60);
    	var seconds = timeSeconds % 60;
    	if (10 > minutes) minutes = '0' + minutes;
    	if (10 > seconds) seconds = '0' + seconds;
    	return minutes + "분 " + seconds + "초";
	},
	setCookie : function(name,value,days) {
		var expires = "";
		if (days) {
	        var date = new Date();
	        date.setTime(date.getTime()+(days*24*60*60*1000));
	        expires = "; expires="+date.toGMTString();
	    }
	    document.cookie = name+"="+value+expires+"; path=/";
	},
	getCookie : function(name) {
	    var nameEQ = name + "=";
	    var ca = document.cookie.split(';');
	    for (var i=0;i < ca.length;i++) {
	        var c = ca[i];
	        while (c.charAt(0) == ' ') c = c.substring(1,c.length);
	        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
	    }
	    return null;
	},
	eraseCookie : function(name) {
		UPMusic.log("eraseCookie : name is " + name);
		document.cookie = name+'=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
	},
	doNotShow24hRewardPop : function() {
		UPMusic.setCookie("upm-reward-introduction", "checked" , 1);
		$('#modal-reward-introduction').modal('hide');
	},
	closeSidebar : function() {
		var nsbw = $('.sidebar').width();
		//var duration = 500;
		var duration = 10;
		var easing = 'swing';
		var overflowFalse = function() {
			$( 'body, html' ).css({
				overflow: 'auto'
			});
		};
		animationReset = {
			marginRight: '+=' + nsbw,
			marginLeft: '-=' + nsbw
		};
		$( '.sidebar' ).animate( animationReset, {
			duration: duration,
			easing: easing,
			complete: overflowFalse
		});
		var maskDiv = $( 'body' ).children().filter(function() {
			return $( this ).data( 'ssbplugin' ) === 'mask' ;
		});
		maskDiv.fadeOut();
		// $('.sidebar').hide();
	},
	pageInit : function() {
		$('.sidebar').materialSidebar({
			settings: {
				opener: '#open-sb',
				wrapper: '.wrapper'
			},
			sidebar: {
				align: 'left',
				width: 280, //20181219 jm 수정
				closingLinks: '#closeButton2'
			}
		});
		var swiperAll = new Swiper('.mainSwiper', {
			slidesPerView: 'auto',
			spaceBetween: 10,
			navigation: {
				nextEl: '.swiper-button-next',
				prevEl: '.swiper-button-prev',
			},
		});
        var searchSlide = new Swiper('.searchSwiper', {
            slidesPerView: 4,
            slidesPerGroup: 4,
            spaceBetween: 0,
        });
        var fundingSwiper = new Swiper('.fundingSwiper', {
            navigation: {
                nextEl: '.swiper-button-next',
                prevEl: '.swiper-button-prev',
            },
        });
		$(".nav-tabs li a").click(function(){
			window.location.hash = $(this).attr('id');
		});
		if (window.location.hash) {
			var hash = window.location.hash.substring(1); //Puts hash in variable, and removes the # character
			console.log(hash);
			$(".nav-tabs li a#" + hash).click();
			window.scrollTo(0, 0);         // execute it straight away
	        setTimeout(function() {
	            window.scrollTo(0, 0);     // run it a bit later also for browser compatibility
	        }, 1);
		}

		//좋아요 피드버튼
		$(".feedcontent_footer>ul>li").click(function(){
			indexNum = $(this).index();
			if ($(this).hasClass("feedbtn_on") ){
				$(this).removeClass("feedbtn_on").children("").children("img").attr("src","./img/feedbtn_"+indexNum+".png");
			}else {
				$(this).addClass("feedbtn_on").children("").children("img").attr("src","./img/afeedbtn_"+indexNum+".png");
			}
		});

		//좋아요 버튼
		$(".like_event ul li:nth-child(3)").click(function(){
			if ($(this).hasClass("feedbtn_on") ){
				$(this).removeClass("feedbtn_on").children("").children("img").attr("src","./img/feedbtn_2.png");
			}else {
				$(this).addClass("feedbtn_on").children("").children("img").attr("src","./img/afeedbtn_2.png");
			}
		});

		$(".comment-toggle").click(function(){
			$(this).parents("ul").parents().siblings(".feedcomment_wrap").slideToggle();
		});

		//공유하기 팝업
		$(".social_feed>img").click(function(){
			$(".social_sharewrap").fadeIn(300);
		});

		$(".close").click(function(){
			$(".social_sharewrap").fadeOut(300);
		});

		//셀렉트박스
		$(".selectbox_design").click(function() {
			selectOption = $(".selectbox_option").attr('class');
	        if ( selectOption.match("selectbox_off") ){
				$(".selectbox_option").removeClass("selectbox_off");
				$(this).children().children("img").css("transform","rotate(180deg)");
			}else{
				$(".selectbox_option").addClass("selectbox_off");
				$(this).children().children("img").css("transform","rotate(0deg)");
			}
	    });

        $('.mainContent01 .ct_title .nav-tabs ul li').click(function () {
            tabNum = $(this).index();
            $(this).addClass('active').siblings("li").removeClass('active');
            $(".mainContent01 .tab-content>div").eq(tabNum).addClass('active').siblings("div").removeClass('active');
            if(swiperAll.length){
                for(i=0; i < swiperAll.length ; i++){
                    swiperAll[i].update();
                }
            }else{
                swiperAll.update();
            }
        });

        $('#home-recent-play-fragment .ct_title .nav-tabs ul li').click(function () {
            tabNum = $(this).index();
            $(this).addClass('active').siblings("li").removeClass('active');
            $("#home-recent-play-fragment .tab-content>div").eq(tabNum).addClass('active').siblings("div").removeClass('active');
            if(swiperAll.length){
                for(i=0; i < swiperAll.length ; i++){
                    swiperAll[i].update();
                }
            }else{
                swiperAll.update();
            }
        });
        $('#home-likes-fragment .ct_title .nav-tabs ul li').click(function () {
            tabNum = $(this).index();
            $(this).addClass('active').siblings("li").removeClass('active');
            $("#home-likes-fragment .tab-content>div").eq(tabNum).addClass('active').siblings("div").removeClass('active');
            if(swiperAll.length){
                for(i=0; i < swiperAll.length ; i++){
                    swiperAll[i].update();
                }
            }else{
                swiperAll.update();
            }
        });

		$(".reward_wrap .nav-tabs li").click(function () {
            tabNum = $(this).index();
            $(this).addClass('active').siblings("li").removeClass('active');
            $(".reward_wrap .tab-content>div").eq(tabNum).addClass('active').siblings("div").removeClass('active');
            if(swiperAll.length){
                for(i=0; i < swiperAll.length ; i++){
                    swiperAll[i].update();
                }
            }else{
                swiperAll.update();
            }
        });


        $(".nav-tabs li").click(function () {
            tabNum = $(this).index();
            $(this).addClass('active').siblings("li").removeClass('active');
            $(".tab-content>div").eq(tabNum).addClass('active').siblings("div").removeClass('active');
            if(swiperAll.length){
                for(i=0; i < swiperAll.length ; i++){
                    swiperAll[i].update();
                }
            }else{
                swiperAll.update();
            }
        });

        // UPMusic.startLoaGding();
	},
    // 2018-10-10 추가 댓글 입력 / 수정시 바이트 체크
    byteCheck : function(el) {
    	var codeByte = 0;
        for (var idx = 0; idx < el.length; idx++) {
            var oneChar = escape(el.charAt(idx));
            if ( oneChar.length == 1 ) {
                codeByte ++;
            } else if (oneChar.indexOf("%u") != -1) {
                codeByte += 2;
            } else if (oneChar.indexOf("%") != -1) {
                codeByte ++;
            }
        }
        return codeByte;
    }
};


$(function() {
    $(window).bind("pageshow", function(event) {
    	if (event.originalEvent && event.originalEvent.persisted) {
    		window.location.reload()
    	}
    });
    UPMusic.pageInit();
    $('.collapse a').click(function () {
    	UPMusic.closeSidebar();
    	var currentURL = window.location.href;
    	var targetURL = $(this).attr('href');
    	var hashCurrent = currentURL.substring(currentURL.lastIndexOf('#'), currentURL.length);
    	var hashTarget = targetURL.substring(targetURL.lastIndexOf('#'), targetURL.length);
    	if(hashCurrent == hashTarget){
    		window.location.reload();
		} else {
            UPMusic.startLoading();
        }
    });
    $('#mobile_home').click(function () {
        UPMusic.closeSidebar();
        UPMusic.startLoading();
    });
    UPMusic.stopLoading();
});

/*$(document).ready(function () {
    //jm
    $("#demo-gallery-2").unitegallery({
        //slider_transition: "fade",
        slider_enable_text_panel: true,
        slider_enable_bullets: true
    });
});*/

