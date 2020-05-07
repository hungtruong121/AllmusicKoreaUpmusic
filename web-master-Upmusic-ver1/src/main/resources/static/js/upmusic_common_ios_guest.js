var UPMusicCommon = {
	_config : {
		_request_login_default : 'REQUEST_LOGIN_DEFAULT',
		_request_login_facebook : 'REQUEST_LOGIN_FACEBOOK',
		_request_login_google : 'REQUEST_LOGIN_GOOGLE',
		_request_login_kakao : 'REQUEST_LOGIN_KAKAO',
		_request_login_naver : 'REQUEST_LOGIN_NAVER',
		_request_password_change : 'REQUEST_PASSWORD_CHANGE',
		_request_share_url : 'REQUEST_URL_SHARE',
		_request_play_video : 'REQUEST_PLAY_VIDEO',
		_request_pause_video : 'REQUEST_PAUSE_VIDEO',
		_request_stop_video : 'REQUEST_STOP_VIDEO',
		email: '',
		password: '',
		loading : false
	},
	openPlayer : function(playerUrl) {
		UPMusic.alert(msgUserAfterLogin);
	},
	login: function(e) {
		if (!e) var e = window.event;
	    e.cancelBubble = true;
	    if (e.stopPropagation) e.stopPropagation();
	    var params = {
    		email: $('#mobile-login-form #demo-inline-inputmail').val(),
	    	password: $('#mobile-login-form #demo-inline-inputpass').val() 	
	    };
		UPMusic.ajaxPost(UPMusic._config._api_auth + '/login', headers, params, UPMusicCommon.loginCallback);
	},
	loginCallback : function(msg, object) {
		UPMusic.log("loginCallback : object is " + object);
		var data = {
    		request_type: 	UPMusicCommon._config._request_login_default,
    		email: $('#mobile-login-form #demo-inline-inputmail').val(),
    		password: $('#mobile-login-form #demo-inline-inputpass').val(),
    		remember_me: $('#mobile-login-form #remember-me').is(":checked"),
    		session_id: msg,
    		user : object
	    };
		webkit.messageHandlers.callbackFromJS.postMessage(JSON.stringify(data));
		window.location.href = "/";
	},
	registeredNewMember : function(email, password) {
		UPMusicCommon._config.email = email;
		UPMusicCommon._config.password = password;
		var params = {
    		email: email,
	    	password: password
	    };
		UPMusic.ajaxPost(UPMusic._config._api_auth + '/login', headers, params, UPMusicCommon.registeredNewMemberCallback);
	},
	registeredNewMemberCallback : function(msg, object) {
		UPMusic.log("registeredNewMemberCallback : object is " + object);
		var data = {
    		request_type: 	UPMusicCommon._config._request_login_default,
    		email: UPMusicCommon._config.email,
    		password: UPMusicCommon._config.password,
    		remember_me: true,
    		session_id: msg,
    		user : object
	    };
		webkit.messageHandlers.callbackFromJS.postMessage(JSON.stringify(data));
		window.location.href = "/";
	},
	changedPassword : function(email, password) {
		UPMusicCommon._config.password = password;
		var params = {
    		email: email,
	    	password: password
	    };
		UPMusic.ajaxPost(UPMusic._config._api_auth + '/login', headers, params, UPMusicCommon.changedPasswordCallback);
	},
	changedPasswordCallback : function(msg, object) {
		UPMusic.log("changedPasswordCallback : object is " + object);
		var data = {
    		request_type: UPMusicCommon._config._request_password_change,
    		new_password: UPMusicCommon._config.password,
    		remember_me: true,
    		session_id: msg,
    		user : object
	    };
		webkit.messageHandlers.callbackFromJS.postMessage(JSON.stringify(data));
		window.location.href = "/";
	},
	socialLogin : function(sns) {
		var request_type = UPMusicCommon._config._request_login_facebook;
		if (sns == 'google') request_type = UPMusicCommon._config._request_login_google;
		else if (sns == 'kakao') request_type = UPMusicCommon._config._request_login_kakao;
		else if (sns == 'naver') request_type = UPMusicCommon._config._request_login_naver;
		webkit.messageHandlers.callbackFromJS.postMessage(JSON.stringify({request_type: request_type}));
	},
	shereUrl : function(sns, urlToShare) {
		var data = {
    		request_type: UPMusicCommon._config._request_share_url,
    		sns : sns,
    		url_to_share: urlToShare
	    };
		webkit.messageHandlers.callbackFromJS.postMessage(JSON.stringify(data));
	},
	likeTrack: function(trackId, e) {
		if (!e) var e = window.event;
	    e.cancelBubble = true;
	    if (e.stopPropagation) e.stopPropagation();
		UPMusic.alert(msgUserAfterLogin);
	},
	addTrackToPlaylist : function(trackId, e) {
		if (!e) var e = window.event;
	    e.cancelBubble = true;
	    if (e.stopPropagation) e.stopPropagation();
		UPMusic.alert(msgUserAfterLogin);
	},
	addTracksToPlaylist : function(fragmentId) {
		UPMusic.alert(msgUserAfterLogin);
	},
	addTracksToPlaylistHorizontal : function(fragmentId) {
		UPMusic.alert(msgUserAfterLogin);
	},
	addTop50ToPlaylist : function(fragmentId) {
		UPMusic.alert(msgUserAfterLogin);
	},
	addAllToPlaylist : function(fragmentId) {
		UPMusic.alert(msgUserAfterLogin);
	},
	addAllToPlaylistHorizontal : function(fragmentId) {
		UPMusic.alert(msgUserAfterLogin);
	},
	showCollectionModal : function(fragmentId) {
		UPMusic.alert(msgUserAfterLogin);
	},
	showCollectionModalWithAll : function(fragmentId) {
		UPMusic.alert(msgUserAfterLogin);
	},
	showCollectionModalWithTrack : function(trackId) {
		UPMusic.alert(msgUserAfterLogin);
	},
	showCollectionModalHorizontal : function(fragmentId) {
		UPMusic.alert(msgUserAfterLogin);
	},
	showCollectionModalFromList : function(fragmentId, selectedTrack, e) {
		UPMusic.alert(msgUserAfterLogin);
	},
	likeAlbum: function(albumId) {
		UPMusic.alert(msgUserAfterLogin);
	},
	likeArtist: function(artistId) {
		UPMusic.alert(msgUserAfterLogin);
	},
	addVideoToPlaylist: function(videoId) {
		// nothing
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

//var Anchors = document.getElementsByTagName("a");
//for (var i = 0; i < Anchors.length ; i++) {
//	if (Anchors[i].href != null && Anchors[i].href != '' && Anchors[i].href != '#') {
//		Anchors[i].addEventListener("click", 
//		        function (event) {
//		            event.preventDefault();
//		            var url = this.href;
//		        	var xhttp = new XMLHttpRequest();
//		            xhttp.onreadystatechange = function() {
//		                if (this.readyState == 4 && this.status == 200) {
//		                	if (this.responseURL.includes('login')) {
//		                		UPMLoginModal.showModal();
//		                	} else {
//		                		window.location = url;
//		                	}
//		                }
//		            };
//		            xhttp.open("GET", url, true);
//		            xhttp.send();
//		        }, 
//		        false);
//	}
//}
