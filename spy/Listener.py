import json

import websocket

from Executor import Executor
from Notifier import Notifier
from Schema import Call, Schema


class Listener:

    def __init__(self, env, oauth):
        websocket.enableTrace(False)
        self.___ws = websocket.WebSocketApp(env.ws.url,
                                            on_open=(lambda ws: self.___on_open(self)),
                                            on_message=(lambda ws, msg: self.___on_message(self, msg)),
                                            on_error=(lambda ws, msg: self.___on_error(self, msg)),
                                            on_close=(lambda ws: self.___on_close(self)),
                                            header={"Authorization": "Bearer " + oauth.access_token}
                                            )

        self.___notifier = Notifier(oauth, env)
        self.___executor = Executor(Schema.read(env.schemaUrl, oauth), (lambda call: self.___notifier.notify(call)))

    def ___on_open(self, ws):
        self.___executor.execute_on_load()

    def ___on_message(self, ws, call):
        self.___executor.execute_on_key(Call(**json.loads(call)))

    def ___on_error(self, ws, error):
        self.___executor.close()

    def ___on_close(self, ws):
        self.___executor.close()

    def listen(self):
        self.___ws.run_forever()
