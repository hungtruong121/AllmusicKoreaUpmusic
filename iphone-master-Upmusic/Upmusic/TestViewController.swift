//
//  TestViewController.swift
//  Upmusic
//
//  Created by nough on 10/08/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import UIKit

class TestViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet weak var testTableView: UITableView!
    
    let testItemList = [
        "웹뷰",
        "QR Reader",
        "로그인",
        "로그아웃",
        "주문, 대기 이벤트 알림",
        "대기",
        "이벤트",
        "MY페이지",
        "네이버 로그인",
        "네이버 로그아웃",
        "카카오 로그인",
        "카카오 로그아웃",
        "페이스북 로그인",
        "페이스북 로그아웃",
        "푸시 테스트 페이지",
        "QR코드 이미지",
        "KG 올앳 결제 테스트 페이지",
        "MAMMA API setPushToken",
        "MAMMA API setSnsProvider",
        "MAMMA API getBadgeInfo",
        "MAMMA API allProcess"
    ]
    
    let testUrlList = [
        "http://www.naver.com",
        "QR Reader",
        Define.LOGIN_URL,
        Define.LOGOUT_URL,
        Define.PUSH_URL,
        Define.WAIT_URL,
        Define.EVENT_URL,
        Define.MYPAGE_URL,
        "네이버 로그인",
        "네이버 로그아웃",
        "카카오 로그인",
        "카카오 로그아웃",
        "페이스북 로그인",
        "페이스북 로그아웃",
        Define.TEST_PUSH_URL,
        Define.TEST_QR_IMAGE_URL,
        Define.TEST_ALLAT_PAY,
        "MAMMA API setPushToken",
        "MAMMA API setSnsProvider",
        "MAMMA API getBadgeInfo",
        "MAMMA API allProcess"
    ]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        testTableView.delegate = self
        testTableView.dataSource = self
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return testItemList.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = testTableView.dequeueReusableCell(withIdentifier: "testItemCell")
        cell?.textLabel?.text = "\(indexPath.row+1). \(testItemList[indexPath.row])"
        return cell!
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        if(testItemList[indexPath.row]=="QR Reader")
        {
            let storyboard: UIStoryboard = self.storyboard!
            let vc = storyboard.instantiateViewController(withIdentifier: "ScannerViewController") as! ScannerViewController
            present(vc, animated: true, completion: nil)
        }
        else if( testItemList[indexPath.row]=="네이버 로그인")
        {
            SnsManager.loginNaver(login: true)
        }
        else if( testItemList[indexPath.row]=="네이버 로그아웃")
        {
            SnsManager.loginNaver(login: false)
        }
        else if( testItemList[indexPath.row]=="카카오 로그인")
        {
            SnsManager.loginKakao(login: true, trial: 1)
        }
        else if( testItemList[indexPath.row]=="카카오 로그아웃")
        {
            SnsManager.loginKakao(login: false, trial: 1)
        }
        else if( testItemList[indexPath.row]=="페이스북 로그인")
        {
            SnsManager.loginFacebook(login: true)
        }
        else if( testItemList[indexPath.row]=="페이스북 로그아웃")
        {
            SnsManager.loginFacebook(login: false)
        }
        else if( testItemList[indexPath.row]=="MAMMA API setPushToken")
        {
            
            DispatchQueue.global(qos: .background).async {
                guard let user_token = AppComm.getCookieUserToken() else {
                    print("[yangs::AC::didSelectRowAt] getCookieUserToken -> nil")
                    return
                }
                
                guard let push_token = AppComm.getPushToken() else {
                    print("[yangs::AC::didSelectRowAt] getPushToken -> nil")
                    return
                }
                
                guard let res = AppComm.setPushToken(user_token: user_token, push_token: push_token) else {
                    print("[yangs::AC::didSelectRowAt] setPushToken -> error")
                    return
                }
                print("[yangs::AppComm::didSelectRowAt] getBadgeInfo res(\(res))")
            }
        }
        else if( testItemList[indexPath.row]=="MAMMA API setSnsProvider")
        {
            /*
             DispatchQueue.global(qos: .background).async {
             guard let provider = AppComm.getSnsProvider() else {
             print("[yangs::AC::didSelectRowAt] getSnsProvider -> nil")
             return
             }
             
             guard let res = AppComm.setSnsProvider(provider: provider) else {
             print("[yangs::AppComm::didSelectRowAt] setSnsProvider communication error")
             return
             }
             print("[yangs::AppComm::didSelectRowAt] setSnsProvider res(\(res))")
             }
             */
        }
        else if( testItemList[indexPath.row]=="MAMMA API getBadgeInfo")
        {
            DispatchQueue.global(qos: .background).async {
                guard let user_token = AppComm.getCookieUserToken() else {
                    print("[yangs::AC::didSelectRowAt] getCookieUserToken -> nil")
                    return
                }
                
                guard
                    let res = AppComm.getBadgeInfo(user_token: user_token),
                    let data = res.data as? [String: Any]
                    else {
                        print("[yangs::AppComm::didSelectRowAt] getBadgeInfo communication error")
                        return
                }
                let badge = BadgeInfo(json: data)
                print("[yangs::AppComm::didSelectRowAt] getBadgeInfo res(\(String(describing: badge)))")
            }
        }
        else if(testItemList[indexPath.row]=="MAMMA API allProcess")
        {
            
        }
        else if( testItemList[indexPath.row]=="웹뷰" ||
            testItemList[indexPath.row]=="KG 올앳 결제 테스트 페이지")
        {
            let storyboard: UIStoryboard = self.storyboard!
            let vc = storyboard.instantiateViewController(withIdentifier: "WebViewController") as! WebViewController
            vc.mainUrl = testUrlList[indexPath.row]
            present(vc, animated: true, completion: nil)
        }
        else
        {
            let storyboard: UIStoryboard = self.storyboard!
            let vc = storyboard.instantiateViewController(withIdentifier: "WebViewController") as! WebViewController
            vc.mainUrl = testUrlList[indexPath.row]
            present(vc, animated: true, completion: nil)
        }
    }
}
