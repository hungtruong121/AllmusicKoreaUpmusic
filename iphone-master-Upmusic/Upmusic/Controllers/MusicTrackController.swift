//
//  MusicTrackController.swift
//  Upmusic
//
//  Created by nough on 24/07/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import UIKit
import Alamofire

class MusicTrackController : UITableViewController {
    
    let s3bucket = "upm-albums"
    let dataKey = ""
    
    fileprivate let cellId = "cellId"
    var musicTracks : [MusicTrack] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        fetchTracks()
        setupTableView()
    }
    
    
    fileprivate func setupTableView() {
        let nib = UINib(nibName: "MusicTrackCell", bundle: nil)
        tableView.register(nib, forCellReuseIdentifier: cellId)
        
        
        let navigationBarAppearace = UINavigationBar.appearance()
        navigationBarAppearace.tintColor = UIColor(red: 24.0/255.0, green: 30.0/255.0, blue: 51.0/255.0, alpha: 1.0)
        navigationBarAppearace.titleTextAttributes = [NSAttributedStringKey(rawValue: NSAttributedStringKey.foregroundColor.rawValue): UIColor.white]
        
        //        let button1 = UIBarButtonItem(image: UIImage(named: "Menu Filled-50"), style: .plain, target: self, action: Selector("action")) // action:#selector(Class.MethodName) for swift 3
        let button1 = UIBarButtonItem(image: UIImage(named: "menu"), style: .plain, target: self, action:nil)
        let button2 = UIBarButtonItem(image: UIImage(named: "search-1"), style: .plain, target: self, action:nil) // action:#selector(Class.MethodName) for swift 3
        navigationItem.leftBarButtonItem  = button1
        navigationItem.leftBarButtonItem?.tintColor = UIColor(red: 116.0/255.0, green: 135.0/255.0, blue: 207.0/255.0, alpha: 1.0)
        navigationItem.rightBarButtonItem = button2
        navigationItem.rightBarButtonItem?.tintColor = UIColor(red: 116.0/255.0, green: 135.0/255.0, blue: 207.0/255.0, alpha: 1.0)
        navigationItem.rightBarButtonItem?.action = #selector(tempVideo)
        
        
        let logo = #imageLiteral(resourceName: "logo")
        let imageView = UIImageView(image: logo)
        let bannerWidth = UINavigationBar().frame.size.width
        let bannerHeight = UINavigationBar().frame.size.height
        let bannerX = bannerWidth / 2 - logo.size.width / 2
        let bannerY = bannerHeight / 2 - logo.size.height / 2
        imageView.frame = CGRect(x: bannerX, y: bannerY, width: bannerWidth, height: bannerHeight)
        navigationItem.titleView = imageView
        
        tableView.tableFooterView = UIView()
    }
    
    
    @objc fileprivate func tempVideo() {
        print("tempVideo")
        let videoLC = VideoListController()
        navigationController?.pushViewController(videoLC, animated: true)
    }
    
    
    
    fileprivate func fetchTracks() {
        
        let URL = "http://up-music.ap-northeast-2.elasticbeanstalk.com/api/track".toSecureHTTPSofDomainChange()
        Alamofire.request(URL).responseArray { (response: DataResponse<[MusicTrack]>) in
            
            let dataArray = response.result.value
            self.musicTracks = dataArray!
            
            //print("[Url!] : \(self.musicTracks[0].coverImageUrl?.toSecureHTTPSofDomainChange())")
//            print("[Url!] : \(self.musicTracks[0].coverImageUrl)")
            
            self.tableView.reloadData()
        }
    }
    
    
    override func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        let activityIndicatorView = UIActivityIndicatorView(activityIndicatorStyle: .whiteLarge)
        activityIndicatorView.color = .darkGray
        activityIndicatorView.startAnimating()
        return activityIndicatorView
    }
    
    override func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return musicTracks.isEmpty ? 200 : 0
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let track = self.musicTracks[indexPath.row]
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        mainController?.maximizePlayerDetails(track: track, playlistTracks: self.musicTracks)
        
