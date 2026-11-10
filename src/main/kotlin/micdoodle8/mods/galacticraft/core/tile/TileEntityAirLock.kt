package micdoodle8.mods.galacticraft.core.tile


open class TileEntityAirLock : TileEntityAdvanced() {
    override fun update() {
        super.update()
    }

    override fun getPacketRange(): Double {
        return 0.0
    }

    override fun getPacketCooldown(): Int {
        return 0
    }

    override fun isNetworkedTile(): Boolean {
        return false
    }
}
