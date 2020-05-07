//
//  WKCookieWebViewController4.swift
//  Upmusic
//
//  Created by nough on 03/10/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import UIKit
import WebKit
import Alamofire
import SwiftyJSON
import GoogleSignIn
import Social
import Device
//import SwiftWebSocket
import StompClientLib
import FBSDKShareKit

class MainWebViewController: UIViewController, WKNavigationDelegate
    ,GIDSignInUIDelegate, UIDocumentInteractionControllerDelegate
//    , WebSocketDelegate
{
    
    
    let cellId = "cellId"
    var socketClient = StompClientLib()
    var socketURL = NSURL()
    
    //timer
    var mTimer : Timer?
    var number : Int = 0
    
    var musicTracks : [MusicTrack] = []
    var clickedIndex : Int!
    var isFirstLoad: Bool = true
    
    ///메인으로 사용중인 웹뷰
    var webView: WebView!
    ///window.open()으로 열리는 새창
    var createWebView: WKWebView!
    var previousUrl: String!
    var activityIndicator: UIActivityIndicatorView!
    let network: NetworkManager = NetworkManager.sharedInstance
    var offlineNetworkView : OfflineNetworkView!
    var offlineNetworkPopup : OfflineNetworkPopup!
    
    var webConfig: WKWebViewConfiguration {
        get {
            NSLog("[webConfig]:::WKWebViewConfiguration setting...")
            NSLog("-----------------------------------------")
            let webCfg = WKWebViewConfiguration()
            let userController = WKUserContentController()
            
            let webHandler = WebHandler()
            let preferences = WKPreferences()
            preferences.javaScriptEnabled = true
            webCfg.preferences = preferences
            
            userController.add(webHandler, name: "callbackFromJS")
            //            userController.add(self, name: "ios")
            
            webCfg.applicationNameForUserAgent = upmusicUserAgent
            webCfg.userContentController = userController;
            return webCfg;
        }
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?){
        self.view.endEditing(true)
    }
    
    @objc func show_offline_network_view() {
        
        print("[show_offline_network_view]")
        
        offlineNetworkView = OfflineNetworkView.instanceFromNib() as! OfflineNetworkView
        
        offlineNetworkView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        
        offlineNetworkView.frame = self.view.bounds
        
        view.addSubview(offlineNetworkView)
        
        //        UserDefaults.standard.set(true, forKey: "isOffline")
    }
    
    @objc func hide_offline_network_view() {
        offlineNetworkView.removeFromSuperview()
        
        UserDefaults.standard.set("false", forKey: "isOffline")
        
        //        self.webView.reload()
        
    }
    
    @objc func show_offline_network_popup() {
        
        print("[show_offline_network_popup]")
        
        offlineNetworkPopup = OfflineNetworkPopup.instanceFromNib() as! OfflineNetworkPopup
        
        offlineNetworkPopup.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        
        offlineNetworkPopup.frame = self.view.bounds
        
        view.addSubview(offlineNetworkPopup)
        
        UserDefaults.standard.set(true, forKey: "isOffline")
    }
    
    
    @objc func hide_offline_network_popup() {
        
        offlineNetworkPopup.removeFromSuperview()
        
        UserDefaults.standard.set("false", forKey: "isOffline")
        
//        load_site()
//        web_view_evaluate_page_load()
        
    }
    
    @objc func check_network_while_app_using() {
        
        print(" > check_network_while_app_using ")
        NetworkManager.isUnreachable { _ in
            print(" > check_network_in_timer [NetworkManager.isUnreachable]")
            
            let offlineViewAddedWhileAppUsing = UserDefaults.standard.integer(forKey: "offlineViewAddedWhileAppUsing")
            if (offlineViewAddedWhileAppUsing == 0) {
                self.show_offline_network_view()
                UserDefaults.standard.set( 1 ,forKey: "offlineViewAddedWhileAppUsing")
            }
            // 재시도 혹은 다시시도 로직은 추가
            
        }
        // LOAD JUST WEBVIEW
        NetworkManager.isReachable { _ in
            print(" > check_network_in_timer [NetworkManager.isReachable] > As Usual!  ")
            
            let offlineViewAddedWhileAppUsing = UserDefaults.standard.integer(forKey: "offlineViewAddedWhileAppUsing")
            if (offlineViewAddedWhileAppUsing != 0) {
                self.hide_offline_network_view()
                self.showActivityIndicator(show: false) // 임시 추가
                
                //                self.load_site()
                //                self.web_view_evaluate_page_load()
                
            }
            
        }
    }
    
    @objc func check_network() {
        
        NetworkManager.isUnreachable { _ in
            print(" > check_network [NetworkManager.isUnreachable]")
            
            let offlineViewAdded = UserDefaults.standard.integer(forKey: "offlineViewAdded")
            if (offlineViewAdded == 0) {
                self.show_offline_network_popup()
                UserDefaults.standard.set( 1 ,forKey: "offlineViewAdded")
            }
            // 재시도 혹은 다시시도 로직은 추가
            
        }
        
        // LOAD JUST WEBVIEW
        NetworkManager.isReachable { _ in
            print(" > check_network [NetworkManager.isReachable] > As Usual!  ")
            
            UserDefaults.standard.set(true, forKey: "offlineViewAddedAtFirst")
            
            let offlineViewAdded = UserDefaults.standard.integer(forKey: "offlineViewAdded")
            if (offlineViewAdded != 0) {
                self.hide_offline_network_popup()
            }
        }
    }
    
    
    
    func setup_indicator() {
        
        activityIndicator = UIActivityIndicatorView()
        activityIndicator.center = self.view.center
        activityIndicator.hidesWhenStopped = true
        activityIndicator.activityIndicatorViewStyle = UIActivityIndicatorView.Style.whiteLarge
        activityIndicator.color = UIColor.init(red: 10.0 / 255.0, green: 51.0 / 255.0, blue: 74.0 / 255.0, alpha: 1.0)
        //rgb(10,51,74)
        view.addSubview(activityIndicator)
    }
    
    func showActivityIndicator(show: Bool) {
        if show {
            activityIndicator.startAnimating()
        } else {
            activityIndicator.stopAnimating()
        }
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        NSLog("[viewDidLoad]:::MainWebViewController setting...")
        NSLog("-----------------------------------------")
        isFirstLoad = true
        UserDefaults.standard.set(false ,forKey: "isEdited")
        UserDefaults.standard.set(0, forKey: "offlineViewAdded")
        UserDefaults.standard.set(0, forKey: "offlineViewAddedWhileAppUsing")
        UserDefaults.standard.set("false", forKey: "isPlaying")
        
        setup_indicator()
        setup_other_s()
        setup_web_view()
        
        if ((UserDefaults.standard.string(forKey: "loginMethod")) != nil) {
            
            if (UserDefaults.standard.string(forKey: "loginMethod") == "local") {
                if (UserDefaults.standard.bool(forKey: "rememberMe") == false) {
                    UserDefaults.standard.set("none", forKey: "loginMethod")
                    UserDefaults.standard.set(false, forKey: "rememberMe")
                    UserDefaults.standard.set("", forKey: "JSESSIONID")
                    UserDefaults.standard.set("", forKey: "TOKEN")
                    open_site()
                    return
                }
                
                NSLog(" > rememberMe : \(UserDefaults.standard.bool(forKey: "rememberMe")) in viewDidLoad")
                
                if (UserDefaults.standard.bool(forKey: "rememberMe") == false) {
                    
                    HTTPCookieStorage.shared.removeCookies(since: Date.distantPast)
                    print("[WebCacheCleaner] All cookies deleted on viewDidload")
                    
                    WKWebsiteDataStore.default().fetchDataRecords(ofTypes: WKWebsiteDataStore.allWebsiteDataTypes()) { records in
                        records.forEach { record in
                            WKWebsiteDataStore.default().removeData(ofTypes: record.dataTypes, for: [record], completionHandler: {})
                            print("[WebCacheCleaner] Record \(record) deleted on viewDidload")
                            
                            let domain = Bundle.main.bundleIdentifier!
                            UserDefaults.standard.removePersistentDomain(forName: domain)
                            UserDefaults.standard.synchronize()
                            
//                            self.set_up_web_view_as_normalmode()
                            self.set_up_web_view_as_empty_item_mode()
                            self.open_site()
                            
                        }
                    }
                    
                } else {
                    // Local-LOGIN
                    NSLog("[LOCAL-LOGIN] in viewDidLoad , rememberME")
                    let email = UserDefaults.standard.string(forKey: "email") ?? ""
                    let password = UserDefaults.standard.string(forKey: "password") ?? ""
                    self.requestLocalLogin(email: email, password: password, completion: {
                        NSLog(" > [requestLocalLogin] in viewDidLoad : Completed.")
                        
                        self.check_user_session_id_exists()
//                        self.set_up_web_view_as_normalmode()
                        self.set_up_web_view_as_empty_item_mode()
                        self.retrieve_cookies()
                        
                        
                    })
                }
                
            } else {
                
                NSLog("[SNS-LOGIN] in viewDidLoad , \(String(describing: UserDefaults.standard.string(forKey: "loginMethod")))")
                check_user_session_id_exists()
                
                set_up_web_view_as_empty_item_mode()
//                set_up_web_view_as_normalmode()
                retrieve_cookies()
                
            }
            
        } else {
            set_up_web_view_as_empty_item_mode()
//            set_up_web_view_as_normalmode()
            retrieve_cookies()
        }
        
    }
    
    private func setup_other_s() {
        GIDSignIn.sharedInstance().uiDelegate = self
    }
    
    // 0. init.
    private func setup_web_view() {
        
        NSLog("[setup_web_view]:::setting...")
        NSLog("-----------------------------------------")
//        webView = WKWebView(frame: self.view.bounds, configuration: webConfig )
        let width = CGFloat(view.frame.width)
        let height = CGFloat(view.frame.height)
        
        webView = WebView(frame: CGRect(x:0, y: 0, width: width, height: height), configuration: webConfig, history : WebViewHistory.init() )
        
        view.addSubview(webView)
        
        let views: [String: Any] = ["webView": webView]
        
        view.addConstraints(NSLayoutConstraint.constraints(
            withVisualFormat: "H:|-0-[webView]-0-|",
            options: [],
            metrics: nil,
            views: views))
        view.addConstraints(NSLayoutConstraint.constraints(
            withVisualFormat: "V:|-0-[webView]-0-|",
            options: [],
            metrics: nil,
            views: views))
        
        webView.uiDelegate = self
        webView.navigationDelegate = self
        webView.allowsBackForwardNavigationGestures = true
        webView.customUserAgent = upmusicUserAgent
        
    }
    
    
    
    @objc func web_view_evaluate_share() {
        // trackId : String
        print("[web_view_evaluate_share] id ::: \(String(describing: UserDefaults.standard.string(forKey: "selectedId")))")
        
//        self.webView.evaluateJavaScript("UPMShareModal.showShareModal(\(UserDefaults.standard.string(forKey: "selectedId"))", completionHandler: nil)
        
        let selectedId = String(describing: UserDefaults.standard.string(forKey: "selectedId")) ?? ""
        let selectedAlbumId = String(describing: UserDefaults.standard.string(forKey: "selectedAlbumId")) ?? ""
        
        self.webView.evaluateJavaScript("UPMShareModal.showShareModal(" + selectedId + ", " + selectedAlbumId + ")") {
            (result, error) in
            //  window.
            //  \(String(describing: UserDefaults.standard.string(forKey: "selectedId"))
            // 49754
            if error != nil {
                print(" > result : \(String(describing: result))")
                print(" > error : \(String(describing: error))")
            }
        }
    }
    
    @objc func set_up_web_view_as_normalmode() {
        
        print("[set_up_web_view_as_normalmode]")
        
        let width = CGFloat(view.frame.width)
        let height = CGFloat(view.frame.height)
        webView.frame = CGRect(x:0, y: 0, width: width, height: height)
        
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        mainController?.mediaPlayerDetailsView.isHidden = true
        
        self.webView.layoutIfNeeded()
        
    }
    
    
    @objc func set_up_web_view_as_empty_item_mode() {
        print("[set_up_web_view_as_playermode]")
        
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        mainController?.mediaPlayerDetailsView.isHidden = false
        
        
        mainController?.peekPlayerDetails(track: nil)
        
        
        
        let width = CGFloat(view.frame.width)
        let height = CGFloat(view.frame.height)
        
        switch Device.version() {
            /*** iPhone ***/
        case .iPhone4:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-48) // iphone 4
        case .iPhone4S:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-48) // iphone 4
        case .iPhone5:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-48) // iphone se
        case .iPhone5C:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-48) // iphone se
        case .iPhone5S:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-48) // iphone se
        case .iPhoneSE:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-48) // iphone se
            break
            
            
        case .iPhone6:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-50) // iphone 6s, 7,8
        case .iPhone6S:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-50) // iphone 6s, 7,8
        case .iPhone7:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-50) // iphone 6s, 7,8
        case .iPhone8:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-50) // iphone 6s, 7,8
            break
            
            
        case .iPhone7Plus:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-48) // iphone * plus
        case .iPhone6Plus:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-48) // iphone * plus
        case .iPhone6SPlus:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-48) // iphone * plus
        case .iPhone8Plus:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-48) // iphone * plus
            break
            
            
        case .iPhoneX:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-80) // iphone xs, x , xr
        case .iPhoneXS:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-80) // iphone xs, x , xr
        case .iPhoneXR:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-80) // iphone xs, x , xr
        case .iPhoneXS_Max:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-80) // iphone xs, x , xr
            break
            /*** iPad ***/
            //        case .iPad1:           print("It's an iPad 1")
            //        case .iPad2:           print("It's an iPad 2")
            //        case .iPad3:           print("It's an iPad 3")
            //        case .iPad4:           print("It's an iPad 4")
            //        case .iPad5:           print("It's an iPad 5")
            //        case .iPad6:           print("It's an iPad 6")
            //        case .iPadAir:         print("It's an iPad Air")
            //        case .iPadAir2:        print("It's an iPad Air 2")
            //        case .iPadMini:        print("It's an iPad Mini")
            //        case .iPadMini2:       print("It's an iPad Mini 2")
            //        case .iPadMini3:       print("It's an iPad Mini 3")
        //        case .iPadMini4:       print("It's an iPad Mini 4")
        case .iPadPro9_7Inch:  print("It's an iPad Pro 9.7 Inch")
        webView.frame = CGRect(x:0, y: 0, width: width, height: height-50) // ipad 9.7, ipad 10.5
        case .iPadPro10_5Inch: print("It's an iPad Pro 10.5 Inch")
        webView.frame = CGRect(x:0, y: 0, width: width, height: height-50) // ipad 9.7, ipad 10.5
            break
            
        case .iPadPro12_9Inch:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-50) // ipad 12.9 1st, ipad 12.9 2nd
            //                    webView.frame = CGRect(x:0, y: 0, width: width, height: height-65) // ipad 12.9 3rd generation.
            break
            
            //            /*** iPod ***/
            //        case .iPodTouch1Gen: print("It's a iPod touch generation 1")
            //        case .iPodTouch2Gen: print("It's a iPod touch generation 2")
            //        case .iPodTouch3Gen: print("It's a iPod touch generation 3")
            //        case .iPodTouch4Gen: print("It's a iPod touch generation 4")
            //        case .iPodTouch5Gen: print("It's a iPod touch generation 5")
            //        case .iPodTouch6Gen: print("It's a iPod touch generation 6")
            
            /*** Simulator ***/
            //        case .Simulator:    print("It's a Simulator")
            
            /*** Unknown ***/
        default:            print("It's an unknown OR simulator device")
        webView.frame = CGRect(x:0, y: 0, width: width, height: height-50) // ipad 9.7, ipad 10.5
            break
        }
        
        //        webView.frame = CGRect(x:0, y: 0, width: width, height: height-65) // ipad 11
        
        
        
        
        self.webView.layoutIfNeeded()
        
    }
    
    
    @objc func set_up_web_view_as_playermode() {
        print("[set_up_web_view_as_playermode]")
        
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        mainController?.mediaPlayerDetailsView.isHidden = false
        
        let width = CGFloat(view.frame.width)
        let height = CGFloat(view.frame.height)
        
        switch Device.version() {
            /*** iPhone ***/
        case .iPhone4:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-48) // iphone 4
        case .iPhone4S:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-48) // iphone 4
        case .iPhone5:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-48) // iphone se
        case .iPhone5C:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-48) // iphone se
        case .iPhone5S:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-48) // iphone se
        case .iPhoneSE:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-48) // iphone se
            break
            
            
        case .iPhone6:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-50) // iphone 6s, 7,8
        case .iPhone6S:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-50) // iphone 6s, 7,8
        case .iPhone7:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-50) // iphone 6s, 7,8
        case .iPhone8:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-50) // iphone 6s, 7,8
            break
            
            
        case .iPhone7Plus:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-48) // iphone * plus
        case .iPhone6Plus:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-48) // iphone * plus
        case .iPhone6SPlus:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-48) // iphone * plus
        case .iPhone8Plus:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-48) // iphone * plus
            break
            
            
        case .iPhoneX:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-80) // iphone xs, x , xr
        case .iPhoneXS:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-80) // iphone xs, x , xr
        case .iPhoneXR:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-80) // iphone xs, x , xr
        case .iPhoneXS_Max:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-80) // iphone xs, x , xr
            break
            /*** iPad ***/
            //        case .iPad1:           print("It's an iPad 1")
            //        case .iPad2:           print("It's an iPad 2")
            //        case .iPad3:           print("It's an iPad 3")
            //        case .iPad4:           print("It's an iPad 4")
            //        case .iPad5:           print("It's an iPad 5")
            //        case .iPad6:           print("It's an iPad 6")
            //        case .iPadAir:         print("It's an iPad Air")
            //        case .iPadAir2:        print("It's an iPad Air 2")
            //        case .iPadMini:        print("It's an iPad Mini")
            //        case .iPadMini2:       print("It's an iPad Mini 2")
            //        case .iPadMini3:       print("It's an iPad Mini 3")
        //        case .iPadMini4:       print("It's an iPad Mini 4")
        case .iPadPro9_7Inch:  print("It's an iPad Pro 9.7 Inch")
        webView.frame = CGRect(x:0, y: 0, width: width, height: height-50) // ipad 9.7, ipad 10.5
        case .iPadPro10_5Inch: print("It's an iPad Pro 10.5 Inch")
        webView.frame = CGRect(x:0, y: 0, width: width, height: height-50) // ipad 9.7, ipad 10.5
            break
            
        case .iPadPro12_9Inch:
            webView.frame = CGRect(x:0, y: 0, width: width, height: height-50) // ipad 12.9 1st, ipad 12.9 2nd
            //                    webView.frame = CGRect(x:0, y: 0, width: width, height: height-65) // ipad 12.9 3rd generation.
            break
            
            //            /*** iPod ***/
            //        case .iPodTouch1Gen: print("It's a iPod touch generation 1")
            //        case .iPodTouch2Gen: print("It's a iPod touch generation 2")
            //        case .iPodTouch3Gen: print("It's a iPod touch generation 3")
            //        case .iPodTouch4Gen: print("It's a iPod touch generation 4")
            //        case .iPodTouch5Gen: print("It's a iPod touch generation 5")
            //        case .iPodTouch6Gen: print("It's a iPod touch generation 6")
            
            /*** Simulator ***/
            //        case .Simulator:    print("It's a Simulator")
            
            /*** Unknown ***/
        default:            print("It's an unknown OR simulator device")
        webView.frame = CGRect(x:0, y: 0, width: width, height: height-50) // ipad 9.7, ipad 10.5
            break
        }
        
        //        webView.frame = CGRect(x:0, y: 0, width: width, height: height-65) // ipad 11
        
        if (UserDefaults.standard.string(forKey: "loginMethod") == "none") {
            set_up_web_view_as_empty_item_mode()
            return
        }
        if (mainController?.mediaPlayerDetailsView.playlistTracks == nil) {
//            set_up_web_view_as_normalmode()
            set_up_web_view_as_empty_item_mode()
            return
        }
        if(mainController?.mediaPlayerDetailsView.playlistTracks.count == 0) {
//            set_up_web_view_as_normalmode()
            set_up_web_view_as_empty_item_mode()
            return
        }
        
        self.webView.layoutIfNeeded()
        
    }
    
    func re_set_up_user_controller() {
    
        let webCfg = WKWebViewConfiguration()
        let userController = WKUserContentController()
        
        let webHandler = WebHandler()
        let preferences = WKPreferences()
        preferences.javaScriptEnabled = true
        webCfg.preferences = preferences
        
        userController.add(webHandler, name: "callbackFromJS")
        webCfg.applicationNameForUserAgent = upmusicUserAgent
        webCfg.userContentController = userController;
        
        webView.configuration.applicationNameForUserAgent = upmusicUserAgent
        webView.configuration.preferences = preferences
        webView.configuration.preferences.javaScriptEnabled = true
        webView.configuration.userContentController = webCfg.userContentController
        
    }
    
    
    func handleCustomGoogleSign() {
        GIDSignIn.sharedInstance().signIn()
    }
    
    
    // [load]
    func open_site()
    {
        let addr = upmusicUrlNoStatic
        let url = URL(string: addr)!
        let request = URLRequest(url: url)
        NSLog("[open_site]::webView loading...")
        
        NSLog("-----------------------------------------")
        
        self.webView.customUserAgent = upmusicUserAgent
        self.webView.load(request)
        
        // POSSIBLE TO RELOAD (FROM OTHER CLASS)
        // WORK WITH BELOW LINE
        NotificationCenter.default.addObserver(self
            , selector: #selector(reloadWebViewAfterLogin)
            , name: NSNotification.Name(rawValue: "reloadWebViewAfterLogin")
            , object: nil)

        NotificationCenter.default.addObserver(self
            , selector: #selector(reloadWebView)
            , name: NSNotification.Name(rawValue: "reload")
            , object: nil)
        NotificationCenter.default.addObserver(self
            , selector: #selector(show_toast)
            , name: NSNotification.Name(rawValue: "toast")
            , object: nil)
        
        
        NotificationCenter.default.addObserver(self
            , selector: #selector(web_view_evaluate_share)
            , name: NSNotification.Name(rawValue: "evaluateShare")
            , object: nil)
        
        NotificationCenter.default.addObserver(self
            , selector: #selector(set_up_web_view_as_empty_item_mode)
            , name: NSNotification.Name(rawValue: "webViewPlayerEmpty")
            , object: nil)
        
        NotificationCenter.default.addObserver(self
            , selector: #selector(set_up_web_view_as_normalmode)
            , name: NSNotification.Name(rawValue: "webViewNormalSize")
            , object: nil)
        NotificationCenter.default.addObserver(self
            , selector: #selector(set_up_web_view_as_playermode)
            , name: NSNotification.Name(rawValue: "webViewPlayerSize")
            , object: nil)
        NotificationCenter.default.addObserver(self
            , selector: #selector(dispatch_share)
            , name: NSNotification.Name(rawValue: "shareToProvider")
            , object: nil)
        NotificationCenter.default.addObserver(self
            , selector: #selector(pop_up_dialog)
            , name: NSNotification.Name(rawValue: "dialogView1")
            , object: nil)
        
        NotificationCenter.default.addObserver(self
            , selector: #selector(registerSocket)
            , name: NSNotification.Name(rawValue: "registerSocket")
            , object: nil)
        
        NotificationCenter.default.addObserver(self
            , selector: #selector(sendSocketPlayTime)
            , name: NSNotification.Name(rawValue: "sendSocketPlayTime")
            , object: nil)
        
        NotificationCenter.default.addObserver(self
            , selector: #selector(sendSocketIncreasePlayTime)
            , name: NSNotification.Name(rawValue: "sendSocketIncreasePlayTime")
            , object: nil)
        
        NotificationCenter.default.addObserver(self
            , selector: #selector(onTimerStart)
            , name: NSNotification.Name(rawValue: "onTimerStart")
            , object: nil)
        NotificationCenter.default.addObserver(self
            , selector: #selector(onTimerEnd)
            , name: NSNotification.Name(rawValue: "onTimerEnd")
            , object: nil)
        
        UserDefaults.standard.set("true", forKey: "isFirstLoad")
        
        // USAGE :
        // NotificationCenter.default.post(name: NSNotification.Name(rawValue: "reload"), object: nil)
        
    }
    
    @objc func show_toast(string: String) {
        TOAST(view: self.view, string: string)
    }
    
    @objc func pop_up_dialog() {
        
    }
    
    func clean_cookie_and_set_sns_login_cookie() {
        
        NSLog("[clean_cookie_and_set_sns_login_cookie]:::function called.")
        HTTPCookieStorage.shared.removeCookies(since: Date.distantPast)
        print("[WebCacheCleaner] All cookies deleted")
        
        WKWebsiteDataStore.default().fetchDataRecords(ofTypes: WKWebsiteDataStore.allWebsiteDataTypes()) { records in
            records.forEach { record in
                WKWebsiteDataStore.default().removeData(ofTypes: record.dataTypes, for: [record], completionHandler: {})
                print("[WebCacheCleaner] Record \(record) deleted")
                
                let cookie = HTTPCookie(properties: [
                    .domain: "upmusic.azurewebsites.net",
                    .path: "/",
                    .name: "JSESSIONID",
                    .value: UserDefaults.standard.string(forKey: "JSESSIONID"),
                    .secure: "TRUE",
                    .expires: NSDate(timeIntervalSinceNow: 31556926)
                    ])!
                
                self.webView.configuration.websiteDataStore.httpCookieStore.setCookie(cookie)
                WKWebsiteDataStore.default().httpCookieStore.setCookie(cookie, completionHandler: {
                    NSLog(" > user_default : \(UserDefaults.standard.string(forKey: "JSESSIONID"))")
                    
                    // COOKIE SETUP
                    self.webView.configuration.websiteDataStore.httpCookieStore.setCookie(cookie)
                    let urlRequest = NSURLRequest(url: NSURL(string: upmusicUrlNoStatic)! as URL)
                    
                    let width = CGFloat(self.view.frame.width)
                    let height = CGFloat(self.view.frame.height)
//
                    self.webView.load(urlRequest as URLRequest)
                    
                    // 소셜 로그인 뒤의 로그인 페이지 비 허용.
//                    self.webView.allowsBackForwardNavigationGestures = false
                    
                    UserDefaults.standard.set("true", forKey: "isFirstLoad")
                    
                    NotificationCenter.default.post(name: NSNotification.Name(rawValue: "fetchUserOwnTracksWhenLogin"), object: nil)
//                    UserDefaults.standard.set("true", forKey: "isFirstLoad")
                })
                
            }
        }
        
        NSLog("[clean_cookie_and_set_sns_login_cookie]:::function ended.")
    }
    
    @objc func reloadWebViewAfterLogin() {
        
        NSLog("[reloadWebViewAfterLogin]:::function called.")
        
        DispatchQueue.global().sync {
            clean_cookie_and_set_sns_login_cookie()
        }
        NSLog("[reloadWebViewAfterLogin]:::function end.")
        
    }
    
    @objc func reloadWebView() {
        
        let url =  NSURL(string: UserDefaults.standard.string(forKey: "reloadUrl") ?? "")
        NSLog("[reloadWebView]:::function called. reloadUrl : \(url)")
        
        let urlRequest = NSURLRequest(url: url as! URL)
        re_set_up_user_controller()
        self.webView.load(urlRequest as URLRequest)
//        re_set_up_user_controller()
        
        NSLog("[reloadWebView]::: agent : \(self.webView.customUserAgent)")
        NSLog("[reloadWebView]:::function end.")
        
    }
    
    // 1. webview1
    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        // dismiss indicator
        NSLog("-----------------------------------------")
        NSLog("[WKWebView]:::[1]::decidePolicyFor navigationAction")
        NSLog("-----------------------------------------")
        
        
        let loginMethod = UserDefaults.standard.string(forKey: "loginMethod")
        
        print(loginMethod)
        
        if (loginMethod == "none" ) { // TODO 조건추가??
            
            UserDefaults.standard.set("", forKey: "JSESSIONID")
            
            HTTPCookieStorage.shared.removeCookies(since: Date.distantPast)
            WKWebsiteDataStore.default().fetchDataRecords(ofTypes: WKWebsiteDataStore.allWebsiteDataTypes()) { records in
                records.forEach { record in
                    WKWebsiteDataStore.default().removeData(ofTypes: record.dataTypes, for: [record], completionHandler: {})
                    print("[WebCacheCleaner] Record \(record) deleted")
                }
            }
            //            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "webViewNormalSize"), object: nil)
            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "webViewPlayerEmpty"), object: nil)
            
