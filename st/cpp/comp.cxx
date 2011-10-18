BaseComponent *p = (BaseComponent*)obj.FindComponent(BASE_COMPONENT);
int id = p->GetId();
std::string name = p->GetName();
int id = p->GetIntAttr("id");
p->SetStringAttr("name", "doom");
p->SetIntAttr("id", 77);
std::string name = p->GetStringAttr("name");

void BaseComponent::SetIntAttr(sid_t attr, int val)
{
    switch (attr)
    {
      case 0xfacd08:
      {
          this->SetId(val);
          break;
      }
      default:
      {
          break;
      }
    }
}

void BaseComponent::SetStringAttr(const char* attr, const std::string& val)
{
    sid_t attr_id(attr);
    
    switch (attr_id)
    {
      case 0xfacd09:
      {
          this->SetName(val);
          break;
      }
      default:
      {
          break;
      }
    }
}


std::shared_ptr<IComponent> comp1 = factory.CreateComponent("BaseComponent");
std::shared_ptr<IComponent> comp2 = factory.CreateComponent("ItemComponent");
std::shared_ptr<IComponent> comp3 = factory.CreateComponent("CombatPropertyComponent");
std::shared_ptr<IComponent> comp4 = factory.CreateComponent("RpgPropertyComponent");

ComponentFactory::CreateComponent(const char* name)
{
    
}

std::auto_ptr<GameObject> CreateItem(Data data)
{
    try {
        std::auto_ptr<GameObject> go(new GameObject());
        ComponentFactory factory;
        auto p1 = factory.CreateComponent("base", data["base_comp"]);
        auto p2 = factory.CreateComponent("item_base", data["item_base"]);
        auto p3 = factory.CreateComponent("vip_item", data["vip_item"]);
        go.AddComponent(p1);
        go.AddComponent(p2);
        go.AddComponent(p3);
        return go;
    }
    catch (CreateComponentException& e) {
        LOG(L_ERROR, e.what());
        return NULL;
    }
}

class IComponentFactory
{
public:
    virtual std::auto_ptr<IComponent> CreateComponentFromXML(const TiXmlElement* data) = 0;
};

class BaseComponentFactory : public IComponentFactory
{
public:
    virtual std::auto_ptr<IComponent> CreateComponentFromXML(const TiXmlElement* data)
        {
            std::auto_ptr<BaseComponent> p(new $BaseComponent$);
            
            const TiXmlElement *component_node = NULL;
            for (const TiXmlElement *node = data->FirstChildElement($"go-component"$); node; node = node->NextSiblingElement()) {
                const char* attribute_name = node->Attribute("name");
                if (strcmp(attribute_name, $"base"$) == 0) {
                    component_node = node;
                    break;
                }
            }
            
            if (component_node) {
                const TiXmlElement *element_$id$ = component_node->FirstChildElement($"id"$);
                if (element_$id$) {
                    const char *$id_text$ = element_id->GetText();
                    if ($id_text$) {
                        int $id$ = atoi($id_text$);
                        p->$SetId$($id$);
                    }
                    else
                        throw AttributeValueNotFound($"Basecomponent"$, $"id"$, __FILE__, __LINE__);
                }
                else
                    throw AttributeNotFound($"BaseComponent"$, $"id"$, __FILE__, __LINE__);

                const TiXmlElement *element_$name$ = component_node->FirstChildElement($"name"$);
                if (element_$name$) {
                    const char *$name_text$ = element_name->GetText();
                    if ($name_text$) {
                        p->SetName($name_text$);
                    }
                    else
                        throw AttributeValueNotFound($"Basecomponent"$, $"name"$, __FILE__, __LINE__);
                }
                else
                    throw AttributeNotFound($"BaseComponent"$, $"name"$, __FILE__, __LINE__);
            }
            else
                throw ComponentNotFound("BaseComponent", __FILE__, __LINE__);
        }
};

class IGameObjectFactory
{
public:
    virtual IGameObject* CreateGameObject() = 0;
};

class MonsterFactory : public IGameObjectFactory
{
public:
    MonsterFactory(const Application& app)
        : app_(&app)
        {
        }
    
    virtual std::auto_ptr<GameObject> CreateGameObject(int monster_id)
        {
            std::auto_ptr<GameObject> go(new GameObject);
            
            const TiXmlElement *data = app_->DataProvider()->FindMonsterData(monster_id);
            if (data) {
                try {
                    IComponentFactory *f0 = app_->ComponentFactoryRegester().GetFactory("BaseComponent");
                    std::auto_ptr<IComponent*> p0(f0->CreateComponentFromXML(data));
                    go->AddComponent(p0);

                    IComponentFactory *f1 = app_->ComponentFactoryRegester().GetFactory("MonsterPropertyComponent");
                    std::auto_ptr<IComponent*> p1(f1->CreateComponentFromXML(data));
                    go->AddComponent(p1);

                    return go;
                }
                catch (Exception& e) {
                    std::cout << e.what() << std::endl;
                    return NULL;
                }
            }
            else {
                return NULL;
            }
        }
    Application *app_;
};

IGameObjectFactory *factory = GameObjectFactoryResgester::GetFactory("monster");
IGameObject *obj = factory->CreateGameObject();

std::auto_ptr<BaseComponent> CreateBaseComponent(Data data)
{
    std::auto_ptr<BaseComponent> p(new BaseComponent());
    p->SetId(data["id"].asInt());
    p->SetName(data["name"].asString());
    for (int i = 0; i < data["res"].size(); ++i) {
        if (p->AddMagicResistence(key, val) == false) {
            LOG(L_ERROR, "BaseComponent AddMagicResistence(%s, %s)",
                data["res"][i]["key"],
                data["res"][i]["value"]);
            throw CreateComponentException("msg");
        }
        p->SetMagicResistence(key, val);
        p->DelMagicResistence(key);
    }
    return p;
}

// set obj name "foo"
BaseComponentBuilder builder;
builder.WithId(10)
.WithName("foo")
.WithName(

std::map;
std::vector;
std::map<T, int>;
std::map<int, Skill*>;

int SkillComponent::GetSkillCdTime(T skill_id)
{
    std::map<T, U>::iterator it = this->skill_cd_time_map_.find(skill_id);
    return it->second;
}

int BaseComponent::GetIntAttr(sid_t attr)
{
    switch (attr)
    {
      case :
      {
        case 0xfacd08:
        {
            return this->GetId();
            break;
        }
          break;
      }
      default:
      {
          break;
      }
    }
}


const TiXmlElement *ele_magic_resistance = component_node->FirstChildElement("magic-resistance");
if (ele_magic_resistance) {
    std::vector<int> val_vec;
    
    for (const TiXmlElement *item_node = data->FirstChildElement("item");
         item_node; item_node = item_node->NextSiblingElement())
    {
        const char *text = item_node->GetText();
        val_vec.push_back(atoi(text));
    }
    
    p->SetMagicResistance(val_vec);
}
else throw AttributeNotFound("CombatPropertyComponent", "magic_resistance", "magic-resistance", __FILE__, __LINE__);
