//
//  AppComm.swift
//  Upmusic
//
//  Created by nough on 10/08/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import UIKit
import Foundation

/*
 provider의 경우 naver는 naver,
        facebook은 facebook,
        kakaotalk은 카카오톡로그인 성공시 내려오는 provider값을 보낸다.
 
 code가 200일 경우 성공. 성공시에는
 http://mammaapp.co.kr/front/app/webview/{id}/{provider_id} 페이지로 이동. Id값의 경우에는 result id, provider_id는 parameter 값. 실패시에는 msg값 팝업으로 띄워줌.
 */

struct MammaResponseData {
    let code: Int
    let status: String?
    let msg: String?
    let id: Int?
    let data: Any
}

struct BadgeInfo: Codable {
    let order_cnt: Int
    let push_cnt: Int
    let wait_cnt: Int
    
    init?(json: [String: Any]) {
        guard
            let order_cnt = json["order_cnt"] as? Int,
            let push_cnt = json["push_cnt"] as? Int,
            let wait_cnt = json["wait_cnt"] as? Int
            else {
                return nil
        }
        self.order_cnt = order_cnt
        self.push_cnt = push_cnt
        self.wait_cnt = wait_cnt
    }
}

class AppComm {
    
    var badgeInfo: BadgeInfo? = nil
    
    static let sharedInstance : AppComm = {
        let instance = AppComm()
        //setup code
        return instance
    }()
    
    static func getBadgeInfo() -> BadgeInfo?
    {
        return AppComm.sharedInstance.badgeInfo
    }
    
    static func getUrlData(address: String) -> Data? {
        var result: Data? = nil
        
        let url = URL(string: address)
        let semaphore = DispatchSemaphore(value: 0)
        let task = URLSession.shared.dataTask(with: url!) {(data, response, error) in
            while(true)
            {
                guard error == nil else {
                    break
                }
                guard let data = data else {
                    break
                }
                result = data
                break
            }
            semaphore.signal()
        }
        task.resume()
        semaphore.wait()
        return result
    }
    
    static func getUrlData_Post(address: String, params: [String: String]) -> Data? {
        var result: Data? = nil
        
        let url = URL(string: address)
        var request = URLRequest(url: url!)
        
        let isPost = true
        if(isPost)
        {
            request.httpMethod = "POST"
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            //print(params)
            do {
                let http_params = try JSONSerialization.data(withJSONObject: params, options: [])
                request.httpBody = http_params
                print(http_params)
            } catch let error {
                print(error)
            }
        }
        
        let semaphore = DispatchSemaphore(value: 0)
        let task = URLSession.shared.dataTask(with: request) {(data, response, error) in
            while(true)
            {
                guard error == nil else {
                    break
                }
                guard let data = data else {
                    break
                }
                result = data
                break
            }
            semaphore.signal()
        }
        task.resume()
        semaphore.wait()
        return result
    }
    
    static func getUrlString(address: String) -> String? {
        guard let data = getUrlData(address: address) else {
            return nil
        }
        return String(data: data, encoding: String.Encoding.utf8)!
    }
    
    static func getUrlString_Post(address: String, params: [String: String]) -> String? {
        guard let data = getUrlData_Post(address: address, params: params) else {
            return nil
        }
        return String(data: data, encoding: String.Encoding.utf8)!
    }
    
