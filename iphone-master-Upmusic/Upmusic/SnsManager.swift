//
//  SnsManager.swift
//  Upmusic
//
//  Created by nough on 10/08/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import Foundation
import Alamofire
import NaverThirdPartyLogin
import FBSDKCoreKit
import FBSDKLoginKit
import GoogleSignIn
import SwiftyJSON

struct SnsLoginInfo {
    var name: String
    var email: String
    var provider: String
    var provider_id: String
    
    var is_success: Bool
    var has_email: Bool
    var profile_image: String?
    var thumbnail_image: String?
    
    init?() {
        self.is_success = false
        self.has_email = false
        self.name = ""
        self.email = ""
        self.provider = ""
        self.provider_id = ""
        self.profile_image = nil
        self.thumbnail_image = nil
    }
}

enum LoginType {
    case None
    case Mamma
    case Naver
    case Kakao
    case Google
    case Facebook
}

class SnsManager: UIViewController {
    
    var isLogin: Bool = false
    var loginType: LoginType = LoginType.None
    
    var snsLoginInfo:SnsLoginInfo? = nil
    var accessToken:String = ""
    
    static let sharedInstance : SnsManager = {
        let instance = SnsManager()
        //setup code
        return instance
    }()
    
    static func processLoginFailed()
    {
        NSLog("[yangs::SM::processLoginFailed]")
        AppComm.sharedInstance.badgeInfo = nil
        SnsManager.sharedInstance.snsLoginInfo = nil
        guard let top = AppUtils.getTopViewController() else {
            return
        }
        AppUtils.openWebView(viewController: top, urlString: Define.LOGOUT_URL)
    }
    
