package de.d151l.place.plugin

import com.twodevsstudio.simplejsonconfig.SimpleJSONConfig
import com.twodevsstudio.simplejsonconfig.api.Config
import de.d151l.place.plugin.util.Metrics
import de.d151l.place.api.database.DatabaseType
import de.d151l.place.plugin.world.block.BlockHistoryManager
import de.d151l.place.plugin.command.CheckBlockCommand
import de.d151l.place.plugin.command.RPlaceCommand
import de.d151l.place.plugin.command.RemoveBlockCommand
import de.d151l.place.plugin.config.BlockingItemsConfig
import de.d151l.place.plugin.config.DatabaseConfig
import de.d151l.place.plugin.config.MessageConfig
import de.d151l.place.plugin.config.PluginConfig
import de.d151l.place.plugin.countdown.CountdownManager
import de.d151l.place.plugin.database.DatabaseManager
import de.d151l.place.plugin.player.PlacePlayerCach
import de.d151l.place.plugin.scorebord.ScoreboardManager
import de.d151l.place.plugin.countdown.CooldownTask
import de.d151l.place.plugin.listener.*
import de.d151l.place.plugin.version.PluginVersion
import de.d151l.place.plugin.world.PlaceWorldManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

/**
 * @created 15/04/2022 - 02:14
 * @project R-Place
 * @author  D151l
 */
class Place(
    val javaPlugin: JavaPlugin
) {

    val config: PluginConfig
    val blockingItems: BlockingItemsConfig
    val messagesConfig: MessageConfig
    val databaseConfig: DatabaseConfig

    val placeWorldManager: PlaceWorldManager
    val scoreboardManager: ScoreboardManager
    val countdownManager: CountdownManager
    val databaseManager: DatabaseManager
    val placePlayerCach: PlacePlayerCach
    val blockHistoryManager: BlockHistoryManager
    val pluginVersion: PluginVersion

    val cooledowns: MutableMap<UUID, Long> = mutableMapOf()

    var blockHistoryCount: Int

    init {
        instance = this
        SimpleJSONConfig.INSTANCE.register(this.javaPlugin, this.javaPlugin.dataFolder)
        this.config = Config.getConfig(PluginConfig::class.java)
        this.blockingItems = Config.getConfig(BlockingItemsConfig::class.java)
        this.messagesConfig = Config.getConfig(MessageConfig::class.java)
        this.databaseConfig = Config.getConfig(DatabaseConfig::class.java)


        this.databaseManager = DatabaseManager(this, DatabaseType.valueOf(this.databaseConfig.databaseType))

        this.placeWorldManager = PlaceWorldManager(this)
        this.scoreboardManager = ScoreboardManager(this)
        this.countdownManager = CountdownManager(this)
        this.placePlayerCach = PlacePlayerCach(this)
        this.blockHistoryManager = BlockHistoryManager(this)
        this.pluginVersion = PluginVersion(this)

        val pluginManager = Bukkit.getPluginManager()
        pluginManager.registerEvents(PlayerJoinListener(this), this.javaPlugin)
        pluginManager.registerEvents(PlayerQuitListener(this), this.javaPlugin)
        pluginManager.registerEvents(BlockListener(this), this.javaPlugin)
        pluginManager.registerEvents(ProtectionListener(), this.javaPlugin)
        pluginManager.registerEvents(ItemListener(this), this.javaPlugin)

        this.javaPlugin.getCommand("removeBlock")?.setExecutor(RemoveBlockCommand(this))
        this.javaPlugin.getCommand("checkBlock")?.setExecutor(CheckBlockCommand(this))
        this.javaPlugin.getCommand("rplace")?.setExecutor(RPlaceCommand(this))

        this.placeWorldManager.setWorldBorder()
        this.blockHistoryCount = this.databaseManager.database.getBlockHistoryCount()

        if (this.config.scoreboardEnabled)
            CooldownTask(this)

        Metrics(this.javaPlugin, 14956)
    }

    fun shutdown() {
        Bukkit.getOnlinePlayers().forEach {
            this.placePlayerCach.unloadPlayerAtServerShutdown(it.uniqueId)
            it.kickPlayer(null)
        }

        this.databaseManager.database.closeConnection()
    }

    companion object {
        lateinit var instance: Place
    }
}