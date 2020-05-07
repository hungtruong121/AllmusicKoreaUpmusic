//
//  WebHandler.swift
//  UpmusicTest
//
//  Created by Lihak Kang on 2018. 8. 5..
//  Copyright © 2018년 UPMusic. All rights reserved.
//

import Foundation
import JavaScriptCore
import WebKit
import Alamofire
import SwiftyJSON
import ObjectMapper
import GoogleSignIn
import Social
import StompClientLib


@objc protocol WebExport : JSExport {
    func callbackFromJS(_ jsonData: [String: Any])
}

class WebHandler : NSObject, WebExport, WKScriptMessageHandler
{
    
    let cellId = "cellId"
    let cellId2 = "cellId2"
    var clickedIndex : Int!
    var activityIndicator = UIActivityIndicatorView()
    public var mainUrl: String = ""
    
    // 웹뷰로부터 전달받는 요청 종류
    static let requestLoginDefault = "REQUEST_LOGIN_DEFAULT";
    static let requestLoginFacebook = "REQUEST_LOGIN_FACEBOOK";
    static let requestLoginGoogle = "REQUEST_LOGIN_GOOGLE";
    static let requestLoginKakao = "REQUEST_LOGIN_KAKAO";
    static let requestLoginNaver = "REQUEST_LOGIN_NAVER";
    static let requestPasswordChange = "REQUEST_PASSWORD_CHANGE";
    static let requestPlaylistUpdate = "REQUEST_PLAYLIST_UPDATE";
    static let requestPlayVideo = "REQUEST_PLAY_VIDEO";
    static let requestPauseVideo = "REQUEST_PAUSE_VIDEO";
    static let requestStopVideo = "REQUEST_STOP_VIDEO";
    
    static let requestLogout = "REQUEST_LOGOUT";
    static let requestUrlShare = "REQUEST_URL_SHARE";
    static let requestPlayAudioShow = "REQUEST_PLAY_AUDIO_SHOW";
    static let requestPlayAudioHide = "REQUEST_PLAY_AUDIO_HIDE";
    
    var musicTracks : [MusicTrack] = []
    var musicTrackSelected : [Bool] = []
    var collectionList : [Collection] = []
    var tempTracks : [MusicTrack] = []
    var tempOrigins : [MusicTrack] = []
    var tempSelectedOrigins : [Bool] = []
    
    func callbackFromJS(_ jsonData: [String: Any]) {
        NSLog("[callbackFromJS] : request_type - \(String(describing: jsonData["request_type"]))")
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        
        if let requestType = jsonData["request_type"] as? String {
            
            switch requestType {
            case WebHandler.requestLoginDefault: // 일반 로그인 요청
                let email = jsonData["email"] as? String
                let password = jsonData["password"] as? String
                let session_id = jsonData["session_id"] as? String
                let remember_me = jsonData["remember_me"] as? Bool
                
                let user = jsonData["user"] as! [String:AnyObject]
                
                // TODO 로그인 처리
                NSLog(" > 로그인 처리 :: email : \(jsonData["email"]), password : \(jsonData["password"])")
                NSLog(" > 로그인 처리 :: session_id : \(session_id)")
                NSLog(" > 로그인 처리 :: user[token] : \(user["token"])")
                NSLog(" > 로그인 처리 :: remember_me : \(remember_me)")
                
                
                UserDefaults.standard.set(user["token"], forKey: "TOKEN")
                UserDefaults.standard.set(remember_me, forKey: "rememberMe")
                UserDefaults.standard.set("local", forKey: "loginMethod")
                UserDefaults.standard.set(session_id, forKey: "JSESSIONID")
                UserDefaults.standard.set(email, forKey: "email")
                UserDefaults.standard.set(password, forKey: "password")
                //UserDefaults.standard.string(forKey: "JSESSIONID")
                
                
//                if let json = try? JSONSerialization.data (withJSONObject: jsonData, options: []) {
//                    // here `json` is your JSON data, an array containing the String
//                    // if you need a JSON string instead of data, then do this:

//                    if let content = String(data: json, encoding: String.Encoding.utf8) {
//                        // here `content` is the JSON data decoded as a String
//                        print(content)
//                    }
//                }
                
                NSLog("-----------------------------------------")
//
                requestLocalLogin(email: email ?? ""
                    , password: password ?? ""
                    , completion: {
                        //updateUI
                })
                
                break
            case WebHandler.requestLoginFacebook: // 페이스북 로그인 요청
                // TODO 로그인 처리
                NSLog("로그인 처리 : requestLoginFacebook")
//                // TRY
//                mainController?.facebookLogin()
                UserDefaults.standard.set("facebook", forKey: "loginMethod")
                SnsManager.loginFacebook(login: true)
                
                break
            case WebHandler.requestLoginGoogle: // 구글 로그인 요청
                // TODO 로그인 처리
                NSLog("로그인 처리 : requestLoginGoogle")
                
                UserDefaults.standard.set("google", forKey: "loginMethod")
                SnsManager.loginGoogle(login: true)
//                MainWebViewController().handleCustomGoogleSign()
                
                break
            case WebHandler.requestLoginKakao: // 카카오 로그인 요청
                // TODO 로그인 처리
                UserDefaults.standard.set("kakao", forKey: "loginMethod")
                NSLog("로그인 처리 : requestLoginKakao")
                
                SnsManager.loginKakao(login: true, trial: 1)
                break
            case WebHandler.requestLoginNaver: // 네이버 로그인 요청
                // TODO 로그인 처리
                NSLog("로그인 처리 : requestLoginNaver")
                
                UserDefaults.standard.set("naver", forKey: "loginMethod")
                SnsManager.loginNaver(login: true)
                
                break
            case WebHandler.requestPasswordChange: // 비밀번호 변경 요청
                let newPassword = jsonData["new_password"] as? String
                // TODO 비밀번호 변경
                NSLog("비밀번호 변경 : requestPasswordChange")
                break
                
            // ERROR LOAD URL....
            case WebHandler.requestPlaylistUpdate: // 재생목록 업데이트 요청
                // TODO 서버에서 재생목록 불러오기
                print("재생목록 업데이트 : requestPlaylistUpdate")
                
                // get Temp Tracks with load Music index 0;
                //
//                UserDefaults.standard.set(cookie.value, forKey: "JSESSIONID")
                var JSESSIONID = ""
                var TOKEN = ""
                NSLog("[JSESSIONID] ::: \(UserDefaults.standard.string(forKey: "JSESSIONID"))")
                JSESSIONID = UserDefaults.standard.string(forKey: "JSESSIONID") ?? ""
                TOKEN = UserDefaults.standard.string(forKey: "TOKEN") ?? ""
                
                requestUserOwnTracks(sessionID: JSESSIONID, token: TOKEN)
                requestUserOwnCollectionList(sessionID: JSESSIONID, token: TOKEN)
                
                // ERROR LOAD URL....
            case WebHandler.requestPlayVideo: // 영상 재생 요청
                // DIFF...
//                let jsonVideo = jsonData["video"] as! [String: Any]
//                NSLog("callbackFromJS : video id - \(String(describing: jsonVideo["id"]))")
//                NSLog("callbackFromJS : video subject - \(String(describing: jsonVideo["subject"]))")
//                NSLog("callbackFromJS : video duration - \(String(describing: jsonVideo["duration"]))")
//                NSLog("callbackFromJS : video filename - \(String(describing: jsonVideo["filename"]))")
//                NSLog("callbackFromJS : video bucket key - \(String(describing: jsonVideo["id"]))/\(String(describing: jsonVideo["filename"]))")
//                // TODO 영상 재생
//                NSLog("영상 재생")
    
                break
                
                
            case WebHandler.requestPauseVideo :
                
                NSLog("[ requestPauseVideo ]")
                break
            case WebHandler.requestStopVideo :
                NSLog("[ requestStopVideo ]")
                break
                
                
            case WebHandler.requestLogout :
                // TODO
                HTTPCookieStorage.clear() //로그아웃이나 세션 만료시 저장된 쿠키를 삭제한다.(사이트주소가 바뀐다거나 하면서 쿠키가 누적될수 있다.)
                
//                if(self.musicTracks.count == 0) {
                    //                        set_up_web_view_as_normalmode()
                    NotificationCenter.default.post(name: NSNotification.Name(rawValue: "webViewPlayerEmpty"), object: nil)
//                } else {
//                    NotificationCenter.default.post(name: NSNotification.Name(rawValue: "webViewPlayerSize"), object: nil)
//                }
//
                break
            
            case WebHandler.requestUrlShare:
                /*
                 SNS 공유
                 웹뷰에서 앱에 전달하는 메시지는 request_type, sns, url_to_share 입니다.
                 request_type :  'REQUEST_URL_SHARE',
                 sns : 'facebook' 또는 'twitter', 'kakao', 'instagram', 'naver'
                 url_to_share : 공유할 url
                 
                 */
                
                let provider = jsonData["sns"] as! String
                let url_to_share = jsonData["url_to_share"] as! String
                
                NSLog(" > SNS 공유 : provider - \(provider), url_to_share - \(url_to_share)")
                
                UserDefaults.standard.set(provider, forKey: "shareProvider")
                UserDefaults.standard.set(url_to_share, forKey: "shareUrl")
                NotificationCenter.default.post(name: NSNotification.Name(rawValue: "shareToProvider"), object: nil)
                
                break
                
            case WebHandler.requestPlayAudioShow :
                
                NSLog(" > [ requestPlayAudioShow ]")
                NotificationCenter.default.post(name: NSNotification.Name(rawValue: "webViewPlayerSize"), object: nil)
                
                mainController?.mediaPlayerDetailsView.isHidden = false
                break
            case WebHandler.requestPlayAudioHide :
                
                NSLog(" > [ requestPlayAudioHide ]")
                NotificationCenter.default.post(name: NSNotification.Name(rawValue: "webViewNormalSize"), object: nil)
                
                let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
                mainController?.mediaPlayerDetailsView.isHidden = true
                
                
                
                break
                
            default:
                NSLog("매치되는 요청을 찾을 수 없음")
                break
            }
            
        }
    }
    
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        
        print("[userContentController]::: didReceive message :: \(message.body)")
        NSLog("[userContentController]::: didReceive")
        
