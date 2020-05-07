//
//  Constants.swift
//  Upmusic
//
//  Created by nough on 28/04/2019.
//  Copyright © 2019 Nough / Ndvor. All rights reserved.
//
import Foundation

let APP_DELEGATE = UIApplication.shared.delegate as! AppDelegate
// http
//  https://upmusic.azurewebsites.net
//  http://192.168.0.87:8080/
let upmusicUrl = NSURL(string: "http://upmusic.azurewebsites.net")
let upmusicUrlNoStatic = "http://upmusic.azurewebsites.net"
let upmusicUserAgent = "UPMusicIOS"
let upmusicSocketPath = "wss://upmusic.azurewebsites.net/upm-player-websocket/websocket" //
let upmusicSocketTopicPath = "/user/topic/player"
let upmusicSocketSendPlaytimePath = "/app/playtime"
let upmusicSocketSendIncreasePlaytimePath = "/app/playtime"