    static func processLoginFailed_NoneEmail()
    {
        NSLog("[processLoginFailed_NoneEmail]")
        guard let top = AppUtils.getTopViewController() else {
            return
        }
        AppUtils.openWebView(viewController: top, urlString: Define.LOGOUT_URL)
        let alert = UIAlertController(title: "안내", message: "이메일이 없는 경우 카카오톡 로그인할 수 없습니다.", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "확인", style: .default, handler: { action in
            AppUtils.openWebView(viewController: top, urlString: Define.LOGOUT_URL)
        }))
        top.present(alert, animated: true)
    }
    
    static func processLoginSuccessed()
    {
        NSLog("[processLoginSuccessed]")
        SnsManager.sharedInstance.isLogin = true;
        DispatchQueue.global(qos: .background).async {
            __processLoginSuccessed()
        }
    }
    
    static func requestSNSLogin(provider: LoginType, token: String) {
        
        NSLog("[requestSNSLogin]:::function called.")
        
        let upmusicUrlNoStatic = "https://upmusic.azurewebsites.net"
        let urlStringFacebook = upmusicUrlNoStatic + "/api/auth/login_or_register_with_facebook"
        let urlStringGoogle = upmusicUrlNoStatic + "/api/auth/login_or_register_with_google"
        let urlStringKakao = upmusicUrlNoStatic + "/api/auth/login_or_register_with_kakao"
        let urlStringNaver = upmusicUrlNoStatic + "/api/auth/login_or_register_with_naver"
        
        let headers: HTTPHeaders = [
            "Accept": "application/json",
            "Content-Type" : "application/json; charset=utf-8"
        ]
        
        var urlString = "" //.toSecureHTTPSofDomainChange()
        var parameters =  ["access_token" : token] // TEMP
        
        
        if (provider == LoginType.Facebook) {
            urlString = (urlStringFacebook)
            parameters = ["access_token" : token]
        }
        
        if (provider == LoginType.Google) {
            urlString = (urlStringGoogle)
            parameters = ["id_token" : token]
        }
        if (provider == LoginType.Kakao) {
            urlString = (urlStringKakao)
            parameters = ["access_token" : token]
        }
        if (provider == LoginType.Naver) {
            urlString = (urlStringNaver)
            parameters = ["access_token" : token]
        }
        
        
//        let semaphore = DispatchSemaphore(value: 0)
        
        // ALAMOFIRE ERROR
        Alamofire.request(urlString
            , method: .post
            , parameters: parameters
            , encoding: JSONEncoding.default
            , headers: headers
            )
            .responseJSON(completionHandler: { response in
                
                switch response.result {
                    
                case .success(let value):
                    let json = JSON(value)
                    
//                    print(" > json :  \(json)")
                    if (json["status"].description == "true") {
                        
                        NSLog(" > message description :  \(json["message"].description)")
                        
                        UserDefaults.standard.set(json["message"].description, forKey: "JSESSIONID")
                        
                        let user = json["object"].dictionaryObject! as [String:AnyObject]
                        NSLog(" > object description :  \(String(describing: json["object"].dictionaryObject))")
                        
                        UserDefaults.standard.set(user["token"], forKey: "TOKEN")
                        
                        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "reloadWebViewAfterLogin"), object: nil)
                        
                    }
                    
                    if (json["status"].description == "false") {
                        NSLog(" > description :  \(json)")
                        
                        // RETRY
                        if (SnsManager.sharedInstance.loginType == LoginType.Kakao) {
                            loginKakao(login: true, trial: 2)
                        }
                        
                    }
                    
                    NSLog("[requestSNSLogin]:::function end")
                case .failure(let error):
                    
                    print(error)
                    NSLog("[requestSNSLogin]:::function end")
                }
            }
                
        )
        
    }
    
    func clean() {
        HTTPCookieStorage.shared.removeCookies(since: Date.distantPast)
        print("[WebCacheCleaner] All cookies deleted")
        
        WKWebsiteDataStore.default().fetchDataRecords(ofTypes: WKWebsiteDataStore.allWebsiteDataTypes()) { records in
            records.forEach { record in
                WKWebsiteDataStore.default().removeData(ofTypes: record.dataTypes, for: [record], completionHandler: {})
                print("[WebCacheCleaner] Record \(record) deleted")
            }
        }
    }
    
    static func __processLoginSuccessed()
    {
        NSLog("[__processLoginSuccessed]!!!")
        
        NSLog(" > loginType : \(SnsManager.sharedInstance.loginType), token : \(SnsManager.sharedInstance.accessToken)")
        
        requestSNSLogin(provider: SnsManager.sharedInstance.loginType, token: SnsManager.sharedInstance.accessToken)
        
    }
    
    static func processLogoutComplete()
    {
        NSLog("[yangs::SM::processLogoutComplete]")
        SnsManager.sharedInstance.isLogin = false;
//        SnsManager.sharedInstance.loginType = LoginType.Mamma
//        AppUtils.openWebView(viewController: AppUtils.getTopViewController()!, urlString: Define.LOGOUT_URL)
    }
    
    static func logout()
    {
        NSLog(" > SNSManager.swift > [::logout::]")
        switch (SnsManager.sharedInstance.loginType)
        {
        case LoginType.None:
            break
        case LoginType.Mamma:
            loginMamma(login: false)
            break
        case LoginType.Naver:
            loginNaver(login: false)
            break
        case LoginType.Kakao:
            loginKakao(login: false, trial: 0)
            break
        case LoginType.Facebook:
            loginFacebook(login: false)
            break
        case LoginType.Google:
            loginGoogle(login: false)
            break
        default:
            break
        }
        
        SnsManager.sharedInstance.snsLoginInfo = nil
        clearCookies()
        clearCache()
        processLogoutComplete()
    }
    
    static func clearCookies(){
        //let cookie = HTTPCookie.self
        let cookieJar = HTTPCookieStorage.shared
        
        for cookie in cookieJar.cookies! {
            cookieJar.deleteCookie(cookie)
        }
    }
    
    static func clearCache(){
        URLCache.shared.removeAllCachedResponses()
        URLCache.shared.diskCapacity = 0
        URLCache.shared.memoryCapacity = 0
    }
    
    static func loginMamma(login :Bool)
    {
        print("[yangs::SU::loginMamma] login(\(login))")
        
//        guard let vc = AppUtils.getTopViewController() else {
//            return
//        }
        
        if(!login)
        {
            processLogoutComplete()
        }
        else
        {
            SnsManager.sharedInstance.loginType = LoginType.Mamma
            //processLoginSuccessed()
        }
    }
    
    
    static func loginGoogle(login :Bool) {
        
        NSLog("[loginGoogle] login(\(login))")
        
        
        MainWebViewController().handleCustomGoogleSign()
        
        
        
    }
    
    
    static func loginFacebook(login :Bool)
    {
        NSLog("[loginFacebook] login(\(login))")
        
        guard let vc = AppUtils.getTopViewController() else {
            return
        }
        
        let fbloginManger: FBSDKLoginManager = FBSDKLoginManager()
        if(!login)
        {
            fbloginManger.logOut()
            if(!Define.useSnsAutoLogout) {
                processLogoutComplete()
            }
        }
        else
        {
            fbloginManger.logIn(withReadPermissions: ["email"], from:vc) {(result, error) -> Void in
                if(error == nil){
                    let fbLoginResult: FBSDKLoginManagerLoginResult  = result!
                    
                    if( result?.isCancelled)!{
                        return
                    }
                    
                    if(fbLoginResult .grantedPermissions.contains("email")){
                        SnsManager.saveFacebookLoginInfo()
                    }
                }
            }
            SnsManager.sharedInstance.loginType = LoginType.Facebook
        }
    }
    
    static func loginNaver(login :Bool)
    {
        NSLog("[loginNaver] login(\(login))")
        
//        guard let vc = AppUtils.getTopViewController() else {
//            return
//        }
        
        let loginInstance = NaverThirdPartyLoginConnection.getSharedInstance()
        
        if(!login)
        {
            
            
            NSLog("[loginNaver] login(false) >> ")
            
            loginInstance?.requestDeleteToken()
            if(!Define.useSnsAutoLogout) {
                processLogoutComplete()
            }
        }
        else
        {
            loginInstance?.isNaverAppOauthEnable = false // 해당 앱에서만, 네이버 앱으로 넘어가지 않고!
            loginInstance?.delegate = SnsManager.sharedInstance
            loginInstance?.requestThirdPartyLogin()
            SnsManager.sharedInstance.loginType = LoginType.Naver
        }
    }
    
    static func loginKakao(login : Bool, trial : Int)
    {
        NSLog("[loginKakao] login(\(login))")
        
        guard let vc = AppUtils.getTopViewController() else {
            return
        }
        
        let session: KOSession = KOSession.shared();
        
        // TRIAL :::::::::::::::::::
        
        if (trial != 2) {
           
            if session.isOpen() {
                session.close()
            }
            if(!Define.useSnsAutoLogout) {
                processLogoutComplete()
            }
        }
        
        // ::::::::::::::::::::::::::
        
        if(!login)
        {
            
            if session.isOpen() {
                session.close()
            }
            if(!Define.useSnsAutoLogout) {
                processLogoutComplete()
            }
        }
        else
        {
            
            SnsManager.sharedInstance.loginType = LoginType.Kakao
            if session.isOpen() {
                SnsManager.sharedInstance.accessToken = session.accessToken ?? ""
                getKakaoLoginInfo(sync: false)
                //processLogoutComplete()
                return
            }
            
            session.open(completionHandler: { (error) -> Void in
                if !session.isOpen() {
                    if let error = error as NSError? {
                        switch error.code {
                        case Int(KOErrorCancelled.rawValue):
                            break
                        default:
                            processLoginFailed()
                            let msg = "카카오 로그인 실패\n\(error.description)"
                            //UIAlertController.showMessage(error.description)
                            let alert = UIAlertController(title: "안내", message: msg, preferredStyle: .alert)
                            alert.addAction(UIAlertAction(title: "확인", style: .default, handler: { action in
                                //AppUtils.openWebView(viewController: viewController, urlString: Define.LOGOUT_URL)
                            }))
                            vc.present(alert, animated: true)
                        }
                    }
                }
            })
        }
    }
    
    static func getLoginInfo_Background() -> SnsLoginInfo?
    {
        switch (SnsManager.sharedInstance.loginType)
        {
        case LoginType.None:
            break
        case LoginType.Mamma:
            return getLoginInfo()
        case LoginType.Naver:
            return getLoginInfo()
        case LoginType.Kakao:
            return getLoginInfo()
        case LoginType.Facebook:
            return getLoginInfo()
        case LoginType.Google:
            return getLoginInfo()
        }
        
        return nil
    }
    
    static func getLoginInfo() -> SnsLoginInfo?
    {
        return SnsManager.sharedInstance.snsLoginInfo
    }
    
    static func getKakaoLoginInfo(sync: Bool) -> SnsLoginInfo?
    {
        NSLog("[getKakaoLoginInfo]::: sync : \(sync)")
        let session: KOSession = KOSession.shared();
        if(session.isOpen())
        {
            NSLog("[getKakaoLoginInfo]::: isOpen")
            // TEMP FOR LOGIN-TEST
            SnsManager.sharedInstance.snsLoginInfo = nil
            if(SnsManager.sharedInstance.snsLoginInfo != nil)
            {
                
                NSLog("[getKakaoLoginInfo]::: snsLoginInfo")
                return SnsManager.sharedInstance.snsLoginInfo
            }
        }
        if(sync) {
            
            NSLog("[getKakaoLoginInfo]:::sync")
            SnsManager.sharedInstance.snsLoginInfo = saveKakaoLoginInfo(sync: sync)
            return SnsManager.sharedInstance.snsLoginInfo
        }
        else {
            return saveKakaoLoginInfo(sync: sync)
        }
    }
    
    static func saveFacebookLoginInfo() -> SnsLoginInfo?
    {
        
        if(FBSDKAccessToken.current() != nil){
            
            
            SnsManager.sharedInstance.accessToken = FBSDKAccessToken.current()?.tokenString ?? ""
            
            FBSDKGraphRequest(graphPath: "me", parameters: ["fields": "id,name , first_name, last_name , email,picture.type(large)"]).start(completionHandler: { (connection, result, error) in
                guard let Info = result as? [String: Any] else
                { return }
                
                if let imageURL = ((Info["picture"] as? [String: Any])?["data"] as? [String: Any])?["url"] as? String {
                    //Download image from imageURL
                }
                print(Info)
                let id = Info["id"]  as! String
                let email = Info["email"]  as! String
                let name = Info["name"]  as! String
                
                //print("result \(email)")
                //print("result \(name)")
                
                if(error != nil){
                    NSLog("[yangs::SU::getFacebookLoginInfo] error(\(error)")
                    return
                }
                
                guard var result: SnsLoginInfo = SnsLoginInfo() else {
                    NSLog("[yangs::SU::getFacebookLoginInfo] error SnsLoginInfo()")
                    return
                }
                result.name = name
                result.has_email = true
                result.email = email
                result.provider = "facebook"
                result.provider_id = id
                result.profile_image = nil
                result.thumbnail_image = nil
                result.is_success = true
                SnsManager.sharedInstance.loginType = LoginType.Facebook
                SnsManager.sharedInstance.snsLoginInfo = result
                SnsManager.processLoginSuccessed()
                NSLog("[yangs::SU::getFacebookLoginInfo] processLoginSuccessed()")
            })
        }
        return nil
    }
    
    static func saveKakaoLoginInfo(sync: Bool) -> SnsLoginInfo?
    {
        NSLog("[saveKakaoLoginInfo]:: sync \(sync)")
        
        let semaphore = DispatchSemaphore(value: 0)
        
        guard var result: SnsLoginInfo = SnsLoginInfo() else {
            NSLog("[saveKakaoLoginInfo] error SnsLoginInfo()")
            return nil
        }
        
        let vc = AppUtils.getTopViewController()
        
        KOSessionTask.userMeTask { [weak vc] (error, me) in
            
            // Email 정보가 이제는 필수가 아니므로 . 로직 변경 (참고 문서 링크)
            // https://devtalk.kakao.com/t/topic/50278
            
            if let error = error as NSError? {
                NSLog("[saveKakaoLoginInfo] error(\(error.description))")
                let alertController = UIAlertController(title: "Alert", message: error.description, preferredStyle: .alert)
                vc?.present(alertController, animated: true, completion: nil)
                if(sync) {
                    semaphore.signal()
                }
                return
            }
            
            
            NSLog("[getSnsLoginInfo_Kakao] [2]")
            NSLog("[getSnsLoginInfo_Kakao] \(me)")
            
            
            guard let me = me as KOUserMe? else {
                NSLog("[getSnsLoginInfo_Kakao] error (KOUserMe)")
                if(sync) {
                    semaphore.signal()
                }
                return
            }
            guard
                let id = me.id,
                let properties = me.properties,
                let name = properties["nickname"],
                let account = me.account,
                let profile_image = properties["profile_image"],
                let thumbnail_image = properties["thumbnail_image"]
                else {
                    NSLog("[getSnsLoginInfo_Kakao] error KOUserMe parse(\(me)")
                    if(sync) {
                        semaphore.signal()
                    }
                    return
            }
            //print("[yangs::SU::getSnsLoginInfo_Kakao] KOUserMe(\(me)")
            
            result.name = name
//            if let email = account.email {
//                result.has_email = true
//                result.email = email
//            }
            
            result.provider = "kakaotalk"
            result.provider_id = id
            result.profile_image = profile_image
            result.thumbnail_image = thumbnail_image
            result.is_success = true
            if(!sync) {
                SnsManager.sharedInstance.snsLoginInfo = result
            }
            SnsManager.sharedInstance.loginType = LoginType.Kakao
        
            NSLog("result.name \(result.name)")
            NSLog("result.provider \(result.provider)")
            NSLog("result.provider_id \(result.provider_id)")
            NSLog("me.id \(me.id)")
            NSLog("me.properties \(me.properties)")
            
            
            
            if(result.is_success) {
                
                
                SnsManager.processLoginSuccessed()
                NSLog("[saveKakaoLoginInfo] processLoginSuccessed()")
            }
            else {
                
                //SnsManager.processLoginFailed()
//                SnsManager.processLoginFailed_NoneEmail()
                NSLog("[saveKakaoLoginInfo] error kakao none email()")
            }
            semaphore.signal()
        }
        if(sync) {
            semaphore.wait()
            if(result.is_success) {
                return result
            }
        }
        return nil
    }
}

