/**
 * upmusic admin javascript
 */
var UPMusicAdmin = {
	_config : {
		_is_debug : true,
		_admin_track : '/admin/music/track',
		_admin_tracks : '/admin/music/tracks',
		_admin_vidoes : '/admin/video',
		_admin_season : '/admin/music/season',
		_admin_guide : '/admin/music/guide_vocal',
		_admin_video : '/admin/video',
		_admin_banner : '/admin/main/banner',
		_admin_api_reward : '/admin/api/reward',
		_admin_api_terms : '/admin/api/terms',
		loading : false
	},
	init : function() {
		// init
	},
	log : function(msg) {
		if (UPMusicAdmin._config._is_debug) console.log(msg);
	},
	alert : function(msg, error) {
		alert(msg);
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
		window.location = url;
	},
	goBack: function() {
		window.history.back();
	},
	ajaxSubmit : function(frm, callback) {
		UPMusicAdmin.log("UPMusicAdmin.ajaxSubmit");
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
		    		UPMusicAdmin.alert(message, true);
		    	}
		    }
		});
		return false;
	},
	ajaxPost: function(url, headers, params, callback) {
		UPMusicAdmin.log("UPMusicAdmin.ajaxPost");
		UPMusicAdmin.log(headers);
		UPMusicAdmin.log(params);
		$.ajax({
		    url: url,
		    type: 'post',
		    headers: headers,
		    data: JSON.stringify(params),
		    contentType: "application/json; charset=UTF-8",
		    dataType: 'json',
		    cache: false,
		    success: function (data) {
		    	UPMusicAdmin.log(data);
		        var result = data.status;
				var message = data.message;
				var object = data.object;
				if (result) {
					callback(message, object);
		    	} else {
		    		UPMusicAdmin.alert(message, true);
		    	}
		    },
            error: function (e) {
            	UPMusicAdmin.alert(e.message, true);
            }
		});
	},
	ajaxPostFormData: function(url, headers, formData, callback) {
		$.ajax({
		    url: url,
		    type: 'post',
		    headers: headers,
		    data: formData,
		    cache: false,
		    processData: false,
		    contentType: false,
		    success: function (data) {
		    	UPMusicAdmin.log(data);
		        var result = data.status;
				var message = data.message;
				var object = data.object;
				console.log(result + ":" + message + ":" + object);
				if (result) {
					callback(message, object);
		    	} else {
		    		UPMusicAdmin.alert(message, true);
		    	}
		    },
            error: function (e) {
            	UPMusicAdmin.alert(e.message, true);
            }
		});
	},
    ajaxPostFormDataXml: function(url, headers, formData, callback) {
        $.ajax({
            url: url,
            type: 'post',
            headers: headers,
            data: formData,
            cache: false,
            processData: false,
            contentType: false,
			dataType: 'text',
            success: function (data) {
                UPMusicAdmin.log(data);
                callback();
            },
            error: function (e) {
                UPMusicAdmin.alert('', true);
            }
        });
    },
	ajaxPostResObj: function(url, headers, params, callback) {
		UPMusicAdmin.log("UPMusicAdmin.ajaxPostResObj");
		UPMusicAdmin.log(headers);
		UPMusicAdmin.log(params);
		$.ajax({
		    url: url,
		    type: 'post',
		    headers: headers,
		    data: JSON.stringify(params),
		    contentType: "application/json; charset=UTF-8",
		    dataType: 'json',
		    cache: false,
		    success: function (data) {
		    	UPMusicAdmin.log(data);
				callback(data);
		    },
            error: function (e) {
            	callback(null);
            }
		});
	},
	ajaxDelete: function(url, headers, callback) {
		UPMusicAdmin.log("UPMusicAdmin.ajaxDelete");
		$.ajax({
		    url: url,
		    type: 'delete',
		    headers: headers,
		    contentType: "application/json; charset=UTF-8",
		    dataType: 'json',
		    cache: false,
		    success: function (data) {
		    	UPMusicAdmin.log(data);
		        var result = data.status;
				var message = data.message;
				var object = data.object;
				if (result) {
					callback(message, object);
		    	} else {
		    		UPMusicAdmin.alert(message, true);
		    	}
		    },
            error: function (e) {
            	UPMusicAdmin.alert(e.message, true);
            }
		});
	},
	ajaxDeleteItems: function(url, headers, params, callback) {
		UPMusicAdmin.log("UPMusicAdmin.ajaxDelete");
		$.ajax({
		    url: url,
		    type: 'POST',
		    headers: headers,
		    contentType: "application/json; charset=UTF-8",
		    dataType: 'json',
		    data: JSON.stringify(params),
		    cache: false,
		    success: function (data) {
		    	UPMusicAdmin.log(data);
		        var result = data.status;
				var message = data.message;
				var object = data.object;
				if (result) {
					callback(message, object);
		    	} else {
		    		UPMusicAdmin.alert(message, true);
		    	}
		    },
            error: function (e) {
            	UPMusicAdmin.alert(e.message, true);
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
		UPMusicAdmin.log("UPMusicAdmin.loadAppend");
		UPMusicAdmin.log(headers);
		UPMusicAdmin.log(params);
		$.ajax({
		    url: url,
		    type: 'post',
		    headers: headers,
		    data: JSON.stringify(params),
		    contentType: "application/json; charset=UTF-8",
		    dataType: 'json',
		    cache: false,
		    success: function (data) {
		    	UPMusicAdmin.log(data);
		    	$(data).appendTo("#" + div_id).fadeIn("slow");
		    },
            error: function (e) {
            	callback(null);
            }
		});
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
	selectItem : function(prefix, item) {
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
    	return minutes + ":" + seconds;
	},
	acceptTrack: function(trackId) {
		if (UPMusicAdmin._config.loading) return;
		UPMusicAdmin._config.loading = true;
		var params = {};
		UPMusicAdmin.ajaxPost(UPMusicAdmin._config._admin_track + '/' + trackId + '/accept', headers, params, UPMusicAdmin.acceptTrackCallback);
	},
    acceptTracks: function(ids) {
		console.log(ids);
        if (UPMusicAdmin._config.loading) return;
        UPMusicAdmin._config.loading = true;
        var params = ids;
        UPMusicAdmin.ajaxPost(UPMusicAdmin._config._admin_tracks + '/accept', headers, params, UPMusicAdmin.acceptTrackCallback);
    },
	acceptTrackCallback: function(msg, obj) {
		console.log(msg + ":" + obj);
		if (msg == 'true') {
			window.location = '/admin/music';
		} else {
			UPMusicAdmin.alert('해당 곡의 심사 처리가 되지 않았습니다.');
		}
		UPMusicAdmin._config.loading = false;
	},
	acceptTracksAdmin: function(ids) {
		console.log(ids);
        if (UPMusicAdmin._config.loading) return;
        UPMusicAdmin._config.loading = true;
        var params = ids;
        UPMusicAdmin.ajaxPost(UPMusicAdmin._config._admin_tracks + '/accept', headers, params, UPMusicAdmin.acceptTrackAdminCallback);
    },
	acceptTrackAdminCallback: function(msg, obj) {
		console.log(msg + ":" + obj);
		if (msg == 'true') {
			window.location = '/admin/music/music_admin';
		} else {
			UPMusicAdmin.alert('해당 곡의 심사 처리가 되지 않았습니다.');
		}
		UPMusicAdmin._config.loading = false;
	},
	rejectTrack: function(trackId, reason) {
		if (UPMusicAdmin._config.loading) return;
		UPMusicAdmin._config.loading = true;
		var params = {reason: reason};
		UPMusicAdmin.ajaxPost(UPMusicAdmin._config._admin_track + '/' + trackId + '/reject', headers, params, UPMusicAdmin.rejectTrackCallback);
	},
	rejectTracks: function(ids, reason) {
		if (UPMusicAdmin._config.loading) return;
		UPMusicAdmin._config.loading = true;
		var params = {
            ids: ids,
			reason: reason
		};
		UPMusicAdmin.ajaxPost(UPMusicAdmin._config._admin_tracks + '/reject', headers, params, UPMusicAdmin.rejectTrackCallback);
	},
	rejectTrackCallback : function(msg, obj) {
		if (msg == 'true') {
			window.location = '/admin/music';
		} else {
			UPMusicAdmin.alert('해당 곡의 심사 처리가 되지 않았습니다.');
		}
		UPMusicAdmin._config.loading = false;
	},
	rejectTracksAdmin: function(ids, reason) {
		if (UPMusicAdmin._config.loading) return;
		UPMusicAdmin._config.loading = true;
		var params = {
            ids: ids,
			reason: reason
		};
		UPMusicAdmin.ajaxPost(UPMusicAdmin._config._admin_tracks + '/reject', headers, params, UPMusicAdmin.rejectTrackAdminCallback);
	},
	rejectTrackAdminCallback : function(msg, obj) {
		if (msg == 'true') {
			window.location = '/admin/music/music_admin';
		} else {
			UPMusicAdmin.alert('해당 곡의 심사 처리가 되지 않았습니다.');
		}
		UPMusicAdmin._config.loading = false;
	},
	recommendTrack: function(trackId, e) {
		if (!e) var e = window.event;
	    e.cancelBubble = true;
	    if (e.stopPropagation) e.stopPropagation();
	    if (UPMusicAdmin._config.loading) return;
		UPMusicAdmin._config.loading = true;
		var params = {};
		UPMusicAdmin.ajaxPost(UPMusicAdmin._config._admin_track + '/' + trackId + '/recommend', headers, params, UPMusicAdmin.recommendTrackCallback);
	},
	recommendTrackCallback: function(msg, obj) {
		if (msg == 'true') {
			$('[id="' + obj + '"]').each(function(value) {
				if ($(this).hasClass('like_btn_round_off')) $(this).removeClass('like_btn_round_off').addClass('like_btn_round_on');
			});
		} else {
			$('[id="' + obj + '"]').each(function(value) {
				if ($(this).hasClass('like_btn_round_on')) $(this).removeClass('like_btn_round_on').addClass('like_btn_round_off');
			});
		}
		UPMusicAdmin._config.loading = false;
	},
	acceptGuide: function(guideId) {
		if (UPMusicAdmin._config.loading) return;
		UPMusicAdmin._config.loading = true;
		var params = {};
		UPMusicAdmin.ajaxPost(UPMusicAdmin._config._admin_guide + '/' + guideId + '/accept', headers, params, UPMusicAdmin.acceptGuideCallback);
	},
	acceptGuideCallback: function(msg, obj) {
		if (msg == 'true') {
			UPMusicAdmin.alert('해당 가이드 보컬의 심사 처리가 되었습니다.');
			window.location = '/admin/music/guide_vocal_judging';
		} else {
			UPMusicAdmin.alert('해당 가이드 보컬의 심사 처리가 되지 않았습니다.');
		}
		UPMusicAdmin._config.loading = false;
	},
	rejectGuide: function(guideId) {
		if (UPMusicAdmin._config.loading) return;
		UPMusicAdmin._config.loading = true;
		var params = {};
		UPMusicAdmin.ajaxPost(UPMusicAdmin._config._admin_guide + '/' + guideId + '/reject', headers, params, UPMusicAdmin.rejectGuideCallback);
	},
	rejectGuideCallback : function(msg, obj) {
		if (msg == 'true') {
			UPMusicAdmin.alert('해당 가이드 보컬의 심사 처리가 되었습니다.');
			window.location = '/admin/music/guide_vocal_judging';
		} else {
			UPMusicAdmin.alert('해당 가이드 보컬의 심사 처리가 되지 않았습니다.');
		}
		UPMusicAdmin._config.loading = false;
	},
	recommendVideo : function(videoId, e) {
		if (!e) var e = window.event;
	    e.cancelBubble = true;
	    if (e.stopPropagation) e.stopPropagation();
		var params = {};
		if (UPMusicAdmin._config.loading) return;
		UPMusicAdmin._config.loading = true;
		UPMusicAdmin.ajaxPost(UPMusicAdmin._config._admin_video + '/' + videoId + '/recommend', headers, params, UPMusicAdmin.recommendVideoCallback);
	},
	recommendVideoCallback: function(msg, obj) {
		if (msg == 'true') {
			$('[id="' + obj + '"]').each(function(value) {
				if ($(this).hasClass('like_btn_round_off')) $(this).removeClass('like_btn_round_off').addClass('like_btn_round_on');
			});
		} else {
			$('[id="' + obj + '"]').each(function(value) {
				if ($(this).hasClass('like_btn_round_on')) $(this).removeClass('like_btn_round_on').addClass('like_btn_round_off');
			});
		}
		UPMusicAdmin._config.loading = false;
	},
	deleteSeason: function(seasonId) {
		var r = confirm("해당 시즌과 연관된 모든 차트가 함께 삭제됩니다. 정말로 삭제하시겠습니까?");
		if (r == true) {
			var param = {
				id : seasonId
			};
			UPMusic.ajaxDeleteItems(UPMusicAdmin._config._admin_season + '/' + seasonId + '/delete', headers, param, UPMusicAdmin.deleteSeasonCallback);
		}
	},
	deleteSeasonCallback : function(msg, obj) {
		if (msg == 'true') {
			window.location = '/admin/music/season';
		} else {
			UPMusicAdmin.alert('해당 시즌을 삭제하지 못했습니다.');
		}
	},
	deleteTracks: function(ids) {
        if (UPMusicAdmin._config.loading) return;
        UPMusicAdmin._config.loading = true;
		var r = confirm("선택된 음원이 모두 삭제됩니다. 정말로 삭제하시겠습니까?");
        var params = {ids : ids};
		if (r == true) {
            UPMusicAdmin.ajaxPost(UPMusicAdmin._config._admin_tracks + '/delete_tracks', headers, params, UPMusicAdmin.deleteTracksCallback);
		}
	},
	deleteTracksCallback : function(msg, obj) {
		if (msg == 'true') {
			window.location.reload();
		} else {
			UPMusicAdmin.alert('해당 음원을 삭제하지 못했습니다.');
		}
	},
	deleteVideos: function(ids) {
        if (UPMusicAdmin._config.loading) return;
        UPMusicAdmin._config.loading = true;
		var r = confirm("선택된 영상이 모두 삭제됩니다. 정말로 삭제하시겠습니까?");
        var params = {ids : ids};
		if (r == true) {
            UPMusicAdmin.ajaxPost(UPMusicAdmin._config._admin_vidoes + '/delete_videos', headers, params, UPMusicAdmin.deleteVideosCallback);
		}
	},
	deleteVideosCallback : function(msg, obj) {
		if (msg == 'true') {
			window.location.reload();
		} else {
			UPMusicAdmin.alert('해당 영상을 삭제하지 못했습니다.');
		}
	},
    deleteBanner: function(bannerId) {
        var r = confirm("해당 배너가 삭제됩니다. 정말로 삭제하시겠습니까?");
        if (r == true) {
            var param = {
                id : bannerId
            };
            UPMusic.ajaxDeleteItems(UPMusicAdmin._config._admin_banner + '/' + bannerId+ '/delete', headers, param, UPMusicAdmin.deleteBannerCallback);
        }
    },
    deleteBannerCallback : function(msg, obj) {
        if (msg == 'true') {
            window.location = '/admin/main/banner';
        } else {
            UPMusicAdmin.alert('해당 배너를 삭제하지 못했습니다.');
        }
    },
	pageInit: function() {
		$(".tab_design ul li").mouseover(function(){
			tabWidth = $(this).width();
			tabNum = $(this).index();
			$(".tab_line").stop().animate({"left":tabWidth*tabNum+"px"},300);
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
			/*$(this).siblings(".FAQ_Answer").slideToggle(300);
			$(this).parents().siblings().children(".FAQ_Answer").slideUp(300);*/
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
	}
	
};

$(function() {
	$('#upm-search-input').on('keydown', function(e) {
	    if (e.which == 13) {
	        e.preventDefault();
	        var query = $('#upm-search-input').val();
	        if (query != '') {
	        	UPMusicAdmin.goto('/admin/search?q=' + query);
	        }
	    }
	});
    UPMusicAdmin.stopLoading();
	UPMusicAdmin.pageInit();
});