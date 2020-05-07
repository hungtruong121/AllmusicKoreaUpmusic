//
//  MediaPlayerPlaylistView+Gestures.swift
//  Upmusic
//
//  Created by nough on 04/09/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import UIKit

extension MediaPlayerPlaylistView : UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: cellId, for: indexPath) as! MusicTrackCell
        let track = musicTracks[indexPath.row]
        cell.track = track
        return cell
    }
    
}