extension SnsManager: NaverThirdPartyLoginConnectionDelegate{
    // ---- 3
    func oauth20ConnectionDidOpenInAppBrowser(forOAuth request: URLRequest!) {
        
        //// 네이버 앱이 설치되어있지 않은 경우에 인앱 브라우저로 열리는데 이때 호출되는 함수
        let naverSignInViewController = NLoginThirdPartyOAuth20InAppBrowserViewController(request: request)!
        AppUtils.getTopViewController()?.present(naverSignInViewController, animated: true, completion: nil)
        
    }
    // ---- 4
    func oauth20ConnectionDidFinishRequestACTokenWithAuthCode() {
        //// 로그인이 성공했을 경우
        NSLog("[oauth20ConnectionDidFinishRequestACTokenWithAuthCode] Naver login success (4)")
        
        
        guard let loginConn = NaverThirdPartyLoginConnection.getSharedInstance() else {
            NSLog(" > error end with loginConn")
            return
        }
        guard let tokenType = loginConn.tokenType else {
            NSLog(" > error end with tokenType")
            return
        }
        guard let accessToken = loginConn.accessToken else {
            NSLog(" > error end with accessToken")
            return
        }
        
        SnsManager.sharedInstance.accessToken = accessToken
        
        getNaverEmailFromURL()
    }
    
