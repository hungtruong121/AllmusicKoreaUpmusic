//
//  MusicTrackCell.swift
//  Upmusic
//
//  Created by nough on 24/07/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import UIKit
import SDWebImage

class MusicTrackCell: UITableViewCell {
    
    @IBOutlet weak var podcastImageView: UIImageView!
    @IBOutlet weak var trackNameLabel: UILabel!
    @IBOutlet weak var artistNameLabel: UILabel!
    
    @IBOutlet weak var PlayButton: UIButton!
    @IBOutlet weak var MoreButton: UIButton!
    @IBOutlet weak var RemoveButton: UIButton!
    
    var isCellSelected:Bool = false
    
    var track: MusicTrack! {
        didSet {
            
//            print(" >>> track.subject?.count : \(track.subject?.count ?? 0)")
            trackNameLabel.text = track.subject
            if (track.subject?.count ?? 0 > 30) {
                trackNameLabel.text = (track.subject?.substring(with: 0..<30) ?? "") + "..."
            }
            
            //            trackNameLabel.text = track.subject
            artistNameLabel.text = track.artistNick
            if (track.artistNick?.count ?? 0 > 60) {
                artistNameLabel.text = (track.subject?.substring(with: 0..<60) ?? "") + "..."
            }
            
            trackNameLabel.sizeToFit()
            artistNameLabel.sizeToFit()
            
            PlayButton.isHidden = true
            RemoveButton.isHidden = true
            isCellSelected = false
            
            let temp = track.coverImageUrl?.toSecureHTTPSofDomainChange()
//            print("CELL SETT : \(temp)")
            guard let url = URL(string: temp ?? "") else { return }
            //            URLSession.shared.dataTask(with: url) { (data, _, _) in
            //                print("Finished downloading image data:", data)
            //                guard let data = data else { return }
            //                DispatchQueue.main.async {
            //                    self.podcastImageView.image = UIImage(data: data)
            //                }
            //
            //            }.resume()
            
            podcastImageView.sd_setImage(with: url, completed: nil)
        }
    }
    
}





