//
//  VideoListController.swift
//  Upmusic
//
//  Created by nough on 04/08/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import UIKit
import FeedKit
import Alamofire
import VGPlayer
import SnapKit


class VideoListController : UITableViewController {
    
    
    var video: VideoFromServer? {
        didSet {
            navigationItem.title = video?.subject
            
            //        let button1 = UIBarButtonItem(image: UIImage(named: "Menu Filled-50"), style: .plain, target: self, action: Selector("action")) // action:#selector(Class.MethodName) for swift 3
            let button1 = UIBarButtonItem(image: UIImage(named: "Menu Filled-50"), style: .plain, target: self, action:nil) // action:#selector(Class.MethodName) for swift 3
            navigationItem.leftBarButtonItem  = button1
            
            let navigationBarAppearace = UINavigationBar.appearance()
            navigationBarAppearace.tintColor = UIColor.white
            navigationBarAppearace.titleTextAttributes = [NSAttributedStringKey(rawValue: NSAttributedStringKey.foregroundColor.rawValue): UIColor.white]
            
        }
    }
    
    fileprivate func fetchItems() {
        
        let URL = "http://up-music.ap-northeast-2.elasticbeanstalk.com/api/video"
        Alamofire.request(URL).responseArray { (response: DataResponse<[VideoFromServer]>) in
            
            let dataArray = response.result.value
            self.videos = dataArray!
            
            //print("[Url!] : \(self.musicTracks[0].coverImageUrl?.toSecureHTTPSofDomainChange())")
            //            print("[Url!] : \(self.musicTracks[0].coverImageUrl)")
            
            self.tableView.reloadData()
        }
    }
    
    fileprivate let cellId = "cellId"
    
    var videos = [VideoFromServer]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        fetchItems()
        setupTableView()
//        setupNavigationBarButtons()
    }
    
    //MARK:- Setup Work
    
//    fileprivate func setupNavigationBarButtons() {
//        //let's check if we have already saved this podcast as fav
//        let savedPodcasts = UserDefaults.standard.savedPodcasts()
//        let hasFavorited = savedPodcasts.index(where: { $0.trackName == self.video?.subject && $0 == self.video?.artistName }) != nil
//        if hasFavorited {
//            // setting up our heart icon
//            navigationItem.rightBarButtonItem = UIBarButtonItem(image: #imageLiteral(resourceName: "heart"), style: .plain, target: nil, action: nil)
//        } else {
//            navigationItem.rightBarButtonItems = [
////                UIBarButtonItem(title: "Favorite", style: .plain, target: self, action: #selector(handleSaveFavorite)),
//                //                UIBarButtonItem(title: "Fetch", style: .plain, target: self, action: #selector(handleFetchSavedPodcasts))
//            ]
//        }
//
//    }
    
//    @objc fileprivate func handleFetchSavedPodcasts() {
//        print("Fetching saved Podcasts from UserDefaults")
//        // how to retrieve our Podcast object from UserDefaults
//        guard let data = UserDefaults.standard.data(forKey: UserDefaults.favoritedPodcastKey) else { return }
//
//        let savedPodcasts = NSKeyedUnarchiver.unarchiveObject(with: data) as? [Podcast]
//
//        savedPodcasts?.forEach({ (p) in
//            print(p.trackName ?? "")
//        })
//
//    }
    
//    @objc fileprivate func handleSaveFavorite() {
//        print("Saving info into UserDefaults")
//
//        guard let video = self.video else { return }
//
//        // 1. Transform Podcast into Data
//        var listOfPodcasts = UserDefaults.standard.savedPodcasts()
//        listOfPodcasts.append(video)
//        let data = NSKeyedArchiver.archivedData(withRootObject: listOfPodcasts)
//
//        UserDefaults.standard.set(data, forKey: UserDefaults.favoritedPodcastKey)
//
//        showBadgeHighlight()
//        navigationItem.rightBarButtonItem = UIBarButtonItem(image: #imageLiteral(resourceName: "heart"), style: .plain, target: nil, action: nil)
//    }
    
//    fileprivate func showBadgeHighlight() {
//        UIApplication.mainTabBarController()?.viewControllers?[1].tabBarItem.badgeValue = "New"
//    }
    
    fileprivate func setupTableView() {
        let nib = UINib(nibName: "VideoListCell", bundle: nil)
        tableView.register(nib, forCellReuseIdentifier: cellId)
        tableView.tableFooterView = UIView()
    }
    
    //MARK:- UITableView
    
    override func tableView(_ tableView: UITableView, editActionsForRowAt indexPath: IndexPath) -> [UITableViewRowAction]? {
        
        let downloadAction = UITableViewRowAction(style: .normal, title: "Download") { (_, _) in
            print("Downloading episode into UserDefaults")
            let video = self.videos[indexPath.row]
//            UserDefaults.standard.downloadEpisode(episode: video)
            
            // download the podcast episode using Alamofire
//            APIService.shared.downloadEpisode(episode: video)
        }
        
        return [downloadAction]
    }
    
    override func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        let activityIndicatorView = UIActivityIndicatorView(activityIndicatorStyle: .whiteLarge)
        activityIndicatorView.color = .darkGray
        activityIndicatorView.startAnimating()
        return activityIndicatorView
    }
    
    override func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return videos.isEmpty ? 200 : 0
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
//        let video = self.videos[indexPath.row]
//        let mainTabBarController = UIApplication.shared.keyWindow?.rootViewController as? MainTabBarController
//        mainTabBarController?.maximizePlayerDetails(episode: episode, playlistEpisodes: self.episodes)

//        let videoLauncher = VideoLauncher()
//        videoLauncher.setVideoURL(url: self.videos[indexPath.row].filenameUrl!)
//        videoLauncher.showVideoPlayer()
        
//        performSegue(withIdentifier: "VGMediaViewController", sender: dataSource[indexPath.row])
        
        
        
//        let vc = WKWebViewController()
//        navigationController?.pushViewController(vc, animated: true)
//
        let vc = VGCustomViewController()
        vc.urlString = self.videos[indexPath.row].filenameUrl!
        print(self.videos[indexPath.row].filenameUrl!)
        navigationController?.pushViewController(vc, animated: true)
        
        
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return videos.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: cellId, for: indexPath) as! VideoListCell
        let video = videos[indexPath.row]
        cell.video = video
        return cell
    }
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 134
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }

    
}
