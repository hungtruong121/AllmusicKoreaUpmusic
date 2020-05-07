//
//  PopupView.swift
//  Upmusic
//
//  Created by nough on 15/10/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import UIKit

class PopupView: UIView {
    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var baseView: UIView!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var confirmBtn: UIButton!
    @IBOutlet weak var closeBtn: UIButton!
    
    @IBAction func closeBtnTapped(_ sender: Any) {
        print("[PopupView] closeBtnTapped")
        self.removeFromSuperview()
    }
    
    @IBAction func confirmBtnTapped(_ sender: Any) {
        print("[PopupView] confirmBtnTapped")
    }
    
}