//            self.open_site()
        }
        
        decisionHandler(.allow)
    }
    
    // 2. webview2
    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        
        NSLog("[WKWebView]:::[2]::didStartProvisionalNavigation")
        NSLog("-----------------------------------------")
        
        showActivityIndicator(show: true)
        
        if ((UserDefaults.standard.string(forKey: "loginMethod")) != nil) {
            
            print(" > loginMethod : \(UserDefaults.standard.string(forKey: "loginMethod")),  rememberMe : \(UserDefaults.standard.bool(forKey: "rememberMe"))")
            
            if (UserDefaults.standard.string(forKey: "loginMethod") == "local") {
                
                if (UserDefaults.standard.bool(forKey: "rememberMe") == false) {
                    UserDefaults.standard.set("none", forKey: "loginMethod")
                    UserDefaults.standard.set(false, forKey: "rememberMe")
                    UserDefaults.standard.set("", forKey: "JSESSIONID")
                    UserDefaults.standard.set("", forKey: "TOKEN")
                    
                }
                
                NSLog(" > rememberMe : \(UserDefaults.standard.bool(forKey: "rememberMe"))")
                
                if (UserDefaults.standard.bool(forKey: "rememberMe") != false) {
                    check_user_session_id_exists()
                    
                    if (isKeyPresentInUserDefaults(key: "JSESSIONID")) {
                        let cookie = HTTPCookie(properties: [
                            .domain: "upmusic.azurewebsites.net",
                            .path: "/",
                            .name: "JSESSIONID",
                            .value: UserDefaults.standard.string(forKey: "JSESSIONID"),
                            .secure: "TRUE",
                            .expires: NSDate(timeIntervalSinceNow: 31556926)
                            ])!
                        
                        self.webView.configuration.websiteDataStore.httpCookieStore.setCookie(cookie)
                        WKWebsiteDataStore.default().httpCookieStore.setCookie(cookie, completionHandler: {
                            NSLog(" > user_default : \(UserDefaults.standard.string(forKey: "JSESSIONID"))")
                            self.webView.configuration.websiteDataStore.httpCookieStore.setCookie(cookie)
                            
                            self.re_set_up_user_controller()
                            
                            
                        })
                    }
                    
                }
            }
            
            
        }
        
        NSLog("-----------------------------------------")
        NSLog("[Starting to load].....(end of [2])")
    }
    
    // 서버 리다이렉트가 발생하는 경우에만 발생하며 이 경우,
    // 다시 decidePolicyForNavigationAction delegate 호출이 발생할 수 있다.
    func webView(_ webView: WKWebView, didReceiveServerRedirectForProvisionalNavigation navigation: WKNavigation!) {
        
        NSLog("[WKWebView]:::[2-1]::didReceiveServerRedirectForProvisionalNavigation")
        NSLog("-----------------------------------------")
        
        
        // 로그아웃 처리
        if (self.webView.url?.absoluteString == upmusicUrlNoStatic + "/auth/logout") {
            NSLog("[WKWebView]:::[!]::: /auth/logout")
            onTimerEnd()
//            let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
//            mainController?.mediaPlayerDetailsView.player.pause()
            
            
        }
        
        if (isKeyPresentInUserDefaults(key: "JSESSIONID")) {
            let cookie = HTTPCookie(properties: [
                .domain: "upmusic.azurewebsites.net",
                .path: "/",
                .name: "JSESSIONID",
                .value: UserDefaults.standard.string(forKey: "JSESSIONID"),
                .secure: "TRUE",
                .expires: NSDate(timeIntervalSinceNow: 31556926)
                ])!
            
            self.webView.configuration.websiteDataStore.httpCookieStore.setCookie(cookie)
            WKWebsiteDataStore.default().httpCookieStore.setCookie(cookie, completionHandler: {
                NSLog(" > user_default : \(UserDefaults.standard.string(forKey: "JSESSIONID"))")
                self.webView.configuration.websiteDataStore.httpCookieStore.setCookie(cookie)
                
                self.re_set_up_user_controller()
            })
        }
        
    }
    
    // 3. webview3
    func webView(_ webView: WKWebView, decidePolicyFor navigationResponse: WKNavigationResponse, decisionHandler: @escaping (WKNavigationResponsePolicy) -> Void) {
        
        NSLog("[WKWebView]:::[3]::decidePolicyFor navigationResponse")
        NSLog("-----------------------------------------")
        
        // 로그아웃 처리
        if (self.webView.url?.absoluteString == upmusicUrlNoStatic + "/auth/logout") {
            NSLog("[WKWebView]:::[!]::: /auth/logout")
            
            let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
            mainController?.mediaPlayerDetailsView.player.pause()
            
            
            let loginMethod =  UserDefaults.standard.string(forKey: "loginMethod")
            
            if (loginMethod == "google") {
                GIDSignIn.sharedInstance()?.signOut()
            }
            
            if (loginMethod == "naver") {
                SnsManager.loginNaver(login: false)
            }
            
//            set_up_web_view_as_normalmode()
            set_up_web_view_as_empty_item_mode()
            
//            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "onTimerEnd"), object: nil)
            onTimerEnd()
            
            UserDefaults.standard.set("none", forKey: "loginMethod")
            UserDefaults.standard.set(false, forKey: "rememberMe")
            UserDefaults.standard.set("", forKey: "JSESSIONID")
            UserDefaults.standard.set("", forKey: "TOKEN")
            
            HTTPCookieStorage.shared.removeCookies(since: Date.distantPast)
            print("[WebCacheCleaner] All cookies deleted")
            
            WKWebsiteDataStore.default().fetchDataRecords(ofTypes: WKWebsiteDataStore.allWebsiteDataTypes()) { records in
                records.forEach { record in
                    WKWebsiteDataStore.default().removeData(ofTypes: record.dataTypes, for: [record], completionHandler: {})
                    print("[WebCacheCleaner] Record \(record) deleted")
                }
            }
            //            open_site()
            return
        }
        
        
        
        if (isKeyPresentInUserDefaults(key: "JSESSIONID")) {
            let cookie = HTTPCookie(properties: [
                .domain: "upmusic.azurewebsites.net",
                .path: "/",
                .name: "JSESSIONID",
                .value: UserDefaults.standard.string(forKey: "JSESSIONID"),
                .secure: "TRUE",
                .expires: NSDate(timeIntervalSinceNow: 31556926)
                ])!
            
            self.webView.configuration.websiteDataStore.httpCookieStore.setCookie(cookie)
            WKWebsiteDataStore.default().httpCookieStore.setCookie(cookie, completionHandler: {
                NSLog(" > user_default : \(UserDefaults.standard.string(forKey: "JSESSIONID"))")
                self.webView.configuration.websiteDataStore.httpCookieStore.setCookie(cookie)
                
                self.re_set_up_user_controller()
            })
        }
        decisionHandler(.allow)
    }
    
    
    
    // 4.
    func webView(_ webView: WKWebView, didCommit navigation: WKNavigation!) {
        
        NSLog("[WKWebView]:::[4]::didCommit")
        NSLog("-----------------------------------------")
        
        if (isKeyPresentInUserDefaults(key: "JSESSIONID")) {
            let cookie = HTTPCookie(properties: [
                .domain: "upmusic.azurewebsites.net",
                .path: "/",
                .name: "JSESSIONID",
                .value: UserDefaults.standard.string(forKey: "JSESSIONID"),
                .secure: "TRUE",
                .expires: NSDate(timeIntervalSinceNow: 31556926)
                ])!
            
            self.webView.configuration.websiteDataStore.httpCookieStore.setCookie(cookie)
            WKWebsiteDataStore.default().httpCookieStore.setCookie(cookie, completionHandler: {
                NSLog(" > user_default : \(UserDefaults.standard.string(forKey: "JSESSIONID"))")
                self.webView.configuration.websiteDataStore.httpCookieStore.setCookie(cookie)
                
                self.re_set_up_user_controller()
            })
        }
        
        
    }
    
    // didCommit 이후에 에러가 발생하는 경우
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        
        NSLog("[WKWebView]:::[4-1]::didFail")
        NSLog("-----------------------------------------")
        NSLog("** Error" + error.localizedDescription)
        
        showActivityIndicator(show: false)
        
        if (isKeyPresentInUserDefaults(key: "JSESSIONID")) {
            let cookie = HTTPCookie(properties: [
                .domain: "upmusic.azurewebsites.net",
                .path: "/",
                .name: "JSESSIONID",
                .value: UserDefaults.standard.string(forKey: "JSESSIONID"),
                .secure: "TRUE",
                .expires: NSDate(timeIntervalSinceNow: 31556926)
                ])!
            
            self.webView.configuration.websiteDataStore.httpCookieStore.setCookie(cookie)
            WKWebsiteDataStore.default().httpCookieStore.setCookie(cookie, completionHandler: {
                NSLog(" > user_default : \(UserDefaults.standard.string(forKey: "JSESSIONID"))")
                self.webView.configuration.websiteDataStore.httpCookieStore.setCookie(cookie)
                
                self.re_set_up_user_controller()
            })
        }
        
    }
    
    
    // 5.
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        NSLog("[WKWebView]:::[5]::didFinish")
        NSLog("-----------------------------------------")
        showActivityIndicator(show: false)
        
        if (isKeyPresentInUserDefaults(key: "JSESSIONID")) {
            let cookie = HTTPCookie(properties: [
                .domain: "upmusic.azurewebsites.net",
                .path: "/",
                .name: "JSESSIONID",
                .value: UserDefaults.standard.string(forKey: "JSESSIONID"),
                .secure: "TRUE",
                .expires: NSDate(timeIntervalSinceNow: 31556926)
                ])!
            
            self.webView.configuration.websiteDataStore.httpCookieStore.setCookie(cookie)
            WKWebsiteDataStore.default().httpCookieStore.setCookie(cookie, completionHandler: {
                NSLog(" > user_default : \(UserDefaults.standard.string(forKey: "JSESSIONID"))")
                self.webView.configuration.websiteDataStore.httpCookieStore.setCookie(cookie)
                
                self.re_set_up_user_controller()
                
            })
        }
        
        let ds = WKWebsiteDataStore.default()
        let cookies = ds.httpCookieStore
        cookies.getAllCookies { (cookies: [HTTPCookie]) in
            var cookieDict = [String : AnyObject]()
            for cookie in cookies {
                NSLog("[Saved cookie]: \(cookie.name)")
                cookieDict[cookie.name] = cookie.properties as AnyObject?
                
                if (cookie.name == "JSESSIONID") {
                    NSLog(" > [JSESSIONID] : \(cookie.value)")
                    UserDefaults.standard.set(cookie.value, forKey: "JSESSIONID")
                    //                    UserDefaults.standard.set(cookie, forKey: "JSESSIONID_COOKIE") // ERROR
                    
                    
                }
                
                if (cookie.name == "upmusic-remember-me-cookie") {
                    NSLog(" > [upmusic-remember-me-cookie] : \(cookie.value)")
                }
                
            }
            UserDefaults.standard.set(cookieDict, forKey: "cookiez")
            NSLog("-----------------------------------------")
        }
        
        re_set_up_user_controller()
        
        
