import json


class Oauth:
    def __init__(self, clintId, clientSecret, authorize, token):
        self.clintId = clintId
        self.clientSecret = clientSecret
        self.authorize = authorize
        self.token = token


class Ws:
    def __init__(self, url, notifyUrl):
        self.url = url
        self.notifyUrl = notifyUrl


class Env:
    def __init__(self, id, createdBy, oauth, ws):
        self.id = id
        self.createdBy = createdBy
        self.oauth = Oauth(**oauth)
        self.ws = Ws(**ws)

    @classmethod
    def read(cls, path):
        with open(path) as json_file:
            load = json.load(json_file)
            return Env(**load)
