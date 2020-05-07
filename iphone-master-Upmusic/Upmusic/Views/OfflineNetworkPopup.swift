//
//  File.swift
//  akccf
//
//  Created by nough on 26/01/2019.
//  Copyright © 2019 nough. All rights reserved.
//

import UIKit

class OfflineNetworkPopup : UIView {
    
    class func instanceFromNib() -> UIView {
        
        return UINib(nibName: "OfflineNetworkPopup", bundle: nil).instantiate(withOwner: nil, options: nil)[0] as! UIView
        
    }
    
    @IBAction func connectAgainClicked(_ sender: Any) {
        
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "checkNetwork"), object: nil)
    }
    
    @IBAction func confirmButtonClicked(_ sender: Any) {
        exit(0) // 어플리케이션 종료 함수..간단
    }
    
    
}
