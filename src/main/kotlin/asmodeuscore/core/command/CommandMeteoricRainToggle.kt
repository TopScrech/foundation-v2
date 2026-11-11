package asmodeuscore.core.command

import foundation.level.FoundationSpaceEnvironment
import asmodeuscore.core.astronomy.dimension.world.data.MeteoricRainSaveData
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.world.level.Level

object CommandMeteoricRainToggle {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("togglemeteoricrain")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes { execute(it.source) }
                // Legacy storm commands ignored trailing arguments
                .then(Commands.argument("arguments", StringArgumentType.greedyString()).executes { execute(it.source) })
        )
    }

    private fun execute(source: CommandSourceStack): Int {
        toggleRainfall(source.level)
        source.sendSuccess({ Component.translatable("commands.downfall.success") }, true)
        return 1
    }

    private fun toggleRainfall(world: Level) {
        val environment = FoundationSpaceEnvironment.get(world)
        if (environment.body.isPresent && environment.hasWeather) {
            val data = MeteoricRainSaveData.get(world, environment.saveName)
            toggleWeather(data)
        }
    }
    fun toggleWeather(data: MeteoricRainSaveData) {
        if (data.isMeteoricStorm()) data.tickMeteoricRain = 1 else data.tickClearWeather = 1
        data.setDirty()
    }
}