//        let session_id = UserDefaults.standard.string(forKey: "JSESSIONID")
        let token = UserDefaults.standard.string(forKey: "TOKEN")
        
        if (UserDefaults.standard.string(forKey: "loginMethod") != "none") {
            if ( UserDefaults.standard.string(forKey: "JSESSIONID") != "") {
                self.requestCheckSession(sessionID: UserDefaults.standard.string(forKey: "JSESSIONID") ?? ""
                    , tokenValue : token ?? "")
            }
        }
        
        let isPlaying = UserDefaults.standard.string(forKey: "isPlaying") ?? "false"
        
        if(isPlaying == "true") {
            print("isPlaying \(isPlaying)")
            set_up_web_view_as_playermode()
        }
        
        
        previousUrl = self.webView.url?.absoluteString
        UserDefaults.standard.set(previousUrl, forKey: "previousUrl")

        
        NSLog(" > [didFinish]::: agent : \(self.webView.customUserAgent)")
        webView.configuration.preferences.javaScriptEnabled = true
    }
    
    
    // custom [Upmusic-server]
    func check_user_session_id_exists() {
        
        NSLog("[check_user_session_id_exists]:::function called")
        NSLog("-----------------------------------------")
        
        if (isKeyPresentInUserDefaults(key: "JSESSIONID")) {
            let session_id = UserDefaults.standard.string(forKey: "JSESSIONID")
            let token = UserDefaults.standard.string(forKey: "TOKEN")
            NSLog("[isKeyPresentInUserDefaults] JSESSIONID  : \(session_id), TOKEN : \(token)")
            
            
            let cookie = HTTPCookie(properties: [
                .domain: "upmusic.azurewebsites.net",
                .path: "/",
                .name: "JSESSIONID",
                .value: session_id,
                .secure: "TRUE",
                .expires: NSDate(timeIntervalSinceNow: 31556926)
                ])!
            self.webView.configuration.websiteDataStore.httpCookieStore.setCookie(cookie)
            WKWebsiteDataStore.default().httpCookieStore.setCookie(cookie, completionHandler: {
                    self.webView.configuration.websiteDataStore.httpCookieStore.setCookie(cookie)
                }
            )
        
        } else {
            NSLog("[isKeyPresentInUserDefaults] nil...")
        }
        
        NSLog("-----------------------------------------")
        
    }
    
    func isKeyPresentInUserDefaults(key: String) -> Bool {
        return UserDefaults.standard.object(forKey: key) != nil
    }
    
    func retrieve_cookies()
    {
        // EXCEPT JSESSION COOKIES......
        NSLog("[retrieve_cookies]:::function called.")
        NSLog("-----------------------------------------")
        
        let ds = WKWebsiteDataStore.default()
        let cookies = ds.httpCookieStore
        let userDefaults = UserDefaults.standard
        
        NSLog("[retrieve_cookies]:::[1]")
        if let cookieDict = userDefaults.dictionary(forKey: "cookiez") {
            
            NSLog("[retrieve_cookies]:::[1-1]")
            var cookies_left = 0
            
            NSLog("[retrieve_cookies]:::[1-1-1]")
            
            
            for (_, cookieProperties) in cookieDict {
                
                NSLog("[retrieve_cookies]:::[1-1-1-1]")
                if let _ = HTTPCookie(properties: cookieProperties as! [HTTPCookiePropertyKey : Any] ) {
                    
                    NSLog("[retrieve_cookies]:::[1-2]")
                    cookies_left += 1
                }
            }
            
            NSLog("[retrieve_cookies]:::[1-1-2]")
            for (_, cookieProperties) in cookieDict {
                NSLog("[retrieve_cookies]:::[1-3]")
                if let cookie = HTTPCookie(properties: cookieProperties as! [HTTPCookiePropertyKey : Any] ) {
                    
                    if (cookie.name == "JSESSIONID") {
                       
                        
                    }
                    
                    cookies.setCookie(cookie, completionHandler: {
                        cookies_left -= 1
                        
                        NSLog("[retrieve_cookies]:::[1-4]")
                        NSLog(" > [\(cookies_left)] LEFT ::::\(cookie.name)")
                        if cookies_left == 0 {
                            NSLog("[All Saved Cookies Printed]")
                            NSLog("-----------------------------------------")
                            
                            self.webView.configuration.websiteDataStore.httpCookieStore.setCookie(cookie)
                            
                            if (UserDefaults.standard.string(forKey: "JSESSIONID") != nil ) {
                                
                                NSLog("[retrieve_cookies]:::[1-5]")
                                let loginCookie = HTTPCookie(properties: [
                                    .domain: "upmusic.azurewebsites.net",
                                    .path: "/",
                                    .name: "JSESSIONID",
                                    .value: UserDefaults.standard.string(forKey: "JSESSIONID"),
                                    .secure: "TRUE",
                                    .expires: NSDate(timeIntervalSinceNow: 31556926)
                                    ])!
                                
                                self.webView.configuration.websiteDataStore.httpCookieStore.setCookie(loginCookie)
                                
                                
                                
                            }
                            
                            NSLog("[retrieve_cookies]:::[1-6]")
                            // MUST ADDED.
                            self.open_site()
                            return
                        }
                    })
                }
            }
            
            NSLog("[retrieve_cookies]:::[1-1-3] maybe info-leak-while-network-connection")
//
//            if cookies_left == 0 {
            if (UserDefaults.standard.string(forKey: "JSESSIONID") != nil ) {
                
                NSLog("[retrieve_cookies]:::[1-1-3-1]")
                let loginCookie = HTTPCookie(properties: [
                    .domain: "upmusic.azurewebsites.net",
                    .path: "/",
                    .name: "JSESSIONID",
                    .value: UserDefaults.standard.string(forKey: "JSESSIONID"),
                    .secure: "TRUE",
                    .expires: NSDate(timeIntervalSinceNow: 31556926)
                    ])!
                
                self.webView.configuration.websiteDataStore.httpCookieStore.setCookie(loginCookie)
                
            }
            // MUST ADDED.
            self.open_site()
            
            NSLog("[retrieve_cookies]:::[1-1-4] end")
            return
//            }
            
            
            
        } else {
            NSLog("[No Saved Cookies]...")
            NSLog("-----------------------------------------")
            open_site()
            return
        }
    }
    
    @objc func dispatch_share() {
        
        var provider = ""
        var shareUrl = ""
        
        provider = UserDefaults.standard.string(forKey: "shareProvider") ?? ""
        shareUrl = UserDefaults.standard.string(forKey: "shareUrl") ?? ""
        
        NSLog(" > [dispatch_share] : provider - \(String(describing: provider)), shareUrl - \(shareUrl)")
        
        if(provider == "facebook") {
            
//            shareTapped(urlToShare: shareUrl)
            
            shareFacebook(urlToShare: shareUrl)
        }
//
        if(provider == "twitter") {
            
            shareTapped(urlToShare: shareUrl)
            
//            shareTwitter(urlToShare: shareUrl)
        }
        
        if(provider == "kakao") {
            shareTapped(urlToShare: shareUrl)
        }

//
        if(provider == "naver") {
            shareTapped(urlToShare: shareUrl)
            
        }
//
//
        if(provider == "instagram") {
            let url = URL(string: shareUrl)
            if let data = try? Data(contentsOf: url!)
            {
                let image: UIImage = UIImage(data: data) ?? UIImage(named: "appicon")!
                shareOnInstagram(image)
            }
        }
//
//        if(provider == "upmusic") {
//
//        }
//
    }
    
    func shareInstagram(photo: UIImage) {
        var docFile: UIDocumentInteractionController?
        let instagramURL = URL(string: "instagram://app")
        if UIApplication.shared.canOpenURL(instagramURL!) {
            let imageData = UIImageJPEGRepresentation(photo, 100) // image to upload
            let writePath = (NSTemporaryDirectory() as NSString).appendingPathComponent("instagram.igo")
            do {
                try imageData?.write(to: URL(fileURLWithPath: writePath), options: .atomic)
            } catch {
                print(error)
            }
            let fileURL = URL(fileURLWithPath: writePath)
            docFile = UIDocumentInteractionController(url: fileURL)
            docFile?.delegate = self
            docFile?.uti = "com.instagram.exlusivegram"
            if UIDevice.current.userInterfaceIdiom == .phone {
                docFile?.presentOpenInMenu(from: (self.view.bounds), in: (self.view)!, animated: true)
            } else {
                
            }
            
        } else {
            // your custom alert function
            
            /**
             //CHEck
             <key>LSApplicationQueriesSchemes</key>
             <array>
             <string>instagram</string>
             </array>
             */
            NSLog("Instagram not installed")
        }
    }
    
    func shareOnInstagram(_ photo: UIImage) {
        
        
        let instagramUrl = URL(string: "instagram://app")!
        
        if UIApplication.shared.canOpenURL(instagramUrl) {
            let imageData = UIImageJPEGRepresentation(photo, 1.0)!
            
            let writePath = URL(fileURLWithPath: NSTemporaryDirectory()).appendingPathComponent("instagram.igo")
            do {
                try imageData.write(to: writePath)
                var documentsInteractionsController = UIDocumentInteractionController(url: writePath)
                documentsInteractionsController.delegate = self as! UIDocumentInteractionControllerDelegate
                documentsInteractionsController.uti = "com.instagram.exclusivegram"
                documentsInteractionsController.presentOpenInMenu(from: CGRect.zero, in: self.view, animated: true)
            }catch {
                return
            }
        } else {
            let alertController = UIAlertController(title: "Install Instagram", message: "You need Instagram app installed to use this feature. Do you want to install it now?", preferredStyle: .alert)
            let installAction = UIAlertAction(title: "Install", style: .default, handler: { (action) in
                //redirect to instagram
            })
            alertController.addAction(installAction)
            
            let laterAction = UIAlertAction(title: "Later", style: .cancel, handler: nil)
            alertController.addAction(laterAction)
            
            present(alertController, animated: true, completion: nil)
        }
    }
    
    
    func showAlert(service:String)
    {
        let alert = UIAlertController(title: "Error", message: "You are not connected to \(service)", preferredStyle: .alert)
        let action = UIAlertAction(title: "Dismiss", style: .cancel, handler: nil)
        
        alert.addAction(action)
        present(alert, animated: true, completion: nil)
    }
    
    
    func shareTapped(urlToShare: String) {
        let activityController = UIActivityViewController(activityItems: [urlToShare], applicationActivities: nil)
        
        var provider = ""
        var shareUrl = ""
        
        provider = UserDefaults.standard.string(forKey: "shareProvider") ?? ""
        shareUrl = UserDefaults.standard.string(forKey: "shareUrl") ?? ""
        
        activityController.excludedActivityTypes = [.addToReadingList,
                                                        .airDrop,
                                                        .assignToContact,
                                                        .copyToPasteboard,
                                                        .mail,
                                                        .message,
                                                        .openInIBooks,
                                                        .print,
                                                        .saveToCameraRoll,
                                                        .postToWeibo,
                                                        .copyToPasteboard,
                                                        .saveToCameraRoll,
                                                        .postToFlickr,
                                                        .postToVimeo,
                                                        .postToTencentWeibo,
                                                        .markupAsPDF,
                                                        .postToFacebook
                                                        ]
        
        if (provider == "facebook") {
            
        }
        
        activityController.completionWithItemsHandler = { (nil, completed, _, error)
            in
            if completed {
                NSLog("completed")
                
                provider = UserDefaults.standard.string(forKey: "shareProvider") ?? ""
                shareUrl = UserDefaults.standard.string(forKey: "shareUrl") ?? ""
                
//                if (provider == "facebook") {
//                    // 추후 수정 // 페이스북 앱이 열렸다면.
//                    self.requestShareReward()
//                }
                
            } else {
                NSLog("canceled")
            }
        }
        
        present(activityController, animated: true) {
            print("presented")
        }
    }
    
    func requestShareReward() {
        NSLog("[requestShareReward]")
        NSLog("-----------------------------------------")
        var URL = upmusicUrlNoStatic + "/api/point_transaction/shareReward" //.toSecureHTTPSofDomainChange()
        
        let headers: HTTPHeaders = [
            "Accept": "application/json",
            "Content-Type" : "application/json; charset=utf-8"
        ]
        
//        let parameters = [
//            "token" : tokenValue
//        ]
        
        Alamofire.request(URL, method: .post
//            , parameters: parameters
            , encoding: JSONEncoding.default
            , headers: headers
            ).responseJSON { response in
                
                NSLog("[requestShareReward] > \(response.result)")
                NSLog("[requestShareReward] > \(String(describing: response.result.value))")
                NSLog("-----------------------------------------")
                
                switch response.result {
                    
                case .success(let value):
                    let json = JSON(value)
                    if (json["status"].description == "true") {
                        NSLog("[TRUE] JSON: \(json["message"].description)")
                    }
                    
                    if (json["status"].description == "false") {
                        NSLog("[FALSE] JSON: \(json)")
                        return
                        
                        
                    }
                    
                case .failure(let error):
                    
                    print(error)
                }
        }
    }
    
    func shareFacebook(urlToShare : String) {
        
        NSLog(" > [shareFacebook]")
        
        let content = FBSDKShareLinkContent()
        content.contentURL = URL(string: urlToShare)
        FBSDKShareDialog.show(from: self, with: content, delegate: self)
        

    }
    
    func shareTwitter(urlToShare: String) {
        NSLog(" > [shareTwitter]")
        
        //Checking if user is connected to Facebook
        if SLComposeViewController.isAvailable(forServiceType: SLServiceTypeTwitter)
        {
            let post = SLComposeViewController(forServiceType: SLServiceTypeTwitter)!
            
            post.setInitialText("[업뮤직] 공유합니다. \n" + urlToShare)
            
            self.present(post, animated: true, completion: nil)
            
        } else {
            self.showAlert(service: "Twitter")
            
        }
    }
    
    // TODO ... WITH SEMAPHORE...?!
    func requestCheckSession(sessionID: String, tokenValue :String) {
        
        NSLog("[requestCheckSession]")
        NSLog("-----------------------------------------")
        var URL = upmusicUrlNoStatic + "/api/auth/check_session" //.toSecureHTTPSofDomainChange()
        
        let headers: HTTPHeaders = [
            "Accept": "application/json",
            "Content-Type" : "application/json; charset=utf-8"
        ]
        
        let parameters = [
            "token" : tokenValue
        ]
        
        //        Alamofire.SessionManager.default.session.configuration.httpCookieStorage?
        //            .setCookies([cookie], for: NSURL(string: URL) as! URL, mainDocumentURL: nil)
        
        Alamofire.request(URL, method: .post
            , parameters: parameters
            , encoding: JSONEncoding.default
            , headers: headers
            ).responseJSON { response in
                
                NSLog("[CheckSession] > \(response.result)")
                NSLog("[CheckSession] > \(response.result.value)")
                NSLog("-----------------------------------------")
                
                switch response.result {
                    
                case .success(let value):
                    let json = JSON(value)
                    if (json["status"].description == "true") {
                        
                        NSLog("[TRUE] JSON: \(json["message"].description)")
                        
// self.fetchUserOwnTracks(sessionID: UserDefaults.standard.string(forKey: "JSESSIONID") ?? "", token: "")
// NotificationCenter.default.post(name: NSNotification.Name(rawValue: "fetchUserOwnTracks"), object: nil)
// UserDefaults.standard.set("true", forKey: "isFirstLoad")
                    }
                    
                    if (json["status"].description == "false") {
                        NSLog("[FALSE] JSON: \(json)")
                        
                        NSLog("[WKWebView]:::[!]::: /auth/logout")
                        
                        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
                        mainController?.mediaPlayerDetailsView.player.pause()
                        
//                        self.set_up_web_view_as_normalmode()
                        self.set_up_web_view_as_empty_item_mode()
                        
                        //            NotificationCenter.default.post(name: NSNotification.Name(rawValue: "onTimerEnd"), object: nil)
                        self.onTimerEnd()
                        
                        UserDefaults.standard.set("none", forKey: "loginMethod")
                        UserDefaults.standard.set(false, forKey: "rememberMe")
                        UserDefaults.standard.set("", forKey: "JSESSIONID")
                        UserDefaults.standard.set("", forKey: "TOKEN")
                        
                        HTTPCookieStorage.shared.removeCookies(since: Date.distantPast)
                        print("[WebCacheCleaner] All cookies deleted")
                        
                        WKWebsiteDataStore.default().fetchDataRecords(ofTypes: WKWebsiteDataStore.allWebsiteDataTypes()) { records in
                            records.forEach { record in
                                WKWebsiteDataStore.default().removeData(ofTypes: record.dataTypes, for: [record], completionHandler: {})
                                print("[WebCacheCleaner] Record \(record) deleted")
                                let urlRequest = NSURLRequest(url: NSURL(string: upmusicUrlNoStatic)! as URL)
                                
                                let width = CGFloat(self.view.frame.width)
                                let height = CGFloat(self.view.frame.height)
                                //
                                self.webView.load(urlRequest as URLRequest)
                                UserDefaults.standard.set("true", forKey: "isFirstLoad")
                                
                                
                            }
                        }
                        //            open_site()
                        
                        return
                        
                        
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
                    completion()
                }
        }
        
        
        
    }
    
    
    
}