//        let window  = UIApplication.shared.keyWindow
//        let playerDetailsView = Bundle.main.loadNibNamed("MediaPlayerDetailsView", owner: self, options: nil)?.first as! UIView
//        playerDetailsView.frame = self.view.frame
//        window?.addSubview(playerDetailsView)

    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return musicTracks.count
        
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: cellId, for: indexPath) as! MusicTrackCell
        let track = musicTracks[indexPath.row]
        cell.track = track
        return cell
    }
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 134
    }
//
//    @objc func minimizePlayerDetails() {
//        maximizedTopAnchorConstraint.isActive = false
//        bottomAnchorConstraint.constant = view.frame.height
//        minimizedTopAnchorConstraint.isActive = true
//
//        UIView.animate(withDuration: 0.5, delay: 0, usingSpringWithDamping: 0.7, initialSpringVelocity: 1, options: .curveEaseOut, animations: {
//
//            self.view.layoutIfNeeded()
//            //            self.tabBar.transform = .identity
//
//            self.mediaPlayerDetailsView.maximizedStackView.alpha = 0
//            self.mediaPlayerDetailsView.miniPlayerView.alpha = 1
//        })
//    }
//
//    func maximizePlayerDetails(track: MusicTrack?, playlistTracks: [MusicTrack] = []) {
//        minimizedTopAnchorConstraint.isActive = false
//        maximizedTopAnchorConstraint.isActive = true
//        maximizedTopAnchorConstraint.constant = 0
//
//        bottomAnchorConstraint.constant = 0
//
//        if track != nil {
//            mediaPlayerDetailsView.track = track
//
//        }
//        mediaPlayerDetailsView.playlistTracks = playlistTracks
//
//
//        UIView.animate(withDuration: 0.5, delay: 0, usingSpringWithDamping: 0.7, initialSpringVelocity: 1, options: .curveEaseOut, animations: {
//
//            self.view.layoutIfNeeded()
//
//            //            self.tabBar.transform = CGAffineTransform(translationX: 0, y: 100)
//
//            self.mediaPlayerDetailsView.maximizedStackView.alpha = 1
//            self.mediaPlayerDetailsView.miniPlayerView.alpha = 0
//
//        })
//
//    }
//
//
//
//    //MARK:- Setup Functions
//    let mediaPlayerDetailsView = MediaPlayerDetailsView.initFromNib()
//
//    var maximizedTopAnchorConstraint: NSLayoutConstraint!
//    var minimizedTopAnchorConstraint: NSLayoutConstraint!
//    var bottomAnchorConstraint: NSLayoutConstraint!
//
//    fileprivate func setupPlayerDetailsView() {
//        print("Setting up MediaPlayerDetailsView")
//
//        // use auto layout
//        //        view.addSubview(mediaPlayerDetailsView)
//        view.insertSubview(mediaPlayerDetailsView, belowSubview: tableView)
//
//        // enables auto layout
//        mediaPlayerDetailsView.translatesAutoresizingMaskIntoConstraints = false
//
//        maximizedTopAnchorConstraint = mediaPlayerDetailsView.topAnchor.constraint(equalTo: view.topAnchor, constant: view.frame.height)
//        maximizedTopAnchorConstraint.isActive = true
//
//        bottomAnchorConstraint = mediaPlayerDetailsView.bottomAnchor.constraint(equalTo: view.bottomAnchor, constant: view.frame.height)
//        bottomAnchorConstraint.isActive = true
//
//        minimizedTopAnchorConstraint = mediaPlayerDetailsView.topAnchor.constraint(equalTo: tableView.bottomAnchor , constant: -64)
//        //        minimizedTopAnchorConstraint.isActive = true
//
//        mediaPlayerDetailsView.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
//        mediaPlayerDetailsView.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
//    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }

    
    
}