    // ---- 5
    func oauth20ConnectionDidFinishRequestACTokenWithRefreshToken() {
        //// 로그인이 성공했을 경우
        NSLog("[oauth20ConnectionDidFinishRequestACTokenWithRefreshToken] Naver login success (5)")
        
        
        guard let loginConn = NaverThirdPartyLoginConnection.getSharedInstance() else {
            NSLog(" > error end with loginConn")
            return
        }
        guard let tokenType = loginConn.tokenType else {
            NSLog(" > error end with tokenType")
            return
        }
        guard let accessToken = loginConn.accessToken else {
            NSLog(" > error end with accessToken")
            return
        }
        
        SnsManager.sharedInstance.accessToken = accessToken
        NSLog(" > sharedInstance : \(SnsManager.sharedInstance.accessToken)")
        NSLog(" > accessToken : \(accessToken)")
        
        getNaverEmailFromURL()
    }
    
    // ---- 6
    func oauth20ConnectionDidFinishDeleteToken() {
        NSLog("[oauth20ConnectionDidFinishDeleteToken] (6)")
    }
    
    // ---- 7
    func oauth20Connection(_ oauthConnection: NaverThirdPartyLoginConnection!, didFailWithError error: Error!) {
        NSLog(error.localizedDescription)
        NSLog(error as! String)
    }
    
