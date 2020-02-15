import asyncio
import json

import websockets

from domain.Schema import Key
from resource.Executor import Executor
from util.Notifier import Notifier


class Listener:
    async def ___handel(self, url, header):
        async with websockets.connect(url, extra_headers=header) as websocket:
            await self.___on_open(websocket)
            while True:
                message = await websocket.recv()
                await self.___on_message(websocket, message)

    def __init__(self, env, oauth):
        self.___headers = {"Authorization": "Bearer " + oauth.access_token}
        self.___url = env.ws.url
        self.___notifier = Notifier(oauth, env)
        self.___executor = Executor((lambda call: self.___notifier.notify(call)))

    async def ___on_open(self, ws):
        await self.___executor.close()

    async def ___on_message(self, ws, call):
        await self.___executor.call(Key(**json.loads(call)))

    def ___on_error(self, ws, error):
        self.___executor.close()

    def ___on_close(self, ws):
        self.___executor.close()

    def listen(self):
        asyncio.get_event_loop().run_until_complete(
            self.___handel(self.___url, self.___headers))
        asyncio.get_event_loop().run_forever()
