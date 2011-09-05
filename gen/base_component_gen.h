#ifndef _BASE_COMPONENT_GEN_H_
#define _BASE_COMPONENT_GEN_H_

#include <string>
#include <sstream>
#include "icomponent.h"

// Game object基本组件
class BaseComponent : public IComponent
{
    // Domain内的唯一id
    int id_;
    // Object的名字
    std::string name_;

public:
    BaseComponent()
        : id_(0),
          name_("(unamed object)")
        {}
    
    ~BaseComponent()
        {}
    
    int GetId() const
        {
            return id_;
        }
    
    void SetId(int id)
        {
            id_ = id;
        }
    
    std::string& GetName() const
        {
            return name_;
        }
    
    void SetName(std::string& name)
        {
            name_ = name;
        }

    virtual std::string ToString() const
        {
            std::ostringstream ss;
            ss << "BaseComponent: ";
            ss << "Id: " << id_;
            ss << "Name: " << name_;
            return ss.str();
        }
};

#endif // _BASE_COMPONENT_GEN_H_