// WHEN FIRST LOAD AND NO USE-WEB VIEW-PLAY-CLICKED. [NOT USED. => ONLY WEB HANDLER.]
extension MainWebViewController : UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return musicTracks.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: cellId, for: indexPath) as! MusicTrackCell
        let track = musicTracks[indexPath.row]
        
        cell.artistNameLabel.tag = indexPath.row
        cell.podcastImageView.tag = indexPath.row
        cell.trackNameLabel.tag = indexPath.row
        
        cell.MoreButton.tag = indexPath.row
        cell.PlayButton.tag = indexPath.row
        cell.RemoveButton.tag = indexPath.row
        
        
        cell.track = track
        
        let tap = UITapGestureRecognizer(target: self, action: #selector(self.playItemClickedAsGesture(sender:)))
        tap.numberOfTapsRequired = 1
        
        cell.artistNameLabel.isUserInteractionEnabled = true
        cell.artistNameLabel.addGestureRecognizer(tap)
        cell.podcastImageView.isUserInteractionEnabled = true
        cell.podcastImageView.addGestureRecognizer(tap)
        cell.trackNameLabel.isUserInteractionEnabled = true
        cell.trackNameLabel.addGestureRecognizer(tap)
        
        cell.PlayButton.isHidden = true
        cell.RemoveButton.isHidden = true
        
        cell.PlayButton.addTarget(self, action: #selector(playItemClicked(sender:)), for: .touchUpInside)
        cell.MoreButton.addTarget(self, action: #selector(requestMore(sender:)), for: .touchUpInside)
        cell.RemoveButton.addTarget(self, action: #selector(removeItemClicked(sender:)), for: .touchUpInside)
        
        
        // reload 후 반영 됨.
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        
        let isEditing = mainController?.mediaPlayerDetailsView.playlistTableView.isEditing ?? false
        if (isEditing) {
//            cell.MoreButton.isHidden = true
            cell.MoreButton.isHidden = false
//            cell.RemoveButton.isHidden = false
            cell.RemoveButton.isHidden = true
            
            cell.artistNameLabel.removeGestureRecognizer(tap)
            cell.podcastImageView.removeGestureRecognizer(tap)
            cell.trackNameLabel.removeGestureRecognizer(tap)
            
        } else {
            cell.MoreButton.isHidden = false
            cell.RemoveButton.isHidden = true
            
            cell.artistNameLabel.addGestureRecognizer(tap)
            cell.podcastImageView.addGestureRecognizer(tap)
            cell.trackNameLabel.addGestureRecognizer(tap)
        }
        
        return cell
    }
    
    // Moving cell
    func tableView(_ tableView: UITableView, editingStyleForRowAt indexPath: IndexPath) -> UITableViewCell.EditingStyle {
        return .none
    }
    func tableView(_ tableView: UITableView, moveRowAt sourceIndexPath: IndexPath, to destinationIndexPath: IndexPath) {

        print("[move] on MainWebView")

        let movedObject = self.musicTracks[sourceIndexPath.row]
        musicTracks.remove(at: sourceIndexPath.row)
        musicTracks.insert(movedObject, at: destinationIndexPath.row)
        debugPrint("\(sourceIndexPath.row) => \(destinationIndexPath.row)")
        NSLog("\(sourceIndexPath.row) => \(destinationIndexPath.row)")
        // To check for correctness enable: self.tableView.reloadData()

        UserDefaults.standard.set(true, forKey: "isEdited")

//        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
//        mainController?.mediaPlayerDetailsView.playlistTableView.reloadData()

    }
    
    //
    @objc func requestMore(sender: AnyObject) {
        print("[requestMore]")
        
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        mainController?.minimizePlayerDetails()
        
        let musicUrl = musicTracks[sender.tag].url ?? ""
        let URL = upmusicUrlNoStatic + musicUrl
        UserDefaults.standard.set(URL, forKey: "reloadUrl")
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "reload"), object: nil)
        
    }
    
    @objc func removeItemClicked(sender: AnyObject) {
        
        print("[removeItemClicked] ")
        
    
        self.musicTracks.remove(at: sender.tag)
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
//        mainController?.mediaPlayerDetailsView.playlistTableView.deleteRows(at: [indexPath], with: .fade)
        mainController?.mediaPlayerDetailsView.playlistTableView.reloadData()
//        print(self.musicTracks)
        
    }
    
    @objc func playItemClicked(sender: AnyObject) {
        print("[playItemClicked] ")
        
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        mainController?.peekPlayerDetails(track: musicTracks[sender.tag], playlistTracks: self.musicTracks)
        
    }
    
    @objc func playItemClickedAsGesture(sender: UIGestureRecognizer) {
        print("[playItemClicked] ")
        
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        mainController?.peekPlayerDetails(track: musicTracks[sender.view?.tag ?? 0], playlistTracks: self.musicTracks)
        
    }
    
}


