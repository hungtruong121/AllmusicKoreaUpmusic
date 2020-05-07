//
//  VideoCell.swift
//  Upmusic
//
//  Created by nough on 03/08/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//



import UIKit
import SDWebImage

class VideoListCell: UITableViewCell {
    
    
    @IBOutlet weak var podcastImageView: UIImageView!
    @IBOutlet weak var trackNameLabel: UILabel!
    @IBOutlet weak var episodeCountLabel: UILabel!
    @IBOutlet weak var artistNameLabel: UILabel!
    
    var video: VideoFromServer! {
        didSet {
            trackNameLabel.text = video.subject
            artistNameLabel.text = video.artistNick
            
            episodeCountLabel.text = "\(video.createdAt ?? "") ."
            
            let temp = video.thumbnailUrl?.toSecureHTTPSofDomainChange()
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

