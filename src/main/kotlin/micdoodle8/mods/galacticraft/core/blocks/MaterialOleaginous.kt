package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.core.util.JavaUtil
import net.minecraft.block.BlockDynamicLiquid
import net.minecraft.block.BlockLiquid
import net.minecraft.block.BlockStaticLiquid
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.MaterialLiquid

//This avoids water and oil mixing, by being a different material
open class MaterialOleaginous constructor(color: MapColor) : MaterialLiquid(color) {
    private val blockLiquidName: Class<*> = BlockLiquid::class.java
    private val blockLiquidStaticName: Class<*> = BlockStaticLiquid::class.java
    private val blockLiquidDynamicName: Class<*> = BlockDynamicLiquid::class.java

    init {
        this.setNoPushMobility()
    }

    //Water and other liquids cannot displace oil, but solid blocks can
    override fun blocksMovement(): Boolean {
        return JavaUtil.instance.isCalledBy(blockLiquidStaticName, blockLiquidName, blockLiquidDynamicName)
    }
}
