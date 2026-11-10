package asmodeuscore.api.entity

import asmodeuscore.core.astronomy.SpaceData.Engine_Type

interface ICustomEngine {
    fun getEngine(): Engine_Type?
}
