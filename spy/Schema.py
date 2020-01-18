import json
from enum import Enum

import requests


class Schema:
    def __init__(self, id, parentId, createdBy, createdDate, title, meta, event):
        self.id = id
        self.parentId = parentId
        self.createdBy = createdBy
        self.createdDate = createdDate
        self.title = title
        self.meta = meta
        self.event = Event(**event)

    @classmethod
    def read(cls, path, oauth):
        sc = requests.get(path, headers={"Authorization": "Bearer " + oauth.access_token})
        return Schema(**json.loads(sc.content))


class Event:
    def __init__(self, resources, listeners, keys):
        self.resources = []
        self.listeners = []
        self.keys = []
        for r in resources:
            self.resources.append(Resource(**r))
        for l in listeners:
            self.listeners.append(Listener(**l))
        for k in keys:
            self.keys.append(Key(**k))


class Key:
    def __init__(self, keyType, listeners):
        self.keyType = keyType
        self.listeners = []
        for l in listeners:
            self.listeners.append(Listener(**l))


class Listener:
    def __init__(self, listenerType, threading, actions):
        self.listenerType = listenerType
        self.threading = threading
        self.actions = []
        for a in actions:
            self.actions.append(Action(**a))


class Call:

    def __init__(self, keyType, listenerType):
        self.keyType = keyType
        self.listenerType = listenerType


class Action:
    def __init__(self, actionType, resourceType):
        self.actionType = actionType
        self.resourceType = resourceType


class Resource:
    def __init__(self, resourceType):
        self.resourceType = resourceType


class ResourceType(Enum):
    PIN_1 = "PIN_1"
    CAMERA = "CAMERA"
    THREAD = "THREAD"


class ListenerType(Enum):
    LOOAD = "LOOAD"
    CLICK = "CLICK"


class ActionType(Enum):
    ON = "ON"
    OFF = "OFF"
    TOGGLE = "TOGGLE"
    BLINK = "BLINK"
    SLEEP = "SLEEP"
    RECORD = "RECORD"
    TEARDOWN = "TEARDOWN"


class KeyType(Enum):
    UP = "UP"
    DOWN = "DOWN"
    LEFT = "LEFT"
    RIGHT = "RIGHT"
    Y = "Y"
    A = "A"
    X = "X"
    B = "B"
