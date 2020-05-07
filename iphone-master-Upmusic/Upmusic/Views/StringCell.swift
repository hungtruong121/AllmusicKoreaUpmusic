//
//  StringCell.swift
//  Upmusic
//
//  Created by nough on 17/10/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import UIKit
import MarqueeLabel

class StringCell: UITableViewCell {
    
    @IBOutlet weak var cellStringLabel : UILabel!
    
    var collection: Collection! {
        didSet {
            
            let str = collection.subject
            
//            let str = "Hello, playground"
//            print(str.substring(from: 7))         // playground
//            print(str.substring(to: 5))           // Hello
//            print(str.substring(with: 7..<11))    // play
        
            var endIndex = str?.count
            if (str?.count ?? 0 > 12) {
                endIndex = 12
            }
            
            cellStringLabel.text = (str?.substring(with: 0..<endIndex!) ?? "") + "..."
            
        }
    }
    
}
