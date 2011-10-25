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

Json::Value BaseComponentGen::ToJSONValue() const
{
    Json::Value root;
    
    root["id"] = this->id_;
    root["object-id"] = this->object_id_;
    root["name"] = this->name_;

    {
        Json::Value& array = root["magic-resistance"];
        ArrayToJsonValue(this->magic_resistance_, &array);
    }
    
    return root;
}

bool BaseComponentGen::FromJSONValue(const Json::Value& root)
{
    try
    {
        this->id_ = root.get("id", 0).asInt();
        this->object_id_ = root.get("object-id", 0).asInt();
        this->name_ = root.get("name", "").asString();

        {
            const Json::Value& array = root["magic-resistance"];
            for (int i = 0; i < array.size(); ++i) {
                this->magic_resistance_.push_back(array[i].asInt());
            }
        }
        return true;
    }
    catch (std::runtime_error& e) {
        LOG(L_ERROR, "FromJSONValue error: %s", e.what().c_str());
        return false;
    }
}

Json::Value FromRecordSet(const RecordSet* rs);

RecordSet = ExecuteSql(sql);

Json::Value RecordSetToJson(const RecordSet* rs);
{
    Json::Value jv;
    jv["id"] = rs->GetFieldValueByName("id");
    jv["name"] = rs->GetAttributeByName("name");
}

class ISQLCmd
{
public:
    virtual ~ISQLCmd()
        {}

    virtual bool AddSubCmd(int task_id, const char* key) = 0;

    virtual const ISQLCmd& GetSubCmd(int task_id) = 0;
    
    virtual RecordSetPtr Execute() const = 0;
};

class NullSQLCmd : public ISQLCmd
{
public:
    virtual ~NullSQLCmd()
        {}

    virtual AddSubCmd(int task_id, const char* key) = 0;

    virtual const ISQLCmd& GetSubCmd(int task_id) = 0;
    
    virtual RecordSetPtr Execute() const = 0;
};

class MySqlGroup : public IMySqlGroup
{
public:
    MySqlGroup(SqlCon* con);

    virtual void Add(const char* cmd, const char* sql);
    
    virtual RecordSetPtr ExecuteSql(const char* key);
    
private:
    SqlCon* con_;
};

Json::Value BaseComponentFactory::DoCreateJSONFromRecordSet(const HWDBRecordSet& rs, const HWSQLCmd& cmd)
{
    Json::Value jv_base;

    // Get "(base :id)"
    int field_id = 0;
    GetIntFieldFromCursor(root_cursor_ptr, "id", &field_id)
        ? jv_base["id"] = field_id
        : throw DBFieldNotFound("base", "id", "id", cmd.DumpSQL(rs).c_str(), __FILE__, __LINE__);

    // Get "(base :nick-name)"
    const char* field_nick_name = NULL;
    GetStringFieldFromCursor(root_cursor_ptr, "nick_name", &field_nick_name)
        ? jv_base["nick-name"] = field_nick_name
        : throw DBFieldNotFound("base[\"nick-name\"]", "nick_name", cmd.DumpSQL(rs).c_str(), __FILE__, __LINE__);
                    
    // Get "(base :friend-list)"
    {
        IHWDBCursorPtr sub_cursor_ptr = rs.GetSubRecordSet("friend-list").GetCursor();
        if (sub_cursor_ptr) {
            std::vector<int> vec_val(sub_cursor_ptr->GetRecordCount());
            for (int i = 0; sub_cursor_ptr->GetRecord(); ++i) {
                if (!GetIntFieldFromCursor(sub_cursor_ptr, "item", &vec_val[i]))
                    throw DBFieldNotFound("base[\"friend-list\"]", "friend-list", "item", cmd.GetSubCmd("friend-list").DumpSQL(rs.GetSubRecordSet("friend-list")).c_str(), __FILE__, __LINE__);
            }
            Json::Value& jv_friend_list = jv_base["friend-list"];
            ArrayToJSONValue(vec_val, &jv_friend_list);
        }
    }

    return jv_base;
}

class PlayerSQLCmd : public HWSQLCmd
{
public:
    PlayerSQLCmd();

