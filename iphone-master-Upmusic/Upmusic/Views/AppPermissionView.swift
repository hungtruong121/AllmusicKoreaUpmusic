//
//  File.swift
//  akccf
//
//  Created by nough on 26/01/2019.
//  Copyright © 2019 nough. All rights reserved.
//

import UIKit

class AppPermissionView : UIView {
    
    var checkTimer : Timer?
    
    class func instanceFromNib() -> UIView {
        
        return UINib(nibName: "AppPermissionView", bundle: nil).instantiate(withOwner: nil, options: nil)[0] as! UIView
        
    }
    
    @IBAction func confirmButtonClicked(_ sender: Any) {
        
        
        let delay = 2.0 // time in seconds
//        checkTimer = Timer.scheduledTimer(timeInterval: 0.5, target: self, selector: #selector(checkPermission), userInfo: nil, repeats: true) // 언제 종료 할지..

        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "checkPermissionTimer"), object: nil)
        
        setupNotification()
        Timer.scheduledTimer(timeInterval: delay, target: self, selector: #selector(setupLocation), userInfo: nil, repeats: false)
        
        
//        self.removeFromSuperview()
    }
    
    @objc func setupNotification() {
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "setupNotification"), object: nil)
    }
    
    @objc func setupLocation() {
        
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "permissionForLocation"), object: nil)
        
//        UserDefaults.standard.set(true, forKey: "isAllPermissionAllowed")
    }
    
    @objc func checkPermission() {
        
        // 개선 permissionForLocation -> alert 없이 ?!
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "permissionLocationNoAlert"), object: nil)
        
        let isAllPermissionAllowed = UserDefaults.standard.bool(forKey: "isAllPermissionAllowed")
        if (isAllPermissionAllowed) {
            if(checkTimer!.isValid){
                checkTimer!.invalidate()
            }
            self.removeFromSuperview()
        }
        
    }
    
    
    
    
}
