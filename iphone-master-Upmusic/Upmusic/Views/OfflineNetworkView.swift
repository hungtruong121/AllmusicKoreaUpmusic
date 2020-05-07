//
//  File.swift
//  akccf
//
//  Created by nough on 26/01/2019.
//  Copyright © 2019 nough. All rights reserved.
//

import UIKit

class OfflineNetworkView : UIView {
    
    class func instanceFromNib() -> UIView {
        return UINib(nibName: "OfflineNetworkView", bundle: nil).instantiate(withOwner: nil, options: nil)[0] as! UIView
        
    }
    
    @IBAction func connectAgainClicked(_ sender: Any) {
        print(" > connectAgainClicked")
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "checkNetworkWhileAppUsing"), object: nil)
    }
}
