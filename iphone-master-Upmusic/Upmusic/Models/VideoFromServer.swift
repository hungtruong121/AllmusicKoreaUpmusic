//
//  Video.swift
//  Upmusic
//
//  Created by nough on 24/07/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import Foundation
import ObjectMapper
import FeedKit

class VideoFromServer:  Mappable{
    var adminUrl: String?
    var artistNick: String?
    var artistUrl: String?
    var createdAt: String?
    var description: String?
    var duration: Int?
    var filename: String?
    var filenameUrl: String?
    var genreName: String?
    var heartCnt: Int?
    var hitCnt: Int?
    var hotPoint: Float?
    var id: String?
    var liked: Bool?
    var member: Member?
    var subject: String?
    var thumbnail: String?
    var thumbnailUrl: String?
    var typeName: String?
    var updatedAt: String?
    var url: String?
    var videoType: String?
    
    required init?(map: Map){
        
    }
    
    func mapping(map: Map) {
        adminUrl <- map["adminUrl"]
        artistNick <- map["artistNick"]
        artistUrl <- map["artistUrl"]
        createdAt <- map["createdAt"]
        description <- map["description"]
        duration <- map["duration"]
        filename <- map["filename"]
        filenameUrl <- map["filenameUrl"]
        genreName <- map["genreName"]
        heartCnt <- map["heartCnt"]
        hitCnt <- map["hitCnt"]
        hotPoint <- map["hotPoint"]
        member <- map["member"]
        subject <- map["subject"]
        id <- map["id"]
        liked <- map["liked"]
        thumbnail <- map["thumbnail"]
        thumbnailUrl <- map["thumbnailUrl"]
        typeName <- map["typeName"]
        updatedAt <- map["updatedAt"]
        url <- map["url"]
        videoType <- map["videoType"]
    }
}
