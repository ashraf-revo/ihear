from abc import abstractmethod


class PiResource:
    @abstractmethod
    async def available(self):
        pass

    @abstractmethod
    async def close(self):
        pass

    @abstractmethod
    async def handel(self, actionType, data):
        pass
