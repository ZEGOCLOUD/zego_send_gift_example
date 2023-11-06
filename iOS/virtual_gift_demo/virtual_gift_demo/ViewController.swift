//
//  ViewController.swift
//  virtual_gift_demo
//
//  Created by zego on 2023/1/9.
//

import UIKit
import ZegoUIKitPrebuiltLiveAudioRoom
import ZegoUIKit

class ViewController: UIViewController {
    
    let appID: UInt32 = <#YourAppID#>
    let appSign: String = <#YourAppSign#>
    
    var userName: String? = "Tina"
    
    var liveAudioRoomVC: ZegoUIKitPrebuiltLiveAudioRoomVC?
    
    lazy var giftView: GiftView = {
        let giftView = GiftView(frame: view.bounds)
        return giftView
    }()
    
    @IBOutlet weak var roomIDTextField: UITextField! {
        didSet {
            let roomID: UInt32 = arc4random() % 999
            roomIDTextField.text = String(format: "%d", roomID)
        }
    }
    
    
    @IBOutlet weak var UserIDTextField: UITextField! {
        didSet {
            let userID: String = String(format: "%d", arc4random() % 999999)
            UserIDTextField.text = userID
        }
    }
    

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }
    
    
    @IBAction func startLiveAudio(_ sender: Any) {
        let config: ZegoUIKitPrebuiltLiveAudioRoomConfig = ZegoUIKitPrebuiltLiveAudioRoomConfig.host()
        let layoutConfig: ZegoLiveAudioRoomLayoutConfig = ZegoLiveAudioRoomLayoutConfig()
        layoutConfig.rowSpecing = 5
        let rowConfig1: ZegoLiveAudioRoomLayoutRowConfig = ZegoLiveAudioRoomLayoutRowConfig()
        rowConfig1.alignment = .spaceAround
        rowConfig1.count = 4
        layoutConfig.rowConfigs = [rowConfig1, rowConfig1]
        layoutConfig.rowSpecing = 0
        config.layoutConfig = layoutConfig
        let liveAudioVC: ZegoUIKitPrebuiltLiveAudioRoomVC = ZegoUIKitPrebuiltLiveAudioRoomVC(appID, appSign: appSign, userID: self.UserIDTextField.text ?? "", userName: userName ?? "", roomID: self.roomIDTextField.text ?? "", config: config)
        liveAudioVC.modalPresentationStyle = .fullScreen
        liveAudioVC.delegate = self
        let backgroundView: LiveAudioHeaderView = LiveAudioHeaderView(frame: CGRect(x: 0, y: 0, width: liveAudioVC.view.frame.width, height: liveAudioVC.view.frame.height))
        backgroundView.setHeadContent("Live Audio Room", roomID: self.roomIDTextField.text ?? "", image: UIImage())
        liveAudioVC.setBackgroundView(backgroundView)
        self.present(liveAudioVC, animated: true)
        self.liveAudioRoomVC = liveAudioVC
        ZegoUIKit.shared.addEventHandler(self)//add ZegoUIKit listener
    }
    
    @IBAction func joinLiveAudio(_ sender: Any) {
        let config: ZegoUIKitPrebuiltLiveAudioRoomConfig = ZegoUIKitPrebuiltLiveAudioRoomConfig.audience()
        let layoutConfig: ZegoLiveAudioRoomLayoutConfig = ZegoLiveAudioRoomLayoutConfig()
        layoutConfig.rowSpecing = 5
        let rowConfig1: ZegoLiveAudioRoomLayoutRowConfig = ZegoLiveAudioRoomLayoutRowConfig()
        rowConfig1.alignment = .spaceAround
        rowConfig1.count = 4
        layoutConfig.rowConfigs = [rowConfig1, rowConfig1]
        layoutConfig.rowSpecing = 0
        config.layoutConfig = layoutConfig
        let liveAudioVC: ZegoUIKitPrebuiltLiveAudioRoomVC = ZegoUIKitPrebuiltLiveAudioRoomVC(appID, appSign: appSign, userID: self.UserIDTextField.text ?? "", userName: userName ?? "", roomID: self.roomIDTextField.text ?? "", config: config)
        liveAudioVC.modalPresentationStyle = .fullScreen
        liveAudioVC.delegate = self
        let backgroundView: LiveAudioHeaderView = LiveAudioHeaderView(frame: CGRect(x: 0, y: 0, width: liveAudioVC.view.frame.width, height: liveAudioVC.view.frame.height))
        backgroundView.setHeadContent("Live Audio Room", roomID: String(format: "ID: %@", self.roomIDTextField.text ?? ""), image: UIImage())
        liveAudioVC.setBackgroundView(backgroundView)
        
        //add send gift button to bottom bar
        let giftButton: UIButton = UIButton()
        giftButton.backgroundColor = UIColor.red
        giftButton.setTitle("Gift", for: .normal)
        giftButton.addTarget(self, action: #selector(sendGift), for: .touchUpInside)
        liveAudioVC.addButtonToMenuBar(giftButton, role: .audience)
        liveAudioVC.addButtonToMenuBar(giftButton, role: .speaker)
        
        self.present(liveAudioVC, animated: true)
        ZegoUIKit.shared.addEventHandler(self)//add ZegoUIKit listener
        self.liveAudioRoomVC = liveAudioVC
    }
    
    @objc func sendGift() {
        //send gift use sendRoomCommand api
        ZegoUIKit.getSignalingPlugin().sendRoomCommand("GIFT") { data in
            if let code = data?["code"] as? Int,
               code == 0 {
                print("Send Gift success.")
                //show gift animation
                self.giftView.show("vap.mp4", container: self.liveAudioRoomVC?.view)
            }
        }
    }

}

extension ViewController: ZegoUIKitPrebuiltLiveAudioRoomVCDelegate,ZegoUIKitEventHandle {
    
    func onLeaveLiveAudioRoom() {
        
    }
    
    //MARK: -ZegoUIKitEventHandle    
    func onInRoomCommandMessageReceived(_ messages: [ZegoSignalingInRoomCommandMessage], roomID: String) {
        if let message = messages.first {
            if message.senderUserID != self.UserIDTextField.text {
                //show gift animation
                self.giftView.show("vap.mp4", container: self.liveAudioRoomVC?.view)
            }
        }
    }
}

