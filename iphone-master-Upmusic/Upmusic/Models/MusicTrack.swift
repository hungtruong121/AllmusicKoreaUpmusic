//
//  MusicTrack.swift
//  Upmusic
//
//  Created by nough on 24/07/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import Foundation
import ObjectMapper
import FeedKit

class MusicTrack :  Mappable, Codable {
    
    required init?(map: Map){
        rejectedReason = ""
        adminUrl = ""
        themeNames = ""
    }
    
    func mapping(map: Map) {
        adminUrl <- map["adminUrl"]
        artistId <- map["artistId"]
        artistNick <- map["artistNick"]
        heartCnt <- map["heartCnt"]
        artistUrl <- map["artistUrl"]
        coverImageUrl <- map["coverImageUrl"]
        duration <- map["duration"]
        filenameUrl <- map["filenameUrl"]
        id <- map["id"]
        liked <- map["liked"]
        musicAlbumId <- map["musicAlbumId"]
        rejectedReason <- map["rejectedReason"]
        subject <- map["subject"]
        themeNames <- map["themeNames"]
        titleTrack <- map["titleTrack"]
        url <- map["url"]
        filename <- map["filename"]
        musicAlbumSubject <- map["musicAlbumSubject"]
        copyrighterCode <- map["copyrighterCode"]
        copyrighterName <- map["copyrighterName"]
    }
    
    var artistId: Int64?
    var artistNick: String?
    var artistUrl: String?
    var coverImageUrl: String?
    var duration: Int64?
    var filename : String?
    var filenameUrl: String?
    var heartCnt: Int64?
    var id: Int64?
    var liked: Bool?
    var musicAlbumId: Int64?
    var musicAlbumSubject: String?
    var rejectedReason: String?
    var subject: String?
    
    var titleTrack: Bool?
    var url: String?
    
    var adminUrl: String? // > x
    var themeNames: String? // > x
    
    var copyrighterCode: String?
    var copyrighterName: String?
    
//    "copyrighterCode" : null, // > ?
//    "copyrighterName" : null, // > ?

    init(feedItem : RSSFeedItem) {
        self.filenameUrl = feedItem.enclosure?.attributes?.url ?? ""
        self.subject = feedItem.title ?? ""
//        self.pubDate = feedItem.pubDate ?? Date()
//        self.description = feedItem.iTunes?.iTunesSubtitle ?? feedItem.description ?? ""
        self.artistNick = feedItem.iTunes?.iTunesAuthor ?? ""
        self.coverImageUrl = feedItem.iTunes?.iTunesImage?.attributes?.href
    }
    
    
    
// swift 2/3/objc method
//
//    init(json: [String: Any]) {
//        adminUrl = json["adminUrl"] as? String ?? ""
//        artistId = json["artistId"] as? String ?? ""
//        artistNick = json["artistNick"] as? String ?? ""
//        artistUrl = json["artistUrl"] as? String ?? ""
//        coverImageUrl = json["coverImageUrl"] as? String ?? ""
//        duration = json["duration"] as? String ?? ""
//        filenameUrl = json["filenameUrl"] as? String ?? ""
//        id = json["id"] as? String ?? ""
//        liked = json["liked"] as? String ?? ""
//        musicAlbumId = json["musicAlbumId"] as? String ?? ""
//        rejectedReason = json["rejectedReason"] as? String ?? ""
//        subject = json["subject"] as? String ?? ""
//        themeNames = json["themeNames"] as? String ?? ""
//        titleTrack = json["titleTrack"] as? String ?? ""
//        url = json["url"] as? String ?? ""
//    }
}
