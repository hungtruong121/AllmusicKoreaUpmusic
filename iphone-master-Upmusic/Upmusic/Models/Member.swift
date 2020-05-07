//
//  Member.swift
//  Upmusic
//
//  Created by nough on 24/07/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import Foundation
import ObjectMapper

class Member : Mappable, Codable {
     
    var artistGrade: String?
    var copyrightMembershipStr: String?
    var createAtStr: String?
    var email: String?
    var fundingPoint: String?
    var gender:String?
    var genderName:String?
    var grade:String?
    var hyc:String?
    var id:String?
    var imgUrl:String?
    var liked:String?
    var memberUrl:String?
    var nick:String?
    var playtime:String?
    var profileImage:String?
    var profileImageUrl:String?
    var registrationType:String?
    var token:String?
    var trackCnt:String?
    var upmPoint:String?
    var url:String?
    var usablePoint:String?
    var usablePointString:String?
    var videoCnt:String?
    
    
    required init?(map: Map){
        
    }
    
    func mapping(map: Map) {
        artistGrade <- map["artistGrade"]
        copyrightMembershipStr <- map["copyrightMembershipStr"]
        createAtStr <- map["createAtStr"]
        email <- map["email"]
        fundingPoint <- map["fundingPoint"]
        gender <- map["gender"]
        genderName <- map["genderName"]
        grade <- map["grade"]
        hyc <- map["hyc"]
        id <- map["id"]
        liked <- map["liked"]
        memberUrl <- map["memberUrl"]
        nick <- map["nick"]
        playtime <- map["playtime"]
        profileImage <- map["profileImage"]
        profileImageUrl <- map["profileImageUrl"]
        registrationType <- map["registrationType"]
        token <- map["token"]
        trackCnt <- map["trackCnt"]
        upmPoint <- map["upmPoint"]
        url <- map["url"]
        usablePoint <- map["usablePoint"]
        usablePointString <- map["usablePointString"]
        videoCnt <- map["videoCnt"]
    }
    
    
    
}
