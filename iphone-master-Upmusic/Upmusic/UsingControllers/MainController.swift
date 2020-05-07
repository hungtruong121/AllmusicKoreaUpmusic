
//
//  MainController.swift
//  Upmusic
//
//  Created by nough on 30/07/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireObjectMapper
import FBSDKCoreKit
import FBSDKLoginKit
import SwiftyJSON

class MainController: UITabBarController {
    
    fileprivate let cellId = "cellId"
    var musicTracks : [MusicTrack] = []
    var musicTrackSelected : [Bool] = []
    
    var tempTracks : [MusicTrack] = []
    var shuffledTracks : [MusicTrack] = []
    var peeked = "true"
    var isPlaylistAndPlayerAtTheSameTime = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //        UINavigationBar.appearance().prefersLargeTitles = true
        
        setupViewControllers()
        setupPlayerDetailsView()
        addNavBarImage()
        
        self.tabBar.isHidden = true
    }
    
    func addNavBarImage() {
        
        let navController = UINavigationBar()
        
        let image = #imageLiteral(resourceName: "logo")
        let imageView = UIImageView(image: image)
        
        let bannerWidth = UINavigationBar().frame.size.width
        let bannerHeight = UINavigationBar().frame.size.height
        
        let bannerX = bannerWidth / 2 - image.size.width / 2
        let bannerY = bannerHeight / 2 - image.size.height / 2
        
        imageView.frame = CGRect(x: bannerX, y: bannerY, width: bannerWidth, height: bannerHeight)
        imageView.contentMode = .scaleAspectFit
        
        
        let navigationBarAppearace = UINavigationBar.appearance()
        navigationBarAppearace.tintColor = UIColor.white
        navigationBarAppearace.titleTextAttributes = [NSAttributedStringKey(rawValue: NSAttributedStringKey.foregroundColor.rawValue): UIColor.white]
        
        navigationItem.titleView = imageView
    }
    
    
    func peekPlayerDetails(track: MusicTrack?, playlistTracks: [MusicTrack] = [], noPlay : Bool) {
        
        maximizedTopAnchorConstraint.isActive = false
        bottomAnchorConstraint.constant = view.frame.height
        minimizedTopAnchorConstraint.isActive = true
        
        if track != nil {
            mediaPlayerDetailsView.peeked = "true"
            mediaPlayerDetailsView.track = track
            
        }
        
        if noPlay == true {
            
            UserDefaults.standard.set("true", forKey: "isFirstLoad")
            mediaPlayerDetailsView.peeked = "true"
            mediaPlayerDetailsView.track = track
//            UserDefaults.standard.set("false", forKey: "isFirstLoad")
            
        }
        
        if playlistTracks != nil {
            if mediaPlayerDetailsView.isKeyPresentInUserDefaults(key: "shuffleMode") {
                mediaPlayerDetailsView.shuffleMode = UserDefaults.standard.string(forKey: "shuffleMode")!
            } else {
                mediaPlayerDetailsView.shuffleMode = "OFF"
                UserDefaults.standard.set(mediaPlayerDetailsView.shuffleMode, forKey: "shuffleMode")
            }
            
            if mediaPlayerDetailsView.shuffleMode == "ON" {
                mediaPlayerDetailsView.playlistTracks = mediaPlayerDetailsView.shuffledPlaylist
            } else {
                mediaPlayerDetailsView.playlistTracks = playlistTracks
            }
        }
        
        UIView.animate(withDuration: 0.5, delay: 0, usingSpringWithDamping: 0.7, initialSpringVelocity: 1, options: .curveEaseOut, animations: {
            
            self.view.layoutIfNeeded()
            self.tabBar.transform = .identity
            
            self.mediaPlayerDetailsView.maximizedStackView.alpha = 0
            self.mediaPlayerDetailsView.maximaizedCloseButton.isHidden = true
            self.mediaPlayerDetailsView.miniPlayerView.alpha = 1
        })
        self.mediaPlayerDetailsView.playlistHide()
        self.mediaPlayerDetailsView.miniPlaylistButton.addTarget(self, action: #selector(maximizePlaylist), for: .touchUpInside)
        self.mediaPlayerDetailsView.MoreButton.addTarget(self, action: #selector(requestMore), for: .touchUpInside)
        self.mediaPlayerDetailsView.HeartButton.addTarget(self, action: #selector(requestHeart), for: .touchUpInside)
        self.mediaPlayerDetailsView.EditButton.addTarget(self, action: #selector(requestEdit), for: .touchUpInside)
        
        
        
    }
    
    func peekPlayerDetails(track: MusicTrack?, playlistTracks: [MusicTrack] = []) {
        
        maximizedTopAnchorConstraint.isActive = false
        bottomAnchorConstraint.constant = view.frame.height
        minimizedTopAnchorConstraint.isActive = true
        
        UserDefaults.standard.set("false", forKey: "isFirstLoad")
        
        if track != nil {
            mediaPlayerDetailsView.peeked = "true"
            mediaPlayerDetailsView.track = track
            
            mediaPlayerDetailsView.titleLabel.text = track?.subject
            mediaPlayerDetailsView.authorLabel.text = track?.artistNick
            mediaPlayerDetailsView.miniTitleLabel.text = track?.subject
            mediaPlayerDetailsView.miniSubtitleLabel.text = track?.artistNick
            
            UserDefaults.standard.set("false", forKey: "isFirstLoad")
            
//            UserDefaults.standard.set("false", forKey: "isFirstLoad")
//            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "setupGestures"), object: nil)
            self.mediaPlayerDetailsView.miniPlaylistButton.addTarget(self, action: #selector(maximizePlaylist), for: .touchUpInside)
        } else {
            // if track nil => empty strings...
            mediaPlayerDetailsView.titleLabel.text = "재생중인 음악이 없습니다."
            mediaPlayerDetailsView.authorLabel.text = ""
            mediaPlayerDetailsView.miniTitleLabel.text = "재생중인 음악이 없습니다."
            mediaPlayerDetailsView.miniSubtitleLabel.text = ""
//
//            mediaPlayerDetailsView.playPauseButton.setImage(#imageLiteral(resourceName: "playerPlay01"), for: .normal)
//            mediaPlayerDetailsView.miniPlayPauseButton.setImage(#imageLiteral(resourceName: "musicPlay01"), for: .normal)
            
//            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "removeGestures"), object: nil)
            self.mediaPlayerDetailsView.miniPlaylistButton.removeTarget(self, action: #selector(maximizePlaylist), for: .touchUpInside)
        }
        
        if playlistTracks != nil {
            
            self.musicTracks = playlistTracks
            self.musicTrackSelected = [Bool] (repeating: false, count: playlistTracks.count)
            
            if mediaPlayerDetailsView.isKeyPresentInUserDefaults(key: "shuffleMode") {
                mediaPlayerDetailsView.shuffleMode = UserDefaults.standard.string(forKey: "shuffleMode")!
            } else {
                mediaPlayerDetailsView.shuffleMode = "OFF"
                UserDefaults.standard.set(mediaPlayerDetailsView.shuffleMode, forKey: "shuffleMode")
            }
            
            if mediaPlayerDetailsView.shuffleMode == "ON" {
                mediaPlayerDetailsView.playlistTracks = mediaPlayerDetailsView.shuffledPlaylist
            } else {
                mediaPlayerDetailsView.playlistTracks = playlistTracks
            }
        }
        
        UIView.animate(withDuration: 0.5, delay: 0, usingSpringWithDamping: 0.7, initialSpringVelocity: 1, options: .curveEaseOut, animations: {
            
            self.view.layoutIfNeeded()
            self.tabBar.transform = .identity
            
            self.mediaPlayerDetailsView.maximizedStackView.alpha = 0
            self.mediaPlayerDetailsView.maximaizedCloseButton.isHidden = true
            self.mediaPlayerDetailsView.miniPlayerView.alpha = 1
        })
        self.mediaPlayerDetailsView.playlistHide()
        self.mediaPlayerDetailsView.MoreButton.addTarget(self, action: #selector(requestMore), for: .touchUpInside)
        self.mediaPlayerDetailsView.HeartButton.addTarget(self, action: #selector(requestHeart), for: .touchUpInside)
        self.mediaPlayerDetailsView.EditButton.addTarget(self, action: #selector(requestEdit), for: .touchUpInside)
        self.mediaPlayerDetailsView.EditCancelButton.addTarget(self, action: #selector(editCancel), for: .touchUpInside)
        self.mediaPlayerDetailsView.hidePlaylistButton.addTarget(self, action: #selector(editCancelAndClose), for: .touchUpInside)
        
    }
    
    
    @objc func minimizePlayerDetails() {
        maximizedTopAnchorConstraint.isActive = false
        bottomAnchorConstraint.constant = view.frame.height
        minimizedTopAnchorConstraint.isActive = true
        
        UIView.animate(withDuration: 0.5, delay: 0, usingSpringWithDamping: 0.7, initialSpringVelocity: 1, options: .curveEaseOut, animations: {
            
            self.view.layoutIfNeeded()
            self.tabBar.transform = .identity
            
            self.mediaPlayerDetailsView.maximizedStackView.alpha = 0
            self.mediaPlayerDetailsView.maximaizedCloseButton.isHidden = true
            self.mediaPlayerDetailsView.miniPlayerView.alpha = 1
        })
        self.mediaPlayerDetailsView.playlistHide()
        self.mediaPlayerDetailsView.miniPlaylistButton.addTarget(self, action: #selector(maximizePlaylist), for: .touchUpInside)
        
    }
    
    
    @objc func openPlaylist() {
        print("[openPlaylist]")
        
//        let vc = VideoListController()
//        let navController = UINavigationController(rootViewController: vc)
//        self.present(vc, animated: true, completion: nil)
//        navigationController?.pushViewController(vc, animated: true)
        
    }
    
    @objc func editCancel() {
        
        mediaPlayerDetailsView.EditCancelButton.isHidden = true
        mediaPlayerDetailsView.playlistTableView.isEditing = false
        
        UserDefaults.standard.set(false, forKey: "isEditing")
        
//        musicTracks = tempTracks
        
        mediaPlayerDetailsView.playlistTableView.reloadData()
        
        self.mediaPlayerDetailsView.EditButton.backgroundColor = UIColor(
            red: 69.0/255.0, green: 76.0/255.0, blue: 104.0/255.0, alpha: 1.0
        )
        
        self.mediaPlayerDetailsView.EditButton.setTitle("편집", for: UIControlState.normal)
        
        self.mediaPlayerDetailsView.bottomMenu.isHidden = true
        
    }
    
    @objc func editCancelAndClose () {
        mediaPlayerDetailsView.EditCancelButton.isHidden = true
        mediaPlayerDetailsView.playlistTableView.isEditing = false
        
        UserDefaults.standard.set(false, forKey: "isEditing")
        
//        musicTracks = tempTracks
        mediaPlayerDetailsView.playlistTableView.reloadData()
        
        self.mediaPlayerDetailsView.EditButton.backgroundColor = UIColor(
            red: 69.0/255.0, green: 76.0/255.0, blue: 104.0/255.0, alpha: 1.0
        )
        
        self.mediaPlayerDetailsView.EditButton.setTitle("편집", for: UIControlState.normal)
        self.mediaPlayerDetailsView.bottomMenu.isHidden = true
        self.mediaPlayerDetailsView.BottomMoreDialogViewContrainer.isHidden = true
        
        
        if(isPlaylistAndPlayerAtTheSameTime == true) {
            minimizePlayerDetails()
            isPlaylistAndPlayerAtTheSameTime = false
        }
        
    }
    
    
    @objc func requestEdit() {
        
        
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "editClicked"), object: nil)
        
//        if (mediaPlayerDetailsView.playlistTableView.isEditing == false) {
//
//            UserDefaults.standard.set(false ,forKey: "isEdited")
////            tempTracks = musicTracks
//            mediaPlayerDetailsView.playlistTableView.isEditing = true
//            UserDefaults.standard.set(true ,forKey: "isEditing")
//
//            self.mediaPlayerDetailsView.EditCancelButton.isHidden = false
//            self.mediaPlayerDetailsView.EditButton.backgroundColor = UIColor(
//                red: 224.0/255.0, green: 225.0/255.0, blue: 227.0/255.0, alpha: 1.0
//            )
//            self.mediaPlayerDetailsView.EditButton.setTitle("편집", for: UIControlState.normal)
//            self.mediaPlayerDetailsView.bottomMenu.isHidden = true
//
//            mediaPlayerDetailsView.playlistTableView.reloadData()
//
//        } else {
//
//
//            let isEdited =
//                UserDefaults.standard.bool(forKey: "isEdited")
//
//            NSLog("result(userDefaults) isEdited : \(isEdited)")
//            if (isEdited == false) {
//                // 변경사항 없음
//                NSLog("[no-edited]")
//                return
//            }
//
//            if (isEdited == true) {
//                // 변경사항 적용
//                NSLog("[edited]")
//                __requestEdit()
//
//            }
//
//            UserDefaults.standard.set(false ,forKey: "isEdited")
//            self.mediaPlayerDetailsView.EditCancelButton.isHidden = true
//            mediaPlayerDetailsView.playlistTableView.isEditing = false
//            UserDefaults.standard.set(false, forKey: "isEditing")
//            self.mediaPlayerDetailsView.EditButton.backgroundColor = UIColor(
//                red: 69.0/255.0, green: 76.0/255.0, blue: 104.0/255.0, alpha: 1.0
//            )
//            self.mediaPlayerDetailsView.EditButton.setTitle("완료", for: UIControlState.normal)
//
//            mediaPlayerDetailsView.playlistTableView.reloadData()
//
//
//        }
        
        
    }
    
    
    
    @objc func __requestEdit() {
        print("[__requestEdit] function called")
        NSLog("-----------------------------------------")
        let URL = upmusicUrlNoStatic + "/api/player/update_list"
        
        let headers: HTTPHeaders = [
            "Accept": "application/json",
            "Content-Type" : "application/json; charset=utf-8"
        ]
        
        //params의 예 {"ids":[123456,789012]}
        let idArray: [Int64]
        
//
//
//        let parameters = [
//            "ids" : ""
//        ]
//
//
//        Alamofire.request(URL, method: .post
//            , parameters: parameters
//            , encoding: JSONEncoding.default
//            , headers: headers
//            ).responseJSON { response in
//                switch response.result {
//
//                case .success(let value):
//
//                    break
//
//                case .failure(let error):
//                    print(error)
//                }
//        }
    
        
        
    }
    
    
    
    
    @objc func requestMore() {
        print("MainController : [requestMore]")
//
//        minimizePlayerDetails()
//
//        let musicUrl = UserDefaults.standard.string(forKey: "currentMusicUrl") ?? ""
//        let URL = upmusicUrlNoStatic + musicUrl
//        UserDefaults.standard.set(URL, forKey: "reloadUrl")
//        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "reload"), object: nil)
        
        

        let stringNick = self.mediaPlayerDetailsView.track.artistNick ?? ""
        let stringSubject = self.mediaPlayerDetailsView.track.subject ?? ""
        
        mediaPlayerDetailsView.BottomMoreDialogViewContrainer.isHidden = false
        mediaPlayerDetailsView.BottomMoreDialogRow0_Label.text =
            stringSubject + " - " + stringNick
        
        
        let tap1 = UITapGestureRecognizer(target: self, action: #selector(self.bottomMoreRow1(sender:)))
        tap1.numberOfTapsRequired = 1
        
        let tap2 = UITapGestureRecognizer(target: self, action: #selector(self.bottomMoreRow2(sender:)))
        tap2.numberOfTapsRequired = 1
        
        let tap3 = UITapGestureRecognizer(target: self, action: #selector(self.bottomMoreRow3(sender:)))
        tap3.numberOfTapsRequired = 1
//
        let tap4 = UITapGestureRecognizer(target: self, action: #selector(self.bottomMoreRow4(sender:)))
        tap4.numberOfTapsRequired = 1
        
        let tap5 = UITapGestureRecognizer(target: self, action: #selector(self.bottomMoreRow5(sender:)))
        tap5.numberOfTapsRequired = 1
        
        mediaPlayerDetailsView.BottomMoreDialogRow1.isUserInteractionEnabled = true
        mediaPlayerDetailsView.BottomMoreDialogRow1.addGestureRecognizer(tap1)
        
        mediaPlayerDetailsView.BottomMoreDialogRow2.isUserInteractionEnabled = true
        mediaPlayerDetailsView.BottomMoreDialogRow2.addGestureRecognizer(tap2)
        
        mediaPlayerDetailsView.BottomMoreDialogRow3.isUserInteractionEnabled = true
        mediaPlayerDetailsView.BottomMoreDialogRow3.addGestureRecognizer(tap3)
        
        mediaPlayerDetailsView.BottomMoreDialogRow4.isUserInteractionEnabled = true
        mediaPlayerDetailsView.BottomMoreDialogRow4.addGestureRecognizer(tap4)
        
        
        if (mediaPlayerDetailsView.track.liked == true) {
            
            mediaPlayerDetailsView.BottomMoreDialogRow4_Image.image = #imageLiteral(resourceName: "playerLikeOn")
            mediaPlayerDetailsView.BottomMoreDialogRow4_Label.text = "좋아요 취소"
            
        }
        
        if (mediaPlayerDetailsView.track.liked == false) {
            
            mediaPlayerDetailsView.BottomMoreDialogRow4_Image.image = #imageLiteral(resourceName: "tabLikeOff")
            mediaPlayerDetailsView.BottomMoreDialogRow4_Label.text = "좋아요"
            
        }
        
        mediaPlayerDetailsView.BottomMoreDialogRow5.isUserInteractionEnabled = true
        mediaPlayerDetailsView.BottomMoreDialogRow5.addGestureRecognizer(tap5)
        
    }
    
    // 아티스트 프로필 url로
    @objc func bottomMoreRow1(sender: UIGestureRecognizer) {
            minimizePlayerDetails()
            //        DEFAULT_URL + "/music" + "/artist" + "/" + "" + track.getArtistId()
            let musicUrl = "/music/artist/\(self.mediaPlayerDetailsView.track.artistId!)"  //+ String(describing: self.mediaPlayerDetailsView.track.artistId)
            
            print("[URL] : \(musicUrl)")
            
            let URL = upmusicUrlNoStatic + musicUrl
            UserDefaults.standard.set(URL, forKey: "reloadUrl")
            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "reload"), object: nil)
            
            mediaPlayerDetailsView.BottomMoreDialogViewContrainer.isHidden = true
    }
    
    
    // 앨범정보 url로
    @objc func bottomMoreRow2(sender: UIGestureRecognizer) {
        
            minimizePlayerDetails()
    //        DEFAULT_URL + "/music" + "/album" + "/" + "" + track.getMusicAlbumId()
            let musicUrl = "/music/album/\(self.mediaPlayerDetailsView.track.musicAlbumId!)" //+ String(describing: self.mediaPlayerDetailsView.track.musicAlbumId)
            print("[URL] : \(musicUrl)")
            let URL = upmusicUrlNoStatic + musicUrl
            UserDefaults.standard.set(URL, forKey: "reloadUrl")
            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "reload"), object: nil)
            
            mediaPlayerDetailsView.BottomMoreDialogViewContrainer.isHidden = true
    }
    
    // 공유하기
    @objc func bottomMoreRow3(sender: UIGestureRecognizer) {
        print("MainController : [bottomMoreRow3]")
        
        minimizePlayerDetails()
        UserDefaults.standard.set(mediaPlayerDetailsView.track.id!, forKey: "selectedId")
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "evaluateShare"), object: nil)
        
        mediaPlayerDetailsView.BottomMoreDialogViewContrainer.isHidden = true
    }
    
    // 좋아요.
    @objc func bottomMoreRow4(sender: UIGestureRecognizer) {
        
        print("MainController : [bottomMoreRow4] call requestHeart()")
        requestHeart()
        
       
    }
    
    // 담기
    @objc func bottomMoreRow5(sender: UIGestureRecognizer) {
        print("MainController : [bottomMoreRow5]")
        
        UserDefaults.standard.set("MainController", forKey: "fromWhichController")
        UserDefaults.standard.set(mediaPlayerDetailsView.track.id!, forKey: "selectedId")
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "bottomMenuDown"), object: nil)
    }
    
    
    
    
    @objc func requestHeart() {
        print("[requestHeart] current track id : \(String(describing: self.mediaPlayerDetailsView.track.id))")
        
        NSLog("-----------------------------------------")
        let URL = upmusicUrlNoStatic
            + "/api/track/" + String(describing: self.mediaPlayerDetailsView.track.id!) + "/like" //.toSecureHTTPSofDomainChange()
        let URLinner = upmusicUrlNoStatic
            + "/api/track/" + String(describing: self.mediaPlayerDetailsView.track.id!)
        
        
        let headers: HTTPHeaders = [
            "Accept": "application/json",
            "Content-Type" : "application/json; charset=utf-8"
        ]
        
        Alamofire.request(URL, method: .post
//            , parameters: parameters
            , encoding: JSONEncoding.default
            , headers: headers
            ).responseJSON { response in
//
//                NSLog("[rCS] > \(response.result)")
//                NSLog("[rCS] > \(response.result.value)")
//                NSLog("-----------------------------------------")
//
                switch response.result {
                    
                case .success(let value):
                    
                    Alamofire.request(URLinner, method: .get
                        ).responseJSON { responseInner in
                            
                            switch responseInner.result {
                                
                                case .success(let valueInner):
                                    
                                    let jsonInner = JSON(valueInner)
                                    let temp = MusicTrack(JSONString: jsonInner.description)
                                    
                                    if (temp != nil) {
                                        
                                        self.mediaPlayerDetailsView.HeartCntLabel.text =  String(describing: temp!.heartCnt!)
                                        self.mediaPlayerDetailsView.track.heartCnt = temp!.heartCnt!
                                    }
                                
                                
                                case .failure(let errorInner):
                                    
                                    print(errorInner)
                            }
                    }
                    
                    let json = JSON(value)
                    
                    if (json["status"].description == "true") {
                        if (json["message"].description == "true") {// true => 좋아요 입력
                            
                            self.mediaPlayerDetailsView.HeartButton.setImage(#imageLiteral(resourceName: "playerLikeOn"), for: .normal)
                            self.mediaPlayerDetailsView.BottomMoreDialogRow4_Image.image = #imageLiteral(resourceName: "playerLikeOn")
                            self.mediaPlayerDetailsView.BottomMoreDialogRow4_Label.text = "좋아요 취소"
                            self.mediaPlayerDetailsView.track.liked = true
                            self.mediaPlayerDetailsView.HeartButton.reloadInputViews()
                            NSLog(" > status true : \(json["message"].description)")
                            
                        }
                        
                        if (json["message"].description == "false") {// false => 좋아요 취소
                            self.mediaPlayerDetailsView.HeartButton.setImage(#imageLiteral(resourceName: "tabLikeOff"), for: .normal)
                            self.mediaPlayerDetailsView.BottomMoreDialogRow4_Image.image = #imageLiteral(resourceName: "tabLikeOff")
                            self.mediaPlayerDetailsView.BottomMoreDialogRow4_Label.text = "좋아요"
                            self.mediaPlayerDetailsView.track.liked = false
                            self.mediaPlayerDetailsView.HeartButton.reloadInputViews()
                            NSLog(" > status false : \(json)")
                        }
                    }
                    
                    if (json["status"].description == "false") {
                        
                    }
                    
                    
                case .failure(let error):
                    
                    print(error)
                }
        }
    
    
    }
        

    
    @objc func maximizePlaylist () {
        //        self.tabBar.isHidden = false
        
        minimizedTopAnchorConstraint.isActive = false
        maximizedTopAnchorConstraint.isActive = true
        maximizedTopAnchorConstraint.constant = 0
        
        bottomAnchorConstraint.constant = 0
        
        UIView.animate(withDuration: 0.5, delay: 0, usingSpringWithDamping: 0.7, initialSpringVelocity: 1, options: .curveEaseOut, animations: {
            
            self.view.layoutIfNeeded()
            
            self.tabBar.transform = CGAffineTransform(translationX: 0, y: 100)
            
            self.mediaPlayerDetailsView.maximizedStackView.alpha = 1
            self.mediaPlayerDetailsView.maximaizedCloseButton.isHidden = false
            self.mediaPlayerDetailsView.miniPlayerView.alpha = 0
            
        })
        
        
        self.mediaPlayerDetailsView.playlistShow()
        
        isPlaylistAndPlayerAtTheSameTime = true
    }
    
    func shuffleArray(array: [MusicTrack]) -> [MusicTrack] {
        
        var tempArray = array
        for index in 0...array.count - 1 {
            
            let randomNumber = arc4random_uniform(UInt32(array.count - 1))
            let randomIndex = Int(randomNumber)
            tempArray[randomIndex] = array[index]
        }
        
        print("tempArray : \(tempArray[1].subject), array : \(array[1].subject)")
        
        return tempArray
    }
    
    func maximizePlayerDetails(track: MusicTrack?, playlistTracks: [MusicTrack] = []) {
//        self.tabBar.isHidden = false
        
//        print("[maximizePlayerDetails] track : \(track)")
        
        minimizedTopAnchorConstraint.isActive = false
        maximizedTopAnchorConstraint.isActive = true
        maximizedTopAnchorConstraint.constant = 0
        bottomAnchorConstraint.constant = 0
        
        print("[maximizePlayerDetails] track check")
        if track != nil {
            
            print("[maximizePlayerDetails] track not nil")
            mediaPlayerDetailsView.peeked = self.peeked
            mediaPlayerDetailsView.track = track
            self.peeked = "false"
            
//            UserDefaults.standard.set("false", forKey: "isFirstLoad")
        }
        
        if playlistTracks != nil {
            
            print("[maximizePlayerDetails] playlistTracks not nil")
            
            self.musicTracks = playlistTracks
            self.musicTrackSelected = [Bool] (repeating: false, count: playlistTracks.count)
            
            if mediaPlayerDetailsView.isKeyPresentInUserDefaults(key: "shuffleMode") {
                mediaPlayerDetailsView.shuffleMode = UserDefaults.standard.string(forKey: "shuffleMode")!
            } else {
                mediaPlayerDetailsView.shuffleMode = "OFF"
                UserDefaults.standard.set(mediaPlayerDetailsView.shuffleMode, forKey: "shuffleMode")
            }
            
//            mediaPlayerDetailsView.shuffledPlaylist = shuffleArray(array: playlistTracks)
//            mediaPlayerDetailsView.originPlaylist = playlistTracks
            
            if mediaPlayerDetailsView.shuffleMode == "ON" {
                mediaPlayerDetailsView.playlistTracks = mediaPlayerDetailsView.shuffledPlaylist
            } else {
                mediaPlayerDetailsView.playlistTracks = playlistTracks
            }
        }
        
        print("[maximizePlayerDetails] track end")
        
        UIView.animate(withDuration: 0.5, delay: 0, usingSpringWithDamping: 0.7, initialSpringVelocity: 1, options: .curveEaseOut, animations: {
            self.view.layoutIfNeeded()
            self.tabBar.transform = CGAffineTransform(translationX: 0, y: 100)
            self.mediaPlayerDetailsView.maximizedStackView.alpha = 1
            self.mediaPlayerDetailsView.maximaizedCloseButton.isHidden = false
            self.mediaPlayerDetailsView.miniPlayerView.alpha = 0
            
        })
        
        self.mediaPlayerDetailsView.MoreButton.addTarget(self, action: #selector(requestMore), for: .touchUpInside)
        self.mediaPlayerDetailsView.HeartButton.addTarget(self, action: #selector(requestHeart), for: .touchUpInside)
        self.mediaPlayerDetailsView.EditButton.addTarget(self, action: #selector(requestEdit), for: .touchUpInside)
        self.mediaPlayerDetailsView.EditCancelButton.addTarget(self, action: #selector(editCancel), for: .touchUpInside)
        self.mediaPlayerDetailsView.hidePlaylistButton.addTarget(self, action: #selector(editCancelAndClose), for: .touchUpInside)
        
    }
    
    //MARK:- Setup Functions
    let mediaPlayerDetailsView = MediaPlayerDetailsView.initFromNib()
    let mediaPlayerPlaylistView = MediaPlayerPlaylistView.initFromNib()
    
    var maximizedTopAnchorConstraint: NSLayoutConstraint!
    var minimizedTopAnchorConstraint: NSLayoutConstraint!
    var bottomAnchorConstraint: NSLayoutConstraint!
    
    fileprivate func setupPlayerDetailsView() {
        print("Setting up PlayerDetailsView")
        
        // use auto layout
        //        view.addSubview(playerDetailsView)
        view.insertSubview(mediaPlayerDetailsView, belowSubview: tabBar)
        
        // enables auto layout
        mediaPlayerDetailsView.translatesAutoresizingMaskIntoConstraints = false
        
        maximizedTopAnchorConstraint = mediaPlayerDetailsView.topAnchor.constraint(equalTo: view.topAnchor, constant: view.frame.height)
        maximizedTopAnchorConstraint.isActive = true
        
        bottomAnchorConstraint = mediaPlayerDetailsView.bottomAnchor.constraint(equalTo: view.bottomAnchor, constant: view.frame.height)
        bottomAnchorConstraint.isActive = true
        
        minimizedTopAnchorConstraint = mediaPlayerDetailsView.topAnchor.constraint(equalTo: tabBar.topAnchor, constant: 0)
        //        minimizedTopAnchorConstraint.isActive = true
        
        mediaPlayerDetailsView.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
        mediaPlayerDetailsView.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
    }
    
    func setupViewControllers() {
        let layout = UICollectionViewFlowLayout()
        let favoritesController = FavoritesController(collectionViewLayout: layout)
        viewControllers = [
//            generateNavigationController(for: MusicTrackController(), title: "Favorites", image: #imageLiteral(resourceName: "favorites"))
            generateNavigationController(for: MainWebViewController(), title: "Favorites", image: #imageLiteral(resourceName: "favorites"))
        ]
    }
    
    //MARK:- Helper Functions
    
    fileprivate func generateNavigationController(for rootViewController: UIViewController, title: String, image: UIImage) -> UIViewController {
        let navController = UINavigationController(rootViewController: rootViewController)
        //        navController.navigationBar.prefersLargeTitles = true
        tabBar.barTintColor = UIColor(red: 24.0/255.0, green: 30.0/255.0, blue: 51.0/255.0, alpha: 1.0)
        tabBar.tintColor = .white

        //        rootViewController.navigationItem.title = title
        navController.tabBarItem.title = title
        navController.tabBarItem.image = image

        let logo = #imageLiteral(resourceName: "logo")
        let imageView = UIImageView(image: logo)
        let bannerWidth = UINavigationBar().frame.size.width
        let bannerHeight = UINavigationBar().frame.size.height
        let bannerX = bannerWidth / 2 - image.size.width / 2
        let bannerY = bannerHeight / 2 - image.size.height / 2
        imageView.frame = CGRect(x: bannerX, y: bannerY, width: bannerWidth, height: bannerHeight)
        rootViewController.navigationItem.titleView = imageView
        rootViewController.navigationController?.navigationBar.barTintColor = UIColor(red: 24.0/255.0, green: 30.0/255.0, blue: 51.0/255.0, alpha: 1.0)
        
        navController.navigationBar.isHidden = true
        
        return navController
        // terminal
        
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    
    func facebookLogin() {
        let fbLoginManager : FBSDKLoginManager = FBSDKLoginManager()
        fbLoginManager.logIn(withReadPermissions: ["email"], from: self) { (result, error) -> Void in
            if (error == nil){
                let fbloginresult : FBSDKLoginManagerLoginResult = result!
                // if user cancel the login
                if (result?.isCancelled)!{
                    return
                }
                if(fbloginresult.grantedPermissions.contains("email"))
                {
                    self.getFBUserData()
                }
            }
        }
    }
    
    func getFBUserData(){
        if((FBSDKAccessToken.current()) != nil){
            FBSDKGraphRequest(graphPath: "me", parameters: ["fields": "id, name, first_name, last_name, picture.type(large), email"]).start(completionHandler: { (connection, result, error) -> Void in
                if (error == nil){
                    //everything works print the user data
                    print(result)
                }
            })
        }
    }
    
//    func googleLogin() {
//        GIDSignIn.sharedInstance().delegate=self
//        GIDSignIn.sharedInstance().uiDelegate=self
//        GIDSignIn.sharedInstance().signIn()
//    }
    
}


