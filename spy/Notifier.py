import requests


class Notifier:
    ___cookies = {"XSRF-TOKEN": "dummy"}

    def __init__(self, oauth, env):
        self.___env = env
        self.___headers = {"X-XSRF-TOKEN": "dummy", "Authorization": "Bearer " + oauth.access_token}

    def notify(self, call):
        return requests.post(self.___env.ws.notifyUrl, json={"to": self.___env.createdBy + "/-", "payload": call},
                             headers=self.___headers, cookies=self.___cookies)
