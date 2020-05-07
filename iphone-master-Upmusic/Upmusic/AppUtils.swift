//
//  AppUtils.swift
//  Upmusic
//
//  Created by nough on 10/08/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import UIKit
import Foundation

class AppUtils {
    
    var webViewController: UIViewController? = nil
    
    static let sharedInstance : AppUtils = {
        let instance = AppUtils()
        //setup code
        return instance
    }()
    
    static func getWebViewController() -> UIViewController?
    {
        return AppUtils.sharedInstance.webViewController
    }
    
    static func getTopViewController() -> UIViewController?
    {
        if var topController = UIApplication.shared.keyWindow?.rootViewController {
            while let presentedViewController = topController.presentedViewController {
                topController = presentedViewController
            }
            return topController
        }
        
        return nil
    }
    
    static func openTestView(viewController: UIViewController) {
        let storyboard: UIStoryboard = viewController.storyboard!
        let vc = storyboard.instantiateViewController(withIdentifier: "TestViewController") as! TestViewController
        viewController.present(vc, animated: true, completion: nil)
    }
    
    static var retryOpenQrCount = 0
    static func openQrView(viewController: UIViewController?) {
        retryOpenQrCount = 0
        DispatchQueue.main.async() {
            __openQrView(viewController: viewController)
        }
    }
    
    static func __openQrView(viewController: UIViewController?) {
        print("[yangs::AU::openQrView]")
        var isRetry: Bool = false
        repeat
        {
            guard let storyboard = getWebViewController()?.storyboard else {
                //guard let storyboard = getTopViewController()?.storyboard else {
                isRetry = true
                break
            }
            guard let vc = storyboard.instantiateViewController(withIdentifier: "ScannerViewController") as? ScannerViewController else {
                isRetry = true
                break
            }
            guard let top = getTopViewController() else {
                isRetry = true
                break;
            }
            if (top is ScannerViewController) {
                break;
            }
            top.present(vc, animated: false, completion: nil)
            break
        } while(false)
        
        if(isRetry) {
            let retryTime = (retryOpenQrCount<20) ? 200 : 1000
            let time = DispatchTime.now() + .milliseconds(retryTime)
            DispatchQueue.main.asyncAfter(deadline: time) {
                print("[yangs::AU::openQrView] [2] retry")
                retryOpenQrCount += 1
                __openQrView(viewController: getTopViewController())
            }
        }
    }
    
    static func openWebView(viewController: UIViewController?, urlString: String) {
        DispatchQueue.main.async() {
            __openWebView(viewController: viewController, urlString: urlString)
        }
    }
    
    static func __openWebView(viewController: UIViewController?, urlString: String) {
        print("[yangs::AU::openWebView] url(\(urlString)")
        var isRetry: Bool = false
        repeat
        {
            guard let storyboard = getTopViewController()?.storyboard else {
                isRetry = true
                break
            }
            guard let vc = storyboard.instantiateViewController(withIdentifier: "WebViewController") as? WebViewController else {
                isRetry = true
                break
            }
            vc.mainUrl = urlString
            getTopViewController()?.present(vc, animated: false, completion: nil)
            let top = getTopViewController()
            if top==nil || !(top is WebViewController) {
                print("[yangs::AU::openWebView] [1] retry")
                let time = DispatchTime.now() + .milliseconds(200)
                DispatchQueue.main.asyncAfter(deadline: time) {
                    __openWebView(viewController: getWebViewController(), urlString: urlString)
                }
            }
            /*
             else {
             let wvc = top as! WebViewController;
             wvc.moveUrl(urlString: urlString)
             }
             */
            break
        } while(false)
        
        if(isRetry) {
            let time = DispatchTime.now() + .milliseconds(200)
            DispatchQueue.main.asyncAfter(deadline: time) {
                print("[yangs::AU::openWebView] [2] retry")
                AppUtils.openWebView(viewController: getTopViewController(), urlString: urlString)
            }
        }
    }
    
    static func isUpdateAvailable() -> Bool {
        let bundleID = Bundle.main.bundleIdentifier!
        guard
            let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String,
            let url = URL(string: "http://itunes.apple.com/lookup?bundleId=\(bundleID)"),
            let data = try? Data(contentsOf: url),
            let json = try? JSONSerialization.jsonObject(with: data, options: .allowFragments) as? [String: Any],
            let results = json?["results"] as? [[String: Any]],
            results.count > 0,
            let appStoreVersion = results[0]["version"] as? String
            else { return false }
        if (version < appStoreVersion) {
            return true
        }
        
        return false
    }
    
    static func getAppStoreVersion() -> String? {
        let bundleID = Bundle.main.bundleIdentifier!
        guard
            let url = URL(string: "http://itunes.apple.com/lookup?bundleId=\(bundleID)"),
            let data = try? Data(contentsOf: url),
            let json = try? JSONSerialization.jsonObject(with: data, options: .allowFragments) as? [String: Any],
            let results = json?["results"] as? [[String: Any]],
            results.count > 0,
            let appStoreVersion = results[0]["version"] as? String
            else { return "" }
        
        return appStoreVersion
    }
    
    static func processPushMessage(userInfo: [AnyHashable : Any])
    {
        print("[yangs::AU::processPushMessage] start")
        var parseFailed: Bool = false
        repeat
        {
            guard let aps = userInfo[AnyHashable("aps")] as? NSDictionary else {
                print("[yangs::AU::processPushMessage] push parse failed [1]")
                parseFailed = true
                break
            }
            
            ///< Firebase 푸시 규격
            if let alert = aps["alert"] as? String {
                alertPushMessage(title: "알림", message: alert, moveUrl: nil)
                return;
            }
            
            ///< 맘마 푸시 규격
            guard
                let alert = aps["alert"] as? NSDictionary,
                let body = alert["body"] as? String,
                let title = alert["title"] as? String
                else {
                    print("[yangs::AU::processPushMessage] push parse failed [2]")
                    parseFailed = true
                    break;
            }
            
            let url = userInfo[AnyHashable("url")] as? String
            alertPushMessage(title: title, message: body, moveUrl: url)
            break;
        } while(false)
        
        if(parseFailed) {
            alertPushMessage(title: "메시지", message: "알림 수신 정보에 오류가 있습니다.", moveUrl: nil)
        }
    }
    
    static func alertPushMessage(title: String, message: String, moveUrl: String?)
    {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        
        if(Define.isOpenFirstViewController==false) {
            Define.isPushOnStart = true
        }
        
        alert.addAction(UIAlertAction(title: "확인", style: .default, handler: { action in
            print("[yangs::AU::processPushMessage] moveUrl(\(moveUrl))")
            if(Define.isPushOnStart) {
                if let url = moveUrl {
                    AppUtils.openWebView(viewController: AppUtils.getTopViewController(), urlString: url)
                }
                else {
                    AppUtils.openWebView(viewController: AppUtils.getTopViewController(), urlString: Define.LOGIN_URL)
                }
            }
            else {
                if let url = moveUrl {
                    AppUtils.openWebView(viewController: AppUtils.getTopViewController(), urlString: url)
                }
            }
        }))
        alert.addAction(UIAlertAction(title: "취소", style: .cancel, handler: { action in
            if(Define.isPushOnStart) {
                AppUtils.openWebView(viewController: AppUtils.getTopViewController(), urlString: Define.LOGIN_URL)
            }
        }))
        
        AppUtils.getTopViewController()?.present(alert, animated: true)
    }
}
