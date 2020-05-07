var UPMusicCommon = {
	_config : {
		_request_playlist_update : 'REQUEST_PLAYLIST_UPDATE',
		_request_play_video : 'REQUEST_PLAY_VIDEO',
		_request_pause_video : 'REQUEST_PAUSE_VIDEO',
		_request_stop_video : 'REQUEST_STOP_VIDEO',
		_request_share_url : 'REQUEST_URL_SHARE',
		currentFragmentId : '',
		selectedTracks : [],
		loading : false,
	},
	shereUrl : function(sns, urlToShare) {
		var data = {
    		request_type: UPMusicCommon._config._request_share_url,
    		sns : sns,
    		url_to_share: urlToShare
	    };
		webkit.messageHandlers.callbackFromJS.postMessage(JSON.stringify(data));
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
			webkit.messageHandlers.callbackFromJS.postMessage(JSON.stringify({request_type: UPMusicCommon._config._request_playlist_update}));
		} else {
			UPMusic.alert(msg);
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
			webkit.messageHandlers.callbackFromJS.postMessage(JSON.stringify({request_type: UPMusicCommon._config._request_playlist_update}));
		} else {
			UPMusic.alert(msg);
			if (obj != null && obj == 'true') {
				webkit.messageHandlers.callbackFromJS.postMessage(JSON.stringify({request_type: UPMusicCommon._config._request_playlist_update}));
			}
		}
		UPMusicCommon._config.loading = false;
	},
	addAllToPlaylist : function(fragmentId) {
		if (UPMusicCommon._config.loading) return;
	    UPMusicCommon._config.loading = true;
	    var selectedTracks = $(fragmentId + ' dl.track-list dd').map(function() {
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
	playVideo : function() {
		UPMusic.log("playVideo");
		try {
    		// var data = {
	    	// 	request_type: UPMusicCommon._config._request_play_video
		    // };
    		// webkit.messageHandlers.onClickPlayTrack.postMessage(JSON.stringify(data));
            webkit.messageHandlers.callbackFromJS.postMessage(JSON.stringify({request_type: UPMusicCommon._config._request_play_video}));
            UPMusicCommon._config.loading = false;
    	} catch (err) {
    		UPMusic.alert(err);
    	}
	},
	pauseVideo : function() {
		UPMusic.log("pauseVideo");
		try {
    		// var data = {
	    	// 	request_type: UPMusicCommon._config._request_pause_video
		    // };
    		// webkit.messageHandlers.onClickPlayTrack.postMessage(JSON.stringify(data));
            webkit.messageHandlers.callbackFromJS.postMessage(JSON.stringify({request_type: UPMusicCommon._config._request_pause_video}));
            UPMusicCommon._config.loading = false;
    	} catch (err) {
    		UPMusic.alert(err);
    	}
	},

	stopVideo : function() {
		UPMusic.log("stopVideo");
		try {
    		// var data = {
	    	// 	request_type: UPMusicCommon._config._request_stop_video
		    // };
    		// webkit.messageHandlers.onClickPlayTrack.postMessage(JSON.stringify(data));
            webkit.messageHandlers.callbackFromJS.postMessage(JSON.stringify({request_type: UPMusicCommon._config._request_stop_video}));
            UPMusicCommon._config.loading = false;
    	} catch (err) {
    		UPMusic.alert(err);
    	}
	}
};
