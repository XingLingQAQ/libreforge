package com.willfp.libreforge.effects.arguments

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.StringUtils
import com.willfp.libreforge.LibReforgePlugin
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.EffectArgument
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getPoints
import com.willfp.libreforge.setPoints
import com.willfp.libreforge.toFriendlyPointName
import com.willfp.libreforge.triggers.InvocationData
import org.bukkit.Sound

object EffectArgumentPointCost : EffectArgument {
    private val plugin = LibReforgePlugin.instance

    override fun isPresent(config: Config): Boolean =
        config.has("point_cost")

    override fun isMet(effect: ConfiguredEffect, data: InvocationData, config: Config): Boolean {
        val player = data.player

        val cost = config.getDoubleFromExpression("point_cost.cost", data.data)
        val type = config.getString("point_cost.type")

        return player.getPoints(type) >= cost
    }

    override fun ifNotMet(effect: ConfiguredEffect, data: InvocationData, config: Config) {
        val player = data.player

        val cost = config.getDoubleFromExpression("point_cost.cost", data.data)
        val type = config.getString("point_cost.type")

        val message = plugin.langYml.getMessage("cannot-afford-type")
            .replace("%cost%", NumberUtils.format(cost))
            .replace("%type%", type.toFriendlyPointName())

        if (plugin.configYml.getBool("cannot-afford-type.in-actionbar")) {
            PlayerUtils.getAudience(player).sendActionBar(StringUtils.toComponent(message))
        } else {
            player.sendMessage(message)
        }

        if (plugin.configYml.getBool("cannot-afford-type.sound.enabled")) {
            player.playSound(
                player.location,
                Sound.valueOf(plugin.configYml.getString("cannot-afford-type.sound.sound").uppercase()),
                1.0f,
                plugin.configYml.getDouble("cannot-afford-type.sound.pitch").toFloat()
            )
        }
    }

    override fun ifMet(effect: ConfiguredEffect, data: InvocationData, config: Config) {
        val cost = config.getDoubleFromExpression("point_cost.cost", data.data)
        val type = config.getString("point_cost.type")

        data.player.setPoints(type, data.player.getPoints(type) - cost)
    }
}