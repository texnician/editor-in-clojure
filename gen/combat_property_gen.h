#ifndef _COMBAT_PROPERTY_GEN_H_
#define _COMBAT_PROPERTY_GEN_H_

#include <string>
#include <sstream>
#include "icomponent.h"

// 怪物专有属性组件
class MonsterPropertyComponent : public IComponent
{
    int species_;
    int monster_rank_;
    int generation_;
    int summon_cost_;
    int attack_;
    int defence_;
    int speed_;
    int mental_;
    int dodge_rate_;
    int crit_rate_;
    int magic_resistance_;

public:
    int GetSpecies() const
        {
            return species_;
        }

    void SetSpecies(int species)
        {
            species_ = species;
        }

    int GetMonsterRank() const
        {
            return monster_rank_;
        }

    void SetMonsterRank(int monster_rank)
        {
            monster_rank_ = monster_rank;
        }
};

#endif // _COMBAT_PROPERTY_GEN_H_
