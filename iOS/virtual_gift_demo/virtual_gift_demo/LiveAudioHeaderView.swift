//
//  ZegoLiveAudioHeaderView.swift
//  ZegoUIKitPrebuiltLiveAudio
//
//  Created by zego on 2022/11/15.
//

import UIKit
import ZegoUIKit

class LiveAudioHeaderView: UIView {
    
    lazy var roomNameLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFont(ofSize: 24, weight: .semibold)
        label.textAlignment = .left
        label.numberOfLines = 1;
        label.textColor = UIColor.colorWithHexString("#1B1B1B")
        return label
    }()
    
    lazy var roomIDLabel: UILabel = {
        let label = UILabel()
        label.textAlignment = .left
        label.font = UIFont.systemFont(ofSize: 12, weight: .regular)
        label.textColor = UIColor.colorWithHexString("#606060")
        return label
    }()
    
    lazy var backgroundImageView: UIImageView = {
        let view = UIImageView()
        return view
    }()

    override init(frame: CGRect) {
        super.init(frame: frame)
        self.addSubview(backgroundImageView)
        self.addSubview(roomNameLabel)
        self.addSubview(roomIDLabel)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        self.setupLayout()
    }
    
    func setupLayout() {
        self.backgroundImageView.frame = CGRect(x: 0, y: 0, width: self.frame.width, height: self.frame.height)
        self.roomNameLabel.frame = CGRect(x: 27, y: 57, width: self.frame.width - 27, height: 33)
        self.roomIDLabel.frame = CGRect(x:27, y: self.roomNameLabel.frame.maxY + 2, width: self.frame.width - 27, height: 21)
    }
    
    public func setHeadContent(_ roomName: String, roomID: String, image: UIImage) {
        self.roomNameLabel.text = roomName
        self.roomIDLabel.text = roomID
        self.backgroundImageView.image = image
    }

}
