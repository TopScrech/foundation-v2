package micdoodle8.mods.galacticraft.core.client

import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase
import net.minecraftforge.fml.common.gameevent.TickEvent.Type

import micdoodle8.mods.galacticraft.core.util.GCLog
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse

abstract class KeyHandler {
    private val keyBindings: Array<KeyBinding>
    private var vKeyBindings: Array<KeyBinding>? = null
    private var keyDown: BooleanArray? = null
    private var repeatings: BooleanArray? = null
    private var vRepeatings: BooleanArray? = null

    @JvmField
    var isDummy: Boolean = false

    constructor(
        keyBindings: Array<KeyBinding>,
        repeatings: BooleanArray,
        vanillaKeys: Array<KeyBinding>,
        vanillaRepeatings: BooleanArray
    ) {
        assert(keyBindings.size == repeatings.size) { "You need to pass two arrays of identical length" }
        assert(vanillaKeys.size == vanillaRepeatings.size) { "You need to pass two arrays of identical length" }
        this.keyBindings = keyBindings
        this.repeatings = repeatings
        this.vKeyBindings = vanillaKeys
        this.vRepeatings = vanillaRepeatings
        this.keyDown = BooleanArray(keyBindings.size + vanillaKeys.size)
    }

    constructor(keyBindings: Array<KeyBinding>) {
        this.keyBindings = keyBindings
        this.isDummy = true
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.side == Side.CLIENT) {
            if (event.phase == TickEvent.Phase.START) {
                this.keyTick(event.type, false)
            } else if (event.phase == TickEvent.Phase.END) {
                this.keyTick(event.type, true)
            }
        }
    }

    fun keyTick(type: TickEvent.Type?, tickEnd: Boolean) {
        val inChat: Boolean = FMLClientHandler.instance().getClient().currentScreen is GuiChat

        for (i: Int in this.keyBindings.indices) {
            val keyBinding: KeyBinding = this.keyBindings[i]
            var keyCode: Int = keyBinding.getKeyCode()
            if (keyCode == Keyboard.KEY_NONE) continue
            var state: Boolean = false

            try {
                if (!inChat) {
                    if (keyCode < 0) {
                        keyCode += 100
                        state = Mouse.isButtonDown(keyCode)
                    } else {
                        state = Keyboard.isKeyDown(keyCode)
                    }
                }
            } catch (e: IndexOutOfBoundsException) {
                GCLog.severe("Invalid keybinding! " + keyBinding.getKeyDescription())
                continue
            }

            if (state != this.keyDown!![i] || state && this.repeatings!![i]) {
                if (state) {
                    this.keyDown(type, keyBinding, tickEnd, state != this.keyDown!![i])
                } else {
                    this.keyUp(type, keyBinding, tickEnd)
                }
                if (tickEnd) {
                    this.keyDown!![i] = state
                }
            }
        }
        for (i: Int in this.vKeyBindings!!.indices) {
            val keyBinding: KeyBinding = this.vKeyBindings!![i]
            val keyCode: Int = keyBinding.getKeyCode()
            if (keyCode == Keyboard.KEY_NONE) continue
            val state: Boolean = if (keyCode < 0) Mouse.isButtonDown(keyCode + 100) else Keyboard.isKeyDown(keyCode)
            if (state != this.keyDown!![i + this.keyBindings.size] || state && this.vRepeatings!![i]) {
                if (state) {
                    this.keyDown(type, keyBinding, tickEnd, state != this.keyDown!![i + this.keyBindings.size])
                } else {
                    this.keyUp(type, keyBinding, tickEnd)
                }
                if (tickEnd) {
                    this.keyDown!![i + this.keyBindings.size] = state
                }
            }
        }
    }

    abstract fun keyDown(types: TickEvent.Type?, kb: KeyBinding?, tickEnd: Boolean, isRepeat: Boolean)

    abstract fun keyUp(types: TickEvent.Type?, kb: KeyBinding?, tickEnd: Boolean)
}
