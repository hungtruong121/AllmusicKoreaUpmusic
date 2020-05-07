//
//  MusicTrackSection.swift
//  Upmusic
//
//  Created by nough on 15/10/2018.
//  Copyright © 2018 Nough / Ndvor. All rights reserved.
//

import Foundation

class MusicTrackSection {
    var isMultiple: Bool
    var title: String
    var items : [MusicTrack]
    
    init(isMultiple: Bool, title: String, items: [MusicTrack]) {
        self.isMultiple = isMultiple
        self.title = title
        self.items = items
    }
}
