//
//  Define.swift
//  Upmusic
//
//  Created by nough on 10/08/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//


import Foundation

class Define {
    
    static let appleAppID : String = "1401078208"

    static let HOST = "upmusic.azurewebsites.net" //실서버
    
    
    
    static let PORT = 21
    
    //static let HOST = "dev.e777.kr" // 개발서버.
    //static let PORT = 999 // 개발서버포트
    
    //static let SERVER_BASE = "http://" + HOST + ((PORT==21) ? "" : ":\(PORT)")
    static let SERVER_BASE = "https://" + HOST //+ ((PORT==21) ? "" : ":\(PORT)")
    
    static let MAIN_URL = SERVER_BASE + "/front/login"
    static let LOGIN_URL = SERVER_BASE + "/front/login"
    static let LOGOUT_URL = SERVER_BASE + "/front/logout"
    static let WAIT_URL = SERVER_BASE + "/front/wait/mylist"
    static let MYPAGE_URL = SERVER_BASE + "/front/mypage"
    static let EVENT_URL = SERVER_BASE + "/front/event"
    static let PUSH_URL = SERVER_BASE + "/front/push"
    static let QR_MAIN_URL = SERVER_BASE + "/qrcode/rfid"
    
    // temp
    static let TEST_PUSH_URL = "http://dev.e777.kr:999/sales/push"
    static let TEST_QR_IMAGE_URL = "http://dev.e777.kr:999/qrcode/A00002"
    static let TEST_ALLAT_PAY = "https://m.allatpay.com/servlet/AllatMobileV2/contract.ContractMainCL?menu_id=m020102"
    
    static let SCHEME_MOVE_LOGIN = "/front/login"
    static let SCHEME_MOVE_EVENT = "/event/rfid"
    static let SCHEME_MOVE_KAKAO_LOGIN = "/front/auth/kakao"
    static let SCHEME_MOVE_NAVER_LOGIN = "/front/auth/naver"
    static let SCHEME_MOVE_FACEBOOK_LOGIN = "/front/auth/facebook"
    
    static let useFacebook : Bool = true
    static let useFirebase : Bool = true
    static let useFcm : Bool = true
    static let useFcmForground : Bool = true
    static let useIAMPort : Bool = false
    static let useAllAt : Bool = false
    
    static let useLoginNaver : Bool = true
    static let useLoginKakao : Bool = true
    static let useLoginGoogle : Bool = true
    static let useLoginFacebook : Bool = true
    static let useSnsAutoLogout: Bool = true
    
    static let isWebviewLoading : Bool = true
    static let isWebviewAlert : Bool = true
    static let isNetworkCheck : Bool = true
    static let isUpdateCheck : Bool = true
    static let showSplashTime : Int = 1000  ///< 스플래시 화면 1초
    
    ///< 네이버 SDK 연동 관련 변수
    static let kServiceAppUrlScheme: String = "com.glosfer.upmusic"
    static let kConsumerKey: String = "NU3wfj54p_eVWz6ZnPBi"
    static let kConsumerSecret: String = "RtcRhgluB7"
    static let kServiceAppName: String = "Upmusic" // TEMP
    
    static var fcmToken: String? = nil
    static var isPushOnStart: Bool = false
    static var isOpenFirstViewController: Bool = false
}

