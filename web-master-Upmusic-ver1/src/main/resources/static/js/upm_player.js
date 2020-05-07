/**
 * upmusic player javascript
 */
var UPMPlayer = {
	_config : {
		currentUserId : 0,
		currentTrackId : 0,
		stompClient : null,
		player : null,
		playlist : [],
		audio : null,
	    context : null,
	    analyser : null,
	    source : null,
	    canvas : null,
	    canvasWidth : 470,
	    canvasHeight: 150,
	    canvasContext : null,
	    meterWidth: 4, //8,
	    gap : 0, // 1
        capHeight : 0, //2,
        capStyle : '#ffffff',
        meterCount : 480 / (1), // (10 + 2),
        capYPositionArray : [],
        gradient : null,
        likedTrackIds : []
	},
	connect: function() {
	    var socket = new SockJS('/upm-player-websocket');
	    UPMPlayer._config.stompClient = Stomp.over(socket);
	    UPMPlayer._config.stompClient.connect({}, function (frame) {
	    	UPMPlayer.setConnected(true);
	        UPMPlayer._config.stompClient.subscribe('/user/topic/player', function (playtimeMessage) {
	        	var playtime = JSON.parse(playtimeMessage.body).playtime;
//	            $("#reward-component #playtime").html(UPMusic.secondsToTime(playtime));
//	            var rewardtime = JSON.parse(playtimeMessage.body).rewardtime;
//	            var process = playtime / (rewardtime * 1000) * 100;
//	            $('#reward-progress-bar').css('width', process + '%');
//	            $('#target-reward-time').html(rewardtime + '분');
                var rewardStep = JSON.parse(playtimeMessage.body).streamingRewardStep;
                var firstLimit = JSON.parse(playtimeMessage.body).firstLimit;
                var secondLimit = JSON.parse(playtimeMessage.body).secondLimit;
                var playtime2 = playtime - (firstLimit * 60);
				$(".streaming_info #playtime").html(UPMusic.secondsToTime(playtime));
                $(".streaming_info #streaming-reward-step").html(rewardStep);
                var process = 0;
	        	switch (rewardStep){
					case 1 : process = (playtime / (firstLimit * 60)) * 100; break;
					case 2 : process = (playtime2 / (secondLimit * 60)) * 100; break;
					case 3 : process = 100; break;
				}
                $('.streaming_bar').css('width', process + '%');
	        	if (rewardStep == 1 && !$("#streaming_profile").hasClass('streaming_profile_1')) {
	        		$("#streaming_profile").removeClass();
	        		$("#streaming_profile").addClass('streaming_profile_1');
	        	}
	        	if (rewardStep == 2 && !$("#streaming_profile").hasClass('streaming_profile_2')) {
	        		$("#streaming_profile").removeClass();
	        		$("#streaming_profile").addClass('streaming_profile_2');
                }
	        	if (rewardStep == 3 && !$("#streaming_profile").hasClass('streaming_profile_3')) {
	        		$("#streaming_profile").removeClass();
                    $("#streaming_profile").addClass('streaming_profile_3');
	        	}
	        });
	        UPMPlayer._config.stompClient.send("/app/playtime", {}, JSON.stringify({'playtime': 0, 'musicTrackId': UPMPlayer._config.currentTrackId}));
	    });
	},
	setConnected: function(connected) {
	    if (connected) {
	        $("#reward-component").show();
	    } else {
	        $("#reward-component").hide();
	    }
	    setInterval(UPMPlayer.checkPlaying, 1000);
	},
	checkPlaying : function() {
		if (!$("#upm-player").data().jPlayer.status.paused && 0 < UPMPlayer._config.currentTrackId) {
			UPMPlayer._config.stompClient.send("/app/increase_playtime", {}, JSON.stringify({'playtime': 0, 'musicTrackId': UPMPlayer._config.currentTrackId}));
		}
	},
	disconnect: function() {
	    if (UPMPlayer._config.stompClient !== null) {
	    	UPMPlayer._config.stompClient.disconnect();
	    }
	    UPMPlayer.setConnected(false);
	},
	initPlaylist : function() {
		UPMPlayer._config.currentUserId = $('#player-current-user-id').val();
		var loop = false;
		var cookieLoop = this.getCookie('upm_loop_' + UPMPlayer._config.currentUserId);
		if (cookieLoop && 1 == parseInt(cookieLoop)) loop = true;
		jQuery.support.cors = true;
		var cssSelector = { jPlayer: "#upm-player", cssSelectorAncestor: "#upm-container" };
		var tracks = [];
		$('#upm-playlist ul li').each(function (index) {
			var track = {
				id: $(this).find('.id').html(),
				url: $(this).find('.url').html(),
				album_id : $(this).find('.album_id').html(),
				title: $(this).find('.title').html(),
				artist_url: $(this).find('.artist_url').html(),
			    artist: $(this).find('.artist').html(),
			    mp3: $(this).find('.mp3').html(),
			    poster: $(this).find('.poster').html(),
			    liked: $(this).find('.liked').html(),
			};
			tracks.push(track);
			if (track.liked == 'true') UPMPlayer._config.likedTrackIds.push(parseInt(track.id));
		});
		var options = { playlistOptions: {autoPlay: true, removeTime: 0, enableRemoveControls: true, loopOnPrevious: true}, play: UPMPlayer.playCallback, muted: false, volumechange: UPMPlayer.volumechangeCallback, loop: loop, /* timeupdate: UPMPlayer.timeupdateCallback,*/ useStateClassSkin: true, swfPath: "/js", supplied: "mp3" };
		setTimeout(function(){
			UPMPlayer._config.player = new jPlayerPlaylist(cssSelector, tracks, options);
			// autoplay 비정상일 경우
//			$('#upm-player').bind($.jPlayer.event.loadeddata, function(event) { 
//				if ($("#upm-player").data().jPlayer.status.paused) {
//					$("#upm-player").data().jPlayer.play(0);
//				}
//		    });
			UPMPlayer.initVisualizer();
		}, 200);
		
//		$('li.jp-previous').on('click', function(e) {
//			if (!$('#upm-container').hasClass('jp-state-looped')) {
//				var current = UPMPlayer._config.player.current;
//				if (0 == current) {
//					setTimeout(function(){ 
//						$("#upm-player").data().jPlayer.play(0);
//					}, 100);
//				}
//			}
//		});
		
//		$.get('/api/player', function(data) {
//			if (data != null) {
//				data.forEach(function (item, index) {
//					var track = {
//						id:item.id,
//						title:item.subject,
//					    artist:item.artistNick,
//					    mp3:item.filenameUrl,
//					    poster:item.coverImageUrl
//					}
//					UPMPlayer._config.playlist.push(track);
//				});
//				UPMPlayer._config.player = new jPlayerPlaylist(cssSelector, UPMPlayer._config.playlist, options);
//			}
//		});
//		$('.jp-playlist').on('click', '.jp-playlist-item-remove', function(){
//		    var index = $(this).parents('li').index('.jp-playlist li');
//		    var song = UPMPlayer._config.player.playlist[index];
//		});
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
	initVisualizer : function() {
		UPMPlayer._config.audio = document.getElementById('jp_audio_0');
		UPMPlayer._config.audio.crossOrigin = "anonymous";
		UPMPlayer._config.audio.type = "audio/mpeg";
		$('audio').prop('crossorigin', "anonymous");
		UPMPlayer._config.context = new AudioContext();
		UPMPlayer._config.analyser = UPMPlayer._config.context.createAnalyser();
		UPMPlayer._config.source = UPMPlayer._config.context.createMediaElementSource(UPMPlayer._config.audio);
		UPMPlayer._config.source.connect(UPMPlayer._config.analyser);
		UPMPlayer._config.analyser.connect(UPMPlayer._config.context.destination);
		
		UPMPlayer._config.canvas = document.getElementById('upm-visualizer-canvas');
		UPMPlayer._config.canvasContext = UPMPlayer._config.canvas.getContext('2d');
		UPMPlayer._config.gradient = UPMPlayer._config.canvasContext.createLinearGradient(0, 0, 0, 100);
		UPMPlayer._config.gradient.addColorStop(1, 'rgba(0,0,0,0)');
		UPMPlayer._config.gradient.addColorStop(0.5, 'rgba(0,0,0,0.4)');
		UPMPlayer._config.gradient.addColorStop(0, 'rgba(0,0,0,0.2)');
		
		UPMPlayer.renderFrame();
	},
	renderFrame : function() {
        var array = new Uint8Array(UPMPlayer._config.analyser.frequencyBinCount);
        UPMPlayer._config.analyser.getByteFrequencyData(array);
        var step = Math.round(array.length / UPMPlayer._config.meterCount);
        UPMPlayer._config.canvasContext.clearRect(0, 0, UPMPlayer._config.canvasWidth, UPMPlayer._config.canvasHeight);
        for (var i = 0; i < UPMPlayer._config.meterCount; i++) {
            var value = array[i * step];
            if (UPMPlayer._config.capYPositionArray.length < Math.round(UPMPlayer._config.meterCount)) {
            	UPMPlayer._config.capYPositionArray.push(value);
            };
            UPMPlayer._config.canvasContext.fillStyle = UPMPlayer._config.capStyle;
            if (value < UPMPlayer._config.capYPositionArray[i]) {
            	UPMPlayer._config.canvasContext.fillRect(i * 6, UPMPlayer._config.canvasHeight - (--UPMPlayer._config.capYPositionArray[i]), UPMPlayer._config.meterWidth, UPMPlayer._config.capHeight);
            } else {
            	UPMPlayer._config.canvasContext.fillRect(i * 6, UPMPlayer._config.canvasHeight - value, UPMPlayer._config.meterWidth, UPMPlayer._config.capHeight);
                UPMPlayer._config.capYPositionArray[i] = value;
            };
            UPMPlayer._config.canvasContext.fillStyle = UPMPlayer._config.gradient;
            UPMPlayer._config.canvasContext.fillRect(i * 6, UPMPlayer._config.canvasHeight - (value - 80) + UPMPlayer._config.capHeight, UPMPlayer._config.meterWidth, UPMPlayer._config.canvasHeight);
        }
        window.requestAnimFrame(UPMPlayer.renderFrame);
    },
	play : function() {
		UPMPlayer._config.player.play();
	},
	playCallback : function(e) {
		if (!e) var e = window.event;
	    e.cancelBubble = true;
	    if (e.stopPropagation) e.stopPropagation();
	    if (UPMusicCommon._config.loading) return;
	    UPMusicCommon._config.loading = true;
		var trackCover = '<a onclick="window.open(&quot;' + e.jPlayer.status.media.url + '&quot;, &quot;_blank&quot;);"><img src="' + e.jPlayer.status.media.poster + '"></a>';
		$('#current-track-cover').html(trackCover);
		var trackInfo = '<ul><li><a onclick="window.open(&quot;' + e.jPlayer.status.media.url + '&quot;, &quot;_blank&quot;);">' + e.jPlayer.status.media.title + '</a></li>';
		trackInfo += '<li><a onclick="window.open(&quot;' + e.jPlayer.status.media.artist_url + '&quot;, &quot;_blank&quot;);"><span class="artist">' + e.jPlayer.status.media.artist + '</span></a></li></ul>';
		trackInfo += '<div class="player_head_right"><a onclick="UPMShareModal.showShareModalFromList(' + e.jPlayer.status.media.album_id + ', ' + e.jPlayer.status.media.id + ');">';
		trackInfo += '<i><img src="img/listcontrols_icon3.png" alt=""  /></i></a>';
		trackInfo += '<a id="' + e.jPlayer.status.media.id + '" onclick="UPMPlayer.likeTrack(' + e.jPlayer.status.media.id + ');">';
		
//		if (e.jPlayer.status.media.liked == 'true') {
//			trackInfo += '<i><span id="track-like" class="heart_icon2 heart_icon_on">&nbsp;</span></i></a></div>';
//		} else {
//			trackInfo += '<i><span id="track-like" class="heart_icon2 heart_icon_off">&nbsp;</span></i></a></div>';
//		}
		var index = $.inArray(parseInt(e.jPlayer.status.media.id), UPMPlayer._config.likedTrackIds);
		if (-1 < index) {
			trackInfo += '<i><span id="track-like" class="heart_icon2 heart_icon_on">&nbsp;</span></i></a></div>';
		} else {
			trackInfo += '<i><span id="track-like" class="heart_icon2 heart_icon_off">&nbsp;</span></i></a></div>';
		}
		$('#current-track-information').html(trackInfo);
		UPMPlayer._config.currentTrackId = e.jPlayer.status.media.id;
		UPMPlayer.setCookie('upm_player_last_item_' + UPMPlayer._config.currentUserId ,e.jPlayer.status.media.id,1);

		if (0 == e.jPlayer.status.currentTime) {
			var params = {};
			UPMusic.ajaxPost(UPMusic._config._api_player + '/' + e.jPlayer.status.media.id + '/add_play_cnt', headers, params, UPMPlayer.addPlayCntCallback);
		}
	},
	addPlayCntCallback : function(msg, obj) {
		if (msg != 'true') {
			$('.jp-pause').click();
			UPMFinalMessageModal.showModal(msg);
		}
		UPMusicCommon._config.loading = false;
	},
	likeTrack: function(trackId, e) {
		if (!e) var e = window.event;
	    e.cancelBubble = true;
	    if (e.stopPropagation) e.stopPropagation();
	    if (UPMusicCommon._config.loading) return;
	    UPMusicCommon._config.loading = true;
		var params = {};
		UPMusic.ajaxPost(UPMusic._config._api_track + '/' + trackId + '/like', headers, params, UPMPlayer.likeTrackCallback);
	},
	likeTrackCallback: function(msg, object) {
		if (msg == 'true') {
			$('[id="' + object + '"]').each(function(value) {
				$(this).find("#track-like").toggleClass('heart_icon_off heart_icon_on');
			});
			if (0 > $.inArray(parseInt(object), UPMPlayer._config.likedTrackIds)) {
				UPMPlayer._config.likedTrackIds.push(parseInt(object));
			}
		} else {
			$('[id="' + object + '"]').each(function(value) {
				$(this).find("#track-like").toggleClass('heart_icon_on heart_icon_off');
			});
			var index = $.inArray(parseInt(object), UPMPlayer._config.likedTrackIds);
			if (-1 < index) {
				UPMPlayer._config.likedTrackIds.splice(index, 1);
			}
		}
		UPMusicCommon._config.loading = false;
	},
	volumechangeCallback : function(e) {
		if (e.jPlayer.options.muted) {
			$('.jp-volume-bar').hide();
		} else {
			$('.jp-volume-bar').show();
		}
	},
	removeTracksFromPlayer : function(fragmentId) {
		var selectedTracks = $(fragmentId + ' input:checkbox:checked').map(function() {
		    if (this.value) return this.value;
		}).get();
		if (0 < selectedTracks.length) {
			if (UPMusicCommon._config.loading) return;
		    UPMusicCommon._config.loading = true;
			var params = {ids : selectedTracks};
  			UPMusic.ajaxDeleteItems(UPMusic._config._api_player + '/remove_tracks', headers, params, UPMPlayer.removeTracksFromPlayerCallback);
		} else {
			UPMusic.alert("음원을 선택해주세요.");
		}		
	},
	removeTracksFromPlayerCallback : function(msg, object) {
		if (msg == 'true') {
			var selectedIndexs = $('#upm-playlist li input:checkbox:checked').map(function() {
				return $(this).parents('li').index();
			}).get();
			var reversedIndex = selectedIndexs.reverse();
			for (i in reversedIndex) {
				UPMPlayer._config.player.remove(reversedIndex[i]);
			}
			var tracks_cnt = parseInt($('#player-track-cnt').html());
			$('#player-track-cnt').html(tracks_cnt - reversedIndex.length);
			$('.playlist_delete #demo-form-inline-checkbox').prop('checked', false);
			if (0 < tracks_cnt - reversedIndex.length) {
				if ($("#upm-player").data().jPlayer.status.paused) {
					$("#upm-player").data().jPlayer.play(0);
				}
			} else {
				$('#current-track-cover').html('<img src="/img/blank_album.jpg" alt="" />');
				$('#current-track-information').html('<span class="subject">재생할 곡이 없습니다</span>');
			}
			
			// 이해할 수 없음. 선택된 곡의 경우엔 항상 -1을 반환
//			var obj = JSON.parse(object);
//			for (i in obj.ids) {
//				var removedIndex = $('.jp-playlist li').index($('input#' + obj.ids[i]).parents('li'));
//				if (-1 == removedIndex && $('input#' + obj.ids[i]).parent().parents('.jp-playlist-current').length) removedIndex = 0;
//				UPMPlayer._config.player.remove(removedIndex);
//			}
		}
		UPMusicCommon._config.loading = false;
	},
	change : function(track) {
	    var audio = $("#upm-audio");      
	    $("#mp3_src").attr("src", sourceUrl);
	    audio[0].pause();
	    audio[0].load();// suspends and restores all audio element
	    // audio[0].play(); changed based on Sprachprofi's comment below
	    audio[0].oncanplaythrough = audio[0].play();
	    UPMPlayer.play();
	}
};

window.requestAnimFrame = (function(callback) {
    return window.requestAnimationFrame || 
    window.webkitRequestAnimationFrame || 
    window.mozRequestAnimationFrame || 
    window.oRequestAnimationFrame || 
    window.msRequestAnimationFrame ||
    function(callback) { window.setTimeout(callback, 1000 / 60); };
})();
window.AudioContext = window.AudioContext || window.webkitAudioContext || window.mozAudioContext;
window.onload = function() {
	UPMPlayer.initPlaylist();
	UPMPlayer.connect();
};