        if (message.name == "callbackFromJS") {
//            NSLog("[callbackFromJS] : \(message.body)")
            NSLog("[callbackFromJS]:::called \(message.body)")
            self.callbackFromJS(convertToDictionary(jsonString: message.body as! String)!)
            
        }
        
        NotificationCenter.default.addObserver(self
            , selector: #selector(fetch_user_own_tracks)
            , name: NSNotification.Name(rawValue: "fetchUserOwnTracks")
            , object: nil)
        
        NotificationCenter.default.addObserver(self
            , selector: #selector(fetch_user_own_tracks_when_login)
            , name: NSNotification.Name(rawValue: "fetchUserOwnTracksWhenLogin")
            , object: nil)
        
        NotificationCenter.default.addObserver(self
            , selector: #selector(bottom_menu_down)
            , name: NSNotification.Name(rawValue: "bottomMenuDown")
            , object: nil)
        NotificationCenter.default.addObserver(self
            , selector: #selector(bottom_menu_delete)
            , name: NSNotification.Name(rawValue: "bottomMenuDelete")
            , object: nil)
        
        NotificationCenter.default.addObserver(self
            , selector: #selector(sub_dialog_clicked)
            , name: NSNotification.Name(rawValue: "subDialogClicked")
            , object: nil)
        NotificationCenter.default.addObserver(self
            , selector: #selector(edit_clicked)
            , name: NSNotification.Name(rawValue: "editClicked")
            , object: nil)
        
    
    }
    
    
    @objc func fetch_user_own_tracks() {
        
        var JSESSIONID = ""
        var TOKEN = ""
        JSESSIONID = UserDefaults.standard.string(forKey: "JSESSIONID") ?? ""
        TOKEN = UserDefaults.standard.string(forKey: "TOKEN") ?? ""
        requestUserOwnTracks(sessionID: JSESSIONID, token: TOKEN)
    }
    
    
    @objc func fetch_user_own_tracks_when_login() {
        
        var JSESSIONID = ""
        var TOKEN = ""
        JSESSIONID = UserDefaults.standard.string(forKey: "JSESSIONID") ?? ""
        TOKEN = UserDefaults.standard.string(forKey: "TOKEN") ?? ""
        requestUserOwnTracksWhenLogin(sessionID: JSESSIONID, token: TOKEN)
    }
    
    
    
    @objc func bottom_menu_down() {
        NSLog("[WebHandler] [bottom_menu_down]")
        
//        let JSESSIONID = UserDefaults.standard.string(forKey: "JSESSIONID") ?? ""
//        requestUserOwnCollectionList(sessionID:JSESSIONID , token: "") //
//        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "dialogView1"), object: nil)
        
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        mainController?.mediaPlayerDetailsView.dialogListContainer.isHidden = false
        
    }
    
    
    @objc func sub_dialog_clicked() {
        
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        NSLog("[WebHandler] [sub_dialog_clicked] subject : \(mainController?.mediaPlayerDetailsView.subDialogTextField.text)")
        
        let subjectText = mainController?.mediaPlayerDetailsView.subDialogTextField.text ?? ""
        var JSESSIONID = ""
        var TOKEN = ""
        NSLog("[JSESSIONID] ::: \(UserDefaults.standard.string(forKey: "JSESSIONID"))")
        JSESSIONID = UserDefaults.standard.string(forKey: "JSESSIONID") ?? ""
        TOKEN = UserDefaults.standard.string(forKey: "TOKEN") ?? ""
        
        if (subjectText == "") {
            return
        }
        
        if (subjectText == nil ) {
            return
        }
        
        requestUserCollectionCreate(sessionID: JSESSIONID, token: TOKEN, subjectValue: subjectText)
        
    }
    
    
    
    @objc func bottom_menu_delete() {
        NSLog("[WebHandler] [bottom_menu_delete]")
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
//        NSLog("[WebHandler] [bottom_menu_delete] > : \()")
        
        tempTracks.removeAll()
        for index in 0...self.musicTrackSelected.count-1 {
            
            if (self.musicTrackSelected[index] == false) {
                tempTracks.append(self.musicTracks[index]) // error sometimes?
                
            }
        }
        
        mainController?.mediaPlayerDetailsView.BottomMoreDialogViewContrainer.isHidden = true
        mainController?.mediaPlayerDetailsView.bottomMenu.isHidden = true
        
        self.musicTracks = tempTracks
        self.musicTrackSelected = [Bool](repeating: false, count: self.musicTracks.count)
        UserDefaults.standard.set(true ,forKey: "isEdited")
        
        NSLog("[WebHandler] > musicTrackSelected : \(musicTrackSelected)")
        mainController?.mediaPlayerDetailsView.playlistTableView.reloadData()
        
//        TOAST(view: , string: "변경 적용하려면 오른쪽 상단의 편집완료 버튼을 눌러주세요.")
    }
    
    @objc func edit_clicked() {
        NSLog("[WebHandler] [edit_clicked]")
        
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        
        if (mainController?.mediaPlayerDetailsView.playlistTableView.isEditing == false) {
            
            UserDefaults.standard.set(false ,forKey: "isEdited")
            self.tempOrigins = self.musicTracks
            self.tempSelectedOrigins = self.musicTrackSelected
            
            mainController?.mediaPlayerDetailsView.playlistTableView.isEditing = true
            UserDefaults.standard.set(true ,forKey: "isEditing")
//            mainController?.mediaPlayerDetailsView.EditCancelButton.isHidden = false
            
            mainController?.mediaPlayerDetailsView.EditCancelButton
                .addTarget(self, action: #selector(editCancelButtonClicked(sender:)), for: .touchUpInside)
            mainController?.mediaPlayerDetailsView.hidePlaylistButton
            .addTarget(self, action: #selector(editCancelButtonClicked(sender:)), for: .touchUpInside)
            
            mainController?.mediaPlayerDetailsView.EditButton.backgroundColor = UIColor(
                red: 224.0/255.0, green: 225.0/255.0, blue: 227.0/255.0, alpha: 1.0
            )
            
            mainController?.mediaPlayerDetailsView.EditButton.setTitle("완료", for: UIControlState.normal)
            mainController?.mediaPlayerDetailsView.bottomMenu.isHidden = true
            mainController?.mediaPlayerDetailsView.playlistTableView.reloadData()
            
        } else {
            
            let isEdited =
                UserDefaults.standard.bool(forKey: "isEdited")
            
            NSLog("result(userDefaults) isEdited : \(isEdited)")
            if (isEdited == false) {
                // 변경사항 없음
                NSLog("[no-edited]")
                //TOAST(view: view, string: "변경 사항이 없습니다.")
            }
            
            if (isEdited == true) {
                // 변경사항 적용
                NSLog("[edited]")
                //TOAST(view: view, string: "변경 사항이 없습니다.")
                requestUpdatePlaylist()
            }
            
            UserDefaults.standard.set(false ,forKey: "isEdited")
            
            mainController?.mediaPlayerDetailsView.bottomMenu.isHidden = true
            mainController?.mediaPlayerDetailsView.EditCancelButton.isHidden = true
            mainController?.mediaPlayerDetailsView.playlistTableView.isEditing = false
            UserDefaults.standard.set(false, forKey: "isEditing")
            mainController?.mediaPlayerDetailsView.EditButton.backgroundColor = UIColor(
                red: 69.0/255.0, green: 76.0/255.0, blue: 104.0/255.0, alpha: 1.0
            )
            
            mainController?.mediaPlayerDetailsView.EditButton.setTitle("편집", for: UIControlState.normal)
            
            mainController?.mediaPlayerDetailsView.playlistTableView.reloadData()
            
            
        }
        
        
    }
    
    
    
    @objc func requestUpdatePlaylist() {
        print("[requestEdit] function called")
        //params의 예 {"ids":[123456,789012]}
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        let apiSuffix = "/api/player/update_list"
        
        NSLog("apiSuffix : \(apiSuffix)")
        
        let headers: HTTPHeaders = [
            //            "JSESSIONID" : sessionID, // NOT WORKING -> ONLY INTERCEPTOR
            "Accept": "application/json",
            "Content-Type" : "application/json; charset=utf-8"
        ]
        
        // PARAM : Long Array  {"ids":[123456,789012]}
        
        var idArray: [Int64] = []
        
        for index in 0...musicTracks.count-1 {
            idArray.append(musicTracks[index].id!)
        }
        
        let parameters = [
            "ids" : idArray
        ]
        
        print(" > parameter : \(parameters)" )
        
        let URL = (upmusicUrlNoStatic + apiSuffix)
        Alamofire.request(URL, method: .post
            , parameters: parameters
            , encoding: JSONEncoding.default
            , headers: headers
            )
            .responseJSON { response in
                switch response.result {
                    
                case .success(let value):
                    let json = JSON(value)
                    if (json["status"].description == "true") {
                        
                        NSLog("[TRUE] JSON: \(json["message"].description)")
                        NSLog("[TRUE] JSON: \(json["status"].description)")
//                        NSLog("[TRUE] JSON: \(json["object"].description)")
                        
                        //TOAST(view: self.view, string: "플레이리스트를 업데이트합니다.")
                    }
                    
                    if (json["status"].description == "false") {
                        // TODO 실패 표시
                        
                        UserDefaults.standard.set("서버 연결에 실패했습니다.", forKey: "toastMessage")
                        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "toastLongMessage"), object: nil)
                        //TOAST(view: self.view, string: "서버 연결에 실패했습니다.")
                    }
                    
                case .failure(let error):
                    print(error)
                }
        }
    }
    
    func convertToDictionary(jsonString: String) -> [String: Any]? {
        if let data = jsonString.data(using: .utf8) {
            do {
                return try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any]
            } catch {
                print(error.localizedDescription)
            }
        }
        return nil
    }
    
    // NULL : JUST FOR REFERENCE
    func requestForm() {
        
        let apiSuffix = "/..."
        let headers: HTTPHeaders = [
            //            "JSESSIONID" : sessionID, // NOT WORKING -> ONLY INTERCEPTOR
            "Accept": "application/json",
            "Content-Type" : "application/json; charset=utf-8"
        ]
        
//        let parameters = [
//            "token" : tokenValue
//        ]
        
        let URL = (upmusicUrlNoStatic + apiSuffix)
        Alamofire.request(URL, method: .post
//            , parameters: parameters
            , encoding: JSONEncoding.default
            , headers: headers
            )
            .responseJSON { response in
                switch response.result {
                    
                case .success(let value):
                    let json = JSON(value)
                    if (json["status"].description == "true") {
                        
                        NSLog("[TRUE] JSON: \(json["message"].description)")
                        
                    }
                    
                    if (json["status"].description == "false") {
                        
                        NSLog("[FALSE] JSON: \(json)")
                    }
                    
                case .failure(let error):
                    print(error)
                }
        }
    }
    
    
    
    func requestLocalLogin(email:String, password: String, completion : @escaping ()->()) {
        
        NSLog("-----------------------------------------")
        NSLog("[requestLocalLogin]:::START")
        
        let urlString = upmusicUrlNoStatic + "/api/auth/login"
        
        let parameters = [
            "email": email,
            "password": password
        ]
        
        let headers: HTTPHeaders = [
            "Accept": "application/json",
            "Content-Type" : "application/json; charset=utf-8"
        ]
        
        NSLog("[urlString] : \(urlString)")
        NSLog("[email] : \(email), [password] : \(password)")
        NSLog("[parameters] : \(parameters)")
        
        NSLog("[requestLocalLogin]:::[step]::[1]")
        let URL = (urlString) //.toSecureHTTPSofDomainChange()
        Alamofire.request(URL, method: .post
            , parameters: parameters
            , encoding: JSONEncoding.default
            , headers: headers
            ).responseJSON { response in
                
                NSLog("[requestLocalLogin]:::[step]::[2]")
                // [ ALAMOFIRE COOKIE RETRIEVE ]
                if let headerFields = response.response?.allHeaderFields as? [String: String],let URL = response.request?.url
                {
                    
                    
//                    let ds = WKWebsiteDataStore.default()
//                    let cookies = ds.httpCookieStore
                    
                    let cookies = HTTPCookie.cookies(withResponseHeaderFields: headerFields, for: URL)
                    
                    var cookieDict = [String : AnyObject]()
                    for cookie in cookies {
                        NSLog("[Alamofire cookie]: \(cookie.name)")
                        cookieDict[cookie.name] = cookie.properties as AnyObject?
                        
                        if (cookie.name == "JSESSIONID") {
                            NSLog(" > [JSESSIONID] : \(cookie.value)")
                            UserDefaults.standard.set(cookie.value, forKey: "JSESSIONID")
                        }
//                        if (cookie.name == "upmusic-remember-me-cookie") {
//                            NSLog(" > [upmusic-remember-me-cookie] : \(cookie.value)")
//                        }
                        
                    }
                    
                    UserDefaults.standard.set(cookieDict, forKey: "cookiez")
                    
                    NSLog("-----------------------------------------")
                }
                
                NSLog("[requestLocalLogin]:::[step]::[3]")
                switch response.result {
                    
                case .success(let value):
                    let json = JSON(value)
                    if (json["status"].description == "true") {
                        
                        NSLog("[TRUE] JSON: \(json["message"].description)")
                        
                    }
                    
                    if (json["status"].description == "false") {
                        
                        NSLog("[FALSE] JSON: \(json)")
                    }
                    
                    NSLog("[requestLocalLogin]:::[step]::[4]")
                    NSLog("[requestLocalLogin]:::[step]::[5]:[END]")
                    NSLog("-----------------------------------------")
                    completion()
                    
                    
                case .failure(let error):
                    
                    print(error)
                    //TOAST(view: <#T##UIView#>, string: "서버 연결에 실패했습니다.")
                    completion()
                }
        }
        
        
        
    }