class WebViewHistory: WKBackForwardList {
//
//    /* Solution 1: return nil, discarding what is in backList & forwardList */
//
//    override var backItem: WKBackForwardListItem? {
//        return nil
//    }
//
//    override var forwardItem: WKBackForwardListItem? {
//        return nil
//    }
    
    /* Solution 2: override backList and forwardList to add a setter */
    
    var myBackList = [WKBackForwardListItem]()
    
    override var backList: [WKBackForwardListItem] {
        get {
            return myBackList
        }
        set(list) {
            myBackList = list
        }
    }
    
    func clearBackList() {
        backList.removeAll()
    }
}


class WebView: WKWebView {
    
    var history: WebViewHistory
    
    override var backForwardList: WebViewHistory {
        return history
    }
    
    
    init(frame: CGRect, configuration: WKWebViewConfiguration, history: WebViewHistory) {
        self.history = history
        super.init(frame: frame, configuration: configuration)
    }
    
    /* Not sure about the best way to handle this part, it was just required for the code to compile... */
    
    required init?(coder: NSCoder) {
        
        if let history = coder.decodeObject(forKey: "history") as? WebViewHistory {
            self.history = history
        }
        else {
            history = WebViewHistory()
        }
        
        super.init(coder: coder)
    }
    
    override func encode(with aCoder: NSCoder) {
        super.encode(with: aCoder)
        aCoder.encode(history, forKey: "history")
    }
}
extension MainWebViewController: WKUIDelegate {
    
    
    
