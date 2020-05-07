//
//  RSSFeed.swift
//  Upmusic
//
//  Created by nough on 24/07/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import FeedKit

extension RSSFeed {
    
    // [ FOR PODCAST ]
    func toEpisodes() -> [Episode] {
        let imageUrl = iTunes?.iTunesImage?.attributes?.href
        
        var episodes = [Episode]() // blank Episode array
        items?.forEach({ (feedItem) in
            var episode = Episode(feedItem: feedItem)
            
            if episode.imageUrl == nil {
                episode.imageUrl = imageUrl
            }
            
            episodes.append(episode)
        })
        return episodes
    }
    
    // [ FOR MUSIC TRACK ]
    func toMusicTracks() -> [MusicTrack] {
        let imageUrl = iTunes?.iTunesImage?.attributes?.href
        
        var tracks = [MusicTrack]() // blank Episode array
        items?.forEach({ (feedItem) in
            var track = MusicTrack(feedItem: feedItem)
            
            if track.coverImageUrl == nil {
                track.coverImageUrl = imageUrl
            }
            
            tracks.append(track)
        })
        return tracks
    }
    
    
    
}
