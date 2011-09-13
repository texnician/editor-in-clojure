#include "gtest/gtest.h"
#include "gmock/gmock.h"

class ComponentFactoryTest : public ::testing::Test {
protected:
    virtual void SetUp()
        {
            data_ = new TiXmlElement("xml string");
        }

    virtual void TearDown()
        {
            delete data_;
        }

    TiXmlElement *data_;
};

TEST_F(ComponentFactoryTest, BaseComponent)
{
    // make xml data
    std::auto_ptr<TiXmlElement> data1(new TiXmlElement("xml string"));
    
    BaseComponentFactory factory;
    std::auto_ptr<BaseComponent> p((BaseComponent*)(factory.CreateFromXML(data1.get()).release()));
    ASSERT_EQ(p->Sid(), sid("BaseComponent"));
    ASSERT_STREQ(p->Name(), "BaseComponent");
    ASSERT_EQ(p->GetId(), 1);
    ASSERT_EQ(p->GetName(), std::string("TestObject"));
    
}

TEST_F(ComponentFactoryTest, Exception)
{
    // exception test
    std::auto_ptr<TiXmlElement> data2(new TiXmlElement("xml string"));
    ASSERT_THROW(factory.CreateFromXML(data2.get()), ComponentNodeNotFound);

    std::auto_ptr<TiXmlElement> data3(new TiXmlElement("xml string"));
    ASSERT_THROW(factory.CreateFromXML(data3.get()), ComponentNameNotFound);

    std::auto_ptr<TiXmlElement> data4(new TiXmlElement("xml string"));
    ASSERT_THROW(factory.CreateFromXML(data4.get()), AttributeValueNotFound);

    std::auto_ptr<TiXmlElement> data5(new TiXmlElement("xml string"));
    ASSERT_THROW(factory.CreateFromXML(data5.get()), AttributeNotFound);

    std::auto_ptr<TiXmlElement> data6(new TiXmlElement("xml string"));
    ASSERT_THROW(factory.CreateFromXML(data6.get()), EnumAttributeValueNotFound);

    std::auto_ptr<TiXmlElement> data7(new TiXmlElement("xml string"));
    ASSERT_THROW(factory.CreateFromXML(data7.get()), BoolValueError);

    std::auto_ptr<TiXmlElement> data8(new TiXmlElement("xml string"));
    ASSERT_THROW(factory.CreateFromXML(data8.get()), nkownAttributeValueType);
}