    //alert 처리
    func webView(_ webView: WKWebView, runJavaScriptAlertPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo,
                 completionHandler: @escaping () -> Void) {
        let alertController = UIAlertController(title: "", message: message, preferredStyle: .alert)
        alertController.addAction(UIAlertAction(title: "확인", style: .default, handler: { (action) in
            completionHandler()
        }))
        
        self.present(alertController, animated: true, completion: nil)
    }
    
    //confirm 처리
    func webView(_ webView: WKWebView, runJavaScriptConfirmPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo,
                 completionHandler: @escaping (Bool) -> Void) {
        let alertController = UIAlertController(title: "", message: message, preferredStyle: .alert)
        alertController.addAction(UIAlertAction(title: "확인", style: .default, handler: { (action) in
            completionHandler(true)
        }))
        alertController.addAction(UIAlertAction(title: "취소", style: .default, handler: { (action) in
            completionHandler(false)
        }))
        
        self.present(alertController, animated: true, completion: nil)
    }
    
    //confirm 처리2
    func webView(_ webView: WKWebView, runJavaScriptTextInputPanelWithPrompt prompt: String, defaultText: String?, initiatedByFrame frame: WKFrameInfo,
                 completionHandler: @escaping (String?) -> Void) {
        let alertController = UIAlertController(title: "", message: prompt, preferredStyle: .alert)
        alertController.addTextField { (textField) in
            textField.text = defaultText
        }
        alertController.addAction(UIAlertAction(title: "확인", style: .default, handler: { (action) in
            if let text = alertController.textFields?.first?.text {
                completionHandler(text)
            } else {
                completionHandler(defaultText)
            }
        }))
        
        alertController.addAction(UIAlertAction(title: "취소", style: .default, handler: { (action) in
            completionHandler(nil)
        }))
        
        self.present(alertController, animated: true, completion: nil)
    }
    
