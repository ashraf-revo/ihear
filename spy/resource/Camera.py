from aiortc import RTCPeerConnection, RTCSessionDescription
from aiortc.contrib.media import MediaPlayer

from domain.Schema import ActionType
from resource.PiResource import PiResource


class Camera(PiResource):
    async def handel(self, actionType, data):
        if actionType == ActionType.SHOT:
            await self.___shot(data)
        if actionType == ActionType.RECORD:
            await self.___record(data)
        if actionType == ActionType.STREAM:
            await self.___stream(data)
        if actionType == ActionType.TEARDOWN:
            await self.___teardown(data)
        pass

    async def available(self):
        self.___available = True

    async def close(self):
        print("close Camera")
        await self.___teardown({})
        self.___available = False
        pass

    ___pc = None
    ___player = None
    ___options = {"framerate": "25", "video_size": "640x480"}

    def __init__(self, param):
        self.___param = param
        self.___available = True

    async def ___shot(self, data):
        print("SHOT ", "Camera")
        return None

    async def ___record(self, data):
        print("RECORD ", "Camera")
        return None

    async def ___stream(self, data):
        if self.___available and data:
            offer = RTCSessionDescription(sdp=data["sdp"], type=data["type"])
            self.___pc = RTCPeerConnection()

            @self.___pc.on("iceconnectionstatechange")
            async def on_iceconnectionstatechange():
                print(self.___pc.iceConnectionState)
                if self.___pc.iceConnectionState == "failed":
                    await self.___teardown({})

            await self.___pc.setRemoteDescription(offer)
            player = MediaPlayer("/dev/video0", format="v4l2", options=self.___options)
            for t in self.___pc.getTransceivers():
                if t.kind == "audio" and player.audio:
                    self.___pc.addTrack(player.audio)
                elif t.kind == "video" and player.video:
                    self.___pc.addTrack(player.video)

            answer = await self.___pc.createAnswer()
            await self.___pc.setLocalDescription(answer)
            print("STREAM ", "Camera")
            res = {"sdp": self.___pc.localDescription.sdp, "type": self.___pc.localDescription.type, "action": "STREAM"}
            self.___param["notify"](res)
            self.___available = False

    async def ___teardown(self, data):
        print("TEARDOWN ", "Camera")
        if self.___pc:
            await self.___pc.close()
        self.___available = True
