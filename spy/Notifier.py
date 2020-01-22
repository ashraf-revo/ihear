import requests


class Notifier:
    ___cookies = {"XSRF-TOKEN": "dummy"}

    def __init__(self, oauth, env):
        self.___env = env
        self.___headers = {"X-XSRF-TOKEN": "dummy", "Authorization": "Bearer " + oauth.access_token}

    def notify(self, call):
        return self.___post(self.___env.ws.notifyUrl, {"to": self.___env.createdBy + "/-", "payload": call})

    def ___post(self, url, data):
        return requests.post(url, json=data, headers=self.___headers, cookies=self.___cookies)
