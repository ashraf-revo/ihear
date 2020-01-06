from gpiozero import LED


class Gpio:
    def __init__(self):
        self.___pins = {17: LED(17)}

    def ___on(self, pin):
        self.___pins.get(pin).on()

    def ___off(self, pin):
        self.___pins.get(pin).off()

    def ___toggle(self, pin):
        self.___pins.get(pin).toggle()

    def ___blink(self, pin):
        self.___pins.get(pin).blink()

    def handle(self, message):
        pin = message['pin']
        if pin and self.___pins.__contains__(pin):
            if message['action'] == 'on':
                self.___on(pin)
            if message['action'] == 'off':
                self.___off(pin)
            if message['action'] == 'toggle':
                self.___toggle(pin)
            if message['action'] == 'blink':
                self.___blink(pin)
