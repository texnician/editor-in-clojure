import re
from functools import partial

ATTR_PT = re.compile('^(Set|Get)(\w+)$')
ATTR_TOKENS_PT = re.compile('([A-Z][a-z]+|[0-9][0-9]*)')

class DuckComponent(object):
    def __init__(self, data):
        self.data = data
        
    def __getattr__(self, name):
        m = ATTR_PT.match(name)
        if m:
            prefix, attr = m.groups()
            if prefix == 'Get':
                return (lambda : self.data[attr])
            elif prefix == 'Set':
                return partial(self._setter, attr)
        else:
            raise AttributeError

    def _setter(self, attr, val):
        self.data[attr] = val

base = DuckComponent({'UniqueId' : 10})
uid = base.GetUniqueId()
print(uid)
base.SetUniqueId(20)
uid = base.GetUniqueId()
print(uid)

class PyBaseComponent(object):
    def __init__(self):
        self.name_ = a
        self.id_ = 10046
        
    def GetUniqueId():
        return 
