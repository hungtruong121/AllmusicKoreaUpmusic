//
//  CMTime.swift
//  Upmusic
//
//  Created by nough on 24/07/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import AVKit

extension CMTime {
    
    func toDisplayString() -> String {
        if CMTimeGetSeconds(self).isNaN {
            return "--:--"
        }
        
        let totalSeconds = Int(CMTimeGetSeconds(self))
        let seconds = totalSeconds % 60
        let minutes = totalSeconds % (60 * 60) / 60
        let hours = totalSeconds / 60 / 60
        let timeFormatString = String(format: "%02d:%02d:%02d", hours, minutes, seconds)
        return timeFormatString
    }
    
}
