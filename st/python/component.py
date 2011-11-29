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

class PyGameObject(object):
    def __init__(self):
        self.comp_map = {}

    def FindComponent(name):
        try:
            return self.comp_map[name]
        except KeyError:
            return None

    def AddComponent(comp):
        self.comp_map[comp.Name()] = comp

def FindComponentAttr(comp):
    pass

class PyBaseComponentGen(object):
    def __init__(self):
        # Domain内的唯一id
        self.id_ = None
        # GameObject运行时id
        self.object_id_ = None
        # Object的名字
        self.name_ = None
        self.id_ = 0
        self.object_id_ = 0
        self.name_ = '(unamed object)'

    def Sid():
        return 0x2559056e

    def Name():
        return 'BaseComponent'

    def GetId(self):
        return self.id_

    def SetId(self, id):
        self.id_ = id

    def GetObjectId(self):
        return self.object_id_

    def SetObjectId(self, object_id):
        self.object_id_ = object_id

    def GetName(self):
        return self.name_

    def SetName(self, name):
        self.name_ = name

class PyBaseComponentFactory(object):
    def CreateFromXML(self, xml_data):
        comp = PyBaseComponentGen()
        attr_list = FindComponentAttr(comp):
        for attr in attr_list:
            raw_name, attr_name = attr
            tp = type(getattr(comp, attr_name))
            if tp == string:
                comp.attr_name = xml_data[raw_name]
            elif tp == bool:
                comp.attr_name = True if xml_data[raw_name] == 'true' else False
            else:
                comp.attr_name = tp(xml_data[raw_name])
import re
import py_component

NAME_TOKEN_PT = re.compile('([A-Z][a-z]+|[0-9][0-9]*)')

def _SplitName(name):
    return NAME_TOKEN_PT.findall(name)

def _FactoryName2ComponentName(factory_name):
    tokens = _SplitName(factory_name)
    return 'Py{0}Gen'.format(''.join(tokens[:-1]))

def _FindComponentAttr(comp):
    '''@return [(raw_name, attr_name), ... ]'''
    pass

def _TypeCast(tp, str_val):
    if tp == string:
        return str_val
    elif tp == bool:
        return True if str_val == 'true' else False
    else:
        return tp(str_val)

def _AttrName2RawName(name):
    return '-'.join(name.split('_'))

def _ComponentCreateFromXML(comp, xml_data):
    attr_list = _FindComponentAttr(comp):
    for attr in attr_list:
        raw_name, attr_name = attr
        tp = type(getattr(comp, attr_name))
        if tp is list:
            for val in comp[raw_name]:
                

class PyMonsterFactory(object):
    def CreateFromXML(self, xml_data):
        obj = PyGameObject()
        if 1:
            factory_name = 'BaseComponentFactory'
            class_name = _FactoryName2ComponentName(factory_name)
            comp = eval('py_component.{0}()'.format(class_name))
            _ComponentCreateFromXML(comp, xml_data)
        if 1:
            pass

        