//
//    fileprivate func
    fileprivate func requestUserOwnCollectionList(sessionID: String, token: String) {
        ///api/collection
        
        
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        
        let apiSuffix = "/api/collection"
        let headers: HTTPHeaders = [
            //            "JSESSIONID" : sessionID, // NOT WORKING -> ONLY INTERCEPTOR
            "Accept": "application/json",
            "Content-Type" : "application/json; charset=utf-8"
        ]
        
        let tokenValue = token
        
        let parameters = [
            "token" : tokenValue
        ]
        
        let URL = (upmusicUrlNoStatic + apiSuffix)
        Alamofire.request(URL, method: .get
//            , parameters: parameters
            , encoding: JSONEncoding.default
            , headers: headers
            )
            .responseJSON { response in
                switch response.result {
                    
                case .success(let value):
                    let json = JSON(value)
                    if (json["status"].description == "true") {
                        
                        NSLog("[TRUE] JSON message: \(json["message"].description)")
                        NSLog("[TRUE] JSON status: \(json["status"].description)")
//                        NSLog("[TRUE] JSON object: \(json["object"].description)")
                        
                        NSLog("[TRUE] JSON object: \(json["object"].description)")
                        
                        self.musicTracks.removeAll()
                        NSLog("[TRUE] JSON object: [0]")
                        
                        if (json["object"].array!.count != 0 ) {
                            NSLog("[TRUE] JSON object: [0-1]")
                            for index in 0...json["object"].array!.count-1 {
                                
                                NSLog("[TRUE] JSON object: [0-2]")
                                //                    print("OBJECT \(index) :::: \(json["object"].array![index].description)")
                                let collection = Collection(JSONString: json["object"].array![index].description )
                                //                    print("TRACK \(index) :::: \(track?.artistNick)")
                                self.collectionList.append(collection!)
                            }
                            
    //                        print("> [collectionList] : \n\(self.collectionList.toJSONString())" )
                            
                            mainController?.mediaPlayerDetailsView.dialogListTableView.delegate = self
                            mainController?.mediaPlayerDetailsView.dialogListTableView.dataSource = self
                            mainController?.mediaPlayerDetailsView.dialogListTableView.reloadData()
                            NSLog("[TRUE] JSON object: [0-3]")
                        }
                        
                    }
                    
                    if (json["status"].description == "false") {
                        
                        NSLog("[FALSE] JSON: \(json)")
                        //TOAST(view: <#T##UIView#>, string: "서버 연결에 실패했습니다.")
                    }
                    
                case .failure(let error):
                    print(error)
                }
        }
    }
    
    fileprivate func requestUserCollectionCreate(sessionID : String, token: String, subjectValue: String) {
        ///api/collection/create
        // {"subject" : value}
        
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        
        let apiSuffix = "/api/collection/create"
        let headers: HTTPHeaders = [
            //            "JSESSIONID" : sessionID, // NOT WORKING -> ONLY INTERCEPTOR
            "Accept": "application/json",
            "Content-Type" : "application/json; charset=utf-8"
        ]
        
        
        let parameters = [
            "subject" : subjectValue
        ]
        
        let URL = (upmusicUrlNoStatic + apiSuffix)
        Alamofire.request(URL, method: .post
            , parameters: parameters
            , encoding: JSONEncoding.default
            , headers: headers
            )
            .responseJSON { response in
                switch response.result {
                    
                case .success(let value):
                    let json = JSON(value)
                    if (json["status"].description == "true") {
                        
                        NSLog("[TRUE] JSON: message \(json["message"].description)")
                        NSLog("[TRUE] JSON: status \(json["status"].description)")
//                        NSLog("[TRUE] JSON: object \(json["object"].description)")
                        
                        
//                        mainController?.mediaPlayerDetailsView.dialogListContainer.isHidden = true
                        mainController?.mediaPlayerDetailsView.subDialogTextField.endEditing(true)
                        mainController?.mediaPlayerDetailsView.subDialogContainer.isHidden = true
                        
                        let collection = Collection(JSONString: json["object"].description )
                        
                        NSLog(" > [new Collection] \(collection?.toJSONString())")
                        
                        self.collectionList.insert(collection!, at: 0)
                        mainController?.mediaPlayerDetailsView.dialogListTableView.reloadData()
                        
                        //TOAST(view: <#T##UIView#>, string: "새로운 플레이리스트를 만들었습니다.")
                        
                    }
                    
                    if (json["status"].description == "false") {
                        
                        NSLog("[FALSE] JSON: \(json)")
                        //TOAST(view: <#T##UIView#>, string: "서버 연결에 실패했습니다.")
                    }
                    
                case .failure(let error):
                    print(error)
                }
        }
    }
    
    fileprivate func requestUserCollectionTrackAddingByID(sessionID: String, token: String, idValue: Int64) {
        ///api/collection/{id}/add_tracks
        
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        let apiSuffix = "/api/collection/\(idValue)/add_tracks"
        
        NSLog("apiSuffix : \(apiSuffix)")
        
        let headers: HTTPHeaders = [
            //            "JSESSIONID" : sessionID, // NOT WORKING -> ONLY INTERCEPTOR
            "Accept": "application/json",
            "Content-Type" : "application/json; charset=utf-8"
        ]
        
        // PARAM : Long Array  {"ids":[123456,789012]}
        
        var idArray: [Int64] = []
        
        let fromWhichController = UserDefaults.standard.string(forKey: "fromWhichController")
        
        if (fromWhichController == "MainController") {
            idArray.append(mainController?.mediaPlayerDetailsView.track.id! ?? 0)
        } else {
            if (UserDefaults.standard.bool(forKey: "isSingleSelection") == true) {
                idArray.append(Int64(UserDefaults.standard.integer(forKey: "selectedId")))
            } else {
                for index in 0...musicTrackSelected.count-1 {
                    
                    if(musicTrackSelected[index] == true) {
                        idArray.append(musicTracks[index].id!)
                    }
                }
            }
            
        }
        
        let parameters = [
            "ids" : idArray
        ]
        
        print(" > parameter : \(parameters)" )
        
        
        let URL = (upmusicUrlNoStatic + apiSuffix)
        Alamofire.request(URL, method: .post
            , parameters: parameters
            , encoding: JSONEncoding.default
            , headers: headers
            )
            .responseJSON { response in
                switch response.result {
                    
                case .success(let value):
                    let json = JSON(value)
                    if (json["status"].description == "true") {
                        
                        NSLog("[TRUE] JSON: \(json["message"].description)")
                        
                        mainController?.mediaPlayerDetailsView.dialogListContainer.isHidden = true
                        mainController?.mediaPlayerDetailsView.BottomMoreDialogViewContrainer.isHidden = true
                        
                        //TOAST(view: <#T##UIView#>, string: "정상적으로 추가했습니다." )
                        
                    }
                    
                    if (json["status"].description == "false") {
                        
                        NSLog("[FALSE] JSON: \(json)")
                        //TOAST(view: <#T##UIView#>, string: "서버 연결에 실패했습니다.")
                        
                    }
                    
                case .failure(let error):
                    print(error)
                }
        }
    }
    
    
    fileprivate func requestUserOwnTracksWhenLogin(sessionID : String, token: String) {
        
        print(" >> requestUserOwnTracksWhenFirstLogin >> ")
        
        let headers: HTTPHeaders = [
            //            "JSESSIONID" : sessionID, // NOT WORKING -> ONLY INTERCEPTOR
            "Accept": "application/json",
            "Content-Type" : "application/json; charset=utf-8"
        ]
        
        let tokenValue = token
        
        let parameters = [
            "token" : tokenValue
        ]
        
        let URL = (upmusicUrlNoStatic + "/api/player/playlist") //.toSecureHTTPSofDomainChange()
        Alamofire.request(URL, method: .post
            , parameters: parameters
            , encoding: JSONEncoding.default
            , headers: headers
            )
            .responseJSON { response in
                switch response.result {
                case .success(let value):
                    let json = JSON(value)
                    if (json["status"].description == "true") {
                        self.musicTracks.removeAll()
                        
                        if (json["object"].array!.count != 0 ) {
                            
                            for index in 0...json["object"].array!.count-1 {
                                let track = MusicTrack(JSONString: json["object"].array![index].description)
                                
                                print(" > rUOT : TRACK \(index) :::: \(String(describing: track?.artistNick))")
                                self.musicTracks.append(track!)
                            }
                            
                            self.musicTrackSelected = [Bool](repeating: false, count: json["object"].array!.count)
                            
                            let track = self.musicTracks[0]
                            let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
                            
                            let isFirstLoad = UserDefaults.standard.string(forKey: "isFirstLoad") ?? "true"
                            print("[0] requestUserOwnTracksWhenFirstLogin isFirstLoad ::: \(String(describing: isFirstLoad))")
                            
                            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "registerSocket"), object: nil)
                            
                            mainController?.peekPlayerDetails(track: track, playlistTracks: self.musicTracks, noPlay: true)
                            
                            mainController?.mediaPlayerDetailsView.originPlaylist = self.musicTracks
                            
                            mainController?.mediaPlayerDetailsView.playlistTableView.dataSource = self as UITableViewDataSource
                            mainController?.mediaPlayerDetailsView.playlistTableView.delegate = self as UITableViewDelegate
                            mainController?.mediaPlayerDetailsView.playlistTableView.reloadData()
                            
                            //TOAST(view: <#T##UIView#>, string: "[재생] 목록을 갱신합니다.")
                            
                        }
                        
                        
                        if (json["object"].array!.count == 0 ) {
                            
                            //TOAST(view: <#T##UIView#>, string: "[재생] 목록에 곡이 없습니다. ")
                        }
                        
                        if(self.musicTracks.count == 0) {
                            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "webViewPlayerEmpty"), object: nil)
                        } else {
                            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "webViewPlayerSize"), object: nil)
                        }
                        
                    }
                    
                    if (json["status"].description == "false") {
                        NSLog("[FAILED]::JSON: \(json)")
                    }
                    
                case .failure(let error):
                    print(error)
                }
                
        }
        
    }
    
    
    
    fileprivate func requestUserOwnTracks(sessionID : String, token: String) {
        
        print(" >> requestUserOwnTracks >> ")
        
        let headers: HTTPHeaders = [
//            "JSESSIONID" : sessionID, // NOT WORKING -> ONLY INTERCEPTOR
            "Accept": "application/json",
            "Content-Type" : "application/json; charset=utf-8"
        ]
        
        let tokenValue = token
        
        let parameters = [
            "token" : tokenValue
        ]
        
        let URL = (upmusicUrlNoStatic + "/api/player/playlist") //.toSecureHTTPSofDomainChange()
        Alamofire.request(URL, method: .post
            , parameters: parameters
            , encoding: JSONEncoding.default
            , headers: headers
            )
            .responseJSON { response in
            switch response.result {
            case .success(let value):
                let json = JSON(value)
                if (json["status"].description == "true") {
                    self.musicTracks.removeAll()
                    
                    if (json["object"].array!.count != 0 ) {
                        
                        for index in 0...json["object"].array!.count-1 {
                            let track = MusicTrack(JSONString: json["object"].array![index].description)
                            
                            print(" > rUOT : TRACK \(index) :::: \(String(describing: track?.artistNick))")
                            self.musicTracks.append(track!)
                        }
                        
                        
                        self.musicTrackSelected = [Bool](repeating: false, count: json["object"].array!.count)
                        
                        let track = self.musicTracks[0]
                        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
                        
                        // TODO
//                        mainController?.peekPlayerDetails(track: track, playlistTracks: self.musicTracks)
                        
                        let isFirstLoad = UserDefaults.standard.string(forKey: "isFirstLoad") ?? "true"
                        print("[0] requestUserOwnTracks isFirstLoad ::: \(String(describing: isFirstLoad))")
                        
                        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "registerSocket"), object: nil)
//                        if (String(describing: isFirstLoad) == "true") {
//
//                            print("[1] requestUserOwnTracks isFirstLoad ::: \(String(describing: isFirstLoad))")
//                            mainController?.peekPlayerDetails(track: track, playlistTracks: self.musicTracks)
//                        } else {
                            print("[2] requestUserOwnTracks isFirstLoad ::: \(String(describing: isFirstLoad))")
                            // TODO
                            mainController?.peekPlayerDetails(track: track, playlistTracks: self.musicTracks)
//                        }
                        
                        mainController?.mediaPlayerDetailsView.originPlaylist = self.musicTracks
                        
                        mainController?.mediaPlayerDetailsView.playlistTableView.dataSource = self as UITableViewDataSource
                        mainController?.mediaPlayerDetailsView.playlistTableView.delegate = self as UITableViewDelegate
                        mainController?.mediaPlayerDetailsView.playlistTableView.reloadData()
                        //TOAST(view: <#T##UIView#>, string: "[재생] 목록을 갱신합니다.")
                        
                        
                        
                    }
                    
                    
                    if (json["object"].array!.count == 0 ) {
                        //TOAST(view: <#T##UIView#>, string: "[재생] 목록에 곡이 없습니다. ")
                    }
                    
                    if(self.musicTracks.count == 0) {
                        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "webViewPlayerEmpty"), object: nil)
                    } else {
                        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "webViewPlayerSize"), object: nil)
                    }
                    
                }
                
                if (json["status"].description == "false") {
                    NSLog("[FAILED]::JSON: \(json)")
                }
                
            case .failure(let error):
                print(error)
            }
 
        }
        
    }
    
    
    
    @objc func requestHeart() {
        
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        
        let selectedId = UserDefaults.standard.integer(forKey: "selectedId")
       
        NSLog("-----------------------------------------")
        let URL = upmusicUrlNoStatic
            + "/api/track/" + String(describing: selectedId) + "/like" //.toSecureHTTPSofDomainChange()
        let URLinner = upmusicUrlNoStatic
            + "/api/track/" + String(describing: selectedId)
        
        
        let headers: HTTPHeaders = [
            "Accept": "application/json",
            "Content-Type" : "application/json; charset=utf-8"
        ]
        
        Alamofire.request(URL, method: .post
            //            , parameters: parameters
            , encoding: JSONEncoding.default
            , headers: headers
            ).responseJSON { response in
                //
                //                NSLog("[rCS] > \(response.result)")
                //                NSLog("[rCS] > \(response.result.value)")
                //                NSLog("-----------------------------------------")
                //
                switch response.result {
                    
                case .success(let value):
                    // INNER FUNCTION
                    Alamofire.request(URLinner, method: .get
                        ).responseJSON { responseInner in
                            
                            switch responseInner.result {
                                
                            case .success(let valueInner):
                                
                                let json = JSON(value) // OUTER
                                let jsonInner = JSON(valueInner)
                                let temp = MusicTrack(JSONString: jsonInner.description)
                                
                                
                                if (temp != nil) {
                                    
                                    self.musicTracks[UserDefaults.standard.integer(forKey: "selectedRow")].heartCnt = temp!.heartCnt!
//                                    mainController.mediaPlayerDetailsView.HeartCntLabel.text =  String(describing: temp!.heartCnt!)
//                                    mainController.mediaPlayerDetailsView.track.heartCnt = temp!.heartCnt!
                                    
                                    if mainController?.mediaPlayerDetailsView.track.id == temp?.id {
                                        mainController?.mediaPlayerDetailsView.track.heartCnt = temp!.heartCnt!
                                        mainController?.mediaPlayerDetailsView.HeartCntLabel.text = String(describing: temp!.heartCnt!)
                                        
                                    }
                                    
                                    if (json["status"].description == "true") {
                                        if (json["message"].description == "true") {// true => 좋아요 입력
                                            
                                            mainController?.mediaPlayerDetailsView.track.liked = true
                                            mainController?.mediaPlayerDetailsView.HeartButton.setImage(#imageLiteral(resourceName: "playerLikeOn"), for: .normal)
                                            NSLog(" > status true : \(json["message"].description)")
                                            
                                        }
                                        
                                        if (json["message"].description == "false") {// false => 좋아요 취소
                                            mainController?.mediaPlayerDetailsView.track.liked = false
                                            mainController?.mediaPlayerDetailsView.HeartButton.setImage(#imageLiteral(resourceName: "tabLikeOff"), for: .normal)
                                            NSLog(" > status false : \(json)")
                                        }
                                    }
                                    
                                    if (json["status"].description == "false") {
                                        
                                    }
                                    
                                    
                                }
                                
                                
                                if (json["status"].description == "true") {
                                    if (json["message"].description == "true") {// true => 좋아요 입력
                                        
                                        self.musicTracks[UserDefaults.standard.integer(forKey: "selectedRow")].liked = true
                                        mainController?.mediaPlayerDetailsView.BottomMoreDialogRow4_Image.image = #imageLiteral(resourceName: "playerLikeOn")
                                        mainController?.mediaPlayerDetailsView.BottomMoreDialogRow4_Label.text = "좋아요 취소"
                                        NSLog(" > status true : \(json["message"].description)")
                                        
                                    }
                                    
                                    if (json["message"].description == "false") {// false => 좋아요 취소
                                        self.musicTracks[UserDefaults.standard.integer(forKey: "selectedRow")].liked = false
                                        mainController?.mediaPlayerDetailsView.BottomMoreDialogRow4_Image.image = #imageLiteral(resourceName: "tabLikeOff")
                                        mainController?.mediaPlayerDetailsView.BottomMoreDialogRow4_Label.text = "좋아요"
                                        NSLog(" > status false : \(json)")
                                    }
                                }
                                
                                if (json["status"].description == "false") {
                                    
                                }
                                
                                
                            case .failure(let errorInner):
                                
                                print(errorInner)
                            }
                    }
                    
                    
                case .failure(let error):
                    
                    print(error)
                }
        }
        
        
    }
    
    
}

extension WebHandler : UITableViewDataSource, UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        if (tableView == mainController?.mediaPlayerDetailsView.dialogListTableView) {
            return collectionList.count
        }
        
        return musicTracks.count
    }
    
