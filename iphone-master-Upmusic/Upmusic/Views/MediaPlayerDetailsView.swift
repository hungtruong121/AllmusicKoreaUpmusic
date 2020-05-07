//
//  MediaPlayerDetailsView.swift
//  Upmusic
//
//  Created by nough on 30/07/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import UIKit
import AVKit
import MediaPlayer
import MarqueeLabel

class MediaPlayerDetailsView: UIView {
    
    let cellId = "cellId"
    let cellId2 = "cellId2"
    
    var shuffleMode = ""
    var repeatMode = ""
    var peeked = ""
    
    static let upmusicUrl = NSURL(string: "https://upmusic.azurewebsites.net")
    let upmusicUrlNoStatic = "https://upmusic.azurewebsites.net"
    let upmusicUserAgent = "UPMusicIOS"
    
    // 음원 관련 (현재 음원)
    var track: MusicTrack! {
        didSet {
            titleLabel.text = track.subject
            authorLabel.text = track.artistNick
            miniTitleLabel.text = track.subject
            miniSubtitleLabel.text = track.artistNick
            
//            trackNameLabel.text = track.subject
//            if (track.subject?.count ?? 0 > 30) {
//                trackNameLabel.text = (track.subject?.substring(with: 0..<30) ?? "") + "..."
//            }
//            titleLabel.text = track.subject
//            if (track.subject?.count ?? 0 > 30) {
//                titleLabel.text = (track.subject?.substring(with: 0..<30) ?? "") + "..."
//            }
//
//            authorLabel.text = track.subject
//            if (track.subject?.count ?? 0 > 100) {
//                authorLabel.text = (track.subject?.substring(with: 0..<100) ?? "") + "..."
//            }
//
//            miniTitleLabel.text = track.subject
//            if (track.subject?.count ?? 0 > 30) {
//                miniTitleLabel.text = (track.subject?.substring(with: 0..<30) ?? "") + "..."
//            }
//
//            miniSubtitleLabel.text = track.subject
//            if (track.subject?.count ?? 0 > 100) {
//                miniSubtitleLabel.text = (track.subject?.substring(with: 0..<100) ?? "") + "..."
//            }
            
//           //  START OF MARQUEE
            titleLabel.numberOfLines = 1
            titleLabel.marqueeType = .MLContinuous
            
            titleLabel.textAlignment = .center
            titleLabel.scrollDuration = 15.0
            titleLabel.animationCurve = .curveEaseInOut
            titleLabel.fadeLength = 10.0
            titleLabel.leadingBuffer = 0
            titleLabel.trailingBuffer = 20.0

            // De-labelize on selection
            titleLabel.labelize = false /// 흐르는 효과 // true이면 작동 하지 않음
            
            //           //  START OF MARQUEE
            miniTitleLabel.numberOfLines = 1
            miniTitleLabel.textAlignment = .center
            miniTitleLabel.marqueeType = .MLContinuous
            miniTitleLabel.scrollDuration = 15.0
            miniTitleLabel.animationCurve = .curveEaseInOut
            miniTitleLabel.fadeLength = 10.0
            miniTitleLabel.leadingBuffer = 0
            miniTitleLabel.trailingBuffer = 20.0
            
            // De-labelize on selection
            miniTitleLabel.labelize = true /// 흐르는 효과 // true이면 작동 하지 않음
            
            
            // Labelize normally, to improve scroll performance
//            titleLabel.labelize = true
//           //  END OF MARQUEE
            
            HeartCntLabel.text = String(describing: track.heartCnt!)
            
            if(track.liked == true) {
//                playerLikeOff
                HeartButton.setImage(#imageLiteral(resourceName: "playerLikeOn"), for: .normal)
                HeartButton.setImage(UIImage(named: "playerLikeOn"), for: .normal)
            }
            
            if(track.liked == false) {
                HeartButton.setImage(#imageLiteral(resourceName: "tabLikeOff"), for: .normal)
                HeartButton.setImage(UIImage(named: "playerLikeOff"), for: .normal)
            }
            
            setupNowPlayingInfo()
            setupAudioSession()
//            initializeS3Cognito() // IF USE AMAZON S3
            UserDefaults.standard.set(track.url, forKey: "currentMusicUrl")
            
            // DEBUG
//            print("[Track Info] ::::::: all : \(track.toJSONString())")
            print("[Track Info] ::::::: self.peeked ::::::: : \(self.peeked)")
            
            // IF USE AMAZON S3 STORAGE SERVICE.
//            downloadTrackFromS3()   // With playTrack()
            
            // PLAY TRACK
             playTrack()
            
            // IF USE AZURE(MS) STORAGE SEVICE
            
            guard let url = URL(string:
                track.coverImageUrl?
                .toSecureHTTPSofDomainChange()
                .addingPercentEncoding(withAllowedCharacters: CharacterSet.urlQueryAllowed)
                ?? "") else { return }
            
            episodeImageView.sd_setImage(with: url)
            miniEpisodeImageView.sd_setImage(with: url)
            
            miniEpisodeImageView.sd_setImage(with: url) { (image, _, _, _) in
                let image = self.episodeImageView.image ?? UIImage()
                let artworkItem = MPMediaItemArtwork(boundsSize: .zero, requestHandler: { (size) -> UIImage in
                    return image
                })
                MPNowPlayingInfoCenter.default().nowPlayingInfo?[MPMediaItemPropertyArtwork] = artworkItem
            }
            NotificationCenter.default.addObserver(self, selector:#selector(playerDidFinishPlaying),name: NSNotification.Name.AVPlayerItemDidPlayToEndTime, object: player.currentItem)
            
        }
    }
    
    // 플레이가 완료되었을 때
    @objc func playerDidFinishPlaying(note: NSNotification){
        print("[Current Play-Item] Finished")
        
        // SOCKET // REWARD
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "onTimerEnd"), object: nil)
        UserDefaults.standard.set("false", forKey: "isPlaying")
        
        self.playPauseButton.setImage(#imageLiteral(resourceName: "playerPlay01"), for: .normal)
        self.miniPlayPauseButton.setImage(#imageLiteral(resourceName: "musicPlay01"), for: .normal)
        
        if isKeyPresentInUserDefaults(key: "shuffleMode") {
            shuffleMode = UserDefaults.standard.string(forKey: "shuffleMode")!
        } else {
            shuffleMode = "OFF"
            UserDefaults.standard.set(shuffleMode, forKey: "shuffleMode")
        }
        
        if isKeyPresentInUserDefaults(key: "repeatMode") {
            repeatMode = UserDefaults.standard.string(forKey: "repeatMode")!
        } else {
            repeatMode = "NOR"
            UserDefaults.standard.set(shuffleMode, forKey: "repeatMode")
        }
        
        
        if shuffleMode == "ON" {
            playlistTracks = shuffleArray(array: originPlaylist)
        } else {
            playlistTracks = originPlaylist
        }
        
        // repeatMode == "NOR" : 마지막 곡의 경우에 stop.
        // repeatMode == "ALL" : 마지막 곡의 경우에 첫곡으로
        if repeatMode == "ALL" {
            handleNextTrack()
        } else {
            // repeatMode == "NOR" : 마지막 곡의 경우에 stop.
            
            let currentIndex = playlistTracks.index { (tr) -> Bool in
                return self.track.subject == tr.subject && self.track.artistNick == tr.artistNick
            }
            
            guard let index = currentIndex else { return }
            
            let nextTrack: MusicTrack
            if index == playlistTracks.count - 1 {
                nextTrack = playlistTracks[0]
                //player
                //player.pause()
            } else {
                nextTrack = playlistTracks[index + 1]
                self.track = nextTrack
            }
            // [SET PLAY NEXT AUTO]
            //self.track = nextTrack
        }
        
    }
    
    
    // 현재 플레이 정보
    fileprivate func setupNowPlayingInfo() {
        var nowPlayingInfo = [String: Any]()
        
        nowPlayingInfo[MPMediaItemPropertyTitle] = track.subject
        nowPlayingInfo[MPMediaItemPropertyArtist] = track.artistNick
        MPNowPlayingInfoCenter.default().nowPlayingInfo = nowPlayingInfo
    }
    
    // Amazon S3
    fileprivate func playTrackWithS3() {
            print("[ Trying to play Track at url ] :", track.filenameUrl?.toGettingOnlyKeyName())
        
        let downloadingFileURL = URL(fileURLWithPath: NSTemporaryDirectory()).appendingPathComponent("nowplaying.mp3")
            
            guard let url = URL(string: downloadingFileURL.absoluteString ) else { return }
            let playerItem = AVPlayerItem(url: url)
            player.replaceCurrentItem(with: playerItem)
            player.play()
        
    }
    
    // 음원을 재생하는 함수.
    fileprivate func playTrack() {
        print(" > [ Trying to play Track at url ] :", track.filenameUrl)
        
//        let downloadingFileURL = URL(fileURLWithPath: NSTemporaryDirectory()).appendingPathComponent("nowplaying.mp3")
        guard let url = URL(string:
            track.filenameUrl!.addingPercentEncoding(withAllowedCharacters: NSCharacterSet.urlQueryAllowed)! ) else { return }
        let playerItem = AVPlayerItem(url: url)
        player.replaceCurrentItem(with: playerItem)
        player.play()
        
        UserDefaults.standard.set(track.id, forKey: "currentTrackId")
        
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "sendSocketPlayTime"), object: nil)
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "onTimerStart"), object: nil)
        UserDefaults.standard.set("true", forKey: "isPlaying")
        
//        if (self.peeked == "true") {
        
