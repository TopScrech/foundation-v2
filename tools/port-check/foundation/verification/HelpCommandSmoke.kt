package foundation.verification

import com.mojang.brigadier.CommandDispatcher
import com.mojang.serialization.JsonOps
import micdoodle8.mods.galacticraft.core.command.CommandGCKit
import net.minecraft.commands.Commands
import net.minecraft.server.permissions.PermissionSet
import com.mojang.brigadier.exceptions.CommandSyntaxException
import micdoodle8.mods.galacticraft.core.command.CommandGCHelp
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.ComponentSerialization

fun verifyHelpCommand() {
    // A fresh dispatcher models the registration pass after a resource reload
    repeat(2) {
        val dispatcher = CommandDispatcher<CommandSourceStack>()
        CommandGCHelp.register(dispatcher)
        val root = requireNotNull(dispatcher.root.getChild("gchelp"))
        val publicSource = Commands.createCompilationContext(PermissionSet.NO_PERMISSIONS)
        val operatorSource = Commands.createCompilationContext(PermissionSet.ALL_PERMISSIONS)
        check(root.command != null && root.requirement.test(publicSource))
        for (input in listOf("gchelp", "gchelp ignored arguments")) {
            // The native compilation context supplies no live player or server
            val parsed = dispatcher.parse(input, publicSource)
            check(parsed.exceptions.isEmpty() && !parsed.reader.canRead())
            check(parsed.context.command != null)
        }
        check(dispatcher.execute("gchelp", publicSource) == 0)
        var itemResolved = false
        CommandGCKit.register(dispatcher) { itemResolved = true; error("Must not resolve kit during registration or rejected execution") }
        val kit = requireNotNull(dispatcher.root.getChild("gckit"))
        check(!kit.requirement.test(publicSource) && kit.requirement.test(operatorSource))
        check(dispatcher.parse("gckit", publicSource).reader.canRead())
        for (input in listOf("gckit", "gckit FoundationTester")) {
            val parsed = dispatcher.parse(input, operatorSource)
            check(parsed.exceptions.isEmpty() && !parsed.reader.canRead() && parsed.context.command != null)
        }
        check(dispatcher.parse("gckit FoundationTester extra", operatorSource).reader.canRead())
        check(runCatching { dispatcher.execute("gckit", operatorSource) }.exceptionOrNull() is CommandSyntaxException)
        check(!itemResolved)
    }
    val message = CommandGCHelp.message()
    val json = ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, message).getOrThrow().asJsonObject
    check(json["translate"].asString == "gui.message.help1")
    val link = json.getAsJsonArray("extra").last().asJsonObject
    check(link["text"].asString == " wiki.micdoodle8.com")
    check(link["color"].asString == "green")
    check(link.getAsJsonObject("click_event")["action"].asString == "open_url")
    check(link.getAsJsonObject("click_event")["url"].asString == "http://wiki.micdoodle8.com/wiki")
    val hover = link.getAsJsonObject("hover_event")
    check(hover["action"].asString == "show_text")
    check(hover.getAsJsonObject("value")["translate"].asString == "gui.message.clicklink")
    val decoded = ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow()
    check(ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, decoded).getOrThrow() == json)
    println("Passed native help and kit command parsing, permission gates, repeated registration, console validation, and localized help codecs")
}
