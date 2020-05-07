//
//  MediaPlayerPlaylistView.swift
//  Upmusic
//
//  Created by nough on 04/09/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import UIKit
import MediaPlayer

class MediaPlayerPlaylistView: UIView {
    
    let cellId = "cellId"
    var musicTracks : [MusicTrack] = []
    
    static func initFromNib() -> MediaPlayerPlaylistView {
        return Bundle.main.loadNibNamed("MediaPlayerPlaylistView", owner: self, options: nil)?.first as! MediaPlayerPlaylistView
    }   
//
//    @IBAction func handleDismiss(_ sender: Any) {
//        let mainController =  UIApplication.shared.keyWindow?.rootViewController as? MainController
//        mainController?.maximizePlaylist()
//
//    }
    
    
    
}
