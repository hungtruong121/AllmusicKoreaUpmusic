/**
 * jQuery音频播放
 * @Author syantao
 * Created by Keystion on 2016-12-19
 * @param  {[type]} jQuery [description]
 */
;(function(jQuery){
    "use strict";
    var AudioPlayer = {
        options: {
            debuggers: false,           // 是否开启播放记录
            container: 'body',               // 把组件塞到什么地方
            source: '',                 // 音频源文件
            imagePath: '/img',                 // 音频源文件
            seekNum: 0,                 // 拖拽是判断之前是否播放
            allowSeek: true,            // 是否可以拖拽
            canplayCallback: null,      // 可以播放之后，做某些事情
            playCallback: null,         // 播放之后，做某些事情
            pauseCallback: null,        // 暂停之后，做某些事情
            timeupdateCallback: null,   // 拖动之后，做某些事情
            endedCallback: null,        // 播放结束之后，做某些事情
            mutedCallback: null,        // 静音之后，做某些事情
            template: $('<div id="componentWrapper">' +
                '<audio id="audio-element" style="display: none;">Your Browser doesnt support Audio tag.</audio>' +
                '<div class="contentPlayer">' +
					'<div class="player_progress player_barwrap"><div class="progress_bg"></div><div class="load_progress"></div>' +
	                '<div class="play_progress player_bar"></div></div>' +
					'<div class="info">' +
						'<p id="player-track-title" class="subject"></p>' +
						'<p class="time"><span class="player_mediaTime_current">00:00</span><span class="player_mediaTime_total"></span></p>' +
					'</div>' +
					'<div class="p_btn"><p class="controls_toggle"><img src="" alt="controls_toggle" /></p></div>' +
				'</div>' +
				'</div>')
        },
        elements: {},
        init: function(options) {
            var _this = this;
            if (!options || !options.container) {
                return false;
            }
            this.options = $.extend(this.options, options);

            // audio DOM
            _this.elements.audioDom = _this.options.template.find('audio')[0];
            // 播放按钮
            _this.elements.playButton = _this.options.template.find('.controls_toggle');
            // 当前时间
            _this.elements.currentTime = _this.options.template.find('.player_mediaTime_current');
            // 总时长
            _this.elements.totalTime = _this.options.template.find('.player_mediaTime_total');
            // 加载进度条
            _this.elements.loadProgress = _this.options.template.find('.load_progress');
            // 播放进度条
            _this.elements.playProgress = _this.options.template.find('.play_progress');
            // 播放进度条wrap
            _this.elements.playProgressWrap = _this.options.template.find('.player_progress');
            // 播放进度条总长
            _this.elements.totalPlayProgress = _this.options.template.find('.progress_bg');
            // 静音按钮
            _this.elements.mutedButton = _this.options.template.find('.player_volume');
            // 音量大小wrap
            _this.elements.volumeWrap = _this.options.template.find('.volume_seekbar');
            // 音量总长
            _this.elements.totalVolume = _this.options.template.find('.volume_bg');
            // 当前音量
            _this.elements.currentVolume = _this.options.template.find('.volume_level');
            $(_this.options.container).append(_this.options.template);

            _this.elements.playButton.find('img').attr('src', _this.options.imagePath + '/mobile/ico_player_play.png');
            _this.elements.mutedButton.find('img').attr('src', _this.options.imagePath + '/player_btn6.png');

            if(options.source){
                _this.updateSource({source: options.source, callback: function(){ _this.log('update source') }});
            }

            _this.elements.currentVolume.width((60 / 100 * _this.elements.audioDom.volume * 100) + 'px');

            // 初始化话事件
            _this.events();
        },
        // 更新音频源
        updateSource: function(o){
            var _this = this;
            // 重置
            _this.reset();

            // 更新音频来源
            _this.elements.audioDom.setAttribute('src', o.source);

            if(typeof o.callback == 'function'){
                o.callback();
            }
        },
        // dom 绑定事件
        events: function() {
            var _this = this;
            var volumeDrag = false;
            _this.elements.volumeWrap.on('mousedown', function(event) {
				volumeDrag = true;
				var volume = _this.elements.totalVolume;
        		var position = event.pageX - volume.offset().left;
				_this.elements.currentVolume.width(position + "px");
				_this.setvolume({
                    event: event
                });
			});
			$(document).on('mouseup', function(event) {
				if (volumeDrag) {
					volumeDrag = false;
					var volume = _this.elements.totalVolume;
	        		var position = event.pageX - volume.offset().left;
					_this.elements.currentVolume.width(position + "px");
					_this.setvolume({
	                    event: event
	                });
				}
			});
			$(document).on('mousemove', function(event) {
				if (volumeDrag) {
					var volume = _this.elements.totalVolume;
	        		var position = event.pageX - volume.offset().left;
					_this.elements.currentVolume.width(position + "px");
					_this.setvolume({
	                    event: event
	                });
				}
			});
            
            _this.elements.mutedButton.on('click', function(event) {
                event.preventDefault();
                /* Act on the event */
                _this.muted();
            });

            var timeDrag = false;
            _this.elements.playProgressWrap.mousedown(function (event) {
				timeDrag = true;
				_this.setPlayProgressWidth(event);
		    });
		    $(document).mouseup(function (event) {
		        if (timeDrag) {
		        	timeDrag = false;
		        	_this.setPlayProgressWidth(event);
		        }
		    });
		    $(document).mousemove(function (event) {
		        if (timeDrag) {
		        	_this.setPlayProgressWidth(event);
		        }
		    });
		    
            // 播放暂停
            _this.elements.playButton.on('click', function(event) {
                event.preventDefault();
                _this.toggleplay({
                    playCallback: function(){ _this.log('toggleplay：play') },
                    pauseCallback: function(){ _this.log('toggleplay：pause') }
                });
            })

            // 当音频的加载已放弃时
            _this.elements.audioDom.onabort = function() {
                _this.log('onabort');

                _this.reset();
            }

            // 当浏览器可以播放音频时
            _this.elements.audioDom.oncanplay = function() {
                _this.log('oncanplay');
                // 判断音频加载完毕
                if (_this.elements.audioDom.readyState == 4) {
                    if(typeof _this.options.canplayCallback == 'function'){
                        _this.options.canplayCallback({'status': true});
                    }
                }
            }
            
            _this.elements.audioDom.addEventListener('loadedmetadata', function() {
            	var duration = Math.round(_this.elements.audioDom.duration);
                var minutes = Math.floor(duration / 60);
                minutes = 10 <= minutes ? minutes : "0" + minutes;
                duration = Math.floor(duration % 60);

                _this.elements.totalTime.html(minutes + ":" + (10 <= duration ? duration : "0" + duration)); 
            });

            // 当浏览器正在下载音频时
            _this.elements.audioDom.onprogress = function() {
                if (_this.elements.audioDom.readyState == 4) {
                    _this.elements.loadProgress.width((_this.elements.audioDom.buffered.end(0) / _this.elements.audioDom.seekable.end(0)) * _this.elements.totalPlayProgress.width());
                }
            };

            // 当浏览器开始查找音频时
            _this.elements.audioDom.onloadstart = function() {
                _this.log('onloadstart');
            }

            // 当音频已开始或不再暂停时
            _this.elements.audioDom.onplay = function() {
                _this.log('onplay');
                _this.elements.playButton.find('img').attr('src', _this.options.imagePath + '/music_stop.png');
            }

            // 当音频已暂停时
            _this.elements.audioDom.onpause = function() {
                _this.log('onpause');
                _this.elements.playButton.find('img').attr('src', _this.options.imagePath + '/mobile/ico_player_play.png');
            }

            // 当目前的播放列表已结束时
            _this.elements.audioDom.onended = function() {
                _this.log('onended');

                if(typeof _this.options.endedCallback == 'function'){
                    _this.options.endedCallback({'status': _this.elements.audioDom.ended});
                }
            }

            // 当用户开始移动/跳跃到音频中的新位置时
            _this.elements.audioDom.onseeking = function() {
                _this.log('onseeking');
            }

            // 当用户已移动/跳跃到音频中的新位置时
            _this.elements.audioDom.onseeked = function() {
                _this.log('onseeked');
            }

            // 当目前的播放位置已更改时
            _this.elements.audioDom.ontimeupdate = function() {
                _this.log('ontimeupdate');
                _this.timeupdate();
            }

            // 当音量已更改时
            _this.elements.audioDom.onvolumechange = function() {
                _this.log('onvolumechange');
                _this.setvolume();
            }
        },
        // 切换播放/暂停
        toggleplay: function(o) {
            var _this = this;
            if (_this.elements.audioDom.paused) {
                _this.play(o.playCallback);
            } else {
                _this.pause(o.pauseCallback);
            }
        },
        // 设置播放
        play: function(o) {
            var _this = this;
            _this.elements.audioDom.play();
            if (typeof o == 'function') {
                o({'status': _this.elements.audioDom.play});
            }

            if(typeof _this.options.playCallback == 'function'){
                _this.options.playCallback({'status': _this.elements.audioDom.play});
            }
        },
        // 设置暂停
        pause: function(o) {
            var _this = this;
            _this.elements.audioDom.pause();
            if (typeof o == 'function') {
                o({'status': _this.elements.audioDom.play});
            }

            if(typeof _this.options.pauseCallback == 'function'){
                _this.options.pauseCallback({'status': _this.elements.audioDom.play});
            }
        },
        // 设置静音否
        muted: function(o) {
            var _this = this;
            if (_this.elements.audioDom.muted) {
                _this.elements.audioDom.muted = false;
                _this.elements.mutedButton.find('img').attr('src', _this.options.imagePath + '/player_btn6.png');
                $('.volume_seekbar .player_bar').show();
            } else {
                _this.elements.audioDom.muted = true;
                _this.elements.mutedButton.find('img').attr('src', _this.options.imagePath + '/mute.png');
                $('.volume_seekbar .player_bar').hide();
            }

            if(typeof _this.options.mutedCallback == 'function'){
                _this.options.mutedCallback({'status': _this.elements.audioDom.muted});
            }
        },
        // 设置声音大小
        setvolume: function(options) {
            var _this = this;
            if (!options) {
                _this.elements.currentVolume.width((_this.elements.totalVolume.width() / 100 * _this.elements.audioDom.volume * 100) + 'px');
            } else {
                if (options.event) {
                    return _this.elements.audioDom.volume = _this.sumVolume(options.event) / 100;
                } else {
                    // _this.log 需要优化
                    _this.options.debuggers && console.error('缺少参数：options.event');
                    return false;
                }
            }
        },
        // 设置播放进度条
        setPlayProgressWidth: function(argument) {
            var _this = this;
            if (!argument) {
                return false;
            }

            // if ((argument.clientX - _this.elements.playProgressWrap.offset().left) > _this.elements.loadProgress.width()) {
            //     _this.elements.playProgress.width(argument.clientX - _this.elements.playProgressWrap.offset().left);
            // } else {
                _this.elements.playProgress.width(argument.clientX - _this.elements.playProgressWrap.offset().left);
                _this.elements.audioDom.currentTime = (argument.clientX - _this.elements.playProgressWrap.offset().left) / _this.elements.totalPlayProgress.width() * _this.elements.audioDom.duration;
            // }
        },
        // 播放时间更改
        timeupdate: function() {
            var _this = this;

            var currTime = Math.floor(_this.elements.audioDom.currentTime).toString();
            var duration = Math.floor(_this.elements.audioDom.duration).toString();

            _this.elements.currentTime.html(_this.formatTime(currTime));

            if (isNaN(duration)) {
                _this.elements.totalTime.html('00:00');
            } else {
                _this.elements.totalTime.html(_this.formatTime(duration));
            }
            _this.elements.playProgress.width((_this.elements.audioDom.currentTime / _this.elements.audioDom.duration) * $('.progress_bg').width());
        },
        // 格式化时间戳相关
        formatTime: function(secs, format) {
            var hr = Math.floor(secs / 3600);
            var min = Math.floor((secs - (hr * 3600)) / 60);
            var sec = Math.floor(secs - (hr * 3600) - (min * 60));

            if (min < 10) {
                min = "0" + min;
            }
            if (sec < 10) {
                sec = "0" + sec;
            }

            return min + ':' + sec;
        },
        // 计算音量
        sumVolume: function(event) {
            var _this = this;
            if (!event) {
                return false;
            }
            var num = event.pageX - _this.elements.totalVolume.offset().left;
            num = Math.max(0, Math.min(1, num / _this.elements.totalVolume.width()));
            num = parseInt(100 * num, 10);
            return num;
        },
        // 重置函数
        reset: function(){
            var _this = this;

            // 播放按钮 当前播放时间 总时间 音量 播放进度条 下载进度条
            _this.elements.playButton.find('img').attr('src', _this.options.imagePath + '/mobile/ico_player_play.png');
            $('#player-track-title').html($('#player-track-title-html').html());
            _this.elements.currentTime.html('00:00');
            _this.elements.totalTime.html('00:00');
            //_this.elements.currentVolume.width(_this.elements.totalVolume);
            _this.elements.playProgress.width(0);
            _this.elements.loadProgress.width(0);
        },
        log: function(log){
            var _this = this;

            if(_this.options.debuggers){
                return console.info(log);
            }
        }
    }

    jQuery.AudioPlayer = AudioPlayer;
})(jQuery);