    static func mammaApiCall_setPushToken(viewController: UIViewController)
    {
        repeat
        {
            guard let user_token = AppComm.getUserToken() else {
                print("[yangs::AC::mammaApiCall_setPushToken] getUserToken -> nil")
                break
            }
            
            guard let push_token = AppComm.getPushToken() else {
                print("[yangs::AC::mammaApiCall_setPushToken] getPushToken -> nil")
                let alert = UIAlertController(title: "안내", message: "알림을 위한 푸시 서비스 토큰 정보를 얻어오는데 실패했습니다.", preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "확인", style: .default, handler: { action in
                    //AppUtils.openWebView(viewController: viewController, urlString: Define.LOGOUT_URL)
                }))
                viewController.present(alert, animated: true)
                break
            }
            
            guard
                let res = AppComm.setPushToken(user_token: user_token, push_token: push_token)
                else {
                    print("[yangs::AC::mammaApiCall_setPushToken] setPushToken -> error")
                    let alert = UIAlertController(title: "안내", message: "알림을 위한 토큰 정보 전송에 실패했습니다.", preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "확인", style: .default, handler: { action in
                        //AppUtils.openWebView(viewController: viewController, urlString: Define.LOGOUT_URL)
                    }))
                    viewController.present(alert, animated: true)
                    break
            }
            print("[yangs::AC::mammaApiCall_setPushToken] success res(\(res))")
        } while(false)
    }
    
    static func mammaApiCall_setSnsProvider(viewController: UIViewController, sns: SnsLoginInfo) -> MammaResponseData?
    {
        return AppComm.setSnsProvider(sns: sns)
    }
    
    static func mammaApiCall_getBadgeInfo(viewController: UIViewController)
    {
        repeat
        {
            var user_token = AppComm.getUserToken()
            if(user_token==nil) {
                print("[yangs::AC::mammaApiCall_getBadgeInfo] error getCookieUserToken(nil)")
                guard let sns = SnsManager.getLoginInfo() else {
                    print("[yangs::AC::mammaApiCall_getBadgeInfo] error sns(nil)")
                    break
                }
                if(sns.has_email) {
                    user_token = sns.email
                }
                else {
                    user_token = sns.name
                }
                if(user_token==nil)
                {
                    DispatchQueue.main.async() {
                        let alert = UIAlertController(title: "안내", message: "로그인 정보가 없습니다.\n다시 로그인 후 시도하세요.", preferredStyle: .alert)
                        alert.addAction(UIAlertAction(title: "확인", style: .default, handler: { action in
                            //AppUtils.openWebView(viewController: viewController, urlString: Define.LOGOUT_URL)
                        }))
                        //viewController.present(alert, animated: true)
                    }
                    break
                }
            }
            
            guard
                let res = AppComm.getBadgeInfo(user_token: user_token!),
                let data = res.data as? [String: Any]
                else {
                    print("[yangs::AppComm::didSelectRowAt] getBadgeInfo communication error")
                    DispatchQueue.main.async() {
                        let alert = UIAlertController(title: "안내", message: "알림 뱃지 정보를 얻어오는데 실패했습니다.", preferredStyle: .alert)
                        alert.addAction(UIAlertAction(title: "확인", style: .default, handler: { action in
                            //AppUtils.openWebView(viewController: viewController, urlString: Define.LOGOUT_URL)
                        }))
                        viewController.present(alert, animated: true)
                    }
                    return
            }
            
            guard let badge = BadgeInfo(json: data) else {
                print("[yangs::AC::mammaApiCall] BadgeInfo -> error")
                let alert = UIAlertController(title: "안내", message: "알림 뱃지 정보 구문을 분석하는데 실패했습니다.", preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "확인", style: .default, handler: { action in
                    //AppUtils.openWebView(viewController: viewController, urlString: Define.LOGOUT_URL)
                }))
                viewController.present(alert, animated: true)
                return
            }
            AppComm.sharedInstance.badgeInfo = badge
            print("[yangs::WVC::commandMamma] getBadgeInfo res(\(String(describing: badge)))")
        } while(false)
    }
    
    static func isMammaLogin() -> Bool
    {
        guard let user_token = getCookieUserToken() else {
            return false
        }
        return true
    }
    
    static func getSnsProvider() -> String?
    {
        var sns_provider: String? = nil
        sns_provider = "sns_provider_test"
        print("[yangs::AppComm::getSnsProvider] sns_provider(\(sns_provider ?? "nil"))")
        return sns_provider
    }
    
    static func getPushToken() -> String?
    {
        let push_token = Define.fcmToken
        print("[yangs::AppComm::getPushToken] push_token(\(push_token ?? "nil"))")
        return push_token
    }
    
    static func getUserToken() -> String?
    {
        var sns_provider = ""
        var user_token = AppComm.getCookieUserToken()
        if(user_token==nil) {
            print("[yangs::AC::getUserToken] getCookieUserToken(nil)")
            guard let sns = SnsManager.getLoginInfo() else {
                print("[yangs::AC::getUserToken] error sns(nil)")
                return nil
            }
            if(sns.has_email) {
                user_token = sns.email
            }
            else {
                user_token = sns.name
            }
            user_token = user_token?.base64Encoded()
            sns_provider = sns.provider
        }
        let user_id = user_token?.base64Decoded()
        print("[yangs::AppComm::getUserToken] provider(\(sns_provider)) user_id(\(user_id ?? "nil")) user_token(\(user_token ?? "nil"))")
        return user_token
    }
    
    static func getCookieUserToken() -> String?
    {
        var user_token: String? = nil
        if let cookies = HTTPCookieStorage.shared.cookies {
            for cookie in cookies {
                if(cookie.name=="user_token") {
                    user_token = cookie.value
                    break
                }
            }
        }
        print("[yangs::AppComm::getCookieUserToken] user_token(\(user_token ?? "nil"))")
        return user_token
    }
    
    static func getUserID() -> String?
    {
        guard let user_token = getUserToken() else {
            return nil
        }
        //guard let user_id = user_token.decodeUrl()?.base64Decoded() else {
        guard let user_id = user_token.decodeUrl()?.utf8DecodedString()?.base64Decoded() else {
            print("[yangs::AppComm::getUserID] error base64Decoded(\(user_token))")
            return nil
        }
        print("[yangs::AppComm::getUserID] user_id(\(user_id))")
        return user_id
    }
    
    static func getCookieUserID() -> String?
    {
        guard let user_token = getCookieUserToken() else {
            return nil
        }
        guard let user_id = user_token.base64Decoded() else {
            print("[yangs::AppComm::getCookieUserID] error base64Decoded(\(user_token))")
            return nil
        }
        print("[yangs::AppComm::getCookieUserID] user_id(\(user_id))")
        return user_id
    }
    
    static func setPushToken(user_token: String, push_token: String) -> MammaResponseData?
    {
        var result: MammaResponseData? = nil
        let urlString = String(format: "%@/token/user/%@/iOS:%@", Define.SERVER_BASE, user_token, push_token)
        print("[yangs::AppComm::setPushToken] request(\(urlString))")
        
        guard let data = AppComm.getUrlData(address: urlString) else {
            return nil
        }
        
        do {
            if let json = try JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [String: Any] {
                print("[yangs::AppComm::setPushToken] response(\(json))")
                guard let code = json["code"] as? Int else {
                    print("[yangs::AppComm::setPushToken] error MAMMA API protocol(\(json))")
                    return nil
                }
                let status = json["status"] as? String
                let msg = json["msg"] as? String
                let id = json["id"] as? Int
                let data = json["data"] as Any
                result = MammaResponseData(code: code, status: status, msg: msg, id: id, data: data)
                return result
            }
        } catch let error {
            print("[yangs::AppComm::setPushToken] error parse json")
            print(error)
        }
        
        return nil
    }
    
    static func setSnsProvider(sns: SnsLoginInfo) -> MammaResponseData?
    {
        var result: MammaResponseData? = nil
//        let urlString = String(format: "%@/front/app/callback", Define.SERVER_BASE)
        let urlString = String(format: "%@", Define.SERVER_BASE)
        print("[yangs::AC::getSnsProvider] request(\(urlString))")
        
        var params = ["email" : "", "name" : "", "provider" : "", "provider_id" : ""]
        params["email"] = sns.email
        params["name"] = sns.name
        params["provider"] = sns.provider
        params["provider_id"] = sns.provider_id
        print(params)
        //guard let data = AppComm.getUrlString(address: urlString) else {
        guard let data = AppComm.getUrlData_Post(address: urlString, params: params) else {
            return nil
        }
        
        let response = String.init(data: data, encoding: .utf8)
        print("[yangs::AC::getSnsProvider] response(\(response ?? "nil"))")
        
        do {
            if let json = try JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [String: Any] {
                print("[yangs::AppComm::setSnsProvider] response(\(json))")
                guard let code = json["code"] as? Int else {
                    print("[yangs::AppComm::setSnsProvider] error MAMMA API protocol(\(json))")
                    return nil
                }
                let status = json["status"] as? String
                let msg = json["msg"] as? String
                let id = json["id"] as? Int
                let data = json["data"] as Any
                result = MammaResponseData(code: code, status: status, msg: msg, id: id, data: data)
                return result
            }
        } catch let error {
            print("[yangs::AppComm::setSnsProvider] error parse json")
            print(error)
        }
        
        return nil
    }
    
    static func processSnsProviderResult(sns: SnsLoginInfo, response: MammaResponseData?) -> Bool
    {
        guard let response = response else {
            print("[yangs::AC::processSnsProviderResult] error response")
            return false
        }
        guard let top = AppUtils.getTopViewController() else {
            print("[yangs::AC::processSnsProviderResult] error getTopViewController(nil)")
            return false
        }
        
        if(response.code==200) {
            guard let id = response.id else {
                print("[yangs::AC::processSnsProviderResult] error SnsProvider id(nil)")
                return false
            }
            let provider_id = sns.provider_id
            //http://mammaapp.co.kr/front/app/webview/{id}/{provider_id}
            let urlString = String(format: "%@/front/app/webview/\(id)/\(provider_id)", Define.SERVER_BASE)
            DispatchQueue.main.async() {
                AppUtils.openWebView(viewController: AppUtils.getTopViewController(), urlString: urlString)
            }
            return true
        }
        else {
            guard let msg = response.msg else {
                print("[yangs::AC::processSnsProviderResult] error SnsProvider msg(nil)")
                return false
            }
            let alert = UIAlertController(title: "안내", message: msg, preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "확인", style: .default, handler: { action in
                //AppUtils.openWebView(viewController: viewController, urlString: Define.LOGOUT_URL)
            }))
            top.present(alert, animated: true)
            return false
        }
    }
    
    static func getBadgeInfo(user_token: String) -> MammaResponseData?
    {
        var result: MammaResponseData? = nil
        let urlString = String(format: "%@/summary/%@", Define.SERVER_BASE, user_token)
        print("[yangs::AppComm::getBadgeInfo] request(\(urlString))")
        
        guard let data = AppComm.getUrlData(address: urlString) else {
            return nil
        }
        
        do {
            //if let json = try JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [String: Any] {
            if let json = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] {
                print("[yangs::AppComm::getBadgeInfo] response(\(json))")
                guard let code = json["code"] as? Int else {
                    print("[yangs::AppComm::getBadgeInfo] error MAMMA API protocol(\(json))")
                    return nil
                }
                let status = json["status"] as? String
                let msg = json["msg"] as? String
                let id = json["id"] as? Int
                let data = json["data"] as Any
                result = MammaResponseData(code: code, status: status, msg: msg, id: id, data: data)
                return result
            }
        } catch let error {
            print("[yangs::AppComm::getBadgeInfo] error parse json")
            print(error)
        }
        
        return nil
    }
}