    // href="_blank" 처리
//    func webView(_ webView: WKWebView, createWebViewWith configuration: WKWebViewConfiguration, for navigationAction: WKNavigationAction, windowFeatures: WKWindowFeatures) -> WKWebView? {
//        if navigationAction.targetFrame == nil {
//            webView.load(navigationAction.request)
//        }
//        return nil
//    }
    
    func webView(_ webView: WKWebView, createWebViewWith configuration: WKWebViewConfiguration, for navigationAction: WKNavigationAction, windowFeatures: WKWindowFeatures) -> WKWebView? {
        
        //뷰를 생성하는 경우
        let frame = UIScreen.main.bounds
        
        //파라미터로 받은 configuration
        createWebView = WKWebView(frame: frame, configuration: configuration)
        
        //오토레이아웃 처리
        createWebView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        
        createWebView.navigationDelegate = self
        createWebView.uiDelegate = self
        
        
        view.addSubview(createWebView!)
        
        return createWebView!
        
        /* 현재 창에서 열고 싶은 경우
         self.webView.load(navigationAction.request)
         return nil
         */
    }

    ///새창 닫기
    ///iOS9.0 이상
    func webViewDidClose(_ webView: WKWebView) {
        if webView == createWebView {
            createWebView?.removeFromSuperview()
            createWebView = nil
        }
    }
    // 중복적으로 리로드 방지
    public func webViewWebContentProcessDidTerminate(_ webView: WKWebView) {
        webView.reload()
    }
    
}

extension MainWebViewController : StompClientLibDelegate {
    
    @objc func onTimerStart() {
        
        print("[ onTimerStart ]")
        
        if let timer = mTimer {
            //timer 객체가 nil 이 아닌경우에는 invalid 상태에만 시작한다
            if !timer.isValid {
                /** 1초마다 timerCallback함수를 호출하는 타이머 */
                mTimer = Timer.scheduledTimer(timeInterval: 1, target: self, selector: #selector(timerCallback), userInfo: nil, repeats: true)
            }
        }else{
            //timer 객체가 nil 인 경우에 객체를 생성하고 타이머를 시작한다
            /** 1초마다 timerCallback함수를 호출하는 타이머 */
            mTimer = Timer.scheduledTimer(timeInterval: 1, target: self, selector: #selector(timerCallback), userInfo: nil, repeats: true)
        }
    }
    
    @objc func onTimerEnd() {
        
        print("[ onTimerEnd ]")
        
        if let timer = mTimer {
            if(timer.isValid){
                timer.invalidate()
            }
        }
    }
    
    //타이머가 호출하는 콜백함수
    @objc func timerCallback(){
        
        print("[ timerCallback ]")
        
//        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "sendSocketPlaytime"), object: nil)
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "sendSocketIncreasePlayTime"), object: nil)
        
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
//        mainController?.mediaPlayerDetailsView.maximizedRewardLabel.text = UserDefaults.standard.string(forKey: "upPoint")
        let playtime = UserDefaults.standard.integer(forKey: "playtime")
        
        mainController?.mediaPlayerDetailsView.maximizedRewardLabel.text = "\(playtime/60)분 \(playtime%60)초"
        
        
        
    }
    
