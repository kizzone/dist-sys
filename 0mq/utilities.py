import json


class Messages:
    def __init__ (self, pid, typeoff, price):
        self.pid = pid
        self.typeoff = typeoff
        self.price = price

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__, sort_keys=True, indent=4)

    def __repr__(self):
        return "Pid: " + str(self.pid) + " Typeoff: " + str(self.typeoff) + "Price: " + str(self.price) + ""