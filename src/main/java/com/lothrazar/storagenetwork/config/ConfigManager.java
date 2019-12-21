package com.lothrazar.storagenetwork.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.lothrazar.storagenetwork.StorageNetwork;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

@SuppressWarnings("serial")
public class ConfigManager {

  private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
  private static ForgeConfigSpec COMMON_CONFIG;
  private static BooleanValue LOGSPAM;
  private static IntValue REFRESHTICKS;
  private static BooleanValue RELOADONCHUNK;
  private static ConfigValue<List<String>> BLACKLIST;
  //    rangeWirelessAccessor = config.getInt("StorageRemoteRange", category, 128, 1, 10000, "How far the Remote item can reach (non-advanced)");
  //    allowFastWorkBenchIntegration = config.getBoolean("allowFastWorkBenchIntegration", category, true, "Allow 'fastworkbench' project to integrate into storage network crafting grids.  Turning off lets you disable integration without uninstalling mod.  Client and server should match for best outcome.");
  //    TileMaster.blacklist = config.getStringList("BlacklistBlocks", category, new String[] {
  //        "extrautils2:playerchest"
  //    }, "Disable these blocks from ever being able to connect to the network, they will be treated as a non-inventory.");
  static {
    initConfig();
  }

  private static void initConfig() {
    COMMON_BUILDER.comment("General settings").push(StorageNetwork.MODID);
    LOGSPAM = COMMON_BUILDER.comment("Enable very spammy logs.  Sometimes useful for debugging. ").define("logSpam", false);
    RELOADONCHUNK = COMMON_BUILDER.comment(
        "If this is true, reload network when a chunk unloads, this keeps your network always up to date.  It has been reported that this cause lag and chunk load issues on servers, so disable if you have any problems. ")
        .define("reloadNetworkWhenUnloadChunk", false);
    REFRESHTICKS = COMMON_BUILDER.comment("How often to auto-refresh a network (one second is 20 ticks)").defineInRange("autoRefreshTicks", 20, 2, 4096);
    List<String> list = new ArrayList<String>();
    list.add("extrautils2:playerchest");
    BLACKLIST = COMMON_BUILDER.comment("Disable these blocks from ever being able to connect to the network, they will be treated as a non-inventory.").define("BlacklistBlocks",
        list);
    COMMON_BUILDER.pop();
    COMMON_CONFIG = COMMON_BUILDER.build();
  }

  public ConfigManager(Path path) {
    final CommentedFileConfig configData = CommentedFileConfig.builder(path)
        .sync()
        .autosave()
        .writingMode(WritingMode.REPLACE)
        .build();
    configData.load();
    COMMON_CONFIG.setConfig(configData);
  }

  public boolean logspam() {
    return LOGSPAM.get();
  }

  public boolean doReloadOnChunk() {
    return RELOADONCHUNK.get();
  }

  public int refreshTicks() {
    return REFRESHTICKS.get();
  }

  public List<String> blacklist() {
    return BLACKLIST.get();
  }
}
