/**
 * upmusic main javascript
 */
var UPMusic = {
	_config : {
		_is_debug : true,
		_cloudfront_url: "https://d1jordkjn05iom.cloudfront.net",
		_user_id : '',
		_api_album : '/api/album',
		_api_artist : '/api/artist',
		_api_check_email : '/api/auth/check_email',
		_api_collection : '/api/collection',
		_api_guide_vocal : '/api/guide_vocal',
		_api_login : '/api/auth/login',
		_api_my_heartlist : '/api/my_heartlist',
		_api_my_playlist : '/api/my_playlist',
		_api_player : '/api/player',
		_api_request : '/api/store/request',
		_api_store : '/api/store',
		_api_track : '/api/track',
		_api_transaction : '/api/point_transaction',
		_api_video : '/api/video',
		_api_vocal_casting : '/api/vocal_casting',
		_api_request_phone_authentication_code : '/api/auth/request_phone_authentication_code',
		_api_request_pw_email_authentication_code : '/api/auth/request_pw_email_authentication_code',
		_api_request_pw_phone_authentication_code : '/api/auth/request_pw_phone_authentication_code',
		_api_search_member : '/api/member/search',
		_api_withdraw : '/api/auth/withdraw'
	},
	init : function() {
		// init
	},
	log : function(msg) {
		if (UPMusic._config._is_debug) console.log(msg);
	},
	alert : function(msg, error) {
		UPMMessageModal.showModal(msg);
//		var flash_msg = '';
//		if (error) {
//			flash_msg = '<div id="error">';
//		} else {
//			flash_msg = '<div>';
//		}
//		flash_msg += msg + '</div>';
//		$('#message').html('<div id="message-body">' + flash_msg + '</div>');
//		$('#message #message-body').slideToggle(1000).delay(3000).slideToggle(1000);
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
	goto: function(url) {
		alert('test');
    	var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
            	if (xhttp.responseURL.includes('login')) {
            		UPMLoginModal.showModal();
            	} else {
            		window.location.href = url;
            	}
            }
        };
        xhttp.open("GET", url, true);
        xhttp.send();
	},
	goto: function(url, e) {
		if (!e) var e = window.event;
	    e.cancelBubble = true;
	    if (e.stopPropagation) e.stopPropagation();
    	var xhttp;
        if (window.XMLHttpRequest) {
        	xhttp = new XMLHttpRequest();
        }
        else{
            xhttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
        xhttp.onreadystatechange = function() {
        	if (xhttp.readyState == 4 && xhttp.status == 200) {
                // if (xhttp.responseURL.includes('login')) {
                 //    UPMLoginModal.showModal();
            	// } else {
            		window.location.href = url;
            	// }
            }
        };

        // UPMusic.startLoading();

        var currentURL = window.location.href;
        var targetURL = url;
        var hashCurrent = currentURL.substring(currentURL.lastIndexOf('#'), currentURL.length);
        var hashTarget = targetURL.substring(targetURL.lastIndexOf('#'), targetURL.length);
        if(hashCurrent == hashTarget){
            window.location.reload();
        } else {
            UPMusic.startLoading();
        }

        xhttp.open("GET", url, true);
        xhttp.send();
	},
	goBack: function() {
		window.history.back();
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
	selectItem : function(item) {
		if ($(item).hasClass("on")) {
			$(item).removeClass("on");
		} else {
			$(item).addClass("on");
		}
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
	pageInit : function() {
		/*
		if (typeof Swiper === 'undefined') return; // default_layout 외에는 불필요
		var storeSwiper = null;
		var storeSwiperTheme = null;
		UPMusic.log(window.location.pathname);
		if (window.location.pathname == '/') {
			storeSwiper = new Swiper('.swiper-container', {
				slidesPerView : 'auto',
				spaceBetween : 30,
//				pagination : {
//					el : '.swiper-container .swiper-pagination',
//					clickable : true,
//				},
			});
		} else {
			storeSwiper = new Swiper('.swiper-container', {
				slidesPerView : 'auto',
				spaceBetween : 30,
//				pagination : {
//					el : '.swiper-container .swiper-pagination',
//					clickable : true,
//				},
			});
			storeSwiperTheme = new Swiper('.swiper-container-theme', {
				slidesPerView : 'auto',
				spaceBetween : 30,
//				pagination : {
//					el : '.swiper-container-theme .swiper-pagination-theme',
//					clickable : true,
//				},
			});
		}
		*/

        var searchSlide = new Swiper('.searchSwiper', {
            slidesPerView: "auto",
            spaceBetween: 0,
        });

		$('#upm-search-input').on('keydown', function(e) {
		    if (e.which == 13) {
		        e.preventDefault();
		        var query = $('#upm-search-input').val().trim();
		        if (query != '') {
		        	UPMusic.goto('/search?q=' + query);
		        } else {
		        	UPMusic.alert("검색어를 입력해주세요.");
		        	return false;
				}
		    }
		});
		$(".tab_design ul li").mouseover(function(){
			tabWidth = $(this).width();
			tabNum = $(this).index();
			$(".tab_line").stop().animate({"left":tabWidth*tabNum+"px"},300);
		});

        $(".tab_design").mouseleave(function(){
            activeNum = $(".tab_design ul li.active").index();
            activeWidth = $(".tab_design ul li").width();
            $(".tab_line").stop().animate({"left":activeNum*activeWidth+"px"},300);
        });

		$(".tab_design ul li").click(function(){
			tabWidth = $(this).width();
			tabNum = $(this).index();
			$('.tab_design ul li').removeClass('active');
			$(this).addClass('active');
			$(".tab_line").stop().animate({"left":tabWidth*tabNum+"px"},300);
			$(".tab_contentwrap>div").eq(tabNum).addClass('active').siblings("div").removeClass('active');
			window.location.hash = $(this).attr('id');
			if (location.hash) {               // do the test straight away
		        window.scrollTo(0, 0);         // execute it straight away
		        setTimeout(function() {
		            window.scrollTo(0, 0);     // run it a bit later also for browser compatibility
		        }, 1);
		    }
		});

		$(".tab_design ul li").first().addClass('active');
		if (window.location.hash) {
			var hash = window.location.hash.substring(1); //Puts hash in variable, and removes the # character
			console.log(hash);
			$(".tab_design ul li#" + hash).click();
		}
		$(".reword_close").click(function(){
			$("#modal-reward-introduction").hide();
		});

		//피드 댓글
		$(".comment-toggle").click(function(){
			$(this).parents("ul").parents().siblings(".feedcomment_wrap").slideToggle();
		});

		//2018-07-31 피드메뉴
		$(".feed_menu").click(function() {
	    if ( $(this).children(".feed_option").hasClass("feed_option_off") ){
			$(this).children(".feed_option").removeClass("feed_option_off");
	    }else{
	        $(this).children(".feed_option").addClass("feed_option_off")
	    }
		});

		//2018-08-03 이미지상세보기
		$(".img_view_list>ul>li").click(function(){
			imgViewNum = $(this).index()+1;
			$(".img_view_itemwrap>img").attr("src","/img/img_detail_item"+imgViewNum+".png");
		});

		//2018-08-03 해당 width를 이미지의 크기와 동일하게 맞춤
		/* "0020674: [업뮤직_피드] 피드 이미지 css 수정 요청" 이슈로 삭제
		var autoWidth = $(".feed_video_section>img").width();
		$(".feed_video_section").width(autoWidth);*/

        $(".funding_bar").each(function(){
            var fundingBar = $(this).width();
            var tx = $(this).siblings('p').width();
            if( fundingBar < tx + 20 ){
                $(this).siblings('p').css({"color":"#686f8b","left":fundingBar+15});
            }else{
                $(this).siblings('p').css({"color":"#FFF","left":fundingBar-tx-15});
            }
        });

		//2018-08-13 FAQ
		//탭 클릭
		$(".FAQ_tab_wrap>div").click(function(){
			indexNum = $(this).index();
			$(this).addClass("FAQ_tab_active").siblings("div").removeClass("FAQ_tab_active");
			$(".FAQ_tab_content_wrap>div").eq(indexNum).addClass('active').siblings("div").removeClass('active');
		});

		//탭 안에 컨텐츠
		$(".FAQ_Question ul").click(function(){
			// Q. 색상변화
			if( $(this).children(".Question_icon").hasClass("Question_active") ){
				$(this).children(".Question_icon").removeClass("Question_active");
			}else {
				$(this).children(".Question_icon").addClass("Question_active");
			}
			// 화살표 올리기
			if( $(this).children("li").children(".Question_ar").hasClass("Question_rotate") ){
				$(this).children("li").children(".Question_ar").removeClass("Question_rotate");
			}else {
				$(this).children("li").children(".Question_ar").addClass("Question_rotate");
			}
			$(this).siblings(".FAQ_Answer").slideToggle(300);
			$(this).parents().siblings().children(".FAQ_Answer").slideUp(300);
			$(this).parents().siblings().children("ul").children(".Question_icon").removeClass("Question_active");
			$(this).parents().siblings().children("ul").children("li").children(".Question_ar").removeClass("Question_rotate");
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

		$('.uploadpop_btn>ul>li').click(function () {
			$('#modal-upload').hide();
        });
















		//좋아요 피드버튼
		/*$(".feedcontent_footer>ul>li").click(function(){
			indexNum = $(this).index();
			if ($(this).hasClass("feedbtn_on") ){
				$(this).removeClass("feedbtn_on").children("").children("img").attr("src","/img/feedbtn_"+indexNum+".png");
			}else {
				$(this).addClass("feedbtn_on").children("").children("img").attr("src","/img/afeedbtn_"+indexNum+".png");
			}
		});*/

	},
    escapeHTML : function(html) {
		var escapeEl = document.createElement('textarea');
        escapeEl.textContent = html;
        return escapeEl.innerHTML;
    },
    unescapeHTML : function(html) {
    	var escapeEl = document.createElement('textarea');
        escapeEl.innerHTML = html;
        return escapeEl.textContent;
    },
    numberComma : function(num) {
        return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
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
	UPMusic.pageInit();
});