    // ---- 8
    func getNaverEmailFromURL(){
        
        NSLog("[getNaverEmailFromURL]:::function called.")
        
        guard let loginConn = NaverThirdPartyLoginConnection.getSharedInstance() else {return}
        guard let tokenType = loginConn.tokenType else {return}
        guard let accessToken = loginConn.accessToken else {return}
        
        let authorization = "\(tokenType) \(accessToken)"
        
        SnsManager.sharedInstance.accessToken = accessToken
        
        Alamofire.request("https://openapi.naver.com/v1/nid/me",
                          method: .get, parameters: nil,
                          encoding: JSONEncoding.default,
                          headers: ["Authorization" : authorization]).responseJSON
            {
                (response)
                
                in
                
                NSLog("[getNaverEmailFromURL]:::Alamofire response.")
                guard let value = response.result.value as? [String: Any] else {
                    NSLog(" > error end with value")
                    return
                }
                guard let object = value["response"] as? [String: Any] else {
                    NSLog(" > error end with object")
                    return
                }
                guard let id = object["id"] as? String else {
                    NSLog(" > error end with id")
                    return
                }
                
                // NAVER-DEVELOPER PAGE SETTING NEED..
//                guard let name = object["name"] as? String else {
//                    NSLog(" > error end with name") //
//                    return
//                }
//                NSLog(" > name : \(name)")
//
                guard let email = object["email"] as? String else {
                    NSLog(" > error end with email")
                    return
                }
                
                guard var result: SnsLoginInfo = SnsLoginInfo() else {
                    NSLog(" > error end with result")
                    return
                }
                
                
//                result.name = name
                result.has_email = true
                result.email = email
                result.provider = "naver"
                result.provider_id = id
                result.profile_image = nil
                result.thumbnail_image = nil
                result.is_success = true
                SnsManager.sharedInstance.snsLoginInfo = result
                SnsManager.processLoginSuccessed()
                NSLog(" > [getNaverEmailFromURL] processLoginSuccessed()")
                
        }
    }
}