    Json::Value LoadAll(IHWDBEnv* p_env, const char* root, const char* inventory_items,
                        const char* friend_list)
        {
            try
            {
                // Init SQL Command
                this->SetSQL(root)
                    .SetSubCmd("inventory-items", HWSQLCmd(inventory_items))
                    .SetSubCmd("friend-list", HWSQLCmd(friend_list));

                // Execute sql
                HWDBRecordSet rs = this->ExecuteRs(p_env);
            
                if (!rs.HasError() && !rs.Empty()) {
                    Json::Value jv;

                    // Get root record set
                    IHWDBCursorPtr root_cursor_ptr = rs.GetCursor();

                    // Duplicated GameObject record set is not allowed
                    if (root_cursor_ptr->GetRecordCount() > 1)
                        throw DBDuplicatedGameObjectRecordSet("player", this->DumpSQL(rs).c_str(), __FILE__, __LINE__);

                    // Load base component
                    {
                        BaseComponentFactory factory;
                        jv["base"] = factory.CreateJSONFromRecordSet(rs, *this);
                    }

                    // Load inventory component
                    {
                        InventoryComponentFactory factory;
                        jv["inventory"] = factory.CreateJSONFromRecordSet(rs, *this);
                    }
                }
                else if (!rs.HasError() && rs.IsEmpty()) {
                    return Json::Value;
                }
                else throw DBSQLError("player", this->DumpSQL(rs).c_str());
            }
            catch (FactoryException& e) {
                LOG(L_ERROR, "%s", e.what());
                return Json::Value;
            }
        }
    
    
    Json::Value LoadAComponent(IHWDBEnv* p_env, const char* root, const char* array_a);

    Json::Value LoadBComponent(IHWDBEnv* p_env, const char* root);

    Json::Value LoadCComponent(IHWDBEnv* p_env, const char* root);
};

Json::Value LoadBaseComponent(IHWDBEnv* p_env,
                              const char* root, const char* friend_list,
                              const char* monster_talbe);

Json::Value MonsterSQLCmd::LoadAll(IHWDBEnv* p_env, const char* root, const char* marks, const char* uint64_id_vec, 
                                   const char* uint_id_vec, const char* magic_resistance, const char* mrs, 
                                   const char* long_id_vec)
{
    try
    {
        // Init SQL Command
        this->SetSQL(root).SetSubCmd("marks", HWSQLCmd(marks))
        .SetSubCmd("uint64-id-vec", HWSQLCmd(uint64_id_vec))
        .SetSubCmd("uint-id-vec", HWSQLCmd(uint_id_vec))
        .SetSubCmd("magic-resistance", HWSQLCmd(magic_resistance))
        .SetSubCmd("mrs", HWSQLCmd(mrs))
        .SetSubCmd("long-id-vec", HWSQLCmd(long_id_vec));

        // Execute sql
        HWDBRecordSet rs = this->ExecuteRs(p_env);

        if (!rs.HasError() && !rs.IsEmpty()) {
            Json::Value jv;

            // Duplicated GameObject record set is not allowed
            IHWDBCursorPtr root_cursor_ptr = rs.GetCursor();
            if (root_cursor_ptr && root_cursor_ptr->GetRecordCount() > 1)
                throw DBDuplicatedGameObjectRecordSet("monster", this->DumpSQL(rs).c_str(), __FILE__, __LINE__);

            {
                BaseComponentFactory factory;
                jv["base"] = factory.CreateJSONFromRecordSet(rs, *this);
            }

            {
                RpgPropertyComponentFactory factory;
                jv["rpg-property"] = factory.CreateJSONFromRecordSet(rs, *this);
            }

            {
                MonsterPropertyComponentFactory factory;
                jv["monster-property"] = factory.CreateJSONFromRecordSet(rs, *this);
            }

            {
                TradeComponentFactory factory;
                jv["trade"] = factory.CreateJSONFromRecordSet(rs, *this);
            }

            {
                CombatPropertyComponentFactory factory;
                jv["combat-property"] = factory.CreateJSONFromRecordSet(rs, *this);
            }
        }
        else if (!rs.HasError() && rs.IsEmpty()) {
            return Json::Value;
        }
        else throw DBSQLError("monster", this->DumpSQL(rs).c_str());
    }
    catch (FactoryException& e) {
        LOG(L_ERROR, "%s", e.what());
        return Json::Value;
    }
}

JSONToMockRecordSet(const Json::Value&)
{
    MockIHWDBCursor root_cursor;

    EXPECT_CALL(root_cursor, GetRecord)
        .WillOnce(Return(true))
        .WillOnce(Return(true))
        .WillOnce(Return(true))
        .WillOnce(Return(true))
        .WillOnce(Return(true))
        .WillRepeatedly(Return(false));
    EXPECT_CALL(cursor, GetAttributeByName(StrEq("id")))
        .WillOnce(Return("0"));
    
    MockIHWDBCursor mr_cursor;
    EXPECT_CALL(mr, GetRecord)
        .WillOnce(Return(true))
        .WillOnce(Return(true))
        .WillOnce(Return(true))
        .WillOnce(Return(true))
        .WillRepeatedly(Return(false));
    EXPECT_CALL(mr, GetAttributeByName(StrEq("item")))
        .WillOnce(Return("a"))
        .WillOnce(Return("b"))
        .WillOnce(Return("c"))
        .WillOnce(Return("d"));

    HWDBRecordSet root;
    rs.SetCursor(root_cursor);
    
    HWDBRecordSet mr;
    mr.SetCursor(mr_cursor);
    rs.SetSubRecordSet("mr", mr);
}

int CreateRole(const char* dest, size_t n, const Json::Value& player, const char* table)
{
    
}
