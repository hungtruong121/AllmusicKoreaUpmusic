/*
 * Playlist Object for the jPlayer Plugin
 * http://www.jplayer.org
 *
 * Copyright (c) 2009 - 2014 Happyworm Ltd
 * Licensed under the MIT license.
 * http://www.opensource.org/licenses/MIT
 *
 * Author: Mark J Panaghiston
 * Version: 2.4.1
 * Date: 19th November 2014
 *
 * Requires:
 *  - jQuery 1.7.0+
 *  - jPlayer 2.8.2+
 */

/*global jPlayerPlaylist:true */

(function($, undefined) {

	jPlayerPlaylist = function(cssSelector, playlist, options) {
		var self = this;

		this.current = 0;
		this.loop = false; // Flag used with the jPlayer repeat event
		this.shuffled = false;
		this.removing = false; // Flag is true during remove animation, disabling the remove() method until complete.
		this.loading = false;
		this.timeDrag = false;
		this.volumeDrag = false;
		this.currentMediaDuration = 0;
		this.currentUserId = 0;

		this.cssSelector = $.extend({}, this._cssSelector, cssSelector); // Object: Containing the css selectors for jPlayer and its cssSelectorAncestor
		this.options = $.extend(true, {
			keyBindings: {
				next: {
					key: 221, // ]
					fn: function() {
						self.next();
					}
				},
				previous: {
					key: 219, // [
					fn: function() {
						self.previous();
					}
				},
				shuffle: {
					key: 83, // s
					fn: function() {
						self.shuffle();
					}
				}
			},
			stateClass: {
				shuffled: "jp-state-shuffled"
			}
		}, this._options, options); // Object: The jPlayer constructor options for this playlist and the playlist options

		this.playlist = []; // Array of Objects: The current playlist displayed (Un-shuffled or Shuffled)
		this.original = []; // Array of Objects: The original playlist
		this.shufflelist = [];

		this._initPlaylist(playlist); // Copies playlist to this.original. Then mirrors this.original to this.playlist. Creating two arrays, where the element pointers match. (Enables pointer comparison.)

		// Setup the css selectors for the extra interface items used by the playlist.
		this.cssSelector.details = this.cssSelector.cssSelectorAncestor + " .jp-details"; // Note that jPlayer controls the text in the title element.
		this.cssSelector.playlist = this.cssSelector.cssSelectorAncestor + " .jp-playlist";
		this.cssSelector.next = this.cssSelector.cssSelectorAncestor + " .jp-next";
		this.cssSelector.previous = this.cssSelector.cssSelectorAncestor + " .jp-previous";
		this.cssSelector.shuffle = this.cssSelector.cssSelectorAncestor + " .jp-shuffle";
		this.cssSelector.shuffleOff = this.cssSelector.cssSelectorAncestor + " .jp-shuffle-off";
		this.cssSelector.repeat = this.cssSelector.cssSelectorAncestor + " .jp-repeat";
		this.cssSelector.repeatOff = this.cssSelector.cssSelectorAncestor + " .jp-repeat-off";

		// Override the cssSelectorAncestor given in options
		this.options.cssSelectorAncestor = this.cssSelector.cssSelectorAncestor;

		// Override the default repeat event handler
		this.options.repeat = function(event) {
			if (self.loop != event.jPlayer.options.loop) {
				self.setCookie('upm_loop_' + self.currentUserId,event.jPlayer.options.loop ? '1' : '0',1)
			}
			self.loop = event.jPlayer.options.loop;
		};

		// Create a ready event handler to initialize the playlist
		$(this.cssSelector.jPlayer).bind($.jPlayer.event.ready, function() {
			self._init();
		});

		// Create an ended event handler to move to the next item
		$(this.cssSelector.jPlayer).bind($.jPlayer.event.ended, function() {
			self.timeDrag = false;
			self.next();
		});
		
		$(this.cssSelector.jPlayer).bind($.jPlayer.event.loadeddata, function(event) {
			self.currentMediaDuration = event.jPlayer.status.duration;
		});

		// Create a play event handler to pause other instances
		$(this.cssSelector.jPlayer).bind($.jPlayer.event.play, function(event) {
			$(this).jPlayer("pauseOthers");
		});

		// Create a resize event handler to show the title in full screen mode.
		$(this.cssSelector.jPlayer).bind($.jPlayer.event.resize, function(event) {
			if(event.jPlayer.options.fullScreen) {
				$(self.cssSelector.details).show();
			} else {
				$(self.cssSelector.details).hide();
			}
		});
		
		// Create click handlers for the extra buttons that do playlist functions.
		$(this.cssSelector.previous).click(function(e) {
			e.preventDefault();
			if (self.loading) return;
			self.loading = true;
			self.previous();
			self.blur(this);
		});

		$(this.cssSelector.next).click(function(e) {
			e.preventDefault();
			if (self.loading) return;
			self.loading = true;
			self.next();
			self.blur(this);
		});

		$(this.cssSelector.shuffle).click(function(e) {
			e.preventDefault();
			if (self.loading) return;
			self.loading = true;
			if(self.shuffled && $(self.cssSelector.jPlayer).jPlayer("option", "useStateClassSkin")) {
				self.shuffle(false);
			} else {
				self.shuffle(true);
			}
			self.blur(this);
		});
		$(this.cssSelector.shuffleOff).click(function(e) {
			e.preventDefault();
			if (self.loading) return;
			self.loading = true;
			self.shuffle(false);
			self.blur(this);
		}).hide();

		// Put the title in its initial display state
		if(!this.options.fullScreen) {
			$(this.cssSelector.details).hide();
		}

		// Remove the empty <li> from the page HTML. Allows page to be valid HTML, while not interfereing with display animations
		$(this.cssSelector.playlist + " ul").empty();

		// Create .on() handlers for the playlist items along with the free media and remove controls.
		this._createItemHandlers();

		// Instance jPlayer
		$(this.cssSelector.jPlayer).jPlayer(this.options);
	};

	jPlayerPlaylist.prototype = {
		_cssSelector: { // static object, instanced in constructor
			jPlayer: "#jquery_jplayer_1",
			cssSelectorAncestor: "#jp_container_1"
		},
		_options: { // static object, instanced in constructor
			playlistOptions: {
				autoPlay: false,
				loopOnPrevious: false,
				shuffleOnLoop: true,
				enableRemoveControls: false,
				displayTime: 'slow',
				addTime: 'fast',
				removeTime: 'fast',
				shuffleTime: 'slow',
				itemClass: "jp-playlist-item",
				freeGroupClass: "jp-free-media",
				freeItemClass: "jp-playlist-item-free",
				removeItemClass: "jp-playlist-item-remove"
			}
		},
		option: function(option, value) { // For changing playlist options only
			if(value === undefined) {
				return this.options.playlistOptions[option];
			}

			this.options.playlistOptions[option] = value;

			switch(option) {
				case "enableRemoveControls":
					this._updateControls();
					break;
				case "itemClass":
				case "freeGroupClass":
				case "freeItemClass":
				case "removeItemClass":
					this._refresh(true); // Instant
					this._createItemHandlers();
					break;
			}
			return this;
		},
		_init: function() {
			var self = this;
			this._refresh(function() {
				if(self.options.playlistOptions.autoPlay) {
					self.play(self.current);
				} else {
					self.select(self.current);
				}
			});
			
			$('.jp-play-bar').mousedown(function (e) {
				self.timeDrag = true;
				self.updatebar(e.pageX);
		    });
		    $(document).mouseup(function (e) {
		        if (self.timeDrag) {
		        	self.timeDrag = false;
		        	self.updatebar(e.pageX);
		        }
		    });
		    $(document).mousemove(function (e) {
		        if (self.timeDrag) {
		        	self.updatebar(e.pageX);
		        }
		    });
		    
		    $('.jp-volume-bar').mousedown(function() {
		        var parentOffset = $(this).offset(),
		        width = $(this).width();
		        $(window).mousemove(function(e) {
		            var x = e.pageX - parentOffset.left,
		            volume = x/width
		            var barValue = Math.floor(volume*100);
		            if (barValue < 0 ) barValue = 0;
		            if (barValue > 100) barValue = 100;
		            jQuery(".jp-volume-bar-value").css("width", barValue + "%");
		            //$("#player").jPlayer("volume", barValue);
		            $(self.cssSelector.jPlayer).jPlayer("volume", barValue/100);
		        });

		        return false;
		    })
		    $(document).on("mouseup", function() {
		        $(window).unbind("mousemove");
		    });
		},
		_initPlaylist: function(playlist) {
			this.currentUserId = $('#player-current-user-id').val();
			this.current = 0;
			this.loop = false;
			var cookieLoop = this.getCookie('upm_loop_' + this.currentUserId);
			if (cookieLoop && 1 == parseInt(cookieLoop)) this.loop = true;
			this.shuffled = false;
			var cookieshuffle = this.getCookie('upm_shuffle_' + this.currentUserId);
			if (cookieshuffle && 1 == parseInt(cookieshuffle)) this.shuffled = true;
			this.removing = false;
			this._updateControls();
			this.original = $.extend(true, [], playlist); // Copy the Array of Objects
			
			// 브라우저 새로고침 시 듣던 재생곡을 다시 시작하기
			var lastItemId = 0;
			var cookieLastItemId = this.getCookie('upm_player_last_item_' + this.currentUserId);
			if (cookieLastItemId && 0 < parseInt(cookieLastItemId)) {
				lastItemId = parseInt(cookieLastItemId);
				this._originalPlaylistWithLastItemId(lastItemId);
			} else {
				this._originalPlaylist();
			}
			
			if (this.shuffled) {
				this.shufflelist.sort(function() {
					return 0.5 - Math.random();
				});
			}
		},
		_originalPlaylistWithLastItemId: function(lastItemId) {
			var self = this;
			this.playlist = [];
			// Make both arrays point to the same object elements. Gives us 2 different arrays, each pointing to the same actual object. ie., Not copies of the object.
			$.each(this.original, function(i) {
				self.playlist[i] = self.original[i];
				self.shufflelist[i] = i;
				if (lastItemId == parseInt(self.original[i].id)) self.current = i;
			});
		},
		_originalPlaylist: function() {
			var self = this;
			this.playlist = [];
			// Make both arrays point to the same object elements. Gives us 2 different arrays, each pointing to the same actual object. ie., Not copies of the object.
			$.each(this.original, function(i) {
				self.playlist[i] = self.original[i];
				self.shufflelist[i] = i;
			});
		},
		_refresh: function(instant) {
			/* instant: Can be undefined, true or a function.
			 *	undefined -> use animation timings
			 *	true -> no animation
			 *	function -> use animation timings and excute function at half way point.
			 */
			var self = this;

			if(instant && !$.isFunction(instant)) {
				$(this.cssSelector.playlist + " ul").empty();
				$.each(this.playlist, function(i) {
					$(self.cssSelector.playlist + " ul").append(self._createListItem(self.playlist[i]));
				});
				this._updateControls();
			} else {
				var displayTime = $(this.cssSelector.playlist + " ul").children().length ? this.options.playlistOptions.displayTime : 0;

				$(this.cssSelector.playlist + " ul").slideUp(displayTime, function() {
					var $this = $(this);
					$(this).empty();
					
					$.each(self.playlist, function(i) {
						$this.append(self._createListItem(self.playlist[i]));
					});
					self._updateControls();
					if($.isFunction(instant)) {
						instant();
					}
					if(self.playlist.length) {
						$(this).slideDown(self.options.playlistOptions.displayTime);
					} else {
						$(this).show();
					}
				});
			}
		},
		_createListItem: function(media) {
			var self = this;

			// Wrap the <li> contents in a <div>
			var listItem = "<li id='track-" + media.id + "'><dl>";
			// The title is given next in the HTML otherwise the float:right on the free media corrupts in IE6/7
			listItem += "<dt><a href='javascript:;' class='" + this.options.playlistOptions.itemClass + "' tabindex='0'><img src='" + media.poster +"' alt='' /></a></dt>";
			listItem += "<dt><a href='javascript:;' class='" + this.options.playlistOptions.itemClass + " play_titlewrap' tabindex='0'><div class='play_titlewrap'><h3>" + media.title + "</h3><p>" + media.artist + "</p></div></a></dt>";
			listItem += "<dd><div class='checkbox playcheckbox-" + media.id + "'><input id='demo-form-inline-checkbox-" + media.id + "' class='checkbox-checked' type='checkbox' value='" + media.id + "' onclick='UPMusic.checkboxEachToggle(\"#all-checkbox\", this.checked);'><label for='demo-form-inline-checkbox-" + media.id + "'><span class=\"checkbox-custom\"></span></label></div></dd>";
			listItem += "</dl></li>";

			return listItem;
		},
		_createItemHandlers: function() {
			var self = this;
			// Create live handlers for the playlist items
			$(this.cssSelector.playlist).off("click", "a." + this.options.playlistOptions.itemClass).on("click", "a." + this.options.playlistOptions.itemClass, function(e) {
				e.preventDefault();
				var index = $(this).parent().parent().parent().index();
				if(self.current !== index) {
					self.play(index);
				} else {
					$(self.cssSelector.jPlayer).jPlayer("play");
				}
				self.blur(this);
			});

//			// Create live handlers that disable free media links to force access via right click
//			$(this.cssSelector.playlist).off("click", "a." + this.options.playlistOptions.freeItemClass).on("click", "a." + this.options.playlistOptions.freeItemClass, function(e) {
//				e.preventDefault();
//				$(this).parent().parent().find("." + self.options.playlistOptions.itemClass).click();
//				self.blur(this);
//			});
//
//			// Create live handlers for the remove controls
//			$(this.cssSelector.playlist).off("click", "a." + this.options.playlistOptions.removeItemClass).on("click", "a." + this.options.playlistOptions.removeItemClass, function(e) {
//				e.preventDefault();
//				var index = $(this).parent().parent().index();
//				self.remove(index);
//				self.blur(this);
//			});
		},
		_updateControls: function() {
			if(this.options.playlistOptions.enableRemoveControls) {
				$(this.cssSelector.playlist + " ." + this.options.playlistOptions.removeItemClass).show();
			} else {
				$(this.cssSelector.playlist + " ." + this.options.playlistOptions.removeItemClass).hide();
			}

			if(this.shuffled) {
				$(this.cssSelector.jPlayer).jPlayer("addStateClass", "shuffled");
			} else {
				$(this.cssSelector.jPlayer).jPlayer("removeStateClass", "shuffled");
			}
			if($(this.cssSelector.shuffle).length && $(this.cssSelector.shuffleOff).length) {
				if(this.shuffled) {
					$(this.cssSelector.shuffleOff).show();
					$(this.cssSelector.shuffle).hide();
				} else {
					$(this.cssSelector.shuffleOff).hide();
					$(this.cssSelector.shuffle).show();
				}
			}
			if($(this.cssSelector.repeat).length && $(this.cssSelector.repeatOff).length) {
				if(this.loop) {
					$(this.cssSelector.repeatOff).show();
					$(this.cssSelector.repeat).hide();
				} else {
					$(this.cssSelector.repeatOff).hide();
					$(this.cssSelector.repeat).show();
				}
			}
		},
		_highlight: function(index) {
			if(this.playlist.length && index !== undefined) {
				$(this.cssSelector.playlist + " .jp-playlist-current").removeClass("jp-playlist-current");
				$(this.cssSelector.playlist + " li:nth-child(" + (index + 1) + ")").addClass("jp-playlist-current").find(".jp-playlist-item").addClass("jp-playlist-current");
				// $(this.cssSelector.details + " li").html("<span class='jp-title'>" + this.playlist[index].title + "</span>" + (this.playlist[index].artist ? " <span class='jp-artist'>by " + this.playlist[index].artist + "</span>" : ""));
			}
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
		updatebar : function (x) {
	        var progress = $('.jp-progress');
	        var maxduration = this.currentMediaDuration;
	        var position = x - progress.offset().left; //Click pos
	        var percentage = 100 * position / progress.width();
	        if (percentage > 100) {
	            percentage = 100;
	        }
	        if (percentage < 0) {
	            percentage = 0;
	        }
	        $(this.cssSelector.jPlayer).jPlayer("playHead", percentage);
	        $('.jp-play-bar').css('width', percentage + '%');
	        //$(this.cssSelector.jPlayer).jPlayer.currentTime = maxduration * percentage / 100;
	    },
		setPlaylist: function(playlist) {
			this._initPlaylist(playlist);
			this._init();
		},
		add: function(media, playNow) {
			$(this.cssSelector.playlist + " ul").append(this._createListItem(media)).find("li:last-child").hide().slideDown(this.options.playlistOptions.addTime);
			this._updateControls();
			this.original.push(media);
			this.playlist.push(media); // Both array elements share the same object pointer. Comforms with _initPlaylist(p) system.

			if(playNow) {
				this.play(this.playlist.length - 1);
			} else {
				if(this.original.length === 1) {
					this.select(0);
				}
			}
		},
		remove: function(index) {
			var self = this;

			if(index === undefined) {
				this._initPlaylist([]);
				this._refresh(function() {
					$(self.cssSelector.jPlayer).jPlayer("clearMedia");
				});
				return true;
			} else {

				if(this.removing) {
					return false;
				} else {
					index = (index < 0) ? self.original.length + index : index; // Negative index relates to end of array.
					if(0 <= index && index < this.playlist.length) {
						this.removing = true;

						$(this.cssSelector.playlist + " li:nth-child(" + (index + 1) + ")").slideUp(this.options.playlistOptions.removeTime, function() {
							$(this).remove();

							if(self.shuffled) {
								var item = self.playlist[index];
								$.each(self.original, function(i) {
									if(self.original[i] === item) {
										self.original.splice(i, 1);
										return false; // Exit $.each
									}
								});
								self.playlist.splice(index, 1);
							} else {
								self.original.splice(index, 1);
								self.playlist.splice(index, 1);
							}

							if(self.original.length) {
								if(index === self.current) {
									self.current = (index < self.original.length) ? self.current : self.original.length - 1; // To cope when last element being selected when it was removed
									self.select(self.current);
								} else if(index < self.current) {
									self.current--;
								}
							} else {
								$(self.cssSelector.jPlayer).jPlayer("clearMedia");
								self.current = 0;
								self.shuffled = false;
								self._updateControls();
							}

							self.removing = false;
						});
					}
					return true;
				}
			}
		},
		select: function(index) {
			index = (index < 0) ? this.original.length + index : index; // Negative index relates to end of array.
			if(0 <= index && index < this.playlist.length) {
				this.current = index;
				this._highlight(index);
				$(this.cssSelector.jPlayer).jPlayer("setMedia", this.playlist[this.current]);
			} else {
				this.current = 0;
			}
		},
		play: function(index) {
			index = (index < 0) ? this.original.length + index : index; // Negative index relates to end of array.
			if(0 <= index && index < this.playlist.length) {
				if(this.playlist.length) {
					this.select(index);
					$(this.cssSelector.jPlayer).jPlayer("play");
				}
			} else if(index === undefined) {
				$(this.cssSelector.jPlayer).jPlayer("play");
			}
		},
		pause: function() {
			$(this.cssSelector.jPlayer).jPlayer("pause");
		},
		next: function() {
			var index = 0;
			if (this.shuffled) {
				var shuffleIndex = $.inArray(this.current, this.shufflelist);
				shuffleIndex = (shuffleIndex + 1 < this.playlist.length) ? shuffleIndex + 1 : 0;
				if (shuffleIndex === 0 && this.loop && this.playlist.length > 1) {
					this.shuffle(true, true);
				}
				index = this.shufflelist[shuffleIndex];
			} else {
				index = (this.current + 1 < this.playlist.length) ? this.current + 1 : 0;
			}
			
			if(this.loop) {
				// See if we need to shuffle before looping to start, and only shuffle if more than 1 item.
//				if(index === 0 && this.shuffled && this.options.playlistOptions.shuffleOnLoop && this.playlist.length > 1) {
//					this.shuffle(true, true); // playNow
//				} else {
					this.play(index);
//				}
			} else {
				// The index will be zero if it just looped round
				if (this.shuffled && index != this.shufflelist[0]) {
					this.play(index);
				} else if (!this.shuffled && index > 0) {
					this.play(index);
				}
			}
			this.loading = false;
		},
		previous: function() {
			var index = (this.current - 1 >= 0) ? this.current - 1 : this.playlist.length - 1;

			if(this.loop && this.options.playlistOptions.loopOnPrevious || index < this.playlist.length - 1) {
				if (1 < $(this.cssSelector.jPlayer).data("jPlayer").status.currentTime) {
					var self = this;
					setTimeout(function(){ 
						$(self.cssSelector.jPlayer).jPlayer("play", 0);
					}, 100);
				} else {
					this.play(index);
				}
			} else {
				if (1 < $(this.cssSelector.jPlayer).data("jPlayer").status.currentTime) {
					var self = this;
					setTimeout(function(){ 
						$(self.cssSelector.jPlayer).jPlayer("play", 0);
					}, 100);
				}
			}
			this.loading = false;
		},
		shuffle: function(shuffled, playNow) {
			var self = this;

			if (shuffled === undefined) {
				shuffled = !this.shuffled;
			}

			if(shuffled || shuffled !== this.shuffled) {
				this.shuffled = shuffled;
				this._updateControls();
				if (shuffled) {
					this.shufflelist.sort(function() {
						return 0.5 - Math.random();
					});
					if (-1 !== this.current) {
						var currentIndex = $.inArray(this.current, this.shufflelist);
						if (-1 !== currentIndex) {
							this.shufflelist.splice(currentIndex, 1);
							this.shufflelist.splice(0, 0, this.current);
						}
					}
				}
				this.setCookie('upm_shuffle_' + this.currentUserId,shuffled ? '1' : '0',1);
			}
			this.loading = false;
		},
		blur: function(that) {
			if($(this.cssSelector.jPlayer).jPlayer("option", "autoBlur")) {
				$(that).blur();
			}
		}
	};
})(jQuery);
