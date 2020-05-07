var UPMusicCommon = {
	_config : {
		currentFragmentId : '',
		selectedTracks : [],
		loading : false,
		winRef : null,
		winParams : 'status=1,directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,top=100,left=500,width=450,height=824'
	},
	openPlayer : function(playerUrl) {
		if (UPMusicCommon._config.winRef == null || typeof(UPMusicCommon._config.winRef) == 'undefined' || UPMusicCommon._config.winRef.closed) {
			UPMusicCommon._config.winRef = window.open(playerUrl, "player", UPMusicCommon._config.winParams);
		} else {
			UPMusicCommon._config.winRef.location.href = playerUrl;
			UPMusicCommon._config.winRef.focus();
			//UPMusicCommon._config.winRef.addTracks();
		}
	},
	openPlayerByFunction : function(playerUrl, currentUserId) {
		UPMusic.setCookie('upm_player_last_item_' + currentUserId,0,1); // UPMusic.eraseCookie('upm_player_last_item'); // 곡의 추가에 의해 호출되는 경우에는 쿠키를 삭제하여 추가된 곡이 재생되도록 한다
		if (UPMusicCommon._config.winRef == null || typeof(UPMusicCommon._config.winRef) == 'undefined' || UPMusicCommon._config.winRef.closed) {
			UPMusicCommon._config.winRef = window.open(playerUrl, "player", UPMusicCommon._config.winParams);
		} else {
			UPMusicCommon._config.winRef.location.href = playerUrl;
			UPMusicCommon._config.winRef.focus();
			//UPMusicCommon._config.winRef.addTracks();
		}
	},
	shereUrl : function(sns, urlToShare) {
		var snsUrl = "";
		if (sns == 'kakao') {
			snsUrl = "https://story.kakao.com/s/share?url=";
		} else if (sns == 'twitter') {
			snsUrl = "https://twitter.com/intent/tweet/?url=";
		} else if (sns == 'facebook') {
			snsUrl = "https://facebook.com/sharer/sharer.php?u=";
		} else if (sns == 'naver') {
			snsUrl = "https://share.naver.com/web/shareView.nhn?title=UPMusic&url=";
		}
		var urlToShare = document.getElementById("modal-share-url-to-copy").value;
		window.open(snsUrl + urlToShare, "_blank", "toolbar=yes,scrollbars=yes,resizable=yes,top=200,left=500,width=400,height=400");
	},
	likeTrack: function(trackId, e) {
		if (!e) var e = window.event;
	    e.cancelBubble = true;
	    if (e.stopPropagation) e.stopPropagation();
	    if (UPMusicCommon._config.loading) return;
	    UPMusicCommon._config.loading = true;
		var params = {};
		UPMusic.ajaxPost(UPMusic._config._api_track + '/' + trackId + '/like', headers, params, UPMusicCommon.likeTrackCallback);
	},
	likeTrackCallback: function(msg, object) {
		if (msg == 'true') {
			$('[id="' + object + '"]').each(function(value) {
				if (!$(this).find("#track-like").hasClass('on')) $(this).find("#track-like").addClass('on');
			});
		} else {
			$('[id="' + object + '"]').each(function(value) {
				if ($(this).find("#track-like").hasClass('on')) $(this).find("#track-like").removeClass('on');
			});
		}
		UPMusicCommon._config.loading = false;
	},
	addTrackToPlaylist : function(trackId, e) {
		if (!e) var e = window.event;
	    e.cancelBubble = true;
	    if (e.stopPropagation) e.stopPropagation();
	    if (UPMusicCommon._config.loading) return;
	    UPMusicCommon._config.loading = true;
		var selectedTracks = [trackId];
		var params = {ids : selectedTracks};
		UPMusic.ajaxPost(UPMusic._config._api_player + '/add_tracks', headers, params, UPMusicCommon.addTrackToPlaylistCallback);
	},
	addTrackToPlaylistCallback : function(msg, obj) {
		if (msg == 'true') {
			$('#button-upm-player').click();
		} else {
			UPMusic.alert(msg);
			if (obj != null && obj == 'true') {
				$('#button-upm-player').click();
			}
		}
		UPMusicCommon._config.loading = false;
	},
	addTracksToPlaylist : function(fragmentId) {
		if (UPMusicCommon._config.loading) return;
	    UPMusicCommon._config.loading = true;
		var selectedTracks = $(fragmentId + ' dl.track-list dd.on').map(function() {
		    return this.id;
		}).get();
		if (0 < selectedTracks.length) {
			UPMusicCommon._config.currentFragmentId = fragmentId;
			var params = {ids : selectedTracks.reverse()};
  			UPMusic.ajaxPost(UPMusic._config._api_player + '/add_tracks', headers, params, UPMusicCommon.addTracksToPlaylistCallback);
		} else {
			UPMusic.alert(msgSelectItem);
			UPMusicCommon._config.loading = false;
		}
	},
	addTracksToPlaylistCallback : function(msg, obj) {
		if (msg == 'true') {
//			$(UPMusicCommon._config.currentFragmentId + ' table tr.on').each(function() {
//			    $(this).removeClass('on');
//			});
			$('#button-upm-player').click();
        } else {
			UPMusic.alert(msg);
			if (obj != null && obj == 'true') {
				$('#button-upm-player').click();
			}
		}
		UPMusicCommon._config.loading = false;
	},
	addTop50ToPlaylist : function(fragmentId) {
		if (UPMusicCommon._config.loading) return;
	    UPMusicCommon._config.loading = true;
		UPMusicCommon._config.currentFragmentId = fragmentId;
		var params = {};
		UPMusic.ajaxPost(UPMusic._config._api_player + '/add_top50_tracks', headers, params, UPMusicCommon.addTracksToPlaylistCallback);
	},
	addRecentPlayedTracksToPlaylist : function(fragmentId) {
		if (UPMusicCommon._config.loading) return;
	    UPMusicCommon._config.loading = true;
		UPMusicCommon._config.currentFragmentId = fragmentId;
		var params = {};
		UPMusic.ajaxPost(UPMusic._config._api_player + '/add_recent_played_tracks', headers, params, UPMusicCommon.addTracksToPlaylistCallback);
	},
	addLikedTracksToPlaylist : function(fragmentId) {
		if (UPMusicCommon._config.loading) return;
	    UPMusicCommon._config.loading = true;
		UPMusicCommon._config.currentFragmentId = fragmentId;
		var params = {};
		UPMusic.ajaxPost(UPMusic._config._api_player + '/add_liked_tracks', headers, params, UPMusicCommon.addTracksToPlaylistCallback);
	},
	addAllToPlaylist : function(fragmentId) {
		if (UPMusicCommon._config.loading) return;
	    UPMusicCommon._config.loading = true;
		var selectedTracks = $(fragmentId + ' dl.track-list dd').map(function() {
		    if (this.id) return this.id;
		}).get();
		if (0 < selectedTracks.length) {
			UPMusicCommon._config.currentFragmentId = fragmentId;
			var params = {ids : selectedTracks.reverse()};
  			UPMusic.ajaxPost(UPMusic._config._api_player + '/add_tracks', headers, params, UPMusicCommon.addTracksToPlaylistCallback);
		} else {
			UPMusic.alert(msgSelectItem);
			UPMusicCommon._config.loading = false;
		}
	},
	addAllToPlaylistHorizontal : function(fragmentId) {
		if (fragmentId == '#home-recent-play-fragment') {
			UPMusicCommon.addRecentPlayedTracksToPlaylist(fragmentId);
		} else {
			UPMusicCommon.addLikedTracksToPlaylist(fragmentId);
		}
	},
	addTracksToPlaylistHorizontal : function(fragmentId) {
		var selectedTracks = $(fragmentId + ' .playlist_content.nearest_plbg_info_on').map(function() {
			if (this.id) return this.id;
		}).get();
		if (0 < selectedTracks.length) {
			UPMusicCommon._config.currentFragmentId = fragmentId;
			var params = {ids : selectedTracks.reverse()};
  			UPMusic.ajaxPost(UPMusic._config._api_player + '/add_tracks', headers, params, UPMusicCommon.addTracksToPlaylistCallback);
		} else {
			UPMusic.alert(msgSelectItem);
			UPMusicCommon._config.loading = false;
		}
	},
	addVideoToPlaylist : function(videoId) {
		if (UPMusicCommon._config.loading) return;
	    UPMusicCommon._config.loading = true;
		var params = {id : videoId};
		UPMusic.ajaxPost(UPMusic._config._api_my_playlist + '/add_video', headers, params, UPMusicCommon.addVideoToPlaylistCallback);
	},
	addVideoToPlaylistCallback : function(msg, obj) {
		UPMusic.log(msg);
		UPMusicCommon._config.loading = false;
	},
	showCollectionModal : function(fragmentId) {
		UPMusicCommon._config.currentFragmentId = fragmentId;
		UPMusicCommon._config.selectedTracks = $(fragmentId + ' dl.track-list dd.on').map(function() {
			if (this.id) return this.id;
		}).get();
		UPMusic.log(UPMusicCommon._config.selectedTracks);
		if (0 < UPMusicCommon._config.selectedTracks.length) {
			UPMusicCommon.renderList();
			$('#modal-collection-list').modal('show');
		} else {
			UPMusic.alert(msgSelectItem);
		}
	},
	showCollectionModalWithAll : function(fragmentId) {
		UPMusicCommon._config.currentFragmentId = fragmentId;
		UPMusicCommon._config.selectedTracks = $(fragmentId + ' dl.track-list dd').map(function() {
			if (this.id) return this.id;
		}).get();
		UPMusic.log(UPMusicCommon._config.selectedTracks);
		if (0 < UPMusicCommon._config.selectedTracks.length) {
			UPMusicCommon.renderList();
			$('#modal-collection-list').modal('show');
		} else {
			UPMusic.alert(msgSelectItem);
		}
	},
	showCollectionModalWithTrack : function(trackId) {
		UPMusicCommon._config.selectedTracks = [trackId];
		UPMusic.log(UPMusicCommon._config.selectedTracks);
		if (0 < UPMusicCommon._config.selectedTracks.length) {
			UPMusicCommon.renderList();
			$('#modal-collection-list').modal('show');
		} else {
			UPMusic.alert(msgSelectItem);
		}
	},
	showCollectionModalHorizontal : function(fragmentId) {
		UPMusicCommon._config.currentFragmentId = fragmentId;
		UPMusicCommon._config.selectedTracks = $(fragmentId + ' .playlist_content.nearest_plbg_info_on').map(function() {
			if (this.id) return this.id;
		}).get();
		UPMusic.log(UPMusicCommon._config.selectedTracks);
		if (0 < UPMusicCommon._config.selectedTracks.length) {
			UPMusicCommon.renderList();
			$('#modal-collection-list').modal('show');
		} else {
			UPMusic.alert(msgSelectItem);
		}
	},
	showCollectionModalFromList : function(fragmentId, selectedTrack, e) {
		if (!e) var e = window.event;
	    e.cancelBubble = true;
	    if (e.stopPropagation) e.stopPropagation();
		UPMusicCommon._config.currentFragmentId = fragmentId;
		UPMusicCommon._config.selectedTracks = [selectedTrack];
		UPMusicCommon.renderList();
		$('#modal-collection-list').modal('show');
	},
	showCollectionModalFromPlayer : function(fragmentId) {
		UPMusicCommon._config.currentFragmentId = fragmentId;
		UPMusicCommon._config.selectedTracks = $(fragmentId + ' input:checkbox:checked').map(function() {
			if (this.value) return this.value;
		}).get();
		UPMusic.log(UPMusicCommon._config.selectedTracks);
		if (0 < UPMusicCommon._config.selectedTracks.length) {
			UPMusicCommon.renderList();
			$('#modal-collection-list').modal('show');
		} else {
			UPMusic.alert(msgSelectItem);
		}
	},
	renderList : function() {
		$.get('/component/my_playlist/collection_modal', function(data) {
			$('#collection-list-modal-body .mylist_content').html(data);
		});
	},
	addTracksToCollection : function(collectionId) {
		if (UPMusicCommon._config.loading) return;
	    UPMusicCommon._config.loading = true;
		if (0 < UPMusicCommon._config.selectedTracks.length) {
			var params = {ids : UPMusicCommon._config.selectedTracks.reverse()};
  			UPMusic.ajaxPost(UPMusic._config._api_collection + '/' + collectionId + '/add_tracks', headers, params, UPMusicCommon.addTracksToCollectionCallback);
		} else {
			UPMusic.alert(msgSelectItem);
			UPMusicCommon._config.loading = false;
		}
	},
	addTracksToCollectionCallback : function(msg, obj) {
		if (msg == 'true') {
			UPMusic.alert(msgAddedToCollection);
			UPMusic.checkboxAllToggle(UPMusicCommon._config.currentFragmentId, false);
			if ($('.playlist_delete #demo-form-inline-checkbox').length) $('.playlist_delete #demo-form-inline-checkbox').prop('checked', false);
			$('#modal-collection-list').modal('hide');
		} else {
			UPMusic.alert(msg);
		}
		UPMusicCommon._config.loading = false;
	},
	likeAlbum: function(albumId) {
		if (UPMusicCommon._config.loading) return;
	    UPMusicCommon._config.loading = true;
		var params = {};
		UPMusic.ajaxPost(UPMusic._config._api_album + '/' + albumId + '/like_from_list', headers, params, UPMusicCommon.likeAlbumCallback);
	},
	likeAlbumCallback: function(msg, object) {
		if (msg == 'true') {
			$('[id="' + object + '"]').each(function(value) {
				if ($(this).find("#album-like").hasClass('heart_icon_off')) $(this).find("#album-like").removeClass('heart_icon_off').addClass('heart_icon_on');
			});
		} else {
			$('[id="' + object + '"]').each(function(value) {
				if ($(this).find("#album-like").hasClass('heart_icon_on')) $(this).find("#album-like").removeClass('heart_icon_on').addClass('heart_icon_off');
			});
		}
		UPMusicCommon._config.loading = false;
	},
	likeArtist: function(artistId) {
		if (UPMusicCommon._config.loading) return;
	    UPMusicCommon._config.loading = true;
		var params = {};
		UPMusic.ajaxPost(UPMusic._config._api_artist + '/' + artistId + '/like_from_list', headers, params, UPMusicCommon.likeArtistCallback);
	},
	likeArtistCallback: function(msg, object) {
		if (msg == 'true') {
			$('[id="artist-' + object[0] + '"]').each(function(value) {
				UPMusic.log("likeArtistCallback : id found - " + object[0]);
				UPMusic.log("likeArtistCallback : has no on - " + !$(this).find("#artist-like").hasClass('on'));
				if (!$(this).find("#artist-like").hasClass('on')) $(this).find("#artist-like").addClass('on');
				$(this).find("#artist-like-cnt").html(object[1]);
			});
		} else if (msg == 'false') {
			UPMusic.log("likeArtistCallback : id found - " + object[0]);
			UPMusic.log("likeArtistCallback : has on - " + $(this).find("#artist-like").hasClass('on'));
			$('[id="artist-' + object[0] + '"]').each(function(value) {
				if ($(this).find("#artist-like").hasClass('on')) $(this).find("#artist-like").removeClass('on');
				$(this).find("#artist-like-cnt").html(object[1]);
			});
		} else {
			UPMusic.alert(msg);
		}
		UPMusicCommon._config.loading = false;
	},
	playVideo : function() {
		UPMusic.log("playVideo");
	},
	pauseVideo : function() {
		UPMusic.log("pauseVideo");
	},
	stopVideo : function() {
		UPMusic.log("stopVideo");
	},
	addAlbumToPlaylist : function(albumId) {
        if (UPMusicCommon._config.loading) return;
        UPMusicCommon._config.loading = true;
		var params = {id : albumId};
  		UPMusic.ajaxPost(UPMusic._config._api_player + '/add_album', headers, params, UPMusicCommon.addTracksToPlaylistCallback);
	},
};
