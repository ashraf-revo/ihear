from enum import Enum


class Key:
    def __init__(self, keyType,keyEvent, actions):
        self.keyType = keyType
        self.keyEvent = keyEvent
        self.actions = []
        for a in actions:
            self.actions.append(Action(**a))


class Action:
    def __init__(self, actionType, resourceType, data):
        self.actionType = actionType
        self.resourceType = resourceType
        self.data = data


class ResourceType(Enum):
    PIN = "PIN"
    CAMERA = "CAMERA"
    THREAD = "THREAD"
    SOCKET = "SOCKET"


class ActionType(Enum):
    ON = "ON"
    OFF = "OFF"
    TOGGLE = "TOGGLE"
    BLINK = "BLINK"
    SLEEP = "SLEEP"
    SHOT = "SHOT"
    RECORD = "RECORD"
    STREAM = "STREAM"
    TEARDOWN = "TEARDOWN"
    ACK = "ACK"
    NAK = "NAK"


class KeyType(Enum):
    UP = "UP"
    DOWN = "DOWN"
    LEFT = "LEFT"
    RIGHT = "RIGHT"
    Y = "Y"
    A = "A"
    X = "X"
    B = "B"

class KeyEvent(Enum):
  KEYDOWN="KEYDOWN"
  KEYUP="KEYUP"
  KEYHOLED="KEYHOLED"

