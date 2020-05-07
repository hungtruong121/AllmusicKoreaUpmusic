//
//  BaseViewController.swift
//  yunstimeschedule
//
//  Created by cheche on 2018. 4. 15..
//  Copyright © 2018년 thecsy. All rights reserved.
//

import Foundation

import UIKit

protocol URLProtocol {
    func URLSuccess(_ data: [String: Any])
    func URLError()
}

class BaseViewController:UIViewController{

    var LoadingView:LoadingViewController?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
    }
    
    func initLoading( _ later:()->()) {
        
        if(self.LoadingView == nil){
            self.LoadingView = LoadingViewController.shared as? LoadingViewController
        }
        later()
    }
    
    func showLoading() {

        self.initLoading(
        {
            if(!(self.LoadingView?.visble)!){
                DispatchQueue.main.async {
                    self.LoadingView?.visble = true
                    self.view.window?.addSubview(self.LoadingView!.view)
                }
            }
        })
    }
    
    func endLoading() {
        
        if(LoadingView != nil){
            
            if((self.LoadingView?.visble)!){
                
                self.LoadingView?.visble = false
                self.LoadingView!.view.removeFromSuperview()
                self.LoadingView?.view.window?.removeFromSuperview()
            }
        }
    }
    
    func showToast(toastTitle: String, toastMsg: String, interval: Double) {
        
        // show message
        let message = UIAlertController(title: toastTitle, message: toastMsg, preferredStyle: UIAlertControllerStyle.alert)
        self.present(message, animated: true, completion: {})
        
        Timer.scheduledTimer(timeInterval: interval, target: self, selector: #selector(dismissToastMessage(sender: )), userInfo: nil, repeats: true)
        
    }

    @objc func dismissToastMessage(sender: AnyObject?) {
        var timer = sender as? Timer
        self.dismiss(animated: true, completion: {
            timer?.invalidate()
            timer = nil
        })
    }
    
    func setURLRequestHeader(_ tos: String, _ parameter: [String: String], _ urlProtocol: URLProtocol) {
        var request = URLRequest(url: URL(string: tos)!)
        request.httpMethod = "POST"
        for i in parameter {
            request.addValue(i.value, forHTTPHeaderField: i.key)
        }
        
        let session = URLSession.shared
        
        session.dataTask(with: request) {(data, response, err) in
            
            if data != nil {
                let responseJSON = try? JSONSerialization.jsonObject(with: data!, options: [])
                if let responseJSON = responseJSON as? [String: Any] {

                    DispatchQueue.main.async() {
                    
                        urlProtocol.URLSuccess(responseJSON)
                        
                    }
                } else {
                    DispatchQueue.main.async() {
                        
                        print(err.debugDescription)
                        urlProtocol.URLError()
                    }
                }
            }
            
            if response != nil {
                if let httpResponse = response as? HTTPURLResponse, let fields = httpResponse.allHeaderFields as? [String : String] { // 쿠키 저장하기
                    let cookies = HTTPCookie.cookies(withResponseHeaderFields: fields, for: response!.url!)
                    HTTPCookieStorage.shared.setCookies(cookies, for: response!.url!, mainDocumentURL: nil)
                }
            }
            
            if err != nil {
                DispatchQueue.main.async() {
                    urlProtocol.URLError()
                }
            }
            
            }.resume()
    }
    
    func getPostString(params:[String:Any]) -> String
    {
        var data = [String]()
        for(key, value) in params
        {
            data.append(key + "=\(value)")
        }
        return data.map { String($0) }.joined(separator: "&")
    }
    
    func setURLRequestBody(_ tos: String, _ body: [String: Any] , _ urlProtocol: URLProtocol) {
        
        print("code : \(tos)")
        
        var request = URLRequest(url: URL(string: tos)!)
        request.httpMethod = "POST"
        
        request.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
        let postString = self.getPostString(params: body)
        request.httpBody = postString.data(using: .utf8)
        
        let session = URLSession.shared
        
        session.dataTask(with: request) {(data, response, err) in
            
            if data != nil {
                
                let responseJSON = try? JSONSerialization.jsonObject(with: data!, options: []) as? [String : Any]
                
                if let responseJSON = responseJSON as? [String: Any] {
                    
                    DispatchQueue.main.async() {
                    
                        urlProtocol.URLSuccess(responseJSON)
                        
                    }
                }
            }
            
            if response != nil {
                
                if let httpResponse = response as? HTTPURLResponse, let fields = httpResponse.allHeaderFields as? [String : String] { // 쿠키 저장하기
                    let cookies = HTTPCookie.cookies(withResponseHeaderFields: fields, for: response!.url!)
                    HTTPCookieStorage.shared.setCookies(cookies, for: response!.url!, mainDocumentURL: nil)
                }
            }else{
                
                //TO-DO exeption
                print("ERR nil")
                
            }
            
            if err != nil {
                
                DispatchQueue.main.async() {
                    
                    urlProtocol.URLError()
                }
            }
            
            }.resume()
    }

}


