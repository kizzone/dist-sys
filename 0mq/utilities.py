import json


class Messages:
    def __init__ (self, pid, typeoff, price):
        self.pid = pid
        self.typeoff = typeoff
        self.price = price

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__, sort_keys=True, indent=4)

    