//
//    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
//
//        let cell = tableView.dequeueReusableCell(withIdentifier: cellId, for: indexPath) as! MusicTrackCell
//
//        cell.isSelected = !cell.isSelected
//
//        if( cell.isSelected ) {
//            cell.contentView.backgroundColor = UIColor(red: 236.0/255.0, green: 240.0/255.0, blue: 242.0/255.0, alpha: 1.0)
//        } else {
//            cell.contentView.backgroundColor = UIColor(red: 1.0, green: 1.0, blue: 1.0, alpha: 1.0)
//        }
//
//        return
//    }
//
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        if (tableView == mainController?.mediaPlayerDetailsView.dialogListTableView) {
            let cell = tableView.dequeueReusableCell(withIdentifier: cellId2, for: indexPath) as! StringCell
            let collection = collectionList[indexPath.row]
            cell.collection = collection
            
            cell.contentView.tag = indexPath.row
            let tap = UITapGestureRecognizer(target: self, action: #selector(self.collectionItemClickedAsGesture(sender:)))
            tap.numberOfTapsRequired = 1
            
            cell.contentView.isUserInteractionEnabled = true
            cell.contentView.addGestureRecognizer(tap)
            return cell
        }
        
        let cell = tableView.dequeueReusableCell(withIdentifier: cellId, for: indexPath) as! MusicTrackCell
        
        print("[tableView] indexPath.row \(indexPath.row)")
//        let track = musicTracks[indexPath.row]
        
        
        let isEditing = mainController?.mediaPlayerDetailsView.playlistTableView.isEditing ?? false
        
        // reload 후 반영 됨.
        if( musicTrackSelected[indexPath.row] == true) {
            cell.contentView.backgroundColor = UIColor(red: 236.0/255.0, green: 240.0/255.0, blue: 242.0/255.0, alpha: 1.0)
        } else {
            cell.contentView.backgroundColor = UIColor(red: 1.0, green: 1.0, blue: 1.0, alpha: 1.0)
        }
        
        // reload 후 반영 됨.
        if(isEditing) {
            
        } else {
            // 전부 선택하지 않도록
            musicTrackSelected[indexPath.row] = false
            cell.contentView.backgroundColor = UIColor(red: 1.0, green: 1.0, blue: 1.0, alpha: 1.0)
        }
        
        cell.contentView.tag = indexPath.row
        cell.artistNameLabel.tag = indexPath.row
        cell.podcastImageView.tag = indexPath.row
        cell.trackNameLabel.tag = indexPath.row
        
        cell.MoreButton.tag = indexPath.row
        cell.PlayButton.tag = indexPath.row
        cell.RemoveButton.tag = indexPath.row
        
        cell.track = musicTracks[indexPath.row] //track
        
        let tap = UITapGestureRecognizer(target: self, action: #selector(self.playItemClickedAsGesture(sender:)))
        tap.numberOfTapsRequired = 1
        
        cell.contentView.isUserInteractionEnabled = true
        cell.contentView.addGestureRecognizer(tap)
//        cell.artistNameLabel.isUserInteractionEnabled = true
//        cell.artistNameLabel.addGestureRecognizer(tap)
//        cell.podcastImageView.isUserInteractionEnabled = true
//        cell.podcastImageView.addGestureRecognizer(tap)
//        cell.trackNameLabel.isUserInteractionEnabled = true
//        cell.trackNameLabel.addGestureRecognizer(tap)
        
        cell.PlayButton.isHidden = true
        cell.RemoveButton.isHidden = true
        
        cell.PlayButton.addTarget(self, action: #selector(playItemClicked(sender:)), for: .touchUpInside)
        cell.MoreButton.addTarget(self, action: #selector(requestMore(sender:)), for: .touchUpInside)
        cell.RemoveButton.addTarget(self, action: #selector(removeItemClicked(sender:)), for: .touchUpInside)
        
        // reload 후 반영 됨.
        if (isEditing) {
            cell.MoreButton.isHidden = true
            cell.RemoveButton.isHidden = true
            
        } else {
            cell.MoreButton.isHidden = false
            cell.RemoveButton.isHidden = true
        }
        
        return cell
    }
    
    // Moving cell
    func tableView(_ tableView: UITableView, editingStyleForRowAt indexPath: IndexPath) -> UITableViewCell.EditingStyle {
        return .none
    }
    func tableView(_ tableView: UITableView, shouldIndentWhileEditingRowAt indexPath: IndexPath) -> Bool {
        return false
    }
    
    func tableView(_ tableView: UITableView, moveRowAt sourceIndexPath: IndexPath, to destinationIndexPath: IndexPath) {
        
        // TODO
        print("[move] on MainWebView")
        
        let movedObject = self.musicTracks[sourceIndexPath.row]
        let movedSelected = self.musicTrackSelected[sourceIndexPath.row]
        
        musicTracks.remove(at: sourceIndexPath.row)
        musicTrackSelected.remove(at: sourceIndexPath.row)
        
        musicTracks.insert(movedObject, at: destinationIndexPath.row)
        musicTrackSelected.insert(movedSelected, at: destinationIndexPath.row)
        
        debugPrint("\(sourceIndexPath.row) => \(destinationIndexPath.row)")
        NSLog("\(sourceIndexPath.row) => \(destinationIndexPath.row)")
        // To check for correctness enable: self.tableView.reloadData()
        
        UserDefaults.standard.set(true, forKey: "isEdited")
    }
    
    
    @objc func loadBottomMenu() {
        
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        mainController?.mediaPlayerDetailsView.bottomMenu.isHidden = false
        
    }
    
    @objc func hideBottomMenu() {
        
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        mainController?.mediaPlayerDetailsView.bottomMenu.isHidden = true
    }
    
//    // TEST.. NOT USED>
    @objc func requestMore(sender: AnyObject) {
        print("[requestMore]")

        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
//        mainController?.minimizePlayerDetails()
//
//        let musicUrl = musicTracks[sender.tag].url ?? ""
//        let URL = upmusicUrlNoStatic + musicUrl
//        UserDefaults.standard.set(URL, forKey: "reloadUrl")
//        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "reload"), object: nil)
//
        mainController?.mediaPlayerDetailsView.bottomMenu.isHidden = true
        mainController?.mediaPlayerDetailsView.BottomMoreDialogViewContrainer.isHidden = false
        
        mainController?.mediaPlayerDetailsView.BottomMoreDialogRow0_Label.text = musicTracks[sender.tag].subject
        
        let stringNick =  musicTracks[sender.tag].artistNick ?? ""
        let stringSubject = musicTracks[sender.tag].subject ?? ""
        
        mainController?.mediaPlayerDetailsView.BottomMoreDialogRow0_Label.text = stringSubject + " - "  + stringNick
        
        let tap1 = UITapGestureRecognizer(target: self, action: #selector(self.bottomMoreRow1(sender:)))
        tap1.numberOfTapsRequired = 1
        
        let tap2 = UITapGestureRecognizer(target: self, action: #selector(self.bottomMoreRow2(sender:)))
        tap1.numberOfTapsRequired = 1
        
        let tap3 = UITapGestureRecognizer(target: self, action: #selector(self.bottomMoreRow3(sender:)))
        tap3.numberOfTapsRequired = 1
        //
        let tap4 = UITapGestureRecognizer(target: self, action: #selector(self.bottomMoreRow4(sender:)))
        tap4.numberOfTapsRequired = 1
        
        let tap5 = UITapGestureRecognizer(target: self, action: #selector(self.bottomMoreRow5(sender:)))
        tap5.numberOfTapsRequired = 1
        
        mainController?.mediaPlayerDetailsView.BottomMoreDialogRow1.tag = sender.tag
        mainController?.mediaPlayerDetailsView.BottomMoreDialogRow2.tag = sender.tag
        
        mainController?.mediaPlayerDetailsView.BottomMoreDialogRow1.isUserInteractionEnabled = true
        mainController?.mediaPlayerDetailsView.BottomMoreDialogRow1.addGestureRecognizer(tap1)
        
        mainController?.mediaPlayerDetailsView.BottomMoreDialogRow2.isUserInteractionEnabled = true
        mainController?.mediaPlayerDetailsView.BottomMoreDialogRow2.addGestureRecognizer(tap2)
        
        // 공유하기.... (...?)
        mainController?.mediaPlayerDetailsView.BottomMoreDialogRow3.isUserInteractionEnabled = true
        mainController?.mediaPlayerDetailsView.BottomMoreDialogRow3.addGestureRecognizer(tap3)
        
        mainController?.mediaPlayerDetailsView.BottomMoreDialogRow4.isUserInteractionEnabled = true
        mainController?.mediaPlayerDetailsView.BottomMoreDialogRow4.addGestureRecognizer(tap4)
        
        if (musicTracks[sender.tag].liked == true) {
            
            mainController?.mediaPlayerDetailsView.BottomMoreDialogRow4_Image.image = #imageLiteral(resourceName: "playerLikeOn")
            mainController?.mediaPlayerDetailsView.BottomMoreDialogRow4_Label.text = "좋아요 취소"
            
        }
        
        if (musicTracks[sender.tag].liked == false) {
            
            mainController?.mediaPlayerDetailsView.BottomMoreDialogRow4_Image.image = #imageLiteral(resourceName: "tabLikeOff")
            mainController?.mediaPlayerDetailsView.BottomMoreDialogRow4_Label.text = "좋아요"
            
        }
        
        mainController?.mediaPlayerDetailsView.BottomMoreDialogRow5.isUserInteractionEnabled = true
        mainController?.mediaPlayerDetailsView.BottomMoreDialogRow5.addGestureRecognizer(tap5)
        
    }
    
    
    @objc func bottomMoreRow1(sender: UIGestureRecognizer) {
        
        print("From WEbhandler [bottomMoreRow1]")
        
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        mainController?.minimizePlayerDetails()
        //        DEFAULT_URL + "/music" + "/artist" + "/" + "" + track.getArtistId()
        let musicUrl = "/music/artist/\(musicTracks[sender.view!.tag].artistId!)"  //+ String(describing: self.mediaPlayerDetailsView.track.artistId)
        
        print("[URL] : \(musicUrl)")
        
        let URL = upmusicUrlNoStatic + musicUrl
        UserDefaults.standard.set(URL, forKey: "reloadUrl")
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "reload"), object: nil)
        
        mainController?.mediaPlayerDetailsView.BottomMoreDialogViewContrainer.isHidden = true
        
    }
    
    @objc func bottomMoreRow2(sender: UIGestureRecognizer) {
        
        print("From WEbhandler [bottomMoreRow2]")
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        mainController?.minimizePlayerDetails()
        //        DEFAULT_URL + "/music" + "/album" + "/" + "" + track.getMusicAlbumId()
        let musicUrl = "/music/album/\(musicTracks[sender.view!.tag].musicAlbumId!)" //+ String(describing: self.mediaPlayerDetailsView.track.musicAlbumId)
        print("[URL] : \(musicUrl)")
        let URL = upmusicUrlNoStatic + musicUrl
        UserDefaults.standard.set(URL, forKey: "reloadUrl")
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "reload"), object: nil)
        
        mainController?.mediaPlayerDetailsView.BottomMoreDialogViewContrainer.isHidden = true
    }
    
    // 공유하기
    @objc func bottomMoreRow3(sender: UIGestureRecognizer) {
        print("WebHandler : [bottomMoreRow3]")
        
        let id = self.musicTracks[sender.view!.tag].id
        let albumId = self.musicTracks[sender.view!.tag].musicAlbumId
        UserDefaults.standard.set(id, forKey: "selectedId")
        UserDefaults.standard.set(albumId, forKey: "selectedAlbumId")
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "evaluateShare"), object: nil)
        
    }
    
    // 좋아요.
    @objc func bottomMoreRow4(sender: UIGestureRecognizer) {
        
        print("WebHandler : [bottomMoreRow4] call requestHeart()")
        UserDefaults.standard.set(musicTracks[sender.view!.tag].id!, forKey: "selectedId")
        UserDefaults.standard.set(sender.view!.tag, forKey: "selectedRow")
        requestHeart()
    }
    
    // 담기
    @objc func bottomMoreRow5(sender: UIGestureRecognizer) {
        print("WebHandler : [bottomMoreRow5]")
        
        UserDefaults.standard.set("WebHandler", forKey: "fromWhichController")
        UserDefaults.standard.set(true, forKey: "isSingleSelection")
        UserDefaults.standard.set(musicTracks[sender.view!.tag].id!, forKey: "selectedId")
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "bottomMenuDown"), object: nil)
    }
    
    @objc func removeItemClicked(sender: AnyObject) {
        
        print("[removeItemClicked] ")
        
        self.musicTracks.remove(at: sender.tag)
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        //        mainController?.mediaPlayerDetailsView.playlistTableView.deleteRows(at: [indexPath], with: .fade)
        mainController?.mediaPlayerDetailsView.playlistTableView.reloadData()
        //        print(self.musicTracks)
        
    }
    
    @objc func editCancelButtonClicked(sender: AnyObject) {
        
        print("[WebHandler][editCancelButtonClicked] ")
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        
        if(mainController?.mediaPlayerDetailsView.playlistTableView.isEditing == true) {
            self.musicTracks = self.tempOrigins
            self.musicTrackSelected = self.tempSelectedOrigins
            mainController?.mediaPlayerDetailsView.playlistTableView.reloadData()
            
        }
        
    }
    
    
    @objc func playItemClickedAsGesture(sender: UIGestureRecognizer) {
        
        print("[playItemClickedAsGesture] sender.tag = \(String(describing: sender.view?.tag))")
        //        mainController?
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        let isEditing = mainController?.mediaPlayerDetailsView.playlistTableView.isEditing ?? false
        if(!isEditing) {
            
            //            print("\(String(describing: self.musicTracks[sender.view?.tag ?? 0].subject))")
            // mainController?.mediaPlayerDetailsView.playlistTracks
            // self.musicTracks
            let index = sender.view?.tag ?? 0
            
            print("[playItemClickedAsGesture][maximizePlayerDetails]")
            
            if (self.musicTracks.count > index) {
                print("[playItemClickedAsGesture][maximizePlayerDetails] working")
                // TODO
                mainController?.maximizePlayerDetails(track: self.musicTracks[index], playlistTracks: self.musicTracks)
            }
            
            
        } else {
            
            if (self.musicTrackSelected[sender.view!.tag] == false) {
                self.musicTrackSelected[sender.view!.tag] = true
            } else {
                self.musicTrackSelected[sender.view!.tag] = false
            }
            
            print(" > selectedTracks : [\(self.musicTrackSelected[sender.view!.tag])]")
            mainController?.mediaPlayerDetailsView.playlistTableView.reloadData()
            print(" > mediaPlayerDetailsView.playlistTableView.reloadData()")
            
            
            if(self.musicTrackSelected.contains(true) ?? false) {
                loadBottomMenu()
            } else {
                hideBottomMenu()
            }
            
        }
    }
    
    
    
    @objc func playItemClicked(sender: AnyObject) {
        print("[playItemClicked] ")
        
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        let isEditing = mainController?.mediaPlayerDetailsView.playlistTableView.isEditing ?? false
        if(!isEditing) {
            // TODO
            mainController?.maximizePlayerDetails(track: musicTracks[sender.view?.tag ?? 0], playlistTracks: self.musicTracks)
        } else {
            
            if (musicTrackSelected[sender.view!.tag]==false) {
                musicTrackSelected[sender.view!.tag] = true
            } else {
                musicTrackSelected[sender.view!.tag] = false
            }
            
            print(" > selectedTracks : [\(musicTrackSelected[sender.view!.tag])]")
            mainController?.mediaPlayerDetailsView.playlistTableView.reloadData()
            
            if(musicTrackSelected.contains(true)) {
                
                UserDefaults.standard.set(false, forKey: "isSingleSelection")
                loadBottomMenu()
            } else {
                hideBottomMenu()
            }
            
        }
        
    }
    
    @objc func collectionItemClickedAsGesture(sender: UIGestureRecognizer) {
        
        print("[collectionItemClickedAsGesture]")
        var JSESSIONID = ""
        var TOKEN = ""
        NSLog("[JSESSIONID] ::: \(UserDefaults.standard.string(forKey: "JSESSIONID"))")
        JSESSIONID = UserDefaults.standard.string(forKey: "JSESSIONID") ?? ""
        TOKEN = UserDefaults.standard.string(forKey: "TOKEN") ?? ""
        
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        
        requestUserCollectionTrackAddingByID(sessionID: JSESSIONID
            , token: TOKEN
            , idValue: collectionList[sender.view!.tag].id ?? 0
        )
        
    }
    
    
    
}
