var UPMusicCommon = {
	_config : {
		loading : false,
		email: '',
		password: ''
	},
	openPlayer : function(playerUrl) {
		UPMLoginModal.showModal();
	},
	login: function(e) {
		if (!e) var e = window.event;
	    e.cancelBubble = true;
	    if (e.stopPropagation) e.stopPropagation();
	    document.getElementById('mobile-login-form').submit();
	},
	registeredNewMember : function(email, password) {
		UPMusicCommon._config.password = password;
		var params = {
    		email: email,
	    	password: password
	    };
		UPMusic.ajaxPost(UPMusic._config._api_auth + '/login', headers, params, UPMusicCommon.registeredNewMemberCallback);
	},
	registeredNewMemberCallback : function(msg, object) {
//		UPMusic.log("registeredNewMemberCallback : object is " + object);
//		var data = {
//    		request_type: 	UPMusicCommon._config._request_login_default,
//    		email: UPMusicCommon._config.email,
//    		password: UPMusicCommon._config.password,
//    		remember_me: true,
//    		session_id: msg,
//    		user : object
//	    };
//		UPMusic.log(JSON.stringify(data));
		window.location.href = '/';
	},
	socialLogin : function(sns) {
		window.location.href = '/auth/' + sns;
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
    		request_type: 	UPMusicCommon._config._request_password_change,
    		new_password: UPMusicCommon._config.password,
    		remember_me: true,
    		session_id: msg,
    		user : object
	    };
		UPMusic.log(JSON.stringify(data));
		// window.location.href = '/';
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
		UPMLoginModal.showModal();
	},
	addTrackToPlaylist : function(trackId, e) {
		if (!e) var e = window.event;
	    e.cancelBubble = true;
	    if (e.stopPropagation) e.stopPropagation();
		UPMLoginModal.showModal();
	},
	addTracksToPlaylist : function(fragmentId) {
		UPMLoginModal.showModal();
	},
	addTracksToPlaylistHorizontal : function(fragmentId) {
		UPMLoginModal.showModal();
	},
	addTop50ToPlaylist : function(fragmentId) {
		UPMLoginModal.showModal();
	},
	addAllToPlaylist : function(fragmentId) {
		UPMLoginModal.showModal();
	},
	addAllToPlaylistHorizontal : function(fragmentId) {
		UPMLoginModal.showModal();
	},
	showCollectionModal : function(fragmentId) {
		UPMLoginModal.showModal();
	},
	showCollectionModalWithAll : function(fragmentId) {
		UPMLoginModal.showModal();
	},
	showCollectionModalWithTrack : function(trackId) {
		UPMLoginModal.showModal();
	},
	showCollectionModalHorizontal : function(fragmentId) {
		UPMLoginModal.showModal();
	},
	showCollectionModalFromList : function(fragmentId, selectedTrack, e) {
		if (!e) var e = window.event;
	    e.cancelBubble = true;
	    if (e.stopPropagation) e.stopPropagation();
	    UPMLoginModal.showModal();
	},
	likeAlbum: function(albumId) {
		UPMLoginModal.showModal();
	},
	likeArtist: function(artistId) {
		UPMLoginModal.showModal();
	},
	addVideoToPlaylist: function(videoId) {
		// nothing
	},
	crowdFunding: function(crowdFundingId){
		UPMLoginModal.showModal();
	},
	playVideo : function() {
		UPMusic.log("playVideo");
	},
	pauseVideo : function() {
		UPMusic.log("pauseVideo");
	},
	stopVideo : function() {
		UPMusic.log("stopVideo");
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