    @objc func registerSocket(){

        print(" > [STOMP] [registerSocket] socket register function called")
        let completedWSURL = upmusicSocketPath
        print("Completed WS URL : \(completedWSURL)")
        socketURL = NSURL(string: completedWSURL)!

        var urlRequest = URLRequest(url: socketURL as URL)
        urlRequest.httpMethod = "POST"
        urlRequest.setValue("JSESSIONID=\(UserDefaults.standard.string(forKey: "JSESSIONID") ?? "")", forHTTPHeaderField: "Cookie")
//        urlRequest.setValue("JSESSIONID=\(UserDefaults.standard.string(forKey: "JSESSIONID"))", forHTTPHeaderField: "Set-Cookie")
        
        urlRequest.httpShouldHandleCookies = true
        
        if (socketClient.isConnected()) {
            //            socketClient.disconnect()
            socketClient.reconnect(request: urlRequest as NSURLRequest, delegate: self as StompClientLibDelegate)
        } else {
            socketClient.openSocketWithURLRequest(request: urlRequest as NSURLRequest, delegate: self as StompClientLibDelegate)
        }
        
    }

    func stompClientDidConnect(client: StompClientLib!) {
        let topic = upmusicSocketTopicPath
        print("[STOMP] [STOMP]  ::: Socket is Connected : \(topic)")
        socketClient.subscribe(destination: topic)
        // Auto Disconnect after 3 sec
//        socketClient.autoDisconnect(time: 5)
        // Reconnect after 4 sec
//        socketClient.reconnect(request: NSURLRequest(url: socketURL as URL) , delegate: self as StompClientLibDelegate, time: 4.0)
    }

    func stompClientDidDisconnect(client: StompClientLib!) {
        print("[STOMP] [STOMP]  ::: Socket is Disconnected")
        //        socketClient.reconnect(request: NSURLRequest(url: socketURL as URL) , delegate: self as StompClientLibDelegate, time: 4.0)
        
        onTimerEnd()
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
//        mainController?.mediaPlayerDetailsView.player.play()
//        mainController?.mediaPlayerDetailsView.handlePlayPause()
//        mainController?.mediaPlayerDetailsView.isHidden = true
        
//        set_up_web_view_as_normalmode()
//        re_set_up_user_controller()
        TOAST(view: self.view, string: "리워드 연결을 할 수 없습니다.")
//        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "reload"), object: nil)
        
    }

    func stompClientWillDisconnect(client: StompClientLib!, withError error: NSError) {
        print("[STOMP] [STOMP]  ::: Socket will is Disconnected")
        
    }

    func stompClient(client: StompClientLib!, didReceiveMessageWithJSONBody jsonBody: AnyObject?, withHeader header: [String : String]?, withDestination destination: String) {
        print("[STOMP] [stompClient]  ::: DESTIONATION : \(destination)")
        print("[STOMP] [stompClient]  ::: JSON BODY : \(String(describing: jsonBody))")
//
//        let playtime = jsonBody?["playtime"]
//        let upPoint = jsonBody?["upPoint"]
//          // 로그로 찍으면 오류가 생길 수 있음.
//        print("[STOMP] [stompClient]  > playtime: \(String(describing: playtime ?? "ERROR"))")
//        print("[STOMP] [stompClient]  > upPoint: \(String(describing: upPoint ?? "ERROR"))")
//        mainController?.mediaPlayerDetailsView.maximizedRewardLabel.text = jsonBody?["upPoint"] ?? "0.0"
        UserDefaults.standard.set(jsonBody?["playtime"] ?? 0, forKey: "playtime")
        UserDefaults.standard.set(jsonBody?["upPoint"] ?? "0.0", forKey: "upPoint")
        
    }

    func stompClientJSONBody(client: StompClientLib!, didReceiveMessageWithJSONBody jsonBody: String?, withHeader header: [String : String]?, withDestination destination: String) {
        
                print("[STOMP] [stompClientJSONBody] with String ")
//        print("[STOMP] [stompClientJSONBody] with String ::: DESTIONATION : \(destination)")
//        print("[STOMP] [stompClientJSONBody] with String ::: JSON BODY : \(String(describing: jsonBody))")
        
    }

    func serverDidSendReceipt(client: StompClientLib!, withReceiptId receiptId: String) {
        print("[STOMP] [STOMP]  ::: Receipt : \(receiptId)")
    }

    func serverDidSendError(client: StompClientLib!, withErrorMessage description: String, detailedErrorMessage message: String?) {
        print("[STOMP] [STOMP]  ::: Error : \(String(describing: message))")
        
        
        onTimerEnd()
        let mainController = UIApplication.shared.keyWindow?.rootViewController as? MainController
        mainController?.mediaPlayerDetailsView.player.play()
        mainController?.mediaPlayerDetailsView.handlePlayPause()
        
//        set_up_web_view_as_normalmode()
        
//        re_set_up_user_controller()
        
        TOAST(view: self.view, string: "리워드 연결을 할 수 없습니다.")
//        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "reload"), object: nil)
        
        
    }

    func serverDidSendPing() {
        print("[STOMP] [STOMP]  ::: Server Ping")
    }
    
    
    // TODO
    // 웹소켓을 사용하셔야 합니다. Swagger에 정의를 하지는 않았습니다.
    // 로그인 상태이면 아래와 같이 웹소켓을 연결합니다.
    // 1. 소켓 URL은 '/upm-player-websocket' 이며,
    // 2. 구독 경로는 '/user/topic/player'
    // 3. 연결 메시지 경로는 '/app/playtime' 입니다. {'playtime': 0, 'musicTrackId': 음원ID}를 메시지로 보내주세요.
    // 위와 같이 연결이 되면, 구독 경로를 통해 받는 메시지는 playtime(오늘 재생시간 - 초단위)과 streamingRewardStep(리워드 단계 - 1,2,3)입니다.
    // 4. 재생이 시작되면, '/app/increase_playtime'에 {'playtime': 0, 'musicTrackId': 음원ID}를 메시지로 보내주세요.
    // 3번과 4번의 메시지 'playtime'은 무시되는 값으로 그냥 0을 넣어 보내주시면 됩니다.
    // * 리워드는 초당 발생하는 것으로, 음원이 재생되는동안 4번의 메시지를 1초마다 전송하여야 합니다.
    
    @objc func sendSocketPlayTime() {
        
        print("[ sendSocketPlayTime ]")
        
        let currentTrackId = UserDefaults.standard.string(forKey: "currentTrackId")
        let jsonObject : [String : Any]  =
        [
            "playtime": 0,
            "musicTrackId": currentTrackId
            ]
        
        let stringfiedJSON = JSONStringify(value: jsonObject as AnyObject)
//        print("[stringfiedJSON] ::: \(stringfiedJSON)")
//        socketClient.sendMessage(message: stringfiedJSON, toDestination: "/app/playtime", withHeaders: nil, withReceipt: nil)
        socketClient.sendJSONForDict(dict: jsonObject as AnyObject, toDestination: "/app/playtime")
        
    }
    
    @objc func sendSocketIncreasePlayTime() {
        
        print("[ sendSocketIncreasePlayTime ]")
        
        let currentTrackId = UserDefaults.standard.string(forKey: "currentTrackId")
        let jsonObject : [String : Any]  =
        [
            "playtime": 0,
            "musicTrackId": currentTrackId
        ]
        
        let stringfiedJSON = JSONStringify(value: jsonObject as AnyObject)
//        print("[stringfiedJSON] ::: \(stringfiedJSON)")
//        socketClient.sendMessage(message: stringfiedJSON, toDestination: "/app/increase_playtime", withHeaders: nil, withReceipt: nil)
        socketClient.sendJSONForDict(dict: jsonObject as AnyObject, toDestination: "/app/increase_playtime")
//        socketClient.sendJSONForDict(dict: jsonObject as AnyObject, toDestination: "/app/increase_playtime")
//        socketClient.sendJSONForDict(dict: jsonObject as AnyObject, toDestination: "/app/increase_playtime")
//        socketClient.sendJSONForDict(dict: jsonObject as AnyObject, toDestination: "/app/increase_playtime")
//        socketClient.sendJSONForDict(dict: jsonObject as AnyObject, toDestination: "/app/playtime")
    }
    
    func JSONStringify(value: AnyObject,prettyPrinted:Bool = false) -> String{
        
        let options = prettyPrinted ? JSONSerialization.WritingOptions.prettyPrinted : JSONSerialization.WritingOptions(rawValue: 0)
        
        if JSONSerialization.isValidJSONObject(value) {
            do{
                let data = try JSONSerialization.data(withJSONObject: value, options: options)
                if let string = NSString(data: data, encoding: String.Encoding.utf8.rawValue) {
                    return string as String
                }
            }catch {
                print("error")
                //Access error here
            }
        }
        return ""
        
    }
    
}



extension MainWebViewController : FBSDKSharingDelegate {
    func sharer(_ sharer: FBSDKSharing!, didCompleteWithResults results: [AnyHashable : Any]!) {
        
        print(" > [didCompleteWithResults]")
        requestShareReward() // 페이스북 일경우 
        
        return
    }
    
    func sharer(_ sharer: FBSDKSharing!, didFailWithError error: Error!) {
        
        print(" > [didFailWithError]")
        return
    }
    
    func sharerDidCancel(_ sharer: FBSDKSharing!) {
        print(" > [sharerDidCancel]")
        return
    }
    
    
}