        let isFirstLoad = UserDefaults.standard.string(forKey: "isFirstLoad")
        print("[MediaPlayerDetailView][track] init [isFirstLoad] \(isFirstLoad)")
        
        if (isFirstLoad == "true") {
            self.player.pause()
            
            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "onTimerEnd"), object: nil)
            UserDefaults.standard.set("false", forKey: "isPlaying")
            
            self.playPauseButton.setImage(#imageLiteral(resourceName: "playerPlay01"), for: .normal)
            self.miniPlayPauseButton.setImage(#imageLiteral(resourceName: "musicPlay01"), for: .normal)
            self.peeked = "false"
            UserDefaults.standard.set("false", forKey: "isFirstLoad")
        
            print(" > [Track Info] ::::::: self.peeked ::::::: : -> \(self.peeked)")
//        }
        }
    }
    
    
    
    // 애져 (AZURE) 다운로드 관련 // 사용하지 않음.
    func downloadTrackFromAzure() {
        // NO SPECIAL SETTING.
    }
     
    // playTrackUsingFileUrl
    fileprivate func playTrackUsingFileUrl() {
        print("Attempt to play Track with file url:",  track.filenameUrl ?? "")
        
        // let's figure out the file name for our episode file url
        guard let fileURL = URL(string:  track.filenameUrl ?? "") else { return }
        let fileName = fileURL.lastPathComponent
        
        guard var trueLocation = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first else { return }
        
        trueLocation.appendPathComponent(fileName)
        print("True Location of episode:", trueLocation.absoluteString)
        let playerItem = AVPlayerItem(url: trueLocation)
        player.replaceCurrentItem(with: playerItem)
        player.play()
    }
    
    // 플레이어 설정.
    let player: AVPlayer = {
        let avPlayer = AVPlayer()
        avPlayer.automaticallyWaitsToMinimizeStalling = false
        return avPlayer
    }()
    
    
    
    // 시간 표시 관련 처리
    fileprivate func observePlayerCurrentTime() {
        let interval = CMTimeMake(1, 2)
        player.addPeriodicTimeObserver(forInterval: interval, queue: .main) { [weak self] (time) in
            self?.currentTimeLabel.text = time.toDisplayString()
            let durationTime = self?.player.currentItem?.duration
            self?.durationLabel.text = durationTime?.toDisplayString()
            
            self?.updateCurrentTimeSlider()
        }
    }
    
    // 슬라이더의 시간 업데이트
    fileprivate func updateCurrentTimeSlider() {
        let currentTimeSeconds = CMTimeGetSeconds(player.currentTime())
        let durationSeconds = CMTimeGetSeconds(player.currentItem?.duration ?? CMTimeMake(1, 1))
        let percentage = currentTimeSeconds / durationSeconds
        
        self.currentTimeSlider.value = Float(percentage)
    }
    
    var panGesture: UIPanGestureRecognizer!
    
    @objc fileprivate func setupGestures() {
        addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(handleTapMaximize)))
        panGesture = UIPanGestureRecognizer(target: self, action: #selector(handlePan))
        miniPlayerView.addGestureRecognizer(panGesture)
        
        maximizedStackView.addGestureRecognizer(UIPanGestureRecognizer(target: self, action: #selector(handleDismissalPan)))
    }
    
    @objc fileprivate func removeGestures() {
        addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(handleTapMaximize)))
        panGesture = UIPanGestureRecognizer(target: self, action: #selector(handlePan))
        miniPlayerView.addGestureRecognizer(panGesture)
        maximizedStackView.addGestureRecognizer(UIPanGestureRecognizer(target: self, action: #selector(handleDismissalPan)))
        
        removeGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(handleTapMaximize)))
        miniPlayerView.removeGestureRecognizer(panGesture)
    }
    
    @objc func handleDismissalPan(gesture: UIPanGestureRecognizer) {
        print("maximizedStackView dismissal")
        
        if gesture.state == .changed {
            print("maximizedStackView dismissal 1-1")
            
//            let translation = gesture.translation(in: superview)
//            maximizedStackView.transform = CGAffineTransform(translationX: 0, y: translation.y)
            
        } else if gesture.state == .ended {
            print("maximizedStackView dismissal 1-2")
            
            let translation = gesture.translation(in: superview)
            UIView.animate(withDuration: 0.5, delay: 0, usingSpringWithDamping: 0.7, initialSpringVelocity: 1, options: .curveEaseOut, animations: {
                
                print("maximizedStackView dismissal 1-3")
                self.maximizedStackView.transform = .identity
                
                if translation.y > 50 {
                    
                    print("maximizedStackView dismissal 1-4")
                    let mainTabBarController = UIApplication.shared.keyWindow?.rootViewController as? MainController
                    mainTabBarController?.minimizePlayerDetails()
                }
                
            })
        }
    }
    
    fileprivate func setupAudioSession() {
        do {
            try AVAudioSession.sharedInstance().setCategory(AVAudioSessionCategoryPlayback)
            try AVAudioSession.sharedInstance().setActive(true)
        } catch let sessionErr {
            print("Failed to activate session:", sessionErr)
        }
    }
    
    fileprivate func setupRemoteControl() {
        UIApplication.shared.beginReceivingRemoteControlEvents()
        
        let commandCenter = MPRemoteCommandCenter.shared()
        
        commandCenter.playCommand.isEnabled = true
        commandCenter.playCommand.addTarget { (_) -> MPRemoteCommandHandlerStatus in
            self.player.play()
            self.playPauseButton.setImage(#imageLiteral(resourceName: "playerPlay04"), for: .normal)
            self.miniPlayPauseButton.setImage(#imageLiteral(resourceName: "musicPlay04"), for: .normal)
            self.setupElapsedTime(playbackRate: 1)
            
            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "onTimerStart"), object: nil)
            UserDefaults.standard.set("true", forKey: "isPlaying")
            return .success
        }
        
        commandCenter.pauseCommand.isEnabled = true
        commandCenter.pauseCommand.addTarget { (_) -> MPRemoteCommandHandlerStatus in
            self.player.pause()
            self.playPauseButton.setImage(#imageLiteral(resourceName: "playerPlay01"), for: .normal)
            self.miniPlayPauseButton.setImage(#imageLiteral(resourceName: "musicPlay01"), for: .normal)
            self.setupElapsedTime(playbackRate: 0)
            
            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "onTimerEnd"), object: nil)
            UserDefaults.standard.set("false", forKey: "isPlaying")
            
            return .success
        }
        
        commandCenter.togglePlayPauseCommand.isEnabled = true
        commandCenter.togglePlayPauseCommand.addTarget { (_) -> MPRemoteCommandHandlerStatus in
            self.handlePlayPause()
            return .success
        }
        
        commandCenter.nextTrackCommand.addTarget(self, action: #selector(handleNextTrack))
        commandCenter.previousTrackCommand.addTarget(self, action: #selector(handlePrevTrack))
    }
    
    func shuffleArray(array: [MusicTrack]) -> [MusicTrack] {
        
        
        var tempArray = array
        for index in 0...array.count - 1 {
            
            let randomNumber = arc4random_uniform(UInt32(array.count - 1))
            let randomIndex = Int(randomNumber)
            tempArray[randomIndex] = array[index]
        }
        
        print("[FOR shuffleArray] : \(String(describing: tempArray[0].subject)), array : \(String(describing: array[0].subject))")
        
        return tempArray
    }
    
    var playlistTracks = [MusicTrack]()
    var playlistTrackSelected : [Bool] = []
    
    var originPlaylist = [MusicTrack]() {
        didSet {
            playlistTracks = originPlaylist
            shuffledPlaylist = shuffleArray(array: originPlaylist)
        }
    }
    
    // 셔플되는 재생목록.
    var shuffledPlaylist = [MusicTrack]()
    
    // 이전 트랙 관련 처리
    @objc fileprivate func handlePrevTrack() {
        // 1. check if playlistTracks == 0 then return
        // 2. find out current track index
        // 3. if track index is 0, wrap to end of list somehow..
        // otherwise play episode index - 1
        if (self.track == nil) {
            print("> [self.track == nil]")
            return
        }
        
        if playlistTracks.isEmpty {
            return
        }
        
        if isKeyPresentInUserDefaults(key: "shuffleMode") {
            shuffleMode = UserDefaults.standard.string(forKey: "shuffleMode")!
        } else {
            shuffleMode = "OFF"
            UserDefaults.standard.set(shuffleMode, forKey: "shuffleMode")
        }
        
        if shuffleMode == "ON" {
            playlistTracks = shuffleArray(array: originPlaylist)
        } else {
            playlistTracks = originPlaylist
        }
        
        let currentIndex = playlistTracks.index { (tr) -> Bool in
            return self.track.subject == tr.subject && self.track.artistNick == tr.artistNick
        }
        
        guard let index = currentIndex else { return }
        let prevTrack: MusicTrack
        if index == 0 {
            let count = playlistTracks.count
            prevTrack = playlistTracks[count - 1]
        } else {
            prevTrack = playlistTracks[index - 1]
        }
        self.track = prevTrack
    }
    
    // 다음 트랙 재생 관련 처리
    @objc fileprivate func handleNextTrack() {
        
        if (self.track == nil) {
            print("> [self.track == nil]")
            return
        }
        
        if playlistTracks.count == 0 {
            return
        }
        
        if isKeyPresentInUserDefaults(key: "shuffleMode") {
            shuffleMode = UserDefaults.standard.string(forKey: "shuffleMode")!
        } else {
            shuffleMode = "OFF"
            UserDefaults.standard.set(shuffleMode, forKey: "shuffleMode")
        }
        
        if shuffleMode == "ON" {
            playlistTracks = shuffleArray(array: originPlaylist)
        } else {
            playlistTracks = originPlaylist
        }
        
        let currentIndex = playlistTracks.index { (tr) -> Bool in
            return self.track.subject == tr.subject && self.track.artistNick == tr.artistNick
        }
        
        guard let index = currentIndex else { return }
        
        let nextTrack: MusicTrack
        if index == playlistTracks.count - 1 {
            nextTrack = playlistTracks[0]
        } else {
            nextTrack = playlistTracks[index + 1]
        }
        
        self.track = nextTrack
    }
    
    
    fileprivate func setupElapsedTime(playbackRate: Float) {
        let elapsedTime = CMTimeGetSeconds(player.currentTime())
        MPNowPlayingInfoCenter.default().nowPlayingInfo?[MPNowPlayingInfoPropertyElapsedPlaybackTime] = elapsedTime
        MPNowPlayingInfoCenter.default().nowPlayingInfo?[MPNowPlayingInfoPropertyPlaybackRate] = playbackRate
    }
    
    fileprivate func observeBoundaryTime() {
        let time = CMTimeMake(1, 3)
        let times = [NSValue(time: time)]
        
        // player has a reference to self
        // self has a reference to player
        player.addBoundaryTimeObserver(forTimes: times, queue: .main) { [weak self] in
            print("Episode started playing")
            self?.enlargeEpisodeImageView()
            self?.setupLockscreenDuration()
        }
    }
    
    // 잠금화면에서의 플레이어 시간 표시
    fileprivate func setupLockscreenDuration() {
        guard let duration = player.currentItem?.duration else { return }
        let durationSeconds = CMTimeGetSeconds(duration)
        MPNowPlayingInfoCenter.default().nowPlayingInfo?[MPMediaItemPropertyPlaybackDuration] = durationSeconds
    }
    
    fileprivate func setupInterruptionObserver() {
        // don't forget to remove self on deinit
        NotificationCenter.default.addObserver(self, selector: #selector(handleInterruption), name: .AVAudioSessionInterruption, object: nil)
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self, name: .AVAudioSessionInterruption, object: nil)
    }
    
    @objc fileprivate func handleInterruption(notification: Notification) {
        guard let userInfo = notification.userInfo else { return }
        
        guard let type = userInfo[AVAudioSessionInterruptionTypeKey] as? UInt else { return }
        
        if type == AVAudioSessionInterruptionType.began.rawValue {
            print("Interruption began")
            
            playPauseButton.setImage(#imageLiteral(resourceName: "playerPlay01"), for: .normal)
            miniPlayPauseButton.setImage(#imageLiteral(resourceName: "musicPlay01"), for: .normal)
            
        } else {
            print("Interruption ended...")
            
            guard let options = userInfo[AVAudioSessionInterruptionOptionKey] as? UInt else { return }
            
            if options == AVAudioSessionInterruptionOptions.shouldResume.rawValue {
                player.play()
                self.playPauseButton.setImage(#imageLiteral(resourceName: "playerPlay04"), for: .normal)
                self.miniPlayPauseButton.setImage(#imageLiteral(resourceName: "musicPlay04"), for: .normal)
            }
            
            
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
        setupRemoteControl()
        setupGestures()
        setupInterruptionObserver()
        observePlayerCurrentTime()
        observeBoundaryTime()
        
        
        NotificationCenter.default.addObserver(self
            , selector: #selector(setupGestures)
            , name: NSNotification.Name(rawValue: "setupGestures")
            , object: nil)
        
        NotificationCenter.default.addObserver(self
            , selector: #selector(removeGestures)
            , name: NSNotification.Name(rawValue: "removeGestures")
            , object: nil)
        
        
    }
    
    static func initFromNib() -> MediaPlayerDetailsView {
        return Bundle.main.loadNibNamed("MediaPlayerDetailsView", owner: self, options: nil)?.first as! MediaPlayerDetailsView
    }
    
    //MARK:- IB Actions and Outlets
    
    @IBOutlet weak var miniEpisodeImageView: UIImageView!
    @IBOutlet weak var miniTitleLabel: MarqueeLabel!
    @IBOutlet weak var miniSubtitleLabel: UILabel!
    
    @IBOutlet weak var miniPlayPauseButton: UIButton! {
        didSet {
            if self.track == nil {
//                self.playPauseButton.setImage(#imageLiteral(resourceName: "playerPlay01"), for: .normal)
                self.miniPlayPauseButton.setImage(#imageLiteral(resourceName: "musicPlay01"), for: .normal)
            }
            
            miniPlayPauseButton.addTarget(self, action: #selector(handlePlayPause), for: .touchUpInside)
            //            miniPlayPauseButton.imageEdgeInsets = .init(top: 8, left: 8, bottom: 8, right: 8)
        }
    }
    
    @IBOutlet weak var miniFastForwardButton: UIButton! {
        didSet {
            miniFastForwardButton.addTarget(self, action: #selector(handleNextTrack), for: .touchUpInside)
            //            miniFastForwardButton.imageEdgeInsets = .init(top: 8, left: 8, bottom: 8, right: 8)
        }
    }
    
    @IBOutlet weak var miniPreviousButton: UIButton! {
        
        didSet {
            miniPreviousButton.addTarget(self, action: #selector(handlePrevTrack), for: .touchUpInside)
            //            miniFastForwardButton.imageEdgeInsets = .init(top: 8, left: 8, bottom: 8, right: 8)
        }
        
    }
    
    @IBOutlet weak var miniPlayerView: UIView!
    @IBOutlet weak var maximizedStackView: UIStackView!
    @IBOutlet weak var maximaizedCloseButton: UIButton!
    @IBOutlet weak var maximizedRewardLabel: UILabel!
    
    @IBOutlet weak var maximizedRewardButton: UIButton!
    
    @IBAction func handleCurrentTimeSliderChange(_ sender: Any) {
        let percentage = currentTimeSlider.value
        guard let duration = player.currentItem?.duration else { return }
        let durationInSeconds = CMTimeGetSeconds(duration)
        let seekTimeInSeconds = Float64(percentage) * durationInSeconds
        let seekTime = CMTimeMakeWithSeconds(seekTimeInSeconds, 1)
        
        MPNowPlayingInfoCenter.default().nowPlayingInfo?[MPNowPlayingInfoPropertyElapsedPlaybackTime] = seekTimeInSeconds
        
        player.seek(to: seekTime)
    }
//    
//    @IBAction func handleFastForward(_ sender: Any) {
//        seekToCurrentTime(delta: 15)
//    }
//    
//    @IBAction func handleRewind(_ sender: Any) {
//        seekToCurrentTime(delta: -15)
//    }
//    
    fileprivate func seekToCurrentTime(delta: Int64) {
        let fifteenSeconds = CMTimeMake(delta, 1)
        let seekTime = CMTimeAdd(player.currentTime(), fifteenSeconds)
        player.seek(to: seekTime)
    }
    
    @IBAction func handleVolumeChange(_ sender: UISlider) {
        player.volume = sender.value
    }
    
    @IBOutlet weak var currentTimeSlider: UISlider!
    @IBOutlet weak var durationLabel: UILabel!
    @IBOutlet weak var currentTimeLabel: UILabel!
    
    @IBAction func handleDismiss(_ sender: Any) {
        let mainController =  UIApplication.shared.keyWindow?.rootViewController as? MainController
        mainController?.minimizePlayerDetails()
    }
    
    fileprivate func enlargeEpisodeImageView() {
        UIView.animate(withDuration: 0.75, delay: 0, usingSpringWithDamping: 0.5, initialSpringVelocity: 1, options: .curveEaseOut, animations: {
            self.playPauseButton.setImage(#imageLiteral(resourceName: "playerPlay04"), for: .normal)
            self.miniPlayPauseButton.setImage(#imageLiteral(resourceName: "musicPlay04"), for: .normal)
            self.episodeImageView.transform = .identity
        })
    }
    
    fileprivate let shrunkenTransform = CGAffineTransform(scaleX: 0.7, y: 0.7)
    
    fileprivate func shrinkEpisodeImageView() {
        UIView.animate(withDuration: 0.75, delay: 0, usingSpringWithDamping: 0.5, initialSpringVelocity: 1, options: .curveEaseOut, animations: {
            self.episodeImageView.transform = self.shrunkenTransform
        })
    }
    
    @IBOutlet weak var episodeImageView: UIImageView! {
        didSet {
            episodeImageView.layer.cornerRadius = 0
            episodeImageView.clipsToBounds = true
            episodeImageView.transform = shrunkenTransform
        }
    }
//
//    @IBOutlet weak var bottomImageView: UIImageView! {
//        didSet {
//            bottomImageView.layer.cornerRadius = 0
//            bottomImageView.clipsToBounds = true
//            bottomImageView.transform = shrunkenTransform
//        }
//    }
//
    
    @IBOutlet weak var HeartCntLabel: UILabel!
    
    @IBOutlet weak var HeartButton: UIButton!
    @IBAction func handleHeart(_ sender: Any) {
        
        print("[handleHeart]")
        
    }
    
    @IBOutlet weak var MoreButton: UIButton!
    @IBAction func handleMore(_ sender: Any) {
        print("[handleMore]")
        
    }
    
    @IBOutlet weak var RepeatButton: UIButton! {
        didSet {
            
            if isKeyPresentInUserDefaults(key: "repeatMode") {
                repeatMode = UserDefaults.standard.string(forKey: "repeatMode")!
            } else {
                repeatMode = "NOR"
                UserDefaults.standard.set(shuffleMode, forKey: "repeatMode")
            }
            
            print("[ RepeatButton ] repeatMode : \(repeatMode)")
            if repeatMode == "ALL" {
                RepeatButton.setImage(#imageLiteral(resourceName: "playerRotationOn"), for: .normal)
            } else {
                RepeatButton.setImage(#imageLiteral(resourceName: "playerRotation"), for: .normal)
            }
            
            RepeatButton.addTarget(self, action: #selector(handleRepeat), for: .touchUpInside)
        }
    }
    @IBOutlet weak var ShuffleButton: UIButton! {
        didSet {
            if isKeyPresentInUserDefaults(key: "shuffleMode") {
                shuffleMode = UserDefaults.standard.string(forKey: "shuffleMode")!
            } else {
                shuffleMode = "OFF"
                UserDefaults.standard.set(shuffleMode, forKey: "shuffleMode")
            }
            
            print("[ ShuffleButton ] shuffleMode : \(shuffleMode)")
            if shuffleMode == "ON" {
                ShuffleButton.setImage(#imageLiteral(resourceName: "playerRandom-1"), for: .normal)
            } else {
                ShuffleButton.setImage(#imageLiteral(resourceName: "playerRandomOff"), for: .normal)
            }
            
//            ShuffleButton.setImage(#imageLiteral(resourceName: "playerRandomOff"), for: .normal)
//            ShuffleButton.setImage(UIImage(named:"playerRandom"), for: .normal)
            ShuffleButton.addTarget(self, action: #selector(handleShuffle), for: .touchUpInside)
        }
    }
    
    @IBOutlet weak var FastForwardButton: UIButton! {
        didSet {
            FastForwardButton.setImage(#imageLiteral(resourceName: "playerPlay03"), for: .normal)
            FastForwardButton.addTarget(self, action: #selector(handleNextTrack), for: .touchUpInside)
            
//            miniFastForwardButton.addTarget(self, action: #selector(handleFastForward(_:)), for: .touchUpInside)
        }
    }
    
    @IBOutlet weak var PreviousButton: UIButton! {
        didSet {
            PreviousButton.setImage(#imageLiteral(resourceName: "playerPlay02"), for: .normal)
            PreviousButton.addTarget(self, action: #selector(handlePrevTrack), for: .touchUpInside)
            
//            miniFastForwardButton.addTarget(self, action: #selector(handleFastForward(_:)), for: .touchUpInside)
        }
    }
    
    @IBOutlet weak var playPauseButton: UIButton! {
        didSet {
            if self.track == nil {
                    self.playPauseButton.setImage(#imageLiteral(resourceName: "playerPlay01"), for: .normal)
//                                self.miniPlayPauseButton.setImage(#imageLiteral(resourceName: "musicPlay01"), for: .normal)
            }
            
            playPauseButton.setImage(#imageLiteral(resourceName: "playerPlay04"), for: .normal)
            playPauseButton.addTarget(self, action: #selector(handlePlayPause), for: .touchUpInside)
        }
    }
    
    @objc func handleRepeat() {
        print("[ handleRepeat ] BEFORE repeatMode : \(repeatMode)")
        //
        if isKeyPresentInUserDefaults(key: "repeatMode") {
            repeatMode = UserDefaults.standard.string(forKey: "repeatMode")!
        } else {
            repeatMode = "NOR"
            UserDefaults.standard.set(shuffleMode, forKey: "repeatMode")
        }
        
//        if RepeatButton.currentImage == #imageLiteral(resourceName: "playerRotation") {
        if repeatMode == "ALL" {
            RepeatButton.setImage(#imageLiteral(resourceName: "playerRotation"), for: .normal)
            repeatMode = "NOR"
            UserDefaults.standard.set(repeatMode, forKey: "repeatMode")
        } else {
            RepeatButton.setImage(#imageLiteral(resourceName: "playerRotationOn"), for: .normal)
            repeatMode = "ALL"
            UserDefaults.standard.set(repeatMode, forKey: "repeatMode")
        }
        
        print("[ handleRepeat ] AFTER repeatMode : \(repeatMode)")
        
        
        
    }
    
    
    
    @objc func handleShuffle() {
        print("[ handleShuffle ] BEFORE shuffleMode : \(shuffleMode)")
//
        if isKeyPresentInUserDefaults(key: "shuffleMode") {
            shuffleMode = UserDefaults.standard.string(forKey: "shuffleMode")!
        } else {
            shuffleMode = "OFF"
            UserDefaults.standard.set(shuffleMode, forKey: "shuffleMode")
        }
        
        //if ShuffleButton.currentImage == #imageLiteral(resourceName: "playerRandomOff") {
        if shuffleMode == "ON" {
            ShuffleButton.setImage(#imageLiteral(resourceName: "playerRandomOff"), for: .normal)
            shuffleMode = "OFF"
            UserDefaults.standard.set(shuffleMode, forKey: "shuffleMode")
        } else {
            ShuffleButton.setImage(#imageLiteral(resourceName: "playerRandom"), for: .normal)
            shuffleMode = "ON"
            UserDefaults.standard.set(shuffleMode, forKey: "shuffleMode")
        }
        print("[ handleShuffle ] AFTER shuffleMode : \(shuffleMode)")
        
        
        if shuffleMode == "ON" {
            shuffledPlaylist = shuffleArray(array: originPlaylist)
            playlistTracks = shuffledPlaylist
        } else {
            playlistTracks = originPlaylist
        }
        
    }
    
    
    @objc func handlePrevious() {
        print("[ handlePrevious ]")
        if (self.track == nil) {
            print("> [self.track == nil]")
            return
        }
        handlePrevTrack()
    }
    
    @objc func handleForward() {
        if (self.track == nil) {
            print("> [self.track == nil]")
            return
        }
        print("[ handleForward ]")
        handleNextTrack()
    }

    @objc func handlePlayPause() {
        print("[ handlePlayPause ] top")
        if (self.track == nil) {
            print("> [self.track == nil]")
            return
        }
        
        print("[ handlePlayPause ] mid")
        if player.timeControlStatus == .paused {
            player.play()
            
            playPauseButton.setImage(#imageLiteral(resourceName: "playerPlay04"), for: .normal)
            miniPlayPauseButton.setImage(#imageLiteral(resourceName: "musicPlay04"), for: .normal)
            enlargeEpisodeImageView()
            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "onTimerStart"), object: nil)
            UserDefaults.standard.set("true", forKey: "isPlaying")
            self.setupElapsedTime(playbackRate: 1)
        } else {
            player.pause()
            
            playPauseButton.setImage(#imageLiteral(resourceName: "playerPlay01"), for: .normal)
            miniPlayPauseButton.setImage(#imageLiteral(resourceName: "musicPlay01"), for: .normal)
            shrinkEpisodeImageView()
            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "onTimerEnd"), object: nil)
            UserDefaults.standard.set("false", forKey: "isPlaying")
            self.setupElapsedTime(playbackRate: 0)
        }
        
        print("[ handlePlayPause ] btm")
    }
    
    @IBOutlet weak var authorLabel: UILabel!
    @IBOutlet weak var titleLabel: MarqueeLabel! {
        didSet {
//            titleLabel = MarqueeLabel.init(frame: <#T##CGRect#>, duration: <#T##TimeInterval#>, andFadeLength: <#T##CGFloat#>)
            
            titleLabel.numberOfLines = 1
            titleLabel.marqueeType = .MLContinuous
            titleLabel.scrollDuration = 5.0
            titleLabel.animationCurve = .curveEaseInOut
            titleLabel.fadeLength = 10.0
            titleLabel.leadingBuffer = 14.0
            
            // Labelize normally, to improve scroll performance
            titleLabel.labelize = true
            
            
        }
    }
    @IBOutlet weak var openPlaylistButton: UIButton!
    
    
    @IBOutlet weak var hidePlaylistButton: UIButton!
    
    @IBOutlet weak var bottomMenu: UIView! {
        didSet {
            bottomMenu.isHidden = true
        }
    }
    
    @IBOutlet weak var dialogListContainer: UIView! {
        didSet {
            dialogListContainer.isHidden = true
        }
    }
    
    @IBOutlet weak var dialogListView: UIView!
    
    @IBOutlet weak var dialogListViewHeader: UIView!
    
    @IBOutlet weak var dialogListViewHeaderCloseButton: UIButton!
    
    @IBAction func dialogListViewHeaderCloseButtonClicked (_ sender: Any) {
        
        NSLog("[dialogListViewHeaderCloseButtonClicked] ")
        dialogListContainer.isHidden = true
    }
    
    @IBOutlet weak var dialogListTableView: UITableView! {
        didSet {
            let nib = UINib(nibName: "StringCell", bundle: nil)
            dialogListTableView.register(nib, forCellReuseIdentifier: cellId2)
        }
    }
    
    @IBOutlet weak var dialogListViewBottomButton: UIButton!
    
    @IBAction func dialogListViewBottomButtonClicked (_ sender: Any) {
        NSLog("[dialogListViewBottomButtonClicked] ")
        subDialogContainer.isHidden = false
    }
    
    @IBOutlet weak var subDialogContainer: UIView! {
        didSet {
            subDialogContainer.isHidden = true
        }
    }
    
    @IBOutlet weak var subDialogView: UIView!
    
    @IBOutlet weak var subDialogViewCloseButton: UIButton!
    
    @IBAction func subDialogViewCloseButtonClicked (_ sender: Any) {
        
        NSLog("[subDialogViewCloseButtonClicked] ")
        subDialogContainer.isHidden = true
    }
    
    @IBOutlet weak var subDialogTextField: UITextField!
    
    @IBAction func subDialogTextFieldClicked (_ sender: Any) {
    
        NSLog("[subDialogTextFieldClicked] ")
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?){
        self.subDialogTextField.endEditing(true)
    }
    
    @IBOutlet weak var subDialogBottomButton: UIButton!
    
    @IBAction func subDialogBottomButtonClicked(_ sender: Any) {
        NSLog("[subDialogBottomButtonClicked] ")
    
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "subDialogClicked"), object: nil)
    }
    
    
    @IBOutlet weak var DialogConfirmViewContainer: UIView! {
        didSet {
            DialogConfirmViewContainer.isHidden = true
        }
    }
    
    @IBOutlet weak var dialogConfirmView: UIView!
    
    @IBOutlet weak var dialogConfirmViewCloseButton: UIButton!
    
    @IBAction func dialogConfirmClosedButtonClicked(_ sender: Any) {
        DialogConfirmViewContainer.isHidden = true
    }
    
    @IBOutlet weak var dialogConfirmViewContextLabel: UILabel!
    
    @IBOutlet weak var dialogConfirmViewLeftButton: UIButton!
    
    @IBAction func dialogConfirmViewLeftButtonClicked(_ sender: Any) {
        
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "bottomMenuDelete"), object: nil)
        DialogConfirmViewContainer.isHidden = true
        
    }
    
    @IBOutlet weak var dialogConfirmViewRightButton: UIButton!
    
    @IBAction func dialogConfirmViewRightButtonClicked(_ sender: Any) {
        
        DialogConfirmViewContainer.isHidden = true
        
    }
    
    @IBOutlet weak var BottomMoreDialogViewContrainer: UIView!{
        didSet {
            BottomMoreDialogViewContrainer.isHidden = true
        }
    }
    
    
    @IBOutlet weak var BottomMoreDialogView: UIView!
    
    @IBOutlet weak var BottomMoreDialogRow0: UIView!
    
    
    @IBOutlet weak var BottomMoreDialogRow0_Label: UILabel!
    
    @IBOutlet weak var BottomMoreDialogRow0_Button: UIButton!
    
    @IBAction func BottomMoreDialogRow0_ButtonClicked(_ sender: Any) {
        BottomMoreDialogViewContrainer.isHidden = true
    }
    
    @IBOutlet weak var BottomMoreDialogRow1: UIView!
    
    @IBOutlet weak var BottomMoreDialogRow2: UIView!
    
    @IBOutlet weak var BottomMoreDialogRow3: UIView!
    
    @IBOutlet weak var BottomMoreDialogRow4: UIView!
    
    @IBOutlet weak var BottomMoreDialogRow5: UIView!
    
    @IBOutlet weak var BottomMoreDialogRow4_Image: UIImageView!
    
    @IBOutlet weak var BottomMoreDialogRow4_Label: UILabel!
    
    
    
    
    @IBOutlet weak var bottomTabDownButton: UIButton!
    
    @IBOutlet weak var bottomTabDeleteButton: UIButton!

    @IBOutlet weak var bottomTabDownText: UILabel!
    
    @IBOutlet weak var bottomTabDeleteText: UILabel!
    
    @IBAction func tabDownButtonClicked(_ sender: Any) {
        
        NSLog("[tabDownButtonClicked]")
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "bottomMenuDown"), object: nil)
        
    }
    
    
    @IBAction func tabDeleteButtonClicked(_ sender: Any) {
        
        NSLog("[tabDeleteButtonClicked]")
        
        DialogConfirmViewContainer.isHidden = false
        
        
    }
    
    
    @IBAction func hidePlaylist(_ sender: Any) {
//        playlistView.isHidden = true
//        playlistView.alpha = 1
        playlistHide()
    }
    
    @IBOutlet weak var EditCancelButton: UIButton!
    @IBOutlet weak var EditButton: UIButton!
    
    @IBAction func editPlaylist(_ sender: Any) {
        //        playlistView.isHidden = true
        //        playlistView.alpha = 1
        print("[editPlaylist]")
    }
    
    @IBAction func EditCancel(_ sender: Any) {
        
        print("[EditCancel]")
    }
    
    @IBAction func showPlaylist(_ sender: Any) {
//        playlistView.isHidden = false
//        playlistView.alpha = 0
        playlistShow()
        
    }
    
    @IBOutlet weak var playlistView: UIView! {
        didSet {
//            playlistView.isHidden = true
//            playlistView.alpha = 0
            playlistHide()
        }
    }
    
    @IBOutlet weak var miniPlaylistButton: UIButton!
    
    
    func playlistShow() {
        print("[playlistShow]")
//        let mainController =  UIApplication.shared.keyWindow?.rootViewController as? MainController
//        mainController?.mediaPlayerDetailsView.playlistTableView.dataSource = WebHandler() as UITableViewDataSource
//        mainController?.mediaPlayerDetailsView.playlistTableView.delegate = WebHandler() as UITableViewDelegate
//        mainController?.mediaPlayerDetailsView.playlistTableView.reloadData()
        playlistView.isHidden = false
    }
    
    func playlistHide() {
        print("[playlistHide]")
        
//        let mainController =  UIApplication.shared.keyWindow?.rootViewController as? MainController
//        mainController?.mediaPlayerDetailsView.playlistTableView.dataSource = WebHandler() as UITableViewDataSource
//        mainController?.mediaPlayerDetailsView.playlistTableView.delegate = WebHandler() as UITableViewDelegate
//        mainController?.mediaPlayerDetailsView.playlistTableView.reloadData()
        
        playlistView.isHidden = true
    }
    
    @IBOutlet weak var playlistTableView: UITableView! {
        didSet {
//            print("[playlistTableView] didSet")
            let nib = UINib(nibName: "MusicTrackCell", bundle: nil)
            playlistTableView.register(nib, forCellReuseIdentifier: cellId)
        }
    }
    
    func isKeyPresentInUserDefaults(key: String) -> Bool {
        return UserDefaults.standard.object(forKey: key) != nil
    }
    
}
